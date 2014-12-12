// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentEventSource.java

package sunw.hotjava.doc;


// Referenced classes of package sunw.hotjava.doc:
//            DocumentListener

public interface DocumentEventSource
{

    public abstract void addDocumentListener(DocumentListener documentlistener);

    public abstract void removeDocumentListener(DocumentListener documentlistener);
}
