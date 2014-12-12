// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GeneralPreferencesDialog.java

package sunw.hotjava.applets;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import sunw.hotjava.HJFrame;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.applet.PrefsPanel;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.*;

// Referenced classes of package sunw.hotjava.applets:
//            GeneralPreferencesDialog

class genButtonPanel extends NPanel
    implements ActionListener
{

    public genButtonPanel(Panel panel, HJBProperties hjbproperties, String s, int i, GeneralPreferencesDialog generalpreferencesdialog)
    {
        super(i);
        cmdButtons = new UIHJButton[4];
        contentDeck = panel;
        properties = hjbproperties;
        propsName = s;
        parent = generalpreferencesdialog;
        setLayout(new FlowLayout(2, 5, 15));
        addButtons();
        validate();
    }

    public Dimension getMinimumSize()
    {
        return new Dimension(getParent().getSize().width, 50);
    }

    public Dimension getPreferredSize()
    {
        return getMinimumSize();
    }

    public void addButtons()
    {
        String s = "prefsdialog";
        UIHJButtonGroup uihjbuttongroup = new UIHJButtonGroup(properties);
        cmdButtons[0] = new UIHJButton(s + ".ok", properties);
        cmdButtons[0].setActionCommand("ok");
        cmdButtons[1] = new UIHJButton(s + ".apply", properties);
        cmdButtons[1].setActionCommand("apply");
        cmdButtons[2] = new UIHJButton(s + ".cancel", properties);
        cmdButtons[2].setActionCommand("cancel");
        cmdButtons[3] = new UIHJButton(s + ".help", properties);
        cmdButtons[3].setActionCommand("help");
        for(int i = 0; i < cmdButtons.length; i++)
        {
            add(cmdButtons[i]);
            cmdButtons[i].addActionListener(this);
            uihjbuttongroup.addButtonToGroup(cmdButtons[i]);
            cmdButtons[i].setFont(parent.regularBold);
        }

    }

    public void doDefaultAction()
    {
        if(defaultButton == null)
        {
            for(int i = 0; i < cmdButtons.length; i++)
            {
                if(!cmdButtons[i].isDefault())
                    continue;
                defaultButton = cmdButtons[i];
                break;
            }

        }
        ActionEvent actionevent = new ActionEvent(defaultButton, 0, defaultButton.getActionCommand());
        actionPerformed(actionevent);
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        if(actionevent.getActionCommand().equals("ok"))
        {
            apply();
            cancelDialog();
            return;
        }
        if(actionevent.getActionCommand().equals("apply"))
        {
            apply();
            return;
        }
        if(actionevent.getActionCommand().equals("cancel"))
        {
            cancelDialog();
            return;
        }
        if(actionevent.getActionCommand().equals("help"))
            help();
    }

    private void apply()
    {
        boolean flag = false;
        int i = contentDeck.getComponentCount();
        for(int j = 0; j < i; j++)
        {
            Component component = contentDeck.getComponent(j);
            if(component instanceof PrefsPanel)
            {
                PrefsPanel prefspanel = (PrefsPanel)component;
                if(prefspanel.apply() == 2)
                    flag = true;
            }
        }

        HJFrame ahjframe[] = HJWindowManager.getHJWindowManager().getAllFrames();
        if(ahjframe != null && flag)
        {
            for(int k = 0; k < ahjframe.length; k++)
                ahjframe[k].reset();

        }
    }

    private void cancelDialog()
    {
        GeneralPreferencesDialog.singleton = null;
        ((Window)getParent()).dispose();
    }

    private void help()
    {
        try
        {
            HJWindowManager.getHJWindowManager().openFrame("hotjava_help", new URL(properties.getProperty("general.panel." + parent.currentPanelDisplayed + ".help")));
            return;
        }
        catch(MalformedURLException malformedurlexception)
        {
            malformedurlexception.printStackTrace();
        }
    }

    private Panel contentDeck;
    private HJBProperties properties;
    private GeneralPreferencesDialog parent;
    private String propsName;
    private static final int OK = 0;
    private static final int APPLY = 1;
    private static final int CANCEL = 2;
    private static final int HELP = 3;
    private UIHJButton cmdButtons[];
    private UIHJButton defaultButton;
}
