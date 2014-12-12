// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InstrumentPanelPart.java

package sunw.hotjava.ui;

import java.awt.*;

// Referenced classes of package sunw.hotjava.ui:
//            ClockPanel, ActivityMonitor

public abstract class InstrumentPanelPart extends Panel
{

    public abstract boolean isEmpty();

    public boolean hasClock()
    {
        return clock != null;
    }

    public boolean hasActivityMonitor()
    {
        return monitor != null;
    }

    public void add(ClockPanel clockpanel)
    {
        clock = clockpanel;
        invalidate();
        add("Clock", clockpanel);
        validate();
        setVisible(true);
    }

    public void add(ActivityMonitor activitymonitor)
    {
        monitor = activitymonitor;
        invalidate();
        add("ActivityMonitor", activitymonitor);
        validate();
        setVisible(true);
    }

    public ClockPanel removeClock()
    {
        ClockPanel clockpanel = clock;
        invalidate();
        remove(clock);
        clock = null;
        validate();
        if(isEmpty())
            setVisible(false);
        return clockpanel;
    }

    public ActivityMonitor removeActivityMonitor()
    {
        ActivityMonitor activitymonitor = monitor;
        invalidate();
        remove(monitor);
        monitor = null;
        validate();
        if(isEmpty())
            setVisible(false);
        return activitymonitor;
    }

    public Insets getInsets()
    {
        return new Insets(0, 0, 0, 0);
    }

    public InstrumentPanelPart()
    {
    }

    ClockPanel clock;
    ActivityMonitor monitor;
    public static final int H_MARGIN = 2;
    public static final int V_MARGIN = 0;
}
