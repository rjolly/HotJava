// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Fattr.java

package com.sun.nfs;

import com.sun.rpc.Xdr;

public abstract class Fattr
{

    boolean valid()
    {
        long l = System.currentTimeMillis();
        return l <= validtime + cachetime;
    }

    abstract void putFattr(Xdr xdr);

    abstract void getFattr(Xdr xdr);

    public Fattr()
    {
    }

    long validtime;
    long cachetime;
    static final int ACMIN = 3000;
    static final int ACMAX = 60000;
    static final int NOBODY = 60001;
    static final int NFS_NOBODY = -2;
}
