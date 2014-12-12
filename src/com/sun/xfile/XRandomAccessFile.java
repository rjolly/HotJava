// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   XRandomAccessFile.java

package com.sun.xfile;

import java.io.*;

// Referenced classes of package com.sun.xfile:
//            XFile, XFileAccessor

public class XRandomAccessFile
    implements DataOutput, DataInput
{

    public XRandomAccessFile(XFile xfile, String s)
        throws IOException
    {
        if(!s.equals("r") && !s.equals("rw"))
            throw new IllegalArgumentException("mode must be r or rw");
        readOnly = s.equals("r");
        xfa = xfile.newAccessor();
        xfa.open(xfile, false, readOnly);
        if(xfa.exists())
        {
            if(readOnly && !xfa.canRead())
                throw new IOException("no read permission");
            if(!readOnly && !xfa.canWrite())
                throw new IOException("no write permission");
        } else
        {
            if(readOnly)
                throw new IOException("no such file or directory");
            if(!xfa.mkfile())
                throw new IOException("no write permission");
        }
    }

    public XRandomAccessFile(String s, String s1)
        throws IOException
    {
        this(new XFile(s), s1);
    }

    private int XFAread(byte abyte0[], int i, int j)
        throws IOException
    {
        if(abyte0 == null)
            throw new NullPointerException();
        if(j == 0)
            return 0;
        if(i < 0 || j < 0 || i >= abyte0.length || i + j > abyte0.length)
            throw new IllegalArgumentException("Invalid argument");
        int k = xfa.read(abyte0, i, j, fp);
        if(k >= 0)
            fp += k;
        return k;
    }

    public int read()
        throws IOException
    {
        byte abyte0[] = new byte[1];
        if(XFAread(abyte0, 0, 1) != 1)
            return -1;
        else
            return abyte0[0] & 0xff;
    }

    public int read(byte abyte0[], int i, int j)
        throws IOException
    {
        return XFAread(abyte0, i, j);
    }

    public int read(byte abyte0[])
        throws IOException
    {
        return XFAread(abyte0, 0, abyte0.length);
    }

    public final void readFully(byte abyte0[])
        throws IOException
    {
        readFully(abyte0, 0, abyte0.length);
    }

    public final void readFully(byte abyte0[], int i, int j)
        throws IOException
    {
        if(XFAread(abyte0, i, j) < j)
            throw new EOFException();
        else
            return;
    }

    public int skipBytes(int i)
        throws IOException
    {
        if(fp + (long)i > xfa.length())
        {
            throw new EOFException();
        } else
        {
            seek(fp + (long)i);
            return i;
        }
    }

    private void XFAwrite(byte abyte0[], int i, int j)
        throws IOException
    {
        if(abyte0 == null)
            throw new NullPointerException();
        if(readOnly)
            throw new IOException("Read only file");
        if(i < 0 || j < 0)
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

    private void writeBytes(byte abyte0[], int i, int j)
        throws IOException
    {
        XFAwrite(abyte0, i, j);
    }

    public void write(byte abyte0[])
        throws IOException
    {
        writeBytes(abyte0, 0, abyte0.length);
    }

    public void write(byte abyte0[], int i, int j)
        throws IOException
    {
        writeBytes(abyte0, i, j);
    }

    public long getFilePointer()
        throws IOException
    {
        return fp;
    }

    public void seek(long l)
        throws IOException
    {
        if(l < 0L || readOnly && l >= xfa.length())
        {
            throw new IOException("illegal seek" + l);
        } else
        {
            fp = l;
            return;
        }
    }

    public long length()
        throws IOException
    {
        return xfa.length();
    }

    public void flush()
        throws IOException
    {
        if(readOnly)
        {
            throw new IOException("Read only file");
        } else
        {
            xfa.flush();
            return;
        }
    }

    public void close()
        throws IOException
    {
        xfa.close();
    }

    public final boolean readBoolean()
        throws IOException
    {
        int i = read();
        if(i < 0)
            throw new EOFException();
        return i != 0;
    }

    public final byte readByte()
        throws IOException
    {
        int i = read();
        if(i < 0)
            throw new EOFException();
        else
            return (byte)i;
    }

    public final int readUnsignedByte()
        throws IOException
    {
        int i = read();
        if(i < 0)
            throw new EOFException();
        else
            return i;
    }

    public final short readShort()
        throws IOException
    {
        int i = read();
        int j = read();
        if((i | j) < 0)
            throw new EOFException();
        else
            return (short)((i << 8) + j);
    }

    public final int readUnsignedShort()
        throws IOException
    {
        int i = read();
        int j = read();
        if((i | j) < 0)
            throw new EOFException();
        else
            return (i << 8) + j;
    }

    public final char readChar()
        throws IOException
    {
        int i = read();
        int j = read();
        if((i | j) < 0)
            throw new EOFException();
        else
            return (char)((i << 8) + j);
    }

    public final int readInt()
        throws IOException
    {
        int i = read();
        int j = read();
        int k = read();
        int l = read();
        if((i | j | k | l) < 0)
            throw new EOFException();
        else
            return (i << 24) + (j << 16) + (k << 8) + l;
    }

    public final long readLong()
        throws IOException
    {
        return ((long)readInt() << 32) + ((long)readInt() & 0xffffffffL);
    }

    public final float readFloat()
        throws IOException
    {
        return Float.intBitsToFloat(readInt());
    }

    public final double readDouble()
        throws IOException
    {
        return Double.longBitsToDouble(readLong());
    }

    public final String readLine()
        throws IOException
    {
        StringBuffer stringbuffer = new StringBuffer();
        int i;
        while((i = read()) != -1 && i != 10) 
            stringbuffer.append((char)i);
        if(i == -1 && stringbuffer.length() == 0)
            return null;
        else
            return stringbuffer.toString();
    }

    public final String readUTF()
        throws IOException
    {
        return DataInputStream.readUTF(this);
    }

    public final void writeBoolean(boolean flag)
        throws IOException
    {
        write(flag ? 1 : 0);
    }

    public final void writeByte(int i)
        throws IOException
    {
        write(i);
    }

    public final void writeShort(int i)
        throws IOException
    {
        write(i >>> 8 & 0xff);
        write(i & 0xff);
    }

    public final void writeChar(int i)
        throws IOException
    {
        write(i >>> 8 & 0xff);
        write(i & 0xff);
    }

    public final void writeInt(int i)
        throws IOException
    {
        write(i >>> 24 & 0xff);
        write(i >>> 16 & 0xff);
        write(i >>> 8 & 0xff);
        write(i & 0xff);
    }

    public final void writeLong(long l)
        throws IOException
    {
        write((int)(l >>> 56) & 0xff);
        write((int)(l >>> 48) & 0xff);
        write((int)(l >>> 40) & 0xff);
        write((int)(l >>> 32) & 0xff);
        write((int)(l >>> 24) & 0xff);
        write((int)(l >>> 16) & 0xff);
        write((int)(l >>> 8) & 0xff);
        write((int)l & 0xff);
    }

    public final void writeFloat(float f)
        throws IOException
    {
        writeInt(Float.floatToIntBits(f));
    }

    public final void writeDouble(double d)
        throws IOException
    {
        writeLong(Double.doubleToLongBits(d));
    }

    public final void writeBytes(String s)
        throws IOException
    {
        int i = s.length();
        for(int j = 0; j < i; j++)
            write((byte)s.charAt(j));

    }

    public final void writeChars(String s)
        throws IOException
    {
        int i = s.length();
        for(int j = 0; j < i; j++)
        {
            char c = s.charAt(j);
            write(c >>> 8 & 0xff);
            write(c & 0xff);
        }

    }

    public final void writeUTF(String s)
        throws IOException
    {
        int i = s.length();
        int j = 0;
        for(int k = 0; k < i; k++)
        {
            char c = s.charAt(k);
            if(c >= '\001' && c <= '\177')
                j++;
            else
            if(c > '\u07FF')
                j += 3;
            else
                j += 2;
        }

        if(j > 65535)
            throw new UTFDataFormatException();
        write(j >>> 8 & 0xff);
        write(j & 0xff);
        for(int l = 0; l < i; l++)
        {
            char c1 = s.charAt(l);
            if(c1 >= '\001' && c1 <= '\177')
                write(c1);
            else
            if(c1 > '\u07FF')
            {
                write(0xe0 | c1 >> 12 & 0xf);
                write(0x80 | c1 >> 6 & 0x3f);
                write(0x80 | c1 & 0x3f);
            } else
            {
                write(0xc0 | c1 >> 6 & 0x1f);
                write(0x80 | c1 & 0x3f);
            }
        }

    }

    private long fp;
    private boolean readOnly;
    private XFileAccessor xfa;
}
