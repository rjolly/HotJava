// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotListFrame.java

package sunw.hotjava.ui;

import java.awt.Insets;
import java.awt.Panel;

// Referenced classes of package sunw.hotjava.ui:
//            BookmarkDialog, FolderDialog, FolderPanel, HotListBuffer, 
//            HotListFrame, ListPanel

class PanelWithInsets extends Panel
{

    public Insets getInsets()
    {
        return new Insets(5, 5, 5, 5);
    }

    PanelWithInsets()
    {
    }
}
