// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   XFurl.java

package com.sun.xfile;

import java.net.MalformedURLException;

class XFurl
{

    XFurl(String s)
        throws MalformedURLException
    {
        s = s.trim();
        url = s;
        int k = s.length();
        int i = s.indexOf(':');
        if(i < 0)
            throw new MalformedURLException("colon expected");
        protocol = s.substring(0, i);
        int j = i;
        i++;
        if(s.regionMatches(i, "//", 0, 2))
        {
            i += 2;
            j = s.indexOf('/', i);
            if(j < 0)
                j = k;
            location = s.substring(i, j);
        }
        for(path = j >= k ? "" : s.substring(j + 1, k); path.endsWith("/"); path = path.substring(0, path.length() - 1));
    }

    XFurl(XFurl xfurl, String s)
        throws MalformedURLException
    {
        protocol = xfurl.getProtocol();
        location = xfurl.getLocation();
        path = xfurl.getPath();
        s = s.trim();
        if(s.indexOf("://") > 0)
        {
            url = s;
            XFurl xfurl1 = new XFurl(s);
            protocol = xfurl1.getProtocol();
            location = xfurl1.getLocation();
            path = xfurl1.getPath();
            return;
        }
        if(s.startsWith("/"))
        {
            path = s.substring(1);
            return;
        }
        String s1 = xfurl.getPath();
        int i = s.length();
        int k;
        for(int j = 0; j <= i; j = k + 1)
        {
            k = s.indexOf("/", j);
            if(k < 0)
                k = i;
            String s2 = s.substring(j, k);
            if(!s2.equals(".") && !s2.equals(""))
                if(s2.equals(".."))
                {
                    int l = s1.lastIndexOf("/");
                    s1 = l >= 0 ? s1.substring(0, l) : "";
                } else
                if(s1.equals(""))
                    s1 = s2;
                else
                    s1 = s1 + "/" + s2;
        }

        path = s1;
    }

    String getProtocol()
    {
        return protocol;
    }

    String getLocation()
    {
        return location;
    }

    String getPath()
    {
        return path;
    }

    String getParent()
    {
        if(path.equals(""))
            return null;
        String s = protocol + ":";
        if(location != null)
            s = s + "//" + location;
        int i = path.lastIndexOf('/');
        if(i >= 0)
            s = s + "/" + path.substring(0, i);
        return s;
    }

    String getName()
    {
        int i = path.lastIndexOf('/');
        if(i < 0)
            return path;
        else
            return path.substring(i + 1);
    }

    public String toString()
    {
        String s = protocol + ":";
        if(location != null)
            s = s + "//" + location;
        if(path != null)
            s = s + "/" + path;
        return s;
    }

    private String url;
    private String protocol;
    private String location;
    private String path;
}
