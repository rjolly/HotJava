// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Rpc.java

package com.sun.rpc;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;

// Referenced classes of package com.sun.rpc:
//            ConnectDatagram, ConnectSocket, Connection, Cred, 
//            CredNone, MsgAcceptedException, MsgRejectedException, RpcException, 
//            RpcHandler, Xdr

public class Rpc
{

    public Rpc(Connection connection, int i, int j)
    {
        rhandler = new RpcHandler();
        conn = connection;
        prog = i;
        vers = j;
        cred = new CredNone();
    }

    public Rpc(String s, int i, int j, int k, String s1, int l)
        throws IOException
    {
        rhandler = new RpcHandler();
        conn = getConnection(s, i, j, k, s1, l);
        prog = j;
        vers = k;
        cred = new CredNone();
    }

    private Connection getConnection(String s, int i, int j, int k, String s1, int l)
        throws IOException
    {
        if(i == 0)
        {
            Rpc rpc = new Rpc(s, 111, 0x186a0, 2, "udp", 128);
            Xdr xdr = new Xdr(128);
            rpc.rpc_header(xdr, 3);
            xdr.xdr_int(j);
            xdr.xdr_int(k);
            xdr.xdr_int(s1.equals("tcp") ? 6 : 17);
            xdr.xdr_int(0);
            Xdr xdr1 = rpc.rpc_call(xdr, 5000, 3);
            i = xdr1.xdr_int();
            if(i == 0)
                throw new MsgAcceptedException(1);
        }
        synchronized(Connection.connections)
        {
            conn = Connection.getCache(s, i, s1);
            if(conn == null)
            {
                if(s1.equals("tcp"))
                    conn = new ConnectSocket(s, i, l);
                else
                    conn = new ConnectDatagram(s, i, l);
                Connection.putCache(conn);
            }
        }
        return conn;
    }

    public void setCred(Cred cred1)
    {
        cred = cred1;
    }

    public Cred getCred()
    {
        return cred;
    }

    public void setRpcHandler(RpcHandler rpchandler)
    {
        rhandler = rpchandler != null ? rpchandler : new RpcHandler();
    }

    public void rpc_header(Xdr xdr, int i)
    {
        xdr.xid = next_xid();
        xdr.xdr_offset((conn instanceof ConnectSocket) ? 4 : 0);
        xdr.xdr_int(xdr.xid);
        xdr.xdr_int(0);
        xdr.xdr_int(2);
        xdr.xdr_int(prog);
        xdr.xdr_int(vers);
        xdr.xdr_int(i);
        cred.putCred(xdr);
    }

    static synchronized int next_xid()
    {
        return xid++;
    }

    public Xdr rpc_call(Xdr xdr, int i)
        throws IOException
    {
        Xdr xdr1 = conn.send(xdr, i);
        if(xdr1.xdr_int() != 1)
            throw new RpcException("Unknown RPC header");
        int j = xdr1.xdr_int();
        switch(j)
        {
        default:
            break;

        case 0: // '\0'
            xdr1.xdr_skip(4);
            byte abyte0[] = xdr1.xdr_bytes();
            int k = xdr1.xdr_int();
            switch(k)
            {
            case 1: // '\001'
            case 2: // '\002'
            case 3: // '\003'
                throw new MsgAcceptedException(k, xdr1.xdr_int(), xdr1.xdr_int());

            case 4: // '\004'
            case 5: // '\005'
            default:
                throw new MsgAcceptedException(k);

            case 0: // '\0'
                break;
            }
            break;

        case 1: // '\001'
            int l = xdr1.xdr_int();
            switch(l)
            {
            case 0: // '\0'
                throw new MsgRejectedException(l, xdr1.xdr_int(), xdr1.xdr_int());

            case 1: // '\001'
                int i1 = xdr1.xdr_int();
                throw new MsgRejectedException(l, i1);
            }
            throw new MsgRejectedException(l);
        }
        return xdr1;
    }

    public Xdr rpc_call(Xdr xdr, int i, int j)
        throws IOException
    {
        boolean flag = false;
        Xdr xdr1 = null;
        long l = System.currentTimeMillis();
        if(j == 0)
            j = 0x7fffffff;
        if(conn instanceof ConnectSocket)
            i = 30000;
        for(int k = 0; k < j;)
            try
            {
                xdr1 = rpc_call(xdr, i);
                break;
            }
            catch(RpcException rpcexception)
            {
                throw rpcexception;
            }
            catch(IOException _ex)
            {
                if(rhandler.timeout(conn.server, k, (int)(System.currentTimeMillis() - l)))
                    throw new InterruptedIOException();
                flag = true;
                i *= 2;
                if(i > 30000)
                    i = 30000;
                k++;
            }

        if(xdr1 == null)
            throw new InterruptedIOException();
        if(flag && xdr1 != null)
            rhandler.ok(conn.server);
        return xdr1;
    }

    public InetAddress getPeer()
    {
        return conn.getPeer();
    }

    public Connection conn;
    int prog;
    int vers;
    Cred cred;
    RpcHandler rhandler;
    private static int xid = (int)System.currentTimeMillis() & 0xfffffff;
    private static final int PMAP_PROG = 0x186a0;
    private static final int PMAP_PORT = 111;
    private static final int PMAP_VERS = 2;
    private static final int PMAP_GETPORT = 3;
    private static final int PMAP_MAXSZ = 128;
    private static final int MAX_TIMEOUT = 30000;
    private static final int MAX_REPLY = 8448;
    static final int CALL = 0;
    static final int REPLY = 1;
    static final int MSG_ACCEPTED = 0;
    static final int MSG_DENIED = 1;
    static final int SUCCESS = 0;
    static final int PROG_UNAVAIL = 1;
    static final int PROG_MISMATCH = 2;
    static final int PROC_UNAVAIL = 3;
    static final int GARBAGE_ARGS = 4;
    static final int SYSTEM_ERR = 5;
    static final int RPC_MISMATCH = 0;
    static final int AUTH_ERROR = 1;

}
