// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NfsException.java

package com.sun.nfs;

import java.io.IOException;

public class NfsException extends IOException
{

    public NfsException(int i)
    {
        super("NFS error: " + i);
        error = i;
    }

    public String toString()
    {
        switch(error)
        {
        case 0: // '\0'
            return "OK";

        case 1: // '\001'
            return "Not owner";

        case 2: // '\002'
            return "No such file or directory";

        case 5: // '\005'
            return "I/O error";

        case 6: // '\006'
            return "No such device or address";

        case 13: // '\r'
            return "Permission denied";

        case 17: // '\021'
            return "File exists";

        case 18: // '\022'
            return "Attempted cross-device link";

        case 19: // '\023'
            return "No such device";

        case 20: // '\024'
            return "Not a directory";

        case 21: // '\025'
            return "Is a directory";

        case 22: // '\026'
            return "Invalid argument";

        case 27: // '\033'
            return "File too large";

        case 28: // '\034'
            return "No space left on device";

        case 30: // '\036'
            return "Read-only file system";

        case 31: // '\037'
            return "Too many links";

        case 63: // '?'
            return "File name too long";

        case 66: // 'B'
            return "Directory not empty";

        case 69: // 'E'
            return "Disk quota exceeded";

        case 70: // 'F'
            return "Stale NFS file handle";

        case 71: // 'G'
            return "Too many levels of remote in path";

        case 10001: 
            return "Illegal NFS file handle";

        case 10002: 
            return "Update sync mismatch";

        case 10003: 
            return "Readdir cookie is stale";

        case 10004: 
            return "Operation not supported";

        case 10005: 
            return "Buffer/request too small";

        case 10006: 
            return "Server fault";

        case 10007: 
            return "Bad type";

        case 10008: 
            return "Jukebox error: try later";
        }
        return "Unknown NFS error: " + error;
    }

    int error;
    public static final int NFS_OK = 0;
    public static final int NFSERR_PERM = 1;
    public static final int NFSERR_NOENT = 2;
    public static final int NFSERR_IO = 5;
    public static final int NFSERR_NXIO = 6;
    public static final int NFSERR_ACCES = 13;
    public static final int NFSERR_EXIST = 17;
    public static final int NFSERR_XDEV = 18;
    public static final int NFSERR_NODEV = 19;
    public static final int NFSERR_NOTDIR = 20;
    public static final int NFSERR_ISDIR = 21;
    public static final int NFSERR_INVAL = 22;
    public static final int NFSERR_FBIG = 27;
    public static final int NFSERR_NOSPC = 28;
    public static final int NFSERR_ROFS = 30;
    public static final int NFSERR_MLINK = 31;
    public static final int NFSERR_NAMETOOLONG = 63;
    public static final int NFSERR_NOTEMPTY = 66;
    public static final int NFSERR_DQUOT = 69;
    public static final int NFSERR_STALE = 70;
    public static final int NFSERR_REMOTE = 71;
    public static final int NFSERR_BADHANDLE = 10001;
    public static final int NFSERR_NOT_SYNC = 10002;
    public static final int NFSERR_BAD_COOKIE = 10003;
    public static final int NFSERR_NOTSUPP = 10004;
    public static final int NFSERR_TOOSMALL = 10005;
    public static final int NFSERR_SERVERFAULT = 10006;
    public static final int NFSERR_BADTYPE = 10007;
    public static final int NFSERR_JUKEBOX = 10008;
}
