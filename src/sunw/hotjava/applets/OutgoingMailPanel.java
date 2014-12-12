// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OutgoingMailPanel.java

package sunw.hotjava.applets;

import java.awt.*;
import java.awt.event.KeyListener;
import sunw.hotjava.applet.PrefsPanel;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.MultiLineLabel;

class OutgoingMailPanel extends Panel
    implements PrefsPanel
{

    public OutgoingMailPanel(KeyListener keylistener1, String s, Font font, Font font1, Font font2)
    {
        props = HJBProperties.getHJBProperties("hjbrowser");
        keylistener = keylistener1;
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        setLayout(gridbaglayout);
        gridbagconstraints.fill = 0;
        gridbagconstraints.anchor = 17;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        MultiLineLabel multilinelabel = new MultiLineLabel(s);
        multilinelabel.setFont(font2);
        add(multilinelabel, gridbagconstraints);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 2;
        gridbagconstraints.gridwidth = 2;
        MultiLineLabel multilinelabel1 = new MultiLineLabel(props.getProperty(propname + ".infotext.label"));
        multilinelabel1.setFont(font);
        add(multilinelabel1, gridbagconstraints);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 3;
        gridbagconstraints.gridwidth = 1;
        MultiLineLabel multilinelabel2 = new MultiLineLabel(props.getProperty(propname + ".emailadd.label"));
        multilinelabel2.setFont(font);
        add(multilinelabel2, gridbagconstraints);
        gridbagconstraints.gridx = 1;
        mailField = new TextField(40);
        mailField.addKeyListener(keylistener1);
        mailField.setFont(font);
        gridbagconstraints.weightx = 1.0D;
        add(mailField, gridbagconstraints);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 4;
        gridbagconstraints.weightx = 0.0D;
        MultiLineLabel multilinelabel3 = new MultiLineLabel(props.getProperty(propname + ".mailsmtp.label"));
        multilinelabel3.setFont(font);
        add(multilinelabel3, gridbagconstraints);
        gridbagconstraints.gridx = 1;
        serverField = new TextField(40);
        serverField.addKeyListener(keylistener1);
        serverField.setFont(font);
        gridbagconstraints.weightx = 1.0D;
        add(serverField, gridbagconstraints);
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.gridwidth = 2;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 5;
        gridbagconstraints.weighty = 1.0D;
        gridbagconstraints.anchor = 18;
        MultiLineLabel multilinelabel4 = new MultiLineLabel(props.getProperty(propname + ".ifyoudonot.label"));
        multilinelabel4.setFont(font);
        add(multilinelabel4, gridbagconstraints);
        init();
    }

    private void init()
    {
        mailField.setText(props.getProperty("user.fromaddr", ""));
        serverField.setText(props.getProperty("mail.host", ""));
    }

    public void stop()
    {
        mailField.removeKeyListener(keylistener);
        serverField.removeKeyListener(keylistener);
    }

    public int apply()
    {
        updateProp("user.fromaddr", mailField.getText());
        updateProp("mail.host", serverField.getText());
        return 1;
    }

    private boolean updateProp(String s, String s1)
    {
        String s2 = props.getProperty(s);
        if(!s1.equals(s2))
        {
            if(s1.length() == 0)
                props.remove(s);
            else
                props.put(s, s1);
            return true;
        } else
        {
            return false;
        }
    }

    private static String propname = "general.outgomail.preferences";
    private HJBProperties props;
    private TextField mailField;
    private TextField serverField;
    private KeyListener keylistener;

}
