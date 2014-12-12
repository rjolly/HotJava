// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BR.java

package sunw.hotjava.tags;

import java.awt.FontMetrics;
import sunw.hotjava.doc.*;

public class BR extends BreakTagItem
{

    public void init(Document document)
    {
        String s = getAttribute("clear");
        if(s != null)
        {
            if(s.equals("left"))
            {
                clearSide = 1;
                return;
            }
            if(s.equals("right"))
            {
                clearSide = 2;
                return;
            }
            if(s.equals("all") || s.equals(""))
                clearSide = 3;
        }
    }

    public boolean format(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state != 0)
        {
            if(super.format(formatter, formatstate, formatstate1))
                return true;
        } else
        {
            formatstate.pos += 4096;
        }
        int i = 0;
        switch(clearSide)
        {
        case 1: // '\001'
            i += formatter.getCumulativeFloaterHeight(formatstate.y + formatstate.above, true);
            break;

        case 2: // '\002'
            i = formatter.getCumulativeFloaterHeight(formatstate.y + formatstate.above, false);
            break;

        case 3: // '\003'
            i = formatter.getCumulativeFloaterHeight(formatstate.y + formatstate.above);
            break;
        }
        int j = getIndex() - 1;
        if(j >= 0)
        {
            Document document = formatter.getDocument();
            DocItem docitem = document.items[j];
            TagItem tagitem = docitem.getTag(document);
            for(; j > 0 && ((docitem instanceof FlowTagItem) || docitem.isEnd() || docitem.isText() && ((TextItem)docitem).getLength() == 0); docitem = document.items[--j])
            {
                if(!docitem.isEnd())
                    continue;
                int k = j + docitem.getOffset();
                if(k >= 0 && !(document.items[k] instanceof FlowTagItem))
                    break;
            }

            if(docitem != null)
                tagitem = docitem.getTag(document);
            if(tagitem != null && (tagitem.getName().equals("br") || tagitem.getName().equals("p")))
            {
                FontMetrics fontmetrics = formatter.getDocFont().getFontMetrics(formatter.displayStyle);
                int l = fontmetrics.getHeight() - formatstate.ascent - formatstate.descent;
                i = Math.max(i, l);
            }
        }
        formatstate.above += i;
        return true;
    }

    public BR()
    {
        clearSide = 0;
    }

    private static final int CLEAR_NONE = 0;
    private static final int CLEAR_LEFT = 1;
    private static final int CLEAR_RIGHT = 2;
    private static final int CLEAR_ALL = 3;
    private int clearSide;
}
