// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Buffer.java

package com.sun.nfs;

import java.io.IOException;

// Referenced classes of package com.sun.nfs:
//            Nfs

public class Buffer extends Thread
{

    public Buffer(Nfs nfs1, int i, int j)
    {
        nfs = nfs1;
        foffset = i;
        bufsize = j;
        buflen = 0;
        minOffset = j;
        maxOffset = 0;
        setDaemon(true);
        try
        {
            setName("Buffer-" + i / j);
        }
        catch(Exception _ex) { }
        action = 0;
        start();
    }

    synchronized int copyFrom(byte abyte0[], int i, long l, int j)
        throws IOException
    {
        if(e != null)
            throw e;
        if(err != null)
            throw err;
        if(status == 0)
            throw new IOException("no data");
        if(buflen < bufsize)
        {
            byte abyte1[] = new byte[bufsize];
            if(buflen > 0)
                System.arraycopy(buf, bufoff, abyte1, 0, buflen);
            buflen = bufsize;
            bufoff = 0;
            buf = abyte1;
        }
        int k = (int)(l - foffset);
        int i1 = Math.min(j, buflen - k);
        i1 = Math.min(i1, (int)(nfs.length() - l));
        System.arraycopy(buf, bufoff + k, abyte0, i, i1);
        return i1;
    }

    synchronized int copyTo(byte abyte0[], int i, long l, int j)
        throws IOException
    {
        if(e != null)
            throw e;
        if(err != null)
            throw err;
        int k = (int)(l - foffset);
        int i1 = Math.min(j, bufsize - k);
        if(status == 0)
        {
            long l1 = Math.min(nfs.length(), foffset + (long)nfs.wsize);
            if(foffset < nfs.length() && (l > foffset || l + (long)j < l1))
            {
                startLoad();
                waitLoaded();
            }
        }
        if(k + i1 > buflen)
        {
            byte abyte1[] = new byte[bufsize];
            if(buf != null)
                System.arraycopy(buf, bufoff, abyte1, 0, buflen);
            buf = abyte1;
            bufoff = 0;
            buflen = bufsize;
        }
        System.arraycopy(abyte0, i, buf, bufoff + k, i1);
        status = 2;
        if(k < minOffset)
            minOffset = k;
        if(k + i1 > maxOffset)
            maxOffset = k + i1;
        return i1;
    }

    synchronized void startLoad()
    {
        action = 1;
        notifyAll();
    }

    synchronized void waitLoaded()
        throws IOException
    {
        if(e != null)
            throw e;
        if(err != null)
            throw err;
        while(status == 0) 
        {
            try
            {
                wait();
            }
            catch(InterruptedException _ex) { }
            if(e != null)
                throw e;
            if(err != null)
                throw err;
        }
    }

    synchronized void waitUnloaded()
        throws IOException
    {
        if(e != null)
            throw e;
        if(err != null)
            throw err;
        while(action == 2) 
        {
            try
            {
                wait();
            }
            catch(InterruptedException _ex) { }
            if(e != null)
                throw e;
            if(err != null)
                throw err;
        }
    }

    synchronized void startUnload(int i)
    {
        nfs.beginWrite();
        action = 2;
        syncType = i;
        notifyAll();
    }

    synchronized void exit()
    {
        action = 3;
        notifyAll();
    }

    public void run()
    {
        synchronized(this)
        {
            try
            {
                do
                {
                    while(action == 0) 
                        try
                        {
                            wait();
                        }
                        catch(InterruptedException _ex) { }
                    switch(action)
                    {
                    default:
                        break;

                    case 1: // '\001'
                        try
                        {
                            nfs.read_otw(this);
                        }
                        catch(IOException ioexception)
                        {
                            if(e == null)
                                e = ioexception;
                        }
                        status = 1;
                        break;

                    case 2: // '\002'
                        try
                        {
                            for(; minOffset < maxOffset; minOffset += nfs.write_otw(this));
                            minOffset = bufsize;
                            maxOffset = 0;
                        }
                        catch(IOException ioexception1)
                        {
                            if(e == null)
                                e = ioexception1;
                        }
                        nfs.endWrite();
                        break;

                    case 3: // '\003'
                        notifyAll();
                        buf = null;
                        return;
                    }
                    action = 0;
                    notifyAll();
                } while(true);
            }
            catch(Error error)
            {
                err = error;
                notifyAll();
                throw error;
            }
        }
    }

    public String toString()
    {
        return nfs.name + " @ " + foffset + " for " + buflen;
    }

    Nfs nfs;
    long foffset;
    byte buf[];
    int bufoff;
    int buflen;
    int bufsize;
    int minOffset;
    int maxOffset;
    int status;
    private int action;
    boolean eof;
    IOException e;
    Error err;
    long writeVerifier;
    int syncType;
    private static final int IDLE = 0;
    private static final int LOAD = 1;
    private static final int UNLOAD = 2;
    private static final int EXIT = 3;
    static final int EMPTY = 0;
    static final int LOADED = 1;
    static final int DIRTY = 2;
    static final int COMMIT = 3;
}
