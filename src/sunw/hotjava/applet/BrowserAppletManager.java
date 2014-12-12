// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BrowserAppletManager.java

package sunw.hotjava.applet;

import java.applet.Applet;
import java.io.IOException;
import java.net.URL;
import sunw.hotjava.security.CommonSecurity;

// Referenced classes of package sunw.hotjava.applet:
//            AppletManager, BasicAppletManager, BrowserSecurityManager

public class BrowserAppletManager
    implements AppletManager
{

    public BrowserAppletManager()
    {
        appletManager = new BasicAppletManager();
        String s = System.getProperty("os.name");
        if(s.startsWith("JavaOS") || s.startsWith("javaos"))
            securityManager = new BrowserSecurityManager(1);
        else
            securityManager = new BrowserSecurityManager();
        System.setSecurityManager(securityManager);
    }

    public ClassLoader refClassLoader(URL url, String as[], Object obj)
    {
        return appletManager.refClassLoader(url, as, obj);
    }

    public Integer deRefClassLoader(URL url, boolean flag)
    {
        return appletManager.deRefClassLoader(url, flag);
    }

    public void destroyApplets(int i)
    {
        appletManager.destroyApplets(i);
    }

    public ThreadGroup getThreadGroup(URL url)
    {
        return appletManager.getThreadGroup(url);
    }

    public void removeThreadGroup(URL url)
    {
        appletManager.removeThreadGroup(url);
    }

    public Applet createApplet(ClassLoader classloader, String s, String s1)
        throws ClassNotFoundException, IllegalAccessException, IOException, InstantiationException, InterruptedException
    {
        return appletManager.createApplet(classloader, s, s1);
    }

    public Class createScriptClass(ClassLoader classloader, String s, byte abyte0[])
    {
        return appletManager.createScriptClass(classloader, s, abyte0);
    }

    public boolean checkFrameAccess()
    {
        return securityManager.checkFrameAccessOK();
    }

    BasicAppletManager appletManager;
    BrowserSecurityManager securityManager;
}
