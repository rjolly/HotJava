// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RequestProcessor.java

package sunw.hotjava.misc;

import java.io.PrintStream;

public class RequestProcessor
    implements Runnable
{
    public static abstract class Request
    {

        public abstract void execute();

        public Request()
        {
        }
    }

    private static final class RequestNode
    {

        Request request;
        long when;
        RequestNode next;

        RequestNode()
        {
        }
    }


    public boolean isCurrentThread()
    {
        return Thread.currentThread() == dispatcher;
    }

    public void postRequest(Request request)
    {
        postRequest(request, 0);
    }

    public void postRequest(Request request, int i)
    {
        long l = (long)i + System.currentTimeMillis();
        synchronized(lock)
        {
            RequestNode requestnode = nodeSave;
            nodeSave = null;
            if(requestnode == null)
                requestnode = new RequestNode();
            requestnode.request = request;
            requestnode.when = l;
            if(requestQueue == null)
                requestQueue = requestnode;
            else
            if(requestQueue.when > requestnode.when)
            {
                requestnode.next = requestQueue;
                requestQueue = requestnode;
            } else
            {
                RequestNode requestnode1;
                for(requestnode1 = requestQueue; requestnode1.next != null && requestnode1.next.when <= requestnode.when; requestnode1 = requestnode1.next);
                requestnode.next = requestnode1.next;
                requestnode1.next = requestnode;
            }
            lock.notifyAll();
        }
    }

    public void run()
    {
        for(long l = System.currentTimeMillis() + restartInterval; System.currentTimeMillis() < l;)
        {
            RequestNode requestnode = null;
            try
            {
                synchronized(lock)
                {
                    while(requestQueue == null) 
                    {
                        long l1 = l - System.currentTimeMillis();
                        if(l1 > 0L)
                            lock.wait(l1);
                        if(System.currentTimeMillis() > l)
                            throw new InterruptedException();
                    }
                    for(long l2 = System.currentTimeMillis(); requestQueue.when > l2; l2 = System.currentTimeMillis())
                        lock.wait(requestQueue.when - l2);

                    requestnode = requestQueue;
                    requestQueue = requestQueue.next;
                }
                requestnode.request.execute();
            }
            catch(InterruptedException _ex) { }
            catch(Throwable throwable)
            {
                System.out.println("Exception on request processor: <" + throwable + ">");
                throwable.printStackTrace();
            }
            finally
            {
                if(requestnode != null)
                {
                    requestnode.request = null;
                    requestnode.next = null;
                    nodeSave = requestnode;
                }
            }
        }

        synchronized(this)
        {
            if(!shutdown)
            {
                String s = dispatcher.getName();
                int i = dispatcher.getPriority();
                dispatcher = new Thread(this, s);
                dispatcher.setPriority(i);
                dispatcher.start();
            }
        }
    }

    public synchronized void shutdown()
    {
        shutdown = true;
        dispatcher.interrupt();
    }

    public RequestProcessor(String s)
    {
        this(s, 0x493e0L);
    }

    public RequestProcessor(String s, long l)
    {
        shutdown = false;
        lock = new Object();
        restartInterval = l;
        dispatcher = new Thread(this, s);
        dispatcher.setPriority(7);
        dispatcher.start();
    }

    public static RequestProcessor getHJBeanQueue()
    {
        if(hjBeanQueue == null)
            hjBeanQueue = new RequestProcessor("HotJava bean request queue");
        return hjBeanQueue;
    }

    private RequestNode requestQueue;
    private RequestNode nodeSave;
    private Thread dispatcher;
    private boolean shutdown;
    private long restartInterval;
    private final Object lock;
    private static final long defaultRestartInterval = 0x493e0L;
    private static RequestProcessor hjBeanQueue = null;

}
