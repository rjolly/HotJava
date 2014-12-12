// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Handler.java

package sunw.hotjava.protocol.doc;

import java.io.IOException;
import java.net.*;

// Referenced classes of package sunw.hotjava.protocol.doc:
//            DocURLConnection

public class Handler extends URLStreamHandler
{

    public Handler()
    {
    }

    public synchronized URLConnection openConnection(URL url)
        throws IOException
    {
        return new DocURLConnection(url);
    }
}
