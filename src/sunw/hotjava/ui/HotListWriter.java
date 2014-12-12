// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotListWriter.java

package sunw.hotjava.ui;

import java.awt.Dialog;
import java.io.File;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            ConfirmDialog, HotList, PageFolder

public class HotListWriter
    implements Runnable
{

    public HotListWriter(HotList hotlist)
    {
        updateTimeStamp = false;
        exportList = false;
        hl = hotlist;
        String s = System.getProperty("user.home");
        if(s != null && !s.endsWith(File.separator))
            s = s + File.separator;
        tmpFile = s + ".hotjava" + File.separator + "temp.html";
    }

    public void export(PageFolder pagefolder, String s, boolean flag, boolean flag1)
    {
        stopThread();
        File file = new File(s);
        long l = file.lastModified();
        long l1 = hl.getFileTimeStamp();
        if(flag && l1 != 0L && l != l1 && file.exists())
        {
            String s1 = System.getProperty("user.home");
            if(s1 != null && !s1.endsWith(File.separator))
                s1 = s1 + File.separator;
            String s2 = s1 + ".hotjava" + File.separator + "hotlist.html." + l;
            sunw.hotjava.HJFrame hjframe = HJWindowManager.getHJWindowManager().getLastFocusHolder();
            ConfirmDialog confirmdialog = new ConfirmDialog("hotlist.reread", hjframe, 1);
            String s3 = "confirm.hotlist.reread.exact.prompt";
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
            String s4 = hjbproperties.getPropertyReplace(s3, s2);
            confirmdialog.setPrompt(s4);
            confirmdialog.show();
            File file1 = new File(s2);
            if(file1.exists())
                file1.delete();
            file.renameTo(file1);
        }
        PageFolder pagefolder1 = (PageFolder)pagefolder.clone();
        folder = pagefolder1;
        exportFile = s;
        updateTimeStamp = flag;
        exportList = flag1;
        thread = new Thread(this, "HotListWriter");
        thread.setPriority(4);
        thread.start();
    }

    public void run()
    {
        try
        {
            if(exportList)
            {
                hl.writeHTMLFile(folder, exportFile, exportList);
            } else
            {
                hl.writeHTMLFile(folder, tmpFile, exportList);
                renameFile();
            }
        }
        finally
        {
            thread = null;
        }
    }

    synchronized void stopThread()
    {
        if(thread != null)
        {
            thread.stop();
            thread = null;
        }
    }

    synchronized void renameFile()
    {
        File file = new File(tmpFile);
        File file1 = new File(exportFile);
        file1.delete();
        file.renameTo(file1);
        HotList hotlist = HotList.getHotList();
        if(updateTimeStamp)
            hotlist.setFileTimeStamp(file1.lastModified());
    }

    HotList hl;
    PageFolder folder;
    String exportFile;
    String tmpFile;
    Thread thread;
    boolean updateTimeStamp;
    boolean exportList;
}
