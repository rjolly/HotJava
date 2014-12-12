// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImageCanvas.java

package sunw.hotjava.forms;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.net.URL;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.bean.ImageCacher;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.tags.A;

// Referenced classes of package sunw.hotjava.forms:
//            FormPanel

class ImageCanvas extends Canvas
    implements DocConstants
{
    class MouseEventListener extends MouseAdapter
    {

        public void mousePressed(MouseEvent mouseevent)
        {
            mouseDown(mouseevent);
        }

        public void mouseReleased(MouseEvent mouseevent)
        {
            mouseUp(mouseevent);
        }

        public void mouseEntered(MouseEvent mouseevent)
        {
            setCursor(new Cursor(12));
        }

        public void mouseExited(MouseEvent mouseevent)
        {
            setCursor(new Cursor(0));
        }

        MouseEventListener()
        {
        }
    }


    public ImageCanvas(Document document, URL url, String s)
    {
        usingErrorImage = false;
        badErrorImage = false;
        border = 2;
        oldWidth = -1;
        oldHeight = -1;
        needToSize = true;
        imageURL = url;
        doc = document;
        loadLazy();
        addMouseListener(new MouseEventListener());
        try
        {
            border = Integer.parseInt(s);
            return;
        }
        catch(NumberFormatException _ex)
        {
            border = 2;
        }
    }

    private static synchronized void loadLazy()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        if(lazyLoadingNotDone)
        {
            errorImage = Globals.getImageFromBeanJar_DontUseThisMethod(hjbproperties.getProperty("img.errimg"));
            lazyLoadingNotDone = false;
        }
    }

    public synchronized void addActionListener(ActionListener actionlistener)
    {
        listeners = AWTEventMulticaster.add(listeners, actionlistener);
    }

    public Dimension preferredSize()
    {
        if(usingErrorImage && errorImage != null)
        {
            int i = errorImage.getWidth(this);
            int j = errorImage.getHeight(this);
            if(i >= 0 && j >= 0)
                return new Dimension(i, j);
        }
        ImageCacher imagecacher = HotJavaBrowserBean.getImageCache();
        Image image = null;
        if(imagecacher != null)
        {
            URL url = null;
            if(doc != null)
                url = doc.getBaseURL();
            image = imagecacher.getImage(doc, imageURL, url);
        }
        if(image != null && !usingErrorImage)
        {
            if(needToSize)
            {
                oldWidth = image.getWidth(this);
                oldHeight = image.getHeight(this);
                needToSize = false;
                if(usingErrorImage)
                    return preferredSize();
            }
            return new Dimension(oldWidth, oldHeight);
        } else
        {
            return super.preferredSize();
        }
    }

    public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1)
    {
        if(badErrorImage || usingErrorImage && image != errorImage)
            return false;
        if(l != oldWidth || i1 != oldHeight)
        {
            FormPanel formpanel = (FormPanel)getParent();
            synchronized(Globals.getAwtLock())
            {
                synchronized(formpanel.doc)
                {
                    int j1 = formpanel.item.getIndex();
                    formpanel.doc.change(j1 << 12, j1 + 1 << 12);
                }
            }
            oldWidth = l;
            oldHeight = i1;
        }
        if((i & 0x40) != 0)
        {
            if(usingErrorImage)
            {
                badErrorImage = true;
                return false;
            }
            usingErrorImage = true;
        }
        if((i & 0x43) != 0)
        {
            FormPanel formpanel1 = (FormPanel)getParent();
            synchronized(Globals.getAwtLock())
            {
                synchronized(formpanel1.doc)
                {
                    int k1 = formpanel1.item.getIndex();
                    formpanel1.doc.change(k1 << 12, k1 + 1 << 12);
                }
            }
        }
        if((i & 0x30) != 0)
        {
            FormPanel formpanel2 = (FormPanel)getParent();
            synchronized(Globals.getAwtLock())
            {
                synchronized(formpanel2.doc)
                {
                    int l1 = formpanel2.item.getIndex();
                    formpanel2.doc.update(l1 << 12, l1 + 1 << 12);
                }
            }
        }
        if((i & 0x40) != 0)
            return false;
        else
            return super.imageUpdate(image, i, j, k, l, i1);
    }

    private void paintBorder(Graphics g, Color color)
    {
        Image image = null;
        ImageCacher imagecacher = HotJavaBrowserBean.getImageCache();
        if(imagecacher != null)
        {
            URL url = null;
            if(doc != null)
                url = doc.getBaseURL();
            image = imagecacher.getImage(doc, imageURL, url);
        }
        int i = image.getWidth(this);
        int j = image.getHeight(this);
        if(i <= 0 || j <= 0)
            return;
        g.setColor(color);
        int k = 0;
        int l = 0;
        for(int i1 = 0; k < border; i1++)
        {
            g.drawRect(l, i1, i - (k * 2 + 1), j - (k * 2 + 1));
            k++;
            l++;
        }

    }

    public void paint(Graphics g)
    {
        ImageCacher imagecacher = HotJavaBrowserBean.getImageCache();
        if(imagecacher != null && !badErrorImage)
        {
            if(!usingErrorImage)
            {
                URL url = null;
                if(doc != null)
                    url = doc.getBaseURL();
                g.drawImage(imagecacher.getImage(doc, imageURL, url), 0, 0, this);
            } else
            {
                g.drawImage(errorImage, 0, 0, this);
            }
            if(border != 0)
                paintBorder(g, A.newColor);
        }
    }

    private void mouseDown(MouseEvent mouseevent)
    {
        if((mouseevent.getModifiers() & 4) != 0)
        {
            ElementInfo elementinfo = new ElementInfo();
            elementinfo.imageURL = imageURL.toExternalForm();
            elementinfo.event = mouseevent;
            ((FormPanel)getParent()).win.dispatchDocumentEvent(1027, elementinfo);
            return;
        }
        if(!badErrorImage)
        {
            depressed = true;
            if(border != 0)
            {
                Graphics g = getGraphics();
                try
                {
                    paintBorder(getGraphics(), A.activeColor);
                }
                finally
                {
                    g.dispose();
                }
                return;
            }
        }
    }

    private void mouseUp(MouseEvent mouseevent)
    {
        if((mouseevent.getModifiers() & 4) != 0)
        {
            ElementInfo elementinfo = new ElementInfo();
            elementinfo.imageURL = imageURL.toExternalForm();
            elementinfo.event = mouseevent;
            ((FormPanel)getParent()).win.dispatchDocumentEvent(1027, elementinfo);
            return;
        }
        Point point = mouseevent.getPoint();
        if(!badErrorImage)
        {
            if(depressed && inside(point.x, point.y))
            {
                if(border != 0)
                {
                    Graphics g = getGraphics();
                    try
                    {
                        paintBorder(g, A.newColor);
                    }
                    finally
                    {
                        g.dispose();
                    }
                }
                String s = point.x + ":" + point.y;
                ((FormPanel)getParent()).processUserAction(new ActionEvent(this, 1001, s));
            }
            depressed = false;
        }
    }

    public void setDimension(int i, int j)
    {
        oldWidth = i;
        oldHeight = j;
    }

    private URL imageURL;
    private Document doc;
    boolean depressed;
    boolean usingErrorImage;
    static Image errorImage = null;
    boolean badErrorImage;
    ActionListener listeners;
    int border;
    int oldWidth;
    int oldHeight;
    boolean needToSize;
    private static boolean lazyLoadingNotDone = true;



}
