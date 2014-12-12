// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Formatter.java

package sunw.hotjava.doc;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.bean.URLPooler;
import sunw.hotjava.forms.SELECT;
import sunw.hotjava.misc.*;
import sunw.hotjava.script.ScriptingEngineInterface;
import sunw.hotjava.tables.*;
import sunw.hotjava.tags.*;
import sunw.html.Attributes;

// Referenced classes of package sunw.hotjava.doc:
//            CompoundVBreakInfo, DocConstants, DocItem, DocLine, 
//            DocPanel, DocStyle, Document, DocumentBackground, 
//            DocumentEvent, DocumentEventMulticaster, DocumentEventSource, DocumentFormatter, 
//            DocumentFormatterPanel, DocumentPanel, DocumentState, ElementInfo, 
//            Floatable, FloaterInfo, FormatState, FormatterVBreakInfo, 
//            ItemComponent, ItemIterator, MouseDownInfo, NamedLink, 
//            ProcessActivationQueueThread, Range, Responsibility, ResponsibilityEnumeration, 
//            ResponsibilityRangeEnumeration, TagItem, TraversalState, VBreakInfo, 
//            DocumentListener, DocFont, TextItem

public abstract class Formatter
    implements DocConstants, DocPanel, ImageObserver, DocumentEventSource
{
    protected class FormatterMouseListener extends MouseAdapter
        implements MouseMotionListener
    {

        public void mouseEntered(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mouseExited(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mousePressed(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mouseReleased(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mouseDragged(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mouseMoved(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        protected FormatterMouseListener()
        {
        }
    }

    private class DocumentEventRequest extends sunw.hotjava.misc.RequestProcessor.Request
    {

        public void execute()
        {
            doMouseAction(mouseEvent);
        }

        private MouseEvent mouseEvent;

        public DocumentEventRequest(MouseEvent mouseevent)
        {
            mouseEvent = mouseevent;
        }
    }


    public void addTopLevelWindowListener(WindowListener windowlistener)
    {
    }

    public DocumentState getDocumentState()
    {
        return ds;
    }

    public int getVScrollBarWidth()
    {
        return 0;
    }

    protected void setVScrollBarWidth(int i)
    {
    }

    protected boolean getRepair()
    {
        return false;
    }

    protected void setRepair(boolean flag)
    {
    }

    public abstract int getAvailableWidth();

    public abstract int getAvailableHeight();

    protected abstract int getStartIndex();

    protected abstract int getMaxIndex();

    public abstract void select(int i, int j);

    protected abstract Graphics getGraphics();

    public abstract void getBackgroundDisplacement(Point point);

    protected abstract void disconnectFromParent(Component component);

    public abstract Container getParent();

    public int getAvailableWidth(int i)
    {
        int j = getMargin(i);
        return width - (j >> 16) - (j & 0xffff);
    }

    public int getMargin(DocStyle docstyle, int i, int j)
    {
        int k = getFloatingIndentAt(j, true) + docstyle.left;
        int l = getFloatingIndentAt(j, false) + docstyle.right;
        return k << 16 | l;
    }

    public int getMargin(int i)
    {
        DocStyle docstyle = getStyleAt(i);
        int j = docstyle.left;
        int k = docstyle.right;
        return j << 16 | k;
    }

    void setToFormattedSize(Component component)
    {
        component.setSize(Math.max(width, docWidth), Math.max(height, docHeight));
    }

    public Document getDocument()
    {
        return doc;
    }

    public void addDocumentListener(DocumentListener documentlistener)
    {
        listeners.add(documentlistener);
    }

    public void removeDocumentListener(DocumentListener documentlistener)
    {
        listeners.remove(documentlistener);
    }

    public void sendDocumentEvent(DocumentEvent documentevent)
    {
        listeners.documentChanged(documentevent);
    }

    public void dispatchDocumentEvent(int i, Object obj)
    {
        DocumentEvent documentevent = new DocumentEvent(this, i, obj);
        listeners.documentChanged(documentevent);
    }

    public void dispatchDocumentEvent(Object obj, int i, boolean flag, Object obj1)
    {
        DocumentEvent documentevent = new DocumentEvent(obj, i, flag, obj1);
        listeners.documentChanged(documentevent);
    }

    public void dispatchDocumentEvent(int i, Object obj, DocItem docitem)
    {
        DocumentEvent documentevent = new DocumentEvent(this, i, obj);
        documentevent.setDocItemSource(docitem);
        listeners.documentChanged(documentevent);
    }

    public Formatter getParentFormatter()
    {
        return null;
    }

    public boolean isFormatterBackgroundColorDeliberate()
    {
        return false;
    }

    private MouseDownInfo getMouseDownInfo()
    {
        return getTopFormatter().getMouseDownInfo();
    }

    protected void setMouseDownInfo(MouseDownInfo mousedowninfo)
    {
        getTopFormatter().setMouseDownInfo(mousedowninfo);
    }

    public void touch()
    {
        touch(doc.isOkToFormat(), 0);
    }

    public abstract void touch(boolean flag, int i);

    public abstract void touch(boolean flag, int i, DocItem docitem);

    public DocFont getDocFont()
    {
        return ds.docStyle.font;
    }

    public final void setBreak(FormatState formatstate, FormatState formatstate1, int i, int j)
    {
        formatstate1.copyFrom(formatstate);
        formatstate1.pos = ((TraversalState) (formatstate)).pos & 0x7ffff000 | i;
        formatstate1.width = j;
    }

    public TagItem inTag(DocStyle docstyle, String s)
    {
        for(; docstyle.tag != null; docstyle = docstyle.next)
            if(s.equals(docstyle.tag.getName()))
                return docstyle.tag;

        return null;
    }

    public Component getPanel(DocItem docitem)
    {
        for(int i = 0; i < nItemComponents; i++)
        {
            ItemComponent itemcomponent = itemComponents[i];
            int j = itemcomponent.getIndex();
            DocItem docitem1 = doc.getItem(j);
            if(docitem1 == docitem)
                return itemcomponent.getComponent();
        }

        return null;
    }

    public boolean hasFrameSetPanel()
    {
        return hasFrameSetPanel;
    }

    public FrameSetPanel getFrameSetPanel()
    {
        for(int i = 0; i < nItemComponents; i++)
        {
            Component component = itemComponents[i].getComponent();
            if(component instanceof FrameSetPanel)
                return (FrameSetPanel)component;
        }

        return null;
    }

    public void addFrameSetPanel(FrameSetPanel framesetpanel)
    {
        if(numberOfFrameSetPanels == allFrameSetPanels.length)
        {
            FrameSetPanel aframesetpanel[] = new FrameSetPanel[(int)((double)allFrameSetPanels.length * 1.5D)];
            System.arraycopy(allFrameSetPanels, 0, aframesetpanel, 0, allFrameSetPanels.length);
            allFrameSetPanels = aframesetpanel;
        }
        allFrameSetPanels[numberOfFrameSetPanels] = framesetpanel;
        numberOfFrameSetPanels++;
    }

    public FrameSetPanel getFrameSetPanel(int i)
    {
        if(i < allFrameSetPanels.length)
            return allFrameSetPanels[i];
        else
            return null;
    }

    public int getNumberOfFrameSetPanels()
    {
        return numberOfFrameSetPanels;
    }

    public TagAppletPanel getAppletPanel(String s)
    {
        for(int i = 0; i < nItemComponents; i++)
        {
            Component component = itemComponents[i].getComponent();
            if(component instanceof TablePanel)
            {
                TagAppletPanel tagappletpanel = ((TablePanel)component).getAppletPanel(s);
                if(tagappletpanel != null)
                    return tagappletpanel;
            } else
            if(component instanceof TagAppletPanel)
            {
                TagAppletPanel tagappletpanel1 = (TagAppletPanel)component;
                String s1 = tagappletpanel1.getParameter("name");
                if(s1 != null)
                    s1 = s1.toLowerCase();
                if(s.equals(s1))
                    return tagappletpanel1;
            }
        }

        return null;
    }

    public void getAppletPanels(Vector vector, boolean flag)
    {
        for(int i = 0; i < nItemComponents; i++)
        {
            Component component = itemComponents[i].getComponent();
            if(component instanceof TablePanel)
                ((TablePanel)component).getAppletPanels(vector);
            else
            if(flag && (component instanceof FrameSetPanel))
                ((FrameSetPanel)component).getAppletPanels(vector);
            else
            if(component instanceof TagAppletPanel)
                vector.addElement(component);
        }

    }

    public void getAppletPanels(Vector vector)
    {
        getAppletPanels(vector, false);
    }

    protected DocStyle getStyleAt(int i)
    {
        return doc.getStyle(getBaseStyle(), i, getStartIndex());
    }

    protected DocStyle getBaseStyle()
    {
        return ds.docStyle;
    }

    protected void wakeScreenUpdater()
    {
    }

    protected void setVisible(Component component, BitSet bitset, int i)
    {
    }

    protected void setInvisible(Component component, BitSet bitset, int i)
    {
    }

    protected void connectBackgroundClient()
    {
    }

    protected void disconnectBackgroundClient()
    {
    }

    protected void quitViewingDoc()
    {
    }

    protected void setFormatterStarted(boolean flag)
    {
    }

    protected void activateItem(int i)
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(doc)
            {
                if(ds.started)
                {
                    processActivationQueue();
                    performActivation(i);
                } else
                {
                    if(activationQueue == null)
                        activationQueue = new Vector();
                    activationQueue.addElement(new Integer(i));
                }
            }
        }
    }

    private void performActivation(int i)
    {
        DocItem docitem = doc.items[i >> 12];
        Component component = docitem.createView(this, doc);
        if(docitem.activate(this, doc))
        {
            if(itemsNeedingDeactivation == null)
                itemsNeedingDeactivation = new Vector(4);
            itemsNeedingDeactivation.addElement(docitem);
        }
        if(component != null)
        {
            if(component instanceof FrameSetPanel)
                hasFrameSetPanel = true;
            validateItemComponentVector();
            itemComponents[nItemComponents++] = new ItemComponent(component, i >> 12);
            Container container = getParent();
            if(container != null && component.getParent() == null)
                container.add(component);
            if(docitem instanceof TABLE)
            {
                for(int j = (i >> 12) + 1; j < ((i >> 12) + docitem.getOffset()) - 1; j++)
                {
                    DocItem docitem1 = doc.items[j];
                    if((docitem1 instanceof TD) || (docitem1 instanceof TABLE))
                        j += docitem1.getOffset() - 1;
                    else
                    if(docitem1.needsActivation() && docitem1.getOffset() >= 0)
                        activateItem((docitem1.getIndex() << 12) + docitem1.getOffset());
                }

            }
            ((DocPanel)component).activateSubItems();
            ((DocPanel)component).start();
            Graphics g = getGraphics();
            if(g != null)
            {
                try
                {
                    paintRange(g, i, i + 4096, false);
                }
                finally
                {
                    g.dispose();
                }
                return;
            }
        }
    }

    public void activateItemComponents()
    {
        DocItem docitem;
        for(int i = getStartIndex(); i < getMaxIndex(); i += docitem.getActivateIncrement())
        {
            docitem = doc.items[i];
            docitem.getOffset();
            if(docitem.needsActivation() && docitem.getOffset() >= 0)
                activateItem(i << 12);
        }

    }

    private void validateItemComponentVector()
    {
        if(itemComponents == null)
            itemComponents = new ItemComponent[1];
        if(nItemComponents >= itemComponents.length)
        {
            ItemComponent aitemcomponent[] = new ItemComponent[(3 * (nItemComponents + 1)) / 2];
            System.arraycopy(itemComponents, 0, aitemcomponent, 0, nItemComponents);
            itemComponents = aitemcomponent;
        }
    }

    protected void processActivationQueue()
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(doc)
            {
                if(activationQueue == null)
                    return;
                int i = activationQueue.size();
                for(int j = 0; j < i; j++)
                {
                    Integer integer = (Integer)activationQueue.elementAt(j);
                    performActivation(integer.intValue());
                }

                activationQueue = null;
            }
        }
    }

    protected void maybeStartActivationQueueThread()
    {
        if(activationQueue == null)
        {
            return;
        } else
        {
            ProcessActivationQueueThread processactivationqueuethread = new ProcessActivationQueueThread(this);
            processactivationqueuethread.setPriority(4);
            processactivationqueuethread.start();
            return;
        }
    }

    protected void copyPanelsForPrinting(Formatter formatter, DocumentState documentstate)
    {
        int i = formatter.nItemComponents;
        ItemComponent aitemcomponent[] = formatter.itemComponents;
        itemComponents = new ItemComponent[i];
        nItemComponents = i;
        for(int j = 0; j < nItemComponents; j++)
        {
            ItemComponent itemcomponent = aitemcomponent[j];
            Component component = itemcomponent.getComponent();
            if(component instanceof TablePanel)
                itemcomponent = new ItemComponent(((TablePanel)component).copyForPrinting(this, documentstate), itemcomponent.getIndex());
            itemComponents[j] = itemcomponent;
        }

    }

    public void start()
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(doc)
            {
                if(!ds.started)
                {
                    getParent();
                    for(int i = 0; i < nItemComponents; i++)
                    {
                        Component component = itemComponents[i].getComponent();
                        setVisible(component, panelVisibility, i);
                        try
                        {
                            ((DocPanel)component).start();
                        }
                        catch(Exception exception)
                        {
                            exception.printStackTrace();
                        }
                    }

                    wakeScreenUpdater();
                    reactivateItems();
                    setFormatterStarted(true);
                    connectBackgroundClient();
                    if(doc.doneParsing())
                        maybeStartActivationQueueThread();
                }
            }
        }
    }

    public void unregisterListeners()
    {
        for(int i = 0; i < nItemComponents; i++)
        {
            Component component = itemComponents[i].getComponent();
            if(component instanceof TablePanel)
                ((TablePanel)component).unregisterListeners();
        }

    }

    public void stop()
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(doc)
            {
                if(ds.started)
                {
                    panelVisibility = new BitSet(nItemComponents);
                    getParent();
                    for(int i = 0; i < nItemComponents; i++)
                    {
                        Component component = itemComponents[i].getComponent();
                        ((DocPanel)component).stop();
                        setInvisible(component, panelVisibility, i);
                    }

                    deactivateItems();
                    setFormatterStarted(false);
                    disconnectBackgroundClient();
                    clearParentBackground();
                }
            }
        }
    }

    public void destroy()
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(doc)
            {
                quitViewingDoc();
                while(nItemComponents > 0) 
                {
                    Component component = itemComponents[--nItemComponents].getComponent();
                    if(ds.started)
                    {
                        ((DocPanel)component).stop();
                        component.setVisible(false);
                    }
                    ((DocPanel)component).destroy();
                    disconnectFromParent(component);
                }
                deactivateItems();
            }
        }
    }

    public void interruptLoading()
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(doc)
            {
                if(ds.started)
                {
                    for(int i = 0; i < nItemComponents; i++)
                    {
                        Component component = itemComponents[i].getComponent();
                        ((DocPanel)component).interruptLoading();
                    }

                    deactivateItemsForInterrupt();
                }
            }
        }
    }

    protected void deactivateItems()
    {
        if(itemsNeedingDeactivation != null)
        {
            for(int i = 0; i < itemsNeedingDeactivation.size(); i++)
            {
                DocItem docitem = (DocItem)itemsNeedingDeactivation.elementAt(i);
                docitem.deactivate(this);
            }

        }
    }

    private void deactivateItemsForInterrupt()
    {
        if(itemsNeedingDeactivation != null)
        {
            for(int i = 0; i < itemsNeedingDeactivation.size(); i++)
            {
                DocItem docitem = (DocItem)itemsNeedingDeactivation.elementAt(i);
                if(docitem.getTag(doc).getName().equals("img"))
                {
                    if(((IMG)docitem).getState() < 128)
                        docitem.deactivate(this);
                } else
                {
                    docitem.deactivate(this);
                }
            }

        }
    }

    protected void reactivateItems()
    {
        if(itemsNeedingDeactivation != null)
        {
            for(int i = 0; i < itemsNeedingDeactivation.size(); i++)
            {
                DocItem docitem = (DocItem)itemsNeedingDeactivation.elementAt(i);
                docitem.activate(this, doc);
            }

        }
    }

    public void reactivateItemsForStopLoading()
    {
        reactivateItems();
    }

    public int findYOfDocLine(int i)
    {
        if(lines.length > i && lines.length >= 0)
        {
            DocLine docline = lines[i];
            return docline.y;
        } else
        {
            return -1;
        }
    }

    public int findPos(int i)
    {
        DocLine adocline[] = lines;
        int j = 0;
        for(int k = nlines - 1; j < k;)
        {
            int l = j + (k - j >> 1);
            DocLine docline = adocline[l];
            if(i < docline.start)
                k = l - 1;
            else
            if(i >= docline.end)
                j = l + 1;
            else
                return l;
        }

        return j;
    }

    protected int findXPos(DocLine docline, DocStyle docstyle, int i)
    {
        if(i < docline.start)
            return 0;
        if(i >= docline.end)
            return docline.width + (docline.margin >> 16);
        int j = docline.start;
        int k = j >> 12;
        int l = i >> 12;
        int i1 = 0;
        while(k < l) 
        {
            DocItem docitem = doc.items[k];
            int j1 = docitem.getOffset();
            if(j1 < 0)
                docitem = doc.items[k + j1];
            FloaterInfo floaterinfo;
            if((floaterinfo = getFloaterInfo(docitem)) == null || !floaterinfo.isLeft)
                if(j1 > 0)
                {
                    if((j & 0xfff) == 0)
                        docstyle = docstyle.push(docitem);
                    i1 += docitem.getStartTagWidth(docline, docstyle, i1, j & 0xfff, 4095);
                } else
                if(j1 < 0)
                {
                    i1 += docitem.getEndTagWidth(docline, docstyle, i1, j & 0xfff, 4095);
                    docstyle = docstyle.next;
                } else
                {
                    docstyle = docitem.modifyStyleInPlace(docstyle);
                    i1 += docitem.getWidth(docline, docstyle, i1, j & 0xfff, 4095);
                }
            j = ++k << 12;
        }
        DocItem docitem1 = doc.items[k];
        int k1 = docitem1.getOffset();
        if(k1 > 0)
        {
            if((j & 0xfff) == 0)
                docstyle = docstyle.push(docitem1);
        } else
        if(k1 == 0)
            docstyle = docitem1.modifyStyleInPlace(docstyle);
        FloaterInfo floaterinfo1 = getFloaterInfo(docitem1);
        if(floaterinfo1 == null)
        {
            return (docline.margin >> 16) + i1 + docitem1.getWidth(docline, docstyle, i1, j & 0xfff, i & 0xfff);
        } else
        {
            Point point = getFloaterOrigin(this, floaterinfo1.item, floaterinfo1.isLeft);
            return point.x;
        }
    }

    protected int findAffected(int i)
    {
        DocLine adocline[] = lines;
        int j = 0;
        for(int k = nlines - 1; j < k;)
        {
            int l = j + (k - j >> 1);
            if(i > adocline[l].tail)
                j = l + 1;
            else
                k = l;
        }

        return j;
    }

    protected int findY(int i)
    {
        DocLine adocline[] = lines;
        int j = 0;
        for(int k = nlines - 1; j < k;)
        {
            int l = j + (k - j >> 1);
            DocLine docline = adocline[l];
            int i1 = docline.y;
            if(i <= i1)
            {
                if(i == i1)
                    k = l;
                else
                    k = l - 1;
            } else
            if(i >= i1 + docline.height)
                j = l + 1;
            else
                return l;
        }

        return j;
    }

    protected int findX(int i, int j)
    {
        return findX(i, j, -1);
    }

    protected int findX(int i, int j, int k)
    {
        if(i >= nlines)
            return getMaxIndex() << 12;
        DocLine docline = lines[i];
        int l = docline.start;
        int i1 = l >> 12;
        int j1 = docline.end;
        DocStyle docstyle = getStyleAt(l);
        FloaterInfo floaterinfo = findFloaterAt(j, k);
        if(floaterinfo != null)
            if(floaterinfo.startY + floaterinfo.height < k)
                return getMaxIndex() << 12;
            else
                return ((DocItem) (floaterinfo.item)).index << 12;
        if(docline.y + docline.height < k)
            return getMaxIndex() << 12;
        if(j <= docline.margin >> 16)
            return l;
        if(j >= docline.width + (docline.margin >> 16))
            return j1;
        int k1 = 0;
        j -= docline.margin >> 16;
        for(int l1 = getMaxIndex(); l < j1 && i1 < l1; l = ++i1 << 12)
        {
            DocItem docitem = doc.items[i1];
            int i2 = docitem.getOffset();
            if(i2 > 0)
            {
                if((l & 0xfff) == 0)
                    docstyle = docstyle.push(docitem);
            } else
            if(i2 == 0)
                docstyle = docitem.modifyStyleInPlace(docstyle);
            int j2 = 0;
            if(i2 > 0)
                j2 = docitem.findStartTagX(docline, docstyle, k1, l, j, this);
            else
            if(i2 < 0)
                j2 = doc.items[i1 + i2].findEndTagX(docline, docstyle, k1, l, j, this);
            else
                j2 = docitem.findX(docline, docstyle, k1, l, j, this);
            if(isFloater(docitem))
            {
                j2 = -1;
                if((docitem instanceof TABLE) && ((TABLE)docitem).getLastX() > j)
                {
                    docitem = doc.items[i1 + i2];
                    i1 += i2;
                    i2 = docitem.getOffset();
                    l = i1 << 12;
                }
            }
            if(j2 >= 0)
                return j2;
            if(docitem instanceof SELECT)
                i1 += docitem.getOffset() - 1;
            k1 -= j2 + 1;
            if(i2 < 0)
                docstyle = docstyle.next;
        }

        return j1;
    }

    public void reformat()
    {
        synchronized(doc)
        {
            int i = getMaxIndex() << 12;
            removeFloatersInRange(0, i);
            DocLine adocline[] = lines;
            for(int j = nlines; j-- > 0;)
                adocline[j].updated = true;

            reformatResponsibles();
        }
    }

    public int findYFor(int i)
    {
        Responsibility responsibility = findResponsibility(i);
        if(responsibility != null)
        {
            DocPanel docpanel = responsibility.getTarget();
            return docpanel.findYFor(i);
        } else
        {
            Point point = new Point(0, 0);
            getBackgroundDisplacement(point);
            DocLine docline = lines[findPos(i)];
            return docline.y + -point.y;
        }
    }

    void adjustXOrigin(int i)
    {
        if(i < 0 && XOriginDelta > i)
        {
            XOriginDelta = i >> 16;
            return;
        } else
        {
            XOriginDelta = 0;
            return;
        }
    }

    int getXOriginDelta()
    {
        return XOriginDelta;
    }

    protected DocLine formatLine(int i, int j, FormatState formatstate, FormatState formatstate1)
    {
        formatstate.y = i;
        if(((TraversalState) (formatstate)).pos != j)
        {
            formatstate.pos = j;
            formatstate.style = getStyleAt(((TraversalState) (formatstate)).pos);
        } else
        if(((TraversalState) (formatstate)).style == null)
            formatstate.style = getStyleAt(((TraversalState) (formatstate)).pos);
        formatstate.startPos = formatstate1.pos = ((TraversalState) (formatstate)).pos;
        formatstate.state = 0;
        formatstate.width = 0;
        formatstate.ascent = formatstate.descent = 0;
        formatstate.above = formatstate.startPos != getStartIndex() << 12 ? 0 : marginHeight;
        formatstate.below = 0;
        formatstate.textAscent = 0;
        formatstate.margin = getMargin(((TraversalState) (formatstate)).style, ((TraversalState) (formatstate)).pos, formatstate.y + formatstate.above);
        formatstate.maxWidth = width - ((formatstate.margin >> 16) + formatstate.margin & 0xffff);
        formatstate.format = ((TraversalState) (formatstate)).style.format;
        int k = getMaxIndex() << 12;
        setBreak(formatstate, formatstate1, 0, formatstate.width);
        while(((TraversalState) (formatstate)).pos < k) 
        {
            int l = ((TraversalState) (formatstate)).pos >> 12;
            DocItem docitem = doc.items[l];
            int j1 = docitem.getOffset();
            if(j1 <= 0 ? j1 >= 0 ? docitem.format(this, formatstate, formatstate1) : doc.items[l + j1].formatEndTag(this, formatstate, formatstate1) : docitem.formatStartTag(this, formatstate, formatstate1))
                break;
            if(formatstate.width > formatstate.maxWidth && ((TraversalState) (formatstate1)).pos > formatstate.startPos && ((TraversalState) (formatstate)).style.nobreak != 1)
            {
                formatstate.pos = ((TraversalState) (formatstate1)).pos;
                formatstate.width = formatstate1.width;
                formatstate.ascent = formatstate1.ascent;
                formatstate.descent = formatstate1.descent;
                formatstate.style = ((TraversalState) (formatstate1)).style;
                break;
            }
        }
        switch(formatstate.format)
        {
        case 3: // '\003'
            int i1 = (formatstate.maxWidth - formatstate.width) / 2 << 16;
            if(i1 < ((TraversalState) (formatstate)).style.left)
                i1 = ((TraversalState) (formatstate)).style.left;
            formatstate.margin += i1;
            adjustXOrigin(i1);
            break;

        case 1: // '\001'
            formatstate.margin += formatstate.maxWidth - formatstate.width << 16;
            break;
        }
        insertQueuedFloaters(formatstate);
        formatFloaterQueue = null;
        finishLineFormatting(formatstate);
        DocLine docline = new DocLine(formatstate.startPos, ((TraversalState) (formatstate)).pos, ((TraversalState) (formatstate)).pos, formatstate.margin, formatstate.width, formatstate.above, formatstate.below, formatstate.ascent, formatstate.descent, formatstate.textAscent);
        formatstate.prevLine = docline;
        formatstate.totalHeight += formatstate.descent + formatstate.ascent;
        return docline;
    }

    protected void finishLineFormatting(FormatState formatstate)
    {
    }

    protected void processMouseAction(MouseEvent mouseevent)
    {
        DocumentEventRequest documenteventrequest = new DocumentEventRequest(mouseevent);
        Globals.getInternalEventsQueue().postRequest(documenteventrequest);
    }

    private void doMouseAction(MouseEvent mouseevent)
    {
        switch(mouseevent.getID())
        {
        case 501: 
            handleMouseDown(mouseevent);
            return;

        case 506: 
            handleMouseDrag(mouseevent);
            return;

        case 502: 
            handleMouseUp(mouseevent);
            return;

        case 503: 
            handleMouseMove(mouseevent);
            return;

        case 504: 
            handleMouseEntered(mouseevent);
            return;

        case 505: 
            dispatchDocumentEvent(1001, null);
            dispatchDocumentEvent(1027, null);
            return;
        }
    }

    protected boolean handleMouseEntered(MouseEvent mouseevent)
    {
        return true;
    }

    protected boolean handleMouseDown(MouseEvent mouseevent)
    {
        String s = selectFrame();
        Container container = getParent();
        container.requestFocus();
        for(; !(container instanceof HotJavaBrowserBean); container = container.getParent());
        Object obj = ((HotJavaBrowserBean)container).getSelector();
        if(obj != null)
            if(obj instanceof DocumentFormatter)
                ((DocumentFormatter)obj).select(0, 0);
            else
                ((TextComponent)obj).select(0, 0);
        MouseDownInfo mousedowninfo = new MouseDownInfo();
        setMouseDownInfo(mousedowninfo);
        mousedowninfo.x = mapPaneXToDocX(mouseevent.getX());
        mousedowninfo.y = mapPaneYToDocY(mouseevent.getY());
        int i = findY(mousedowninfo.y);
        mousedowninfo.pos = findX(i, mousedowninfo.x, mousedowninfo.y);
        clickCancelled = false;
        if(findResponsibility(mousedowninfo.pos) == null || (mouseevent.getSource() instanceof DocumentFormatterPanel) || (mouseevent.getSource() instanceof TableElementPanel))
        {
            int j = mousedowninfo.pos >> 12;
            if(mouseevent.isPopupTrigger())
            {
                setHoverInfo(mouseevent, i, mousedowninfo, s);
                return true;
            }
            if(j < doc.nitems)
            {
                DocItem docitem = doc.items[j];
                DocStyle docstyle = getStyleAt(mousedowninfo.pos);
                if(docitem.needsLoading())
                {
                    HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
                    String s1 = hjbproperties.getProperty("img.load");
                    dispatchDocumentEvent(1000, s1);
                    docitem.load(this);
                    return true;
                }
                if(docitem.isMappable())
                {
                    mousedowninfo.tag = (TagItem)docitem;
                    select(mousedowninfo.pos, mousedowninfo.pos);
                    DocLine docline = lines[i];
                    FloaterInfo floaterinfo = getFloaterInfo(docitem);
                    int k;
                    int l;
                    if(floaterinfo == null)
                    {
                        k = mousedowninfo.x - findXPos(docline, docstyle, j << 12);
                        l = mousedowninfo.y - docline.y;
                    } else
                    {
                        Point point = getFloaterOrigin(this, floaterinfo.item, floaterinfo.isLeft);
                        k = mousedowninfo.x - point.x;
                        l = mousedowninfo.y - floaterinfo.startY;
                    }
                    String s2 = doc.getBaseURL() != null ? doc.getBaseURL().toString() : null;
                    NamedLink namedlink = docitem.map(docline, docstyle, s2, k, l);
                    if(namedlink == null)
                        return true;
                    URL url = namedlink.url;
                    DocumentEvent documentevent1 = new DocumentEvent(this, 1035, mouseevent.isShiftDown(), url);
                    documentevent1.setDocItemSource(((DocItem) (namedlink.tag == null ? docitem : ((DocItem) (namedlink.tag)))));
                    getTopFormatter().sendDocumentEvent(documentevent1);
                    if(!documentevent1.getEventCancelled())
                        getTopFormatter().dispatchDocumentEvent(1001, url, ((DocItem) (namedlink.tag == null ? docitem : ((DocItem) (namedlink.tag)))));
                    else
                        clickCancelled = true;
                    return true;
                }
                TagItem tagitem = inTag(docstyle, "a");
                if(tagitem != null && docstyle.href != null)
                {
                    DocumentEvent documentevent = new DocumentEvent(this, 1035, mouseevent.isShiftDown(), docstyle.href);
                    documentevent.setDocItemSource(tagitem);
                    getTopFormatter().sendDocumentEvent(documentevent);
                    if(!documentevent.getEventCancelled())
                    {
                        getTopFormatter().dispatchDocumentEvent(1001, docstyle.href, tagitem);
                        mousedowninfo.tag = tagitem;
                    } else
                    {
                        clickCancelled = true;
                    }
                    select(mousedowninfo.pos, mousedowninfo.pos);
                    paintTag(tagitem);
                    return true;
                }
            }
            if(mouseevent.isShiftDown())
            {
                if(mousedowninfo.pos <= ds.selStart)
                {
                    if(ds.selecter != this)
                        ds.selEnd = mousedowninfo.pos;
                    select(mousedowninfo.pos, ds.selEnd);
                    mousedowninfo.pos = ds.selEnd;
                } else
                {
                    if(ds.selecter != this)
                        ds.selStart = mousedowninfo.pos;
                    select(ds.selStart, mousedowninfo.pos);
                    mousedowninfo.pos = ds.selStart;
                }
            } else
            {
                select(mousedowninfo.pos, mousedowninfo.pos);
            }
        }
        return true;
    }

    String selectFrame()
    {
        for(Container container = getParent(); container != null; container = container.getParent())
            if(container instanceof FramePanel)
            {
                dispatchDocumentEvent(1041, container);
                ((FramePanel)container).setSelected(true);
                return ((FramePanel)container).getFormatter().getDocument().getURL().toString();
            }

        return null;
    }

    protected boolean handleMouseDrag(MouseEvent mouseevent)
    {
        Point point = mouseevent.getPoint();
        MouseDownInfo mousedowninfo = getMouseDownInfo();
        if(mousedowninfo != null && findResponsibility(mousedowninfo.pos) == null)
        {
            if(mousedowninfo.tag != null)
            {
                if(Math.abs(mapPaneXToDocX(point.x) - mousedowninfo.x) < 5 && Math.abs(mapPaneYToDocY(point.y) - mousedowninfo.y) < 5)
                    return true;
                TagItem tagitem = mousedowninfo.tag;
                mousedowninfo.tag = null;
                paintTag(tagitem);
                dispatchDocumentEvent(1001, null);
            }
            int i = findX(findY(mapPaneYToDocY(point.y)), mapPaneXToDocX(point.x));
            if(ds.selecter != this)
                mousedowninfo.pos = i;
            select(mousedowninfo.pos, i);
        }
        return true;
    }

    private boolean sameFileURL(URL url, URL url1)
    {
        return url.getPort() == url1.getPort() && url.getProtocol().equals(url1.getProtocol()) && url.getFile().equals(url1.getFile()) && url.getHost().equals(url1.getHost());
    }

    protected boolean handleMouseUp(MouseEvent mouseevent)
    {
        if(mouseevent.isPopupTrigger())
        {
            unsetHoverInfo(mouseevent);
            return true;
        }
        Point point = mouseevent.getPoint();
        int i = findY(mapPaneYToDocY(point.y));
        int j = findX(i, mapPaneXToDocX(point.x), mapPaneYToDocY(point.y));
        if(findResponsibility(j) == null)
        {
            MouseDownInfo mousedowninfo = getMouseDownInfo();
            if(mousedowninfo != null && mousedowninfo.tag != null)
            {
                TagItem tagitem = mousedowninfo.tag;
                mousedowninfo.tag = null;
                paintTag(tagitem);
                int k = j >> 12;
                if(k < getMaxIndex())
                {
                    DocLine docline = lines[i];
                    DocItem docitem = doc.items[k];
                    DocStyle docstyle = getStyleAt(k << 12);
                    FloaterInfo floaterinfo = getFloaterInfo(docitem);
                    int l;
                    int i1;
                    if(floaterinfo == null)
                    {
                        l = mapPaneXToDocX(point.x) - findXPos(docline, getStyleAt(docline.start), k << 12);
                        i1 = mapPaneYToDocY(point.y) - docline.y;
                    } else
                    {
                        Point point1 = getFloaterOrigin(this, floaterinfo.item, floaterinfo.isLeft);
                        l = mapPaneXToDocX(point.x) - point1.x;
                        i1 = mapPaneYToDocY(point.y) - floaterinfo.startY;
                    }
                    NamedLink namedlink = tagitem.map(docline, docstyle, mousedowninfo.url, l, i1);
                    if(namedlink == null)
                    {
                        URL url = getDocument().getURL();
                        namedlink = new NamedLink("_top", null, url);
                    }
                    TagItem tagitem1 = inTag(docstyle, "a");
                    DocumentEvent documentevent = null;
                    if(tagitem1 != null && docstyle.href != null)
                    {
                        documentevent = new DocumentEvent(this, 1036, mouseevent.isShiftDown(), docstyle.href);
                        documentevent.setDocItemSource(tagitem1);
                    } else
                    if(docitem != null && docitem.isMappable())
                    {
                        documentevent = new DocumentEvent(this, 1036, mouseevent.isShiftDown(), null);
                        documentevent.setDocItemSource(((DocItem) (namedlink.tag == null ? docitem : ((DocItem) (namedlink.tag)))));
                    }
                    if(documentevent != null)
                    {
                        getTopFormatter().sendDocumentEvent(documentevent);
                        if(documentevent.getEventCancelled())
                            return true;
                    }
                    if(namedlink.url == null)
                    {
                        Properties properties = new Properties();
                        if(mousedowninfo.url == null)
                            return true;
                        properties.put("badlink", mousedowninfo.url);
                        if(doc.getBaseURL() == null)
                        {
                            DocumentFormatter documentformatter1 = getTopFormatter();
                            if(documentformatter1.parent != null)
                            {
                                DocumentPanel documentpanel = (DocumentPanel)documentformatter1.parent.getParent();
                                if(documentpanel != null && documentpanel.setsBaseURL())
                                {
                                    dispatchDocumentEvent(1032, properties);
                                } else
                                {
                                    URL url2 = doc.getBaseURL();
                                    if(url2 != null)
                                        properties.put("baseurl", url2.toExternalForm());
                                    dispatchDocumentEvent(1011, properties);
                                }
                            }
                        }
                        return true;
                    }
                    DocumentFormatter documentformatter = getTopFormatter();
                    if(documentformatter.parent != null)
                    {
                        URL url1 = doc.getBaseURL();
                        boolean flag = false;
                        Date date = doc.getExpirationDate();
                        if(date != null && date.getTime() <= System.currentTimeMillis())
                            flag = true;
                        if(!mouseevent.isShiftDown() && url1 != null && getLinkTarget(tagitem).equals("_self") && sameFileURL(url1, namedlink.url) && !flag)
                        {
                            DocumentEvent documentevent1 = new DocumentEvent(documentformatter.parent.getParent(), 1034, mouseevent.isShiftDown(), namedlink);
                            documentevent1.setDocItemSource(namedlink.tag == null ? ((DocItem) (tagitem)) : ((DocItem) (namedlink.tag)));
                            documentevent1.setClickCount(mouseevent.getClickCount());
                            getTopFormatter().sendDocumentEvent(documentevent1);
                            if(!documentevent1.getEventCancelled())
                            {
                                namedlink = tagitem.map(docline, docstyle, mousedowninfo.url, l, i1);
                                dispatchDocumentEvent(1010, documentformatter.parent.getParent());
                                documentformatter.gotoLabel(namedlink.url.getRef());
                                HotJavaBrowserBean.getURLPoolManager().add(namedlink.url.toExternalForm());
                                dispatchDocumentEvent(1001, null);
                                dispatchDocumentEvent(1005, null);
                            }
                        } else
                        {
                            namedlink.referer = getDocument().getURL();
                            if(namedlink.name == null)
                                namedlink.name = tagitem1 == null || docstyle.href == null ? getLinkTarget(tagitem) : getLinkTarget(tagitem1);
                            DocumentEvent documentevent2 = new DocumentEvent(documentformatter.parent.getParent(), 1034, mouseevent.isShiftDown(), namedlink);
                            documentevent2.setDocItemSource(namedlink.tag == null ? ((DocItem) (tagitem)) : ((DocItem) (namedlink.tag)));
                            documentevent2.setClickCount(mouseevent.getClickCount());
                            getTopFormatter().sendDocumentEvent(documentevent2);
                            if(!documentevent2.getEventCancelled())
                            {
                                NamedLink namedlink1 = tagitem.map(docline, docstyle, mousedowninfo.url, l, i1);
                                namedlink1.referer = getDocument().getURL();
                                if(namedlink1.name == null)
                                    namedlink1.name = tagitem1 == null || docstyle.href == null ? getLinkTarget(tagitem) : getLinkTarget(tagitem1);
                                if(namedlink1.isJS)
                                {
                                    String s = namedlink1.url.getFile();
                                    if(s == null)
                                    {
                                        setMouseDownInfo(null);
                                        return true;
                                    }
                                    if(s.startsWith("/"))
                                        s = s.substring(1);
                                    DocumentPanel documentpanel1 = (DocumentPanel)documentformatter.parent.getParent();
                                    HotJavaBrowserBean hotjavabrowserbean = HotJavaBrowserBean.getContainingHotJavaBrowserBean(documentpanel1);
                                    String s1 = null;
                                    String s2 = namedlink1.url.toExternalForm();
                                    int j1 = s2.lastIndexOf('?');
                                    if(j1 != -1)
                                        s1 = s2.substring(j1);
                                    hotjavabrowserbean.handleJavaScriptURL(s, documentpanel1.getName(), namedlink1.name, documentpanel1, "link.href", s1);
                                    setMouseDownInfo(null);
                                    return true;
                                }
                                dispatchDocumentEvent(documentformatter.parent.getParent(), 1002, mouseevent.isShiftDown(), namedlink1);
                            }
                        }
                        paintTag(tagitem);
                    }
                }
            }
            setMouseDownInfo(null);
            return true;
        } else
        {
            setMouseDownInfo(null);
            return false;
        }
    }

    public static String handleScript(DocumentFormatter documentformatter, String s, String s1, int i, String s2)
    {
        String s3 = null;
        try
        {
            ScriptingEngineInterface scriptingengineinterface = Globals.getScriptingEngine();
            if(scriptingengineinterface != null && documentformatter != null)
            {
                if(!scriptingengineinterface.isLanguageSupported(s2))
                    return null;
                sunw.hotjava.script.ScriptContextInterface scriptcontextinterface = documentformatter.getScriptContext();
                if(scriptcontextinterface == null)
                {
                    scriptcontextinterface = scriptingengineinterface.getContext(documentformatter);
                    documentformatter.setScriptContext(scriptcontextinterface);
                }
                s3 = scriptingengineinterface.evaluateString(scriptcontextinterface, documentformatter.getSecuritySupport(), s, s1, i, s2);
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
            System.out.println("scripting error: " + exception + " " + exception.getMessage());
        }
        return s3;
    }

    protected boolean handleMouseMove(MouseEvent mouseevent)
    {
        Point point = mouseevent.getPoint();
        int i = mapPaneXToDocX(point.x);
        int j = mapPaneYToDocY(point.y);
        int k = findY(j);
        int l = findX(k, i, j);
        ElementInfo elementinfo = new ElementInfo();
        elementinfo.event = mouseevent;
        if(findResponsibility(l) == null)
        {
            DocLine docline = lines[k];
            if(docline == null)
                return true;
            int i1 = docline.margin >> 16;
            DocStyle docstyle = getStyleAt(l);
            if(docstyle.href != null)
                elementinfo.hrefURL = docstyle.href.toString();
            int j1 = l >> 12;
            Object obj = null;
            if(j1 < doc.items.length)
            {
                Object obj1 = doc.items[j1];
                URL url = getImageURL(j1);
                if(url != null)
                    elementinfo.imageURL = url.toString();
                if(obj1 instanceof TagItem)
                {
                    TagItem tagitem1 = (TagItem)obj1;
                    Attributes attributes = tagitem1.getAttributes();
                    if(attributes != null)
                        elementinfo.altText = attributes.get("alt");
                }
                if(obj1 != null && ((DocItem) (obj1)).isMappable())
                {
                    FloaterInfo floaterinfo = getFloaterInfo(((DocItem) (obj1)));
                    int k1;
                    int l1;
                    if(floaterinfo == null)
                    {
                        k1 = mapPaneXToDocX(point.x) - findXPos(docline, docstyle, j1 << 12);
                        l1 = mapPaneYToDocY(point.y) - docline.y;
                    } else
                    {
                        Point point1 = getFloaterOrigin(this, floaterinfo.item, floaterinfo.isLeft);
                        k1 = mapPaneXToDocX(point.x) - point1.x;
                        l1 = mapPaneYToDocY(point.y) - floaterinfo.startY;
                    }
                    String s = doc.getBaseURL() != null ? doc.getBaseURL().toString() : null;
                    NamedLink namedlink = ((DocItem) (obj1)).map(docline, docstyle, s, k1, l1);
                    String s1 = ((DocItem) (obj1)).getMapAltText(docline, docstyle, k1, l1);
                    if(s1 != null)
                        elementinfo.altText = s1;
                    DocumentEvent documentevent1 = null;
                    URL url1 = null;
                    if(namedlink != null)
                    {
                        url1 = namedlink.url;
                        elementinfo.hrefURL = url1.toString();
                        if(namedlink.tag != null)
                            obj1 = namedlink.tag;
                    } else
                    {
                        elementinfo.hrefURL = null;
                    }
                    documentevent1 = new DocumentEvent(this, 1001, mouseevent.isShiftDown(), url1);
                    documentevent1.setDocItemSource(((DocItem) (obj1)));
                    getTopFormatter().sendDocumentEvent(documentevent1);
                    if(documentevent1.getEventCancelled())
                        elementinfo.setStatus = false;
                    dispatchDocumentEvent(1027, elementinfo);
                    return true;
                }
            }
            DocumentEvent documentevent = new DocumentEvent(this, 1001, mouseevent.isShiftDown(), docstyle.href);
            if(docstyle.href != null)
            {
                elementinfo.hrefURL = docstyle.href.toString();
                TagItem tagitem = inTag(docstyle, "a");
                if(tagitem != null)
                    documentevent.setDocItemSource(tagitem);
            }
            getTopFormatter().sendDocumentEvent(documentevent);
            if(documentevent.getEventCancelled())
                elementinfo.setStatus = false;
            dispatchDocumentEvent(1027, elementinfo);
        }
        return true;
    }

    private void unsetHoverInfo(MouseEvent mouseevent)
    {
        mouseevent.getPoint();
        MouseDownInfo mousedowninfo = new MouseDownInfo();
        mousedowninfo.x = mapPaneXToDocX(mouseevent.getX());
        mousedowninfo.y = mapPaneYToDocY(mouseevent.getY());
        int i = findY(mousedowninfo.y);
        mousedowninfo.pos = findX(i, mousedowninfo.x, mousedowninfo.y);
        if(findResponsibility(mousedowninfo.pos) == null || (mouseevent.getSource() instanceof DocumentFormatterPanel) || (mouseevent.getSource() instanceof TableElementPanel))
        {
            int j = mousedowninfo.pos >> 12;
            ElementInfo elementinfo = new ElementInfo();
            elementinfo.frameURL = selectFrame();
            URL url = null;
            URL url1 = null;
            if(j < doc.nitems)
            {
                url = getLinkURL(j, i, mousedowninfo);
                url1 = getImageURL(j);
                DocItem docitem = doc.items[j];
                if(docitem != null && docitem.isMappable())
                {
                    TagItem tagitem = (TagItem)docitem;
                    Attributes attributes = tagitem.getAttributes();
                    elementinfo.altText = attributes.get("alt");
                }
            }
            if(url1 != null)
                elementinfo.imageURL = url1.toString();
            if(url != null)
                elementinfo.hrefURL = url.toString();
            elementinfo.event = mouseevent;
            dispatchDocumentEvent(1027, elementinfo);
        }
    }

    private void setHoverInfo(MouseEvent mouseevent, int i, MouseDownInfo mousedowninfo, String s)
    {
        int j = mousedowninfo.pos >> 12;
        ElementInfo elementinfo = new ElementInfo();
        if(j < doc.nitems)
        {
            URL url = getLinkURL(j, i, mousedowninfo);
            if(url != null)
                elementinfo.hrefURL = url.toString();
            URL url1 = getImageURL(j);
            if(url1 != null)
                elementinfo.imageURL = url1.toString();
            DocItem docitem = doc.items[j];
            if(docitem.isMappable())
            {
                TagItem tagitem = (TagItem)docitem;
                Attributes attributes = tagitem.getAttributes();
                elementinfo.altText = attributes.get("alt");
            }
        }
        elementinfo.frameURL = s;
        elementinfo.event = mouseevent;
        dispatchDocumentEvent(1027, elementinfo);
    }

    private URL getLinkURL(int i, int j, MouseDownInfo mousedowninfo)
    {
        DocItem docitem = doc.items[i];
        DocStyle docstyle = getStyleAt(mousedowninfo.pos);
        if(docitem != null && docitem.isMappable())
        {
            DocLine docline = lines[j];
            FloaterInfo floaterinfo = getFloaterInfo(docitem);
            int k;
            int l;
            if(floaterinfo == null)
            {
                k = mousedowninfo.x - findXPos(docline, docstyle, i << 12);
                l = mousedowninfo.y - docline.y;
            } else
            {
                Point point = getFloaterOrigin(this, floaterinfo.item, floaterinfo.isLeft);
                k = mousedowninfo.x - point.x;
                l = mousedowninfo.y - floaterinfo.startY;
            }
            String s = doc.getBaseURL() != null ? doc.getBaseURL().toString() : null;
            NamedLink namedlink = docitem.map(docline, docstyle, s, k, l);
            if(namedlink != null)
                return namedlink.url;
        } else
        {
            TagItem tagitem = inTag(docstyle, "a");
            if(tagitem != null)
                try
                {
                    return new URL(doc.getBaseURL(), docstyle.href);
                }
                catch(MalformedURLException _ex) { }
        }
        return null;
    }

    private URL getImageURL(int i)
    {
        DocItem docitem = doc.items[i];
        if(docitem != null)
            return docitem.getImageURL();
        else
            return null;
    }

    public String getLinkTarget(TagItem tagitem)
    {
        String s = null;
        s = tagitem.getLinkTarget();
        if(s != null)
            return s;
        s = (String)doc.getProperty("target");
        if(s == null)
            s = "_self";
        return s;
    }

    public DocumentFormatter getTopFormatter()
    {
        return ds.topFormatter;
    }

    public void paintBackground(Graphics g, int i, int j, int k, int l)
    {
        paintBack(g, i, j, k, l);
    }

    protected void paintBack(Graphics g, int i, int j, int k, int l)
    {
        if(ds.bg != null && !isFormatterBackgroundColorDeliberate())
        {
            Point point = new Point(0, 0);
            getBackgroundDisplacement(point);
            ds.bg.paint(g, point.x, point.y, i, j, k, l);
            return;
        } else
        {
            g.setColor(getFormatterBackgroundColor());
            g.fillRect(i, j, k, l);
            return;
        }
    }

    public Color getFormatterBackgroundColor()
    {
        return ds.background;
    }

    protected DocStyle paintLine(Graphics g, DocLine docline, int i, int j, int k, DocStyle docstyle, boolean flag)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        displayPos = docline.start;
        displayStyle = docstyle;
        int l = docline.end;
        int i1;
        if(docline.margin >> 16 < 0)
        {
            DocStyle docstyle1 = getStyleAt(docline.start);
            i1 = getMargin(docstyle1, docline.start, docline.y) >> 16;
        } else
        {
            i1 = (docline.margin >> 16) - getXOriginDelta();
        }
        if(docstyle == null)
            displayStyle = docstyle = getStyleAt(displayPos);
        if(displayPos <= ds.selEnd && l >= ds.selStart)
        {
            int j1 = findXPos(docline, docstyle, ds.selStart);
            if(ds.selStart == ds.selEnd)
            {
                if(ds.showcur)
                {
                    g.setColor(hjbproperties.getColor("hotjava.curcolor", curColor));
                    g.fillRect(j1, i, 1, docline.height);
                }
            } else
            if(ds.showsel && (ds.selecter == this || ds.topFormatter != this))
            {
                int k1 = findXPos(docline, docstyle, ds.selEnd);
                g.setColor(hjbproperties.getColor("hotjava.selcolor", selColor));
                g.fillRect(j1, i, k1 - j1, docline.height);
            }
        }
        while(displayPos < l) 
        {
            DocItem docitem = doc.items[displayPos >> 12];
            int l1 = docitem.getOffset();
            if(l1 > 0)
            {
                if((displayPos & 0xfff) == 0)
                    displayStyle = displayStyle.push(docitem);
                i1 += docitem.paint(this, g, i1, i, docline, flag);
            } else
            if(l1 < 0)
            {
                i1 += docitem.paint(this, g, i1, i, docline, flag);
                displayStyle = displayStyle.next;
            } else
            {
                displayStyle = docitem.modifyStyleInPlace(displayStyle);
                i1 += docitem.paint(this, g, i1, i, docline, flag);
            }
        }
        return displayStyle;
    }

    public boolean paintRangeNoFloaters(Graphics g, int i, int j, boolean flag)
    {
        for(Enumeration enumeration = enumerateResponsiblesInRange(i, j); enumeration.hasMoreElements();)
        {
            Responsibility responsibility = (Responsibility)enumeration.nextElement();
            if(responsibility.startIndex > j >> 12)
                break;
            if(i < responsibility.getStartPos())
            {
                setRepair(getRepair() | paintLocalRangeNoFloaters(g, i, responsibility.getStartPos(), flag));
                i = responsibility.getStartPos();
            }
            if(responsibility.getStartPos() <= i)
            {
                DocPanel docpanel = responsibility.getTarget();
                int k = Math.min(j, responsibility.endIndex << 12);
                ((TablePanel)docpanel).paintRangeNoFloaters(g, i, k, flag);
                i = responsibility.endIndex << 12;
            }
        }

        if(i <= j)
            return paintLocalRangeNoFloaters(g, i, j, flag);
        else
            return false;
    }

    protected boolean paintLocalRangeNoFloaters(Graphics g, int i, int j, boolean flag)
    {
        int k = findPos(i);
        DocStyle docstyle = null;
        boolean flag1 = ds.paintingScreen;
        boolean flag2 = false;
        try
        {
            ds.paintingScreen = true;
            for(; k < nlines; k++)
            {
                DocLine docline = lines[k];
                if(docline.start > j)
                    break;
                if(!docline.updated || flag1)
                {
                    if(docstyle == null)
                        docstyle = getStyleAt(docline.start);
                    int l = docline.width + (docline.margin >> 16);
                    int i1 = docline.start >= i ? 0 : findXPos(docline, docstyle, i);
                    int j1 = docline.end <= j ? l : findXPos(docline, docstyle, j);
                    Graphics g1 = g.create();
                    setHackGraphics(g);
                    try
                    {
                        clipToDrawableArea(g1);
                        if(i1 != 0 || j1 != l)
                            g1.clipRect(i1, getDocumentY(), j1 - i1, height);
                        if(flag)
                            paintBack(g1, 0, docline.y, l, docline.height);
                        docstyle = paintLine(g1, docline, docline.y, 0, l, docstyle, false);
                    }
                    finally
                    {
                        setHackGraphics(null);
                        g1.dispose();
                    }
                } else
                {
                    flag2 = true;
                    setRepair(true);
                    docstyle = null;
                }
            }

            paintFloatersInRange(g, i, j);
        }
        finally
        {
            ds.paintingScreen = flag1;
        }
        return flag2;
    }

    public boolean paintRange(Graphics g, int i, int j, boolean flag)
    {
        for(Enumeration enumeration = enumerateResponsiblesInRange(i, j); enumeration.hasMoreElements();)
        {
            Responsibility responsibility = (Responsibility)enumeration.nextElement();
            if(responsibility.startIndex > j >> 12)
                break;
            if(i < responsibility.getStartPos())
            {
                setRepair(getRepair() | paintLocalRange(g, i, responsibility.getStartPos(), flag));
                i = responsibility.getStartPos();
            }
            if(responsibility.getStartPos() <= i)
            {
                DocPanel docpanel = responsibility.getTarget();
                int k = Math.min(j, responsibility.endIndex << 12);
                ((TablePanel)docpanel).paintRange(g, i, k, flag);
                i = responsibility.endIndex << 12;
            }
        }

        if(i <= j)
            return paintLocalRange(g, i, j, flag);
        else
            return false;
    }

    public int getDocumentY()
    {
        return 0;
    }

    protected boolean paintLocalRange(Graphics g, int i, int j, boolean flag)
    {
        int k = findPos(i);
        DocStyle docstyle = null;
        boolean flag1 = ds.paintingScreen;
        boolean flag2 = false;
        try
        {
            ds.paintingScreen = true;
            for(; k < nlines; k++)
            {
                DocLine docline = lines[k];
                if(docline.start > j)
                    break;
                if(!docline.updated || flag1)
                {
                    if(docstyle == null)
                        docstyle = getStyleAt(docline.start);
                    int l = docline.start >= i ? 0 : findXPos(docline, docstyle, i);
                    int i1 = docline.end <= j ? docline.width + (docline.margin << 16) : findXPos(docline, docstyle, j);
                    Graphics g1 = g.create();
                    setHackGraphics(g);
                    try
                    {
                        clipToDrawableArea(g1);
                        if(l != 0 || i1 != width)
                            g1.clipRect(l, getDocumentY(), i1 - l, height);
                        if(flag)
                            paintBack(g1, 0, docline.y, docline.width + (docline.margin >> 16), docline.height);
                        docstyle = paintLine(g1, docline, docline.y, 0, docline.width + (docline.margin >> 16), docstyle, false);
                    }
                    finally
                    {
                        setHackGraphics(null);
                        g1.dispose();
                    }
                } else
                {
                    flag2 = true;
                    setRepair(true);
                    docstyle = null;
                }
            }

            paintFloaters(g, i, j);
        }
        finally
        {
            ds.paintingScreen = flag1;
        }
        return flag2;
    }

    protected void setHackGraphics(Graphics g)
    {
        hackGraphics = g;
    }

    public Graphics getHackGraphics()
    {
        return hackGraphics;
    }

    protected void clipToDrawableArea(Graphics g)
    {
    }

    protected int formatPrintDocument(int i)
    {
        int j = 0;
        int k = getMaxIndex() << 12;
        int l = getStartIndex() << 12;
        int i1 = i;
        FormatState formatstate = new FormatState();
        FormatState formatstate1 = new FormatState();
        formatstate.isPrinting = true;
        formatstate.pos = -1;
        while(l < k) 
        {
            DocLine docline = formatLine(i1, l, formatstate, formatstate1);
            if(docline == null)
                break;
            docline.y = i1;
            l = docline.end;
            i1 += docline.height;
            docHeight += docline.height;
            if(nlines >= lines.length)
            {
                DocLine adocline[] = new DocLine[(3 * (lines.length + 1)) / 2];
                System.arraycopy(lines, 0, adocline, 0, nlines);
                lines = adocline;
            }
            if(docline.width > j)
                j = docline.width;
            lines[nlines] = docline;
            nlines++;
        }
        return j;
    }

    public int findSplitY(int i, int j, FormatterVBreakInfo formattervbreakinfo)
    {
        int k = 0;
        if(formattervbreakinfo != null)
            k = formattervbreakinfo.getStartLine();
        int l = findFloatersSplitY(i, j, formattervbreakinfo);
        boolean flag = false;
        int i1 = i;
        int k1;
        for(k1 = k; k1 < nlines; k1++)
        {
            DocLine docline = lines[k1];
            int l1 = docline.y + docline.height + 1;
            if(l1 < i)
                continue;
            flag = l1 == i;
            i1 = flag ? l1 : docline.y;
            break;
        }

        if(k1 >= nlines)
            return l;
        if(flag)
        {
            return Math.min(l, i1);
        } else
        {
            DocLine docline1 = lines[k1];
            int j1 = findLineSplitY(docline1, i, j, formattervbreakinfo);
            return Math.min(j1, l);
        }
    }

    int findLineSplitY(DocLine docline, int i, int j, FormatterVBreakInfo formattervbreakinfo)
    {
        int k = docline.start;
        int l = docline.end;
        DocStyle docstyle = getStyleAt(k);
        int i1 = i;
        for(ItemIterator itemiterator = new ItemIterator(k, doc); itemiterator.getPos() < l;)
        {
            DocItem docitem = doc.items[itemiterator.getIndex()];
            int j1 = docitem.getOffset();
            int k1;
            if(j1 > 0)
            {
                if(itemiterator.getOffset() == 0)
                    docstyle = docstyle.push(docitem);
                k1 = docitem.findSplitY(this, itemiterator, docstyle, i, j, docline, formattervbreakinfo);
            } else
            if(j1 < 0)
            {
                k1 = docitem.findSplitY(this, itemiterator, docstyle, i, j, docline, formattervbreakinfo);
                docstyle = docstyle.next;
            } else
            {
                docstyle = docitem.modifyStyleInPlace(docstyle);
                k1 = docitem.findSplitY(this, itemiterator, docstyle, i, j, docline, formattervbreakinfo);
            }
            if(k1 < i1)
                i1 = k1;
        }

        return i1;
    }

    private int findFloatersSplitY(int i, int j, FormatterVBreakInfo formattervbreakinfo)
    {
        if(floaters == null || floaters.size() == 0)
            return i;
        int k = i;
        for(int l = floaters.size(); --l >= 0;)
        {
            FloaterInfo floaterinfo = (FloaterInfo)floaters.elementAt(l);
            if(!floaterinfo.invalid && floaterinfo.inRange(i))
            {
                int i1 = floaterinfo.item.findSplitY(this, floaterinfo, i, j, formattervbreakinfo);
                if(i1 < k)
                    k = i1;
            }
        }

        return k;
    }

    public boolean recordBreakInfo(int i, int j, FormatterVBreakInfo formattervbreakinfo, FormatterVBreakInfo formattervbreakinfo1)
    {
        int k = 0;
        if(formattervbreakinfo != null)
            k = formattervbreakinfo.getStartLine();
        boolean flag = true;
        int l;
        for(l = k; l < nlines; l++)
        {
            DocLine docline = lines[l];
            if(docline.y < i)
                continue;
            flag = docline.y == i;
            break;
        }

        boolean flag1 = l < nlines;
        formattervbreakinfo.setEndLine(l);
        formattervbreakinfo1.setYOffset(i);
        formattervbreakinfo1.setEndLine(nlines);
        boolean flag2 = recordFloaterBreakInfo(i, j, formattervbreakinfo, formattervbreakinfo1);
        boolean flag3 = flag1 | flag2;
        if(flag || l == 0)
        {
            formattervbreakinfo1.setStartLine(l);
            return flag3;
        } else
        {
            l--;
            formattervbreakinfo1.setStartLine(l);
            recordLineBreakInfo(lines[l], i, j, formattervbreakinfo, formattervbreakinfo1);
            return flag3;
        }
    }

    public void recordLineBreakInfo(DocLine docline, int i, int j, FormatterVBreakInfo formattervbreakinfo, FormatterVBreakInfo formattervbreakinfo1)
    {
        int k = docline.start;
        int l = docline.end;
        DocStyle docstyle = getStyleAt(k);
        for(ItemIterator itemiterator = new ItemIterator(k, doc); itemiterator.getPos() < l;)
        {
            DocItem docitem = doc.items[itemiterator.getIndex()];
            int i1 = docitem.getOffset();
            if(i1 > 0)
            {
                if(itemiterator.getOffset() == 0)
                    docstyle = docstyle.push(docitem);
                docitem.recordBreakInfo(this, itemiterator, docstyle, i, j, docline, formattervbreakinfo, formattervbreakinfo1);
            } else
            if(i1 < 0)
            {
                docitem.recordBreakInfo(this, itemiterator, docstyle, i, j, docline, formattervbreakinfo, formattervbreakinfo1);
                docstyle = docstyle.next;
            } else
            {
                docstyle = docitem.modifyStyleInPlace(docstyle);
                docitem.recordBreakInfo(this, itemiterator, docstyle, i, j, docline, formattervbreakinfo, formattervbreakinfo1);
            }
        }

    }

    public boolean recordFloaterBreakInfo(int i, int j, FormatterVBreakInfo formattervbreakinfo, FormatterVBreakInfo formattervbreakinfo1)
    {
        boolean flag = false;
        if(floaters == null)
            return flag;
        int k = formattervbreakinfo.getYOffset();
        for(Enumeration enumeration = floaters.elements(); enumeration.hasMoreElements();)
        {
            FloaterInfo floaterinfo = (FloaterInfo)enumeration.nextElement();
            if(floaterinfo.startY + floaterinfo.height >= k && floaterinfo.startY < i)
            {
                formattervbreakinfo.addFloater(floaterinfo);
                if(floaterinfo.startY + floaterinfo.height > j)
                    flag = true;
                floaterinfo.item.recordBreakInfo(this, floaterinfo, i, j, formattervbreakinfo, formattervbreakinfo1);
            }
        }

        return flag;
    }

    protected void print(Graphics g, FormatterVBreakInfo formattervbreakinfo, int i)
    {
        int j = 0;
        int k = nlines;
        if(formattervbreakinfo != null)
        {
            j = formattervbreakinfo.getStartLine();
            k = formattervbreakinfo.getEndLine();
        }
        if(j < nlines)
        {
            DocStyle docstyle = null;
            for(int l = j; l < k; l++)
            {
                DocLine docline = lines[l];
                docstyle = printLine(g, docline, docline.y, 0, i, docstyle, formattervbreakinfo);
            }

        }
        printBreakFloaters(g, formattervbreakinfo);
    }

    protected DocStyle printLine(Graphics g, DocLine docline, int i, int j, int k, DocStyle docstyle, FormatterVBreakInfo formattervbreakinfo)
    {
        displayPos = docline.start;
        displayStyle = docstyle;
        int l = docline.end;
        int i1 = docline.margin >> 16;
        if(docstyle == null)
            displayStyle = docstyle = getStyleAt(displayPos);
        while(displayPos < l) 
        {
            DocItem docitem = doc.items[displayPos >> 12];
            int j1 = docitem.getOffset();
            VBreakInfo vbreakinfo = null;
            if(formattervbreakinfo != null)
                vbreakinfo = formattervbreakinfo.getItemBreakInfo(docitem);
            if(j1 > 0)
            {
                if((displayPos & 0xfff) == 0)
                    displayStyle = displayStyle.push(docitem);
                i1 += docitem.print(this, g, i1, i, docline, vbreakinfo);
            } else
            if(j1 < 0)
            {
                i1 += docitem.print(this, g, i1, i, docline, vbreakinfo);
                displayStyle = displayStyle.next;
            } else
            {
                displayStyle = docitem.modifyStyleInPlace(displayStyle);
                i1 += docitem.print(this, g, i1, i, docline, vbreakinfo);
            }
        }
        return displayStyle;
    }

    protected void printBreakFloaters(Graphics g, FormatterVBreakInfo formattervbreakinfo)
    {
        if(formattervbreakinfo == null)
            return;
        Enumeration enumeration = formattervbreakinfo.enumerateFloaters();
        if(enumeration == null)
            return;
        FloaterInfo floaterinfo;
        for(; enumeration.hasMoreElements(); printAFloater(g, floaterinfo, formattervbreakinfo.getItemBreakInfo(floaterinfo.item)))
            floaterinfo = (FloaterInfo)enumeration.nextElement();

    }

    private void printAFloater(Graphics g, FloaterInfo floaterinfo, VBreakInfo vbreakinfo)
    {
        Floatable floatable = (Floatable)floaterinfo.item;
        Point point = getFloaterOrigin(this, floaterinfo.item, floaterinfo.isLeft);
        floatable.print(this, g, point.x, floaterinfo.startY, vbreakinfo);
    }

    protected void paintTag(TagItem tagitem)
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(doc)
            {
                int i = tagitem.getIndex();
                if(i < getMaxIndex() && doc.items[i] == tagitem)
                {
                    Graphics g = getGraphics();
                    if(g != null)
                        try
                        {
                            if(tagitem.getOffset() > 0)
                                paintRange(g, i << 12, i + tagitem.getOffset() + 1 << 12, true);
                            else
                                paintRange(g, i << 12, i + 1 << 12, true);
                        }
                        finally
                        {
                            g.dispose();
                        }
                }
            }
        }
    }

    private int mapPaneXToDocX(int i)
    {
        return i + getXOriginDelta();
    }

    private int mapPaneYToDocY(int i)
    {
        return i;
    }

    public boolean isFloater(DocItem docitem)
    {
        if(floaters == null || floaters.size() == 0)
            return false;
        int i = floaters.size();
        for(int j = 0; j < i; j++)
        {
            FloaterInfo floaterinfo = (FloaterInfo)floaters.elementAt(j);
            if(docitem == floaterinfo.item)
                return true;
        }

        return false;
    }

    private FloaterInfo getFloaterInfo(DocItem docitem)
    {
        if(floaters == null || floaters.size() == 0)
            return null;
        int i = floaters.size();
        for(int j = 0; j < i; j++)
        {
            FloaterInfo floaterinfo = (FloaterInfo)floaters.elementAt(j);
            if(docitem == floaterinfo.item)
                return floaterinfo;
        }

        return null;
    }

    public boolean floatersInYRange(int i)
    {
        if(floaters == null)
            return false;
        for(int j = floaters.size(); --j >= 0;)
        {
            FloaterInfo floaterinfo = (FloaterInfo)floaters.elementAt(j);
            if(!floaterinfo.invalid && floaterinfo.inRange(i))
                return true;
        }

        return false;
    }

    private FloaterInfo findFloaterAt(int i, int j)
    {
        if(floaters == null || floaters.size() == 0)
            return null;
        int k = floaters.size();
        for(int l = 0; l < k; l++)
        {
            FloaterInfo floaterinfo = (FloaterInfo)floaters.elementAt(l);
            if(floaterinfo.startY > j)
                break;
            if(floaterinfo.inRange(j))
            {
                Point point = getFloaterOrigin(this, floaterinfo.item, floaterinfo.isLeft);
                if(point.x <= i && i < point.x + floaterinfo.item.getWidth(this, floaterinfo.style))
                    return floaterinfo;
            }
        }

        return null;
    }

    protected int adjustDocHeightForFloaters(int i)
    {
        if(floaters == null || floaters.size() == 0)
            return i;
        int j = floaters.size();
        for(int k = 0; k < j; k++)
        {
            FloaterInfo floaterinfo = (FloaterInfo)floaters.elementAt(k);
            if(floaterinfo.startY + floaterinfo.height > i)
                i = floaterinfo.startY + floaterinfo.height;
        }

        return i;
    }

    protected int getFloatingIndentAt(int i, boolean flag)
    {
        if(floaters == null || floaters.size() == 0)
            return 0;
        int j = 0;
        for(int k = floaters.size(); --k >= 0;)
        {
            FloaterInfo floaterinfo = (FloaterInfo)floaters.elementAt(k);
            if(!floaterinfo.invalid && floaterinfo.isLeft == flag && floaterinfo.inRange(i))
            {
                j += floaterinfo.item.getWidth(this, floaterinfo.style);
                i = floaterinfo.startY;
            }
        }

        return j;
    }

    public Point getFloaterOrigin(Formatter formatter, TagItem tagitem, boolean flag)
    {
        Point point = new Point(0, 0);
        if(formatter.floaters == null)
            return point;
        int i = formatter.floaters.size();
        FloaterInfo floaterinfo = null;
        int j;
        for(j = 0; j < i; j++)
        {
            FloaterInfo floaterinfo1 = (FloaterInfo)formatter.floaters.elementAt(j);
            if(floaterinfo1.item != tagitem)
                continue;
            floaterinfo = floaterinfo1;
            point.y = floaterinfo.startY;
            break;
        }

        int k = point.y;
        int l = 0;
        for(; j >= 0; j--)
        {
            FloaterInfo floaterinfo2 = (FloaterInfo)floaters.elementAt(j);
            if(floaterinfo2.isLeft == flag && floaterinfo2.inRange(k))
            {
                l += floaterinfo2.item.getWidth(this, floaterinfo2.style);
                k = floaterinfo2.startY;
            }
        }

        if(flag)
        {
            l += floaterinfo.style.left;
            l -= floaterinfo.item.getWidth(this, floaterinfo.style);
        } else
        {
            l += floaterinfo.style.right;
            l = width - l;
        }
        point.x = l;
        return point;
    }

    public int getCumulativeFloaterHeight(int i)
    {
        int j = getCumulativeFloaterHeight(i, true);
        return Math.max(j, getCumulativeFloaterHeight(i, false));
    }

    public int getCumulativeFloaterHeight(int i, boolean flag)
    {
        if(floaters == null)
            return 0;
        int j = floaters.size();
        int k = 0;
        int l = 0;
        boolean flag1 = false;
        for(int i1 = 0; i1 < j; i1++)
        {
            FloaterInfo floaterinfo = (FloaterInfo)floaters.elementAt(i1);
            if(floaterinfo.invalid || floaterinfo.isLeft != flag)
                continue;
            if(floaterinfo.startY < k && floaterinfo.startY + floaterinfo.height >= l)
            {
                k = Math.max(k, floaterinfo.startY + floaterinfo.height);
            } else
            {
                if(floaterinfo.startY > i)
                    break;
                l = floaterinfo.startY;
                k = floaterinfo.startY + floaterinfo.height;
            }
            if(floaterinfo.inRange(i))
                flag1 = true;
        }

        if(flag1)
            return k - i;
        else
            return 0;
    }

    public void queueFloater(Formatter formatter, FormatState formatstate, TagItem tagitem, int i, boolean flag)
    {
        if(formatter.formatFloaterQueue == null)
            formatter.formatFloaterQueue = new Vector();
        FloaterInfo floaterinfo = new FloaterInfo();
        floaterinfo.item = tagitem;
        floaterinfo.style = ((TraversalState) (formatstate)).style;
        floaterinfo.isLeft = flag;
        floaterinfo.height = i;
        floaterinfo.invalid = false;
        int j = tagitem.getIndex();
        if(formatstate.width == 0)
        {
            boolean flag1 = insertQueuedFloater(floaterinfo, formatstate.y + formatstate.above);
            int k = tagitem.getWidth(this, ((TraversalState) (formatstate)).style);
            if(flag1)
            {
                if(flag)
                    formatstate.margin += k << 16;
                else
                    formatstate.margin += k;
                formatstate.maxWidth -= k;
                return;
            }
        } else
        {
            formatter.formatFloaterQueue.addElement(floaterinfo);
        }
    }

    protected void insertQueuedFloaters(FormatState formatstate)
    {
        if(formatFloaterQueue != null)
        {
            for(Enumeration enumeration = formatFloaterQueue.elements(); enumeration.hasMoreElements(); insertQueuedFloater((FloaterInfo)enumeration.nextElement(), formatstate.y + formatstate.ascent + formatstate.descent + formatstate.above + formatstate.below));
        }
    }

    private boolean insertQueuedFloater(FloaterInfo floaterinfo, int i)
    {
        boolean flag = true;
        if(floaters == null)
            floaters = new Vector();
        floaterinfo.startY = i;
        int j = i + floaterinfo.height;
        int k = findY(i);
        int l = ++k;
        if(l < nlines)
        {
            int j1 = findY(j);
            if(j1 < nlines)
            {
                for(; l <= j1; l++)
                    lines[l].updated = true;

                invalidateFloatersInRange(lines[k].start, lines[j1].end);
                invalidateFloaterDocLines(j1 + 1, ((DocItem) (floaterinfo.item)).index);
            }
        }
        boolean flag1 = false;
        int k1 = floaters.size();
        for(int i1 = 0; i1 < k1; i1++)
        {
            FloaterInfo floaterinfo1 = (FloaterInfo)floaters.elementAt(i1);
            if(floaterinfo1.item == floaterinfo.item)
            {
                floaterinfo1.startY = i;
                floaterinfo1.height = floaterinfo.height;
                flag1 = true;
                flag = floaterinfo1.invalid;
                if(floaterinfo1.invalid)
                    floaterinfo1.invalid = false;
                break;
            }
            if(floaterinfo1.startY <= i && (floaterinfo1.startY != i || ((DocItem) (floaterinfo1.item)).index <= ((DocItem) (floaterinfo.item)).index))
                continue;
            floaters.insertElementAt(floaterinfo, i1);
            flag1 = true;
            break;
        }

        if(!flag1)
            floaters.addElement(floaterinfo);
        return flag;
    }

    protected void invalidateFloatersInRange(int i, int j)
    {
        if(floaters != null)
        {
            int k = floaters.size();
            for(int l = 0; l < k; l++)
            {
                FloaterInfo floaterinfo = (FloaterInfo)floaters.elementAt(l);
                int i1 = ((DocItem) (floaterinfo.item)).index << 12;
                if(i1 >= i && i1 < j)
                    floaterinfo.invalid = true;
                else
                if(i1 >= j)
                    return;
            }

        }
    }

    private void invalidateFloaterDocLines(int i, int j)
    {
        if(floaters == null)
            return;
        int k = floaters.size();
        for(int l = i; l < nlines; l++)
        {
            for(int i1 = 0; i1 < k; i1++)
            {
                FloaterInfo floaterinfo = (FloaterInfo)floaters.elementAt(i1);
                if(j > ((DocItem) (floaterinfo.item)).index)
                    continue;
                if(floaterinfo.startY >= lines[l].y && l + 1 < nlines && floaterinfo.startY < lines[l + 1].y)
                {
                    lines[l].updated = floaterinfo.invalid = true;
                    continue;
                }
                if(floaterinfo.startY > lines[l].y)
                    break;
            }

        }

    }

    protected void removeFloatersInRange(int i, int j)
    {
        if(floaters != null)
        {
            for(int k = 0; k < floaters.size(); k++)
            {
                FloaterInfo floaterinfo = (FloaterInfo)floaters.elementAt(k);
                int l = ((DocItem) (floaterinfo.item)).index << 12;
                if(l >= i && l < j)
                {
                    floaters.removeElementAt(k);
                    k--;
                } else
                if(l >= j)
                    return;
            }

        }
    }

    protected void paintFloaters(Graphics g, int i, int j, int k, int l)
    {
        if(floaters != null)
        {
            Rectangle rectangle = new Rectangle(i, j, k, l);
            Rectangle rectangle1 = new Rectangle();
            for(Enumeration enumeration = floaters.elements(); enumeration.hasMoreElements();)
            {
                FloaterInfo floaterinfo = (FloaterInfo)enumeration.nextElement();
                rectangle1.y = floaterinfo.startY;
                rectangle1.height = floaterinfo.height;
                Point point = getFloaterOrigin(this, floaterinfo.item, floaterinfo.isLeft);
                rectangle1.x = point.x;
                rectangle1.width = floaterinfo.item.getWidth(this, floaterinfo.style);
                if(intersects(rectangle, rectangle1))
                    paintAFloater(g, floaterinfo);
            }

        }
    }

    private boolean intersects(Rectangle rectangle, Rectangle rectangle1)
    {
        return rectangle1.x + rectangle1.width >= rectangle.x && rectangle1.y + rectangle1.height >= rectangle.y && rectangle1.x <= rectangle.x + rectangle.width && rectangle1.y <= rectangle.y + rectangle.height;
    }

    protected void paintFloatersInRange(Graphics g, int i, int j)
    {
        if(floaters != null)
        {
            int k = i >> 12;
            int l = j >> 12;
            for(Enumeration enumeration = floaters.elements(); enumeration.hasMoreElements();)
            {
                FloaterInfo floaterinfo = (FloaterInfo)enumeration.nextElement();
                if(l <= ((DocItem) (floaterinfo.item)).index)
                    return;
                if(k <= ((DocItem) (floaterinfo.item)).index)
                    paintAFloater(g, floaterinfo);
            }

        }
    }

    protected void paintFloaters(Graphics g, int i, int j)
    {
        if(i > j)
            return;
        int k = findPos(i);
        DocLine docline = lines[k];
        int l = 0;
        int i1 = docHeight;
        if(docline != null)
            l = docline.y;
        k = findPos(j);
        docline = lines[k];
        if(docline != null)
            i1 = docline.y + docline.height;
        paintFloaters(g, 0, l, docline.width + (docline.margin >> 16), i1 - l);
    }

    private void paintAFloater(Graphics g, FloaterInfo floaterinfo)
    {
        if(floaterinfo.invalid)
        {
            return;
        } else
        {
            Floatable floatable = (Floatable)floaterinfo.item;
            Point point = getFloaterOrigin(this, floaterinfo.item, floaterinfo.isLeft);
            floatable.paint(this, g, point.x, point.y);
            return;
        }
    }

    protected int highestSplitFloaterY(int i)
    {
        int j = -1;
        if(floaters != null)
        {
            for(int k = 0; k < floaters.size(); k++)
            {
                FloaterInfo floaterinfo = (FloaterInfo)floaters.elementAt(k);
                if(floaterinfo.startY < i && floaterinfo.startY + floaterinfo.height > i && (j == -1 || j > floaterinfo.startY))
                    j = floaterinfo.startY;
            }

        }
        return j;
    }

    protected int getFloatersStartY(int i)
    {
        int j = 0;
        if(floaters == null || floaters.size() == 0)
            return i;
        for(int k = floaters.size(); --k >= 0;)
        {
            FloaterInfo floaterinfo = (FloaterInfo)floaters.elementAt(k);
            if(!floaterinfo.invalid && floaterinfo.inRange(i) && j < floaterinfo.startY)
                j = floaterinfo.startY;
        }

        if(j == 0)
            return i;
        else
            return j;
    }

    protected void updateFloatersYInRange(int i, int j, int k)
    {
        if(floaters != null)
        {
            int l = floaters.size();
            for(int i1 = 0; i1 < l; i1++)
            {
                FloaterInfo floaterinfo = (FloaterInfo)floaters.elementAt(i1);
                int j1 = ((DocItem) (floaterinfo.item)).index << 12;
                if(j1 >= i && j1 < j)
                    floaterinfo.startY += k;
                else
                if(j1 >= j)
                    return;
            }

        }
    }

    protected void updateFloatersY(int i)
    {
        if(floaters != null)
        {
            int j = floaters.size();
            for(int k = 0; k < j; k++)
                ((FloaterInfo)floaters.elementAt(k)).startY += i;

        }
    }

    protected void dirtyFloaterLines(FormatState formatstate, int i, int j)
    {
        Range range = new Range(0x7fffffff, 0);
        getFloaterYRange(formatstate, floaters, i, j, range);
        for(int k = findY(range.minVal); lines[k] != null && lines[k].y < range.maxVal; k++)
        {
            lines[k].updated = true;
            invalidateFloatersInRange(lines[k].start, lines[k].end);
        }

    }

    private void getFloaterYRange(FormatState formatstate, Vector vector, int i, int j, Range range)
    {
        if(vector == null)
            return;
        for(Enumeration enumeration = vector.elements(); enumeration.hasMoreElements();)
        {
            FloaterInfo floaterinfo = (FloaterInfo)enumeration.nextElement();
            TagItem tagitem = floaterinfo.item;
            int k = ((DocItem) (tagitem)).index << 12;
            if(k >= j)
                return;
            if(k >= i)
            {
                floaterinfo.height = tagitem.getAscent(this, formatstate) + tagitem.getDescent(this, formatstate);
                range.minVal = Math.min(floaterinfo.startY, range.minVal);
                range.maxVal = Math.max(floaterinfo.startY + floaterinfo.height, range.maxVal);
            }
        }

    }

    protected void repaintAffectedFloaters(int i, int j)
    {
        if(floaters == null)
            return;
        int k = floaters.size();
        for(int l = 0; l < k; l++)
        {
            FloaterInfo floaterinfo = (FloaterInfo)floaters.elementAt(l);
            if(!floaterinfo.invalid)
            {
                int i1 = ((DocItem) (floaterinfo.item)).index << 12;
                if(i1 >= j)
                    return;
                if(i1 >= i)
                {
                    int j1 = floaterinfo.startY;
                    int k1 = floaterinfo.startY + floaterinfo.height;
                    Graphics g = getGraphics();
                    if(g != null)
                        try
                        {
                            for(int l1 = l; l1 < k; l1++)
                            {
                                FloaterInfo floaterinfo1 = (FloaterInfo)floaters.elementAt(l1);
                                if(floaterinfo1.isLeft != floaterinfo.isLeft)
                                    continue;
                                if(k1 < floaterinfo1.startY || j1 > floaterinfo1.startY + floaterinfo1.height)
                                    break;
                                paintAFloater(g, floaterinfo1);
                                j1 = Math.min(j1, floaterinfo1.startY);
                                k1 = Math.max(k1, floaterinfo1.startY + floaterinfo1.height);
                            }

                        }
                        finally
                        {
                            g.dispose();
                        }
                }
            }
        }

    }

    public void claimResponsibilityFor(DocPanel docpanel, int i, int j)
    {
        if(respVec == null)
            respVec = new Vector();
        Responsibility responsibility = new Responsibility(docpanel, i, j);
        int k = respVec.size();
        for(int l = 0; l < k; l++)
        {
            Responsibility responsibility1 = (Responsibility)respVec.elementAt(l);
            if(responsibility1.startIndex > j)
            {
                respVec.insertElementAt(responsibility, l);
                return;
            }
        }

        respVec.addElement(responsibility);
    }

    public void disclaimResponsibilityFor(DocPanel docpanel, int i, int j)
    {
        if(respVec == null)
            return;
        for(int k = 0; k < respVec.size(); k++)
        {
            Responsibility responsibility = (Responsibility)respVec.elementAt(k);
            if(responsibility.startIndex >= i && responsibility.endIndex <= j)
            {
                respVec.removeElementAt(k);
                k--;
            }
        }

    }

    protected void notifyResponsiblesInRange(Document document, int i, int j, int k)
    {
        DocPanel docpanel;
        for(Enumeration enumeration = subsequentResponsiblesFrom(j); enumeration.hasMoreElements(); docpanel.notify(document, i, j, k))
        {
            Responsibility responsibility = (Responsibility)enumeration.nextElement();
            docpanel = responsibility.getTarget();
        }

    }

    protected Responsibility findResponsibility(int i)
    {
        if(respVec == null)
            return null;
        int j = getResponsibilityIndex(i);
        if(j < respVec.size())
            return (Responsibility)respVec.elementAt(j);
        else
            return null;
    }

    private int getResponsibilityIndex(int i)
    {
        if(respVec == null)
            return 0;
        int j = respVec.size();
        int k = i >> 12;
        int l = 0;
        for(l = 0; l < j; l++)
        {
            Responsibility responsibility = (Responsibility)respVec.elementAt(l);
            if(responsibility.startIndex <= k && k < responsibility.endIndex)
                break;
        }

        return l;
    }

    private Enumeration enumerateResponsiblesInRange(int i, int j)
    {
        if(respVec == null)
            return new ResponsibilityRangeEnumeration(this, 0, -1);
        int k = -1;
        int l = -1;
        int i1 = respVec.size();
        int j1 = i >> 12;
        int k1 = j >> 12;
        boolean flag = false;
        for(int l1 = 0; l1 < i1; l1++)
        {
            Responsibility responsibility = (Responsibility)respVec.elementAt(l1);
            if(responsibility.startIndex <= k1 && responsibility.endIndex >= j1)
            {
                if(k < 0)
                    k = l1;
                l = l1;
                continue;
            }
            if(responsibility.startIndex > k1)
                break;
        }

        return new ResponsibilityRangeEnumeration(this, k, l);
    }

    private Enumeration subsequentResponsiblesFrom(int i)
    {
        int j = getResponsibilityIndex(i);
        return new ResponsibilityEnumeration(this, j);
    }

    int countResponsiblities()
    {
        if(respVec == null)
            return 0;
        else
            return respVec.size();
    }

    Responsibility responsibilityAt(int i)
    {
        if(respVec == null)
            return null;
        else
            return (Responsibility)respVec.elementAt(i);
    }

    public void setDocFont(DocFont docfont)
    {
        synchronized(doc)
        {
            if(docfont != ds.docStyle.font)
            {
                Vector vector = getAllChildFormatters();
                for(int i = 0; i < vector.size(); i++)
                {
                    Formatter formatter = (Formatter)vector.elementAt(i);
                    formatter.setDocFont(docfont);
                }

            }
            ds.docStyle.font = docfont;
            reformat();
        }
    }

    protected void reformatResponsibles()
    {
        if(respVec == null)
            return;
        int i = respVec.size();
        for(int j = 0; j < i; j++)
        {
            Responsibility responsibility = (Responsibility)respVec.elementAt(j);
            DocPanel docpanel = responsibility.getTarget();
            docpanel.reformat();
        }

    }

    void getFrameSetFormatters(FrameSetPanel framesetpanel, Vector vector)
    {
        Component acomponent[] = framesetpanel.getFrameList();
        for(int i = 0; i < acomponent.length; i++)
        {
            Component component = acomponent[i];
            if(component instanceof FramePanel)
                vector.addElement(((FramePanel)component).getFormatter());
            else
                getFrameSetFormatters((FrameSetPanel)component, vector);
        }

    }

    protected Vector getAllChildFormatters()
    {
        Vector vector = getChildFormatters();
        if(vector == null)
            vector = new Vector();
        FrameSetPanel framesetpanel = getFrameSetPanel();
        if(framesetpanel != null)
            getFrameSetFormatters(framesetpanel, vector);
        return vector;
    }

    public Vector getChildFormatters()
    {
        if(respVec == null)
            return null;
        Vector vector = new Vector();
        int i = respVec.size();
        for(int j = 0; j < i; j++)
        {
            Responsibility responsibility = (Responsibility)respVec.elementAt(j);
            DocPanel docpanel = responsibility.getTarget();
            if(docpanel instanceof TablePanel)
                ((TablePanel)docpanel).addTablePanelFormatters(vector);
        }

        return vector;
    }

    public void clearParentBackground()
    {
        Container container = getParent();
        if(container != null)
        {
            Graphics g = container.getGraphics();
            Dimension dimension = container.getSize();
            if(g != null)
            {
                g.setColor(Color.white);
                g.fillRect(0, 0, dimension.width, dimension.height);
                g.dispose();
            }
        }
    }

    public void addBlinker(TextItem textitem)
    {
    }

    public void setObsolete(boolean flag)
    {
        int i = Math.min(nItemComponents, itemComponents.length);
        for(int j = 0; j < i; j++)
        {
            Component component = itemComponents[j].getComponent();
            if(component != null)
                ((DocPanel)component).setObsolete(flag);
        }

    }

    public void updateDocument()
    {
        FrameSetPanel framesetpanel = getFrameSetPanel();
        if(framesetpanel != null)
            framesetpanel.updateDocument();
    }

    public Formatter()
    {
        ds = new DocumentState();
        listeners = new DocumentEventMulticaster();
        marginHeight = 10;
        hasFrameSetPanel = false;
        allFrameSetPanels = new FrameSetPanel[6];
        clickCancelled = false;
    }

    public abstract void activateSubItems();

    public abstract void notify(Document document, int i, int j, int k);

    public abstract boolean imageUpdate(Image image, int i, int j, int k, int l, int i1);

    public int width;
    public int height;
    static final int FINISHED = 128;
    protected Document doc;
    protected DocumentState ds;
    private DocumentEventMulticaster listeners;
    protected int docWidth;
    protected int docHeight;
    protected int XOriginDelta;
    public int displayPos;
    public DocStyle displayStyle;
    protected int marginHeight;
    protected DocLine lines[];
    protected int nlines;
    protected int nItemComponents;
    protected ItemComponent itemComponents[];
    protected Vector itemsNeedingDeactivation;
    private boolean hasFrameSetPanel;
    private Vector respVec;
    protected Vector floaters;
    public Vector formatFloaterQueue;
    private BitSet panelVisibility;
    private Graphics hackGraphics;
    private Vector activationQueue;
    private IMG lastImage;
    private static final Color curColor = new Color(255, 0, 0);
    private static final Color selColor = new Color(160, 160, 160);
    private FrameSetPanel allFrameSetPanels[];
    private int numberOfFrameSetPanels;
    private boolean clickCancelled;


}
