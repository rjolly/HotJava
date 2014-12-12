// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotListReader.java

package sunw.hotjava.ui;

import java.io.File;

// Referenced classes of package sunw.hotjava.ui:
//            HotList

public class HotListReader
    implements Runnable
{

    public HotListReader(HotList hotlist)
    {
        updateTimeStamp = false;
        hl = hotlist;
    }

    public void importFromHTML(String s, boolean flag, String s1)
    {
        stopThread();
        importFile = s;
        updateTimeStamp = flag;
        importFile2 = s1;
        thread = new Thread(this, "HotListReader");
        thread.setPriority(2);
        thread.start();
        if(!isFinishedImporting())
            raiseImportPriority();
    }

    public void importFromHTML(String s, boolean flag)
    {
        stopThread();
        importFile = s;
        updateTimeStamp = flag;
        thread = new Thread(this, "HotListReader");
        thread.setPriority(2);
        thread.start();
        if(!isFinishedImporting())
            raiseImportPriority();
    }

    public void run()
    {
        try
        {
            if(importFile != null)
            {
                hl.readHTMLFile(importFile);
                File file = new File(importFile);
                if(updateTimeStamp)
                    hl.setFileTimeStamp(file.lastModified());
            }
            if(importFile2 != null)
                hl.readHTMLFile(importFile2);
        }
        finally
        {
            thread = null;
        }
    }

    public boolean isFinishedImporting()
    {
        return thread == null;
    }

    synchronized void raiseImportPriority()
    {
        if(thread != null)
            thread.setPriority(9);
    }

    synchronized void stopThread()
    {
        if(thread != null)
        {
            thread.stop();
            thread = null;
        }
    }

    HotList hl;
    String importFile;
    boolean updateTimeStamp;
    String importFile2;
    Thread thread;
}
