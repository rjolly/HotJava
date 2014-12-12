// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InstrumentPanelControls.java

package sunw.hotjava.ui;

import java.awt.*;

// Referenced classes of package sunw.hotjava.ui:
//            InstrumentPanelControls, InstrumentPanelControlsLayout

class InstrumentPanelSpacer extends Panel
{

    public InstrumentPanelSpacer()
    {
    }

    public InstrumentPanelSpacer(Component component)
    {
        Dimension dimension = component.getSize();
        widthReserved = dimension.width;
        heightReserved = dimension.height;
    }

    public InstrumentPanelSpacer(int i, int j)
    {
        widthReserved = i;
        heightReserved = j;
    }

    public Dimension getMinimumSize()
    {
        return new Dimension(widthReserved, heightReserved);
    }

    public Dimension getPreferredSize()
    {
        return getMinimumSize();
    }

    public void setReservedWidth(int i)
    {
        widthReserved = i;
    }

    public void setReservedHeight(int i)
    {
        heightReserved = i;
    }

    public void setSize(int i, int j)
    {
        widthReserved = i;
        heightReserved = j;
        super.setSize(i, j);
    }

    private int widthReserved;
    private int heightReserved;
}
