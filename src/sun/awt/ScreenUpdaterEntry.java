// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ScreenUpdater.java

package sun.awt;


// Referenced classes of package sun.awt:
//            ScreenUpdater, UpdateClient

class ScreenUpdaterEntry
{

    ScreenUpdaterEntry(UpdateClient updateclient, long l, Object obj, ScreenUpdaterEntry screenupdaterentry)
    {
        client = updateclient;
        when = l;
        arg = obj;
        next = screenupdaterentry;
    }

    UpdateClient client;
    long when;
    ScreenUpdaterEntry next;
    Object arg;
}
