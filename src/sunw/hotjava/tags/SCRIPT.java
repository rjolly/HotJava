// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SCRIPT.java

package sunw.hotjava.tags;

import java.awt.Graphics;
import java.io.*;
import java.net.*;
import sunw.hotjava.bean.CookieJarInterface;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.Globals;
import sunw.html.Attributes;

public class SCRIPT extends TagItem
{

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        formatstate.pos += getOffset() + 1 << 12;
        return false;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        int k = getOffset();
        if(k < 0)
            k = 1;
        formatter.displayPos += k << 12;
        return 0;
    }

    public boolean measureStartTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        measurestate.pos += getOffset() + 1 << 12;
        return false;
    }

    public void init(Document document)
    {
        Attributes attributes = getAttributes();
        if(attributes == null)
            return;
        language = attributes.get("language");
        String s = attributes.get("src");
        if(s == null)
            return;
        BufferedReader bufferedreader = null;
        try
        {
            sourceURL = new URL(document.getURL(), s);
            if(!"file".equals(document.getURL().getProtocol()) && "file".equals(sourceURL.getProtocol()))
            {
                System.out.println("Access to script at " + sourceURL.toExternalForm() + " disallowed from non-file: URL");
                sourceURL = null;
                return;
            }
            CookieJarInterface cookiejarinterface = HotJavaBrowserBean.getCookiesManager();
            boolean flag = false;
            int i = 0;
            do
            {
                URLConnection urlconnection = sourceURL.openConnection();
                if(cookiejarinterface != null)
                    cookiejarinterface.applyRelevantCookies(sourceURL, urlconnection);
                Globals.tryToStopFollowRedirects(urlconnection);
                if(bufferedreader != null)
                    bufferedreader.close();
                bufferedreader = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
                flag = false;
                if(urlconnection instanceof HttpURLConnection)
                {
                    HttpURLConnection httpurlconnection = (HttpURLConnection)urlconnection;
                    int k = httpurlconnection.getResponseCode();
                    if(k >= 300 && k <= 305 && k != 304)
                    {
                        String s1 = httpurlconnection.getHeaderField("Location");
                        if(s1 != null && i < 5)
                        {
                            flag = true;
                            sourceURL = new URL(httpurlconnection.getURL(), s1);
                            i++;
                        }
                    }
                }
            } while(flag);
            char ac[] = new char[512];
            int j = 0;
            source = new StringBuffer(512);
            while(j >= 0) 
            {
                j = bufferedreader.read(ac);
                if(j != -1)
                    source.append(ac, 0, j);
            }
        }
        catch(MalformedURLException _ex) { }
        catch(IOException _ex) { }
        finally
        {
            if(bufferedreader != null)
                try
                {
                    bufferedreader.close();
                }
                catch(IOException _ex) { }
        }
        hasSource = true;
    }

    public boolean hasSource()
    {
        return hasSource;
    }

    public String getSource()
    {
        if(source != null)
            return source.toString();
        else
            return "";
    }

    public String getLanguage()
    {
        return language;
    }

    public URL getSourceURL()
    {
        return sourceURL;
    }

    public SCRIPT()
    {
        hasSource = false;
    }

    private StringBuffer source;
    private boolean hasSource;
    private URL sourceURL;
    private String language;
    private static final int BUFCONST = 512;
}
