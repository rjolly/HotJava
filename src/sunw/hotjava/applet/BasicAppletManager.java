// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BasicAppletManager.java

package sunw.hotjava.applet;

import java.applet.Applet;
import java.io.*;
import java.net.URL;
import java.util.Hashtable;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.security.AppletSecurityException;

// Referenced classes of package sunw.hotjava.applet:
//            AppletClassLoader, AppletObjectInputStream, AppletResourceLoader, AppletThreadGroup

public class BasicAppletManager
{

    private synchronized void flushClassLoader(URL url)
    {
        AppletClassLoader appletclassloader = (AppletClassLoader)classloaders.get(url);
        appletclassloader.destroy();
        classloaders.remove(url);
        codeBaseRefCounts.remove(url);
    }

    public void removeThreadGroup(URL url)
    {
        synchronized(threadGroups)
        {
            threadGroups.remove(url);
        }
    }

    public synchronized ClassLoader refClassLoader(URL url, String as[], Object obj)
    {
        if(url != null)
            try
            {
                AppletClassLoader appletclassloader = (AppletClassLoader)classloaders.get(url);
                boolean flag = false;
                if(appletclassloader == null)
                {
                    appletclassloader = new AppletClassLoader(url, this, obj);
                    flag = true;
                }
                incrementCodeBaseRefCount(appletclassloader.getBase());
                if(as != null && as.length > 0)
                    loadJarFiles(appletclassloader, as);
                if(flag)
                    classloaders.put(url, appletclassloader);
                return appletclassloader;
            }
            catch(IOException _ex)
            {
                System.out.println("IOException!!!!!!");
            }
            catch(InterruptedException _ex)
            {
                System.out.println("InterruptedException");
            }
        return null;
    }

    public static AppletClassLoader getClassLoaderIfExists(URL url)
    {
        AppletClassLoader appletclassloader = (AppletClassLoader)classloaders.get(url);
        return appletclassloader;
    }

    private void incrementCodeBaseRefCount(URL url)
    {
        synchronized(codeBaseRefCounts)
        {
            Integer integer = (Integer)codeBaseRefCounts.get(url);
            if(integer == null)
            {
                codeBaseRefCounts.put(url, new Integer(1));
            } else
            {
                integer = new Integer(integer.intValue() + 1);
                codeBaseRefCounts.put(url, integer);
            }
            if(integer == null)
                integer = new Integer(1);
        }
    }

    public ThreadGroup getThreadGroup(URL url)
    {
        Object obj;
        synchronized(threadGroups)
        {
            obj = (ThreadGroup)threadGroups.get(url);
            if(obj == null || ((ThreadGroup) (obj)).isDestroyed())
            {
                obj = new AppletThreadGroup(url + "-threadGroup");
                ((ThreadGroup) (obj)).setDaemon(true);
                threadGroups.put(url, obj);
            }
        }
        return ((ThreadGroup) (obj));
    }

    public synchronized Integer deRefClassLoader(URL url, boolean flag)
    {
        synchronized(codeBaseRefCounts)
        {
            Integer integer1 = (Integer)codeBaseRefCounts.get(url);
            if(integer1 == null || integer1.intValue() <= 0)
            {
                System.err.println("Invalid count!");
                Thread.dumpStack();
                integer1 = null;
            } else
            if(integer1.intValue() == 1)
                integer1 = null;
            else
                integer1 = new Integer(integer1.intValue() - 1);
            if(integer1 == null)
            {
                flushClassLoader(url);
                codeBaseRefCounts.remove(url);
            } else
            {
                codeBaseRefCounts.put(url, integer1);
            }
            Integer integer = integer1;
            return integer;
        }
    }

    private static synchronized void flushClassLoaders()
    {
        classloaders = new Hashtable(11);
        codeBaseRefCounts = new Hashtable(11);
    }

    private void loadJarFiles(AppletClassLoader appletclassloader, String as[])
        throws IOException, InterruptedException
    {
        AppletResourceLoader appletresourceloader = appletclassloader.getResourceLoader();
        for(int i = 0; i < as.length; i++)
            if(as[i] != null)
                appletresourceloader.loadJar(appletclassloader.getBase(), as[i]);

    }

    public Applet createApplet(ClassLoader classloader, String s, String s1)
        throws ClassNotFoundException, IllegalAccessException, IOException, InstantiationException, InterruptedException
    {
        AppletClassLoader appletclassloader = (AppletClassLoader)classloader;
        Applet applet = null;
        if(s != null)
        {
            applet = null;
            if(s.startsWith("sunw.hotjava.applets.") || s.startsWith("sunw.hotjava.bean.applets."))
                applet = (Applet)Class.forName(s).newInstance();
            if(applet == null)
            {
                Class class1 = loadCode(s, appletclassloader);
                if(class1 != null)
                    applet = (Applet)class1.newInstance();
                else
                    throw new ClassNotFoundException(s);
            }
        } else
        if(s1 != null)
        {
            java.io.InputStream inputstream = appletclassloader.getResourceAsStream(s1);
            if(inputstream == null)
                inputstream = appletclassloader.openRelativeURLWithSecurity(appletclassloader.getCodeBase(), s1);
            AppletObjectInputStream appletobjectinputstream = new AppletObjectInputStream(inputstream, appletclassloader);
            Object obj = appletobjectinputstream.readObject();
            applet = (Applet)obj;
        }
        return applet;
    }

    private Class loadCode(String s, AppletClassLoader appletclassloader)
        throws ClassNotFoundException
    {
        String s1 = ".class";
        String s2 = ".java";
        int i = s.lastIndexOf('.');
        String s9 = "";
        if(i != -1)
            s9 = s.substring(i);
        if(i != -1 && s.indexOf('/') != -1)
        {
            String s3 = s.substring(0, i);
            String s6 = s3.replace('/', '.');
            try
            {
                return loadCode(s3, s9, s6, appletclassloader);
            }
            catch(IOException _ex)
            {
                throw new ClassNotFoundException(s6);
            }
        }
        if(s9.equals(s2) || s9.equals(s1))
        {
            String s4 = s.substring(0, i);
            s4 = s4.replace('.', '/');
            s9 = s1;
            String s7 = s4.replace('/', '.');
            try
            {
                return loadCode(s4, s9, s7, appletclassloader);
            }
            catch(IOException ioexception)
            {
                throw new ClassNotFoundException(s7 + ":  " + ioexception.getLocalizedMessage());
            }
        }
        String s5 = s.replace('.', '/');
        s9 = s1;
        String s8 = s5.replace('/', '.');
        try
        {
            return loadCode(s5, s9, s8, appletclassloader);
        }
        catch(IOException _ex) { }
        catch(ClassNotFoundException _ex) { }
        if(i < 0)
            i = s.length();
        s5 = s.substring(0, i);
        s5 = s5.replace('.', '/');
        s9 = s.substring(i);
        s8 = s5.replace('/', '.');
        try
        {
            return loadCode(s5, s9, s8, appletclassloader);
        }
        catch(IOException _ex) { }
        catch(ClassNotFoundException _ex) { }
        s5 = s.substring(0, i);
        s5 = s5.replace('.', '/');
        s9 = s.substring(i);
        s8 = s.replace('/', '.');
        try
        {
            return loadCode(s5, s9, s8, appletclassloader);
        }
        catch(IOException _ex)
        {
            throw new ClassNotFoundException(s8);
        }
    }

    private Class loadCode(String s, String s1, String s2, AppletClassLoader appletclassloader)
        throws ClassNotFoundException, IOException
    {
        if(!isPriviledgedPage(appletclassloader))
            checkNewAppletPackageAccess(s2);
        return appletclassloader.loadCode(s, s1, s2);
    }

    private boolean isPriviledgedPage(AppletClassLoader appletclassloader)
    {
        URL url = appletclassloader.getCodeBase();
        String s = url.getProtocol();
        return "doc".equals(s) || "mailto".equals(s);
    }

    public void destroyApplets(int i)
    {
        AppletThreadGroup.letAppletsFinish(i);
    }

    public static void checkNewAppletPackageAccess(String s)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        int i = s.indexOf('.');
        do
        {
            if(i < 0)
                i = s.length();
            String s1 = s.substring(0, i);
            if(Boolean.valueOf(hjbproperties.getProperty("package.restrict.definition." + s1)).booleanValue())
                throw new AppletSecurityException("checkpackagedefinition", s);
            if(i != s.length())
                i = s.indexOf('.', i + 1);
            else
                return;
        } while(true);
    }

    public Class createScriptClass(ClassLoader classloader, String s, byte abyte0[])
    {
        if(!(classloader instanceof AppletClassLoader))
        {
            return null;
        } else
        {
            AppletClassLoader appletclassloader = (AppletClassLoader)classloader;
            return appletclassloader.defineRealClassFromBytes(s, abyte0, 0, abyte0.length);
        }
    }

    public BasicAppletManager()
    {
    }

    private static String propPrefix = "appletpanel.";
    private static Hashtable classloaders = new Hashtable(11);
    private static Hashtable codeBaseRefCounts = new Hashtable(11);
    private static Hashtable threadGroups = new Hashtable(11);

}
