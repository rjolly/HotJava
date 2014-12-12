// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   XFileInputStream.java

package com.sun.xfile;

import java.io.*;

// Referenced classes of package com.sun.xfile:
//            XFile, XFileAccessor

public class XFileInputStream extends InputStream
{

    public XFileInputStream(XFile xfile)
        throws IOException
    {
        xfa = xfile.newAccessor();
        if(!xfa.open(xfile, true, true))
            throw new FileNotFoundException("no file");
        if(!xfa.canRead())
            throw new IOException("no read permission");
        else
            return;
    }

    public XFileInputStream(String s)
        throws IOException
    {
        this(new XFile(s));
    }

    private synchronized int XFAread(byte abyte0[], int i, int j)
        throws IOException
    {
        if(abyte0 == null)
            throw new NullPointerException();
        if(j == 0)
            return 0;
        if(i < 0 || j < 0 || i >= abyte0.length || i + j > abyte0.length)
            throw new IllegalArgumentException("Invalid argument");
        int k = xfa.read(abyte0, i, j, fp);
        if(k <= 0)
        {
            return -1;
        } else
        {
            fp += k;
            return k;
        }
    }

    public int read()
        throws IOException
    {
        byte abyte0[] = new byte[1];
        if(XFAread(abyte0, 0, 1) != 1)
            return -1;
        else
            return abyte0[0];
    }

    public int read(byte abyte0[])
        throws IOException
    {
        return XFAread(abyte0, 0, abyte0.length);
    }

    public int read(byte abyte0[], int i, int j)
        throws IOException
    {
        return XFAread(abyte0, i, j);
    }

    public int available()
        throws IOException
    {
        return (int)(xfa.length() - fp);
    }

    public long skip(long l)
        throws IOException
    {
        if(l < 0L)
        {
            throw new IllegalArgumentException("illegal skip: " + l);
        } else
        {
            fp += l;
            return l;
        }
    }

    public void close()
        throws IOException
    {
        xfa.close();
    }

    private long fp;
    private XFileAccessor xfa;
}
