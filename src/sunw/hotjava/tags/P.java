// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   P.java

package sunw.hotjava.tags;

import sunw.hotjava.doc.*;
import sunw.hotjava.forms.INPUT;

// Referenced classes of package sunw.hotjava.tags:
//            Align

public class P extends BlockTagItem
{

    public void init(Document document)
    {
        if(super.atts != null)
            fmt = Align.getFormat(super.atts, "align", 2);
    }

    protected boolean leaveSpace(Formatter formatter, FormatState formatstate)
    {
        Document document = formatter.getDocument();
        int i = getIndex() + 1;
        if(getOffset() == 2 && i < document.nitems)
        {
            DocItem docitem = document.items[i];
            if((docitem instanceof INPUT) && ((INPUT)docitem).isHidden(formatter))
            {
                ignoreTag = true;
                return false;
            }
        }
        return super.leaveSpace(formatter, formatstate);
    }

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(getIndex() == 0)
        {
            formatstate.format = fmt;
            ignoreTag = true;
            formatstate.pos += 4096;
            formatstate.style = ((TraversalState) (formatstate)).style.push(this);
            ((TraversalState) (formatstate)).style.format = fmt;
            return false;
        }
        int i = getIndex() - 1;
        Document document = formatter.getDocument();
        DocItem docitem = document.items[i];
        if(docitem.isBlock())
        {
            formatstate.style = ((TraversalState) (formatstate)).style.push(this);
            if(fmt == 2 || fmt == formatstate.format)
                ignoreTag = true;
            else
                formatstate.format = ((TraversalState) (formatstate)).style.format;
            formatstate.pos += 4096;
            return false;
        }
        if(super.formatStartTag(formatter, formatstate, formatstate1))
            return true;
        if(getOffset() == 1)
        {
            int j = getIndex() + getOffset() + 1;
            if(j < document.nitems)
            {
                DocItem docitem1 = document.items[j];
                TagItem tagitem = docitem1.getTag(document);
                if(tagitem != null)
                {
                    String s = tagitem.getName();
                    if(!"p".equals(s))
                        formatstate.above = Math.max(formatstate.above, super.style.getAbove(((TraversalState) (formatstate)).style));
                }
            }
        }
        return false;
    }

    public boolean formatEndTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state != 0)
            return true;
        formatstate.pos += 4096;
        formatstate.style = ((TraversalState) (formatstate)).style.next;
        int i = getIndex() + getOffset() + 1;
        Document document = formatter.getDocument();
        if(i < document.nitems)
        {
            DocItem docitem = document.items[i];
            TagItem tagitem = docitem.getTag(document);
            if(tagitem != null && tagitem.getName().equals("p"))
                return true;
            if(!omittedEnd)
            {
                formatstate.above = Math.max(formatstate.above, super.style.getBelow(((TraversalState) (formatstate)).style));
                formatstate.format = 0;
            }
        }
        return true;
    }

    public void modifyStyle(DocStyle docstyle)
    {
        super.modifyStyle(docstyle);
        if(fmt != 2)
            docstyle.format = fmt;
    }

    public P()
    {
        omittedEnd = false;
        fmt = 2;
        ignoreTag = false;
    }

    public boolean omittedEnd;
    private int fmt;
    private boolean ignoreTag;
}
