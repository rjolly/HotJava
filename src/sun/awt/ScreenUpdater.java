// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ScreenUpdater.java

package sun.awt;


// Referenced classes of package sun.awt:
//            ScreenUpdaterEntry, UpdateClient

public class ScreenUpdater extends Thread
{

    private static ThreadGroup getScreenUpdaterThreadGroup()
    {
        ThreadGroup threadgroup;
        for(threadgroup = Thread.currentThread().getThreadGroup(); threadgroup.getParent() != null && threadgroup.getParent().getParent() != null; threadgroup = threadgroup.getParent());
        return threadgroup;
    }

    private ScreenUpdater()
    {
        super(getScreenUpdaterThreadGroup(), "Screen Updater");
        start();
    }

    private synchronized ScreenUpdaterEntry nextEntry()
        throws InterruptedException
    {
        do
        {
            while(first == null) 
                wait();
            long l = first.when - System.currentTimeMillis();
            if(l <= 0L)
            {
                ScreenUpdaterEntry screenupdaterentry = first;
                first = first.next;
                return screenupdaterentry;
            }
            wait(l);
        } while(true);
    }

    public void run()
    {
        do
            try
            {
                setPriority(4);
                ScreenUpdaterEntry screenupdaterentry = nextEntry();
                setPriority(6);
                try
                {
                    screenupdaterentry.client.updateClient(screenupdaterentry.arg);
                }
                catch(Throwable throwable)
                {
                    throwable.printStackTrace();
                }
                screenupdaterentry = null;
            }
            catch(InterruptedException _ex)
            {
                return;
            }
        while(true);
    }

    public void notify(UpdateClient updateclient)
    {
        notify(updateclient, 100L, null);
    }

    public synchronized void notify(UpdateClient updateclient, long l)
    {
        notify(updateclient, l, null);
    }

    public synchronized void notify(UpdateClient updateclient, long l, Object obj)
    {
        long l1 = System.currentTimeMillis() + l;
        long l2 = first == null ? -1L : first.when;
        if(first != null)
            if(first.client == updateclient && first.arg == obj)
            {
                if(l1 >= first.when)
                    return;
                first = first.next;
            } else
            {
                for(ScreenUpdaterEntry screenupdaterentry = first; screenupdaterentry.next != null; screenupdaterentry = screenupdaterentry.next)
                {
                    if(screenupdaterentry.next.client != updateclient || screenupdaterentry.next.arg != obj)
                        continue;
                    if(l1 >= screenupdaterentry.next.when)
                        return;
                    screenupdaterentry.next = screenupdaterentry.next.next;
                    break;
                }

            }
        if(first == null || first.when > l1)
        {
            first = new ScreenUpdaterEntry(updateclient, l1, obj, first);
        } else
        {
            ScreenUpdaterEntry screenupdaterentry1 = first;
            do
            {
                if(screenupdaterentry1.next == null || screenupdaterentry1.next.when > l1)
                {
                    screenupdaterentry1.next = new ScreenUpdaterEntry(updateclient, l1, obj, screenupdaterentry1.next);
                    break;
                }
                screenupdaterentry1 = screenupdaterentry1.next;
            } while(true);
        }
        if(l2 != first.when)
            super.notify();
    }

    public synchronized void removeClient(UpdateClient updateclient)
    {
        ScreenUpdaterEntry screenupdaterentry = first;
        ScreenUpdaterEntry screenupdaterentry1 = null;
        for(; screenupdaterentry != null; screenupdaterentry = screenupdaterentry.next)
            if(screenupdaterentry.client.equals(updateclient))
            {
                if(screenupdaterentry1 == null)
                    first = screenupdaterentry.next;
                else
                    screenupdaterentry1.next = screenupdaterentry.next;
            } else
            {
                screenupdaterentry1 = screenupdaterentry;
            }

    }

    private ScreenUpdaterEntry first;
    public static final ScreenUpdater updater = new ScreenUpdater();

}
