// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HR.java

package sunw.hotjava.tags;

import java.awt.Color;
import java.awt.Graphics;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.Globals;

// Referenced classes of package sunw.hotjava.tags:
//            Align

public class HR extends EmptyTagItem
{

    public boolean isBlock()
    {
        return true;
    }

    public String getText()
    {
        return "\n";
    }

    public void init(Document document)
    {
        super.init(document);
        size = 2;
        shade = true;
        relwidth = 100;
        String s = getAttribute("size");
        if(s != null)
            try
            {
                size = Integer.valueOf(s).intValue();
            }
            catch(Exception _ex) { }
        s = getAttribute("width");
        if(s != null)
            if(s.endsWith("%"))
                try
                {
                    relwidth = Integer.valueOf(s.substring(0, s.length() - 1)).intValue();
                }
                catch(Exception _ex) { }
            else
                try
                {
                    abswidth = Integer.valueOf(s).intValue();
                }
                catch(Exception _ex) { }
        align = Align.getAlign(super.atts, "align", -1);
        shade = getAttribute("noshade") == null;
    }

    public int startOffset(int i)
    {
        return 0;
    }

    public int getWidth(DocStyle docstyle)
    {
        return formattedWidth;
    }

    public boolean format(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state == 0)
        {
            int i = formatstate.maxWidth;
            if(abswidth != 0)
                i = abswidth;
            else
            if(relwidth != 100)
                i = (i * relwidth) / 100;
            formattedWidth = i;
            switch(align)
            {
            case 8: // '\b'
                formatstate.format = 1;
                break;

            case 2: // '\002'
                formatstate.format = 3;
                break;

            case 7: // '\007'
                formatstate.format = 0;
                break;

            default:
                if(formatstate.format == 2)
                    formatstate.format = 3;
                break;
            }
            formatstate.width = i;
            formatstate.ascent = 10 + size;
            formatstate.descent = 10;
            formatstate.pos += 4096;
        }
        return true;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        int k = docline.width;
        int l = size;
        if(shade)
            l = size - 1;
        j += docline.baseline - l;
        if(shade)
        {
            Color color = formatter.getFormatterBackgroundColor();
            g.setColor(Globals.getVisible3DColor(color));
            g.draw3DRect(i, j, k, l, size == 1);
        } else
        {
            g.setColor(Color.black);
            g.fillRect(i, j, k, l);
        }
        formatter.displayPos += 4096;
        return 1000;
    }

    public boolean measureItem(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        int i = formatstate.maxWidth;
        if(abswidth != 0)
            i = abswidth;
        else
        if(relwidth != 100)
            i = (i * relwidth) / 100;
        measurement.setMinWidth(1);
        measurement.setPreferredWidth(i);
        measurestate.pos += 4096;
        return true;
    }

    public HR()
    {
    }

    int size;
    int abswidth;
    int relwidth;
    int formattedWidth;
    int align;
    boolean shade;
}
