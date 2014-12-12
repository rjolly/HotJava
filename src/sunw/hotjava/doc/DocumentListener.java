// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentListener.java

package sunw.hotjava.doc;

import java.util.EventListener;

// Referenced classes of package sunw.hotjava.doc:
//            DocumentEvent

public interface DocumentListener
    extends EventListener
{

    public abstract void documentChanged(DocumentEvent documentevent);
}
