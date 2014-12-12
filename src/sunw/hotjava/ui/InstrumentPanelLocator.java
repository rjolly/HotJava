// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InstrumentPanelLocator.java

package sunw.hotjava.ui;

import java.awt.Component;
import java.awt.Container;

// Referenced classes of package sunw.hotjava.ui:
//            InstrumentPanelPart, InstrumentPanelLocatorLayout, Locator

public class InstrumentPanelLocator extends InstrumentPanelPart
{

    public InstrumentPanelLocator()
    {
        setLayout(new InstrumentPanelLocatorLayout());
    }

    public boolean isEmpty()
    {
        return !hasLocator() && !hasActivityMonitor();
    }

    public boolean hasLocator()
    {
        return locator != null;
    }

    public void add(Locator locator1)
    {
        locator = locator1;
        invalidate();
        add("Locator", locator1);
        validate();
        setVisible(true);
    }

    public Locator removeLocator()
    {
        Locator locator1 = locator;
        invalidate();
        remove(locator);
        locator = null;
        validate();
        setVisible(!isEmpty());
        return locator1;
    }

    Locator locator;
}
