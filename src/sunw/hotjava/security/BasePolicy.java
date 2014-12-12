// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BasePolicy.java

package sunw.hotjava.security;

import java.io.*;
import java.net.*;
import java.security.Identity;
import java.security.Principal;
import java.util.Hashtable;

// Referenced classes of package sunw.hotjava.security:
//            AppletSecurityException, CommonSecurity, ExtPermissions, SiteIdentity

public abstract class BasePolicy
{

    protected CommonSecurity getCSM()
    {
        if(csm != null)
        {
            return csm;
        } else
        {
            csm = CommonSecurity.getCommonSecurity();
            return csm;
        }
    }

    static final void resetPermissions()
    {
        extPermissionsTable.clear();
    }

    final ExtPermissions findPermissions()
    {
        Principal principal = getCallerIdentity();
        return findPermissions(principal);
    }

    protected final ExtPermissions findPermissions(Object obj)
    {
        if(obj == null)
            return null;
        if(extPermissionsTable == null)
            return null;
        else
            return (ExtPermissions)extPermissionsTable.get(obj);
    }

    ExtPermissions obtainPermissions()
    {
        synchronized(extPermissionsTable)
        {
            Principal principal = getCallerIdentity();
            if(principal == null)
            {
                ExtPermissions extpermissions = null;
                return extpermissions;
            }
            ExtPermissions extpermissions4 = findPermissions(principal);
            if(extpermissions4 != null)
            {
                ExtPermissions extpermissions1 = extpermissions4;
                return extpermissions1;
            }
            extpermissions4 = new ExtPermissions();
            if(extpermissions4 == null)
            {
                ExtPermissions extpermissions2 = null;
                return extpermissions2;
            }
            extPermissionsTable.put(principal, extpermissions4);
            ExtPermissions extpermissions3 = extpermissions4;
            return extpermissions3;
        }
    }

    protected final Principal getCallerIdentity()
        throws AppletSecurityException
    {
        Class class1 = getCSM().currentLoadedClassSec();
        Object aobj[] = null;
        Object obj = null;
        aobj = class1.getSigners();
        if(aobj == null)
        {
            String s = getCSM().getAppletHost();
            if(s != null)
                obj = new SiteIdentity(s);
        } else
        {
            obj = (Identity)aobj[0];
        }
        return ((Principal) (obj));
    }

    String getRealPath(String s)
    {
        String s1 = null;
        try
        {
            s1 = (new File(s)).getCanonicalPath();
        }
        catch(IOException _ex)
        {
            if(File.separatorChar == '/' && s.startsWith("~"))
                try
                {
                    s = (new File("~")).getCanonicalPath() + s.substring(1);
                }
                catch(IOException _ex2) { }
            s1 = (new File(s)).getAbsolutePath();
        }
        return s1;
    }

    boolean testConnect(String s, String s1, boolean flag)
    {
        if(!s.equals(s1))
            try
            {
                boolean flag1 = s1 == null || "".equals(s1);
                boolean flag2 = s == null || "".equals(s);
                if(flag1 != flag2)
                    return false;
                InetAddress inetaddress = InetAddress.getByName(s1);
                InetAddress inetaddress2 = InetAddress.getByName(s);
                return inetaddress2.equals(inetaddress);
            }
            catch(UnknownHostException _ex)
            {
                return false;
            }
        try
        {
            InetAddress inetaddress1 = InetAddress.getByName(s1);
            return true;
        }
        catch(UnknownHostException _ex)
        {
            return flag;
        }
    }

    public abstract boolean checkAccess(Thread thread);

    public abstract boolean checkAccess(ThreadGroup threadgroup);

    public abstract boolean checkLink(String s);

    public abstract boolean checkPackageAccess(String s);

    public abstract boolean checkPackageDefinition(String s);

    public abstract boolean checkSetFactory();

    public abstract boolean checkMemberAccess(Class class1, int i, int j);

    public abstract boolean checkAwtEventQueueAccess();

    public abstract boolean checkSecurityAccess(String s);

    public abstract boolean checkFrameAccess();

    public abstract boolean checkExit(int i);

    public abstract boolean checkExec(String s);

    public abstract boolean checkPropertiesAccess();

    public abstract boolean checkPropertyAccess(String s);

    public abstract boolean checkRead(String s, URL url);

    public abstract boolean checkWrite(String s);

    public abstract boolean checkRead(FileDescriptor filedescriptor);

    public abstract boolean checkWrite(FileDescriptor filedescriptor);

    public abstract boolean checkDelete(String s);

    public abstract boolean checkListen(int i);

    public abstract boolean checkAccept(String s, int i);

    public abstract boolean checkMulticast(InetAddress inetaddress);

    public abstract boolean checkConnect(String s, int i);

    public abstract boolean checkTopLevelWindow(Object obj);

    public abstract boolean checkRun();

    public abstract boolean checkPrintJobAccess();

    public abstract boolean checkSystemClipboardAccess();

    public boolean hasPermissions(Principal principal)
    {
        return false;
    }

    public BasePolicy()
    {
    }

    public static final boolean ACTION_ALLOWED = true;
    public static final boolean ACTION_DISALLOWED = false;
    static CommonSecurity csm;
    private static Hashtable extPermissionsTable = new Hashtable();

}
