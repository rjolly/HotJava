// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Handler.java

package sunw.hotjava.protocol.http;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

// Referenced classes of package sunw.hotjava.protocol.http:
//            HttpURLConnectionCookie

public class Handler extends sun.net.www.protocol.http.Handler
{

    public Handler()
    {
    }

    public Handler(String s, int i)
    {
        super(s, i);
    }

    protected URLConnection openConnection(URL url)
        throws IOException
    {
        return new HttpURLConnectionCookie(url, this);
    }
}
