// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   QPEncoder.java

package sunw.hotjava.misc;

import java.io.*;
import sun.misc.CharacterEncoder;

public class QPEncoder extends CharacterEncoder
{

    public QPEncoder(boolean flag, boolean flag1)
    {
        encodeSpaces = false;
        terminateLineWithCrLf = true;
        encodeSpaces = flag;
        terminateLineWithCrLf = flag1;
    }

    protected int bytesPerAtom()
    {
        return 3;
    }

    protected int bytesPerLine()
    {
        return 76;
    }

    protected void encodeAtom(OutputStream outputstream, byte abyte0[], int i, int j)
    {
    }

    public String encode(byte abyte0[])
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
        try
        {
            encodeBuffer(bytearrayinputstream, bytearrayoutputstream);
            bytearrayoutputstream.flush();
        }
        catch(Exception _ex)
        {
            throw new Error("CharacterEncoder::encodeBuffer internal error");
        }
        return bytearrayoutputstream.toString();
    }

    public void encodeBuffer(InputStream inputstream, OutputStream outputstream)
        throws IOException
    {
        BufferedInputStream bufferedinputstream = new BufferedInputStream(inputstream, 76);
        int j = 0;
        int i;
        while((i = bufferedinputstream.read()) != -1) 
            if(i == 10 || i == 13)
            {
                if(i == 13)
                {
                    bufferedinputstream.mark(1);
                    if(bufferedinputstream.read() != 10)
                        bufferedinputstream.reset();
                }
                if(terminateLineWithCrLf)
                    outputstream.write(13);
                outputstream.write(10);
                j = 0;
            } else
            {
                bufferedinputstream.mark(1);
                int k;
                if(i < 32 || i == 127 || i == 63 || (i & 0x80) != 0 || i == 61 || i == 32 && ((k = bufferedinputstream.read()) == 13 || k == 10))
                {
                    if((j += 3) > 75)
                    {
                        outputstream.write(61);
                        if(terminateLineWithCrLf)
                            outputstream.write(13);
                        outputstream.write(10);
                        j = 3;
                    }
                    outputstream.write(61);
                    outputstream.write(hex[i >> 4]);
                    outputstream.write(hex[i & 0xf]);
                } else
                {
                    if(++j > 75)
                    {
                        outputstream.write(61);
                        if(terminateLineWithCrLf)
                            outputstream.write(13);
                        outputstream.write(10);
                        j = 1;
                    }
                    if(i == 32 && encodeSpaces)
                        outputstream.write(95);
                    else
                        outputstream.write(i);
                }
                bufferedinputstream.reset();
            }
    }

    boolean encodeSpaces;
    boolean terminateLineWithCrLf;
    private static final int MAX = 75;
    private static final char hex[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'A', 'B', 'C', 'D', 'E', 'F'
    };

}
