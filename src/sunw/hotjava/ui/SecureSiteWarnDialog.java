// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SecureSiteWarnDialog.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            BagDialog, EnterKeyListener, KeyPressInterest, MultiLineLabel, 
//            UIHJButton, UserDialog, UserImageLabel

class SecureSiteWarnDialog extends BagDialog
    implements ActionListener, KeyPressInterest
{

    public SecureSiteWarnDialog(boolean flag)
    {
        super(propname, HJWindowManager.getHJWindowManager().getLastFocusHolder(), HJBProperties.getHJBProperties("hjbrowser"));
        props = HJBProperties.getHJBProperties("hjbrowser");
        toSecure = flag;
        GridBagLayout gridbaglayout = (GridBagLayout)getLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.fill = 0;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = -1;
        gridbagconstraints.anchor = 18;
        UserImageLabel userimagelabel = new UserImageLabel("securitydialog.icon.info", props);
        gridbagconstraints.anchor = 18;
        gridbagconstraints.insets = new Insets(10, 20, 0, 40);
        add(userimagelabel, gridbagconstraints);
        gridbagconstraints.insets = new Insets(5, 20, 5, 20);
        gridbagconstraints.anchor = 18;
        MultiLineLabel multilinelabel = new MultiLineLabel("", 20, 0, 0);
        if(flag)
            multilinelabel.setLabel(props.getProperty(propname + ".tosecure.prompt", "To secure site"));
        else
            multilinelabel.setLabel(props.getProperty(propname + ".fromsecure.prompt", "To insecure site"));
        addComponent(multilinelabel, gridbaglayout, gridbagconstraints);
        gridbagconstraints.anchor = 10;
        EnterKeyListener enterkeylistener = new EnterKeyListener(this);
        checkbox = new Checkbox(props.getProperty(propname + ".checkbox", "Don't Show this message in the future."));
        checkbox.addKeyListener(enterkeylistener);
        addComponent(checkbox, gridbaglayout, gridbagconstraints);
        Panel panel = new Panel();
        gridbagconstraints.anchor = 13;
        button1 = new UIHJButton(propname + ".continue", props);
        button1.addActionListener(this);
        panel.add(button1);
        addComponent(panel, gridbaglayout, gridbagconstraints);
        setSize(350, 250);
        centerOnScreen();
    }

    public boolean action(Event event, Object obj)
    {
        if(event.target == button1)
        {
            result = YES;
            setVisible(false);
            return true;
        } else
        {
            return false;
        }
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        if(actionevent.getSource() == button1)
        {
            result = YES;
            setVisible(false);
        }
    }

    public void processEnterEvent(KeyEvent keyevent)
    {
        actionPerformed(new ActionEvent(button1, 0, ""));
    }

    public int handleDialog()
    {
        pack();
        invalidate();
        validate();
        setVisible(true);
        dispose();
        if(checkbox.getState())
            if(toSecure)
                props.put("security.preference.tosecuresite.warn", "false");
            else
                props.put("security.preference.fromsecuresite.warn", "false");
        return result;
    }

    private static String propname = "security.preference.site.warn";
    UIHJButton button1;
    Checkbox checkbox;
    private int result;
    private boolean toSecure;
    private HJBProperties props;
    static int YES;
    static int NO = 1;

}
