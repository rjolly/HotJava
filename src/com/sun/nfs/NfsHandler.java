// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NfsHandler.java

package com.sun.nfs;

import com.sun.rpc.RpcHandler;

public abstract class NfsHandler extends RpcHandler
{

    public abstract boolean timeout(String s, int i, int j);

    public abstract void ok(String s);

    public NfsHandler()
    {
    }
}
