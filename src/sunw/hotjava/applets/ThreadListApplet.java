// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ThreadListApplet.java

package sunw.hotjava.applets;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.List;
import java.awt.Panel;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.RaisedPanel;
import sunw.hotjava.ui.UserLabel;
import sunw.hotjava.ui.UserTextButton;

// Referenced classes of package sunw.hotjava.applets:
//            HotJavaApplet

public class ThreadListApplet extends HotJavaApplet
    implements Runnable
{

    public void init()
    {
        setLayout(new BorderLayout());
        java.awt.Font font = HotJavaApplet.properties.getFont("threadlist.listfont");
        topPanel = new RaisedPanel();
        add("North", topPanel);
        topPanel.setLayout(new BorderLayout());
        Panel panel = new Panel();
        panel.setLayout(new BorderLayout());
        topPanel.add("North", panel);
        panel.add("South", new UserLabel("threadlist.legend"));
        Panel panel1 = new Panel();
        panel1.setLayout(new BorderLayout());
        add("Center", panel1);
        panel1.add(threadList = new List(8, false));
        threadList.setFont(font);
        Panel panel2 = new Panel();
        add("South", panel2);
        panel2.add(killButton = new UserTextButton("threadlist.kill", HotJavaApplet.properties));
        killButton.disable();
        panel2.add(raiseButton = new UserTextButton("threadlist.raise", HotJavaApplet.properties));
        raiseButton.disable();
        panel2.add(lowerButton = new UserTextButton("threadlist.lower", HotJavaApplet.properties));
        lowerButton.disable();
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

    java.util.Vector getAllThreadGroups()
    {
        int i = rootThreadGroup.activeGroupCount();
        ThreadGroup athreadgroup[] = new ThreadGroup[i];
        i = rootThreadGroup.enumerate(athreadgroup);
        java.util.Vector vector = new java.util.Vector(i);
        for(int j = 0; j < i; j++)
            vector.addElement(athreadgroup[j]);

        return vector;
    }

    java.util.Vector getAllThreads()
    {
        int i = rootThreadGroup.activeCount();
        Thread athread[] = new Thread[i];
        i = rootThreadGroup.enumerate(athread);
        java.util.Vector vector = new java.util.Vector(i);
        for(int j = 0; j < i; j++)
            vector.addElement(athread[j]);

        return vector;
    }

    ThreadGroup getRootThreadGroup()
    {
        ThreadGroup threadgroup;
        for(threadgroup = Thread.currentThread().getThreadGroup(); threadgroup.getParent() != null; threadgroup = threadgroup.getParent());
        return threadgroup;
    }

    ThreadGroup getMainThreadGroup()
    {
        ThreadGroup athreadgroup[] = new ThreadGroup[rootThreadGroup.activeGroupCount()];
        int i = rootThreadGroup.enumerate(athreadgroup, false);
        for(int j = 0; j < i; j++)
            if("main".equals(athreadgroup[j].getName()))
                return athreadgroup[j];

        return null;
    }

    boolean isChildOf(Thread thread, ThreadGroup threadgroup)
    {
        ThreadGroup threadgroup1 = thread.getThreadGroup();
        if(threadgroup1 == threadgroup)
            return true;
        else
            return isChildOf(threadgroup1, threadgroup);
    }

    boolean isChildOf(ThreadGroup threadgroup, ThreadGroup threadgroup1)
    {
        for(ThreadGroup threadgroup2 = threadgroup.getParent(); threadgroup2 != null; threadgroup2 = threadgroup2.getParent())
            if(threadgroup2 == threadgroup1)
                return true;

        return threadgroup1 == null;
    }

    final boolean isAppletThreadGroup(ThreadGroup threadgroup)
    {
        if(threadgroup == null)
            return false;
        if(threadgroup.getClass().toString().equals("class sunw.hotjava.applets.AppletThreadGroup"))
            return true;
        else
            return isAppletThreadGroup(threadgroup.getParent());
    }

    final boolean isAppletThread(Thread thread)
    {
        return thread != null && isAppletThreadGroup(thread.getThreadGroup());
    }

    void die()
    {
        dieNow = true;
    }

    void sleep(Thread thread)
    {
        int i = HotJavaApplet.properties.getInteger("threadlist.sleep", 2000);
        try
        {
            Thread.sleep(i);
            return;
        }
        catch(InterruptedException _ex)
        {
            return;
        }
    }

    public void run()
    {
        Thread thread = Thread.currentThread();
        thread.setName("ThreadListApplet thread");
        rootThreadGroup = getRootThreadGroup();
        mainThreadGroup = getMainThreadGroup();
        allExpandedGroups = new java.util.Vector(3);
        allExpandedGroups.addElement(mainThreadGroup);
        for(dieNow = false; !dieNow;)
        {
            allThreads = getAllThreads();
            allThreadGroups = getAllThreadGroups();
            if(!noChanges(allThreads, oldThreads) || !noChanges(allThreadGroups, oldThreadGroups))
            {
                removeDeadGroups(allExpandedGroups, allThreadGroups);
                clear();
                populateList(threadList, rootThreadGroup, selectedObject, 0, 0);
                oldThreads = allThreads;
                oldThreadGroups = allThreadGroups;
                prepare();
            }
            sleep(thread);
        }

    }

    void clear()
    {
        threadList.clear();
        displayedThreads = new java.util.Vector(10);
    }

    void removeDeadGroups(java.util.Vector vector, java.util.Vector vector1)
    {
        for(java.util.Enumeration enumeration = vector.elements(); enumeration.hasMoreElements();)
        {
            Object obj = enumeration.nextElement();
            if(!vector1.contains(obj))
                vector.removeElement(obj);
        }

    }

    boolean noChanges(java.util.Vector vector, java.util.Vector vector1)
    {
        if(vector != null && vector1 != null && vector.size() == vector1.size())
        {
            for(java.util.Enumeration enumeration = vector.elements(); enumeration.hasMoreElements();)
            {
                Object obj = enumeration.nextElement();
                if(!vector1.contains(obj))
                    return false;
            }

            return true;
        } else
        {
            return false;
        }
    }

    void updateEntry(Object obj)
    {
        int i = displayedThreads.indexOf(obj);
        String s;
        if(obj instanceof Thread)
            s = threadInfo((Thread)obj, calculateDepth((Thread)obj));
        else
        if(obj instanceof ThreadGroup)
            s = threadGroupInfo((ThreadGroup)obj, calculateDepth((ThreadGroup)obj) - 1);
        else
            return;
        threadList.replaceItem(s, i);
    }

    final String spaces(int i)
    {
        String s = "";
        for(int j = 0; j < i; j++)
            s = s + " ";

        return s;
    }

    String pad(String s, int i)
    {
        if(s.length() > i)
            return s.substring(0, i);
        else
            return pad(s + "                    ", i);
    }

    String threadInfo(Thread thread, int i)
    {
        String s = "";
        if(isAppletThread(thread))
            s = s + HotJavaApplet.properties.getProperty("threadlist.applet");
        if(thread.isDaemon())
            s = s + HotJavaApplet.properties.getProperty("threadlist.daemon");
        return pad(spaces(i * 2) + thread.getName(), 50) + "  " + pad(String.valueOf(thread.getPriority()), 4) + s;
    }

    String threadGroupInfo(ThreadGroup threadgroup, int i)
    {
        String s = allExpandedGroups.contains(threadgroup) ? "+ " : "> ";
        String s1 = "";
        if(isAppletThreadGroup(threadgroup))
            s1 = s1 + HotJavaApplet.properties.getProperty("threadlist.applet");
        if(threadgroup.isDaemon())
            s1 = s1 + HotJavaApplet.properties.getProperty("threadlist.daemon");
        s1 = s1 + HotJavaApplet.properties.getProperty("threadlist.group");
        return pad(spaces(i * 2) + s + threadgroup.getName(), 50) + "  " + pad(String.valueOf(threadgroup.getMaxPriority()), 4) + s1;
    }

    int populateList(List list, ThreadGroup threadgroup, Object obj, int i, int j)
    {
        int k = i;
        int j1 = threadgroup.activeGroupCount();
        ThreadGroup athreadgroup[] = new ThreadGroup[j1];
        j1 = threadgroup.enumerate(athreadgroup, false);
        for(int l = 0; l < j1; l++)
        {
            ThreadGroup threadgroup1 = athreadgroup[l];
            list.addItem(threadGroupInfo(threadgroup1, j), k);
            displayedThreads.insertElementAt(threadgroup1, k);
            if(allExpandedGroups.contains(threadgroup1))
                k = populateList(list, threadgroup1, obj, k + 1, j + 1);
            else
                k++;
        }

        j1 = threadgroup.activeCount();
        Thread athread[] = new Thread[j1];
        j1 = threadgroup.enumerate(athread, false);
        for(int i1 = 0; i1 < j1; i1++)
        {
            Thread thread = athread[i1];
            list.addItem(threadInfo(thread, j), k);
            displayedThreads.insertElementAt(thread, k);
            k++;
        }

        return k;
    }

    synchronized void prepare()
    {
        if(selectedObject != null)
        {
            int i = displayedThreads.indexOf(selectedObject);
            if(i > -1)
                threadList.select(i);
            else
                selectedObject = null;
        }
        if(selectedObject instanceof Thread)
        {
            Thread thread = (Thread)selectedObject;
            boolean flag = isAppletThread(thread);
            killButton.enable(flag);
            int j = thread.getPriority();
            raiseButton.enable(flag && j < thread.getThreadGroup().getMaxPriority());
            lowerButton.enable(flag && j > 1);
            return;
        }
        if(selectedObject instanceof ThreadGroup)
        {
            ThreadGroup threadgroup = (ThreadGroup)selectedObject;
            killButton.enable(isAppletThreadGroup(threadgroup));
            raiseButton.disable();
            lowerButton.disable();
            return;
        } else
        {
            killButton.disable();
            raiseButton.disable();
            lowerButton.disable();
            return;
        }
    }

    public synchronized boolean action(Event event, Object obj)
    {
        if(event.target == threadList)
        {
            int i = threadList.getSelectedIndex();
            Object obj1 = displayedThreads.elementAt(i);
            if(obj1 instanceof ThreadGroup)
            {
                if(allExpandedGroups.contains(obj1))
                    shrinkGroup((ThreadGroup)obj1, i);
                else
                    expandGroup((ThreadGroup)obj1, i);
                updateEntry(obj1);
            }
            prepare();
            return true;
        }
        if(obj instanceof String)
        {
            String s = (String)obj;
            if(s.endsWith("raise"))
            {
                Thread thread = (Thread)selectedObject;
                thread.setPriority(thread.getPriority() + 1);
                updateEntry(selectedObject);
                prepare();
                return true;
            }
            if(s.endsWith("lower"))
            {
                Thread thread1 = (Thread)selectedObject;
                thread1.setPriority(thread1.getPriority() - 1);
                updateEntry(selectedObject);
                prepare();
                return true;
            }
            if(s.endsWith("kill"))
            {
                kill(selectedObject);
                prepare();
                return true;
            }
        }
        return false;
    }

    public synchronized boolean handleEvent(Event event)
    {
        switch(event.id)
        {
        case 701: // Event.LIST_SELECT
            int i = threadList.getSelectedIndex();
            if(i != -1)
                selectedObject = displayedThreads.elementAt(i);
            prepare();
            return true;

        case 702: // Event.LIST_DESELECT
            selectedObject = null;
            prepare();
            return true;
        }
        return super.handleEvent(event);
    }

    int calculateDepth(ThreadGroup threadgroup)
    {
        ThreadGroup threadgroup1 = threadgroup.getParent();
        if(threadgroup1 == null)
            return 0;
        else
            return 1 + calculateDepth(threadgroup1);
    }

    int calculateDepth(Thread thread)
    {
        ThreadGroup threadgroup = thread.getThreadGroup();
        if(threadgroup == null)
            return 0;
        else
            return calculateDepth(threadgroup);
    }

    void shrinkGroup(ThreadGroup threadgroup, int i)
    {
        int j = i + 1;
        while(j < displayedThreads.size()) 
        {
            Object obj = displayedThreads.elementAt(j);
            if(obj instanceof Thread)
            {
                Thread thread = (Thread)obj;
                if(!isChildOf(thread, threadgroup))
                    break;
                displayedThreads.removeElementAt(j);
                threadList.delItem(j);
                continue;
            }
            ThreadGroup threadgroup1 = (ThreadGroup)obj;
            if(!isChildOf(threadgroup1, threadgroup))
                break;
            displayedThreads.removeElementAt(j);
            threadList.delItem(j);
        }
        allExpandedGroups.removeElement(threadgroup);
    }

    void expandGroup(ThreadGroup threadgroup, int i)
    {
        int j = calculateDepth(threadgroup);
        populateList(threadList, threadgroup, selectedObject, i + 1, j);
        allExpandedGroups.addElement(threadgroup);
    }

    void kill(Object obj)
    {
        if(obj instanceof Thread)
        {
            Thread thread = (Thread)obj;
            thread.stop();
            return;
        }
        if(obj instanceof ThreadGroup)
        {
            ThreadGroup threadgroup = (ThreadGroup)obj;
            threadgroup.stop();
        }
    }

    public ThreadListApplet()
    {
    }

    static final String propName = "threadlist";
    Thread myThread;
    boolean dieNow;
    Panel topPanel;
    List threadList;
    java.util.Vector displayedThreads;
    Object selectedObject;
    ThreadGroup rootThreadGroup;
    ThreadGroup mainThreadGroup;
    java.util.Vector allThreads;
    java.util.Vector allThreadGroups;
    java.util.Vector oldThreads;
    java.util.Vector oldThreadGroups;
    java.util.Vector allExpandedGroups;
    UserTextButton killButton;
    UserTextButton raiseButton;
    UserTextButton lowerButton;
    static final int defaultMsecSleep = 2000;
    static final int colsName = 50;
    static final int colsPrio = 4;
    static final int indentPerLevel = 2;
    static final String threadGroupClosedMark = "> ";
    static final String threadGroupOpenMark = "+ ";
}
