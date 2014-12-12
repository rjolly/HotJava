// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ConnectDatagram.java

package com.sun.rpc;

import java.io.IOException;
import java.net.*;

// Referenced classes of package com.sun.rpc:
//            Connection, Xdr

public class ConnectDatagram extends Connection
{

    public ConnectDatagram(String s, int i, int j)
        throws IOException
    {
        super(s, i, "udp", j);
        ds = new DatagramSocket();
        addr = InetAddress.getByName(s);
        start();
    }

    void sendOne(Xdr xdr)
        throws IOException
    {
        interrupt();
        ds.send(new DatagramPacket(xdr.xdr_buf(), xdr.xdr_offset(), addr, super.port));
    }

    void receiveOne(Xdr xdr, int i)
        throws IOException
    {
        ds.setSoTimeout(i);
        dp = new DatagramPacket(xdr.xdr_buf(), xdr.xdr_buf().length);
        ds.receive(dp);
    }

    InetAddress getPeer()
    {
        return dp.getAddress();
    }

    void dropConnection()
    {
        suspend();
    }

    void checkConnection()
    {
        resume();
    }

    protected void finalize()
        throws Throwable
    {
        if(ds != null)
        {
            ds.close();
            ds = null;
        }
        super.finalize();
    }

    DatagramSocket ds;
    DatagramPacket dp;
    InetAddress addr;
}
