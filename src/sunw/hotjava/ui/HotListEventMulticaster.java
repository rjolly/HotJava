// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotListEventMulticaster.java

package sunw.hotjava.ui;


// Referenced classes of package sunw.hotjava.ui:
//            HotListListener, HotListEvent

public class HotListEventMulticaster
    implements HotListListener
{

    public HotListEventMulticaster(HotListListener hotlistlistener, HotListListener hotlistlistener1)
    {
        a = hotlistlistener;
        b = hotlistlistener1;
    }

    public void hotlistChanged(HotListEvent hotlistevent)
    {
        a.hotlistChanged(hotlistevent);
        b.hotlistChanged(hotlistevent);
    }

    public static HotListListener add(HotListListener hotlistlistener, HotListListener hotlistlistener1)
    {
        return addInternal(hotlistlistener, hotlistlistener1);
    }

    public static HotListListener remove(HotListListener hotlistlistener, HotListListener hotlistlistener1)
    {
        return removeInternal(hotlistlistener, hotlistlistener1);
    }

    private static HotListListener addInternal(HotListListener hotlistlistener, HotListListener hotlistlistener1)
    {
        if(hotlistlistener == null)
            return hotlistlistener1;
        if(hotlistlistener1 == null)
            return hotlistlistener;
        else
            return new HotListEventMulticaster(hotlistlistener, hotlistlistener1);
    }

    protected HotListListener remove(HotListListener hotlistlistener)
    {
        if(hotlistlistener == a)
            return b;
        if(hotlistlistener == b)
            return a;
        HotListListener hotlistlistener1 = removeInternal(a, hotlistlistener);
        HotListListener hotlistlistener2 = removeInternal(b, hotlistlistener);
        if(hotlistlistener1 == a && hotlistlistener2 == b)
            return this;
        else
            return addInternal(hotlistlistener1, hotlistlistener2);
    }

    private static HotListListener removeInternal(HotListListener hotlistlistener, HotListListener hotlistlistener1)
    {
        if(hotlistlistener == hotlistlistener1 || hotlistlistener == null)
            return null;
        if(hotlistlistener instanceof HotListEventMulticaster)
            return ((HotListEventMulticaster)hotlistlistener).remove(hotlistlistener1);
        else
            return hotlistlistener;
    }

    private final HotListListener a;
    private final HotListListener b;
}
