// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentCache.java

package sunw.hotjava.doc;

import java.io.PrintStream;
import java.net.URL;
import java.util.*;
import sun.misc.Ref;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.bean.URLPooler;
import sunw.hotjava.misc.*;

// Referenced classes of package sunw.hotjava.doc:
//            DocException, DocParser, Document, DocumentRef

public class DocumentCache
{

    public static boolean seenDocument(URL url)
    {
        return hash.get(url) != null;
    }

    public static synchronized void putDocument(Document document)
    {
        URL url = document.getURL();
        if(url != null)
        {
            String s = url.toExternalForm();
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            String s1 = "hotjava.documents.disablecache";
            boolean flag = hjbproperties.getBoolean(s1);
            if(s.indexOf('?') < 0 && s.indexOf("cgi-bin", 0) < 0 && !flag)
                hash.put(url, new DocumentRef(url, document));
            putURLPool(s);
        }
    }

    public static Document getDocument(URL url)
    {
        return getDocument(url, null, false, null);
    }

    public static Document getDocument(URL url, URL url1)
    {
        return getDocument(url, url1, false, null);
    }

    public static Document getDocument(URL url, URL url1, boolean flag)
    {
        return getDocument(url, url1, flag, null);
    }

    public static Document getDocument(URL url, URL url1, HotJavaBrowserBean hotjavabrowserbean)
    {
        return getDocument(url, url1, false, hotjavabrowserbean);
    }

    public static Document getDocument(URL url, URL url1, boolean flag, HotJavaBrowserBean hotjavabrowserbean)
    {
        Document document1;
        synchronized(Globals.getAwtLock())
        {
            synchronized(sunw.hotjava.doc.DocumentCache.class)
            {
                DocumentRef documentref = (DocumentRef)hash.get(url);
                if(documentref == null)
                {
                    documentref = new DocumentRef(url, url1, hotjavabrowserbean);
                    Document document = (Document)documentref.get(flag);
                    putDocument(document);
                    Document document2 = document;
                    return document2;
                }
                putURLPool(url.toExternalForm());
                document1 = (Document)documentref.get();
                if(document1.getPragma() != null && document1.getPragma().indexOf("no-cache") >= 0)
                {
                    documentref = new DocumentRef(url, url1, hotjavabrowserbean);
                    document1 = (Document)documentref.get();
                    putDocument(document1);
                    Document document3 = document1;
                    return document3;
                }
                try
                {
                    document1.setProperty("url", url);
                    document1.setProperty("referer", url1);
                }
                catch(DocException docexception)
                {
                    System.out.println("Could not update doc URL because of " + docexception);
                }
                if(document1 != null)
                {
                    Date date = document1.getExpirationDate();
                    if(date != null && date.getTime() <= System.currentTimeMillis())
                    {
                        documentref.flush();
                        Document document4 = (Document)documentref.get();
                        return document4;
                    }
                }
                if(document1.doneParsing())
                {
                    Document document5 = new Document(document1.getURL(), document1.getReferer());
                    document5.setConnector(document1.getConnector());
                    try
                    {
                        new DocParser(document5, document1.getSourceDocument(), hotjavabrowserbean);
                        document1 = document5;
                    }
                    catch(DocException _ex) { }
                }
            }
        }
        return document1;
    }

    private static void putURLPool(String s)
    {
        URLPooler urlpooler = HotJavaBrowserBean.getURLPoolManager();
        if(urlpooler != null)
            urlpooler.add(s);
    }

    public static Enumeration getDocuments()
    {
        return hash.elements();
    }

    public static void removeDocument(URL url)
    {
        hash.remove(url);
    }

    public DocumentCache()
    {
    }

    static final SelfCleaningHashtable hash = new SelfCleaningHashtable();

}
