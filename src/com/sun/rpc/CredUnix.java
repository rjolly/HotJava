// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CredUnix.java

package com.sun.rpc;

import java.io.IOException;
import java.net.UnknownHostException;

// Referenced classes of package com.sun.rpc:
//            Cred, MsgAcceptedException, Rpc, RpcException, 
//            Xdr

public class CredUnix extends Cred
{

    public CredUnix(int i, int j)
    {
        cr = new Xdr(64);
        uid = i;
        gid = j;
    }

    public CredUnix()
    {
        this(60001, 60001);
    }

    synchronized void putCred(Xdr xdr)
    {
        xdr.xdr_int(1);
        cr.xdr_offset(0);
        cr.xdr_int((int)(System.currentTimeMillis() / 1000L));
        cr.xdr_string("javaclient");
        cr.xdr_int(uid);
        cr.xdr_int(gid);
        if(gids == null)
        {
            cr.xdr_int(0);
        } else
        {
            cr.xdr_int(gids.length);
            for(int i = 0; i < gids.length; i++)
                cr.xdr_int(gids[i]);

        }
        xdr.xdr_bytes(cr);
        xdr.xdr_int(0);
        xdr.xdr_int(0);
    }

    void getCred(Xdr xdr)
    {
        xdr.xdr_int();
        xdr.xdr_int();
        xdr.xdr_int();
        xdr.xdr_string();
        uid = xdr.xdr_int();
        gid = xdr.xdr_int();
        int i = xdr.xdr_int();
        if(i > 0)
        {
            gids = new int[i];
            for(int j = 0; j < i; j++)
                gids[j] = xdr.xdr_int();

        }
        xdr.xdr_int();
        xdr.xdr_int();
    }

    public boolean fetchCred(String s, String s1, String s2)
    {
        s1 = disguise(s1);
        s2 = disguise(s2);
        try
        {
            try
            {
                return callV2(s, s1, s2);
            }
            catch(MsgAcceptedException msgacceptedexception)
            {
                if(((RpcException) (msgacceptedexception)).error != 2)
                    return false;
                else
                    return callV1(s, s1, s2);
            }
        }
        catch(IOException _ex)
        {
            return false;
        }
    }

    public void setCred()
    {
        uid = 60001;
        gid = 60001;
        gids = null;
    }

    public void setCred(int i, int j, int ai[])
    {
        uid = i;
        gid = j;
        gids = ai;
    }

    private String disguise(String s)
    {
        byte abyte0[] = s.getBytes();
        for(int i = 0; i < abyte0.length; i++)
            abyte0[i] = (byte)(abyte0[i] & 0x7f ^ 0x5b);

        return new String(abyte0);
    }

    public int getUid()
    {
        return uid;
    }

    public int getGid()
    {
        return gid;
    }

    public int[] getGids()
    {
        return gids;
    }

    public String getHome()
    {
        return home;
    }

    public int getUmask()
    {
        return def_umask;
    }

    private boolean callV1(String s, String s1, String s2)
        throws UnknownHostException, IOException
    {
        Rpc rpc = new Rpc(s, 0, 0x249f1, 1, "udp", 512);
        Xdr xdr = new Xdr(512);
        rpc.rpc_header(xdr, 1);
        xdr.xdr_string(s1);
        xdr.xdr_string(s2);
        Xdr xdr1 = rpc.rpc_call(xdr, 10000, 2);
        status = xdr1.xdr_int();
        if(status == 2)
        {
            return false;
        } else
        {
            uid = xdr1.xdr_int();
            gid = xdr1.xdr_int();
            gids = null;
            home = null;
            def_umask = 0;
            return true;
        }
    }

    private boolean callV2(String s, String s1, String s2)
        throws UnknownHostException, IOException
    {
        Rpc rpc = new Rpc(s, 0, 0x249f1, 2, "udp", 512);
        Xdr xdr = new Xdr(512);
        rpc.rpc_header(xdr, 13);
        xdr.xdr_string("(anyhost)");
        xdr.xdr_string(s1);
        xdr.xdr_string(s2);
        xdr.xdr_string("Java client");
        Xdr xdr1 = rpc.rpc_call(xdr, 10000, 2);
        status = xdr1.xdr_int();
        if(status == 2)
            return false;
        uid = xdr1.xdr_int();
        gid = xdr1.xdr_int();
        gids = new int[xdr1.xdr_int()];
        for(int i = 0; i < gids.length; i++)
            gids[i] = xdr1.xdr_int();

        home = xdr1.xdr_string();
        def_umask = xdr1.xdr_int();
        return true;
    }

    public String toString()
    {
        String s = "AUTH_UNIX:\n   uid=" + uid + ",gid=" + gid + "\n";
        if(gids != null)
        {
            s = s + "   gids=";
            for(int i = 0; i < gids.length; i++)
                s = s + gids[i] + " ";

        }
        if(home != null)
            s = s + "\n   home=" + home;
        if(def_umask != 0)
            s = s + "\n   umask=0" + Long.toOctalString(def_umask);
        return s;
    }

    private int uid;
    private int gid;
    private int gids[];
    private String home;
    private int def_umask;
    public int status;
    static final int AUTH_UNIX = 1;
    static final int UID_NOBODY = 60001;
    static final int GID_NOBODY = 60001;
    private static final int PCNFSDPROG = 0x249f1;
    private static final int PCNFSD_AUTH = 1;
    private static final int PCNFSD2_AUTH = 13;
    private static final int MAXREPLY = 512;
    static final int AUTH_RES_OK = 0;
    static final int AUTH_RES_FAKE = 1;
    static final int AUTH_RES_FAIL = 2;
    private Xdr cr;
}
