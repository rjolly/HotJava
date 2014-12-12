// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   URLPool.java

package sunw.hotjava.bean;

import java.io.*;
import java.util.*;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.bean:
//            PoolEntry, URLPoolEvent, URLPoolListener, URLPooler

public class URLPool
    implements URLPooler, Runnable
{

    public URLPool()
    {
        if(!initialized)
            initURLPool();
    }

    private void initURLPool()
    {
        initialized = true;
        urlpoolListeners = null;
        pool = new Hashtable();
        expire = 0x240c8400L;
    }

    public PoolEntry[] getURLPoolEntries()
    {
        int i = 0;
        Hashtable hashtable = pool;
        PoolEntry apoolentry[];
        synchronized(hashtable)
        {
            Hashtable hashtable1 = (Hashtable)pool.clone();
            apoolentry = new PoolEntry[hashtable1.size()];
            for(Enumeration enumeration = hashtable1.keys(); enumeration.hasMoreElements();)
            {
                String s = (String)enumeration.nextElement();
                Date date = (Date)hashtable1.get(s);
                apoolentry[i++] = new PoolEntry(date, s);
            }

        }
        return apoolentry;
    }

    public void addToURLPool(PoolEntry apoolentry[])
    {
        if(apoolentry != null)
        {
            for(int i = 0; i < apoolentry.length; i++)
                add(apoolentry[i].url, apoolentry[i].lastTouched);

        }
    }

    public boolean createURLPoolFromFile(String s)
    {
        new Hashtable();
        System.err.println("Loading: " + s);
        if(s == null)
            return false;
        try
        {
            BufferedInputStream bufferedinputstream = null;
            try
            {
                bufferedinputstream = new BufferedInputStream(new FileInputStream(s));
                char ac[] = new char[512];
                int i = bufferedinputstream.read();
                long l = (new Date()).getTime();
                while(i > 0) 
                {
                    while(i == 32 || i == 9 || i == 10 || i == 13) 
                        i = bufferedinputstream.read();
                    if(i == 35)
                    {
                        for(; i != -1 && i != 10 && i != 13; i = bufferedinputstream.read());
                    } else
                    {
                        long l1 = 0L;
                        for(; i >= 48 && i <= 57; i = bufferedinputstream.read())
                            l1 = (l1 * 10L + (long)i) - 48L;

                        l1 *= 1000L;
                        for(; i == 32 || i == 9; i = bufferedinputstream.read());
                        int j = 0;
                        for(; i != -1 && i != 10 && i != 13; i = bufferedinputstream.read())
                            if(j < ac.length)
                                ac[j++] = (char)i;

                        if(j > 0 && l - l1 < expire)
                        {
                            char ac1[] = new char[j];
                            System.arraycopy(ac, 0, ac1, 0, j);
                            String s1 = new String(ac1, 0, j);
                            add(s1, new Date(l1));
                        }
                    }
                }
            }
            finally
            {
                if(bufferedinputstream != null)
                    bufferedinputstream.close();
            }
        }
        catch(FileNotFoundException _ex)
        {
            System.err.println("Failed to load: " + s);
            return false;
        }
        catch(IOException _ex)
        {
            System.err.println("Failed to load: " + s);
            return false;
        }
        return true;
    }

    public void add(String s)
    {
        Date date = new Date();
        add(s, date);
    }

    private void add(String s, Date date)
    {
        Date date1 = (Date)pool.put(s, date);
        if(date1 == null || !date1.equals(date))
            fireURLPoolEvent(new URLPoolEvent(0));
    }

    public void saveURLPoolToFile(String s)
    {
        if(s != null)
        {
            fname = s;
            Thread thread = new Thread(this, "SaveURLPool");
            thread.setPriority(3);
            thread.start();
        }
    }

    public Date get(String s)
    {
        return (Date)pool.get(s);
    }

    public void run()
    {
        System.err.println("Saving: " + fname);
        int i = 0;
        Hashtable hashtable = pool;
        String as[];
        synchronized(hashtable)
        {
            as = new String[hashtable.size()];
            for(Enumeration enumeration = hashtable.keys(); enumeration.hasMoreElements();)
            {
                String s = (String)enumeration.nextElement();
                as[i++] = s;
            }

        }
        try
        {
            PrintWriter printwriter = null;
            try
            {
                Date date = new Date();
                long l = date.getTime();
                printwriter = new PrintWriter(new BufferedWriter(new FileWriter(fname)), false);
                printwriter.println("# " + date);
                for(int j = 0; j < i; j++)
                {
                    String s1 = as[j];
                    Date date1 = (Date)hashtable.get(s1);
                    if(date1 != null)
                    {
                        long l1 = date1.getTime();
                        if(l - l1 < expire)
                        {
                            printwriter.print(l1 / 1000L);
                            printwriter.print(' ');
                            printwriter.println(s1);
                        } else
                        {
                            pool.remove(s1);
                        }
                    }
                }

            }
            finally
            {
                if(printwriter != null)
                    printwriter.close();
            }
            return;
        }
        catch(IOException _ex)
        {
            System.err.println("Failed to save: " + fname);
        }
    }

    public void setURLExpirationAmt(int i)
    {
        expire = 0x5265c00 * i;
    }

    public int getURLExpirationAmt()
    {
        return (int)(expire / 0x5265c00L);
    }

    public void discardAllURLs()
    {
        pool.clear();
        fireURLPoolEvent(new URLPoolEvent(1));
    }

    public void purgeExpiredURLs()
    {
        int i = 0;
        Hashtable hashtable = pool;
        String as[];
        synchronized(hashtable)
        {
            as = new String[hashtable.size()];
            for(Enumeration enumeration = hashtable.keys(); enumeration.hasMoreElements();)
            {
                String s = (String)enumeration.nextElement();
                as[i++] = s;
            }

        }
        Date date = new Date();
        long l = date.getTime();
        for(int j = 0; j < i; j++)
        {
            String s1 = as[j];
            Date date1 = (Date)hashtable.get(s1);
            if(date1 != null)
            {
                long l1 = date1.getTime();
                if(l - l1 >= expire)
                    pool.remove(s1);
            }
        }

    }

    public void addURLPoolListener(URLPoolListener urlpoollistener)
    {
        if(urlpoolListeners == null)
            urlpoolListeners = new Vector(2, 2);
        urlpoolListeners.addElement(urlpoollistener);
    }

    public void removeURLPoolListener(URLPoolListener urlpoollistener)
    {
        if(urlpoolListeners != null)
        {
            urlpoolListeners.removeElement(urlpoollistener);
            if(urlpoolListeners.size() == 0)
                urlpoolListeners = null;
        }
    }

    public void fireURLPoolEvent(URLPoolEvent urlpoolevent)
    {
        if(urlpoolListeners == null)
            return;
        int i = urlpoolListeners.size();
        for(int j = 0; j < i; j++)
        {
            URLPoolListener urlpoollistener = (URLPoolListener)urlpoolListeners.elementAt(j);
            urlpoollistener.urlPoolChanged(urlpoolevent);
        }

    }

    private static Hashtable pool;
    private static long expire;
    private static transient Vector urlpoolListeners;
    private static HJBProperties properties;
    private static boolean initialized;
    private static final int DEFAULT_EXPIRE = 7;
    private static final int MSPERDAY = 0x5265c00;
    private static String fname;
}
