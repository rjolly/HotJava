// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Fattr3.java

package com.sun.nfs;

import com.sun.rpc.Xdr;
import java.util.Date;

// Referenced classes of package com.sun.nfs:
//            Fattr

class Fattr3 extends Fattr
{

    Fattr3()
    {
    }

    Fattr3(Xdr xdr)
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
        xdr.xdr_hyper(size);
        xdr.xdr_hyper(used);
        xdr.xdr_hyper(rdev);
        xdr.xdr_hyper(fsid);
        xdr.xdr_hyper(fileid);
        xdr.xdr_u_int(atime / 1000L);
        xdr.xdr_u_int((atime % 1000L) * 0xf4240L);
        xdr.xdr_u_int(mtime / 1000L);
        xdr.xdr_u_int((mtime % 1000L) * 0xf4240L);
        xdr.xdr_u_int(ctime / 1000L);
        xdr.xdr_u_int((ctime % 1000L) * 0xf4240L);
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
        size = xdr.xdr_hyper();
        used = xdr.xdr_hyper();
        rdev = xdr.xdr_hyper();
        fsid = xdr.xdr_hyper();
        fileid = xdr.xdr_hyper();
        atime = xdr.xdr_u_int() * 1000L + xdr.xdr_u_int() / 0xf4240L;
        mtime = xdr.xdr_u_int() * 1000L + xdr.xdr_u_int() / 0xf4240L;
        ctime = xdr.xdr_u_int() * 1000L + xdr.xdr_u_int() / 0xf4240L;
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
        return " ftype = " + ftype + "\n" + "  mode = 0" + Long.toOctalString(mode) + "\n" + " nlink = " + nlink + "\n" + "   uid = " + uid + "\n" + "   gid = " + gid + "\n" + "  size = " + size + "\n" + "  used = " + used + "\n" + "  rdev = 0x" + Long.toHexString(rdev) + "\n" + "  fsid = " + fsid + "\n" + "fileid = " + fileid + "\n" + " atime = " + new Date(atime) + "\n" + " mtime = " + new Date(mtime) + "\n" + " ctime = " + new Date(ctime);
    }

    int ftype;
    long mode;
    long nlink;
    long uid;
    long gid;
    long size;
    long used;
    long rdev;
    long fsid;
    long fileid;
    long atime;
    long mtime;
    long ctime;
}
