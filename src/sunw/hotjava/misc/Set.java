// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Set.java

package sunw.hotjava.misc;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

public class Set
    implements Cloneable, Serializable
{

    public Set()
    {
        hashtable = new Hashtable(11);
    }

    public boolean isMember(Object obj)
    {
        if(obj == null)
            return false;
        else
            return hashtable.containsKey(obj);
    }

    public synchronized void add(Object obj)
    {
        if(obj == null)
            throw new NullPointerException();
        if(!isMember(obj))
            hashtable.put(obj, this);
    }

    public synchronized void remove(Object obj)
    {
        if(obj == null)
        {
            throw new NullPointerException();
        } else
        {
            hashtable.remove(obj);
            return;
        }
    }

    public Enumeration getMembers()
    {
        return hashtable.keys();
    }

    public int size()
    {
        return hashtable.size();
    }

    Hashtable hashtable;
    static final long serialVersionUID = 0x9b8ae36544ae0603L;
}
