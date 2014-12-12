// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DragAndDropListener.java

package sunw.hotjava.ui;

import java.util.EventListener;

// Referenced classes of package sunw.hotjava.ui:
//            DragAndDropEvent

public interface DragAndDropListener
    extends EventListener
{

    public abstract void dragAndDropPerformed(DragAndDropEvent draganddropevent);
}
