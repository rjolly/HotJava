// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RpcException.java

package com.sun.rpc;

import java.io.IOException;

public class RpcException extends IOException
{

    public RpcException(String s)
    {
        super("RPC error: " + s);
    }

    public RpcException(int i)
    {
        super("RPC error: " + i);
        error = i;
    }

    public int error;
}
