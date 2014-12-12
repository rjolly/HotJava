// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FormEvent.java

package sunw.hotjava.forms;


// Referenced classes of package sunw.hotjava.forms:
//            FORM

public class FormEvent
{

    public FormEvent(FORM form, int i)
    {
        cancelled = false;
        source = form;
        type = i;
    }

    public int getType()
    {
        return type;
    }

    public FORM source()
    {
        return source;
    }

    public boolean getCancelled()
    {
        return cancelled;
    }

    public void setCancelled(boolean flag)
    {
        cancelled = flag;
    }

    public static final int SUBMIT = 0;
    public static final int RESET = 1;
    private int type;
    private FORM source;
    private boolean cancelled;
}
