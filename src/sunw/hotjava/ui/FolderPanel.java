// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotListFrame.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import sunw.hotjava.HJFrame;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.bean.HTMLBrowsable;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            Bookmark, BookmarkDialog, FolderDialog, HotCanvas, 
//            HotList, HotListBuffer, HotListEntry, HotListEvent, 
//            HotListFrame, ListCanvas, ListPanel, PanelWithInsets, 
//            Webmark, HotListListener

class FolderPanel extends Panel
{
    private class HotListEventListener
        implements HotListListener
    {

        public void hotlistChanged(HotListEvent hotlistevent)
        {
            processHotListEvent(hotlistevent);
        }

        HotListEventListener()
        {
        }
    }


    public FolderPanel(HotListFrame hotlistframe)
    {
        frm = hotlistframe;
        setLayout(new BorderLayout());
        folderPanel = new Panel();
        folderPane = new ScrollPane();
        folderPane.add(folderPanel);
        add("Center", folderPane);
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        folderPanel.setBackground(hjbproperties.getColor("hotlistframe.bgcolor", null));
        folderPane.setBackground(hjbproperties.getColor("hotjava.background", null));
    }

    public HotCanvas getCanvas()
    {
        return hotCanvas;
    }

    public void initialize()
    {
        hotlist = frm.getHotList();
        String s = frm.getListName();
        int i = getSize().width;
        int j = getSize().height;
        hotCanvas = new HotCanvas(hotlist, folderPanel, s, i, j, frm);
        java.awt.event.ComponentListener componentlistener = hotCanvas.getListener();
        folderPane.addComponentListener(componentlistener);
        hotCanvas.setListElementImage(frm.getBookImage());
        hotCanvas.setListContainerImages(frm.getOpenImage(), frm.getClosedImage());
        invalidate();
        folderPanel.setLayout(new BorderLayout());
        folderPanel.add("West", hotCanvas);
        folderPanel.setSize(hotCanvas.getPreferredSize());
        validate();
        MouseAdapter mouseadapter = new MouseAdapter() {

            public void mousePressed(MouseEvent mouseevent)
            {
                processFolderPanelMouseEvent(mouseevent);
            }

            public void mouseReleased(MouseEvent mouseevent)
            {
                processFolderPanelMouseEvent(mouseevent);
            }

        }
;
        addMouseListener(mouseadapter);
        MouseMotionAdapter mousemotionadapter = new MouseMotionAdapter() {

            public void mouseDragged(MouseEvent mouseevent)
            {
                processFolderPanelMouseEvent(mouseevent);
            }

        }
;
        addMouseMotionListener(mousemotionadapter);
        hotlist.addHotListListener(new HotListEventListener());
    }

    private void redispatchEvent(MouseEvent mouseevent)
    {
        Point point = mouseevent.getComponent().getLocation();
        mouseevent.translatePoint(point.x, point.y);
        frm.dispatchEvent(mouseevent);
    }

    public ScrollPane getFolderPane()
    {
        return folderPane;
    }

    public boolean processHotListEvent(HotListEvent hotlistevent)
    {
        Object obj = hotlistevent.getArgument();
        HotListEntry hotlistentry = (HotListEntry)obj;
        switch(hotlistevent.getID())
        {
        default:
            break;

        case 50: // '2'
            if(hotCanvas.getMainContainer() != null)
                hotCanvas.addEntry(hotlistentry);
            break;

        case 51: // '3'
            hotCanvas.delItem(hotlistentry);
            break;

        case 52: // '4'
            hotCanvas.changeFolderState(hotlistentry);
            break;
        }
        return true;
    }

    public void processFolderPanelMouseEvent(MouseEvent mouseevent)
    {
        switch(mouseevent.getID())
        {
        case 501: 
            if(mouseevent.getClickCount() == 2 && !dragged)
            {
                visitDocument(mouseevent.isShiftDown());
                return;
            }
            // fall through

        case 502: 
            ListItem listitem = hotCanvas.getSelectedItem();
            if(listitem != null && (listitem instanceof Bookmark))
            {
                Bookmark bookmark = (Bookmark)listitem;
                Webmark webmark = bookmark.getWebmark();
                String s = webmark.getURL();
                frm.setMessage(s);
            } else
            {
                frm.setMessage("");
            }
            redispatchEvent(mouseevent);
            dragged = false;
            return;

        case 506: 
            redispatchEvent(mouseevent);
            dragged = true;
            return;

        default:
            return;
        }
    }

    public void visitDocument(boolean flag)
    {
        ListItem listitem = hotCanvas.getSelectedItem();
        if(listitem == null)
            return;
        if(!(listitem instanceof Bookmark))
            return;
        Bookmark bookmark = (Bookmark)listitem;
        Webmark webmark = bookmark.getWebmark();
        try
        {
            URL url = new URL(webmark.getURL());
            HJFrame hjframe = HJWindowManager.getHJWindowManager().getLastFocusHolder();
            if(flag || hjframe == null)
            {
                HJWindowManager.getHJWindowManager().cloneFrame(hjframe, url);
                return;
            } else
            {
                hjframe.getHTMLBrowsable().setDocumentURL(url);
                return;
            }
        }
        catch(MalformedURLException _ex)
        {
            return;
        }
    }

    public void notify(int i, Object obj)
    {
    }

    HotListFrame frm;
    HotCanvas hotCanvas;
    HotList hotlist;
    Panel folderPanel;
    ScrollPane folderPane;
    static final String propName = "hotlistframe";
    static boolean dragged;
}
