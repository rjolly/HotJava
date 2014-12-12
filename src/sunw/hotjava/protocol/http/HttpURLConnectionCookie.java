// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HttpURLConnectionCookie.java

package sunw.hotjava.protocol.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import sun.net.ProgressData;
import sun.net.ProgressEntry;
import sun.net.www.protocol.http.HttpURLConnection;
import sunw.hotjava.bean.CookieJarInterface;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.misc.*;

// Referenced classes of package sunw.hotjava.protocol.http:
//            Handler

public class HttpURLConnectionCookie extends HttpURLConnection
{
    class HandleUnclosedStream extends sunw.hotjava.misc.RequestProcessor.Request
    {

        public void setProgressEntry(ProgressEntry progressentry)
        {
            pe = progressentry;
        }

        public void execute()
        {
            ProgressData.pdata.unregister(pe);
            pe = null;
        }

        ProgressEntry pe;

        public HandleUnclosedStream()
        {
        }
    }


    protected HttpURLConnectionCookie(URL url, Handler handler)
        throws IOException
    {
        super(url, handler);
        inputGiven = false;
        unclosedStreamHandler = new HandleUnclosedStream();
    }

    public HttpURLConnectionCookie(URL url, String s, int i)
    {
        super(url, s, i);
        inputGiven = false;
        unclosedStreamHandler = new HandleUnclosedStream();
    }

    public void connect()
        throws IOException
    {
        if(!super.connected && cookiesObj != null)
        {
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            String s = System.getProperty("http.agent", "hotjava");
            String s1 = System.getProperty("os.arch", "") + " [" + Locale.getDefault().getLanguage() + "] " + System.getProperty("os.name", "") + " " + System.getProperty("os.version", "") + "; " + hjbproperties.getProperty("browser.differentiator", "");
            s = s + " (" + s1 + ")";
            setRequestProperty("User-Agent", s);
            cookiesObj.applyRelevantCookies(super.url, this);
        }
        super.connect();
    }

    public InputStream getInputStream()
        throws IOException
    {
        InputStream inputstream = super.getInputStream();
        synchronized(this)
        {
            if(!inputGiven)
            {
                inputGiven = true;
                if(cookiesObj != null)
                    cookiesObj.recordAnyCookies(this);
            }
        }
        return inputstream;
    }

    public String getContentType()
    {
        String s = getHeaderField("content-type");
        if(s != null && s.endsWith("x-x509-ca-cert"))
            s = s.replace('-', '_');
        return s;
    }

    public void finalize()
    {
//        if(super.pe != null)
//        {
//            unclosedStreamHandler.setProgressEntry(super.pe);
//            RequestProcessor.getHJBeanQueue().postRequest(unclosedStreamHandler);
//            super.pe = null;
//        }
    }

    private boolean inputGiven;
    private HandleUnclosedStream unclosedStreamHandler;
    private static CookieJarInterface cookiesObj = HotJavaBrowserBean.getCookiesManager();

}
