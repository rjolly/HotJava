// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InstrumentPanel.java

package sunw.hotjava.ui;

import java.awt.*;

// Referenced classes of package sunw.hotjava.ui:
//            RaisedPanel, ClockPanel, InstrumentPanelControls, InstrumentPanelLayout, 
//            InstrumentPanelLocator, InstrumentPanelMessageLine, InstrumentPanelPart, Toolbar, 
//            ActivityMonitor, Locator, MessageLine

public class InstrumentPanel extends RaisedPanel
{

    public InstrumentPanel()
    {
        hasClock = false;
        setLayout(new InstrumentPanelLayout());
    }

    protected InstrumentPanelControls getControls()
    {
        if(controls == null)
        {
            controls = new InstrumentPanelControls("left");
            add(controls);
        }
        return controls;
    }

    protected InstrumentPanelLocator getLocatorBox()
    {
        if(locatorBox == null)
        {
            locatorBox = new InstrumentPanelLocator();
            add(locatorBox);
        }
        return locatorBox;
    }

    protected InstrumentPanelMessageLine getMessageBox()
    {
        if(messageBox == null)
        {
            messageBox = new InstrumentPanelMessageLine();
            add(messageBox);
        }
        return messageBox;
    }

    public boolean isEmpty()
    {
        return (controls == null || !haveControls()) && (locatorBox == null || !hasLocator()) && (messageBox == null || !hasMessageLine()) && !hasClock;
    }

    public boolean hasLocator()
    {
        return locatorBox != null && locatorBox.hasLocator();
    }

    public boolean hasMessageLine()
    {
        return messageBox != null && messageBox.hasMessageLine();
    }

    public boolean haveControls()
    {
        return controls != null && (controls.haveToolbars() || controls.hasActivityMonitor());
    }

    public boolean haveToolbars()
    {
        return controls != null && controls.haveToolbars();
    }

    public boolean hasClock()
    {
        return messageBox != null && messageBox.hasClock();
    }

    public boolean hasActivityMonitor()
    {
        return controls != null && controls.hasActivityMonitor() || locatorBox != null && locatorBox.hasActivityMonitor() || messageBox != null && messageBox.hasActivityMonitor();
    }

    public void add(InstrumentPanelControls instrumentpanelcontrols)
    {
        invalidate();
        add("Controls", instrumentpanelcontrols);
        validate();
    }

    public void add(InstrumentPanelLocator instrumentpanellocator)
    {
        invalidate();
        add("LocatorBox", instrumentpanellocator);
        validate();
    }

    public void add(InstrumentPanelMessageLine instrumentpanelmessageline)
    {
        invalidate();
        add("MessageBox", instrumentpanelmessageline);
        validate();
    }

    public void add(Toolbar toolbar)
    {
        invalidate();
        if(!haveControls() && hasLocator() && locatorBox.hasActivityMonitor())
            moveActivityMonitorLocatorToControls();
        getControls().add(toolbar);
        validate();
    }

    public void add(ClockPanel clockpanel)
    {
        invalidate();
        getMessageBox().add(clockpanel);
        validate();
    }

    public void add(ActivityMonitor activitymonitor)
    {
        invalidate();
        if(haveControls())
            controls.add(activitymonitor);
        else
        if(hasLocator())
        {
            locatorBox.add(activitymonitor);
        } else
        {
            getControls().getPreferredSize();
            getControls().add(activitymonitor);
        }
        validate();
    }

    public void add(Locator locator)
    {
        invalidate();
        InstrumentPanelLocator instrumentpanellocator = getLocatorBox();
        instrumentpanellocator.add(locator);
        validate();
    }

    public void add(MessageLine messageline)
    {
        invalidate();
        InstrumentPanelMessageLine instrumentpanelmessageline = getMessageBox();
        instrumentpanelmessageline.add(messageline);
        validate();
    }

    public Toolbar removeToolbar(Toolbar toolbar)
    {
        Toolbar toolbar1 = toolbar;
        invalidate();
        if(haveControls())
        {
            controls.remove(toolbar);
            if(!haveToolbars() && controls.hasActivityMonitor() && hasLocator())
                moveActivityMonitorControlsToLocator();
        }
        validate();
        return toolbar1;
    }

    public Locator removeLocator()
    {
        Locator locator = null;
        invalidate();
        if(locatorBox != null && locatorBox.hasLocator())
            locator = locatorBox.removeLocator();
        validate();
        return locator;
    }

    public MessageLine removeMessageLine()
    {
        MessageLine messageline = null;
        invalidate();
        if(messageBox != null && messageBox.hasMessageLine())
            messageline = messageBox.removeMessageLine();
        validate();
        return messageline;
    }

    public ActivityMonitor removeActivityMonitor()
    {
        ActivityMonitor activitymonitor = null;
        invalidate();
        if(controls != null && controls.hasActivityMonitor())
            activitymonitor = controls.removeActivityMonitor();
        else
        if(locatorBox != null && locatorBox.hasActivityMonitor())
            activitymonitor = locatorBox.removeActivityMonitor();
        else
        if(messageBox != null && messageBox.hasActivityMonitor())
            activitymonitor = messageBox.removeActivityMonitor();
        validate();
        return activitymonitor;
    }

    public ClockPanel removeClock()
    {
        ClockPanel clockpanel = null;
        invalidate();
        if(messageBox != null && messageBox.hasClock())
            clockpanel = messageBox.removeClock();
        if(clockpanel != null)
        {
            Container container = clockpanel.getParent();
            if(container != null)
                container.remove(clockpanel);
        }
        validate();
        return clockpanel;
    }

    public void checkClock(String s)
    {
        if(s != null && "on".equalsIgnoreCase(s))
        {
            if(!hasClock())
            {
                startClock();
                setVisible(true);
                return;
            }
        } else
        {
            stopClock();
        }
    }

    protected void startClock()
    {
        ClockPanel clockpanel = new ClockPanel(getParent().getFont());
        add(clockpanel);
        clockpanel.start();
        hasClock = true;
    }

    protected void stopClock()
    {
        ClockPanel clockpanel = removeClock();
        if(clockpanel != null)
            clockpanel.stop();
        hasClock = false;
    }

    protected void moveActivityMonitorLocatorToControls()
    {
        ActivityMonitor activitymonitor = locatorBox.removeActivityMonitor();
        if(activitymonitor != null)
            getControls().add(activitymonitor);
    }

    protected void moveActivityMonitorControlsToLocator()
    {
        ActivityMonitor activitymonitor = controls.removeActivityMonitor();
        if(activitymonitor != null)
            getLocatorBox().add(activitymonitor);
    }

    public Insets getInsets()
    {
        return new Insets(0, 0, 0, 0);
    }

    InstrumentPanelControls controls;
    InstrumentPanelLocator locatorBox;
    InstrumentPanelMessageLine messageBox;
    boolean hasClock;
}
