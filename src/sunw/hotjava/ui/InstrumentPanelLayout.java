// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InstrumentPanelLayout.java

package sunw.hotjava.ui;

import java.awt.*;
import java.util.Hashtable;

class InstrumentPanelLayout extends GridBagLayout
{

    public InstrumentPanelLayout()
    {
    }

    public void addLayoutComponent(Component component, Object obj)
    {
        String s = (String)obj;
        if("Controls".equalsIgnoreCase(s))
            controls = component;
        else
        if("LocatorBox".equalsIgnoreCase(s))
            locatorBox = component;
        else
        if("MessageBox".equalsIgnoreCase(s))
            messageBox = component;
        resetConstraints();
    }

    public void removeLayoutComponent(Component component)
    {
        if(component == controls)
            controls = null;
        else
        if(component == locatorBox)
            locatorBox = null;
        else
        if(component == messageBox)
            messageBox = null;
        resetConstraints();
    }

    protected void resetConstraints()
    {
        clearConstraints();
        int i = 0;
        if(controls != null)
            constrain(controls, 0, i++, 1, 17, 1, 1.0D);
        if(locatorBox != null)
            constrain(locatorBox, 0, i++, 1, 17, 1, 1.0D);
        if(messageBox != null)
            constrain(messageBox, 0, i, 1, 17, 1, 1.0D);
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
        gridbagconstraints.gridheight = k;
        gridbagconstraints.anchor = l;
        gridbagconstraints.fill = i1;
        gridbagconstraints.weightx = d;
        setConstraints(component, gridbagconstraints);
    }

    Component controls;
    Component locatorBox;
    Component messageBox;
}
