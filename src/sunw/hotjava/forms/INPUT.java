// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   INPUT.java

package sunw.hotjava.forms;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.bean.ImageCacher;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJColor;
import sunw.hotjava.tags.Align;
import sunw.html.Attributes;

// Referenced classes of package sunw.hotjava.forms:
//            FORM, FormPanel

public class INPUT extends EmptyTagItem
    implements Floatable, SizeItem, ImageObserver
{

    public synchronized void init(Document document)
    {
        setAttribute("value", squeezeOut(getAttribute("value"), '\n'));
        super.init(document);
        doc = document;
        String s = getAttribute("align");
        if(s != null && s.equals("center"))
            setAttribute("align", "middle");
        align = Align.getAlign(super.atts);
        isImage = false;
        String s1 = getAttribute("type");
        if(s1 != null && s1.equals("image"))
        {
            isImage = true;
            String s2 = getAttribute("src");
            try
            {
                imageURL = new URL(document.getBaseURL(), s2);
            }
            catch(MalformedURLException _ex) { }
            String s3 = getAttribute("width");
            String s4 = getAttribute("height");
            if(s3 == null || s4 == null)
            {
                registerImageWaitRef();
                preLoadImage();
            }
        }
    }

    private void preLoadImage()
    {
        currImage = getImage(Globals.getRegisteredFrame());
    }

    private Image getImage(Component component)
    {
        ImageCacher imagecacher = HotJavaBrowserBean.getImageCache();
        Image image = null;
        if(imagecacher != null)
        {
            URL url = null;
            if(doc != null)
                url = doc.getBaseURL();
            image = imagecacher.getImage(doc, imageURL, url);
        }
        if(image != null && component != null)
        {
            component.prepareImage(image, this);
            int i = image.getWidth(this);
            int j = image.getHeight(this);
            if(i != -1 && j != -1)
                unregisterImageWaitRef();
        }
        return image;
    }

    public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1)
    {
        if((i & 0x30) != 0)
        {
            unregisterImageWaitRef();
            currImage = null;
            return false;
        }
        if((i & 0x40) != 0)
        {
            unregisterImageWaitRef();
            return false;
        }
        return (i & 0xa0) == 0;
    }

    private void registerImageWaitRef()
    {
        doc.addSizeItemWidthWaitRef(this);
        doc.addSizeItemHeightWaitRef(this);
        registered = true;
    }

    public void unregisterImageWaitRef()
    {
        if(registered)
            doc.removeSizeItemWaitRef(this);
        registered = false;
    }

    private String squeezeOut(String s, char c)
    {
        if(s == null)
            return null;
        char ac[] = s.toCharArray();
        int i = ac.length;
        for(int j = 0; j < i; j++)
            if(ac[j] == c)
            {
                i--;
                System.arraycopy(ac, j + 1, ac, j, i - j);
                j--;
            }

        return new String(ac, 0, i);
    }

    public boolean needsActivation()
    {
        return true;
    }

    public boolean isHidden(Formatter formatter)
    {
        FormPanel formpanel = (FormPanel)formatter.getPanel(this);
        if(formpanel != null)
            return formpanel.isHidden();
        else
            return false;
    }

    public int getWidth(DocStyle docstyle)
    {
        sunw.hotjava.doc.DocumentFormatter documentformatter = docstyle.win;
        if(documentformatter != null)
            return getWidth(((Formatter) (documentformatter)), docstyle);
        else
            return 0;
    }

    public int getWidth(Formatter formatter, DocStyle docstyle)
    {
        FormPanel formpanel = (FormPanel)formatter.getPanel(this);
        if(formpanel != null)
        {
            Dimension dimension = formpanel.getPreferredSize();
            return dimension.width;
        } else
        {
            return 0;
        }
    }

    public boolean format(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state == 2)
            return true;
        formatstate.state = 1;
        FormPanel formpanel = (FormPanel)formatter.getPanel(this);
        if(formpanel == null || isHidden(formatter))
        {
            formatstate.pos += 4096;
            return false;
        }
        Dimension dimension = formpanel.getPreferredSize();
        formpanel.setSize(dimension.width, dimension.height);
        formpanel.validate();
        if(isImage && alignIsFloating())
        {
            boolean flag = false;
            if(!formatter.isFloater(this) && formatstate.width + dimension.width > formatstate.maxWidth)
            {
                if(formatstate.startPos != ((TraversalState) (formatstate)).pos)
                {
                    formatstate.below += formatter.getCumulativeFloaterHeight(formatstate.y);
                    return true;
                }
                flag = true;
            }
            formatter.queueFloater(formatter, formatstate, this, getAscent(formatter, formatstate) + getDescent(formatter, formatstate), align == 7);
            formatstate.pos += 4096;
            return flag;
        }
        if(formatstate.width + dimension.width > formatstate.maxWidth && formatstate.width != 0)
            return true;
        int i = formpanel.getAscent();
        if(isImage)
            i = Math.max(Align.getAscent(formatstate, align, dimension.height), i);
        formatstate.ascent = Math.max(formatstate.ascent, Math.max(((TraversalState) (formatstate)).style.ascent, i));
        formatstate.descent = Math.max(formatstate.descent, Math.max(((TraversalState) (formatstate)).style.descent, dimension.height - i));
        if(formpanel.getType() > 2 && formpanel.getType() != 12 && formatstate.textAscent <= formatstate.descent)
            formatstate.above += formatstate.descent / 2;
        formatstate.width += dimension.width;
        formatstate.pos += 4096;
        return false;
    }

    private int paintImage(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        if(isHidden(formatter))
            return 0;
        FormPanel formpanel = (FormPanel)formatter.getPanel(this);
        if(formpanel != null)
        {
            Dimension dimension = formpanel.getSize();
            if(isImage)
            {
                if(docline != null)
                    j += Align.yOffset(docline, formatter.displayStyle, align, dimension.height);
            } else
            if(docline != null)
            {
                j += docline.height - docline.getBelow() - formpanel.getPreferredSize().height;
                if(formpanel.getType() > 2 && formpanel.getType() != 12 && docline.textAscent != 0)
                    j -= docline.lndescent / 2;
            }
            formpanel.setLocation(i, j);
            DocStyle docstyle = formatter.displayStyle;
            if(docstyle != null)
            {
                HJColor hjcolor = new HJColor(formatter.getFormatterBackgroundColor());
                if(hjcolor.similar(docstyle.color))
                    formpanel.setForeground(hjcolor.getContrastingColor());
                else
                    formpanel.setForeground(docstyle.color);
            }
            formpanel.setBackground(formatter.getFormatterBackgroundColor());
            formpanel.validate();
            formpanel.setVisible(true);
            return dimension.width;
        } else
        {
            return 0;
        }
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        formatter.displayPos += 4096;
        if(isImage && alignIsFloating())
            return 0;
        else
            return paintImage(formatter, g, i, j, docline);
    }

    public int printImage(Formatter formatter, Graphics g, int i, int j, DocLine docline, VBreakInfo vbreakinfo)
    {
        FormPanel formpanel = (FormPanel)formatter.getPanel(this);
        if(formpanel != null)
        {
            Dimension dimension = formpanel.getSize();
            int k = dimension.width;
            int l = dimension.height;
            if(isImage)
            {
                if(docline != null)
                    j += Align.yOffset(docline, formatter.displayStyle, align, dimension.height);
            } else
            if(docline != null)
            {
                j += docline.height - docline.getBelow() - formpanel.getPreferredSize().height;
                if(formpanel.getType() > 2 && formpanel.getType() != 12 && docline.textAscent != 0)
                    j -= docline.lndescent / 2;
            }
            Graphics g1 = g.create(i, j, k, l);
            DocStyle docstyle = formatter.displayStyle;
            if(docstyle != null)
            {
                HJColor hjcolor = new HJColor(docstyle.color);
                formpanel.setForeground(hjcolor.getContrastingColor());
            }
            boolean flag = formpanel.isVisible();
            try
            {
                if(!flag)
                    formpanel.setVisible(true);
                formpanel.printAll(g1);
            }
            catch(Exception _ex) { }
            finally
            {
                g1.dispose();
                if(!flag)
                    formpanel.setVisible(false);
            }
            return dimension.width;
        } else
        {
            return 0;
        }
    }

    public int print(Formatter formatter, Graphics g, int i, int j, DocLine docline, VBreakInfo vbreakinfo)
    {
        formatter.displayPos += 4096;
        if(alignIsFloating())
            return 0;
        else
            return printImage(formatter, g, i, j, docline, vbreakinfo);
    }

    public int paint(Formatter formatter, Graphics g, int i, int j)
    {
        return paintImage(formatter, g, i, j, null);
    }

    public int print(Formatter formatter, Graphics g, int i, int j, VBreakInfo vbreakinfo)
    {
        return printImage(formatter, g, i, j, null, null);
    }

    public int getFormPanel(Formatter formatter, Vector vector)
    {
        FormPanel formpanel;
        do
            formpanel = (FormPanel)formatter.getPanel(this);
        while(formpanel == null && (formatter = formatter.getParentFormatter()) != null);
        if(formpanel != null)
            vector.addElement(formpanel);
        return 1;
    }

    public Component createView(Formatter formatter, Document document)
    {
        FormPanel formpanel = new FormPanel(formatter, document, this, null);
        int i = getIndex();
        document.change(i << 12, i + 1 << 12);
        return formpanel;
    }

    public boolean measureItem(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        FormPanel formpanel = (FormPanel)formatter.getPanel(this);
        if(formpanel != null)
        {
            Dimension dimension = formpanel.getPreferredSize();
            measurement.setMinWidth(dimension.width);
            measurement.setPreferredWidth(dimension.width);
            if(dimension.equals(new Dimension()))
                measurestate.measurementInvalid = true;
        }
        measurestate.pos += 4096;
        return false;
    }

    public void setFormParent(TagItem tagitem)
    {
        if(tagitem == null || !(tagitem instanceof FORM))
        {
            return;
        } else
        {
            parentForm = (FORM)tagitem;
            return;
        }
    }

    public TagItem getFormParent()
    {
        return parentForm;
    }

    private boolean alignIsFloating()
    {
        return align == 7 || align == 8;
    }

    public int getAscent(Formatter formatter, FormatState formatstate)
    {
        return Align.getAscent(formatstate, align, getHeight(formatter));
    }

    public int getDescent(Formatter formatter, FormatState formatstate)
    {
        return Align.getDescent(formatstate, align, getHeight(formatter));
    }

    private int getHeight(Formatter formatter)
    {
        FormPanel formpanel = (FormPanel)formatter.getPanel(this);
        if(formpanel != null)
        {
            Dimension dimension = formpanel.getPreferredSize();
            return dimension.height;
        } else
        {
            return 0;
        }
    }

    protected boolean determineIfTagHasHandlers()
    {
        Attributes attributes = getAttributes();
        if(attributes == null)
            return false;
        String s = attributes.get("type");
        if(s == null)
            s = "text";
        if(s.equalsIgnoreCase("text"))
            return myDetermineHandlers(myHandlers[1]);
        if(s.equalsIgnoreCase("password"))
            return myDetermineHandlers(myHandlers[2]);
        if(s.equalsIgnoreCase("checkbox"))
            return myDetermineHandlers(myHandlers[3]);
        if(s.equalsIgnoreCase("radio"))
            return myDetermineHandlers(myHandlers[4]);
        if(s.equalsIgnoreCase("submit"))
            return myDetermineHandlers(myHandlers[5]);
        if(s.equalsIgnoreCase("reset"))
            return myDetermineHandlers(myHandlers[6]);
        if(s.equalsIgnoreCase("image"))
            return myDetermineHandlers(myHandlers[7]);
        if(s.equalsIgnoreCase("hidden"))
            return myDetermineHandlers(myHandlers[8]);
        if(s.equalsIgnoreCase("textarea"))
            return myDetermineHandlers(myHandlers[9]);
        if(s.equalsIgnoreCase("choice"))
            return myDetermineHandlers(myHandlers[10]);
        if(s.equalsIgnoreCase("list"))
            return myDetermineHandlers(myHandlers[11]);
        if(s.equalsIgnoreCase("file"))
            return myDetermineHandlers(myHandlers[12]);
        if(s.equalsIgnoreCase("button"))
            return myDetermineHandlers(myHandlers[13]);
        else
            return false;
    }

    private boolean myDetermineHandlers(String as[])
    {
        Attributes attributes = getAttributes();
        for(int i = 0; i < as.length; i++)
            if(attributes.get(as[i]) != null)
                return true;

        return false;
    }

    public boolean visit(DocItemVisitor docitemvisitor)
    {
        return docitemvisitor.visitINPUTTag(this);
    }

    public Dimension getSize()
    {
        return null;
    }

    public boolean hasSize()
    {
        return false;
    }

    public void waiterTimedOut()
    {
    }

    public INPUT()
    {
        registered = false;
    }

    private int align;
    private boolean isImage;
    Document doc;
    Image currImage;
    URL imageURL;
    boolean registered;
    static final int TEXT = 1;
    static final int PASSWORD = 2;
    static final int FILE = 12;
    public static final String myHandlers[][] = {
        new String[0], {
            "onblur", "onchange", "onfocus", "onselect"
        }, {
            "onblur", "onfocus"
        }, {
            "onblur", "onclick", "onfocus"
        }, {
            "onblur", "onclick", "onfocus"
        }, {
            "onblur", "onclick", "onfocus"
        }, {
            "onblur", "onclick", "onfocus"
        }, {
            "onblur", "onclick", "onfocus"
        }, new String[0], new String[0], 
        new String[0], new String[0], {
            "onblur", "onclick", "onfocus"
        }, {
            "onblur", "onclick", "onfocus", "onmouseup", "onmousedown"
        }
    };
    private FORM parentForm;

}
