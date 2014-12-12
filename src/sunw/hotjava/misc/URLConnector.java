// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   URLConnector.java

package sunw.hotjava.misc;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public interface URLConnector
{

    public abstract URLConnection openConnection(URL url)
        throws IOException;
}
