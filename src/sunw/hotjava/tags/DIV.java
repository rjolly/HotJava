// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DIV.java

package sunw.hotjava.tags;

import sunw.hotjava.doc.*;

// Referenced classes of package sunw.hotjava.tags:
//            Align

public class DIV extends BlockTagItem
{

    public void init(Document document)
    {
        if(super.atts != null)
            fmt = Align.getFormat(super.atts, "align", 0);
    }

    public void modifyStyle(DocStyle docstyle)
    {
        super.modifyStyle(docstyle);
        docstyle.format = fmt;
    }

    public DIV()
    {
        fmt = 0;
    }

    private int fmt;
}
