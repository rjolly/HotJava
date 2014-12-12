// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CookieJar.java

package sunw.hotjava.bean;

import java.beans.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import sun.net.www.protocol.http.HttpURLConnection;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.misc.HttpCookie;

// Referenced classes of package sunw.hotjava.bean:
//            CookieJarInterface

public class CookieJar
    implements CookieJarInterface
{

    public CookieJar()
    {
        if(!initialized)
        {
            vceListeners = new VetoableChangeSupport(this);
            cookieJar = new Hashtable();
            initialized = true;
        }
    }

    public void recordAnyCookies(URLConnection urlconnection)
    {
        if(!(urlconnection instanceof HttpURLConnection))
            return;
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        String s = hjbproperties.getProperty("hotjava.cookie.acceptpolicy");
        if(s == null || s.equals("none"))
            return;
        HttpURLConnection httpurlconnection = (HttpURLConnection)urlconnection;
        String s1;
        for(int i = 1; (s1 = httpurlconnection.getHeaderFieldKey(i)) != null; i++)
            if(s1.equalsIgnoreCase("set-cookie"))
            {
                String s2 = httpurlconnection.getHeaderField(i);
                recordCookie(httpurlconnection, s2);
            }

    }

    public void recordCookie(HttpURLConnection httpurlconnection, String s)
    {
        HttpCookie httpcookie = new HttpCookie(httpurlconnection.getURL(), s);
        String as[] = {
            "com", "edu", "net", "org", "gov", "mil", "int"
        };
        String s1 = httpcookie.getDomain();
        if(s1 == null)
            return;
        int i = s1.indexOf(':');
        if(i != -1)
        {
            int j;
            try
            {
                j = Integer.valueOf(s1.substring(i + 1, s1.length())).intValue();
            }
            catch(Exception _ex)
            {
                return;
            }
            int k = httpurlconnection.getURL().getPort();
            k = k != -1 ? k : 80;
            j = j != -1 ? j : 80;
            if(j != k)
                return;
            s1 = s1.substring(0, i);
        }
        s1.toLowerCase();
        String s2 = httpurlconnection.getURL().getHost();
        s2.toLowerCase();
        boolean flag = s2.equals(s1);
        if(!flag && s2.endsWith(s1))
        {
            int l = 2;
            for(int i1 = 0; i1 < as.length; i1++)
                if(s1.endsWith(as[i1]))
                    l = 1;

            int j1;
            for(j1 = s1.length(); j1 > 0 && l > 0; l--)
                j1 = s1.lastIndexOf('.', j1 - 1);

            if(j1 > 0)
                flag = true;
        }
        if(flag)
            recordCookie(httpcookie);
    }

    public void recordCookie(HttpCookie httpcookie)
    {
        if(!checkIfCookieOK(httpcookie))
            return;
        synchronized(cookieJar)
        {
            String s = httpcookie.getDomain().toLowerCase();
            Vector vector = (Vector)cookieJar.get(s);
            if(vector == null)
                vector = new Vector();
            addOrReplaceCookie(vector, httpcookie);
            cookieJar.put(s, vector);
        }
    }

    public boolean checkIfCookieOK(HttpCookie httpcookie)
    {
        try
        {
            if(vceListeners != null)
                vceListeners.fireVetoableChange("cookie", null, httpcookie);
        }
        catch(PropertyVetoException _ex)
        {
            return false;
        }
        return true;
    }

    private void addOrReplaceCookie(Vector vector, HttpCookie httpcookie)
    {
        int i = vector.size();
        String s = httpcookie.getPath();
        String s1 = httpcookie.getName();
        HttpCookie httpcookie1 = null;
        int j = -1;
        for(int k = 0; k < i; k++)
        {
            HttpCookie httpcookie2 = (HttpCookie)vector.elementAt(k);
            String s2 = httpcookie2.getPath();
            if(!s.equals(s2))
                continue;
            String s3 = httpcookie2.getName();
            if(!s1.equals(s3))
                continue;
            httpcookie1 = httpcookie2;
            j = k;
            break;
        }

        if(httpcookie1 != null)
            if(httpcookie.isSaveableInMemory())
            {
                vector.setElementAt(httpcookie, j);
                return;
            } else
            {
                vector.removeElementAt(j);
                return;
            }
        if(httpcookie.isSaveableInMemory())
            vector.addElement(httpcookie);
    }

    public void applyRelevantCookies(URL url, URLConnection urlconnection)
    {
        if(!(urlconnection instanceof HttpURLConnection))
            return;
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        String s = hjbproperties.getProperty("hotjava.cookie.acceptpolicy");
        if(s == null || s.equals("none"))
        {
            return;
        } else
        {
            HttpURLConnection httpurlconnection = (HttpURLConnection)urlconnection;
            applyCookiesForHost(url, httpurlconnection);
            return;
        }
    }

    private void applyCookiesForHost(URL url, HttpURLConnection httpurlconnection)
    {
        String s = null;
        Vector vector = getAllRelevantCookies(url);
        if(vector != null)
        {
            for(Enumeration enumeration = vector.elements(); enumeration.hasMoreElements();)
            {
                HttpCookie httpcookie = (HttpCookie)enumeration.nextElement();
                if(s == null)
                    s = httpcookie.getNameValue();
                else
                    s = s + "; " + httpcookie.getNameValue();
            }

            if(s != null)
                httpurlconnection.setRequestProperty("Cookie", s);
        }
    }

    private Vector getAllRelevantCookies(URL url)
    {
        String s = url.getHost();
        Vector vector = getSubsetRelevantCookies(s, url);
        int i;
        while((i = s.indexOf('.', 1)) >= 0) 
        {
            s = s.substring(i + 1);
            Vector vector1 = getSubsetRelevantCookies(s, url);
            if(vector1 != null)
            {
                for(Enumeration enumeration = vector1.elements(); enumeration.hasMoreElements(); vector.addElement(enumeration.nextElement()))
                    if(vector == null)
                        vector = new Vector(2);

            }
        }
        return vector;
    }

    private Vector getSubsetRelevantCookies(String s, URL url)
    {
        Vector vector = (Vector)cookieJar.get(s);
        Vector vector1 = (Vector)cookieJar.get(s + ":" + (url.getPort() != -1 ? url.getPort() : 80));
        if(vector1 != null)
        {
            if(vector == null)
                vector = new Vector(10);
            for(Enumeration enumeration = vector1.elements(); enumeration.hasMoreElements(); vector.addElement(enumeration.nextElement()));
        }
        if(vector == null)
            return null;
        String s1 = url.getFile();
        int i = s1.indexOf('?');
        if(i > 0)
            s1 = s1.substring(0, i);
        Enumeration enumeration1 = vector.elements();
        Vector vector2 = new Vector(10);
        while(enumeration1.hasMoreElements()) 
        {
            HttpCookie httpcookie = (HttpCookie)enumeration1.nextElement();
            String s2 = httpcookie.getPath();
            if(s1.startsWith(s2) && !httpcookie.hasExpired())
                vector2.addElement(httpcookie);
        }
        if(vector2.size() > 1)
        {
            for(int j = 0; j < vector2.size() - 1; j++)
            {
                HttpCookie httpcookie1 = (HttpCookie)vector2.elementAt(j);
                String s3 = httpcookie1.getPath();
                if(!s3.endsWith("/"))
                    s3 = s3 + "/";
                for(int k = j + 1; k < vector2.size(); k++)
                {
                    HttpCookie httpcookie2 = (HttpCookie)vector2.elementAt(k);
                    String s4 = httpcookie2.getPath();
                    if(!s4.endsWith("/"))
                        s4 = s4 + "/";
                    int l = 0;
                    for(int i1 = -1; (i1 = s3.indexOf('/', i1 + 1)) != -1;)
                        l++;

                    int j1 = -1;
                    int k1;
                    for(k1 = 0; (j1 = s4.indexOf('/', j1 + 1)) != -1; k1++);
                    if(k1 > l)
                    {
                        vector2.setElementAt(httpcookie1, k);
                        vector2.setElementAt(httpcookie2, j);
                        httpcookie1 = httpcookie2;
                        s3 = s4;
                    }
                }

            }

        }
        return vector2;
    }

    private void saveCookiesToStream(PrintWriter printwriter)
    {
        for(Enumeration enumeration = cookieJar.elements(); enumeration.hasMoreElements();)
        {
            Vector vector = (Vector)enumeration.nextElement();
            for(Enumeration enumeration1 = vector.elements(); enumeration1.hasMoreElements();)
            {
                HttpCookie httpcookie = (HttpCookie)enumeration1.nextElement();
                if(httpcookie.getExpirationDate() != null)
                    if(httpcookie.isSaveable())
                        printwriter.println(httpcookie);
                    else
                        vector.removeElement(httpcookie);
            }

        }

        printwriter.print("");
    }

    public void addToCookieJar(HttpCookie ahttpcookie[])
    {
        if(ahttpcookie != null)
        {
            for(int i = 0; i < ahttpcookie.length; i++)
                recordCookie(ahttpcookie[i]);

        }
    }

    public void addToCookieJar(String s, URL url)
    {
        recordCookie(new HttpCookie(url, s));
    }

    public void loadCookieJarFromFile(String s)
    {
        try
        {
            FileReader filereader = new FileReader(s);
            BufferedReader bufferedreader = new BufferedReader(filereader);
            String s1;
            try
            {
                while((s1 = bufferedreader.readLine()) != null) 
                {
                    HttpCookie httpcookie = new HttpCookie(s1);
                    recordCookie(httpcookie);
                }
            }
            finally
            {
                bufferedreader.close();
            }
            return;
        }
        catch(IOException _ex)
        {
            return;
        }
    }

    public void saveCookieJarToFile(String s)
    {
        try
        {
            FileWriter filewriter = new FileWriter(s);
            PrintWriter printwriter = new PrintWriter(filewriter, false);
            try
            {
                saveCookiesToStream(printwriter);
            }
            finally
            {
                printwriter.close();
            }
            return;
        }
        catch(IOException ioexception)
        {
            System.err.println("Saving cookies failed " + ioexception.getMessage());
        }
    }

    public HttpCookie[] getAllCookies()
    {
        Vector vector = new Vector();
        Hashtable hashtable = (Hashtable)cookieJar.clone();
        synchronized(hashtable)
        {
            for(Enumeration enumeration = hashtable.elements(); enumeration.hasMoreElements();)
            {
                Vector vector1 = (Vector)enumeration.nextElement();
                for(int j = 0; j < vector1.size(); j++)
                {
                    HttpCookie httpcookie = (HttpCookie)vector1.elementAt(j);
                    vector.addElement(httpcookie);
                }

            }

        }
        HttpCookie ahttpcookie[] = new HttpCookie[vector.size()];
        for(int i = 0; i < vector.size(); i++)
            ahttpcookie[i] = (HttpCookie)vector.elementAt(i);

        return ahttpcookie;
    }

    public HttpCookie[] getCookiesForURL(URL url)
    {
        Vector vector = getAllRelevantCookies(url);
        if(vector == null)
            return null;
        int i = 0;
        HttpCookie ahttpcookie[] = new HttpCookie[vector.size()];
        for(Enumeration enumeration = vector.elements(); enumeration.hasMoreElements();)
            ahttpcookie[i++] = (HttpCookie)enumeration.nextElement();

        return ahttpcookie;
    }

    public void setCookieDisable(boolean flag)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        hjbproperties.put("hotjava.enableCookies", flag ? "false" : "true");
    }

    public void discardAllCookies()
    {
        try
        {
            if(vceListeners != null)
                vceListeners.fireVetoableChange("cookie", null, null);
        }
        catch(PropertyVetoException _ex) { }
        cookieJar.clear();
    }

    public void purgeExpiredCookies()
    {
        for(Enumeration enumeration = cookieJar.elements(); enumeration.hasMoreElements();)
        {
            Vector vector = (Vector)enumeration.nextElement();
            for(Enumeration enumeration1 = vector.elements(); enumeration1.hasMoreElements();)
            {
                HttpCookie httpcookie = (HttpCookie)enumeration1.nextElement();
                if(httpcookie.hasExpired())
                    vector.removeElement(httpcookie);
            }

        }

    }

    public void addVetoableChangeListener(VetoableChangeListener vetoablechangelistener)
    {
        vceListeners.addVetoableChangeListener(vetoablechangelistener);
    }

    public void removeVetoableChangeListener(VetoableChangeListener vetoablechangelistener)
    {
        vceListeners.removeVetoableChangeListener(vetoablechangelistener);
    }

    private boolean shouldRejectCookie(HttpCookie httpcookie)
    {
        return false;
    }

    private VetoableChangeSupport vceListeners;
    private int listenerNum;
    private static Hashtable cookieJar;
    private static boolean initialized;
}
