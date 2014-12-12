// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ThreadCountApplet.java

package sunw.hotjava.applets;

import java.awt.*;
import sun.misc.MessageUtils;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.RaisedPanel;

// Referenced classes of package sunw.hotjava.applets:
//            HotJavaApplet

public class ThreadCountApplet extends HotJavaApplet
    implements Runnable
{

    public void init()
    {
        setLayout(new BorderLayout());
        properties.getFont("threadlist.listfont");
        count = new RaisedPanel();
        count.setLayout(new BorderLayout());
        add("Center", count);
        count.add("Center", threadCountLabel = new Label("", 0));
        rootThreadGroup = getRootThreadGroup();
    }

    public void start()
    {
        if(myThread != null && myThread.isAlive())
        {
            die();
            try
            {
                myThread.join();
            }
            catch(InterruptedException _ex) { }
        }
        myThread = new Thread(this);
        myThread.start();
    }

    public void stop()
    {
        if(myThread != null && myThread.isAlive())
            die();
        myThread = null;
    }

    ThreadGroup getRootThreadGroup()
    {
        ThreadGroup threadgroup;
        for(threadgroup = Thread.currentThread().getThreadGroup(); threadgroup.getParent() != null; threadgroup = threadgroup.getParent());
        return threadgroup;
    }

    void die()
    {
        dieNow = true;
    }

    void updateLabel(int i, int j)
    {
        String s = "threadlist.threadcount." + (i != 1 ? "plural" : "single");
        String s1 = properties.getPropertyReplace(s, String.valueOf(i));
        s = "threadlist.groupcount." + (j != 1 ? "plural" : "single");
        String s2 = properties.getPropertyReplace(s, String.valueOf(j));
        threadCountLabel.setText(s1 + s2);
        count.invalidate();
        count.validate();
    }

    public void run()
    {
        Thread thread = Thread.currentThread();
        thread.setName("ThreadCountApplet thread");
        for(dieNow = false; !dieNow;)
        {
            updateLabel(rootThreadGroup.activeCount(), rootThreadGroup.activeGroupCount());
            int i = properties.getInteger("threadlist.sleep", 2000);
            try
            {
                Thread.sleep(i);
            }
            catch(InterruptedException _ex) { }
        }

    }

    public ThreadCountApplet()
    {
        properties = HJBProperties.getHJBProperties("hjbrowser");
    }

    HJBProperties properties;
    static final String propName = "threadlist";
    static final MessageUtils mu = new MessageUtils();
    Thread myThread;
    boolean dieNow;
    Panel count;
    Label threadCountLabel;
    ThreadGroup rootThreadGroup;
    static final int defaultMsecSleep = 2000;

}
