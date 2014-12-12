// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Document.java

package sunw.hotjava.doc;

import java.awt.Color;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import sun.awt.ScreenUpdater;
import sun.awt.UpdateClient;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.bean.ImageCacher;
import sunw.hotjava.forms.FORM;
import sunw.hotjava.misc.*;
import sunw.hotjava.tags.*;
import sunw.html.*;

// Referenced classes of package sunw.hotjava.doc:
//            BasefontTagRecord, DocBusyException, DocConstants, DocError, 
//            DocException, DocItem, DocItemEnumeration, DocNotEditableException, 
//            DocParser, DocStyle, DocView, DocumentCache, 
//            DocumentEvent, DocumentEventMulticaster, DocumentEventSource, DocumentFormatter, 
//            EmptyTagItem, EndTagItem, FlowTagItem, OverlappingStringMatch, 
//            Range, SizeItem, StyleSheet, TagItem, 
//            TextItem, DocumentListener

public final class Document
    implements DocConstants, PropertyChangeListener, DocumentEventSource, UpdateClient
{

    public Document()
    {
        this(Globals.getDefaultDTD(), null, null, null, null);
    }

    public Document(String s)
    {
        this(Globals.getDefaultDTD(), null, s, null, null);
    }

    public Document(String s, String s1)
    {
        this(Globals.getDefaultDTD(), null, s, s1, null);
    }

    public Document(URL url)
    {
        this(Globals.getDefaultDTD(), url, null, null, null);
    }

    public Document(URL url, URL url1)
    {
        this(Globals.getDefaultDTD(), url, null, null, url1);
    }

    public Document(URL url, String s)
    {
        this(Globals.getDefaultDTD(), url, s, null, null);
    }

    public Document(DTD dtd, URL url, String s)
    {
        this(Globals.getDefaultDTD(), url, s, null, null);
    }

    public Document(DTD dtd, URL url, String s, String s1, URL url1)
    {
        reloaded = false;
        props = new Hashtable(11);
        ncompleted = 0x7ffff;
        items = new DocItem[10];
        endPtr = items.length;
        listeners = new DocumentEventMulticaster();
        views = new DocView[4];
        parsingCompleted = false;
        changes = new PropertyChangeSupport(this);
        vchanges = new VetoableChangeSupport(this);
        bIsOkToFormat = false;
        framed = false;
        timeOutStack = new Stack();
        cw = new CharArrayWriter();
        windowHandlerExists = false;
        hasScriptWrites = false;
        basefontTagRecord = new BasefontTagRecord(this);
        try
        {
            setProperty("dtd", dtd);
            if(url != null)
                setProperty("url", url);
            if(s != null)
                setProperty("title", s);
            if(s1 != null)
                setProperty("doctype", s1);
            if(url1 != null)
                setProperty("referer", url1);
        }
        catch(DocException _ex) { }
        sizeItemListWidth = new Hashtable();
        sizeItemListHeight = new Hashtable();
        bIsOkToFormat = false;
        setDocumentTitle(s);
    }

    public int hashCode()
    {
        Object obj = getProperty("url");
        if(obj != null)
            return obj.hashCode();
        else
            return super.hashCode();
    }

    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(obj != null && (obj instanceof Document))
        {
            Object obj1 = ((Document)obj).getProperty("url");
            if(obj1 != null && obj1.equals(getProperty("url")))
                return true;
        }
        return false;
    }

    public boolean wasReloaded()
    {
        return reloaded;
    }

    public void setReloaded(boolean flag)
    {
        if(flag)
            reloaded = true;
    }

    public void setClientPullSpec(String s)
    {
        clientPullSpec = s;
    }

    public String getClientPullSpec()
    {
        return clientPullSpec;
    }

    public void addDocumentListener(DocumentListener documentlistener)
    {
        listeners.add(documentlistener);
    }

    public void removeDocumentListener(DocumentListener documentlistener)
    {
        listeners.remove(documentlistener);
    }

    public int getState()
    {
        return state;
    }

    public synchronized void setState(int i)
        throws DocBusyException
    {
        if(i >= 10 && owner != null && owner != Thread.currentThread())
            throw new DocBusyException(this, "document busy");
        if(state != i)
        {
            state = i;
            dispatchDocumentEvent(1005, new Integer(i));
        }
    }

    public DocView[] getViews()
    {
        return views;
    }

    public DocView getView()
    {
        DocView docview = null;
        for(int i = 0; i < views.length; i++)
            if(views[i] != null && (views[i] instanceof DocumentFormatter))
                docview = views[i];

        return docview;
    }

    private boolean needsActivation(int i)
    {
        if(!items[i].needsActivation())
            return false;
        while(i-- > 0) 
        {
            DocItem docitem = items[i];
            int j = docitem.offset;
            if(j > 0)
            {
                if(docitem.needsActivation())
                    return false;
            } else
            if(j < 0)
                i += j;
        }
        return true;
    }

    public void setCompleted(int i)
        throws DocBusyException
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                if(owner == null || owner != Thread.currentThread())
                    throw new DocBusyException(this, "not owner");
                if(i == 0x7fffffff && getTagName(items[0]).equals("plaintext"))
                {
                    int j;
                    for(j = 0; j < items.length && items[j] != null; j++);
                    endTagComplete(items[0], j << 12);
                }
                int k = i >> 12;
                if(ncompleted < k)
                {
                    int l = Math.min(nitems, k);
                    for(int k1 = ncompleted; k1 < l; k1++)
                    {
                        int i2 = items[k1].offset;
                        if(i2 <= 0 && needsActivation(k1 + i2))
                            notifyViews(20, k1 + i2 << 12, 1 - i2 << 12);
                    }

                } else
                {
                    int i1 = Math.min(nitems, ncompleted);
                    for(int l1 = k; l1 < i1; l1++)
                    {
                        int j2 = items[l1].offset;
                        if(j2 <= 0 && needsActivation(l1 + j2))
                            notifyViews(21, l1 + j2 << 12, 1 - j2 << 12);
                    }

                }
                int j1 = ncompleted;
                ncompleted = k;
                if(i == 0x7fffffff)
                {
                    nitems = j1;
                    DocItem adocitem[] = new DocItem[nitems];
                    System.arraycopy(items, 0, adocitem, 0, nitems);
                    items = adocitem;
                    endPtr = nitems;
                    if(wasReloaded())
                        DocumentCache.putDocument(this);
                    notifyViews(22, 0, 0x7fffffff);
                    parsingCompleted = true;
                    dispatchFormatScreen();
                }
            }
        }
    }

    public void setParsingCompleted(boolean flag)
    {
        parsingCompleted = flag;
    }

    public boolean doneParsing()
    {
        return parsingCompleted;
    }

    public String getPragma()
    {
        return pragma;
    }

    public void setPragma(String s)
    {
        pragma = s;
    }

    public Object getProperty(String s)
    {
        return props.get(s);
    }

    public Object getProperty(String s, Object obj)
    {
        Object obj1 = getProperty(s);
        if(obj1 != null)
            return obj1;
        else
            return obj;
    }

    public DTD getDTD()
    {
        return (DTD)getProperty("dtd");
    }

    public URL getURL()
    {
        return (URL)getProperty("url");
    }

    public URL getReferer()
    {
        return (URL)getProperty("referer");
    }

    public URL getBaseURL()
    {
        URL url = (URL)getProperty("base");
        if(url != null)
            return url;
        else
            return getURL();
    }

    public String getTitle()
    {
        return (String)getProperty("title");
    }

    public String getDoctype()
    {
        return (String)getProperty("doctype");
    }

    public String getTitleString()
    {
        String s = getTitle();
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        if(s == null || s.equals(""))
        {
            s = hjbproperties.getProperty("hotjava.untitled");
            URL url = getURL();
            if(url != null)
                s = s + " (" + url + ")";
        }
        return s;
    }

    public Document getDocThisIsSourceOf()
    {
        return (Document)getProperty("docThisIsSourceOf");
    }

    public Date getExpirationDate()
    {
        return (Date)getProperty("expiration-date");
    }

    public void setExpirationDate(Date date)
    {
        try
        {
            setProperty("expiration-date", date);
            return;
        }
        catch(DocException _ex)
        {
            return;
        }
    }

    public Date getLastModifiedDate()
    {
        return (Date)getProperty("last-modified");
    }

    public void setLastModifiedDate(Date date)
    {
        try
        {
            setProperty("last-modified", date);
            return;
        }
        catch(DocException _ex)
        {
            return;
        }
    }

    public void setProperty(String s, Object obj)
        throws DocException
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                if(changeNotVetoed(s, obj))
                {
                    modify();
                    if(obj == null)
                        props.remove(s);
                    else
                        props.put(s, obj);
                    dispatchDocumentEvent(1004, s);
                }
            }
        }
    }

    public Thread getOwner()
    {
        return owner;
    }

    public int setOwner(int i, Thread thread)
        throws DocBusyException
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                int k = state;
                if(owner != null && owner != Thread.currentThread())
                    throw new DocBusyException(this, "not owner");
                owner = thread;
                if(state != i)
                {
                    state = i;
                    dispatchDocumentEvent(1005, new Integer(i));
                }
                int j = k;
                return j;
            }
        }
    }

    public int setOwner(int i, Thread thread, Parser parser)
        throws DocBusyException
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                int k = setOwner(i, thread);
                currentParser = parser;
                if(parser instanceof DocParser)
                    ((DocParser)parser).addPropertyChangeListener(this);
                int j = k;
                return j;
            }
        }
    }

    public Parser getParser()
    {
        return currentParser;
    }

    public BasefontTagRecord getBasefontTagRecord()
    {
        return basefontTagRecord;
    }

    public int basefontSizeAt(DocStyle docstyle, int i)
    {
        return basefontTagRecord.basefontSizeAt(docstyle, i);
    }

    public int length()
    {
        return nitems << 12;
    }

    public synchronized void interruptOwner()
    {
        if(owner != null)
            owner.interrupt();
    }

    public void interruptOwnerWaitCompletion()
    {
        if(owner == null)
            return;
        Thread thread;
        synchronized(owner)
        {
            thread = owner;
            owner.interrupt();
        }
        try
        {
            thread.setPriority(2);
        }
        catch(NullPointerException _ex) { }
        Properties properties = new Properties();
        properties.put("baseurl", getBaseURL().toExternalForm());
        dispatchDocumentEvent(1012, properties);
        if(state != 11)
        {
            try
            {
                thread.join();
                return;
            }
            catch(InterruptedException interruptedexception)
            {
                interruptedexception.printStackTrace();
            }
            return;
        } else
        {
            state = 15;
            return;
        }
    }

    private void dispatchDocumentEvent(int i, Object obj)
    {
        DocumentEvent documentevent = new DocumentEvent(this, i, obj);
        documentevent.setDocument(this);
        listeners.documentChanged(documentevent);
    }

    public void notifyViews(int i, int j, int k)
    {
        for(int l = 0; l < nviews; l++)
            views[l].notify(this, i, j, k);

    }

    public int countViews()
    {
        return nviews;
    }

    public void addView(DocView docview)
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                if(nviews >= views.length)
                {
                    DocView adocview[] = new DocView[nviews + 8];
                    System.arraycopy(views, 0, adocview, 0, nviews);
                    views = adocview;
                }
                views[nviews++] = docview;
                docview.notify(this, 15, 0, nitems << 12);
                for(int i = 0; i < nitems; i++)
                {
                    DocItem docitem = items[i];
                    if(docitem != null)
                    {
                        int j = docitem.offset;
                        if(j >= 0 && i + j < ncompleted && needsActivation(i))
                            docview.notify(this, 20, i << 12, j + 1 << 12);
                    }
                }

            }
        }
    }

    public void removeView(DocView docview)
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                for(int i = 0; i < nviews; i++)
                {
                    if(views[i] != docview)
                        continue;
                    System.arraycopy(views, i + 1, views, i, nviews - (i + 1));
                    nviews--;
                    for(i = 0; i < nitems; i++)
                    {
                        DocItem docitem = items[i];
                        if(docitem != null)
                        {
                            int j = docitem.offset;
                            if(j >= 0 && i + j < ncompleted && needsActivation(i))
                                docview.notify(this, 21, i << 12, j + 1 << 12);
                        }
                    }

                    docview.notify(this, 16, 0, nitems << 12);
                    break;
                }

                views[nviews] = null;
            }
        }
    }

    TagItem makeTag(String s)
    {
        return makeTag(getDTD().getElement(s), null);
    }

    TagItem makeTag(Element element, Attributes attributes)
    {
        Object obj;
        try
        {
            Class class1 = (Class)tagClassCache.get(element);
            if(class1 == null)
            {
                HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
                String s = hjbproperties.getProperty(element.getName() + ".class");
                if(s == null)
                    s = "sunw.hotjava.tags." + element.getName().toUpperCase();
                class1 = Class.forName(s);
                tagClassCache.put(element, class1);
            }
            obj = (TagItem)class1.newInstance();
        }
        catch(Exception _ex)
        {
            if(element.isEmpty())
                obj = new EmptyTagItem();
            else
                obj = new FlowTagItem();
        }
        ((TagItem)obj).style = StyleSheet.getStyleSheet(element);
        ((TagItem)obj).atts = attributes;
        try
        {
            ((TagItem) (obj)).init(this);
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return ((TagItem) (obj));
    }

    synchronized void endTagComplete(DocItem docitem, int i)
    {
        int j = i >> 12;
        if(endPtr != j)
        {
            int k = docitem.index + docitem.offset;
            int l = j - docitem.index;
            docitem.offset = l;
            DocItem docitem1 = items[k];
            items[j] = docitem1;
            docitem1.offset = -l;
            docitem1.setIndex(j, this);
            items[endPtr] = null;
        }
        endPtr++;
    }

    private void modify()
        throws DocBusyException, DocNotEditableException
    {
        switch(state)
        {
        case 0: // '\0'
            setState(2);
            return;

        case 2: // '\002'
        case 4: // '\004'
            return;
        }
        if(state >= 10)
        {
            if(owner != Thread.currentThread())
                throw new DocBusyException(this, "document busy");
            else
                return;
        } else
        {
            throw new DocNotEditableException(this, "document not editable: " + state);
        }
    }

    private void insertItems(int i, int j)
    {
        if(i + j > endPtr)
        {
            int k = Math.max(nitems + j, items.length * 2);
            DocItem adocitem[] = new DocItem[k];
            int l = k - items.length;
            System.arraycopy(items, 0, adocitem, 0, i);
            int i1 = endPtr + l;
            System.arraycopy(items, endPtr, adocitem, i1, items.length - endPtr);
            for(int j1 = i1; j1 < k; j1++)
            {
                DocItem docitem = adocitem[j1];
                int k1 = docitem.offset - l;
                docitem.offset = k1;
                docitem.setIndex(j1, this);
                adocitem[j1 + k1].offset = -k1;
            }

            items = adocitem;
            endPtr = i1;
        }
        nitems += j;
    }

    private int deleteItems(int i, int j)
    {
        int k = i;
        int i1;
        for(int l = i; l < j; l += i1 + 1)
        {
            i1 = items[l].offset;
            if(l + i1 < j && i1 >= 0)
                continue;
            k = deleteItems(l + 1, j);
            j = l;
            break;
        }

        if(i < j)
        {
            for(int j1 = i; j1 < j; j1++)
            {
                DocItem docitem = items[j1];
                int l1 = docitem.offset;
                if(l1 >= 0 && j1 + l1 < ncompleted && needsActivation(j1))
                    notifyViews(21, j1 << 12, l1 + 1 << 12);
            }

            int k1 = j - i;
            System.arraycopy(items, j, items, i, nitems - j);
            nitems -= k1;
            basefontTagRecord.notifyDocItemsDeleted(i, k1);
            for(int i2 = 0; i2 < k1; i2++)
                items[nitems + i2] = null;

            if(nitems < items.length >> 1)
            {
                DocItem adocitem[] = new DocItem[items.length >> 1];
                System.arraycopy(items, 0, adocitem, 0, nitems);
                items = adocitem;
            }
            for(int j2 = i - 1; j2 >= 0; j2--)
            {
                int k2 = items[j2].offset;
                if(k2 > 0)
                {
                    k2 -= k1;
                    items[j2].offset = k2;
                    items[j2 + k2].offset = -k2;
                } else
                {
                    j2 += k2;
                }
            }

            for(int l2 = i; l2 < nitems; l2++)
                items[l2].setIndex(items[l2].index - k1, this);

            notifyViews(16, i << 12, j - i << 12);
        }
        return k;
    }

    private int splitItem(int i)
    {
        if(i < 0)
            return 0;
        if(i > nitems << 12)
            return nitems;
        int j = i >> 12;
        if((i & 0xfff) == 0)
            return j;
        DocItem docitem = items[j++].split(this, i);
        if(docitem != null)
        {
            insertItems(j, 1);
            docitem.setIndex(j, this);
            items[j] = docitem;
            notifyViews(10, i, 0);
        }
        return j;
    }

    public int insertTag(int i, String s, Attributes attributes)
        throws DocException
    {
        return insertTag(i, getDTD().getElement(s), attributes);
    }

    public int insertTag(int i, Element element, Attributes attributes)
        throws DocException
    {
        return insertTag(i, makeTag(element, attributes));
    }

    public int insertTag(int i, TagItem tagitem)
        throws DocException
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                modify();
                int k = splitItem(i);
                insertItems(k, 1);
                tagitem.offset = 0;
                tagitem.setIndex(k, this);
                items[k] = tagitem;
                notifyViews(15, k << 12, 4096);
                if(k < ncompleted && needsActivation(k))
                    notifyViews(20, k << 12, 4096);
                int j = k + 1 << 12;
                return j;
            }
        }
    }

    public int insertTagPair(int i, String s, Attributes attributes)
        throws DocException
    {
        return insertTagPair(i, getDTD().getElement(s), attributes);
    }

    public int insertTagPair(int i, Element element, Attributes attributes)
        throws DocException
    {
        return insertTagPair(i, makeTag(element, attributes));
    }

    public int insertTagPair(int i, TagItem tagitem)
        throws DocException
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                modify();
                EndTagItem endtagitem = new EndTagItem();
                int k = splitItem(i);
                insertItems(k, 2);
                endPtr--;
                tagitem.offset = endPtr - k;
                tagitem.setIndex(k, this);
                items[k] = tagitem;
                endtagitem.offset = -((DocItem) (tagitem)).offset;
                endtagitem.setIndex(endPtr, this);
                items[endPtr] = endtagitem;
                notifyViews(15, k << 12, 8192);
                if(k + 1 < ncompleted && needsActivation(k))
                    notifyViews(20, k << 12, 8192);
                int j = k + 1 << 12;
                return j;
            }
        }
    }

    public int insert(int i, String s)
        throws DocException
    {
        char ac[] = new char[s.length()];
        s.getChars(0, ac.length, ac, 0);
        return insert(i, ac, 0, ac.length);
    }

    public int insert(int i, int j)
        throws DocException
    {
        char ac[] = {
            (char)(j & 0xffff)
        };
        return insert(i, ac, 0, 1);
    }

    public int insert(int i, char ac[], int j, int k)
        throws DocException
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                modify();
                if(i < 0)
                    i = 0;
                else
                if(i > nitems << 12)
                    i = nitems << 12;
                int k1 = i >> 12;
                int l1 = k1;
                if(l1 >= 0 && l1 < nitems && items[l1] != null && items[l1].isText())
                {
                    int i2 = items[l1].insert(this, i, ac, j, k);
                    if(i2 != -1)
                    {
                        notifyViews(12, i2 - k, k);
                        int l = i2;
                        return l;
                    }
                }
                if(l1 > 0 && (i & 0xfff) == 0 && items[l1 - 1].isText())
                {
                    int j2 = items[l1 - 1].insert(this, i - 1, ac, j, k);
                    if(j2 != -1)
                    {
                        notifyViews(12, j2 - k, k);
                        int i1 = j2;
                        return i1;
                    }
                }
                l1 = splitItem(i);
                int k2 = 0;
                int l2 = j;
                int i3;
                for(i3 = k - l2; i3 > 4095;)
                {
                    insertItems(l1, 1);
                    TextItem textitem = new TextItem(ac, l2, 4095);
                    k2++;
                    textitem.offset = 0;
                    textitem.setIndex(l1, this);
                    items[l1] = textitem;
                    l2 += 4095;
                    i3 -= 4095;
                    l1++;
                }

                insertItems(l1, 1);
                TextItem textitem1 = new TextItem(ac, l2, i3);
                k2++;
                textitem1.offset = 0;
                textitem1.setIndex(l1, this);
                items[l1] = textitem1;
                ncompleted++;
                notifyViews(15, k1 << 12, k2 << 12);
                int j1 = l1 + 1 << 12;
                return j1;
            }
        }
    }

    public void surround(int i, int j, TagItem tagitem, int k)
        throws DocException
    {
        if(j >> 12 <= i >> 12 || i >> 12 < 0)
            throw new DocError(this, "invalid surround: " + (i >> 12) + " - " + (j >> 12));
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                modify();
                EndTagItem endtagitem = new EndTagItem();
                int l = j >> 12;
                int i1 = i >> 12;
                int j1 = k >> 12;
                int k1 = items.length;
                insertItems(j1, 2);
                int l1 = items.length;
                l += l1 - k1;
                System.arraycopy(items, i1, items, i1 + 1, j1 - i1);
                for(int i2 = i1 + 1; i2 <= j1; i2++)
                {
                    DocItem docitem = items[i2];
                    if(docitem.offset > 0 && i2 + docitem.offset > j1)
                        docitem.offset -= 2;
                    docitem.setIndex(i2, this);
                }

                tagitem.offset = l - i1;
                tagitem.setIndex(i1, this);
                items[i1] = tagitem;
                System.arraycopy(items, endPtr, items, endPtr - 1, (l - endPtr) + 1);
                endPtr--;
                for(int j2 = endPtr; j2 < l; j2++)
                {
                    DocItem docitem1 = items[j2];
                    if(docitem1.offset < 0 && j2 + docitem1.offset < endPtr)
                        docitem1.offset += 2;
                    docitem1.setIndex(j2, this);
                }

                endtagitem.offset = -((DocItem) (tagitem)).offset;
                endtagitem.setIndex(l, this);
                items[l] = endtagitem;
                notifyViews(15, l << 12, 4096);
                notifyViews(15, i1 << 12, 4096);
                notifyViews(17, i1 + 1 << 12, l - i1 - 1 << 12);
            }
        }
    }

    public int delete(int i, int j)
        throws DocException
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                modify();
                if(i < 0)
                    i = 0;
                if(j > nitems << 12)
                    j = nitems << 12;
                if(i >= j)
                {
                    int k = j;
                    return k;
                }
                int j1 = i >> 12;
                int k1 = j >> 12;
                if((j & 0xfff) > 0 && !items[k1].isText())
                    j = ++k1 << 12;
                if(j1 == k1)
                {
                    items[j1].delete(this, i, j - i);
                    notifyViews(13, i, j - i);
                    int l = i;
                    return l;
                }
                if((i & 0xfff) > 0 && items[j1].isText())
                {
                    int l1 = 4095 - (i & 0xfff);
                    items[j1++].delete(this, i, l1);
                    notifyViews(13, i, l1);
                }
                if(j1 < k1)
                    k1 = deleteItems(j1, k1);
                if((j & 0xfff) > 0)
                {
                    int i2 = j & 0xfff;
                    items[k1].delete(this, j1 << 12, i2);
                    notifyViews(13, j1 << 12, i2);
                }
                int i1 = k1 << 12;
                return i1;
            }
        }
    }

    public void change(int i, int j)
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                if(i < 0)
                    i = 0;
                if(j > nitems << 12)
                    j = nitems << 12;
                if(i >= j)
                    return;
                notifyViews(17, i, j - i);
            }
        }
    }

    public void paint(int i, int j)
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                if(i < 0)
                    i = 0;
                if(j > nitems << 12)
                    j = nitems << 12;
                if(i >= j)
                    return;
                notifyViews(18, i, j - i);
            }
        }
    }

    public void update(int i, int j)
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                if(i < 0)
                    i = 0;
                if(j > nitems << 12)
                    j = nitems << 12;
                if(i >= j)
                    return;
                notifyViews(19, i, j - i);
            }
        }
    }

    DocItem getInnermostOpenStartTag()
    {
        if(endPtr < items.length)
        {
            DocItem docitem = items[endPtr];
            return items[endPtr + docitem.offset];
        } else
        {
            return null;
        }
    }

    DocItemEnumeration getDocItems(int i, int j)
    {
        return new DocItemEnumeration(this, i, j);
    }

    public synchronized DocStyle getStyle(DocStyle docstyle, int i, int j)
    {
        Stack stack = null;
        int k = i >> 12;
        if((i & 0xfff) != 0)
            k++;
        while(k-- > j && items != null) 
        {
            DocItem docitem = items[k];
            if(docitem != null)
            {
                int l = docitem.offset;
                if(l > 0)
                {
                    DocStyle docstyle1 = getStyle(docstyle, k << 12, j).push(docitem);
                    if(stack != null)
                        while(!stack.empty()) 
                        {
                            DocItem docitem2 = (DocItem)stack.pop();
                            docstyle1 = docitem2.modifyStyleInPlace(docstyle1);
                        }
                    return docstyle1;
                }
                if(l == 0 && docitem.modifiesStyleInPlace())
                {
                    if(stack == null)
                        stack = new Stack();
                    stack.push(docitem);
                } else
                if(l < 0)
                {
                    int i1 = basefontTagRecord.numIndices <= 0 ? -1 : basefontTagRecord.maxLessThanOrEqual(k);
                    k += l;
                    if(i1 > k)
                    {
                        if(stack == null)
                            stack = new Stack();
                        stack.push(items[i1]);
                    }
                }
            }
        }
        if(stack != null)
            while(!stack.empty()) 
            {
                DocItem docitem1 = (DocItem)stack.pop();
                if(docitem1 == null)
                    break;
                docstyle = docitem1.modifyStyleInPlace(docstyle);
            }
        return docstyle;
    }

    public synchronized int findLabel(String s)
    {
        for(int i = 0; i < nitems; i++)
        {
            DocItem docitem = items[i];
            if(docitem.offset >= 0)
            {
                int j = docitem.findLabel(s);
                if(j != -1)
                    return j;
            }
        }

        return -1;
    }

    private String getColorString(Color color)
    {
        int i = color.getRed();
        int j = color.getGreen();
        int k = color.getBlue();
        char ac[] = {
            '#', Character.forDigit(i >> 4, 16), Character.forDigit(i & 0xf, 16), Character.forDigit(j >> 4, 16), Character.forDigit(j & 0xf, 16), Character.forDigit(k >> 4, 16), Character.forDigit(k & 0xf, 16)
        };
        return new String(ac);
    }

    public void write(OutputStream outputstream)
        throws DocException, IOException
    {
        OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);
        write(((Writer) (outputstreamwriter)));
        outputstreamwriter.flush();
    }

    public void write(Writer writer)
        throws DocException, IOException
    {
        HTMLOutputWriter htmloutputwriter = new HTMLOutputWriter(writer, getDTD());
        int i = state;
        try
        {
            i = setOwner(13, Thread.currentThread());
            String s = getDoctype();
            if(s != null)
                htmloutputwriter.doctype(s);
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            htmloutputwriter.comment(hjbproperties.getProperty("hotjava.html.header") + " " + new Date());
            TagItem tagitem = makeTag("html");
            TagItem tagitem1 = makeTag("head");
            TagItem tagitem2 = makeTag("title");
            TagItem tagitem3 = makeTag("body");
            Attributes attributes = new Attributes();
            String s1;
            if((s1 = (String)getProperty("background.img")) != null)
                attributes.put("background", s1);
            Color color;
            if((color = (Color)getProperty("background.color")) != null)
                attributes.put("bgcolor", getColorString(color));
            if((color = (Color)getProperty("text.color")) != null)
                attributes.put("text", getColorString(color));
            if((color = (Color)getProperty("link.color")) != null)
                attributes.put("link", getColorString(color));
            if((color = (Color)getProperty("vlink.color")) != null)
                attributes.put("vlink", getColorString(color));
            if((color = (Color)getProperty("alink.color")) != null)
                attributes.put("alink", getColorString(color));
            tagitem3.atts = attributes;
            htmloutputwriter.startTag(tagitem);
            htmloutputwriter.startTag(tagitem1);
            htmloutputwriter.startTag(tagitem2);
            s1 = getTitle();
            if(s1 != null)
                htmloutputwriter.text(s1);
            htmloutputwriter.endTag(tagitem2);
            URL url = (URL)getProperty("base");
            if(url != null)
            {
                TagItem tagitem4 = makeTag("base");
                tagitem4.atts = new Attributes();
                tagitem4.atts.put("href", url.toExternalForm());
                htmloutputwriter.emptyTag(tagitem4);
            }
            htmloutputwriter.endTag(tagitem1);
            htmloutputwriter.startTag(tagitem3);
            for(int j = 0; j < nitems; j++)
            {
                int k = items[j].offset;
                if(k > 0)
                    items[j].writeStartTag(htmloutputwriter);
                else
                if(k < 0)
                    items[j + k].writeEndTag(htmloutputwriter);
                else
                    items[j].write(htmloutputwriter);
            }

        }
        finally
        {
            setOwner(i, null);
            htmloutputwriter.flush();
        }
    }

    public synchronized int startPos(int i)
    {
        int j = i >> 12;
        int k = i & 0xfff;
        for(; j >= 0 && j < nitems; j--)
        {
            i = items[j].startOffset(this, k);
            if(i >= 0)
                return j << 12 | i;
            k = 4096;
        }

        return 0;
    }

    public int getSize()
    {
        return srcLength / 2;
    }

    protected CharArrayWriter getWriter()
    {
        return cw;
    }

    protected void zipSource()
    {
        srcBuffer = null;
        try
        {
            if(!GZIPOutputStream)
            {
                GZIPOutputStream = true;
                Class class1 = Class.forName("java.util.zip.GZIPOutputStream");
                GZIPOutputStreamCon = class1.getConstructor(new Class[] {
                    java.io.OutputStream.class
                });
                GZIPOutputStreamWrite = class1.getMethod("write", new Class[] {
                    byte[].class, Integer.TYPE, Integer.TYPE
                });
                GZIPOutputStreamClose = class1.getMethod("close", new Class[0]);
            }
            byte abyte0[] = new byte[cw.size() * 2];
            char ac[] = cw.toCharArray();
            int i = 0;
            for(int j = 0; j < ac.length; j++)
            {
                abyte0[i++] = (byte)(ac[j] & 0xff);
                abyte0[i++] = (byte)(ac[j] >> 8 & 0xff);
            }

            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            Object obj = GZIPOutputStreamCon.newInstance(new Object[] {
                bytearrayoutputstream
            });
            GZIPOutputStreamWrite.invoke(obj, new Object[] {
                abyte0, new Integer(0), new Integer(ac.length * 2)
            });
            GZIPOutputStreamClose.invoke(obj, new Object[0]);
            srcBuffer = bytearrayoutputstream.toByteArray();
            srcLength = abyte0.length;
            cw = null;
            return;
        }
        catch(ClassNotFoundException _ex)
        {
            return;
        }
        catch(InvocationTargetException _ex)
        {
            return;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    protected Reader unzipSource()
    {
        if(cw != null)
            return new CharArrayReader(cw.toCharArray());
        try
        {
            if(!GZIPInputStream)
            {
                GZIPInputStream = true;
                Class class1 = Class.forName("java.util.zip.GZIPInputStream");
                GZIPInputStreamCon = class1.getConstructor(new Class[] {
                    java.io.InputStream.class
                });
                GZIPInputStreamRead = class1.getMethod("read", new Class[] {
                    byte[].class, Integer.TYPE, Integer.TYPE
                });
                GZIPInputStreamClose = class1.getMethod("close", new Class[0]);
            }
            byte abyte0[] = null;
            int i = 1;
            Object obj = GZIPInputStreamCon.newInstance(new Object[] {
                new ByteArrayInputStream(srcBuffer)
            });
            abyte0 = new byte[srcLength];
            int j;
            while((j = ((Integer)GZIPInputStreamRead.invoke(obj, new Object[] {
                abyte0, new Integer(i - 1), new Integer(srcLength - i)
            })).intValue()) > 0) 
                i += j;
            GZIPInputStreamClose.invoke(obj, new Object[0]);
            char ac[] = new char[i / 2];
            int k = 0;
            for(int l = 0; l < i / 2; l++)
            {
                ac[l] |= (char)(abyte0[k++] & 0xff);
                ac[l] |= (char)(abyte0[k++] << 8 & 0xff00);
            }

            return new CharArrayReader(ac);
        }
        catch(ClassNotFoundException _ex)
        {
            return null;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return null;
    }

    public Reader getSourceDocument()
        throws DocException
    {
        if(getProperty("template.src.url") == null)
            if(srcLength > 0)
                return unzipSource();
            else
                return new CharArrayReader(cw.toCharArray());
        CharArrayWriter chararraywriter = new CharArrayWriter();
        try
        {
            write(chararraywriter);
        }
        catch(IOException _ex)
        {
            throw new DocError(this, "I/O error on buffer");
        }
        StringReader stringreader = new StringReader(chararraywriter.toString());
        return stringreader;
    }

    public synchronized void reset()
        throws DocException
    {
        setState(0);
        owner = null;
        parsingCompleted = false;
        setIsOkToFormat(false);
    }

    public void clearProperties()
        throws DocException
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                modify();
                DTD dtd = getDTD();
                URL url = getURL();
                URL url1 = getReferer();
                props = new Hashtable(11);
                setProperty("dtd", dtd);
                setProperty("url", url);
                setProperty("referer", url1);
            }
        }
    }

    public void clear()
        throws DocException
    {
        synchronized(Globals.getAwtLock())
        {
            synchronized(this)
            {
                modify();
                delete(0, 0x7fffffff);
                cw = new CharArrayWriter();
                srcBuffer = null;
                endPtr = items.length;
            }
        }
    }

    public Range find(String s)
    {
        return find(s, 0);
    }

    public Range find(String s, int i)
    {
        return find(s, i, true);
    }

    public synchronized Range find(String s, int i, boolean flag)
    {
        OverlappingStringMatch overlappingstringmatch = new OverlappingStringMatch();
        String s1 = null;
        int j = i >> 12;
        int k = i & 0xfff;
        boolean flag1 = false;
        int l = 0;
        int i1 = 0;
        while(j < nitems) 
        {
            DocItem docitem = items[j];
            if(!flag1)
            {
                int j1 = docitem.find(s, k, flag, overlappingstringmatch);
                if(j1 >= 0)
                {
                    if(overlappingstringmatch.matchLength == s.length())
                    {
                        Range range = new Range(j << 12 | j1, j << 12 | j1 + s.length());
                        return range;
                    }
                    flag1 = true;
                    l = j;
                    i1 = j1;
                    s1 = s.substring(overlappingstringmatch.matchLength);
                }
            } else
            if((docitem instanceof FlowTagItem) || (docitem instanceof TextItem) || (docitem instanceof EndTagItem) || (docitem instanceof EmptyTagItem))
            {
                int k1 = docitem.find(s1, k, flag, overlappingstringmatch);
                if(k1 >= 0)
                {
                    String s2 = docitem.getText();
                    if(!s2.startsWith(overlappingstringmatch.overlappingMatch))
                    {
                        flag1 = false;
                        k = 0;
                        continue;
                    }
                    if(overlappingstringmatch.matchLength < s1.length())
                    {
                        s1 = s1.substring(overlappingstringmatch.matchLength);
                    } else
                    {
                        Range range1 = new Range(l << 12 | i1, j << 12 | overlappingstringmatch.matchLength);
                        return range1;
                    }
                } else
                if(docitem instanceof TextItem)
                {
                    flag1 = false;
                    k = 0;
                    continue;
                }
            } else
            {
                flag1 = false;
                k = 0;
                continue;
            }
            k = 0;
            j++;
        }
        return null;
    }

    public synchronized int count(int i, int j)
    {
        int k = 0;
        for(int l = i; l < j;)
        {
            int i1 = items[l].offset;
            if(i1 > 0)
                l += i1;
            l++;
            k++;
        }

        return k;
    }

    public void print()
    {
        Object obj;
        for(Enumeration enumeration = props.keys(); enumeration.hasMoreElements(); System.out.println(obj + "=" + props.get(obj)))
            obj = enumeration.nextElement();

        for(int i = 0; i < nitems; i++)
        {
            DocItem docitem = items[i];
            String s = docitem.getClass().getName();
            System.out.println(i + " = " + s.substring(s.lastIndexOf('.') + 1) + "," + docitem);
        }

    }

    public synchronized DocItem getItem(int i)
    {
        if(i < 0 || i > ncompleted && i < endPtr && items[i] == null)
            throw new ArrayIndexOutOfBoundsException(i);
        else
            return items[i];
    }

    public boolean isStateless()
    {
        for(int i = 0; i < nitems; i++)
            if((items[i] instanceof APPLET) || (items[i] instanceof FORM))
                return false;

        return true;
    }

    private boolean releaseImages()
    {
        for(int i = 0; i < nitems; i++)
            if((items[i] instanceof IMG) && !((IMG)items[i]).isMappable())
            {
                IMG img = (IMG)items[i];
                img.flush();
            }

        return false;
    }

    private boolean releaseImageMaps()
    {
        for(int i = 0; i < nitems; i++)
            if((items[i] instanceof IMG) && ((IMG)items[i]).isMappable())
            {
                IMG img = (IMG)items[i];
                img.flush();
            }

        return false;
    }

    private boolean releaseApplets()
    {
        for(int i = 0; i < nitems; i++)
            if(items[i] instanceof APPLET)
            {
                items[i].flush();
                notifyViews(21, i << 12, 4096);
            }

        return false;
    }

    private boolean releaseForms()
    {
        for(int i = 0; i < nitems; i++)
            if(items[i] instanceof FORM)
            {
                items[i].flush();
                notifyViews(21, i << 12, i + items[i].getOffset() << 12);
            }

        return false;
    }

    public void invalidateFrames()
    {
        for(int i = 0; i < nitems; i++)
        {
            if(items[i] instanceof FRAMESET)
            {
                java.awt.Container container = ((FRAMESET)items[i]).getPanel();
                if(container != null)
                    ((FrameSetPanel)container).invalidateFrames();
            }
            if(items[i] instanceof FRAME)
            {
                URL url = ((FRAME)items[i]).getSrc();
                if(url != null)
                    DocumentCache.removeDocument(url);
            }
        }

    }

    public void setIHaveFrameSet()
    {
        framed = true;
    }

    public boolean hasFrameSet()
    {
        return framed;
    }

    public boolean checkDate()
    {
        if(getConnector() != null)
            return false;
        URL url = getURL();
        if(url == null)
            return false;
        try
        {
            HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection();
            httpurlconnection.setRequestMethod("HEAD");
            httpurlconnection.setRequestProperty("Cache-Control", "no-cache");
            httpurlconnection.setRequestProperty("Pragma", "no-cache");
            httpurlconnection.getInputStream();
            Date date = new Date(httpurlconnection.getLastModified());
            if(getLastModifiedDate().equals(date))
                return true;
        }
        catch(Exception _ex)
        {
            return false;
        }
        return false;
    }

    public void reloadFrames()
    {
        if(!hasFrameSet())
            return;
        for(int i = 0; i < nitems; i++)
            if(items[i] instanceof FRAMESET)
            {
                java.awt.Container container = ((FRAMESET)items[i]).getPanel();
                if(container != null)
                    ((FrameSetPanel)container).reload();
            }

    }

    public void reload()
        throws DocException
    {
        setState(4);
        interruptOwnerWaitCompletion();
        invalidateFrames();
        frameList = null;
        ImageCacher imagecacher = HotJavaBrowserBean.getImageCache();
        if(imagecacher != null)
            imagecacher.flushDocumentImages(this);
        setState(0);
        delete(0, 0x7fffffff);
        reset();
        setProperty("background.img", null);
        setProperty("background.color", null);
        setProperty("text.color", null);
        setProperty("link.color", null);
        setProperty("vlink.color", null);
        setProperty("alink.color", null);
        setProperty("charset", null);
        reloaded = true;
        notifyViews(24, 0, 0);
        DocParser docparser = new DocParser(this, false);
        docparser.addPropertyChangeListener(this);
        releaseImages();
        releaseImageMaps();
    }

    public void setParserProperties(Properties properties)
    {
        try
        {
            new DocParser(this, properties);
            return;
        }
        catch(DocException _ex)
        {
            return;
        }
    }

    public void setConnector(URLConnector urlconnector)
    {
        connector = urlconnector;
    }

    public URLConnector getConnector()
    {
        return connector;
    }

    public String toString()
    {
        return getClass().getName() + "[title=" + getTitle() + ",url=" + getURL() + ",state=" + state + "]";
    }

    public void addPropertyChangeListener(PropertyChangeListener propertychangelistener)
    {
        changes.addPropertyChangeListener(propertychangelistener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertychangelistener)
    {
        changes.removePropertyChangeListener(propertychangelistener);
    }

    public void addVetoableChangeListener(VetoableChangeListener vetoablechangelistener)
    {
        vchanges.addVetoableChangeListener(vetoablechangelistener);
    }

    public void removeVetoableChangeListener(VetoableChangeListener vetoablechangelistener)
    {
        vchanges.removeVetoableChangeListener(vetoablechangelistener);
    }

    public void setErrorMessage(String s)
    {
        changes.firePropertyChange("documentError", documentError, s);
        documentError = s;
    }

    public void setDocumentTitle(String s)
    {
        String _tmp = (String)getProperty("title");
        try
        {
            setProperty("title", s);
            return;
        }
        catch(DocException _ex)
        {
            return;
        }
    }

    public String getDocumentTitle()
    {
        return getTitle();
    }

    public void propertyChange(PropertyChangeEvent propertychangeevent)
    {
        if(propertychangeevent.getPropertyName().equals("parseError"))
        {
            setErrorMessage("parse: " + (String)propertychangeevent.getNewValue());
            return;
        }
        if(propertychangeevent.getPropertyName().equals("documentString"))
            changes.firePropertyChange("documentString", propertychangeevent.getOldValue(), propertychangeevent.getNewValue());
    }

    protected boolean changeNotVetoed(String s, Object obj)
    {
        boolean flag = true;
        String as[] = {
            "background.img", "background.color", "text.color", "link.color", "vlink.color", "alink.color"
        };
        String as1[] = {
            "backgroundImage", "backgroundColor", "textColor", "linkColor", "visitedLinkColor", "clickLinkColor"
        };
        for(int i = 0; i < as.length; i++)
        {
            if(!s.equals(as[i]))
                continue;
            try
            {
                vchanges.fireVetoableChange(as1[i], props.get(s), obj);
            }
            catch(PropertyVetoException _ex)
            {
                flag = false;
            }
            break;
        }

        return flag;
    }

    public void printDocContext(int i, int j)
    {
        int k = i >> 12;
        int l = k - j;
        int i1 = k + j;
        if(l < 0)
            l = 0;
        if(i1 > items.length)
            i1 = items.length;
        System.out.println("***************************************");
        System.out.println("*  Current doc context of " + j);
        for(int j1 = l; j1 < i1; j1++)
        {
            TagItem tagitem = null;
            if(items[j1] != null && (items[j1] instanceof TagItem))
                tagitem = (TagItem)items[j1];
            if(j1 == k)
                System.out.println("* ===> items[" + j1 + "] == <" + (items[j1] == null ? "null" : getTagName(items[j1])) + "> offset = " + (items[j1] == null ? 0 : items[j1].offset));
            else
                System.out.println("*      items[" + j1 + "] == <" + (items[j1] == null ? "null" : getTagName(items[j1])) + "> offset = " + (items[j1] == null ? 0 : items[j1].offset));
        }

        System.out.println("***************************************");
    }

    public String getTagName(DocItem docitem)
    {
        if(docitem instanceof TagItem)
            return ((TagItem)docitem).getElement().getName();
        if(docitem instanceof EndTagItem)
            return "/" + ((EndTagItem)docitem).getTag(this).getElement().getName();
        if(docitem instanceof TextItem)
            return "text item";
        else
            return "Unknown";
    }

    public Hashtable getImageMaps()
    {
        if(imageMaps == null)
            synchronized(this)
            {
                if(imageMaps == null)
                    imageMaps = new Hashtable(11);
            }
        return imageMaps;
    }

    public ImageMap findImageMap(URL url)
    {
        if(imageMaps != null)
            return (ImageMap)imageMaps.get(url);
        else
            return null;
    }

    public void addSizeItemWidthWaitRef(SizeItem sizeitem)
    {
        startTimer();
        sizeItemListWidth.put(sizeitem, dummyInt);
    }

    public void addSizeItemHeightWaitRef(SizeItem sizeitem)
    {
        startTimer();
        sizeItemListHeight.put(sizeitem, dummyInt);
    }

    public void addSizeItemWaitRef(SizeItem sizeitem)
    {
        addSizeItemWidthWaitRef(sizeitem);
        addSizeItemHeightWaitRef(sizeitem);
    }

    public void removeSizeItemWidthWaitRef(SizeItem sizeitem)
    {
        boolean flag = isSizeItemWidthWaitRef();
        sizeItemListWidth.remove(sizeitem);
        if(flag)
            dispatchFormatScreen();
    }

    public void removeSizeItemHeightWaitRef(SizeItem sizeitem)
    {
        boolean flag = isSizeItemHeightWaitRef();
        sizeItemListHeight.remove(sizeitem);
        if(flag)
            dispatchFormatScreen();
    }

    public void removeSizeItemWaitRef(SizeItem sizeitem)
    {
        boolean flag = isSizeItemWaitRef();
        sizeItemListWidth.remove(sizeitem);
        sizeItemListHeight.remove(sizeitem);
        if(flag)
            dispatchFormatScreen();
    }

    public void dispatchFormatScreen()
    {
        setIsOkToFormat(false);
        if(sizeItemListWidth.size() <= 0 && sizeItemListHeight.size() <= 0 && parsingCompleted)
        {
            synchronized(Globals.getAwtLock())
            {
                synchronized(this)
                {
                    timeOutStack.removeAllElements();
                    notifyViews(23, 0, 0);
                }
            }
            return;
        } else
        {
            return;
        }
    }

    public void purgeSizeItemWidthWaitRef()
    {
        sizeItemListWidth = new Hashtable();
    }

    public void purgeSizeItemHeightWaitRef()
    {
        sizeItemListHeight = new Hashtable();
    }

    public void purgeSizeItemWaitRef()
    {
        purgeSizeItemWidthWaitRef();
        purgeSizeItemHeightWaitRef();
    }

    public boolean isSizeItemWidthWaitRef()
    {
        return sizeItemListWidth.size() > 0;
    }

    public boolean isSizeItemHeightWaitRef()
    {
        return sizeItemListHeight.size() > 0;
    }

    public boolean isSizeItemWaitRef()
    {
        return sizeItemListWidth.size() > 0 || sizeItemListHeight.size() > 0;
    }

    public void setIsOkToFormat(boolean flag)
    {
        if(!isSizeItemWaitRef() && doneParsing() && !flag)
        {
            bIsOkToFormat = true;
            return;
        } else
        {
            bIsOkToFormat = flag;
            return;
        }
    }

    public boolean isOkToFormat()
    {
        return bIsOkToFormat;
    }

    public void updateClient(Object obj)
    {
        boolean flag = isSizeItemWaitRef();
        Long long1 = null;
        if(!timeOutStack.empty())
            long1 = (Long)timeOutStack.peek();
        if(obj != null && long1 != null && long1.equals(obj))
        {
            timeOutStack.removeAllElements();
            System.out.println("DOC TIMED OUT ON IMG");
            SizeItem sizeitem;
            for(Enumeration enumeration = sizeItemListWidth.keys(); enumeration.hasMoreElements(); sizeitem.waiterTimedOut())
                sizeitem = (SizeItem)enumeration.nextElement();

            SizeItem sizeitem1;
            for(Enumeration enumeration1 = sizeItemListHeight.keys(); enumeration1.hasMoreElements(); sizeitem1.waiterTimedOut())
                sizeitem1 = (SizeItem)enumeration1.nextElement();

        }
        purgeSizeItemWaitRef();
        if(flag)
            dispatchFormatScreen();
    }

    private void startTimer()
    {
        if(sizeItemListWidth.size() <= 0 && sizeItemListHeight.size() <= 0)
        {
            Long long1 = new Long(System.currentTimeMillis());
            timeOutStack.push(long1);
            int i = HJBProperties.getHJBProperties("beanPropertiesKey").getInteger("allimages.timeout", 60000);
            ScreenUpdater.updater.notify(this, i, long1);
        }
    }

    public int addFrame(FRAME frame)
    {
        if(frameList == null)
            frameList = new Vector();
        frameList.addElement(frame);
        return frameList.size() - 1;
    }

    public void removeFrame(FRAME frame)
    {
        if(frameList != null)
            frameList.removeElement(frame);
    }

    public Enumeration getFrameList()
    {
        if(frameList != null)
            return frameList.elements();
        else
            return null;
    }

    public FRAME getFrameAt(int i)
    {
        if(frameList != null && frameList.size() > i)
            return (FRAME)frameList.elementAt(i);
        else
            return null;
    }

    public int getNumberOfFrames()
    {
        if(frameList != null)
            return frameList.size();
        else
            return 0;
    }

    public void addJavaScriptTag(Tag tag)
    {
        if(javaScriptTags == null)
            javaScriptTags = new Hashtable(10);
        javaScriptTags.put(tag, tag);
    }

    public void setHasScriptWrites(boolean flag)
    {
        hasScriptWrites = flag;
    }

    public boolean hasJavaScriptTags()
    {
        if(windowHandlerExists)
            return true;
        windowHandlerExists = hasWindowHandler();
        return windowHandlerExists || javaScriptTags != null || hasScriptWrites;
    }

    public boolean doesTagHasJavaScript(Tag tag)
    {
        if(javaScriptTags == null)
            return false;
        return javaScriptTags.get(tag) != null;
    }

    public Enumeration getJavaScriptTags()
    {
        return javaScriptTags.elements();
    }

    private boolean hasWindowHandler()
    {
        for(int i = 0; i < windowHandlers.length; i++)
            if(getProperty(windowHandlers[i]) != null)
                return true;

        return false;
    }

    public boolean isExpired()
    {
        Date date = getExpirationDate();
        return date != null && date.getTime() <= System.currentTimeMillis();
    }

    private int state;
    private boolean reloaded;
    private Thread owner;
    private Parser currentParser;
    private String pragma;
    private Hashtable props;
    private int ncompleted;
    public int nitems;
    public DocItem items[];
    private int endPtr;
    private DocumentEventMulticaster listeners;
    private int nviews;
    private DocView views[];
    private BasefontTagRecord basefontTagRecord;
    private URLConnector connector;
    private boolean parsingCompleted;
    private String documentError;
    private PropertyChangeSupport changes;
    private VetoableChangeSupport vchanges;
    private Hashtable imageMaps;
    private Hashtable sizeItemListWidth;
    private Hashtable sizeItemListHeight;
    private static final Integer dummyInt = new Integer(0);
    private boolean bIsOkToFormat;
    private Hashtable javaScriptTags;
    private boolean framed;
    private Stack timeOutStack;
    private String clientPullSpec;
    private CharArrayWriter cw;
    private byte srcBuffer[];
    private int srcLength;
    private static Hashtable tagClassCache = new Hashtable();
    private static boolean GZIPOutputStream;
    private static Constructor GZIPOutputStreamCon;
    private static Method GZIPOutputStreamWrite;
    private static Method GZIPOutputStreamClose;
    private static boolean GZIPInputStream;
    private static Constructor GZIPInputStreamCon;
    private static Method GZIPInputStreamRead;
    private static Method GZIPInputStreamClose;
    private Vector frameList;
    private boolean windowHandlerExists;
    private boolean hasScriptWrites;
    private static final String windowHandlers[] = {
        "onblur", "onfocus", "ondragdrop", "onload", "onmove", "onresize", "onunload"
    };

}
