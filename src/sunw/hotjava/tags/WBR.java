// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   WBR.java

package sunw.hotjava.tags;

import sunw.hotjava.doc.*;

public class WBR extends EmptyTagItem
{

    public boolean format(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state != 0)
        {
            if(((TraversalState) (formatstate)).style.nobreak == 1)
                ((TraversalState) (formatstate)).style.nobreak = 2;
            formatter.setBreak(formatstate, formatstate1, 0, formatstate.width);
        }
        formatstate.pos += 4096;
        return false;
    }

    public boolean measureItem(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        if(((TraversalState) (measurestate)).style.nobreak == 1)
            ((TraversalState) (measurestate)).style.nobreak = 2;
        return super.measureItem(formatter, formatstate, measurement, measurestate);
    }

    public WBR()
    {
    }
}
