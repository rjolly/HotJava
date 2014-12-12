// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletsJavaScripts.java

package sunw.hotjava.applets;

import java.awt.*;
import java.awt.event.KeyListener;
import sunw.hotjava.applet.PrefsPanel;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.MultiLineLabel;

public class AppletsJavaScripts extends Panel
    implements PrefsPanel
{

    public AppletsJavaScripts(KeyListener keylistener)
    {
        props = HJBProperties.getHJBProperties("hjbrowser");
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        setLayout(new GridBagLayout());
        MultiLineLabel amultilinelabel[] = new MultiLineLabel[3];
        Font font = Font.decode(props.getProperty(propname + "font." + "regular"));
        Font font1 = Font.decode(props.getProperty(propname + "font." + "headings"));
        for(int i = 1; i < 4; i++)
            amultilinelabel[i - 1] = new MultiLineLabel(props.getProperty(propname + "appletsjavascript.label" + i), font);

        MultiLineLabel multilinelabel = new MultiLineLabel(props.getProperty(propname + "appletsjavascript.heading"), font1);
        gridbagconstraints.gridx = gridbagconstraints.gridy = 0;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 1.0D;
        gridbagconstraints.insets = new Insets(10, 10, 10, 10);
        gridbagconstraints.fill = 0;
        gridbagconstraints.anchor = 18;
        add(multilinelabel, gridbagconstraints);
        for(int j = 1; j < 3; j++)
        {
            gridbagconstraints.gridy = j;
            add(amultilinelabel[j - 1], gridbagconstraints);
        }

        gridbagconstraints.weighty = 97D;
        gridbagconstraints.gridy = 3;
        add(amultilinelabel[2], gridbagconstraints);
    }

    public int apply()
    {
        return 0;
    }

    public void stop()
    {
    }

    private static String propname = "security.preference.";
    private HJBProperties props;

}
