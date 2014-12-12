// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SystemProperties.java

package sunw.hotjava.bean;

import java.util.Hashtable;
import java.util.Observer;

public class SystemProperties
{

    public SystemProperties()
    {
        this(new Hashtable());
    }

    public SystemProperties(Hashtable hashtable)
    {
        this(hashtable, null);
    }

    SystemProperties(Hashtable hashtable, Hashtable hashtable1)
    {
        editOnlyUserList = false;
        values = hashtable;
        defaults = hashtable1;
    }

    SystemProperties(Hashtable hashtable, Object obj, Object obj1)
    {
        editOnlyUserList = false;
        values = hashtable;
        defaults = null;
        newKey = obj;
        newValue = obj1;
    }

    void setObserver(Observer observer1)
    {
        observer = observer1;
    }

    public Hashtable getValues()
    {
        Hashtable hashtable = values;
        if(hashtable == null)
            return null;
        Hashtable hashtable1 = (Hashtable)hashtable.clone();
        if(newKey != null)
            if(newValue == null)
                hashtable1.remove(newKey);
            else
                hashtable1.put(newKey, newValue);
        return hashtable1;
    }

    public SystemProperties setValue(String s, String s1)
    {
        values.put(s, s1);
        return this;
    }

    Hashtable values;
    Hashtable defaults;
    Observer observer;
    Object newKey;
    Object newValue;
    boolean editOnlyUserList;
}
