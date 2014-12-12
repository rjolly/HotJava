// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SecurityDialog.java

package sunw.hotjava.security;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.EventObject;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.*;

// Referenced classes of package sunw.hotjava.security:
//            CommonSecurity, SDCheckbox

final class SecurityDialog extends BagDialog
    implements KeyPressInterest, ActionListener
{

    public SecurityDialog(Frame frame)
    {
        super(propName, frame, HJBProperties.getHJBProperties("hjbrowser"));
        properties = HJBProperties.getHJBProperties("hjbrowser");
        keylistener = new EnterKeyListener(this);
        GridBagLayout gridbaglayout = (GridBagLayout)getLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.anchor = 17;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = -1;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        UserImageLabel userimagelabel = new UserImageLabel("securitydialog.icon.quest", properties);
        gridbagconstraints.anchor = 18;
        gridbagconstraints.insets = new Insets(10, 20, 0, 40);
        addComponent(userimagelabel, gridbaglayout, gridbagconstraints);
        gridbagconstraints.insets = new Insets(4, 20, 0, 0);
        addComponent(label = new Label("permission"), gridbaglayout, gridbagconstraints);
        gridbagconstraints.insets = new Insets(8, 32, 8, 0);
        addComponent(objLabel = new Label("target"), gridbaglayout, gridbagconstraints);
        gridbagconstraints.insets = new Insets(5, 20, 5, 0);
        addComponent(despLabel = new MultiLineLabel("", 4, 0, 0), gridbaglayout, gridbagconstraints);
        gridbagconstraints.insets = new Insets(0, 20, 0, 0);
        addComponent(appLabel = new Label("applet"), gridbaglayout, gridbagconstraints);
        gridbagconstraints.insets = new Insets(0, 32, 0, 0);
        addComponent(siteLabel = new Label("site"), gridbaglayout, gridbagconstraints);
        addComponent(certLabel = new Label("cert"), gridbaglayout, gridbagconstraints);
        gridbagconstraints.insets = new Insets(8, 32, 0, 0);
        group = new CheckboxGroup();
        first = new SDCheckbox("allow", true, group, 1, keylistener);
        addComponent(first, gridbaglayout, gridbagconstraints);
        gridbagconstraints.insets = new Insets(0, 32, 0, 0);
        all = new SDCheckbox("allow.all", false, group, 2, keylistener);
        addComponent(all, gridbaglayout, gridbagconstraints);
        SDCheckbox sdcheckbox = new SDCheckbox("deny", false, group, 0, keylistener);
        addComponent(sdcheckbox, gridbaglayout, gridbagconstraints);
        gridbagconstraints.insets = new Insets(0, 5, 5, 10);
        gridbagconstraints.anchor = 13;
        super.ok = new UIHJButton(propName + ".ok", true, super.hjbProperties);
        super.ok.addActionListener(this);
        addComponent(super.ok, gridbaglayout, gridbagconstraints);
    }

    private int getAuthorization(String s, String s1)
    {
        int i = askAuthorization(s, s1);
        return i;
    }

    private int askAuthorization(String s, String s1)
    {
        synchronized(lock)
        {
            super.result = 0;
            group.setSelectedCheckbox(first);
            String s2 = getProp(s, "Applet wants to " + s);
            label.setText(s2);
            objLabel.setText(String.valueOf(s1));
            s2 = getProp(s + ".desp");
            if(s2 != null)
            {
                despLabel.setLabel(s2);
                despLabel.setFont(new Font("Dialog", 0, 12));
            } else
            {
                remove(despLabel);
            }
            CommonSecurity commonsecurity = (CommonSecurity)System.getSecurityManager();
            s2 = commonsecurity.getClassName();
            if(s2 == null)
                s2 = getProp("applet.unknown");
            else
                s2 = getProp("applet") + " " + s2;
            appLabel.setText(s2);
            s2 = commonsecurity.getSiteName();
            if(s2 == null)
                s2 = getProp("site.unknown");
            else
                s2 = getProp("site") + " " + s2;
            siteLabel.setText(s2);
            s2 = commonsecurity.getSignerName();
            if(s2 == null)
                s2 = getProp("signer.unknown");
            else
                s2 = getProp("signer") + " " + s2;
            certLabel.setText(s2);
            s2 = getProp(s + ".all", "Allow all similar actions");
            all.setLabel(s2);
        }
        return handleDialog();
    }

    public int getReadAuthorization(String s)
    {
        return getAuthorization("read", s);
    }

    public int getWriteAuthorization(String s)
    {
        return getAuthorization("write", s);
    }

    public int getDeleteAuthorization(String s)
    {
        return getAuthorization("delete", s);
    }

    public int getExecAuthorization(String s)
    {
        return getAuthorization("exec", s);
    }

    public int getPropAuthorization(String s)
    {
        return getAuthorization("prop", s);
    }

    public int getConnectAuthorization(String s)
    {
        return getAuthorization("connect", s);
    }

    public int getAcceptAuthorization(String s)
    {
        return getAuthorization("accept", s);
    }

    public int getListenAuthorization(String s)
    {
        return getAuthorization("listen", s);
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        if(actionevent.getSource() == super.ok)
        {
            SDCheckbox sdcheckbox = (SDCheckbox)group.getSelectedCheckbox();
            super.result = sdcheckbox.value;
            setVisible(false);
        }
    }

    public void processEnterEvent(KeyEvent keyevent)
    {
        actionPerformed(new ActionEvent(super.ok, 0, ""));
    }

    public int handleDialog()
    {
        pack();
        invalidate();
        validate();
        centerOnScreen();
        setVisible(true);
        dispose();
        return super.result;
    }

    static String propName = "hotjava.security";
    private Label label;
    private Label objLabel;
    private MultiLineLabel despLabel;
    private Label appLabel;
    private Label siteLabel;
    private Label certLabel;
    private CheckboxGroup group;
    private Class clazz;
    private URL base;
    private SDCheckbox first;
    private SDCheckbox all;
    private static Object lock = new Object();
    private EnterKeyListener keylistener;
    static final int DENIED = 0;
    static final int ALLOWED = 1;
    static final int ALLOW_ALL = 2;
    private HJBProperties properties;

}
