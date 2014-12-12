// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FlowTagItem.java

package sunw.hotjava.doc;

import java.awt.Graphics;
import sunw.html.Element;

// Referenced classes of package sunw.hotjava.doc:
//            TagItem, DocConstants, DocStyle, FormatState, 
//            Formatter, Measurement, StyleSheet, TraversalState, 
//            DocLine, MeasureState

public class FlowTagItem extends TagItem
{

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state == 2)
        {
            return true;
        } else
        {
            formatter.setBreak(formatstate, formatstate1, 0, formatstate.width);
            isItBlink = super.style.elem.getName().equalsIgnoreCase("blink");
            formatstate.style = ((TraversalState) (formatstate)).style.push(this);
            formatstate.state = 1;
            formatstate.pos += 4096;
            return false;
        }
    }

    public boolean formatEndTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state == 2)
        {
            return true;
        } else
        {
            formatstate.state = 1;
            formatstate.style = ((TraversalState) (formatstate)).style.next;
            formatstate.pos += 4096;
            return false;
        }
    }

    public void modifyStyle(DocStyle docstyle)
    {
        super.modifyStyle(docstyle);
        if(isItBlink)
            docstyle.blinking = true;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        formatter.displayPos += 4096;
        return 0;
    }

    public int findStartTagX(DocLine docline, DocStyle docstyle, int i, int j, int k, Formatter formatter)
    {
        return -1;
    }

    public int findEndTagX(DocLine docline, DocStyle docstyle, int i, int j, int k, Formatter formatter)
    {
        return findStartTagX(docline, docstyle, i, j, k, formatter);
    }

    public int getStartTagWidth(DocLine docline, DocStyle docstyle, int i, int j, int k)
    {
        return 0;
    }

    public int getEndTagWidth(DocLine docline, DocStyle docstyle, int i, int j, int k)
    {
        return getStartTagWidth(docline, docstyle, i, j, k);
    }

    public boolean measureStartTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        int i = 0;
        if(formatstate.state == 2)
        {
            return true;
        } else
        {
            measurestate.style = ((TraversalState) (measurestate)).style.push(this);
            formatstate.state = 1;
            measurement.setMinWidth(i);
            measurement.setPreferredWidth(i);
            measurestate.pos += 4096;
            return false;
        }
    }

    public boolean measureEndTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        int i = 0;
        if(formatstate.state == 2)
        {
            return true;
        } else
        {
            formatstate.state = 1;
            measurement.setMinWidth(i);
            measurement.setPreferredWidth(i);
            measurestate.style = ((TraversalState) (measurestate)).style.next;
            measurestate.pos += 4096;
            return false;
        }
    }

    public FlowTagItem()
    {
        isItBlink = false;
    }

    private boolean isItBlink;
}
