// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InstrumentPanelControls.java

package sunw.hotjava.ui;

import java.awt.*;

// Referenced classes of package sunw.hotjava.ui:
//            InstrumentPanelPart, InstrumentPanelControlsLayout, InstrumentPanelSpacer, Toolbar

public class InstrumentPanelControls extends InstrumentPanelPart
{

    public InstrumentPanelControls()
    {
        setLayout(new InstrumentPanelControlsLayout());
        spacer = new InstrumentPanelSpacer();
        add(spacer);
    }

    public InstrumentPanelControls(int i)
    {
        setLayout(new InstrumentPanelControlsLayout(i));
        spacer = new InstrumentPanelSpacer();
        add(spacer);
    }

    public InstrumentPanelControls(String s)
    {
        setLayout(new InstrumentPanelControlsLayout(s));
        spacer = new InstrumentPanelSpacer();
        add(spacer);
    }

    public Insets getInsets()
    {
        Insets insets = super.getInsets();
        insets.top = 0;
        insets.left = 2;
        insets.bottom = 0;
        insets.right = 0;
        return insets;
    }

    protected void add(InstrumentPanelSpacer instrumentpanelspacer)
    {
        add("Spacer", instrumentpanelspacer);
    }

    public boolean isEmpty()
    {
        return !hasActivityMonitor() && !haveToolbars();
    }

    public boolean haveToolbars()
    {
        return ((InstrumentPanelControlsLayout)getLayout()).haveToolbars();
    }

    public void add(Toolbar toolbar)
    {
        add("Toolbar", toolbar);
        setVisible(true);
    }

    public void remove(Toolbar toolbar)
    {
        remove(toolbar);
        if(isEmpty())
            setVisible(false);
    }

    InstrumentPanelSpacer spacer;
}
