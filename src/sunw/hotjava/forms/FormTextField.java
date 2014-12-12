// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FormTextField.java

package sunw.hotjava.forms;

import java.awt.*;
import java.awt.event.*;
import sunw.hotjava.doc.DocumentEvent;
import sunw.hotjava.doc.DocumentPanel;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.forms:
//            FormPanel

public class FormTextField extends TextField
{

    FormTextField(int i)
    {
        maxChars = 0x7fffffff;
        init(i);
    }

    FormTextField(int i, int j)
    {
        super(j);
        maxChars = 0x7fffffff;
        init(i);
    }

    FormTextField(int i, FormPanel formpanel)
    {
        this(i);
        panel = formpanel;
    }

    FormTextField(int i, int j, FormPanel formpanel)
    {
        this(i, j);
        panel = formpanel;
    }

    void init(int i)
    {
        maxChars = i;
        KeyAdapter keyadapter = new KeyAdapter() {

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

        }
;
        addKeyListener(keyadapter);
        TextListener textlistener = new TextListener() {

            public void textValueChanged(TextEvent textevent)
            {
                processTextEvent(textevent);
            }

        }
;
        addTextListener(textlistener);
    }

    public void processKeyEvent(KeyEvent keyevent)
    {
        switch(keyevent.getKeyCode())
        {
        case 33: // '!'
        case 34: // '"'
        case 35: // '#'
        case 36: // '$'
        case 38: // '&'
        case 40: // '('
            if(panel != null)
            {
                sunw.hotjava.bean.HotJavaBrowserBean hotjavabrowserbean = panel.getContainingHotJavaBrowserBean();
                hotjavabrowserbean.processKeyActionEvent(keyevent);
            }
            return;

        default:
            if(keyevent.getID() == 401)
            {
                if(keyevent.isActionKey())
                    return;
                int i = getText().length();
                if(i + 1 > maxChars)
                {
                    int j = getSelectionStart();
                    int k = getSelectionEnd();
                    if(j == k || (i - (k - j)) + 1 > maxChars)
                    {
                        Toolkit.getDefaultToolkit().beep();
                        keyevent.consume();
                        return;
                    }
                }
            }
            break;

        case 8: // '\b'
        case 9: // '\t'
        case 10: // '\n'
        case 37: // '%'
        case 39: // '\''
        case 127: // '\177'
            break;
        }
        if(panel != null && getSelectedText() != null && !getSelectedText().equals(""))
        {
            panel.unselectTextInOtherComponent();
            panel.docSel = getSelectedText();
            panel.dispatchDocumentEvent(1007);
        }
    }

    public void processTextEvent(TextEvent textevent)
    {
        if(getText().length() > maxChars)
        {
            Toolkit.getDefaultToolkit().beep();
            setText(getText().substring(0, maxChars));
        }
    }

    public void paint(Graphics g)
    {
        setBackground(Color.white);
        setForeground(Color.black);
    }

    public void setText(String s)
    {
        if(s.length() > maxChars)
        {
            super.setText(s.substring(0, maxChars));
            return;
        } else
        {
            super.setText(s);
            return;
        }
    }

    public Dimension getPreferredSize(int i)
    {
        String s = Toolkit.getDefaultToolkit().getClass().getName();
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        Dimension dimension = super.getPreferredSize(i);
        dimension.width += hjbproperties.getInteger("input.adjustment." + s, 0);
        return dimension;
    }

    int maxChars;
    FormPanel panel;
}
