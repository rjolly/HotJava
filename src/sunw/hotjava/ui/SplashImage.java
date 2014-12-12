// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SplashFrame.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.image.ImageObserver;

// Referenced classes of package sunw.hotjava.ui:
//            SplashFrame

class SplashImage extends Canvas
{

    SplashImage(String s)
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        try
        {
            java.net.URL url = ClassLoader.getSystemResource(s);
            image = toolkit.getImage(url);
            prepareImage(image, this);
            return;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public boolean imageUpdate(Image image1, int i, int j, int k, int l, int i1)
    {
        if((i & 0xc0) != 0)
        {
            synchronized(this)
            {
                image = null;
                loaded = true;
                notify();
            }
            return false;
        }
        boolean flag = false;
        int j1 = 0;
        int k1 = 0;
        if((i & 1) != 0)
        {
            j1 = l;
            flag = true;
        }
        if((i & 2) != 0)
        {
            k1 = i1;
            flag = true;
        }
        if(flag)
            setSize(j1, k1);
        if((i & 0x20) != 0)
            synchronized(this)
            {
                loaded = true;
                Graphics g = getGraphics();
                try
                {
                    paint(g);
                    getToolkit().sync();
                    notify();
                }
                finally
                {
                    g.dispose();
                }
            }
        return true;
    }

    public void setSize(int i, int j)
    {
        super.setSize(i, j);
        Container container = getParent();
        Insets insets = container.getInsets();
        container.setSize(i + insets.left + insets.right, j + insets.top + insets.bottom);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        if(g == null)
            return;
        if(image != null && loaded)
        {
            getSize();
            g.drawImage(image, 0, 0, null);
            return;
        }
        if(image == null && loaded)
        {
            if(emergencyFont == null)
                emergencyFont = new Font("SansSerif", 1, 30);
            g.setFont(emergencyFont);
            g.drawString("Launching Browser...", 35, 175);
        }
    }

    synchronized void waitTillLoaded()
    {
        while(!loaded) 
            try
            {
                wait();
            }
            catch(Exception _ex) { }
    }

    boolean isOk()
    {
        return loaded && image != null;
    }

    private Image image;
    private boolean loaded;
    private Font emergencyFont;
}
