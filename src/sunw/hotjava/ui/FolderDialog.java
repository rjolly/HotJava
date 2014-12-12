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
//            UserDialog, BookmarkDialog, FolderPanel, HotListBuffer, 
//            HotListFrame, ListPanel, PanelWithInsets, UIHJButton, 
//            UIHJButtonGroup, UserLabel, UserTextField

class FolderDialog extends UserDialog
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


    public FolderDialog(String s, String s1, String s2, Frame frame)
    {
        super(s + s1, frame, true, HJBProperties.getHJBProperties("hjbrowser"));
        textFieldString = "";
        ActionEventsListener actioneventslistener = new ActionEventsListener();
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        PanelWithInsets panelwithinsets = new PanelWithInsets();
        panelwithinsets.setLayout(new BorderLayout());
        panelwithinsets.add("West", labelField = new UserLabel(s + s2));
        panelwithinsets.add("East", textField = new UserTextField(s + ".dialog.textfield"));
        add("Center", panelwithinsets);
        UIHJButtonGroup uihjbuttongroup = new UIHJButtonGroup(hjbproperties);
        PanelWithInsets panelwithinsets1 = new PanelWithInsets();
        panelwithinsets1.setLayout(new FlowLayout(2));
        panelwithinsets1.add(ok = new UIHJButton(s + ".dialogOK", hjbproperties));
        panelwithinsets1.add(cancel = new UIHJButton(s + ".dialogCancel", hjbproperties));
        uihjbuttongroup.addButtonToGroup(ok);
        uihjbuttongroup.addButtonToGroup(cancel);
        add("South", panelwithinsets1);
        setSize(getPreferredSize());
        centerOnScreen();
        ok.addActionListener(actioneventslistener);
        cancel.addActionListener(actioneventslistener);
        textField.addActionListener(actioneventslistener);
        WindowAdapter windowadapter = new WindowAdapter() {

            public void windowOpened(WindowEvent windowevent)
            {
                textField.setText(textFieldString);
                textField.selectAll();
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
                textField.requestFocus();
            }

        }
;
        labelField.addFocusListener(focusadapter);
        pack();
    }

    public void setDefaultText(String s)
    {
        if(s != null)
            textFieldString = s;
    }

    public String getFolderName()
    {
        setVisible(true);
        dispose();
        return textFieldString;
    }

    public boolean action(ActionEvent actionevent)
    {
        actionevent.getActionCommand();
        Object obj = actionevent.getSource();
        if((obj instanceof UserTextField) || obj == ok)
        {
            textFieldString = textField.getText();
            setVisible(false);
            return true;
        }
        if(obj == cancel)
        {
            textFieldString = null;
            setVisible(false);
            return true;
        } else
        {
            return true;
        }
    }

    UserLabel labelField;
    UserTextField textField;
    String textFieldString;
    UIHJButton ok;
    UIHJButton cancel;
}
