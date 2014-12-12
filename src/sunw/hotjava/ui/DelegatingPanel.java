// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DelegatingPanel.java

package sunw.hotjava.ui;

import java.awt.*;

public class DelegatingPanel extends Panel
{

    public DelegatingPanel()
    {
        setLayout(null);
    }

    public Component add(Component component)
    {
        if(child != null)
            super.remove(child);
        super.add(component);
        child = component;
        return component;
    }

    public synchronized Component add(Component component, int i)
    {
        return add(component);
    }

    public synchronized Component add(String s, Component component)
    {
        return add(component);
    }

    public synchronized void remove(Component component)
    {
        super.remove(component);
        child = null;
    }

    public synchronized void removeAll()
    {
        super.removeAll();
        child = null;
    }

    public synchronized Dimension getPreferredSize()
    {
        if(child != null)
            return child.getPreferredSize();
        else
            return getSize();
    }

    public synchronized Dimension getMinimumSize()
    {
        if(child != null)
            return child.getMinimumSize();
        else
            return new Dimension(0, 0);
    }

    public synchronized void setEnabled(boolean flag)
    {
        if(child != null)
            child.setEnabled(flag);
    }

    public boolean isEnabled()
    {
        if(child != null)
            return child.isEnabled();
        else
            return true;
    }

    public void layout()
    {
        if(child != null)
            child.layout();
        super.layout();
    }

    protected Component child;
}
