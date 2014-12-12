// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   XFileAccessor.java

package com.sun.file;

import com.sun.xfile.XFile;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class XFileAccessor
    implements com.sun.xfile.XFileAccessor
{

    public boolean open(XFile xfile, boolean flag, boolean flag1)
    {
        xf = xfile;
        readOnly = flag1;
        file = new File(unEscape(xfile.getPath().replace('/', sep)));
        return file.exists();
    }

    private String unEscape(String s)
    {
        String s1 = "0123456789abcdef";
        String s2 = "";
        int j = s.length();
        int i;
        for(int k = 0; k < j; k = i + 1)
        {
            i = s.indexOf("%", k);
            if(i < 0)
                i = j;
            s2 = s2 + s.substring(k, i);
            if(i == j)
                break;
            if(i < j - 2)
            {
                int l = s1.indexOf(s.toLowerCase().charAt(i + 1));
                int i1 = s1.indexOf(s.toLowerCase().charAt(i + 2));
                if(l > 0 && i1 > 0)
                {
                    s2 = s2 + new String(new byte[] {
                        (byte)(l << 4 | i1)
                    });
                    i += 2;
                    continue;
                }
            }
            s2 = s2 + "%";
        }

        return s2;
    }

    public XFile getXFile()
    {
        return xf;
    }

    public boolean exists()
    {
        return file.exists();
    }

    public boolean canWrite()
    {
        return file.canWrite();
    }

    public boolean canRead()
    {
        return file.canRead();
    }

    public boolean isFile()
    {
        return file.isFile();
    }

    public boolean isDirectory()
    {
        return file.isDirectory();
    }

    public long lastModified()
    {
        return file.lastModified();
    }

    public long length()
    {
        return file.length();
    }

    public boolean mkfile()
    {
        try
        {
            FileOutputStream fileoutputstream = new FileOutputStream(file);
            fileoutputstream.getFD().sync();
            fileoutputstream.close();
            return true;
        }
        catch(IOException _ex)
        {
            return false;
        }
    }

    public boolean mkdir()
    {
        return file.mkdir();
    }

    public boolean renameTo(XFile xfile)
    {
        return file.renameTo(new File(xfile.getPath()));
    }

    public String[] list()
    {
        return file.list();
    }

    public boolean delete()
    {
        return file.delete();
    }

    public int read(byte abyte0[], int i, int j, long l)
        throws IOException
    {
        if(raf == null)
            raf = new RandomAccessFile(file, readOnly ? "r" : "rw");
        if(l != fp)
        {
            fp = l;
            raf.seek(l);
        }
        int k = raf.read(abyte0, i, j);
        if(k > 0)
            fp += k;
        return k;
    }

    public void write(byte abyte0[], int i, int j, long l)
        throws IOException
    {
        if(raf == null)
            raf = new RandomAccessFile(file, readOnly ? "r" : "rw");
        if(l != fp)
        {
            fp = l;
            raf.seek(l);
        }
        raf.write(abyte0, i, j);
        fp += j;
    }

    public void flush()
        throws IOException
    {
    }

    public void close()
        throws IOException
    {
        if(raf != null)
            raf.close();
    }

    public String toString()
    {
        return file.toString();
    }

    public XFileAccessor()
    {
        sep = System.getProperty("file.separator").charAt(0);
    }

    private XFile xf;
    private File file;
    private RandomAccessFile raf;
    private boolean readOnly;
    private long fp;
    char sep;
}
