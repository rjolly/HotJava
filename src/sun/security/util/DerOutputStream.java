// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DerOutputStream.java

package sun.security.util;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

// Referenced classes of package sun.security.util:
//            BigInt, DerValue, ObjectIdentifier

public class DerOutputStream extends ByteArrayOutputStream
{

    public DerOutputStream(int i)
    {
        super(i);
    }

    public DerOutputStream()
    {
    }

    public void write(byte byte0, byte abyte0[])
        throws IOException
    {
        write(byte0);
        putLength(abyte0.length);
        write(abyte0, 0, abyte0.length);
    }

    public void write(byte byte0, DerOutputStream deroutputstream)
        throws IOException
    {
        write(byte0);
        putLength(((ByteArrayOutputStream) (deroutputstream)).count);
        write(((ByteArrayOutputStream) (deroutputstream)).buf, 0, ((ByteArrayOutputStream) (deroutputstream)).count);
    }

    public void putDerValue(DerValue dervalue)
        throws IOException
    {
        dervalue.emit(this);
    }

    public void putInteger(BigInt bigint)
        throws IOException
    {
        byte abyte0[] = bigint.toByteArray();
        write(2);
        if((abyte0[0] & 0x80) != 0)
        {
            putLength(abyte0.length + 1);
            write(0);
        } else
        {
            putLength(abyte0.length);
        }
        write(abyte0, 0, abyte0.length);
    }

    public void putBitString(byte abyte0[])
        throws IOException
    {
        write(3);
        putLength(abyte0.length + 1);
        write(0);
        write(abyte0);
    }

    public void putOctetString(byte abyte0[])
        throws IOException
    {
        write((byte)4, abyte0);
    }

    public void putNull()
        throws IOException
    {
        write(5);
        putLength(0);
    }

    public void putOID(ObjectIdentifier objectidentifier)
        throws IOException
    {
        objectidentifier.emit(this);
    }

    public void putSequence(DerValue adervalue[])
        throws IOException
    {
        DerOutputStream deroutputstream = new DerOutputStream();
        for(int i = 0; i < adervalue.length; i++)
            adervalue[i].emit(deroutputstream);

        write((byte)48, deroutputstream);
    }

    public void putSet(DerValue adervalue[])
        throws IOException
    {
        DerOutputStream deroutputstream = new DerOutputStream();
        for(int i = 0; i < adervalue.length; i++)
            adervalue[i].emit(deroutputstream);

        write((byte)49, deroutputstream);
    }

    public void putPrintableString(String s)
        throws IOException
    {
        write(19);
        putLength(s.length());
        for(int i = 0; i < s.length(); i++)
            write((byte)s.charAt(i));

    }

    public void putIA5String(String s)
        throws IOException
    {
        write(22);
        putLength(s.length());
        for(int i = 0; i < s.length(); i++)
            write((byte)s.charAt(i));

    }

    public void putUTCTime(Date date)
        throws IOException
    {
        TimeZone timezone = TimeZone.getTimeZone("GMT");
        String s = "yyMMddHHmmss'Z'";
        SimpleDateFormat simpledateformat = new SimpleDateFormat(s);
        simpledateformat.setTimeZone(timezone);
        byte abyte0[] = simpledateformat.format(date).getBytes();
        write(23);
        putLength(abyte0.length);
        write(abyte0);
    }

    void putLength(int i)
        throws IOException
    {
        if(i < 128)
        {
            write((byte)i);
            return;
        }
        if(i < 256)
        {
            write(-127);
            write((byte)i);
            return;
        }
        if(i < 0x10000)
        {
            write(-126);
            write((byte)(i >> 8));
            write((byte)i);
            return;
        }
        if(i < 0x1000000)
        {
            write(-125);
            write((byte)(i >> 16));
            write((byte)(i >> 8));
            write((byte)i);
            return;
        } else
        {
            write(-124);
            write((byte)(i >> 24));
            write((byte)(i >> 16));
            write((byte)(i >> 8));
            write((byte)i);
            return;
        }
    }
}
