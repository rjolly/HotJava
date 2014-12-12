// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentFormatterRef.java

package sunw.hotjava.doc;

import java.awt.Container;
import java.net.URL;
import sunw.hotjava.misc.URLConnector;

// Referenced classes of package sunw.hotjava.doc:
//            Document, DocumentCache, DocumentFormatter, DocumentFormatterRef, 
//            Formatter, DocFont

class PreservedState
{

    PreservedState(DocumentFormatter documentformatter)
    {
        parent = documentformatter.parent;
        url = documentformatter.getDocument().getURL();
        if(url == null)
            document = documentformatter.getDocument();
        connector = documentformatter.getDocument().getConnector();
        referer = documentformatter.getDocument().getReferer();
        font = documentformatter.getDocFont();
    }

    DocumentFormatter reconstituteFormatter()
    {
        Document document1 = document;
        if(document1 == null)
            document1 = DocumentCache.getDocument(url, referer);
        DocumentFormatter documentformatter = new DocumentFormatter(parent, document1, font);
        return documentformatter;
    }

    URL getURL()
    {
        return url;
    }

    void setDocFont(DocFont docfont)
    {
        font = docfont;
    }

    private URL url;
    private URL referer;
    private URLConnector connector;
    private Container parent;
    private DocFont font;
    private Document document;
}
