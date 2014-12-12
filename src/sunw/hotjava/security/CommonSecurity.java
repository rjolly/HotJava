// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CommonSecurity.java

package sunw.hotjava.security;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.security.Certificate;
import java.security.Identity;
import sun.security.x509.X500Name;
import sunw.hotjava.applet.AppletSecurity;
import sunw.hotjava.applet.AppletSecurityContext;

// Referenced classes of package sunw.hotjava.security:
//            AppletSecurityException, BasePolicy, PolicyNone

public abstract class CommonSecurity extends AppletSecurity
{

    public CommonSecurity()
    {
        csm = this;
    }

    static CommonSecurity getCommonSecurity()
    {
        return csm;
    }

    protected abstract BasePolicy findPolicy(ClassLoader classloader);

    public BasePolicy getUntrustedPolicy()
    {
        return untrusted;
    }

    public BasePolicy getHighTrustPolicy()
    {
        return high;
    }

    public BasePolicy getMediumTrustPolicy()
    {
        return medium;
    }

    public BasePolicy getLowTrustPolicy()
    {
        return low;
    }

    public BasePolicy getNoPolicy()
    {
        return none;
    }

    public BasePolicy getGenericPolicy()
    {
        return generic;
    }

    protected void setBeanDelegateSecurityManager(CommonSecurity commonsecurity)
    {
        beanDelegateSecurityManager = commonsecurity;
    }

    public static CommonSecurity getBeanDelegateSecurityManager()
    {
        return beanDelegateSecurityManager;
    }

    private static boolean isDir(String s)
    {
        File file = new File(s);
        if(file.isDirectory())
            return true;
        if(s.length() == 2 && s.endsWith(":"))
            return (new File(s + File.separator)).isDirectory();
        else
            return false;
    }

    public static InputStream getInputStream(String s)
        throws Exception
    {
        String s1 = System.getProperty("user.home");
        if(s1 == null)
            throw new Exception("user.home property not set");
        if(!isDir(s1))
            throw new Exception("Can't read user.home: " + s1);
        if(s1 != null && !s1.endsWith(File.separator))
            s1 = s1 + File.separator;
        String s2 = s1 + ".hotjava" + File.separator + "security1.1" + File.separator + s;
        File file = new File(s2);
        if(!file.exists())
            return null;
        if(!file.canRead() || !file.canWrite())
        {
            throw new Exception("Can't read or write user.home: " + file);
        } else
        {
            FileInputStream fileinputstream = new FileInputStream(file.getPath());
            return new BufferedInputStream(fileinputstream);
        }
    }

    public static OutputStream getOutputStream(String s)
        throws Exception
    {
        String s1 = System.getProperty("user.home");
        if(s1 == null)
            throw new Exception("user.home property not set");
        if(!isDir(s1))
            throw new Exception("Can't read user.home: " + s1);
        if(s1 != null && !s1.endsWith(File.separator))
            s1 = s1 + File.separator;
        String s2 = s1 + ".hotjava" + File.separator + "security1.1";
        File file = new File(s2);
        file.mkdirs();
        String s3 = s1 + File.separator + ".hotjava" + File.separator + "security1.1" + File.separator + s;
        File file1 = new File(s3);
        return new BufferedOutputStream(new FileOutputStream(file1.getPath()));
    }

    public synchronized void checkCreateClassLoader()
    {
        if(classLoaderDepth() == 2 + depthAdjustor)
            throw new AppletSecurityException("checkcreateclassloader");
        else
            return;
    }

    public void checkAccess(Thread thread)
    {
        if(classLoaderDepth() == 3 + depthAdjustor && !inThreadGroup(thread))
        {
            ClassLoader classloader = currentClassLoader();
            BasePolicy basepolicy = findPolicy(classloader);
            if(!basepolicy.checkAccess(thread))
                throw new AppletSecurityException("checkaccess.thread");
        }
    }

    public void checkAccess(ThreadGroup threadgroup)
    {
        if(classLoaderDepth() == 4 + depthAdjustor && !inThreadGroup(threadgroup))
        {
            ClassLoader classloader = currentClassLoader();
            BasePolicy basepolicy = findPolicy(classloader);
            if(!basepolicy.checkAccess(threadgroup))
                throw new AppletSecurityException("checkaccess.threadgroup");
        }
    }

    public void checkExit(int i)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkExit(i))
            throw new AppletSecurityException("checkexit", String.valueOf(i));
        else
            return;
    }

    public void checkExec(String s)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkExec(s))
            throw new AppletSecurityException("checkexec");
        else
            return;
    }

    public void checkLink(String s)
    {
        ClassLoader classloader = currentClassLoader();
        switch(classLoaderDepth() - depthAdjustor)
        {
        case 2: // '\002'
        case 3: // '\003'
            BasePolicy basepolicy = findPolicy(classloader);
            if(!basepolicy.checkLink(s))
                throw new AppletSecurityException("checklink", s);
            break;
        }
    }

    public void checkPropertiesAccess()
    {
        ClassLoader classloader = currentClassLoader();
        if(classLoaderDepth() == 2 + depthAdjustor)
        {
            BasePolicy basepolicy = findPolicy(classloader);
            if(!basepolicy.checkPropertiesAccess())
                throw new AppletSecurityException("checkpropsaccess");
        }
    }

    public void checkPropertyAccess(String s)
    {
        ClassLoader classloader = currentClassLoader();
        if(classLoaderDepth() == 2 + depthAdjustor || classLoaderDepth() == 3 + depthAdjustor)
        {
            BasePolicy basepolicy = findPolicy(classloader);
            if(!basepolicy.checkPropertyAccess(s))
                throw new AppletSecurityException("checkpropsaccess.key", s);
        }
    }

    public void checkRead(String s)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(basepolicy instanceof PolicyNone)
            return;
        URL url = getAppletURL();
        if(!basepolicy.checkRead(s, url))
            throw new AppletSecurityException("checkread.exception", s);
        else
            return;
    }

    public void checkRead(String s, URL url)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkRead(s, url))
            throw new AppletSecurityException("checkread.exception", s);
        else
            return;
    }

    public void checkRead(String s, Object obj)
    {
        checkRead(s);
        if(obj != null)
            checkRead(s, ((AppletSecurityContext)obj).getCodeBase());
    }

    public void checkRead(FileDescriptor filedescriptor)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkRead(filedescriptor))
            throw new AppletSecurityException("checkread.fd");
        else
            return;
    }

    public void checkWrite(String s)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkWrite(s))
            throw new AppletSecurityException("checkwrite", s, s);
        else
            return;
    }

    public void checkWrite(FileDescriptor filedescriptor)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkWrite(filedescriptor))
            throw new AppletSecurityException("checkwrite.fd");
        else
            return;
    }

    public void checkDelete(String s)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkDelete(s))
            throw new AppletSecurityException("checkdelete", s);
        else
            return;
    }

    public void checkListen(int i)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkListen(i))
            throw new AppletSecurityException("checklisten", String.valueOf(i));
        else
            return;
    }

    public void checkAccept(String s, int i)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkAccept(s, i))
            throw new AppletSecurityException("checkaccept", s, String.valueOf(i));
        else
            return;
    }

    public void checkMulticast(InetAddress inetaddress)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkMulticast(inetaddress))
            throw new AppletSecurityException("checkmulticast", inetaddress.toString());
        else
            return;
    }

    public void checkConnect(String s, int i)
    {
        int j = classDepth("sun.net.www.http.HttpClient");
        if(j > 1)
            return;
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkConnect(s, i))
            throw new AppletSecurityException("checkconnect.networknone", getAppletHost(), s);
        else
            return;
    }

    public boolean checkTopLevelWindow(Object obj)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        return basepolicy.checkTopLevelWindow(obj);
    }

    public void checkPackageAccess(String s)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkPackageAccess(s))
            throw new AppletSecurityException("checkpackageaccess", s);
        else
            return;
    }

    public void checkPackageDefinition(String s)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkPackageDefinition(s))
            throw new AppletSecurityException("checkpackagedefinition", s);
        else
            return;
    }

    public void checkSetFactory()
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkSetFactory())
            throw new AppletSecurityException("cannotsetfactory");
        else
            return;
    }

    public void checkMemberAccess(Class class1, int i)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkMemberAccess(class1, i, classLoaderDepth() - depthAdjustor))
            throw new AppletSecurityException("checkmemberaccess");
        else
            return;
    }

    public void checkPrintJobAccess()
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkPrintJobAccess())
            throw new AppletSecurityException("checkgetprintjob");
        else
            return;
    }

    public void checkSystemClipboardAccess()
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkSystemClipboardAccess())
            throw new AppletSecurityException("checksystemclipboardaccess");
        else
            return;
    }

    public void checkAwtEventQueueAccess()
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkAwtEventQueueAccess())
            throw new AppletSecurityException("checkawteventqueueaccess");
        else
            return;
    }

    public void checkSecurityAccess(String s)
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkSecurityAccess(s))
            throw new AppletSecurityException("checksecurityaccess", s);
        else
            return;
    }

    public void checkRun()
    {
        BasePolicy basepolicy = findPolicy(currentClassLoader());
        if(!basepolicy.checkRun())
            throw new AppletSecurityException("checkrun");
        else
            return;
    }

    public void checkFrameAccess()
    {
        ClassLoader classloader = currentClassLoader();
        int i = classLoaderDepth();
        if(i > 3 + depthAdjustor || i < depthAdjustor)
            return;
        BasePolicy basepolicy = findPolicy(classloader);
        if(!basepolicy.checkFrameAccess())
            throw new AppletSecurityException("checkframeaccess");
        else
            return;
    }

    public boolean checkFrameAccessOK()
    {
        currentClassLoader();
        int i = classLoaderDepth();
        return i > 2 + depthAdjustor || i < depthAdjustor;
    }

    protected BasePolicy getDefaultPermission(String s, String s1)
    {
        String s2 = System.getProperty(s, s1);
        if(s2.equalsIgnoreCase("Medium"))
            return medium;
        if(s2.equalsIgnoreCase("Low"))
            return low;
        if(s2.equalsIgnoreCase("Untrusted"))
            return untrusted;
        else
            return high;
    }

    Class getCurrentClass()
    {
        return currentLoadedClass();
    }

    public String getSignerName()
    {
        Class class1 = currentLoadedClass();
        Object aobj[] = class1.getSigners();
        if(aobj == null)
            return null;
        Identity identity = (Identity)aobj[0];
        Certificate acertificate[] = identity.certificates();
        X500Name x500name = (X500Name)acertificate[0].getPrincipal();
        try
        {
            return x500name.getOrganization() + " " + x500name.getCommonName();
        }
        catch(IOException _ex)
        {
            return null;
        }
    }

    public String getSiteName()
    {
        URL url = getAppletURL();
        if(url == null)
            return "local applet";
        else
            return url.getHost();
    }

    public String getClassName()
    {
        Class class1 = currentLoadedClass();
        return class1.getName();
    }

    protected boolean inClassAbove()
    {
        Class aclass[] = getClassContext();
        int k = 0;
        if(aclass[0].getName().equals("sunw.hotjava.applet.BrowserSecurityManager"))
            k = 1;
        for(int i = k + 1; i < aclass.length; i++)
            if(!aclass[k].isAssignableFrom(aclass[i]))
            {
                for(int j = i + 1; j < aclass.length; j++)
                    if(aclass[k].isAssignableFrom(aclass[j]))
                        return true;

                return false;
            }

        return true;
    }

    public void reset()
    {
        super.reset();
        String s = System.getProperty("appletviewer.security.mode");
        if(s == null)
            s = "host";
        if(s.equals("unrestricted"))
        {
            networkMode = 3;
            return;
        }
        if(s.equals("none"))
        {
            networkMode = 1;
            return;
        } else
        {
            networkMode = 2;
            return;
        }
    }

    public URL getAppletURL()
    {
        Object obj = getSecurityContext();
        if(obj instanceof URL)
            return (URL)obj;
        if(obj instanceof AppletSecurityContext)
            return ((AppletSecurityContext)obj).getCodeBase();
        else
            return null;
    }

    public String getAppletHost()
    {
        return getAppletHost(getAppletURL());
    }

    public String getAppletHost(URL url)
    {
        if(url == null)
            return null;
        String s = url.getHost();
        String s1 = "applet." + url.getProtocol() + ".host.meaningful";
        if(!Boolean.getBoolean(s1))
            s = "";
        return s;
    }

    boolean policyInClass(String s)
    {
        return inClass(s);
    }

    int policyClassDepth(String s)
    {
        return classDepth(s);
    }

    Class currentLoadedClassSec()
    {
        return currentLoadedClass();
    }

    void setInCheck(boolean flag)
    {
        super.inCheck = flag;
    }

    public ClassLoader policyGetLoader()
    {
        return currentClassLoader();
    }

    private static CommonSecurity beanDelegateSecurityManager = null;
    int networkMode;
    static final int NETWORK_NONE = 1;
    static final int NETWORK_HOST = 2;
    static final int NETWORK_UNRESTRICTED = 3;
    static final int PRIVELEGED_PORT = 1024;
    protected BasePolicy untrusted;
    protected BasePolicy high;
    protected BasePolicy medium;
    protected BasePolicy low;
    protected BasePolicy none;
    protected BasePolicy generic;
    protected static final int PERMISSIONS = 0;
    protected static final int SIGNED = 1;
    protected static final int UNSIGNED = 2;
    protected static final int UNSAFE = 3;
    private static final String subDirName = "security1.1";
    private static CommonSecurity csm = null;
    protected static int depthAdjustor;

}
