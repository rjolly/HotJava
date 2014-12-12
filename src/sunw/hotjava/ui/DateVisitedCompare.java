// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DateVisitedCompare.java

package sunw.hotjava.ui;

import sunw.hotjava.misc.Compare;

// Referenced classes of package sunw.hotjava.ui:
//            HotListEntry, Separator

class DateVisitedCompare
    implements Compare
{

    public int doCompare(Object obj, Object obj1)
    {
        HotListEntry hotlistentry = (HotListEntry)obj;
        HotListEntry hotlistentry1 = (HotListEntry)obj1;
        if(hotlistentry instanceof Separator)
            return 1;
        if(hotlistentry1 instanceof Separator)
        {
            return -1;
        } else
        {
            int i = hotlistentry.getLastVisit();
            int j = hotlistentry1.getLastVisit();
            return j - i;
        }
    }

    DateVisitedCompare()
    {
    }
}
