// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PrefsPanel.java

package sunw.hotjava.applet;


public interface PrefsPanel
{

    public abstract int apply();

    public abstract void stop();

    public static final int FAILED = 0;
    public static final int SUCCESS = 1;
    public static final int UPDATE = 2;
}
