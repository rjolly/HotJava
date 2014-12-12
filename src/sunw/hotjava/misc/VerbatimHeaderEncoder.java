// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   VerbatimHeaderEncoder.java

package sunw.hotjava.misc;

import java.io.*;
import sun.misc.CharacterEncoder;

public class VerbatimHeaderEncoder extends CharacterEncoder
{

    public VerbatimHeaderEncoder(String s)
    {
        header = s;
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
        super.pStream.print(" ");
    }

    protected int bytesPerAtom()
    {
        return 1;
    }

    protected int bytesPerLine()
    {
        return 75;
    }

    protected void encodeAtom(OutputStream outputstream, byte abyte0[], int i, int j)
        throws IOException
    {
        if(j != 1)
        {
            throw new IOException("VerbatimHeaderEncoder: bad chunk size");
        } else
        {
            outputstream.write(abyte0[i]);
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

    public static void main(String args[])
    {
        String s = "Usage: VerbatimHeaderEncoder <header> <content>";
        if(args.length != 2)
        {
            System.out.println(s);
            System.exit(1);
        }
        VerbatimHeaderEncoder verbatimheaderencoder = new VerbatimHeaderEncoder(args[0]);
        try
        {
            verbatimheaderencoder.encodeBuffer(args[1].getBytes(), System.out);
            return;
        }
        catch(IOException ioexception)
        {
            System.out.println("Error encoding header:");
            ioexception.printStackTrace();
            return;
        }
    }

    String header;
}
