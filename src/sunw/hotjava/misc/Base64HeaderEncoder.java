// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Base64HeaderEncoder.java

package sunw.hotjava.misc;

import java.io.*;
import sun.misc.BASE64Encoder;
import sun.misc.CharacterEncoder;

public class Base64HeaderEncoder extends CharacterEncoder
{

    public Base64HeaderEncoder(String s, String s1)
    {
        header = s;
        charset = s1;
    }

    protected void encodeBufferPrefix(OutputStream outputstream)
        throws IOException
    {
        super.encodeBufferPrefix(outputstream);
        super.pStream.print(header + ":");
    }

    protected void encodeLinePrefix(OutputStream outputstream, int i)
        throws IOException
    {
        super.pStream.print(" =?" + charset + "?b?");
    }

    protected void encodeLineSuffix(OutputStream outputstream)
        throws IOException
    {
        super.pStream.println("?=");
    }

    protected int bytesPerAtom()
    {
        return 4;
    }

    protected int bytesPerLine()
    {
        int i = 76 - charset.length() - " =??b??=".length();
        return i - i % 4;
    }

    protected void encodeAtom(OutputStream outputstream, byte abyte0[], int i, int j)
        throws IOException
    {
        if(j != 4)
        {
            throw new IOException("Base64HeaderEncoder: bad chunk size");
        } else
        {
            outputstream.write(abyte0[i]);
            outputstream.write(abyte0[i + 1]);
            outputstream.write(abyte0[i + 2]);
            outputstream.write(abyte0[i + 3]);
            return;
        }
    }

    protected int readFully(InputStream inputstream, byte abyte0[])
        throws IOException
    {
        for(int i = 0; i < abyte0.length; i++)
        {
            int j = inputstream.read();
            if(j == -1)
                return i;
            if(j == 13 || j == 10)
                i--;
            else
                abyte0[i] = (byte)j;
        }

        return abyte0.length;
    }

    public void encodeBuffer(InputStream inputstream, OutputStream outputstream)
        throws IOException
    {
        BASE64Encoder base64encoder = new BASE64Encoder();
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        base64encoder.encodeBuffer(inputstream, bytearrayoutputstream);
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(bytearrayoutputstream.toByteArray());
        super.encodeBuffer(bytearrayinputstream, outputstream);
    }

    public void encode(InputStream inputstream, OutputStream outputstream)
        throws IOException
    {
        BASE64Encoder base64encoder = new BASE64Encoder();
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        base64encoder.encode(inputstream, bytearrayoutputstream);
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(bytearrayoutputstream.toByteArray());
        super.encode(bytearrayinputstream, outputstream);
    }

    public static void main(String args[])
    {
        String s = "Usage: Base64HeaderEncoder <header> <charset> <content>";
        if(args.length != 3)
        {
            System.out.println(s);
            System.exit(1);
        }
        Base64HeaderEncoder base64headerencoder = new Base64HeaderEncoder(args[0], args[1]);
        try
        {
            base64headerencoder.encodeBuffer(args[2].getBytes(), System.out);
            return;
        }
        catch(IOException ioexception)
        {
            ioexception.printStackTrace();
        }
    }

    String header;
    String charset;
}
