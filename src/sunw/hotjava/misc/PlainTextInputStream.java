// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PlainTextInputStream.java

package sunw.hotjava.misc;

import java.io.FilterInputStream;
import java.io.InputStream;

public class PlainTextInputStream extends FilterInputStream
{

    PlainTextInputStream(InputStream inputstream)
    {
        super(inputstream);
    }
}
