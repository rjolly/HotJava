// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SaveToFileApplet.java

package sunw.hotjava.applets;

import java.applet.Applet;
import java.awt.*;
import java.io.File;
import java.net.URL;
import sunw.hotjava.HJFrame;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.bean.DocumentStacker;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.applets:
//            HotJavaApplet

public class SaveToFileApplet extends HotJavaApplet
{

    public void init()
    {
        frame = HJWindowManager.getHJWindowManager().getLastFocusHolder();
        props = HJBProperties.getHJBProperties("hjbrowser");
        setLayout(new FlowLayout());
        saveButton = new Button(getParameter("saveLabel", "Save"));
        add(saveButton);
        cancelButton = new Button(getParameter("cancelLabel", "Cancel"));
        add(cancelButton);
    }

    private void revertWindow()
    {
        frame.removeHistoryObject();
        DocumentStacker documentstacker = frame.getDocumentStacker();
        documentstacker.previousDocument();
    }

    public boolean action(Event event, Object obj)
    {
        if(event.target == cancelButton)
        {
            revertWindow();
            return true;
        }
        if(event.target == saveButton)
        {
            URL url = null;
            try
            {
                url = new URL(getParameter("url"));
            }
            catch(Exception _ex) { }
            String s = getParameter("targetFile");
            if(s == null || s.length() == 0)
            {
                s = url.getFile();
                int i = s.lastIndexOf('/');
                if(i >= 0)
                    s = s.substring(i + 1);
            }
            File file = askUserForFile("viewerunknown.savedialog", 1, s);
            if(file != null)
            {
                String s1 = file.getAbsolutePath();
                String s2 = props.getPropertyReplace("progressDialog.title.label", "Saving to: ") + s1;
                frame.save(url, s1, s2, true, false);
            }
            return true;
        } else
        {
            return super.action(event, obj);
        }
    }

    public SaveToFileApplet()
    {
    }

    private Button saveButton;
    private Button cancelButton;
    HJBProperties props;
    HJFrame frame;
}
