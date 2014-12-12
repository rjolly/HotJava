// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FormPanel.java

package sunw.hotjava.forms;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Hashtable;
import java.util.Properties;
import sunw.hotjava.bean.CookieJarInterface;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.*;

// Referenced classes of package sunw.hotjava.forms:
//            FORM, FormPanel, PostFilesURLConnector, PostURLConnector

class SubmitThread extends Thread
    implements DocView, DocumentListener
{

    SubmitThread(FormPanel formpanel, String s, boolean flag, Hashtable hashtable)
    {
        panel = formpanel;
        data = s;
        clone = flag;
        files = hashtable;
    }

    public void run()
    {
        FORM form = panel.getFormItem();
        String s = form.getMethod();
        String s1 = form.getAction();
        String s2 = form.getTarget();
        String s3 = form.getEncType();
        CookieJarInterface cookiejarinterface = HotJavaBrowserBean.getCookiesManager();
        if(s2 == null)
            s2 = "_self";
        Object obj = null;
        try
        {
            URL url1;
            if(s1 == null)
            {
                URL url2 = panel.doc.getBaseURL();
                String s6 = url2.getFile();
                int i = s6.indexOf('?');
                if(i >= 0)
                    s6 = s6.substring(0, i);
                url1 = new URL(url2.getProtocol(), url2.getHost(), url2.getPort(), s6);
            } else
            {
                if(s1.indexOf(':') != -1)
                {
                    String s4 = s1.substring(0, s1.indexOf(':'));
                    if(s4.equalsIgnoreCase("javascript"))
                    {
                        String s7 = s1.substring("javascript:".length());
                        Formatter.handleScript(panel.getFormatter().getTopFormatter(), s7, "form.action", 0, null);
                        return;
                    }
                }
                url1 = new URL(panel.doc.getBaseURL(), s1);
            }
            if(url1.getProtocol().equals("mailto"))
                s2 = "_mailto";
            URL url3 = panel.doc.getURL();
            Object obj1 = null;
            URL url;
            URLConnection urlconnection;
            if("post".equals(s))
            {
                if("multipart/form-data".equals(s3))
                    obj1 = new PostFilesURLConnector(url3, data, files);
                else
                    obj1 = new PostURLConnector(url3, data);
                url = url1;
                urlconnection = ((URLConnector) (obj1)).openConnection(url);
            } else
            {
                if(url1.getProtocol().equals("file"))
                    url = url1;
                else
                    url = new URL(url1 + "?" + data);
                urlconnection = url.openConnection();
                if(cookiejarinterface != null)
                    cookiejarinterface.applyRelevantCookies(url, urlconnection);
                if(url3 != null)
                    urlconnection.setRequestProperty("Referer", url3.toExternalForm());
            }
            URL url4 = url;
            boolean flag = false;
            int j = 0;
            do
            {
                Globals.tryToStopFollowRedirects(urlconnection);
                flag = false;
                if(urlconnection instanceof HttpURLConnection)
                {
                    HttpURLConnection httpurlconnection = (HttpURLConnection)urlconnection;
                    try
                    {
                        httpurlconnection.setRequestProperty("Referer", url3.toExternalForm());
                    }
                    catch(IllegalAccessError _ex) { }
                    int k = 200;
                    try
                    {
                        k = httpurlconnection.getResponseCode();
                    }
                    catch(IOException _ex)
                    {
                        if(httpurlconnection.getResponseCode() == 401)
                            try
                            {
                                processAccessDenied(url, s2);
                                return;
                            }
                            catch(Exception _ex2)
                            {
                                return;
                            }
                    }
                    if(k >= 300 && k <= 305 && k != 304)
                    {
                        String s8 = httpurlconnection.getHeaderField("Location");
                        if(s8 != null && j < 5)
                        {
                            flag = true;
                            url4 = new URL(httpurlconnection.getURL(), s8);
                            urlconnection = url4.openConnection();
                            if(cookiejarinterface != null)
                                cookiejarinterface.applyRelevantCookies(url4, urlconnection);
                            j++;
                        }
                    }
                }
            } while(flag);
            if(j > 0)
                url = url4;
            InputStream inputstream = urlconnection.getInputStream();
            inputstream.available();
            if(inputstream != null)
            {
                try
                {
                    HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
                    String s9 = hjbproperties.getPropertyReplace("form.title", s);
                    Document document2 = new Document(url, s9);
                    document2.addView(this);
                    document2.setConnector(((URLConnector) (obj1)));
                    if(clone)
                    {
                        panel.getContainingHotJavaBrowserBean().internalGoto(panel, document2, clone);
                    } else
                    {
                        String s10 = panel.getContainingDocumentPanelName();
                        panel.getContainingHotJavaBrowserBean().internalGoto(s10, s2, document2, clone);
                    }
                    new DocParser(document2, urlconnection);
                    return;
                }
                catch(DocException docexception4)
                {
                    docexception4.printStackTrace();
                }
                return;
            }
        }
        catch(MalformedURLException _ex)
        {
            panel.setSubmitsAllowed(true);
            Document document = new Document();
            try
            {
                document.setProperty("url", new URL("doc:/lib/templates/unknown.url.html"));
            }
            catch(DocException docexception)
            {
                docexception.printStackTrace();
                return;
            }
            catch(MalformedURLException malformedurlexception)
            {
                malformedurlexception.printStackTrace();
                return;
            }
            panel.getContainingHotJavaBrowserBean().internalGoto(panel, s2, document, false);
            Properties properties = new Properties();
            if(s1 != null)
                properties.put("badurl", s1);
            try
            {
                new DocParser(document, properties);
                return;
            }
            catch(DocException docexception2)
            {
                docexception2.printStackTrace();
            }
            return;
        }
        catch(IOException _ex)
        {
            panel.setSubmitsAllowed(true);
            Document document1 = new Document();
            try
            {
                document1.setProperty("url", new URL(panel.doc.getBaseURL(), s1));
            }
            catch(DocException docexception1)
            {
                docexception1.printStackTrace();
                return;
            }
            catch(MalformedURLException malformedurlexception1)
            {
                malformedurlexception1.printStackTrace();
                return;
            }
            String s5 = panel.getContainingDocumentPanelName();
            panel.getContainingHotJavaBrowserBean().internalGoto(s5, s2, document1, false);
            try
            {
                new DocParser(document1, new Properties());
                return;
            }
            catch(DocException docexception3)
            {
                docexception3.printStackTrace();
            }
            return;
        }
    }

    private void processAccessDenied(URL url, String s)
    {
        Document document = new Document();
        try
        {
            document.setProperty("url", new URL("doc:/lib/templates/open.forbidden.html"));
        }
        catch(DocException docexception)
        {
            docexception.printStackTrace();
            return;
        }
        catch(MalformedURLException malformedurlexception)
        {
            malformedurlexception.printStackTrace();
            return;
        }
        panel.getContainingHotJavaBrowserBean().internalGoto(panel, s, document, false);
        Properties properties = new Properties();
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        properties.put("url", url.toExternalForm());
        String s1 = "http.forbidden";
        String s2 = hjbproperties.getProperty(s1);
        properties.put("message", s2);
        try
        {
            new DocParser(document, properties);
            return;
        }
        catch(DocException docexception1)
        {
            docexception1.printStackTrace();
        }
    }

    public void notify(Document document, int i, int j, int k)
    {
        if(i == 22)
        {
            document.removeView(this);
            DocView docview = document.getView();
            if(docview instanceof DocumentFormatter)
            {
                DocumentFormatter documentformatter = (DocumentFormatter)docview;
                documentformatter.addDocumentListener(this);
                return;
            }
            panel.setSubmitsAllowed(true);
        }
    }

    public void documentChanged(DocumentEvent documentevent)
    {
        if(documentevent.getID() == 1038)
        {
            panel.setSubmitsAllowed(true);
            DocumentFormatter documentformatter = (DocumentFormatter)documentevent.getSource();
            documentformatter.removeDocumentListener(this);
        }
    }

    FormPanel panel;
    String data;
    boolean clone;
    Hashtable files;
}
