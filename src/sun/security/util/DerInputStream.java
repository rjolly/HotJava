// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DerInputStream.java

package sun.security.util;

import java.io.*;
import java.util.Date;
import java.util.Vector;

// Referenced classes of package sun.security.util:
//            DerInputBuffer, DerValue, ObjectIdentifier, BigInt

public class DerInputStream
{

    public DerInputStream(byte abyte0[])
    {
        buffer = new DerInputBuffer(abyte0);
        buffer.mark(0x7fffffff);
    }

    public DerInputStream(byte abyte0[], int i, int j)
    {
        buffer = new DerInputBuffer(abyte0, i, j);
        buffer.mark(0x7fffffff);
    }

    DerInputStream(DerInputBuffer derinputbuffer)
    {
        buffer = derinputbuffer;
        buffer.mark(0x7fffffff);
    }

    public DerInputStream subStream(int i, boolean flag)
        throws IOException
    {
        DerInputBuffer derinputbuffer = buffer.dup();
        derinputbuffer.truncate(i);
        if(flag)
            buffer.skip(i);
        return new DerInputStream(derinputbuffer);
    }

    public BigInt getInteger()
        throws IOException
    {
        if(buffer.read() != 2)
            throw new IOException("DER input, Integer tag error");
        else
            return buffer.getUnsigned(getLength(buffer));
    }

    public byte[] getBitString()
        throws IOException
    {
        if(buffer.read() != 3)
            throw new IOException("DER input not an bit string");
        int i = getLength(buffer);
        if(buffer.read() != 0)
            return null;
        byte abyte0[] = new byte[--i];
        if(buffer.read(abyte0) != i)
            throw new IOException("short read of DER bit string");
        else
            return abyte0;
    }

    public byte[] getOctetString()
        throws IOException
    {
        if(buffer.read() != 4)
            throw new IOException("DER input not an octet string");
        int i = getLength(buffer);
        byte abyte0[] = new byte[i];
        if(buffer.read(abyte0) != i)
            throw new IOException("short read of DER octet string");
        else
            return abyte0;
    }

    public void getNull()
        throws IOException
    {
        if(buffer.read() != 5 || buffer.read() != 0)
            throw new IOException("getNull, bad data");
        else
            return;
    }

    public ObjectIdentifier getOID()
        throws IOException
    {
        return new ObjectIdentifier(this);
    }

    public DerValue[] getSequence(int i)
        throws IOException
    {
        if(buffer.read() != 48)
            throw new IOException("Sequence tag error");
        else
            return readVector(i);
    }

    public DerValue[] getSet(int i)
        throws IOException
    {
        if(buffer.read() != 49)
            throw new IOException("Set tag error");
        else
            return readVector(i);
    }

    protected DerValue[] readVector(int i)
        throws IOException
    {
        int j = getLength(buffer);
        if(j == 0)
            return null;
        DerInputStream derinputstream;
        if(buffer.available() == j)
            derinputstream = this;
        else
            derinputstream = subStream(j, true);
        Vector vector = new Vector(i, 5);
        do
        {
            DerValue dervalue = new DerValue(derinputstream.buffer);
            vector.addElement(dervalue);
        } while(derinputstream.available() > 0);
        if(derinputstream.available() != 0)
            throw new IOException("extra data at end of vector");
        int l = vector.size();
        DerValue adervalue[] = new DerValue[l];
        for(int k = 0; k < l; k++)
            adervalue[k] = (DerValue)vector.elementAt(k);

        return adervalue;
    }

    public DerValue getDerValue()
        throws IOException
    {
        return new DerValue(buffer);
    }

    public Date getUTCTime()
        throws IOException
    {
        if(buffer.read() != 23)
            throw new IOException("DER input, UTCtime tag invalid ");
        if(buffer.available() < 12)
            throw new IOException("DER input, UTCtime short input");
        int i = getLength(buffer);
        if(i < 11 || i > 17)
            throw new IOException("DER getUTCTime length error");
        int j = 10 * Character.digit((char)buffer.read(), 10);
        j += Character.digit((char)buffer.read(), 10);
        if(j < 80)
            j += 100;
        int k = 10 * Character.digit((char)buffer.read(), 10);
        k += Character.digit((char)buffer.read(), 10);
        k--;
        int l = 10 * Character.digit((char)buffer.read(), 10);
        l += Character.digit((char)buffer.read(), 10);
        int i1 = 10 * Character.digit((char)buffer.read(), 10);
        i1 += Character.digit((char)buffer.read(), 10);
        int j1 = 10 * Character.digit((char)buffer.read(), 10);
        j1 += Character.digit((char)buffer.read(), 10);
        int k1;
        if((i -= 10) == 3 || i == 7)
        {
            k1 = 10 * Character.digit((char)buffer.read(), 10);
            k1 += Character.digit((char)buffer.read(), 10);
            i -= 2;
        } else
        {
            k1 = 0;
        }
        if(k < 0 || l <= 0 || k > 11 || l > 31 || i1 >= 24 || j1 >= 60 || k1 >= 60)
            throw new IOException("Parse UTC time, invalid format");
        long l1 = Date.UTC(j, k, l, i1, j1, k1);
        if(i != 1 && i != 5)
            throw new IOException("Parse UTC time, invalid offset");
        switch(buffer.read())
        {
        case 43: // '+'
            int i2 = 10 * Character.digit((char)buffer.read(), 10);
            i2 += Character.digit((char)buffer.read(), 10);
            int k2 = 10 * Character.digit((char)buffer.read(), 10);
            k2 += Character.digit((char)buffer.read(), 10);
            if(i2 >= 24 || k2 >= 60)
                throw new IOException("Parse UTCtime, +hhmm");
            l1 += (i2 * 60 + k2) * 60 * 1000;
            break;

        case 90: // 'Z'
            break;

        case 45: // '-'
            int j2 = 10 * Character.digit((char)buffer.read(), 10);
            j2 += Character.digit((char)buffer.read(), 10);
            int l2 = 10 * Character.digit((char)buffer.read(), 10);
            l2 += Character.digit((char)buffer.read(), 10);
            if(j2 >= 24 || l2 >= 60)
                throw new IOException("Parse UTCtime, -hhmm");
            l1 -= (j2 * 60 + l2) * 60 * 1000;
            break;

        default:
            throw new IOException("Parse UTCtime, garbage offset");
        }
        return new Date(l1);
    }

    int getByte()
        throws IOException
    {
        return 0xff & buffer.read();
    }

    int peekByte()
        throws IOException
    {
        return buffer.peek();
    }

    int getLength()
        throws IOException
    {
        return getLength(((InputStream) (buffer)));
    }

    static int getLength(InputStream inputstream)
        throws IOException
    {
        int j = inputstream.read();
        int i;
        if((j & 0x80) == 0)
        {
            i = j;
        } else
        {
            j &= 0x7f;
            if(j <= 0 || j > 4)
                throw new IOException("DerInput.getLength(): lengthTag=" + j + ", " + (j != 0 ? "too big." : "Indefinite length encoding not supported or incorrect DER encoding."));
            i = 0;
            for(; j > 0; j--)
            {
                i <<= 8;
                i += 0xff & inputstream.read();
            }

        }
        return i;
    }

    public void mark(int i)
    {
        buffer.mark(i);
    }

    public void reset()
    {
        buffer.reset();
    }

    public int available()
    {
        return buffer.available();
    }

    DerInputBuffer buffer;
}
