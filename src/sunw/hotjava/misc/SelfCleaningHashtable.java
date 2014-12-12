// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SelfCleaningHashtable.java

package sunw.hotjava.misc;

import java.util.Hashtable;
import java.util.Vector;
import sun.misc.Ref;

public class SelfCleaningHashtable extends Hashtable
{

    public SelfCleaningHashtable(int i)
    {
        super(i);
        keyVector = new Vector();
    }

    public SelfCleaningHashtable()
    {
        keyVector = new Vector();
    }

    public synchronized Object get(Object obj)
    {
        Object obj1 = super.get(obj);
        doSomeCleanup(1);
        return obj1;
    }

    public synchronized Object put(Object obj, Object obj1)
    {
        Object obj2 = super.put(obj, obj1);
        if(obj2 == null)
            keyVector.addElement(obj);
        doSomeCleanup(2);
        return obj2;
    }

    public synchronized Object remove(Object obj)
    {
        Object obj1 = super.remove(obj);
        keyVector.removeElement(obj);
        doSomeCleanup(2);
        return obj1;
    }

    protected synchronized void doSomeCleanup(int i)
    {
        if(keyVector.size() == 0)
            return;
        for(; i != 0; i--)
        {
            if(cleanupIndex >= keyVector.size())
            {
                cleanupIndex = 0;
                return;
            }
            Object obj = keyVector.elementAt(cleanupIndex);
            Object obj1 = super.get(obj);
            if((obj1 instanceof Ref) && ((Ref)obj1).check() == null)
            {
                super.remove(obj);
                keyVector.removeElement(obj);
                if(keyVector.size() == 0)
                {
                    cleanupIndex = 0;
                    return;
                }
            } else
            {
                cleanupIndex++;
            }
            cleanupIndex = cleanupIndex % keyVector.size();
        }

    }

    private Vector keyVector;
    private int cleanupIndex;
}
