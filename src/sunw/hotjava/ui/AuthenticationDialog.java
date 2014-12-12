// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AuthenticationDialog.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.EventObject;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            UserDialog, UIHJButton, UIHJButtonGroup, UserLabel

public class AuthenticationDialog extends UserDialog
    implements Runnable
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


    public AuthenticationDialog(String s, Frame frame)
    {
        super(s, frame, true, prps);
        actionEventsListener = new ActionEventsListener();
        setLayout(new BorderLayout());
        Panel panel = new Panel();
        panel.setLayout(new GridLayout(0, 1));
        panel.add(errorLabel = new UserLabel(s + ".errorLabel"));
        panel.add(label = new UserLabel(s + ".label"));
        add("North", panel);
        Panel panel1 = new Panel();
        GridBagLayout gridbaglayout = new GridBagLayout();
        panel1.setLayout(gridbaglayout);
        addComponent(new UserLabel(s + ".username"), panel1, gridbaglayout, 0, 0, 1, 1);
        addComponent(nameField = new TextField(35), panel1, gridbaglayout, 1, 0, 2, 1);
        nameField.addActionListener(actionEventsListener);
        addComponent(new UserLabel(s + ".password"), panel1, gridbaglayout, 0, 1, 1, 1);
        addComponent(passField = new TextField(35), panel1, gridbaglayout, 1, 1, 2, 1);
        add("Center", panel1);
        passField.setEchoCharacter('*');
        passField.addActionListener(actionEventsListener);
        Panel panel2 = new Panel();
        panel2.setLayout(new FlowLayout(2));
        panel2.add(ok = new UIHJButton(s + ".ok", prps));
        panel2.add(cancel = new UIHJButton(s + ".cancel", prps));
        UIHJButtonGroup uihjbuttongroup = new UIHJButtonGroup(prps);
        uihjbuttongroup.addButtonToGroup(ok);
        uihjbuttongroup.addButtonToGroup(cancel);
        add("South", panel2);
        pack();
        setResizable(false);
        try
        {
            centerOnScreen();
        }
        catch(Throwable _ex) { }
        ok.addActionListener(actionEventsListener);
        cancel.addActionListener(actionEventsListener);
    }

    void addComponent(Component component, Container container, GridBagLayout gridbaglayout, int i, int j, int k, int l)
    {
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridx = i;
        gridbagconstraints.gridy = j;
        gridbagconstraints.gridwidth = k;
        gridbagconstraints.gridheight = l;
        gridbaglayout.setConstraints(component, gridbagconstraints);
        container.add(component);
    }

    public void action(ActionEvent actionevent)
    {
        Object obj = actionevent.getSource();
        if(obj == nameField)
        {
            passField.requestFocus();
            return;
        }
        if(obj == ok || obj == passField)
        {
            nameStr = nameField.getText();
            passStr = passField.getText();
            hide();
            return;
        }
        if(actionevent.getSource() instanceof UIHJButton)
        {
            hide();
            return;
        } else
        {
            return;
        }
    }

    void handleDialog()
    {
        monitor = new Object();
        (new Thread(this)).start();
        synchronized(monitor)
        {
            try
            {
                monitor.wait();
            }
            catch(InterruptedException _ex) { }
        }
    }

    public void run()
    {
        Thread.currentThread().setName("DialogWaiter");
        show();
        synchronized(monitor)
        {
            monitor.notify();
        }
        dispose();
    }

    public String getAuthString(URL url, String s, String s1, boolean flag)
    {
        nameStr = null;
        passStr = null;
        if(flag)
            errorLabel.setText("     " + prps.getProperty(getName() + ".badloginmsg"));
        else
            errorLabel.setText(null);
        String as[] = {
            s, url.toString()
        };
        String s2 = MessageFormat.format(prps.getProperty(getName() + ".loginmsg"), as);
        label.setText(s2);
        if(s1 != null)
            nameField.setText(s1);
        handleDialog();
        String s3 = null;
        if(nameStr != null && passStr != null)
            s3 = nameStr + ":" + passStr;
        return s3;
    }

    public String getUserName()
    {
        return nameStr;
    }

    public String getPassword()
    {
        return passStr;
    }

    public static void main(String args[])
        throws Exception
    {
        Frame frame = new Frame("testing");
        frame.resize(500, 500);
        frame.show();
        AuthenticationDialog authenticationdialog = new AuthenticationDialog("hotjava.auth", frame);
        synchronized(new Object())
        {
            System.out.println("returned " + authenticationdialog.getAuthString(new URL("http://java.sun.com"), "JavaSoft!", null, false));
        }
    }

    Object monitor;
    private String nameStr;
    private String passStr;
    private static HJBProperties prps = HJBProperties.getHJBProperties("hjbrowser");
    Label errorLabel;
    Label label;
    TextField nameField;
    TextField passField;
    UIHJButton ok;
    UIHJButton cancel;
    ActionListener actionEventsListener;

}
