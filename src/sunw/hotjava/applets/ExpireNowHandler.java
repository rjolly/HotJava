// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TextPanel.java

package sunw.hotjava.applets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.bean.URLPooler;

// Referenced classes of package sunw.hotjava.applets:
//            IgnoreNonDigitsListener, TextPanel

class ExpireNowHandler
    implements ActionListener
{

    public void actionPerformed(ActionEvent actionevent)
    {
        URLPooler urlpooler = HJWindowManager.getHJWindowManager().getURLPoolManager();
        urlpooler.discardAllURLs();
    }

    ExpireNowHandler()
    {
    }
}
