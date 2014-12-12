// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CertificateTrust.java

package sunw.hotjava.security;

import java.io.Serializable;
import java.util.StringTokenizer;

public class CertificateTrust
    implements Serializable
{

    public CertificateTrust(String as[])
    {
        StringTokenizer stringtokenizer = new StringTokenizer(as[0], ";");
        if(stringtokenizer.hasMoreTokens())
            type = Integer.parseInt(stringtokenizer.nextToken());
        if(stringtokenizer.hasMoreTokens())
            mode = Integer.parseInt(stringtokenizer.nextToken());
        if(stringtokenizer.hasMoreTokens())
            verified = Boolean.getBoolean(stringtokenizer.nextToken());
    }

    public CertificateTrust(int i, int j, boolean flag)
    {
        type = i;
        mode = j;
        verified = flag;
    }

    public String[] toAnnots()
    {
        String as[] = new String[1];
        as[0] = type + ";" + mode + ";" + verified;
        return as;
    }

    public int getMode()
    {
        return mode;
    }

    public void setMode(int i)
    {
        mode = i;
    }

    public int getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String s)
    {
        name = s;
    }

    public void setKey(Object obj)
    {
        key = obj;
    }

    public Object getKey()
    {
        return key;
    }

    int mode;
    int type;
    boolean verified;
    boolean isTesting;
    private String name;
    private Object key;
    public static final int CA = 1;
    public static final int SITE = 2;
    public static final int SOFTWARE = 3;
    public static final int SITE_TEMP = 4;
    public static final int SOFTWARE_TEMP = 5;
    public static final int ALLOW = 6;
    public static final int REJECT = 7;
    public static final int WARN = 8;
    public static final int BLOCKED = 9;
    public static final int SAFE = 10;
    public static final int ASK_FIRST = 11;
    public static final int UNPROTECTED = 12;
    public static final int DEFAULT = 13;
    public static final int UNKNOWN = 14;
}
