// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HJPopupMenu.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import sunw.hotjava.bean.DocumentStacker;
import sunw.hotjava.doc.ElementInfo;
import sunw.hotjava.misc.HJBProperties;

public class HJPopupMenu extends PopupMenu
{

    public HJPopupMenu(Component component, ActionListener actionlistener, DocumentStacker documentstacker)
    {
        super(title());
        props = HJBProperties.getHJBProperties("hjbrowser");
        parentComponent = component;
        stacker = documentstacker;
        createImageMenuItems(actionlistener);
        createLinkMenuItems(actionlistener);
        createFrameMenuItems(actionlistener);
        createStandardMenuItems(actionlistener);
    }

    private static String title()
    {
        props = HJBProperties.getHJBProperties("hjbrowser");
        String s = props.getProperty("hotjava.popupMenu.title");
        if(s == null)
            return "HotJava Commands";
        int i = s.indexOf('|');
        if(i >= 0)
            return s.substring(0, i);
        else
            return s;
    }

    public synchronized void show(ElementInfo elementinfo)
    {
        removeAll();
        if(elementinfo.hrefURL != null)
            addLinkItems(elementinfo.hrefURL);
        if(elementinfo.imageURL != null)
            addImageItems(elementinfo.imageURL);
        if(elementinfo.frameURL != null)
            addFrameItems(elementinfo.frameURL);
        addStandardItems(elementinfo.frameURL == null);
        parentComponent.add(this);
        Point point = parentComponent.getLocationOnScreen();
        Point point1 = elementinfo.event.getComponent().getLocationOnScreen();
        point.x = (point1.x - point.x) + elementinfo.event.getX();
        point.y = (point1.y - point.y) + elementinfo.event.getY();
        super.show(parentComponent, point.x, point.y);
    }

    private void addStandardItems(boolean flag)
    {
        add(back);
        if(stacker.isPreviousAvailable())
            back.enable();
        else
            back.disable();
        add(forward);
        if(stacker.isNextAvailable())
            forward.enable();
        else
            forward.disable();
        add(open);
        if(flag)
            add(pageSource);
    }

    private void addImageItems(String s)
    {
        add(openImage);
        add(saveImage);
        add(openImageInNewFrame);
        addSeparator();
        openImage.setActionCommand("go " + s);
        openImageInNewFrame.setActionCommand("clonego " + s);
        saveImage.setActionCommand("savefile " + s);
        s = s.substring(s.lastIndexOf("/") + 1);
        saveImage.setLabel(props.getPropertyReplace("hotjava.imageMenu.save", s));
    }

    private void addLinkItems(String s)
    {
        add(openLink);
        add(rememberLink);
        add(openLinkInNewFrame);
        add(saveLink);
        addSeparator();
        openLink.setActionCommand("go " + s);
        openLinkInNewFrame.setActionCommand("clonego " + s);
        saveLink.setActionCommand("savefile " + s);
        rememberLink.setActionCommand("remember " + s);
        s = s.substring(s.lastIndexOf("/") + 1);
        saveLink.setLabel(props.getPropertyReplace("hotjava.linkMenu.save", s));
    }

    private void addFrameItems(String s)
    {
        add(frameSource);
        frameSource.setActionCommand("framesource");
        add(openFrameInNewWindow);
        openFrameInNewWindow.setActionCommand("clonego " + s);
        addSeparator();
    }

    private void createStandardMenuItems(ActionListener actionlistener)
    {
        back = createMenuItem("hotjava.navigationMenu.back", "back");
        back.addActionListener(actionlistener);
        forward = createMenuItem("hotjava.navigationMenu.forward", "forward");
        forward.addActionListener(actionlistener);
        open = createMenuItem("hotjava.navigationMenu.showgotoplacedialog", "");
        open.addActionListener(actionlistener);
        open.setActionCommand("showgotoplacedialog");
        pageSource = createMenuItem("hotjava.navigationMenu.pageSource", "");
        pageSource.addActionListener(actionlistener);
        pageSource.setActionCommand("source");
    }

    private void createLinkMenuItems(ActionListener actionlistener)
    {
        openLink = createMenuItem("hotjava.linkMenu.open", "");
        openLink.addActionListener(actionlistener);
        rememberLink = createMenuItem("hotjava.linkMenu.remember", "");
        rememberLink.addActionListener(actionlistener);
        openLinkInNewFrame = createMenuItem("hotjava.linkMenu.openClone", "");
        openLinkInNewFrame.addActionListener(actionlistener);
        saveLink = createMenuItem("hotjava.linkMenu.save", "");
        saveLink.addActionListener(actionlistener);
    }

    private void createImageMenuItems(ActionListener actionlistener)
    {
        openImage = createMenuItem("hotjava.imageMenu.open", "");
        openImage.addActionListener(actionlistener);
        saveImage = createMenuItem("hotjava.imageMenu.save", "");
        saveImage.addActionListener(actionlistener);
        openImageInNewFrame = createMenuItem("hotjava.imageMenu.openClone", "");
        openImageInNewFrame.addActionListener(actionlistener);
    }

    private void createFrameMenuItems(ActionListener actionlistener)
    {
        frameSource = createMenuItem("hotjava.frameMenu.viewSource", "");
        frameSource.addActionListener(actionlistener);
        openFrameInNewWindow = createMenuItem("hotjava.frameMenu.openClone", "");
        openFrameInNewWindow.addActionListener(actionlistener);
    }

    private MenuItem createMenuItem(String s, String s1)
    {
        String s2 = props.getProperty(s);
        MenuItem menuitem = new MenuItem(s2);
        menuitem.setActionCommand(s1);
        return menuitem;
    }

    private static HJBProperties props;
    private boolean isShowing;
    private Component parentComponent;
    private MenuItem back;
    private MenuItem forward;
    private MenuItem open;
    private MenuItem pageSource;
    private MenuItem openLink;
    private MenuItem rememberLink;
    private MenuItem openLinkInNewFrame;
    private MenuItem saveLink;
    private MenuItem openImage;
    private MenuItem saveImage;
    private MenuItem openImageInNewFrame;
    private MenuItem frameSource;
    private MenuItem openFrameInNewWindow;
    private DocumentStacker stacker;
}
