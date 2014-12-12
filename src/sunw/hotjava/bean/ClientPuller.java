// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotJavaBrowserBean.java

package sunw.hotjava.bean;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import sunw.hotjava.doc.*;

// Referenced classes of package sunw.hotjava.bean:
//            HotJavaBrowserBean

class ClientPuller extends Thread
{

    ClientPuller(HotJavaBrowserBean hotjavabrowserbean, DocumentPanel documentpanel, URL url, String s, int i)
    {
        topView = hotjavabrowserbean;
        target = documentpanel;
        originalURL = url;
        if(s == null || s.length() == 0)
            refreshment = url.toExternalForm();
        else
            refreshment = s;
        delay = i;
        setName("Client-pull: " + s);
        setPriority(3);
    }

    boolean isCurrentDoc()
    {
        if(target != null && target.getDocument() != null && target.getDocument().getURL() != null)
            return target.getDocument().getURL().equals(originalURL);
        else
            return false;
    }

    void refreshDocument()
    {
        try
        {
            URL url = target.getDocument().getBaseURL();
            URL url1 = new URL(url, refreshment);
            if(DocumentCache.seenDocument(url1))
                DocumentCache.removeDocument(url1);
            topView.showDocument(target, url1, url);
            Document document = DocumentCache.getDocument(url);
            document.setExpirationDate(new Date());
            return;
        }
        catch(MalformedURLException malformedurlexception)
        {
            malformedurlexception.printStackTrace();
        }
    }

    public void run()
    {
        long l = (long)(delay * 1000) + System.currentTimeMillis();
        do
        {
            if(!isCurrentDoc())
                return;
            if(System.currentTimeMillis() >= l && isCurrentDoc())
            {
                refreshDocument();
                return;
            }
            try
            {
                Thread.sleep(2000L);
            }
            catch(InterruptedException _ex) { }
        } while(true);
    }

    HotJavaBrowserBean topView;
    DocumentPanel target;
    URL originalURL;
    String refreshment;
    int delay;
}
