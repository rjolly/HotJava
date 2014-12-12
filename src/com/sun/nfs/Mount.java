// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Mount.java

package com.sun.nfs;

import com.sun.rpc.*;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

class Mount
{

    byte[] getFH(String s, String s1, int i)
        throws UnknownHostException, IOException
    {
        Xdr xdr = new Xdr(1024);
        Rpc rpc = new Rpc(s, 0, 0x186a5, i != 2 ? 3 : 1, "udp", 512);
        rpc.setCred(new CredUnix(0, 0));
        rpc.rpc_header(xdr, 1);
        xdr.xdr_string(s1);
        Xdr xdr1 = rpc.rpc_call(xdr, 3000, 3);
        int j = xdr1.xdr_int();
        if(j != 0)
            if((j == 2 || j == 13) && !s1.startsWith("/"))
                return getFH(s, "/" + s1, i);
            else
                throw new IOException("Mount status: " + j);
        byte abyte0[] = i != 2 ? xdr1.xdr_bytes() : xdr1.xdr_raw(32);
        rpc.rpc_header(xdr, 3);
        xdr.xdr_string(s1);
        try
        {
            rpc.rpc_call(xdr, 1000);
        }
        catch(InterruptedIOException _ex) { }
        return abyte0;
    }

    static String[] getExports(String s)
        throws UnknownHostException, IOException
    {
        Xdr xdr = new Xdr(255);
        String as[] = new String[32];
        int i = 0;
        Xdr xdr1;
        try
        {
            Rpc rpc = new Rpc(s, 0, 0x186a5, 1, "tcp", 8192);
            rpc.setCred(new CredUnix(0, 0));
            rpc.rpc_header(xdr, 5);
            xdr1 = rpc.rpc_call(xdr, 3000, 3);
        }
        catch(UnknownHostException unknownhostexception)
        {
            throw unknownhostexception;
        }
        catch(IOException _ex)
        {
            return new String[0];
        }
        while(xdr1.xdr_bool()) 
        {
            as[i++] = xdr1.xdr_string();
            if(i >= as.length)
            {
                String as1[] = as;
                as = new String[i * 2];
                System.arraycopy(as1, 0, as, 0, i);
            }
            for(; xdr1.xdr_bool(); xdr1.xdr_string());
        }
        if(i < as.length)
        {
            String as2[] = as;
            as = new String[i];
            System.arraycopy(as2, 0, as, 0, i);
        }
        return as;
    }

    Mount()
    {
    }

    private static final int MOUNTPROG = 0x186a5;
    private static final int MOUNTPROC_MNT = 1;
    private static final int MOUNTPROC_UMNT = 3;
    private static final int MOUNTPROC_EXPORT = 5;
    private static final int FHSIZE = 32;
    private static final int FHSIZE3 = 64;
    private static final int ENOENT = 2;
    private static final int EACCES = 13;
}
