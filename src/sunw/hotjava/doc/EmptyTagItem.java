// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EmptyTagItem.java

package sunw.hotjava.doc;

import java.awt.Graphics;

// Referenced classes of package sunw.hotjava.doc:
//            TagItem, DocConstants, DocStyle, FormatState, 
//            Formatter, Measurement, TraversalState, DocLine, 
//            MeasureState

public class EmptyTagItem extends TagItem
{

    public int getAscent(DocStyle docstyle)
    {
        return 0;
    }

    public int getDescent(DocStyle docstyle)
    {
        return 0;
    }

    public int getDescent(Formatter formatter, FormatState formatstate)
    {
        return getDescent(((TraversalState) (formatstate)).style);
    }

    public int getAscent(Formatter formatter, FormatState formatstate)
    {
        return getAscent(((TraversalState) (formatstate)).style);
    }

    public int getWidth(Formatter formatter, DocStyle docstyle)
    {
        return getWidth(docstyle);
    }

    protected int getWidth(DocStyle docstyle)
    {
        return 0;
    }

    protected int getIncrement()
    {
        return 4096;
    }

    public boolean format(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state == 2)
        {
            return true;
        } else
        {
            formatter.setBreak(formatstate, formatstate1, 0, formatstate.width);
            formatstate.state = 1;
            formatstate.width += getWidth(formatter, ((TraversalState) (formatstate)).style);
            formatstate.ascent = Math.max(formatstate.ascent, Math.max(((TraversalState) (formatstate)).style.ascent, getAscent(formatter, formatstate)));
            formatstate.descent = Math.max(formatstate.descent, Math.max(((TraversalState) (formatstate)).style.descent, getDescent(formatter, formatstate)));
            formatstate.pos += getIncrement();
            return false;
        }
    }

    public int findX(DocLine docline, DocStyle docstyle, int i, int j, int k, Formatter formatter)
    {
        int l = getWidth(formatter, docstyle);
        k -= i;
        if(k < l >> 1)
            return j;
        if(k <= l)
            return j + 1;
        else
            return -l - 1;
    }

    public int getWidth(DocLine docline, DocStyle docstyle, int i, int j, int k)
    {
        if(j < k)
            return getWidth(docstyle);
        else
            return 0;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        formatter.displayPos += getIncrement();
        return 0;
    }

    public boolean measureItem(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        measurement.setMinWidth(getWidth(formatter, ((TraversalState) (measurestate)).style));
        measurement.setPreferredWidth(getWidth(formatter, ((TraversalState) (measurestate)).style));
        measurestate.pos += getIncrement();
        return false;
    }

    public EmptyTagItem()
    {
    }
}
