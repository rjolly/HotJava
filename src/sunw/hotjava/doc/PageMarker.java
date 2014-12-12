// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PageMarker.java

package sunw.hotjava.doc;

import java.awt.Graphics;
import java.awt.PrintJob;

// Referenced classes of package sunw.hotjava.doc:
//            PageMargins, Document

public interface PageMarker
{

    public abstract void adjustMargins(PageMargins pagemargins);

    public abstract void markBefore(Document document, Graphics g, PrintJob printjob, int i, int j);

    public abstract void markAfter(Document document, Graphics g, PrintJob printjob, int i, int j);
}
