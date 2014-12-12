// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CAPTION.java

package sunw.hotjava.tables;

import sunw.hotjava.doc.*;

public class CAPTION extends TagItem
{

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        formatstate.pos += 4096;
        return false;
    }

    public boolean formatEndTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        formatstate.pos += 4096;
        return false;
    }

    public boolean measureStartTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        measurestate.style = ((TraversalState) (measurestate)).style.push(this);
        measurestate.pos += 4096;
        return false;
    }

    public boolean measureEndTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        measurestate.style = ((TraversalState) (measurestate)).style.next;
        measurestate.pos += 4096;
        return false;
    }

    public void modifyStyle(DocStyle docstyle)
    {
        super.style.apply(docstyle);
        docstyle.left = 0;
        docstyle.right = 0;
        docstyle.ascent = 0;
        docstyle.descent = 0;
        docstyle.nobreak = 0;
        docstyle.underline = false;
        docstyle.script = 0;
        docstyle.href = null;
        docstyle.format = 3;
    }

    public CAPTION()
    {
    }
}
