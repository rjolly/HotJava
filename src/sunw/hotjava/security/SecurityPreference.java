// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SecurityPreference.java

package sunw.hotjava.security;


public interface SecurityPreference
{

    public abstract void init();

    public abstract void updateFields();

    public abstract String getName();

    public abstract String[] getPropertyNames();

    public abstract void apply();

    public abstract void reset();
}
