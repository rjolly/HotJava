// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   QPHeaderEncoder.java

package sunw.hotjava.misc;

import java.io.*;
import sun.misc.CharacterEncoder;

// Referenced classes of package sunw.hotjava.misc:
//            QPEncoder

public class QPHeaderEncoder extends CharacterEncoder
{

    public QPHeaderEncoder(String s, String s1)
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
        super.pStream.print(" =?" + charset + "?q?");
    }

    protected void encodeLineSuffix(OutputStream outputstream)
        throws IOException
    {
        super.pStream.println("?=");
    }

    protected int bytesPerAtom()
    {
        return 3;
    }

    protected int bytesPerLine()
    {
        int i = 76 - charset.length() - " =??q??=".length();
        return i;
    }

    protected void encodeAtom(OutputStream outputstream, byte abyte0[], int i, int j)
        throws IOException
    {
        for(int k = 0; k < j; k++)
            outputstream.write(abyte0[i + k]);

    }

    protected int readFully(InputStream inputstream, byte abyte0[])
        throws IOException
    {
        for(int i = 0; i < abyte0.length; i++)
        {
            inputstream.mark(1);
            int j = inputstream.read();
            if(j == -1)
                return i;
            if(j == 61 && i > abyte0.length - 3)
            {
                inputstream.reset();
                return i;
            }
            abyte0[i] = (byte)j;
        }

        return abyte0.length;
    }

    private void removeTerminations(InputStream inputstream, OutputStream outputstream)
        throws IOException
    {
        int i;
        while((i = inputstream.read()) >= 0) 
        {
            if(i == 13 || i == 10)
                continue;
            if(i == 61)
            {
                int j = inputstream.read();
                if(j == -1)
                    break;
                if(j != 13 && j != 10)
                {
                    outputstream.write(i);
                    outputstream.write(j);
                }
            } else
            {
                outputstream.write(i);
            }
        }
    }

    public void encodeBuffer(InputStream inputstream, OutputStream outputstream)
        throws IOException
    {
        QPEncoder qpencoder = new QPEncoder(true, true);
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        qpencoder.encodeBuffer(inputstream, bytearrayoutputstream);
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(bytearrayoutputstream.toByteArray());
        ByteArrayOutputStream bytearrayoutputstream1 = new ByteArrayOutputStream();
        removeTerminations(bytearrayinputstream, bytearrayoutputstream1);
        byte abyte0[] = bytearrayoutputstream1.toByteArray();
        int i = abyte0.length;
        ByteArrayInputStream bytearrayinputstream1 = new ByteArrayInputStream(abyte0);
        BufferedInputStream bufferedinputstream = new BufferedInputStream(bytearrayinputstream1);
        boolean flag = false;
        byte abyte1[] = new byte[bytesPerLine()];
        encodeBufferPrefix(outputstream);
        int j;
        for(int k = 0; k < i; k += j)
        {
            j = readFully(bufferedinputstream, abyte1);
            encodeLinePrefix(outputstream, j);
            for(int l = 0; l < j; l += bytesPerAtom())
                if(l + bytesPerAtom() <= j)
                    encodeAtom(outputstream, abyte1, l, bytesPerAtom());
                else
                    encodeAtom(outputstream, abyte1, l, j - l);

            encodeLineSuffix(outputstream);
        }

        encodeBufferSuffix(outputstream);
    }

    public void encode(InputStream inputstream, OutputStream outputstream)
        throws IOException
    {
        QPEncoder qpencoder = new QPEncoder(true, true);
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        qpencoder.encodeBuffer(inputstream, bytearrayoutputstream);
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(bytearrayoutputstream.toByteArray());
        BufferedInputStream bufferedinputstream = new BufferedInputStream(bytearrayinputstream);
        super.encode(bufferedinputstream, outputstream);
    }

    public static void main(String args[])
    {
        String s = "Usage: QPHeaderEncoder <header> <charset> <content>";
        if(args.length != 3)
        {
            System.out.println(s);
            System.exit(1);
        }
        QPHeaderEncoder qpheaderencoder = new QPHeaderEncoder(args[0], args[1]);
        try
        {
            qpheaderencoder.encodeBuffer(args[2].getBytes(), System.out);
            return;
        }
        catch(Exception exception)
        {
            System.out.println("Error encoding header:");
            exception.printStackTrace();
            return;
        }
    }

    String header;
    String charset;
}
