// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletImageRef.java

package sunw.hotjava.applet;

import java.awt.Toolkit;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import sun.awt.image.URLImageSource;
import sun.misc.Ref;
import sunw.hotjava.bean.CookieJarInterface;
import sunw.hotjava.bean.HotJavaBrowserBean;

public class AppletImageRef extends Ref
{

    public AppletImageRef(URL url1)
    {
        reload = false;
        url = url1;
    }

    public void flush()
    {
        super.flush();
    }

    public Object get(URL url1)
    {
        referer = url1;
        return super.get();
    }

    public Object get(boolean flag, URL url1)
    {
        reload = flag;
        referer = url1;
        return super.get();
    }

    public int getReloadCount()
    {
        return reloadCount;
    }

    public Object reconstitute()
    {
        reloadCount++;
        java.awt.Image image;
        try
        {
            URLConnection urlconnection = url.openConnection();
            CookieJarInterface cookiejarinterface = HotJavaBrowserBean.getCookiesManager();
            if(cookiejarinterface != null)
                cookiejarinterface.applyRelevantCookies(url, urlconnection);
            if(reload)
            {
                urlconnection.setRequestProperty("Pragma", "no-cache");
                urlconnection.setRequestProperty("Cache-Control", "no-cache");
            }
            if(referer != null)
                urlconnection.setRequestProperty("Referer", referer.toExternalForm());
            image = Toolkit.getDefaultToolkit().createImage(new URLImageSource(url, urlconnection));
        }
        catch(Exception exception)
        {
            System.out.println(exception);
            image = Toolkit.getDefaultToolkit().createImage(new URLImageSource(url));
        }
        return image;
    }

    URL url;
    private int reloadCount;
    private boolean reload;
    private URL referer;
}
