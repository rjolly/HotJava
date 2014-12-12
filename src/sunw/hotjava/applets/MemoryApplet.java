// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MemoryApplet.java

package sunw.hotjava.applets;

import java.awt.*;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.applets:
//            HotJavaApplet

public class MemoryApplet extends HotJavaApplet
    implements Runnable
{

    public void init()
    {
        delay = getIntParameter("delay", 1000);
        totalMsg = properties.getProperty("memoryapplet.total.text", "Total");
        freeMsg = properties.getProperty("memoryapplet.free.text", "Free");
        kbMsg = properties.getProperty("memoryapplet.kb.text", "Kb");
    }

    public synchronized void start()
    {
        d = size();
        thread = new Thread(this, "MemoryApplet");
        thread.start();
    }

    public synchronized void stop()
    {
        thread.stop();
        thread = null;
    }

    public synchronized void run()
    {
        while(thread == Thread.currentThread()) 
            try
            {
                repaint();
                wait(delay);
            }
            catch(InterruptedException _ex)
            {
                return;
            }
    }

    public void paint(Graphics g)
    {
        Runtime runtime = Runtime.getRuntime();
        g.setFont(font);
        g.setColor(getBackground());
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(Color.black);
        FontMetrics fontmetrics = g.getFontMetrics();
        g.drawString(totalMsg + " " + runtime.totalMemory() / 1024L + kbMsg + ", " + freeMsg + " " + runtime.freeMemory() / 1024L + kbMsg, 5, (d.height + fontmetrics.getAscent()) / 2);
    }

    public MemoryApplet()
    {
    }

    private static HJBProperties properties = HJBProperties.getHJBProperties("hjbrowser");
    static final Font font = new Font("SansSerif", 1, 18);
    Thread thread;
    int delay;
    Dimension d;
    String totalMsg;
    String freeMsg;
    String kbMsg;

}
