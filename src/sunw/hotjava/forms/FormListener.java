// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FormListener.java

package sunw.hotjava.forms;


// Referenced classes of package sunw.hotjava.forms:
//            FormEvent

public interface FormListener
{

    public abstract void submitEvent(FormEvent formevent);

    public abstract void resetEvent(FormEvent formevent);

    public abstract String getListenerName();
}
