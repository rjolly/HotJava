// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BASEFONT.java

package sunw.hotjava.tags;

import sunw.hotjava.doc.*;

public class BASEFONT extends EmptyTagItem
{

    public void init(Document document)
    {
        super.init(document);
        try
        {
            String s = getAttribute("size");
            if(s != null)
                size = Integer.parseInt(s) + 2;
        }
        catch(NumberFormatException _ex) { }
        if(size < 3)
        {
            size = 3;
            return;
        }
        if(size > 9)
            size = 9;
    }

    public void setIndex(int i, Document document)
    {
        int j = getIndex();
        super.setIndex(i, document);
        if(j == -1)
        {
            document.getBasefontTagRecord().addTag(i);
            return;
        } else
        {
            document.getBasefontTagRecord().changeTagIndex(j, i);
            return;
        }
    }

    public boolean modifiesStyleInPlace()
    {
        return true;
    }

    public DocStyle modifyStyleInPlace(DocStyle docstyle)
    {
        DocStyle docstyle1 = docstyle;
        if(docstyle.next == null)
        {
            docstyle1 = (DocStyle)docstyle.clone();
            docstyle = docstyle1;
        } else
        {
            DocStyle docstyle2;
            do
            {
                docstyle2 = docstyle;
                docstyle = docstyle.next;
            } while(docstyle.next != null);
            docstyle = (DocStyle)docstyle.clone();
            docstyle2.next = docstyle;
        }
        docstyle.font = docstyle.font.getIndex(docstyle.adjustFontSize(size));
        docstyle1.font = docstyle1.font.getIndex(docstyle1.adjustFontSize(size));
        return docstyle1;
    }

    public boolean measureItem(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        measurestate.style = ((TraversalState) (measurestate)).style.push(this);
        return super.measureItem(formatter, formatstate, measurement, measurestate);
    }

    public boolean format(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        formatstate.style = modifyStyleInPlace(((TraversalState) (formatstate)).style);
        return super.format(formatter, formatstate, formatstate1);
    }

    public int getRawSize()
    {
        return size;
    }

    public BASEFONT()
    {
        size = 5;
    }

    int size;
}
