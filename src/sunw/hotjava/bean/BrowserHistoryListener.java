// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BrowserHistoryListener.java

package sunw.hotjava.bean;

import java.util.EventListener;

// Referenced classes of package sunw.hotjava.bean:
//            BrowserHistoryEvent

public interface BrowserHistoryListener
    extends EventListener
{

    public abstract void executeHistoryCommand(BrowserHistoryEvent browserhistoryevent);
}
