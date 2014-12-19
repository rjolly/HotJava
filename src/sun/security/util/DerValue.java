// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DerValue.java

package sun.security.util;

import java.io.*;

// Referenced classes of package sun.security.util:
//            DerInputBuffer, DerInputStream, DerOutputStream, ObjectIdentifier, 
//            BigInt

public class DerValue
{

    boolean isUniversal()
    {
        return (tag & 0xc0) == 0;
    }

    boolean isApplication()
    {
        return (tag & 0xc0) == 64;
    }

    public boolean isContextSpecific()
    {
        return (tag & 0xc0) == 128;
    }

    boolean isPrivate()
    {
        return (tag & 0xc0) == 192;
    }

    public boolean isConstructed()
    {
        return (tag & 0x20) == 32;
    }

    public DerValue(String s)
        throws IOException
    {
        tag = 19;
        length = s.length();
        byte abyte0[] = new byte[length];
        for(int i = 0; i < length; i++)
            abyte0[i] = (byte)s.charAt(i);

        buffer = new DerInputBuffer(abyte0);
        data = new DerInputStream(buffer);
        data.mark(0x7fffffff);
    }

    public DerValue(byte byte0, byte abyte0[])
    {
        tag = byte0;
        buffer = new DerInputBuffer(abyte0);
        length = abyte0.length;
        data = new DerInputStream(buffer);
        data.mark(0x7fffffff);
    }

    DerValue(DerInputBuffer derinputbuffer)
        throws IOException
    {
        tag = (byte)derinputbuffer.read();
        length = DerInputStream.getLength(derinputbuffer);
        buffer = derinputbuffer.dup();
        buffer.truncate(length);
        data = new DerInputStream(buffer);
        derinputbuffer.skip(length);
    }

    public DerValue(byte abyte0[])
        throws IOException
    {
        init(true, new ByteArrayInputStream(abyte0));
    }

    public DerValue(byte abyte0[], int i, int j)
        throws IOException
    {
        init(true, new ByteArrayInputStream(abyte0, i, j));
    }

    public DerValue(InputStream inputstream)
        throws IOException
    {
        init(false, inputstream);
    }

    private void init(boolean flag, InputStream inputstream)
        throws IOException
    {
        tag = (byte)inputstream.read();
        length = DerInputStream.getLength(inputstream);
        if(length == 0)
            return;
        if(flag && inputstream.available() != length)
        {
            throw new IOException("extra DER value data (constructor)");
        } else
        {
            byte abyte0[] = new byte[length];
            DataInputStream datainputstream = new DataInputStream(inputstream);
            datainputstream.readFully(abyte0);
            buffer = new DerInputBuffer(abyte0);
            data = new DerInputStream(buffer);
            return;
        }
    }

    /**
     * @deprecated Method emit is deprecated
     */

    public void emit(DerOutputStream deroutputstream)
        throws IOException
    {
        deroutputstream.write(tag);
        deroutputstream.putLength(length);
        if(length > 0)
        {
            byte abyte0[] = new byte[length];
            buffer.reset();
            if(buffer.read(abyte0) != length)
                throw new IOException("short DER value read (emit)");
            deroutputstream.write(abyte0);
        }
    }

    public ObjectIdentifier getOID()
        throws IOException
    {
        if(tag != 6)
            throw new IOException("DerValue.getOID, not an OID " + tag);
        else
            return new ObjectIdentifier(buffer);
    }

    public byte[] getOctetString()
        throws IOException
    {
        if(tag != 4)
            throw new IOException("DerValue.getOctetString, not an Octet String: " + tag);
        byte abyte0[] = new byte[length];
        if(buffer.read(abyte0) != length)
            throw new IOException("short read on DerValue buffer");
        else
            return abyte0;
    }

    public BigInt getInteger()
        throws IOException
    {
        if(tag != 2)
            throw new IOException("DerValue.getInteger, not an int " + tag);
        else
            return buffer.getUnsigned(data.available());
    }

    public byte[] getBitString()
        throws IOException
    {
        if(tag != 3)
            throw new IOException("DerValue.getBitString, not a bit string " + tag);
        else
            return buffer.getBitString();
    }

    public String getAsString()
        throws IOException
    {
        if(tag == 19)
            return getPrintableString();
        if(tag == 22)
            return getIA5String();
        if(tag == 20)
            return getT61String();
        else
            return null;
    }

    public String getPrintableString()
        throws IOException
    {
        if(tag != 19)
            throw new IOException("DerValue.getPrintableString, not a string " + tag);
        else
            return simpleGetString();
    }

    private String simpleGetString()
        throws IOException
    {
        StringBuffer stringbuffer = new StringBuffer(length);
        try
        {
            int i = length;
            data.reset();
            while(i-- > 0) 
                stringbuffer.append((char)data.getByte());
        }
        catch(IOException _ex)
        {
            return null;
        }
        return new String(stringbuffer);
    }

    public String getT61String()
        throws IOException
    {
        if(tag != 20)
            throw new IOException("DerValue.getT61String, not T61 " + tag);
        else
            return simpleGetString();
    }

    public String getIA5String()
        throws IOException
    {
        if(tag != 22)
            throw new IOException("DerValue.getIA5String, not IA5 " + tag);
        else
            return simpleGetString();
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof DerValue)
            return equals((DerValue)obj);
        else
            return false;
    }

    public boolean equals(DerValue dervalue)
    {
        data.reset();
        dervalue.data.reset();
        if(this == dervalue)
            return true;
        if(tag != dervalue.tag)
            return false;
        else
            return buffer.equals(dervalue.buffer);
    }

    public String toString()
    {
        try
        {
            if(tag == 19)
                return "\"" + getPrintableString() + "\"";
            if(tag == 5)
                return "[DerValue, null]";
            if(tag == 6)
                return "OID." + getOID();
            else
                return "[DerValue, tag = " + tag + ", length = " + length + "]";
        }
        catch(IOException _ex)
        {
            throw new IllegalArgumentException("misformatted DER value");
        }
    }

    public byte[] toByteArray()
        throws IOException
    {
        DerOutputStream deroutputstream = new DerOutputStream();
        emit(deroutputstream);
        data.reset();
        return deroutputstream.toByteArray();
    }

    public DerInputStream toDerInputStream()
        throws IOException
    {
        if(tag == 48 || tag == 49)
            return new DerInputStream(buffer);
        else
            throw new IOException("toDerInputStream rejects tag type " + tag);
    }

    public byte tag;
    protected DerInputBuffer buffer;
    public DerInputStream data;
    private int length;
    public static final byte tag_Integer = 2;
    public static final byte tag_BitString = 3;
    public static final byte tag_OctetString = 4;
    public static final byte tag_Null = 5;
    public static final byte tag_ObjectId = 6;
    public static final byte tag_PrintableString = 19;
    public static final byte tag_T61String = 20;
    public static final byte tag_IA5String = 22;
    public static final byte tag_UtcTime = 23;
    public static final byte tag_Sequence = 48;
    public static final byte tag_SequenceOf = 48;
    public static final byte tag_Set = 49;
    public static final byte tag_SetOf = 49;
}
