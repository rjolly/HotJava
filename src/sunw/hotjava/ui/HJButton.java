// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HJButton.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;

public class HJButton extends Button
    implements KeyListener
{

    public HJButton()
    {
        addKeyListener(this);
    }

    public HJButton(String s)
    {
        super(s);
        addKeyListener(this);
    }

    protected void processEvent(AWTEvent awtevent)
    {
        if(awtevent instanceof ActionEvent)
        {
            processActionEvent((ActionEvent)awtevent);
            return;
        }
        if(awtevent instanceof KeyEvent)
        {
            KeyEvent keyevent = (KeyEvent)awtevent;
            if(keyevent.getID() == 401 && keyevent.getKeyCode() == 10)
            {
                ActionEvent actionevent = new ActionEvent(this, 0, getActionCommand());
                processActionEvent(actionevent);
                return;
            }
        }
        super.processEvent(awtevent);
    }

    public void keyPressed(KeyEvent keyevent)
    {
    }

    public void keyReleased(KeyEvent keyevent)
    {
    }

    public void keyTyped(KeyEvent keyevent)
    {
    }
}
