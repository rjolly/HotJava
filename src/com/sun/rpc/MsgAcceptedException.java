// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MsgAcceptedException.java

package com.sun.rpc;


// Referenced classes of package com.sun.rpc:
//            RpcException

public class MsgAcceptedException extends RpcException
{

    public MsgAcceptedException(int i)
    {
        super(i);
    }

    public MsgAcceptedException(int i, int j, int k)
    {
        super(i);
        lo = j;
        hi = k;
    }

    public String toString()
    {
        switch(super.error)
        {
        case 1: // '\001'
            return "Program unavailable";

        case 2: // '\002'
            return "Program number mismatch: low=" + lo + ",high=" + hi;

        case 3: // '\003'
            return "Procedure Unavailable: low=" + lo + ",high=" + hi;

        case 4: // '\004'
            return "Garbage Arguments";

        case 5: // '\005'
            return "System error";
        }
        return "Unknown RPC Error = " + super.error;
    }

    int lo;
    int hi;
    public static final int PROG_UNAVAIL = 1;
    public static final int PROG_MISMATCH = 2;
    public static final int PROC_UNAVAIL = 3;
    public static final int GARBAGE_ARGS = 4;
    public static final int SYSTEM_ERR = 5;
}
