// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   XFileWriter.java

package com.sun.xfile;

import java.io.IOException;
import java.io.OutputStreamWriter;

// Referenced classes of package com.sun.xfile:
//            XFileOutputStream, XFile

public class XFileWriter extends OutputStreamWriter
{

    public XFileWriter(String s)
        throws IOException
    {
        super(new XFileOutputStream(s));
    }

    public XFileWriter(String s, boolean flag)
        throws IOException
    {
        super(new XFileOutputStream(s, flag));
    }

    public XFileWriter(XFile xfile)
        throws IOException
    {
        super(new XFileOutputStream(xfile));
    }
}
