// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ContentViewerUnknownApplet.java

package sunw.hotjava.applets;

import java.applet.Applet;
import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import sunw.hotjava.HJFrame;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.bean.HTMLBrowsable;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.misc.URLApplicationLauncher;
import sunw.hotjava.ui.ConfirmDialog;
import sunw.hotjava.ui.UserTextButton;

// Referenced classes of package sunw.hotjava.applets:
//            HotJavaApplet

public class ContentViewerUnknownApplet extends HotJavaApplet
    implements sunw.hotjava.misc.URLApplicationLauncher.ErrorListener
{

    public ContentViewerUnknownApplet()
    {
        setName("contentviewerunknown");
    }

    public void init()
    {
        super.init();
        setLayout(new FlowLayout());
        props = HJBProperties.getHJBProperties("hjbrowser");
        try
        {
            url = new URL(getParameter("url"));
        }
        catch(MalformedURLException _ex)
        {
            System.err.println("saveFile: bad url " + getParameter("url"));
        }
        targetFile = getParameter("targetFile");
        add(save = new UserTextButton("viewerunknown.save", props));
        add(view = new UserTextButton("viewerunknown.view", props));
        add(application = new UserTextButton("viewerunknown.application", props));
    }

    public boolean action(Event event, Object obj)
    {
        String s = (String)obj;
        if(s.equals(save.getName()))
            return saveFile();
        if(s.equals(view.getName()))
            return viewInBrowser();
        if(s.equals(application.getName()))
            return askForApplication();
        else
            return false;
    }

    boolean saveFile()
    {
        String s = targetFile;
        if(s == null || s.length() == 0)
        {
            s = url.getFile();
            int i = s.lastIndexOf('/');
            if(i >= 0)
                s = s.substring(i + 1);
        }
        File file = askUserForFile("viewerunknown.savedialog", 1, s);
        HJFrame hjframe = HJWindowManager.getHJWindowManager().getLastFocusHolder();
        if(file != null)
        {
            String s1 = file.getAbsolutePath();
            String s2 = props.getPropertyReplace("progressDialog.title.label", "Saving to: ") + s1;
            hjframe.save(url, s1, s2, true, false);
        }
        return true;
    }

    boolean viewInBrowser()
    {
        URL url1 = null;
        try
        {
            url1 = new URL(getParameter("url"));
        }
        catch(MalformedURLException malformedurlexception)
        {
            System.err.println("ContentViewerUnknownApplet bad URL: " + malformedurlexception);
        }
        try
        {
            URL url2 = new URL("verbatim:" + url1.toExternalForm());
            HJFrame hjframe = HJWindowManager.getHJWindowManager().getLastFocusHolder();
            hjframe.getHTMLBrowsable().setDocumentURL(url2);
        }
        catch(MalformedURLException malformedurlexception1)
        {
            System.err.println("ContentViewerUnknownApplet view as-is exception: " + malformedurlexception1);
        }
        return true;
    }

    boolean askForApplication()
    {
        try
        {
            File file = askUserForFile("viewerunknown.getapplicationdialog", 0, "");
            if(file != null)
            {
                String s1 = file.getAbsolutePath();
                if(!file.exists())
                {
                    String s3 = props.getPropertyReplace("viewerunknown.appnotfound.msg", "Application not found  : ");
                    showErrorMessage(s3 + s1);
                } else
                {
                    try
                    {
                        URLApplicationLauncher urlapplicationlauncher = new URLApplicationLauncher(url, s1);
                        urlapplicationlauncher.addListener(this);
                        urlapplicationlauncher.start();
                    }
                    catch(Exception _ex)
                    {
                        String s4 = props.getPropertyReplace("viewerunknown.faillaunch.msg", "Could not launch: ");
                        showErrorMessage(s4 + s1);
                    }
                }
            } else
            {
                String s2 = props.getPropertyReplace("viewerunknown.cancel.msg", "Canceled");
                showStatus(s2);
            }
        }
        catch(NullPointerException _ex)
        {
            String s = props.getPropertyReplace("viewerunknown.nonrecoverable.msg", "Could not launch, a non-recoverable error occurred.");
            showErrorMessage(s);
        }
        return true;
    }

    private Frame getEnclosingFrame()
    {
        Container container;
        for(container = getParent(); container != null && !(container instanceof Frame); container = container.getParent());
        return (Frame)container;
    }

    private void showErrorMessage(String s)
    {
        Frame frame = getEnclosingFrame();
        ConfirmDialog confirmdialog = new ConfirmDialog(getName() + ".error", frame, 1);
        confirmdialog.setPrompt(s);
        confirmdialog.show();
    }

    private void showErrorMessageFromKey(String s)
    {
        String s1 = props.getProperty(getName() + "." + s + ".msg");
        showErrorMessage(s1);
    }

    public void launcherErrorOccurred(sunw.hotjava.misc.URLApplicationLauncher.ErrorEvent errorevent)
    {
        switch(errorevent.getId())
        {
        case 2001: 
            showErrorMessageFromKey("file-not-found");
            return;

        case 2002: 
            showErrorMessageFromKey("file-not-executable");
            return;

        case 2003: 
        default:
            showErrorMessageFromKey("launch-failed");
            return;
        }
    }

    URL url;
    UserTextButton save;
    UserTextButton view;
    UserTextButton application;
    HJBProperties props;
    String targetFile;
}
