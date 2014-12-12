// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   XFileOutputStream.java

package com.sun.xfile;

import java.io.IOException;
import java.io.OutputStream;

// Referenced classes of package com.sun.xfile:
//            XFile, XFileAccessor

public class XFileOutputStream extends OutputStream
{

    public XFileOutputStream(XFile xfile)
        throws IOException
    {
        xfa = xfile.newAccessor();
        if(xfa.open(xfile, true, false))
        {
            if(!xfa.isFile())
                throw new IOException("not a file");
            if(!xfa.canWrite())
                throw new IOException("no write permission");
        }
        if(!xfa.mkfile())
            throw new IOException("no write permission");
        else
            return;
    }

    public XFileOutputStream(String s)
        throws IOException
    {
        this(new XFile(s));
    }

    public XFileOutputStream(XFile xfile, boolean flag)
        throws IOException
    {
        xfa = xfile.newAccessor();
        boolean flag1;
        if(flag1 = xfa.open(xfile, true, false))
        {
            if(!xfa.isFile())
                throw new IOException("not a file");
            if(!xfa.canWrite())
                throw new IOException("no write permission");
        }
        if((!flag1 || !flag) && !xfa.mkfile())
            throw new IOException("no write permission");
        if(flag)
            fp = xfa.length();
    }

    public XFileOutputStream(String s, boolean flag)
        throws IOException
    {
        this(new XFile(s), flag);
    }

    private synchronized void XFAwrite(byte abyte0[], int i, int j)
        throws IOException
    {
        if(abyte0 == null)
            throw new NullPointerException();
        if(j == 0)
            return;
        if(i < 0 || j < 0 || i >= abyte0.length || i + j > abyte0.length)
        {
            throw new IllegalArgumentException("Invalid argument");
        } else
        {
            xfa.write(abyte0, i, j, fp);
            fp += j;
            return;
        }
    }

    public void write(int i)
        throws IOException
    {
        XFAwrite(new byte[] {
            (byte)i
        }, 0, 1);
    }

    public void write(byte abyte0[])
        throws IOException
    {
        XFAwrite(abyte0, 0, abyte0.length);
    }

    public void write(byte abyte0[], int i, int j)
        throws IOException
    {
        XFAwrite(abyte0, i, j);
    }

    public void flush()
        throws IOException
    {
        xfa.flush();
    }

    public void close()
        throws IOException
    {
        xfa.close();
    }

    protected void finalize()
        throws IOException
    {
        close();
    }

    private long fp;
    private XFileAccessor xfa;
}
