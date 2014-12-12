// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletPanel.java

package sunw.hotjava.applet;

import java.applet.*;
import java.awt.*;
import java.io.PrintStream;
import java.net.URL;
import java.util.StringTokenizer;
import sun.misc.Queue;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.applet:
//            AppletEvent, AppletEventMulticaster, AppletListener, AppletManager

public abstract class AppletPanel extends Panel
    implements AppletStub, Runnable
{
    private static class QueueEntry
    {

        AppletPanel target;
        int id;

        QueueEntry(AppletPanel appletpanel, int i)
        {
            target = appletpanel;
            id = i;
        }
    }

    private static class DocProtocolAppletThread extends Thread
    {

        public static Thread register(AppletPanel appletpanel)
        {
            synchronized(lock)
            {
                activeApplets++;
                if(AppletPanel.docCodeBaseThread == null && appletpanel.am != null)
                {
                    ThreadGroup threadgroup = appletpanel.am.getThreadGroup(appletpanel.getCodeBase());
                    AppletPanel.docCodeBaseThread = new DocProtocolAppletThread(threadgroup);
                    AppletPanel.docCodeBaseThread.start();
                }
            }
            return AppletPanel.docCodeBaseThread;
        }

        public void run()
        {
            do
            {
                AppletEvent appletevent;
                AppletPanel appletpanel;
                do
                    try
                    {
                        QueueEntry queueentry = (QueueEntry)AppletPanel.docCodeBaseQueue.dequeue();
                        appletpanel = queueentry.target;
                        appletevent = new AppletEvent(appletpanel, queueentry.id, null);
                    }
                    catch(InterruptedException _ex)
                    {
                        synchronized(lock)
                        {
                            activeApplets = 0;
                            AppletPanel.docCodeBaseThread = null;
                        }
                        return;
                    }
                while(!appletpanel.processEvent(appletevent));
                synchronized(lock)
                {
                    activeApplets--;
                    if(activeApplets <= 0)
                    {
                        AppletPanel.docCodeBaseThread = null;
                        return;
                    }
                }
            } while(true);
        }

        private static int activeApplets;
        private static Object lock = new Object();


        private DocProtocolAppletThread(ThreadGroup threadgroup)
        {
            super(threadgroup, "HotJava Applets");
        }
    }


    protected abstract String getCode();

    protected abstract int getWidth();

    protected abstract int getHeight();

    protected abstract String getJarFiles();

    protected abstract String getSerializedObject();

    public Thread getAppletHandlerThread()
    {
        return handler;
    }

    public URL getCodeBase()
    {
        return baseURL;
    }

    public void init()
    {
        if(am == null)
            am = getAM();
        if(am == null)
        {
            String s = getCode();
            System.out.println("No applet manager loaded.. applets don't work");
            status = 7;
            showAppletStatus("nocreate", s);
            showAppletLog("nocreate", s);
            return;
        }
        setLayout(new BorderLayout());
        try
        {
            appletSize.width = getWidth();
            appletSize.height = getHeight();
        }
        catch(NumberFormatException numberformatexception)
        {
            status = 7;
            showAppletStatus("badattribute.exception");
            showAppletLog("badattribute.exception");
            showAppletException(numberformatexception);
        }
        if(isDocProtocolApplet())
        {
            handler = DocProtocolAppletThread.register(this);
            return;
        } else
        {
            ThreadGroup threadgroup = am.getThreadGroup(getCodeBase());
            String s1 = "applet-" + getCode();
            handler = new Thread(threadgroup, this, "thread " + s1);
            handler.setPriority(4);
            handler.start();
            return;
        }
    }

    public Dimension minimumSize()
    {
        return new Dimension(appletSize.width, appletSize.height);
    }

    protected AppletManager getAM()
    {
        return HotJavaBrowserBean.getAppletManager();
    }

    public Dimension preferredSize()
    {
        return minimumSize();
    }

    public synchronized void addAppletListener(AppletListener appletlistener)
    {
        listeners = AppletEventMulticaster.add(listeners, appletlistener);
    }

    public void dispatchAppletEvent(int i, Object obj)
    {
        if(listeners != null)
        {
            AppletEvent appletevent = new AppletEvent(this, i, obj);
            listeners.appletStateChanged(appletevent);
        }
    }

    public void sendEvent(int i)
    {
        getQueue().enqueue(new QueueEntry(this, i));
    }

    private QueueEntry getNextEvent()
        throws InterruptedException
    {
        QueueEntry queueentry = (QueueEntry)getQueue().dequeue();
        return queueentry;
    }

    private synchronized Queue getQueue()
    {
        if(queue == null)
            if(isDocProtocolApplet())
                queue = docCodeBaseQueue;
            else
                queue = new Queue();
        return queue;
    }

    protected boolean isDocProtocolApplet()
    {
        return "doc".equalsIgnoreCase(getCodeBase().getProtocol());
    }

    public void run()
    {
        AppletEvent appletevent;
        AppletPanel appletpanel;
        do
            try
            {
                QueueEntry queueentry = getNextEvent();
                appletpanel = queueentry.target;
                queueentry.target = null;
                appletevent = new AppletEvent(appletpanel, queueentry.id, null);
            }
            catch(InterruptedException _ex)
            {
                showAppletStatus("bail");
                return;
            }
        while(!appletpanel.processEvent(appletevent));
    }

    private boolean processEvent(AppletEvent appletevent)
    {
        try
        {
            switch(appletevent.getID())
            {
            default:
                break;

            case 1: // '\001'
                if(okToLoad())
                    runLoader();
                break;

            case 2: // '\002'
                if(status != 1)
                {
                    showAppletStatus("notloaded");
                    break;
                }
                applet.resize(appletSize);
                if(doInit)
                    applet.init();
                doInit = true;
                validate();
                status = 2;
                showAppletStatus("inited");
                break;

            case 3: // '\003'
                if(status != 2)
                {
                    showAppletStatus("notinited");
                } else
                {
                    applet.resize(appletSize);
                    applet.start();
                    validate();
                    applet.show();
                    status = 3;
                    showAppletStatus("started");
                }
                break;

            case 4: // '\004'
                if(status != 3)
                {
                    showAppletStatus("notstarted");
                } else
                {
                    status = 2;
                    applet.hide();
                    applet.stop();
                    showAppletStatus("stopped");
                }
                break;

            case 5: // '\005'
                if(status != 2)
                {
                    showAppletStatus("notstopped");
                } else
                {
                    status = 1;
                    applet.destroy();
                    showAppletStatus("destroyed");
                }
                break;

            case 0: // '\0'
                if(status != 1)
                {
                    showAppletStatus("notdestroyed");
                } else
                {
                    status = 0;
                    remove(applet);
                    applet = null;
                    showAppletStatus("disposed");
                }
                break;

            case 6: // '\006'
                return true;
            }
        }
        catch(Exception exception)
        {
            if(exception.getMessage() != null)
                showAppletStatus("exception2", exception.getClass().getName(), exception.getMessage());
            else
                showAppletStatus("exception", exception.getClass().getName());
            showAppletException(exception);
        }
        catch(ThreadDeath _ex)
        {
            showAppletStatus("death");
            return true;
        }
        catch(Error error)
        {
            if(error.getMessage() != null)
                showAppletStatus("error2", error.getClass().getName(), error.getMessage());
            else
                showAppletStatus("error", error.getClass().getName());
            showAppletException(error);
        }
        clearLoadAbortRequest();
        return false;
    }

    private void runLoader()
    {
        String s = getCode();
        try
        {
            if(status != 0)
            {
                showAppletStatus("notdisposed");
                return;
            }
            dispatchAppletEvent(51235, null);
            status = 1;
            String s1 = getSerializedObject();
            String as[] = null;
            String s2 = getJarFiles();
            if(s2 != null)
            {
                StringTokenizer stringtokenizer = new StringTokenizer(s2, ",", false);
                as = new String[stringtokenizer.countTokens()];
                for(int i = 0; stringtokenizer.hasMoreTokens(); i++)
                {
                    String s3 = stringtokenizer.nextToken().trim();
                    as[i] = s3;
                }

            }
            ClassLoader classloader = am.refClassLoader(getCodeBase(), as, null);
            loading = true;
            applet = am.createApplet(classloader, s, s1);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            status = 7;
            showAppletStatus("notfound", s);
            showAppletLog("notfound", s);
            showAppletException(classnotfoundexception);
            return;
        }
        catch(InstantiationException instantiationexception)
        {
            status = 7;
            showAppletStatus("nocreate", s);
            showAppletLog("nocreate", s);
            showAppletException(instantiationexception);
            return;
        }
        catch(IllegalAccessException illegalaccessexception)
        {
            status = 7;
            showAppletStatus("noconstruct", s);
            showAppletLog("noconstruct", s);
            showAppletException(illegalaccessexception);
            return;
        }
        catch(Exception exception1)
        {
            status = 7;
            showAppletStatus("exception", exception1.getMessage());
            showAppletException(exception1);
            return;
        }
        catch(ThreadDeath _ex)
        {
            status = 7;
            showAppletStatus("death");
            return;
        }
        catch(Error error)
        {
            status = 7;
            showAppletStatus("error", error.getMessage());
            showAppletException(error);
            return;
        }
        finally
        {
            loading = false;
            dispatchAppletEvent(51236, null);
        }
        applet.setStub(this);
        applet.hide();
        add("Center", applet);
        showAppletStatus("loaded");
        validate();
        if(handler != docCodeBaseThread)
            handler.setPriority(3);
    }

    protected boolean isPriviledgedPage()
    {
        return false;
    }

    protected synchronized void stopLoading()
    {
        if(loading && handler != docCodeBaseThread)
        {
            handler.interrupt();
            return;
        } else
        {
            setLoadAbortRequest();
            return;
        }
    }

    protected synchronized boolean okToLoad()
    {
        return !loadAbortRequest;
    }

    protected synchronized void clearLoadAbortRequest()
    {
        loadAbortRequest = false;
    }

    protected synchronized void setLoadAbortRequest()
    {
        loadAbortRequest = true;
    }

    public boolean isActive()
    {
        return status == 3;
    }

    public void appletResize(int i, int j)
    {
        appletSize.width = i;
        appletSize.height = j;
        dispatchAppletEvent(51234, preferredSize());
    }

    public Applet getApplet()
    {
        return applet;
    }

    protected void showAppletStatus(String s)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        String s1 = hjbproperties.getProperty(propPrefix + s);
        getAppletContext().showStatus(s1);
    }

    protected void showAppletStatus(String s, Object obj)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        String s1 = propPrefix + s;
        String s2 = hjbproperties.getPropertyReplace(s1, (String)obj);
        getAppletContext().showStatus(s2);
    }

    protected void showAppletStatus(String s, Object obj, Object obj1)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        String s1 = propPrefix + s;
        String s2 = hjbproperties.getPropertyReplace(s1, (String)obj, (String)obj1);
        getAppletContext().showStatus(s2);
    }

    protected void showAppletLog(String s)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        System.out.println(hjbproperties.getProperty(propPrefix + s));
    }

    protected void showAppletLog(String s, Object obj)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        String s1 = propPrefix + s;
        System.out.println(hjbproperties.getPropertyReplace(s1, (String)obj));
    }

    protected void showAppletException(Throwable throwable)
    {
        throwable.printStackTrace();
        repaint();
    }

    public AppletPanel()
    {
        doInit = true;
        loading = false;
        appletSize = new Dimension(100, 100);
        loadAbortRequest = false;
    }

    public abstract URL getDocumentBase();

    public abstract String getParameter(String s);

    public abstract AppletContext getAppletContext();

    private static String propPrefix = "appletpanel.";
    static Queue docCodeBaseQueue = new Queue();
    static Thread docCodeBaseThread = null;
    protected Applet applet;
    protected AppletManager am;
    protected URL baseURL;
    protected boolean doInit;
    private boolean loading;
    public static final int APPLET_DISPOSE = 0;
    public static final int APPLET_LOAD = 1;
    public static final int APPLET_INIT = 2;
    public static final int APPLET_START = 3;
    public static final int APPLET_STOP = 4;
    public static final int APPLET_DESTROY = 5;
    public static final int APPLET_QUIT = 6;
    public static final int APPLET_ERROR = 7;
    public static final int APPLET_RESIZE = 51234;
    public static final int APPLET_LOADING = 51235;
    public static final int APPLET_LOADING_COMPLETED = 51236;
    protected int status;
    Thread handler;
    Dimension appletSize;
    private boolean loadAbortRequest;
    private AppletListener listeners;
    private Queue queue;


}
