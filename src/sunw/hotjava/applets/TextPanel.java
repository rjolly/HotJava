// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TextPanel.java

package sunw.hotjava.applets;

import java.awt.*;
import java.awt.event.KeyListener;
import java.util.Vector;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.applet.PrefsPanel;
import sunw.hotjava.bean.URLPooler;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.*;

// Referenced classes of package sunw.hotjava.applets:
//            ExpireNowHandler, IgnoreNonDigitsListener

class TextPanel extends Panel
    implements PrefsPanel
{

    public TextPanel(KeyListener keylistener1, String s, Font font1, Font font2, Font font3)
    {
        cboxVector = new Vector(2, 2);
        props = HJBProperties.getHJBProperties("hjbrowser");
        keylistener = keylistener1;
        regular = font1;
        regularBold = font2;
        headings = font3;
        GridBagLayout gridbaglayout = new GridBagLayout();
        c = new GridBagConstraints();
        setLayout(gridbaglayout);
        c.insets = new Insets(10, 10, 0, 0);
        c.fill = 0;
        c.anchor = 18;
        c.gridx = 0;
        c.gridy = 0;
        MultiLineLabel multilinelabel = new MultiLineLabel(s);
        multilinelabel.setFont(font3);
        multilinelabel.setMarginWidth(0);
        add(multilinelabel, c);
        c.gridy = 2;
        addLabel("screenfont.label");
        c.weightx = 0.0D;
        c.gridx = 1;
        c.gridwidth = 3;
        font = new Choice();
        add(font, c);
        c.gridwidth = 1;
        c.gridx = 4;
        addLabel("size.label");
        c.gridx = 5;
        fontSize = new UserChoice("fontsize", props);
        c.weightx = 1.0D;
        add(fontSize, c);
        c.weightx = 0.0D;
        c.gridx = 0;
        c.gridy = 3;
        addLabel("printerfont.label");
        c.gridx = 1;
        c.gridwidth = 3;
        print = new Choice();
        add(print, c);
        c.gridwidth = 1;
        c.gridx = 4;
        addLabel("size.label");
        c.gridx = 5;
        c.weightx = 1.0D;
        printSize = new UserChoice("fontsize", props);
        add(printSize, c);
        c.weightx = 0.0D;
        String as[] = getToolkit().getFontList();
        for(int i = 0; i < as.length; i++)
            if(!as[i].equals("ZapfDingbats") && !as[i].equals("Symbol"))
            {
                font.addItem(as[i]);
                print.addItem(as[i]);
            }

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        addLabel("visitedlinks.label");
        c.gridwidth = 1;
        String s1 = props.getProperty("urlpool.expires", "14");
        expiredField = new TextField(s1, 2);
        c.gridx = 2;
        add(expiredField, c);
        expiredField.addKeyListener(keylistener1);
        expiredField.addKeyListener(new IgnoreNonDigitsListener());
        c.gridx = 3;
        addLabel("days.label");
        c.gridx = 4;
        expireNowButton = new UIHJButton(propname + "expire", props);
        expireNowButton.addActionListener(expireNowHandler = new ExpireNowHandler());
        c.weightx = 1.0D;
        c.gridwidth = 2;
        add(expireNowButton, c);
        c.gridwidth = 1;
        c.weightx = 0.0D;
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        links = addCheckbox("underline.label", keylistener1);
        c.gridx = 0;
        c.gridy = 6;
        c.weighty = 1.0D;
        turnOffCBox = addCheckbox("turnoff.label", keylistener1);
        init();
    }

    private UserLabel addLabel(String s)
    {
        UserLabel userlabel = new UserLabel(propname + s);
        userlabel.setFont(regular);
        add(userlabel, c);
        return userlabel;
    }

    private UserCheckbox addCheckbox(String s, CheckboxGroup checkboxgroup, KeyListener keylistener1)
    {
        UserCheckbox usercheckbox = new UserCheckbox(propname + s, checkboxgroup, props);
        usercheckbox.setFont(regular);
        add(usercheckbox, c);
        usercheckbox.addKeyListener(keylistener1);
        cboxVector.addElement(usercheckbox);
        return usercheckbox;
    }

    private UserCheckbox addCheckbox(String s, KeyListener keylistener1)
    {
        UserCheckbox usercheckbox = new UserCheckbox(propname + s, props);
        usercheckbox.setFont(regular);
        add(usercheckbox, c);
        usercheckbox.addKeyListener(keylistener1);
        cboxVector.addElement(usercheckbox);
        return usercheckbox;
    }

    private void removeCheckboxes()
    {
        int i = cboxVector.size();
        for(int j = 0; j < i; j++)
            ((Component)cboxVector.elementAt(j)).removeKeyListener(keylistener);

    }

    private void init()
    {
        String s = props.getProperty("hotjava.docfontsize");
        try
        {
            switch(Integer.decode(s).intValue())
            {
            case -3: 
            case -2: 
                fontSize.select(0);
                break;

            case -1: 
                fontSize.select(1);
                break;

            case 0: // '\0'
                fontSize.select(2);
                break;

            case 1: // '\001'
                fontSize.select(3);
                break;

            case 2: // '\002'
            case 3: // '\003'
                fontSize.select(4);
                break;
            }
        }
        catch(NumberFormatException _ex)
        {
            fontSize.select(2);
        }
        s = props.getProperty("hotjava.printfontsize");
        try
        {
            switch(Integer.decode(s).intValue())
            {
            case -3: 
            case -2: 
                printSize.select(0);
                break;

            case -1: 
                printSize.select(1);
                break;

            case 0: // '\0'
                printSize.select(2);
                break;

            case 1: // '\001'
                printSize.select(3);
                break;

            case 2: // '\002'
            case 3: // '\003'
                printSize.select(4);
                break;
            }
        }
        catch(NumberFormatException _ex)
        {
            printSize.select(2);
        }
        font.select(props.getProperty("hotjava.docfont"));
        print.select(props.getProperty("hotjava.printfont"));
        links.setState(props.getBoolean("anchorStyle"));
        turnOffCBox.setState(!props.getBoolean("hotjava.blink"));
    }

    public void stop()
    {
        removeCheckboxes();
        expiredField.removeKeyListener(keylistener);
        expireNowButton.removeActionListener(expireNowHandler);
    }

    private boolean updateProp(String s, String s1)
    {
        String s2 = props.getProperty(s);
        if(!s1.equals(s2))
        {
            if(s1.length() == 0)
                props.remove(s);
            else
                props.put(s, s1);
            return true;
        } else
        {
            return false;
        }
    }

    public int apply()
    {
        boolean flag = false;
        String s = String.valueOf(fontSize.getSelectedIndex() - 2);
        flag |= updateProp("hotjava.docfontsize", s);
        s = String.valueOf(printSize.getSelectedIndex() - 2);
        flag |= updateProp("hotjava.printfontsize", s);
        boolean flag1 = updateProp("urlpool.expires", expiredField.getText());
        flag |= flag1;
        if(flag1)
        {
            URLPooler urlpooler = HJWindowManager.getHJWindowManager().getURLPoolManager();
            Integer integer = new Integer(expiredField.getText());
            urlpooler.setURLExpirationAmt(integer.intValue());
        }
        flag |= updateProp("hotjava.docfont", font.getSelectedItem());
        flag |= updateProp("hotjava.printfont", print.getSelectedItem());
        flag |= updateProp("anchorStyle", String.valueOf(links.getState()));
        updateProp("hotjava.blink", String.valueOf(!turnOffCBox.getState()));
        if(flag)
        {
            HJWindowManager hjwindowmanager = HJWindowManager.getHJWindowManager();
            hjwindowmanager.setDocFontAllWindows();
        }
        return 1;
    }

    private static String propname = "general.text.";
    private HJBProperties props;
    private GridBagConstraints c;
    private Font regular;
    private Font regularBold;
    private Font headings;
    private Choice font;
    private Choice fontSize;
    private Choice print;
    private Choice printSize;
    private UserCheckbox links;
    private UserCheckbox turnOffCBox;
    private KeyListener keylistener;
    private Vector cboxVector;
    private TextField expiredField;
    private UIHJButton expireNowButton;
    private ExpireNowHandler expireNowHandler;

}
