// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EndTagItem.java

package sunw.hotjava.doc;

import java.awt.Graphics;

// Referenced classes of package sunw.hotjava.doc:
//            DocItem, Document, Formatter, TagItem, 
//            FormatState, DocLine, Measurement, MeasureState

public final class EndTagItem extends DocItem
{

    public TagItem getTag(Document document)
    {
        return (TagItem)document.items[super.index + super.offset];
    }

    public boolean isEnd()
    {
        return true;
    }

    public boolean format(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        return getTag(formatter.getDocument()).formatEndTag(formatter, formatstate, formatstate1);
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        return getTag(formatter.getDocument()).paintEndTag(formatter, g, i, j, docline);
    }

    public boolean measureEndTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        return getTag(formatter.getDocument()).measureEndTag(formatter, formatstate, measurement, measurestate);
    }

    public EndTagItem()
    {
    }
}
