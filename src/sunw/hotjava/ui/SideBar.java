// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SideBar.java

package sunw.hotjava.ui;

import java.awt.Container;
import java.awt.FlowLayout;
import sun.awt.OrientableFlowLayout;

// Referenced classes of package sunw.hotjava.ui:
//            RaisedPanel

public class SideBar extends RaisedPanel
{

    public SideBar()
    {
        setLayout(new OrientableFlowLayout(1, 1, 0, 0, 0, 0, 0));
    }

    public boolean isEmpty()
    {
        return countComponents() <= 0;
    }
}
