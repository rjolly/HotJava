// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletIOException.java

package sunw.hotjava.applet;

import java.io.IOException;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.security.SecurityGlobals;

public class AppletIOException extends IOException
{

    public AppletIOException(String s)
    {
        super(s);
        key = s;
    }

    public AppletIOException(String s, Object obj)
    {
        this(s);
        arg = obj;
    }

    public String getLocalizedMessage()
    {
        String s = arg != null ? arg.toString() : "??";
        return props.getPropertyReplace(propPrefix + key, s);
    }

    private static HJBProperties props = HJBProperties.getHJBProperties("securityProperties");
    protected static String propPrefix = "appletioexception.";
    protected String key;
    protected Object arg;

}
