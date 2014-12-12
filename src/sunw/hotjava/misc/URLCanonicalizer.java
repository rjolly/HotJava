// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   URLCanonicalizer.java

package sunw.hotjava.misc;


public class URLCanonicalizer
{

    public URLCanonicalizer()
    {
    }

    public String canonicalize(String s)
    {
        String s1 = s;
        if(s.startsWith("ftp."))
            s1 = "ftp://" + s;
        else
        if(s.startsWith("gopher."))
            s1 = "gopher://" + s;
        else
        if(s.startsWith("/"))
            s1 = "file:" + s;
        else
        if(!hasProtocolName(s))
        {
            if(isSimpleHostName(s))
                s = "www." + s + ".com";
            s1 = "http://" + s;
        }
        return s1;
    }

    public boolean hasProtocolName(String s)
    {
        int i = s.indexOf(':');
        if(i <= 0)
            return false;
        for(int j = 0; j < i; j++)
        {
            char c = s.charAt(j);
            if((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && c != '-')
                return false;
        }

        return true;
    }

    protected boolean isSimpleHostName(String s)
    {
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '-')
                return false;
        }

        return true;
    }
}
