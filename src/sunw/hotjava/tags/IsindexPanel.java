// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   IsindexPanel.java

package sunw.hotjava.tags;

import java.awt.*;
import java.net.*;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.doc.DocPanel;
import sunw.hotjava.doc.Document;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;

public class IsindexPanel extends Panel
    implements DocPanel
{

    IsindexPanel(Document document, String s, String s1)
    {
        doc = document;
        action = s1;
        setLayout(new FlowLayout(0, 2, 0));
        if(s == null)
        {
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            s = hjbproperties.getProperty("hotjava.search.msg");
        }
        add(new Label(s));
        add(keyWord = new TextField(40));
        hide();
    }

    IsindexPanel(IsindexPanel isindexpanel)
    {
        doc = isindexpanel.doc;
        action = isindexpanel.action;
        keyWord = isindexpanel.keyWord;
    }

    public boolean keyDown(Event event, int i)
    {
        if(event.target == keyWord && (i & 0xff) == 10)
        {
            String s = URLEncoder.encode(keyWord.getText());
            URL url = doc.getReferer();
            try
            {
                URL url1;
                if(action == null)
                    url1 = new URL(Globals.removeGETstring(doc.getURL()) + "?" + s);
                else
                    url1 = new URL(action + "?" + s);
                getContainingHotJavaBrowserBean().internalGoto(this, url1, url, event.shiftDown());
            }
            catch(MalformedURLException _ex) { }
            return true;
        } else
        {
            return false;
        }
    }

    public void start()
    {
    }

    public void stop()
    {
    }

    public void destroy()
    {
    }

    public void setObsolete(boolean flag)
    {
    }

    public void interruptLoading()
    {
    }

    public void notify(Document document, int i, int j, int k)
    {
    }

    public void activateSubItems()
    {
    }

    public void reformat()
    {
        validate();
    }

    public int findYFor(int i)
    {
        return 0;
    }

    protected HotJavaBrowserBean getContainingHotJavaBrowserBean()
    {
        return HotJavaBrowserBean.getContainingHotJavaBrowserBean(this);
    }

    private Document doc;
    private String action;
    private TextField keyWord;
}
