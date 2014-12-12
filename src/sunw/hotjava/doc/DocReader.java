// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocReader.java

package sunw.hotjava.doc;

import java.io.*;

class DocReader extends Reader
{

    DocReader(Reader reader1, CharArrayWriter chararraywriter)
    {
        reader = reader1;
        cw = chararraywriter;
    }

    public int read(char ac[], int i, int j)
        throws IOException
    {
        int k = reader.read(ac, i, j);
        if(k > 0)
            cw.write(ac, i, k);
        return k;
    }

    public void close()
        throws IOException
    {
        reader.close();
    }

    Reader reader;
    CharArrayWriter cw;
}
