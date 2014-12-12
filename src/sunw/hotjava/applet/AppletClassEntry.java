// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletClassEntry.java

package sunw.hotjava.applet;

import java.security.Identity;

class AppletClassEntry
{

    AppletClassEntry(byte abyte0[], int i, int j, String s, Identity aidentity[])
    {
        classBuf = abyte0;
        start = i;
        len = j;
        name = s;
        ids = aidentity;
    }

    public int hashCode()
    {
        return name.hashCode();
    }

    public String toString()
    {
        return "className=" + name;
    }

    byte classBuf[];
    int start;
    int len;
    String name;
    Identity ids[];
}
