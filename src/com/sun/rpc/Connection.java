// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Connection.java

package com.sun.rpc;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.util.Hashtable;

// Referenced classes of package com.sun.rpc:
//            Xdr

public abstract class Connection extends Thread
{

    public Connection(String s, int i, String s1, int j)
    {
        waiters = new Hashtable();
        server = s;
        port = i;
        proto = s1;
        maxSize = j;
        setName("Listener-" + s);
        setDaemon(true);
    }

    public static Connection getCache(String s, int i, String s1)
    {
        Connection connection = (Connection)connections.get(s + ":" + i + ":" + s1);
        return connection;
    }

    public static void putCache(Connection connection)
    {
        connections.put(connection.server + ":" + connection.port + ":" + connection.proto, connection);
    }

    abstract void sendOne(Xdr xdr)
        throws IOException;

    abstract void receiveOne(Xdr xdr, int i)
        throws IOException;

    abstract InetAddress getPeer();

    abstract void dropConnection();

    abstract void checkConnection();

    public String toString()
    {
        return server + ":" + port + ":" + proto;
    }

    synchronized Xdr send(Xdr xdr, int i)
        throws IOException
    {
        checkConnection();
        sendOne(xdr);
        waiters.put(new Integer(xdr.xid), new Integer(i));
        while(xid != xdr.xid) 
        {
            long l = System.currentTimeMillis();
            if(err != null)
                throw err;
            try
            {
                wait(i);
            }
            catch(InterruptedException _ex) { }
            if(err != null)
                throw err;
            i = (int)((long)i - (System.currentTimeMillis() - l));
            if(i <= 0)
            {
                waiters.remove(new Integer(xdr.xid));
                throw new InterruptedIOException();
            }
        }
        xid = 0;
        waiters.remove(new Integer(xdr.xid));
        notifyAll();
        return reply;
    }

    public void run()
    {
        try
        {
            while(true) 
            {
                synchronized(this)
                {
                    while(xid != 0) 
                        try
                        {
                            wait();
                        }
                        catch(InterruptedException _ex) { }
                }
                reply = new Xdr(maxSize);
                try
                {
                    receiveOne(reply, 0x493e0);
                }
                catch(InterruptedIOException _ex)
                {
                    if(waiters.isEmpty())
                        dropConnection();
                }
                catch(IOException _ex)
                {
                    continue;
                }
                synchronized(this)
                {
                    xid = reply.xdr_int();
                    if(waiters.containsKey(new Integer(xid)))
                        notifyAll();
                    else
                        xid = 0;
                }
            }
        }
        catch(Error error)
        {
            err = error;
            synchronized(this)
            {
                notifyAll();
            }
            throw error;
        }
    }

    static Hashtable connections = new Hashtable();
    public String server;
    public int port;
    String proto;
    Hashtable waiters;
    static final int IDLETIME = 0x493e0;
    int xid;
    Xdr reply;
    int maxSize;
    Error err;

}
