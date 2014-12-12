// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotListEvent.java

package sunw.hotjava.ui;

import java.util.EventObject;

public class HotListEvent extends EventObject
{

    public HotListEvent(Object obj, int i, Object obj1)
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

    public static final int ADDGOTO = 50;
    public static final int DELGOTO = 51;
    public static final int CHANGEFOLDERSTATE = 52;
    public static final int LISTADD = 53;
    public static final int LISTDEL = 54;
    Object arg;
    private int id;
}
