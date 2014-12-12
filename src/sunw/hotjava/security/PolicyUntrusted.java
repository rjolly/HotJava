// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PolicyUntrusted.java

package sunw.hotjava.security;

import java.io.File;
import java.io.FileDescriptor;
import java.net.InetAddress;
import java.net.URL;

// Referenced classes of package sunw.hotjava.security:
//            BasePolicy

public class PolicyUntrusted extends BasePolicy
{

    public static PolicyUntrusted getPolicyUntrusted()
    {
        if(untrust == null)
            untrust = new PolicyUntrusted();
        return untrust;
    }

    protected PolicyUntrusted()
    {
    }

    protected boolean filesCaseSensitive()
    {
        if(File.separatorChar == '\\')
            return false;
        return File.separatorChar != ':';
    }

    public boolean checkAccess(Thread thread)
    {
        return false;
    }

    public boolean checkAccess(ThreadGroup threadgroup)
    {
        return false;
    }

    public boolean checkExit(int i)
    {
        return false;
    }

    public boolean checkExec(String s)
    {
        return false;
    }

    public boolean checkLink(String s)
    {
        return false;
    }

    public boolean checkPropertiesAccess()
    {
        return false;
    }

    public boolean checkPropertyAccess(String s)
    {
        return false;
    }

    public boolean checkRead(String s)
    {
        return false;
    }

    public boolean checkRead(String s, URL url)
    {
        return false;
    }

    public boolean checkWrite(String s)
    {
        return false;
    }

    public boolean checkRead(FileDescriptor filedescriptor)
    {
        return false;
    }

    public boolean checkWrite(FileDescriptor filedescriptor)
    {
        return false;
    }

    public boolean checkDelete(String s)
    {
        return false;
    }

    public boolean checkListen(int i)
    {
        return false;
    }

    public boolean checkAccept(String s, int i)
    {
        return false;
    }

    public boolean checkConnect(String s, int i)
    {
        return false;
    }

    public boolean checkMulticast(InetAddress inetaddress)
    {
        return false;
    }

    public boolean checkConnect(String s, String s1)
    {
        return false;
    }

    public boolean checkTopLevelWindow(Object obj)
    {
        return false;
    }

    public boolean checkPackageAccess(String s)
    {
        return false;
    }

    public boolean checkPackageDefinition(String s)
    {
        return false;
    }

    public boolean checkSetFactory()
    {
        return false;
    }

    public boolean checkMemberAccess(Class class1, int i, int j)
    {
        return false;
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

    public boolean checkSecurityAccess(String s)
    {
        return false;
    }

    public boolean checkRun()
    {
        return false;
    }

    public boolean checkFrameAccess()
    {
        return false;
    }

    private static PolicyUntrusted untrust;
}
