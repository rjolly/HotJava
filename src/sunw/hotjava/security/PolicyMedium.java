// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PolicyMedium.java

package sunw.hotjava.security;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.security:
//            PolicyHigh, AppletSecurityException, BasePolicy, CommonSecurity, 
//            ExtPermissions, PolicyUntrusted, SecurityDialog

public class PolicyMedium extends PolicyHigh
{

    public static PolicyMedium getPolicyMedium()
    {
        if(med == null)
            med = new PolicyMedium();
        return med;
    }

    protected PolicyMedium()
    {
    }

    public boolean checkExec(String s)
    {
        if(super.checkExec(s))
            return true;
        ExtPermissions extpermissions = findPermissions();
        if(extpermissions != null && extpermissions.execOK())
            return true;
        SecurityDialog securitydialog = getSecurityDialog();
        int i = securitydialog.getExecAuthorization(s);
        if(i <= 0)
            return false;
        if(i != 2)
            return true;
        extpermissions = obtainPermissions();
        if(extpermissions != null)
            extpermissions.allowAllExec();
        return true;
    }

    public boolean checkPropertyAccess(String s)
    {
        if(super.checkPropertyAccess(s))
            return true;
        ExtPermissions extpermissions = findPermissions();
        if(extpermissions != null && extpermissions.propOK(s))
            return true;
        SecurityDialog securitydialog = getSecurityDialog();
        int i = securitydialog.getPropAuthorization(s);
        if(i <= 0)
            return false;
        extpermissions = obtainPermissions();
        if(extpermissions != null)
            if(i == 2)
                extpermissions.allowAllProps();
            else
                extpermissions.allowProp(s);
        return true;
    }

    private boolean checkFile(String s, URL url, boolean flag)
    {
        if(url == null)
            return true;
        String s1 = getRealPath(s);
        ExtPermissions extpermissions = findPermissions();
        if(extpermissions != null)
            if(flag)
            {
                if(extpermissions.writeOK(s1))
                    return true;
            } else
            if(extpermissions.readOK(s1))
                return true;
        String s2 = url.getHost();
        if(!flag && url.getProtocol().equals("file") && (s2 == null || s2.equals("") || s2.equals("~") || s2.equals("localhost")))
        {
            String s3 = null;
            try
            {
                s3 = (new File(url.getFile())).getCanonicalPath();
            }
            catch(IOException _ex)
            {
                return false;
            }
            if(s1.startsWith(s3))
            {
                System.out.println(" %%%% PolicyMedium : checkFile : ACTION_ALLOWED ");
                return true;
            }
        }
        SecurityDialog securitydialog = getSecurityDialog();
        if(securitydialog == null)
            return false;
        int i;
        if(flag)
            i = securitydialog.getWriteAuthorization(s1);
        else
            i = securitydialog.getReadAuthorization(s1);
        if(i <= 0)
            return false;
        extpermissions = obtainPermissions();
        if(flag)
        {
            if(i == 2)
                extpermissions.allowAllWrite();
            else
                extpermissions.allowWrite(s1);
        } else
        if(i == 2)
            extpermissions.allowAllRead();
        else
            extpermissions.allowRead(s1);
        return true;
    }

    public boolean checkRead(String s, URL url)
    {
        if(super.checkRead(s, url))
            return true;
        else
            return checkFile(s, url, false);
    }

    public boolean checkWrite(String s)
    {
        if(super.checkWrite(s))
            return true;
        else
            return checkFile(s, getCSM().getAppletURL(), true);
    }

    public synchronized boolean checkRead(FileDescriptor filedescriptor)
    {
        if(getCSM().policyInClass("java.lang.Runtime"))
            return true;
        else
            return super.checkRead(filedescriptor);
    }

    public synchronized boolean checkWrite(FileDescriptor filedescriptor)
    {
        if(getCSM().policyInClass("java.lang.Runtime"))
            return true;
        else
            return super.checkWrite(filedescriptor);
    }

    public boolean checkDelete(String s)
    {
        if(super.checkDelete(s))
            return true;
        String s1 = getRealPath(s);
        ExtPermissions extpermissions = findPermissions();
        if(extpermissions != null && extpermissions.deleteOK(s1))
            return true;
        SecurityDialog securitydialog = getSecurityDialog();
        if(securitydialog == null)
            return false;
        int i = securitydialog.getDeleteAuthorization(s1);
        if(i <= 0)
            return false;
        extpermissions = obtainPermissions();
        if(i == 2)
            extpermissions.allowAllDelete();
        else
            extpermissions.allowDelete(s1);
        return true;
    }

    public boolean checkListen(int i)
    {
        if(super.checkListen(i))
            return true;
        ExtPermissions extpermissions = findPermissions();
        if(extpermissions != null && extpermissions.listenOK(i))
            return true;
        SecurityDialog securitydialog = getSecurityDialog();
        int j = securitydialog.getListenAuthorization(Integer.toString(i));
        if(j <= 0)
            return false;
        extpermissions = obtainPermissions();
        if(extpermissions != null)
            if(j == 2)
                extpermissions.allowAllListen();
            else
                extpermissions.allowListen(i);
        return true;
    }

    public boolean checkAccept(String s, int i)
    {
        if(super.checkAccept(s, i))
            return true;
        ExtPermissions extpermissions = findPermissions();
        if(extpermissions != null && extpermissions.acceptOK(s, i))
            return true;
        SecurityDialog securitydialog = getSecurityDialog();
        int j = securitydialog.getAcceptAuthorization(s);
        if(j <= 0)
            return false;
        extpermissions = obtainPermissions();
        if(extpermissions != null)
            if(j == 2)
                extpermissions.allowAllAccept();
            else
                extpermissions.allowAccept(s, i);
        return true;
    }

    public boolean checkConnect(String s, String s1, boolean flag)
    {
        if(super.checkConnect(s, s1, flag))
            return true;
        ExtPermissions extpermissions = findPermissions();
        if(extpermissions != null && extpermissions.connectOK(s1, 0))
            return true;
        if(testConnect(s, s1, flag))
            return true;
        SecurityDialog securitydialog = getSecurityDialog();
        int i = securitydialog.getConnectAuthorization(s1);
        if(i <= 0)
            return false;
        extpermissions = obtainPermissions();
        if(extpermissions != null)
            if(i == 2)
                extpermissions.allowAllConnect();
            else
                extpermissions.allowConnect(s1, 0);
        return true;
    }

    public boolean checkMulticast(InetAddress inetaddress)
    {
        if(super.checkMulticast(inetaddress))
            return true;
        ExtPermissions extpermissions = findPermissions();
        if(extpermissions != null && extpermissions.multicastOK(inetaddress))
            return true;
        SecurityDialog securitydialog = getSecurityDialog();
        int i = securitydialog.getConnectAuthorization("<multicast>");
        if(i <= 0)
            return false;
        extpermissions = obtainPermissions();
        if(extpermissions != null)
            if(i == 2)
                extpermissions.allowAllConnect();
            else
                extpermissions.allowConnect("<multicast>", 0);
        return true;
    }

    static final SecurityDialog getSecurityDialog()
    {
        SecurityDialog securitydialog = new SecurityDialog(HJBProperties.getLastFocusHolder());
        if(securitydialog == null)
            throw new AppletSecurityException("feedback.prevented");
        else
            return securitydialog;
    }

    private static PolicyMedium med;
}
