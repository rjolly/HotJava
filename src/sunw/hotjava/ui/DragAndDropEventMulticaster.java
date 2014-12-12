// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DragAndDropEventMulticaster.java

package sunw.hotjava.ui;


// Referenced classes of package sunw.hotjava.ui:
//            DragAndDropListener, DragAndDropEvent

public class DragAndDropEventMulticaster
    implements DragAndDropListener
{

    public DragAndDropEventMulticaster(DragAndDropListener draganddroplistener, DragAndDropListener draganddroplistener1)
    {
        a = draganddroplistener;
        b = draganddroplistener1;
    }

    public void dragAndDropPerformed(DragAndDropEvent draganddropevent)
    {
        a.dragAndDropPerformed(draganddropevent);
        b.dragAndDropPerformed(draganddropevent);
    }

    public static DragAndDropListener add(DragAndDropListener draganddroplistener, DragAndDropListener draganddroplistener1)
    {
        return addInternal(draganddroplistener, draganddroplistener1);
    }

    public static DragAndDropListener remove(DragAndDropListener draganddroplistener, DragAndDropListener draganddroplistener1)
    {
        return removeInternal(draganddroplistener, draganddroplistener1);
    }

    private static DragAndDropListener addInternal(DragAndDropListener draganddroplistener, DragAndDropListener draganddroplistener1)
    {
        if(draganddroplistener == null)
            return draganddroplistener1;
        if(draganddroplistener1 == null)
            return draganddroplistener;
        else
            return new DragAndDropEventMulticaster(draganddroplistener, draganddroplistener1);
    }

    protected DragAndDropListener remove(DragAndDropListener draganddroplistener)
    {
        if(draganddroplistener == a)
            return b;
        if(draganddroplistener == b)
            return a;
        DragAndDropListener draganddroplistener1 = removeInternal(a, draganddroplistener);
        DragAndDropListener draganddroplistener2 = removeInternal(b, draganddroplistener);
        if(draganddroplistener1 == a && draganddroplistener2 == b)
            return this;
        else
            return addInternal(draganddroplistener1, draganddroplistener2);
    }

    private static DragAndDropListener removeInternal(DragAndDropListener draganddroplistener, DragAndDropListener draganddroplistener1)
    {
        if(draganddroplistener == draganddroplistener1 || draganddroplistener == null)
            return null;
        if(draganddroplistener instanceof DragAndDropEventMulticaster)
            return ((DragAndDropEventMulticaster)draganddroplistener).remove(draganddroplistener1);
        else
            return draganddroplistener;
    }

    private final DragAndDropListener a;
    private final DragAndDropListener b;
}
