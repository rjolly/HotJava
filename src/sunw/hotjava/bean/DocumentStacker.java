// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentStacker.java

package sunw.hotjava.bean;

import java.beans.PropertyChangeListener;
import java.io.*;

// Referenced classes of package sunw.hotjava.bean:
//            BrowserHistoryListener, BrowserHistoryEvent

public interface DocumentStacker
    extends BrowserHistoryListener
{

    public abstract void addBrowserHistoryListener(BrowserHistoryListener browserhistorylistener);

    public abstract void removeBrowserHistoryListener(BrowserHistoryListener browserhistorylistener);

    public abstract void executeHistoryCommand(BrowserHistoryEvent browserhistoryevent);

    public abstract void previousDocument();

    public abstract void nextDocument();

    public abstract void eraseDocumentHistory();

    public abstract void setNextAvailable(boolean flag);

    public abstract boolean isNextAvailable();

    public abstract void setContentsDepth(int i);

    public abstract int getContentsDepth();

    public abstract void setLogicalDepth(int i);

    public abstract int getLogicalDepth();

    public abstract void setPreviousAvailable(boolean flag);

    public abstract boolean isPreviousAvailable();

    public abstract void addPropertyChangeListener(PropertyChangeListener propertychangelistener);

    public abstract void removePropertyChangeListener(PropertyChangeListener propertychangelistener);

    public abstract void writeExternal(ObjectOutput objectoutput)
        throws IOException;

    public abstract void readExternal(ObjectInput objectinput)
        throws IOException, ClassNotFoundException;
}
