// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentFormatterRef.java

package sunw.hotjava.doc;

import java.awt.Container;
import java.net.URL;

// Referenced classes of package sunw.hotjava.doc:
//            Document, DocumentFormatter, Formatter, PreservedState, 
//            DocFont

public class DocumentFormatterRef
{

    public DocumentFormatterRef(Container container, Document document, DocFont docfont)
    {
        offsetStored = false;
        formatter = new DocumentFormatter(container, document, docfont);
    }

    public DocumentFormatter getFormatter()
    {
        if(formatter == null)
            reconstitute();
        else
            formatter.setRepaintOnWake(true);
        return formatter;
    }

    public void updateDocument()
    {
        if(formatter == null)
            return;
        if(formatter.getDocument().isExpired())
        {
            flush();
            reconstitute();
            return;
        } else
        {
            formatter.updateDocument();
            return;
        }
    }

    DocumentFormatter getFormatterIfNotRemoved()
    {
        return formatter;
    }

    public Document getDocument()
    {
        DocumentFormatter documentformatter = getFormatter();
        return documentformatter.getDocument();
    }

    public URL getURL()
    {
        URL url;
        if(formatter != null)
            url = formatter.getDocument().getURL();
        else
            url = preservedState.getURL();
        return url;
    }

    boolean isDocument(Document document)
    {
        return formatter != null && formatter.getDocument() == document;
    }

    public void setDocFont(DocFont docfont)
    {
        if(formatter != null)
        {
            formatter.setDocFont(docfont);
            return;
        } else
        {
            preservedState.setDocFont(docfont);
            return;
        }
    }

    public void addClient()
    {
        refCount++;
    }

    public void removeClient()
    {
        if(refCount > 0)
        {
            refCount--;
            if(refCount == 0)
                flush();
        }
    }

    public void removeAllClients()
    {
        if(refCount > 0)
        {
            refCount = 0;
            flush();
        }
    }

    private void flush()
    {
        preservedState = new PreservedState(formatter);
        formatter.stop();
        formatter.destroy();
        formatter = null;
    }

    private void reconstitute()
    {
        formatter = preservedState.reconstituteFormatter();
        preservedState = null;
    }

    private DocumentFormatter formatter;
    private PreservedState preservedState;
    private int refCount;
    public boolean offsetStored;
}
