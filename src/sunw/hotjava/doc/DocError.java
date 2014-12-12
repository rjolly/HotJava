// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocError.java

package sunw.hotjava.doc;


// Referenced classes of package sunw.hotjava.doc:
//            Document

public class DocError extends Error
{

    public DocError(String s)
    {
        super(s);
    }

    public DocError(Document document, String s)
    {
        super(s);
        doc = document;
    }

    public Document doc;
}
