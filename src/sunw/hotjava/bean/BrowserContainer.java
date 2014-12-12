// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BrowserContainer.java

package sunw.hotjava.bean;

import java.awt.Dimension;
import java.awt.Point;
import java.net.URL;

// Referenced classes of package sunw.hotjava.bean:
//            HTMLBrowsable, ConsoleDialogListener

public interface BrowserContainer
{

    public abstract void alertDialog(String s);

    public abstract boolean confirmDialog(String s);

    public abstract String promptDialog(String s);

    public abstract void setBarVisible(int i, boolean flag);

    public abstract boolean isBarVisible(int i);

    public abstract void closeBrowser();

    public abstract HTMLBrowsable requestNewFrame(String s, URL url, boolean flag, boolean flag1, boolean flag2, boolean flag3, Point point, 
            Dimension dimension);

    public abstract boolean findPrompt();

    public abstract boolean hasConsoleDialog();

    public abstract void addConsoleDialogListener(ConsoleDialogListener consoledialoglistener);

    public abstract void removeConsoleDialogListener(ConsoleDialogListener consoledialoglistener);

    public static final int LOCATIONBAR = 0;
    public static final int MENUBAR = 1;
    public static final int PERSONALBAR = 2;
    public static final int STATUSBAR = 3;
    public static final int TOOLBAR = 4;
    public static final int LAST_TOOLBAR = 4;
}
