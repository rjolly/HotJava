// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletEvent.java

package sunw.hotjava.applet;

import java.util.EventObject;

public class AppletEvent extends EventObject
{

    public AppletEvent(Object obj, int i, Object obj1)
    {
        super(obj);
        arg = obj1;
        id = i;
    }

    public int getID()
    {
        return id;
    }

    public Object getArgument()
    {
        return arg;
    }

    public String toString()
    {
        String s = getClass().getName() + "[source=" + super.source + " + id=" + id;
        if(arg != null)
            s = s + " + arg=" + arg;
        s = s + " ]";
        return s;
    }

    private Object arg;
    private int id;
}
