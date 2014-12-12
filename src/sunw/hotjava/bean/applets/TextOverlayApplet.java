// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TextOverlayApplet.java

package sunw.hotjava.bean.applets;

import java.applet.Applet;
import java.awt.*;
import sunw.hotjava.misc.Globals;

// Referenced classes of package sunw.hotjava.bean.applets:
//            HotJavaBeanApplet

public class TextOverlayApplet extends HotJavaBeanApplet
{

    public void init()
    {
        String s = getParameter("image");
        if(s != null)
            image = Globals.getImageFromBeanJar_DontUseThisMethod(s);
        String s1 = getParameter("font", "SansSerif-bold-24");
        overlayFont = Font.decode(s1);
        metrics = getFontMetrics(overlayFont);
        setFont(overlayFont);
        String s2 = getParameter("posx");
        if(s2 != null)
        {
            centerX = "center".equals(s2);
            try
            {
                posX = Integer.decode(s2).intValue();
            }
            catch(NumberFormatException _ex) { }
        }
        String s3 = getParameter("posy");
        if(s3 != null)
        {
            centerY = "center".equals(s3);
            try
            {
                posY = Integer.decode(s3).intValue();
            }
            catch(NumberFormatException _ex) { }
        }
        text = getParameter("text");
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        if(image != null)
            g.drawImage(image, 0, 0, getBackground(), this);
        if(text != null)
        {
            int i = posX;
            int j = posY;
            if(centerX)
                i = (size().width - metrics.stringWidth(text)) / 2;
            else
            if(i < 0)
                i = (size().width - metrics.stringWidth(text)) + i;
            if(centerY)
                j = (size().height + metrics.getAscent()) / 2;
            else
            if(j < 0)
                j = size().height + metrics.getAscent() + j;
            g.drawString(text, i, j);
        }
    }

    protected String getParameter(String s, String s1)
    {
        String s2 = getParameter(s);
        if(s2 != null)
            return s2;
        else
            return s1;
    }

    public TextOverlayApplet()
    {
        posX = -20;
        posY = 0;
        defaultCenterX = false;
        defaultCenterY = true;
        centerX = defaultCenterX;
        centerY = defaultCenterY;
    }

    static final String defaultFont = "SansSerif-bold-24";
    static final String centerFlag = "center";
    Image image;
    String text;
    Font overlayFont;
    FontMetrics metrics;
    static final int defaultPosX = -20;
    static final int defaultPosY = 0;
    int posX;
    int posY;
    boolean defaultCenterX;
    boolean defaultCenterY;
    boolean centerX;
    boolean centerY;
}
