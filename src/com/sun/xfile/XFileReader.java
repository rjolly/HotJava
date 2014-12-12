// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   XFileReader.java

package com.sun.xfile;

import java.io.IOException;
import java.io.InputStreamReader;

// Referenced classes of package com.sun.xfile:
//            XFileInputStream, XFile

public class XFileReader extends InputStreamReader
{

    public XFileReader(String s)
        throws IOException
    {
        super(new XFileInputStream(s));
    }

    public XFileReader(XFile xfile)
        throws IOException
    {
        super(new XFileInputStream(xfile));
    }
}
