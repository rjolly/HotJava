// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TagAppletPanel.java

package sunw.hotjava.tags;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import sun.applet.AppletAudioClip;
import sun.awt.ScreenUpdater;
import sun.awt.UpdateClient;
import sun.awt.image.URLImageSource;
import sun.misc.Ref;
import sunw.hotjava.applet.*;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.*;
import sunw.hotjava.tables.TableElementPanel;
import sunw.hotjava.ui.*;
import sunw.html.Attributes;

// Referenced classes of package sunw.hotjava.tags:
//            APPLET, AppletPanelThreadGroupRef, OBJECT, PARAM

public class TagAppletPanel extends AppletPanel
    implements DocPanel, AppletContext, DocConstants, ImageObserver, UpdateClient, DocumentEventSource, SizeItem
{
    private final class AppletEventListener
        implements AppletListener
    {

        public void appletStateChanged(AppletEvent appletevent)
        {
            switch(appletevent.getID())
            {
            case 51236: 
                processAppletLoaded(appletevent);
                // fall through

            case 51234: 
                processAppletResized(appletevent);
                doc.removeSizeItemWaitRef(TagAppletPanel.this);
                // fall through

            case 51235: 
            default:
                return;
            }
        }

        AppletEventListener()
        {
        }
    }

    private final class AppletMouseListener extends MouseAdapter
    {

        public void mouseReleased(MouseEvent mouseevent)
        {
            processMouseReleased(mouseevent);
        }

        AppletMouseListener()
        {
        }
    }

    private class AppletMouseMotionListener extends MouseMotionAdapter
    {

        public void mouseEntered(MouseEvent mouseevent)
        {
            requestFocus();
        }

        AppletMouseMotionListener()
        {
        }
    }

    private final class AppletEmbeddedFrame extends Frame
    {

        public Container getParent()
        {
            return null;
        }

        public void setTitle(String s)
        {
        }

        public void setIconImage(Image image)
        {
        }

        public void setMenuBar(MenuBar menubar)
        {
        }

        public void setResizable(boolean flag)
        {
        }

        public void remove(MenuComponent menucomponent)
        {
        }

        public boolean isResizable()
        {
            return false;
        }

        AppletEmbeddedFrame()
        {
        }
    }

    private static class AppletPanelThreadGroupRef extends Ref
    {

        public Object reconstitute()
        {
            return null;
        }

        AppletPanelThreadGroupRef(ThreadGroup threadgroup)
        {
            setThing(threadgroup);
        }
    }

    private static class ThreadDestroyRequest extends sunw.hotjava.misc.RequestProcessor.Request
    {

        public void execute()
        {
            ThreadGroup threadgroup = (ThreadGroup)group.check();
            if(threadgroup != null)
            {
                threadgroup.stop();
                am.deRefClassLoader(codeBase, false);
            }
        }

        private AppletPanelThreadGroupRef group;
        private AppletManager am;
        private URL codeBase;

        ThreadDestroyRequest(AppletPanelThreadGroupRef appletpanelthreadgroupref, AppletManager appletmanager, URL url)
        {
            group = appletpanelthreadgroupref;
            codeBase = url;
            am = appletmanager;
        }
    }

    private static class ThreadInterruptRequest extends sunw.hotjava.misc.RequestProcessor.Request
    {

        public void execute()
        {
            ThreadGroup threadgroup = (ThreadGroup)group.check();
            if(threadgroup != null)
            {
                int i = threadgroup.activeCount();
                Thread athread[] = new Thread[i];
                int j = threadgroup.enumerate(athread);
                for(int k = 0; k < j; k++)
                    try
                    {
                        athread[k].interrupt();
                    }
                    catch(Throwable _ex) { }

                if(destroyDelay >= 0)
                {
                    ThreadDestroyRequest threaddestroyrequest = new ThreadDestroyRequest(group, am, codeBase);
                    RequestProcessor.getHJBeanQueue().postRequest(threaddestroyrequest, destroyDelay);
                    return;
                }
                am.deRefClassLoader(codeBase, false);
            }
        }

        private AppletPanelThreadGroupRef group;
        private int destroyDelay;
        private AppletManager am;
        private URL codeBase;

        ThreadInterruptRequest(AppletPanelThreadGroupRef appletpanelthreadgroupref, int i, AppletManager appletmanager, URL url)
        {
            codeBase = url;
            am = appletmanager;
            group = appletpanelthreadgroupref;
            destroyDelay = i;
        }
    }

    private static class ExceptionDialog extends UserDialog
    {

        public void action(ActionEvent actionevent)
        {
            setVisible(false);
            dispose();
        }

        TextArea text;
        Button button;

        ExceptionDialog(String s, Frame frame, String s1)
        {
            super(s, frame, HJBProperties.getHJBProperties("beanPropertiesKey"));
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            setLayout(new BorderLayout());
            add("Center", text = new UserTextArea(s, hjbproperties));
            text.setEditable(false);
            Panel panel = new Panel();
            panel.add(button = new UserTextButton(s + ".cancel", hjbproperties));
            button.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent actionevent)
                {
                    action(actionevent);
                }

            }
);
            add("South", panel);
            text.setText(s1);
            pack();
            centerOnScreen();
        }
    }


    TagAppletPanel(Document document, APPLET applet)
    {
        exceptionTextBuffer = new StringWriter();
        exceptionStream = new PrintWriter(exceptionTextBuffer);
        loaded = false;
        destroyed = false;
        docListeners = new DocumentEventMulticaster();
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        synchronized(sunw.hotjava.tags.TagAppletPanel.class)
        {
            if(lazyLoadingNotDone)
            {
                errorImage = Globals.getImageFromBeanJar_DontUseThisMethod(hjbproperties.getProperty("applet.errimg"));
                delayImage = Globals.getImageFromBeanJar_DontUseThisMethod(hjbproperties.getProperty("applet.delayimg"));
                altFont = hjbproperties.getFont("altmessagefont");
                lazyLoadingNotDone = false;
            }
        }
        doc = document;
        item = applet;
        setVisible(false);
        param = new Hashtable(11);
        paramCase = new Hashtable(11);
        Hashtable hashtable = new Hashtable(11);
        int i = applet.getIndex();
        for(int j = i + applet.getOffset(); i < j; i++)
        {
            DocItem docitem = document.getItem(i);
            if(docitem instanceof PARAM)
            {
                Attributes attributes = ((PARAM)docitem).getAttributes();
                String s2 = attributes.get("name");
                String s6 = attributes.get("value");
                if(s2 != null && s6 != null)
                {
                    String s7 = s2.toLowerCase();
                    String s9 = (String)hashtable.get(s7);
                    if(s9 != null)
                    {
                        if(paramCase.get(s9) == null)
                            paramCase.put(s9, param.get(s7));
                        paramCase.put(s2, s6);
                    }
                    param.put(s7, s6);
                    hashtable.put(s7, s2);
                }
            }
        }

        String s;
        for(Enumeration enumeration = paramCase.keys(); enumeration.hasMoreElements(); param.remove(s))
            s = ((String)enumeration.nextElement()).toLowerCase();

        String s1 = getAttribute("classid");
        if(s1 != null && s1.equals("clsid:8AD9C840-044E-11D1-B3E9-00805F499D93"))
        {
            String s3 = (String)param.get("codebase");
            if(s3 != null)
            {
                if(s3.toLowerCase().endsWith(".jar"))
                {
                    int k = s3.lastIndexOf("/");
                    s3 = s3.substring(0, k + 1);
                } else
                if(!s3.endsWith("/"))
                    s3 = s3 + "/";
                try
                {
                    baseURL = new URL(getDocumentBase(), s3);
                }
                catch(MalformedURLException _ex) { }
            }
        } else
        {
            String s4 = getParameter("codebase");
            if(s4 != null)
            {
                if(s4.toLowerCase().endsWith(".jar"))
                {
                    int l = s4.lastIndexOf("/");
                    s4 = s4.substring(0, l + 1);
                } else
                if(!s4.endsWith("/"))
                    s4 = s4 + "/";
                try
                {
                    baseURL = new URL(getDocumentBase(), s4);
                }
                catch(MalformedURLException _ex) { }
            }
        }
        if(baseURL == null)
        {
            baseURL = getDocumentBase();
            String s5 = baseURL.getFile();
            int i1 = s5.lastIndexOf('/');
            if(i1 > 0 && i1 < s5.length() - 1)
                try
                {
                    baseURL = new URL(baseURL, s5.substring(0, i1 + 1));
                }
                catch(MalformedURLException _ex) { }
        }
        alt = getParameter("alt");
        document.addSizeItemWaitRef(this);
        HJBProperties hjbproperties1 = HJBProperties.getHJBProperties("beanPropertiesKey");
        int j1 = hjbproperties1.getInteger("image.timeout", 10000);
        ScreenUpdater.updater.notify(this, j1, this);
        init();
        addMouseListener(new AppletMouseListener());
        addMouseMotionListener(new AppletMouseMotionListener());
        addAppletListener(new AppletEventListener());
        String s8 = applet.getAttributes() != null ? applet.getAttributes().toString() : null;
        showStatus(hjbproperties1.getPropertyReplace("applet.start", s8, baseURL.toString()));
        if(super.status == 7)
        {
            determineSize(errorImage, 0, -1, -1);
            return;
        }
        boolean flag;
        if(isDocProtocolApplet())
        {
            delayLoading = false;
            flag = true;
        } else
        {
            delayLoading = hjbproperties1.getBoolean("delayAppletLoading");
            flag = true;
        }
        String s10 = getCode();
        if(s10 == null && getSerializedObject() == null)
            return;
        if(s10 != null)
            s10 = s10.replace('/', '.');
        if(flag && !delayLoading)
        {
            sendEvent(1);
            sendEvent(2);
            return;
        } else
        {
            determineSize(delayImage, 0, -1, -1);
            return;
        }
    }

    public Document getDocument()
    {
        return doc;
    }

    public DocItem getItem()
    {
        return item;
    }

    public String getParameter(String s)
    {
        if(item.getAttributes() == null)
            return null;
        String s1 = s.toLowerCase();
        String s2 = item.getAttributes().get(s1);
        if(s2 == null && param != null)
            s2 = (String)param.get(s1);
        if(s2 == null && paramCase != null)
            s2 = (String)paramCase.get(s);
        return s2;
    }

    private String getAttribute(String s)
    {
        Attributes attributes = item.getAttributes();
        if(attributes != null)
            return attributes.get(s);
        else
            return null;
    }

    public URL getDocumentBase()
    {
        return doc.getBaseURL();
    }

    public URL getCodeBase()
    {
        return baseURL;
    }

    public int getWidth()
    {
        return item.getWidthAttribute(getTopFormatter());
    }

    public int getHeight()
    {
        return item.getHeightAttribute(getTopFormatter());
    }

    public String getCode()
    {
        String s = getParameter("code");
        if(s == null)
        {
            if(getSerializedObject() != null)
                return null;
            s = getParameter("classid");
            if(s != null)
            {
                int i = s.indexOf(':');
                if(i != -1 && s.length() > i + 1)
                    s = s.substring(i + 1) + ".class";
            }
        }
        if(s != null && s.startsWith("sunw.hotjava") && !s.equals("sunw.hotjava.applets.SetHotJavaPropertiesApplet") && !isPriviledgedPage())
            s = null;
        return s;
    }

    protected boolean isPriviledgedPage()
    {
        boolean flag = doc.getProperty("template.src.url") != null;
        if(flag)
            return true;
        URL url = getDocumentBase();
        if(url == null)
            return false;
        String s = url.getProtocol();
        return "doc".equals(s) || "mailto".equals(s);
    }

    public String getJarFiles()
    {
        String s = getParameter("archive");
        if(s != null)
            return s;
        String s1 = getParameter("codebase");
        if(s1 != null && s1.endsWith(".jar"))
            return s1;
        else
            return null;
    }

    public String getSerializedObject()
    {
        if(item.getObject() instanceof OBJECT)
        {
            String s = getParameter("type");
            if(s == null)
                return null;
            s = s.toLowerCase();
            if(s.equals("application/x-java-serialized-object") || s.equals("application/vnd.javasoft.java-serialized-object") || s.equals("application/java-serialized-object"))
                return getParameter("data");
        } else
        if(item.getObject() instanceof APPLET)
            return getAttribute("object");
        return null;
    }

    public AppletContext getAppletContext()
    {
        return this;
    }

    public void paint(Graphics g)
    {
        if(shouldShowBrokenApplet())
            drawBrokenAppletImage(g);
    }

    public void print(Graphics g)
    {
        try
        {
            if(shouldShowBrokenApplet())
            {
                drawBrokenAppletImage(g);
                return;
            }
            Applet applet = getApplet();
            if(applet != null)
            {
                applet.printAll(g);
                return;
            }
        }
        catch(Exception _ex) { }
    }

    private boolean shouldShowBrokenApplet()
    {
        boolean flag = true;
        return super.status == 7 || !loaded && (!flag || delayLoading) || destroyed;
    }

    private void drawBrokenAppletImage(Graphics g)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        Color color = hjbproperties.getColor("hotjava.errorcolor", Color.lightGray);
        g.setColor(color);
        Image image = super.status != 7 ? delayImage : errorImage;
        int i = getWidth();
        int j = getHeight();
        g.fill3DRect(0, 0, i, j, true);
        Graphics g1 = g.create(2, 2, i - 1, j - 1);
        try
        {
            int k = image.getWidth(this);
            int l = image.getHeight(this);
            g1.drawImage(image, 1, 1, k, l, color, this);
            if(alt != null)
            {
                g1.setFont(altFont);
                g1.setColor(hjbproperties.getColor("hotjava.alttextcolor", Color.lightGray));
                g1.drawString(alt, k, j - g1.getFontMetrics(g1.getFont()).getAscent());
            }
        }
        finally
        {
            g1.dispose();
        }
    }

    public AudioClip getAudioClip(URL url)
    {
        SecurityManager securitymanager = System.getSecurityManager();
        if(securitymanager != null)
            securitymanager.checkConnect(url.getHost(), url.getPort());
        return new AppletAudioClip(url);
    }

    public Image getImage(URL url)
    {
        SecurityManager securitymanager = System.getSecurityManager();
        if(securitymanager != null)
            securitymanager.checkConnect(url.getHost(), url.getPort());
        return Toolkit.getDefaultToolkit().createImage(new URLImageSource(url));
    }

    private Formatter getFormatter()
    {
        Container container = super.getParent();
        if(container != null)
            return ((FormatterOwner)container).getFormatter();
        else
            return null;
    }

    public DocumentFormatter getTopFormatter(boolean flag)
    {
        DocumentFormatter documentformatter = null;
        for(Object obj = this; (obj = ((Component) (obj)).getParent()) != null;)
            if(obj instanceof DocumentFormatterPanel)
            {
                documentformatter = (DocumentFormatter)((DocumentFormatterPanel)obj).getFormatter();
                if(!flag)
                    return documentformatter;
            }

        return documentformatter;
    }

    private DocumentFormatter getTopFormatter()
    {
        for(Object obj = this; (obj = ((Component) (obj)).getParent()) != null;)
            if(obj instanceof DocumentFormatterPanel)
                return (DocumentFormatter)((DocumentFormatterPanel)obj).getFormatter();

        return null;
    }

    public Applet getApplet(String s)
    {
        Object obj = null;
        s = s.toLowerCase();
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        DocumentFormatter documentformatter = getTopFormatter(hjbproperties.getBoolean("hotjava.security.getInterFrameApplets"));
        Vector vector = documentformatter.getAppletPanelsAcrossFrames();
        for(int i = 0; i < vector.size(); i++)
        {
            TagAppletPanel tagappletpanel = (TagAppletPanel)vector.elementAt(i);
            String s1 = tagappletpanel.getParameter("name");
            if(s.equalsIgnoreCase(s1) && (hjbproperties.getBoolean("hotjava.security.getInterFrameApplets") || tagappletpanel.getDocumentBase().equals(getDocumentBase())) && getCodeBase().equals(tagappletpanel.getCodeBase()))
                return tagappletpanel.getApplet();
        }

        return null;
    }

    public Container getParent()
    {
        AppletManager appletmanager = HotJavaBrowserBean.getAppletManager();
        if(appletmanager != null && appletmanager.checkFrameAccess())
            return super.getParent();
        if(fakeFrame == null)
            synchronized(this)
            {
                if(fakeFrame == null)
                    fakeFrame = new AppletEmbeddedFrame();
            }
        return fakeFrame;
    }

    public Enumeration getApplets()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        DocumentFormatter documentformatter = getTopFormatter(hjbproperties.getBoolean("hotjava.security.getInterFrameApplets"));
        Vector vector = documentformatter.getAppletPanelsAcrossFrames();
        for(int i = 0; i < vector.size(); i++)
        {
            TagAppletPanel tagappletpanel = (TagAppletPanel)vector.elementAt(i);
            if((tagappletpanel.getDocumentBase().equals(getDocumentBase()) || hjbproperties.getBoolean("hotjava.security.getInterFrameApplets")) && getCodeBase().equals(tagappletpanel.getCodeBase()))
            {
                vector.setElementAt(tagappletpanel.getApplet(), i);
            } else
            {
                vector.removeElementAt(i);
                i--;
            }
        }

        return vector.elements();
    }

    public void showDocument(URL url)
    {
        showDocument(url, "_self");
    }

    public void showDocument(URL url, String s)
    {
        URL url1 = getDocument().getURL();
        final DocumentPanel evtSource = getOwningDocumentPanel();
        final NamedLink arg = new NamedLink(s, url, url1);
        RequestProcessor.getHJBeanQueue().postRequest(new sunw.hotjava.misc.RequestProcessor.Request() {

            public void execute()
            {
                evtSource.dispatchDocumentEvent(1002, arg);
            }

        }
);
    }

    public void addDocumentListener(DocumentListener documentlistener)
    {
        docListeners.add(documentlistener);
    }

    public void removeDocumentListener(DocumentListener documentlistener)
    {
        docListeners.remove(documentlistener);
        doc = null;
    }

    public void showStatus(String s)
    {
        DocumentEvent documentevent = new DocumentEvent(this, 1000, s);
        docListeners.documentChanged(documentevent);
        Formatter formatter = getFormatter();
        if(formatter != null)
            formatter.dispatchDocumentEvent(1000, s);
    }

    protected void showAppletLog(String s)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        String s1 = hjbproperties.getProperty(s);
        if(s1 == null)
        {
            s1 = hjbproperties.getProperty(s);
            if(s1 == null)
                s1 = s;
        }
        exceptionStream.println(s1);
        System.out.println(s1);
    }

    protected void showAppletException(Throwable throwable)
    {
        repaint();
        exceptionStream.println(throwable.getLocalizedMessage() + ":\n");
        throwable.printStackTrace(exceptionStream);
        throwable.printStackTrace();
    }

    private Frame getFrame()
    {
        for(Container container = super.getParent(); container != null; container = container.getParent())
            if(container instanceof Frame)
                return (Frame)container;

        return null;
    }

    protected DocumentPanel getOwningDocumentPanel()
    {
        for(Container container = super.getParent(); container != null; container = container.getParent())
            if(container instanceof DocumentPanel)
                return (DocumentPanel)container;

        return null;
    }

    void showException()
    {
        if(exceptionDialog == null)
        {
            exceptionStream.flush();
            exceptionDialog = new ExceptionDialog("applet.error", getFrame(), exceptionTextBuffer.toString());
        }
        if(!exceptionDialog.isVisible())
            exceptionDialog.show();
    }

    public void activateSubItems()
    {
    }

    public void start()
    {
        Applet applet = getApplet();
        if(applet != null)
            applet.setBackground(applet.getBackground());
        sendEvent(3);
    }

    public void stop()
    {
        sendEvent(4);
    }

    public void destroy()
    {
        destroy(false);
    }

    private void destroy(boolean flag)
    {
        synchronized(this)
        {
            if(destroyed)
                return;
            destroyed = true;
        }
        if(flag)
        {
            super.status = 0;
            Applet applet = super.applet;
            super.applet = null;
            if(applet != null)
                remove(applet);
        } else
        {
            sendEvent(5);
            sendEvent(0);
            sendEvent(6);
        }
        decrementCodeBaseRefCount(getCodeBase(), flag);
    }

    private void decrementCodeBaseRefCount(URL url, boolean flag)
    {
        if(super.am == null)
            super.am = getAM();
        if(super.am == null)
            return;
        super.am.refClassLoader(getCodeBase(), null, null);
        Integer integer = super.am.deRefClassLoader(getCodeBase(), flag);
        if(integer.intValue() != 1 || isDocProtocolApplet())
        {
            super.am.deRefClassLoader(getCodeBase(), flag);
            return;
        }
        int i;
        if(flag)
            i = 0;
        else
            i = getThreadCleanupInterval();
        if(i >= 0)
        {
            int j;
            if(flag)
                j = 0;
            else
                j = getThreadDestroyInterval();
            AppletPanelThreadGroupRef appletpanelthreadgroupref = new AppletPanelThreadGroupRef(super.am.getThreadGroup(getCodeBase()));
            ThreadInterruptRequest threadinterruptrequest = new ThreadInterruptRequest(appletpanelthreadgroupref, j, super.am, getCodeBase());
            RequestProcessor.getHJBeanQueue().postRequest(threadinterruptrequest, i);
        }
        super.am.removeThreadGroup(getCodeBase());
    }

    public void destroyIfNeeded(boolean flag)
    {
        if(!destroyed)
        {
            if(isActive())
            {
                stop();
                destroy(flag);
                return;
            }
            if(super.status == 4)
                destroy();
        }
    }

    public void interruptLoading()
    {
        stopLoading();
    }

    public void notify(Document document, int i, int j, int k)
    {
    }

    public void reformat()
    {
    }

    public int findYFor(int i)
    {
        return 0;
    }

    private void processAppletLoaded(AppletEvent appletevent)
    {
        if(super.status == 7)
        {
            determineSize(errorImage, 0, -1, -1);
            return;
        }
        Color color = (Color)doc.getProperty("background.color");
        if(color != null)
            setBackground(color);
        Color color1 = (Color)doc.getProperty("text.color");
        if(color1 != null)
            setForeground(color1);
    }

    private void processAppletResized(AppletEvent appletevent)
    {
        Dimension dimension = (Dimension)appletevent.getArgument();
        if(dimension == null)
            return;
        Dimension dimension1 = getSize();
        if(dimension1.equals(dimension))
            return;
        setSize(dimension);
        validate();
        synchronized(Globals.getAwtLock())
        {
            synchronized(doc)
            {
                Formatter formatter = getFormatter();
                if(formatter != null)
                    formatter.notify(doc, 17, item.getIndex() << 12, 4096);
            }
        }
    }

    private void processMouseReleased(MouseEvent mouseevent)
    {
        if(super.status == 7)
        {
            showException();
            return;
        }
        if(!loaded)
        {
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            if(delayLoading)
            {
                String s = "applet.delay.start";
                String s1 = item.getAttributes().toString();
                String s2 = baseURL.toString();
                String s3 = hjbproperties.getPropertyReplace(s, s1, s2);
                showStatus(s3);
                item.adjustDimension();
                sendEvent(1);
                sendEvent(2);
                start();
                synchronized(Globals.getAwtLock())
                {
                    synchronized(doc)
                    {
                        Formatter formatter = getFormatter();
                        if(formatter != null)
                            formatter.notify(doc, 17, item.getIndex() << 12, 4096);
                    }
                }
                loaded = true;
            }
        }
        requestFocus();
    }

    private void determineSize(Image image, int i, int j, int k)
    {
        if((i & 1) == 0)
            j = image.getWidth(this);
        if((i & 2) == 0)
            k = image.getHeight(this);
        if(j < 0 || k < 0)
            return;
        j += 5;
        k += 5;
        int l;
        int i1;
        if(alt == null)
        {
            l = j;
            i1 = k;
        } else
        {
            FontMetrics fontmetrics = Globals.getFontMetrics(altFont);
            l = fontmetrics.stringWidth(alt) + j;
            i1 = fontmetrics.getHeight() <= k ? k : fontmetrics.getHeight();
        }
        int j1 = item.getWidthAttribute(getTopFormatter());
        int k1 = item.getHeightAttribute(getTopFormatter());
        int l1 = j1 <= l ? l : j1;
        int i2 = k1 <= i1 ? i1 : k1;
        if(j1 != 0 && l1 > j1 || k1 != 0 && i2 > k1)
            item.adjustDimension(l1, i2);
        repaint();
    }

    public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1)
    {
        if((i & 0x40) != 0)
            return false;
        if((i & 0x30) != 0)
        {
            determineSize(image, i, l, i1);
            synchronized(Globals.getAwtLock())
            {
                synchronized(doc)
                {
                    Formatter formatter = getFormatter();
                    if(formatter != null)
                        formatter.notify(doc, 17, item.getIndex() << 12, 4096);
                }
            }
        } else
        if((i & 0x80) == 0)
            ScreenUpdater.updater.notify(this, 100L);
        return (i & 0xa0) == 0;
    }

    public void updateClient(Object obj)
    {
        if(obj != null && obj.equals(this))
            doc.removeSizeItemWaitRef(this);
        synchronized(Globals.getAwtLock())
        {
            synchronized(doc)
            {
                Formatter formatter = getFormatter();
                if(formatter != null)
                {
                    int i = item.getIndex();
                    if(super.getParent() instanceof TableElementPanel)
                    {
                        for(int j = i; j > -1; j--)
                        {
                            if(!doc.getTagName(doc.getItem(j)).equals("td"))
                                continue;
                            if(doc.getItem(j).getOffset() > 0)
                                i = j;
                            break;
                        }

                    }
                    getFormatter().notify(doc, 19, i << 12, 4096);
                }
            }
        }
    }

    private static int getThreadDestroyInterval()
    {
        if(threadDestroyInterval == -1)
        {
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            String s = "appletpanel.thread.destroy.interval";
            threadDestroyInterval = hjbproperties.getInteger(s, 500);
        }
        return threadDestroyInterval;
    }

    private static int getThreadCleanupInterval()
    {
        if(threadCleanupInterval == -1)
        {
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            String s = "appletpanel.thread.cleanup.interval";
            threadCleanupInterval = hjbproperties.getInteger(s, 5000);
        }
        return threadCleanupInterval;
    }

    public boolean hasSize()
    {
        return false;
    }

    public void waiterTimedOut()
    {
    }

    public void setObsolete(boolean flag)
    {
    }

    static final String propName = "applet";
    static Image errorImage = null;
    static Image delayImage = null;
    static Font altFont = null;
    private static boolean lazyLoadingNotDone = true;
    public static boolean delayLoading;
    Document doc;
    APPLET item;
    String alt;
    URL baseURL;
    Hashtable param;
    Hashtable paramCase;
    StringWriter exceptionTextBuffer;
    PrintWriter exceptionStream;
    ExceptionDialog exceptionDialog;
    boolean loaded;
    private boolean destroyed;
    DocumentEventMulticaster docListeners;
    private Frame fakeFrame;
    private static int threadDestroyInterval = -1;
    private static int threadCleanupInterval = -1;




}
