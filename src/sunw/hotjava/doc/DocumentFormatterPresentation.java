// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentFormatterPresentation.java

package sunw.hotjava.doc;

import java.awt.Point;
import java.awt.ScrollPane;
import java.net.URL;

// Referenced classes of package sunw.hotjava.doc:
//            DocumentFormatter, DocumentFormatterRef, DocumentPanel

class DocumentFormatterPresentation
{

    DocumentFormatterPresentation(DocumentFormatterRef documentformatterref)
    {
        scrollPosition = new Point(0, 0);
        formatterRef = documentformatterref;
        formatterRef.addClient();
    }

    Point getScrollPosition()
    {
        return new Point(scrollPosition.x, scrollPosition.y);
    }

    void setScrollPosition(int i, int j)
    {
        scrollPosition.x = i;
        scrollPosition.y = j;
    }

    void setScrollPosition(Point point)
    {
        scrollPosition.x = point.x;
        scrollPosition.y = point.y;
    }

    synchronized DocumentFormatterRef getFormatterRef()
    {
        if(isFlushed)
        {
            formatterRef.addClient();
            isFlushed = false;
        }
        return formatterRef;
    }

    public URL getURL()
    {
        return formatterRef.getURL();
    }

    boolean getIsFlushed()
    {
        return isFlushed;
    }

    synchronized void flush()
    {
        if(!isFlushed)
        {
            isFlushed = true;
            formatterRef.removeClient();
        }
    }

    public String toString()
    {
        return getClass().getName() + "[" + formatterRef + " (" + scrollPosition.x + "," + scrollPosition.y + ") flushed=" + isFlushed + "]";
    }

    public void showIn(DocumentPanel documentpanel)
    {
        documentpanel.show(getFormatterRef());
        documentpanel.setScrollPosition(scrollPosition);
        if(getFormatterRef() != null)
            getFormatterRef().getFormatter().scrollWhenDone(scrollPosition);
    }

    private Point scrollPosition;
    private DocumentFormatterRef formatterRef;
    boolean isFlushed;
}
