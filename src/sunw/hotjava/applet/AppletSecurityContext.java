// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletSecurityContext.java

package sunw.hotjava.applet;

import java.net.URL;

public class AppletSecurityContext
{

    public AppletSecurityContext(URL url, Object obj)
    {
        codebase = url;
        dom = obj;
    }

    public URL getCodeBase()
    {
        return codebase;
    }

    public Object getDOM()
    {
        return dom;
    }

    URL codebase;
    Object dom;
}
