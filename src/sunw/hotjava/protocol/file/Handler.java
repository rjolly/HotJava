// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Handler.java

package sunw.hotjava.protocol.file;

import java.io.IOException;
import java.net.*;

// Referenced classes of package sunw.hotjava.protocol.file:
//            FileURLConnection

public class Handler extends URLStreamHandler
{

    public synchronized URLConnection openConnection(URL url)
        throws IOException
    {
        String s = url.getHost();
        if(s == null || s.equals("") || s.equals("~") || s.equals("localhost"))
            return new FileURLConnection(url);
        URLConnection urlconnection;
        try
        {
            URL url1 = new URL("ftp", s, url.getFile() + (url.getRef() != null ? "#" + url.getRef() : ""));
            urlconnection = url1.openConnection();
        }
        catch(IOException _ex)
        {
            urlconnection = null;
        }
        if(urlconnection == null)
            throw new IOException("Unable to connect to: " + url.toExternalForm());
        else
            return urlconnection;
    }

    public Handler()
    {
    }

    private static String installDirectory = System.getProperty("hotjava.home");

}
