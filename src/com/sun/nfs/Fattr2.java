// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Fattr2.java

package com.sun.nfs;

import com.sun.rpc.Xdr;
import java.util.Date;

// Referenced classes of package com.sun.nfs:
//            Fattr

class Fattr2 extends Fattr
{

    Fattr2()
    {
    }

    Fattr2(Xdr xdr)
    {
        getFattr(xdr);
    }

    void putFattr(Xdr xdr)
    {
        xdr.xdr_int(ftype);
        xdr.xdr_u_int(mode);
        xdr.xdr_u_int(nlink);
        xdr.xdr_u_int(uid);
        xdr.xdr_u_int(gid);
        xdr.xdr_u_int(size);
        xdr.xdr_u_int(blocksz);
        xdr.xdr_u_int(rdev);
        xdr.xdr_u_int(blocks);
        xdr.xdr_u_int(fsid);
        xdr.xdr_u_int(fileid);
        xdr.xdr_u_int(atime / 1000L);
        xdr.xdr_u_int(atime % 1000L);
        xdr.xdr_u_int(mtime / 1000L);
        xdr.xdr_u_int(mtime % 1000L);
        xdr.xdr_u_int(ctime / 1000L);
        xdr.xdr_u_int(ctime % 1000L);
    }

    void getFattr(Xdr xdr)
    {
        long l = mtime;
        ftype = xdr.xdr_int();
        mode = xdr.xdr_u_int();
        nlink = xdr.xdr_u_int();
        uid = xdr.xdr_u_int();
        if(uid == -2L)
            uid = 60001L;
        gid = xdr.xdr_u_int();
        if(gid == -2L)
            gid = 60001L;
        size = xdr.xdr_u_int();
        blocksz = xdr.xdr_u_int();
        rdev = xdr.xdr_u_int();
        blocks = xdr.xdr_u_int();
        fsid = xdr.xdr_u_int();
        fileid = xdr.xdr_u_int();
        atime = xdr.xdr_u_int() * 1000L + xdr.xdr_u_int();
        mtime = xdr.xdr_u_int() * 1000L + xdr.xdr_u_int();
        ctime = xdr.xdr_u_int() * 1000L + xdr.xdr_u_int();
        long l1 = mtime - l;
        if(l1 > 0L)
        {
            super.cachetime = l1;
            if(super.cachetime < 3000L)
                super.cachetime = 3000L;
            else
            if(super.cachetime > 60000L)
                super.cachetime = 60000L;
        }
        super.validtime = System.currentTimeMillis();
    }

    public String toString()
    {
        return "  ftype = " + ftype + "\n" + "   mode = 0" + Long.toOctalString(mode) + "\n" + "  nlink = " + nlink + "\n" + "    uid = " + uid + "\n" + "    gid = " + gid + "\n" + "   size = " + size + "\n" + "blocksz = " + blocksz + "\n" + "   rdev = 0x" + Long.toHexString(rdev) + "\n" + " blocks = " + blocks + "\n" + "   fsid = " + fsid + "\n" + " fileid = " + fileid + "\n" + "  atime = " + new Date(atime) + "\n" + "  mtime = " + new Date(mtime) + "\n" + "  ctime = " + new Date(ctime);
    }

    int ftype;
    long mode;
    long nlink;
    long uid;
    long gid;
    long size;
    long blocksz;
    long rdev;
    long blocks;
    long fsid;
    long fileid;
    long atime;
    long mtime;
    long ctime;
}
