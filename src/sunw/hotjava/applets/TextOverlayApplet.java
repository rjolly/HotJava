// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TextOverlayApplet.java

package sunw.hotjava.applets;

import java.applet.Applet;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

// Referenced classes of package sunw.hotjava.applets:
//            HotJavaApplet

public class TextOverlayApplet extends HotJavaApplet
{

    public void init()
    {
        String s = getParameter("image");
        if(s != null)
            try
            {
                URL url = new URL(getDocumentBase(), s);
                image = getImage(url);
            }
            catch(MalformedURLException _ex)
            {
                showStatus("Bad image URL: " + s);
            }
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
