// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InstrumentPanelLocator.java

package sunw.hotjava.ui;

import java.awt.*;
import java.util.Hashtable;

// Referenced classes of package sunw.hotjava.ui:
//            InstrumentPanelLocator

class InstrumentPanelLocatorLayout extends GridBagLayout
{

    public InstrumentPanelLocatorLayout()
    {
    }

    public void addLayoutComponent(Component component, Object obj)
    {
        String s = (String)obj;
        if("Locator".equalsIgnoreCase(s))
            locator = component;
        else
        if("ActivityMonitor".equalsIgnoreCase(s))
            monitor = component;
        resetConstraints();
    }

    public void removeLayoutComponent(Component component)
    {
        if(component == locator)
            locator = null;
        else
        if(component == monitor)
            monitor = null;
        resetConstraints();
    }

    protected void resetConstraints()
    {
        clearConstraints();
        int i = 0;
        if(locator != null)
            i++;
        if(monitor != null)
            i++;
        int j = 0;
        if(locator != null)
            constrain(locator, j++, 0, -1, 17, 2, 1.0D);
        if(monitor != null)
            constrain(monitor, j, 0, 1, 13, 0, 0.0D);
    }

    private void clearConstraints()
    {
        super.comptable.clear();
    }

    private void constrain(Component component, int i, int j, int k, int l, int i1, double d)
    {
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridx = i;
        gridbagconstraints.gridy = j;
        gridbagconstraints.gridwidth = k;
        gridbagconstraints.anchor = l;
        gridbagconstraints.fill = i1;
        gridbagconstraints.weightx = d;
        setConstraints(component, gridbagconstraints);
    }

    Component locator;
    Component monitor;
}
