// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HJWindowManager.java

package sunw.hotjava;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.Beans;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import sunw.hotjava.applet.AppletManager;
import sunw.hotjava.bean.CookieJarInterface;
import sunw.hotjava.bean.CurrentDocument;
import sunw.hotjava.bean.HTMLBrowsable;
import sunw.hotjava.bean.LinkEvent;
import sunw.hotjava.bean.LinkListener;
import sunw.hotjava.bean.URLPooler;
import sunw.hotjava.misc.GlobalRegistry;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.misc.RequestProcessor;

// Referenced classes of package sunw.hotjava:
//            HJFrame, PeriodicSaveManager

public class HJWindowManager
    implements LinkListener
{

    public static HJWindowManager getHJWindowManager()
    {
        if(hjwm == null)
            hjwm = new HJWindowManager();
        return hjwm;
    }

    private HJWindowManager()
    {
        properties = HJBProperties.getHJBProperties("hjbrowser");
        frames = new Vector();
        initBeans();
    }

    private void initBeans()
    {
        try
        {
            if(urlpool == null)
                urlpool = (URLPooler)Beans.instantiate(null, properties.getProperty("hotjava.urlpool.manager", "sunw.hotjava.bean.URLPool"));
        }
        catch(ClassNotFoundException _ex)
        {
            System.out.println("Temporary warning message: couldn't find URLPool!! Get a new message for this warning in hjbrowserResourceBundle");
        }
        catch(IOException _ex) { }
        try
        {
            if(cookies == null)
            {
                cookies = (CookieJarInterface)Beans.instantiate(null, properties.getProperty("hotjava.cookie.manager", "sunw.hotjava.bean.CookieJar"));
                GlobalRegistry.setCookiesManager(cookies);
                return;
            }
        }
        catch(ClassNotFoundException _ex)
        {
            System.out.println("Temporary warning message: couldn't find URLPool!! Get a new message for this warning in hjbrowserResourceBundle");
            return;
        }
        catch(IOException _ex) { }
    }

    public URLPooler getURLPoolManager()
    {
        return urlpool;
    }

    public CookieJarInterface getCookiesManager()
    {
        return cookies;
    }

    public HJFrame createFrame(URL url)
    {
        if(windowAdapter == null)
            windowAdapter = new WindowAdapter() {

                public void windowActivated(WindowEvent windowevent)
                {
                    HJWindowManager.lastActiveFrame = (HJFrame)windowevent.getWindow();
                    HJBProperties.setLastFocusHolder(HJWindowManager.lastActiveFrame);
                }

            }
;
        HJFrame hjframe = new HJFrame(url);
        synchronized(frames)
        {
            frames.addElement(hjframe);
        }
        hjframe.addWindowListener(windowAdapter);
        hjframe.getHTMLBrowsable().addLinkListener(this);
        if(appletMan == null)
            appletMan = hjframe.getAppletManager();
        return hjframe;
    }

    public HJFrame createFrame(URL url, boolean flag, boolean flag1, boolean flag2, boolean flag3, Point point, Dimension dimension)
    {
        if(windowAdapter == null)
            windowAdapter = new WindowAdapter() {

                public void windowActivated(WindowEvent windowevent)
                {
                    HJWindowManager.lastActiveFrame = (HJFrame)windowevent.getWindow();
                    HJBProperties.setLastFocusHolder(HJWindowManager.lastActiveFrame);
                }

            }
;
        HJFrame hjframe = new HJFrame(url, flag, flag1, flag2, flag3, point, dimension);
        synchronized(frames)
        {
            frames.addElement(hjframe);
        }
        hjframe.addWindowListener(windowAdapter);
        hjframe.getHTMLBrowsable().addLinkListener(this);
        if(appletMan == null)
            appletMan = hjframe.getAppletManager();
        return hjframe;
    }

    public HJFrame createNoDecorFrame(URL url, HJFrame hjframe)
    {
        if(windowAdapter == null)
            windowAdapter = new WindowAdapter() {

                public void windowActivated(WindowEvent windowevent)
                {
                    HJWindowManager.lastActiveFrame = (HJFrame)windowevent.getWindow();
                    HJBProperties.setLastFocusHolder(HJWindowManager.lastActiveFrame);
                }

            }
;
        HTMLBrowsable htmlbrowsable = hjframe.getHTMLBrowsable();
        prevDocument.documentURL = htmlbrowsable.getDocumentURL();
        prevDocument.documentTitle = htmlbrowsable.getDocumentTitle();
        prevDocument.documentSource = htmlbrowsable.getDocumentSource();
        Point point = hjframe.getLocation();
        point.translate(-50, 50);
        HJFrame hjframe1 = new HJFrame(url, point);
        synchronized(frames)
        {
            frames.addElement(hjframe1);
        }
        hjframe1.addWindowListener(windowAdapter);
        hjframe1.getHTMLBrowsable().addLinkListener(this);
        if(appletMan == null)
            appletMan = hjframe1.getAppletManager();
        return hjframe1;
    }

    public void closeFrame(Window window)
    {
        synchronized(frames)
        {
            if(window == null)
                return;
            frames.removeElement(window);
            window.setVisible(false);
            window.dispose();
            if(frames.size() == 0)
                quit();
        }
    }

    public synchronized void quit()
    {
        PeriodicSaveManager.getPeriodicSaveManager().prepareToExit();
        try
        {
            Thread.sleep(1000L);
        }
        catch(InterruptedException _ex) { }
        synchronized(frames)
        {
            String s = properties.getProperty("appletpanel.thread.cleanup.interval");
            int i = 5000;
            if(s != null)
                i = Integer.parseInt(s);
            hideAllWindows();
            startQuitWatchdog();
            appletMan.destroyApplets(i);
            for(int j = 0; j < frames.size(); j++)
            {
                HJFrame hjframe = (HJFrame)frames.elementAt(j);
                hjframe.dispose();
            }

            frames.setSize(0);
            System.exit(0);
        }
    }

    private void startQuitWatchdog()
    {
        String s = "hotjava.quit.watchdog.timeout";
        final int maxDelay = properties.getInteger(s, 6000);
        (new Thread() {

            public void run()
            {
                try
                {
                    Thread.sleep(maxDelay);
                }
                catch(InterruptedException _ex)
                {
                    System.out.println("Watchdog interrupted!");
                }
                String s1 = "Quit watchdog timer expired.";
                s1.concat(" -- Calling System.exit()");
                System.out.println(s1);
                System.exit(1);
            }

        }
).start();
    }

    private void hideAllWindows()
    {
        RequestProcessor.getHJBeanQueue().postRequest(new sunw.hotjava.misc.RequestProcessor.Request() {

            public void execute()
            {
                synchronized(HJWindowManager.frames)
                {
                    for(int i = 0; i < HJWindowManager.frames.size(); i++)
                    {
                        HJFrame hjframe = (HJFrame)HJWindowManager.frames.elementAt(i);
                        hjframe.setVisible(false);
                    }

                }
            }

        }
);
    }

    public void resetDecorationsAllWindows()
    {
        RequestProcessor.getHJBeanQueue().postRequest(new sunw.hotjava.misc.RequestProcessor.Request() {

            public void execute()
            {
                synchronized(HJWindowManager.frames)
                {
                    for(int i = 0; i < HJWindowManager.frames.size(); i++)
                    {
                        HJFrame hjframe = (HJFrame)HJWindowManager.frames.elementAt(i);
                        if(hjframe.isDecorated())
                            hjframe.reset();
                    }

                }
            }

        }
);
    }

    public void setDocFontAllWindows()
    {
        RequestProcessor.getHJBeanQueue().postRequest(new sunw.hotjava.misc.RequestProcessor.Request() {

            public void execute()
            {
                synchronized(HJWindowManager.frames)
                {
                    for(int i = 0; i < HJWindowManager.frames.size(); i++)
                    {
                        HJFrame hjframe = (HJFrame)HJWindowManager.frames.elementAt(i);
                        hjframe.getHTMLBrowsable().setDocFontNotInPlace();
                    }

                }
            }

        }
);
    }

    public void openFrame(String s, URL url)
    {
        HJFrame hjframe = findFrameNamed(s, lastActiveFrame.getHTMLBrowsable());
        if(hjframe != null && s.equals("_hotjava_help") && hjframe.isIconified())
        {
            hjframe.getHTMLBrowsable().setName("");
            hjframe = null;
        }
        if(hjframe == null || "_blank".equals(s))
        {
            hjframe = createFrame(url);
            hjframe.getHTMLBrowsable().setName(s);
            hjframe.show();
            return;
        }
        hjframe.getHTMLBrowsable().setDocumentURL(url);
        if(s.equals("_hotjava_help"))
            hjframe.toFront();
    }

    public HTMLBrowsable openFrame(String s, URL url, boolean flag, boolean flag1, boolean flag2, boolean flag3, Point point, 
            Dimension dimension)
    {
        HJFrame hjframe = findFrameNamed(s, lastActiveFrame.getHTMLBrowsable());
        if(hjframe == null || "_blank".equals(s))
        {
            hjframe = createFrame(url, flag, flag1, flag2, flag3, point, dimension);
            hjframe.getHTMLBrowsable().setName(s);
            hjframe.show();
        } else
        {
            LinkEvent linkevent = url != null ? new LinkEvent(null, s, url.toExternalForm()) : new LinkEvent(null, s, "");
            hjframe.getHTMLBrowsable().handleLinkEvent(linkevent);
        }
        return hjframe.getHTMLBrowsable();
    }

    public HJFrame cloneFrame(HJFrame hjframe, URL url)
    {
        HJFrame hjframe1 = createFrame(url);
        if(hjframe != null)
        {
            Dimension dimension = hjframe.getSize();
            Point point = hjframe.getLocation();
            Insets insets = hjframe.getInsets();
            hjframe1.setBounds(point.x + insets.top, point.y + insets.top, dimension.width, dimension.height);
        }
        hjframe1.show();
        return hjframe1;
    }

    public void showStatusAll(String s)
    {
        for(int i = 0; i < frames.size(); i++)
        {
            HJFrame hjframe = (HJFrame)frames.elementAt(i);
            hjframe.showStatus(s);
        }

    }

    public HJFrame[] getAllFrames()
    {
        HJFrame ahjframe[];
        synchronized(frames)
        {
            ahjframe = new HJFrame[frames.size()];
            frames.copyInto(ahjframe);
        }
        return ahjframe;
    }

    public HJFrame getLastFocusHolder()
    {
        return lastActiveFrame;
    }

    public CurrentDocument getPrevDocument()
    {
        return prevDocument;
    }

    public int noOfFrames()
    {
        return frames.size();
    }

    public synchronized HTMLBrowsable hyperlinkPerformed(LinkEvent linkevent)
    {
        HTMLBrowsable htmlbrowsable = null;
        String s = linkevent.getTarget();
        if(s == null || s.equalsIgnoreCase("_self") || s.equalsIgnoreCase("_parent") || s.startsWith("_refresh") || s.equalsIgnoreCase("_top"))
        {
            HJFrame hjframe = findFrameNamed(linkevent.getFrameSource(), linkevent.getBrowser());
            HTMLBrowsable htmlbrowsable1 = hjframe.getHTMLBrowsable();
            prevDocument.documentURL = htmlbrowsable1.getDocumentURL();
            prevDocument.documentTitle = htmlbrowsable1.getDocumentTitle();
            prevDocument.documentSource = htmlbrowsable1.getDocumentSource();
            htmlbrowsable = hjframe.getHTMLBrowsable();
            htmlbrowsable.handleLinkEvent(linkevent);
        } else
        {
            Object obj = null;
            if(!s.equalsIgnoreCase("_blank"))
            {
                HJFrame hjframe1 = findFrameNamed(s, linkevent.getBrowser());
                if(hjframe1 != null)
                {
                    htmlbrowsable = hjframe1.getHTMLBrowsable();
                    htmlbrowsable.handleLinkEvent(linkevent);
                    return htmlbrowsable;
                }
            }
            String s1 = linkevent.getHREF();
            if(s1 == null)
                s1 = properties.getProperty("hotjava.blank.html", "doc:/lib/hotjava/blank.html");
            try
            {
                URL url = new URL(s1);
                HJFrame hjframe2 = (HJFrame)frames.elementAt(0);
                HJFrame hjframe3;
                if(s.equals("_mailto"))
                {
                    hjframe3 = createNoDecorFrame(url, hjframe2);
                } else
                {
                    hjframe3 = createFrame(url);
                    Dimension dimension = hjframe2.getSize();
                    Point point = hjframe2.getLocation();
                    Insets insets = hjframe2.getInsets();
                    hjframe3.setBounds(point.x + insets.top, point.y + insets.top, dimension.width, dimension.height);
                }
                hjframe3.show();
                htmlbrowsable = hjframe3.getHTMLBrowsable();
                if(!s.equalsIgnoreCase("_blank"))
                    htmlbrowsable.setName(s);
            }
            catch(MalformedURLException _ex)
            {
                System.out.println("Malformed URL Exception " + s1);
                Thread.dumpStack();
            }
        }
        return htmlbrowsable;
    }

    private HJFrame findFrameNamed(String s, HTMLBrowsable htmlbrowsable)
    {
        synchronized(frames)
        {
            int i = frames.size();
            if(isBrowserContainFrame(htmlbrowsable, s))
            {
                for(int j = 0; j < i; j++)
                {
                    HJFrame hjframe3 = (HJFrame)frames.elementAt(j);
                    if(hjframe3.getHTMLBrowsable() == htmlbrowsable)
                    {
                        HJFrame hjframe = hjframe3;
                        return hjframe;
                    }
                }

            } else
            {
                for(int k = 0; k < i; k++)
                {
                    HJFrame hjframe4 = (HJFrame)frames.elementAt(k);
                    if(isBrowserContainFrame(hjframe4.getHTMLBrowsable(), s))
                    {
                        HJFrame hjframe1 = hjframe4;
                        return hjframe1;
                    }
                }

            }
            HJFrame hjframe2 = null;
            return hjframe2;
        }
    }

    private boolean isBrowserContainFrame(HTMLBrowsable htmlbrowsable, String s)
    {
        if(htmlbrowsable == null)
            return false;
        if(htmlbrowsable.getName().equalsIgnoreCase(s))
            return true;
        String as[] = htmlbrowsable.getFrameList();
        for(int i = 0; as != null && i < as.length; i++)
            if(as[i] != null && as[i].equalsIgnoreCase(s))
                return true;

        return false;
    }

    static final int defaultXOffset = 25;
    static final int defaultYOffset = 10;
    private static WindowAdapter windowAdapter;
    private static HJFrame lastActiveFrame;
    private static CurrentDocument prevDocument = new CurrentDocument();
    private static AppletManager appletMan;
    private static HJWindowManager hjwm;
    private static URLPooler urlpool;
    private static CookieJarInterface cookies;
    private static CookieJarInterface cookieJar;
    private static final String urlpoolManagerClass = "sunw.hotjava.bean.URLPool";
    private static final String cookieManagerClass = "sunw.hotjava.bean.CookieJar";
    HJBProperties properties;
    private static Vector frames;




}
