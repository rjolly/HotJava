// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TEXTAREA.java

package sunw.hotjava.forms;

import java.awt.Component;
import sunw.hotjava.doc.*;

// Referenced classes of package sunw.hotjava.forms:
//            SELECT, FORM, FormPanel

public class TEXTAREA extends SELECT
{

    public TEXTAREA()
    {
        super.handlerStrings = myHandlers;
    }

    public Component createView(Formatter formatter, Document document)
    {
        FormPanel formpanel = new FormPanel(formatter, document, this, "textarea");
        formpanel.setBackground(formatter.getFormatterBackgroundColor());
        int i = getIndex();
        int j = getOffset();
        document.change(i << 12, i + j + 1 << 12);
        return formpanel;
    }

    public boolean isPreformatted()
    {
        return true;
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
        return docitemvisitor.visitTEXTAREATag(this);
    }

    private FORM parentForm;
    private static final String myHandlers[] = {
        "onblur", "onfocus", "onchange", "onkeydown", "onkeypress", "onkeyup", "onselect"
    };

}
