// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProgressEntry.java

package sun.net;


public class ProgressEntry
{

    public ProgressEntry(String s, String s1)
    {
        connected = false;
        label = s;
        type = 4;
        need = 0;
        setType(s, s1);
    }

    public void setType(String s, String s1)
    {
        if(s1 != null)
            if(s1.startsWith("image"))
                type = 1;
            else
            if(s1.startsWith("audio"))
                type = 3;
            else
            if(s1.equals("application/java-vm"))
                type = 2;
            else
            if(s1.startsWith("text/html"))
                type = 0;
        if(type == 4)
        {
            if(s.endsWith(".html") || s.endsWith("/") || s.endsWith(".htm"))
            {
                type = 0;
                return;
            }
            if(s.endsWith(".class"))
            {
                type = 2;
                return;
            }
            if(s.endsWith(".gif") || s.endsWith(".xbm") || s.endsWith(".jpeg") || s.endsWith(".jpg") || s.endsWith(".jfif"))
            {
                type = 1;
                return;
            }
            if(s.endsWith(".au"))
                type = 3;
        }
    }

    public void update(int i, int j)
    {
        if(need == 0)
            need = j;
        read = i;
    }

    public synchronized boolean connected()
    {
        if(!connected)
        {
            connected = true;
            return false;
        } else
        {
            return true;
        }
    }

    public static final int HTML = 0;
    public static final int IMAGE = 1;
    public static final int CLASS = 2;
    public static final int AUDIO = 3;
    public static final int OTHER = 4;
    public int need;
    public int read;
    public int what;
    public int index;
    public boolean connected;
    public String label;
    public int type;
}
