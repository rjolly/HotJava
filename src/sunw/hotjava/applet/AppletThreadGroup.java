// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletThreadGroup.java

package sunw.hotjava.applet;


public class AppletThreadGroup extends ThreadGroup
{

    public AppletThreadGroup(String s, int i)
    {
        this(getAppletsGroup(), s, i);
    }

    public AppletThreadGroup(String s)
    {
        this(getAppletsGroup(), s, 4);
    }

    private AppletThreadGroup(ThreadGroup threadgroup, String s, int i)
    {
        super(threadgroup, s);
        setMaxPriority(i);
    }

    private static synchronized ThreadGroup getAppletsGroup()
    {
        if(appletsGroup == null || appletsGroup.isDestroyed())
            appletsGroup = new ThreadGroup("applets");
        return appletsGroup;
    }

    public static void letAppletsFinish(int i)
    {
        if(appletsGroup == null)
            return;
        if(i <= 0)
            return;
        long l = System.currentTimeMillis() + (long)i;
        Thread athread[] = new Thread[appletsGroup.activeCount()];
        appletsGroup.enumerate(athread);
        for(int j = 0; j < athread.length; j++)
            if(athread[j] != null)
            {
                long l1 = l - System.currentTimeMillis();
                if(l1 <= 0L)
                    return;
                try
                {
                    athread[j].join(l1);
                }
                catch(InterruptedException _ex) { }
            }

    }

    private static ThreadGroup appletsGroup = null;

}
