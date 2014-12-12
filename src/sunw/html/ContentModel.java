// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ContentModel.java

package sunw.html;

import java.io.Serializable;
import java.util.Vector;

// Referenced classes of package sunw.html:
//            Element

public final class ContentModel
    implements Serializable
{

    public ContentModel()
    {
    }

    public ContentModel(Element element)
    {
        this(0, element, null);
    }

    public ContentModel(int i, ContentModel contentmodel)
    {
        this(i, contentmodel, null);
    }

    public ContentModel(int i, Object obj, ContentModel contentmodel)
    {
        type = i;
        content = obj;
        next = contentmodel;
    }

    public boolean empty()
    {
        switch(type)
        {
        case 42: // '*'
        case 63: // '?'
            return true;

        case 43: // '+'
        case 124: // '|'
            for(ContentModel contentmodel = (ContentModel)content; contentmodel != null; contentmodel = contentmodel.next)
                if(contentmodel.empty())
                    return true;

            return false;

        case 38: // '&'
        case 44: // ','
            for(ContentModel contentmodel1 = (ContentModel)content; contentmodel1 != null; contentmodel1 = contentmodel1.next)
                if(!contentmodel1.empty())
                    return false;

            return true;
        }
        return false;
    }

    public void getElements(Vector vector)
    {
        switch(type)
        {
        case 42: // '*'
        case 43: // '+'
        case 63: // '?'
            ((ContentModel)content).getElements(vector);
            return;

        case 38: // '&'
        case 44: // ','
        case 124: // '|'
            for(ContentModel contentmodel = (ContentModel)content; contentmodel != null; contentmodel = contentmodel.next)
                contentmodel.getElements(vector);

            return;
        }
        vector.addElement(content);
    }

    public boolean first(Object obj)
    {
        switch(type)
        {
        case 42: // '*'
        case 43: // '+'
        case 63: // '?'
            return ((ContentModel)content).first(obj);

        case 44: // ','
            for(ContentModel contentmodel = (ContentModel)content; contentmodel != null; contentmodel = contentmodel.next)
            {
                if(contentmodel.first(obj))
                    return true;
                if(!contentmodel.empty())
                    return false;
            }

            return false;

        case 38: // '&'
        case 124: // '|'
            Element element = (Element)obj;
            if(valSet == null)
            {
                valSet = new boolean[Element.maxIndex + 1];
                val = new boolean[Element.maxIndex + 1];
            }
            if(valSet[element.index])
                return val[element.index];
            for(ContentModel contentmodel1 = (ContentModel)content; contentmodel1 != null; contentmodel1 = contentmodel1.next)
            {
                if(!contentmodel1.first(obj))
                    continue;
                val[element.index] = true;
                break;
            }

            valSet[element.index] = true;
            return val[element.index];
        }
        return content == obj;
    }

    public Element first()
    {
        switch(type)
        {
        case 38: // '&'
        case 42: // '*'
        case 63: // '?'
        case 124: // '|'
            return null;

        case 43: // '+'
        case 44: // ','
            return ((ContentModel)content).first();
        }
        return (Element)content;
    }

    public String toString()
    {
        switch(type)
        {
        case 42: // '*'
            return content + "*";

        case 63: // '?'
            return content + "?";

        case 43: // '+'
            return content + "+";

        case 38: // '&'
        case 44: // ','
        case 124: // '|'
            char ac[] = {
                ' ', (char)type, ' '
            };
            String s = "";
            for(ContentModel contentmodel = (ContentModel)content; contentmodel != null; contentmodel = contentmodel.next)
            {
                s = s + contentmodel;
                if(contentmodel.next != null)
                    s = s + new String(ac);
            }

            return "(" + s + ")";
        }
        return content.toString();
    }

    public int type;
    public Object content;
    public ContentModel next;
    private boolean valSet[];
    private boolean val[];
}
