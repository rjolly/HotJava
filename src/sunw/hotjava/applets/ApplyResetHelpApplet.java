// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ApplyResetHelpApplet.java

package sunw.hotjava.applets;

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import sunw.hotjava.HJFrame;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.bean.DocumentStacker;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.applets:
//            HotJavaApplet, PrefAppletManager

public class ApplyResetHelpApplet extends HotJavaApplet
    implements ActionListener
{

    public void init()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        setFont(hjbproperties.getFont("prefs.font"));
        setLayout(new GridLayout(1, 4, 15, 15));
        String s = hjbproperties.getProperty(getParameter("applylabel"), "OK");
        apply = new Button(s);
        apply.setActionCommand("apply");
        apply.addActionListener(this);
        add(apply);
        s = hjbproperties.getProperty(getParameter("cancellabel"), "Cancel");
        cancel = new Button(s);
        cancel.setActionCommand("cancel");
        cancel.addActionListener(this);
        add(cancel);
        s = hjbproperties.getProperty(getParameter("resetlabel"), "Reset");
        reset = new Button(s);
        reset.setActionCommand("reset");
        reset.addActionListener(this);
        add(reset);
        if(getParameter("helpurl") != null)
        {
            String s1 = hjbproperties.getProperty(getParameter("helplabel"), "Help");
            Button button = new Button(s1);
            button.setActionCommand("help");
            button.addActionListener(this);
            add(button);
        }
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        String s = actionevent.getActionCommand();
        Vector vector = new Vector();
        int i = 1;
        do
        {
            String s1 = getParameter("namedapplet" + i++);
            if(s1 == null)
                break;
            vector.addElement(s1);
        } while(true);
        if(s.equals("apply"))
        {
            for(int j = 0; j < vector.size(); j++)
            {
                String s3 = (String)vector.elementAt(j);
                PrefAppletManager.applyApplet(s3);
            }

            HJFrame hjframe1 = HJWindowManager.getHJWindowManager().getLastFocusHolder();
            if(hjframe1.isServiceFrame())
            {
                hjframe1.dispose();
            } else
            {
                hjframe1.removeHistoryObject();
                DocumentStacker documentstacker1 = hjframe1.getDocumentStacker();
                documentstacker1.previousDocument();
            }
        }
        if(s.equals("cancel"))
        {
            HJFrame hjframe = HJWindowManager.getHJWindowManager().getLastFocusHolder();
            if(hjframe.isServiceFrame())
            {
                hjframe.dispose();
            } else
            {
                hjframe.removeHistoryObject();
                DocumentStacker documentstacker = hjframe.getDocumentStacker();
                documentstacker.previousDocument();
            }
        }
        if(s.equals("reset"))
        {
            for(int k = 0; k < vector.size(); k++)
            {
                String s4 = (String)vector.elementAt(k);
                PrefAppletManager.resetApplet(s4);
            }

        }
        if(s.equals("help"))
        {
            String s2 = getParameter("helpurl");
            try
            {
                getAppletContext().showDocument(new URL(s2), "_blank");
                return;
            }
            catch(MalformedURLException _ex)
            {
                return;
            }
        } else
        {
            return;
        }
    }

    public ApplyResetHelpApplet()
    {
    }

    private Button apply;
    private Button cancel;
    private Button reset;
}
