// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SPACER.java

package sunw.hotjava.tags;

import java.awt.Graphics;
import sunw.hotjava.doc.*;

// Referenced classes of package sunw.hotjava.tags:
//            Align

public class SPACER extends EmptyTagItem
    implements Floatable
{

    public SPACER()
    {
    }

    public void init(Document document)
    {
        String s = getAttribute("type");
        if(s == null)
            s = "horizontal";
        if(s.equals("vertical"))
            type = 1;
        else
        if(s.equals("block"))
            type = 2;
        else
            type = 0;
        align = Align.getAlign(super.atts);
        if(type == 2)
        {
            height = TagItem.parseInt(super.atts, "height", 0, 0);
            width = TagItem.parseInt(super.atts, "width", 0, 0);
            return;
        } else
        {
            size = TagItem.parseInt(super.atts, "size", 0, 0);
            return;
        }
    }

    public int getAscent(Formatter formatter, FormatState formatstate)
    {
        switch(type)
        {
        case 0: // '\0'
            return 0;

        case 1: // '\001'
            return Align.getAscent(formatstate, align, size);

        case 2: // '\002'
            return Align.getAscent(formatstate, align, height);
        }
        return 0;
    }

    public int getDescent(Formatter formatter, FormatState formatstate)
    {
        switch(type)
        {
        case 0: // '\0'
            return 0;

        case 1: // '\001'
            return Align.getDescent(formatstate, align, size);

        case 2: // '\002'
            return Align.getDescent(formatstate, align, height);
        }
        return 0;
    }

    public int getWidth(DocStyle docstyle)
    {
        switch(type)
        {
        case 0: // '\0'
            return size;

        case 1: // '\001'
            return 0;

        case 2: // '\002'
            return width;
        }
        return 0;
    }

    public boolean format(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        switch(type)
        {
        case 0: // '\0'
            return formatHorizontal(formatter, formatstate, formatstate1);

        case 1: // '\001'
            return formatVertical(formatter, formatstate, formatstate1);

        case 2: // '\002'
            return formatBlock(formatter, formatstate, formatstate1);
        }
        return false;
    }

    private boolean formatHorizontal(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        return super.format(formatter, formatstate, formatstate1);
    }

    private boolean formatVertical(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state != 0)
        {
            formatstate.below = Math.max(formatstate.below, size);
            return true;
        } else
        {
            formatstate.state = 1;
            formatstate.pos += getIncrement();
            return false;
        }
    }

    private boolean formatBlock(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(alignIsFloating())
        {
            boolean flag = false;
            if(!formatter.isFloater(this) && formatstate.width + getWidth(formatter, ((TraversalState) (formatstate)).style) > formatstate.maxWidth)
            {
                if(formatstate.startPos != ((TraversalState) (formatstate)).pos)
                {
                    formatstate.below += formatter.getCumulativeFloaterHeight(formatstate.y);
                    return true;
                }
                flag = true;
            }
            formatter.queueFloater(formatter, formatstate, this, getAscent(formatter, formatstate) + getDescent(formatter, formatstate), align == 7);
            formatstate.pos += getIncrement();
            return flag;
        } else
        {
            return super.format(formatter, formatstate, formatstate1);
        }
    }

    public boolean measureItem(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        int i = getWidth(formatter, ((TraversalState) (measurestate)).style);
        if(type == 2 && alignIsFloating())
        {
            measurement.setFloaterMinWidth(i);
            measurement.setFloaterPreferredWidth(i);
        } else
        {
            measurement.setMinWidth(i);
            measurement.setPreferredWidth(i);
        }
        measurestate.pos += getIncrement();
        return false;
    }

    private boolean alignIsFloating()
    {
        return align == 7 || align == 8;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        formatter.displayPos += getIncrement();
        if(alignIsFloating())
            return 0;
        else
            return getWidth(formatter.displayStyle);
    }

    public int paint(Formatter formatter, Graphics g, int i, int j)
    {
        formatter.displayPos += getIncrement();
        return getWidth(formatter.displayStyle);
    }

    public int print(Formatter formatter, Graphics g, int i, int j, VBreakInfo vbreakinfo)
    {
        return paint(formatter, g, i, j);
    }

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private static final int BLOCK = 2;
    private int type;
    private int align;
    private int height;
    private int width;
    private int size;
}
