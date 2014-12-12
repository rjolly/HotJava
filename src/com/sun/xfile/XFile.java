// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   XFile.java

package com.sun.xfile;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.*;

// Referenced classes of package com.sun.xfile:
//            XFileAccessor, XFileExtensionAccessor, XFilenameFilter, XFurl

public class XFile
{

    public XFile(String s)
    {
        urlStr = s;
        if(s == null)
            throw new NullPointerException();
        try
        {
            url = new XFurl(s);
            xfa = loadAccessor(url);
            return;
        }
        catch(Exception _ex) { }
        if(s.startsWith(".:"))
            s = s.substring(2);
        nativeFile = new File(s);
        xfa = makeNative(nativeFile);
    }

    public XFile(XFile xfile, String s)
    {
        if(s == null)
            throw new NullPointerException();
        try
        {
            url = new XFurl(s);
            xfa = loadAccessor(url);
        }
        catch(Exception _ex)
        {
            if(s.startsWith(".:"))
            {
                s = s.substring(2);
                xfile = null;
            }
            if(xfile == null)
            {
                nativeFile = new File(s);
                xfa = makeNative(nativeFile);
            } else
            if(xfile.nativeFile != null)
            {
                if((new File(s)).isAbsolute())
                    nativeFile = new File(s);
                else
                    nativeFile = new File(xfile.nativeFile, s);
                xfa = makeNative(nativeFile);
            } else
            {
                try
                {
                    url = new XFurl(xfile.getURL(), s);
                    xfa = loadAccessor(url);
                }
                catch(Exception _ex2)
                {
                    System.out.println("Error: " + xfile.getURL() + " " + s);
                }
            }
        }
        urlStr = url.toString();
    }

    private Class loadClass(String s, String s1, Hashtable hashtable)
        throws ClassNotFoundException, IllegalAccessException
    {
        Class class1 = (Class)hashtable.get(s);
        if(class1 != null)
            return class1;
        String s2 = System.getProperty("java.protocol.xfile");
        if(s2 == null)
            s2 = "";
        else
            s2 = s2 + "|";
        s2 = s2 + "com.sun";
        for(StringTokenizer stringtokenizer = new StringTokenizer(s2, "|"); class1 == null && stringtokenizer.hasMoreTokens();)
        {
            String s3 = stringtokenizer.nextToken().trim();
            String s4 = s3 + "." + s + "." + s1;
            try
            {
                class1 = Class.forName(s4);
            }
            catch(Exception _ex) { }
        }

        if(class1 == null)
        {
            throw new ClassNotFoundException();
        } else
        {
            hashtable.put(s, class1);
            return class1;
        }
    }

    private XFileAccessor loadAccessor(XFurl xfurl)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Class class1 = loadClass(xfurl.getProtocol(), "XFileAccessor", cachedAccessors);
        if(class1 == null)
            return null;
        else
            return (XFileAccessor)class1.newInstance();
    }

    private XFileAccessor makeNative(File file)
    {
        char c = File.separatorChar;
        try
        {
            url = new XFurl("file:///" + file.getPath().replace(c, '/'));
            return loadAccessor(url);
        }
        catch(Exception _ex)
        {
            System.out.println("Error: makenative:" + file.getPath());
        }
        return null;
    }

    private boolean bind()
    {
        if(bound)
        {
            return true;
        } else
        {
            bound = xfa.open(this, false, false);
            return bound;
        }
    }

    private boolean getBound()
    {
        return bound;
    }

    private File getNative()
    {
        return nativeFile;
    }

    protected XFileAccessor newAccessor()
    {
        try
        {
            return loadAccessor(url);
        }
        catch(Exception _ex)
        {
            return makeNative(nativeFile);
        }
    }

    private XFileAccessor getAccessor()
    {
        return xfa;
    }

    public XFileExtensionAccessor getExtensionAccessor()
    {
        try
        {
            String s;
            if(url.getProtocol().equals("nfs"))
                s = "nfsXFileExtensionAccessor";
            else
                s = "XFileExtensionAccessor";
            Class class1 = loadClass(url.getProtocol(), s, cachedExtensionAccessors);
            Constructor constructor = class1.getConstructor(new Class[] {
                getClass()
            });
            return (XFileExtensionAccessor)constructor.newInstance(new Object[] {
                this
            });
        }
        catch(Exception _ex)
        {
            return null;
        }
    }

    private XFurl getURL()
    {
        return url;
    }

    public String getFileSystemName()
    {
        return url.getProtocol();
    }

    public String getName()
    {
        if(nativeFile != null)
            return nativeFile.getName();
        else
            return url.getName();
    }

    public String getPath()
    {
        if(nativeFile != null)
            return nativeFile.getPath();
        else
            return url.getPath();
    }

    public String getAbsolutePath()
    {
        if(nativeFile != null)
            return nativeFile.getAbsolutePath();
        else
            return urlStr;
    }

    public String getCanonicalPath()
        throws IOException
    {
        if(nativeFile != null)
            return nativeFile.getCanonicalPath();
        else
            return urlStr;
    }

    public String getParent()
    {
        if(nativeFile != null)
            return nativeFile.getParent();
        else
            return url.getParent();
    }

    public boolean isAbsolute()
    {
        if(nativeFile != null)
            return nativeFile.isAbsolute();
        else
            return true;
    }

    public boolean exists()
    {
        if(!bind())
            return false;
        else
            return xfa.exists();
    }

    public boolean canWrite()
    {
        if(!bind())
            return false;
        else
            return xfa.canWrite();
    }

    public boolean canRead()
    {
        if(!bind())
            return false;
        else
            return xfa.canRead();
    }

    public boolean isFile()
    {
        if(!bind())
            return false;
        else
            return xfa.isFile();
    }

    public boolean isDirectory()
    {
        if(!bind())
            return false;
        else
            return xfa.isDirectory();
    }

    public long lastModified()
    {
        if(!bind())
            return 0L;
        else
            return xfa.lastModified();
    }

    public long length()
    {
        if(!bind())
            return 0L;
        if(xfa.exists())
            return xfa.length();
        else
            return 0L;
    }

    public boolean renameTo(XFile xfile)
    {
        if(xfile == null)
            throw new NullPointerException();
        if(!xfa.getClass().isInstance(xfile.getAccessor()))
            return false;
        if(!bind())
            return false;
        boolean flag = xfa.renameTo(xfile);
        if(flag)
        {
            url = xfile.getURL();
            urlStr = xfile.getAbsolutePath();
            nativeFile = xfile.getNative();
            xfa = xfile.getAccessor();
            bound = xfile.getBound();
        }
        return flag;
    }

    public boolean mkdir()
    {
        bind();
        return xfa.mkdir();
    }

    public boolean mkdirs()
    {
        bind();
        if(exists())
            return false;
        if(mkdir())
            return true;
        String s = getParent();
        return s != null && (new XFile(s)).mkdirs() && mkdir();
    }

    public String[] list()
    {
        if(!bind())
            return null;
        else
            return xfa.list();
    }

    public String[] list(XFilenameFilter xfilenamefilter)
    {
        if(!bind())
            return null;
        String as[] = list();
        if(as == null)
            return null;
        Vector vector = new Vector();
        for(int i = 0; i < as.length; i++)
            if(xfilenamefilter == null || xfilenamefilter.accept(this, as[i]))
                vector.addElement(as[i]);

        String as1[] = new String[vector.size()];
        vector.copyInto(as1);
        return as1;
    }

    public boolean delete()
    {
        if(!bind())
        {
            return false;
        } else
        {
            boolean flag = xfa.delete();
            bound = !flag;
            return flag;
        }
    }

    public int hashCode()
    {
        return urlStr.hashCode() ^ 0x12d591;
    }

    public boolean equals(Object obj)
    {
        if(obj == null || !(obj instanceof XFile))
            return false;
        else
            return url.toString().equals(((XFile)obj).getURL().toString());
    }

    public String toString()
    {
        if(nativeFile != null)
            return nativeFile.toString();
        else
            return urlStr;
    }

    private XFileAccessor xfa;
    private XFurl url;
    private String urlStr;
    private File nativeFile;
    private boolean bound;
    static Hashtable cachedAccessors = new Hashtable();
    static Hashtable cachedExtensionAccessors = new Hashtable();

}
