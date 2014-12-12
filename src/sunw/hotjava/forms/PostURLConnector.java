// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PostURLConnector.java

package sunw.hotjava.forms;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import sunw.hotjava.bean.CookieJarInterface;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.misc.URLConnector;

public class PostURLConnector
    implements URLConnector
{

    public PostURLConnector(URL url, String s)
    {
        referer = url;
        formData = s;
    }

    public URLConnection openConnection(URL url)
        throws IOException
    {
        CookieJarInterface cookiejarinterface = HotJavaBrowserBean.getCookiesManager();
        URLConnection urlconnection = url.openConnection();
        if(cookiejarinterface != null)
            cookiejarinterface.applyRelevantCookies(url, urlconnection);
        if(referer != null)
            urlconnection.setRequestProperty("Referer", referer.toExternalForm());
        urlconnection.setDoOutput(true);
        PrintWriter printwriter = null;
        try
        {
            printwriter = new PrintWriter(new OutputStreamWriter(urlconnection.getOutputStream()));
            printwriter.print(formData);
            printwriter.flush();
        }
        finally
        {
            if(printwriter != null)
                printwriter.close();
        }
        return urlconnection;
    }

    private URL referer;
    private String formData;
}
