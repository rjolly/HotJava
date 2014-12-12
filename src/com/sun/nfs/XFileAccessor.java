// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   XFileAccessor.java

package com.sun.nfs;

import com.sun.xfile.XFile;
import java.io.IOException;

// Referenced classes of package com.sun.nfs:
//            Nfs, NfsConnect

public class XFileAccessor
    implements com.sun.xfile.XFileAccessor
{

    public boolean open(XFile xfile, boolean flag, boolean flag1)
    {
        xf = xfile;
        try
        {
            nfs = NfsConnect.connect(xfile.getAbsolutePath());
            return true;
        }
        catch(IOException _ex)
        {
            return false;
        }
    }

    public XFile getXFile()
    {
        return xf;
    }

    protected Nfs getParent(XFile xfile)
        throws IOException
    {
        XFile xfile1 = new XFile(xfile.getParent());
        XFileAccessor xfileaccessor = new XFileAccessor();
        xfileaccessor.open(xfile1, serial, readOnly);
        return xfileaccessor.getNfs();
    }

    protected Nfs getNfs()
    {
        return nfs;
    }

    public boolean exists()
    {
        try
        {
            return nfs.exists();
        }
        catch(Exception _ex)
        {
            return false;
        }
    }

    public boolean canWrite()
    {
        try
        {
            return nfs.canWrite();
        }
        catch(IOException _ex)
        {
            return false;
        }
    }

    public boolean canRead()
    {
        try
        {
            return nfs.canRead();
        }
        catch(IOException _ex)
        {
            return false;
        }
    }

    public boolean isFile()
    {
        try
        {
            return nfs.isFile();
        }
        catch(IOException _ex)
        {
            return false;
        }
    }

    public boolean isDirectory()
    {
        try
        {
            return nfs.isDirectory();
        }
        catch(IOException _ex)
        {
            return false;
        }
    }

    public long lastModified()
    {
        try
        {
            return nfs.mtime();
        }
        catch(IOException _ex)
        {
            return 0L;
        }
    }

    public long length()
    {
        try
        {
            return nfs.length();
        }
        catch(IOException _ex)
        {
            return 0L;
        }
    }

    public boolean mkfile()
    {
        try
        {
            nfs = getParent(xf).create(xf.getName(), 438L);
            return true;
        }
        catch(Exception _ex)
        {
            return false;
        }
    }

    public boolean mkdir()
    {
        try
        {
            nfs = getParent(xf).mkdir(xf.getName(), 511L);
            return true;
        }
        catch(Exception _ex)
        {
            return false;
        }
    }

    public boolean renameTo(XFile xfile)
    {
        try
        {
            Nfs nfs1 = getParent(xf);
            Nfs nfs2 = getParent(xfile);
            return nfs1.rename(nfs2, xf.getName(), xfile.getName());
        }
        catch(Exception _ex)
        {
            return false;
        }
    }

    public String[] list()
    {
        try
        {
            return nfs.readdir();
        }
        catch(IOException _ex)
        {
            return null;
        }
    }

    public boolean delete()
    {
        try
        {
            boolean flag;
            if(isFile())
                flag = getParent(xf).remove(xf.getName());
            else
                flag = getParent(xf).rmdir(xf.getName());
            if(flag)
            {
                nfs.invalidate();
                nfs = null;
            }
            return flag;
        }
        catch(Exception _ex)
        {
            return false;
        }
    }

    public int read(byte abyte0[], int i, int j, long l)
        throws IOException
    {
        int k = nfs.read(abyte0, i, j, l);
        return k;
    }

    public void write(byte abyte0[], int i, int j, long l)
        throws IOException
    {
        nfs.write(abyte0, i, j, l);
    }

    public void flush()
        throws IOException
    {
        nfs.flush();
    }

    public void close()
        throws IOException
    {
        nfs.close();
    }

    public String toString()
    {
        return nfs.toString();
    }

    public XFileAccessor()
    {
    }

    XFile xf;
    boolean serial;
    boolean readOnly;
    Nfs nfs;
}
