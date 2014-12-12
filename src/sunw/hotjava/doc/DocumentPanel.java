// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentPanel.java

package sunw.hotjava.doc;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EventObject;
import java.util.Vector;
import sunw.hotjava.applet.*;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.forms.FormPanel;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.script.ScriptingEngineInterface;
import sunw.hotjava.tags.FramePanel;
import sunw.hotjava.tags.FrameSetPanel;

// Referenced classes of package sunw.hotjava.doc:
//            DocBusyException, DocConstants, DocException, DocFont, 
//            DocPanel, DocParser, DocView, Document, 
//            DocumentEvent, DocumentEventMulticaster, DocumentEventSource, DocumentFormatter, 
//            DocumentFormatterPanel, DocumentFormatterRef, DocumentStack, DocumentState, 
//            Formatter, NamedLink, Range, DocumentListener

public class DocumentPanel extends ScrollPane
    implements DocPanel, DocConstants, DocumentEventSource, DocView
{
    protected class PanelDocListener
        implements DocumentListener, Serializable
    {

        public void documentChanged(DocumentEvent documentevent)
        {
            if(current != null)
                processPanelDocumentEvent(documentevent);
        }

        protected PanelDocListener()
        {
        }
    }

    private final class AppletEventListener
        implements AppletListener
    {

        public void appletStateChanged(AppletEvent appletevent)
        {
            switch(appletevent.getID())
            {
            case 51235: 
                appletLoadingCount++;
                return;

            case 51236: 
                appletLoadingCount--;
                return;
            }
        }

        AppletEventListener()
        {
        }
    }


    public DocumentPanel()
    {
        super(0);
        listeners = new DocumentEventMulticaster();
        marginWidth = 20;
        marginHeight = 10;
        oldWidth = -1;
        oldHeight = -1;
        oldHorizontalInset = -1;
        obsolete = false;
        docListener = new PanelDocListener();
        try
        {
            Globals.initSystem();
        }
        catch(Exception exception)
        {
            System.out.println("Unable to initialize bean:  " + exception);
            exception.printStackTrace();
        }
        initPanel();
    }

    public DocumentPanel(DocFont docfont, int i)
    {
        super(i);
        listeners = new DocumentEventMulticaster();
        marginWidth = 20;
        marginHeight = 10;
        oldWidth = -1;
        oldHeight = -1;
        oldHorizontalInset = -1;
        obsolete = false;
        docListener = new PanelDocListener();
        initPanel();
        setSize(5, 5);
        font = docfont;
        int j = Globals.getFontMetrics(docfont).getAscent();
        Adjustable adjustable = getHAdjustable();
        if(adjustable != null)
            adjustable.setUnitIncrement(j);
        Adjustable adjustable1 = getVAdjustable();
        if(adjustable1 != null)
            adjustable1.setUnitIncrement(j);
    }

    public DocumentPanel(DocFont docfont, int i, FramePanel framepanel)
    {
        this(docfont, i);
        parentPanel = framepanel;
    }

    private void initPanel()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        panel = new DocumentFormatterPanel();
        add(panel);
        panel.setDocumentPanel(this);
        setBackground(hjbproperties.getColor("html.background", null));
        panels.addElement(this);
        setName("XXUntitledFrame" + nameCounter);
        nameCounter++;
        KeyAdapter keyadapter = new KeyAdapter() {

            public void keyPressed(KeyEvent keyevent)
            {
                processKeyEvent(keyevent);
            }

        }
;
        addKeyListener(keyadapter);
    }

    public DocumentFormatterPanel getDocumentFormatterPanel()
    {
        return panel;
    }

    public void addDocumentListener(DocumentListener documentlistener)
    {
        listeners.add(documentlistener);
    }

    public void removeDocumentListener(DocumentListener documentlistener)
    {
        listeners.remove(documentlistener);
    }

    public void dispatchDocumentEvent(int i, Object obj)
    {
        DocumentEvent documentevent = new DocumentEvent(this, i, obj);
        listeners.documentChanged(documentevent);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String s)
    {
        String s1 = name;
        if(s != null)
            s = s.toLowerCase();
        name = s;
        if(s1 != null)
            dispatchDocumentEvent(1030, s1);
        dispatchDocumentEvent(1029, s);
    }

    public Document getDocument()
    {
        if(current != null)
            return current.getDocument();
        else
            return null;
    }

    public void gotoLabel(String s)
    {
        if(current != null)
            current.getFormatter().gotoLabel(s);
    }

    public DocFont getDocFont()
    {
        return font;
    }

    public void setMargins(int i, int j)
    {
        if(marginWidth != i || marginHeight != j)
        {
            marginWidth = i;
            marginHeight = j;
            if(current != null)
                current.getFormatter().setMargins(i, j);
        }
    }

    public void reloadDocument()
    {
        Document document = getDocument();
        if(document != null && document.getURL() != null)
            try
            {
                current.getFormatter().reset();
                current.getFormatter().clearParentBackground();
                current.getFormatter().top();
                document.reload();
                if(document.isOkToFormat())
                    current.getFormatter().forceFormatScreen();
                else
                    document.addView(this);
                String s = document.getURL().getRef();
                if(s != null)
                {
                    gotoLabel(s);
                    return;
                }
            }
            catch(DocException docexception)
            {
                docexception.printStackTrace();
                return;
            }
    }

    void forceRelayout()
    {
        Document document = getDocument();
        if(document != null && document.getURL() != null)
        {
            for(int i = getComponentCount() - 1; i >= 0; i--)
                if(getComponent(i) instanceof DocumentFormatterPanel)
                {
                    Container container = (Container)getComponent(i);
                    Component acomponent[] = container.getComponents();
                    for(int j = container.getComponentCount() - 1; j >= 0; j--)
                        if(acomponent[j] instanceof FormPanel)
                            ((FormPanel)acomponent[j]).forceResize();

                }

            current.getFormatter().reset();
            current.getFormatter().layout();
            validate();
        }
    }

    public boolean showsSameDocument(Document document)
    {
        Document document1 = getDocument();
        if(document1 != null && document1.getURL() != null && document != null && document1.getURL().equals(document.getURL()) && !document1.hasJavaScriptTags())
        {
            String s = document.getURL().getRef();
            if(s != null)
                gotoLabel(s);
            if(document1.getClientPullSpec() != null || document1.isExpired())
                return false;
            return document.getConnector() == null;
        } else
        {
            return false;
        }
    }

    public boolean hasFrameSetPanel(DocumentFormatter documentformatter)
    {
        return documentformatter.hasFrameSetPanel();
    }

    public void reload()
    {
        if(current == null)
            return;
        URL url = current.getURL();
        if(url != null && url.toExternalForm().startsWith("doc:/lib/hotjava/generated___"))
        {
            return;
        } else
        {
            reloadDocument();
            return;
        }
    }

    public void activateItemComponents()
    {
        if(current != null)
            current.getFormatter().activateItemComponents();
    }

    public void activateSubItems()
    {
        if(current != null)
            current.getFormatter().activateSubItems();
    }

    public void start()
    {
        if(current != null)
        {
            current.getFormatter().initializeParent();
            current.getFormatter().start();
        }
    }

    public void stop()
    {
        if(current != null)
            current.getFormatter().stop();
    }

    public void interruptLoading()
    {
        if(current != null)
            current.getFormatter().interruptLoading();
    }

    public void removeNotify()
    {
        stop();
        destroy();
        super.removeNotify();
    }

    public void destroy()
    {
        String s = getName();
        dispatchDocumentEvent(1030, s);
        setFormatter(null);
        if(containingMaster != null)
            containingMaster.removeTopDocListenerFromSource(this);
        if(docListener != null)
        {
            removeDocumentListener(docListener);
            docListener = null;
        }
        panels.removeElement(this);
    }

    public void notify(Document document, int i, int j, int k)
    {
        if(i == 23)
        {
            showNextFormatter();
            next = null;
            document.removeView(this);
            current.getFormatter().forceFormatScreen();
        }
    }

    public void reformat()
    {
    }

    public int findYFor(int i)
    {
        return 0;
    }

    public void print(PrintJob printjob, HotJavaBrowserBean hotjavabrowserbean)
        throws DocBusyException
    {
        DocumentFormatter documentformatter = current.getFormatter();
        if(documentformatter.hasFrameSetPanel())
        {
            int i = documentformatter.countPanels();
            for(int j = 0; j < i; j++)
            {
                Component component = documentformatter.getPanel(j);
                if(component instanceof FrameSetPanel)
                    ((FrameSetPanel)component).print(printjob, hotjavabrowserbean);
            }

            return;
        } else
        {
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            int k = hjbproperties.getInteger("hotjava.printfontsize", 0);
            DocFont docfont = DocFont.findFont(true, 0, 5 + k);
            (new DocumentFormatter(current.getFormatter(), docfont)).print(printjob, hotjavabrowserbean);
            return;
        }
    }

    public boolean find(String s)
    {
        return find(s, true);
    }

    public boolean find(String s, boolean flag)
    {
        return find(s, flag, false);
    }

    public boolean find(String s, boolean flag, boolean flag1)
    {
        Document document = getDocument();
        Object obj = null;
        DocumentFormatter documentformatter = current.getFormatter();
        synchronized(Globals.getAwtLock())
        {
            synchronized(document)
            {
                int i = ((Formatter) (documentformatter)).ds.selEnd;
                Range range;
                if(i < 0)
                    range = document.find(s, 0, flag);
                else
                if((range = document.find(s, i, flag)) == null)
                    range = document.find(s, 0, flag);
                if(range == null)
                {
                    boolean flag2 = false;
                    return flag2;
                }
                try
                {
                    int j = documentformatter.findYFor(range.minVal);
                    int k = documentformatter.getDocumentY();
                    if(j < k || j > k + ((Formatter) (documentformatter)).height)
                        documentformatter.scrollTo(0, j);
                }
                catch(IllegalArgumentException _ex) { }
                documentformatter.showSelection(false);
                documentformatter.select(range.minVal, range.maxVal);
                documentformatter.showSelection(true);
                boolean flag3 = true;
                return flag3;
            }
        }
    }

    int getVerticalInset(boolean flag)
    {
        Insets insets = insets();
        int i = getHScrollbarHeight();
        int j = insets.top + insets.bottom;
        if(flag && j > i)
            j -= i;
        return j;
    }

    int getHorizontalInset(boolean flag)
    {
        Insets insets = insets();
        int i = getVScrollbarWidth();
        int j = insets.left + insets.right;
        if(flag && j >= i)
            j -= i;
        return j;
    }

    private void setFormatterSize(Dimension dimension, DocumentFormatter documentformatter)
    {
        int i = dimension.width;
        int j = dimension.height;
        if(documentformatter.hasFrameSetPanel())
        {
            int k = getHorizontalInset(true);
            int i1 = getVerticalInset(true);
            documentformatter.setSize(i - k, j - i1, 0, true);
            return;
        } else
        {
            int l = getHorizontalInset(false);
            int j1 = getVerticalInset(true);
            int k1 = l - getHorizontalInset(true);
            i -= l;
            j -= j1;
            documentformatter.setSize(i, j, k1);
            return;
        }
    }

    protected void fitPanelToDocument()
    {
        DocumentFormatter documentformatter = current.getFormatter();
        Dimension dimension = getSize();
        int i = getVScrollbarWidth();
        int j = getHScrollbarHeight();
        Insets insets = getInsets();
        boolean flag = insets.left + insets.right >= i;
        boolean flag1 = insets.top + insets.bottom >= j;
        int k = dimension.width - (insets.left + insets.right) - (flag ? i : 0);
        int l = dimension.height - (insets.top + insets.bottom) - (flag1 ? j : 0);
        int i1 = panel.getSize().width;
        int j1 = panel.getSize().height;
        int k1 = ((Formatter) (documentformatter)).docWidth;
        int l1 = ((Formatter) (documentformatter)).docHeight;
        int i2 = i1;
        int j2 = j1;
        if(k1 <= k)
            if(l1 > l)
                i2 = k - i;
            else
                i2 = k;
        if(l1 <= l)
            if(k1 > k)
                j2 = l - j;
            else
                j2 = l;
        if(k1 > k)
            i2 = k1;
        if(l1 > l)
            j2 = l1;
        if(i2 > MaxWindowSize)
            i2 = MaxWindowSize;
        if(j2 > MaxWindowSize)
            j2 = MaxWindowSize;
        if(i2 != i1 || j2 != j1)
        {
            Document document = getDocument();
            if(document != null)
            {
                synchronized(Globals.getAwtLock())
                {
                    synchronized(document)
                    {
                        panel.setSize(i2, j2);
                        validate();
                    }
                }
                return;
            }
            panel.setSize(i2, j2);
            validate();
        }
    }

    private void showNextFormatter()
    {
        if(next != null)
            showIt(next);
    }

    private void setFormatter(DocumentFormatterRef documentformatterref)
    {
        if(current != null && current != documentformatterref)
        {
            DocumentFormatter documentformatter = getFormatter();
            dispatchDocumentEvent(1018, documentformatter);
            documentformatter.getDocument().removeDocumentListener(docListener);
            documentformatter.removeDocumentListener(docListener);
            documentformatter.removeListeners();
            deregisterInterestIn(documentformatter.getDocument());
            current.getFormatter().setObsolete(true);
            current.removeClient();
        }
        if(current != documentformatterref)
            current = documentformatterref;
        if(documentformatterref == null)
            return;
        DocumentFormatter documentformatter1 = current.getFormatter();
        if(documentformatterref.offsetStored)
            documentformatterref.offsetStored = false;
        else
        if(documentformatter1.getDocument().getURL() != null && documentformatter1.getDocument().getURL().getRef() != null)
            documentformatter1.gotoLabel(documentformatter1.getDocument().getURL().getRef());
        else
            documentformatter1.scrollTo(0, 0);
        dispatchDocumentEvent(1017, documentformatter1);
        Container container = getParent();
        if(!documentformatter1.hasFrameSetPanel())
        {
            if(!(container instanceof FramePanel))
            {
                dispatchDocumentEvent(1041, null);
                return;
            }
            dispatchDocumentEvent(1041, container);
            ((FramePanel)container).setSelected(true);
        }
    }

    protected void registerInterestIn(Document document)
    {
    }

    protected void deregisterInterestIn(Document document)
    {
    }

    public DocumentFormatter getFormatter()
    {
        if(current == null)
            return null;
        else
            return current.getFormatter();
    }

    public DocumentFormatterRef getFormatterRef()
    {
        return current;
    }

    protected Frame getContainingFrame()
    {
        for(Container container = getParent(); container != null; container = container.getParent())
            if(container instanceof Frame)
                return (Frame)container;

        return null;
    }

    public DocumentPanel getContainingDocumentPanel()
    {
        for(Container container = getParent(); container != null; container = container.getParent())
            if(container instanceof DocumentPanel)
                return (DocumentPanel)container;

        return null;
    }

    protected HotJavaBrowserBean getContainingHotJavaBrowserBean()
    {
        return HotJavaBrowserBean.getContainingHotJavaBrowserBean(this);
    }

    public void show(Document document)
    {
        show(new DocumentFormatterRef(panel, document, font));
    }

    public void show(DocumentFormatterRef documentformatterref)
    {
        Document document = documentformatterref.getDocument();
        DocumentFormatter documentformatter = documentformatterref.getFormatter();
        documentformatter.setObsolete(false);
        documentformatter.initializeParent();
        Container container = getParent();
        if(container != null && (container instanceof FramePanel))
            documentformatter.setMargins(marginWidth, marginHeight);
        documentformatter.start();
        documentformatterref.addClient();
        documentformatter.addDocumentListener(docListener);
        documentformatter.getDocument().addDocumentListener(docListener);
        documentformatter.addListeners();
        registerInterestIn(documentformatter.getDocument());
        documentformatter.processDocumentEvent(new DocumentEvent(this, 1043, null));
        if(document.isOkToFormat())
        {
            showIt(documentformatterref);
            documentformatter.setToFormattedSize(panel);
            documentformatter.dispatchDocumentEvent(1028, documentformatter);
            return;
        }
        if(next != null)
            next.getFormatter().stop();
        document.addView(this);
        if(current != null)
            current.getFormatter().unregisterListeners();
        next = documentformatterref;
        if(current == null)
            current = documentformatterref;
        dispatchDocumentEvent(1006, documentformatterref.getDocument());
    }

    public void showIt(DocumentFormatterRef documentformatterref)
    {
        if(!documentformatterref.equals(current))
            stop();
        setFormatter(documentformatterref);
        DocumentFormatter documentformatter = documentformatterref.getFormatter();
        setMargins(marginWidth, marginHeight);
        setFormatterSize(getSize(), documentformatter);
        if(current != null)
        {
            Document document = current.getDocument();
            if(document.getClientPullSpec() != null)
                requestTimedRefresh(document.getClientPullSpec());
        }
        if(documentformatter.isFormatDone())
            fitPanelToDocument();
        if(!isVisible())
            setVisible(true);
        dispatchDocumentEvent(1006, getDocument());
    }

    public boolean isLoadingNext()
    {
        return next != null;
    }

    public void doLayout()
    {
        super.doLayout();
        Dimension dimension = getSize();
        int i = getHorizontalInset(false);
        if(oldWidth != dimension.width || oldHeight != dimension.height || oldHorizontalInset != i)
        {
            oldWidth = dimension.width;
            oldHeight = dimension.height;
            oldHorizontalInset = i;
            if(current != null)
            {
                DocumentFormatter documentformatter = current.getFormatter();
                setFormatterSize(dimension, documentformatter);
            }
        }
    }

    protected void requestTimedRefresh(String s)
    {
        if(current != null && current.getDocument() != null)
            dispatchDocumentEvent(1002, new NamedLink("_refresh " + s, current.getDocument().getURL()));
    }

    public void processPanelDocumentEvent(DocumentEvent documentevent)
    {
        switch(documentevent.getID())
        {
        default:
            break;

        case 1012: 
            interruptLoading();
            return;

        case 1003: 
            Object obj = documentevent.getArgument();
            DocumentFormatter documentformatter = getFormatter();
            if(documentformatter != null && documentformatter.isFormatDone())
            {
                fitPanelToDocument();
                return;
            }
            if(obj != null && (obj instanceof DocumentFormatter))
            {
                fitPanelToDocument();
                return;
            }
            break;

        case 1004: 
            if(this instanceof HotJavaBrowserBean)
            {
                listeners.documentChanged(documentevent);
                return;
            }
            break;
        }
    }

    protected boolean setsBaseURL()
    {
        return false;
    }

    public void processKeyEvent(KeyEvent keyevent)
    {
        if(keyevent.isActionKey())
            processKeyActionEvent(keyevent);
        else
            processPanelKeyEvent(keyevent);
        Container container = getParent();
        if(container != null)
            container.dispatchEvent(keyevent);
    }

    protected void processPanelKeyEvent(KeyEvent keyevent)
    {
    }

    public void processKeyActionEvent(KeyEvent keyevent)
    {
        Object obj = keyevent.getSource();
        DocumentFormatter documentformatter = getFormatter();
        switch(keyevent.getKeyCode())
        {
        default:
            break;

        case 36: // '$'
            if(!(obj instanceof TextComponent))
            {
                documentformatter.top();
                return;
            }
            break;

        case 34: // '"'
            documentformatter.scrollBy(0, ((Formatter) (documentformatter)).height);
            return;

        case 33: // '!'
            documentformatter.scrollBy(0, -((Formatter) (documentformatter)).height);
            return;

        case 35: // '#'
            if(!(obj instanceof TextComponent))
            {
                documentformatter.scrollTo(0, ((Formatter) (documentformatter)).docHeight - ((Formatter) (documentformatter)).height);
                return;
            }
            break;

        case 38: // '&'
            if(!(obj instanceof TextArea))
            {
                Adjustable adjustable = getVAdjustable();
                documentformatter.scrollBy(0, -adjustable.getUnitIncrement());
                return;
            }
            break;

        case 40: // '('
            if(!(obj instanceof TextArea))
            {
                Adjustable adjustable1 = getVAdjustable();
                documentformatter.scrollBy(0, adjustable1.getUnitIncrement());
                return;
            }
            break;

        case 37: // '%'
            if(!(obj instanceof TextComponent) && keyevent.getModifiers() == 0)
            {
                Adjustable adjustable2 = getHAdjustable();
                documentformatter.scrollBy(-adjustable2.getUnitIncrement(), 0);
                return;
            }
            break;

        case 39: // '\''
            if(!(obj instanceof TextComponent) && keyevent.getModifiers() == 0)
            {
                Adjustable adjustable3 = getHAdjustable();
                documentformatter.scrollBy(adjustable3.getUnitIncrement(), 0);
                return;
            }
            break;
        }
    }

    public void removePanel(DocumentPanel documentpanel)
    {
        panels.removeElement(documentpanel);
    }

    public void addNotify()
    {
        super.addNotify();
        containingMaster = HotJavaBrowserBean.getContainingHotJavaBrowserBean(this);
        containingMaster.addTopDocListenerToSource(this);
    }

    public boolean isInFrame()
    {
        return parentPanel != null;
    }

    public FramePanel getParentFramePanel()
    {
        return parentPanel;
    }

    public void setDocumentSource(Reader reader)
    {
        setDocumentSource(reader, "text/html", false, null);
    }

    public void setDocumentSource(Reader reader, String s, boolean flag, String s1)
    {
        Document document = makeNewGeneratedDocument(s1);
        try
        {
            new DocParser(document, reader);
            DocumentStack documentstack = getContainingHotJavaBrowserBean().getDocumentStack();
            DocumentFormatterRef documentformatterref = new DocumentFormatterRef(getDocumentFormatterPanel(), document, getDocFont());
            if(flag)
            {
                documentstack.replace(this, documentformatterref);
                return;
            } else
            {
                show(documentformatterref);
                return;
            }
        }
        catch(DocException _ex)
        {
            return;
        }
    }

    protected Document makeNewGeneratedDocument(String s)
    {
        URL url = null;
        URL url1 = null;
        ScriptingEngineInterface scriptingengineinterface = Globals.getScriptingEngine();
        if(scriptingengineinterface != null)
            url1 = scriptingengineinterface.getCodebaseFromCaller();
        try
        {
            if(url1 == null)
                url1 = new URL("doc:/lib/hotjava/");
            String s1 = "generated___" + generatedID + url1.getFile();
            int i = s1.lastIndexOf('/');
            if(i != -1)
                s1 = s1.substring(i + 1);
            url = s != null ? new URL(url1, "generated___" + generatedID++ + s1 + s) : new URL(url1, "generated___" + generatedID++ + s1);
            url1 = new URL(url1, "");
        }
        catch(MalformedURLException _ex) { }
        Document document = new Document();
        try
        {
            document.setProperty("base", url1);
            document.setProperty("url", url);
            document.setProperty("documentURL", "");
            document.setProperty("documentString", "");
        }
        catch(DocException _ex) { }
        return document;
    }

    public void setObsolete(boolean flag)
    {
        obsolete = flag;
    }

    public boolean getObsolete()
    {
        return obsolete;
    }

    static final long serialVersionUID = 0x84b7173d95d8c849L;
    private static int MaxWindowSize = System.getProperty("os.name").startsWith("Windows") ? 32767 : 0x7fffffff;
    private static final String GENERATEDDocBase = "doc:/lib/hotjava/";
    private static final String GENERATEDDocName = "generated___";
    private static final String GENERATEDDocPrefix = "doc:/lib/hotjava/generated___";
    protected String name;
    protected DocumentFormatterPanel panel;
    protected DocumentEventMulticaster listeners;
    protected DocumentFormatterRef current;
    protected DocumentFormatterRef next;
    private static int nameCounter;
    protected DocFont font;
    protected int marginWidth;
    protected int marginHeight;
    private int oldWidth;
    private int oldHeight;
    private int oldHorizontalInset;
    static Vector panels = new Vector();
    protected int appletLoadingCount;
    private HotJavaBrowserBean containingMaster;
    private FramePanel parentPanel;
    private boolean obsolete;
    private PanelDocListener docListener;
    private static int generatedID;

}
