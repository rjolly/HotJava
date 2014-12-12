// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Heading.java

package sunw.hotjava.tags;

import sunw.hotjava.doc.*;

// Referenced classes of package sunw.hotjava.tags:
//            Align, LI

public class Heading extends BlockTagItem
{

    public String getText()
    {
        return "\n";
    }

    public void init(Document document)
    {
        if(super.atts != null)
            fmt = Align.getFormat(super.atts, "align", 2);
    }

    protected boolean beginState(Formatter formatter, FormatState formatstate)
    {
        return formatstate.state == 0 || enclosedInLI(formatter);
    }

    private boolean enclosedInLI(Formatter formatter)
    {
        if(getIndex() > 0)
        {
            Document document = formatter.getDocument();
            DocItem docitem = document.items[getIndex() - 1];
            if(docitem instanceof LI)
                return true;
        }
        return false;
    }

    protected boolean leaveSpace(Formatter formatter, FormatState formatstate)
    {
        return super.leaveSpace(formatter, formatstate) && !enclosedInLI(formatter);
    }

    public void modifyStyle(DocStyle docstyle)
    {
        super.modifyStyle(docstyle);
        if(fmt != 2)
            docstyle.format = fmt;
    }

    public Heading()
    {
        fmt = 2;
    }

    private int fmt;
}
