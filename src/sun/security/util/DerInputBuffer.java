// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DerInputBuffer.java

package sun.security.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

// Referenced classes of package sun.security.util:
//            BigInt

class DerInputBuffer extends ByteArrayInputStream
    implements Cloneable
{

    DerInputBuffer(byte abyte0[])
    {
        super(abyte0);
    }

    DerInputBuffer(byte abyte0[], int i, int j)
    {
        super(abyte0);
    }

    DerInputBuffer dup()
    {
        try
        {
            DerInputBuffer derinputbuffer = (DerInputBuffer)clone();
            derinputbuffer.mark(0x7fffffff);
            return derinputbuffer;
        }
        catch(CloneNotSupportedException clonenotsupportedexception)
        {
            throw new IllegalArgumentException(clonenotsupportedexception.toString());
        }
    }

    int peek()
        throws IOException
    {
        if(super.pos >= super.count)
            throw new IOException("out of data");
        else
            return super.buf[super.pos];
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof DerInputBuffer)
            return equals((DerInputBuffer)obj);
        else
            return false;
    }

    boolean equals(DerInputBuffer derinputbuffer)
    {
        int i = available();
        if(this == derinputbuffer)
            return true;
        if(derinputbuffer.available() != available())
            return false;
        for(int j = 0; j < i; j++)
            if(super.buf[super.pos + j] != derinputbuffer.buf[derinputbuffer.pos + j])
                return false;

        return true;
    }

    void truncate(int i)
        throws IOException
    {
        if(i > available())
        {
            throw new IOException("insufficient data");
        } else
        {
            super.count = super.pos + i;
            return;
        }
    }

    BigInt getUnsigned(int i)
        throws IOException
    {
        if(i > available())
            throw new IOException("short read, getInteger");
        if(super.buf[super.pos] == 0)
        {
            i--;
            skip(1L);
        }
        byte abyte0[] = new byte[i];
        System.arraycopy(super.buf, super.pos, abyte0, 0, i);
        skip(i);
        return new BigInt(abyte0);
    }

    byte[] getBitString()
    {
        if(super.pos >= super.count || super.buf[super.pos] != 0)
        {
            return null;
        } else
        {
            int i = available();
            byte abyte0[] = new byte[i - 1];
            System.arraycopy(super.buf, super.pos + 1, abyte0, 0, i - 1);
            super.pos = super.count;
            return abyte0;
        }
    }
}
