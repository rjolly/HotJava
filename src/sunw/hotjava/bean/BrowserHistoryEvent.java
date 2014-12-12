// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BrowserHistoryEvent.java

package sunw.hotjava.bean;

import java.util.EventObject;

public class BrowserHistoryEvent extends EventObject
{

    public BrowserHistoryEvent(Object obj, int i)
    {
        this(obj, i, null);
    }

    public BrowserHistoryEvent(Object obj, int i, Object obj1)
    {
        super(obj);
        command = i;
        argument = obj1;
    }

    int getCommand()
    {
        return command;
    }

    Object getArgument()
    {
        return argument;
    }

    static final int Clear = 0;
    static final int Next = 1;
    static final int Previous = 2;
    static final int NextAvailable = 3;
    static final int PreviousAvailable = 4;
    static final int Noop = 5;
    static final int SetLogicalDepth = 6;
    static final int SetContentsDepth = 7;
    private int command;
    private Object argument;
}
