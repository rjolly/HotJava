// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GC.java

package sunw.hotjava.misc;

import java.io.PrintStream;

// Referenced classes of package sunw.hotjava.misc:
//            Globals, HJBProperties

public final class GC extends Thread
{

    GC()
    {
        verbose = HJBProperties.getHJBProperties("beanPropertiesKey").getBoolean("gc.verbose");
    }

    public synchronized void run()
    {
        long l = Runtime.getRuntime().freeMemory();
        boolean flag = true;
        do
        {
            long l1;
            long l3;
            do
            {
                int k;
                do
                {
                    long l2;
                    int j;
                    do
                    {
                        int i;
                        do
                        {
                            try
                            {
                                if(!flag)
                                    wait();
                                flag = false;
                            }
                            catch(InterruptedException _ex)
                            {
                                return;
                            }
                            l1 = Runtime.getRuntime().totalMemory();
                            l2 = Runtime.getRuntime().freeMemory();
                            i = (int)((100L * l2) / l1);
                        } while(i > 50);
                        j = (int)(100L - (100L * l2) / l);
                    } while(j < 50);
                    k = (int)((100L * (l - l2)) / l1);
                } while(k < 5);
                l3 = System.currentTimeMillis();
                Runtime.getRuntime().gc();
                l = Runtime.getRuntime().freeMemory();
            } while(!verbose);
            l3 = System.currentTimeMillis() - l3;
            System.out.println("[GC: total=" + l1 + ", free=" + l + " (" + (l * 100L) / l1 + "%)" + ", time=" + l3 + "ms]");
        } while(true);
    }

    public static synchronized void collect()
    {
    }

    boolean verbose;
    final int minPercentFree = 50;
    final int minAllocatedFromFree = 50;
    final int minAllocatedFromTotal = 5;
    private static GC gc;
}
