// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PolicyNone.java

package sunw.hotjava.security;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.net.URL;

// Referenced classes of package sunw.hotjava.security:
//            BasePolicy, CommonSecurity

public class PolicyNone extends BasePolicy
{

    public static PolicyNone getPolicyNone()
    {
        if(none == null)
            none = new PolicyNone();
        return none;
    }

    protected PolicyNone()
    {
    }

    public boolean checkAccess(Thread thread)
    {
        return true;
    }

    public boolean checkAccess(ThreadGroup threadgroup)
    {
        return true;
    }

    public boolean checkExit(int i)
    {
        ClassLoader classloader = getCSM().policyGetLoader();
        return classloader == null;
    }

    public boolean checkExec(String s)
    {
        return true;
    }

    public boolean checkLink(String s)
    {
        return true;
    }

    public boolean checkPropertiesAccess()
    {
        return true;
    }

    public boolean checkPropertyAccess(String s)
    {
        return true;
    }

    public boolean checkRead(String s)
    {
        return true;
    }

    public boolean checkRead(String s, URL url)
    {
        return true;
    }

    public boolean checkWrite(String s)
    {
        return true;
    }

    public boolean checkRead(FileDescriptor filedescriptor)
    {
        return true;
    }

    public boolean checkWrite(FileDescriptor filedescriptor)
    {
        return true;
    }

    public boolean checkDelete(String s)
    {
        return true;
    }

    public boolean checkListen(int i)
    {
        return true;
    }

    public boolean checkAccept(String s, int i)
    {
        return true;
    }

    public boolean checkConnect(String s, int i)
    {
        return true;
    }

    public boolean checkConnect(String s, int i, Object obj)
    {
        return true;
    }

    public boolean checkConnect(String s, String s1, boolean flag)
    {
        return true;
    }

    public boolean checkConnect(String s, String s1)
    {
        return true;
    }

    public boolean checkTopLevelWindow(Object obj)
    {
        return true;
    }

    public boolean checkPackageAccess(String s)
    {
        return true;
    }

    public boolean checkPackageDefinition(String s)
    {
        return true;
    }

    public boolean checkSetFactory()
    {
        return true;
    }

    public boolean checkMemberAccess(Class class1, int i, int j)
    {
        return true;
    }

    public boolean checkPrintJobAccess()
    {
        return true;
    }

    public boolean checkSystemClipboardAccess()
    {
        return true;
    }

    public boolean checkAwtEventQueueAccess()
    {
        return true;
    }

    public boolean checkSecurityAccess(String s)
    {
        return true;
    }

    public boolean checkFrameAccess()
    {
        return true;
    }

    public boolean checkRun()
    {
        return true;
    }

    public boolean checkMulticast(InetAddress inetaddress)
    {
        return true;
    }

    private static PolicyNone none;
}
