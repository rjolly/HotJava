// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InstrumentPanelControls.java

package sunw.hotjava.ui;

import java.awt.*;
import java.util.Hashtable;
import java.util.Vector;

// Referenced classes of package sunw.hotjava.ui:
//            InstrumentPanelControls, InstrumentPanelSpacer

class InstrumentPanelControlsLayout extends GridBagLayout
{

    public InstrumentPanelControlsLayout()
    {
        this(10);
    }

    public InstrumentPanelControlsLayout(int i)
    {
        toolbarList = new Vector();
        toolbarAlignment = i;
    }

    public InstrumentPanelControlsLayout(String s)
    {
        this(10);
        if("left".equalsIgnoreCase(s))
        {
            toolbarAlignment = 17;
            return;
        }
        if("right".equalsIgnoreCase(s))
            toolbarAlignment = 13;
    }

    public void addLayoutComponent(Component component, Object obj)
    {
        String s = (String)obj;
        if("Toolbar".equalsIgnoreCase(s))
            addToolbar(component);
        else
        if("ActivityMonitor".equalsIgnoreCase(s))
            monitor = component;
        else
        if("Spacer".equalsIgnoreCase(s))
        {
            spacer = component;
            spacer.setVisible(false);
        }
        resetConstraints();
    }

    public void removeLayoutComponent(Component component)
    {
        if(toolbarList.contains(component))
            removeToolbar(component);
        else
        if(component == monitor)
            monitor = null;
        resetConstraints();
    }

    private void clearConstraints()
    {
        super.comptable.clear();
    }

    private void addToolbar(Component component)
    {
        toolbarList.addElement(component);
    }

    private void removeToolbar(Component component)
    {
        toolbarList.removeElement(component);
    }

    public boolean haveToolbars()
    {
        return toolbarList.size() > 0;
    }

    public boolean hasActivityMonitor()
    {
        return monitor != null;
    }

    public Component getActivityMonitor()
    {
        return monitor;
    }

    protected void resetConstraints()
    {
        clearConstraints();
        int i = toolbarList.size();
        if(i > 0)
            if(monitor != null)
            {
                constrainToolsAndMonitor();
                return;
            } else
            {
                constrainTools();
                return;
            }
        if(monitor != null)
            constrainMonitor();
    }

    private void constrainToolsAndMonitor()
    {
        monitor.getPreferredSize();
        spacer.setVisible(false);
        constrainToolbars(toolbarList.size(), 1, 0, 1, toolbarAlignment, 0, 1.0D);
        constrain(monitor, toolbarList.size() + 1, 0, 1, 13, 0);
    }

    private void constrainTools()
    {
        spacer.setVisible(false);
        constrainToolbars(toolbarList.size(), 0, 0, 0, toolbarAlignment, 0, 1.0D);
    }

    private void constrainMonitor()
    {
        Dimension dimension = monitor.getPreferredSize();
        spacer.setSize(dimension.width, dimension.height);
        constrain(spacer, 0, 0, 1, 17, 2);
        spacer.setVisible(true);
        constrain(monitor, 2, 0, 1, 13, 0, 1.0D);
    }

    private void constrainToolbars(int i, int j, int k, int l, int i1, int j1, double d)
    {
        if(i > 1)
        {
            for(int k1 = 0; k1 < i - 1; k1++)
                constrain((Component)toolbarList.elementAt(k1), j + k1, k, 1, i1, j1, d);

            constrain((Component)toolbarList.elementAt(i - 1), (j + i) - 1, k, l, i1, j1, d);
            return;
        } else
        {
            constrain((Component)toolbarList.elementAt(0), j, k, l, i1, j1, d);
            return;
        }
    }

    private void constrain(Component component, int i, int j, int k, int l, int i1)
    {
        constrain(component, i, j, k, l, i1, 0.0D);
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

    int toolbarAlignment;
    Vector toolbarList;
    Component spacer;
    Component monitor;
}
