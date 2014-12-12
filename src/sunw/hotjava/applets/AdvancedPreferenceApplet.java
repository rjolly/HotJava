// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AdvancedPreferenceApplet.java

package sunw.hotjava.applets;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.applets:
//            PrefApplet, PrefAppletManager

public class AdvancedPreferenceApplet extends PrefApplet
    implements KeyListener, FocusListener
{

    public void init()
    {
        Insets insets = new Insets(0, 5, 0, 0);
        Insets insets1 = new Insets(10, 5, 0, 0);
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        setLayout(new GridBagLayout());
        gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.anchor = 18;
        gridbagconstraints.fill = 0;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.insets = insets1;
        gridbagconstraints.ipadx = 0;
        gridbagconstraints.ipady = 0;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = -1;
        add(titleLabel, gridbagconstraints);
        add(securityLabel, gridbagconstraints);
        gridbagconstraints.insets = insets;
        add(securityModeLabel, gridbagconstraints);
        add(securityModeLabel, gridbagconstraints);
        basicMode.addKeyListener(this);
        basicMode.addFocusListener(this);
        basicMode.setCheckboxGroup(securityModeCheckboxGroup);
        adminMode.addKeyListener(this);
        adminMode.addFocusListener(this);
        adminMode.setCheckboxGroup(securityModeCheckboxGroup);
        add(basicMode, gridbagconstraints);
        add(adminMode, gridbagconstraints);
        gridbagconstraints.insets = insets1;
        add(warningLabel, gridbagconstraints);
        gridbagconstraints.insets = insets;
        enterWarning.addKeyListener(this);
        enterWarning.addFocusListener(this);
        add(enterWarning, gridbagconstraints);
        leaveWarning.addKeyListener(this);
        leaveWarning.addFocusListener(this);
        add(leaveWarning, gridbagconstraints);
        gridbagconstraints.insets = insets1;
        add(historyLabel, gridbagconstraints);
        gridbagconstraints.insets = insets;
        Panel panel = new Panel();
        panel.add(expirationDaysLabel1);
        expirationDays.addKeyListener(this);
        panel.add(expirationDays);
        panel.add(expirationDaysLabel2);
        panel.add(clearHistoryButton);
        add(panel, gridbagconstraints);
        gridbagconstraints.insets = insets1;
        add(cacheManagementLabel, gridbagconstraints);
        gridbagconstraints.insets = insets;
        add(clearImageCacheButton, gridbagconstraints);
    }

    public void updateFields()
    {
        Properties properties = PrefAppletManager.getProperties("advancedPreferenceApplet");
        if(properties.getProperty("hotjava.securitymode").equalsIgnoreCase("Basic"))
            securityModeCheckboxGroup.setSelectedCheckbox(basicMode);
        else
            securityModeCheckboxGroup.setSelectedCheckbox(adminMode);
        if(properties.getProperty("security.preference.tosecuresite.warn").equals("true"))
            enterWarning.setState(true);
        else
            enterWarning.setState(false);
        if(properties.getProperty("security.preference.fromsecuresite.warn").equals("true"))
            leaveWarning.setState(true);
        else
            leaveWarning.setState(false);
        expirationDays.setText(PrefApplet.properties.getProperty("urlpool.expires", "168"));
    }

    public String getName()
    {
        return "advancedPreferenceApplet";
    }

    public String[] getPropertyNames()
    {
        String as[] = new String[4];
        as[0] = "hotjava.securitymode";
        as[1] = "security.preference.tosecuresite.warn";
        as[2] = "security.preference.fromsecuresite.warn";
        as[3] = "urlpool.expires";
        return as;
    }

    public void apply()
    {
        updateProperties();
        String as[] = getPropertyNames();
        Properties properties = PrefAppletManager.getProperties("advancedPreferenceApplet");
        for(int i = 0; i < as.length; i++)
        {
            String s = as[i];
            String s1 = properties.getProperty(s);
            if(s1 == null || s1.length() == 0)
                PrefApplet.properties.remove(s);
            else
                PrefApplet.properties.put(s, s1);
        }

        PrefApplet.properties.save();
    }

    public void reset()
    {
        String as[] = getPropertyNames();
        Properties properties = PrefAppletManager.getProperties("advancedPreferenceApplet");
        for(int i = 0; i < as.length; i++)
        {
            String s = as[i];
            properties.put(s, PrefApplet.properties.getProperty(s, ""));
        }

        PrefAppletManager.updateApplet("advancedPreferenceApplet");
    }

    private void updateProperties()
    {
        String as[] = new String[4];
        as[0] = securityModeCheckboxGroup.getSelectedCheckbox() != basicMode ? "Administrator" : "Basic";
        as[1] = (new Boolean(enterWarning.getState())).toString();
        as[2] = (new Boolean(leaveWarning.getState())).toString();
        as[3] = expirationDays.getText();
        String as1[] = getPropertyNames();
        boolean flag = false;
        Properties properties = PrefAppletManager.getProperties("advancedPreferenceApplet");
        for(int i = 0; i < as.length; i++)
        {
            String s = properties.getProperty(as1[i]);
            if(!as[i].equals(s))
            {
                properties.put(as1[i], as[i]);
                flag = true;
            }
        }

        if(flag)
            PrefAppletManager.updateApplet("advancedPreferenceApplet");
    }

    public void focusGained(FocusEvent focusevent)
    {
    }

    public void focusLost(FocusEvent focusevent)
    {
        updateProperties();
    }

    public void keyTyped(KeyEvent keyevent)
    {
        if(keyevent.getKeyChar() == '\r')
            updateProperties();
    }

    public void keyPressed(KeyEvent keyevent)
    {
        if(keyevent.getSource() == expirationDays)
            switch(keyevent.getKeyCode())
            {
            default:
                char c = keyevent.getKeyChar();
                if(!keyevent.isActionKey() && (c < '0' || c > '9'))
                {
                    Toolkit.getDefaultToolkit().beep();
                    keyevent.consume();
                }
                break;

            case 8: // '\b'
            case 9: // '\t'
            case 10: // '\n'
            case 16: // '\020'
            case 127: // '\177'
                break;
            }
    }

    public void keyReleased(KeyEvent keyevent)
    {
    }

    public AdvancedPreferenceApplet()
    {
        titleLabel = new Label(PrefApplet.properties.getProperty("advanced.preference.title", "Advanced"));
        securityLabel = new Label(PrefApplet.properties.getProperty("advanced.preference.security.title", "Security"));
        securityModeLabel = new Label(PrefApplet.properties.getProperty("advanced.preference.security.subtitle", "Security Mode"));
        securityModeCheckboxGroup = new CheckboxGroup();
        basicMode = new Checkbox(PrefApplet.properties.getProperty("advanced.preference.security.choice1", "Basic"));
        adminMode = new Checkbox(PrefApplet.properties.getProperty("advanced.preference.security.choice2", "Administrative"));
        warningLabel = new Label(PrefApplet.properties.getProperty("advanced.preference.warn.title", "Show A Warning Before:"));
        enterWarning = new Checkbox(PrefApplet.properties.getProperty("advanced.preference.warn.choice1", "Entering a Secure Web Site"));
        leaveWarning = new Checkbox(PrefApplet.properties.getProperty("advanced.preference.warn.choice2", "Leaving a Secure Web Site"));
        historyLabel = new Label(PrefApplet.properties.getProperty("advanced.preference.history.title", "Visiting Pages History"));
        expirationDaysLabel1 = new Label(PrefApplet.properties.getProperty("advanced.preference.history.day1", "Visited Links Expire After"));
        expirationDaysLabel2 = new Label(PrefApplet.properties.getProperty("advanced.preferencehistory.day2", "Days"));
        expirationDays = new TextField("", 3);
        clearHistoryButton = new Button(PrefApplet.properties.getProperty("advanced.preference.history.clear", "Clear History"));
        cacheManagementLabel = new Label(PrefApplet.properties.getProperty("advanced.preference.cache.title", "Cache Management"));
        clearImageCacheButton = new Button(PrefApplet.properties.getProperty("advanced.preference.cache.clear", "Clear Image Cache"));
    }

    private static final String name = "advancedPreferenceApplet";
    private static final String propname = "advanced.preference";
    static String securityMode;
    private Label titleLabel;
    private Label securityLabel;
    private Label securityModeLabel;
    private CheckboxGroup securityModeCheckboxGroup;
    private Checkbox basicMode;
    private Checkbox adminMode;
    private Label warningLabel;
    private Checkbox enterWarning;
    private Checkbox leaveWarning;
    private Label historyLabel;
    private Label expirationDaysLabel1;
    private Label expirationDaysLabel2;
    private TextField expirationDays;
    private Button clearHistoryButton;
    private Label cacheManagementLabel;
    private Button clearImageCacheButton;

    static 
    {
        securityMode = PrefApplet.properties.getProperty("hotjava.securitymode");
    }
}
