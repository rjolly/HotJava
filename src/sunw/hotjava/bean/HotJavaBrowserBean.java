// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotJavaBrowserBean.java

package sunw.hotjava.bean;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.beans.*;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.io.ByteToCharConverter;
import sun.net.ProgressData;
import sun.net.ProgressEntry;
import sunw.hotjava.applet.AppletManager;
import sunw.hotjava.doc.DocException;
import sunw.hotjava.doc.DocFont;
import sunw.hotjava.doc.DocPanel;
import sunw.hotjava.doc.DocParser;
import sunw.hotjava.doc.Document;
import sunw.hotjava.doc.DocumentCache;
import sunw.hotjava.doc.DocumentEvent;
import sunw.hotjava.doc.DocumentEventSource;
import sunw.hotjava.doc.DocumentFormatter;
import sunw.hotjava.doc.DocumentFormatterPanel;
import sunw.hotjava.doc.DocumentFormatterRef;
import sunw.hotjava.doc.DocumentListener;
import sunw.hotjava.doc.DocumentPanel;
import sunw.hotjava.doc.DocumentStack;
import sunw.hotjava.doc.ElementInfo;
import sunw.hotjava.doc.Formatter;
import sunw.hotjava.doc.NamedLink;
import sunw.hotjava.doc.TextItem;
import sunw.hotjava.forms.FormPanel;
import sunw.hotjava.misc.*;
import sunw.hotjava.tags.*;

// Referenced classes of package sunw.hotjava.bean:
//            BeanConfirmDialog, BrowserHistoryEvent, BrowserHistoryListener, ClientPuller, 
//            CurrentDocument, DocumentSelection, FrameToken, HTMLBrowsable, 
//            HotJavaDocumentStack, ImageCacher, InvalidFrameException, LinkEvent, 
//            LinkListener, URLPooler, CookieJarInterface

public class HotJavaBrowserBean extends DocumentPanel
    implements Externalizable, BrowserHistoryListener, Observer, Serializable, HTMLBrowsable, PropertyChangeListener, VetoableChangeListener, Runnable
{
    protected class DocListener
        implements DocumentListener, Serializable
    {

        public void documentChanged(DocumentEvent documentevent)
        {
            processDocumentEvent(documentevent);
        }

        protected DocListener()
        {
        }
    }

    private class NewBeanPoster
        implements BeanListener
    {

        public void targetBean(HotJavaBrowserBean hotjavabrowserbean)
        {
            if(docToShowInNewBean != null)
            {
                hotjavabrowserbean.interruptLoading();
                while(!hotjavabrowserbean.getDocument().doneParsing()) 
                    try
                    {
                        Thread.sleep(500L);
                    }
                    catch(InterruptedException _ex) { }
                hotjavabrowserbean.internalGoto(postSource, postTarget, docToShowInNewBean, false);
                docToShowInNewBean = null;
            }
        }

        NewBeanPoster()
        {
        }
    }

    private class LinkEventRequest extends sunw.hotjava.misc.RequestProcessor.Request
    {

        public void execute()
        {
            HTMLBrowsable htmlbrowsable = listener.hyperlinkPerformed(event);
            if(htmlbrowsable != null && notifier != null)
                notifier.targetBean((HotJavaBrowserBean)htmlbrowsable);
        }

        private LinkListener listener;
        private LinkEvent event;
        private BeanListener notifier;

        LinkEventRequest(LinkListener linklistener, LinkEvent linkevent, BeanListener beanlistener)
        {
            listener = linklistener;
            event = linkevent;
            notifier = beanlistener;
        }
    }

    class Blinker extends sunw.hotjava.misc.RequestProcessor.Request
    {

        public void stop()
        {
            stop = true;
        }

        public void execute()
        {
            if(getFormatter() != null)
            {
                HotJavaBrowserBean.blinkon = properties.getBoolean("hotjava.blink");
                DocumentFormatter documentformatter = getFormatter();
                Vector vector = documentformatter.getBlinkers();
                for(Enumeration enumeration = vector.elements(); enumeration.hasMoreElements();)
                    try
                    {
                        boolean flag1 = true;
                        TextItem textitem = (TextItem)enumeration.nextElement();
                        if(HotJavaBrowserBean.blinkon)
                            textitem.invert();
                        else
                            flag1 = textitem.noblink();
                        if(flag1)
                        {
                            int i = textitem.getIndex();
                            Document document = getDocument();
                            if(document != null)
                                document.paint(i << 12, i + 1 << 12);
                        }
                    }
                    catch(Exception exception)
                    {
                        System.out.println(exception);
                    }

            }
            if(!stop)
                RequestProcessor.getHJBeanQueue().postRequest(this, delay);
        }

        HJBProperties properties;
        private HotJavaBrowserBean bean;
        int delay;
        boolean stop;

        public Blinker(HotJavaBrowserBean hotjavabrowserbean1, int i)
        {
            properties = HJBProperties.getHJBProperties("beanPropertiesKey");
            stop = false;
            bean = hotjavabrowserbean1;
            delay = i;
            RequestProcessor.getHJBeanQueue().postRequest(this, i);
        }
    }

    public interface BeanListener
    {

        public abstract void targetBean(HotJavaBrowserBean hotjavabrowserbean);
    }


    public HotJavaBrowserBean()
    {
        topDocListener = new DocListener();
        currentCursor = 0;
        statusMessage = "";
        activeMessage = "";
        defaultMessage = "";
        errorMessage = "";
        documentTitle = "";
        changeSupport = new PropertyChangeSupport(this);
        vetoChangeSupport = new VetoableChangeSupport(this);
        fLastKnownFreeMem = -1L;
        curDoc = new CurrentDocument();
        isSecure = false;
        connProgress = new long[10];
        cloneNecessary = true;
        nextAvailable = false;
        previousAvailable = false;
        fireDocumentPropertyChange = true;
        RequestProcessor.getHJBeanQueue();
        documentStack = new DocumentStack(this);
        int i = Integer.getInteger("blink.delay", 850).intValue();
        blinker = new Blinker(this, i);
    }

    protected HotJavaBrowserBean(HotJavaBrowserBean hotjavabrowserbean)
    {
        this();
    }

    public Object getSelector()
    {
        return selector;
    }

    public Object clone()
    {
        return new HotJavaBrowserBean();
    }

    public boolean canForward()
    {
        return documentStack.canForward();
    }

    public void forward()
    {
        documentStack.forward();
    }

    public boolean canBack()
    {
        return documentStack.canBack();
    }

    public void back()
    {
        documentStack.back();
    }

    public DocumentPanel findPanel(Container container, String s)
    {
        if(s != null)
        {
            if(container != null && (container instanceof DocumentPanel))
            {
                DocumentPanel documentpanel = (DocumentPanel)container;
                if(s.equalsIgnoreCase(documentpanel.getName()) && !documentpanel.getObsolete())
                    return documentpanel;
            }
            int i = container.countComponents();
            for(int j = 0; j < i; j++)
            {
                Component component = container.getComponent(j);
                if(component instanceof DocumentPanel)
                {
                    DocumentPanel documentpanel1 = (DocumentPanel)component;
                    if(s.equalsIgnoreCase(documentpanel1.getName()) && !documentpanel1.getObsolete())
                        return documentpanel1;
                }
                if(component instanceof Container)
                {
                    DocumentPanel documentpanel2 = findPanel((Container)component, s);
                    if(documentpanel2 != null)
                        return documentpanel2;
                }
            }

        }
        return null;
    }

    protected void showDocument(DocumentPanel documentpanel, URL url, URL url1)
    {
        Document document = DocumentCache.getDocument(url, url1, this);
        showDocument(documentpanel, document);
    }

    protected void showDocument(DocumentPanel documentpanel, Document document)
    {
        if(documentpanel.showsSameDocument(document))
            return;
        DocumentFormatterRef documentformatterref = new DocumentFormatterRef(documentpanel.getDocumentFormatterPanel(), document, super.font);
        synchronized(documentStack)
        {
            documentStack.push(documentpanel, documentformatterref);
            documentpanel.show(documentformatterref);
        }
        notifyHistoryChanges();
    }

    public void handlePOST(String s, Document document, String s1)
    {
        URL url = document.getURL();
        if(!isSetCurrentDocumentAllowed(url, s, true) || url == null)
            return;
        if(s == null || s.equals(""))
            s = "_self";
        s = s.toLowerCase();
        Object obj = getPanelToShowDocIn(s1, s);
        if(s.equals("_top") || s.equals("_self") || s.equals("_parent"))
        {
            if(obj == null)
                obj = this;
            showDocument(((DocumentPanel) (obj)), document);
            return;
        }
        if(!s.equals("_blank") && obj != null)
        {
            showDocument(((DocumentPanel) (obj)), document);
            return;
        } else
        {
            docToShowInNewBean = document;
            handleLinkEventWithDefault(s, null, s1, new NewBeanPoster());
            return;
        }
    }

    private DocumentPanel getPanelToShowDocIn(String s, String s1)
    {
        DocumentPanel documentpanel = findPanel(this, s);
        if(s1.equals("_self"))
            return documentpanel;
        if(s1.equals("_parent"))
            return documentpanel.getContainingDocumentPanel();
        if(s1.equals("_top"))
            return this;
        if(s1.equals("_blank"))
            return null;
        else
            return findPanel(this, s1);
    }

    public void handleNewHREF(String s, URL url, String s1, Document document)
    {
        if(!isSetCurrentDocumentAllowed(url, s, true) || url == null)
            return;
        activeMessage = defaultMessage = "";
        if(s == null || s.equals(""))
            s = "_self";
        DocumentPanel documentpanel = findPanel(this, s);
        DocumentPanel documentpanel1 = findPanel(this, s1);
        URL url1 = null;
        if(documentpanel1 != null && documentpanel1.getDocument() != null)
            url1 = documentpanel1.getDocument().getURL();
        if(documentpanel1 != null && s.equalsIgnoreCase("_self"))
            if(document == null)
            {
                showDocument(documentpanel1, url, url1);
                return;
            } else
            {
                showDocument(documentpanel1, document);
                return;
            }
        if(documentpanel1 != null && s.equalsIgnoreCase("_parent"))
        {
            DocumentPanel documentpanel2 = documentpanel1.getContainingDocumentPanel();
            if(documentpanel2 == null)
                documentpanel2 = documentpanel1;
            if(document == null)
            {
                showDocument(documentpanel2, url, url1);
                return;
            } else
            {
                showDocument(documentpanel2, document);
                return;
            }
        }
        if(s.startsWith("_refresh"))
        {
            setupClientPull(s, url1, documentpanel1);
            return;
        }
        if(documentpanel != null)
            if(document == null)
            {
                showDocument(documentpanel, url, url1);
                return;
            } else
            {
                showDocument(documentpanel, document);
                return;
            }
        if(document == null)
        {
            showDocument(this, url, url1);
            return;
        } else
        {
            showDocument(this, document);
            return;
        }
    }

    private void pushInternalLink(DocumentPanel documentpanel)
    {
        documentStack.push(documentpanel, documentpanel.getFormatterRef());
    }

    public void setDocFont(DocFont docfont)
    {
        super.font = docfont;
        if(super.current != null)
            super.current.setDocFont(docfont);
        documentStack.setDocFont(docfont);
    }

    protected void registerInterestIn(Document document)
    {
        document.addPropertyChangeListener(this);
        document.addVetoableChangeListener(this);
    }

    protected void deregisterInterestIn(Document document)
    {
        document.removePropertyChangeListener(this);
        document.removeVetoableChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent propertychangeevent)
    {
        String s = propertychangeevent.getPropertyName();
        if(s.equals("documentLoadStatus"))
        {
            setStatusMessage((String)propertychangeevent.getNewValue());
            return;
        }
        if(s.equals("documentTitle"))
        {
            setDocumentTitle((String)propertychangeevent.getNewValue());
            return;
        }
        if(s.equals("documentError"))
        {
            setErrorMessage((String)propertychangeevent.getNewValue());
            return;
        }
        if(s.equals("documentString"))
            refreshDocumentString((String)propertychangeevent.getNewValue());
    }

    protected Frame getContainingFrame()
    {
        for(Container container = getParent(); container != null; container = container.getParent())
            if(container instanceof Frame)
                return (Frame)container;

        return null;
    }

    private void setCurrentCursor(int i)
    {
        currentCursor = i;
        Frame frame = getContainingFrame();
        if(frame != null)
            frame.setCursor(Cursor.getPredefinedCursor(i));
    }

    public HotJavaBrowserBean getSelfRef()
    {
        return this;
    }

    public boolean handleJavaScriptURL(String s, String s1, String s2, DocumentPanel documentpanel, String s3, String s4)
    {
        if(s != null)
        {
            String s5 = null;
            String s6 = s.trim().toLowerCase();
            if(s6.startsWith("javascript:"))
                s5 = s.substring("javascript:".length());
            else
            if(s6.startsWith("doc://unknown.protocol/javascript:"))
                s5 = s.substring("doc://unknown.protocol/javascript:".length());
            if(s5 != null)
            {
                if(s1 == null)
                    s1 = getName();
                if(s2 == null)
                    s2 = s1;
                Object obj = getPanelToShowDocIn(s1, s2);
                if(obj == null)
                    obj = documentpanel == null ? ((Object) (this)) : ((Object) (documentpanel));
                DocumentFormatter documentformatter = ((DocumentPanel) (obj)).getFormatter();
                if(documentformatter == null)
                {
                    ((DocumentPanel) (obj)).show(new Document());
                    documentformatter = ((DocumentPanel) (obj)).getFormatter();
                }
                String s7 = Formatter.handleScript(documentformatter, s5, s3, 0, null);
                if(s7 != null)
                    ((DocumentPanel) (obj)).setDocumentSource(new StringReader(s7), "text/html", false, s4);
                return true;
            }
        }
        return false;
    }

    public void addLinkListener(LinkListener linklistener)
    {
        if(linkListeners == null)
            linkListeners = new Vector(2, 2);
        linkListeners.addElement(linklistener);
    }

    public void removeLinkListener(LinkListener linklistener)
    {
        if(linkListeners != null)
        {
            linkListeners.removeElement(linklistener);
            if(linkListeners.size() == 0)
                linkListeners = null;
        }
    }

    public void handleLinkEventWithDefault(String s, URL url, String s1, BeanListener beanlistener)
    {
        String s2 = null;
        if(url != null)
            s2 = url.toExternalForm();
        if(linkListeners != null)
        {
            LinkEvent linkevent = new LinkEvent(s1, s, s2, this);
            fireLinkEvent(linkevent, beanlistener);
            return;
        }
        if(!handleJavaScriptURL(s2, s1, s, this, "handleLinkEventWithDefault", null))
        {
            handleNewHREF(s, url, s1, null);
            if(beanlistener != null)
                beanlistener.targetBean(null);
        }
    }

    public void handleLinkEvent(LinkEvent linkevent)
    {
        String s = linkevent.getHREF();
        if(!handleJavaScriptURL(s, linkevent.getFrameSource(), linkevent.getTarget(), this, "handleLinkEvent", null))
            try
            {
                URL url = new URL(s);
                handleNewHREF(linkevent.getTarget(), url, linkevent.getFrameSource(), null);
                return;
            }
            catch(MalformedURLException _ex)
            {
                return;
            }
        else
            return;
    }

    private void fireLinkEvent(LinkEvent linkevent, BeanListener beanlistener)
    {
        if(linkListeners != null)
        {
            int i = linkListeners.size();
            for(int j = 0; j < i; j++)
            {
                LinkListener linklistener = (LinkListener)linkListeners.elementAt(j);
                LinkEventRequest linkeventrequest = new LinkEventRequest(linklistener, linkevent, beanlistener);
                RequestProcessor.getHJBeanQueue().postRequest(linkeventrequest);
            }

        }
    }

    private void doDocumentCleanup()
    {
        String s = defaultMessage;
        defaultMessage = "";
        changeSupport.firePropertyChange("defaultStatusMessage", s, "");
    }

    public void addPropertyChangeListener(PropertyChangeListener propertychangelistener)
    {
        changeSupport.addPropertyChangeListener(propertychangelistener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertychangelistener)
    {
        changeSupport.removePropertyChangeListener(propertychangelistener);
    }

    public void addVetoableChangeListener(VetoableChangeListener vetoablechangelistener)
    {
        vetoChangeSupport.addVetoableChangeListener(vetoablechangelistener);
    }

    public void removeVetoableChangeListener(VetoableChangeListener vetoablechangelistener)
    {
        vetoChangeSupport.removeVetoableChangeListener(vetoablechangelistener);
    }

    public void setDocumentString(String s)
    {
        if(s == null || handleJavaScriptURL(s, getName(), getName(), this, "setDocumentString", null) || s.equals(curDoc.documentString))
            return;
        if(getDocFont() == null)
        {
            startupURLString = s;
            return;
        }
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long l = runtime.freeMemory();
        changeSupport.firePropertyChange("freeMemory", new Long(fLastKnownFreeMem), new Long(fLastKnownFreeMem = l));
        s = Globals.canonicalize(s);
        try
        {
            CurrentDocument currentdocument = new CurrentDocument();
            if(curDoc.documentURL == null || !curDoc.documentURL.toString().equals(s))
                currentdocument.documentURL = new URL(s);
            else
                currentdocument.documentURL = curDoc.documentURL;
            currentdocument.documentString = s;
            currentdocument.frameName = "_self";
            currentdocument.externalHint = false;
            CurrentDocument currentdocument1 = curDoc;
            setCurrentDocument(currentdocument);
            if(!curDoc.documentString.equals(currentdocument1.documentString))
                changeSupport.firePropertyChange("documentString", currentdocument1.documentString, curDoc.documentString);
            if(!curDoc.documentURL.equals(currentdocument1.documentURL))
            {
                changeSupport.firePropertyChange("documentURL", currentdocument1.documentURL, curDoc.documentURL);
                return;
            }
        }
        catch(MalformedURLException _ex)
        {
            try
            {
                URL url = new URL("doc://unknown.protocol/" + s);
                handleNewHREF(getName(), url, null, null);
                return;
            }
            catch(Exception _ex2)
            {
                return;
            }
        }
    }

    private void refreshDocumentString(String s)
    {
        URL url = null;
        changeSupport.firePropertyChange("documentString", "", s);
        try
        {
            url = new URL(s);
        }
        catch(MalformedURLException _ex) { }
        changeSupport.firePropertyChange("documentURL", "", url);
        curDoc.documentString = s;
    }

    public String getDocumentString()
    {
        String s = curDoc.documentString;
        if(s == null)
            return "";
        else
            return s;
    }

    public void setDocumentURL(URL url)
    {
        if(url == null)
        {
            setDocumentString("");
            return;
        }
        if(handleJavaScriptURL(url.toExternalForm(), getName(), getName(), this, "setDocumentURL", null))
            return;
        if(getDocFont() == null)
        {
            startupURL = url;
            return;
        }
        if(curDoc != null && curDoc.documentString != null && url.toString().equals(curDoc.documentString))
            return;
        CurrentDocument currentdocument = new CurrentDocument();
        if(curDoc.documentString == null || !curDoc.documentString.equals(url.toString()))
            currentdocument.documentString = url.toString();
        else
            currentdocument.documentString = curDoc.documentString;
        currentdocument.documentURL = url;
        currentdocument.frameName = "_self";
        currentdocument.externalHint = false;
        CurrentDocument currentdocument1 = curDoc;
        setCurrentDocument(currentdocument);
        if(!curDoc.documentString.equals(currentdocument1.documentString))
            changeSupport.firePropertyChange("documentString", currentdocument1.documentString, curDoc.documentString);
        if(!curDoc.documentURL.equals(currentdocument1.documentURL))
            changeSupport.firePropertyChange("documentURL", currentdocument1.documentURL, curDoc.documentURL);
    }

    protected boolean isSetCurrentDocumentAllowed(URL url, String s, boolean flag1)
    {
        boolean flag2 = true;
        if(url == null)
            return htmlReader != null;
        if(url != null && curDoc != null && curDoc.documentString != null && url.toString().equals(curDoc.documentString))
            return true;
        if(s == null && flag1)
            s = "_top";
        CurrentDocument currentdocument = new CurrentDocument();
        currentdocument.documentURL = url;
        currentdocument.documentString = url.toString();
        currentdocument.frameName = s;
        currentdocument.externalHint = flag1;
        try
        {
            vetoChangeSupport.fireVetoableChange("currentDocument", curDoc, currentdocument);
            CurrentDocument currentdocument1 = curDoc;
            curDoc = currentdocument;
            String s1 = null;
            if(curDoc.documentURL != null)
                s1 = curDoc.documentURL.toString();
            if(s1 == null || !s1.equals(curDoc.documentString))
                curDoc.documentString = s1;
            changeSupport.firePropertyChange("currentDocument", currentdocument1, curDoc);
            if(fireDocumentPropertyChange || s == null || s.equals(getName()) || "_top".equals(s))
            {
                if(!curDoc.documentString.equals(currentdocument1.documentString))
                    changeSupport.firePropertyChange("documentString", currentdocument1.documentString, curDoc.documentString);
                if(!curDoc.documentURL.equals(currentdocument1.documentURL))
                    changeSupport.firePropertyChange("documentURL", currentdocument1.documentURL, curDoc.documentURL);
            }
        }
        catch(PropertyVetoException _ex)
        {
            flag2 = false;
        }
        return flag2;
    }

    public void setCurrentDocument(CurrentDocument currentdocument)
    {
        if(currentdocument.documentURL != null)
            handleNewHREF(currentdocument.frameName, currentdocument.documentURL, null, null);
    }

    public CurrentDocument getCurrentDocument()
    {
        return curDoc;
    }

    protected void notifyHistoryChanges()
    {
        if(nextAvailable != canForward())
        {
            nextAvailable = !nextAvailable;
            dispatchBrowserHistoryEvent(3, new Boolean(nextAvailable));
        }
        if(previousAvailable != canBack())
        {
            previousAvailable = !previousAvailable;
            dispatchBrowserHistoryEvent(4, new Boolean(previousAvailable));
        }
    }

    public URL getDocumentURL()
    {
        return curDoc.documentURL;
    }

    public URL getFrameURL()
    {
        if(selectedFrame != null)
            return selectedFrame.getFormatter().getDocument().getURL();
        else
            return null;
    }

    public Reader getDocumentSource()
    {
        Document document = getDocument();
        if(document != null)
            try
            {
                return document.getSourceDocument();
            }
            catch(DocException _ex)
            {
                setErrorMessage("Can't get source for this template");
            }
        return null;
    }

    public Reader getFrameSource()
    {
        if(selectedFrame != null)
            try
            {
                return selectedFrame.getFormatter().getDocument().getSourceDocument();
            }
            catch(DocException _ex) { }
        return null;
    }

    public int saveDocumentSource(FileOutputStream fileoutputstream, Observer observer)
        throws IOException
    {
        Document document = getDocument();
        if(document != null)
        {
            int i = document.getSize();
            saveSource(fileoutputstream, observer, document);
            return i;
        } else
        {
            return -1;
        }
    }

    public int saveFrameSource(FileOutputStream fileoutputstream, Observer observer)
        throws IOException
    {
        if(selectedFrame != null)
        {
            Document document = selectedFrame.getFormatter().getDocument();
            int i = document.getSize();
            saveSource(fileoutputstream, observer, document);
            return i;
        } else
        {
            return -1;
        }
    }

    private void saveSource(FileOutputStream fileoutputstream, Observer observer, Document document)
        throws IOException
    {
        try
        {
            BufferedReader bufferedreader = new BufferedReader(document.getSourceDocument());
            String s = getCharset();
            try
            {
                ByteToCharConverter.getConverter(s);
            }
            catch(UnsupportedEncodingException _ex)
            {
                s = "8859_1";
            }
            OutputStreamWriter outputstreamwriter = new OutputStreamWriter(fileoutputstream, s);
            StreamCopier streamcopier = new StreamCopier(bufferedreader, outputstreamwriter, observer);
            (new Thread(streamcopier)).start();
            return;
        }
        catch(DocException _ex)
        {
            setErrorMessage("Can't get source ");
        }
    }

    public int saveURLContent(URL url, FileOutputStream fileoutputstream, Observer observer)
        throws IOException
    {
        URLConnection urlconnection = url.openConnection();
        java.io.InputStream inputstream = urlconnection.getInputStream();
        int i = urlconnection.getContentLength();
        StreamCopier streamcopier = new StreamCopier(inputstream, new BufferedOutputStream(fileoutputstream), observer);
        (new Thread(streamcopier)).start();
        return i;
    }

    public void setDocumentSource(Reader reader)
    {
        Reader reader1 = htmlReader;
        htmlReader = reader;
        try
        {
            if(getDocFont() != null)
            {
                Document document = makeNewGeneratedDocument(null);
                new DocParser(document, htmlReader, this);
                showDocument(this, document);
                curDoc = new CurrentDocument();
                curDoc.documentSource = reader;
                changeSupport.firePropertyChange("documentSource", reader1, reader);
                changeSupport.firePropertyChange("documentURL", curDoc.documentURL, "");
                changeSupport.firePropertyChange("documentString", curDoc.documentString, "");
                return;
            }
        }
        catch(DocException _ex) { }
    }

    public void setDocumentSource(Reader reader, FrameToken frametoken)
        throws InvalidFrameException
    {
        Object obj = null;
        if(frametoken == null || frametoken.getIndexSet().length == 0)
            obj = this;
        else
            obj = findDocumentPanel(frametoken);
        ((DocumentPanel) (obj)).setDocumentSource(reader);
    }

    private DocumentPanel findDocumentPanel(FrameToken frametoken)
        throws InvalidFrameException
    {
        DocumentFormatter documentformatter = super.current.getFormatter();
        if(!documentformatter.hasFrameSetPanel())
            throw new InvalidFrameException();
        int ai[] = frametoken.getIndexSet();
        Component acomponent[] = documentformatter.getFrameSetPanel().getFrameList();
        FramePanel framepanel = null;
        for(int i = 0; i < ai.length; i++)
        {
            FRAME frame = documentformatter.getDocument().getFrameAt(ai[i]);
            if(frame == null)
                throw new InvalidFrameException();
            framepanel = findFramePanel(acomponent, frame);
            if(framepanel == null)
                throw new InvalidFrameException();
            if(i + 1 < ai.length)
            {
                documentformatter = framepanel.getFormatter();
                if(!documentformatter.hasFrameSetPanel())
                    throw new InvalidFrameException();
                acomponent = documentformatter.getFrameSetPanel().getFrameList();
            }
        }

        if(framepanel != null)
            return framepanel.getDocumentPanel();
        else
            return null;
    }

    protected void setDocumentTitle(String s)
    {
        String s1 = documentTitle;
        documentTitle = s;
        changeSupport.firePropertyChange("documentTitle", s1, s);
    }

    public String getDocumentTitle()
    {
        return documentTitle;
    }

    protected void setErrorMessage(String s)
    {
        errorMessage = s;
        changeSupport.firePropertyChange("errorMessage", errorMessage, s);
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    protected void setIndicatedElement(ElementInfo elementinfo)
    {
        ElementInfo elementinfo1 = getIndicatedElement();
        if(elementinfo != null)
        {
            hover = (ElementInfo)elementinfo.clone();
            if(hover.hrefURL != null && hover.hrefURL.startsWith("doc://unknown.protocol/"))
                hover.hrefURL = hover.hrefURL.substring("doc://unknown.protocol/".length());
        } else
        {
            hover = null;
        }
        changeSupport.firePropertyChange("indicatedElement", elementinfo1, hover);
    }

    public void setStatusMessage(String s)
    {
        activeMessage = s;
        if(s == null || "".equals(s))
            statusMessage = defaultMessage;
        else
            statusMessage = s;
        changeSupport.firePropertyChange("statusMessage", null, statusMessage);
    }

    public String getStatusMessage()
    {
        return statusMessage;
    }

    public String getDefaultStatusMessage()
    {
        return defaultMessage;
    }

    public boolean isSecureConnection()
    {
        return isSecure;
    }

    public void setSecureConnection(URL url)
    {
        isSecure = url.getProtocol().equals("https");
        changeSupport.firePropertyChange("secureConnection", null, url);
    }

    /**
     * @deprecated Method getFrameList is deprecated
     */

    public String[] getFrameList()
    {
        return frameList;
    }

    /**
     * @deprecated Method setFrameList is deprecated
     */

    protected synchronized void setFrameList(String as[])
    {
        String as1[] = frameList;
        frameList = as;
        changeSupport.firePropertyChange("frameList", as1, as);
    }

    /**
     * @deprecated Method getFrameList is deprecated
     */

    public String getFrameList(int i)
    {
        if(i > 0 && i < frameList.length)
            return frameList[i];
        else
            return null;
    }

    /**
     * @deprecated Method setFrameList is deprecated
     */

    protected synchronized void setFrameList(int i, String s)
    {
        if(i >= 0 && i < frameList.length)
        {
            String s1 = frameList[i];
            frameList[i] = s;
            changeSupport.firePropertyChange("frameList", s1, s);
            return;
        }
        if(i < 0)
        {
            boolean flag1 = false;
            if(frameList == null)
            {
                String as[] = new String[1];
                as[0] = s;
                setFrameList(as);
                return;
            }
            for(int j = 0; j < frameList.length; j++)
            {
                if(frameList[j] != null)
                    continue;
                setFrameList(j, s);
                flag1 = true;
                break;
            }

            if(!flag1)
            {
                String as1[] = new String[frameList.length + 1];
                System.arraycopy(frameList, 0, as1, 0, frameList.length);
                int k = frameList.length;
                as1[k] = s;
                setFrameList(as1);
            }
        }
    }

    public DocumentSelection getSelection()
    {
        return docSel;
    }

    public String getCharset()
    {
        Document document = getDocument();
        String s = null;
        if(document != null)
            s = (String)document.getProperty("charset");
        if(s == null)
            return "";
        else
            return s;
    }

    public void setCharset(String s)
    {
        try
        {
            ByteToCharConverter.getConverter(s);
        }
        catch(UnsupportedEncodingException _ex)
        {
            System.out.println("Charset " + s + " is not supported. Use 8859_1");
            s = "8859_1";
        }
        Document document = getDocument();
        if(document != null)
            try
            {
                Globals.setCharset(s);
                document.setProperty("charset", s);
                return;
            }
            catch(Exception _ex)
            {
                return;
            }
        else
            return;
    }

    public double getLoadingProgress()
    {
        return percentRec;
    }

    public boolean isDocumentReloadable()
    {
        return canReload();
    }

    public Object getOpener()
    {
        return opener;
    }

    public void setOpener(Object obj)
    {
        opener = obj;
    }

    public void clearImageCache()
    {
        if(imgCache != null)
            imgCache.flushAllImages();
    }

    public int find(int i, String s)
    {
        return find(i, s, false);
    }

    public int find(int i, String s, boolean flag1)
    {
        if(selectedFrame != null)
            return selectedFrame.find(s, flag1);
        DocumentFormatter documentformatter = getFormatter();
        if(documentformatter != null)
        {
            documentformatter.showSelection(false);
            documentformatter.select(i, i);
            if(super.find(s, flag1, false))
            {
                int j = documentformatter.getSelectEnd();
                return j;
            }
        }
        return -1;
    }

    public void print(PrintJob printjob)
    {
        try
        {
            print(printjob, this);
            return;
        }
        catch(Exception exception)
        {
            System.out.println("bean print exception");
            exception.printStackTrace();
            return;
        }
    }

    public void setDocFontNotInPlace()
    {
        setDocFont();
    }

    private DocFont setDocFont()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        int i = hjbproperties.getInteger("hotjava.docfontsize", 0);
        DocFont docfont = DocFont.findFont(false, 0, 5 + i);
        setDocFont(docfont);
        return docfont;
    }

    public void addNotify()
    {
        super.addNotify();
        ProgressData.pdata.addObserver(this);
        Frame frame = getContainingFrame();
        if(frame != null)
        {
            Globals.registerFrame(frame);
            allInstances.addElement(this);
            DocFont docfont = setDocFont();
            int i = Globals.getFontMetrics(docfont).getAscent();
            Adjustable adjustable = getHAdjustable();
            if(adjustable != null)
                adjustable.setUnitIncrement(i);
            Adjustable adjustable1 = getVAdjustable();
            if(adjustable1 != null)
                adjustable1.setUnitIncrement(i);
            if(serializedDoc != null)
            {
                setCurrentDocument(serializedDoc);
                return;
            }
            if(htmlReader != null)
            {
                Document document = new Document();
                try
                {
                    new DocParser(document, htmlReader, this);
                    showDocument(this, document);
                    changeSupport.firePropertyChange("documentSource", null, htmlReader);
                    return;
                }
                catch(DocException _ex)
                {
                    return;
                }
            }
            if(startupURLString != null)
            {
                setDocumentString(startupURLString);
                startupURLString = null;
                return;
            }
            if(startupURL != null)
            {
                setDocumentURL(startupURL);
                startupURL = null;
            }
        }
    }

    public void vetoableChange(PropertyChangeEvent propertychangeevent)
        throws PropertyVetoException
    {
        String s = propertychangeevent.getPropertyName();
        Object obj = propertychangeevent.getNewValue();
        Object obj1 = propertychangeevent.getOldValue();
        vetoChangeSupport.fireVetoableChange(s, obj1, obj);
    }

    public void processDocumentEvent(DocumentEvent documentevent)
    {
        switch(documentevent.getID())
        {
        case 1032: 
        default:
            break;

        case 1002: 
            NamedLink namedlink = (NamedLink)documentevent.getArgument();
            String s;
            if(documentevent.isShiftDown())
                s = "_blank";
            else
                s = namedlink.name;
            doDocumentCleanup();
            DocumentPanel documentpanel = (DocumentPanel)documentevent.getSource();
            if(namedlink.url.getProtocol().equals("mailto") && linkListeners != null)
            {
                changeSupport.firePropertyChange("handleMailto", null, namedlink.url);
                return;
            } else
            {
                handleLinkEventWithDefault(s, namedlink.url, documentpanel.getName(), null);
                return;
            }

        case 1025: 
            Object obj = documentevent.getArgument();
            Object obj1 = documentevent.getSource();
            if(obj instanceof Document)
            {
                showDocument(this, (Document)obj);
                return;
            }
            if(obj instanceof NamedLink)
            {
                NamedLink namedlink1 = (NamedLink)documentevent.getArgument();
                String s1;
                if(documentevent.isShiftDown())
                    s1 = "_blank";
                else
                    s1 = namedlink1.name;
                if(obj1 instanceof DocPanel)
                {
                    handleNewHREF(s1, namedlink1.url, namedlink1.referer.toString(), null);
                    return;
                }
                if(isSetCurrentDocumentAllowed(namedlink1.url, null, documentevent.isShiftDown()))
                {
                    showDocument(this, namedlink1.url, namedlink1.referer);
                    return;
                }
            }
            break;

        case 1029: 
            setFrameList(-1, (String)documentevent.getArgument());
            return;

        case 1030: 
            if(frameList == null)
                break;
            for(int i = 0; i < frameList.length; i++)
                if(frameList[i] != null && frameList[i].equals((String)documentevent.getArgument()))
                {
                    setFrameList(i, null);
                    return;
                }

            return;

        case 1007: 
            DocumentSelection documentselection = docSel;
            DocumentFormatter documentformatter = (DocumentFormatter)documentevent.getSource();
            int j = flag;
            docSel = new DocumentSelection();
            docSel.text = documentformatter.getSelectedText();
            docSel.html = documentformatter.getSelectedSource();
            if(docSel.text == null)
            {
                Object obj2 = documentevent.getArgument();
                if(obj2 instanceof FormPanel)
                {
                    selector = ((FormPanel)obj2).getComponent();
                    flag = 0;
                    FormPanel formpanel = (FormPanel)obj2;
                    docSel.text = formpanel.getSelectedText();
                    docSel.html = formpanel.getSelectedText();
                } else
                {
                    flag = 1;
                }
            } else
            {
                selector = documentformatter;
                flag = 1;
            }
            if((docSel.text == null || docSel.text.equals("")) && flag == 1 && j == 0)
            {
                docSel = documentselection;
                flag = 0;
                return;
            } else
            {
                changeSupport.firePropertyChange("selection", documentselection, docSel);
                return;
            }

        case 1004: 
            String as[] = {
                "background.img", "background.color", "text.color", "link.color", "vlink.color", "alink.color", "title", "charset"
            };
            String as1[] = {
                "backgroundImage", "backgroundColor", "textColor", "linkColor", "visitedLinkColor", "clickLinkColor", "documentTitle", "charset"
            };
            String s2 = (String)documentevent.getArgument();
            Document document = null;
            document = documentevent.getDocument();
            if(s2.equals("url"))
            {
                URL url = document.getURL();
                curDoc.documentURL = url;
                curDoc.documentString = url.toString();
                gotoLabel(url.getRef());
                return;
            }
            for(int k = 0; k < as.length; k++)
                if(s2.equals(as[k]))
                {
                    changeSupport.firePropertyChange(as1[k], null, document.getProperty(s2));
                    return;
                }

            return;

        case 1006: 
            Document document1 = (Document)documentevent.getArgument();
            if(documentevent.getSource() == this)
                fireDocumentPropertyChange = true;
            else
                fireDocumentPropertyChange = false;
            if(document1 != null && documentevent.getSource() == this)
            {
                URL url1 = document1.getURL();
                if(isSetCurrentDocumentAllowed(url1, null, false))
                    notifyHistoryChanges();
                String s3 = document1.getTitle();
                if(documentTitle != s3)
                {
                    setDocumentTitle(s3);
                    return;
                }
            } else
            {
                notifyHistoryChanges();
            }
            return;

        case 1027: 
            ElementInfo elementinfo = (ElementInfo)documentevent.getArgument();
            if(elementinfo == null || hover == null || elementinfo.hrefURL == null || hover.hrefURL == null || !elementinfo.hrefURL.equals(hover.hrefURL) || elementinfo.imageURL == null || hover.imageURL == null || !elementinfo.imageURL.equals(hover.imageURL) || elementinfo.event == null || hover.event == null || elementinfo.imageURL != null && elementinfo.hrefURL != null || elementinfo.event.getModifiers() != hover.event.getModifiers())
                setIndicatedElement(elementinfo);
            return;

        case 1024: 
            setErrorMessage((String)documentevent.getArgument());
            return;

        case 1000: 
            setStatusMessage((String)documentevent.getArgument());
            return;

        case 1039: 
            defaultMessage = (String)documentevent.getArgument();
            if(activeMessage == null || "".equals(activeMessage))
                setStatusMessage(activeMessage);
            changeSupport.firePropertyChange("defaultStatusMessage", null, defaultMessage);
            return;

        case 1019: 
            TagAppletPanel tagappletpanel = (TagAppletPanel)documentevent.getArgument();
            tagappletpanel.addDocumentListener(topDocListener);
            super.processPanelDocumentEvent(documentevent);
            return;

        case 1017: 
            Object obj3 = documentevent.getArgument();
            if(obj3 instanceof DocumentFormatter)
            {
                addTopDocListenerToSource((DocumentFormatter)obj3);
                return;
            }
            break;

        case 1018: 
            Object obj4 = documentevent.getArgument();
            if(obj4 instanceof DocumentFormatter)
            {
                removeTopDocListenerFromSource((DocumentFormatter)obj4);
                return;
            }
            break;

        case 1010: 
            DocumentPanel documentpanel1 = (DocumentPanel)documentevent.getArgument();
            pushInternalLink(documentpanel1);
            notifyHistoryChanges();
            return;

        case 1003: 
            fitPanelToDocument();
            return;

        case 1037: 
            repaintComponents(super.panel);
            return;

        case 1041: 
            if(selectedFrame != null)
                selectedFrame.setSelected(false);
            selectedFrame = (FramePanel)documentevent.getArgument();
            changeSupport.firePropertyChange("frameExists", null, getFrameURL());
            return;
        }
    }

    public void addTopDocListenerToSource(DocumentEventSource documenteventsource)
    {
        documenteventsource.addDocumentListener(topDocListener);
    }

    public void removeTopDocListenerFromSource(DocumentEventSource documenteventsource)
    {
        documenteventsource.removeDocumentListener(topDocListener);
    }

    public void removeNotify()
    {
        blinker.stop();
        ProgressData.pdata.deleteObserver(this);
        allInstances.removeElement(this);
        Frame frame = getActiveFrame();
        Globals.registerFrame(frame);
        clear();
        super.removeNotify();
    }

    protected void repaintComponents(Container container)
    {
        if(container != null)
        {
            Component acomponent[] = container.getComponents();
            for(int i = 0; i < acomponent.length; i++)
                if(acomponent[i] instanceof Container)
                    repaintComponents((Container)acomponent[i]);

            if(container instanceof DocumentFormatterPanel)
                container.repaint();
        }
    }

    protected void processPanelKeyEvent(KeyEvent keyevent)
    {
        switch(keyevent.getKeyCode())
        {
        case 23: // '\027'
            list();
            return;
        }
    }

    public boolean canReload()
    {
        Document document = getDocument();
        return document != null && document.getURL() != null;
    }

    public void reload()
    {
        if(getDocument().getConnector() != null)
        {
            BeanConfirmDialog beanconfirmdialog = new BeanConfirmDialog("repost", getContainingFrame());
            beanconfirmdialog.setVisible(true);
            if(beanconfirmdialog.getAnswer() == 1)
                return;
        }
        getFormatter().dispatchDocumentEvent(1040, null);
        tryToEnableAppletReload();
        super.reload();
    }

    protected void tryToEnableAppletReload()
    {
        Vector vector = new Vector();
        getFormatter().getAppletPanels(vector, true);
        if(vector.size() == 0)
            return;
        Vector vector1 = new Vector();
        for(int i = 0; i < vector.size(); i++)
        {
            URL url = ((TagAppletPanel)vector.elementAt(i)).getCodeBase();
            if(!vector1.contains(url))
                vector1.addElement(url);
        }

        Vector vector2 = getAllHotJavaBrowserBeans();
        for(int j = 0; j < vector2.size(); j++)
        {
            HotJavaBrowserBean hotjavabrowserbean = (HotJavaBrowserBean)vector2.elementAt(j);
            if(hotjavabrowserbean != null && hotjavabrowserbean != this)
            {
                Vector vector3 = new Vector();
                hotjavabrowserbean.getFormatter().getAppletPanels(vector3, true);
                for(int l = 0; l < vector3.size(); l++)
                {
                    boolean flag1 = false;
                    TagAppletPanel tagappletpanel = (TagAppletPanel)vector3.elementAt(l);
                    for(int i1 = 0; i1 < vector.size(); i1++)
                    {
                        TagAppletPanel tagappletpanel1 = (TagAppletPanel)vector.elementAt(i1);
                        flag1 = flag1 || tagappletpanel.getItem() == tagappletpanel1.getItem();
                    }

                    if(!flag1)
                    {
                        vector1.removeElement(tagappletpanel.getCodeBase());
                        if(vector1.size() == 0)
                            return;
                    }
                }

            }
        }

        for(int k = 0; k < vector2.size(); k++)
        {
            HotJavaBrowserBean hotjavabrowserbean1 = (HotJavaBrowserBean)vector2.elementAt(k);
            hotjavabrowserbean1.documentStack.flushDocumentsContainingCodebases(vector1);
        }

    }

    public boolean canStopLoading()
    {
        Document document = super.next == null ? null : super.next.getDocument();
        return document != null && (document.getState() == 11 || document.getState() == 12 || super.appletLoadingCount > 0 || !document.isOkToFormat());
    }

    public void stopLoading()
    {
        Document document = getDocument();
        findPanel(this, getName());
        if(super.next != null)
        {
            document.interruptOwnerWaitCompletion();
            Document document1 = super.next.getDocument();
            if(document1 != null)
            {
                document1.interruptOwnerWaitCompletion();
                return;
            }
        } else
        {
            document.interruptOwnerWaitCompletion();
        }
    }

    public void print(boolean flag1)
    {
        printSelectedFrame = flag1;
        (new Thread(this, "Document Printer")).start();
    }

    public void run()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        Frame frame = getContainingFrame();
        java.util.Properties properties = hjbproperties.getDynamicProperties();
        PrintJob printjob = getToolkit().getPrintJob(frame, getDocument().getTitle(), properties);
        if(printjob != null)
        {
            int i = frame.getCursorType();
            try
            {
                frame.setCursor(Cursor.getPredefinedCursor(3));
                hjbproperties.save();
                try
                {
                    if(!printSelectedFrame)
                        print(printjob, this);
                    else
                    if(selectedFrame != null)
                        selectedFrame.print(printjob, this);
                }
                catch(Throwable throwable)
                {
                    throwable.printStackTrace();
                    printjob.end();
                    String s1 = "printjob.err.msg";
                    String s2 = hjbproperties.getProperty(s1);
                    setErrorMessage(s2);
                    return;
                }
                printjob.end();
            }
            finally
            {
                frame.setCursor(i);
            }
            setErrorMessage(hjbproperties.getProperty("printjob.done.msg"));
            return;
        } else
        {
            String s = "printjob.cancelled.msg";
            setErrorMessage(hjbproperties.getProperty(s));
            return;
        }
    }

    public void internalGoto(DocPanel docpanel, URL url, URL url1, boolean flag1)
    {
        showDocument(this, url, url1);
    }

    public void internalGoto(DocPanel docpanel, Document document, boolean flag1)
    {
        if(docpanel instanceof DocumentPanel)
            showDocument((DocumentPanel)docpanel, document);
    }

    public void internalGoto(DocPanel docpanel, String s, Document document, boolean flag1)
    {
        handleNewHREF(s, document.getURL(), null, document);
    }

    public void internalGoto(String s, String s1, Document document, boolean flag1)
    {
        if(flag1)
            s1 = "_blank";
        if(document.getConnector() == null)
        {
            handleLinkEventWithDefault(s1, document.getURL(), s, null);
            return;
        } else
        {
            handlePOST(s1, document, s);
            return;
        }
    }

    public static HotJavaBrowserBean getContainingHotJavaBrowserBean(Container container)
    {
        for(; container != null; container = container.getParent())
            if(container instanceof HotJavaBrowserBean)
                return (HotJavaBrowserBean)container;

        return null;
    }

    public DocumentStack getDocumentStack()
    {
        return documentStack;
    }

    private void setupClientPull(String s, URL url, DocumentPanel documentpanel)
    {
        int i = s.indexOf(' ');
        if(i < 0)
        {
            System.err.println("Client-pull: bad refresh spec, " + s);
            return;
        }
        try
        {
            String s1 = s.substring(i).trim();
            StringTokenizer stringtokenizer;
            if(s1.indexOf(';') > 0)
                stringtokenizer = new StringTokenizer(s1, ";");
            else
            if(s1.indexOf(',') > 0)
                stringtokenizer = new StringTokenizer(s1, ",");
            else
                stringtokenizer = new StringTokenizer(s1);
            int l = stringtokenizer.countTokens();
            int k;
            String s4;
            switch(l)
            {
            case 1: // '\001'
                String s2 = stringtokenizer.nextToken().trim();
                try
                {
                    k = Integer.parseInt(s2);
                }
                catch(NumberFormatException _ex)
                {
                    System.err.println("Client-pull: bad refresh spec, " + s);
                    return;
                }
                s4 = url.toExternalForm();
                break;

            case 2: // '\002'
                String s3 = stringtokenizer.nextToken().trim();
                try
                {
                    k = Integer.parseInt(s3);
                }
                catch(NumberFormatException _ex)
                {
                    System.err.println("Client-pull: bad refresh spec, " + s);
                    return;
                }
                s4 = stringtokenizer.nextToken().trim();
                if(s4 == null || !s4.startsWith("URL") && !s4.startsWith("url"))
                    break;
                int j = s4.indexOf('=');
                if(j == -1)
                {
                    System.err.println("Client-pull: bad refresh doc spec, " + s4);
                    return;
                }
                s4 = s4.substring(j + 1);
                break;

            default:
                System.err.println("Client-pull: bad spec, " + l + " tokens in spec, " + s1);
                return;
            }
            ClientPuller clientpuller = new ClientPuller(this, documentpanel, url, s4, k);
            clientpuller.start();
            return;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public void flushDocumentStack(boolean flag1)
    {
        documentStack.flush(flag1);
    }

    public void clear()
    {
        documentStack.flush(true);
        notifyHistoryChanges();
    }

    public void destroyAllApplets(boolean flag1)
    {
        Vector vector = new Vector();
        getFormatter().getAppletPanels(vector, true);
        documentStack.addAppletPanels(vector);
        for(int i = 0; i < vector.size(); i++)
        {
            TagAppletPanel tagappletpanel = (TagAppletPanel)vector.elementAt(i);
            tagappletpanel.destroyIfNeeded(flag1);
        }

    }

    public void destroyAllApplets()
    {
        destroyAllApplets(true);
    }

    public ElementInfo getIndicatedElement()
    {
        return hover;
    }

    public static Frame getActiveFrame()
    {
        synchronized(allInstances)
        {
            for(int i = 0; i < allInstances.size(); i++)
            {
                HotJavaBrowserBean hotjavabrowserbean = (HotJavaBrowserBean)allInstances.elementAt(i);
                if(hotjavabrowserbean.isVisible())
                {
                    Frame frame = hotjavabrowserbean.getContainingFrame();
                    return frame;
                }
            }

            if(allInstances.size() > 0)
            {
                HotJavaBrowserBean hotjavabrowserbean1 = (HotJavaBrowserBean)allInstances.elementAt(0);
                Frame frame1 = hotjavabrowserbean1.getContainingFrame();
                return frame1;
            }
            Frame frame2 = null;
            return frame2;
        }
    }

    protected Vector getAllHotJavaBrowserBeans()
    {
        return (Vector)allInstances.clone();
    }

    public void update(Observable observable, Object obj)
    {
        if(!isVisible())
            return;
        ProgressEntry progressentry = (ProgressEntry)obj;
        double d = percentRec;
        synchronized(this)
        {
            if(progressentry.index >= connProgress.length)
            {
                long al[] = connProgress;
                connProgress = new long[progressentry.index + 1];
                System.arraycopy(al, 0, connProgress, 0, al.length);
            }
            switch(progressentry.what)
            {
            case 0: // '\0'
                if(countConn == 0)
                {
                    percentRec = 0.0D;
                    totalBytes = 0L;
                    totalRec = 0L;
                }
                connProgress[progressentry.index] = 0L;
                countConn++;
                break;

            case 1: // '\001'
                totalBytes += progressentry.need;
                totalRec += progressentry.read;
                connProgress[progressentry.index] = progressentry.read;
                break;

            case 2: // '\002'
                totalRec += (long)progressentry.read - connProgress[progressentry.index];
                connProgress[progressentry.index] = progressentry.read;
                break;

            case 3: // '\003'
                totalRec += (long)progressentry.need - connProgress[progressentry.index];
                connProgress[progressentry.index] = 0L;
                countConn--;
                if(countConn <= 0)
                {
                    countConn = 0;
                    totalRec = 1L;
                    totalBytes = 1L;
                }
                break;
            }
            if(totalBytes == 0L)
                percentRec = 0.0D;
            else
                percentRec = (double)totalRec / (double)totalBytes;
        }
        changeSupport.firePropertyChange("loadingProgress", new Double(d), new Double(percentRec));
    }

    private void updateDoc(Document document)
    {
        Date date = document.getExpirationDate();
        if(date != null && date.getTime() <= System.currentTimeMillis())
            try
            {
                document.reload();
                return;
            }
            catch(Exception _ex)
            {
                return;
            }
        URL url = document.getURL();
        curDoc.documentURL = url;
        setDocumentTitle(document.getTitle());
        if(url == null)
        {
            curDoc.documentString = "";
            return;
        } else
        {
            curDoc.documentString = url.toString();
            return;
        }
    }

    public void executeHistoryCommand(BrowserHistoryEvent browserhistoryevent)
    {
        switch(browserhistoryevent.getCommand())
        {
        default:
            break;

        case 0: // '\0'
            clear();
            return;

        case 1: // '\001'
            forward();
            Document document = getDocument();
            if(document != null)
            {
                updateDoc(document);
                return;
            }
            break;

        case 2: // '\002'
            back();
            Document document1 = getDocument();
            if(document1 != null)
            {
                updateDoc(document1);
                return;
            }
            break;

        case 7: // '\007'
            HotJavaDocumentStack hotjavadocumentstack = (HotJavaDocumentStack)browserhistoryevent.getSource();
            getDocumentStack().setContentsDepth(hotjavadocumentstack.getContentsDepth());
            return;

        case 6: // '\006'
            HotJavaDocumentStack hotjavadocumentstack1 = (HotJavaDocumentStack)browserhistoryevent.getSource();
            getDocumentStack().setLogicalDepth(hotjavadocumentstack1.getLogicalDepth());
            return;
        }
    }

    public synchronized void addBrowserHistoryListener(BrowserHistoryListener browserhistorylistener)
    {
        if(historyListeners == null)
            historyListeners = new Vector();
        cloneNecessary = true;
        historyListeners.addElement(browserhistorylistener);
    }

    public synchronized void removeBrowserHistoryListener(BrowserHistoryListener browserhistorylistener)
    {
        if(historyListeners == null)
        {
            return;
        } else
        {
            cloneNecessary = true;
            historyListeners.removeElement(browserhistorylistener);
            return;
        }
    }

    protected void dispatchBrowserHistoryEvent(int i, Object obj)
    {
        synchronized(this)
        {
            if(historyListeners == null)
                return;
            if(cloneNecessary)
            {
                targets = (Vector)historyListeners.clone();
                cloneNecessary = false;
            }
        }
        BrowserHistoryEvent browserhistoryevent = new BrowserHistoryEvent(this, i, obj);
        for(int j = 0; j < targets.size(); j++)
        {
            BrowserHistoryListener browserhistorylistener = (BrowserHistoryListener)targets.elementAt(j);
            browserhistorylistener.executeHistoryCommand(browserhistoryevent);
        }

    }

    public void writeExternal(ObjectOutput objectoutput)
        throws IOException
    {
        objectoutput.writeInt(1);
        objectoutput.writeObject(curDoc);
        Rectangle rectangle = getBounds();
        objectoutput.writeInt(rectangle.x);
        objectoutput.writeInt(rectangle.y);
        objectoutput.writeInt(rectangle.width);
        objectoutput.writeInt(rectangle.height);
    }

    public void readExternal(ObjectInput objectinput)
        throws IOException, ClassNotFoundException
    {
        int i = objectinput.readInt();
        if(i == 1)
        {
            serializedDoc = (CurrentDocument)objectinput.readObject();
            int j = objectinput.readInt();
            int k = objectinput.readInt();
            int l = objectinput.readInt();
            int i1 = objectinput.readInt();
            setBounds(j, k, l, i1);
            return;
        } else
        {
            throw new IOException("Unrecognized " + getClass().getName() + " version:  " + i);
        }
    }

    public Component getComponent()
    {
        return this;
    }

    public Dimension getPreferredSize()
    {
        return getSize();
    }

    public Dimension getMinimumSize()
    {
        return getSize();
    }

    public FrameToken getTopLevelFrame()
    {
        Document document = super.current.getFormatter().getDocument();
        if(document.getNumberOfFrames() > 0)
        {
            FRAME aframe[] = new FRAME[document.getNumberOfFrames()];
            for(int i = 0; i < aframe.length; i++)
                aframe[i] = document.getFrameAt(i);

            FrameToken frametoken = new FrameToken(null, new int[0]);
            createFrameChildren(frametoken, aframe, new int[0], super.current.getFormatter());
            return frametoken;
        } else
        {
            return null;
        }
    }

    private void createFrameChildren(FrameToken frametoken, FRAME aframe[], int ai[], DocumentFormatter documentformatter)
    {
        FrameToken aframetoken[] = new FrameToken[aframe.length];
        DocumentFormatter adocumentformatter[] = new DocumentFormatter[1];
        adocumentformatter[0] = null;
        for(int i = 0; i < aframe.length; i++)
        {
            int ai1[] = new int[ai.length + 1];
            for(int j = 0; j < ai.length; j++)
                ai1[j] = ai[j];

            ai1[ai1.length - 1] = i;
            aframetoken[i] = new FrameToken(frametoken, ai1, aframe[i].getAttributes().get("name"));
            String s = "";
            for(int k = 0; k < ai.length; k++)
                s = s + ai[k] + ".";

            FRAME aframe1[] = findChildFrames(aframe[i], documentformatter, adocumentformatter);
            if(aframe1 != null)
                createFrameChildren(aframetoken[i], aframe1, ai1, adocumentformatter[0]);
        }

        frametoken.setChildren(aframetoken);
    }

    private FRAME[] findChildFrames(FRAME frame, DocumentFormatter documentformatter, DocumentFormatter adocumentformatter[])
    {
        Document document = documentformatter.getDocument();
        int i;
        for(i = frame.getIndex(); i >= 0 && !(document.items[i] instanceof FRAMESET); i--);
        if(i < 0)
            return null;
        int j = documentformatter.getNumberOfFrameSetPanels();
        FrameSetPanel framesetpanel = null;
        for(int k = 0; k < j; k++)
        {
            framesetpanel = documentformatter.getFrameSetPanel(k);
            if(framesetpanel.getFrameSetTag() == document.items[i])
                break;
            framesetpanel = null;
        }

        if(framesetpanel == null)
            return null;
        Component acomponent[] = framesetpanel.getFrameList();
        FramePanel framepanel = findFramePanel(acomponent, frame);
        if(framepanel == null)
            return null;
        DocumentFormatter documentformatter1 = framepanel.getFormatter();
        if(documentformatter1 == null)
            return null;
        adocumentformatter[0] = documentformatter1;
        Document document1 = documentformatter1.getDocument();
        if(document1.getNumberOfFrames() > 0)
        {
            FRAME aframe[] = new FRAME[document1.getNumberOfFrames()];
            for(int l = 0; l < aframe.length; l++)
                aframe[l] = document1.getFrameAt(l);

            return aframe;
        } else
        {
            return null;
        }
    }

    private FramePanel findFramePanel(Component acomponent[], FRAME frame)
    {
        FramePanel framepanel = null;
        for(int i = 0; i < acomponent.length; i++)
        {
            if(acomponent[i] instanceof FramePanel)
            {
                if(((FramePanel)acomponent[i]).getFrameTag() != frame)
                    continue;
                framepanel = (FramePanel)acomponent[i];
                break;
            }
            if(!(acomponent[i] instanceof FrameSetPanel))
                continue;
            framepanel = findFramePanel(((FrameSetPanel)acomponent[i]).getFrameList(), frame);
            if(framepanel != null)
                break;
        }

        return framepanel;
    }

    public void setAppletManager(AppletManager appletmanager)
    {
        if(appletManager == null)
        {
            appletManager = appletmanager;
            Thread thread = new Thread("Scripting Engine Prefetcher") {

                public void run()
                {
                    Globals.getScriptingEngine();
                }

            }
;
            thread.setPriority(2);
            thread.start();
        }
    }

    public static AppletManager getAppletManager()
    {
        return appletManager;
    }

    public void setURLPoolManager(URLPooler urlpooler)
    {
        urlpool = urlpooler;
    }

    public static URLPooler getURLPoolManager()
    {
        return urlpool;
    }

    public void processExtViewerURLInStack(URL url)
    {
        synchronized(documentStack)
        {
            documentStack.setViewer(url);
        }
    }

    public void setCookiesManager(CookieJarInterface cookiejarinterface)
    {
        cookieJar = cookiejarinterface;
    }

    public static CookieJarInterface getCookiesManager()
    {
        return cookieJar;
    }

    public void setImageCache(ImageCacher imagecacher)
    {
        imgCache = imagecacher;
    }

    public static ImageCacher getImageCache()
    {
        return imgCache;
    }

    private DocListener topDocListener;
    static final String propName = "hotjava";
    private static final String debugProp = "hotjava.debug.BeanDocumentPanel";
    private static final boolean debugFrameTokens = false;
    private DocumentStack documentStack;
    int currentCursor;
    protected String statusMessage;
    protected String activeMessage;
    protected String defaultMessage;
    protected String errorMessage;
    protected String documentTitle;
    private int flag;
    private Object selector;
    protected PropertyChangeSupport changeSupport;
    protected VetoableChangeSupport vetoChangeSupport;
    protected long fLastKnownFreeMem;
    private CurrentDocument curDoc;
    private Reader htmlReader;
    private boolean isSecure;
    private DocumentSelection docSel;
    private String frameList[];
    private CurrentDocument serializedDoc;
    private static final int externalizedVersion = 1;
    private String startupURLString;
    private URL startupURL;
    private static Vector allInstances = new Vector(1);
    private long connProgress[];
    private int countConn;
    private double percentRec;
    private long totalBytes;
    private long totalRec;
    private FramePanel selectedFrame;
    private boolean printSelectedFrame;
    private transient boolean cloneNecessary;
    private transient Vector targets;
    private transient Vector historyListeners;
    private transient Vector linkListeners;
    private ElementInfo hover;
    private boolean nextAvailable;
    private boolean previousAvailable;
    private boolean fireDocumentPropertyChange;
    static final long serialVersionUID = 0x9716d328e6cfc50eL;
    private Blinker blinker;
    private static boolean blinkon = true;
    private Document docToShowInNewBean;
    private String postTarget;
    private String postSource;
    private Object opener;
    private static AppletManager appletManager = null;
    private static ImageCacher imgCache = null;
    private static URLPooler urlpool;
    private static CookieJarInterface cookieJar;







}
