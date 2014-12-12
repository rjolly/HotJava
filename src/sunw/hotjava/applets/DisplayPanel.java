// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DisplayPanel.java

package sunw.hotjava.applets;

import java.awt.*;
import java.awt.event.KeyListener;
import sunw.hotjava.applet.PrefsPanel;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.misc.URLCanonicalizer;
import sunw.hotjava.ui.*;

public class DisplayPanel extends Panel
    implements PrefsPanel
{

    public DisplayPanel(KeyListener keylistener, String s, Font font, Font font1, Font font2)
    {
        props = HJBProperties.getHJBProperties("hjbrowser");
        keyListener = keylistener;
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        setLayout(gridbaglayout);
        gridbagconstraints.anchor = 17;
        gridbagconstraints.fill = 0;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 1.0D;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.insets = new Insets(10, 10, 0, 10);
        MultiLineLabel multilinelabel = new MultiLineLabel(s);
        multilinelabel.setMarginWidth(0);
        multilinelabel.setMarginHeight(0);
        multilinelabel.setFont(font2);
        add(multilinelabel, gridbagconstraints);
        gridbagconstraints.insets = new Insets(0, 10, 0, 10);
        MultiLineLabel multilinelabel1 = new MultiLineLabel(props.getProperty(propname + "homepage.label"));
        multilinelabel1.setMarginWidth(0);
        multilinelabel1.setMarginHeight(0);
        multilinelabel1.setFont(font);
        gridbaglayout.setConstraints(multilinelabel1, gridbagconstraints);
        gridbagconstraints.gridy = 1;
        add(multilinelabel1, gridbagconstraints);
        gridbagconstraints.gridwidth = 2;
        gridbagconstraints.gridy = 2;
        gridbagconstraints.gridwidth = 3;
        gridbagconstraints.insets = new Insets(-10, 10, 0, 0);
        gridbagconstraints.anchor = 17;
        gridbagconstraints.fill = 0;
        urlHomePage = new UserTextField("homepage.path");
        urlHomePage.addKeyListener(keylistener);
        urlHomePage.setText(props.getProperty("user.homepage"));
        gridbaglayout.setConstraints(urlHomePage, gridbagconstraints);
        urlHomePage.setFont(font);
        add(urlHomePage);
        gridbagconstraints.ipadx = 5;
        gridbagconstraints.insets = new Insets(10, 10, 0, 10);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 4;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 1.0D;
        Label label = new Label(props.getProperty(propname + "browserstarts.label"));
        gridbaglayout.setConstraints(label, gridbagconstraints);
        label.setFont(font);
        add(label);
        CheckboxGroup checkboxgroup = new CheckboxGroup();
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 5;
        gridbagconstraints.anchor = 17;
        gridbagconstraints.gridwidth = 2;
        gridbagconstraints.insets = new Insets(-10, 10, 0, 10);
        homePageCheckbox = new UserCheckbox("homepage.showhome", checkboxgroup, props);
        gridbaglayout.setConstraints(homePageCheckbox, gridbagconstraints);
        homePageCheckbox.addKeyListener(keylistener);
        add(homePageCheckbox);
        homePageCheckbox.setFont(font);
        gridbagconstraints.gridx = 1;
        gridbagconstraints.gridy = 5;
        gridbagconstraints.gridwidth = 1;
        blankPageCheckbox = new UserCheckbox("homepage.dontshowhome", checkboxgroup, props);
        gridbaglayout.setConstraints(blankPageCheckbox, gridbagconstraints);
        blankPageCheckbox.addKeyListener(keylistener);
        add(blankPageCheckbox);
        blankPageCheckbox.setFont(font);
        boolean flag = props.getBoolean("hotjava.gohome");
        if(flag)
            homePageCheckbox.setState(true);
        else
            blankPageCheckbox.setState(true);
        gridbagconstraints.insets = new Insets(10, 10, 0, 10);
        gridbagconstraints.fill = 1;
        gridbagconstraints.gridwidth = 2;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 7;
        Label label1 = new Label(props.getProperty(propname + "showtoolbar.label"));
        gridbaglayout.setConstraints(label1, gridbagconstraints);
        label1.setFont(font);
        add(label1);
        CheckboxGroup checkboxgroup1 = new CheckboxGroup();
        gridbagconstraints.insets = new Insets(-5, 10, 0, 10);
        gridbagconstraints.fill = 0;
        gridbagconstraints.anchor = 17;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 8;
        imagesAndTextCheckbox = new Checkbox(props.getProperty(propname + "imageandtext.cb"), true, checkboxgroup1);
        gridbaglayout.setConstraints(imagesAndTextCheckbox, gridbagconstraints);
        imagesAndTextCheckbox.addKeyListener(keylistener);
        add(imagesAndTextCheckbox);
        imagesAndTextCheckbox.setFont(font);
        gridbagconstraints.insets = new Insets(-15, 10, 0, 10);
        gridbagconstraints.anchor = 17;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 9;
        imagesOnlyCheckbox = new Checkbox(props.getProperty(propname + "imagesonly.cb"), false, checkboxgroup1);
        gridbaglayout.setConstraints(imagesOnlyCheckbox, gridbagconstraints);
        imagesOnlyCheckbox.addKeyListener(keylistener);
        imagesOnlyCheckbox.setFont(font);
        add(imagesOnlyCheckbox);
        gridbagconstraints.anchor = 17;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 10;
        textOnlyCheckbox = new Checkbox(props.getProperty(propname + "textonly.cb"), false, checkboxgroup1);
        gridbaglayout.setConstraints(textOnlyCheckbox, gridbagconstraints);
        textOnlyCheckbox.addKeyListener(keylistener);
        textOnlyCheckbox.setFont(font);
        add(textOnlyCheckbox);
        String s1 = props.getProperty("hotjava.buttonappearance");
        if(s1.equals("ImageAndText"))
            checkboxgroup1.setSelectedCheckbox(imagesAndTextCheckbox);
        else
        if(s1.equals("ImageOnly"))
            checkboxgroup1.setSelectedCheckbox(imagesOnlyCheckbox);
        else
        if(s1.equals("TextOnly"))
            checkboxgroup1.setSelectedCheckbox(textOnlyCheckbox);
        else
            checkboxgroup1.setSelectedCheckbox(imagesOnlyCheckbox);
        gridbagconstraints.insets = new Insets(10, 10, 0, 10);
        gridbagconstraints.fill = 0;
        gridbagconstraints.gridx = 2;
        gridbagconstraints.gridy = 7;
        Label label2 = new Label(props.getProperty(propname + "screenlayout.label"));
        gridbaglayout.setConstraints(label2, gridbagconstraints);
        label2.setFont(font);
        add(label2);
        gridbagconstraints.insets = new Insets(-5, 10, 0, 0);
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.gridx = 2;
        gridbagconstraints.gridy = 8;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.anchor = 17;
        Label label3 = new Label(props.getProperty(propname + "buttons.label"), 0);
        gridbaglayout.setConstraints(label3, gridbagconstraints);
        label3.setFont(font);
        add(label3);
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.gridx = 3;
        gridbagconstraints.insets = new Insets(-5, -50, 0, 10);
        buttonsChoice = new UserChoice("buttonstyle", props);
        gridbaglayout.setConstraints(buttonsChoice, gridbagconstraints);
        buttonsChoice.addKeyListener(keylistener);
        add(buttonsChoice);
        buttonsChoice.setFont(font);
        buttonsChoice.select(propsToChoice("hotjava.default.toolbar.position"));
        gridbagconstraints.gridx = 2;
        gridbagconstraints.gridy = 9;
        gridbagconstraints.insets = new Insets(-15, 10, 0, 0);
        Label label4 = new Label(props.getProperty(propname + "url.label"), 0);
        gridbaglayout.setConstraints(label4, gridbagconstraints);
        label4.setFont(font);
        add(label4);
        gridbagconstraints.gridx = 3;
        gridbagconstraints.insets = new Insets(-15, -50, 0, 10);
        urlChoice = new UserChoice("locatorstyle", props);
        gridbaglayout.setConstraints(urlChoice, gridbagconstraints);
        urlChoice.addKeyListener(keylistener);
        add(urlChoice);
        urlChoice.setFont(font);
        urlChoice.select(propsToChoice("hotjava.locator.position"));
        gridbagconstraints.gridx = 2;
        gridbagconstraints.gridy = 10;
        gridbagconstraints.insets = new Insets(-15, 10, 0, 0);
        Label label5 = new Label(props.getProperty(propname + "statusbar.label"), 0);
        gridbaglayout.setConstraints(label5, gridbagconstraints);
        label5.setFont(font);
        add(label5);
        gridbagconstraints.gridx = 3;
        gridbagconstraints.insets = new Insets(-15, -50, 0, 10);
        statusChoice = new UserChoice("messagelinestyle", props);
        gridbaglayout.setConstraints(statusChoice, gridbagconstraints);
        statusChoice.addKeyListener(keylistener);
        add(statusChoice);
        statusChoice.setFont(font);
        statusChoice.select(propsToChoice("hotjava.messageline.position"));
        gridbagconstraints.insets = new Insets(0, 10, 0, 0);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridwidth = 2;
        gridbagconstraints.gridy = 12;
        gifAnimationsCheckbox = new Checkbox(props.getProperty(propname + "playgif.label"), true);
        gridbaglayout.setConstraints(gifAnimationsCheckbox, gridbagconstraints);
        gifAnimationsCheckbox.addKeyListener(keylistener);
        add(gifAnimationsCheckbox);
        gifAnimationsCheckbox.setFont(font);
        gifAnimationsCheckbox.setState(!props.getBoolean("turnOffGifAnimations"));
        gridbagconstraints.gridx = 2;
        gridbagconstraints.gridy = 12;
        loadImagesCheckbox = new Checkbox(props.getProperty(propname + "loadimages.label"), true);
        gridbaglayout.setConstraints(loadImagesCheckbox, gridbagconstraints);
        loadImagesCheckbox.addKeyListener(keylistener);
        add(loadImagesCheckbox);
        loadImagesCheckbox.setFont(font);
        loadImagesCheckbox.setState(!props.getBoolean("delayImageLoading"));
        gridbagconstraints.insets = new Insets(-10, 10, 0, 10);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 13;
        confirmOnExitCheckbox = new Checkbox(props.getProperty(propname + "exitconfirm.label"), true);
        gridbaglayout.setConstraints(confirmOnExitCheckbox, gridbagconstraints);
        confirmOnExitCheckbox.addKeyListener(keylistener);
        add(confirmOnExitCheckbox);
        confirmOnExitCheckbox.setFont(font);
        confirmOnExitCheckbox.setState(props.getBoolean("quitConfirmation"));
        gridbagconstraints.gridx = 2;
        gridbagconstraints.gridy = 13;
        loadAppletsCheckbox = new Checkbox(props.getProperty(propname + "loadapplets.label"), true);
        gridbaglayout.setConstraints(loadAppletsCheckbox, gridbagconstraints);
        loadAppletsCheckbox.addKeyListener(keylistener);
        add(loadAppletsCheckbox);
        loadAppletsCheckbox.setFont(font);
        loadAppletsCheckbox.setState(!props.getBoolean("delayAppletLoading"));
        gridbagconstraints.insets = new Insets(0, 10, 0, 0);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 14;
        gridbagconstraints.fill = 0;
        gridbagconstraints.gridwidth = 4;
        showHotlistCheckbox = new UserCheckbox("hotjava.show.hotlist", props);
        gridbaglayout.setConstraints(showHotlistCheckbox, gridbagconstraints);
        showHotlistCheckbox.setFont(font);
        add(showHotlistCheckbox);
        showHotlistCheckbox.setState(props.getBoolean("hotlistmenucheckbox.state"));
    }

    public void stop()
    {
        urlHomePage.removeKeyListener(keyListener);
        homePageCheckbox.removeKeyListener(keyListener);
        blankPageCheckbox.removeKeyListener(keyListener);
        imagesAndTextCheckbox.removeKeyListener(keyListener);
        imagesOnlyCheckbox.removeKeyListener(keyListener);
        textOnlyCheckbox.removeKeyListener(keyListener);
        buttonsChoice.removeKeyListener(keyListener);
        statusChoice.removeKeyListener(keyListener);
        urlChoice.removeKeyListener(keyListener);
        loadImagesCheckbox.removeKeyListener(keyListener);
        gifAnimationsCheckbox.removeKeyListener(keyListener);
        loadAppletsCheckbox.removeKeyListener(keyListener);
        confirmOnExitCheckbox.removeKeyListener(keyListener);
    }

    public int apply()
    {
        boolean flag = false;
        flag |= updateProp("hotjava.gohome", homePageCheckbox.getState());
        String s = urlHomePage.getText();
        if(s != null && !s.equals(""))
        {
            URLCanonicalizer urlcanonicalizer = new URLCanonicalizer();
            s = urlcanonicalizer.canonicalize(s);
        }
        flag |= updateProp("user.homepage", s);
        String s1;
        if(imagesAndTextCheckbox.getState())
            s1 = "ImageAndText";
        else
        if(imagesOnlyCheckbox.getState())
            s1 = "ImageOnly";
        else
        if(textOnlyCheckbox.getState())
            s1 = "TextOnly";
        else
            s1 = "ImageOnly";
        flag |= updateProp("hotjava.buttonappearance", s1);
        flag |= updateProp("hotjava.default.toolbar.position", choiceToProps(buttonsChoice.getSelectedIndex()));
        flag |= updateProp("hotjava.locator.position", choiceToProps(urlChoice.getSelectedIndex()));
        flag |= updateProp("hotjava.messageline.position", choiceToProps(statusChoice.getSelectedIndex()));
        flag |= updateProp("hotlistmenucheckbox.state", showHotlistCheckbox.getState());
        updateProp("quitConfirmation", confirmOnExitCheckbox.getState());
        updateProp("delayAppletLoading", !loadAppletsCheckbox.getState());
        updateProp("delayImageLoading", !loadImagesCheckbox.getState());
        updateProp("turnOffGifAnimations", !gifAnimationsCheckbox.getState());
        return !flag ? 1 : 2;
    }

    private boolean updateProp(String s, boolean flag)
    {
        return updateProp(s, flag ? "true" : "false");
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

    private int propsToChoice(String s)
    {
        String s1 = props.getProperty(s);
        if(s1.equals("top"))
            return 0;
        if(s1.equals("bottom"))
            return 1;
        if(s1.equals("left"))
            return 2;
        return !s1.equals("right") ? 4 : 3;
    }

    private String choiceToProps(int i)
    {
        if(i == 0)
            return "top";
        if(i == 1)
            return "bottom";
        if(i == 2)
            return "left";
        if(i == 3)
            return "right";
        else
            return "none";
    }

    private static String propname = "general.display.preferences.";
    private HJBProperties props;
    private UserTextField urlHomePage;
    private UserCheckbox homePageCheckbox;
    private UserCheckbox blankPageCheckbox;
    private Checkbox imagesOnlyCheckbox;
    private Checkbox imagesAndTextCheckbox;
    private Checkbox textOnlyCheckbox;
    private UserChoice buttonsChoice;
    private UserChoice statusChoice;
    private UserChoice urlChoice;
    private UserCheckbox showHotlistCheckbox;
    private Checkbox confirmOnExitCheckbox;
    private Checkbox loadAppletsCheckbox;
    private Checkbox loadImagesCheckbox;
    private Checkbox gifAnimationsCheckbox;
    private KeyListener keyListener;

}
