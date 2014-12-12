// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotListFrame.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import sunw.hotjava.HJFrame;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.misc.URLCanonicalizer;

// Referenced classes of package sunw.hotjava.ui:
//            UserFrame, Bookmark, BookmarkDialog, BrowserListCanvas, 
//            DragAndDropEvent, DragStartInfo, DraggableContainer, EntrySeparator, 
//            Folder, FolderDialog, FolderPanel, HotCanvas, 
//            HotList, HotListBuffer, HotListEntry, ListCanvas, 
//            ListContainer, ListItem, ListItemComponent, ListPanel, 
//            PageFolder, PanelWithInsets, PlacesMessageLine, Separator, 
//            UserMenuBar, Webmark, DragAndDropListener

public class HotListFrame extends UserFrame
{
    protected class DnDListener
        implements DragAndDropListener
    {

        public void dragAndDropPerformed(DragAndDropEvent draganddropevent)
        {
            processDragAndDropEvent(draganddropevent);
        }

        protected DnDListener()
        {
        }
    }

    private final class ActionEventsListener
        implements ActionListener
    {

        public void actionPerformed(ActionEvent actionevent)
        {
            boolean flag = (actionevent.getModifiers() & 1) != 0;
            action(actionevent, actionevent.getActionCommand(), flag);
        }

        ActionEventsListener()
        {
        }
    }


    public static HotListFrame getHotListFrame(HJFrame hjframe)
    {
        if(frm == null)
        {
            frm = new HotListFrame(hjframe);
            HotList hotlist = HotList.getHotList();
            if(!hotlist.isFinishedImporting())
                hotlist.raiseImportPriority();
        }
        if(frm.iconified)
        {
            frm.dispose();
            frm = new HotListFrame(hjframe);
        }
        return frm;
    }

    public static HotListFrame getHotListFrame()
    {
        return frm;
    }

    public ActionListener getActionEventListener()
    {
        return actionEventsListener;
    }

    private HotListFrame(HJFrame hjframe)
    {
        super("hotlistframe");
        iconified = false;
        properties = HJBProperties.getHJBProperties("hjbrowser");
        owner = hjframe;
        Insets insets = insets();
        setSize(insets.left + insets.right + 150, insets.top + insets.bottom + 150);
        actionEventsListener = new ActionEventsListener();
        String s = properties.getProperty("hotlistframe.bookmarkImage");
        String s1 = properties.getProperty("hotlistframe.diropenImage");
        String s2 = properties.getProperty("hotlistframe.dirclosedImage");
        String s3 = properties.getProperty("hotlistframe.listImage");
        properties.getProperty("hotlistframe.listname");
        book_img = initImage(s);
        open_img = initImage(s1);
        closed_img = initImage(s2);
        list_img = initImage(s3);
        buffer = new HotListBuffer();
        initFrame();
        WindowAdapter windowadapter = new WindowAdapter() {

            public void windowIconified(WindowEvent windowevent)
            {
                setTitle(title);
                iconified = true;
            }

            public void windowDeiconified(WindowEvent windowevent)
            {
                setTitle(title);
                iconified = false;
            }

            public void windowClosing(WindowEvent windowevent)
            {
                closeFrame();
            }

            public void windowOpened(WindowEvent windowevent)
            {
                setBoundsForFrame();
            }

        }
;
        addWindowListener(windowadapter);
        MouseAdapter mouseadapter = new MouseAdapter() {

            public void mouseReleased(MouseEvent mouseevent)
            {
                processMouseEvent(mouseevent);
            }

        }
;
        addMouseListener(mouseadapter);
        MouseMotionAdapter mousemotionadapter = new MouseMotionAdapter() {

            public void mouseDragged(MouseEvent mouseevent)
            {
                processMouseEvent(mouseevent);
            }

        }
;
        addMouseMotionListener(mousemotionadapter);
    }

    public void initFrame()
    {
        setLayout(new BorderLayout());
        invalidate();
        title = properties.getProperty("hotlistframe.title");
        listPanel = new ListPanel(this);
        folderPanel = new FolderPanel(this);
        add("North", listPanel);
        add("Center", folderPanel);
        messageLine = new PlacesMessageLine();
        add("South", messageLine);
        setFrameMenuBar();
        setBackground(properties.getColor("hotjava.background", null));
        validate();
        pack();
    }

    public void setListName(String s)
    {
        listPanel.setLabelName(s);
    }

    public void setMessage(String s)
    {
        messageLine.setMessage(s);
    }

    public void setAdjustables(Graphics g, ScrollPane scrollpane)
    {
        FontMetrics fontmetrics = g.getFontMetrics();
        int i = fontmetrics.getAscent();
        Adjustable adjustable = scrollpane.getHAdjustable();
        if(adjustable != null)
            adjustable.setUnitIncrement(i);
        Adjustable adjustable1 = scrollpane.getVAdjustable();
        if(adjustable1 != null)
            adjustable1.setUnitIncrement(i);
    }

    public void initializePanels()
    {
        if(listPanel.getCanvas() == null || folderPanel.getCanvas() == null)
        {
            folderPanel.initialize();
            listPanel.initialize();
            DnDListener dndlistener = new DnDListener();
            HotCanvas hotcanvas = listPanel.getCanvas();
            hotcanvas.addDragAndDropListener(dndlistener);
            hotcanvas = folderPanel.getCanvas();
            hotcanvas.addDragAndDropListener(dndlistener);
        }
    }

    public void show()
    {
        super.show();
        Graphics g = getGraphics();
        if(g != null)
        {
            try
            {
                ScrollPane scrollpane = listPanel.getListPane();
                setAdjustables(g, scrollpane);
                scrollpane = folderPanel.getFolderPane();
                setAdjustables(g, scrollpane);
            }
            finally
            {
                g.dispose();
            }
            return;
        } else
        {
            return;
        }
    }

    public void openList(String s)
    {
        initializePanels();
        show();
        BrowserListCanvas browserlistcanvas = (BrowserListCanvas)listPanel.getCanvas();
        Folder folder = browserlistcanvas.getFolder(s);
        Folder folder1 = (Folder)browserlistcanvas.getSelectedItem();
        if(folder == null)
            return;
        browserlistcanvas.setSelectedItem(folder);
        try
        {
            if(folder1 != null)
            {
                folder1.toggleSelected();
                boolean flag = true;
                folder1.setContainerState(flag);
            }
            folder.toggleSelected();
            selectCanvas(browserlistcanvas);
            displayList(folder);
            storeSelection = folder;
            return;
        }
        catch(Exception exception)
        {
            System.out.println("Failed to postEvent: " + listPanel);
            exception.printStackTrace();
            return;
        }
    }

    public FolderPanel getFolderPanel()
    {
        return folderPanel;
    }

    public ListPanel getListPanel()
    {
        return listPanel;
    }

    public Panel getContainingPanelForCanvas(HotCanvas hotcanvas)
    {
        if(hotcanvas instanceof BrowserListCanvas)
            return getContainingListPanel(hotcanvas);
        else
            return getContainingFolderPanel(hotcanvas);
    }

    public ListPanel getContainingListPanel(HotCanvas hotcanvas)
    {
        for(Container container = hotcanvas.getParent(); container != null; container = container.getParent())
            if(container instanceof ListPanel)
                return (ListPanel)container;

        return null;
    }

    public FolderPanel getContainingFolderPanel(HotCanvas hotcanvas)
    {
        for(Container container = hotcanvas.getParent(); container != null; container = container.getParent())
            if(container instanceof FolderPanel)
                return (FolderPanel)container;

        return null;
    }

    private Image initImage(String s)
    {
        MediaTracker mediatracker = new MediaTracker(this);
        Image image = properties.getImage(s);
        mediatracker.addImage(image, 0);
        try
        {
            mediatracker.waitForID(0);
        }
        catch(Exception _ex)
        {
            image = null;
        }
        return image;
    }

    public Image getBookImage()
    {
        return book_img;
    }

    public Image getOpenImage()
    {
        return open_img;
    }

    public Image getClosedImage()
    {
        return closed_img;
    }

    public Image getListImage()
    {
        return list_img;
    }

    public HotList getHotList()
    {
        return HotList.getHotList();
    }

    public Window getOwner()
    {
        return owner;
    }

    public HotListBuffer getBuffer()
    {
        return buffer;
    }

    public String getListName()
    {
        return properties.getProperty("hotlistframe.listname");
    }

    protected void setFrameMenuBar()
    {
        hotlistframemenubar = new UserMenuBar("hotlistframemenubar", actionEventsListener);
        setMenuBar(hotlistframemenubar);
        enableCutCopyRename(false);
        updatePaste();
        updateUndo();
        enableSorting(false);
    }

    public void processDragAndDropEvent(DragAndDropEvent draganddropevent)
    {
        switch(draganddropevent.getID())
        {
        default:
            break;

        case 91960: 
            if(draggable == null)
            {
                DragStartInfo dragstartinfo = (DragStartInfo)draganddropevent.getArgument();
                draggable = new DraggableContainer(this, this, dragstartinfo);
                HotCanvas hotcanvas = getListPanel().getCanvas();
                DragAndDropListener draganddroplistener = hotcanvas.getDragAndDropListener();
                draggable.addDragAndDropListener(draganddroplistener);
                hotcanvas = getFolderPanel().getCanvas();
                draganddroplistener = hotcanvas.getDragAndDropListener();
                draggable.addDragAndDropListener(draganddroplistener);
            }
            break;
        }
    }

    public void processMouseEvent(MouseEvent mouseevent)
    {
        switch(mouseevent.getID())
        {
        default:
            break;

        case 502: 
            if(draggable != null)
            {
                draggable.processDragAndDropEvent(this, mouseevent);
                ListItemComponent listitemcomponent = (ListItemComponent)draggable.getDraggee();
                ListItem listitem = listitemcomponent.getBaseItem();
                draggable.setVisible(false);
                draggable.dispose();
                draggable = null;
                if(selectedCanvas != null && !selectedCanvas.itemMoved())
                    return;
                ListContainer listcontainer = listitem.getParentListContainer();
                Folder folder = (Folder)listcontainer.getParentListContainer();
                Folder folder1 = (Folder)listcontainer;
                boolean flag = folder1.closed();
                if(folder.isTopList() && !flag)
                {
                    folder1.setContainerState(!flag);
                    displayList(folder1);
                }
                ListItem listitem1 = selectedCanvas.getSelectedItem();
                if(listitem1 != null)
                    listitem1.toggleSelected();
                selectedCanvas.setSelectedItem(null);
                selectedCanvas.setCurrentItem(null);
                getHotList().exportHTMLFile();
                enableCutCopyRename(false);
                updatePaste();
                updateUndo();
                return;
            }
            break;

        case 506: 
            if(draggable != null)
            {
                draggable.processDragAndDropEvent(this, mouseevent);
                return;
            }
            break;
        }
    }

    public void notify(int i, Object obj)
    {
    }

    public boolean action(ActionEvent actionevent, String s, boolean flag)
    {
        actionevent.getActionCommand();
        if(s.equals("newlist"))
        {
            HotCanvas hotcanvas = listPanel.getCanvas();
            Folder folder = (Folder)hotcanvas.getMainContainer();
            String s5 = new String(".newlist");
            String s10 = new String(".listdialog.label");
            FolderDialog folderdialog = new FolderDialog("hotlistframe", s5, s10, this);
            String s15 = properties.getProperty("hotlistframe.default.newlist", "New List");
            s15 = getHotList().getUniqueName(s15, (PageFolder)folder.getHotListEntry());
            folderdialog.setDefaultText(s15);
            String s17 = folderdialog.getFolderName();
            if(s17 == null)
                return true;
            if(s17.equals(""))
                s17 = s15;
            getHotList().addFolder((PageFolder)folder.getHotListEntry(), s17, true, -5);
            getHotList().exportHTMLFile();
            repaint();
            return true;
        }
        if(s.equals("newfolder"))
        {
            HotCanvas hotcanvas1 = selectedCanvas;
            if(hotcanvas1 == null || hotcanvas1.getSelectedItemFolder() == null)
                hotcanvas1 = listPanel.getCanvas();
            if(hotcanvas1 == null)
                return true;
            Folder folder1 = (Folder)hotcanvas1.getSelectedItemFolder();
            String s6 = new String(".newfolder");
            String s11 = new String(".folderdialog.label");
            FolderDialog folderdialog1 = new FolderDialog("hotlistframe", s6, s11, this);
            String s16 = folderdialog1.getFolderName();
            if(s16 == null)
                return true;
            if(s16.equals(""))
                s16 = properties.getProperty("hotlistframe.newfolder.title");
            ListItem listitem9 = hotcanvas1.getSelectedItem();
            PageFolder pagefolder5 = (PageFolder)folder1.getHotListEntry();
            int j = -5;
            if(listitem9 != null)
            {
                Object obj4;
                if(listitem9 instanceof Bookmark)
                    obj4 = ((Bookmark)listitem9).getWebmark();
                else
                if(listitem9 instanceof Folder)
                    obj4 = ((Folder)listitem9).getPageFolder();
                else
                    obj4 = ((EntrySeparator)listitem9).getHotListEntry();
                j = pagefolder5.getItemPos(((HotListEntry) (obj4))) + 1;
                if(j == 0)
                    j = -5;
            }
            PageFolder pagefolder7 = (PageFolder)folder1.getHotListEntry();
            getHotList().addFolder(pagefolder7, s16, true, j);
            getHotList().sort(pagefolder7);
            getHotList().exportHTMLFile();
            HotCanvas hotcanvas5 = getFolderPanel().getCanvas();
            updateCanvas(hotcanvas5, folder1);
            return true;
        }
        if(s.equals("newplace"))
        {
            HotCanvas hotcanvas2 = selectedCanvas;
            if(hotcanvas2 == null || hotcanvas2.getSelectedItemFolder() == null)
                hotcanvas2 = listPanel.getCanvas();
            if(hotcanvas2 == null)
                return true;
            Folder folder2 = (Folder)hotcanvas2.getSelectedItemFolder();
            if(folder2 == null)
                return false;
            String s7 = new String(".newplace");
            String s12 = new String(".placedialog.URLlabel");
            String s14 = new String(".placedialog.titlelabel");
            BookmarkDialog bookmarkdialog = new BookmarkDialog("hotlistframe", s7, s12, s14, this);
            String s18 = bookmarkdialog.getPlaceName();
            setMessage(" ");
            if(s18 == null || s18.equals(""))
                return true;
            if(s18.startsWith("|"))
            {
                String s20 = properties.getProperty("hotlistframe.newplaceerr.msg");
                setMessage(s20);
                return true;
            }
            StringTokenizer stringtokenizer = new StringTokenizer(s18, "|", false);
            String s21 = null;
            String s22 = null;
            while(stringtokenizer.hasMoreTokens()) 
                if(s21 == null)
                    s21 = stringtokenizer.nextToken();
                else
                    s22 = stringtokenizer.nextToken();
            ListItem listitem10 = hotcanvas2.getSelectedItem();
            PageFolder pagefolder8 = (PageFolder)folder2.getHotListEntry();
            int k = -5;
            if(listitem10 != null)
            {
                Object obj5;
                if(listitem10 instanceof Bookmark)
                    obj5 = ((Bookmark)listitem10).getWebmark();
                else
                if(listitem10 instanceof Folder)
                    obj5 = ((Folder)listitem10).getPageFolder();
                else
                    obj5 = ((EntrySeparator)listitem10).getHotListEntry();
                k = pagefolder8.getItemPos(((HotListEntry) (obj5))) + 1;
                if(k == 0)
                    k = -5;
            }
            try
            {
                URLCanonicalizer urlcanonicalizer = new URLCanonicalizer();
                s21 = urlcanonicalizer.canonicalize(s21);
                URL url = new URL(null, s21);
                getHotList().addItemToFolder(url.toString(), s22, pagefolder8, k);
                getHotList().sort(pagefolder8);
                getHotList().exportHTMLFile();
                HotCanvas hotcanvas6 = getFolderPanel().getCanvas();
                updateCanvas(hotcanvas6, folder2);
            }
            catch(MalformedURLException malformedurlexception)
            {
                malformedurlexception.printStackTrace();
            }
            return true;
        }
        if(s.equals("cut") || s.equals("delete"))
        {
            ListItem listitem = selectedCanvas.getSelectedItem();
            HotListEntry hotlistentry = null;
            buffer.saveInBuffer(listitem, s);
            if(listitem instanceof Folder)
            {
                Folder folder4 = (Folder)listitem;
                hotlistentry = folder4.getHotListEntry();
            }
            if(listitem instanceof Bookmark)
            {
                Bookmark bookmark = (Bookmark)listitem;
                hotlistentry = bookmark.getHotListEntry();
            }
            if(listitem instanceof EntrySeparator)
            {
                EntrySeparator entryseparator = (EntrySeparator)listitem;
                hotlistentry = entryseparator.getHotListEntry();
            }
            if(listitem != null)
            {
                getHotList().deleteGotoItem(hotlistentry);
                getHotList().exportHTMLFile();
            }
            listitem = selectedCanvas.getSelectedItem();
            if(listitem == null)
                enableCutCopyRename(false);
            updatePaste();
            updateUndo();
            enableReverseSort(false);
            return true;
        }
        if(s.equals("copy"))
        {
            ListItem listitem1 = selectedCanvas.getSelectedItem();
            buffer.saveInBuffer(listitem1, "copy");
            updatePaste();
            updateUndo();
            getHotList().exportHTMLFile();
            return true;
        }
        if(s.equals("paste"))
        {
            ListItem listitem2 = selectedCanvas.getSelectedItem();
            String s2 = properties.getProperty("hotlistframe.pasteerr.msg");
            if(listitem2 == null)
            {
                setMessage(s2);
                return true;
            }
            if(!(listitem2 instanceof Folder))
            {
                setMessage(s2);
                return true;
            }
            Folder folder5 = (Folder)listitem2;
            PageFolder pagefolder = (PageFolder)folder5.getHotListEntry();
            HotListEntry hotlistentry4 = buffer.getEntryFromBuffer();
            ListItem listitem8 = buffer.getListItemFromBuffer();
            Object obj3 = null;
            boolean flag2 = true;
            if(listitem8 instanceof Folder)
            {
                Folder folder9 = (Folder)listitem8;
                flag2 = folder9.closed();
            }
            HotCanvas hotcanvas4 = getFolderPanel().getCanvas();
            if(hotlistentry4 instanceof PageFolder)
            {
                boolean flag3 = getHotList().pasteFolder(pagefolder, (PageFolder)hotlistentry4, flag2, 0);
                if(!flag3)
                    return true;
                updateCanvas(hotcanvas4, folder5);
            }
            if(hotlistentry4 instanceof Webmark)
            {
                boolean flag4 = getHotList().pasteWebmark(pagefolder, (Webmark)hotlistentry4, 0);
                if(!flag4)
                    return true;
            }
            if(hotlistentry4 instanceof Separator)
                getHotList().addSeparatorItem(pagefolder, 0);
            buffer.saveInBuffer(listitem8, "paste");
            updateUndo();
            getHotList().exportHTMLFile();
            enableReverseSort(false);
            return true;
        }
        if(s.equals("undo"))
        {
            buffer.disableUndo();
            updateUndo();
            String s1 = buffer.getOperationFromBuffer();
            if(s1.equals("copy"))
            {
                buffer.clearBuffer();
                updatePaste();
                getHotList().exportHTMLFile();
            }
            if(s1.equals("paste"))
            {
                ListItem listitem4 = selectedCanvas.getSelectedItem();
                if(listitem4 == null)
                {
                    String s8 = properties.getProperty("hotlistframe.pasteundoerr");
                    setMessage(s8);
                    return true;
                }
                if(!(listitem4 instanceof Folder))
                {
                    String s9 = properties.getProperty("hotlistframe.pasteundoerr");
                    setMessage(s9);
                    return true;
                }
                Folder folder6 = (Folder)listitem4;
                PageFolder pagefolder1 = (PageFolder)folder6.getHotListEntry();
                HotListEntry hotlistentry5 = buffer.getEntryFromBuffer();
                HotListEntry hotlistentry6 = getHotList().getEntry(pagefolder1, hotlistentry5);
                getHotList().deleteGotoItem(hotlistentry6);
                getHotList().exportHTMLFile();
                enableReverseSort(false);
            }
            if(s1.equals("cut") || s1.equals("delete"))
            {
                HotListEntry hotlistentry1 = buffer.getEntryFromBuffer();
                ListItem listitem6 = buffer.getListItemFromBuffer();
                buffer.clearBuffer();
                updatePaste();
                if(listitem6 == null)
                    return true;
                Object obj1 = null;
                boolean flag1 = true;
                if(listitem6 instanceof Folder)
                {
                    Folder folder7 = (Folder)listitem6;
                    flag1 = folder7.closed();
                }
                getFolderPanel().getCanvas();
                PageFolder pagefolder4 = hotlistentry1.getParent();
                if(hotlistentry1 instanceof PageFolder)
                {
                    getHotList().pasteFolder(pagefolder4, (PageFolder)hotlistentry1, flag1, 0);
                    getHotList().sort(pagefolder4);
                    reInitialize(pagefolder4, listitem6);
                }
                if(hotlistentry1 instanceof Webmark)
                {
                    getHotList().pasteWebmark(pagefolder4, (Webmark)hotlistentry1, 0);
                    getHotList().sort(pagefolder4.getSortType(), pagefolder4);
                    reInitialize(pagefolder4, listitem6);
                }
                if(hotlistentry1 instanceof Separator)
                {
                    getHotList().addSeparatorItem(pagefolder4, 0);
                    reInitialize(pagefolder4, listitem6);
                }
                getHotList().exportHTMLFile();
                enableReverseSort(false);
            }
            frm.setMessage(" ");
            return true;
        }
        if(s.equals("rename"))
        {
            ListItem listitem3 = selectedCanvas.getSelectedItem();
            if(listitem3 == null)
            {
                String s3 = properties.getProperty("hotlistframe.noitemselected.msg");
                setMessage(s3);
                return true;
            }
            if(listitem3 instanceof Bookmark)
                renameBookmark((Bookmark)listitem3, this);
            else
                renameContainer((Folder)listitem3, this);
            enableReverseSort(false);
            return true;
        }
        if(s.equals("newseparator"))
        {
            HotCanvas hotcanvas3 = selectedCanvas;
            if(hotcanvas3 == null || hotcanvas3.getSelectedItem() == null)
            {
                String s4 = properties.getProperty("hotlistframe.noselection.msg");
                setMessage(s4);
                return true;
            }
            Folder folder3 = (Folder)hotcanvas3.getSelectedItemFolder();
            ListItem listitem7 = hotcanvas3.getSelectedItem();
            PageFolder pagefolder2 = (PageFolder)folder3.getHotListEntry();
            Object obj2;
            if(listitem7 instanceof Bookmark)
                obj2 = ((Bookmark)listitem7).getWebmark();
            else
            if(listitem7 instanceof Folder)
                obj2 = ((Folder)listitem7).getPageFolder();
            else
                obj2 = ((EntrySeparator)listitem7).getHotListEntry();
            int i = pagefolder2.getItemPos(((HotListEntry) (obj2))) + 1;
            if(i == 0)
                i = -5;
            getHotList().addSeparatorItem(pagefolder2, i);
            getHotList().exportHTMLFile();
            enableReverseSort(false);
            return true;
        }
        if(s.equals("import"))
        {
            HotList hotlist = getHotList();
            hotlist.importList(this);
            if(!hotlist.isFinishedImporting())
                hotlist.raiseImportPriority();
            return true;
        }
        if(s.equals("export"))
        {
            HotList hotlist1 = HotList.getHotList();
            if(selectedCanvas != null)
            {
                ListItem listitem5 = selectedCanvas.getSelectedItem();
                Object obj = null;
                String s13 = properties.getProperty("hotlistframe.exportList");
                PageFolder pagefolder3 = new PageFolder(null, s13, true);
                if(listitem5 instanceof Folder)
                {
                    Folder folder8 = (Folder)listitem5;
                    HotListEntry hotlistentry2 = folder8.getHotListEntry();
                    pagefolder3.addElement(hotlistentry2);
                }
                if(listitem5 instanceof Bookmark)
                {
                    Bookmark bookmark1 = (Bookmark)listitem5;
                    HotListEntry hotlistentry3 = bookmark1.getHotListEntry();
                    String s19 = properties.getProperty("hotlistframe.exportItem");
                    PageFolder pagefolder6 = new PageFolder(pagefolder3, s19, true);
                    pagefolder6.addElement(hotlistentry3);
                    pagefolder3.addElement(pagefolder6);
                }
                hotlist1.exportList(this, pagefolder3);
            } else
            {
                hotlist1.exportList(this, null);
            }
        }
        if(s.equals("atoz"))
        {
            sort(70);
            return true;
        }
        if(s.equals("date"))
        {
            sort(71);
            return true;
        }
        if(s.equals("frequent"))
        {
            sort(72);
            return true;
        }
        if(s.equals("reverse"))
        {
            sort(73);
            return true;
        }
        if(s.equals("closewin"))
        {
            closeFrame();
            return true;
        }
        if(s.equals("quit"))
        {
            if(!owner.confirmExit(true))
            {
                return true;
            } else
            {
                closeFrame();
                owner.saveStateToProperties();
                properties.save();
                HJWindowManager.getHJWindowManager().quit();
                return true;
            }
        } else
        {
            return false;
        }
    }

    public void updateCanvas(Component component, Folder folder)
    {
        PageFolder pagefolder = (PageFolder)folder.getHotListEntry();
        HotCanvas hotcanvas = (HotCanvas)component;
        if(pagefolder.getGrandParent() == null)
        {
            hotcanvas.createInitialFolder(folder);
            boolean flag = folder.closed();
            if(!flag)
                hotcanvas.initialize(pagefolder);
            else
                hotcanvas.setMainContainer(null);
        } else
        {
            for(; pagefolder.getGrandParent() != null; pagefolder = pagefolder.getParent());
            HotCanvas hotcanvas1 = getListPanel().getCanvas();
            hotcanvas.createInitialFolder((Folder)hotcanvas1.getMainContainer());
            hotcanvas.initialize(pagefolder);
        }
        hotcanvas.repaint();
    }

    public void selectCanvas(HotCanvas hotcanvas)
    {
        ListItem listitem = hotcanvas.getSelectedItem();
        if(selectedCanvas == null)
            selectedCanvas = hotcanvas;
        ListItem listitem1 = selectedCanvas.getSelectedItem();
        if(selectedCanvas != hotcanvas)
        {
            if(listitem1 != null)
            {
                listitem1.toggleSelected();
                if(selectedCanvas instanceof BrowserListCanvas)
                    storeSelection = ((BrowserListCanvas)selectedCanvas).getSelectedItem();
                selectedCanvas.setSelectedItem(null);
                selectedCanvas.setCurrentItem(null);
                selectedCanvas.repaint();
            }
            selectedCanvas = hotcanvas;
        }
        if(hotcanvas instanceof BrowserListCanvas)
        {
            String s = properties.getProperty("hotlist.netscape");
            if(listitem != null)
                readOnlyList = listitem.text.equals(s);
            ListItem listitem3 = ((BrowserListCanvas)selectedCanvas).previousSelection();
            ListItem listitem2 = selectedCanvas.getSelectedItem();
            if(listitem3 == null)
                listitem3 = storeSelection;
            if(listitem2 != listitem3 && (listitem3 instanceof Folder))
            {
                Folder folder = (Folder)listitem3;
                if(folder != null)
                {
                    boolean flag = folder.closed();
                    if(!flag)
                        folder.setContainerState(!flag);
                }
            }
        }
        setMenuItemStates(listitem);
    }

    public void setMenuItemStates(ListItem listitem)
    {
        if(listitem != null)
        {
            updateEditing();
            enableSorting(true);
            enableRename(true);
            if(listitem instanceof Folder)
            {
                PageFolder pagefolder = ((Folder)listitem).getPageFolder();
                if(pagefolder.getSortType() == 0)
                {
                    enableReverseSort(false);
                    return;
                }
            } else
            {
                if(listitem instanceof Bookmark)
                {
                    enableSorting(false);
                    return;
                }
                if(listitem instanceof EntrySeparator)
                {
                    enableSorting(false);
                    enableRename(false);
                }
            }
            return;
        } else
        {
            enableSorting(false);
            return;
        }
    }

    public void sort(int i)
    {
        if(selectedCanvas == null)
            return;
        ListItem listitem = selectedCanvas.getSelectedItem();
        if(listitem == null)
        {
            String s = properties.getProperty("hotlistframe.sorterr.msg");
            setMessage(s);
            return;
        }
        if(!(listitem instanceof Folder))
        {
            String s1 = properties.getProperty("hotlistframe.sorterr.msg");
            setMessage(s1);
            return;
        } else
        {
            Folder folder = (Folder)listitem;
            PageFolder pagefolder = folder.getPageFolder();
            HotList.getHotList().sort(i, pagefolder);
            HotCanvas hotcanvas = getFolderPanel().getCanvas();
            updateCanvas(hotcanvas, folder);
            enableReverseSort(true);
            getHotList().exportHTMLFile();
            return;
        }
    }

    public boolean displayList(Folder folder)
    {
        HotCanvas hotcanvas = frm.getFolderPanel().getCanvas();
        BrowserListCanvas browserlistcanvas = (BrowserListCanvas)frm.getListPanel().getCanvas();
        boolean flag = folder.closed();
        if(!flag)
        {
            browserlistcanvas.repaint();
            return true;
        }
        folder.setContainerState(!flag);
        ScrollPane scrollpane = frm.getFolderPanel().getFolderPane();
        scrollpane.setScrollPosition(0, 0);
        browserlistcanvas.repaint();
        PageFolder pagefolder = folder.getPageFolder();
        hotcanvas.createInitialFolder(folder);
        flag = folder.closed();
        if(!flag)
            hotcanvas.initialize(pagefolder);
        else
            hotcanvas.setMainContainer(null);
        hotcanvas.repaint();
        scrollpane.doLayout();
        setListName(folder.getTitle());
        return true;
    }

    public void renameContainer(Folder folder, Frame frame)
    {
        String s = null;
        if(folder != null)
            if(selectedCanvas instanceof BrowserListCanvas)
            {
                String s1 = new String(".renamelist");
                String s3 = new String(".renamelistdialog.label");
                FolderDialog folderdialog = new FolderDialog("hotlistframe", s1, s3, this);
                folderdialog.setDefaultText(folder.getTitle());
                s = folderdialog.getFolderName();
            } else
            {
                String s2 = new String(".renamefolder");
                String s4 = new String(".renamefolderdialog.label");
                FolderDialog folderdialog1 = new FolderDialog("hotlistframe", s2, s4, this);
                folderdialog1.setDefaultText(folder.getTitle());
                s = folderdialog1.getFolderName();
            }
        if(s == null || s.equals(""))
            return;
        folder.setTitle(s);
        selectedCanvas.repaint();
        PageFolder pagefolder = (PageFolder)folder.getHotListEntry();
        if(selectedCanvas instanceof BrowserListCanvas)
            setListName(s);
        getHotList().renameInMenus(pagefolder, s, null);
        pagefolder.setTitle(s);
        getHotList().exportHTMLFile();
    }

    public void renameBookmark(Bookmark bookmark, Frame frame)
    {
        Object obj = null;
        if(bookmark != null)
        {
            Webmark webmark = (Webmark)bookmark.getHotListEntry();
            String s1 = new String(".renameplace");
            String s2 = new String(".renameplacedialog.URLlabel");
            String s3 = new String(".renameplacedialog.titlelabel");
            BookmarkDialog bookmarkdialog = new BookmarkDialog("hotlistframe", s1, s2, s3, this);
            bookmarkdialog.setDefaultURL(webmark.getURL());
            bookmarkdialog.setDefaultTitle(bookmark.getTitle());
            String s = bookmarkdialog.getPlaceName();
            if(s == null || s.equals(""))
                return;
            StringTokenizer stringtokenizer = new StringTokenizer(s, "|", false);
            String s4 = null;
            String s5 = null;
            while(stringtokenizer.hasMoreTokens()) 
                if(s4 == null)
                    s4 = stringtokenizer.nextToken();
                else
                    s5 = stringtokenizer.nextToken();
            try
            {
                URLCanonicalizer urlcanonicalizer = new URLCanonicalizer();
                s4 = urlcanonicalizer.canonicalize(s4);
                URL url = new URL(null, s4);
                bookmark.setTitle(s5);
                selectedCanvas.repaint();
                String s6 = url.toExternalForm();
                frm.setMessage(s6);
                getHotList().renameInMenus(webmark, s5, s6);
                webmark.setURL(s6);
                webmark.setTitle(s5);
                getHotList().exportHTMLFile();
                return;
            }
            catch(MalformedURLException malformedurlexception)
            {
                malformedurlexception.printStackTrace();
            }
            return;
        } else
        {
            return;
        }
    }

    public static boolean frameVisible()
    {
        return frm != null;
    }

    public static boolean isIconified()
    {
        return frm.iconified;
    }

    public void reInitialize(PageFolder pagefolder, ListItem listitem)
    {
        HotCanvas hotcanvas = getFolderPanel().getCanvas();
        if(pagefolder.getGrandParent() == null)
        {
            Folder folder = (Folder)listitem.getParentListContainer();
            hotcanvas.createInitialFolder(folder);
            boolean flag = folder.closed();
            if(!flag)
                hotcanvas.initialize(pagefolder);
            else
                hotcanvas.setMainContainer(null);
        } else
        {
            for(; pagefolder.getGrandParent() != null; pagefolder = pagefolder.getParent());
            HotCanvas hotcanvas1 = getListPanel().getCanvas();
            hotcanvas.createInitialFolder((Folder)hotcanvas1.getMainContainer());
            hotcanvas.initialize(pagefolder);
        }
        hotcanvas.repaint();
    }

    public void saveStateToProperties()
    {
        if(isShowing())
        {
            Point point = getLocation();
            Dimension dimension = getToolkit().getScreenSize();
            if(point.x > dimension.width || point.y > dimension.height)
                return;
            if(point.x < 0 || point.y < 0)
                return;
            Dimension dimension1 = getSize();
            if(properties != null)
            {
                properties.put("hotlistframe.width", String.valueOf(dimension1.width));
                properties.put("hotlistframe.height", String.valueOf(dimension1.height));
                properties.put("hotlistframe.x", String.valueOf(point.x));
                properties.put("hotlistframe.y", String.valueOf(point.y));
                properties.save();
            }
        }
    }

    public void closeFrame()
    {
        HotList.getHotList().exportHTMLFile();
        saveStateToProperties();
        dispose();
        frm = null;
    }

    public void setBoundsForFrame()
    {
        Dimension dimension = getSize();
        dimension.width = Integer.parseInt(properties.getProperty("hotlistframe.width"));
        dimension.height = Integer.parseInt(properties.getProperty("hotlistframe.height"));
        int i = Integer.parseInt(properties.getProperty("hotlistframe.x"));
        int j = Integer.parseInt(properties.getProperty("hotlistframe.y"));
        setBounds(i, j, dimension.width, dimension.height);
    }

    private void enableItem(String s, boolean flag)
    {
        MenuItem menuitem = hotlistframemenubar.getMenuItem(s);
        if(menuitem != null)
            menuitem.setEnabled(flag);
    }

    public void enableCutCopyRename(boolean flag)
    {
        enableItem("delete", !readOnlyList && flag);
        enableItem("cut", !readOnlyList && flag);
        enableItem("rename", !readOnlyList && flag);
        enableItem("copy", flag);
    }

    public void updatePaste()
    {
        enableItem("paste", !readOnlyList && buffer.canPaste());
    }

    public void updateUndo()
    {
        enableItem("undo", !readOnlyList && buffer.canUndo());
    }

    public void enableReverseSort(boolean flag)
    {
        enableItem("reverse", flag);
    }

    public void enableRename(boolean flag)
    {
        enableItem("rename", !readOnlyList && flag);
    }

    public void updateEditing()
    {
        updatePaste();
        updateUndo();
        enableCutCopyRename(true);
        enableItem("newfolder", !readOnlyList);
        enableItem("newplace", !readOnlyList);
        enableItem("newseparator", !readOnlyList);
    }

    public void enableSorting(boolean flag)
    {
        enableItem("atoz", flag);
        enableItem("date", flag);
        enableItem("frequent", flag);
        enableItem("reverse", flag);
    }

    private static HotListFrame frm = null;
    static final String propName = "hotlistframe";
    private HJBProperties properties;
    Panel topPanel;
    HJFrame owner;
    ListPanel listPanel;
    FolderPanel folderPanel;
    HotCanvas selectedCanvas;
    PlacesMessageLine messageLine;
    UserMenuBar hotlistframemenubar;
    ActionListener actionEventsListener;
    String title;
    Image book_img;
    Image open_img;
    Image closed_img;
    Image list_img;
    ListItem storeSelection;
    private DraggableContainer draggable;
    boolean readOnlyList;
    HotListBuffer buffer;
    boolean iconified;

}
