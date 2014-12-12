// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Cookies.java

package sunw.hotjava.applets;

import java.awt.*;
import java.awt.event.*;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.applet.PrefsPanel;
import sunw.hotjava.bean.CookieJarInterface;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.*;

public class Cookies extends Panel
    implements PrefsPanel, ActionListener
{

    public Cookies(KeyListener keylistener1)
    {
        initialized = false;
        keylistener = keylistener1;
        Font font = Font.decode(properties.getProperty("security.preference.font.regular", ""));
        Font font1 = Font.decode(properties.getProperty("security.preference.font.headings", ""));
        initialized = true;
        GridBagLayout gridbaglayout = new GridBagLayout();
        setLayout(gridbaglayout);
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridwidth = 10;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbagconstraints.anchor = 18;
        gridbagconstraints.fill = 0;
        gridbagconstraints.insets = new Insets(10, 10, 10, 10);
        gridbagconstraints.ipadx = 0;
        gridbagconstraints.ipady = 0;
        heading = new MultiLineLabel(properties.getProperty("security.preference.cookies.heading", ""), font1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        add(heading, gridbagconstraints);
        mlabel1 = new MultiLineLabel(properties.getProperty("security.preference.cookies.body", ""), font);
        gridbagconstraints.gridy = 1;
        gridbagconstraints.gridheight = 2;
        gridbagconstraints.insets = new Insets(10, 10, 10, 10);
        add(mlabel1, gridbagconstraints);
        String s = properties.getProperty("hotjava.cookie.acceptpolicy", "notify");
        cboxgroup = new CheckboxGroup();
        cbox1 = new Checkbox(properties.getProperty("security.preference.cookies.checkbox1", ""), s.equals("all"), cboxgroup);
        cbox1.addKeyListener(keylistener1);
        cbox2 = new Checkbox(properties.getProperty("security.preference.cookies.checkbox2", ""), s.equals("notify"), cboxgroup);
        cbox2.addKeyListener(keylistener1);
        cbox3 = new Checkbox(properties.getProperty("security.preference.cookies.checkbox3", ""), s.equals("none"), cboxgroup);
        cbox3.addKeyListener(keylistener1);
        button = new UIHJButton("security.preference.cookies.discardButton", properties);
        button.addActionListener(this);
        gridbagconstraints.gridy = 3;
        gridbagconstraints.gridheight = 1;
        add(cbox1, gridbagconstraints);
        gridbagconstraints.gridy = 4;
        add(cbox2, gridbagconstraints);
        gridbagconstraints.gridy = 5;
        add(cbox3, gridbagconstraints);
        gridbagconstraints.gridy = 7;
        add(button, gridbagconstraints);
        MultiLineLabel multilinelabel = new MultiLineLabel(properties.getProperty("security.preference.cookies.discardWarning", ""), font);
        gridbagconstraints.fill = 0;
        gridbagconstraints.gridy = 8;
        add(multilinelabel, gridbagconstraints);
        Label label = new Label();
        gridbagconstraints.weighty = 1.0D;
        gridbagconstraints.fill = 3;
        gridbagconstraints.gridheight = 0;
        add(label, gridbagconstraints);
    }

    public int apply()
    {
        if(initialized)
        {
            Checkbox checkbox = cboxgroup.getSelectedCheckbox();
            if(checkbox == cbox1)
                properties.put("hotjava.cookie.acceptpolicy", "all");
            else
            if(checkbox == cbox2)
            {
                properties.put("hotjava.cookie.acceptpolicy", "notify");
            } else
            {
                properties.put("hotjava.cookie.acceptpolicy", "none");
                HJWindowManager hjwindowmanager = HJWindowManager.getHJWindowManager();
                if(hjwindowmanager != null)
                {
                    CookieJarInterface cookiejarinterface = hjwindowmanager.getCookiesManager();
                    if(cookiejarinterface != null)
                        cookiejarinterface.discardAllCookies();
                }
            }
        }
        return 1;
    }

    public void stop()
    {
        cbox1.removeKeyListener(keylistener);
        cbox2.removeKeyListener(keylistener);
        cbox3.removeKeyListener(keylistener);
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        HJWindowManager hjwindowmanager = HJWindowManager.getHJWindowManager();
        if(hjwindowmanager != null)
        {
            CookieJarInterface cookiejarinterface = hjwindowmanager.getCookiesManager();
            if(cookiejarinterface != null)
            {
                cookiejarinterface.discardAllCookies();
                Object obj;
                for(obj = this; !(obj instanceof Frame); obj = ((Component) (obj)).getParent());
                ConfirmDialog confirmdialog = new ConfirmDialog("cookies.deleted", (Frame)obj, 1, true);
                confirmdialog.show();
            }
        }
    }

    private static final HJBProperties properties = HJBProperties.getHJBProperties("hjbrowser");
    private static final String propsname = "security.preference.cookies";
    MultiLineLabel heading;
    MultiLineLabel mlabel1;
    List list;
    CheckboxGroup cboxgroup;
    Checkbox cbox1;
    Checkbox cbox2;
    Checkbox cbox3;
    Checkbox cbox4;
    UIHJButton button;
    private boolean initialized;
    private static final String ALL = "all";
    private static final String NOTIFY = "notify";
    private static final String NONE = "none";
    private static KeyListener keylistener;

}
