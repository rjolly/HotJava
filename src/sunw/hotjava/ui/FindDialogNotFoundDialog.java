// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FindStringDialog.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            UserDialog, FindDialogButtonPane, FindDialogFormPane, FindStringDialog, 
//            UIHJButton

class FindDialogNotFoundDialog extends UserDialog
    implements ActionListener
{

    FindDialogNotFoundDialog(String s, Frame frame, boolean flag, HJBProperties hjbproperties)
    {
        super(s, frame, flag, hjbproperties);
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        setLayout(gridbaglayout);
        notFoundLabel = new Label(hjbproperties.getProperty("finddialog.not.found.label"));
        okButton = new UIHJButton("find.dialog.subdialog.notFound", true, hjbproperties);
        gridbagconstraints.anchor = 10;
        gridbagconstraints.fill = 0;
        gridbagconstraints.ipadx = 0;
        gridbagconstraints.ipady = 0;
        gridbagconstraints.insets = new Insets(10, 10, 10, 10);
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 1.0D;
        constrain(notFoundLabel, gridbaglayout, gridbagconstraints, 0, 0, 1, 1);
        gridbagconstraints.weighty = 0.0D;
        gridbagconstraints.insets = new Insets(0, 0, 5, 5);
        gridbagconstraints.anchor = 13;
        constrain(okButton, gridbaglayout, gridbagconstraints, 0, 1, 1, 1);
        add(notFoundLabel);
        add(okButton);
        okButton.addActionListener(this);
        okButton.requestFocus();
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        hide();
    }

    private void constrain(Component component, GridBagLayout gridbaglayout, GridBagConstraints gridbagconstraints, int i, int j, int k, int l)
    {
        gridbagconstraints.gridx = i;
        gridbagconstraints.gridy = j;
        gridbagconstraints.gridwidth = k;
        gridbagconstraints.gridheight = l;
        gridbaglayout.setConstraints(component, gridbagconstraints);
    }

    public void processKeyEvent(KeyEvent keyevent)
    {
        super.processKeyEvent(keyevent);
        System.out.println("Got key event for <" + keyevent.getKeyChar() + ">");
    }

    private UIHJButton okButton;
    private Label notFoundLabel;
}
