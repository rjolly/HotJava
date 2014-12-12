// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Handler.java

package sun.net.www.protocol.nfs;

import java.net.*;

// Referenced classes of package sun.net.www.protocol.nfs:
//            NfsURLConnection

public class Handler extends URLStreamHandler
{

    public synchronized URLConnection openConnection(URL url)
    {
        return new NfsURLConnection(url);
    }

    public Handler()
    {
    }
}
