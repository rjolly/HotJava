// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   APPLET.java

package sunw.hotjava.tags;

import java.awt.*;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.*;
import sunw.html.Attributes;

// Referenced classes of package sunw.hotjava.tags:
//            Align, TagAppletPanel, OBJECT

public class APPLET extends FlowTagItem
    implements Floatable
{

    public APPLET()
    {
        adjustedWidth = -1;
        adjustedHeight = -1;
        adjustedW = -1;
        adjustedH = -1;
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        if(lazyLoadingNotDone)
        {
            borderColor = hjbproperties.getColor("applet.bordercolor", Color.lightGray);
            lazyLoadingNotDone = false;
        }
    }

    public synchronized void init(Document document)
    {
        super.atts = getAttributes();
        if(super.atts == null)
        {
            width = new Length(null);
            height = new Length(null);
            return;
        }
        width = new Length(getAttribute("width"));
        height = new Length(getAttribute("height"));
        if(width.isSet())
        {
            if(!width.isPercentage())
                adjustedW = width.getValue();
            if(!height.isSet())
                height = width;
            else
            if(!height.isPercentage())
                adjustedH = height.getValue();
        } else
        if(height.isSet())
        {
            width = height;
        } else
        {
            width = new Length("250");
            height = new Length("250");
            adjustedW = 250;
            adjustedH = 250;
        }
        hspace = TagItem.parseInt(super.atts, "hspace", 0, 0);
        effHSpace = hspace * 2;
        vspace = TagItem.parseInt(super.atts, "vspace", 0, 0);
        effVSpace = vspace * 2;
        border = TagItem.parseInt(super.atts, "border", 0, 0);
        align = Align.getAlign(super.atts);
    }

    public void init(Document document, OBJECT object1)
    {
        object = object1;
        init(document);
    }

    public int getOffset()
    {
        if(object == null)
            return super.getOffset();
        else
            return object.getOffset();
    }

    public Attributes getAttributes()
    {
        if(object == null)
            return super.getAttributes();
        else
            return object.getAttributes();
    }

    public int getIndex()
    {
        if(object == null)
            return super.getIndex();
        else
            return object.getIndex();
    }

    public DocItem getObject()
    {
        if(object == null)
            return this;
        else
            return object;
    }

    public void adjustDimension(int i, int j)
    {
        adjustedWidth = i;
        adjustedHeight = j;
    }

    public void adjustDimension()
    {
        adjustedWidth = -1;
        adjustedHeight = -1;
    }

    public int getWidthAttribute(Formatter formatter)
    {
        if(adjustedWidth != -1)
            return adjustedWidth;
        if(width.isPercentage())
        {
            if(formatter == null)
                return 0;
            else
                return (int)((double)formatter.getAvailableWidth() * ((double)width.getValue() / 100D));
        } else
        {
            return width.getValue();
        }
    }

    private int getWidth(Formatter formatter)
    {
        if(adjustedW != -1)
            return adjustedW + effHSpace + border * 2;
        else
            return getWidthAttribute(formatter) + effHSpace + border * 2;
    }

    public int getWidth(Formatter formatter, DocStyle docstyle)
    {
        return getWidth(formatter);
    }

    public int getHeightAttribute(Formatter formatter)
    {
        if(adjustedHeight != -1)
            return adjustedHeight;
        if(height.isPercentage())
        {
            if(formatter == null)
                return 0;
            else
                return (int)((double)formatter.getAvailableHeight() * ((double)height.getValue() / 100D));
        } else
        {
            return height.getValue();
        }
    }

    private int getHeight(Formatter formatter)
    {
        if(adjustedH != -1)
            return adjustedH + effVSpace + border * 2;
        else
            return getHeightAttribute(formatter) + effVSpace + border * 2;
    }

    public int getAscent(Formatter formatter, FormatState formatstate)
    {
        return Align.getAscent(formatstate, align, getHeight(formatter));
    }

    public int getDescent(Formatter formatter, FormatState formatstate)
    {
        return Align.getDescent(formatstate, align, getHeight(formatter));
    }

    public boolean needsActivation()
    {
        return true;
    }

    private boolean alignIsFloating()
    {
        return align == 7 || align == 8;
    }

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state == 2)
            return true;
        if(alignIsFloating())
        {
            boolean flag = false;
            if(!formatter.isFloater(this) && formatstate.width + getWidth(formatter, ((TraversalState) (formatstate)).style) > formatstate.maxWidth)
            {
                if(formatstate.startPos != ((TraversalState) (formatstate)).pos)
                {
                    formatstate.below += formatter.getCumulativeFloaterHeight(formatstate.y);
                    return true;
                }
                flag = true;
            }
            formatter.queueFloater(formatter, formatstate, (TagItem)getObject(), getAscent(formatter, formatstate) + getDescent(formatter, formatstate), align == 7);
            formatstate.pos += getOffset() << 12;
            return flag;
        } else
        {
            formatter.setBreak(formatstate, formatstate1, 0, formatstate.width);
            formatstate.state = 1;
            formatstate.width += getWidth(formatter);
            formatstate.ascent = Math.max(formatstate.ascent, Math.max(((TraversalState) (formatstate)).style.ascent, getAscent(formatter, formatstate)));
            formatstate.descent = Math.max(formatstate.descent, Math.max(((TraversalState) (formatstate)).style.descent, getDescent(formatter, formatstate)));
            formatstate.pos += getOffset() << 12;
            formatEndTag(formatter, formatstate, formatstate1);
            return false;
        }
    }

    public boolean formatEndTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        formatstate.pos += 4096;
        return false;
    }

    public int findStartTagX(DocLine docline, DocStyle docstyle, int i, int j, int k, Formatter formatter)
    {
        formatter.getDocumentState();
        int l = getWidth(formatter, docstyle);
        k -= i;
        if(k < l >> 1)
            return j;
        if(k <= l)
            return j + 1;
        else
            return -l - 1;
    }

    public int findEndTagX(DocLine docline, DocStyle docstyle, int i, int j, int k, Formatter formatter)
    {
        return -1;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        if(alignIsFloating())
        {
            formatter.displayPos += getOffset() << 12;
            return 0;
        } else
        {
            int k = super.paint(formatter, g, i, j - 5, docline);
            formatter.displayPos += getOffset() - 1 << 12;
            k += paintApplet(formatter, g, i + k, j, docline);
            k += paintEndTag(formatter, g, i + k, j - 5, docline);
            formatter.displayStyle = formatter.displayStyle.next;
            return k;
        }
    }

    public int paint(Formatter formatter, Graphics g, int i, int j)
    {
        return paintApplet(formatter, g, i, j, null);
    }

    private int paintApplet(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        TagAppletPanel tagappletpanel = (TagAppletPanel)formatter.getPanel(getObject());
        if(tagappletpanel != null)
        {
            DocStyle docstyle = formatter.displayStyle;
            int k = getWidthAttribute(formatter);
            int l = getHeightAttribute(formatter);
            int i1 = k + border * 2;
            int j1 = l + border * 2;
            if(docline != null)
                j += Align.yOffset(docline, docstyle, align, j1 + effVSpace);
            if(border > 0)
            {
                Color color = g.getColor();
                Color color1 = formatter.getFormatterBackgroundColor();
                g.setColor(Globals.getVisible3DColor(color1));
                for(int k1 = 0; k1 < border;)
                {
                    g.draw3DRect(i + hspace, j + vspace, i1 - (k1 * 2 + 1), j1 - (k1 * 2 + 1), true);
                    k1++;
                    i++;
                    j++;
                }

                g.setColor(color);
            }
            Rectangle rectangle = tagappletpanel.bounds();
            if(rectangle.x != i + hspace || rectangle.y != j + vspace || rectangle.width != k || rectangle.height != l)
                tagappletpanel.reshape(i + hspace, j + vspace, k, l);
            tagappletpanel.validate();
            tagappletpanel.show();
        }
        return getWidth(formatter);
    }

    public int print(Formatter formatter, Graphics g, int i, int j, DocLine docline, VBreakInfo vbreakinfo)
    {
        j += docline.baseline - docline.lnascent;
        formatter.displayPos += getOffset() << 12;
        int k = getWidth(formatter);
        int l = getHeight(formatter);
        g.setColor(Color.black);
        TagAppletPanel tagappletpanel = (TagAppletPanel)formatter.getPanel(this);
        if(tagappletpanel != null)
        {
            Graphics g1 = g.create(i + hspace, j + vspace, k - effHSpace, l - effVSpace);
            try
            {
                tagappletpanel.print(g1);
            }
            finally
            {
                g1.dispose();
            }
        }
        return k;
    }

    public int print(Formatter formatter, Graphics g, int i, int j, VBreakInfo vbreakinfo)
    {
        int k = getWidth(formatter);
        int l = getHeight(formatter);
        g.setColor(Color.black);
        TagAppletPanel tagappletpanel = (TagAppletPanel)formatter.getPanel(this);
        if(tagappletpanel != null)
        {
            Graphics g1 = g.create(i + hspace, j + vspace, k - effHSpace, l - effVSpace);
            try
            {
                tagappletpanel.print(g1);
            }
            finally
            {
                g1.dispose();
            }
        }
        return k;
    }

    public Component createView(Formatter formatter, Document document)
    {
        TagAppletPanel tagappletpanel = new TagAppletPanel(document, this);
        formatter.dispatchDocumentEvent(1019, tagappletpanel);
        Color color = formatter.getFormatterBackgroundColor();
        tagappletpanel.setBackground(color);
        return tagappletpanel;
    }

    public boolean measureStartTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        if(formatstate.state == 2)
            return true;
        int i = getWidth(formatter);
        if(alignIsFloating())
        {
            measurement.setFloaterMinWidth(i);
            measurement.setFloaterPreferredWidth(i);
        } else
        {
            measurement.setMinWidth(i);
            measurement.setPreferredWidth(i);
        }
        measurestate.pos += getOffset() << 12;
        return false;
    }

    public boolean measureEndTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        measurestate.pos += 4096;
        return false;
    }

    public boolean visit(DocItemVisitor docitemvisitor)
    {
        return docitemvisitor.visitAPPLETTag(this);
    }

    private static boolean lazyLoadingNotDone = true;
    int adjustedWidth;
    int adjustedHeight;
    int adjustedW;
    int adjustedH;
    Length width;
    Length height;
    int hspace;
    int vspace;
    int effHSpace;
    int effVSpace;
    int align;
    int border;
    OBJECT object;
    static Color borderColor = null;

}
