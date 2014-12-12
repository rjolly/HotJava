// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AREA.java

package sunw.hotjava.tags;

import sunw.hotjava.doc.*;

public class AREA extends EmptyTagItem
{

    public AREA()
    {
        super.handlerStrings = myHandlers;
    }

    public boolean visit(DocItemVisitor docitemvisitor)
    {
        return docitemvisitor.visitAREATag(this);
    }

    private static final String myHandlers[] = {
        "onclick", "ondblclick", "onmouseout", "onmouseover"
    };

}
