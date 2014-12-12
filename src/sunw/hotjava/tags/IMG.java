// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   IMG.java

package sunw.hotjava.tags;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import sun.awt.ScreenUpdater;
import sun.awt.UpdateClient;
import sun.misc.Ref;
import sunw.hotjava.bean.ImageCacherImageRef;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.*;
import sunw.html.Attributes;

// Referenced classes of package sunw.hotjava.tags:
//            A, Align, ImageMap, ImageMapParser, 
//            OBJECT

public class IMG extends EmptyTagItem
    implements DocumentListener, ImageObserver, UpdateClient, Floatable, SizeItem
{

    public IMG()
    {
        width = -1;
        height = -1;
        fullyInited = false;
        needToRedraw = false;
        notLoaded = false;
        ismap = false;
        shapes = false;
        imageIsLoading = false;
        allImagesLoaded = false;
        doAnimation = true;
        cacheLoaded = false;
        firstTimeFromCache = true;
        if(lazyLoadingNotDone)
            synchronized(sunw.hotjava.tags.IMG.class)
            {
                if(lazyLoadingNotDone)
                {
                    HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
                    errImg = Globals.getImageFromBeanJar_DontUseThisMethod(hjbproperties.getProperty("img.errimg"));
                    delayImg = Globals.getImageFromBeanJar_DontUseThisMethod(hjbproperties.getProperty("img.delayimg"));
                    lazyLoadingNotDone = false;
                }
            }
        super.handlerStrings = myHandlers;
    }

    public void documentChanged(DocumentEvent documentevent)
    {
        if(documentevent.getID() == 1038 || documentevent.getID() == 1028)
            allImagesLoaded = true;
        if(documentevent.getID() == 1022 || documentevent.getID() == 1002 || documentevent.getID() == 1020 || documentevent.getID() == 1021 || documentevent.getID() == 1012 || documentevent.getID() == 1009 || documentevent.getID() == 1015 || documentevent.getID() == 1040)
            allImagesLoaded = false;
    }

    public void updateClient(Object obj)
    {
        if(obj != null && obj.equals(new Integer(hashCode())))
        {
            unregisterImageWaitRef();
            int i;
            if((flags & 0x10) <= 0)
            {
                notLoaded = true;
                determineSize(currImage, 0, -1, -1);
                checkSize();
                unregisterImageWaitRef();
                return;
            } else
            {
                return;
            }
        }
        synchronized(Globals.getAwtLock())
        {
            synchronized(doc)
            {
                i = getIndex();
                doc.update(i << 12, i + 1 << 12);
            }
        }
    }

    public boolean needsLoading()
    {
        return notLoaded;
    }

    public int getState()
    {
        return flags;
    }

    public void load(Formatter formatter)
    {
        flags |= 0x40;
        notLoaded = false;
        if(paramWidth != null)
        {
            if(paramWidth.isPercentage())
                width = 0;
            else
            if(paramWidth.isSet())
                width = paramWidth.getValue();
            else
                width = -1;
        } else
        {
            width = -1;
        }
        if(paramHeight != null)
        {
            if(paramHeight.isPercentage())
                height = 0;
            else
            if(paramHeight.isSet())
                height = paramHeight.getValue();
            else
                height = -1;
        } else
        {
            height = -1;
        }
        determineSize(formatter);
        checkSize();
        int i = getIndex();
        formatter.getDocument().change(i << 12, i + 1 << 12);
    }

    public boolean isMappable()
    {
        return usemap != null || href != null && ismap;
    }

    public NamedLink map(DocLine docline, DocStyle docstyle, String s, int i, int j)
    {
        int k = height >= 0 ? height : 0;
        k += getBorder() * 2;
        k += vspace * 2;
        int l = yOffset(docline);
        j -= l + vspace;
        i -= hspace;
        if(i >= 0 && j >= 0 && i < width && j < height)
        {
            if(usemap != null)
            {
                URL url = null;
                try
                {
                    url = new URL(doc.getBaseURL(), usemap);
                }
                catch(MalformedURLException _ex)
                {
                    return null;
                }
                if(imageMap == null)
                    imageMap = doc.findImageMap(url);
                if(imageMap != null)
                {
                    imageMap.updateAreaCoords(width, height);
                    return imageMap.map(i, j);
                }
            }
            if(ismap && href != null)
            {
                URL url1 = null;
                try
                {
                    url1 = new URL(doc.getBaseURL(), href + "?" + i + "," + j);
                }
                catch(MalformedURLException _ex)
                {
                    try
                    {
                        url1 = new URL("doc://unknown.protocol/" + href + "?" + i + "," + j);
                    }
                    catch(MalformedURLException _ex2) { }
                }
                return new NamedLink(null, url1, null);
            }
        }
        return null;
    }

    public String getMapAltText(DocLine docline, DocStyle docstyle, int i, int j)
    {
        if(usemap == null)
            return null;
        URL url = null;
        try
        {
            url = new URL(doc.getBaseURL(), usemap);
        }
        catch(MalformedURLException _ex)
        {
            return null;
        }
        if(imageMap == null)
            imageMap = doc.findImageMap(url);
        if(imageMap == null)
        {
            return null;
        } else
        {
            imageMap.updateAreaCoords(width, height);
            getBorder();
            int k = yOffset(docline);
            j -= k + vspace;
            i -= hspace;
            return imageMap.getMapAltText(i, j);
        }
    }

    public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1)
    {
        lastImageFlags = i;
        if((i & 0x10) != 0 && !allImagesLoaded)
            return false;
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        boolean flag = !hjbproperties.getBoolean("turnOffGifAnimations");
        if(flag != doAnimation)
            if(!flag)
            {
                doAnimation = false;
            } else
            {
                doAnimation = true;
                offScreen = null;
            }
        if(offScreen != null && !doAnimation)
            return (i & 0xa0) == 0;
        if(imageObservers != null && image != errImg && image != delayImg)
            synchronized(imageObservers)
            {
                ImageObserver imageobserver;
                for(Enumeration enumeration = imageObservers.elements(); enumeration.hasMoreElements(); imageobserver.imageUpdate(image, i, j, k, l, i1))
                    imageobserver = (ImageObserver)enumeration.nextElement();

            }
        if(!imageIsLoading)
            return false;
        Image image1 = getImage(null);
        if((flags & 1) != 0 || (flags & 2) != 0 && image != errImg || (flags & 2) == 0 && beanImageLoc != null && image != image1 || (flags & 2) == 0 && image != (notLoaded ? delayImg : image1))
            return false;
        if((i & 0x40) != 0)
        {
            if((flags & 2) == 0)
            {
                flags |= 2;
                flags |= 0x80;
                image = errImg;
                determineSize(image, i, -1, -1);
                synchronized(Globals.getAwtLock())
                {
                    synchronized(doc)
                    {
                        int l1 = getIndex();
                        unregisterImageWaitRef();
                        flags |= 0x10;
                        if((flags & 0x40) > 0)
                            doc.change(l1 << 12, l1 + 1 << 12);
                    }
                }
            } else
            {
                flags |= 1;
                flags |= 0x80;
            }
            return false;
        }
        int j1 = width;
        int k1 = height;
        determineSize(image, i, l, i1);
        checkSize();
        if((flags & 0x40) > 0 && (width != j1 || height != k1))
            synchronized(Globals.getAwtLock())
            {
                synchronized(doc)
                {
                    int i2 = getIndex();
                    doc.change(i2 << 12, i2 + 1 << 12);
                }
            }
        if((i & 0x30) != 0)
        {
            synchronized(Globals.getAwtLock())
            {
                synchronized(doc)
                {
                    needToRedraw = true;
                    int j2 = getIndex();
                    doc.update(j2 << 12, j2 + 1 << 12);
                }
            }
            comp = null;
            currImage = null;
            flags |= 0x80;
        } else
        if((i & 0x80) == 0)
        {
            ScreenUpdater.updater.notify(this, 100L);
            flags |= 0x80;
        }
        return (i & 0xa0) == 0;
    }

    String findEnclosingHref()
    {
        for(int i = getIndex(); i-- > 0;)
        {
            DocItem docitem = doc.getItem(i);
            int j = docitem.getOffset();
            if(j > 0)
            {
                TagItem tagitem = docitem.getTag(doc);
                if(tagitem != null && tagitem.getName().equals("a"))
                    if(tagitem.getAttributes() != null)
                        return tagitem.getAttributes().get("href");
                    else
                        return null;
            } else
            if(j < 0)
                i += j;
        }

        return null;
    }

    private void determineSize(Formatter formatter)
    {
        if(paramWidth != null && paramWidth.isPercentage() && formatter != null)
            width = (int)((double)formatter.getAvailableWidth() * ((double)paramWidth.getValue() / 100D));
        if(paramHeight != null && paramHeight.isPercentage() && formatter != null)
            height = (int)((double)formatter.getAvailableHeight() * ((double)paramHeight.getValue() / 100D));
        if(paramHeight == null && paramWidth != null)
            height = -1;
        else
        if(paramWidth == null && paramHeight != null)
            width = -1;
        determineSize(getImage(formatter.getParent()), 0, width, height);
    }

    private void determineSize(Image image, int i, int j, int k)
    {
        if((flags & 2) != 0 || notLoaded)
        {
            if(image == null)
                return;
            j = image.getWidth(this);
            k = image.getHeight(this);
            if(j < 0 || k < 0)
                return;
            int l;
            int i1;
            if(alt == null)
            {
                l = j;
                i1 = k;
            } else
            {
                FontMetrics fontmetrics = Globals.getFontMetrics(altFont);
                l = j + fontmetrics.stringWidth(alt) + 2;
                int j1 = fontmetrics.getHeight();
                i1 = k <= j1 ? j1 : k;
            }
            if(width < l)
                width = l;
            if(height < i1)
            {
                height = i1;
                return;
            }
        } else
        if(image != null && (width < 0 || height < 0))
        {
            if((i & 1) == 0)
                j = image.getWidth(this);
            if((i & 2) == 0)
                k = image.getHeight(this);
            if(j >= 0 && k >= 0)
            {
                if(width < 0)
                    if(k > 0 && (flags & 8) != 0)
                    {
                        if(height == 0)
                            width = -1;
                        else
                            width = (j * height) / k;
                    } else
                    {
                        width = j;
                    }
                if(height < 0)
                {
                    if(j > 0 && (flags & 4) != 0)
                        if(width == 0)
                        {
                            height = -1;
                            return;
                        } else
                        {
                            height = (k * width) / j;
                            return;
                        }
                    height = k;
                }
            }
        }
    }

    protected int getIncrement()
    {
        if(object != null)
            return getOffset() << 12;
        else
            return 4096;
    }

    public int getOffset()
    {
        if(object == null)
            return 0;
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

    public URL getImageURL()
    {
        return src;
    }

    public synchronized void init(Document document, OBJECT object1)
    {
        object = object1;
        init(document);
    }

    public synchronized void init(Document document)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        Attributes attributes = getAttributes();
        doc = document;
        if(attributes != null)
        {
            String s = attributes.get("src");
            if(s == null && object != null)
                s = attributes.get("data");
            if(s != null)
            {
                if(s.startsWith("internal-gopher-"))
                {
                    String s1 = hjbproperties.getProperty(s);
                    if(s1 != null)
                        s = s1;
                }
                if(s.startsWith("doc:/lib/images/"))
                    beanImageLoc = s.substring("doc:/lib/images/".length());
                else
                if(s.toLowerCase().startsWith("javascript:"))
                {
                    scriptImageLoc = s.substring("javascript:".length());
                    beanImageLoc = "";
                    notLoaded = true;
                } else
                {
                    beanImageLoc = null;
                    try
                    {
                        String s2 = (String)document.getProperty("template.src.url");
                        URL url;
                        if(s2 == null)
                            url = document.getBaseURL();
                        else
                            url = new URL(s2);
                        src = new URL(url, filterString(s));
                        ImageCacherImageRef imagecacherimageref = new ImageCacherImageRef(document, src);
                        if(imagecacherimageref == null)
                        {
                            flags |= 2;
                            flags |= 0x80;
                        } else
                        {
                            boolean flag = hjbproperties.getBoolean("delayImageLoading");
                            notLoaded = flag;
                        }
                    }
                    catch(MalformedURLException _ex)
                    {
                        src = null;
                        flags |= 2;
                        flags |= 0x80;
                    }
                }
                currImage = getImage(Globals.getRegisteredFrame());
            }
            if(attributes.get("width") != null)
            {
                paramWidth = new Length(attributes.get("width"));
                if(paramWidth.isPercentage())
                {
                    width = 0;
                    flags |= 4;
                } else
                if(paramWidth.isSet())
                {
                    width = paramWidth.getValue();
                    flags |= 4;
                } else
                {
                    width = -1;
                }
            }
            if(attributes.get("height") != null)
            {
                paramHeight = new Length(attributes.get("height"));
                if(paramHeight.isPercentage())
                {
                    height = 0;
                    flags |= 8;
                } else
                if(paramHeight.isSet())
                {
                    height = paramHeight.getValue();
                    flags |= 8;
                } else
                {
                    height = -1;
                }
            }
            alt = attributes.get("alt");
            if(alt != null)
                altFont = hjbproperties.getFont("altmessagefont");
            determineSize(currImage, 0, width, height);
            if(width > 0 && height > 0)
                flags |= 0x10;
            hspace = TagItem.parseInt(attributes, "hspace", 0, 0);
            vspace = TagItem.parseInt(attributes, "vspace", 0, 0);
            ismap = attributes.get("ismap") != null;
            String s3 = attributes.get("usemap");
            try
            {
                if(s3 != null)
                {
                    usemap = (new URL(document.getBaseURL(), s3)).toString();
                    if(s3.charAt(0) != '#')
                    {
                        ImageMapParser imagemapparser = new ImageMapParser(usemap, document.getImageMaps());
                        (new Thread(imagemapparser)).start();
                    }
                }
            }
            catch(MalformedURLException _ex) { }
            if(usemap == null && attributes.get("shapes") != null)
            {
                shapes = true;
                usemap = src.toString();
            }
            String s4 = attributes.get("align");
            if(s4 != null && s4.equals("center"))
                attributes.put("align", "middle");
            align = Align.getAlign(attributes);
        } else
        {
            flags = 1;
        }
        registerImageWaitRef();
    }

    private void registerImageWaitRef()
    {
        int i = -1;
        int j = -1;
        boolean flag = false;
        if((flags & 0x10) > 0)
            return;
        Integer integer = new Integer(hashCode());
        imageIsLoading = true;
        currImage = getImage(Globals.getRegisteredFrame());
        if(currImage != null)
        {
            i = currImage.getWidth(this);
            j = currImage.getHeight(this);
        }
        if((flags & 4) <= 0 && i < 0)
        {
            doc.addSizeItemWidthWaitRef(this);
            flags |= 0x20;
            flag = true;
        }
        if((flags & 8) <= 0 && j < 0)
        {
            flags |= 0x20;
            doc.addSizeItemHeightWaitRef(this);
            flag = true;
        }
        if(flag)
        {
            int k = HJBProperties.getHJBProperties("beanPropertiesKey").getInteger("image.timeout", 10000);
            ScreenUpdater.updater.notify(this, k, integer);
            return;
        } else
        {
            flags |= 0x10;
            return;
        }
    }

    private void checkSize()
    {
        if((flags & 0x20) != 0 && width >= 0 && height >= 0)
        {
            unregisterImageWaitRef();
            flags |= 0x10;
        }
    }

    private void unregisterImageWaitRef()
    {
        flags = flags & 0xffffffdf;
        doc.removeSizeItemWaitRef(this);
    }

    public Component createView(Formatter formatter, Document document)
    {
        DocumentFormatter documentformatter = (DocumentFormatter)document.getView();
        documentformatter.addDocumentListener(this);
        if(!fullyInited)
        {
            if(shapes)
            {
                imageMap = new ImageMap(src);
                int i = getIndex() + 1;
                for(int j = i + getOffset(); i < j; i++)
                {
                    DocItem docitem = document.getItem(i);
                    if(!(docitem instanceof A))
                        break;
                    ((A)docitem).addImapInfo(document, imageMap);
                    i += docitem.getOffset();
                }

            }
            href = findEnclosingHref();
            fullyInited = true;
        }
        return null;
    }

    private int getBorder()
    {
        boolean flag = false;
        int i = 2;
        Attributes attributes = getAttributes();
        if(attributes != null)
        {
            flag = attributes.get("border") != null;
            if(flag && attributes.get("border").equalsIgnoreCase("none"))
                i = 0;
            else
                i = TagItem.parseInt(attributes, "border", 0, 2);
        }
        if(object != null || href != null || flag || usemap != null)
            return i;
        else
            return 0;
    }

    int yOffset(DocLine docline)
    {
        int i = docline.textAscent != 0 ? docline.textAscent : docline.lnascent;
        return docline.baseline - (ascent != 0 ? ascent : i);
    }

    public int getAscent(Formatter formatter, FormatState formatstate)
    {
        ascent = Align.getAscent(formatstate, align, height + getBorder() * 2 + vspace * 2);
        return ascent;
    }

    public int getDescent(Formatter formatter, FormatState formatstate)
    {
        return Align.getDescent(formatstate, align, height + getBorder() * 2 + vspace * 2);
    }

    public int getWidth(Formatter formatter, DocStyle docstyle)
    {
        return width + getBorder() * 2 + hspace * 2;
    }

    public int getWidth(DocStyle docstyle)
    {
        return width + getBorder() * 2 + hspace * 2;
    }

    public boolean needsActivation()
    {
        return src != null || beanImageLoc != null;
    }

    public boolean activate(Formatter formatter, Document document)
    {
        startLoadingImage(formatter);
        determineSize(formatter);
        checkSize();
        return true;
    }

    private void startLoadingImage(Formatter formatter)
    {
        if(scriptImageLoc != null)
        {
            setImageScript(scriptImageLoc, formatter);
            scriptImageLoc = null;
            return;
        }
        if(imageIsLoading)
            return;
        if(formatter != null && formatter.getParent() != null)
        {
            imageIsLoading = true;
            getImage(formatter.getParent());
        }
    }

    public void deactivate(Formatter formatter)
    {
        imageIsLoading = false;
    }

    public boolean format(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(src == null && beanImageLoc == null)
        {
            formatstate.pos += getIncrement();
            return false;
        }
        determineSize(formatter);
        checkSize();
        startLoadingImage(formatter);
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
            formatstate.pos += getIncrement();
            return flag;
        } else
        {
            return super.format(formatter, formatstate, formatstate1);
        }
    }

    public boolean measureItem(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        if(src == null && beanImageLoc == null)
        {
            measurestate.pos += getIncrement();
            return false;
        }
        int i = getWidth(formatter, ((TraversalState) (measurestate)).style);
        if(alignIsFloating())
        {
            measurement.setFloaterMinWidth(i);
            measurement.setFloaterPreferredWidth(i);
        } else
        {
            measurement.setMinWidth(i);
            measurement.setPreferredWidth(i);
        }
        measurestate.pos += getIncrement();
        return false;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        return paint(formatter, g, i, j, docline, false);
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline, boolean flag)
    {
        if(src == null && beanImageLoc == null)
        {
            formatter.displayPos += getIncrement();
            return 0;
        }
        if(alignIsFloating())
        {
            formatter.displayPos += getIncrement();
            return 0;
        }
        try
        {
            int k = super.paint(formatter, g, i, j - 3, docline);
            k += paintImage(formatter, g, i + k, j, docline);
            return k;
        }
        catch(Exception exception)
        {
            System.err.println("Image failed to paint because: " + exception);
            exception.printStackTrace();
            return 0;
        }
    }

    public int print(Formatter formatter, Graphics g, int i, int j, DocLine docline, VBreakInfo vbreakinfo)
    {
        return paint(formatter, g, i, j, docline);
    }

    public int paint(Formatter formatter, Graphics g, int i, int j)
    {
        return paintImage(formatter, g, i, j, null);
    }

    public int print(Formatter formatter, Graphics g, int i, int j, VBreakInfo vbreakinfo)
    {
        return paintImage(formatter, g, i, j, null);
    }

    private int paintImage(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        int k;
        try
        {
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            DocStyle docstyle = formatter.displayStyle;
            int i1 = hspace * 2;
            int j1 = vspace * 2;
            Image image;
            if(doAnimation || offScreen == null)
            {
                image = getImage(formatter.getParent());
            } else
            {
                image = offScreen;
                flags = 32;
            }
            if((flags & 1) == 0 && (width < 0 || height < 0))
            {
                determineSize(image, 0, -1, -1);
                checkSize();
            }
            int k1 = getBorder();
            int l = height >= 0 ? height : 0;
            k = width >= 0 ? width : 0;
            k += k1 * 2;
            l += k1 * 2;
            k += i1;
            l += j1;
            if(docline != null)
                j += yOffset(docline);
            Color color = formatter.getFormatterBackgroundColor();
            if(object != null)
            {
                Color color1 = g.getColor();
                g.setColor(Globals.getVisible3DColor(color));
                for(int i2 = 0; i2 < k1;)
                {
                    g.draw3DRect(i + hspace, j + vspace, k - i1 - (i2 * 2 + 1), l - j1 - (i2 * 2 + 1), true);
                    i2++;
                    i++;
                    j++;
                }

                g.setColor(color1);
            } else
            if(k1 != 0)
            {
                if(usemap != null)
                {
                    Color color2 = hjbproperties.getColor("a.newcolor", Globals.mapNamedColor("0x0000B0"));
                    Color color4 = (Color)docstyle.doc.getProperty("link.color", color2);
                    g.setColor(color4);
                } else
                {
                    g.setColor(docstyle.color);
                }
                for(int l1 = 0; l1 < k1;)
                {
                    g.drawRect(i + hspace, j + vspace, k - i1 - (l1 * 2 + 1), l - j1 - (l1 * 2 + 1));
                    l1++;
                    i++;
                    j++;
                }

            }
            if((flags & 3) != 0 || notLoaded)
            {
                Color color3 = hjbproperties.getColor("hotjava.errorcolor", Color.lightGray);
                g.setColor(color3);
                g.fill3DRect(i + hspace, j + vspace, width, height, true);
                if((flags & 1) == 0)
                {
                    Graphics g1 = g.create(i + hspace, j + vspace, width - 1, height - 1);
                    try
                    {
                        g1.drawImage(image, 1, 1, image.getWidth(this), image.getHeight(this), color3, this);
                        if(alt != null)
                        {
                            g1.setFont(altFont);
                            g1.setColor(hjbproperties.getColor("hotjava.alttextcolor", Color.lightGray));
                            g1.drawString(alt, image.getWidth(this), height - g1.getFontMetrics(altFont).getAscent());
                        }
                    }
                    finally
                    {
                        g1.dispose();
                    }
                }
                if((flags & 3) != 0)
                    notLoaded = false;
            } else
            if((imageRef != null || beanImageLoc != null) && width > 0 && height > 0)
            {
                DocumentState documentstate = formatter.getDocumentState();
                if(needToRedraw && formatter.getParent() != null)
                {
                    needToRedraw = false;
                    int j2 = i + hspace;
                    int k2 = j + vspace;
                    Image image1 = formatter.getParent().createImage(width, height);
                    Graphics g2 = image1.getGraphics();
                    try
                    {
                        g2.translate(-j2, -k2);
                        formatter.paintBackground(g2, j2, k2, width, height);
                        g2.drawImage(image, j2, k2, width, height, this);
                    }
                    finally
                    {
                        g2.dispose();
                    }
                    g.drawImage(image1, j2, k2, width, height, this);
                    if(!doAnimation && offScreen == null && (lastImageFlags & 0x20) == 0)
                        offScreen = image1;
                } else
                if(documentstate.bg == null)
                    g.drawImage(image, i + hspace, j + vspace, width, height, color, this);
                else
                    g.drawImage(image, i + hspace, j + vspace, width, height, this);
            } else
            if(width > 0 && height > 0)
            {
                g.setColor(color);
                g.draw3DRect(i, j, width - 1, height - 1, true);
            }
        }
        catch(Exception exception)
        {
            k = 0;
            System.err.println("Image drawing failed: " + exception);
            exception.printStackTrace();
        }
        return k;
    }

    private boolean alignIsFloating()
    {
        return align == 7 || align == 8;
    }

    public void flush()
    {
        ImageCacherImageRef imagecacherimageref = imageRef;
        if(imagecacherimageref != null)
            imagecacherimageref.flush();
    }

    private Image getImage(Component component)
    {
        if((flags & 2) != 0)
        {
            cacheLoaded = false;
            return errImg;
        }
        if(notLoaded)
        {
            cacheLoaded = false;
            return delayImg;
        }
        if(beanImageLoc != null)
        {
            if(beanImage == null && component != null)
            {
                beanImage = Globals.getImageFromBeanJar_DontUseThisMethod(beanImageLoc);
                if(beanImage == null)
                    return delayImg;
                component.prepareImage(beanImage, this);
            }
            return beanImage;
        }
        if(imageRef == null)
        {
            if(src == null)
                return null;
            imageRef = new ImageCacherImageRef(doc, src);
            if(imageRef == null)
                return null;
            imageRefReloads = imageRef.getReloadCount() - 1;
        }
        Image image = (Image)imageRef.check();
        if(image == null && component != null)
            image = (Image)imageRef.get();
        int i = imageRef.getReloadCount();
        if(imageRefReloads != i)
            if(component == null)
                image = null;
            else
            if(image != null)
            {
                boolean flag = component.prepareImage(image, this);
                if(flag)
                {
                    if((flags & 0x20) > 0)
                    {
                        unregisterImageWaitRef();
                        flags |= 0x10;
                    }
                    cacheLoaded = true;
                    if(firstTimeFromCache)
                    {
                        if(imageObservers != null)
                            synchronized(imageObservers)
                            {
                                ImageObserver imageobserver;
                                for(Enumeration enumeration = imageObservers.elements(); enumeration.hasMoreElements(); imageobserver.imageUpdate(image, 32, 0, 0, width, height))
                                    imageobserver = (ImageObserver)enumeration.nextElement();

                            }
                        firstTimeFromCache = false;
                    }
                }
                imageRefReloads = i;
            } else
            {
                notLoaded = true;
                cacheLoaded = false;
            }
        return image;
    }

    public Dimension getSize()
    {
        return new Dimension(width, height);
    }

    public boolean hasSize()
    {
        return (flags & 0x10) > 0;
    }

    public void waiterTimedOut()
    {
        if((flags & 0x10) <= 0)
        {
            notLoaded = true;
            determineSize(getImage(null), 0, -1, -1);
            checkSize();
        }
    }

    public int addImageObserver(ImageObserver imageobserver)
    {
        if(imageObservers == null)
            imageObservers = new Vector();
        synchronized(imageObservers)
        {
            imageObservers.addElement(imageobserver);
        }
        if((flags & 1) != 0 || (flags & 2) != 0)
            return 64;
        if((lastImageFlags & 0x80) == 0 && ((lastImageFlags & 0x20) != 0 || cacheLoaded))
            return 32;
        else
            return lastImageFlags;
    }

    public void removeImageObserver(ImageObserver imageobserver)
    {
        if(imageObservers != null)
        {
            synchronized(imageObservers)
            {
                imageObservers.removeElement(imageobserver);
                if(imageObservers.isEmpty())
                    imageObservers = null;
            }
            return;
        } else
        {
            return;
        }
    }

    public boolean isImageDone()
    {
        return (flags & 0x80) != 0 || cacheLoaded;
    }

    private void printOutFlagBits()
    {
        System.out.print("[[[ My flag has: ");
        if((flags & 1) != 0)
            System.out.print("TOTALERROR ");
        if((flags & 2) != 0)
            System.out.print("IMGERROR ");
        if((flags & 4) != 0)
            System.out.print("WIDTHSPECIFIED ");
        if((flags & 8) != 0)
            System.out.print("HEIGHTSPECIFIED ");
        System.out.println(" ]]]");
    }

    private void printOutBits(int i)
    {
        System.out.print("[[[ Got image bits: ");
        if((i & 0x20) != 0)
            System.out.print("ALLBITS ");
        if((i & 0x80) != 0)
            System.out.print("ABORT ");
        if((i & 0x40) != 0)
            System.out.print("ERROR ");
        if((i & 0x10) != 0)
            System.out.print("FRAMEBITS ");
        if((i & 2) != 0)
            System.out.print("HEIGHT ");
        if((i & 4) != 0)
            System.out.print("PROPERTIES ");
        if((i & 1) != 0)
            System.out.print("WIDTH ");
        if((i & 8) != 0)
            System.out.print("SOMEBITS ");
        System.out.println(" ]]]");
    }

    public void setImageScript(String s, Formatter formatter)
    {
        String s1 = Formatter.handleScript(formatter.getTopFormatter(), s, "(IMG SRC)", 0, null);
        if(s1 != null)
        {
            beanImage = Toolkit.getDefaultToolkit().createImage(s1.getBytes());
            firstTimeFromCache = formatter.getParent().prepareImage(beanImage, this);
        } else
        {
            flags |= 2;
        }
        cacheLoaded = false;
        flags |= 0x80;
        notLoaded = false;
    }

    public void setImageURL(URL url, Formatter formatter)
    {
        firstTimeFromCache = true;
        cacheLoaded = false;
        src = url;
        doc = formatter.getDocument();
        ImageCacherImageRef imagecacherimageref = new ImageCacherImageRef(doc, src);
        if(imagecacherimageref == null)
        {
            flags |= 2;
            return;
        } else
        {
            imageRef = null;
            boolean flag = HJBProperties.getHJBProperties("beanPropertiesKey").getBoolean("delayImageLoading");
            notLoaded = flag;
            activate(formatter, doc);
            updateClient(null);
            return;
        }
    }

    public boolean visit(DocItemVisitor docitemvisitor)
    {
        return docitemvisitor.visitIMGTag(this);
    }

    URL src;
    private ImageCacherImageRef imageRef;
    private int imageRefReloads;
    private int align;
    private int ascent;
    private Document doc;
    private int width;
    private int height;
    private Length paramWidth;
    private Length paramHeight;
    private int vspace;
    private int hspace;
    private OBJECT object;
    private boolean fullyInited;
    private boolean needToRedraw;
    private boolean notLoaded;
    private boolean ismap;
    private String href;
    private boolean shapes;
    private boolean imageIsLoading;
    private String usemap;
    private String alt;
    private Font altFont;
    private int flags;
    private Image beanImage;
    private Image currImage;
    private Component comp;
    private String beanImageLoc;
    private String scriptImageLoc;
    private ImageMap imageMap;
    private static final int TOTALERROR = 1;
    private static final int IMGERROR = 2;
    private static final int WIDTHSPECIFIED = 4;
    private static final int HEIGHTSPECIFIED = 8;
    private static final int IMGSIZED = 16;
    private static final int IMGREGISTERED = 32;
    private static final int DELAYLOAD = 64;
    private static final int FINISHED = 128;
    private static Image errImg = null;
    private static Image delayImg = null;
    private boolean allImagesLoaded;
    protected Image offScreen;
    protected boolean doAnimation;
    private static boolean lazyLoadingNotDone = true;
    private int lastImageFlags;
    private Vector imageObservers;
    private boolean cacheLoaded;
    private boolean firstTimeFromCache;
    private static final String myHandlers[] = {
        "onabort", "onerror", "onload"
    };
    private static boolean debugPaint;

}
