// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Locator.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.EventObject;
import sunw.hotjava.bean.DocumentSelection;
import sunw.hotjava.bean.HTMLBrowsable;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            InfoLabel, InputField

public class Locator extends Panel
    implements PropertyChangeListener
{
    private final class LocatorFocusListener
        implements FocusListener
    {

        public void focusGained(FocusEvent focusevent)
        {
            processFocusEvent();
        }

        public void focusLost(FocusEvent focusevent)
        {
            processFocusEvent();
        }

        LocatorFocusListener()
        {
        }
    }

    private final class LocatorMouseListener extends MouseAdapter
    {

        public void mouseExited(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mousePressed(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mouseReleased(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mouseDragged(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mouseMoved(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        LocatorMouseListener()
        {
        }
    }

    private final class LocatorKeyListener extends KeyAdapter
    {

        public void keyPressed(KeyEvent keyevent)
        {
            processKeyEvent(keyevent);
        }

        public void keyReleased(KeyEvent keyevent)
        {
            processKeyEvent(keyevent);
        }

        public void keyTyped(KeyEvent keyevent)
        {
            processKeyEvent(keyevent);
        }

        LocatorKeyListener()
        {
        }
    }


    public Locator(HTMLBrowsable htmlbrowsable)
    {
        properties = HJBProperties.getHJBProperties("hjbrowser");
        changeSupport = new PropertyChangeSupport(this);
        HJBrowser = htmlbrowsable;
        GridBagLayout gridbaglayout = new GridBagLayout();
        setLayout(gridbaglayout);
        input = new InputField(htmlbrowsable);
        input.addFocusListener(new LocatorFocusListener());
        input.addMouseListener(new LocatorMouseListener());
        input.addKeyListener(new LocatorKeyListener());
        label = new InfoLabel(input.getFont());
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.anchor = 13;
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(0, 10, 0, 0);
        gridbaglayout.setConstraints(label, gridbagconstraints);
        add(label);
        gridbagconstraints.gridx = 1;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = -1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.fill = 2;
        gridbagconstraints.insets = new Insets(0, 5, 2, 1);
        gridbaglayout.setConstraints(input, gridbagconstraints);
        add(input);
    }

    public void propertyChange(PropertyChangeEvent propertychangeevent)
    {
        if(propertychangeevent.getPropertyName().equals("documentURL"))
        {
            input.setText(propertychangeevent.getNewValue().toString());
            return;
        }
        if(propertychangeevent.getSource() != this && propertychangeevent.getPropertyName().equals("selection") && isSelectedTextExists())
        {
            int i = input.getCaretPosition();
            input.select(i, i);
        }
    }

    public Dimension getMinimumSize()
    {
        Dimension dimension = super.getMinimumSize();
        Insets insets = getInsets();
        dimension.height = Math.max(dimension.height, input.getMinimumSize().height + insets.top + insets.bottom);
        return dimension;
    }

    public Dimension getPreferredSize()
    {
        Dimension dimension = super.getPreferredSize();
        Insets insets = getInsets();
        dimension.height = Math.max(dimension.height, input.getPreferredSize().height + insets.top + insets.bottom);
        return dimension;
    }

    public void paste(String s)
    {
        input.paste(s);
    }

    public void addPropertyChangeListener(PropertyChangeListener propertychangelistener)
    {
        changeSupport.addPropertyChangeListener(propertychangelistener);
    }

    private boolean isSelectedTextExists()
    {
        return input.getSelectionStart() != input.getSelectionEnd();
    }

    protected void processFocusEvent()
    {
        DocumentSelection documentselection = new DocumentSelection();
        if(!isSelectedTextExists())
            documentselection.text = "";
        else
            documentselection.text = input.getSelectedText();
        changeSupport.firePropertyChange("selection", null, documentselection);
    }

    protected void processMouseAction(MouseEvent mouseevent)
    {
        if(mouseevent.getID() == 502)
        {
            DocumentSelection documentselection = new DocumentSelection();
            if(!isSelectedTextExists())
                documentselection.text = "";
            else
                documentselection.text = input.getSelectedText();
            changeSupport.firePropertyChange("selection", null, documentselection);
        }
    }

    public void processKeyEvent(KeyEvent keyevent)
    {
        DocumentSelection documentselection = new DocumentSelection();
        if(!isSelectedTextExists())
            documentselection.text = "";
        else
            documentselection.text = input.getSelectedText();
        changeSupport.firePropertyChange("selection", null, documentselection);
    }

    HJBProperties properties;
    private InfoLabel label;
    private InputField input;
    String docURL;
    HTMLBrowsable HJBrowser;
    protected PropertyChangeSupport changeSupport;
}
