// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletListener.java

package sunw.hotjava.applet;

import java.util.EventListener;

// Referenced classes of package sunw.hotjava.applet:
//            AppletEvent

public interface AppletListener
    extends EventListener
{

    public abstract void appletStateChanged(AppletEvent appletevent);
}
