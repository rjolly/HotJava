// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nfs2.java

package com.sun.nfs;

import com.sun.rpc.*;
import java.io.IOException;

// Referenced classes of package com.sun.nfs:
//            Nfs, Buffer, Fattr, Fattr2, 
//            NfsConnect, NfsException

public class Nfs2 extends Nfs
{

    public Nfs2(Rpc rpc, byte abyte0[], String s, Fattr2 fattr2)
    {
        super.rpc = rpc;
        super.fh = abyte0;
        if(s.startsWith("./"))
            s = s.substring(2);
        super.name = s;
        attr = fattr2 != null ? fattr2 : new Fattr2();
        super.rsize = 8192;
        super.NRA = 2;
        super.NWB = 8;
    }

    void getattr()
        throws IOException
    {
        Xdr xdr = new Xdr(super.rsize + 200);
        super.rpc.rpc_header(xdr, 1);
        xdr.xdr_raw(super.fh);
        Xdr xdr1;
        try
        {
            xdr1 = super.rpc.rpc_call(xdr, 2000, 2);
        }
        catch(IOException _ex)
        {
            return;
        }
        int i = xdr1.xdr_int();
        if(i != 0)
        {
            throw new NfsException(i);
        } else
        {
            attr.getFattr(xdr1);
            return;
        }
    }

    void checkAttr()
        throws IOException
    {
        if(!attr.valid())
            getattr();
    }

    boolean cacheOK(long l)
        throws IOException
    {
        checkAttr();
        return l == attr.mtime;
    }

    void invalidate()
    {
        attr.validtime = 0L;
    }

    long mtime()
        throws IOException
    {
        checkAttr();
        return attr.mtime;
    }

    long length()
        throws IOException
    {
        checkAttr();
        if(super.maxLength > attr.size)
            return super.maxLength;
        else
            return attr.size;
    }

    boolean exists()
        throws IOException
    {
        checkAttr();
        return true;
    }

    private boolean check_access(long l)
    {
        boolean flag = false;
        long l1 = NfsConnect.getCred().getUid();
        long l2 = NfsConnect.getCred().getGid();
        int ai[] = NfsConnect.getCred().getGids();
        l <<= 6;
        if(l1 != attr.uid)
        {
            l >>= 3;
            if(l2 != attr.gid)
            {
                int i = 0;
                if(ai != null)
                    i = ai.length;
                for(int j = 0; j < i; j++)
                    if(flag = (long)ai[j] == attr.gid)
                        break;

                if(!flag)
                    l >>= 3;
            }
        }
        return (attr.mode & l) == l;
    }

    boolean canWrite()
        throws IOException
    {
        checkAttr();
        return check_access(2L);
    }

    boolean canRead()
        throws IOException
    {
        checkAttr();
        return check_access(4L);
    }

    boolean isFile()
        throws IOException
    {
        checkAttr();
        return attr.ftype == 1;
    }

    boolean isDirectory()
        throws IOException
    {
        checkAttr();
        return attr.ftype == 2;
    }

    boolean isSymlink()
        throws IOException
    {
        checkAttr();
        return attr.ftype == 5;
    }

    Fattr getAttr()
        throws IOException
    {
        checkAttr();
        return attr;
    }

    Nfs lookup(String s)
        throws IOException
    {
        String s1;
        if(s == null)
        {
            s1 = super.name;
            s = super.name;
        } else
        if(super.name == null)
            s1 = s;
        else
            s1 = super.name + "/" + s;
        Object obj = Nfs.cache_get(super.rpc.conn.server, s1);
        if(obj != null && ((Nfs) (obj)).cacheOK(super.cacheTime))
        {
            if(((Nfs2)obj).attr.ftype == 5)
                obj = NfsConnect.followLink(((Nfs) (obj)));
            return ((Nfs) (obj));
        }
        Xdr xdr = new Xdr(super.rsize + 200);
        super.rpc.rpc_header(xdr, 4);
        xdr.xdr_raw(super.fh);
        xdr.xdr_string(s);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 5000, 0);
        int i = xdr1.xdr_int();
        if(i != 0)
            throw new NfsException(i);
        byte abyte0[] = xdr1.xdr_raw(32);
        Fattr2 fattr2 = new Fattr2(xdr1);
        obj = new Nfs2(super.rpc, abyte0, s1, fattr2);
        Nfs.cache_put(((Nfs) (obj)));
        if(((Nfs2)obj).attr.ftype == 5)
            obj = NfsConnect.followLink(((Nfs) (obj)));
        return ((Nfs) (obj));
    }

    void read_otw(Buffer buffer)
        throws IOException
    {
        Xdr xdr = new Xdr(super.rsize + 200);
        super.rpc.rpc_header(xdr, 6);
        xdr.xdr_raw(super.fh);
        xdr.xdr_u_int(buffer.foffset);
        xdr.xdr_u_int(super.rsize);
        xdr.xdr_u_int(super.rsize);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 1000, 0);
        int i = xdr1.xdr_int();
        if(i != 0)
        {
            throw new NfsException(i);
        } else
        {
            attr.getFattr(xdr1);
            int j = xdr1.xdr_int();
            buffer.eof = buffer.foffset + (long)super.rsize >= attr.size;
            buffer.buf = xdr1.xdr_buf();
            buffer.bufoff = xdr1.xdr_offset();
            buffer.buflen = j;
            super.cacheTime = attr.mtime;
            return;
        }
    }

    int write_otw(Buffer buffer)
        throws IOException
    {
        Xdr xdr = new Xdr(super.wsize + 200);
        int i = (int)buffer.foffset + buffer.minOffset;
        int j = buffer.maxOffset - buffer.minOffset;
        super.rpc.rpc_header(xdr, 8);
        xdr.xdr_raw(super.fh);
        xdr.xdr_u_int(i);
        xdr.xdr_u_int(i);
        xdr.xdr_u_int(j);
        xdr.xdr_bytes(buffer.buf, buffer.bufoff + buffer.minOffset, j);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 2000, 0);
        int k = xdr1.xdr_int();
        if(k != 0)
        {
            throw new NfsException(k);
        } else
        {
            attr.getFattr(xdr1);
            buffer.status = 1;
            buffer.writeVerifier = 0L;
            super.cacheTime = attr.mtime;
            return j;
        }
    }

    String[] readdir()
        throws IOException
    {
        long l = 0L;
        boolean flag = false;
        String as[] = new String[32];
        int i = 0;
        if(super.dircache != null && cacheOK(super.cacheTime))
            return super.dircache;
        Xdr xdr = new Xdr(super.rsize + 200);
        Xdr xdr1;
        for(; !flag; flag = xdr1.xdr_bool())
        {
            super.rpc.rpc_header(xdr, 16);
            xdr.xdr_raw(super.fh);
            xdr.xdr_u_int(l);
            xdr.xdr_u_int(4096L);
            xdr1 = super.rpc.rpc_call(xdr, 2000, 0);
            int j = xdr1.xdr_int();
            if(j != 0)
                throw new NfsException(j);
            while(xdr1.xdr_bool()) 
            {
                xdr1.xdr_u_int();
                String s = xdr1.xdr_string();
                l = xdr1.xdr_u_int();
                if(!s.equals(".") && !s.equals(".."))
                {
                    as[i++] = s;
                    if(i >= as.length)
                    {
                        String as2[] = as;
                        as = new String[i * 2];
                        System.arraycopy(as2, 0, as, 0, i);
                    }
                }
            }
        }

        if(i < as.length)
        {
            String as1[] = as;
            as = new String[i];
            System.arraycopy(as1, 0, as, 0, i);
        }
        super.dircache = as;
        super.cacheTime = attr.mtime;
        return as;
    }

    String readlink()
        throws IOException
    {
        if(super.symlink != null && cacheOK(super.cacheTime))
            return super.symlink;
        Xdr xdr = new Xdr(super.rsize + 200);
        super.rpc.rpc_header(xdr, 5);
        xdr.xdr_raw(super.fh);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 2000, 0);
        int i = xdr1.xdr_int();
        if(i != 0)
        {
            throw new NfsException(i);
        } else
        {
            super.symlink = xdr1.xdr_string();
            super.cacheTime = attr.mtime;
            return super.symlink;
        }
    }

    Nfs create(String s, long l)
        throws IOException
    {
        return create_otw(9, s, l);
    }

    Nfs mkdir(String s, long l)
        throws IOException
    {
        return create_otw(14, s, l);
    }

    private Nfs create_otw(int i, String s, long l)
        throws IOException
    {
        long l1 = System.currentTimeMillis();
        Xdr xdr = new Xdr(super.rsize + 200);
        super.rpc.rpc_header(xdr, i);
        xdr.xdr_raw(super.fh);
        xdr.xdr_string(s);
        xdr.xdr_u_int(l);
        xdr.xdr_u_int(NfsConnect.getCred().getUid());
        xdr.xdr_u_int(NfsConnect.getCred().getGid());
        xdr.xdr_u_int(0L);
        xdr.xdr_u_int(l1 / 1000L);
        xdr.xdr_u_int(l1 % 1000L);
        xdr.xdr_u_int(l1 / 1000L);
        xdr.xdr_u_int(l1 % 1000L);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 2000, 0);
        int j = xdr1.xdr_int();
        if(j != 0)
        {
            throw new NfsException(j);
        } else
        {
            byte abyte0[] = xdr1.xdr_raw(32);
            Fattr2 fattr2 = new Fattr2(xdr1);
            String s1 = super.name + "/" + s;
            Nfs2 nfs2 = new Nfs2(super.rpc, abyte0, s1, fattr2);
            Nfs.cache_put(nfs2);
            super.dircache = null;
            return nfs2;
        }
    }

    void fsinfo()
        throws IOException
    {
        super.wsize = 8192;
    }

    long commit(int i, int j)
        throws IOException
    {
        return 0L;
    }

    boolean remove(String s)
        throws IOException
    {
        return remove_otw(10, s);
    }

    boolean rmdir(String s)
        throws IOException
    {
        return remove_otw(15, s);
    }

    private boolean remove_otw(int i, String s)
        throws IOException
    {
        Xdr xdr = new Xdr(super.rsize + 200);
        super.rpc.rpc_header(xdr, i);
        xdr.xdr_raw(super.fh);
        xdr.xdr_string(s);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 2000, 0);
        int j = xdr1.xdr_int();
        if(j != 0)
        {
            throw new NfsException(j);
        } else
        {
            Nfs.cache_remove(this, s);
            super.dircache = null;
            return true;
        }
    }

    boolean rename(Nfs nfs, String s, String s1)
        throws IOException
    {
        Xdr xdr = new Xdr(super.rsize + 200);
        super.rpc.rpc_header(xdr, 11);
        xdr.xdr_raw(super.fh);
        xdr.xdr_string(s);
        xdr.xdr_raw(nfs.getFH());
        xdr.xdr_string(s1);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 2000, 0);
        int i = xdr1.xdr_int();
        if(i != 0)
        {
            throw new NfsException(i);
        } else
        {
            Nfs.cache_remove(this, s);
            super.dircache = null;
            nfs.dircache = null;
            return true;
        }
    }

    Fattr2 attr;
    private static final int NFSPROC2_NULL = 0;
    private static final int NFSPROC2_GETATTR = 1;
    private static final int NFSPROC2_SETATTR = 2;
    private static final int NFSPROC2_LOOKUP = 4;
    private static final int NFSPROC2_READLINK = 5;
    private static final int NFSPROC2_READ = 6;
    private static final int NFSPROC2_WRITE = 8;
    private static final int NFSPROC2_CREATE = 9;
    private static final int NFSPROC2_REMOVE = 10;
    private static final int NFSPROC2_RENAME = 11;
    private static final int NFSPROC2_LINK = 12;
    private static final int NFSPROC2_SYMLINK = 13;
    private static final int NFSPROC2_MKDIR = 14;
    private static final int NFSPROC2_RMDIR = 15;
    private static final int NFSPROC2_READDIR = 16;
    private static final int NFSPROC2_STATFS = 17;
    private static final int NFS_OK = 0;
    private static final int RWSIZE = 8192;
    private static final int FHSIZE = 32;
    int nwb;
}
