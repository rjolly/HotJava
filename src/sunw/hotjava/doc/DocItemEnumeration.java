// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocItemEnumeration.java

package sunw.hotjava.doc;

import java.util.Enumeration;
import java.util.NoSuchElementException;

// Referenced classes of package sunw.hotjava.doc:
//            DocConstants, DocItem, Document

public class DocItemEnumeration
    implements Enumeration, DocConstants
{

    DocItemEnumeration(Document document, int i, int j)
    {
        doc = document;
        if(i < 0)
            i = 0;
        int k = document.nitems - 1 << 12;
        if(document.nitems > 0)
        {
            DocItem docitem = document.items[document.nitems - 1];
            if(docitem != null)
            {
                String s = docitem.getText();
                if(s != null)
                    k += s.length();
            }
        }
        if(j > k)
            j = k;
        if(j < i)
        {
            throw new IllegalArgumentException("Illegal range: " + i + " to " + j);
        } else
        {
            startItem = i >> 12;
            endItem = j >> 12;
            curItem = startItem;
            return;
        }
    }

    public boolean hasMoreElements()
    {
        return curItem <= endItem;
    }

    public Object nextElement()
    {
        if(curItem <= endItem)
        {
            DocItem docitem = doc.items[curItem];
            curItem++;
            return docitem;
        } else
        {
            throw new NoSuchElementException(getClass().getName());
        }
    }

    Document doc;
    int curItem;
    int startItem;
    int endItem;
}
