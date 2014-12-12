// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BlockTagItem.java

package sunw.hotjava.doc;

import java.awt.Graphics;
import sunw.html.Attributes;
import sunw.html.Element;

// Referenced classes of package sunw.hotjava.doc:
//            TagItem, DocConstants, DocItem, DocLine, 
//            DocStyle, Document, FormatState, Formatter, 
//            MeasureState, Measurement, StyleSheet, TraversalState

public class BlockTagItem extends TagItem
{

    public boolean isBlock()
    {
        return true;
    }

    public String getText()
    {
        return "\n";
    }

    public boolean isPreformatted()
    {
        return isType("verbatim");
    }

    public int startOffset(Document document, int i)
    {
        if(isEnd())
            return -1;
        return super.index <= 0 || !document.items[super.index - 1].isBlock() || !document.items[super.index - 1].isStart() ? 0 : -1;
    }

    protected boolean leaveSpace(Formatter formatter, FormatState formatstate)
    {
        if(formatstate.prevLine == null)
            return false;
        if(formatstate.prevLine.getBelow() > super.style.getAbove(((TraversalState) (formatstate)).style))
            return false;
        if(formatstate.totalHeight == 0)
            return false;
        return super.offset != 1;
    }

    protected boolean beginState(Formatter formatter, FormatState formatstate)
    {
        return formatstate.state == 0;
    }

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(!beginState(formatter, formatstate))
            return true;
        dl = formatstate.prevLine;
        left = super.style.left;
        if(super.style.elem.getName().equals("dl") && (((TraversalState) (formatstate)).style.tag == null || !((TraversalState) (formatstate)).style.tag.getName().equals("dl")))
            left = 0;
        formatstate.style = ((TraversalState) (formatstate)).style.push(this);
        formatstate.maxWidth -= left + super.style.right;
        formatstate.margin += (left << 16) + super.style.right;
        if(leaveSpace(formatter, formatstate))
            formatstate.above = Math.max(formatstate.above, super.style.getAbove(((TraversalState) (formatstate)).style));
        formatstate.format = ((TraversalState) (formatstate)).style.format;
        formatstate.pos += 4096;
        return false;
    }

    public boolean formatEndTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        formatstate.state = 2;
        if(super.offset != 1 && (formatstate.prevLine != dl || formatstate.width != 0))
            formatstate.below = Math.max(formatstate.below, super.style.getBelow(((TraversalState) (formatstate)).style));
        formatstate.style = ((TraversalState) (formatstate)).style.next;
        formatstate.pos += 4096;
        return false;
    }

    public void modifyStyle(DocStyle docstyle)
    {
        super.style.apply(docstyle);
        docstyle.left += getName().equals("dl") ? left : super.style.left;
        docstyle.right += super.style.right;
        if(compacted)
            docstyle.compact = true;
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
        if(!beginState(formatter, formatstate))
        {
            return true;
        } else
        {
            measurestate.style = ((TraversalState) (measurestate)).style.push(this);
            measurestate.margin += (super.style.left << 16) + super.style.right;
            measurement.setMinWidth(i);
            measurement.setPreferredWidth(i);
            measurestate.pos += 4096;
            return false;
        }
    }

    public boolean measureEndTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        formatstate.state = 2;
        measurestate.style = ((TraversalState) (measurestate)).style.next;
        measurestate.pos += 4096;
        return true;
    }

    public void init(Document document)
    {
        if(super.atts != null)
            compacted = super.atts.get("compact") != null;
        super.init(document);
    }

    public BlockTagItem()
    {
        compacted = false;
    }

    private DocLine dl;
    boolean compacted;
    int left;
}
