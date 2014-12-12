// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ShowPropertyApplet.java

package sunw.hotjava.applets;

import java.applet.Applet;
import java.awt.*;
import sunw.hotjava.misc.ColorNameTable;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.applets:
//            HotJavaApplet

public class ShowPropertyApplet extends HotJavaApplet
{

    public void init()
    {
        sourceText = getParameter("text", "");
        String s = getParameter("font", "SansSerif-14");
        Font font1 = Font.decode(s);
        metrics = getFontMetrics(font1);
        setFont(font1);
        Color color = getColorParameter("fgcolor", defaultForeground);
        Color color1 = getColorParameter("bgcolor", defaultBackground);
        setForeground(color);
        setBackground(color1);
        String s1 = getParameter("posx", "0");
        centerX = "center".equals(s1);
        try
        {
            posX = Integer.decode(s1).intValue();
        }
        catch(NumberFormatException _ex)
        {
            posX = 0;
        }
        String s2 = getParameter("posy", "center");
        centerY = "center".equals(s2);
        try
        {
            posY = Integer.decode(s2).intValue();
            return;
        }
        catch(NumberFormatException _ex)
        {
            posY = 0;
        }
    }

    private Color getColorParameter(String s, Color color)
    {
        Color color1 = null;
        String s1 = getParameter(s);
        if(s1 != null)
            color1 = mapNamedColor(s1);
        if(color1 == null)
            return color;
        else
            return color1;
    }

    private Color mapNamedColor(String s)
    {
        if(s == null)
            return null;
        Color color = ColorNameTable.getColor(s);
        if(color == null)
        {
            if(s.indexOf('#') < 0 && s.indexOf("0x") < 0)
                s = "#" + s;
            try
            {
                color = Color.decode(s);
            }
            catch(NumberFormatException _ex)
            {
                color = null;
            }
        }
        return color;
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    private String parse(String s)
    {
        String s1 = "";
        for(int i = 0; i < s.length(); i++)
            if(s.charAt(i) != '`')
            {
                s1 = s1 + s.charAt(i);
            } else
            {
                int j = ++i;
                for(; i < s.length() && s.charAt(i) != '`'; i++);
                if(i > j)
                {
                    String s2 = s.substring(j, i);
                    String s3 = System.getProperty(s2);
                    if(s3 == null)
                        s3 = HJBProperties.getHJBProperties("hjbrowser").getProperty(s2);
                    if(s3 != null)
                        s1 = s1 + s3;
                }
            }

        return s1;
    }

    public void paint(Graphics g)
    {
        String s = parse(sourceText);
        int i = posX;
        int j = posY;
        if(centerX)
            i = (getSize().width - metrics.stringWidth(s)) / 2;
        else
        if(i < 0)
            i = (getSize().width - metrics.stringWidth(s)) + i;
        if(centerY)
            j = (getSize().height + metrics.getAscent()) / 2;
        else
        if(j < 0)
            j = getSize().height + metrics.getAscent() + j;
        g.setColor(getForeground());
        g.setFont(getFont());
        g.drawString(s, i, j);
    }

    public ShowPropertyApplet()
    {
    }

    private static final String centerFlag = "center";
    private static final String defaultFont = "SansSerif-14";
    private static final Color defaultForeground;
    private static final Color defaultBackground;
    private static final String defaultPosX = "0";
    private static final String defaultPosY = "center";
    private String sourceText;
    private Font font;
    private FontMetrics metrics;
    private int posX;
    private int posY;
    private boolean centerX;
    private boolean centerY;

    static 
    {
        defaultForeground = Color.black;
        defaultBackground = Color.white;
    }
}
