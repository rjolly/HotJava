// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GlobalRegistry.java

package sunw.hotjava.misc;

import sunw.hotjava.bean.CookieJarInterface;

public class GlobalRegistry
{

    public static void setCookiesManager(CookieJarInterface cookiejarinterface)
    {
        cookiejar = cookiejarinterface;
    }

    public static CookieJarInterface getCookiesManager()
    {
        return cookiejar;
    }

    public GlobalRegistry()
    {
    }

    private static CookieJarInterface cookiejar;
}
