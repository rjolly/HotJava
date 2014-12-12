// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Console.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            Console, ConsoleOutput, RaisedPanel

class ConsoleFrame extends Frame
{
    class FrameCloseAdapter extends WindowAdapter
    {

        public void windowIconified(WindowEvent windowevent)
        {
            iconic = true;
        }

        public void windowDeiconified(WindowEvent windowevent)
        {
            iconic = false;
        }

        public void windowClosing(WindowEvent windowevent)
        {
            Console.close();
        }

        FrameCloseAdapter()
        {
        }
    }

    class ClearConsole
        implements ActionListener
    {

        public void actionPerformed(ActionEvent actionevent)
        {
            Console.clear();
        }

        ClearConsole()
        {
        }
    }

    class CloseConsole
        implements ActionListener
    {

        public void actionPerformed(ActionEvent actionevent)
        {
            Console.close();
        }

        CloseConsole()
        {
        }
    }


    ConsoleFrame()
    {
        super("javaconsole");
        iconic = false;
        properties = HJBProperties.getHJBProperties("hjbrowser");
        addWindowListener(new FrameCloseAdapter());
        String s = properties.getProperty("javaconsole.title");
        if(s != null)
            setTitle(s);
        java.awt.Image image = properties.getImage("hotjava.icon");
        if(image != null)
            setLayout(new BorderLayout());
        add("South", newButtonPanel());
        try
        {
            add("Center", newConsoleTextArea());
            pack();
            setVisible(true);
            Console.showStatus("enabled.msg");
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
            Console.showStatus("redirectFail.msg");
        }
        setBackground(properties.getColor("hotjava.background", null));
    }

    TextArea newConsoleTextArea()
        throws Throwable
    {
        int i = properties.getInteger("javaconsole.textRows", 16);
        int j = properties.getInteger("javaconsole.textColumns", 52);
        TextArea textarea = new TextArea("", i, j, 1);
        Font font = Font.getFont("javaconsole.textFont");
        if(font != null)
            textarea.setFont(font);
        textarea.setEditable(false);
        console = new ConsoleOutput(textarea);
        return textarea;
    }

    Panel newButtonPanel()
    {
        RaisedPanel raisedpanel = new RaisedPanel();
        raisedpanel.setLayout(new FlowLayout(1, 18, 4));
        Button button = new Button();
        button.setLabel(properties.getProperty("javaconsole.clear.text"));
        button.addActionListener(new ClearConsole());
        Button button1 = new Button();
        button1.setLabel(properties.getProperty("javaconsole.close.text"));
        button1.addActionListener(new CloseConsole());
        raisedpanel.add(button);
        raisedpanel.add(button1);
        return raisedpanel;
    }

    public void toFront()
    {
        if(iconic)
        {
            java.awt.Point point = getLocation();
            setVisible(false);
            setLocation(point);
            setVisible(true);
            return;
        } else
        {
            super.toFront();
            return;
        }
    }

    public void dispose()
    {
        console.dispose();
        super.dispose();
    }

    public void clear()
    {
        console.clear();
    }

    ConsoleOutput console;
    boolean iconic;
    HJBProperties properties;
}
