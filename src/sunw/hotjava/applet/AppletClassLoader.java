// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletClassLoader.java

package sunw.hotjava.applet;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.security.Identity;
import java.util.Hashtable;
import java.util.Vector;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.security.*;

// Referenced classes of package sunw.hotjava.applet:
//            AppletClassEntry, AppletIOException, AppletLoadRedirectException, AppletResourceLoader, 
//            AppletSecurityContext, BasicAppletManager, HotJavaClassLoader, ScriptingEngineLoader

public class AppletClassLoader extends ClassLoader
    implements HotJavaClassLoader
{

    public void destroy()
    {
        archives = null;
        base = null;
        dom = null;
        resources = null;
        rawClasses = null;
        appletManager = null;
    }

    AppletClassLoader(URL url, BasicAppletManager basicappletmanager, Object obj)
    {
        archives = new Vector();
        rawClasses = new Hashtable();
        dom = obj;
        appletManager = basicappletmanager;
        base = url;
        resources = new AppletResourceLoader(this);
    }

    URL getCodeBase()
    {
        return base;
    }

    Object getDOM()
    {
        return dom;
    }

    public Object getSecurityContext()
    {
        return new AppletSecurityContext(base, dom);
    }

    private boolean hostsEqual(URL url, URL url1)
    {
        String s = url.getHost();
        String s1 = url1.getHost();
        return hostsEqual(s, s1);
    }

    private boolean hostsEqual(String s, String s1)
    {
        if(s == null)
            return s1 == null;
        if(s1 == null)
            return false;
        if(s.equalsIgnoreCase(s1))
            return true;
        try
        {
            InetAddress inetaddress = InetAddress.getByName(s);
            InetAddress inetaddress1 = InetAddress.getByName(s1);
            return inetaddress.equals(inetaddress1);
        }
        catch(UnknownHostException _ex) { }
        catch(SecurityException _ex) { }
        return false;
    }

    URL makeRelativeURLWithSecurity(URL url, String s)
        throws IOException
    {
        URL url1 = new URL(url, s);
        String s1 = url.getHost();
        String s2 = url1.getHost();
        if(!url.getProtocol().equals(url1.getProtocol()) || s1 == null && s2 != null || s1 != null && !s1.equals(s2) || url.getPort() != url1.getPort())
            throw new AppletIOException("throw.illegal.relative", s);
        else
            return url1;
    }

    InputStream openRelativeURLWithSecurity(URL url, String s)
        throws IOException
    {
        URL url1 = makeRelativeURLWithSecurity(url, s);
        URLConnection urlconnection = checkRedirects(url1.openConnection());
        return openConnection(urlconnection);
    }

    URLConnection checkRedirects(URLConnection urlconnection)
        throws IOException
    {
        int i = 0;
        boolean flag;
        do
        {
            tryToStopFollowRedirects(urlconnection);
            flag = false;
            if(urlconnection instanceof HttpURLConnection)
            {
                HttpURLConnection httpurlconnection = (HttpURLConnection)urlconnection;
                int j = httpurlconnection.getResponseCode();
                if(j >= 300 && j <= 305 && j != 304)
                {
                    URL url = httpurlconnection.getURL();
                    String s = httpurlconnection.getHeaderField("Location");
                    URL url1 = null;
                    if(s != null)
                        url1 = new URL(url, s);
                    httpurlconnection.disconnect();
                    if(url1 == null || !url.getProtocol().equals(url1.getProtocol()) || url.getPort() != url1.getPort() || !hostsEqual(url, url1) || i >= 5)
                        throw new AppletLoadRedirectException("loadclass.throw.redirect", url, url1);
                    flag = true;
                    urlconnection = url1.openConnection();
                    i++;
                }
            }
        } while(flag);
        return urlconnection;
    }

    InputStream openConnection(URLConnection urlconnection)
        throws IOException
    {
        try
        {
            return urlconnection.getInputStream();
        }
        catch(IOException ioexception)
        {
            throw ioexception;
        }
    }

    private void tryToStopFollowRedirects(URLConnection urlconnection)
    {
        if(!(urlconnection instanceof HttpURLConnection))
            return;
        Class class1 = urlconnection.getClass();
        Class aclass[] = new Class[1];
        Object aobj[] = new Object[1];
        aclass[0] = Boolean.TYPE;
        aobj[0] = new Boolean(false);
        try
        {
            Method method = class1.getMethod("setInstanceFollowRedirects", aclass);
            method.invoke(urlconnection, aobj);
            return;
        }
        catch(Exception _ex)
        {
            System.out.println("Cookies with redirection probably won't work");
        }
    }

    private Class loadClass(String s, URL url)
        throws IOException
    {
        InputStream inputstream = null;
        try
        {
            URLConnection urlconnection = url.openConnection();
            urlconnection.setAllowUserInteraction(false);
            urlconnection = checkRedirects(urlconnection);
            inputstream = openConnection(urlconnection);
            int i = urlconnection.getContentLength();
            byte abyte0[] = new byte[i != -1 ? i : 4096];
            int j = 0;
            Thread thread = Thread.currentThread();
            int k;
            while((k = inputstream.read(abyte0, j, abyte0.length - j)) >= 0 && !thread.isInterrupted()) 
            {
                if((j += k) != abyte0.length)
                    continue;
                if(i >= 0)
                    break;
                byte abyte1[] = new byte[j * 2];
                System.arraycopy(abyte0, 0, abyte1, 0, j);
                abyte0 = abyte1;
            }
            if(thread.isInterrupted())
                throw new AppletIOException("loadclass.throw.interrupted", url);
            setAccSuperBit(abyte0, 0, j);
            Class class1 = defineClass(s, abyte0, 0, j);
            return class1;
        }
        catch(IOException ioexception)
        {
            throw ioexception;
        }
        catch(Throwable throwable)
        {
            System.out.println(s);
            throwable.printStackTrace();
            throw new AppletIOException("loadclass.throw.notloaded", url);
        }
        finally
        {
            if(inputstream != null)
                inputstream.close();
        }
    }

    public Class loadClass(String s)
        throws ClassNotFoundException
    {
        return loadClass(s, true);
    }

    protected Class loadClass(String s, boolean flag)
        throws ClassNotFoundException
    {
        Class class1 = findLoadedClass(s);
        if(class1 == null)
        {
            SecurityManager securitymanager = System.getSecurityManager();
            if(securitymanager != null)
            {
                int i = s.lastIndexOf('.');
                if(i >= 0)
                    securitymanager.checkPackageAccess(s.substring(0, i));
            }
            try
            {
                return findSystemClass(s);
            }
            catch(ClassNotFoundException _ex)
            {
                class1 = findClass(s);
            }
            if(class1 == null)
            {
                ScriptingEngineLoader scriptingengineloader = ScriptingEngineLoader.getLoader();
                if(scriptingengineloader != null)
                    try
                    {
                        class1 = scriptingengineloader.loadClass(s);
                    }
                    catch(ClassNotFoundException _ex) { }
            }
            if(class1 == null)
                throw new ClassNotFoundException(s);
        }
        if(flag)
            resolveClass(class1);
        return class1;
    }

    synchronized Class loadCode(String s, String s1, String s2)
        throws ClassNotFoundException, IOException
    {
        String s3 = null;
        int i = s2.lastIndexOf('.');
        if(i < 0)
            s3 = "";
        else
            s3 = s2.substring(0, i);
        int j = 0;
        boolean flag = false;
        TrustManager trustmanager = TrustManager.getTrustManager();
        if(trustmanager != null)
        {
            CertificateTrust acertificatetrust[] = trustmanager.getSessionSites();
            String s4 = base.getHost();
            for(int k = 0; k < acertificatetrust.length; k++)
            {
                if(!hostsEqual(s4, acertificatetrust[k].getName()))
                    continue;
                j = acertificatetrust[k].getMode();
                flag = true;
                break;
            }

        }
        if(flag && j == 9)
            throw new AppletSecurityException("checkpackageaccess", s3);
        boolean flag1 = Boolean.getBoolean("hotjava.appletsJS.enableApplets");
        boolean flag2 = !System.getProperty("hotjava.default.signed.security").equalsIgnoreCase("untrusted");
        Class class1 = findLoadedClass(s2);
        if(class1 != null)
        {
            Object aobj[] = class1.getSigners();
            if(aobj == null && !flag1 && !flag)
                throw new AppletSecurityException("checkpackageaccess", s3);
            if(aobj != null)
            {
                boolean flag3 = false;
                if(trustmanager != null)
                {
label0:
                    for(int l = 0; l < aobj.length; l++)
                    {
                        if(!(aobj[l] instanceof Identity))
                            continue;
                        java.security.Certificate acertificate[] = ((Identity)aobj[l]).certificates();
                        for(int i1 = 0; i1 < acertificate.length; i1++)
                        {
                            java.security.Certificate certificate = acertificate[i1];
                            CertificateTrust certificatetrust = trustmanager.getTrustForCert(certificate);
                            if(certificatetrust == null || certificatetrust.getMode() == 13)
                                continue;
                            flag3 = true;
                            if(certificatetrust.getMode() != 9)
                                continue;
                            j = certificatetrust.getMode();
                            break label0;
                        }

                    }

                }
                if(flag3)
                {
                    if(j == 9)
                        throw new AppletSecurityException("checkpackageaccess", s3);
                } else
                if(!flag2 && !flag)
                    throw new AppletSecurityException("checkpackageaccess", s3);
            }
            return class1;
        }
        SecurityManager securitymanager = System.getSecurityManager();
        if(securitymanager != null)
            securitymanager.checkPackageAccess(s3);
        try
        {
            return findSystemClass(s2);
        }
        catch(ClassNotFoundException _ex) { }
        if(securitymanager != null)
            securitymanager.checkPackageDefinition(s3);
        AppletClassEntry appletclassentry = (AppletClassEntry)rawClasses.get(s2);
        if(appletclassentry != null)
            try
            {
                Identity aidentity[] = appletclassentry.ids;
                if(aidentity == null && !flag1 && !flag)
                    throw new AppletSecurityException("checkpackageaccess", s3);
                if(aidentity != null)
                {
                    boolean flag4 = false;
                    if(trustmanager != null)
                    {
label1:
                        for(int j1 = 0; j1 < aidentity.length; j1++)
                        {
                            if(!(aidentity[j1] instanceof Identity))
                                continue;
                            java.security.Certificate acertificate1[] = ((Identity)aidentity[j1]).certificates();
                            for(int k1 = 0; k1 < acertificate1.length; k1++)
                            {
                                java.security.Certificate certificate1 = acertificate1[k1];
                                CertificateTrust certificatetrust1 = trustmanager.getTrustForCert(certificate1);
                                if(certificatetrust1 == null || certificatetrust1.getMode() == 13)
                                    continue;
                                flag4 = true;
                                if(certificatetrust1.getMode() != 9)
                                    continue;
                                j = certificatetrust1.getMode();
                                break label1;
                            }

                        }

                    }
                    if(flag4)
                    {
                        if(j == 9)
                            throw new AppletSecurityException("checkpackageaccess", s3);
                    } else
                    if(!flag2 && !flag)
                        throw new AppletSecurityException("checkpackageaccess", s3);
                }
                Class class3 = super.defineClass(appletclassentry.classBuf, appletclassentry.start, appletclassentry.len);
                if(appletclassentry.ids != null)
                    setSigners(class3, appletclassentry.ids);
                Class class2 = class3;
                return class2;
            }
            finally
            {
                rawClasses.remove(s2);
            }
        try
        {
            URL url = new URL(base, s + s1);
            class1 = loadClass(s2, url);
        }
        catch(FileNotFoundException _ex)
        {
            errorMsg("filenotfound", s2);
        }
        catch(ClassFormatError _ex)
        {
            errorMsg("fileformat", s2);
        }
        catch(IOException ioexception)
        {
            throw ioexception;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
            errorMsg("fileexception", exception.toString(), s2);
        }
        catch(ThreadDeath threaddeath)
        {
            errorMsg("filedeath", s2);
            throw threaddeath;
        }
        catch(Error error)
        {
            errorMsg("fileerror", error.toString(), s2);
        }
        if(class1 != null)
            resolveClass(class1);
        return class1;
    }

    AppletClassEntry defineClassFromBytes(String s, byte abyte0[], int i, int j)
    {
        setAccSuperBit(abyte0, i, j);
        AppletClassEntry appletclassentry = new AppletClassEntry(abyte0, i, j, s, null);
        rawClasses.put(appletclassentry, appletclassentry);
        rawClasses.put(s, appletclassentry);
        return appletclassentry;
    }

    void errorMsg(String s, String s1)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("securityProperties");
        String s2 = hjbproperties.getPropertyReplace(propPrefix + s, s1);
        System.err.println(s2);
    }

    void errorMsg(String s, String s1, String s2)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("securityProperties");
        String s3 = hjbproperties.getPropertyReplace(propPrefix + s, s1, s2);
        System.err.println(s3);
    }

    private synchronized Class findClass(String s)
    {
        Class class1 = findLoadedClass(s);
        if(class1 != null)
            return class1;
        SecurityManager securitymanager = System.getSecurityManager();
        if(securitymanager != null)
        {
            int i = s.lastIndexOf('.');
            if(i >= 0)
                securitymanager.checkPackageDefinition(s.substring(0, i));
        }
        AppletClassEntry appletclassentry = (AppletClassEntry)rawClasses.get(s);
        if(appletclassentry != null)
            try
            {
                Class class3 = super.defineClass(appletclassentry.classBuf, appletclassentry.start, appletclassentry.len);
                if(appletclassentry.ids != null)
                    setSigners(class3, appletclassentry.ids);
                Class class2 = class3;
                return class2;
            }
            finally
            {
                rawClasses.remove(s);
            }
        String s1 = s.replace('.', '/') + ".class";
        try
        {
            URL url = new URL(base, s1);
            class1 = loadClass(s, url);
        }
        catch(FileNotFoundException _ex)
        {
            errorMsg("filenotfound", s);
        }
        catch(ClassFormatError _ex)
        {
            errorMsg("fileformat", s);
        }
        catch(IOException _ex)
        {
            errorMsg("fileioexception", s);
        }
        catch(Exception exception1)
        {
            errorMsg("fileexception", exception1.toString(), s);
        }
        catch(ThreadDeath threaddeath)
        {
            errorMsg("filedeath", s);
            throw threaddeath;
        }
        catch(Error error)
        {
            errorMsg("fileerror", error.toString(), s);
        }
        return class1;
    }

    public AppletResourceLoader getResourceLoader()
    {
        return resources;
    }

    private boolean resourceExists(URL url)
    {
        boolean flag = true;
        try
        {
            URLConnection urlconnection = url.openConnection();
            if(urlconnection instanceof HttpURLConnection)
            {
                HttpURLConnection httpurlconnection = (HttpURLConnection)urlconnection;
                int i = httpurlconnection.getResponseCode();
                httpurlconnection.disconnect();
                if(i == 200)
                    return true;
                if(i >= 400)
                    return false;
            } else
            {
                InputStream inputstream = url.openStream();
                inputstream.close();
            }
        }
        catch(Exception _ex)
        {
            flag = false;
        }
        return flag;
    }

    public URL getResource(String s)
    {
        URL url = ClassLoader.getSystemResource(s);
        if(url != null)
            return url;
        try
        {
            url = resources.getResourceAsURL(s);
            if(url != null)
                return url;
            url = new URL(base, s);
            if(!resourceExists(url))
                url = null;
        }
        catch(Exception _ex)
        {
            url = null;
        }
        return url;
    }

    public InputStream getResourceAsStream(String s)
    {
        InputStream inputstream = ClassLoader.getSystemResourceAsStream(s);
        if(inputstream != null)
            return inputstream;
        URL url = getResource(s);
        if(url == null)
            return null;
        try
        {
            return url.openStream();
        }
        catch(IOException _ex)
        {
            return null;
        }
    }

    public ThreadGroup getThreadGroup()
    {
        return appletManager.getThreadGroup(base);
    }

    private void setAccSuperBit(byte abyte0[], int i, int j)
    {
        int k = Math.min(i + j, abyte0.length);
        if(k - i < 12)
            return;
        int l = (0xff & abyte0[i]) << 24 | (0xff & abyte0[i + 1]) << 16 | (0xff & abyte0[i + 2]) << 8 | 0xff & abyte0[i + 3];
        int i1 = (0xff & abyte0[i + 4]) << 8 | 0xff & abyte0[i + 5];
        int j1 = (0xff & abyte0[i + 6]) << 8 | 0xff & abyte0[i + 7];
        i += 8;
        if(l != 0xcafebabe || j1 != 45 || i1 > 3)
            return;
        int k1 = (0xff & abyte0[i]) << 8 | 0xff & abyte0[i + 1];
        i += 2;
        for(k1--; k1 > 0; k1--)
        {
            if(k - i < 5)
                return;
            switch(abyte0[i] & 0xff)
            {
            case 7: // '\007'
            case 8: // '\b'
                i += 3;
                break;

            case 3: // '\003'
            case 4: // '\004'
            case 9: // '\t'
            case 10: // '\n'
            case 11: // '\013'
            case 12: // '\f'
                i += 5;
                break;

            case 5: // '\005'
            case 6: // '\006'
                k1--;
                i += 9;
                break;

            case 1: // '\001'
                i++;
                int l1 = (0xff & abyte0[i]) << 8 | 0xff & abyte0[i + 1];
                i += l1 + 2;
                break;

            case 2: // '\002'
            default:
                return;
            }
        }

        if(k - i < 2)
        {
            return;
        } else
        {
            i++;
            abyte0[i] |= 0x20;
            return;
        }
    }

    public URL getBase()
    {
        return base;
    }

    public Class defineRealClassFromBytes(String s, byte abyte0[], int i, int j)
    {
        setAccSuperBit(abyte0, i, j);
        Class class1 = defineClass(s, abyte0, i, j);
        if(class1 != null)
        {
            resolveClass(class1);
            return class1;
        } else
        {
            return null;
        }
    }

    private static String propPrefix = "appletclassloader.";
    Vector archives;
    URL base;
    Object dom;
    AppletResourceLoader resources;
    protected Hashtable rawClasses;
    private BasicAppletManager appletManager;

}
