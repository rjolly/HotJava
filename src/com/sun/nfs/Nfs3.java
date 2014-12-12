// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Nfs3.java

package com.sun.nfs;

import com.sun.rpc.*;
import java.io.IOException;

// Referenced classes of package com.sun.nfs:
//            Nfs, Buffer, Fattr, Fattr3, 
//            NfsConnect, NfsException

class Nfs3 extends Nfs
{

    Nfs3(Rpc rpc, byte abyte0[], String s, Fattr3 fattr3)
    {
        accessBits = -1;
        prevWriteIndex = -1;
        super.rpc = rpc;
        super.fh = abyte0;
        if(s.startsWith("./"))
            s = s.substring(2);
        super.name = s;
        attr = fattr3 != null ? fattr3 : new Fattr3();
        super.rsize = 32768;
        super.NRA = 1;
        super.NWB = 4;
        super.NWC = 10;
    }

    void getattr()
        throws IOException
    {
        Xdr xdr1 = new Xdr(super.rsize + 200);
        super.rpc.rpc_header(xdr1, 1);
        xdr1.xdr_bytes(super.fh);
        Xdr xdr;
        try
        {
            xdr = super.rpc.rpc_call(xdr1, 2000, 2);
        }
        catch(IOException _ex)
        {
            return;
        }
        int i = xdr.xdr_int();
        if(i != 0)
        {
            throw new NfsException(i);
        } else
        {
            attr.getFattr(xdr);
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

    private boolean check_access(int i)
        throws IOException
    {
        boolean flag = true;
        byte byte0 = 28;
        if(accessBits < 0 || !cacheOK(accessTime))
        {
            Xdr xdr = new Xdr(super.rsize + 200);
            super.rpc.rpc_header(xdr, 4);
            xdr.xdr_bytes(super.fh);
            xdr.xdr_int(flag | byte0);
            Xdr xdr1 = super.rpc.rpc_call(xdr, 5000, 0);
            int j = xdr1.xdr_int();
            if(xdr1.xdr_bool())
                attr.getFattr(xdr1);
            if(j != 0)
                throw new NfsException(j);
            accessBits = xdr1.xdr_int();
            accessTime = attr.mtime;
        }
        if((i & 4) != 0)
            return (accessBits & flag) != 0;
        if((i & 2) != 0)
            return (accessBits & byte0) != 0;
        else
            return true;
    }

    boolean canWrite()
        throws IOException
    {
        return check_access(2);
    }

    boolean canRead()
        throws IOException
    {
        return check_access(4);
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
        Fattr3 fattr3 = null;
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
            if(((Nfs3)obj).attr.ftype == 5)
                obj = NfsConnect.followLink(((Nfs) (obj)));
            return ((Nfs) (obj));
        }
        Xdr xdr = new Xdr(super.rsize + 200);
        super.rpc.rpc_header(xdr, 3);
        xdr.xdr_bytes(super.fh);
        xdr.xdr_string(s);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 5000, 0);
        int i = xdr1.xdr_int();
        if(i != 0)
        {
            if(xdr1.xdr_bool())
                attr.getFattr(xdr1);
            throw new NfsException(i);
        }
        byte abyte0[] = xdr1.xdr_bytes();
        if(xdr1.xdr_bool())
            fattr3 = new Fattr3(xdr1);
        if(xdr1.xdr_bool())
            attr.getFattr(xdr1);
        obj = new Nfs3(super.rpc, abyte0, s1, fattr3);
        Nfs.cache_put(((Nfs) (obj)));
        if(((Nfs3)obj).attr.ftype == 5)
            obj = NfsConnect.followLink(((Nfs) (obj)));
        return ((Nfs) (obj));
    }

    void read_otw(Buffer buffer)
        throws IOException
    {
        Xdr xdr = new Xdr(super.rsize + 200);
        super.rpc.rpc_header(xdr, 6);
        xdr.xdr_bytes(super.fh);
        xdr.xdr_hyper(buffer.foffset);
        xdr.xdr_int(super.rsize);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 1000, 0);
        int i = xdr1.xdr_int();
        if(xdr1.xdr_bool())
            attr.getFattr(xdr1);
        if(i != 0)
            throw new NfsException(i);
        int j = xdr1.xdr_int();
        buffer.eof = xdr1.xdr_bool();
        if((long)j != xdr1.xdr_u_int())
        {
            throw new NfsException(10005);
        } else
        {
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
        super.rpc.rpc_header(xdr, 7);
        xdr.xdr_bytes(super.fh);
        xdr.xdr_hyper(buffer.foffset + (long)buffer.minOffset);
        xdr.xdr_u_int(buffer.maxOffset - buffer.minOffset);
        xdr.xdr_int(buffer.syncType);
        xdr.xdr_bytes(buffer.buf, buffer.bufoff + buffer.minOffset, buffer.maxOffset - buffer.minOffset);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 2000, 0);
        int i = xdr1.xdr_int();
        if(xdr1.xdr_bool())
        {
            xdr1.xdr_hyper();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
        }
        if(xdr1.xdr_bool())
        {
            attr.getFattr(xdr1);
            super.cacheTime = attr.mtime;
        }
        if(i != 0)
            throw new NfsException(i);
        int j = xdr1.xdr_int();
        if(xdr1.xdr_int() == 2)
            buffer.status = 1;
        else
            buffer.status = 3;
        buffer.writeVerifier = xdr1.xdr_hyper();
        return j;
    }

    String[] readdir()
        throws IOException
    {
        long l = 0L;
        long l1 = 0L;
        boolean flag = false;
        String as[] = new String[32];
        int i = 0;
        if(super.dircache != null)
            if(cacheOK(super.cacheTime))
            {
                return super.dircache;
            } else
            {
                super.dircache = null;
                return readdir_old();
            }
        Xdr xdr = new Xdr(super.rsize + 200);
        Xdr xdr1;
        for(; !flag; flag = xdr1.xdr_bool())
        {
            super.rpc.rpc_header(xdr, 17);
            xdr.xdr_bytes(super.fh);
            xdr.xdr_hyper(l);
            xdr.xdr_hyper(l1);
            xdr.xdr_u_int(1024L);
            xdr.xdr_u_int(8192L);
            xdr1 = super.rpc.rpc_call(xdr, 3000, 0);
            int j = xdr1.xdr_int();
            if(xdr1.xdr_bool())
                attr.getFattr(xdr1);
            if(j == 10004)
                return readdir_old();
            if(j != 0)
                throw new NfsException(j);
            l1 = xdr1.xdr_hyper();
            while(xdr1.xdr_bool()) 
            {
                xdr1.xdr_hyper();
                String s = xdr1.xdr_string();
                l = xdr1.xdr_hyper();
                Fattr3 fattr3 = null;
                byte abyte0[] = null;
                if(xdr1.xdr_bool())
                    fattr3 = new Fattr3(xdr1);
                if(xdr1.xdr_bool())
                    abyte0 = xdr1.xdr_bytes();
                if(!s.equals(".") && !s.equals(".."))
                {
                    as[i++] = s;
                    if(i >= as.length)
                    {
                        String as2[] = as;
                        as = new String[i * 2];
                        System.arraycopy(as2, 0, as, 0, i);
                    }
                    if(abyte0 != null && fattr3 != null)
                    {
                        String s1;
                        if(super.name == null)
                            s1 = s;
                        else
                            s1 = super.name + "/" + s;
                        Nfs.cache_put(new Nfs3(super.rpc, abyte0, s1, fattr3));
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

    String[] readdir_old()
        throws IOException
    {
        long l = 0L;
        long l1 = 0L;
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
            xdr.xdr_bytes(super.fh);
            xdr.xdr_hyper(l);
            xdr.xdr_hyper(l1);
            xdr.xdr_u_int(8192L);
            xdr1 = super.rpc.rpc_call(xdr, 3000, 0);
            int j = xdr1.xdr_int();
            if(xdr1.xdr_bool())
                attr.getFattr(xdr1);
            if(j != 0)
                throw new NfsException(j);
            l1 = xdr1.xdr_hyper();
            while(xdr1.xdr_bool()) 
            {
                xdr1.xdr_hyper();
                String s = xdr1.xdr_string();
                if(!s.equals(".") && !s.equals(".."))
                    as[i++] = s;
                if(i >= as.length)
                {
                    String as2[] = as;
                    as = new String[i * 2];
                    System.arraycopy(as2, 0, as, 0, i);
                }
                l = xdr1.xdr_hyper();
            }
        }

        if(i == 0)
            return null;
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
        xdr.xdr_bytes(super.fh);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 2000, 0);
        int i = xdr1.xdr_int();
        if(xdr1.xdr_bool())
            attr.getFattr(xdr1);
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
        byte abyte0[] = null;
        Fattr3 fattr3 = null;
        Xdr xdr = new Xdr(super.rsize + 200);
        super.rpc.rpc_header(xdr, 8);
        xdr.xdr_bytes(super.fh);
        xdr.xdr_string(s);
        xdr.xdr_int(0);
        xdr.xdr_bool(true);
        xdr.xdr_u_int(l);
        xdr.xdr_bool(true);
        xdr.xdr_u_int(NfsConnect.getCred().getUid());
        xdr.xdr_bool(true);
        xdr.xdr_u_int(NfsConnect.getCred().getGid());
        xdr.xdr_bool(true);
        xdr.xdr_hyper(0L);
        xdr.xdr_int(1);
        xdr.xdr_int(1);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 2000, 0);
        int i = xdr1.xdr_int();
        if(i != 0)
        {
            if(xdr1.xdr_bool())
            {
                xdr1.xdr_hyper();
                xdr1.xdr_u_int();
                xdr1.xdr_u_int();
                xdr1.xdr_u_int();
                xdr1.xdr_u_int();
            }
            if(xdr1.xdr_bool())
                attr.getFattr(xdr1);
            throw new NfsException(i);
        }
        if(xdr1.xdr_bool())
            abyte0 = xdr1.xdr_bytes();
        if(xdr1.xdr_bool())
            fattr3 = new Fattr3(xdr1);
        if(xdr1.xdr_bool())
        {
            xdr1.xdr_hyper();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
        }
        if(xdr1.xdr_bool())
            attr.getFattr(xdr1);
        Nfs3 nfs3;
        if(abyte0 != null && fattr3 != null)
        {
            String s1 = super.name + "/" + s;
            nfs3 = new Nfs3(super.rpc, abyte0, s1, fattr3);
            Nfs.cache_put(nfs3);
        } else
        {
            nfs3 = null;
        }
        return nfs3;
    }

    Nfs mkdir(String s, long l)
        throws IOException
    {
        byte abyte0[] = null;
        Fattr3 fattr3 = null;
        Nfs3 nfs3 = null;
        Xdr xdr = new Xdr(super.rsize + 200);
        super.rpc.rpc_header(xdr, 9);
        xdr.xdr_bytes(super.fh);
        xdr.xdr_string(s);
        xdr.xdr_bool(true);
        xdr.xdr_u_int(l);
        xdr.xdr_bool(true);
        xdr.xdr_u_int(NfsConnect.getCred().getUid());
        xdr.xdr_bool(true);
        xdr.xdr_u_int(NfsConnect.getCred().getGid());
        xdr.xdr_bool(true);
        xdr.xdr_hyper(0L);
        xdr.xdr_int(1);
        xdr.xdr_int(1);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 2000, 0);
        int i = xdr1.xdr_int();
        if(i != 0)
        {
            if(xdr1.xdr_bool())
            {
                xdr1.xdr_hyper();
                xdr1.xdr_u_int();
                xdr1.xdr_u_int();
                xdr1.xdr_u_int();
                xdr1.xdr_u_int();
            }
            if(xdr1.xdr_bool())
                attr.getFattr(xdr1);
            throw new NfsException(i);
        }
        if(xdr1.xdr_bool())
            abyte0 = xdr1.xdr_bytes();
        if(xdr1.xdr_bool())
            fattr3 = new Fattr3(xdr1);
        if(xdr1.xdr_bool())
        {
            xdr1.xdr_hyper();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
        }
        if(xdr1.xdr_bool())
            attr.getFattr(xdr1);
        if(abyte0 != null && fattr3 != null)
        {
            String s1 = super.name + "/" + s;
            nfs3 = new Nfs3(super.rpc, abyte0, s1, fattr3);
            Nfs.cache_put(nfs3);
        }
        super.dircache = null;
        return nfs3;
    }

    void fsinfo()
        throws IOException
    {
        Xdr xdr = new Xdr(super.rsize + 200);
        super.rpc.rpc_header(xdr, 19);
        xdr.xdr_bytes(super.fh);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 2000, 0);
        int i = xdr1.xdr_int();
        if(xdr1.xdr_bool())
            attr.getFattr(xdr1);
        if(i != 0)
        {
            throw new NfsException(i);
        } else
        {
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
            super.wsize = xdr1.xdr_int();
            return;
        }
    }

    long commit(int i, int j)
        throws IOException
    {
        Xdr xdr = new Xdr(super.wsize + 200);
        super.rpc.rpc_header(xdr, 21);
        xdr.xdr_bytes(super.fh);
        xdr.xdr_hyper(i);
        xdr.xdr_u_int(j);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 2000, 0);
        int k = xdr1.xdr_int();
        if(xdr1.xdr_bool())
        {
            xdr1.xdr_hyper();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
        }
        if(xdr1.xdr_bool())
            attr.getFattr(xdr1);
        if(k != 0)
            throw new NfsException(k);
        else
            return xdr1.xdr_hyper();
    }

    boolean remove(String s)
        throws IOException
    {
        return remove_otw(12, s);
    }

    boolean rmdir(String s)
        throws IOException
    {
        return remove_otw(13, s);
    }

    private boolean remove_otw(int i, String s)
        throws IOException
    {
        Xdr xdr = new Xdr(super.rsize + 200);
        super.rpc.rpc_header(xdr, i);
        xdr.xdr_bytes(super.fh);
        xdr.xdr_string(s);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 2000, 0);
        int j = xdr1.xdr_int();
        if(xdr1.xdr_bool())
        {
            xdr1.xdr_hyper();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
        }
        if(xdr1.xdr_bool())
            attr.getFattr(xdr1);
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
        super.rpc.rpc_header(xdr, 14);
        xdr.xdr_bytes(super.fh);
        xdr.xdr_string(s);
        xdr.xdr_bytes(nfs.getFH());
        xdr.xdr_string(s1);
        Xdr xdr1 = super.rpc.rpc_call(xdr, 2000, 0);
        int i = xdr1.xdr_int();
        if(xdr1.xdr_bool())
        {
            xdr1.xdr_hyper();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
            xdr1.xdr_u_int();
        }
        if(xdr1.xdr_bool())
            attr.getFattr(xdr1);
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

    Fattr3 attr;
    int accessBits;
    long accessTime;
    private static final int NFSPROC3_NULL = 0;
    private static final int NFSPROC3_GETATTR = 1;
    private static final int NFSPROC3_SETATTR = 2;
    private static final int NFSPROC3_LOOKUP = 3;
    private static final int NFSPROC3_ACCESS = 4;
    private static final int NFSPROC3_READLINK = 5;
    private static final int NFSPROC3_READ = 6;
    private static final int NFSPROC3_WRITE = 7;
    private static final int NFSPROC3_CREATE = 8;
    private static final int NFSPROC3_MKDIR = 9;
    private static final int NFSPROC3_SYMLINK = 10;
    private static final int NFSPROC3_MKNOD = 11;
    private static final int NFSPROC3_REMOVE = 12;
    private static final int NFSPROC3_RMDIR = 13;
    private static final int NFSPROC3_RENAME = 14;
    private static final int NFSPROC3_LINK = 15;
    private static final int NFSPROC3_READDIR = 16;
    private static final int NFSPROC3_READDIRPLUS = 17;
    private static final int NFSPROC3_FSSTAT = 18;
    private static final int NFSPROC3_FSINFO = 19;
    private static final int NFSPROC3_PATHCONF = 20;
    private static final int NFSPROC3_COMMIT = 21;
    private static final int NFS_OK = 0;
    private static final int NFS3ERR_NOTSUPP = 10004;
    private static final int RWSIZE = 32768;
    private static final int DIRCOUNT = 1024;
    private static final int MAXBSIZE = 8192;
    private static final int DONT_CHANGE = 0;
    private static final int SERVER_TIME = 1;
    private static final int CLIENT_TIME = 2;
    private static final int ACCESS3_READ = 1;
    private static final int ACCESS3_LOOKUP = 2;
    private static final int ACCESS3_MODIFY = 4;
    private static final int ACCESS3_EXTEND = 8;
    private static final int ACCESS3_DELETE = 16;
    private static final int ACCESS3_EXECUTE = 32;
    private static final int UNCHECKED = 0;
    private static final int GUARDED = 1;
    private static final int EXCLUSIVE = 2;
    private static final int UNSTABLE = 0;
    private static final int DATA_SYNC = 1;
    private static final int FILE_SYNC = 2;
    int nra;
    int nwb;
    int prevWriteIndex;
}
