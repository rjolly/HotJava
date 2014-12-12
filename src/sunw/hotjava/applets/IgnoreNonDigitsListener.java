// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TextPanel.java

package sunw.hotjava.applets;

import java.awt.Toolkit;
import java.awt.event.*;

// Referenced classes of package sunw.hotjava.applets:
//            ExpireNowHandler, TextPanel

class IgnoreNonDigitsListener extends KeyAdapter
{

    public void keyPressed(KeyEvent keyevent)
    {
        switch(keyevent.getKeyCode())
        {
        default:
            char c = keyevent.getKeyChar();
            if(!keyevent.isActionKey() && (c < '0' || c > '9'))
            {
                Toolkit.getDefaultToolkit().beep();
                keyevent.consume();
            }
            // fall through

        case 8: // '\b'
        case 9: // '\t'
        case 10: // '\n'
        case 16: // '\020'
        case 127: // '\177'
            return;
        }
    }

    IgnoreNonDigitsListener()
    {
    }
}
