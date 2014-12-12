// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotListFrame.java

package sunw.hotjava.ui;


// Referenced classes of package sunw.hotjava.ui:
//            Bookmark, BookmarkDialog, EntrySeparator, Folder, 
//            FolderDialog, FolderPanel, HotListFrame, ListPanel, 
//            PanelWithInsets, HotListEntry, ListItem

class HotListBuffer
{

    public void HotListBuffer()
    {
        frm = HotListFrame.getHotListFrame();
    }

    public void clearBuffer()
    {
        listItem = null;
        op = null;
        entry = null;
        canUndo = false;
    }

    public void saveInBuffer(ListItem listitem, String s)
    {
        listItem = listitem;
        op = s;
        canUndo = true;
        if(listitem instanceof Folder)
        {
            Folder folder = (Folder)listitem;
            entry = folder.getHotListEntry();
        }
        if(listitem instanceof Bookmark)
        {
            Bookmark bookmark = (Bookmark)listitem;
            entry = bookmark.getHotListEntry();
        }
        if(listitem instanceof EntrySeparator)
        {
            EntrySeparator entryseparator = (EntrySeparator)listitem;
            entry = entryseparator.getHotListEntry();
        }
    }

    public HotListEntry getEntryFromBuffer()
    {
        return entry;
    }

    public ListItem getListItemFromBuffer()
    {
        return listItem;
    }

    public String getOperationFromBuffer()
    {
        return op;
    }

    public void disableUndo()
    {
        canUndo = false;
    }

    public boolean canUndo()
    {
        return canUndo;
    }

    public boolean canPaste()
    {
        return op != null && !op.equals("delete");
    }

    HotListBuffer()
    {
        canUndo = false;
    }

    HotListFrame frm;
    HotListEntry entry;
    ListItem listItem;
    String op;
    private boolean canUndo;
}
