// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ClockPanel.java

package sunw.hotjava.applets;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.KeyListener;
import sunw.hotjava.applet.PrefsPanel;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.Clock;
import sunw.hotjava.ui.MultiLineLabel;
import sunw.hotjava.ui.UserCheckbox;
import sunw.hotjava.ui.UserLabel;

class ClockPanel extends Panel
    implements PrefsPanel, Runnable
{

    public ClockPanel(KeyListener keylistener1, String s, Font font, Font font1, Font font2)
    {
        cboxVector = new java.util.Vector(2, 2);
        running = true;
        props = HJBProperties.getHJBProperties("hjbrowser");
        keylistener = keylistener1;
        GridBagLayout gridbaglayout = new GridBagLayout();
        c = new GridBagConstraints();
        setLayout(gridbaglayout);
        c.insets = new Insets(10, 10, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0D;
        c.weighty = 1.0D;
        c.anchor = 17;
        MultiLineLabel multilinelabel = new MultiLineLabel(s);
        multilinelabel.setFont(font2);
        multilinelabel.setMarginWidth(0);
        multilinelabel.setMarginHeight(0);
        add(multilinelabel, c);
        c.gridy = 1;
        c.gridx = 1;
        c.insets = new Insets(0, 10, 0, 0);
        showTimeCB = addCheckbox("showtime", keylistener1);
        c.gridy = 2;
        c.fill = 1;
        c.weightx = 4D;
        c.weighty = 4D;
        timefmts = new List(8);
        add(timefmts, c);
        int i = props.getInteger("time.fmt.count", 0);
        for(int j = 0; j < i; j++)
        {
            String s1 = props.getProperty("time.fmt.label." + (j + 1));
            if(s1 != null)
                timefmts.add(s1);
        }

        c.fill = 0;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0D;
        c.weighty = 1.0D;
        showDateCB = addCheckbox("showdate", keylistener1);
        c.gridy = 2;
        c.fill = 1;
        c.weightx = 4D;
        c.weighty = 4D;
        datefmts = new List(8);
        add(datefmts, c);
        i = props.getInteger("date.fmt.count", 0);
        for(int k = 0; k < i; k++)
        {
            String s2 = props.getProperty("date.fmt.label." + (k + 1));
            if(s2 != null)
                datefmts.add(s2);
        }

        c.fill = 0;
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(20, 10, 0, 0);
        c.fill = 2;
        c.gridwidth = 0;
        sample = addLabel("sample");
        init();
    }

    private UserLabel addLabel(String s)
    {
        UserLabel userlabel = new UserLabel(propname + s);
        add(userlabel, c);
        return userlabel;
    }

    private UserCheckbox addCheckbox(String s, CheckboxGroup checkboxgroup, KeyListener keylistener1)
    {
        UserCheckbox usercheckbox = new UserCheckbox(propname + s, checkboxgroup, props);
        add(usercheckbox, c);
        usercheckbox.addKeyListener(keylistener1);
        cboxVector.addElement(usercheckbox);
        return usercheckbox;
    }

    private UserCheckbox addCheckbox(String s, KeyListener keylistener1)
    {
        UserCheckbox usercheckbox = new UserCheckbox(propname + s, props);
        add(usercheckbox, c);
        usercheckbox.addKeyListener(keylistener1);
        cboxVector.addElement(usercheckbox);
        return usercheckbox;
    }

    private void removeCheckboxes()
    {
        int i = cboxVector.size();
        for(int j = 0; j < i; j++)
            ((Component)cboxVector.elementAt(j)).removeKeyListener(keylistener);

    }

    private void init()
    {
        props.getProperty("hotjava.clock", "on").equals("on");
        boolean flag = props.getProperty("hotjava.time", "on").equals("on");
        boolean flag1 = props.getProperty("hotjava.date", "off").equals("on");
        String s = props.getProperty("hotjava.time.format", "h:mm a");
        String s1 = props.getProperty("hotjava.date.format", "mm/dd/yy");
        int i = props.getInteger("time.fmt.count", 0);
        int j = 0;
        for(int k = 0; k < i; k++)
        {
            String s2 = props.getProperty("time.fmt." + (k + 1));
            if(!s.equals(s2))
                continue;
            j = k;
            break;
        }

        timefmts.select(j);
        j = 0;
        i = props.getInteger("date.fmt.count", 0);
        for(int l = 0; l < i; l++)
        {
            String s3 = props.getProperty("date.fmt." + (l + 1));
            if(!s1.equals(s3))
                continue;
            j = l;
            break;
        }

        datefmts.select(j);
        showTimeCB.setState(flag);
        showDateCB.setState(flag1);
        running = true;
        (new Thread(this)).start();
    }

    private String getFormat()
    {
        String s = "";
        String s1 = "";
        if(showTimeCB.getState())
            s = props.getProperty("time.fmt." + (timefmts.getSelectedIndex() + 1), "");
        if(showDateCB.getState())
            s1 = props.getProperty("date.fmt." + (datefmts.getSelectedIndex() + 1), "");
        String s2;
        if(s1.length() != 0 && s.length() != 0)
            s2 = s1 + " " + s;
        else
            s2 = s1 + s;
        return s2;
    }

    public boolean updateCB(String s, Checkbox checkbox, String s1)
    {
        String s2 = props.getProperty(s, s1);
        String s3 = checkbox.getState() ? "on" : "off";
        if(!s2.equals(s3))
        {
            props.put(s, s3);
            return true;
        } else
        {
            return false;
        }
    }

    public int apply()
    {
        boolean flag = false;
        String s = props.getProperty("time.fmt." + (timefmts.getSelectedIndex() + 1), "");
        String s1 = props.getProperty("date.fmt." + (datefmts.getSelectedIndex() + 1), "");
        String s2 = props.getProperty("hotjava.clock", "on");
        String s3 = !showTimeCB.getState() && !showDateCB.getState() ? "off" : "on";
        if(!s2.equals(s3))
        {
            props.put("hotjava.clock", s3);
            flag = true;
        }
        flag |= updateCB("hotjava.time", showTimeCB, "on");
        flag |= updateCB("hotjava.date", showDateCB, "off");
        String s4 = props.getProperty("hotjava.clock.format");
        s3 = getFormat();
        if(!s3.equals(s4))
        {
            flag |= true;
            props.put("hotjava.clock.format", s3);
        }
        s4 = props.getProperty("hotjava.time.format");
        if(!s.equals(s4))
            props.put("hotjava.time.format", s);
        s4 = props.getProperty("hotjava.date.format");
        if(!s1.equals(s4))
            props.put("hotjava.date.format", s1);
        return !flag ? 1 : 2;
    }

    public void stop()
    {
        running = false;
        removeCheckboxes();
        cboxVector = null;
    }

    public void run()
    {
        String s2 = null;
        while(running) 
        {
            String s = getFormat();
            String s1 = Clock.formatTime(s);
            if(!s1.equals(s2))
            {
                sample.setText(s1);
                s2 = s1;
            }
            try
            {
                Thread.sleep(250L);
            }
            catch(Exception _ex) { }
        }
    }

    private static String propname = "general.clock.";
    private HJBProperties props;
    private GridBagConstraints c;
    private UserCheckbox showTimeCB;
    private UserCheckbox showDateCB;
    private List timefmts;
    private List datefmts;
    private java.util.Vector cboxVector;
    private UserLabel sample;
    private boolean running;
    private KeyListener keylistener;

}
