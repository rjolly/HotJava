// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletsJS.java

package sunw.hotjava.applets;

import java.awt.*;
import java.awt.event.*;
import sunw.hotjava.applet.PrefsPanel;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.MultiLineLabel;

public class AppletsJS extends Panel
    implements PrefsPanel, ItemListener
{

    public AppletsJS(KeyListener keylistener1)
    {
        initialized = false;
        keylistener = keylistener1;
        Font font = Font.decode(properties.getProperty("security.preference.font.regular", ""));
        Font font1 = Font.decode(properties.getProperty("security.preference.font.headings", ""));
        initialized = true;
        GridBagLayout gridbaglayout = new GridBagLayout();
        setLayout(gridbaglayout);
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridwidth = 10;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbagconstraints.anchor = 18;
        gridbagconstraints.fill = 0;
        gridbagconstraints.insets = new Insets(10, 10, 10, 10);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        heading = new MultiLineLabel(properties.getProperty("security.preference.applets.heading", ""), font1);
        add(heading, gridbagconstraints);
        gridbagconstraints.insets = new Insets(5, 10, 5, 10);
        MultiLineLabel multilinelabel = new MultiLineLabel(properties.getProperty("security.preference.applets.body", ""), font);
        gridbagconstraints.gridy = -1;
        add(multilinelabel, gridbagconstraints);
        String s = properties.getProperty("hotjava.default.security", "Safe");
        String s1 = properties.getProperty("hotjava.appletsJS.enableApplets", "");
        String s2 = properties.getProperty("hotjava.appletsJS.enableJavaScript", "");
        applets = new Checkbox(properties.getProperty("security.preference.applets.checkBox0", ""), s1.equals("true"));
        applets.setFont(font);
        applets.addKeyListener(keylistener1);
        applets.addItemListener(this);
        javaScript = new Checkbox(properties.getProperty("security.preference.applets.checkBox1", ""), s2.equals("true"));
        javaScript.setFont(font);
        javaScript.addKeyListener(keylistener1);
        javaScript.addItemListener(this);
        add(applets, gridbagconstraints);
        add(javaScript, gridbagconstraints);
        mlabel1 = new MultiLineLabel(properties.getProperty("security.preference.applets.label0", ""), font);
        add(mlabel1, gridbagconstraints);
        cboxgroup = new CheckboxGroup();
        cbox1 = new Checkbox(properties.getProperty("security.preference.applets.checkBox2", ""), s.equals("Safe"), cboxgroup);
        cbox1.addKeyListener(keylistener1);
        cbox1.setFont(font);
        add(cbox1, gridbagconstraints);
        l1 = new MultiLineLabel(properties.getProperty("security.preference.applets.label1", ""), font);
        gridbagconstraints.insets = new Insets(-7, 30, 5, -45);
        l1.setMarginWidth(30);
        add(l1, gridbagconstraints);
        gridbagconstraints.insets = new Insets(5, 10, 5, 10);
        cbox2 = new Checkbox(properties.getProperty("security.preference.applets.checkBox3", ""), s.equals("Medium"), cboxgroup);
        cbox2.addKeyListener(keylistener1);
        cbox2.setFont(font);
        add(cbox2, gridbagconstraints);
        l2 = new MultiLineLabel(properties.getProperty("security.preference.applets.label2", ""), font);
        gridbagconstraints.insets = new Insets(-7, 30, 5, -45);
        l2.setMarginWidth(30);
        add(l2, gridbagconstraints);
        gridbagconstraints.insets = new Insets(5, 10, 5, 10);
        gridbagconstraints.weighty = 1.0D;
        gridbagconstraints.fill = 3;
        gridbagconstraints.gridheight = 0;
        MultiLineLabel multilinelabel1 = new MultiLineLabel(properties.getProperty("security.preference.applets.override", ""), font);
        add(multilinelabel1, gridbagconstraints);
        itemStateChanged(null);
    }

    public void itemStateChanged(ItemEvent itemevent)
    {
        boolean flag = applets.getState() || javaScript.getState();
        cbox1.setEnabled(flag);
        cbox2.setEnabled(flag);
        l1.setEnabled(flag);
        l2.setEnabled(flag);
        if(!flag)
        {
            cboxgroup.setSelectedCheckbox(null);
            return;
        }
        if(cboxgroup.getSelectedCheckbox() == null)
            cboxgroup.setSelectedCheckbox(cbox1);
    }

    public int apply()
    {
        if(initialized)
        {
            properties.put("hotjava.appletsJS.enableApplets", applets.getState() ? "true" : "false");
            properties.put("hotjava.appletsJS.enableJavaScript", javaScript.getState() ? "true" : "false");
            properties.put("hotjava.default.security", cboxgroup.getSelectedCheckbox() != cbox1 ? "Medium" : "Safe");
        }
        return 1;
    }

    public void stop()
    {
        applets.removeKeyListener(keylistener);
        javaScript.removeKeyListener(keylistener);
        cbox1.removeKeyListener(keylistener);
        cbox2.removeKeyListener(keylistener);
    }

    private static final HJBProperties properties = HJBProperties.getHJBProperties("hjbrowser");
    private static final String propsname = "security.preference.applets.";
    MultiLineLabel heading;
    MultiLineLabel mlabel1;
    CheckboxGroup cboxgroup;
    Checkbox cbox1;
    Checkbox cbox2;
    Checkbox applets;
    Checkbox javaScript;
    private boolean initialized;
    private static final String restricted = "Safe";
    private static final String askFirst = "Medium";
    private KeyListener keylistener;
    private MultiLineLabel l1;
    private MultiLineLabel l2;

}
