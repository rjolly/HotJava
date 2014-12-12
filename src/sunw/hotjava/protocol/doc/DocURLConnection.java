// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocURLConnection.java

package sunw.hotjava.protocol.doc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.net.www.MessageHeader;
import sun.net.www.URLConnection;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;

public class DocURLConnection extends URLConnection
{

    DocURLConnection(URL url)
    {
        super(url);
    }

    private URL makeURL(String s)
        throws MalformedURLException
    {
        if(s != null && s.charAt(s.length() - 1) != '/')
            s = s + "/";
        return new URL(s);
    }

    private URL[] getLocations()
    {
        Vector vector = new Vector();
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        String s = hjbproperties.getProperty("hotjava.home");
        if(s == null)
            s = "/usr/local/hotjava";
        try
        {
            URL url = makeURL("file:" + s);
            vector.addElement(url);
        }
        catch(MalformedURLException _ex) { }
        String s1 = hjbproperties.getProperty("doc.path");
        if(s1 != null)
        {
            for(StringTokenizer stringtokenizer = new StringTokenizer(s1, "|"); stringtokenizer.hasMoreTokens();)
                try
                {
                    URL url1 = makeURL("file:" + stringtokenizer.nextToken());
                    vector.addElement(url1);
                }
                catch(MalformedURLException _ex) { }

        }
        String s2 = hjbproperties.getProperty("doc.url");
        if(s2 != null)
            try
            {
                URL url2 = makeURL(s2);
                vector.addElement(url2);
            }
            catch(MalformedURLException _ex) { }
        URL aurl[] = new URL[vector.size()];
        for(int i = 0; i < vector.size(); i++)
            aurl[i] = (URL)vector.elementAt(i);

        return aurl;
    }

    String[] getVersions()
    {
        String s = getURL().getFile();
        if(s.charAt(0) == '/')
            s = s.substring(1);
        int i = s.lastIndexOf('/');
        String as[];
        if(i <= 0)
        {
            as = new String[1];
        } else
        {
            as = new String[3];
            String s1 = s.substring(0, i);
            String s2 = s.substring(i, s.length());
            as[0] = s1 + "/" + Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry() + s2;
            as[1] = s1 + "/" + Locale.getDefault().getLanguage() + s2;
        }
        as[as.length - 1] = s;
        return as;
    }

    private boolean createConnection(String as[], URL aurl[], boolean flag)
    {
        for(int i = 0; i < as.length; i++)
        {
            for(int j = 0; j < aurl.length; j++)
                try
                {
                    URL url = flag ? ClassLoader.getSystemResource(as[i]) : new URL(aurl[j], as[i]);
                    if(url == null)
                        continue;
                    if(url.getProtocol().equals("file"))
                    {
                        String s = url.getFile();
                        File file = new File(s);
                        if(!file.exists())
                            continue;
                    }
                    java.net.URLConnection urlconnection = url.openConnection();
                    is = urlconnection.getInputStream();
                    if(is != null && (urlconnection instanceof HttpURLConnection))
                    {
                        HttpURLConnection httpurlconnection = (HttpURLConnection)urlconnection;
                        int k = httpurlconnection.getResponseCode();
                        if(k >= 400 && k < 500)
                        {
                            is.close();
                            is = null;
                        }
                    }
                    if(is != null)
                    {
                        MessageHeader messageheader = new MessageHeader();
                        String s1 = urlconnection.getContentType();
                        if(s1 != null)
                            messageheader.add("content-type", s1);
                        int l = urlconnection.getContentLength();
                        if(l >= 0)
                            messageheader.add("Content-length", String.valueOf(l));
                        setProperties(messageheader);
                        super.connected = true;
                        return true;
                    }
                }
                catch(IOException _ex) { }

        }

        return false;
    }

    public synchronized void connect()
        throws IOException
    {
        if(super.connected)
            return;
        String as[] = getVersions();
        URL aurl[] = getLocations();
        if(createConnection(as, aurl, true))
            return;
        if(createConnection(as, aurl, false))
            return;
        String s = getURL().getFile();
        s = s.substring(1);
        is = ClassLoader.getSystemResourceAsStream(s);
        if(is != null)
        {
            super.connected = true;
            return;
        } else
        {
            throw new FileNotFoundException(super.url.toExternalForm());
        }
    }

    public synchronized InputStream getInputStream()
        throws IOException
    {
        if(!super.connected)
            connect();
        return is;
    }

    InputStream is;
}
