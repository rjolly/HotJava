// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   LinkListener.java

package sunw.hotjava.bean;


// Referenced classes of package sunw.hotjava.bean:
//            LinkEvent, HTMLBrowsable

public interface LinkListener
{

    public abstract HTMLBrowsable hyperlinkPerformed(LinkEvent linkevent);
}
