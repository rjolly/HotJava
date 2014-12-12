// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MsgRejectedException.java

package com.sun.rpc;


// Referenced classes of package com.sun.rpc:
//            RpcException

public class MsgRejectedException extends RpcException
{

    public MsgRejectedException(int i)
    {
        super(i);
    }

    public MsgRejectedException(int i, int j)
    {
        super(i);
        why = j;
    }

    public MsgRejectedException(int i, int j, int k)
    {
        super(i);
        lo = j;
        hi = k;
    }

    public String toString()
    {
        switch(super.error)
        {
        case 0: // '\0'
            return "Version mismatch: low=" + lo + ",high=" + hi;

        case 1: // '\001'
            String s = "Authentication error: ";
            switch(why)
            {
            case 1: // '\001'
                s = s + "bogus credentials (seal broken)";
                break;

            case 2: // '\002'
                s = s + "client should begin new session";
                break;

            case 3: // '\003'
                s = s + "bogus verifier (seal broken)";
                break;

            case 4: // '\004'
                s = s + "verifier expired or was replayed";
                break;

            case 5: // '\005'
                s = s + "too weak";
                break;

            case 6: // '\006'
                s = s + "bogus response verifier";
                break;

            case 7: // '\007'
            default:
                s = s + "unknown reason";
                break;
            }
            return s;
        }
        return "Unknown RPC Error = " + super.error;
    }

    int lo;
    int hi;
    int why;
    public static final int RPC_MISMATCH = 0;
    public static final int AUTH_ERROR = 1;
    public static final int AUTH_BADCRED = 1;
    public static final int AUTH_REJECTEDCRED = 2;
    public static final int AUTH_BADVERF = 3;
    public static final int AUTH_REJECTEDVERF = 4;
    public static final int AUTH_TOOWEAK = 5;
    public static final int AUTH_INVALIDRESP = 6;
    public static final int AUTH_FAILED = 7;
}
