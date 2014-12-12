// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EnterKeyListener.java

package sunw.hotjava.ui;

import java.awt.AWTEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Referenced classes of package sunw.hotjava.ui:
//            KeyPressInterest

public class EnterKeyListener extends KeyAdapter
{

    public EnterKeyListener(KeyPressInterest keypressinterest)
    {
        comp = keypressinterest;
    }

    public void keyPressed(KeyEvent keyevent)
    {
        if(keyevent.getID() == 401 && keyevent.getKeyCode() == 10)
            comp.processEnterEvent(keyevent);
    }

    KeyPressInterest comp;
}
