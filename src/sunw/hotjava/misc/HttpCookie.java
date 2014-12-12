// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HttpCookie.java

package sunw.hotjava.misc;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Date;
import java.util.StringTokenizer;

// Referenced classes of package sunw.hotjava.misc:
//            HJBProperties, RfcDateParser

public class HttpCookie
{

    public HttpCookie(String s)
    {
        isSecure = false;
        parseCookieString(s);
    }

    public HttpCookie(Date date, String s, String s1, String s2, boolean flag)
    {
        isSecure = false;
        expirationDate = date;
        nameAndValue = s;
        path = s1;
        domain = s2;
        isSecure = flag;
    }

    public HttpCookie(URL url, String s)
    {
        isSecure = false;
        parseCookieString(s);
        applyDefaults(url);
    }

    private void applyDefaults(URL url)
    {
        if(domain == null)
            domain = url.getHost() + ":" + (url.getPort() != -1 ? url.getPort() : 80);
        if(path == null)
        {
            path = url.getFile();
            int i = path.lastIndexOf("/");
            if(i > -1)
                path = path.substring(0, i);
        }
    }

    private void parseCookieString(String s)
    {
        StringTokenizer stringtokenizer = new StringTokenizer(s, ";");
        if(!stringtokenizer.hasMoreTokens())
        {
            nameAndValue = "=";
            return;
        }
        nameAndValue = stringtokenizer.nextToken().trim();
        while(stringtokenizer.hasMoreTokens()) 
        {
            String s1 = stringtokenizer.nextToken().trim();
            if(s1.equalsIgnoreCase("secure"))
            {
                isSecure = true;
            } else
            {
                int i = s1.indexOf("=");
                if(i >= 0)
                {
                    String s2 = s1.substring(0, i);
                    String s3 = s1.substring(i + 1);
                    if(s2.equalsIgnoreCase("path"))
                        path = s3;
                    else
                    if(s2.equalsIgnoreCase("domain"))
                    {
                        if(s3.indexOf(".") == 0)
                            domain = s3.substring(1);
                        else
                            domain = s3;
                    } else
                    if(s2.equalsIgnoreCase("expires"))
                        expirationDate = parseExpireDate(s3);
                }
            }
        }
    }

    private Date getDefaultExpiration()
    {
        if(!defaultSet)
        {
            Field field = null;
            try
            {
                Class class1 = Class.forName("sunw.hotjava.misc.Globals");
                field = class1.getDeclaredField("beanProps");
            }
            catch(NoSuchFieldException _ex) { }
            catch(ClassNotFoundException _ex)
            {
                System.out.println("$$ Ann : HttpCookie: Class not found!");
            }
            if(field != null)
                try
                {
                    HJBProperties hjbproperties = HJBProperties.getHJBProperties((String)field.get(null));
                    defExprTime = hjbproperties.getInteger("hotjava.cookies.default.expiration");
                }
                catch(IllegalAccessException _ex) { }
            defaultSet = true;
        }
        return new Date(System.currentTimeMillis() + defExprTime);
    }

    public String getNameValue()
    {
        return nameAndValue;
    }

    public String getName()
    {
        if(nameAndValue == null)
            return "=";
        int i = nameAndValue.indexOf("=");
        if(i < 0)
            return "=";
        else
            return nameAndValue.substring(0, i);
    }

    public String getDomain()
    {
        return domain;
    }

    public String getPath()
    {
        return path;
    }

    public Date getExpirationDate()
    {
        return expirationDate;
    }

    public boolean hasExpired()
    {
        if(expirationDate == null)
            return false;
        return expirationDate.getTime() <= System.currentTimeMillis();
    }

    public boolean isSaveable()
    {
        return expirationDate != null && expirationDate.getTime() > System.currentTimeMillis();
    }

    public boolean isSaveableInMemory()
    {
        return expirationDate == null || expirationDate != null && expirationDate.getTime() > System.currentTimeMillis();
    }

    public boolean isSecure()
    {
        return isSecure;
    }

    private Date parseExpireDate(String s)
    {
        RfcDateParser rfcdateparser = new RfcDateParser(s);
        Date date = rfcdateparser.getDate();
        if(date == null)
            date = getDefaultExpiration();
        return date;
    }

    public String toString()
    {
        String s = nameAndValue != null ? nameAndValue : "=";
        if(expirationDate != null)
            s = s + "; expires=" + expirationDate;
        if(path != null)
            s = s + "; path=" + path;
        if(domain != null)
            s = s + "; domain=" + domain;
        if(isSecure)
            s = s + "; secure";
        return s;
    }

    private Date expirationDate;
    private String nameAndValue;
    private String path;
    private String domain;
    private boolean isSecure;
    private static boolean defaultSet;
    private static long defExprTime;
}
