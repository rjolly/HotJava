// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BulletTagItem.java

package sunw.hotjava.doc;

import java.awt.FontMetrics;
import java.awt.Graphics;

// Referenced classes of package sunw.hotjava.doc:
//            BlockTagItem, DocConstants, DocFont, DocStyle, 
//            FormatState, Formatter, StyleSheet, TagItem, 
//            TraversalState, DocLine

public class BulletTagItem extends BlockTagItem
{

    public int startOffset(int i)
    {
        return 0;
    }

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(getName().equalsIgnoreCase("dd") && ((TraversalState) (formatstate)).style.compact && formatstate.state == 1)
        {
            FontMetrics fontmetrics = ((TraversalState) (formatstate)).style.font.getFontMetrics(((TraversalState) (formatstate)).style);
            dist = super.style.left - formatstate.width - fontmetrics.charWidth('0');
            if(dist >= 0)
            {
                formatstate.state = 1;
                formatstate.style = ((TraversalState) (formatstate)).style.push(this);
                formatstate.above = Math.max(formatstate.above, super.style.getAbove(((TraversalState) (formatstate)).style));
                formatstate.maxWidth -= super.style.right;
                formatstate.margin += super.style.right;
                formatstate.pos += 4096;
                dist = super.style.left - formatstate.width;
                formatstate.width = super.style.left;
                formatstate.ascent = ((TraversalState) (formatstate)).style.ascent;
                formatstate.descent = ((TraversalState) (formatstate)).style.descent;
                formatstate.format = ((TraversalState) (formatstate)).style.format;
                return false;
            }
        }
        if(formatstate.state != 0)
        {
            return true;
        } else
        {
            formatstate.state = 1;
            formatstate.style = ((TraversalState) (formatstate)).style.push(this);
            int i = super.style.left;
            int j = super.style.right;
            formatstate.maxWidth -= i + j;
            formatstate.margin += (i << 16) + j;
            formatstate.above = Math.max(formatstate.above, super.style.getAbove(((TraversalState) (formatstate)).style));
            formatstate.ascent = ((TraversalState) (formatstate)).style.ascent;
            formatstate.descent = ((TraversalState) (formatstate)).style.descent;
            formatstate.format = ((TraversalState) (formatstate)).style.format;
            formatstate.pos += 4096;
            return false;
        }
    }

    public boolean formatEndTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(!((TraversalState) (formatstate)).style.compact)
            formatstate.state = 2;
        else
        if(formatstate.state != 2)
            formatstate.state = 1;
        formatstate.below = Math.max(formatstate.below, super.style.getBelow(((TraversalState) (formatstate)).style));
        formatstate.style = ((TraversalState) (formatstate)).style.next;
        formatstate.pos += 4096;
        return false;
    }

    public int findStartTagX(DocLine docline, DocStyle docstyle, int i, int j, int k, Formatter formatter)
    {
        if((j & 0xfff) != 0)
        {
            int l = super.style.left;
            return -l - 1;
        } else
        {
            return -1;
        }
    }

    public int getStartTagWidth(DocLine docline, DocStyle docstyle, int i, int j, int k)
    {
        if(j != 0 && j < k)
            return super.style.left;
        else
            return 0;
    }

    public int getEndTagWidth(DocLine docline, DocStyle docstyle, int i, int j, int k)
    {
        return 0;
    }

    public int paintBullet(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        return super.style.left;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        int k = super.style.left;
        int l = 0;
        formatter.displayPos += 4096;
        if(dist >= 0)
            return dist;
        l = paintBullet(formatter, g, i - k, j, docline);
        if(l > k)
            return l - k;
        else
            return 0;
    }

    public BulletTagItem()
    {
        dist = -1;
    }

    int dist;
}
