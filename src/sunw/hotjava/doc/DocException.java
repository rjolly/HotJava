// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocException.java

package sunw.hotjava.doc;


// Referenced classes of package sunw.hotjava.doc:
//            Document

public class DocException extends Exception
{

    public DocException(String s)
    {
        super(s);
    }

    public DocException(Document document, String s)
    {
        super(s);
        doc = document;
    }

    public Document doc;
}
