// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletEventMulticaster.java

package sunw.hotjava.applet;


// Referenced classes of package sunw.hotjava.applet:
//            AppletListener, AppletEvent

public class AppletEventMulticaster
    implements AppletListener
{

    public AppletEventMulticaster(AppletListener appletlistener, AppletListener appletlistener1)
    {
        a = appletlistener;
        b = appletlistener1;
    }

    public void appletStateChanged(AppletEvent appletevent)
    {
        a.appletStateChanged(appletevent);
        b.appletStateChanged(appletevent);
    }

    public static AppletListener add(AppletListener appletlistener, AppletListener appletlistener1)
    {
        return addInternal(appletlistener, appletlistener1);
    }

    public static AppletListener remove(AppletListener appletlistener, AppletListener appletlistener1)
    {
        return removeInternal(appletlistener, appletlistener1);
    }

    private static AppletListener addInternal(AppletListener appletlistener, AppletListener appletlistener1)
    {
        if(appletlistener == null)
            return appletlistener1;
        if(appletlistener1 == null)
            return appletlistener;
        else
            return new AppletEventMulticaster(appletlistener, appletlistener1);
    }

    protected AppletListener remove(AppletListener appletlistener)
    {
        if(appletlistener == a)
            return b;
        if(appletlistener == b)
            return a;
        AppletListener appletlistener1 = removeInternal(a, appletlistener);
        AppletListener appletlistener2 = removeInternal(b, appletlistener);
        if(appletlistener1 == a && appletlistener2 == b)
            return this;
        else
            return addInternal(appletlistener1, appletlistener2);
    }

    private static AppletListener removeInternal(AppletListener appletlistener, AppletListener appletlistener1)
    {
        if(appletlistener == appletlistener1 || appletlistener == null)
            return null;
        if(appletlistener instanceof AppletEventMulticaster)
            return ((AppletEventMulticaster)appletlistener).remove(appletlistener1);
        else
            return appletlistener;
    }

    private final AppletListener a;
    private final AppletListener b;
}
