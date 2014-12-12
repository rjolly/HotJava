// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InstrumentPanelMessageLine.java

package sunw.hotjava.ui;

import java.awt.*;
import java.util.Hashtable;

// Referenced classes of package sunw.hotjava.ui:
//            InstrumentPanelMessageLine

class InstrumentPanelMessageLayout extends GridBagLayout
{

    public InstrumentPanelMessageLayout()
    {
    }

    public void addLayoutComponent(Component component, Object obj)
    {
        String s = (String)obj;
        if("MessageLine".equalsIgnoreCase(s))
            messageLine = component;
        else
        if("Clock".equalsIgnoreCase(s))
            clock = component;
        resetConstraints();
    }

    public void removeLayoutComponent(Component component)
    {
        if(component == messageLine)
            messageLine = null;
        else
        if(component == clock)
            clock = null;
        resetConstraints();
    }

    protected void resetConstraints()
    {
        clearConstraints();
        int i = 0;
        if(messageLine != null)
            constrain(messageLine, i++, 0, 1, 17, 2, 1.0D);
        if(clock != null)
            constrain(clock, i++, 0, 1, 13, 0, messageLine == null ? 1.0D : 0.0D);
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

    Component messageLine;
    Component clock;
}
