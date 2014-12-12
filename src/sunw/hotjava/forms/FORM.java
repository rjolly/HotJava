// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FORM.java

package sunw.hotjava.forms;

import java.util.Enumeration;
import java.util.Vector;
import sunw.hotjava.doc.*;
import sunw.hotjava.tables.TABLE;

// Referenced classes of package sunw.hotjava.forms:
//            FormEvent, FormListener, INPUT, SELECT, 
//            TEXTAREA

public class FORM extends BlockTagItem
{

    public FORM()
    {
        formListeners = new Vector();
        formElements = new Vector();
        super.handlerStrings = myHandlers;
    }

    public void init(Document document)
    {
        doc = document;
        super.init(document);
    }

    public boolean formatEndTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(doc.length() >> 12 > getIndex() + getOffset() + 1)
        {
            DocItem docitem = doc.getItem(getIndex() + getOffset() + 1);
            if(docitem.getOffset() < 0)
                docitem = doc.getItem(docitem.getIndex() + docitem.getOffset());
            if(docitem.getOffset() != 0 && (docitem instanceof FORM))
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
        if(formatstate.above == 0 && getIndex() + getOffset() + 1 < formatter.getDocument().nitems && (formatter.getDocument().items[getIndex() + getOffset() + 1] instanceof TABLE))
            formatstate.above = super.style.getAbove(((TraversalState) (formatstate)).style) / 2;
        return super.formatEndTag(formatter, formatstate, formatstate1);
    }

    String getAction()
    {
        String s = getAttribute("action");
        if(s != null)
        {
            int i = s.indexOf('?');
            String s1 = getAttribute("method");
            if(i != -1 && (s1 == null || s1.equalsIgnoreCase("get")))
                s = s.substring(0, i);
        }
        return s;
    }

    String getTarget()
    {
        String s = getAttribute("target");
        return s;
    }

    String getEncType()
    {
        String s = getAttribute("enctype");
        return s;
    }

    String getMethod()
    {
        String s = getAttribute("method");
        if(s != null)
            return s.toLowerCase();
        else
            return null;
    }

    public void addFormElement(TagItem tagitem)
    {
        if(!(tagitem instanceof INPUT) && !(tagitem instanceof SELECT) && !(tagitem instanceof TEXTAREA))
        {
            return;
        } else
        {
            formElements.addElement(tagitem);
            return;
        }
    }

    public Enumeration getFormElements()
    {
        return formElements.elements();
    }

    public int getFormPanel(Formatter formatter, Vector vector)
    {
        return getOffset() + 1;
    }

    boolean aboutToSubmit()
    {
        return sendFormEventToListeners(new FormEvent(this, 0));
    }

    boolean aboutToReset()
    {
        return sendFormEventToListeners(new FormEvent(this, 1));
    }

    private boolean sendFormEventToListeners(FormEvent formevent)
    {
        for(Enumeration enumeration = formListeners.elements(); enumeration.hasMoreElements();)
        {
            FormListener formlistener = (FormListener)enumeration.nextElement();
            if(formevent.getType() == 0)
                formlistener.submitEvent(formevent);
            else
            if(formevent.getType() == 1)
                formlistener.resetEvent(formevent);
            if(formevent.getCancelled())
                break;
        }

        return !formevent.getCancelled();
    }

    public void addFormListener(FormListener formlistener)
    {
        if(formlistener != null)
            formListeners.addElement(formlistener);
    }

    public void removeFormListener(FormListener formlistener)
    {
        if(formlistener != null && formListeners.contains(formlistener))
            formListeners.removeElement(formlistener);
    }

    public boolean visit(DocItemVisitor docitemvisitor)
    {
        return docitemvisitor.visitFORMTag(this);
    }

    private Vector formElements;
    private Document doc;
    private static final String myHandlers[] = {
        "onsubmit", "onreset"
    };
    private Vector formListeners;

}
