// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotListFrame.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            BookmarkDialog, BrowserListCanvas, Folder, FolderDialog, 
//            FolderPanel, HotCanvas, HotList, HotListBuffer, 
//            HotListEntry, HotListEvent, HotListFrame, ListCanvas, 
//            ListContainer, PanelWithInsets, HotListListener

class ListPanel extends Panel
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


    public ListPanel(HotListFrame hotlistframe)
    {
        frm = hotlistframe;
        setLayout(new BorderLayout());
        listPanel = new Panel();
        listPane = new ScrollPane();
        listPane.add(listPanel);
        add("Center", listPane);
        add("South", message = new Label(""));
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        listPanel.setBackground(hjbproperties.getColor("hotlistframe.bgcolor", null));
        listPane.setBackground(hjbproperties.getColor("hotjava.background", null));
    }

    public Dimension getPreferredSize()
    {
        int i = frm.getListImage().getHeight(this);
        return new Dimension(getSize().width, i * 12);
    }

    public HotListFrame getHotListFrame()
    {
        return frm;
    }

    public HotCanvas getCanvas()
    {
        return listCanvas;
    }

    public void setLabelName(String s)
    {
        message.setText(s);
    }

    public void initialize()
    {
        hotlist = frm.getHotList();
        String s = frm.getListName();
        int i = getSize().width;
        int j = getSize().height;
        listCanvas = new BrowserListCanvas(hotlist, listPanel, s, i, j, frm);
        java.awt.event.ComponentListener componentlistener = listCanvas.getListener();
        listPane.addComponentListener(componentlistener);
        listCanvas.setListElementImage(frm.getBookImage());
        listCanvas.setListContainerImages(frm.getListImage(), frm.getListImage());
        listCanvas.createInitialFolder();
        listCanvas.initialize(hotlist.getRegularFolder());
        invalidate();
        listPanel.setLayout(new BorderLayout());
        listPanel.add("West", listCanvas);
        listPanel.setSize(listCanvas.getPreferredSize());
        validate();
        MouseAdapter mouseadapter = new MouseAdapter() {

            public void mousePressed(MouseEvent mouseevent)
            {
                processListPanelMouseEvent(mouseevent);
            }

            public void mouseReleased(MouseEvent mouseevent)
            {
                processListPanelMouseEvent(mouseevent);
            }

        }
;
        addMouseListener(mouseadapter);
        MouseMotionAdapter mousemotionadapter = new MouseMotionAdapter() {

            public void mouseDragged(MouseEvent mouseevent)
            {
                processListPanelMouseEvent(mouseevent);
            }

        }
;
        addMouseMotionListener(mousemotionadapter);
        hotlist.addHotListListener(new HotListEventListener());
    }

    public ScrollPane getListPane()
    {
        return listPane;
    }

    private void redispatchEvent(MouseEvent mouseevent)
    {
        Point point = mouseevent.getComponent().getLocation();
        mouseevent.translatePoint(point.x, point.y);
        frm.dispatchEvent(mouseevent);
    }

    public void processListPanelMouseEvent(MouseEvent mouseevent)
    {
        switch(mouseevent.getID())
        {
        case 501: 
            ListItem listitem = listCanvas.getSelectedItem();
            if(listitem == null)
                return;
            if(!(listitem instanceof Folder))
                return;
            Folder folder = (Folder)listitem;
            if(mouseevent.getClickCount() == 2 && !frm.readOnlyList)
            {
                frm.renameContainer(folder, frm);
                return;
            } else
            {
                frm.displayList(folder);
                return;
            }

        case 502: 
            frm.setMessage("");
            redispatchEvent(mouseevent);
            return;

        case 506: 
            redispatchEvent(mouseevent);
            return;
        }
    }

    public boolean processHotListEvent(HotListEvent hotlistevent)
    {
        Object obj = hotlistevent.getArgument();
        HotListEntry hotlistentry = (HotListEntry)obj;
        switch(hotlistevent.getID())
        {
        default:
            break;

        case 53: // '5'
            listCanvas.addEntry(hotlistentry, true);
            break;

        case 54: // '6'
            Folder folder = (Folder)listCanvas.getSelectedItemFolder();
            boolean flag = folder.closed();
            listCanvas.delItem(hotlistentry, true);
            if(!flag)
            {
                HotCanvas hotcanvas = frm.getFolderPanel().getCanvas();
                hotcanvas.setMainContainer(null);
                hotcanvas.repaint();
            }
            break;

        case 52: // '4'
            listCanvas.changeFolderState(hotlistentry);
            break;
        }
        return true;
    }

    public void notify(int i, Object obj)
    {
    }

    Label message;
    Panel listPanel;
    ScrollPane listPane;
    HotListFrame frm;
    BrowserListCanvas listCanvas;
    HotList hotlist;
}
