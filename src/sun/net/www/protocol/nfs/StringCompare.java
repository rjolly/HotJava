// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NfsURLConnection.java

package sun.net.www.protocol.nfs;

import sun.misc.Compare;

// Referenced classes of package sun.net.www.protocol.nfs:
//            NfsURLConnection

class StringCompare
    implements Compare
{

    public int doCompare(Object obj, Object obj1)
    {
        String s = ((String)obj).toLowerCase();
        String s1 = ((String)obj1).toLowerCase();
        return s.compareTo(s1);
    }

    StringCompare()
    {
    }
}
