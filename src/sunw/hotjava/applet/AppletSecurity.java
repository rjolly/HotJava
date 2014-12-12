// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletSecurity.java

package sunw.hotjava.applet;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.applet.AppletClassLoader;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.security.AppletSecurityException;

// Referenced classes of package sunw.hotjava.applet:
//            AppletClassLoader, AppletSecurityContext

public class AppletSecurity extends sun.applet.AppletSecurity
{

    public AppletSecurity()
    {
        reset();
    }

    public void reset()
    {
        String s = System.getProperty("appletviewer.security.mode");
        if(s == null)
            s = "host";
        if(s.equals("unrestricted"))
            networkMode = 3;
        else
        if(s.equals("none"))
            networkMode = 1;
        else
            networkMode = 2;
        loadedClasses = new Hashtable();
    }

    boolean hjInApplet()
    {
        return inClassLoader();
    }

    public Object getSecurityContext()
    {
        ClassLoader classloader = currentClassLoader();
        if(classloader == null)
            return null;
        if(classloader instanceof sunw.hotjava.applet.AppletClassLoader)
        {
            sunw.hotjava.applet.AppletClassLoader appletclassloader = (sunw.hotjava.applet.AppletClassLoader)classloader;
            return new AppletSecurityContext(appletclassloader.getCodeBase(), appletclassloader.getDOM());
        }
        if(classloader instanceof AppletClassLoader)
            return super.getSecurityContext();
        else
            throw new AppletSecurityException("getsecuritycontext.unknown");
    }

    public synchronized void checkCreateClassLoader()
    {
        if(classLoaderDepth() == 2)
            throw new AppletSecurityException("checkcreateclassloader");
        else
            return;
    }

    public boolean inThreadGroup(ThreadGroup threadgroup)
    {
        ClassLoader classloader = currentClassLoader();
        if(classloader instanceof sunw.hotjava.applet.AppletClassLoader)
        {
            sunw.hotjava.applet.AppletClassLoader appletclassloader = (sunw.hotjava.applet.AppletClassLoader)classloader;
            ThreadGroup threadgroup1 = appletclassloader.getThreadGroup();
            return threadgroup1.parentOf(threadgroup);
        } else
        {
            return super.inThreadGroup(threadgroup);
        }
    }

    public boolean inThreadGroup(Thread thread)
    {
        return inThreadGroup(thread.getThreadGroup());
    }

    void hjParseACL(Vector vector, String s, String s1)
    {
        String s2 = System.getProperty("path.separator");
        for(StringTokenizer stringtokenizer = new StringTokenizer(s, s2); stringtokenizer.hasMoreTokens();)
        {
            String s3 = stringtokenizer.nextToken();
            if(s3.startsWith("~"))
            {
                String s4 = System.getProperty("user.home");
                if(s4 != null && s4.endsWith(File.separator))
                    s4 = s4.substring(0, s4.length() - 1);
                vector.addElement(s4 + s3.substring(1));
            } else
            if(s3.equals("+"))
            {
                if(s1 != null)
                    hjParseACL(vector, s1, null);
            } else
            {
                vector.addElement(s3);
            }
        }

    }

    String[] hjParseACL(String s, String s1)
    {
        if(s == null)
            return new String[0];
        if(s.equals("*"))
        {
            return null;
        } else
        {
            Vector vector = new Vector();
            hjParseACL(vector, s, s1);
            String as[] = new String[vector.size()];
            vector.copyInto(as);
            return as;
        }
    }

    private void hjInitializeACLs()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        readACL = hjParseACL(hjbproperties.getProperty("acl.read"), hjbproperties.getProperty("acl.read.default"));
        writeACL = hjParseACL(hjbproperties.getProperty("acl.write"), hjbproperties.getProperty("acl.write.default"));
        initACL = true;
    }

    public void checkConnect(String s, int i, Object obj)
    {
        checkConnect(s, i);
        if(obj != null)
            checkConnect(((URL)obj).getHost(), s);
    }

    public synchronized void checkConnect(String s, String s1, boolean flag)
    {
        if(s == null)
            return;
        switch(networkMode)
        {
        case 1: // '\001'
            throw new AppletSecurityException("checkconnect.networknone", s, s1);

        case 2: // '\002'
            try
            {
                super.inCheck = true;
                if(!s.equals(s1))
                    try
                    {
                        InetAddress inetaddress = InetAddress.getByName(s1);
                        InetAddress inetaddress2 = InetAddress.getByName(s);
                        if(inetaddress2.equals(inetaddress))
                            return;
                        else
                            throw new AppletSecurityException("checkconnect.networkhost1", s1, s);
                    }
                    catch(UnknownHostException _ex)
                    {
                        throw new AppletSecurityException("checkconnect.networkhost2", s1, s);
                    }
                try
                {
                    InetAddress inetaddress1 = InetAddress.getByName(s1);
                    return;
                }
                catch(UnknownHostException _ex) { }
                if(flag)
                    return;
                else
                    throw new AppletSecurityException("checkconnect.networkhost3", s1);
            }
            finally
            {
                super.inCheck = false;
            }

        case 3: // '\003'
            return;
        }
        throw new AppletSecurityException("checkconnect", s, s1);
    }

    public synchronized void checkConnect(String s, String s1)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        checkConnect(s, s1, hjbproperties.getBoolean("trustProxy"));
    }

    public ThreadGroup getThreadGroup()
    {
        ClassLoader classloader = currentClassLoader();
        if(classloader != null && (classloader instanceof sunw.hotjava.applet.AppletClassLoader))
        {
            sunw.hotjava.applet.AppletClassLoader appletclassloader = (sunw.hotjava.applet.AppletClassLoader)classloader;
            return appletclassloader.getThreadGroup();
        } else
        {
            return super.getThreadGroup();
        }
    }

    private static boolean debug;
    static final int NETWORK_NONE = 1;
    static final int NETWORK_HOST = 2;
    static final int NETWORK_UNRESTRICTED = 3;
    private static final int PRIVELEGED_PORT = 1024;
    boolean initACL;
    String readACL[];
    String writeACL[];
    int networkMode;
    Hashtable loadedClasses;
}
