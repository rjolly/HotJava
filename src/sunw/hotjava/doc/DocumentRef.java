// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentRef.java

package sunw.hotjava.doc;

import java.net.URL;
import sun.misc.Ref;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.misc.URLConnector;

// Referenced classes of package sunw.hotjava.doc:
//            DocException, DocParser, Document

class DocumentRef extends Ref
{

    DocumentRef(URL url1, URL url2, HotJavaBrowserBean hotjavabrowserbean)
    {
        reload = false;
        url = url1;
        referer = url2;
        hjbean = hotjavabrowserbean;
    }

    DocumentRef(URL url1, Document document)
    {
        reload = false;
        url = url1;
        referer = document.getReferer();
        connector = document.getConnector();
        setThing(document);
    }

    public Object get(boolean flag)
    {
        reload = flag;
        return get();
    }

    public Object reconstitute()
    {
        Document document = new Document(url, referer);
        document.setConnector(connector);
        try
        {
            new DocParser(document, !reload, hjbean);
        }
        catch(DocException _ex) { }
        return document;
    }

    URL url;
    URL referer;
    URLConnector connector;
    boolean reload;
    HotJavaBrowserBean hjbean;
}
