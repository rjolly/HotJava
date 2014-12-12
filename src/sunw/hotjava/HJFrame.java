// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HJFrame.java

package sunw.hotjava;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import sun.misc.VM;
import sun.misc.VMNotification;
import sun.net.ProgressData;
import sun.net.ProgressEntry;
import sun.net.www.protocol.http.HttpAuthenticator;
import sun.net.www.protocol.http.HttpURLConnection;
import sunw.hotjava.applet.AppletManager;
import sunw.hotjava.bean.BrowserContainer;
import sunw.hotjava.bean.ConsoleDialogListener;
import sunw.hotjava.bean.CookieJarInterface;
import sunw.hotjava.bean.CurrentDocument;
import sunw.hotjava.bean.DocumentSelection;
import sunw.hotjava.bean.DocumentStacker;
import sunw.hotjava.bean.HTMLBrowsable;
import sunw.hotjava.bean.ImageCacher;
import sunw.hotjava.bean.URLPooler;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.misc.RequestProcessor;
import sunw.hotjava.ui.ActivityMonitor;
import sunw.hotjava.ui.AuthenticationDialog;
import sunw.hotjava.ui.CheckboxDialog;
import sunw.hotjava.ui.ConfirmDialog;
import sunw.hotjava.ui.FindStringDialog;
import sunw.hotjava.ui.HJMenuListener;
import sunw.hotjava.ui.HotList;
import sunw.hotjava.ui.HyperLinkListener;
import sunw.hotjava.ui.InstrumentPanel;
import sunw.hotjava.ui.Locator;
import sunw.hotjava.ui.MessageLine;
import sunw.hotjava.ui.ProgressDialog;
import sunw.hotjava.ui.SideBar;
import sunw.hotjava.ui.Toolbar;
import sunw.hotjava.ui.UserFrame;
import sunw.hotjava.ui.UserMenu;
import sunw.hotjava.ui.UserMenuBar;
import sunw.hotjava.ui.UserVetoableChangeListener;

// Referenced classes of package sunw.hotjava:
//            HJWindowManager, PasswordCache, PasswordCacheEntry

public class HJFrame extends UserFrame
    implements BrowserContainer, Observer, HttpAuthenticator, VMNotification
{
    private class BeanPropertyListener
        implements PropertyChangeListener
    {

        public void propertyChange(PropertyChangeEvent propertychangeevent)
        {
            if(propertychangeevent.getPropertyName().equals("documentTitle"))
            {
                String s = null;
                String s4 = properties.getProperty("hotjava.title");
                if(propertychangeevent.getNewValue() != null)
                {
                    s = propertychangeevent.getNewValue().toString();
                    if(s != null && s.length() != 0)
                        s = s + ": ";
                }
                if(s == null)
                    s = "";
                setTitle(s + s4);
                return;
            }
            if(propertychangeevent.getPropertyName().equals("documentURL"))
            {
                String s1 = " ";
                String s5 = propertychangeevent.getNewValue().toString();
                if(s5 != null && s5.length() > 0)
                    s1 = s1 + s5;
                if(flag != 1 && browser.getCurrentDocument().documentURL != null)
                {
                    histTitle.addElement(s1);
                    histURL.addElement(browser.getCurrentDocument().documentURL);
                    HotList.getHotList().markVisited((URL)propertychangeevent.getNewValue());
                    flag = 1;
                    return;
                } else
                {
                    flag = 0;
                    return;
                }
            }
            if(propertychangeevent.getPropertyName().equals("selection"))
            {
                DocumentSelection documentselection = (DocumentSelection)propertychangeevent.getNewValue();
                if(menuBar != null)
                {
                    MenuItem menuitem = menuBar.getMenuItem("copy");
                    if(documentselection != null && documentselection.text != null && !documentselection.text.equals(""))
                    {
                        selectionString = documentselection.text;
                        if(menuitem != null)
                        {
                            menuitem.setEnabled(true);
                            return;
                        }
                    } else
                    {
                        selectionString = "";
                        if(menuitem != null)
                            menuitem.setEnabled(false);
                    }
                    return;
                }
            } else
            {
                if(propertychangeevent.getPropertyName().equals("statusMessage"))
                {
                    String s2 = propertychangeevent.getNewValue().toString();
                    if(s2 == null)
                        s2 = "";
                    showStatus(s2);
                    return;
                }
                if(propertychangeevent.getPropertyName().equals("defaultStatusMessage"))
                {
                    String s3 = defaultStatusMessage;
                    defaultStatusMessage = propertychangeevent.getNewValue().toString();
                    if(messageLine != null && messageLine.getMessage().equals(s3))
                    {
                        showStatus("");
                        return;
                    }
                } else
                if(propertychangeevent.getPropertyName().equals("frameExists") && menuBar != null)
                {
                    boolean flag1 = propertychangeevent.getNewValue() != null;
                    MenuItem menuitem1 = menuBar.getMenuItem("framesource");
                    if(menuitem1 != null)
                        menuitem1.setEnabled(flag1);
                    menuitem1 = menuBar.getMenuItem("saveframe");
                    if(menuitem1 != null)
                        menuitem1.setEnabled(flag1);
                    menuitem1 = menuBar.getMenuItem("printframe");
                    if(menuitem1 != null)
                        menuitem1.setEnabled(flag1);
                    if(flag1)
                        HotList.getHotList().markVisited((URL)propertychangeevent.getNewValue());
                }
            }
        }

        BeanPropertyListener()
        {
        }
    }

    class UpdateRequest extends sunw.hotjava.misc.RequestProcessor.Request
    {

        public void execute()
        {
            if(menuBar == null)
                return;
            MenuItem menuitem = menuBar.getMenuItem("stop");
            if(enable)
            {
                if(menuitem != null)
                    menuitem.setEnabled(true);
                if(toolbar != null)
                {
                    toolbar.enableTool("stop");
                    return;
                }
            } else
            {
                if(menuitem != null)
                    menuitem.setEnabled(false);
                if(toolbar != null)
                    toolbar.disableTool("stop");
            }
        }

        private boolean enable;

        UpdateRequest(boolean flag1)
        {
            enable = flag1;
        }
    }

    private class CloseRequest extends sunw.hotjava.misc.RequestProcessor.Request
    {

        public void execute()
        {
            if(isDecorated && !hasBeenAltered)
            {
                saveStateToProperties();
                HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
                hjbproperties.save();
            }
            HJWindowManager hjwindowmanager = HJWindowManager.getHJWindowManager();
            hjwindowmanager.closeFrame(frame);
        }

        private HJFrame frame;

        public CloseRequest(HJFrame hjframe1)
        {
            frame = hjframe1;
        }
    }

    class FrameRequest extends sunw.hotjava.misc.RequestProcessor.Request
    {

        public void execute()
        {
            synchronized(this)
            {
                HJWindowManager hjwindowmanager = HJWindowManager.getHJWindowManager();
                spawnedBean = hjwindowmanager.openFrame(target, url, showLocator, showMenubar, showStatusbar, showToolbar, topLeft, dim);
                notify();
            }
        }

        private String target;
        private URL url;
        private boolean showLocator;
        private boolean showMenubar;
        private boolean showStatusbar;
        private boolean showToolbar;
        private Point topLeft;
        private Dimension dim;

        public FrameRequest(String s, URL url1, boolean flag1, boolean flag2, boolean flag3, boolean flag4, 
                Point point, Dimension dimension)
        {
            target = s;
            url = url1;
            showLocator = flag1;
            showMenubar = flag2;
            showStatusbar = flag3;
            showToolbar = flag4;
            topLeft = point;
            dim = dimension;
        }
    }


    public void newAllocState(int i, int j, boolean flag1)
    {
        boolean flag2 = properties.getBoolean("hotjava.memory.debug");
        if(j == 1)
        {
            docStack.setLogicalDepth(properties.getInteger("hotjava.docstack.logical.depth", 100));
            docStack.setContentsDepth(properties.getInteger("hotjava.docstack.contents.depth", 10));
            imageCache.setReferenceDepth(properties.getInteger("hotjava.browser.image.stack.depth", 10));
            if(flag2)
            {
                showStatus("Received Green memory notification");
                System.out.println("Green!");
                System.out.println("Green!");
                System.out.println("Green!");
                System.out.println("Green!");
                return;
            }
        } else
        {
            if(j == 2)
            {
                if(flag2)
                {
                    showStatus("Received Yellow memory notification");
                    System.out.println("*********** Memory state Yellow! ************");
                    System.out.println("*********** Memory state Yellow! ************");
                    System.out.println("*********** Memory state Yellow! ************");
                    System.out.println("*********** Memory state Yellow! ************");
                }
                docStack.setLogicalDepth(properties.getInteger("hotjava.docstack.yellow.logical.depth", 1));
                docStack.setContentsDepth(properties.getInteger("hotjava.docstack.yellow.contents.depth", 1));
                imageCache.setReferenceDepth(properties.getInteger("hotjava.browser.yellow.image.stack.depth", 1));
                HJWindowManager hjwindowmanager = HJWindowManager.getHJWindowManager();
                URLPooler urlpooler = hjwindowmanager.getURLPoolManager();
                CookieJarInterface cookiejarinterface = hjwindowmanager.getCookiesManager();
                urlpooler.purgeExpiredURLs();
                cookiejarinterface.purgeExpiredCookies();
                System.gc();
                return;
            }
            if(j == 3)
            {
                if(flag2)
                {
                    showStatus("Received Red memory notification");
                    System.out.println("************ Memory state Red! ***************");
                    System.out.println("************ Memory state Red! ***************");
                    System.out.println("************ Memory state Red! ***************");
                    System.out.println("************ Memory state Red! ***************");
                }
                docStack.setLogicalDepth(properties.getInteger("hotjava.docstack.red.logical.depth", 0));
                docStack.setContentsDepth(properties.getInteger("hotjava.docstack.red.contents.depth", 0));
                imageCache.flushAllImages();
                imageCache.setReferenceDepth(properties.getInteger("hotjava.browser.red.image.stack.depth", 0));
                HJWindowManager hjwindowmanager1 = HJWindowManager.getHJWindowManager();
                URLPooler urlpooler1 = hjwindowmanager1.getURLPoolManager();
                CookieJarInterface cookiejarinterface1 = hjwindowmanager1.getCookiesManager();
                urlpooler1.discardAllURLs();
                cookiejarinterface1.discardAllCookies();
                System.gc();
            }
        }
    }

    public void setServiceFrame(boolean flag1)
    {
        isService = flag1;
    }

    public boolean isServiceFrame()
    {
        return isService;
    }

    HJFrame(URL url)
    {
        super("hotjava");
        properties = HJBProperties.getHJBProperties("hjbrowser");
        messagePane = 0;
        locatorPane = 0;
        toolbarPane = 0;
        menubarPane = 0;
        actvMonPane = 0;
        clockMode = "off";
        isDecorated = true;
        isService = false;
        defaultStatusMessage = "";
        iconified = false;
        hasBeenAltered = false;
        selectionString = "";
        histTitle = new Vector();
        histURL = new Vector();
        isDecorated = true;
        int i = properties.getBoolean("hotjava.useMenuBar") ? 1 : 0;
        String s = properties.getProperty("hotjava.default.toolbar.position", "top");
        int j = translateLocation(s);
        s = properties.getProperty("hotjava.locator.position", "top");
        int k = translateLocation(s);
        s = properties.getProperty("hotjava.messageline.position", "top");
        int l = translateLocation(s);
        setupFrame(url, k, i, l, j, null, null);
    }

    HJFrame(URL url, Point point)
    {
        super("hotjava");
        properties = HJBProperties.getHJBProperties("hjbrowser");
        messagePane = 0;
        locatorPane = 0;
        toolbarPane = 0;
        menubarPane = 0;
        actvMonPane = 0;
        clockMode = "off";
        isDecorated = true;
        isService = false;
        defaultStatusMessage = "";
        iconified = false;
        hasBeenAltered = false;
        selectionString = "";
        histTitle = new Vector();
        histURL = new Vector();
        isDecorated = false;
        int i = Integer.getInteger("hotjava.nodecor.width", 640).intValue();
        int j = Integer.getInteger("hotjava.nodecor.height", 480).intValue();
        setupFrame(url, 0, 0, 0, 0, point, new Dimension(i, j));
    }

    HJFrame(URL url, boolean flag1, boolean flag2, boolean flag3, boolean flag4, Point point, Dimension dimension)
    {
        super("hotjava");
        properties = HJBProperties.getHJBProperties("hjbrowser");
        messagePane = 0;
        locatorPane = 0;
        toolbarPane = 0;
        menubarPane = 0;
        actvMonPane = 0;
        clockMode = "off";
        isDecorated = true;
        isService = false;
        defaultStatusMessage = "";
        iconified = false;
        hasBeenAltered = false;
        selectionString = "";
        histTitle = new Vector();
        histURL = new Vector();
        isDecorated = false;
        int i = flag2 ? 1 : 0;
        int j;
        if(flag4)
        {
            String s = properties.getProperty("hotjava.default.toolbar.position", "top");
            j = translateLocation(s);
            if(j == 0)
                j = 1;
        } else
        {
            j = 0;
        }
        int k;
        if(flag1)
        {
            String s1 = properties.getProperty("hotjava.locator.position", "top");
            k = translateLocation(s1);
            if(k == 0)
                k = 1;
        } else
        {
            k = 0;
        }
        int l;
        if(flag3)
        {
            String s2 = properties.getProperty("hotjava.messageline.position", "top");
            l = translateLocation(s2);
            if(l == 0)
                l = 2;
        } else
        {
            l = 0;
        }
        setupFrame(url, k, i, l, j, point, dimension);
    }

    protected void setupFrame(URL url, int i, int j, int k, int l, Point point, Dimension dimension)
    {
        int i1 = l != 1 && i != 1 ? 0 : 1;
        if(isDecorated)
            clockMode = properties.getProperty("hotjava.clock", "off");
        HttpURLConnection.setDefaultAuthenticator(this);
        WindowAdapter windowadapter = new WindowAdapter() {

            public void windowClosing(WindowEvent windowevent)
            {
                if(isDecorated && !confirmExit(false))
                {
                    return;
                } else
                {
                    closeBrowser();
                    return;
                }
            }

            public void windowIconified(WindowEvent windowevent)
            {
                iconified = true;
            }

            public void windowDeiconified(WindowEvent windowevent)
            {
                iconified = false;
                if(HJFrame.isShowingExitDialog && HJFrame.floatingExitDialog)
                    windowevent.getWindow().toBack();
            }

        }
;
        addWindowListener(windowadapter);
        try
        {
            browser = (HTMLBrowsable)Beans.instantiate(null, "sunw.hotjava.bean.HotJavaBrowserBean");
            docStack = (DocumentStacker)Beans.instantiate(null, "sunw.hotjava.bean.HotJavaDocumentStack");
            docStack.addBrowserHistoryListener(browser);
            browser.addBrowserHistoryListener(docStack);
            imageCache = (ImageCacher)Beans.instantiate(null, "sunw.hotjava.bean.ImageCache");
            imageCache.setReferenceDepth(properties.getInteger("hotjava.browser.image.stack.depth", 10));
            browser.setImageCache(imageCache);
            try
            {
                if(apman == null)
                    apman = (AppletManager)Beans.instantiate(null, properties.getProperty("hotjava.applet.manager", "sunw.hotjava.applet.BrowserAppletManager"));
                browser.setAppletManager(apman);
            }
            catch(ClassNotFoundException _ex)
            {
                if(properties.getBoolean("hotjava.display.applet.manager.missing"))
                    System.out.println(properties.getProperty("applet.manager.missing"));
            }
            HJWindowManager hjwindowmanager = HJWindowManager.getHJWindowManager();
            browser.setURLPoolManager(hjwindowmanager.getURLPoolManager());
            browser.setCookiesManager(hjwindowmanager.getCookiesManager());
            browser.addVetoableChangeListener(new UserVetoableChangeListener());
            try
            {
                Class.forName("sun.security.jsafe.Provider");
                if(sslMgr == null)
                    sslMgr = Beans.instantiate(null, properties.getProperty("hotjava.security.sslmanager", "sunw.hotjava.security.SSLManager"));
            }
            catch(ClassNotFoundException _ex)
            {
                System.err.println("Running the browser without SSL Manager");
            }
            add("Center", browser.getComponent());
            if(url != null)
            {
                browser.setDocumentString(url.toString());
            } else
            {
                String s = properties.getProperty("hotjava.gohome");
                Boolean boolean1 = new Boolean(s);
                if(!isDecorated || !boolean1.booleanValue())
                {
                    String s1 = properties.getProperty("hotjava.blank.html", "doc:/lib/hotjava/blank.html");
                    browser.setDocumentString(s1);
                } else
                {
                    String s2 = properties.getProperty("user.homepage");
                    if(s2 == null)
                        s2 = properties.getProperty("home.url");
                    if(s2 == null)
                        s2 = "http://sun.com";
                    browser.setDocumentString(s2);
                }
            }
            actionListener = new HJMenuListener(this);
            setShowMenus(j != 0);
            if(menuBar != null)
            {
                MenuItem menuitem = menuBar.getMenuItem("back");
                MenuItem menuitem1 = menuBar.getMenuItem("forward");
                if(menuitem != null)
                    menuitem.setEnabled(false);
                if(menuitem1 != null)
                    menuitem1.setEnabled(false);
                MenuItem menuitem2 = menuBar.getMenuItem("copy");
                if(menuitem2 != null)
                    menuitem2.setEnabled(false);
            }
            linkListener = new HyperLinkListener(this);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw new Error("Cannot find HotJavaBrowserBean " + classnotfoundexception);
        }
        catch(IOException ioexception)
        {
            throw new Error("Cannot intstantiate HotJavaBrowserBean " + ioexception);
        }
        add("North", topPane = new InstrumentPanel());
        add("South", bottomPane = new InstrumentPanel());
        add("East", rightPane = new SideBar());
        add("West", leftPane = new SideBar());
        topPane.setVisible(false);
        bottomPane.setVisible(false);
        rightPane.setVisible(false);
        leftPane.setVisible(false);
        toolbar = Toolbar.getDefaultToolbar(this);
        if(toolbar != null)
        {
            toolbar.disableTool("back");
            toolbar.disableTool("forward");
        }
        locator = new Locator(browser);
        browser.addPropertyChangeListener(locator);
        if(sslMgr != null)
            try
            {
                Class class1 = Class.forName("sunw.hotjava.security.SecurityMonitorListener");
                class1.newInstance();
            }
            catch(Throwable throwable)
            {
                throwable.printStackTrace();
            }
        messageLine = new MessageLine(this);
        browser.addPropertyChangeListener(messageLine);
        applyDecorations(l, i, k, i1);
        if(j != 0)
            updateMenuPlaces();
        BeanPropertyListener beanpropertylistener = new BeanPropertyListener();
        browser.addPropertyChangeListener(beanpropertylistener);
        locator.addPropertyChangeListener(beanpropertylistener);
        PropertyChangeListener propertychangelistener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent propertychangeevent)
            {
                MenuItem menuitem3 = null;
                MenuItem menuitem4 = null;
                if(menuBar != null)
                {
                    menuitem3 = menuBar.getMenuItem("back");
                    menuitem4 = menuBar.getMenuItem("forward");
                }
                if(docStack.isPreviousAvailable())
                {
                    if(toolbar != null)
                        toolbar.enableTool("back");
                    if(menuitem3 != null)
                        menuitem3.setEnabled(true);
                } else
                {
                    if(toolbar != null)
                        toolbar.disableTool("back");
                    if(menuitem3 != null)
                        menuitem3.setEnabled(false);
                }
                if(docStack.isNextAvailable())
                {
                    if(toolbar != null)
                        toolbar.enableTool("forward");
                    if(menuitem4 != null)
                    {
                        menuitem4.setEnabled(true);
                        return;
                    }
                } else
                {
                    if(toolbar != null)
                        toolbar.disableTool("forward");
                    if(menuitem4 != null)
                        menuitem4.setEnabled(false);
                }
            }

        }
;
        docStack.addPropertyChangeListener(propertychangelistener);
        ProgressData.pdata.addObserver(this);
        KeyAdapter keyadapter = new KeyAdapter() {

            public void keyPressed(KeyEvent keyevent)
            {
                switch(keyevent.getKeyCode())
                {
                case 27: // '\033'
                    stopLoading();
                    return;
                }
            }

        }
;
        addKeyListener(keyadapter);
        if(dimension != null)
            resize(dimension.width, dimension.height);
        if(point != null)
            setLocation(point);
        VM.registerVMNotification(this);
    }

    public boolean isDecorated()
    {
        return isDecorated;
    }

    public boolean hasSSL()
    {
        return sslMgr != null;
    }

    public String getHistTitle(int i)
    {
        return (String)histTitle.elementAt(i);
    }

    public URL getHistURL(int i)
    {
        return (URL)histURL.elementAt(i);
    }

    public int getHistSize()
    {
        return histTitle.size();
    }

    public void removeHistoryObject()
    {
        flag = 1;
    }

    public synchronized void update(Observable observable, Object obj)
    {
        ProgressEntry progressentry = (ProgressEntry)obj;
        switch(progressentry.what)
        {
        case 0: // '\0'
            if(count == 0)
                RequestProcessor.getHJBeanQueue().postRequest(new UpdateRequest(true));
            count++;
            return;

        case 1: // '\001'
        case 2: // '\002'
            RequestProcessor.getHJBeanQueue().postRequest(new UpdateRequest(true));
            return;

        case 3: // '\003'
            count--;
            if(count <= 0)
            {
                RequestProcessor.getHJBeanQueue().postRequest(new UpdateRequest(false));
                return;
            }
            break;
        }
    }

    public void reset()
    {
        invalidate();
        try
        {
            RequestProcessor.getHJBeanQueue().postRequest(new sunw.hotjava.misc.RequestProcessor.Request() {

                public void execute()
                {
                    synchronized(getTreeLock())
                    {
                        resetDecorations();
                    }
                }

            }
);
        }
        finally
        {
            validate();
        }
    }

    private void resetDecorations()
    {
        if(hasBeenAltered)
        {
            return;
        } else
        {
            String s = properties.getProperty("hotjava.default.toolbar.position", "top");
            int i = translateLocation(s);
            setActivityMonitor(0);
            removeToolbar();
            s = properties.getProperty("hotjava.locator.position", "top");
            int j = translateLocation(s);
            s = properties.getProperty("hotjava.messageline.position", "top");
            int k = translateLocation(s);
            int l = i != 1 && j != 1 ? 0 : 1;
            clockMode = properties.getProperty("hotjava.clock", "off");
            applyDecorations(i, j, k, l);
            updateMenuPlaces();
            return;
        }
    }

    private void applyDecorations(int i, int j, int k, int l)
    {
        synchronized(getTreeLock())
        {
            synchronized(this)
            {
                invalidate();
                if(toolbar != null)
                {
                    toolbar.resetButtonAppearance();
                    String s = properties.getProperty("user.homepage");
                    if(s == null || s.equals(""))
                        toolbar.disableTool("home");
                    else
                        toolbar.enableTool("home");
                }
                setToolbar(i);
                setLocator(j);
                setMessageLine(k);
                setActivityMonitor(l);
                if("on".equalsIgnoreCase(clockMode))
                {
                    if(bottomPane.hasClock())
                        bottomPane.removeClock();
                    if(topPane.hasClock())
                        topPane.removeClock();
                }
                if(messagePane == 1)
                    topPane.checkClock(clockMode);
                else
                    bottomPane.checkClock(clockMode);
                topPane.setVisible(!topPane.isEmpty());
                bottomPane.setVisible(!bottomPane.isEmpty());
                rightPane.setVisible(!rightPane.isEmpty());
                leftPane.setVisible(!leftPane.isEmpty());
                validate();
            }
        }
    }

    public int translateLocation(String s)
    {
        if(s.equalsIgnoreCase("top"))
            return 1;
        if(s.equalsIgnoreCase("bottom"))
            return 2;
        if(s.equalsIgnoreCase("right"))
            return 3;
        return !s.equalsIgnoreCase("left") ? 0 : 4;
    }

    protected synchronized void setToolbar(int i)
    {
        if(toolbarPane != i)
        {
            invalidate();
            switch(toolbarPane)
            {
            case 1: // '\001'
                topPane.removeToolbar(toolbar);
                break;

            case 2: // '\002'
                bottomPane.removeToolbar(toolbar);
                break;

            case 3: // '\003'
                rightPane.remove(toolbar);
                break;

            case 4: // '\004'
                leftPane.remove(toolbar);
                break;
            }
            switch(i)
            {
            case 1: // '\001'
                topPane.invalidate();
                toolbar.orientHorizontally();
                topPane.add(toolbar);
                topPane.validate();
                break;

            case 2: // '\002'
                bottomPane.invalidate();
                toolbar.orientHorizontally();
                bottomPane.add(toolbar);
                bottomPane.validate();
                break;

            case 3: // '\003'
                rightPane.invalidate();
                toolbar.orientVertically();
                rightPane.add(toolbar);
                rightPane.validate();
                break;

            case 4: // '\004'
                leftPane.invalidate();
                toolbar.orientVertically();
                leftPane.add(toolbar);
                leftPane.validate();
                break;
            }
            validate();
            toolbarPane = i;
        }
    }

    public synchronized Toolbar removeToolbar()
    {
        switch(toolbarPane)
        {
        case 1: // '\001'
            topPane.removeToolbar(toolbar);
            break;

        case 2: // '\002'
            bottomPane.removeToolbar(toolbar);
            break;

        case 3: // '\003'
            rightPane.remove(toolbar);
            break;

        case 4: // '\004'
            leftPane.remove(toolbar);
            break;
        }
        toolbarPane = 0;
        return toolbar;
    }

    private void setLocator(int i)
    {
        if(locatorPane != i)
        {
            invalidate();
            MenuItem menuitem = null;
            if(menuBar != null)
                menuBar.getMenuItem("gotopage");
            if(locatorPane == 1)
                topPane.removeLocator();
            else
            if(locatorPane == 2)
                bottomPane.removeLocator();
            switch(i)
            {
            case 1: // '\001'
                if(menuitem != null)
                    menuitem.setEnabled(true);
                topPane.invalidate();
                topPane.add(locator);
                topPane.validate();
                break;

            case 2: // '\002'
                if(menuitem != null)
                    menuitem.setEnabled(true);
                bottomPane.invalidate();
                bottomPane.add(locator);
                bottomPane.validate();
                break;

            case 0: // '\0'
                if(menuitem != null)
                    menuitem.setEnabled(false);
                break;

            case 3: // '\003'
            case 4: // '\004'
            default:
                System.out.println("Can't put locator in pane: " + i);
                break;
            }
            validate();
            locatorPane = i;
        }
    }

    public void setMessageLine(int i)
    {
        if(messagePane != i)
        {
            invalidate();
            if(messagePane == 1)
                topPane.removeMessageLine();
            else
            if(messagePane == 2)
                bottomPane.removeMessageLine();
            switch(i)
            {
            case 1: // '\001'
                topPane.invalidate();
                topPane.add(messageLine);
                topPane.validate();
                break;

            case 2: // '\002'
                bottomPane.invalidate();
                bottomPane.add(messageLine);
                bottomPane.validate();
                break;

            case 3: // '\003'
            case 4: // '\004'
            default:
                System.out.println("Can't put messageLine in pane: " + i);
                break;

            case 0: // '\0'
                break;
            }
            validate();
            messagePane = i;
        }
    }

    private void setActivityMonitor(int i)
    {
        if(actvMonPane != i)
        {
            invalidate();
            if(actvMonPane == 1)
            {
                topPane.removeActivityMonitor();
                activityMonitor = null;
            } else
            if(actvMonPane == 2)
            {
                bottomPane.removeActivityMonitor();
                activityMonitor = null;
            }
            boolean flag1 = properties.getProperty("hotjava.default.toolbar.position", "top").equals("top");
            boolean flag2 = flag1 && properties.getProperty("hotjava.buttonappearance", "ImageAndText").equals("ImageAndText");
            if(i == 1)
            {
                activityMonitor = new ActivityMonitor(browser, this, flag2);
                topPane.invalidate();
                topPane.add(activityMonitor);
                topPane.validate();
                actvMonPane = 1;
            } else
            if(i == 2)
            {
                activityMonitor = new ActivityMonitor(browser, this, flag2);
                bottomPane.invalidate();
                bottomPane.add(activityMonitor);
                bottomPane.validate();
                actvMonPane = 2;
            } else
            if(i == 0)
            {
                actvMonPane = 0;
            } else
            {
                System.out.println("Can't put activityMonitor in pane: " + i);
                actvMonPane = 0;
            }
            validate();
        }
    }

    public AppletManager getAppletManager()
    {
        return apman;
    }

    public ActionListener getActionListener()
    {
        return actionListener;
    }

    public HTMLBrowsable getHTMLBrowsable()
    {
        return browser;
    }

    public DocumentStacker getDocumentStacker()
    {
        return docStack;
    }

    public void showStatus(String s)
    {
        if(!isDecorated())
            return;
        if(s == null || s.equals(""))
            s = defaultStatusMessage;
        if(messageLine != null)
            messageLine.setMessage(s);
    }

    public void setSubstMessage(String s, String s1)
    {
        messageLine.setMessage(properties.getPropertyReplace(s, s1));
    }

    public UserMenu getGotoMenu()
    {
        if(menuBar != null)
        {
            String s = properties.getProperty("hotlist.goto.menu");
            return menuBar.getMenu(s);
        } else
        {
            return null;
        }
    }

    public UserMenu getListsMenu()
    {
        if(menuBar != null)
        {
            UserMenu usermenu = getGotoMenu();
            String s = properties.getProperty("hotlist.lists.menu");
            return (UserMenu)usermenu.getItem(s);
        } else
        {
            return null;
        }
    }

    public UserMenu getRememberToMenu()
    {
        if(menuBar != null)
        {
            UserMenu usermenu = getGotoMenu();
            String s = properties.getProperty("hotlist.remember.menu");
            return (UserMenu)usermenu.getItem(s);
        } else
        {
            return null;
        }
    }

    public MenuItem getMenuItem(String s)
    {
        if(menuBar != null)
            return menuBar.getMenuItem(s);
        else
            return null;
    }

    public synchronized void setShowMenus(boolean flag1)
    {
        if((menubarPane != 0) != flag1)
        {
            invalidate();
            if(flag1)
            {
                HotList hotlist = HotList.getHotList();
                menuBar = new UserMenuBar("browsemenubar", actionListener);
                setMenuBar(menuBar);
                hotlist.addMenusToFrame(this);
                String s = "hotjava.charset";
                String s1 = "charset " + properties.getProperty(s);
                CheckboxMenuItem checkboxmenuitem = (CheckboxMenuItem)getMenuItem(s1);
                if(checkboxmenuitem != null)
                    checkboxmenuitem.setState(true);
                menubarPane = 1;
            } else
            {
                setMenuBar(null);
                menuBar = null;
                menubarPane = 0;
            }
            validate();
        }
    }

    private void updateMenuPlaces()
    {
        HotList hotlist = HotList.getHotList();
        hotlist.updateMenuPlaces(this);
    }

    public void previousDocument()
    {
        removeHistoryObject();
        docStack.previousDocument();
    }

    public void nextDocument()
    {
        removeHistoryObject();
        docStack.nextDocument();
    }

    public void stopLoading()
    {
        browser.stopLoading();
    }

    public void alertDialog(String s)
    {
    }

    public boolean confirmDialog(String s)
    {
        return false;
    }

    public String promptDialog(String s)
    {
        return null;
    }

    public void setBarVisible(int i, boolean flag1)
    {
        hasBeenAltered = true;
        switch(i)
        {
        case 2: // '\002'
        default:
            break;

        case 0: // '\0'
            int j;
            if(flag1)
            {
                String s = properties.getProperty("hotjava.locator.position", "top");
                j = translateLocation(s);
                if(j == 0)
                    j = 1;
            } else
            {
                j = 0;
            }
            setLocator(j);
            int i1 = j != 1 && toolbarPane != 1 ? 0 : 1;
            setActivityMonitor(i1);
            return;

        case 1: // '\001'
            setShowMenus(flag1);
            return;

        case 3: // '\003'
            int k;
            if(flag1)
            {
                String s1 = properties.getProperty("hotjava.messageline.position", "top");
                k = translateLocation(s1);
                if(k == 0)
                    k = 2;
            } else
            {
                k = 0;
            }
            setMessageLine(k);
            if(flag1 && "on".equalsIgnoreCase(clockMode))
            {
                if(bottomPane.hasClock())
                    bottomPane.removeClock();
                if(topPane.hasClock())
                    topPane.removeClock();
                if(messagePane == 1)
                {
                    topPane.checkClock("on");
                    return;
                } else
                {
                    bottomPane.checkClock("on");
                    return;
                }
            }
            break;

        case 4: // '\004'
            int l;
            if(flag1)
            {
                String s2 = properties.getProperty("hotjava.default.toolbar.position", "top");
                l = translateLocation(s2);
                if(l == 0)
                    l = 1;
            } else
            {
                l = 0;
            }
            setLocator(l);
            int j1 = l != 1 || locatorPane != 1 ? 0 : 1;
            setActivityMonitor(j1);
            return;
        }
    }

    public boolean isBarVisible(int i)
    {
        boolean flag1 = false;
        switch(i)
        {
        case 0: // '\0'
            flag1 = locatorPane != 0;
            break;

        case 1: // '\001'
            flag1 = menubarPane != 0;
            break;

        case 3: // '\003'
            flag1 = messagePane != 0;
            break;

        case 4: // '\004'
            flag1 = toolbarPane != 0;
            break;
        }
        return flag1;
    }

    public void closeBrowser()
    {
        CloseRequest closerequest = new CloseRequest(this);
        RequestProcessor.getHJBeanQueue().postRequest(closerequest);
    }

    public boolean findPrompt()
    {
        if(browser.getDocumentString() == null)
            return false;
        if(findDialog == null)
            findDialog = new FindStringDialog("finddialog", this, properties);
        findDialog.setVisible(true);
        return findDialog.getResult();
    }

    public HTMLBrowsable requestNewFrame(String s, URL url, boolean flag1, boolean flag2, boolean flag3, boolean flag4, Point point, 
            Dimension dimension)
    {
        spawnedBean = null;
        FrameRequest framerequest = new FrameRequest(s, url, flag1, flag2, flag3, flag4, point, dimension);
        try
        {
            synchronized(framerequest)
            {
                RequestProcessor.getHJBeanQueue().postRequest(framerequest);
                framerequest.wait(10000L);
            }
        }
        catch(InterruptedException _ex) { }
        return spawnedBean;
    }

    public boolean hasConsoleDialog()
    {
        if(checkedConsole)
            return hasConsole;
        String s = properties.getProperty("hotjava.consoleClass");
        if(s != null)
            try
            {
                Class.forName(s);
                hasConsole = true;
            }
            catch(ClassNotFoundException _ex) { }
        checkedConsole = true;
        return hasConsole;
    }

    public void addConsoleDialogListener(ConsoleDialogListener consoledialoglistener)
    {
        if(!hasConsoleDialog())
            return;
        if(consoleListeners == null)
            consoleListeners = new Vector(10);
        if(!consoleListeners.contains(consoledialoglistener))
            consoleListeners.addElement(consoledialoglistener);
    }

    public void removeConsoleDialogListener(ConsoleDialogListener consoledialoglistener)
    {
        if(!hasConsoleDialog() || consoleListeners == null)
            return;
        if(consoleListeners.contains(consoledialoglistener))
            consoleListeners.removeElement(consoledialoglistener);
    }

    public static void alertConsoleListeners(boolean flag1)
    {
        if(consoleListeners == null || consoleListeners.size() == 0)
            return;
        for(Enumeration enumeration = consoleListeners.elements(); enumeration.hasMoreElements();)
        {
            ConsoleDialogListener consoledialoglistener = (ConsoleDialogListener)enumeration.nextElement();
            if(flag1)
                consoledialoglistener.consoleShown();
            else
                consoledialoglistener.consoleDismissed();
        }

    }

    public void paste(String s)
    {
        Component component = getFocusOwner();
        if(component != null && (component instanceof TextComponent) && component.isShowing())
        {
            TextComponent textcomponent = (TextComponent)component;
            int i = textcomponent.getSelectionStart();
            int j = textcomponent.getSelectionEnd();
            String s1 = textcomponent.getText();
            if(s == null)
                s = "";
            String s2 = i != 0 ? s1.substring(0, i) : "";
            int k = s1.length();
            String s3 = j != k ? s1.substring(j, k) : "";
            s1 = s2 + s + s3;
            textcomponent.setText(s1);
            textcomponent.setCaretPosition((s2 + s).length());
        }
    }

    public String getSelection()
    {
        return selectionString;
    }

    public boolean confirmExit(boolean flag1)
    {
        String s = properties.getProperty("quitConfirmation");
        boolean flag2 = true;
        if(s != null && s.equals("true") && (flag1 || HJWindowManager.getHJWindowManager().noOfFrames() == 1))
        {
            synchronized(exitDialogLock)
            {
                if(isShowingExitDialog)
                {
                    boolean flag3 = false;
                    return flag3;
                }
                isShowingExitDialog = true;
            }
            Object obj = iconified ? ((Object) (new Frame())) : ((Object) (this));
            floatingExitDialog = iconified;
            CheckboxDialog checkboxdialog = new CheckboxDialog(((Frame) (obj)), "quit", properties, true);
            checkboxdialog.show();
            if(checkboxdialog.getCheckboxAnswer())
                properties.put("quitConfirmation", "false");
            flag2 = checkboxdialog.getAnswer() == 0;
            isShowingExitDialog = false;
        }
        return flag2;
    }

    public boolean schemeSupported(String s)
    {
        return "Basic".equalsIgnoreCase(s);
    }

    public String authString(URL url, String s, String s1)
    {
        PasswordCacheEntry passwordcacheentry = pwCache.getCacheEntry(url);
        boolean flag1 = false;
        if(passwordcacheentry != null)
        {
            if(!cacheEntryPremature(passwordcacheentry))
                return passwordcacheentry.getEncodedRepresentation();
            pwCache.removeEntry(passwordcacheentry);
            flag1 = true;
        }
        HJWindowManager hjwindowmanager = HJWindowManager.getHJWindowManager();
        Object obj = hjwindowmanager.getLastFocusHolder();
        if(obj == null)
            obj = new Frame();
        String s2 = (new AuthenticationDialog("hotjava.auth", ((Frame) (obj)))).getAuthString(url, s1, null, flag1);
        if(s2 != null)
        {
            PasswordCacheEntry passwordcacheentry1 = pwCache.registerPassword(url, s2);
            return passwordcacheentry1.getEncodedRepresentation();
        } else
        {
            return null;
        }
    }

    private boolean cacheEntryPremature(PasswordCacheEntry passwordcacheentry)
    {
        long l = 1000 * properties.getInteger("hotjava.url.timeout", 10);
        return passwordcacheentry.creationTime + l >= System.currentTimeMillis();
    }

    public void save(URL url, String s, String s1, boolean flag1, boolean flag2)
    {
        ProgressDialog progressdialog = new ProgressDialog(this, s1, url, s);
        try
        {
            FileOutputStream fileoutputstream = new FileOutputStream(s);
            int i = 0;
            if(flag1)
                i = browser.saveURLContent(url, fileoutputstream, progressdialog);
            else
            if(flag2)
                i = browser.saveFrameSource(fileoutputstream, progressdialog);
            else
                i = browser.saveDocumentSource(fileoutputstream, progressdialog);
            progressdialog.setTotalBytes(i);
            progressdialog.start();
            return;
        }
        catch(IOException ioexception)
        {
            progressdialog.dispose();
            String s2 = ioexception.getMessage();
            int j = s2.lastIndexOf(System.getProperty("file.separator"));
            String as[] = {
                s2.substring(0, j + 1)
            };
            ConfirmDialog confirmdialog = new ConfirmDialog("save.error", this, 1, as);
            confirmdialog.show();
            return;
        }
    }

    public boolean isIconified()
    {
        return iconified;
    }

    private static final String beanClass = "sunw.hotjava.bean.HotJavaBrowserBean";
    private static final String appletManagerClass = "sunw.hotjava.applet.BrowserAppletManager";
    private static final String sslManagerClass = "sunw.hotjava.security.SSLManager";
    private static final String docStackClass = "sunw.hotjava.bean.HotJavaDocumentStack";
    private static final String imageCacheClass = "sunw.hotjava.bean.ImageCache";
    private static final PasswordCache pwCache = new PasswordCache();
    private HJBProperties properties;
    private FindStringDialog findDialog;
    static final String propName = "hotjava";
    private MessageLine messageLine;
    private Locator locator;
    private Toolbar toolbar;
    private ActivityMonitor activityMonitor;
    InstrumentPanel topPane;
    InstrumentPanel bottomPane;
    SideBar rightPane;
    SideBar leftPane;
    public static final int PANE_NOWHERE = 0;
    public static final int PANE_TOP = 1;
    public static final int PANE_BOTTOM = 2;
    public static final int PANE_RIGHT = 3;
    public static final int PANE_LEFT = 4;
    private int messagePane;
    private int locatorPane;
    private int toolbarPane;
    private int menubarPane;
    private int actvMonPane;
    private String clockMode;
    private HTMLBrowsable browser;
    private DocumentStacker docStack;
    private ImageCacher imageCache;
    private ActionListener actionListener;
    private HyperLinkListener linkListener;
    private UserMenuBar menuBar;
    private static AppletManager apman;
    private static Object sslMgr;
    private static final Object exitDialogLock = new Object();
    private static boolean isShowingExitDialog;
    private boolean isDecorated;
    private boolean isService;
    private String defaultStatusMessage;
    private boolean iconified;
    private static boolean floatingExitDialog;
    private boolean hasBeenAltered;
    private String selectionString;
    int flag;
    private Vector histTitle;
    private Vector histURL;
    private int count;
    private static boolean checkedConsole;
    private static boolean hasConsole;
    private static Vector consoleListeners = null;
    private HTMLBrowsable spawnedBean;






















}
