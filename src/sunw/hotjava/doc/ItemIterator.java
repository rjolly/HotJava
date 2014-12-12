// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ItemIterator.java

package sunw.hotjava.doc;


// Referenced classes of package sunw.hotjava.doc:
//            DocConstants, DocItem, Document

public class ItemIterator
    implements DocConstants
{

    ItemIterator(int i, Document document)
    {
        pos = i;
        doc = document;
    }

    public void skipItem()
    {
        pos += 4096;
    }

    public void skipToEnd()
    {
        pos += doc.items[getIndex()].getOffset() << 12;
    }

    public int getPos()
    {
        return pos;
    }

    public int getOffset()
    {
        return pos & 0xfff;
    }

    public int getIndex()
    {
        return pos >> 12;
    }

    private Document doc;
    private int pos;
}
