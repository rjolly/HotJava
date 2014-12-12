// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentEventMulticaster.java

package sunw.hotjava.doc;


// Referenced classes of package sunw.hotjava.doc:
//            DocumentListener, DocumentEvent

public class DocumentEventMulticaster
    implements DocumentListener
{
    private static class Node
        implements DocumentListener
    {

        public void documentChanged(DocumentEvent documentevent)
        {
            a.documentChanged(documentevent);
            b.documentChanged(documentevent);
        }

        static DocumentListener add(DocumentListener documentlistener, DocumentListener documentlistener1)
        {
            return addInternal(documentlistener, documentlistener1);
        }

        static DocumentListener remove(DocumentListener documentlistener, DocumentListener documentlistener1)
        {
            return removeInternal(documentlistener, documentlistener1);
        }

        static DocumentListener addInternal(DocumentListener documentlistener, DocumentListener documentlistener1)
        {
            if(documentlistener == null)
                return documentlistener1;
            if(documentlistener1 == null)
                return documentlistener;
            else
                return new Node(documentlistener, documentlistener1);
        }

        DocumentListener remove(DocumentListener documentlistener)
        {
            if(documentlistener == a)
                return b;
            if(documentlistener == b)
                return a;
            DocumentListener documentlistener1 = removeInternal(a, documentlistener);
            DocumentListener documentlistener2 = removeInternal(b, documentlistener);
            if(documentlistener1 == a && documentlistener2 == b)
                return this;
            else
                return addInternal(documentlistener1, documentlistener2);
        }

        static DocumentListener removeInternal(DocumentListener documentlistener, DocumentListener documentlistener1)
        {
            if(documentlistener == documentlistener1 || documentlistener == null)
                return null;
            if(documentlistener instanceof Node)
                return ((Node)documentlistener).remove(documentlistener1);
            else
                return documentlistener;
        }

        private DocumentListener a;
        private DocumentListener b;

        private Node(DocumentListener documentlistener, DocumentListener documentlistener1)
        {
            a = documentlistener;
            b = documentlistener1;
        }
    }


    public DocumentEventMulticaster()
    {
    }

    public void documentChanged(DocumentEvent documentevent)
    {
        DocumentListener documentlistener = listeners;
        if(documentlistener != null)
            documentlistener.documentChanged(documentevent);
    }

    public synchronized void add(DocumentListener documentlistener)
    {
        listeners = Node.add(listeners, documentlistener);
    }

    public synchronized void remove(DocumentListener documentlistener)
    {
        listeners = Node.remove(listeners, documentlistener);
    }

    private DocumentListener listeners;
}
