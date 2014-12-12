// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotListFrame.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            UserDialog, FolderDialog, FolderPanel, HotListBuffer, 
//            HotListFrame, ListPanel, PanelWithInsets, UIHJButton, 
//            UIHJButtonGroup, UserLabel, UserTextField

class BookmarkDialog extends UserDialog
{
    private final class ActionEventsListener
        implements ActionListener
    {

        public void actionPerformed(ActionEvent actionevent)
        {
            action(actionevent);
        }

        ActionEventsListener()
        {
        }
    }


    public BookmarkDialog(String s, String s1, String s2, String s3, Frame frame)
    {
        super(s + s1, frame, true, HJBProperties.getHJBProperties("hjbrowser"));
        titleString = "";
        URLString = "";
        returnString = "";
        ActionEventsListener actioneventslistener = new ActionEventsListener();
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        PanelWithInsets panelwithinsets = new PanelWithInsets();
        panelwithinsets.setLayout(new BorderLayout());
        panelwithinsets.add("West", URLLabel = new UserLabel(s + s2));
        panelwithinsets.add("East", URLTextField = new UserTextField(s + ".dialog.textfield"));
        add("North", panelwithinsets);
        PanelWithInsets panelwithinsets1 = new PanelWithInsets();
        panelwithinsets1.setLayout(new BorderLayout());
        panelwithinsets1.add("West", titleLabel = new UserLabel(s + s3));
        panelwithinsets1.add("East", titleTextField = new UserTextField(s + ".dialog.textfield"));
        add("Center", panelwithinsets1);
        UIHJButtonGroup uihjbuttongroup = new UIHJButtonGroup(hjbproperties);
        PanelWithInsets panelwithinsets2 = new PanelWithInsets();
        panelwithinsets2.setLayout(new FlowLayout(2));
        panelwithinsets2.add(ok = new UIHJButton(s + ".dialogOK", hjbproperties));
        panelwithinsets2.add(cancel = new UIHJButton(s + ".dialogCancel", hjbproperties));
        uihjbuttongroup.addButtonToGroup(ok);
        uihjbuttongroup.addButtonToGroup(cancel);
        add("South", panelwithinsets2);
        setSize(getPreferredSize());
        centerOnScreen();
        ok.addActionListener(actioneventslistener);
        cancel.addActionListener(actioneventslistener);
        URLTextField.addActionListener(actioneventslistener);
        titleTextField.addActionListener(actioneventslistener);
        WindowAdapter windowadapter = new WindowAdapter() {

            public void windowOpened(WindowEvent windowevent)
            {
                URLTextField.setText(URLString);
                titleTextField.setText(titleString);
                URLTextField.selectAll();
            }

            public void windowClosing(WindowEvent windowevent)
            {
                setVisible(false);
            }

        }
;
        addWindowListener(windowadapter);
        FocusAdapter focusadapter = new FocusAdapter() {

            public void focusGained(FocusEvent focusevent)
            {
                URLTextField.requestFocus();
            }

        }
;
        URLLabel.addFocusListener(focusadapter);
        pack();
    }

    public void setDefaultURL(String s)
    {
        if(s != null)
            URLString = s;
    }

    public void setDefaultTitle(String s)
    {
        if(s != null)
            titleString = s;
    }

    public String getPlaceName()
    {
        setVisible(true);
        dispose();
        return returnString;
    }

    public boolean action(ActionEvent actionevent)
    {
        actionevent.getActionCommand();
        Object obj = actionevent.getSource();
        if(obj == URLTextField)
        {
            URLString = URLTextField.getText();
            if(URLString == null)
                URLString = "";
            titleTextField.requestFocus();
            titleTextField.selectAll();
        }
        if(obj == titleTextField || obj == ok)
        {
            titleString = titleTextField.getText();
            if(titleString == null)
                titleString = "";
            URLString = URLTextField.getText();
            if(URLString == null)
                URLString = "";
            returnString = URLString + "|" + titleString;
            setVisible(false);
            return true;
        }
        if(obj == cancel)
        {
            returnString = null;
            setVisible(false);
            return true;
        } else
        {
            return true;
        }
    }

    UserLabel URLLabel;
    UserLabel titleLabel;
    UserTextField URLTextField;
    UserTextField titleTextField;
    String titleString;
    String URLString;
    String returnString;
    UIHJButton ok;
    UIHJButton cancel;
}
