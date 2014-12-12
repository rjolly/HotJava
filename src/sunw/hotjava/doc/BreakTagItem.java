// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BreakTagItem.java

package sunw.hotjava.doc;

import java.awt.Graphics;

// Referenced classes of package sunw.hotjava.doc:
//            TagItem, DocConstants, FormatState, Formatter, 
//            StyleSheet, TraversalState, DocLine, DocStyle, 
//            Measurement, MeasureState

public class BreakTagItem extends TagItem
{

    public boolean isBlock()
    {
        return true;
    }

    public boolean format(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state != 0)
        {
            return true;
        } else
        {
            formatstate.above = Math.max(formatstate.above, super.style.getAbove(((TraversalState) (formatstate)).style));
            formatstate.pos += 4096;
            return false;
        }
    }

    public int findX(DocLine docline, DocStyle docstyle, int i, int j, int k, Formatter formatter)
    {
        if(k < i)
            return j;
        else
            return -1;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        formatter.displayPos += 4096;
        return 0;
    }

    public boolean measureItem(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        measurestate.pos += 4096;
        return true;
    }

    public BreakTagItem()
    {
    }
}
