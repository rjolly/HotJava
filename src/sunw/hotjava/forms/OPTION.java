// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OPTION.java

package sunw.hotjava.forms;

import sunw.hotjava.doc.*;

// Referenced classes of package sunw.hotjava.forms:
//            FORM

public class OPTION extends FlowTagItem
{

    boolean selected()
    {
        String s = getAttribute("selected");
        return s != null;
    }

    void markTextItemsAsPseudo(Document document)
    {
        int i = getIndex();
        for(int j = i + getOffset(); i++ < j;)
        {
            DocItem docitem = document.getItem(i);
            if(docitem.isText())
                ((TextItem)docitem).setPseudoTextItem(true);
        }

    }

    public void setFormParent(TagItem tagitem)
    {
        if(tagitem == null || !(tagitem instanceof FORM))
        {
            return;
        } else
        {
            parentForm = (FORM)tagitem;
            return;
        }
    }

    public TagItem getFormParent()
    {
        return parentForm;
    }

    public boolean visit(DocItemVisitor docitemvisitor)
    {
        return docitemvisitor.visitOPTIONTag(this);
    }

    public OPTION()
    {
    }

    private FORM parentForm;
}
