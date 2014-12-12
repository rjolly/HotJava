// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Clock.java

package sunw.hotjava.ui;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import sunw.hotjava.misc.ClockFormat;
import sunw.hotjava.misc.HJBProperties;

public class Clock extends Canvas
    implements Observer
{

    Clock(Font font1)
    {
        if(!font1.isBold())
            font = new Font(font1.getName(), 1, font1.getSize());
        else
            font = font1;
        props = HJBProperties.getHJBProperties("hjbrowser");
    }

    public void start()
    {
        show();
    }

    public void stop()
    {
        hide();
    }

    public Dimension getPreferredSize()
    {
        String s = ClockFormat.getFormat(props.getProperty("hotjava.clock.format", "MMM d', 'h:mm a"));
        if(!s.equals(clockPattern))
        {
            clockPattern = s;
            Graphics g = getGraphics();
            if(g != null)
                try
                {
                    FontMetrics fontmetrics = getFontMetrics(font);
                    g.setFont(font);
                    width = fontmetrics.stringWidth(getCurrentTime()) + 10;
                    height = fontmetrics.getHeight() + 4;
                }
                finally
                {
                    g.dispose();
                }
        }
        return new Dimension(width, height);
    }

    public Dimension minimumSize()
    {
        return getPreferredSize();
    }

    public Insets getInsets()
    {
        return new Insets(0, 0, 0, 0);
    }

    public void paint(Graphics g)
    {
        Rectangle rectangle = getBounds();
        g.setColor(getBackground());
        g.getColor();
        Color color = new Color(102, 102, 102);
        Color color1 = new Color(226, 226, 226);
        g.setColor(color);
        g.drawRect(rectangle.x, rectangle.y, rectangle.width - 2, rectangle.height - 2);
        g.setColor(color1);
        g.drawRect(rectangle.x + 1, rectangle.y + 1, rectangle.width - 2, rectangle.height - 2);
        g.setColor(new Color(0, 0, 127));
        g.setFont(font);
        FontMetrics fontmetrics = getFontMetrics(font);
        g.drawString(getCurrentTime(), rectangle.x + 5, rectangle.y + 2 + fontmetrics.getAscent());
    }

    public static String formatTime(String s)
    {
        Calendar calendar = Calendar.getInstance();
        java.util.Date date = calendar.getTime();
        SimpleDateFormat simpledateformat = new SimpleDateFormat(s);
        simpledateformat.setCalendar(calendar);
        return simpledateformat.format(date);
    }

    protected String getCurrentTime()
    {
        return formatTime(clockPattern);
    }

    public void update(Observable observable, Object obj)
    {
        repaint();
    }

    HJBProperties props;
    Font font;
    private static int width;
    private static int height;
    private static final int hMargin = 10;
    private static final int vMargin = 4;
    private static final String DEFAULT_CLOCK_FORMAT = "MMM d', 'h:mm a";
    private static String clockPattern = "MMM d', 'h:mm a";

}
