// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CredNone.java

package com.sun.rpc;


// Referenced classes of package com.sun.rpc:
//            Cred, Xdr

public class CredNone extends Cred
{

    void putCred(Xdr xdr)
    {
        xdr.xdr_int(0);
        xdr.xdr_int(0);
        xdr.xdr_int(0);
        xdr.xdr_int(0);
    }

    void getCred(Xdr xdr)
    {
        xdr.xdr_int();
        xdr.xdr_int();
        xdr.xdr_int();
        xdr.xdr_int();
    }

    public CredNone()
    {
    }

    static final int AUTH_NONE = 0;
}
