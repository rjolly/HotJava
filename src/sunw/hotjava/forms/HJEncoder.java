// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HJEncoder.java

package sunw.hotjava.forms;

import java.io.*;
import java.util.BitSet;

public class HJEncoder
{

    private HJEncoder()
    {
    }

    public static String encode(String s, String s1)
    {
        byte byte0 = 10;
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(s.length());
        ByteArrayOutputStream bytearrayoutputstream1 = new ByteArrayOutputStream(byte0);
        OutputStreamWriter outputstreamwriter;
        try
        {
            outputstreamwriter = new OutputStreamWriter(bytearrayoutputstream1, s1);
        }
        catch(UnsupportedEncodingException _ex)
        {
            outputstreamwriter = new OutputStreamWriter(bytearrayoutputstream1);
        }
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(dontNeedEncoding.get(c))
            {
                if(c == ' ')
                    c = '+';
                bytearrayoutputstream.write(c);
                continue;
            }
            try
            {
                outputstreamwriter.write(c);
                outputstreamwriter.flush();
            }
            catch(IOException _ex)
            {
                bytearrayoutputstream1.reset();
                continue;
            }
            byte abyte0[] = bytearrayoutputstream1.toByteArray();
            for(int j = 0; j < abyte0.length; j++)
                if(dontNeedEncoding.get(abyte0[j] & 0xff))
                {
                    bytearrayoutputstream.write(abyte0[j] & 0xff);
                } else
                {
                    bytearrayoutputstream.write(37);
                    char c1 = Character.forDigit(abyte0[j] >> 4 & 0xf, 16);
                    if(Character.isLetter(c1))
                        c1 -= ' ';
                    bytearrayoutputstream.write(c1);
                    c1 = Character.forDigit(abyte0[j] & 0xf, 16);
                    if(Character.isLetter(c1))
                        c1 -= ' ';
                    bytearrayoutputstream.write(c1);
                }

            bytearrayoutputstream1.reset();
        }

        return bytearrayoutputstream.toString();
    }

    static BitSet dontNeedEncoding;
    static final int caseDiff = 32;

    static 
    {
        dontNeedEncoding = new BitSet(256);
        for(int i = 97; i <= 122; i++)
            dontNeedEncoding.set(i);

        for(int j = 65; j <= 90; j++)
            dontNeedEncoding.set(j);

        for(int k = 48; k <= 57; k++)
            dontNeedEncoding.set(k);

        dontNeedEncoding.set(32);
        dontNeedEncoding.set(45);
        dontNeedEncoding.set(95);
        dontNeedEncoding.set(46);
        dontNeedEncoding.set(42);
    }
}
