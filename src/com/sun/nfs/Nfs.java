// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nfs.java

package com.sun.nfs;

import com.sun.rpc.Connection;
import com.sun.rpc.Rpc;
import java.io.IOException;
import java.util.Hashtable;

// Referenced classes of package com.sun.nfs:
//            Buffer, NfsException, Fattr

public abstract class Nfs
{

    abstract void checkAttr()
        throws IOException;

    abstract boolean cacheOK(long l)
        throws IOException;

    abstract void getattr()
        throws IOException;

    abstract long mtime()
        throws IOException;

    abstract long length()
        throws IOException;

    abstract boolean exists()
        throws IOException;

    abstract boolean canWrite()
        throws IOException;

    abstract boolean canRead()
        throws IOException;

    abstract boolean isFile()
        throws IOException;

    abstract boolean isDirectory()
        throws IOException;

    abstract boolean isSymlink()
        throws IOException;

    abstract Fattr getAttr()
        throws IOException;

    abstract Nfs lookup(String s)
        throws IOException;

    abstract void read_otw(Buffer buffer)
        throws IOException;

    abstract int write_otw(Buffer buffer)
        throws IOException;

    abstract String[] readdir()
        throws IOException;

    abstract String readlink()
        throws IOException;

    abstract Nfs create(String s, long l)
        throws IOException;

    abstract Nfs mkdir(String s, long l)
        throws IOException;

    abstract boolean remove(String s)
        throws IOException;

    abstract boolean rename(Nfs nfs, String s, String s1)
        throws IOException;

    abstract boolean rmdir(String s)
        throws IOException;

    abstract void fsinfo()
        throws IOException;

    abstract long commit(int i, int j)
        throws IOException;

    abstract void invalidate();

    byte[] getFH()
    {
        return fh;
    }

    static void cache_put(Nfs nfs)
    {
        cacheNfs.put(nfs.rpc.conn.server + ":" + nfs.name, nfs);
    }

    static Nfs cache_get(String s, String s1)
    {
        return (Nfs)cacheNfs.get(s + ":" + s1);
    }

    static void cache_remove(Nfs nfs, String s)
    {
        if(nfs.name.equals("."))
        {
            cacheNfs.remove(nfs.rpc.conn.server + ":" + s);
            return;
        } else
        {
            cacheNfs.remove(nfs.rpc.conn.server + ":" + nfs.name + "/" + s);
            return;
        }
    }

    synchronized int read(byte abyte0[], int i, int j, long l)
        throws IOException
    {
        Object obj = null;
        int i1 = 0;
        int j1 = 0;
        if(!cacheOK(cacheTime) && bufferList != null)
        {
            for(int k1 = 0; k1 < bufferList.length; k1++)
                if(k1 != prevWriteIndex)
                    bufferList[k1] = null;

            prevReadIndex = -1;
        }
        if(l >= length())
            return -1;
        while(j > 0) 
        {
            if(l >= length())
                break;
            if(bufferList == null)
                bufferList = new Buffer[(int)length() / rsize + 1];
            int k = (int)l / rsize;
            if(k > maxIndexRead)
                maxIndexRead = k;
            if(k != prevReadIndex)
            {
                if(prevReadIndex >= 0 && prevReadIndex != prevWriteIndex)
                {
                    Buffer buffer = bufferList[prevReadIndex];
                    if(buffer.status == 1)
                    {
                        bufferList[prevReadIndex] = null;
                        buffer.exit();
                    }
                    if(k == prevReadIndex + 1 && k >= maxIndexRead)
                        i1 = NRA;
                }
                prevReadIndex = k;
            }
            for(int l1 = k; l1 <= k + i1; l1++)
            {
                if(l1 >= bufferList.length)
                    break;
                Buffer buffer1 = bufferList[l1];
                if(buffer1 == null)
                {
                    Buffer buffer2 = new Buffer(this, l1 * rsize, rsize);
                    buffer2.startLoad();
                    bufferList[l1] = buffer2;
                }
            }

            Buffer buffer3 = bufferList[k];
            try
            {
                buffer3.waitLoaded();
            }
            catch(NfsException nfsexception)
            {
                if(nfsexception.error == 72)
                {
                    rsize = 8192;
                    bufferList = new Buffer[(int)length() / rsize + 1];
                } else
                {
                    throw nfsexception;
                }
                continue;
            }
            int i2 = buffer3.buflen;
            if(i2 < rsize && !buffer3.eof)
            {
                rsize = i2;
                bufferList = null;
                prevReadIndex = -1;
                prevWriteIndex = -1;
            } else
            {
                int j2 = buffer3.copyFrom(abyte0, i, l, j);
                i += j2;
                l += j2;
                j -= j2;
                j1 += j2;
            }
        }
        return j1;
    }

    void beginWrite()
    {
        synchronized(wbLock)
        {
            while(nwb >= NWB) 
                try
                {
                    wbLock.wait();
                }
                catch(InterruptedException _ex) { }
            nwb++;
        }
    }

    void endWrite()
    {
        synchronized(wbLock)
        {
            nwb--;
            wbLock.notify();
        }
    }

    synchronized void write(byte abyte0[], int i, int j, long l)
        throws IOException
    {
        if(wsize == 0)
            fsinfo();
        if(bufferList == null)
        {
            long l1 = Math.max(length(), 50 * wsize);
            bufferList = new Buffer[(int)l1 / wsize + 1];
        }
        while(j > 0) 
        {
            int k = (int)l / wsize;
            if(k != prevWriteIndex)
            {
                if(prevWriteIndex >= 0)
                {
                    bufferList[prevWriteIndex].startUnload(0);
                    checkCommit(false);
                }
                prevWriteIndex = k;
            }
            if(k >= bufferList.length)
            {
                Buffer abuffer[] = new Buffer[bufferList.length * 2];
                for(int i1 = 0; i1 < bufferList.length; i1++)
                    abuffer[i1] = bufferList[i1];

                bufferList = abuffer;
            }
            Buffer buffer = bufferList[k];
            if(buffer == null)
            {
                buffer = new Buffer(this, k * wsize, wsize);
                bufferList[k] = buffer;
            }
            int j1 = buffer.copyTo(abyte0, i, l, j);
            i += j1;
            l += j1;
            j -= j1;
            if(l > maxLength)
                maxLength = l;
        }
    }

    void checkCommit(boolean flag)
        throws IOException
    {
        int i = 0x7fffffff;
        int j = 0;
        int k = 0;
        for(int l = 0; l < bufferList.length; l++)
        {
            Buffer buffer = bufferList[l];
            if(buffer != null)
            {
                if(flag)
                    buffer.waitUnloaded();
                if(buffer.status == 1)
                {
                    if(l != prevReadIndex && l != prevWriteIndex)
                    {
                        bufferList[l] = null;
                        buffer.exit();
                    }
                } else
                if(buffer.status == 3)
                {
                    k++;
                    if(l < i)
                        i = l;
                    if(l > j)
                        j = l;
                }
            }
        }

        if(flag)
        {
            Buffer buffer1 = bufferList[prevWriteIndex];
            if(buffer1 != null && buffer1.status == 2)
                if(k == 0)
                {
                    buffer1.startUnload(2);
                    buffer1.waitUnloaded();
                } else
                {
                    buffer1.startUnload(0);
                    buffer1.waitUnloaded();
                    if(prevWriteIndex < i)
                        i = prevWriteIndex;
                    if(prevWriteIndex > j)
                        j = prevWriteIndex;
                }
        }
        if(k > 0 && (flag || k >= NWC))
        {
            int i1 = i * rsize + bufferList[i].minOffset;
            int j1 = (j * rsize + bufferList[j].maxOffset) - i1;
            long l1 = commit(i1, j1);
            for(int k1 = i; k1 <= j; k1++)
            {
                Buffer buffer2 = bufferList[k1];
                if(buffer2 != null)
                {
                    if(flag)
                        buffer2.waitUnloaded();
                    if(buffer2.status == 3)
                        if(buffer2.writeVerifier == l1)
                        {
                            if(k1 == prevReadIndex || k1 == prevWriteIndex)
                            {
                                buffer2.status = 1;
                            } else
                            {
                                bufferList[k1] = null;
                                buffer2.exit();
                            }
                        } else
                        if(flag)
                        {
                            buffer2.startUnload(2);
                            buffer2.waitUnloaded();
                        } else
                        {
                            buffer2.startUnload(0);
                        }
                }
            }

        }
    }

    public synchronized void flush()
        throws IOException
    {
        if(prevWriteIndex >= 0)
            checkCommit(true);
    }

    public synchronized void close()
        throws IOException
    {
        if(bufferList == null)
            return;
        flush();
        for(int i = 0; i < bufferList.length; i++)
            if(bufferList[i] != null)
            {
                Buffer buffer = bufferList[i];
                bufferList[i] = null;
                buffer.exit();
            }

        prevReadIndex = -1;
        prevWriteIndex = -1;
    }

    protected void finalize()
        throws Throwable
    {
        close();
        super.finalize();
    }

    public String toString()
    {
        try
        {
            if(isSymlink())
                if(symlink != null)
                    return "\"" + name + "\": symlink -> \"" + symlink + "\"";
                else
                    return "\"" + name + "\": symlink";
            if(isDirectory())
            {
                String s = "\":" + name + "\" directory";
                if(dircache != null)
                    return s + "(" + dircache.length + " entries)";
                else
                    return s;
            } else
            {
                return "\"" + name + "\": file (" + length() + " bytes)";
            }
        }
        catch(IOException ioexception)
        {
            return ioexception.getMessage();
        }
    }

    public Nfs()
    {
        wbLock = new Object();
        prevReadIndex = -1;
        prevWriteIndex = -1;
    }

    byte fh[];
    Rpc rpc;
    String name;
    String dircache[];
    String symlink;
    Buffer bufferList[];
    long cacheTime;
    int rsize;
    int wsize;
    private Object wbLock;
    static Hashtable cacheNfs = new Hashtable();
    static final int NFREG = 1;
    static final int NFDIR = 2;
    static final int NFLNK = 5;
    private static final int ASYNC = 0;
    private static final int SYNC = 2;
    int NRA;
    int NWB;
    int NWC;
    int nwb;
    int prevReadIndex;
    int prevWriteIndex;
    int maxIndexRead;
    long maxLength;
    static final int RBIT = 4;
    static final int WBIT = 2;

}
