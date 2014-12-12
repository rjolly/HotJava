// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Locator.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.bean.HTMLBrowsable;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.misc.URLCanonicalizer;

// Referenced classes of package sunw.hotjava.ui:
//            InfoLabel, Locator

class InputField extends TextField
{
    private final class ActionEventsListener
        implements ActionListener
    {

        public void actionPerformed(ActionEvent actionevent)
        {
            String s = getText();
            actionevent.getModifiers();
            try
            {
                URL url = new URL((new URLCanonicalizer()).canonicalize(s));
                if(url.getProtocol().equals("mailto"))
                {
                    HJWindowManager hjwindowmanager = HJWindowManager.getHJWindowManager();
                    hjwindowmanager.createNoDecorFrame(url, hjwindowmanager.getLastFocusHolder()).show();
                    return;
                } else
                {
                    hjbrowser.setDocumentString(s);
                    return;
                }
            }
            catch(MalformedURLException _ex)
            {
                hjbrowser.setDocumentString("doc://unknown.protocol/" + s);
            }
        }

        ActionEventsListener()
        {
        }
    }

    private final class KeyListener extends KeyAdapter
    {

        public void keyPressed(KeyEvent keyevent)
        {
            if(keyevent.isActionKey() || keyevent.getKeyCode() == 27)
                hjbrowser.processKeyActionEvent(keyevent);
        }

        KeyListener()
        {
        }
    }


    public InputField(HTMLBrowsable htmlbrowsable)
    {
        properties = HJBProperties.getHJBProperties("hjbrowser");
        hjbrowser = htmlbrowsable;
        addActionListener(new ActionEventsListener());
        addKeyListener(new KeyListener());
    }

    public void paste(String s)
    {
        int i = getSelectionStart();
        int j = getSelectionEnd();
        String s1 = getText();
        if(s == null)
            s = "";
        String s2 = i != 0 ? s1.substring(0, i) : "";
        int k = s1.length();
        String s3 = j != k ? s1.substring(j, k) : "";
        s1 = s2 + s + s3;
        setText(s1);
    }

    HJBProperties properties;
    HTMLBrowsable hjbrowser;
}
