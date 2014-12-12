// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProgressApplet.java

package sunw.hotjava.applets;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import sun.net.ProgressData;
import sun.net.ProgressEntry;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.applets:
//            HotJavaApplet

public class ProgressApplet extends HotJavaApplet
    implements Observer
{

    public ProgressApplet()
    {
        if(lazyLoadingNotDone)
        {
            meterFont = properties.getFont("progress.meterfont");
            legendFont = properties.getFont("progress.legendfont");
            lazyLoadingNotDone = false;
        }
    }

    public void start()
    {
        ProgressData.pdata.addObserver(this);
    }

    public void stop()
    {
        ProgressData.pdata.deleteObserver(this);
    }

    public synchronized void update(Observable observable, Object obj)
    {
        if(!isVisible())
            return;
        Graphics g = getGraphics();
        if(g == null)
            return;
        try
        {
            Dimension dimension = size();
            ProgressEntry progressentry = (ProgressEntry)obj;
            int i = progressentry.index;
            g.clipRect(10, 0, dimension.width - 20, dimension.height);
            switch(progressentry.what)
            {
            case 0: // '\0'
                paint(g, i, progressentry);
                break;

            case 1: // '\001'
                eraseEntry(g, i);
                paint(g, i, progressentry);
                break;

            case 2: // '\002'
                paintUpdate(g, i, progressentry);
                break;

            case 3: // '\003'
                eraseEntry(g, i);
                break;
            }
        }
        catch(Exception exception1)
        {
            exception1.printStackTrace();
        }
        finally
        {
            g.dispose();
        }
    }

    void eraseEntry(Graphics g, int i)
    {
        Dimension dimension = size();
        FontMetrics fontmetrics = g.getFontMetrics(meterFont);
        int j = 12 + fontmetrics.getHeight() + 5;
        int k = dimension.width - 20;
        int l = 35 + i * j;
        g.setColor(getBackground());
        g.fillRect(10, l, k, j);
        g.setColor(Color.gray);
        g.drawRect(10, l + fontmetrics.getHeight(), k - 1, 12);
    }

    void paint(Graphics g, int i, ProgressEntry progressentry)
    {
        if(progressentry.label != null)
        {
            Dimension dimension = size();
            String s = progressentry.label;
            FontMetrics fontmetrics = g.getFontMetrics(meterFont);
            int j = 12 + fontmetrics.getHeight() + 5;
            g.setFont(meterFont);
            g.setColor(Color.black);
            g.drawString(s, 10, (35 + i * j + fontmetrics.getHeight()) - (fontmetrics.getDescent() + 1));
            if(progressentry.connected)
            {
                int k = 10 + fontmetrics.stringWidth(s) + 10;
                if(progressentry.need >= 10240)
                    s = String.valueOf(progressentry.need / 1024) + properties.getProperty("progress.kb");
                else
                    s = String.valueOf(progressentry.need) + properties.getProperty("progress.bytes");
                k = Math.max(k, dimension.width - 10 - fontmetrics.stringWidth(s));
                g.drawString(s, k, (35 + i * j + fontmetrics.getHeight()) - (fontmetrics.getDescent() + 1));
            }
        }
        paintUpdate(g, i, progressentry);
    }

    void paintUpdate(Graphics g, int i, ProgressEntry progressentry)
    {
        Dimension dimension = size();
        FontMetrics fontmetrics = g.getFontMetrics(meterFont);
        int j = 12 + fontmetrics.getHeight() + 5;
        int k = dimension.width - 20;
        int l = 35 + fontmetrics.getHeight() + i * j;
        int i1 = progressentry.read != 0 ? (int)(((float)k / (float)progressentry.need) * (float)progressentry.read) : 1;
        g.setColor(Color.black);
        g.drawRect(10, l, k - 1, 12);
        g.setColor(colors[progressentry.type]);
        g.fillRect(11, l + 1, i1 - 1, 11);
    }

    public void paint(Graphics g)
    {
        Dimension dimension = size();
        g.setFont(legendFont);
        FontMetrics fontmetrics = g.getFontMetrics();
        int i = 10;
        for(int j = 0; j < colors.length; j++)
        {
            g.setColor(colors[j]);
            g.fillRect(i + 1, 6, 13, 13);
            g.setColor(Color.black);
            g.drawRect(i, 5, 14, 14);
            i += 18;
            g.drawString(names[j], i, 17);
            i += fontmetrics.stringWidth(names[j]) + 10;
        }

        g.draw3DRect(0, 25, dimension.width, 2, false);
        ProgressEntry aprogressentry[] = ProgressData.pdata.getStreams();
        g.clipRect(10, 0, dimension.width - 20, dimension.height);
        g.setFont(meterFont);
        for(int k = 0; k < aprogressentry.length; k++)
        {
            ProgressEntry progressentry = aprogressentry[k];
            if(progressentry != null)
            {
                if(progressentry.need == progressentry.read && progressentry.read != 0)
                    eraseEntry(g, k);
                else
                    paint(g, k, progressentry);
            } else
            {
                eraseEntry(g, k);
            }
        }

    }

    static Font meterFont = null;
    static Font legendFont = null;
    static final int KLIMIT = 10240;
    static final int lmargin = 10;
    static final int barheight = 12;
    static final int ystart = 35;
    private static HJBProperties properties;
    static final Color colors[];
    static final String names[];
    private static boolean lazyLoadingNotDone = true;

    static 
    {
        properties = HJBProperties.getHJBProperties("hjbrowser");
        colors = (new Color[] {
            properties.getColor("progress.html.color", null), properties.getColor("progress.image.color", null), properties.getColor("progress.class.color", null), properties.getColor("progress.audio.color", null), properties.getColor("progress.other.color", null), properties.getColor("progress.connect.color", null)
        });
        names = (new String[] {
            properties.getProperty("progress.html.name"), properties.getProperty("progress.image.name"), properties.getProperty("progress.class.name"), properties.getProperty("progress.audio.name"), properties.getProperty("progress.other.name"), properties.getProperty("progress.connect.name")
        });
    }
}
