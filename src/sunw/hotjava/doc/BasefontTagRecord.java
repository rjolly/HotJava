// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BasefontTagRecord.java

package sunw.hotjava.doc;

import sunw.hotjava.tags.BASEFONT;

// Referenced classes of package sunw.hotjava.doc:
//            DocStyle, Document

public final class BasefontTagRecord
{

    public BasefontTagRecord(Document document)
    {
        doc = document;
        indices = null;
        numIndices = 0;
    }

    public int basefontSizeAt(DocStyle docstyle, int i)
    {
        synchronized(doc)
        {
            synchronized(this)
            {
                int l = maxLessThanOrEqual(i);
                if(l < 0)
                {
                    int j = docstyle.adjustFontSize(5);
                    return j;
                }
                int i1 = ((BASEFONT)doc.getItem(indices[l])).getRawSize();
                int k = docstyle.adjustFontSize(i1);
                return k;
            }
        }
    }

    public void changeTagIndex(int i, int j)
    {
        removeTag(i);
        addTag(j);
    }

    public synchronized void addTag(int i)
    {
        if(indices == null)
            indices = new int[1];
        else
        if(numIndices == indices.length)
        {
            int j = indices.length * 2;
            int ai[] = indices;
            indices = new int[j];
            for(int i1 = 0; i1 < numIndices; i1++)
                indices[i1] = ai[i1];

        }
        int k = maxLessThanOrEqual(i);
        k++;
        for(int l = numIndices - 1; l >= k; l--)
            indices[l + 1] = indices[l];

        indices[k] = i;
        numIndices++;
    }

    private void removeTag(int i)
    {
        int j = maxLessThanOrEqual(i);
        for(int k = j; k < numIndices - 1; k++)
            indices[k] = indices[k + 1];

        numIndices--;
    }

    synchronized void notifyDocItemsDeleted(int i, int j)
    {
        int k = (i + j) - 1;
        int l = 0;
        for(int i1 = 0; i1 < numIndices; i1++)
            if(indices[i1] < i)
                indices[l++] = indices[i1];
            else
            if(indices[i1] > k)
                indices[l++] = indices[i1] - j;

        numIndices = l;
        if(numIndices == 0)
            indices = null;
    }

    int maxLessThanOrEqual(int i)
    {
        int j = 0;
        int k;
        for(k = numIndices - 1; j < k;)
        {
            int l = (k + j + 1) / 2;
            if(indices[l] <= i)
                j = l;
            else
                k = l - 1;
        }

        if(j > k || indices[k] > i)
            return -1;
        else
            return k;
    }

    private int indices[];
    public int numIndices;
    private Document doc;
}
