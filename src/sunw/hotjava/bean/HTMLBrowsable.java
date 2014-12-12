// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HTMLBrowsable.java

package sunw.hotjava.bean;

import java.awt.Component;
import java.awt.PrintJob;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.io.*;
import java.net.URL;
import java.util.Observer;
import sunw.hotjava.applet.AppletManager;

// Referenced classes of package sunw.hotjava.bean:
//            BrowserHistoryListener, LinkListener, LinkEvent, CurrentDocument, 
//            DocumentSelection, URLPooler, CookieJarInterface, ImageCacher

public interface HTMLBrowsable
    extends BrowserHistoryListener, Cloneable
{

    public abstract void addLinkListener(LinkListener linklistener);

    public abstract void removeLinkListener(LinkListener linklistener);

    public abstract void handleLinkEvent(LinkEvent linkevent);

    public abstract void addBrowserHistoryListener(BrowserHistoryListener browserhistorylistener);

    public abstract void removeBrowserHistoryListener(BrowserHistoryListener browserhistorylistener);

    public abstract CurrentDocument getCurrentDocument();

    public abstract void addPropertyChangeListener(PropertyChangeListener propertychangelistener);

    public abstract void removePropertyChangeListener(PropertyChangeListener propertychangelistener);

    public abstract void addVetoableChangeListener(VetoableChangeListener vetoablechangelistener);

    public abstract void removeVetoableChangeListener(VetoableChangeListener vetoablechangelistener);

    public abstract URL getDocumentURL();

    public abstract URL getFrameURL();

    public abstract Reader getDocumentSource();

    public abstract Reader getFrameSource();

    public abstract int saveDocumentSource(FileOutputStream fileoutputstream, Observer observer)
        throws IOException;

    public abstract int saveFrameSource(FileOutputStream fileoutputstream, Observer observer)
        throws IOException;

    public abstract int saveURLContent(URL url, FileOutputStream fileoutputstream, Observer observer)
        throws IOException;

    public abstract String getDocumentTitle();

    public abstract String getDocumentString();

    public abstract void setDocumentString(String s);

    public abstract void setDocumentURL(URL url);

    public abstract void setDocumentSource(Reader reader);

    public abstract void stopLoading();

    public abstract void reload();

    public abstract int find(int i, String s);

    public abstract int find(int i, String s, boolean flag);

    public abstract void clearImageCache();

    public abstract String getCharset();

    public abstract void setCharset(String s);

    public abstract DocumentSelection getSelection();

    public abstract void print(boolean flag);

    public abstract void print(PrintJob printjob);

    public abstract void setAppletManager(AppletManager appletmanager);

    public abstract void setURLPoolManager(URLPooler urlpooler);

    public abstract void setCookiesManager(CookieJarInterface cookiejarinterface);

    public abstract void setDocFontNotInPlace();

    public abstract Object clone();

    public abstract Component getComponent();

    public abstract void processKeyActionEvent(KeyEvent keyevent);

    public abstract void destroyAllApplets(boolean flag);

    public abstract void setImageCache(ImageCacher imagecacher);

    public abstract String getName();

    public abstract void setName(String s);

    public abstract String[] getFrameList();
}
