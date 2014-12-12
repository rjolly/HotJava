// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Main.java

package sunw.hotjava;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import sun.net.www.http.HttpClient;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.misc.URLCanonicalizer;
import sunw.hotjava.ui.SplashFrame;

// Referenced classes of package sunw.hotjava:
//            HJWindowManager, PeriodicSaveManager

public class Main
{

    public static void main(String args[])
    {
        boolean flag = false;
        String s = null;
        for(int i = 0; i < args.length; i++)
            if(args[i].equals("-nosplash"))
                flag = true;
            else
            if(args[i].equals("-log"))
                try
                {
                    FileOutputStream fileoutputstream = new FileOutputStream(args[++i]);
                    PrintStream printstream = new PrintStream(fileoutputstream);
                    System.setOut(printstream);
                    System.setErr(printstream);
                }
                catch(IOException ioexception)
                {
                    ioexception.printStackTrace();
                }
            else
            if(!args[i].startsWith("-"))
                s = args[i];

        SplashFrame splashframe = null;
        if(!flag)
        {
            splashframe = new SplashFrame("HotJava");
            splashframe.show();
            splashframe.toFront();
            splashframe.waitTillLoaded();
        }
        Properties properties = System.getProperties();
        String s1 = properties.getProperty("exec.path");
        if(s1 != null)
        {
            char c = properties.getProperty("path.separator", ":").charAt(0);
            char c1 = '|';
            String s2 = properties.getProperty("exec.path").replace(c, c1);
            properties.put("exec.path", s2);
            System.setProperties(properties);
        }
        String s3 = "/";
        String s4 = System.getProperty("admin.properties");
        if(s4 == null)
        {
            String s5 = System.getProperty("hotjava.home");
            File file = new File(s5 + s3 + "properties.admin");
            if(file.exists())
                s4 = s5 + s3 + "properties.admin";
        }
        Vector vector = new Vector();
        vector.addElement(s3 + "lib" + s3 + "hotjavaBean.properties");
        vector.addElement(s3 + "lib" + s3 + "hotjavaBrowser.properties");
        vector.addElement(s3 + "lib" + s3 + "properties." + System.getProperty("os.name").toLowerCase());
        vector.addElement(s3 + "lib" + s3 + "properties." + System.getProperty("user.language"));
        vector.addElement(s3 + "lib" + s3 + "customizable.properties");
        if(s4 != null)
            vector.addElement(s4);
        String args1[] = new String[vector.size()];
        vector.copyInto(args1);
        vector = null;
        String args2[] = {
            "hjResourceBundle", "hjbrowserResourceBundle"
        };
        HJBProperties.createProperties("hjbrowser", args1, args2);
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        if(hjbproperties.getProperty("hotjava.charset") == null)
            hjbproperties.put("hotjava.charset", "8859_1");
        HttpClient.resetProperties();
        if(s == null && !hjbproperties.getBoolean("hotjava.gohome"))
            s = "doc:/lib/hotjava/blank.html";
        if(s == null || s.equals(""))
            s = hjbproperties.getProperty("user.homepage", hjbproperties.getProperty("home.url", "http://www.sun.com"));
        String s6 = hjbproperties.getProperty("hotjava.version");
        String s7 = hjbproperties.getProperty("properties.version");
        URLCanonicalizer urlcanonicalizer = new URLCanonicalizer();
        s = urlcanonicalizer.canonicalize(s);
        try
        {
            URL url = new URL(s);
            HJWindowManager hjwindowmanager = HJWindowManager.getHJWindowManager();
            HJFrame hjframe = hjwindowmanager.createFrame(url);
            PeriodicSaveManager periodicsavemanager = PeriodicSaveManager.getPeriodicSaveManager();
            Thread thread = new Thread(periodicsavemanager, "autosave");
            thread.setPriority(3);
            thread.start();
            hjframe.show();
            if(!s7.equals(s6))
            {
                hjbproperties.put("hotjava.version", s7);
                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                Dimension dimension1 = new Dimension(510, 495);
                Point point = new Point((dimension.width - dimension1.width) / 2, (dimension.height - dimension1.height) / 2);
                s = hjbproperties.getProperty("welcome.doc", "doc:/lib/hotjava/welcome.html");
                URL url1 = new URL(s);
                HJFrame hjframe1 = hjwindowmanager.createFrame(url1, false, false, false, false, point, dimension1);
                hjframe1.show();
            }
        }
        catch(MalformedURLException _ex)
        {
            System.out.println("Bad URL! " + s);
        }
        if(!flag)
        {
            splashframe.hide();
            splashframe.dispose();
        }
    }

    public Main()
    {
    }
}
