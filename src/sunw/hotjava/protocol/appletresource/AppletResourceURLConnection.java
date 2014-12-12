// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletResourceURLConnection.java

package sunw.hotjava.protocol.appletresource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import sun.net.www.MessageHeader;
import sun.net.www.URLConnection;
import sunw.hotjava.applet.AppletClassLoader;
import sunw.hotjava.applet.AppletResourceLoader;
import sunw.hotjava.applet.BasicAppletManager;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.security.SecurityGlobals;

public class AppletResourceURLConnection extends URLConnection
{

    AppletResourceURLConnection(URL url)
    {
        super(url);
    }

    public void connect()
        throws IOException
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("securityProperties");
        if(super.connected)
            return;
        URL url = getURL();
        String s = url.getFile();
        if(s.startsWith("/"))
            s = s.substring(1);
        StringTokenizer stringtokenizer = new StringTokenizer(s, "!");
        URL url1 = null;
        boolean flag = false;
        try
        {
            url1 = new URL(stringtokenizer.nextToken());
            String s1 = stringtokenizer.nextToken();
            String s2 = stringtokenizer.nextToken();
        }
        catch(NoSuchElementException _ex)
        {
            flag = true;
        }
        catch(MalformedURLException _ex)
        {
            flag = true;
        }
        if(!flag)
        {
            String s3 = url.getHost();
            String s5 = url1.getHost();
            if(s3 == null || !s3.equals(s5) || url.getPort() != url1.getPort())
                flag = true;
        }
        if(flag)
        {
            String s4 = "appletresource.notfound";
            String s6 = getURL().toString();
            String s8 = hjbproperties.getPropertyReplace(s4, s6);
            throw new IOException(s8);
        }
        AppletClassLoader appletclassloader = BasicAppletManager.getClassLoaderIfExists(url1);
        if(appletclassloader != null)
        {
            AppletResourceLoader appletresourceloader = appletclassloader.getResourceLoader();
            resource = appletresourceloader.getResourceBytes(url);
            if(resource != null)
            {
                MessageHeader messageheader = new MessageHeader();
                messageheader.add("content-type", appletresourceloader.getResourceType(url));
                messageheader.add("content-length", String.valueOf(resource.length));
                setProperties(messageheader);
            }
        }
        if(resource == null)
        {
            String s7 = "appletresource.notfound";
            String s9 = getURL().toString();
            String s10 = hjbproperties.getPropertyReplace(s7, s9);
            throw new IOException(s10);
        } else
        {
            super.connected = true;
            return;
        }
    }

    public synchronized InputStream getInputStream()
        throws IOException
    {
        connect();
        return new ByteArrayInputStream(resource);
    }

    byte resource[];
}
