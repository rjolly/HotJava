// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotJavaDocumentStack.java

package sunw.hotjava.bean;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.Vector;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.bean:
//            BrowserHistoryEvent, BrowserHistoryListener, DocumentStacker

public class HotJavaDocumentStack
    implements Externalizable, BrowserHistoryListener, DocumentStacker
{

    public HotJavaDocumentStack()
    {
        changeSupport = new PropertyChangeSupport(this);
        nextDocumentAvailable = false;
        previousDocumentAvailable = false;
        cloneNecessary = true;
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        setContentsDepth(hjbproperties.getInteger("hotjava.docstack.contents.depth", 10));
        setLogicalDepth(hjbproperties.getInteger("hotjava.docstack.logical.depth", 100));
    }

    public synchronized void addBrowserHistoryListener(BrowserHistoryListener browserhistorylistener)
    {
        if(listeners == null)
            listeners = new Vector();
        cloneNecessary = true;
        listeners.addElement(browserhistorylistener);
        dispatchBrowserHistoryEvent(6);
        dispatchBrowserHistoryEvent(7);
    }

    public synchronized void removeBrowserHistoryListener(BrowserHistoryListener browserhistorylistener)
    {
        if(listeners == null)
        {
            return;
        } else
        {
            cloneNecessary = true;
            listeners.removeElement(browserhistorylistener);
            return;
        }
    }

    protected void dispatchBrowserHistoryEvent(int i)
    {
        synchronized(this)
        {
            if(listeners == null)
                return;
            if(cloneNecessary)
            {
                targets = (Vector)listeners.clone();
                cloneNecessary = false;
            }
        }
        BrowserHistoryEvent browserhistoryevent = new BrowserHistoryEvent(this, i);
        for(int j = 0; j < targets.size(); j++)
        {
            BrowserHistoryListener browserhistorylistener = (BrowserHistoryListener)targets.elementAt(j);
            browserhistorylistener.executeHistoryCommand(browserhistoryevent);
        }

    }

    public void executeHistoryCommand(BrowserHistoryEvent browserhistoryevent)
    {
        switch(browserhistoryevent.getCommand())
        {
        case 3: // '\003'
            boolean flag = ((Boolean)browserhistoryevent.getArgument()).booleanValue();
            setNextAvailable(flag);
            return;

        case 4: // '\004'
            boolean flag1 = ((Boolean)browserhistoryevent.getArgument()).booleanValue();
            setPreviousAvailable(flag1);
            return;
        }
    }

    public void previousDocument()
    {
        dispatchBrowserHistoryEvent(2);
    }

    public void nextDocument()
    {
        dispatchBrowserHistoryEvent(1);
    }

    public void eraseDocumentHistory()
    {
        System.out.println("Erasing Doc Stack");
        dispatchBrowserHistoryEvent(0);
        setPreviousAvailable(false);
        setNextAvailable(false);
    }

    public void setNextAvailable(boolean flag)
    {
        boolean flag1 = nextDocumentAvailable;
        nextDocumentAvailable = flag;
        changeSupport.firePropertyChange("nextDocumentAvailable", new Boolean(flag1), new Boolean(flag));
    }

    public boolean isNextAvailable()
    {
        return nextDocumentAvailable;
    }

    public void setContentsDepth(int i)
    {
        contentsDepth = i;
        dispatchBrowserHistoryEvent(7);
    }

    public int getContentsDepth()
    {
        return contentsDepth;
    }

    public void setLogicalDepth(int i)
    {
        logicalDepth = i;
        dispatchBrowserHistoryEvent(6);
    }

    public int getLogicalDepth()
    {
        return logicalDepth;
    }

    public void setPreviousAvailable(boolean flag)
    {
        boolean flag1 = previousDocumentAvailable;
        previousDocumentAvailable = flag;
        changeSupport.firePropertyChange("previousDocumentAvailable", new Boolean(flag1), new Boolean(flag));
    }

    public boolean isPreviousAvailable()
    {
        return previousDocumentAvailable;
    }

    public void addPropertyChangeListener(PropertyChangeListener propertychangelistener)
    {
        changeSupport.addPropertyChangeListener(propertychangelistener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertychangelistener)
    {
        changeSupport.removePropertyChangeListener(propertychangelistener);
    }

    private void writeObject(ObjectOutputStream objectoutputstream)
        throws IOException
    {
        objectoutputstream.defaultWriteObject();
        synchronized(this)
        {
            if(listeners == null)
                return;
            if(cloneNecessary)
            {
                targets = (Vector)listeners.clone();
                cloneNecessary = false;
            }
        }
        for(int i = 0; i < targets.size(); i++)
        {
            BrowserHistoryListener browserhistorylistener = (BrowserHistoryListener)targets.elementAt(i);
            if(browserhistorylistener instanceof Serializable)
                objectoutputstream.writeObject(browserhistorylistener);
        }

        objectoutputstream.writeObject(null);
    }

    private void readObject(ObjectInputStream objectinputstream)
        throws ClassNotFoundException, IOException
    {
        objectinputstream.defaultReadObject();
        Object obj;
        while((obj = objectinputstream.readObject()) != null) 
            addBrowserHistoryListener((BrowserHistoryListener)obj);
    }

    public void writeExternal(ObjectOutput objectoutput)
        throws IOException
    {
        objectoutput.writeInt(2);
        objectoutput.writeInt(contentsDepth);
        objectoutput.writeInt(logicalDepth);
    }

    public void readExternal(ObjectInput objectinput)
        throws IOException, ClassNotFoundException
    {
        int i = objectinput.readInt();
        if(i > 2 || i < 1)
            throw new IOException("Unrecognized " + getClass().getName() + " version:  " + i);
        if(i > 1)
        {
            setContentsDepth(objectinput.readInt());
            setLogicalDepth(objectinput.readInt());
        }
    }

    static final long serialVersionUID = 0x2429adf0eac10685L;
    private int contentsDepth;
    private int logicalDepth;
    private static final int externalizedVersion = 2;
    protected PropertyChangeSupport changeSupport;
    private boolean nextDocumentAvailable;
    private boolean previousDocumentAvailable;
    private boolean cloneNecessary;
    private Vector targets;
    private Vector listeners;
}
