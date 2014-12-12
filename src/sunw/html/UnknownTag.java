// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UnknownTag.java

package sunw.html;


// Referenced classes of package sunw.html:
//            Tag, Element, Attributes

final class UnknownTag
    implements Tag
{

    UnknownTag(Element element, Attributes attributes)
    {
        elem = element;
        atts = attributes;
    }

    public boolean isBlock()
    {
        return false;
    }

    public boolean isPreformatted()
    {
        return true;
    }

    public Element getElement()
    {
        return elem;
    }

    public Attributes getAttributes()
    {
        return atts;
    }

    public String toString()
    {
        if(atts != null)
            return "<TAG " + elem + " " + atts + ">";
        else
            return "<TAG " + elem + ">";
    }

    public boolean hasJavaScriptHandlers()
    {
        return false;
    }

    Element elem;
    Attributes atts;
}
