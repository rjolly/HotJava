// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   URLPoolEvent.java

package sunw.hotjava.bean;

import java.awt.Event;

public class URLPoolEvent extends Event
{

    public URLPoolEvent(int i)
    {
        super(null, 0, null);
        eventDesc = i;
    }

    public int getEventDescription()
    {
        return eventDesc;
    }

    public static final int ADDED = 0;
    public static final int REPLACED = 1;
    private int eventDesc;
}
