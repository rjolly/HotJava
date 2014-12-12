// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentFormatter.java

package sunw.hotjava.doc;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Vector;
import sun.awt.ScreenUpdater;
import sun.awt.UpdateClient;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.misc.*;
import sunw.hotjava.script.*;
import sunw.hotjava.tables.TableElementPanel;
import sunw.hotjava.tables.TablePanel;
import sunw.hotjava.tags.TagAppletPanel;

// Referenced classes of package sunw.hotjava.doc:
//            Formatter, DocBusyException, DocConstants, DocItem, 
//            DocItemEnumeration, DocLine, DocPanel, DocStyle, 
//            DocView, Document, DocumentBackground, DocumentEvent, 
//            DocumentFormatterPanel, DocumentPanel, DocumentState, DragThread, 
//            EndTagItem, FormatState, FormatterVBreakInfo, ItemComponent, 
//            MouseDownInfo, PageMargins, PageMarker, Responsibility, 
//            SelectionOwner, TextItem, TraversalState, VBreakInfo, 
//            TagItem, DocFont, DocumentListener

public class DocumentFormatter extends Formatter
    implements DocView, UpdateClient, SelectionOwner, Serializable
{
    private final class DocListener
        implements DocumentListener
    {

        public void documentChanged(DocumentEvent documentevent)
        {
            processDocumentEvent(documentevent);
        }

        public DocListener()
        {
        }
    }

    static class FinishFormatRequest extends sunw.hotjava.misc.RequestProcessor.Request
    {

        public void execute()
        {
            try
            {
                ScriptingEngineInterface scriptingengineinterface = Globals.getScriptingEngine();
                if(scriptingengineinterface != null && formatter.getDocument().hasJavaScriptTags())
                    scriptingengineinterface.registerListeners(formatter);
            }
            catch(Exception exception)
            {
                exception.printStackTrace();
            }
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            String s = hjbproperties.getProperty("hotjava.formatting.done", "");
            formatter.dispatchDocumentEvent(1000, s);
            formatter.dispatchDocumentEvent(1038, null);
        }

        private DocumentFormatter formatter;

        public FinishFormatRequest(DocumentFormatter documentformatter)
        {
            formatter = documentformatter;
        }
    }


    public ScriptContextInterface getScriptContext()
    {
        return scriptContext;
    }

    public void setScriptContext(ScriptContextInterface scriptcontextinterface)
    {
        scriptContext = scriptcontextinterface;
        scriptContextCodebase = scriptcontextinterface == null ? null : getDocument().getURL();
    }

    public ScriptSecuritySupportInterface getSecuritySupport()
    {
        return securitySupport;
    }

    public void setSecuritySupport(ScriptSecuritySupportInterface scriptsecuritysupportinterface)
    {
        securitySupport = scriptsecuritysupportinterface;
    }

    public DocumentFormatter(Container container, Document document, DocFont docfont)
    {
        this(container, document, docfont, true);
    }

    public DocumentFormatter(Container container, Document document, DocFont docfont, boolean flag)
    {
        SYNC_PAINTS = new Object();
        bFormatDone = false;
        repaintOnWake = false;
        needScrollOnFinish = false;
        marginWidth = 10;
        printing = false;
        blinkers = new Vector();
        dragLock = new Object();
        dragDelay = 100;
        defaultStatusMessage = "";
        bFormatDone = false;
        parent = container;
        scroller = (DocumentPanel)container.getParent();
        synchronized(Globals.getAwtLock())
        {
            synchronized(document)
            {
                stdInit(document, docfont);
                super.itemComponents = new ItemComponent[6];
                document.addView(this);
            }
        }
        formatterDocListener = new DocListener();
        Container container1 = getParent();
        if(container1 == null)
            return;
        formatterMouseListener = new Formatter.FormatterMouseListener();
        ScriptingEngineInterface scriptingengineinterface = Globals.getScriptingEngine();
        if(scriptingengineinterface != null && flag)
            try
            {
                setScriptContext(scriptingengineinterface.getContext(this));
                return;
            }
            catch(RuntimeException runtimeexception)
            {
                System.out.println("[[[ DocumentFormatter(): could not instantiate script context: <" + runtimeexception + "> ]]]");
                runtimeexception.printStackTrace();
                return;
            }
        else
            return;
    }

    public DocumentFormatter(DocumentFormatter documentformatter, DocFont docfont)
    {
        SYNC_PAINTS = new Object();
        bFormatDone = false;
        repaintOnWake = false;
        needScrollOnFinish = false;
        marginWidth = 10;
        printing = false;
        blinkers = new Vector();
        dragLock = new Object();
        dragDelay = 100;
        defaultStatusMessage = "";
        parent = null;
        scroller = null;
        stdInit(((Formatter) (documentformatter)).doc, docfont);
        copyPanelsForPrinting(documentformatter, super.ds);
        printing = true;
    }

    public DocumentFormatter()
    {
        SYNC_PAINTS = new Object();
        bFormatDone = false;
        repaintOnWake = false;
        needScrollOnFinish = false;
        marginWidth = 10;
        printing = false;
        blinkers = new Vector();
        dragLock = new Object();
        dragDelay = 100;
        defaultStatusMessage = "";
        parent = null;
        scroller = null;
    }

    public void removeListeners()
    {
        super.doc.removeDocumentListener(formatterDocListener);
        parent.removeMouseListener(formatterMouseListener);
        parent.removeMouseMotionListener(formatterMouseListener);
        parent.removeKeyListener(keyListener);
    }

    public void unregisterListeners()
    {
        removeListeners();
        super.unregisterListeners();
    }

    public void addListeners()
    {
        super.doc.addDocumentListener(formatterDocListener);
        parent.addMouseListener(formatterMouseListener);
        parent.addMouseMotionListener(formatterMouseListener);
        parent.addKeyListener(keyListener);
    }

    private void stdInit(Document document, DocFont docfont)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        super.docHeight = 10;
        super.doc = document;
        super.lines = new DocLine[100];
        super.lines[0] = new DocLine(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        super.nlines = 0;
        super.ds.docStyle = new DocStyle(docfont);
        super.ds.docStyle.doc = document;
        super.ds.docStyle.win = this;
        super.ds.started = true;
        super.ds.background = hjbproperties.getColor("hotjava.docbgcolor", Color.white);
        super.ds.topFormatter = this;
        resetDocStyle();
        super.ds.selStart = -1;
        super.ds.selEnd = -1;
    }

    public ScrollPane getScrollPane()
    {
        return scroller;
    }

    protected void setVScrollBarWidth(int i)
    {
        scrollBarWidth = i;
    }

    public int getVScrollBarWidth()
    {
        return scrollBarWidth;
    }

    protected int getStartIndex()
    {
        return 0;
    }

    protected boolean getRepair()
    {
        return repair;
    }

    protected void setRepair(boolean flag)
    {
        if(bFormatDone)
            repair = flag;
    }

    public void stop()
    {
        reset();
        super.stop();
    }

    public boolean isFormatDone()
    {
        return bFormatDone;
    }

    public void reset()
    {
        bFormatDone = false;
    }

    protected int getMaxIndex()
    {
        return super.doc.nitems;
    }

    public String getDefaultStatusMessage()
    {
        return defaultStatusMessage;
    }

    public void setDefaultStatusMessage(String s)
    {
        defaultStatusMessage = s;
        dispatchDocumentEvent(1039, s);
    }

    public void getBackgroundDisplacement(Point point)
    {
    }

    public boolean hasSelection()
    {
        return hasSelection(super.ds);
    }

    boolean hasSelection(DocumentState documentstate)
    {
        if(documentstate.selStart == documentstate.selEnd)
            return false;
        return documentstate.selStart != -1 && documentstate.selEnd != -1;
    }

    public Vector getAppletPanels()
    {
        Vector vector = new Vector();
        getAppletPanels(vector);
        return vector;
    }

    public Vector getAppletPanelsAcrossFrames()
    {
        Vector vector = new Vector();
        getAppletPanels(vector, true);
        return vector;
    }

    public void gotoLabel(String s)
    {
        synchronized(super.doc)
        {
            label = s;
            if(super.ds.started && label != null)
            {
                bFormatDone = true;
                touch(false, 0);
            }
        }
    }

    public boolean setSize(int i, int j, int k)
    {
        return setSize(i, j, k, false);
    }

    public boolean setSize(int i, int j, int k, boolean flag)
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(super.doc)
            {
                if(super.width != i || scrollBarWidth != k)
                {
                    int l = getVScrollBarWidth();
                    if(super.width + l != i + k)
                    {
                        setVScrollBarWidth(k);
                        super.width = i;
                        super.height = j;
                        reformat();
                        boolean flag1 = true;
                        return flag1;
                    }
                    if(flag)
                    {
                        super.width = i;
                        super.height = j;
                    }
                    boolean flag2 = false;
                    return flag2;
                }
                if(super.height != j)
                {
                    super.height = j;
                    touch(false, 100);
                }
            }
        }
        return false;
    }

    public void setMargins(int i, int j)
    {
        synchronized(super.doc)
        {
            if(marginWidth != i || super.marginHeight != j)
            {
                marginWidth = i;
                super.marginHeight = j;
                resetDocStyle();
                reformat();
            }
        }
    }

    public void setRepaintOnWake(boolean flag)
    {
        repaintOnWake = flag;
    }

    protected void wakeScreenUpdater()
    {
        if(repaintOnWake)
            ScreenUpdater.updater.notify(this, 0L, REPAINT);
        repaintOnWake = false;
    }

    protected void setVisible(Component component, BitSet bitset, int i)
    {
        if(bitset != null && bitset.get(i))
            component.setVisible(true);
    }

    protected void setInvisible(Component component, BitSet bitset, int i)
    {
        if(component.isVisible())
        {
            bitset.set(i);
            component.setVisible(false);
        }
    }

    protected void connectBackgroundClient()
    {
        if(super.ds.bg != null && parent != null)
            super.ds.bg.addClient(parent);
    }

    protected void disconnectBackgroundClient()
    {
        if(super.ds.bg != null && parent != null)
            super.ds.bg.removeClient(parent);
    }

    protected void quitViewingDoc()
    {
        super.doc.removeView(this);
    }

    protected void disconnectFromParent(Component component)
    {
        if(parent != null)
            parent.remove(component);
    }

    protected void setFormatterStarted(boolean flag)
    {
        super.ds.started = flag;
    }

    public Container getParent()
    {
        return parent;
    }

    public void interruptLoading()
    {
        super.interruptLoading();
    }

    protected MouseDownInfo getMouseDownInfo()
    {
        return down;
    }

    protected void setMouseDownInfo(MouseDownInfo mousedowninfo)
    {
        down = mousedowninfo;
    }

    protected boolean handleMouseDown(MouseEvent mouseevent)
    {
        parent.requestFocus();
        dispatchDocumentEvent(1026, null);
        return super.handleMouseDown(mouseevent);
    }

    public Graphics getGraphics()
    {
        if(parent != null && super.ds.started)
            return parent.getGraphics();
        else
            return null;
    }

    public void repaint()
    {
        if(super.ds.started)
            ScreenUpdater.updater.notify(this, 100L, REPAINT);
    }

    public int countPanels()
    {
        return super.nItemComponents;
    }

    public Component getPanel(int i)
    {
        if(i < super.nItemComponents)
            return super.itemComponents[i].getComponent();
        else
            return null;
    }

    public Formatter getCorrectFormatter(int i)
    {
        return findCorrectFormatter(this, i);
    }

    private Formatter findCorrectFormatter(Formatter formatter, int i)
    {
        Responsibility responsibility = formatter.findResponsibility(i);
        if(responsibility == null)
            return formatter;
        DocPanel docpanel = responsibility.getTarget();
        if(!(docpanel instanceof TablePanel))
            return formatter;
        TablePanel tablepanel = (TablePanel)docpanel;
        for(Enumeration enumeration = tablepanel.enumeratePanels(); enumeration.hasMoreElements();)
        {
            TableElementPanel tableelementpanel = (TableElementPanel)enumeration.nextElement();
            if(tableelementpanel.containsPos(i))
            {
                Formatter formatter1 = tableelementpanel.getFormatter();
                return findCorrectFormatter(formatter1, i);
            }
        }

        return formatter;
    }

    public DocStyle getStyle()
    {
        return super.ds.docStyle;
    }

    public Document getDocument()
    {
        return super.doc;
    }

    public int getDocumentX()
    {
        return docX;
    }

    public void setDocumentX(int i)
    {
        docX = i;
    }

    public void setDocumentY(int i)
    {
        docY = i;
    }

    public int getDocumentY()
    {
        if(scroller != null)
        {
            Point point = scroller.getScrollPosition();
            docY = point.y;
            return point.y;
        } else
        {
            docY = 0;
            return 0;
        }
    }

    public TagItem getActive()
    {
        if(down == null)
            return null;
        else
            return down.tag;
    }

    public int getWidth()
    {
        return super.width;
    }

    public int getAvailableWidth()
    {
        return super.width - 2 * marginWidth;
    }

    public int getAvailableHeight()
    {
        return super.height - 2 * super.marginHeight;
    }

    public void initializeParent()
    {
    }

    public Color getDocBackgroundColor()
    {
        if(parent != null)
            return parent.getBackground();
        else
            return null;
    }

    public void destroy()
    {
        ScriptingEngineInterface scriptingengineinterface = Globals.getScriptingEngine();
        if(scriptingengineinterface != null)
            scriptingengineinterface.unregisterListeners(this);
        super.destroy();
    }

    void resetDocStyle()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        if(super.doc == null)
            return;
        if(super.ds.bg != null)
        {
            super.ds.bg.removeClient(parent);
            super.ds.bg = null;
        }
        Color color = (Color)super.doc.getProperty("text.color");
        if(color != null)
            super.ds.docStyle.color = color;
        super.ds.background = (Color)super.doc.getProperty("background.color");
        if(super.ds.background == null)
            super.ds.background = hjbproperties.getColor("hotjava.docbgcolor", Color.white);
        if(super.ds.bg != null)
        {
            if(super.ds.started && parent != null)
                super.ds.bg.removeClient(parent);
            super.ds.bg = null;
        }
        String s = (String)super.doc.getProperty("background.img");
        if(s != null)
            try
            {
                super.ds.bg = DocumentBackground.getDocumentBackground(new URL(super.doc.getBaseURL(), s), super.ds.background, super.doc);
                if(super.ds.started && parent != null)
                    super.ds.bg.addClient(parent);
            }
            catch(MalformedURLException _ex) { }
        super.ds.docStyle.left = super.ds.docStyle.right = marginWidth;
    }

    public void notify(Document document, int i, int j, int k)
    {
        int l = findAffected(j);
        switch(i)
        {
        case 11: // '\013'
        case 14: // '\016'
        case 23: // '\027'
        default:
            break;

        case 10: // '\n'
            int j1 = j & 0x7ffff000;
            int k2 = 4096 - (j & 0xfff);
            int l3 = j1 + 4096;
            if(hasSelection(super.ds))
            {
                if((super.ds.selStart & 0x7ffff000) == j1 && super.ds.selStart > j)
                    super.ds.selStart += k2;
                else
                if(super.ds.selStart > l3)
                    super.ds.selStart += 4096;
                if((super.ds.selEnd & 0x7ffff000) == j1 && super.ds.selEnd >= j)
                    super.ds.selEnd += k2;
                else
                if(super.ds.selEnd > l3)
                    super.ds.selEnd += 4096;
            }
            for(; l < super.nlines; l++)
            {
                DocLine docline5 = super.lines[l];
                docline5.updated = true;
                if((docline5.start & 0x7ffff000) == j1 && docline5.start > j)
                    docline5.start += k2;
                else
                if(docline5.start >= l3)
                    docline5.start += 4096;
                if((docline5.end & 0x7ffff000) == j1 && docline5.end >= j)
                    docline5.end += k2;
                else
                    docline5.end += 4096;
                if((docline5.tail & 0x7ffff000) == j1 && docline5.tail >= j)
                    docline5.tail += k2;
                else
                    docline5.tail += 4096;
            }

            if(l > 0)
                invalidateFloatersInRange(j, super.lines[--l].end);
            notifyResponsiblesInRange(document, i, j, k);
            return;

        case 12: // '\f'
            int k1 = j & 0x7ffff000;
            if(hasSelection(super.ds))
            {
                if((super.ds.selStart & 0x7ffff000) == k1 && super.ds.selStart > j)
                    super.ds.selStart += k;
                if((super.ds.selEnd & 0x7ffff000) == k1 && super.ds.selEnd >= j)
                    super.ds.selEnd += k;
            }
            int l2 = k1 + 4096;
            for(; l < super.nlines; l++)
            {
                DocLine docline4 = super.lines[l];
                if(docline4.start > l2)
                    break;
                docline4.updated = true;
                if((docline4.start & 0x7ffff000) == k1 && docline4.start > j)
                    docline4.start += k;
                if((docline4.end & 0x7ffff000) == k1 && docline4.end >= j)
                    docline4.end += k;
                if((docline4.tail & 0x7ffff000) == k1 && docline4.tail >= j)
                    docline4.tail += k;
            }

            if(l > 0)
                invalidateFloatersInRange(j, super.lines[--l].end);
            notifyResponsiblesInRange(document, i, j, k);
            return;

        case 13: // '\r'
            notifyResponsiblesInRange(document, i, j, k);
            int l1 = j & 0x7ffff000;
            if(hasSelection(super.ds))
            {
                if((super.ds.selStart & 0x7ffff000) == l1 && super.ds.selStart > j)
                    super.ds.selStart -= k;
                if((super.ds.selEnd & 0x7ffff000) == l1 && super.ds.selEnd > j)
                    super.ds.selEnd -= k;
            }
            for(; l < super.nlines; l++)
            {
                DocLine docline1 = super.lines[l];
                if(docline1.start <= j)
                {
                    invalidateFloatersInRange(docline1.start, docline1.end);
                    docline1.updated = true;
                }
                if((docline1.start & 0x7ffff000) == l1 && docline1.start > j)
                    docline1.start -= k;
                if((docline1.end & 0x7ffff000) == l1 && docline1.end > j)
                    docline1.end -= k;
                if((docline1.tail & 0x7ffff000) == l1 && docline1.tail > j)
                    docline1.tail -= k;
            }

            return;

        case 15: // '\017'
            if(hasSelection(super.ds))
                adjustSelectionBy(j, k);
            for(; l < super.nlines; l++)
            {
                DocLine docline = super.lines[l];
                if(docline.start == docline.end)
                    continue;
                if(docline.start > j)
                    break;
                if(docline.end >= j)
                    docline.end += k;
                docline.tail += k;
                docline.updated = true;
            }

            adjustSubsequentLinesBy(l, k);
            if(super.nlines > 0)
                invalidateFloatersInRange(j, super.lines[super.nlines - 1].end);
            notifyResponsiblesInRange(document, i, j, k);
            return;

        case 16: // '\020'
            int i2 = j + k;
            notifyResponsiblesInRange(document, i, j, k);
            removeFloatersInRange(j, i2);
            if(hasSelection(super.ds))
                adjustSelectionBy(j, -k);
            if(l < super.nlines)
            {
                DocLine docline2 = super.lines[l];
                if(docline2.start < j)
                {
                    if(docline2.end > j)
                        docline2.end = j;
                    docline2.tail = j;
                    docline2.updated = true;
                    l++;
                }
            }
            for(; l < super.nlines; l++)
            {
                DocLine docline3 = super.lines[l];
                if(docline3.tail > i2)
                {
                    if(docline3.start < i2)
                    {
                        docline3.start = j;
                        if(docline3.end <= i2)
                            docline3.end = j;
                        else
                            docline3.end -= k;
                        docline3.tail -= k;
                        docline3.updated = true;
                        l++;
                    }
                    break;
                }
                docline3.start = docline3.end = docline3.tail = j;
                docline3.updated = true;
            }

            adjustSubsequentLinesBy(l, -k);
            return;

        case 18: // '\022'
        case 19: // '\023'
            synchronized(SYNC_PAINTS)
            {
                Graphics g = getGraphics();
                if(g != null)
                    try
                    {
                        paintRangeNoFloaters(g, j, j + k, i == 18);
                    }
                    catch(Exception _ex) { }
                    finally
                    {
                        g.dispose();
                    }
                return;
            }

        case 17: // '\021'
            int j2 = j + k;
            int i3 = l;
            boolean flag = false;
            for(; l < super.nlines; l++)
            {
                DocLine docline6 = super.lines[l];
                if(docline6.start >= j2)
                    break;
                docline6.updated = true;
                flag = true;
            }

            for(; l < super.nlines; l++)
            {
                if(super.lines[l].y == super.lines[i3].y)
                {
                    super.lines[l].updated = true;
                    j2 = super.lines[l].end;
                    flag = true;
                    continue;
                }
                if(super.lines[l].y > super.lines[i3].y)
                    break;
            }

            notifyResponsiblesInRange(document, i, j, k);
            dirtyFloaterLines(new FormatState(), j, j2);
            touch(flag, 200);
            return;

        case 22: // '\026'
            processActivationQueue();
            return;

        case 20: // '\024'
            activateItem(j);
            return;

        case 21: // '\025'
            notifyResponsiblesInRange(document, i, j, k);
            DocItem docitem = document.items[j >> 12];
            for(int i1 = 0; i1 < super.nItemComponents; i1++)
            {
                Component component = super.itemComponents[i1].getComponent();
                int i4 = super.itemComponents[i1].getIndex();
                DocItem docitem2 = document.items[i4];
                if(docitem2 == docitem)
                {
                    ((DocPanel)component).stop();
                    ((DocPanel)component).destroy();
                    if(parent != null)
                        parent.remove(component);
                    System.arraycopy(super.itemComponents, i1 + 1, super.itemComponents, i1, super.nItemComponents - i1 - 1);
                    super.itemComponents[super.nItemComponents - 1] = null;
                    super.nItemComponents--;
                    i1--;
                }
            }

            if(super.itemsNeedingDeactivation != null)
            {
                for(int j3 = 0; j3 < super.itemsNeedingDeactivation.size(); j3++)
                {
                    DocItem docitem1 = (DocItem)super.itemsNeedingDeactivation.elementAt(j3);
                    docitem1.deactivate(this);
                }

                return;
            }
            break;

        case 24: // '\030'
            ScriptingEngineInterface scriptingengineinterface = Globals.getScriptingEngine();
            if(scriptingengineinterface != null)
            {
                scriptingengineinterface.unregisterListeners(this);
                setScriptContext(null);
            }
            for(int k3 = 0; k3 < super.itemComponents.length; k3++)
                if(super.itemComponents[k3] != null && (super.itemComponents[k3].getComponent() instanceof TagAppletPanel))
                {
                    ((TagAppletPanel)super.itemComponents[k3].getComponent()).destroy();
                    System.out.println("Destroying applet " + k3);
                }

            return;
        }
    }

    private void adjustSelectionBy(int i, int j)
    {
        if(super.ds.selStart > i)
            super.ds.selStart += j;
        if(super.ds.selEnd >= i)
            super.ds.selEnd += j;
    }

    private void adjustSubsequentLinesBy(int i, int j)
    {
        for(int k = i; k < super.nlines; k++)
        {
            DocLine docline = super.lines[k];
            if(docline.start != docline.end)
            {
                docline.start += j;
                docline.end += j;
                docline.tail += j;
            }
        }

    }

    public void activateSubItems()
    {
    }

    synchronized boolean paint(Graphics g, int i, int j, int k, int l, boolean flag, boolean flag1)
    {
        boolean flag2 = false;
        int i1 = j;
        int j1 = i1 + l;
        DocStyle docstyle = null;
        synchronized(SYNC_PAINTS)
        {
            for(int k1 = findY(j); k1 < super.nlines; k1++)
            {
                DocLine docline = super.lines[k1];
                if(docline.y >= j1)
                    break;
                i1 = docline.y;
                if(flag)
                    paintBack(g, i, i1, k, docline.height);
                if(!docline.updated)
                {
                    docstyle = paintLine(g, docline, i1, i, k, docstyle, flag1);
                } else
                {
                    flag2 = true;
                    docstyle = null;
                }
                i1 += docline.height;
            }

            if(flag && i1 < j1)
                paintBack(g, i, i1, k, j1 - i1);
            paintFloaters(g, i, j, k, l);
        }
        if(bFormatDone)
            return flag2;
        else
            return false;
    }

    public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1)
    {
        if(super.ds.started)
            ScreenUpdater.updater.notify(this, 200L, REPAINT);
        return true;
    }

    void copyLines(Graphics g, int i, int j, int k)
    {
        if(super.lines == null || super.lines.length < j)
            return;
        while(i < j) 
        {
            while(i < j && super.lines[i].updated) 
            {
                k += super.lines[i].height;
                i++;
            }
            if(i < j)
            {
                DocLine docline = super.lines[i];
                int l = docline.y;
                int i1 = docline.height;
                int j1;
                for(j1 = i + 1; j1 < j && !super.lines[j1].updated;)
                    i1 += super.lines[j1++].height;

                if(l > k)
                    g.copyArea(0, l, super.width, i1, 0, k - l);
                else
                if(l < k)
                {
                    copyLines(g, j1, j, k + i1);
                    g.copyArea(0, l, super.width, i1, 0, k - l);
                    return;
                }
                i = j1;
                k += i1;
            }
        }
    }

    void notifyDocSize()
    {
        if(super.ds.started)
            dispatchDocumentEvent(1003, this);
    }

    public void forceFormatScreen()
    {
        ScreenUpdater.updater.notify(this, 0L, FORMAT_SCREEN);
    }

    void formatScreen()
    {
        DocLine adocline[] = null;
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int i1 = 0;
        int j1 = 0;
        int k1 = 0;
        int l1 = 0;
        int i2 = 0;
        int j2 = 0;
        boolean flag = false;
        int k2 = super.docWidth;
        int l2 = super.docHeight;
        boolean flag1 = k == 1;
        if(getVScrollBarWidth() == 0)
        {
            int i3 = Math.max(scroller.getVScrollbarWidth(), 15);
            setVScrollBarWidth(i3);
            super.width -= i3;
        }
        Vector vector = (Vector)(super.floaters == null ? null : super.floaters.clone());
        do
        {
            setCompleted(false);
            super.docHeight = l2;
            super.docWidth = k2;
            k = ((flag1) ? 1 : 0);
            adocline = super.lines;
            i = super.nlines;
            j = super.doc.nitems << 12;
            k = getDocumentY();
            l = super.height + k;
            super.floaters = (Vector)(vector == null ? null : vector.clone());
            i1 = 0;
            j1 = 0;
            k1 = 0;
            if(fs == null)
                fs = new FormatState();
            if(bs == null)
                bs = new FormatState();
            fs.pos = -1;
            if(i > 0)
            {
                if(k > 0)
                {
                    int j3 = getFloatersStartY(k);
                    j1 = findY(j3);
                    for(k1 = super.doc.startPos(adocline[j1].start); j1 > 0 && k1 < adocline[j1].start; j1--);
                }
                if(k1 > adocline[j1].start)
                    adocline[j1].updated = true;
                else
                    for(; j1 < i && !adocline[j1].updated; j1++);
                if(j1 > 0)
                {
                    DocLine docline = adocline[j1 - 1];
                    i1 = docline.y + docline.height;
                    k1 = docline.end;
                }
                if(i1 >= l || k1 > j)
                {
                    ScreenUpdater.updater.notify(this, 0L, FORMAT_DOCUMENT);
                    return;
                }
            }
            i2 = i1;
            l1 = j1;
            j2 = j1;
            while(i1 < l && k1 < j) 
            {
                DocLine docline1 = formatLine(i1, k1, fs, bs);
                if(docline1 == null)
                    break;
                docline1.updated = true;
                k1 = docline1.end;
                docline1.y = i1;
                int l3 = docline1.height;
                i1 += l3;
                for(; j2 < i && adocline[j2].end <= k1; j2++)
                    l3 -= adocline[j2].height;

                super.docHeight += l3;
                if(k > super.docHeight)
                    k = super.docHeight;
                int j4 = docline1.margin >> 16;
                if(j4 < 0)
                {
                    DocStyle docstyle = getStyleAt(docline1.start);
                    j4 = getMargin(docstyle, docline1.start, docline1.y) >> 16;
                }
                if(super.docWidth < j4 + docline1.width)
                    super.docWidth = j4 + docline1.width;
                if(j2 == j1)
                {
                    byte byte0 = 10;
                    if(i + byte0 > adocline.length)
                    {
                        int i5 = Math.max(i + byte0, (3 * (adocline.length + 1)) / 2);
                        DocLine adocline1[] = new DocLine[i5];
                        System.arraycopy(adocline, 0, adocline1, 0, i);
                        adocline = adocline1;
                    }
                    System.arraycopy(adocline, j2, adocline, j2 + byte0, i - j2);
                    j2 += byte0;
                    i += byte0;
                }
                adocline[j1++] = docline1;
                if(j2 < i && i1 < l && !adocline[j2].updated && adocline[j2].start == k1 && adocline[j2].margin == fs.margin)
                {
                    do
                    {
                        DocLine docline2 = adocline[j2++];
                        docline2.updated = super.ds.bg != null && docline2.y != i1;
                        if(docline2.updated)
                            invalidateFloatersInRange(docline2.start, docline2.end);
                        i1 += docline2.height;
                        adocline[j1++] = docline2;
                    } while(j2 < i && i1 < l && !adocline[j2].updated);
                    k1 = adocline[j2 - 1].end;
                }
            }
            flag = true;
            if(!hasFrameSetPanel())
            {
                int k3 = i2;
                int i4 = l1;
                int k4 = -1;
                for(; i4 < i && k3 < l; i4++)
                    if(adocline[i4] != null)
                    {
                        k3 += adocline[i4].height;
                        k4 = i4;
                    }

                if(i4 >= i && k4 >= 0)
                {
                    DocLine docline4 = adocline[k4];
                    int j5 = getCumulativeFloaterHeight(docline4.y);
                    int k5 = docline4.y + j5;
                    if(k5 > k3)
                        k3 = k5;
                }
                if(k3 < l && scroller != null)
                {
                    int l4 = Math.max(scroller.getVScrollbarWidth(), 15);
                    if(getVScrollBarWidth() == l4)
                    {
                        super.width += l4;
                        setVScrollBarWidth(0);
                        flag = false;
                    }
                }
            }
        } while(!flag);
        boolean flag2 = true;
        if(parent != null && super.ds.background != null && !super.ds.background.equals(parent.getBackground()))
        {
            parent.setBackground(super.ds.background);
            boolean flag3 = !super.ds.started;
        }
        synchronized(SYNC_PAINTS)
        {
            Graphics g = getGraphics();
            if(g != null)
                try
                {
                    if(l1 < j1)
                    {
                        copyLines(g, l1, j1, i1 = i2);
                        DocStyle docstyle1 = null;
                        try
                        {
                            super.ds.paintingScreen = true;
                            while(l1 < j1) 
                            {
                                DocLine docline5 = adocline[l1++];
                                docline5.y = i1;
                                if(docline5.updated)
                                {
                                    docline5.updated = false;
                                    paintBack(g, 0, i1, super.width, docline5.height);
                                    docstyle1 = paintLine(g, docline5, i1, 0, super.width, docstyle1, false);
                                } else
                                {
                                    docstyle1 = null;
                                }
                                i1 += docline5.height;
                            }
                        }
                        finally
                        {
                            super.ds.paintingScreen = false;
                        }
                    }
                    if(i1 < l)
                        paintBack(g, 0, i1, super.width, super.height - i1);
                    paintFloaters(g, 0, i2, super.width, super.height - (i2 - k));
                }
                finally
                {
                    g.dispose();
                }
        }
        if(j2 < i && adocline[j2].y != i1)
        {
            updateFloatersYInRange(k1, j, i1 - adocline[j2].y);
            do
            {
                DocLine docline3 = adocline[j2++];
                docline3.y = i1;
                i1 += docline3.height;
                adocline[j1++] = docline3;
            } while(j2 < i);
        } else
        {
            System.arraycopy(adocline, j2, adocline, j1, i - j2);
        }
        i += j1 - j2;
        super.lines = adocline;
        super.nlines = i;
        ScreenUpdater.updater.notify(this, 0L, FORMAT_DOCUMENT);
    }

    public void scrollWhenDone(Point point)
    {
        scrollWhenDone(point.x, point.y);
    }

    public void scrollWhenDone(int i, int j)
    {
        xToScroll = i;
        yToScroll = j;
        needScrollOnFinish = true;
    }

    void formatDocument()
    {
        setCompleted(false);
        DocLine adocline[] = super.lines;
        int i = super.nlines;
        long l = System.currentTimeMillis() + 200L;
        if(newY == 0)
            newY = getDocumentY();
        int j;
        for(j = 0; j < i && !adocline[j].updated; j++);
        int k = super.doc.nitems << 12;
        int i1 = j <= 0 ? 0 : adocline[j - 1].end;
        int j1 = j <= 0 ? 0 : adocline[j - 1].y + adocline[j - 1].height;
        int l1 = j;
        boolean flag = false;
        if(fs == null)
        {
            fs = new FormatState();
            fs.pos = -1;
        }
        if(bs == null)
            bs = new FormatState();
        while(i1 < k) 
        {
            DocLine docline = formatLine(j1, i1, fs, bs);
            if(docline == null)
                break;
            if(j1 + docline.height > newY && j1 < super.height)
            {
                touch();
                break;
            }
            docline.y = j1;
            i1 = docline.end;
            j1 += docline.height;
            int k2 = docline.height;
            for(; l1 < i && adocline[l1].end <= i1; l1++)
                k2 -= adocline[l1].height;

            if(j1 < newY)
                newY += k2;
            super.docHeight += k2;
            if(newY > super.docHeight)
                newY = super.docHeight;
            else
            if(newY < 0)
                newY = 0;
            int i3 = docline.margin >> 16;
            if(i3 < 0)
            {
                DocStyle docstyle = getStyleAt(docline.start);
                i3 = getMargin(docstyle, docline.start, docline.y) >> 16;
            }
            if(super.docWidth < i3 + docline.width)
                super.docWidth = i3 + docline.width;
            if(l1 == j)
            {
                byte byte0 = 10;
                if(i + byte0 > adocline.length)
                {
                    DocLine adocline1[] = new DocLine[adocline.length * 2];
                    System.arraycopy(adocline, 0, adocline1, 0, i);
                    adocline = adocline1;
                }
                System.arraycopy(adocline, l1, adocline, l1 + byte0, i - l1);
                l1 += byte0;
                i += byte0;
            }
            adocline[j++] = docline;
            if(l1 < i && !adocline[l1].updated && adocline[l1].start == i1 && adocline[l1].margin == fs.margin)
            {
                do
                {
                    DocLine docline1 = adocline[l1++];
                    updateFloatersYInRange(docline1.start, docline1.end, j1 - docline1.y);
                    docline1.y = j1;
                    j1 += docline1.height;
                    adocline[j++] = docline1;
                } while(l1 < i && !adocline[l1].updated);
                i1 = adocline[l1 - 1].end;
            }
            if(System.currentTimeMillis() >= l)
            {
                ScreenUpdater.updater.notify(this, 0L, FORMAT_DOCUMENT);
                flag = true;
                break;
            }
        }
        super.docHeight = adjustDocHeightForFloaters(super.docHeight);
        if(l1 < i && adocline[l1].y != j1)
        {
            updateFloatersYInRange(i1, k, j1 - adocline[l1].y);
            do
            {
                DocLine docline2 = adocline[l1++];
                docline2.y = j1;
                j1 += docline2.height;
                adocline[j++] = docline2;
            } while(l1 < i);
        } else
        {
            System.arraycopy(adocline, l1, adocline, j, i - l1);
        }
        i += j - l1;
        if(!flag)
        {
            int i2 = 0;
            int k1 = 0;
            for(int j3 = 0; j3 < i && i2 < super.nItemComponents; j3++)
            {
                int k3 = super.itemComponents[i2].getIndex();
                DocLine docline3 = adocline[j3];
                if(k3 <= docline3.end >> 12)
                    for(; k3 >= docline3.start >> 12 && k3 < docline3.end >> 12; k3 = super.itemComponents[i2].getIndex())
                    {
                        Component component = super.itemComponents[i2].getComponent();
                        Rectangle rectangle = component.getBounds();
                        if((rectangle.y + rectangle.height < docline3.y || docline3.y + docline3.height <= rectangle.y) && (k1 + docline3.height <= newY || k1 >= super.height))
                            component.setLocation(rectangle.x, docline3.y);
                        if(++i2 >= super.nItemComponents)
                            break;
                    }

                k1 += docline3.height;
            }

        }
        super.nlines = i;
        super.lines = adocline;
        if(!flag)
        {
            if(parent != null && super.ds.background != null && !super.ds.background.equals(parent.getBackground()))
                parent.setBackground(super.ds.background);
            notifyDocSize();
            adjustScrollpaneOrigin();
            if(needScrollOnFinish)
            {
                scroller.setScrollPosition(xToScroll, yToScroll);
                docX = xToScroll;
                docY = yToScroll;
                needScrollOnFinish = false;
                label = null;
            }
            setCompleted(true);
        }
        if(label != null)
        {
            int j2 = super.doc.findLabel(label);
            if(j2 != -1 && j2 <= i1)
            {
                int l2 = findYFor(j2);
                scrollTo(docX, l2);
                Point point = scroller.getScrollPosition();
                if(point.y >= l2)
                    label = null;
            }
        }
        FinishFormatRequest finishformatrequest = new FinishFormatRequest(this);
        Globals.getInternalEventsQueue().postRequest(finishformatrequest);
        bFormatDone = true;
        fs = bs = null;
    }

    private void adjustScrollpaneOrigin()
    {
        int i = getDocumentY();
        int j = docX;
        docX = -getXOriginDelta();
        if(newY != i || docX != j)
        {
            if(i > newY && i < newY + super.height || i < newY && i + super.height > newY)
            {
                Graphics g = getGraphics();
                if(g != null)
                {
                    boolean flag = false;
                    boolean flag1 = false;
                    if(i < newY)
                    {
                        int i1 = (i + super.height) - newY;
                        int k = i;
                        g.copyArea(docX, k, super.width, i1, 0, newY - i);
                    } else
                    {
                        int j1 = (newY + super.height) - i;
                        int l = (i + super.height) - j1;
                        g.copyArea(docX, l, super.width, j1, 0, i - l);
                    }
                    g.dispose();
                }
            }
            scrollTo(docX, newY);
        }
        newY = 0;
    }

    public void updateClient(Object obj)
    {
        System.currentTimeMillis();
        if(super.ds.started)
        {
            synchronized(Globals.getAwtLock())
            {
                synchronized(getTopDocument())
                {
                    synchronized(super.doc)
                    {
                        if(obj != null)
                        {
                            if(obj.equals(REPAINT))
                            {
                                Graphics g = getGraphics();
                                if(g != null)
                                {
                                    int i = parent.getSize().width;
                                    if(paint(g, 0, 0, i, super.height, true, false))
                                        touch();
                                    g.dispose();
                                }
                                return;
                            }
                            if(obj.equals(FORMAT_SCREEN))
                            {
                                repair = false;
                                formatScreen();
                            } else
                            if(obj.equals(FORMAT_DOCUMENT))
                                formatDocument();
                        } else
                        if(repair)
                        {
                            repair = false;
                            formatScreen();
                        } else
                        {
                            formatDocument();
                        }
                    }
                }
            }
            return;
        } else
        {
            return;
        }
    }

    private Document getTopDocument()
    {
        if(parent == null)
            return super.doc;
        Container container;
        for(container = parent; !(container instanceof HotJavaBrowserBean);)
        {
            container = container.getParent();
            if(container == null)
                return super.doc;
        }

        HotJavaBrowserBean hotjavabrowserbean = (HotJavaBrowserBean)container;
        Document document = hotjavabrowserbean.getDocument();
        if(document != null)
            return document;
        else
            return super.doc;
    }

    public int updatePriority()
    {
        return !repair ? 4 : 5;
    }

    public void touch(boolean flag, int i, DocItem docitem)
    {
        if(!super.doc.isOkToFormat() || !bFormatDone)
            return;
        int j = findPos(docitem.getIndex() << 12);
        if(j < super.nlines)
            super.lines[j].updated = true;
        touch(flag, i);
    }

    public void touch(boolean flag, int i)
    {
        if(getParent() == null)
            return;
        if(!super.doc.isOkToFormat() || !bFormatDone)
            return;
        repair = flag | repair;
        if(super.ds.started)
            ScreenUpdater.updater.notify(this, i);
    }

    protected boolean handleMouseEntered(MouseEvent mouseevent)
    {
        if(mouseevent.getSource() == parent)
            dispatchDocumentEvent(1039, defaultStatusMessage);
        return super.handleMouseEntered(mouseevent);
    }

    protected boolean handleMouseUp(MouseEvent mouseevent)
    {
        synchronized(dragLock)
        {
            if(dragThread != null)
            {
                dragThread.stop();
                dragThread = null;
            }
        }
        return super.handleMouseUp(mouseevent);
    }

    protected boolean handleMouseDrag(MouseEvent mouseevent)
    {
        Point point = mouseevent.getPoint();
        if(super.handleMouseDrag(mouseevent))
        {
            int i = point.x;
            int j = point.y;
            Point point1 = scroller.getScrollPosition();
            Dimension dimension = scroller.getViewportSize();
            int k = point1.x + dimension.width;
            int l = point1.y + dimension.height;
            int i1 = scroller.getVAdjustable().getUnitIncrement();
            int j1 = scroller.getHAdjustable().getUnitIncrement();
            if(i > k)
            {
                scrollBy(j1, 0);
                notifyDragThread(j1, 0, mouseevent);
            } else
            if(i < point1.x)
            {
                scrollBy(-j1, 0);
                notifyDragThread(-j1, 0, mouseevent);
            }
            if(j > l)
            {
                scrollBy(0, i1);
                notifyDragThread(0, i1, mouseevent);
            } else
            if(j < point1.y)
            {
                scrollBy(0, -i1);
                notifyDragThread(0, -i1, mouseevent);
            }
        }
        return true;
    }

    private void notifyDragThread(int i, int j, MouseEvent mouseevent)
    {
        synchronized(dragLock)
        {
            if(dragThread == null)
            {
                dragThread = new DragThread(i, j, dragDelay, this, mouseevent);
                dragThread.start();
            } else
            {
                dragThread.notifyDragThread(i, j);
            }
        }
    }

    public void reformat()
    {
        if(!super.doc.isOkToFormat() || printing)
        {
            return;
        } else
        {
            super.reformat();
            ScreenUpdater.updater.notify(this, 0L, FORMAT_SCREEN);
            return;
        }
    }

    public void layout()
    {
        if(!super.doc.isOkToFormat())
        {
            return;
        } else
        {
            super.docWidth = 0;
            reformat();
            return;
        }
    }

    public void scrollTo(Point point)
    {
        scrollTo(point.x, point.y);
    }

    public void scrollTo(int i, int j)
    {
        int k = scroller.getViewportSize().height;
        if(super.docHeight > k && j > super.docHeight - k)
            j = super.docHeight - k;
        if(j < 0)
            j = 0;
        try
        {
            scroller.getHAdjustable().setValue(i);
            scroller.getVAdjustable().setValue(j);
            return;
        }
        catch(IllegalArgumentException _ex)
        {
            return;
        }
    }

    public void scrollBy(int i, int j)
    {
        if(i == 0 && j == 0)
            return;
        synchronized(Globals.getAwtLock())
        {
            synchronized(super.doc)
            {
                notifyDocSize();
                Point point = scroller.getScrollPosition();
                scrollTo(point.x + i, point.y + j);
            }
        }
    }

    public void top()
    {
        scrollTo(0, 0);
    }

    public void paint(Graphics g, boolean flag)
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(super.doc)
            {
                if(super.ds.started)
                {
                    boolean flag1 = !flag;
                    Rectangle rectangle = g.getClipRect();
                    if(rectangle == null)
                    {
                        if(paint(g, 0, 0, super.width, super.height, true, flag1))
                            touch();
                    } else
                    if(paint(g, rectangle.x, rectangle.y, rectangle.width, rectangle.height, true, flag1))
                        touch();
                }
            }
        }
    }

    public void select(int i, int j)
    {
        super.ds.selecter = this;
        doSelect(i, j);
    }

    public void doSelect(int i, int j)
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(super.doc)
            {
                if(i > j)
                {
                    int k = j;
                    j = i;
                    i = k;
                }
                if(i == super.ds.selStart && j == super.ds.selEnd)
                    return;
                if(i == j && i == 0 && !hasSelection(super.ds))
                    return;
                int l = super.ds.selStart;
                int i1 = super.ds.selEnd;
                super.ds.selStart = i;
                super.ds.selEnd = j;
                Graphics g = getGraphics();
                if(g != null)
                {
                    if(super.ds.selStart > i1 || super.ds.selEnd < l)
                    {
                        paintRange(g, l, i1, true);
                        paintRange(g, super.ds.selStart, super.ds.selEnd, true);
                    } else
                    {
                        int j1 = Math.min(super.ds.selStart, l);
                        int k1 = Math.max(super.ds.selStart, l);
                        if(j1 < k1)
                            paintRange(g, j1, k1, true);
                        j1 = Math.min(super.ds.selEnd, i1);
                        k1 = Math.max(super.ds.selEnd, i1);
                        if(j1 < k1)
                            paintRange(g, j1, k1, true);
                    }
                    g.dispose();
                }
            }
        }
        dispatchDocumentEvent(1007, super.doc);
    }

    public void lostOwnership(Clipboard clipboard, Transferable transferable)
    {
        lostOwnership();
    }

    public void lostOwnership()
    {
        select(-1, -1);
    }

    public void selectAll()
    {
        select(0, super.doc.length());
    }

    public synchronized void scrollTo(int i)
    {
        int j = findYFor(i);
        scrollTo(0, j);
    }

    public void showCursor(boolean flag)
    {
        synchronized(super.doc)
        {
            if(super.ds.showcur != flag)
            {
                super.ds.showcur = flag;
                if(super.ds.selStart == super.ds.selEnd)
                {
                    Graphics g = getGraphics();
                    if(g != null)
                    {
                        paintRange(g, super.ds.selStart, super.ds.selEnd, true);
                        g.dispose();
                    }
                }
            }
        }
    }

    public void showSelection(boolean flag)
    {
        synchronized(super.doc)
        {
            if(super.ds.showsel != flag)
            {
                super.ds.showsel = flag;
                if(super.ds.selStart < super.ds.selEnd)
                {
                    Graphics g = getGraphics();
                    if(g != null)
                    {
                        paintRange(g, super.ds.selStart, super.ds.selEnd, true);
                        g.dispose();
                    }
                }
            }
        }
    }

    public int getSelectEnd()
    {
        return super.ds.selEnd;
    }

    public void processDocumentEvent(DocumentEvent documentevent)
    {
        switch(documentevent.getID())
        {
        default:
            break;

        case 1043: 
            resetDocStyle();
            return;

        case 1004: 
            if(documentevent.getSource() == super.doc)
            {
                String s = (String)documentevent.getArgument();
                if(s.equals("background.img") || s.equals("text.color") || s.equals("background.color") || s.equals("margin.left") || s.equals("margin.right"))
                {
                    resetDocStyle();
                    return;
                }
                if("url".equals(s))
                {
                    if(getDocument().getURL().equals(scriptContextCodebase))
                        return;
                    ScriptingEngineInterface scriptingengineinterface = Globals.getScriptingEngine();
                    if(scriptingengineinterface != null && getScriptContext() != null)
                    {
                        scriptingengineinterface.unregisterListeners(this);
                        try
                        {
                            setScriptContext(scriptingengineinterface.getContext(this));
                            return;
                        }
                        catch(RuntimeException runtimeexception)
                        {
                            runtimeexception.printStackTrace();
                        }
                        return;
                    }
                }
            }
            break;

        case 1005: 
            if(super.doc.getState() == 12)
            {
                super.docWidth = 0;
                return;
            }
            break;

        case 1009: 
        case 1012: 
            synchronized(Globals.getAwtLock())
            {
                synchronized(super.doc)
                {
                    for(int i = 0; i < super.nItemComponents; i++)
                    {
                        Component component = super.itemComponents[i].getComponent();
                        ((DocPanel)component).interruptLoading();
                    }

                    if(super.itemsNeedingDeactivation != null)
                    {
                        for(int j = 0; j < super.itemsNeedingDeactivation.size(); j++)
                        {
                            DocItem docitem = (DocItem)super.itemsNeedingDeactivation.elementAt(j);
                            docitem.deactivate(this);
                        }

                    }
                }
            }
            return;
        }
    }

    public void processKeyEvent(KeyEvent keyevent)
    {
        switch(keyevent.getKeyCode())
        {
        case 12: // '\f'
            for(int i = 0; i < super.nlines; i++);
            super.doc.print();
            return;
        }
    }

    public String getSelectedText()
    {
        if(!hasSelection(super.ds))
            return null;
        StringBuffer stringbuffer = new StringBuffer();
        int i;
        int j;
        synchronized(this)
        {
            i = super.ds.selStart;
            j = super.ds.selEnd;
        }
        synchronized(super.doc)
        {
            for(DocItemEnumeration docitemenumeration = super.doc.getDocItems(i, j); docitemenumeration.hasMoreElements();)
            {
                DocItem docitem = (DocItem)docitemenumeration.nextElement();
                if(docitem.isText())
                {
                    TextItem textitem = (TextItem)docitem;
                    stringbuffer.append(textitem.getText(i, j));
                } else
                if(docitem.isBlock())
                    stringbuffer.append("\n");
                else
                if(docitem.isEnd())
                {
                    TagItem tagitem = ((EndTagItem)docitem).getTag(super.doc);
                    String s = tagitem.getText();
                    if(s != null)
                        stringbuffer.append(s);
                }
            }

        }
        return stringbuffer.toString();
    }

    public String getSelectedSource()
    {
        if(!hasSelection(super.ds))
            return null;
        StringBuffer stringbuffer = new StringBuffer();
        int i;
        int j;
        synchronized(this)
        {
            i = super.ds.selStart;
            j = super.ds.selEnd;
        }
        synchronized(super.doc)
        {
            for(DocItemEnumeration docitemenumeration = super.doc.getDocItems(i, j); docitemenumeration.hasMoreElements();)
            {
                DocItem docitem = (DocItem)docitemenumeration.nextElement();
                if(docitem.isText())
                {
                    TextItem textitem = (TextItem)docitem;
                    stringbuffer.append(textitem.getText(i, j));
                } else
                if(docitem.isStart() || docitem.isEnd())
                {
                    String s = docitem.toString();
                    if(s != null)
                        stringbuffer.append(s);
                }
            }

        }
        return stringbuffer.toString();
    }

    public void print(PrintJob printjob, HotJavaBrowserBean hotjavabrowserbean)
        throws DocBusyException
    {
        int i = super.doc.setOwner(14, Thread.currentThread());
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        super.ds.docStyle.left = 0;
        super.ds.docStyle.right = 0;
        super.ds.background = Color.white;
        Graphics g = printjob.getGraphics();
        super.ds.docStyle.nonScreenGraphics = g;
        String s = hjbproperties.getProperty("hotjava.pageMarkerClass");
        if(s == null)
            s = "sunw.hotjava.doc.StdPageMarker";
        PageMarker pagemarker = null;
        try
        {
            Object obj = Class.forName(s).newInstance();
            if(obj instanceof PageMarker)
                pagemarker = (PageMarker)obj;
        }
        catch(Exception exception)
        {
            System.err.println("Failed to instantiate: " + s);
            exception.printStackTrace(System.err);
        }
        try
        {
            Dimension dimension = printjob.getPageDimension();
            PageMargins pagemargins = new PageMargins();
            int j = printjob.getPageResolution();
            pagemargins.setTopMargin((hjbproperties.getInteger("hotjava.print.margin.top", 72) * j) / 72);
            pagemargins.setBottomMargin((hjbproperties.getInteger("hotjava.print.margin.bottom", 72) * j) / 72);
            pagemargins.setLeftMargin((hjbproperties.getInteger("hotjava.print.margin.left", 72) * j) / 72);
            pagemargins.setRightMargin((hjbproperties.getInteger("hotjava.print.margin.right", 72) * j) / 72);
            if(pagemarker != null)
                pagemarker.adjustMargins(pagemargins);
            String s1 = hjbproperties.getProperty("printjob.format.msg");
            hotjavabrowserbean.setStatusMessage(s1);
            Dimension dimension1 = new Dimension(dimension.width - pagemargins.getHorizMargin(), dimension.height - pagemargins.getVertMargin());
            setSize(dimension1.width, dimension1.height, 0);
            formatPrintDocument(0);
            Vector vector = new Vector(20);
            paginate(vector, pagemargins, dimension1);
            hotjavabrowserbean.setStatusMessage(hjbproperties.getProperty("printjob.start.msg"));
            printPages(printjob, g, hotjavabrowserbean, pagemarker, vector, pagemargins, dimension1);
            hotjavabrowserbean.setStatusMessage(hjbproperties.getProperty("printjob.done.msg"));
        }
        finally
        {
            super.doc.setOwner(i, null);
        }
    }

    private void paginate(Vector vector, PageMargins pagemargins, Dimension dimension)
    {
        int i = dimension.height;
        FormatterVBreakInfo formattervbreakinfo = new FormatterVBreakInfo();
        int j = 0;
        boolean flag = false;
        do
        {
            int k = j + i;
            j = findSplitY(k, i, formattervbreakinfo);
            FormatterVBreakInfo formattervbreakinfo1 = new FormatterVBreakInfo();
            flag = recordBreakInfo(j, k, formattervbreakinfo, formattervbreakinfo1);
            formattervbreakinfo1.setYOffset(j);
            vector.addElement(formattervbreakinfo);
            if(flag)
                formattervbreakinfo = formattervbreakinfo1;
        } while(flag);
    }

    private void printPages(PrintJob printjob, Graphics g, HotJavaBrowserBean hotjavabrowserbean, PageMarker pagemarker, Vector vector, PageMargins pagemargins, Dimension dimension)
    {
        int i = vector.size();
        int j = printjob.lastPageFirst() ? i - 1 : 0;
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        String s = hjbproperties.getProperty("printjob.page.msg");
        MessageFormat messageformat;
        try
        {
            Class.forName("java.text.MessageFormat");
            messageformat = new MessageFormat(s);
        }
        catch(ClassNotFoundException _ex)
        {
            messageformat = null;
        }
        Object aobj[] = new Object[1];
label0:
        do
        {
            do
            {
                aobj[0] = String.valueOf(j + 1);
                String s1;
                if(messageformat == null)
                    s1 = simpleFormat(s, aobj);
                else
                    s1 = messageformat.format(((Object) (aobj)));
                hotjavabrowserbean.setStatusMessage(s1);
                Graphics g1 = g;
                g = null;
                if(g1 == null)
                    g1 = printjob.getGraphics();
                try
                {
                    if(pagemarker != null)
                        pagemarker.markBefore(super.doc, g1.create(), printjob, j, i);
                    FormatterVBreakInfo formattervbreakinfo = (FormatterVBreakInfo)vector.elementAt(j);
                    int k = dimension.width;
                    Graphics g2 = g1.create();
                    int l = formattervbreakinfo.getYOffset();
                    g1.translate(pagemargins.getLeftMargin(), pagemargins.getTopMargin() - l);
                    g1.clipRect(0, l, dimension.width, dimension.height);
                    g1.setColor(Color.black);
                    g1.setFont(super.ds.docStyle.font);
                    print(g1, formattervbreakinfo, k);
                    Dimension dimension1 = printjob.getPageDimension();
                    g2.setColor(Color.white);
                    g2.fillRect(0, (dimension1.height - pagemargins.getBottomMargin()) + 1, dimension1.width, pagemargins.getBottomMargin());
                    if(pagemarker != null)
                        pagemarker.markAfter(super.doc, g2, printjob, j, i);
                }
                finally
                {
                    g1.dispose();
                }
                if(!printjob.lastPageFirst())
                    continue label0;
            } while(j-- > 0);
            return;
        } while(++j < i);
    }

    private String simpleFormat(String s, Object aobj[])
    {
        return s;
    }

    private void setCompleted(boolean flag)
    {
        flag &= super.doc.doneParsing();
        if(getParent() instanceof DocumentFormatterPanel)
        {
            DocumentFormatterPanel documentformatterpanel = (DocumentFormatterPanel)getParent();
            documentformatterpanel.setCompleted(flag);
        }
    }

    public void addBlinker(TextItem textitem)
    {
        if(!blinkers.contains(textitem))
            blinkers.addElement(textitem);
    }

    public Vector getBlinkers()
    {
        return blinkers;
    }

    public Container parent;
    private DocumentPanel scroller;
    static final long serialVersionUID = 0x635b7b1d1d9ef055L;
    private static final Integer FORMAT_SCREEN = new Integer(10000);
    private static final Integer FORMAT_DOCUMENT = new Integer(10001);
    private static final Integer REPAINT = new Integer(10003);
    private Object SYNC_PAINTS;
    boolean bFormatDone;
    int scrollBarWidth;
    boolean repair;
    boolean repaintOnWake;
    private int xToScroll;
    private int yToScroll;
    private boolean needScrollOnFinish;
    private MouseDownInfo down;
    TagItem active;
    private int marginWidth;
    private int docX;
    private int docY;
    private int newY;
    private boolean printing;
    private Vector blinkers;
    DragThread dragThread;
    Object dragLock;
    int dragDelay;
    String label;
    long lastFormatedTime;
    int pendingFormatRequest;
    private FormatState fs;
    private FormatState bs;
    private Formatter.FormatterMouseListener formatterMouseListener;
    private DocListener formatterDocListener;
    private KeyListener keyListener;
    private ScriptContextInterface scriptContext;
    private URL scriptContextCodebase;
    private ScriptSecuritySupportInterface securitySupport;
    private String defaultStatusMessage;

}
