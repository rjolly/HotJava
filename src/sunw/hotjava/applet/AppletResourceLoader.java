// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletResourceLoader.java

package sunw.hotjava.applet;

import java.applet.AudioClip;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.*;
import java.net.*;
import java.security.Identity;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import sun.applet.AppletAudioClip;
import sun.awt.image.URLImageSource;
import sun.tools.jar.JarVerifierStream;
import sunw.hotjava.security.TrustManager;

// Referenced classes of package sunw.hotjava.applet:
//            AppletClassEntry, AppletClassLoader

public class AppletResourceLoader
{

    public AppletResourceLoader(AppletClassLoader appletclassloader)
    {
        jarsLoaded = new Hashtable(3);
        resourceHash = new Hashtable(11);
        mimeHash = new Hashtable(11);
        loader = appletclassloader;
    }

    public void loadJar(URL url, String s)
        throws IOException
    {
        URL url1 = loader.makeRelativeURLWithSecurity(url, s);
        synchronized(jarsLoaded)
        {
            do
            {
                Boolean boolean1 = (Boolean)jarsLoaded.get(url1);
                if(boolean1 == null)
                {
                    jarsLoaded.put(url1, Boolean.FALSE);
                    break;
                }
                if(boolean1 == Boolean.TRUE)
                    return;
                if(boolean1 == Boolean.FALSE)
                    try
                    {
                        jarsLoaded.wait();
                    }
                    catch(InterruptedException interruptedexception)
                    {
                        throw new IOException(interruptedexception.toString());
                    }
            } while(true);
        }
        boolean flag = false;
        try
        {
            URLConnection urlconnection = loader.checkRedirects(url1.openConnection());
            InputStream inputstream = loader.openConnection(urlconnection);
            loadJar(url, url1, inputstream);
            flag = true;
        }
        catch(FileNotFoundException _ex)
        {
            System.out.println("Resource " + url1 + " was not found");
        }
        finally
        {
            synchronized(jarsLoaded)
            {
                if(flag)
                    jarsLoaded.put(url1, Boolean.TRUE);
                else
                    jarsLoaded.remove(url1);
                jarsLoaded.notifyAll();
            }
        }
    }

    private void loadJar(URL url, URL url1, InputStream inputstream)
        throws IOException
    {
        JarVerifierStream jarverifierstream = null;
        Hashtable hashtable = new Hashtable();
        try
        {
            jarverifierstream = new JarVerifierStream(inputstream);
            ZipEntry zipentry = null;
            byte abyte0[] = new byte[2048];
            while((zipentry = jarverifierstream.getNextEntry()) != null) 
            {
                String s = zipentry.getName();
                String s1 = null;
                byte abyte1[] = null;
                int i = (int)zipentry.getSize();
                ByteArrayOutputStream bytearrayoutputstream = null;
                if(i > 0)
                {
                    abyte1 = new byte[i];
                    int l;
                    for(int j = i; j > 0; j -= l)
                    {
                        l = jarverifierstream.read(abyte1, i - j, j);
                        if(l == -1)
                        {
                            byte abyte2[] = abyte1;
                            abyte1 = new byte[i - j];
                            System.arraycopy(abyte2, 0, abyte1, 0, abyte1.length);
                        }
                    }

                    int i1 = jarverifierstream.read();
                    if(i1 != -1)
                    {
                        bytearrayoutputstream = new ByteArrayOutputStream();
                        bytearrayoutputstream.write(abyte1);
                        abyte1 = null;
                        bytearrayoutputstream.write(i1);
                    }
                } else
                {
                    bytearrayoutputstream = new ByteArrayOutputStream();
                }
                if(bytearrayoutputstream != null)
                {
                    int k;
                    while((k = jarverifierstream.read(abyte0)) > 0) 
                        bytearrayoutputstream.write(abyte0, 0, k);
                    abyte1 = bytearrayoutputstream.toByteArray();
                }
                if(s1 == null)
                    s1 = guessManifestType(s);
                if(s1 == null)
                {
                    ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte1);
                    s1 = URLConnection.guessContentTypeFromStream(bytearrayinputstream);
                }
                if(s1 == null)
                    s1 = "input-stream/input-stream";
                if(s1.startsWith("application/java-vm"))
                {
                    String s2 = s.substring(0, s.length() - 6).replace('/', '.');
                    AppletClassEntry appletclassentry = loader.defineClassFromBytes(s2, abyte1, 0, abyte1.length);
                    hashtable.put(s, appletclassentry);
                } else
                if(!s1.equals("manifest/manifest") && !s1.equals("manifest/signature-bin"))
                    putLocalResource(makeResourceURL(url1, s), abyte1, s1);
            }
            Enumeration enumeration = jarverifierstream.getBlocks();
            jarverifierstream.getManifest();
            if(enumeration != null)
                signClasses(jarverifierstream.getVerifiedSignatures(), hashtable);
        }
        catch(IOException ioexception)
        {
            ioexception.printStackTrace();
        }
        finally
        {
            if(jarverifierstream != null)
                jarverifierstream.close();
        }
    }

    private void signClasses(Hashtable hashtable, Hashtable hashtable1)
    {
        TrustManager trustmanager = TrustManager.getTrustManager();
        if(hashtable == null || trustmanager == null)
            return;
        for(Enumeration enumeration = hashtable.keys(); enumeration.hasMoreElements();)
        {
            String s = (String)enumeration.nextElement();
            Vector vector = (Vector)hashtable.get(s);
            Object obj = hashtable1.get(s);
            Identity aidentity[] = new Identity[vector.size()];
            vector.copyInto(aidentity);
            java.security.Certificate acertificate[][] = new java.security.Certificate[aidentity.length][];
            for(int i = 0; i < aidentity.length; i++)
                acertificate[i] = aidentity[i].certificates();

            AppletClassEntry appletclassentry;
            if(trustmanager.isTrustedFor(acertificate, "software", new Date()))
            {
                if((obj instanceof AppletClassEntry) && (appletclassentry = (AppletClassEntry)loader.rawClasses.get(obj)) != null && appletclassentry == obj)
                    appletclassentry.ids = aidentity;
            } else
            {
                loader.rawClasses.clear();
                hashtable1.clear();
                return;
            }
        }

    }

    String guessManifestType(String s)
    {
        s = s.toUpperCase();
        if(s.startsWith("/"))
            s = s.substring(1);
        if(!s.startsWith("META-INF/"))
            return null;
        if(s.equalsIgnoreCase("META-INF/MANIFEST.MF"))
            return "manifest/manifest";
        if(s.endsWith("DSA") || s.endsWith("PK7") || s.endsWith("PGP"))
            return "manifest/signature-bin";
        if(s.endsWith("SF"))
            return "manifest/signature-asc";
        else
            return null;
    }

    public AudioClip getAudioClip(URL url)
    {
        return new AppletAudioClip(url);
    }

    public Image getImage(URL url)
    {
        return Toolkit.getDefaultToolkit().createImage(new URLImageSource(url));
    }

    private void putLocalResource(URL url, byte abyte0[], String s)
    {
        resourceHash.put(url, abyte0);
        mimeHash.put(url, s);
    }

    public URL getResourceAsURL(String s)
        throws MalformedURLException
    {
        synchronized(jarsLoaded)
        {
            for(Enumeration enumeration = jarsLoaded.keys(); enumeration.hasMoreElements();)
            {
                URL url1 = (URL)enumeration.nextElement();
                URL url2 = makeResourceURL(url1, s);
                if(getResourceBytes(url2) != null)
                {
                    URL url = url2;
                    return url;
                }
            }

        }
        return null;
    }

    public byte[] getResourceBytes(URL url)
    {
        return (byte[])resourceHash.get(url);
    }

    public String getResourceType(URL url)
    {
        return (String)mimeHash.get(url);
    }

    private URL makeResourceURL(URL url, String s)
        throws MalformedURLException
    {
        return new URL("appletresource:/" + loader.base + "!" + url + "!" + s);
    }

    private Hashtable jarsLoaded;
    private Hashtable resourceHash;
    private Hashtable mimeHash;
    AppletClassLoader loader;
}
