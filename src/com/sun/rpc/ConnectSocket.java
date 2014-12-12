// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ConnectSocket.java

package com.sun.rpc;

import java.io.*;
import java.net.*;

// Referenced classes of package com.sun.rpc:
//            Connection, Xdr

public class ConnectSocket extends Connection
{

    public ConnectSocket(String s, int i, int j)
        throws IOException
    {
        super(s, i, "tcp", j);
        rcv_mark = new Xdr(4);
        doConnect();
        start();
    }

    private void doConnect()
        throws IOException
    {
        if(super.server == null)
        {
            throw new UnknownHostException("null host");
        } else
        {
            sock = new Socket(super.server, super.port);
            sock.setTcpNoDelay(true);
            ins = sock.getInputStream();
            outs = sock.getOutputStream();
            return;
        }
    }

    private void doClose()
        throws IOException
    {
        if(ins != null)
        {
            ins.close();
            ins = null;
        }
        if(outs != null)
        {
            outs.close();
            outs = null;
        }
        if(sock != null)
        {
            sock.close();
            sock = null;
        }
    }

    void sendOne(Xdr xdr)
        throws IOException
    {
        int j = 0;
        int k = xdr.xdr_offset();
        synchronized(this)
        {
            int i;
            for(int i1 = 4; i1 < k; i1 += i)
            {
                i = k - i1;
                if(i > 1456)
                    i = 1456;
                if(i1 + i >= k)
                    j = 0x80000000;
                xdr.xdr_offset(i1 - 4);
                int l = xdr.xdr_int();
                xdr.xdr_offset(i1 - 4);
                xdr.xdr_int(j | i);
                outs.write(xdr.xdr_buf(), i1 - 4, i + 4);
                outs.flush();
                xdr.xdr_offset(i1 - 4);
                xdr.xdr_int(l);
            }

            xdr.xdr_offset(k);
        }
    }

    void receiveOne(Xdr xdr, int i)
        throws IOException
    {
        boolean flag = false;
        sock.setSoTimeout(i);
        try
        {
            int j;
            long l;
            for(j = 0; !flag; j = (int)((long)j + l))
            {
                if(ins.read(rcv_mark.xdr_buf()) != 4)
                    throw new IOException("TCP record mark: lost connection");
                rcv_mark.xdr_offset(0);
                l = rcv_mark.xdr_u_int();
                flag = (l & 0xffffffff80000000L) != 0L;
                l &= 0x7fffffffL;
                int k;
                for(int i1 = 0; (long)i1 < l; i1 += k)
                {
                    k = ins.read(xdr.xdr_buf(), j + i1, (int)l - i1);
                    if(k < 0)
                        throw new IOException("TCP data: lost connection");
                }

            }

            xdr.xdr_size(j);
            return;
        }
        catch(InterruptedIOException interruptedioexception)
        {
            throw interruptedioexception;
        }
        catch(IOException ioexception)
        {
            reconnect();
            throw ioexception;
        }
    }

    InetAddress getPeer()
    {
        return sock.getInetAddress();
    }

    void reconnect()
    {
        System.err.println("Lost connection to " + super.server + " - attempting to reconnect");
        synchronized(this)
        {
            do
                try
                {
                    doClose();
                    doConnect();
                    break;
                }
                catch(IOException _ex)
                {
                    try
                    {
                        Thread.sleep(5000L);
                    }
                    catch(InterruptedException _ex2) { }
                }
            while(true);
        }
        System.err.println("Reconnected to " + super.server);
    }

    void dropConnection()
    {
        try
        {
            doClose();
        }
        catch(IOException _ex) { }
        suspend();
    }

    void checkConnection()
    {
        if(sock != null)
        {
            return;
        } else
        {
            reconnect();
            resume();
            return;
        }
    }

    protected void finalize()
        throws Throwable
    {
        doClose();
        super.finalize();
    }

    static final int LAST_FRAG = 0x80000000;
    static final int SIZE_MASK = 0x7fffffff;
    static final int MTUSZ = 1456;
    private OutputStream outs;
    private InputStream ins;
    private Socket sock;
    Xdr rcv_mark;
}
