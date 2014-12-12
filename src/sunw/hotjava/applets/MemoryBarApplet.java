// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MemoryBarApplet.java

package sunw.hotjava.applets;

import java.awt.*;

// Referenced classes of package sunw.hotjava.applets:
//            HotJavaApplet

public class MemoryBarApplet extends HotJavaApplet
    implements Runnable
{

    public void init()
    {
        delay = getIntParameter("delay", 1000);
    }

    public synchronized void start()
    {
        d = size();
        thread = new Thread(this, "MemoryBarApplet");
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
        double d1 = 1.0D - (double)runtime.freeMemory() / (double)runtime.totalMemory();
        int i = (int)(d1 * (double)(d.width - 2));
        g.setColor(Color.black);
        g.drawRect(0, 0, d.width - 1, d.height - 1);
        g.setColor(Color.red);
        g.fillRect(1, 1, i, d.height - 2);
        g.setColor(Color.green);
        g.fillRect(i, 1, d.width - (i + 2), d.height - 2);
        g.setFont(font);
        g.setColor(Color.black);
        FontMetrics fontmetrics = g.getFontMetrics();
        String s = (int)(d1 * 100D) + "%";
        if(d1 < 0.5D)
            i += 2;
        else
            i -= 2 + fontmetrics.stringWidth(s);
        g.drawString(s, i, (d.height + fontmetrics.getAscent()) / 2);
    }

    public MemoryBarApplet()
    {
    }

    static final Font font = new Font("Monospaced", 0, 14);
    Thread thread;
    int delay;
    Dimension d;

}
