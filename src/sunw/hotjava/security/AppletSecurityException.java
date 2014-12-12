// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletSecurityException.java

package sunw.hotjava.security;

import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.security:
//            SecurityGlobals

public class AppletSecurityException extends SecurityException
{

    public AppletSecurityException(String s)
    {
        super(s);
        key = s;
    }

    public AppletSecurityException(String s, String s1)
    {
        this(s);
        msgobj = new String[1];
        msgobj[0] = s1;
    }

    public AppletSecurityException(String s, String s1, String s2)
    {
        this(s);
        msgobj = new String[2];
        msgobj[0] = s1;
        msgobj[1] = s2;
    }

    public String getLocalizedMessage()
    {
        if(msgobj != null)
            return props.getPropertyReplace(propPrefix + key, msgobj);
        else
            return props.getProperty(propPrefix + key);
    }

    public String toString()
    {
        return getLocalizedMessage();
    }

    private static HJBProperties props = HJBProperties.getHJBProperties("securityProperties");
    private static String propPrefix = "appletsecurityexception.";
    private String key;
    private String msgobj[];

}
