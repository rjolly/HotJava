// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SignedApplets.java

package sunw.hotjava.applets;

import java.awt.*;
import java.awt.event.KeyListener;
import sunw.hotjava.applet.PrefsPanel;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.MultiLineLabel;

public class SignedApplets extends Panel
    implements PrefsPanel
{

    private String getProp(String s)
    {
        return properties.getProperty(s, "");
    }

    public SignedApplets(KeyListener keylistener1)
    {
        labels = new MultiLineLabel[3];
        cbox = new Checkbox[3];
        initialized = false;
        keylistener = keylistener1;
        Font font = Font.decode(getProp("security.preference.font.regular"));
        Font font1 = Font.decode(getProp("security.preference.font.headings"));
        initialized = true;
        GridBagLayout gridbaglayout = new GridBagLayout();
        setLayout(gridbaglayout);
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridwidth = 5;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbagconstraints.anchor = 18;
        gridbagconstraints.fill = 0;
        gridbagconstraints.insets = new Insets(10, 0, 10, 0);
        gridbagconstraints.ipadx = 0;
        gridbagconstraints.ipady = 0;
        String s = getProp("security.preference.signedApplets.heading");
        heading = new MultiLineLabel(s, font1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        add(heading, gridbagconstraints);
        s = getProp("security.preference.signedApplets.body");
        mlabel1 = new MultiLineLabel(s, font);
        mlabel1.setMarginWidth(20);
        gridbagconstraints.gridy = 1;
        gridbagconstraints.gridheight = 2;
        add(mlabel1, gridbagconstraints);
        String s1 = getProp("hotjava.default.signed.security");
        if(s1.length() == 0)
            s1 = "Medium";
        gridbagconstraints.gridheight = 1;
        cboxgroup = new CheckboxGroup();
        for(int i = 0; i < cbox.length; i++)
        {
            cbox[i] = new Checkbox(getProp("security.preference.signedApplets.checkbox" + (i + 1)), s1.equals(policy[i]), cboxgroup);
            cbox[i].addKeyListener(keylistener1);
            cbox[i].setFont(font);
            labels[i] = new MultiLineLabel(getProp("security.preference.signedApplets.labelbox" + (i + 1)), font);
            labels[i].setMarginWidth(20);
            gridbagconstraints.gridy = 3 + i * 2;
            gridbagconstraints.insets = new Insets(10, 0, 10, 0);
            add(cbox[i], gridbagconstraints);
            gridbagconstraints.gridy = 4 + i * 2;
            gridbagconstraints.insets = new Insets(-12, 20, 10, 0);
            add(labels[i], gridbagconstraints);
        }

        s = getProp("security.preference.applets.override");
        MultiLineLabel multilinelabel = new MultiLineLabel(s, font);
        gridbagconstraints.fill = 0;
        gridbagconstraints.gridy = 9;
        gridbagconstraints.insets = new Insets(10, 0, 10, 0);
        gridbagconstraints.weighty = 1.0D;
        gridbagconstraints.fill = 3;
        gridbagconstraints.gridheight = 0;
        add(multilinelabel, gridbagconstraints);
    }

    public int apply()
    {
        if(initialized)
        {
            Checkbox checkbox = cboxgroup.getSelectedCheckbox();
            for(int i = 0; i < cbox.length; i++)
            {
                if(checkbox != cbox[i])
                    continue;
                properties.put("hotjava.default.signed.security", policy[i]);
                break;
            }

        }
        return 1;
    }

    public void stop()
    {
        for(int i = 0; i < cbox.length; i++)
            cbox[i].removeKeyListener(keylistener);

    }

    private static final HJBProperties properties = HJBProperties.getHJBProperties("hjbrowser");
    private static final String propsname = "security.preference.signedApplets.";
    MultiLineLabel heading;
    MultiLineLabel body;
    MultiLineLabel mlabel1;
    MultiLineLabel labels[];
    Checkbox cbox[];
    CheckboxGroup cboxgroup;
    private boolean initialized;
    private KeyListener keylistener;
    private static final String policy[] = {
        "Untrusted", "High", "Medium"
    };

}
