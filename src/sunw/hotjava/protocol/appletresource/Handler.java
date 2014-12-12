// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Handler.java

package sunw.hotjava.protocol.appletresource;

import java.net.*;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

// Referenced classes of package sunw.hotjava.protocol.appletresource:
//            AppletResourceURLConnection

public class Handler extends URLStreamHandler
{

    public synchronized URLConnection openConnection(URL url)
    {
        return new AppletResourceURLConnection(url);
    }

    public void parseURL(URL url, String s, int i, int j)
    {
        String s1 = url.getProtocol();
        String s2 = "";
        int k = 0;
        String s3 = "";
        if(i <= j - 2 && s.charAt(i) == '/' && s.charAt(i + 1) == '/')
        {
            i += 2;
            int l = s.indexOf('/', i);
            if(l < 0)
                i = j;
            else
                i = l;
        }
        StringTokenizer stringtokenizer = new StringTokenizer(s.substring(i + 1, j), "!");
        try
        {
            URL url1 = new URL(stringtokenizer.nextToken());
            s2 = url1.getHost();
            k = url1.getPort();
        }
        catch(NoSuchElementException _ex) { }
        catch(MalformedURLException _ex) { }
        if(i < j)
            s3 = s.substring(i, j);
        setURL(url, s1, s2, k, s3, null);
    }

    public Handler()
    {
    }
}
