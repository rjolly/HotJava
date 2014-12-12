// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AudioApplet.java

package sunw.hotjava.applets;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EventObject;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.UserImageButton;

// Referenced classes of package sunw.hotjava.applets:
//            HotJavaApplet

public class AudioApplet extends HotJavaApplet
{
    class playAction
        implements ActionListener
    {

        public void actionPerformed(ActionEvent actionevent)
        {
            Object obj = actionevent.getSource();
            if(obj.equals(playButton))
            {
                ac.play();
                return;
            }
            if(obj.equals(stopButton))
                ac.stop();
        }

        playAction()
        {
        }
    }


    public void init()
    {
        url = null;
        setLayout(new FlowLayout(1, 5, 5));
        playAction playaction = new playAction();
        java.awt.Image image = props.getImage(System.getProperty("button.forward.up"));
        if(image != null)
        {
            UserImageButton userimagebutton = new UserImageButton("forward");
            userimagebutton.addActionListener(playaction);
            playButton = userimagebutton;
        } else
        {
            Button button = new Button("Play");
            button.addActionListener(playaction);
            playButton = button;
        }
        add(playButton);
        image = props.getImage(System.getProperty("button.stop.up"));
        if(image != null)
        {
            UserImageButton userimagebutton1 = new UserImageButton("stop");
            userimagebutton1.addActionListener(playaction);
            stopButton = userimagebutton1;
        } else
        {
            Button button1 = new Button("Stop");
            button1.addActionListener(playaction);
            stopButton = button1;
        }
        add(stopButton);
        setVisible(true);
        try
        {
            url = new URL(getParameter("url"));
        }
        catch(MalformedURLException _ex)
        {
            System.err.println("saveFile: bad url " + getParameter("url"));
        }
        ac = getAudioClip(url);
    }

    public void start()
    {
        ac.play();
    }

    public void stop()
    {
        ac.stop();
    }

    public AudioApplet()
    {
        props = HJBProperties.getHJBProperties("hjbrowser");
    }

    public static final String BUTTON_PLAY = "Play";
    public static final String BUTTON_STOP = "Stop";
    public Component playButton;
    public Component stopButton;
    URL url;
    HJBProperties props;
    AudioClip ac;
}
