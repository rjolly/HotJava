// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ANIMATE.java

package sunw.hotjava.tags;

import java.awt.Graphics;
import sunw.hotjava.doc.*;

public class ANIMATE extends TagItem
{

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        formatstate.pos += getOffset() + 1 << 12;
        return false;
    }

    public boolean measureStartTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        measurestate.pos += getOffset() + 1 << 12;
        return false;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        int k = getOffset();
        if(k < 0)
            k = 1;
        formatter.displayPos += k << 12;
        return 0;
    }

    public ANIMATE()
    {
    }
}
