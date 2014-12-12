// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotList.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.*;
import sun.io.ByteToCharConverter;
import sunw.hotjava.HJFrame;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.misc.*;
import sunw.html.DTD;
import sunw.html.Parser;

// Referenced classes of package sunw.hotjava.ui:
//            AlphabeticalCompare, ConfirmDialog, DateVisitedCompare, HotListEntry, 
//            HotListEvent, HotListEventMulticaster, HotListFrame, HotListHTMLParser, 
//            HotListListener, HotListReader, HotListWriter, PageFolder, 
//            Separator, UserFileDialog, UserMenu, UserMenuItem, 
//            VisitFrequencyCompare, Webmark

public class HotList
{

    public synchronized void addHotListListener(HotListListener hotlistlistener)
    {
        listeners = HotListEventMulticaster.add(listeners, hotlistlistener);
    }

    public synchronized void removeHotListListener(HotListListener hotlistlistener)
    {
        listeners = HotListEventMulticaster.remove(listeners, hotlistlistener);
    }

    protected void dispatchHotListEvent(int i, Object obj)
    {
        if(listeners != null)
        {
            HotListEvent hotlistevent = new HotListEvent(this, i, obj);
            listeners.hotlistChanged(hotlistevent);
        }
    }

    void dbg(String s)
    {
    }

    public static HotList getHotList()
    {
        if(hotList == null)
            hotList = new HotList();
        return hotList;
    }

    private HotList()
    {
        importingOurType = false;
        menusOn = false;
        properties = HJBProperties.getHJBProperties("hjbrowser");
        beanProps = HJBProperties.getHJBProperties("beanPropertiesKey");
        untitledTitle = properties.getProperty("hotlist.untitled");
        newPlaces = properties.getProperty("hotlist.newplaces");
        coolPlaces = properties.getProperty("hotlist.coolplaces");
        menuPlaces = properties.getProperty("hotlist.menuplaces", "");
        hotListsMenu = properties.getProperty("hotlist.lists.menu");
        rememberMenu = properties.getProperty("hotlist.remember.menu");
        boolean flag = false;
        writer = new HotListWriter(this);
        threadReader = new HotListReader(this);
        hotlistName = properties.getProperty("hotlist.listname");
        listsMenu = new UserMenu(hotListsMenu, null);
        rememberToMenu = new UserMenu(rememberMenu, null);
        regularFolder = new PageFolder(null, hotlistName, true);
        regularFolder.setUserMenu(listsMenu);
        currentRegularFolder = regularFolder;
        folderStack = new Stack();
        String s = beanProps.getProperty("dtd");
        try
        {
            dtd = DTD.getDTD(s);
        }
        catch(IOException _ex)
        {
            throw new Error("could not get default dtd: " + s);
        }
        dtd = createDTD(dtd, s);
        String s1 = properties.getProperty("user.home");
        if(s1 != null && !s1.endsWith(File.separator))
            s1 = s1 + File.separator;
        exportFile = s1 + ".hotjava" + File.separator + "hotlist.html";
        importFile = exportFile;
        try
        {
            File file = new File(importFile);
            flag = file.exists();
        }
        catch(Exception _ex) { }
        if(!flag)
        {
            addGotoFolder(newPlaces, true, -5);
            addGotoFolder(coolPlaces, true, -5);
            if(!menuPlaces.equals(""))
                addGotoFolder(menuPlaces, true, -5);
            PageFolder pagefolder = regularFolder.getChildFolder(coolPlaces);
            PageFolder pagefolder1 = currentRegularFolder;
            currentRegularFolder = pagefolder;
            addPlaces(properties.getProperty("hotlist.coolplaceslist"));
            if(!menuPlaces.equals(""))
            {
                PageFolder pagefolder2 = regularFolder.getChildFolder(menuPlaces);
                currentRegularFolder = pagefolder2;
            }
            addPlaces(properties.getProperty("hotlist.menuplaceslist", ""));
            currentRegularFolder = pagefolder1;
        }
        String s2 = "";
        try
        {
            s2 = s1 + ".netscape" + File.separator + "bookmarks.html";
            File file1 = new File(s2);
            boolean flag1 = file1.exists();
            if(!flag1)
            {
                s2 = s1 + ".netscape" + File.separator + "bookmarks.htm";
                File file2 = new File(s2);
                boolean flag2 = file2.exists();
            }
        }
        catch(Exception _ex) { }
        importHTMLFile(importFile, true, s2);
    }

    public long getFileTimeStamp()
    {
        return fileTimeStamp;
    }

    public void setFileTimeStamp(long l)
    {
        fileTimeStamp = l;
    }

    public boolean processDoctype(String s)
    {
        boolean flag = true;
        doctype = s;
        String s1 = properties.getProperty("hotlist.netscapeVersion");
        if(doctype.toLowerCase().indexOf(s1) != -1)
        {
            String s2 = properties.getProperty("hotlist.netscape");
            s2 = getUniqueName(s2);
            addGotoFolder(s2, true, -5);
            importingOurType = false;
            folderPush();
            flag = false;
        } else
        if(doctype.toLowerCase().indexOf("hotjava-hotlist-version-2") != -1)
            flag = false;
        return flag;
    }

    public boolean importOurType()
    {
        return importingOurType;
    }

    public String getHotListName()
    {
        return hotlistName;
    }

    public boolean canHandleHTML()
    {
        return dtd != null;
    }

    String normalizeTitle(String s, String s1)
    {
        if(s1 == null || s1.equals(""))
            s1 = untitledTitle + "(" + s + ")";
        return s1;
    }

    public void addCurrent(URL url, String s, String s1)
    {
        addCurrentItem(url.toString(), s, s1);
    }

    public synchronized void addItemToFolder(String s, String s1, PageFolder pagefolder, int i)
    {
        s1 = normalizeTitle(s, s1);
        String s2 = properties.getPropertyReplace("hotlist.addtogo.msg", s1, ((HotListEntry) (pagefolder)).title);
        Vector vector = pagefolder.getContents();
        int j = vector.size();
        for(int k = 0; k < j; k++)
        {
            HotListEntry hotlistentry = (HotListEntry)vector.elementAt(k);
            if(hotlistentry.url.equals(s))
            {
                s2 = properties.getPropertyReplace("hotlist.entryexists.msg", s1, ((HotListEntry) (pagefolder)).title);
                HotListFrame hotlistframe = HotListFrame.getHotListFrame();
                if(HotListFrame.frameVisible() && !HotListFrame.isIconified())
                {
                    hotlistframe.setMessage(s2);
                    return;
                } else
                {
                    HJFrame hjframe = HJWindowManager.getHJWindowManager().getLastFocusHolder();
                    hjframe.showStatus(s2);
                    return;
                }
            }
        }

        int l = (int)(System.currentTimeMillis() / 1000L);
        Webmark webmark = new Webmark(pagefolder, s, s1, 0, l);
        if(i == -5)
            pagefolder.addElement(webmark);
        else
            pagefolder.addElement(webmark, i);
        addToMenus(webmark, -5);
        dispatchHotListEvent(50, webmark);
        HotListFrame hotlistframe1 = HotListFrame.getHotListFrame();
        if(HotListFrame.frameVisible() && !HotListFrame.isIconified())
        {
            hotlistframe1.setMessage(s2);
            return;
        } else
        {
            HJFrame hjframe1 = HJWindowManager.getHJWindowManager().getLastFocusHolder();
            hjframe1.showStatus(s2);
            return;
        }
    }

    public synchronized void addCurrentItem(String s, String s1, String s2)
    {
        PageFolder pagefolder;
        if(s2 == null || s2.startsWith("addgoto"))
        {
            pagefolder = regularFolder.getChildFolder(newPlaces);
            if(pagefolder == null)
            {
                PageFolder pagefolder1 = currentRegularFolder;
                currentRegularFolder = regularFolder;
                addGotoFolder(newPlaces, true, 0);
                currentRegularFolder = pagefolder1;
                pagefolder = regularFolder.getChildFolder(newPlaces);
            }
        } else
        {
            pagefolder = regularFolder.getChildFolder(s2);
            if(pagefolder == null)
                return;
        }
        addItemToFolder(s, s1, pagefolder, -5);
    }

    public boolean addFolder(PageFolder pagefolder, String s, boolean flag, int i)
    {
        currentRegularFolder = pagefolder;
        boolean flag1 = addGotoFolder(s, flag, i);
        if(flag1)
        {
            String s1 = properties.getPropertyReplace("hotlist.addtogo.msg", s, ((HotListEntry) (currentRegularFolder)).title);
            if(pagefolder.getParent() == null)
                s1 = properties.getProperty("hotlist.added.list.msg", "Added new list") + " " + s;
            HotListFrame hotlistframe = HotListFrame.getHotListFrame();
            if(hotlistframe != null)
                hotlistframe.setMessage(s1);
        }
        currentRegularFolder = regularFolder;
        return flag1;
    }

    public synchronized boolean addGotoFolder(String s, boolean flag, int i)
    {
        return addGotoFolder(s, flag, 0, 0, 0, "false", i);
    }

    public synchronized boolean addGotoFolder(String s, boolean flag, int i, int j, int k, String s1, int l)
    {
        Object obj = null;
        String s2 = properties.getPropertyReplace("hotlist.addtogo.msg", s, ((HotListEntry) (currentRegularFolder)).title);
        PageFolder pagefolder = ((HotListEntry) (currentRegularFolder)).parent;
        Vector vector = currentRegularFolder.getContents();
        int i1 = vector.size();
        for(int j1 = 0; j1 < i1; j1++)
        {
            HotListEntry hotlistentry = (HotListEntry)vector.elementAt(j1);
            if(hotlistentry.title.equals(s))
            {
                if(pagefolder != null)
                    s2 = properties.getPropertyReplace("hotlist.entryexists.msg", s, ((HotListEntry) (currentRegularFolder)).title);
                else
                    s2 = properties.getPropertyReplace("hotlist.listexists.msg", s);
                HotListFrame hotlistframe = HotListFrame.getHotListFrame();
                if(hotlistframe != null)
                    hotlistframe.setMessage(s2);
                return false;
            }
        }

        PageFolder pagefolder1 = currentRegularFolder.getParent();
        s2 = properties.getPropertyReplace("hotlist.addtogo.msg", s, ((HotListEntry) (currentRegularFolder)).title);
        if(pagefolder1 == null)
            s2 = properties.getProperty("hotlist.added.list.msg", "Added list") + " " + s;
        HotListFrame hotlistframe1 = HotListFrame.getHotListFrame();
        if(hotlistframe1 != null)
            hotlistframe1.setMessage(s2);
        PageFolder pagefolder2 = new PageFolder(currentRegularFolder, s, pagefolder != null ? flag : true, i, j, k, s1);
        if(l == -5)
            currentRegularFolder.addElement(pagefolder2);
        else
            currentRegularFolder.addElement(pagefolder2, l);
        addToMenus(pagefolder2, l);
        if(pagefolder == null)
        {
            dispatchHotListEvent(53, pagefolder2);
        } else
        {
            propagateVisitCountAndLastVisit(pagefolder2, i, j);
            dispatchHotListEvent(50, pagefolder2);
        }
        return true;
    }

    public void folderPop()
    {
        if(folderStack.empty())
        {
            currentRegularFolder = regularFolder;
            return;
        }
        folderStack.pop();
        if(folderStack.empty())
        {
            currentRegularFolder = regularFolder;
            return;
        } else
        {
            currentRegularFolder = (PageFolder)folderStack.peek();
            return;
        }
    }

    public void folderPush()
    {
        PageFolder pagefolder = currentRegularFolder.getLastAddedPageFolder();
        if(pagefolder != null)
        {
            folderStack.push(pagefolder);
            currentRegularFolder = pagefolder;
            return;
        } else
        {
            folderStack.push(currentRegularFolder);
            return;
        }
    }

    public synchronized boolean pasteFolder(PageFolder pagefolder, PageFolder pagefolder1, boolean flag, int i)
    {
        PageFolder pagefolder2 = (PageFolder)pagefolder1.clone();
        boolean flag1 = addFolder(pagefolder, pagefolder2.getTitle(), flag, i);
        if(!flag1)
            return flag1;
        Vector vector = pagefolder.getContents();
        if(vector == null)
            return flag1;
        String s = ((HotListEntry) (pagefolder2)).title;
        PageFolder pagefolder3 = null;
        for(int j = 0; j < vector.size(); j++)
        {
            HotListEntry hotlistentry = (HotListEntry)vector.elementAt(j);
            if(!hotlistentry.title.equals(s) || !(hotlistentry instanceof PageFolder))
                continue;
            pagefolder3 = (PageFolder)hotlistentry;
            break;
        }

        vector = pagefolder2.getContents();
        if(vector == null)
            return flag1;
        for(int k = 0; k < vector.size(); k++)
        {
            HotListEntry hotlistentry1 = (HotListEntry)vector.elementAt(k);
            if(hotlistentry1 instanceof PageFolder)
            {
                pasteFolder(pagefolder3, (PageFolder)hotlistentry1, ((PageFolder)hotlistentry1).closed(), k);
            } else
            {
                pagefolder3.addElement(hotlistentry1);
                hotlistentry1.setParentListContainer(pagefolder3);
                addToMenus(hotlistentry1, -5);
            }
        }

        printFolder(pagefolder);
        return flag1;
    }

    public synchronized boolean pasteWebmark(PageFolder pagefolder, Webmark webmark, int i)
    {
        PageFolder pagefolder1 = currentRegularFolder;
        currentRegularFolder = pagefolder;
        String s = webmark.getURL();
        String s1 = webmark.getTitle();
        int j = webmark.getVisitCount();
        int k = webmark.getLastVisit();
        boolean flag = addGotoItem(s, s1, j, k, i);
        currentRegularFolder = pagefolder1;
        return flag;
    }

    public synchronized void addGotoItem(URL url, String s)
    {
        addGotoItem(url.toString(), s);
    }

    public synchronized void addGotoItem(String s, String s1)
    {
        addGotoItem(s, s1, 0, 0, -5);
    }

    public synchronized boolean addGotoItem(String s, String s1, int i, int j, int k)
    {
        s1 = normalizeTitle(s, s1);
        Vector vector = currentRegularFolder.getContents();
        int l = vector.size();
        for(int i1 = 0; i1 < l; i1++)
        {
            HotListEntry hotlistentry = (HotListEntry)vector.elementAt(i1);
            if(hotlistentry.url.equals(s))
            {
                String s2 = properties.getPropertyReplace("hotlist.entryexists.msg", s1, ((HotListEntry) (currentRegularFolder)).title);
                HotListFrame hotlistframe = HotListFrame.getHotListFrame();
                if(hotlistframe != null)
                    hotlistframe.setMessage(s2);
                return false;
            }
        }

        String s3 = properties.getPropertyReplace("hotlist.addtogo.msg", s1, ((HotListEntry) (currentRegularFolder)).title);
        HotListFrame hotlistframe1 = HotListFrame.getHotListFrame();
        if(hotlistframe1 != null)
            hotlistframe1.setMessage(s3);
        Webmark webmark = new Webmark(currentRegularFolder, s, s1, i, j);
        if(k == -5)
            currentRegularFolder.addElement(webmark);
        else
            currentRegularFolder.addElement(webmark, k);
        addToMenus(webmark, k);
        propagateVisitCountAndLastVisit(webmark, i, j);
        dispatchHotListEvent(50, webmark);
        return true;
    }

    public synchronized void addSeparatorItem()
    {
        addSeparatorItem(currentRegularFolder, -5);
    }

    public synchronized void addSeparatorItem(PageFolder pagefolder, int i)
    {
        Separator separator = new Separator(pagefolder, " ", "separator");
        if(i == -5)
            pagefolder.addElement(separator);
        else
            pagefolder.addElement(separator, i);
        String s = properties.getPropertyReplace("hotlist.addtogo.msg", separator.getTitle(), ((HotListEntry) (currentRegularFolder)).title);
        HotListFrame hotlistframe = HotListFrame.getHotListFrame();
        if(hotlistframe != null)
            hotlistframe.setMessage(s);
        addToMenus(separator, i);
        dispatchHotListEvent(50, separator);
    }

    public synchronized void changeFolderValue(HotListEntry hotlistentry, boolean flag)
    {
        if(!(hotlistentry instanceof PageFolder))
        {
            return;
        } else
        {
            PageFolder pagefolder = (PageFolder)hotlistentry;
            dbg(" Folder = " + pagefolder);
            pagefolder.setClosedValue(flag);
            dispatchHotListEvent(52, hotlistentry);
            return;
        }
    }

    public synchronized void deleteGotoItem(HotListEntry hotlistentry)
    {
        String s = hotlistentry.title;
        if((s.equals(newPlaces) || s.equals(coolPlaces) || s.equals(menuPlaces)) && hotlistentry.getGrandParent() == null)
        {
            String s1 = properties.getProperty("hotlist.nodelete.msg");
            HotListFrame hotlistframe = HotListFrame.getHotListFrame();
            if(hotlistframe != null)
                hotlistframe.setMessage(s1);
            return;
        }
        PageFolder pagefolder = ((HotListEntry) (hotlistentry.parent)).parent;
        String s2 = properties.getPropertyReplace("hotlist.delete.msg", hotlistentry.title, ((HotListEntry) (hotlistentry.parent)).title);
        if(pagefolder == null)
            s2 = properties.getProperty("hotlist.deleted.list.msg", "Deleted list") + " " + hotlistentry.title;
        HotListFrame hotlistframe1 = HotListFrame.getHotListFrame();
        if(hotlistframe1 != null)
            hotlistframe1.setMessage(s2);
        if(pagefolder == null)
            dispatchHotListEvent(54, hotlistentry);
        else
            dispatchHotListEvent(51, hotlistentry);
        removeFromFolder(regularFolder, hotlistentry);
    }

    synchronized boolean removeFromFolder(PageFolder pagefolder, HotListEntry hotlistentry)
    {
        Vector vector = pagefolder.getContents();
        int i = vector.size();
        for(int j = 0; j < i; j++)
        {
            HotListEntry hotlistentry1 = (HotListEntry)vector.elementAt(j);
            if(hotlistentry1 == hotlistentry)
            {
                removeFromMenus(hotlistentry);
                vector.removeElementAt(j);
                return true;
            }
            if(hotlistentry1 instanceof PageFolder)
            {
                PageFolder pagefolder1 = (PageFolder)hotlistentry1;
                if(removeFromFolder(pagefolder1, hotlistentry))
                    return true;
            }
        }

        return false;
    }

    public synchronized void addMenusToFrame(HJFrame hjframe)
    {
        UserMenu usermenu = hjframe.getGotoMenu();
        if(usermenu != null)
        {
            gotoMenuCount = usermenu.getItemCount();
            UserMenu usermenu1 = rememberToMenu.clone(hjframe.getActionListener());
            int i = usermenu.getItemPosition(rememberMenu);
            if(i != -1)
            {
                usermenu.remove(i);
                usermenu.insert(usermenu1, i);
            }
            while(!threadReader.isFinishedImporting()) 
                try
                {
                    wait(200L);
                }
                catch(InterruptedException _ex) { }
            UserMenu usermenu2 = listsMenu.clone(hjframe.getActionListener());
            i = usermenu.getItemPosition(hotListsMenu);
            if(i != -1)
            {
                usermenu.remove(i);
                usermenu.insert(usermenu2, i);
            }
            updateMenuPlaces(hjframe);
        }
    }

    public synchronized void updateMenuPlaces(HJFrame hjframe)
    {
        boolean flag = properties.getBoolean("hotlistmenucheckbox.state");
        if(flag)
        {
            addMenuPlaces(hjframe);
            return;
        } else
        {
            removeMenuPlaces(hjframe);
            return;
        }
    }

    private void addMenuPlaces(HJFrame hjframe)
    {
        UserMenu usermenu = hjframe.getGotoMenu();
        UserMenu usermenu1 = listsMenu.clone(hjframe.getActionListener());
        UserMenu usermenu2 = (UserMenu)usermenu1.getItem(menuPlaces);
        if(usermenu2 != null && usermenu.getItemCount() == gotoMenuCount)
        {
            usermenu1.remove(usermenu2);
            usermenu.addSeparator();
            MenuItem menuitem;
            for(; usermenu2.getItemCount() > 0; usermenu.add(menuitem))
                menuitem = usermenu2.getItem(0);

        }
    }

    private void removeMenuPlaces(HJFrame hjframe)
    {
        UserMenu usermenu = hjframe.getGotoMenu();
        PageFolder pagefolder = regularFolder.getChildFolder(menuPlaces);
        if(pagefolder != null && usermenu.getItemCount() > gotoMenuCount)
        {
            int i = pagefolder.getCount();
            int j = usermenu.getItemCount();
            for(int k = 0; i != 0 && k < i + 1; k++)
                usermenu.remove(--j);

        }
    }

    public void reparent(HotListEntry hotlistentry, PageFolder pagefolder, int i)
    {
        String s = properties.getProperty("hotlist.netscape");
        if(((HotListEntry) (pagefolder)).title.equals(s))
            return;
        HotListFrame.getHotListFrame();
        if(!(hotlistentry instanceof Separator) && pagefolder != hotlistentry.getParent() && pagefolder.isChild(hotlistentry.getTitle()))
        {
            String s1 = properties.getPropertyReplace("hotlist.entryexists.msg", hotlistentry.getTitle(), pagefolder.getTitle());
            HotListFrame hotlistframe = HotListFrame.getHotListFrame();
            if(hotlistframe != null)
                hotlistframe.setMessage(s1);
            return;
        }
        deleteGotoItem(hotlistentry);
        if(hotlistentry instanceof PageFolder)
        {
            pasteFolder(pagefolder, (PageFolder)hotlistentry, ((PageFolder)hotlistentry).closed(), i);
            dispatchHotListEvent(50, hotlistentry);
            return;
        }
        if(hotlistentry instanceof Webmark)
        {
            pasteWebmark(pagefolder, (Webmark)hotlistentry, i);
            dispatchHotListEvent(50, hotlistentry);
            return;
        }
        if(hotlistentry instanceof Separator)
        {
            addSeparatorItem(pagefolder, i);
            dispatchHotListEvent(50, hotlistentry);
        }
    }

    public UserMenu getSpecificMenu(UserMenu usermenu, PageFolder pagefolder, String s)
    {
        UserMenu usermenu1 = pagefolder.getUserMenu();
        String s1 = usermenu1.getName();
        if(s1.equals(s))
            return usermenu;
        UserMenu usermenu2 = getSpecificMenu(usermenu, pagefolder.getParent(), s);
        for(int i = 0; usermenu2 != null && i < usermenu2.getItemCount(); i++)
        {
            MenuItem menuitem = usermenu2.getItem(i);
            String s2 = menuitem.getName();
            if((menuitem instanceof UserMenu) && s2.equals(pagefolder.getTitle()))
                return (UserMenu)menuitem;
        }

        return null;
    }

    synchronized void addMenuItem(final MenuItem itemToAdd, final UserMenu menuToAddTo, final int pos)
    {
        RequestProcessor.getHJBeanQueue().postRequest(new sunw.hotjava.misc.RequestProcessor.Request() {

            public void execute()
            {
                if(menuToAddTo != null && menuToAddTo.getItem(itemToAdd.getName(), false) == null)
                {
                    int i = pos != -5 ? pos : menuToAddTo.getItemCount();
                    if(itemToAdd.getActionCommand().equals("add separator"))
                    {
                        menuToAddTo.insertSeparator(i);
                        return;
                    }
                    menuToAddTo.insert(itemToAdd, i);
                }
            }

        }
);
    }

    synchronized void addToMenus(HotListEntry hotlistentry, int i)
    {
        HJFrame ahjframe[] = HJWindowManager.getHJWindowManager().getAllFrames();
        for(int j = 0; j < ahjframe.length; j++)
        {
            if(hotlistentry.getParent() == regularFolder)
            {
                UserMenu usermenu = ahjframe[j].getRememberToMenu();
                addToRememberToMenu(hotlistentry, usermenu, ahjframe[j].getActionListener());
            }
            if(hotlistentry.isInMenuPlacesFolder() && ahjframe[j].getListsMenu() != null)
            {
                UserMenu usermenu1 = ahjframe[j].getGotoMenu();
                if(hotlistentry.getParent().getTitle().equals(menuPlaces) && hotlistentry.getParent().getCount() == 1)
                    usermenu1.addSeparator();
                UserMenu usermenu3 = getSpecificMenu(usermenu1, hotlistentry.getParent(), menuPlaces);
                addToMenu(hotlistentry, usermenu3, ahjframe[j].getActionListener(), i);
            } else
            if(!menuPlaces.equals(hotlistentry.getTitle()))
            {
                UserMenu usermenu2 = ahjframe[j].getListsMenu();
                UserMenu usermenu4 = getSpecificMenu(usermenu2, hotlistentry.getParent(), hotListsMenu);
                addToMenu(hotlistentry, usermenu4, ahjframe[j].getActionListener(), i);
            }
        }

        if(hotlistentry.getParent() == regularFolder)
            addToRememberToMenu(hotlistentry, rememberToMenu, null);
        MenuItem menuitem = addToMenu(hotlistentry, hotlistentry.getParent().getUserMenu(), null, i);
        if(hotlistentry instanceof PageFolder)
        {
            UserMenu usermenu5 = (UserMenu)menuitem;
            PageFolder pagefolder = (PageFolder)hotlistentry;
            pagefolder.setUserMenu(usermenu5);
        }
    }

    synchronized void addToRememberToMenu(HotListEntry hotlistentry, UserMenu usermenu, ActionListener actionlistener)
    {
        String s = properties.getProperty("hotlist.netscape");
        if(hotlistentry.title.equals(s))
        {
            return;
        } else
        {
            UserMenuItem usermenuitem = new UserMenuItem("addgoto " + hotlistentry.getTitle(), hotlistentry.getTitle(), actionlistener);
            addMenuItem(usermenuitem, usermenu, -5);
            return;
        }
    }

    synchronized MenuItem addToMenu(HotListEntry hotlistentry, UserMenu usermenu, ActionListener actionlistener, int i)
    {
        Object obj;
        if(hotlistentry instanceof Separator)
            obj = new UserMenuItem("add separator", "", null);
        else
        if(hotlistentry instanceof PageFolder)
        {
            obj = new UserMenu(hotlistentry.title, actionlistener);
            ((MenuItem) (obj)).setActionCommand("");
        } else
        {
            obj = new UserMenuItem("go " + hotlistentry.url, hotlistentry.title, actionlistener);
        }
        if(usermenu != null && usermenu.getName().equals("gotomenu") && i != -5)
            i = ((i + usermenu.getItemCount()) - hotlistentry.getParent().getCount()) + 1;
        addMenuItem(((MenuItem) (obj)), usermenu, i);
        return ((MenuItem) (obj));
    }

    synchronized void removeFromMenus(HotListEntry hotlistentry)
    {
        HJFrame ahjframe[] = HJWindowManager.getHJWindowManager().getAllFrames();
        for(int i = 0; i < ahjframe.length; i++)
        {
            if(hotlistentry.getParent() == regularFolder)
            {
                UserMenu usermenu = ahjframe[i].getRememberToMenu();
                removeFromRememberToMenu(hotlistentry, usermenu);
            }
            UserMenu usermenu3;
            if(hotlistentry.isInMenuPlacesFolder())
            {
                UserMenu usermenu1 = ahjframe[i].getGotoMenu();
                if(hotlistentry.getParent().getTitle().equals(menuPlaces) && hotlistentry.getParent().getCount() == 1)
                {
                    int j = usermenu1.getItemCount();
                    usermenu1.remove(j - 2);
                }
                usermenu3 = getSpecificMenu(usermenu1, hotlistentry.getParent(), menuPlaces);
            } else
            {
                UserMenu usermenu2 = ahjframe[i].getListsMenu();
                usermenu3 = getSpecificMenu(usermenu2, hotlistentry.getParent(), hotListsMenu);
            }
            removeFromMenu(hotlistentry, usermenu3);
        }

        if(hotlistentry.getParent() == regularFolder)
            removeFromRememberToMenu(hotlistentry, rememberToMenu);
        UserMenu usermenu4 = getSpecificMenu(listsMenu, hotlistentry.getParent(), hotListsMenu);
        removeFromMenu(hotlistentry, usermenu4);
    }

    synchronized void removeFromRememberToMenu(HotListEntry hotlistentry, UserMenu usermenu)
    {
        if(usermenu != null)
        {
            MenuItem menuitem = usermenu.getItem("addgoto " + hotlistentry.getTitle(), false);
            if(menuitem != null)
                usermenu.remove(menuitem);
        }
    }

    synchronized void removeFromMenu(HotListEntry hotlistentry, UserMenu usermenu)
    {
        if(usermenu != null)
        {
            if(hotlistentry instanceof Separator)
            {
                PageFolder pagefolder = hotlistentry.getParent();
                int i = pagefolder.getItemPos(hotlistentry);
                if(i > -1)
                {
                    int j = usermenu.getItemCount();
                    int k = pagefolder.getCount();
                    usermenu.remove(j - (k - i));
                }
                return;
            }
            String s;
            if(hotlistentry instanceof PageFolder)
                s = hotlistentry.title;
            else
                s = "go " + hotlistentry.url;
            MenuItem menuitem = usermenu.getItem(s, false);
            if(menuitem != null)
                usermenu.remove(menuitem);
        }
    }

    public synchronized void renameInMenus(HotListEntry hotlistentry, String s, String s1)
    {
        HJFrame ahjframe[] = HJWindowManager.getHJWindowManager().getAllFrames();
        for(int i = 0; i < ahjframe.length; i++)
        {
            if(hotlistentry.getParent() == regularFolder)
            {
                UserMenu usermenu = ahjframe[i].getRememberToMenu();
                renameInRememberToMenu(hotlistentry, usermenu, s);
            }
            if(hotlistentry.isInMenuPlacesFolder())
            {
                UserMenu usermenu1 = ahjframe[i].getGotoMenu();
                UserMenu usermenu3 = getSpecificMenu(usermenu1, hotlistentry.getParent(), menuPlaces);
                renameInMenu(hotlistentry, usermenu3, s, s1);
            } else
            {
                UserMenu usermenu2 = ahjframe[i].getListsMenu();
                UserMenu usermenu4 = getSpecificMenu(usermenu2, hotlistentry.getParent(), hotListsMenu);
                renameInMenu(hotlistentry, usermenu4, s, s1);
            }
        }

        if(hotlistentry.getParent() == regularFolder)
            renameInRememberToMenu(hotlistentry, rememberToMenu, s);
        UserMenu usermenu5 = getSpecificMenu(listsMenu, hotlistentry.getParent(), hotListsMenu);
        renameInMenu(hotlistentry, usermenu5, s, s1);
    }

    synchronized void renameInRememberToMenu(HotListEntry hotlistentry, UserMenu usermenu, String s)
    {
        MenuItem menuitem = usermenu.getItem("addgoto " + hotlistentry.getTitle(), false);
        if(menuitem != null)
        {
            menuitem.setLabel(s);
            menuitem.setName("addgoto " + s);
            menuitem.setActionCommand("addgoto " + s);
        }
    }

    synchronized void renameInMenu(HotListEntry hotlistentry, UserMenu usermenu, String s, String s1)
    {
        if(usermenu != null)
        {
            String s2;
            String s3;
            if(hotlistentry instanceof PageFolder)
            {
                s3 = s;
                s = s;
                s2 = hotlistentry.getTitle();
            } else
            {
                s3 = "go " + s1;
                s2 = "go " + hotlistentry.url;
            }
            MenuItem menuitem = usermenu.getItem(s2, false);
            if(menuitem != null)
            {
                menuitem.setLabel(s);
                menuitem.setName(s3);
            }
        }
    }

    public void exportHTMLFile(String s)
    {
        exportHTMLFile(regularFolder, s);
    }

    public void exportHTMLFile()
    {
        exportHTMLFile(regularFolder, exportFile, true, false);
    }

    public void exportHTMLFile(PageFolder pagefolder, String s)
    {
        exportHTMLFile(pagefolder, s, false, true);
    }

    public void exportHTMLFile(PageFolder pagefolder, String s, boolean flag, boolean flag1)
    {
        System.out.println("writing out ... " + ((HotListEntry) (pagefolder)).title);
        writer.export(pagefolder, s, flag, flag1);
    }

    public void writeHTMLFile(PageFolder pagefolder, String s, boolean flag)
    {
        FileWriter filewriter = null;
        pagefolder.getTitle();
        try
        {
            String s1 = File.separator;
            String s2 = System.getProperty("user.home");
            if(s2 != null && !s2.endsWith(s1))
                s2 = s2 + s1;
            File file = new File(s2 + ".hotjava" + s1);
            file.mkdirs();
            File file1 = new File(s);
            filewriter = new FileWriter(file1);
            writeHTMLFolder(pagefolder, filewriter, flag);
        }
        catch(IOException _ex)
        {
            HJFrame hjframe = HJWindowManager.getHJWindowManager().getLastFocusHolder();
            ConfirmDialog confirmdialog = new ConfirmDialog("hotlist.save.failed", hjframe, 1);
            String s3 = "confirm.hotlist.save.failed.exact.prompt";
            String s4 = properties.getPropertyReplace(s3, exportFile);
            confirmdialog.setPrompt(s4);
            confirmdialog.show();
        }
        finally
        {
            if(filewriter != null)
                try
                {
                    filewriter.flush();
                    filewriter.close();
                }
                catch(IOException _ex) { }
        }
    }

    private void writeHTMLFolder(PageFolder pagefolder, Writer writer1, boolean flag)
    {
        String s = "\n";
        String s1 = "<DL>";
        String s2 = "</DL>";
        String s3 = "<DT>";
        String s4 = "</DT>";
        Vector vector = pagefolder.getContents();
        try
        {
            writer1.write(s1 + s);
            for(int i = 0; i < vector.size(); i++)
            {
                writer1.write(s3 + s);
                HotListEntry hotlistentry = (HotListEntry)vector.elementAt(i);
                if(hotlistentry instanceof PageFolder)
                {
                    PageFolder pagefolder1 = (PageFolder)hotlistentry;
                    String s6 = properties.getProperty("hotlist.netscape");
                    if(!((HotListEntry) (pagefolder1)).title.equals(s6) || flag)
                    {
                        String s8 = "<H3";
                        if(pagefolder1.closed())
                            s8 = s8 + " folded";
                        s8 = s8 + " visit_count=";
                        s8 = s8 + pagefolder1.getVisitCount();
                        s8 = s8 + " last_visit=";
                        s8 = s8 + pagefolder1.getLastVisit();
                        s8 = s8 + " sort_type=";
                        s8 = s8 + pagefolder1.getSortType();
                        String s9 = "false";
                        if(pagefolder1.isReverseSorted())
                            s9 = "true";
                        s8 = s8 + " reverse = ";
                        s8 = s8 + s9;
                        if(((HotListEntry) (pagefolder1)).title.equals(newPlaces) && !flag)
                        {
                            s8 = s8 + " special=personal";
                            s8 = s8 + ">";
                            s8 = s8 + newPlaces;
                        } else
                        if(((HotListEntry) (pagefolder1)).title.equals(coolPlaces) && !flag)
                        {
                            s8 = s8 + " special=cool";
                            s8 = s8 + ">";
                            s8 = s8 + coolPlaces;
                        } else
                        {
                            s8 = s8 + ">";
                            s8 = s8 + pagefolder1.getTitle();
                        }
                        s8 = s8 + "</H3>";
                        writer1.write(s8 + s);
                        if(pagefolder1.getContents().size() != 0)
                            writeHTMLFolder(pagefolder1, writer1, true);
                    }
                } else
                if(hotlistentry instanceof Webmark)
                {
                    Webmark webmark = (Webmark)hotlistentry;
                    String s7 = "<A";
                    s7 = s7 + " href=";
                    s7 = s7 + webmark.getURL();
                    s7 = s7 + " visit_count=";
                    s7 = s7 + webmark.getVisitCount();
                    s7 = s7 + " last_visit=";
                    s7 = s7 + webmark.getLastVisit();
                    s7 = s7 + ">";
                    s7 = s7 + webmark.getTitle();
                    s7 = s7 + "</A>";
                    writer1.write(s7 + s);
                } else
                if(hotlistentry instanceof Separator)
                {
                    Separator _tmp = (Separator)hotlistentry;
                    String s5 = "<HR>";
                    writer1.write(s5 + s);
                }
                writer1.write(s4 + s);
            }

            writer1.write(s2 + s + "<P>" + s);
            return;
        }
        catch(IOException _ex)
        {
            return;
        }
    }

    public PageFolder getRegularFolder()
    {
        return regularFolder;
    }

    private void readLegacyData()
    {
        String s = properties.getProperty("user.home");
        String s1 = File.separator;
        if(s != null && !s.endsWith(s1))
            s = s + s1;
        String s2 = s + ".hotjava" + s1 + "hotlist-default";
        String s3 = properties.getProperty("hotlist.altinput");
        String s4 = null;
        if(s3 != null)
            s4 = s + s1 + s3;
        properties.getProperty("hotlist.err.altinput");
        properties.getProperty("hotlist.err.noinput");
        BufferedReader bufferedreader;
        try
        {
            hotlistFile = new File(s2);
            bufferedreader = new BufferedReader(new FileReader(hotlistFile));
        }
        catch(Exception _ex)
        {
            try
            {
                if(s4 == null)
                    return;
                hotlistFile = new File(s4);
                bufferedreader = new BufferedReader(new FileReader(hotlistFile));
            }
            catch(Exception _ex2)
            {
                return;
            }
        }
        String s5 = properties.getProperty("hotlist.legacy");
        addGotoFolder(s5, true, -5);
        folderPush();
        try
        {
            try
            {
                bufferedreader.readLine();
                bufferedreader.readLine();
                do
                {
                    String s6 = bufferedreader.readLine();
                    String s7 = bufferedreader.readLine();
                    boolean flag = false;
                    if(s6 == null || s7 == null)
                        break;
                    int i = s6.indexOf(' ');
                    if(i != -1)
                    {
                        boolean flag1;
                        if(s6.substring(i).startsWith(" inMenu"))
                            flag1 = true;
                        s6 = s6.substring(0, i);
                    }
                    addGotoItem(s6, s7);
                } while(true);
            }
            finally
            {
                if(bufferedreader != null)
                {
                    bufferedreader.close();
                    folderPop();
                }
            }
            return;
        }
        catch(IOException _ex)
        {
            return;
        }
    }

    public void importList(Frame frame)
    {
        UserFileDialog userfiledialog = new UserFileDialog(frame, "hotlist.import.dialog", 0);
        userfiledialog.setDirectory(properties.getProperty("user.home"));
        userfiledialog.show();
        String s = userfiledialog.getDirectory() + userfiledialog.getFile();
        if(s == null)
            return;
        s.length();
        if(s.endsWith(".html") || s.endsWith(".htm"))
        {
            importingOurType = true;
            importHTMLFile(s);
            String s1 = properties.getPropertyReplace("hotlist.importdone.msg", userfiledialog.getFile());
            HotListFrame hotlistframe = (HotListFrame)frame;
            if(hotlistframe != null)
            {
                hotlistframe.setMessage(s1);
                return;
            }
        } else
        {
            String s2 = properties.getProperty("hotlist.importerr.msg");
            HotListFrame hotlistframe1 = (HotListFrame)frame;
            if(hotlistframe1 != null)
                hotlistframe1.setMessage(s2);
        }
    }

    public void exportList(Frame frame, PageFolder pagefolder)
    {
        UserFileDialog userfiledialog = new UserFileDialog(frame, "hotlist.export.dialog", 1);
        if(fileDialogDirectory != null)
            userfiledialog.setDirectory(fileDialogDirectory);
        int i = exportFile.lastIndexOf(File.separator);
        if(i >= 0)
        {
            String s = exportFile.substring(i + 1);
            userfiledialog.setFile(s);
        }
        userfiledialog.show();
        String s1 = userfiledialog.getFile();
        if(s1 == null || "".equals(s1))
            return;
        fileDialogDirectory = userfiledialog.getDirectory();
        if(fileDialogDirectory != null)
            s1 = userfiledialog.getDirectory() + s1;
        if((new File(s1)).exists() && !userfiledialog.providesSaveConfirmation())
        {
            String s2 = "overwrite.file";
            ConfirmDialog confirmdialog = new ConfirmDialog(s2, frame);
            confirmdialog.setVisible(true);
            if(confirmdialog.getAnswer() == 0)
                return;
        }
        if(pagefolder == null)
        {
            exportHTMLFile(s1);
            return;
        } else
        {
            exportHTMLFile(pagefolder, s1);
            return;
        }
    }

    public boolean isFinishedImporting()
    {
        return threadReader.isFinishedImporting();
    }

    public void raiseImportPriority()
    {
        threadReader.raiseImportPriority();
    }

    public void importHTMLFile(String s, boolean flag, String s1)
    {
        threadReader.importFromHTML(s, flag, s1);
    }

    public void importHTMLFile()
    {
        importHTMLFile(importFile);
    }

    public void importHTMLFile(String s)
    {
        importHTMLFile(s, false);
    }

    public void importHTMLFile(String s, boolean flag)
    {
        threadReader.importFromHTML(s, flag);
    }

    public void readHTMLFile(String s)
    {
        String s1 = s;
        currentRegularFolder = regularFolder;
        try
        {
            InputStreamReader inputstreamreader = null;
            String s2 = properties.getProperty("hotjava.charset");
            ByteToCharConverter bytetocharconverter = null;
            try
            {
                bytetocharconverter = ByteToCharConverter.getConverter(s2);
            }
            catch(UnsupportedEncodingException _ex)
            {
                System.out.println("Charset " + s2 + " is not supported. Use 8859_1");
                s2 = "8859_1";
                properties.put("hotjava.charset", s2);
                properties.save();
            }
            inputstreamreader = new InputStreamReader(new FileInputStream(s1), s2);
            HotListHTMLParser hotlisthtmlparser = new HotListHTMLParser(this);
            hotlisthtmlparser.parse(inputstreamreader, dtd);
            for(; !folderStack.empty(); folderStack.pop());
        }
        catch(Exception _ex) { }
        importingOurType = false;
    }

    public void sortMenus(PageFolder pagefolder)
    {
        HJFrame ahjframe[] = HJWindowManager.getHJWindowManager().getAllFrames();
        for(int i = 0; i < ahjframe.length; i++)
        {
            UserMenu usermenu2;
            if(pagefolder.isInMenuPlacesFolder() || pagefolder.getTitle().equals(menuPlaces))
            {
                UserMenu usermenu = ahjframe[i].getGotoMenu();
                usermenu2 = getSpecificMenu(usermenu, pagefolder, menuPlaces);
            } else
            {
                UserMenu usermenu1 = ahjframe[i].getListsMenu();
                usermenu2 = getSpecificMenu(usermenu1, pagefolder, hotListsMenu);
            }
            sortMenu(usermenu2, pagefolder);
        }

        UserMenu usermenu3 = getSpecificMenu(listsMenu, pagefolder, hotListsMenu);
        sortMenu(usermenu3, pagefolder);
    }

    public void sortMenu(UserMenu usermenu, PageFolder pagefolder)
    {
        if(usermenu == null || pagefolder == null)
            return;
        Vector vector = pagefolder.getContents();
        for(int i = 0; i < vector.size(); i++)
        {
            HotListEntry hotlistentry = (HotListEntry)vector.elementAt(i);
            if(hotlistentry instanceof Separator)
            {
                usermenu.removeFirstSeparator();
                usermenu.addSeparator();
            } else
            {
                String s;
                if(hotlistentry instanceof PageFolder)
                    s = hotlistentry.getTitle();
                else
                    s = "go " + hotlistentry.url;
                MenuItem menuitem = usermenu.getItem(s, false);
                if(menuitem != null)
                {
                    usermenu.remove(menuitem);
                    addMenuItem(menuitem, usermenu, -5);
                }
            }
        }

    }

    public void sort(PageFolder pagefolder)
    {
        if(pagefolder.isReverseSorted())
        {
            sort(73, pagefolder);
            return;
        } else
        {
            sort(pagefolder.getSortType(), pagefolder);
            return;
        }
    }

    public void sort(int i, PageFolder pagefolder)
    {
        boolean flag = false;
        if(i == 73)
        {
            if(pagefolder.isReverseSorted())
                flag = false;
            else
                flag = true;
            pagefolder.reverseSort();
            i = pagefolder.getSortType();
            if(i == 0)
                return;
        }
        sort(i, pagefolder, flag);
    }

    public void sort(int i, PageFolder pagefolder, boolean flag)
    {
        if(!flag)
            pagefolder.setSortType(i);
        switch(i)
        {
        case 70: // 'F'
            sortAlphabetical(pagefolder, flag);
            break;

        case 71: // 'G'
            sortOnDates(pagefolder, flag);
            break;

        case 72: // 'H'
            sortOnVisitFrequency(pagefolder, flag);
            break;
        }
        sortMenus(pagefolder);
    }

    public void sortAlphabetical(PageFolder pagefolder, boolean flag)
    {
        String s;
        if(flag)
        {
            s = properties.getPropertyReplace("hotlist.sortreversealphabetical.msg", ((HotListEntry) (pagefolder)).title);
            SortVector.reverse(pagefolder.getContents());
        } else
        {
            AlphabeticalCompare alphabeticalcompare = new AlphabeticalCompare();
            s = properties.getPropertyReplace("hotlist.sortalphabetical.msg", ((HotListEntry) (pagefolder)).title);
            SortVector.quicksort(pagefolder.getContents(), alphabeticalcompare);
        }
        HotListFrame hotlistframe = HotListFrame.getHotListFrame();
        if(hotlistframe != null)
            hotlistframe.setMessage(s);
    }

    public void sortOnVisitFrequency(PageFolder pagefolder, boolean flag)
    {
        String s;
        if(flag)
        {
            s = properties.getPropertyReplace("hotlist.sortreversevisitfrequency.msg", ((HotListEntry) (pagefolder)).title);
            SortVector.reverse(pagefolder.getContents());
        } else
        {
            VisitFrequencyCompare visitfrequencycompare = new VisitFrequencyCompare();
            s = properties.getPropertyReplace("hotlist.sortvisitfrequency.msg", ((HotListEntry) (pagefolder)).title);
            SortVector.quicksort(pagefolder.getContents(), visitfrequencycompare);
        }
        HotListFrame hotlistframe = HotListFrame.getHotListFrame();
        if(hotlistframe != null)
            hotlistframe.setMessage(s);
    }

    public void sortOnDates(PageFolder pagefolder, boolean flag)
    {
        String s;
        if(flag)
        {
            s = properties.getPropertyReplace("hotlist.sortreversedatevisited.msg", ((HotListEntry) (pagefolder)).title);
            SortVector.reverse(pagefolder.getContents());
        } else
        {
            DateVisitedCompare datevisitedcompare = new DateVisitedCompare();
            s = properties.getPropertyReplace("hotlist.sortdatevisited.msg", ((HotListEntry) (pagefolder)).title);
            SortVector.quicksort(pagefolder.getContents(), datevisitedcompare);
        }
        HotListFrame hotlistframe = HotListFrame.getHotListFrame();
        if(hotlistframe != null)
            hotlistframe.setMessage(s);
    }

    public void sortAlphabeticalRecursive(PageFolder pagefolder, Compare compare, boolean flag)
    {
        Vector vector = pagefolder.getContents();
        for(int i = 0; i < vector.size(); i++)
        {
            HotListEntry hotlistentry = (HotListEntry)vector.elementAt(i);
            if(hotlistentry instanceof PageFolder)
            {
                PageFolder pagefolder1 = (PageFolder)hotlistentry;
                pagefolder1.setSortType(pagefolder.getSortType());
                if(flag)
                    pagefolder1.reverseSort();
                sortAlphabeticalRecursive((PageFolder)hotlistentry, compare, flag);
            }
        }

        SortVector.quicksort(vector, compare);
    }

    public void sortOnVisitFrequencyRecursive(PageFolder pagefolder, Compare compare, boolean flag)
    {
        Vector vector = pagefolder.getContents();
        for(int i = 0; i < vector.size(); i++)
        {
            HotListEntry hotlistentry = (HotListEntry)vector.elementAt(i);
            if(hotlistentry instanceof PageFolder)
            {
                PageFolder pagefolder1 = (PageFolder)hotlistentry;
                pagefolder1.setSortType(pagefolder.getSortType());
                if(flag)
                    pagefolder1.reverseSort();
                sortOnVisitFrequencyRecursive((PageFolder)hotlistentry, compare, flag);
            }
        }

        SortVector.quicksort(vector, compare);
        if(vector.size() > 0)
        {
            HotListEntry hotlistentry1 = (HotListEntry)vector.elementAt(0);
            int j = hotlistentry1.getVisitCount();
            pagefolder.setVisitCount(j);
        }
    }

    public void sortOnDatesRecursive(PageFolder pagefolder, Compare compare, boolean flag)
    {
        Vector vector = pagefolder.getContents();
        for(int i = 0; i < vector.size(); i++)
        {
            HotListEntry hotlistentry = (HotListEntry)vector.elementAt(i);
            if(hotlistentry instanceof PageFolder)
            {
                PageFolder pagefolder1 = (PageFolder)hotlistentry;
                pagefolder1.setSortType(pagefolder.getSortType());
                if(flag)
                    pagefolder1.reverseSort();
                sortOnDatesRecursive((PageFolder)hotlistentry, compare, flag);
            }
        }

        SortVector.quicksort(vector, compare);
        if(vector.size() > 0)
        {
            HotListEntry hotlistentry1 = (HotListEntry)vector.elementAt(0);
            int j = hotlistentry1.getLastVisit();
            pagefolder.setLastVisit(j);
        }
    }

    public void printFolder(PageFolder pagefolder)
    {
        Vector vector = pagefolder.getContents();
        for(int i = 0; i < vector.size(); i++)
        {
            HotListEntry hotlistentry = (HotListEntry)vector.elementAt(i);
            if(hotlistentry instanceof PageFolder)
                printFolder((PageFolder)hotlistentry);
        }

    }

    public void propagateVisitCountAndLastVisit(HotListEntry hotlistentry, int i, int j)
    {
        PageFolder pagefolder = hotlistentry.getParent();
        if(pagefolder == null)
            return;
        if(i > pagefolder.getVisitCount() || j > pagefolder.getLastVisit())
        {
            pagefolder.setVisitCount(i);
            pagefolder.setLastVisit(j);
            propagateVisitCountAndLastVisit(((HotListEntry) (pagefolder)), i, j);
        }
    }

    private void visitedDocument(PageFolder pagefolder, URL url)
    {
        Vector vector = pagefolder.getContents();
        int i = vector.size();
        for(int j = 0; j < i; j++)
        {
            HotListEntry hotlistentry = (HotListEntry)vector.elementAt(j);
            if(hotlistentry instanceof PageFolder)
            {
                String s = properties.getProperty("hotlist.netscape");
                if(!hotlistentry.title.equals(s))
                    visitedDocument((PageFolder)hotlistentry, url);
            } else
            if(hotlistentry instanceof Webmark)
                try
                {
                    if(hotlistentry.url.equals(url.toString()))
                    {
                        Webmark webmark = (Webmark)hotlistentry;
                        int k = webmark.getVisitCount();
                        webmark.setVisitCount(++k);
                        int l = (int)(System.currentTimeMillis() / 1000L);
                        webmark.setLastVisit(l);
                        propagateVisitCountAndLastVisit(webmark, k, l);
                        dispatchHotListEvent(52, webmark);
                    }
                }
                catch(Exception _ex) { }
        }

    }

    public void markVisited(URL url)
    {
        visitedDocument(regularFolder, url);
    }

    public HotListEntry getEntry(PageFolder pagefolder, HotListEntry hotlistentry)
    {
        return getEntry(pagefolder, hotlistentry.title);
    }

    public HotListEntry getEntry(PageFolder pagefolder, String s)
    {
        Vector vector = pagefolder.getContents();
        if(vector == null)
            return null;
        for(int i = 0; i < vector.size(); i++)
        {
            HotListEntry hotlistentry = (HotListEntry)vector.elementAt(i);
            if(hotlistentry.title.equals(s))
                return hotlistentry;
        }

        return null;
    }

    public void addPlaces(String s)
    {
        if(s == null)
            return;
        int i = s.indexOf('|');
        if(i < 0)
            return;
        s = s.substring(i + 1);
        for(StringTokenizer stringtokenizer = new StringTokenizer(s, "|"); stringtokenizer.hasMoreTokens();)
        {
            String s1 = stringtokenizer.nextToken();
            String s2 = s1;
            String s4 = s1;
            int j = s1.indexOf('=');
            if(j >= 0)
            {
                String s3 = s1.substring(0, j);
                String s5 = s1.substring(j + 1);
                addGotoItem(s5, s3);
            }
        }

    }

    public String getUniqueName(String s)
    {
        return getUniqueName(s, currentRegularFolder);
    }

    public String getUniqueName(String s, PageFolder pagefolder)
    {
        Vector vector = pagefolder.getContents();
        if(vector == null)
            return s;
        HotListEntry hotlistentry = getEntry(pagefolder, s);
        if(hotlistentry == null)
            return s;
        for(int i = 1; i < vector.size(); i++)
        {
            HotListEntry hotlistentry1 = getEntry(pagefolder, s + String.valueOf(i));
            if(hotlistentry1 == null)
                return s + String.valueOf(i);
        }

        return s + String.valueOf(vector.size());
    }

    private static DTD createDTD(DTD dtd1, String s)
    {
        String s1 = "/";
        String s2 = "lib" + s1 + s + ".bdtd";
        InputStream inputstream = null;
        try
        {
            try
            {
                inputstream = ClassLoader.getSystemResourceAsStream(s2);
                if(inputstream == null)
                    throw new Error("Unable to find a .bdtd file :" + s2);
                dtd1.read(new DataInputStream(new BufferedInputStream(inputstream)));
                DTD.putDTDHash(s, dtd1);
            }
            finally
            {
                if(inputstream != null)
                    inputstream.close();
            }
        }
        catch(Exception exception1)
        {
            System.out.println(exception1);
        }
        return dtd1;
    }

    private static HotList hotList = null;
    static final String officialVersionNumber = "hotjava-hotlist-version-2";
    static final String inMenuTag = " inMenu";
    static final String propName = "hotlist";
    String untitledTitle;
    String newPlaces;
    String coolPlaces;
    String menuPlaces;
    UserMenu rememberToMenu;
    UserMenu listsMenu;
    String hotListsMenu;
    String rememberMenu;
    private static int gotoMenuCount;
    private long fileTimeStamp;
    static final int ADD_TO_END = -5;
    String hotlistName;
    File hotlistFile;
    DTD dtd;
    String fileDialogDirectory;
    String exportFile;
    String importFile;
    static final boolean debug = false;
    PageFolder regularFolder;
    PageFolder currentRegularFolder;
    Stack folderStack;
    boolean importingOurType;
    boolean menusOn;
    String doctype;
    HotListWriter writer;
    HotListReader threadReader;
    public static final int SORTATOZ = 70;
    public static final int SORTDATES = 71;
    public static final int SORTFREQ = 72;
    public static final int SORTREVERSE = 73;
    protected HotListListener listeners;
    private HJBProperties properties;
    private HJBProperties beanProps;

}
