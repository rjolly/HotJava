// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Attributes.java

package sunw.html;

import java.util.StringTokenizer;

public final class Attributes
    implements Cloneable
{

    public Attributes()
    {
        data = nodata;
    }

    public Attributes(String s, String s1)
    {
        data = nodata;
        append(s, s1);
    }

    public Attributes(String s)
    {
        data = nodata;
        String s1;
        int i;
        for(StringTokenizer stringtokenizer = new StringTokenizer(s, ","); stringtokenizer.hasMoreTokens(); append(s1.substring(0, i), s1.substring(i + 1)))
        {
            s1 = stringtokenizer.nextToken();
            i = s1.indexOf('=');
        }

    }

    public int length()
    {
        return len >> 1;
    }

    public String getName(int i)
    {
        return data[i << 1];
    }

    public String get(int i)
    {
        return data[(i << 1) + 1];
    }

    public synchronized String get(String s)
    {
        for(int i = 0; i < len; i += 2)
            if(data[i].equals(s))
                return data[i + 1];

        return null;
    }

    public synchronized void put(String s, String s1)
    {
        for(int i = 0; i < len; i += 2)
            if(data[i].equals(s))
            {
                if(cow)
                {
                    String as[] = new String[len];
                    System.arraycopy(data, 0, as, 0, len);
                    data = as;
                    cow = false;
                }
                data[i + 1] = s1;
                return;
            }

        append(s, s1);
    }

    public synchronized void append(String s, String s1)
    {
        if(len == data.length || cow)
        {
            String as[] = new String[len + 8];
            System.arraycopy(data, 0, as, 0, len);
            data = as;
            cow = false;
        }
        data[len++] = s;
        data[len++] = s1;
    }

    public synchronized Object clone()
    {
        try
        {
            Attributes attributes = (Attributes)super.clone();
            attributes.cow = cow = true;
            return attributes;
        }
        catch(CloneNotSupportedException _ex)
        {
            throw new InternalError();
        }
    }

    public synchronized String toString()
    {
        String s = "";
        for(int i = 0; i < len; i += 2)
            s = s + (i <= 0 ? "" : " ") + data[i] + "=\"" + data[i + 1] + "\"";

        return s;
    }

    private static String nodata[] = new String[0];
    private String data[];
    private int len;
    private boolean cow;

}
