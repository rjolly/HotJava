// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotJavaApplet.java

package sunw.hotjava.applets;

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.io.File;
import java.net.*;
import sunw.hotjava.HJFrame;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.ConfirmDialog;
import sunw.hotjava.ui.UserFileDialog;

public class HotJavaApplet extends Applet
{

    public String getParameter(String s, String s1)
    {
        String s2 = getParameter(s);
        if(s2 != null)
            return s2;
        else
            return s1;
    }

    protected String getMailAddress()
    {
        String s = properties.getProperty("user.fromaddr");
        if(s == null)
        {
            s = System.getProperty("user.name");
            if(s != null)
            {
                String s1 = properties.getProperty("mail.host");
                if(s1 == null)
                    try
                    {
                        s1 = InetAddress.getLocalHost().getHostName();
                    }
                    catch(UnknownHostException _ex) { }
                s = s + "@" + s1;
            } else
            {
                s = "";
            }
        }
        return s;
    }

    protected void showDocument(URL url)
    {
        getAppletContext().showDocument(url);
    }

    protected int getIntParameter(String s, int i)
    {
        String s1 = getParameter(s);
        if(s1 != null)
            try
            {
                return Integer.parseInt(s1);
            }
            catch(NumberFormatException _ex) { }
        return i;
    }

    public File askUserForFile(String s, int i)
    {
        return askUserForFile(s, i, null);
    }

    public File askUserForFile(String s, int i, String s1)
    {
        HJFrame hjframe = HJWindowManager.getHJWindowManager().getLastFocusHolder();
        File file = null;
        UserFileDialog userfiledialog = new UserFileDialog(hjframe, s, i);
        userfiledialog.setDirectory(getFileDialogDirectory());
        if(s1 != null)
            userfiledialog.setFile(s1);
        userfiledialog.show();
        hjframe.showStatus("");
        String s2 = userfiledialog.getDirectory();
        setFileDialogDirectory(s2);
        String s3 = userfiledialog.getFile();
        if(s3 != null)
        {
            if(s2.endsWith(File.separator))
                s2 = s2.substring(0, s2.lastIndexOf(File.separator));
            file = new File(s2, s3);
            if(i == 1 && !userfiledialog.providesSaveConfirmation() && file.exists())
            {
                ConfirmDialog confirmdialog = new ConfirmDialog("overwrite.file", hjframe);
                confirmdialog.show();
                if(confirmdialog.getAnswer() == 0)
                    file = null;
            }
        }
        return file;
    }

    private String getFileDialogDirectory()
    {
        if(fileDialogDirectory == null)
        {
            fileDialogDirectory = System.getProperty("hotjava.file.dialog.directory");
            if(fileDialogDirectory != null)
            {
                File file = new File(fileDialogDirectory);
                try
                {
                    if(!file.isDirectory())
                        fileDialogDirectory = null;
                }
                catch(SecurityException _ex)
                {
                    fileDialogDirectory = null;
                }
            }
            if(fileDialogDirectory == null)
                if(Boolean.getBoolean("hotjava.file.dialog.use.startup.dir"))
                    fileDialogDirectory = System.getProperty("user.dir");
                else
                    fileDialogDirectory = System.getProperty("user.home");
        }
        return fileDialogDirectory;
    }

    public static void setFileDialogDirectory(String s)
    {
        fileDialogDirectory = s;
    }

    public HotJavaApplet()
    {
    }

    static String fileDialogDirectory;
    public static HJBProperties properties = HJBProperties.getHJBProperties("hjbrowser");

}
