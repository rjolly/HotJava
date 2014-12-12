// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TD.java

package sunw.hotjava.tables;

import sunw.hotjava.doc.*;
import sunw.hotjava.tags.Align;
import sunw.html.Attributes;

public class TD extends TagItem
{

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        formatstate.style = ((TraversalState) (formatstate)).style.push(this);
        formatstate.pos += 4096;
        return false;
    }

    public boolean formatEndTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        formatstate.style = ((TraversalState) (formatstate)).style.next;
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

    public boolean isBlock()
    {
        return true;
    }

    public void modifyStyle(DocStyle docstyle)
    {
        String s = null;
        super.style.apply(docstyle);
        docstyle.left = 0;
        docstyle.right = 0;
        docstyle.ascent = 0;
        docstyle.descent = 0;
        docstyle.nobreak = 0;
        docstyle.underline = false;
        docstyle.script = 0;
        docstyle.href = null;
        if("th".equals(getName()))
            docstyle.font = docstyle.font.getBold();
        if(!ignoreNoWrap && getAttribute("width") != null)
            ignoreNoWrap = true;
        if(getAttribute("nowrap") != null && !ignoreNoWrap)
            docstyle.nobreak = 1;
        if((s = getAttribute("align")) != null)
            docstyle.format = Align.getFormat(super.atts, "align", 0);
        if(docstyle.format == 2)
            if("th".equals(getName()))
                docstyle.format = 3;
            else
            if("td".equals(getName()))
                docstyle.format = 0;
        applyPadding(docstyle);
    }

    private void applyPadding(DocStyle docstyle)
    {
        int i = getCellPadding(docstyle);
        docstyle.left += i;
        docstyle.right += i;
    }

    private int getCellPadding(DocStyle docstyle)
    {
        int i = 1;
        for(; docstyle.tag != null; docstyle = docstyle.next)
        {
            if(!"table".equals(docstyle.tag.getName()))
                continue;
            Attributes attributes = docstyle.tag.getAttributes();
            if(attributes != null)
            {
                int j = 0;
                String s = attributes.get("border");
                if(s != null)
                {
                    j = 1;
                    try
                    {
                        j = Integer.parseInt(s);
                    }
                    catch(Exception _ex) { }
                }
                String s1 = attributes.get("cellpadding");
                if(s1 != null)
                    try
                    {
                        i = Integer.parseInt(s1);
                    }
                    catch(Exception _ex) { }
                i += j <= 0 ? 0 : 1;
            }
            break;
        }

        return i;
    }

    public void ignoreNoWrap()
    {
        ignoreNoWrap = true;
    }

    public TD()
    {
        ignoreNoWrap = false;
    }

    boolean ignoreNoWrap;
}
