// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CompoundVBreakInfo.java

package sunw.hotjava.doc;

import java.util.Hashtable;

// Referenced classes of package sunw.hotjava.doc:
//            VBreakInfo

public class CompoundVBreakInfo extends VBreakInfo
{

    public VBreakInfo getItemBreakInfo(Object obj)
    {
        if(itemBreakInfo == null)
            return null;
        else
            return (VBreakInfo)itemBreakInfo.get(obj);
    }

    public void setItemBreakInfo(Object obj, VBreakInfo vbreakinfo)
    {
        if(itemBreakInfo == null)
            itemBreakInfo = new Hashtable(11);
        itemBreakInfo.put(obj, vbreakinfo);
    }

    public CompoundVBreakInfo()
    {
    }

    private Hashtable itemBreakInfo;
}
