// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Handler.java

package sunw.hotjava.protocol.mailto;

import java.net.*;

// Referenced classes of package sunw.hotjava.protocol.mailto:
//            MailToURLConnection

public class Handler extends URLStreamHandler
{

    public synchronized URLConnection openConnection(URL url)
    {
        return new MailToURLConnection(url);
    }

    public void parseURL(URL url, String s, int i, int j)
    {
        String s1 = url.getProtocol();
        String s2 = "";
        int k = url.getPort();
        String s3 = "";
        if(i < j)
            s3 = s.substring(i, j);
        setURL(url, s1, s2, k, s3, null);
    }

    public Handler()
    {
    }
}
