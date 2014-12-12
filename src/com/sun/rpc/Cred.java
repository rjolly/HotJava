// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Cred.java

package com.sun.rpc;


// Referenced classes of package com.sun.rpc:
//            Xdr

abstract class Cred
{

    abstract void putCred(Xdr xdr);

    abstract void getCred(Xdr xdr);

    Cred()
    {
    }
}
