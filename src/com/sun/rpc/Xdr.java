// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Xdr.java

package com.sun.rpc;


public class Xdr
{

    public Xdr(int i)
    {
        buf = new byte[i];
        off = 0;
    }

    public void xdr_skip(int i)
    {
        int j = (off += i) % XDRUNIT;
        if(j > 0)
            off += XDRUNIT - j;
    }

    public byte[] xdr_buf()
    {
        return buf;
    }

    public int xdr_offset()
    {
        return off;
    }

    public void xdr_offset(int i)
    {
        off = i;
    }

    public int xdr_size()
    {
        return size;
    }

    public void xdr_size(int i)
    {
        size = i;
    }

    public int xdr_int()
    {
        return (buf[off++] & 0xff) << 24 | (buf[off++] & 0xff) << 16 | (buf[off++] & 0xff) << 8 | buf[off++] & 0xff;
    }

    public void xdr_int(int i)
    {
        buf[off++] = (byte)(i >>> 24);
        buf[off++] = (byte)(i >> 16);
        buf[off++] = (byte)(i >> 8);
        buf[off++] = (byte)i;
    }

    public long xdr_u_int()
    {
        return (long)((buf[off++] & 0xff) << 24 | (buf[off++] & 0xff) << 16 | (buf[off++] & 0xff) << 8 | buf[off++] & 0xff);
    }

    public void xdr_u_int(long l)
    {
        buf[off++] = (byte)(int)(l >>> 24 & 255L);
        buf[off++] = (byte)(int)(l >> 16);
        buf[off++] = (byte)(int)(l >> 8);
        buf[off++] = (byte)(int)l;
    }

    public long xdr_hyper()
    {
        return (long)(buf[off++] & 0xff) << 56 | (long)(buf[off++] & 0xff) << 48 | (long)(buf[off++] & 0xff) << 40 | (long)(buf[off++] & 0xff) << 32 | (long)(buf[off++] & 0xff) << 24 | (long)(buf[off++] & 0xff) << 16 | (long)(buf[off++] & 0xff) << 8 | (long)(buf[off++] & 0xff);
    }

    public void xdr_hyper(long l)
    {
        buf[off++] = (byte)(int)(l >>> 56);
        buf[off++] = (byte)(int)(l >> 48 & 255L);
        buf[off++] = (byte)(int)(l >> 40 & 255L);
        buf[off++] = (byte)(int)(l >> 32 & 255L);
        buf[off++] = (byte)(int)(l >> 24 & 255L);
        buf[off++] = (byte)(int)(l >> 16 & 255L);
        buf[off++] = (byte)(int)(l >> 8 & 255L);
        buf[off++] = (byte)(int)(l & 255L);
    }

    public boolean xdr_bool()
    {
        return xdr_int() != 0;
    }

    public void xdr_bool(boolean flag)
    {
        xdr_int(flag ? 1 : 0);
    }

    public float xdr_float()
    {
        return Float.intBitsToFloat(xdr_int());
    }

    public void xdr_float(float f)
    {
        xdr_int(Float.floatToIntBits(f));
    }

    public String xdr_string()
    {
        int i = xdr_int();
        String s = new String(buf, off, i);
        xdr_skip(i);
        return s;
    }

    public void xdr_string(String s)
    {
        xdr_bytes(s.getBytes());
    }

    public byte[] xdr_bytes()
    {
        return xdr_raw(xdr_int());
    }

    public void xdr_bytes(byte abyte0[])
    {
        xdr_bytes(abyte0, 0, abyte0.length);
    }

    public void xdr_bytes(byte abyte0[], int i)
    {
        xdr_bytes(abyte0, 0, i);
    }

    public void xdr_bytes(byte abyte0[], int i, int j)
    {
        xdr_int(j);
        System.arraycopy(abyte0, i, buf, off, j);
        xdr_skip(j);
    }

    public void xdr_bytes(Xdr xdr)
    {
        xdr_bytes(xdr.xdr_buf(), xdr.xdr_offset());
    }

    public byte[] xdr_raw(int i)
    {
        if(i == 0)
        {
            return null;
        } else
        {
            byte abyte0[] = new byte[i];
            System.arraycopy(buf, off, abyte0, 0, i);
            xdr_skip(i);
            return abyte0;
        }
    }

    public void xdr_raw(byte abyte0[])
    {
        int i = abyte0.length;
        System.arraycopy(abyte0, 0, buf, off, i);
        xdr_skip(i);
    }

    private static int XDRUNIT = 4;
    private byte buf[];
    private int size;
    private int off;
    int xid;

}
