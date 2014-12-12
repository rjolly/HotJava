// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   XFileAccessor.java

package com.sun.xfile;

import java.io.IOException;

// Referenced classes of package com.sun.xfile:
//            XFile

public interface XFileAccessor
{

    public abstract boolean open(XFile xfile, boolean flag, boolean flag1);

    public abstract XFile getXFile();

    public abstract boolean exists();

    public abstract boolean canWrite();

    public abstract boolean canRead();

    public abstract boolean isFile();

    public abstract boolean isDirectory();

    public abstract long lastModified();

    public abstract long length();

    public abstract boolean mkfile();

    public abstract boolean mkdir();

    public abstract boolean renameTo(XFile xfile);

    public abstract boolean delete();

    public abstract String[] list();

    public abstract int read(byte abyte0[], int i, int j, long l)
        throws IOException;

    public abstract void write(byte abyte0[], int i, int j, long l)
        throws IOException;

    public abstract void flush()
        throws IOException;

    public abstract void close()
        throws IOException;
}
