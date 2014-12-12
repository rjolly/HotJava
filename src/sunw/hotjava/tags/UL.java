// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UL.java

package sunw.hotjava.tags;

import java.awt.FontMetrics;
import java.awt.Graphics;
import sunw.hotjava.doc.*;

// Referenced classes of package sunw.hotjava.tags:
//            LI, OL

public class UL extends BlockTagItem
{

    public void init(Document document)
    {
        doc = document;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        String s = getAttribute("type");
        if(s != null)
        {
            bulletType = LI.bulletNameToType(s);
            return super.paint(formatter, g, i, j, docline);
        }
        UL ul = findEnclosingUL(formatter);
        if(ul != null)
        {
            bulletType = LI.nextBulletType(ul.bulletType);
            topLevel = false;
        }
        return super.paint(formatter, g, i, j, docline);
    }

    private UL findEnclosingUL(Formatter formatter)
    {
        DocStyle docstyle = formatter.displayStyle;
        for(docstyle = docstyle.next; docstyle != null; docstyle = docstyle.next)
            if(docstyle.tag instanceof UL)
                return (UL)docstyle.tag;

        return null;
    }

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if((((TraversalState) (formatstate)).style.tag instanceof UL) || (((TraversalState) (formatstate)).style.tag instanceof OL) || (((TraversalState) (formatstate)).style.tag instanceof LI))
        {
            int i = formatstate.above;
            int j = formatstate.ascent;
            if(!super.formatStartTag(formatter, formatstate, formatstate1))
            {
                formatstate.above = i;
                formatstate.ascent = j;
                return false;
            } else
            {
                return true;
            }
        } else
        {
            return super.formatStartTag(formatter, formatstate, formatstate1);
        }
    }

    public boolean formatEndTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(doc.length() >> 12 > getIndex() + getOffset() + 1)
        {
            DocItem docitem = doc.getItem(getIndex() + getOffset() + 1);
            if(docitem.getOffset() < 0)
                docitem = doc.getItem(docitem.getIndex() + docitem.getOffset());
            if(docitem.getOffset() != 0 && ((docitem instanceof LI) || (docitem instanceof UL) || (docitem instanceof OL)))
            {
                int i = formatstate.below;
                int j = formatstate.descent;
                if(!super.formatEndTag(formatter, formatstate, formatstate1))
                {
                    formatstate.below = i;
                    formatstate.descent = j;
                    return false;
                } else
                {
                    return true;
                }
            }
        }
        return super.formatEndTag(formatter, formatstate, formatstate1);
    }

    public void modifyStyle(DocStyle docstyle)
    {
        super.modifyStyle(docstyle);
        FontMetrics fontmetrics = docstyle.getFontMetrics();
        docstyle.ascent = fontmetrics.getAscent();
        docstyle.descent = fontmetrics.getDescent();
    }

    public UL()
    {
        bulletType = 0;
        topLevel = true;
    }

    int bulletType;
    boolean topLevel;
    private Document doc;
}
