// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NfsURL.java

package com.sun.nfs;

import java.net.MalformedURLException;

public class NfsURL
{

    public NfsURL(String s)
        throws MalformedURLException
    {
        pub = true;
        int l = s.length();
        s = s.trim();
        url = s;
        int i = s.indexOf(':');
        if(i < 0)
            throw new MalformedURLException("colon expected");
        protocol = s.substring(0, i);
        i++;
        int j;
        if(s.regionMatches(i, "//", 0, 2))
        {
            i += 2;
            j = s.indexOf('/', i);
            if(j < 0)
                j = l;
            location = s.substring(0, j);
            int k = s.indexOf(':', i);
            if(k > 0 && k < j)
            {
                byte abyte0[] = s.substring(k + 1, j).toLowerCase().getBytes();
                for(int i1 = 0; i1 < abyte0.length; i1++)
                    if(abyte0[i1] >= 48 && abyte0[i1] <= 57)
                        port = port * 10 + (abyte0[i1] - 48);
                    else
                        switch(abyte0[i1])
                        {
                        case 118: // 'v'
                            version = abyte0[++i1] - 48;
                            break;

                        case 116: // 't'
                            proto = "tcp";
                            break;

                        case 117: // 'u'
                            proto = "udp";
                            break;

                        case 119: // 'w'
                            pub = true;
                            break;

                        case 109: // 'm'
                            pub = false;
                            break;

                        default:
                            throw new MalformedURLException("invalid port number");
                        }

            } else
            {
                k = j;
            }
            if(i < k)
                host = s.substring(i, k);
        } else
        {
            j = i;
        }
        if(j < l)
            file = s.substring(j + 1, l);
    }

    public String getProtocol()
    {
        return protocol;
    }

    public String getLocation()
    {
        return location;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public String getFile()
    {
        return file;
    }

    int getVersion()
    {
        return version;
    }

    String getProto()
    {
        return proto;
    }

    boolean getPub()
    {
        return pub;
    }

    public String toString()
    {
        String s = getProtocol() + ":";
        if(host != null)
            s = s + "//" + host;
        if(port > 0)
            s = s + ":" + port;
        if(file != null)
            s = s + "/" + file;
        return s;
    }

    private String url;
    private String protocol;
    private String host;
    private String location;
    private int port;
    private String file;
    private int version;
    private String proto;
    private boolean pub;
}
