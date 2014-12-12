// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FindStringDialog.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;
import sunw.hotjava.HJFrame;
import sunw.hotjava.bean.HTMLBrowsable;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            UserDialog, FindDialogButtonPane, FindDialogFormPane, FindDialogNotFoundDialog, 
//            UIHJButton, UIHJButtonGroup, UserCheckbox, UserLabel, 
//            UserTextField

public class FindStringDialog extends UserDialog
{
    private final class ActionEventsListener
        implements ActionListener
    {

        public void actionPerformed(ActionEvent actionevent)
        {
            if(actionevent.getSource() == textField)
            {
                find();
                return;
            } else
            {
                action(actionevent.getActionCommand());
                return;
            }
        }

        ActionEventsListener()
        {
        }
    }

    private final class EnterHitListener extends KeyAdapter
    {

        public void keyTyped(KeyEvent keyevent)
        {
            if(keyevent.getKeyChar() == '\r')
                action(((UIHJButton)keyevent.getSource()).getActionCommand());
        }

        EnterHitListener()
        {
        }
    }


    public FindStringDialog(String s, HJFrame hjframe, HJBProperties hjbproperties)
    {
        this(s, hjframe, false, hjbproperties);
    }

    public FindStringDialog(String s, HJFrame hjframe, boolean flag, HJBProperties hjbproperties)
    {
        super(s, hjframe, flag, hjbproperties);
        result = false;
        frame = hjframe;
        props = hjbproperties;
        actionEventsListener = new ActionEventsListener();
        enterHitListener = new EnterHitListener();
        bean = hjframe.getHTMLBrowsable();
        init();
    }

    public void setVisible(boolean flag)
    {
        super.setVisible(flag);
        if(flag)
            textField.requestFocus();
    }

    public boolean getResult()
    {
        return result;
    }

    public void action(String s)
    {
        if(s.equals(findButton.getActionCommand()))
        {
            result = find();
            return;
        }
        if(s.equals(clearButton.getActionCommand()))
        {
            clear();
            return;
        }
        if(s.equals(dismissButton.getActionCommand()))
        {
            dismiss();
            return;
        } else
        {
            return;
        }
    }

    private boolean find()
    {
        boolean flag = false;
        String s = textField.getText();
        if(s != null && s.length() > 0)
        {
            currentPosition = bean.find(currentPosition, s, !caseSensitiveCheckbox.getState());
            if(currentPosition == -1)
            {
                FindDialogNotFoundDialog finddialognotfounddialog = new FindDialogNotFoundDialog("finddialog.not.found", frame, true, props);
                Point point = frame.getLocationOnScreen();
                Dimension dimension = frame.getSize();
                finddialognotfounddialog.pack();
                Dimension dimension1 = finddialognotfounddialog.getSize();
                int i = (point.x + dimension.width) / 2;
                int j = (point.y + dimension.height) / 2;
                int k = i - dimension1.width / 4;
                int l = j - dimension1.height / 4;
                finddialognotfounddialog.setLocation(k, l);
                finddialognotfounddialog.show();
                currentPosition = 0;
            } else
            {
                flag = true;
            }
        }
        return flag;
    }

    private boolean clear()
    {
        currentPosition = 0;
        textField.setText("");
        textField.requestFocus();
        return true;
    }

    private boolean dismiss()
    {
        hide();
        return true;
    }

    private void init()
    {
        setLayout(new BorderLayout());
        initFormPane();
        initButtonsPane();
        pack();
        centerOnScreen();
        currentPosition = 0;
    }

    private void initFormPane()
    {
        FindDialogFormPane finddialogformpane = new FindDialogFormPane();
        finddialogformpane.setLayout(new GridBagLayout());
        textField = new UserTextField("finddialog.textfield");
        UserLabel userlabel = new UserLabel("finddialog.label");
        caseSensitiveCheckbox = new UserCheckbox("finddialog.casesensitive", HJBProperties.getHJBProperties("hjbrowser"));
        constrain(userlabel, 0, 0, 13, 1, finddialogformpane);
        constrain(textField, 1, 0, 17, 0, finddialogformpane);
        constrain(caseSensitiveCheckbox, 1, 1, 17, 1, finddialogformpane);
        add("Center", finddialogformpane);
        textField.addActionListener(actionEventsListener);
    }

    private void initButtonsPane()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        FindDialogButtonPane finddialogbuttonpane = new FindDialogButtonPane();
        UIHJButtonGroup uihjbuttongroup = new UIHJButtonGroup(hjbproperties);
        finddialogbuttonpane.setLayout(new FlowLayout(2));
        findButton = new UIHJButton("finddialog.findbutton", hjbproperties);
        finddialogbuttonpane.add(findButton);
        findButton.addActionListener(actionEventsListener);
        findButton.addKeyListener(enterHitListener);
        uihjbuttongroup.addButtonToGroup(findButton);
        clearButton = new UIHJButton("finddialog.clearbutton", hjbproperties);
        finddialogbuttonpane.add(clearButton);
        clearButton.addActionListener(actionEventsListener);
        clearButton.addKeyListener(enterHitListener);
        uihjbuttongroup.addButtonToGroup(clearButton);
        dismissButton = new UIHJButton("finddialog.dismissbutton", hjbproperties);
        finddialogbuttonpane.add(dismissButton);
        dismissButton.addActionListener(actionEventsListener);
        dismissButton.addKeyListener(enterHitListener);
        uihjbuttongroup.addButtonToGroup(clearButton);
        Panel panel = new Panel();
        panel.setLayout(new BorderLayout());
        panel.add("Center", finddialogbuttonpane);
        add("South", panel);
    }

    private void constrain(Component component, int i, int j, int k, int l, Container container)
    {
        GridBagLayout gridbaglayout = (GridBagLayout)container.getLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridx = i;
        gridbagconstraints.gridy = j;
        gridbagconstraints.gridwidth = l;
        gridbagconstraints.anchor = k;
        container.add(component);
        gridbaglayout.setConstraints(component, gridbagconstraints);
    }

    UserCheckbox caseSensitiveCheckbox;
    UserTextField textField;
    UIHJButton findButton;
    UIHJButton clearButton;
    UIHJButton dismissButton;
    ActionListener actionEventsListener;
    KeyListener enterHitListener;
    HTMLBrowsable bean;
    int currentPosition;
    boolean result;
    Frame frame;
    HJBProperties props;

}
