// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SizeItem.java

package sunw.hotjava.doc;

import java.awt.Dimension;

public interface SizeItem
{

    public abstract Dimension getSize();

    public abstract boolean hasSize();

    public abstract void waiterTimedOut();
}
