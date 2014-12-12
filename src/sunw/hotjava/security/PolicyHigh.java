// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PolicyHigh.java

package sunw.hotjava.security;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Member;
import java.net.URL;
import sun.applet.AppletClassLoader;
import sunw.hotjava.applet.AppletSecurity;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.security:
//            PolicyUntrusted, BasePolicy, CommonSecurity

public class PolicyHigh extends PolicyUntrusted
{

    public static PolicyHigh getPolicyHigh()
    {
        if(high == null)
            high = new PolicyHigh();
        return high;
    }

    protected PolicyHigh()
    {
        properties = HJBProperties.getHJBProperties("beanPropertiesKey");
    }

    public boolean checkAccess(Thread thread)
    {
        CommonSecurity commonsecurity = getCSM();
        return commonsecurity.inThreadGroup(thread);
    }

    public boolean checkAccess(ThreadGroup threadgroup)
    {
        CommonSecurity commonsecurity = getCSM();
        return commonsecurity.inThreadGroup(threadgroup);
    }

    public boolean checkPropertyAccess(String s)
    {
        if(properties.getBoolean(s + ".applet"))
            return true;
        else
            return super.checkPropertyAccess(s);
    }

    public boolean checkRead(String s, URL url)
    {
        String s1 = getRealPath(s);
        String s2 = url.getHost();
        if(url.getProtocol().equals("file") && (s2 == null || s2.equals("") || s2.equals("~") || s2.equals("localhost")))
        {
            String s3 = null;
            try
            {
                String s4 = url.getFile();
                s4.replace('/', File.separatorChar);
                s3 = (new File(s4)).getCanonicalPath();
            }
            catch(IOException _ex)
            {
                return false;
            }
            if(!filesCaseSensitive())
            {
                s1 = s1.toLowerCase();
                s3 = s3.toLowerCase();
            }
            if(s1.startsWith(s3))
                return true;
        }
        return false;
    }

    public boolean checkWrite(String s)
    {
        getRealPath(s);
        return false;
    }

    public synchronized boolean checkRead(FileDescriptor filedescriptor)
    {
        return getCSM().policyInClass("java.net.SocketInputStream") && filedescriptor.valid();
    }

    public synchronized boolean checkWrite(FileDescriptor filedescriptor)
    {
        return getCSM().policyInClass("java.net.SocketOutputStream") && filedescriptor.valid();
    }

    public boolean checkListen(int i)
    {
        return i <= 0 || i >= 1024;
    }

    public boolean checkAccept(String s, int i)
    {
        if(i < 1024)
            return false;
        else
            return checkConnect(s, i);
    }

    public boolean checkConnect(String s, int i)
    {
        ClassLoader classloader = getCSM().policyGetLoader();
        if(classloader instanceof sunw.hotjava.applet.AppletClassLoader)
            return checkConnect(getCSM().getAppletHost(), s);
        if(classloader instanceof AppletClassLoader)
        {
            AppletClassLoader _tmp = (AppletClassLoader)classloader;
            return checkConnect(getCSM().getAppletHost(), s);
        } else
        {
            return false;
        }
    }

    public boolean checkConnect(String s, String s1, boolean flag)
    {
        if(s == null)
            return true;
        switch(getCSM().networkMode)
        {
        case 1: // '\001'
            return false;

        case 2: // '\002'
            if(!testConnect(s, s1, flag))
                return false;
            // fall through

        case 3: // '\003'
            return true;

        default:
            return false;
        }
    }

    public boolean checkConnect(String s, String s1)
    {
        return checkConnect(s, s1, properties.getBoolean("trustProxy"));
    }

    public boolean checkTopLevelWindow(Object obj)
    {
        return false;
    }

    public boolean checkPackageAccess(String s)
    {
        int i = s.indexOf('.');
        do
        {
            if(i < 0)
                i = s.length();
            String s1 = s.substring(0, i);
            if(properties.getBoolean("package.restrict.access." + s1))
                return false;
            if(i != s.length())
                i = s.indexOf('.', i + 1);
            else
                return true;
        } while(true);
    }

    public boolean checkPackageDefinition(String s)
    {
        int i = s.indexOf('.');
        do
        {
            if(i < 0)
                i = s.length();
            String s1 = s.substring(0, i);
            if(properties.getBoolean("package.restrict.definition." + s1))
                return false;
            if(i != s.length())
                i = s.indexOf('.', i + 1);
            else
                return true;
        } while(true);
    }

    public boolean checkSetFactory()
    {
        return false;
    }

    public boolean checkMemberAccess(Class class1, int i, int j)
    {
        if(i != 0)
        {
            ClassLoader classloader = getCSM().policyGetLoader();
            if(classloader != null && j <= 2 && classloader != class1.getClassLoader())
                return false;
        }
        return true;
    }

    public boolean checkPrintJobAccess()
    {
        return false;
    }

    public boolean checkSystemClipboardAccess()
    {
        return false;
    }

    public boolean checkAwtEventQueueAccess()
    {
        return false;
    }

    public boolean checkRun()
    {
        return true;
    }

    private static PolicyHigh high;
    private HJBProperties properties;
}
