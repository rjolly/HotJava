// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OKApplet.java

package sunw.hotjava.applets;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import sunw.hotjava.HJFrame;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.UIHJButton;

// Referenced classes of package sunw.hotjava.applets:
//            HotJavaApplet

public class OKApplet extends HotJavaApplet
    implements ActionListener
{

    public void init()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        apply = new UIHJButton("applylabel", hjbproperties);
        apply.setActionCommand("apply");
        apply.addActionListener(this);
        add(apply);
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        String s = actionevent.getActionCommand();
        if(s.equals("apply"))
        {
            HJWindowManager hjwindowmanager = HJWindowManager.getHJWindowManager();
            HJFrame hjframe = hjwindowmanager.getLastFocusHolder();
            if(hjframe.getGotoMenu() == null)
                hjwindowmanager.closeFrame(hjframe);
        }
    }

    public OKApplet()
    {
    }

    private UIHJButton apply;
}
