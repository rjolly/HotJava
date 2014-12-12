// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ClockPanel.java

package sunw.hotjava.ui;

import java.awt.*;
import java.util.Observable;

// Referenced classes of package sunw.hotjava.ui:
//            Clock, ClockTicker

public class ClockPanel extends Panel
{

    public ClockPanel(Font font)
    {
        clockFont = font;
        if(clockTicker == null)
        {
            clockTicker = new ClockTicker();
            clockTicker.start();
        }
        clock = new Clock(font);
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = getConstraints();
        gridbaglayout.setConstraints(clock, gridbagconstraints);
        setLayout(gridbaglayout);
        clockTicker.addObserver(clock);
        add(clock);
    }

    private GridBagConstraints getConstraints()
    {
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.anchor = 13;
        gridbagconstraints.fill = 0;
        gridbagconstraints.weightx = 0.0D;
        return gridbagconstraints;
    }

    public void start()
    {
        if(clock != null)
            clock.start();
    }

    public void stop()
    {
        if(clock != null)
        {
            clock.stop();
            clock = null;
        }
        if(clockTicker != null)
        {
            clockTicker.stop();
            clockTicker = null;
        }
    }

    static ClockTicker clockTicker;
    Clock clock;
    int width;
    Font clockFont;
}
