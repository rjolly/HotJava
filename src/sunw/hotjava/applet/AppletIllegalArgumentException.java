// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletIllegalArgumentException.java

package sunw.hotjava.applet;

import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.security.SecurityGlobals;

public class AppletIllegalArgumentException extends IllegalArgumentException
{

    public AppletIllegalArgumentException(String s)
    {
        super(s);
        key = s;
    }

    public String getLocalizedMessage()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("securityProperties");
        return hjbproperties.getProperty(propPrefix + key);
    }

    private static String propPrefix = "appletillegalargumentexception.";
    private String key;

}
