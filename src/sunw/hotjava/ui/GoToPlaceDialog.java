// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GoToPlaceDialog.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.EventObject;
import sunw.hotjava.HJFrame;
import sunw.hotjava.bean.HTMLBrowsable;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            UserDialog, GoToPlaceDialogButtonPane, GoToPlaceDialogFormPane, UIHJButton, 
//            UIHJButtonGroup, UserFileDialog, UserLabel, UserTextField

public class GoToPlaceDialog extends UserDialog
    implements ActionListener
{

    public GoToPlaceDialog(String s, HJFrame hjframe, HJBProperties hjbproperties)
    {
        this(s, hjframe, false, hjbproperties);
    }

    public GoToPlaceDialog(String s, HJFrame hjframe, boolean flag, HJBProperties hjbproperties)
    {
        super(s, hjframe, flag, hjbproperties);
        bean = hjframe.getHTMLBrowsable();
        properties = hjbproperties;
        init();
    }

    public void setVisible(boolean flag)
    {
        super.setVisible(flag);
        if(flag)
            textField.requestFocus();
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        Object obj = actionevent.getSource();
        if(obj == textField || obj == goButton)
            goTo();
        if(obj == clearButton)
            clear();
        if(obj == dismissButton)
            dismiss();
        if(obj == fileButton)
            getFile();
    }

    private void goTo()
    {
        String s = textField.getText();
        if(!s.equals(""))
        {
            bean.setDocumentString(s);
            dismiss();
            return;
        } else
        {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
    }

    private void clear()
    {
        textField.setText("");
        textField.requestFocus();
    }

    private void dismiss()
    {
        hide();
    }

    private void init()
    {
        setLayout(new BorderLayout());
        initFormPane();
        initButtonsPane();
        pack();
        centerOnScreen();
    }

    private void initFormPane()
    {
        GoToPlaceDialogFormPane gotoplacedialogformpane = new GoToPlaceDialogFormPane();
        gotoplacedialogformpane.setLayout(new FlowLayout());
        UserLabel userlabel = new UserLabel("gotodialog.label");
        gotoplacedialogformpane.add(userlabel);
        textField = new UserTextField("gotodialog.text");
        gotoplacedialogformpane.add(textField);
        textField.addActionListener(this);
        fileButton = new UIHJButton("gotodialog.file", super.props);
        gotoplacedialogformpane.add(fileButton);
        fileButton.addActionListener(this);
        add("Center", gotoplacedialogformpane);
    }

    private void initButtonsPane()
    {
        GoToPlaceDialogButtonPane gotoplacedialogbuttonpane = new GoToPlaceDialogButtonPane();
        UIHJButtonGroup uihjbuttongroup = new UIHJButtonGroup(super.props);
        gotoplacedialogbuttonpane.setLayout(new FlowLayout(2));
        goButton = new UIHJButton("gotodialog.go", super.props);
        gotoplacedialogbuttonpane.add(goButton);
        uihjbuttongroup.addButtonToGroup(goButton);
        goButton.addActionListener(this);
        clearButton = new UIHJButton("gotodialog.clear", super.props);
        gotoplacedialogbuttonpane.add(clearButton);
        uihjbuttongroup.addButtonToGroup(clearButton);
        clearButton.addActionListener(this);
        dismissButton = new UIHJButton("gotodialog.dismiss", super.props);
        gotoplacedialogbuttonpane.add(dismissButton);
        uihjbuttongroup.addButtonToGroup(dismissButton);
        dismissButton.addActionListener(this);
        add("South", gotoplacedialogbuttonpane);
    }

    private void getFile()
    {
        Frame frame = (Frame)getParent();
        UserFileDialog userfiledialog = new UserFileDialog(frame, "hotjava.opendialog");
        if(fileDialogDirectory != null)
            userfiledialog.setDirectory(fileDialogDirectory);
        userfiledialog.setVisible(true);
        String s = userfiledialog.getFile();
        String s1 = userfiledialog.getDirectory();
        fileDialogDirectory = s1;
        if(s != null)
        {
            String s2 = "file:" + s1 + s;
            s2 = s2.replace(File.separatorChar, '/');
            textField.setText(s2);
        }
    }

    private UserTextField textField;
    private UIHJButton goButton;
    private UIHJButton clearButton;
    private UIHJButton dismissButton;
    private UIHJButton fileButton;
    private HTMLBrowsable bean;
    private HJBProperties properties;
    private static String fileDialogDirectory = null;

}
