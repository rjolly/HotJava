// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TagStack.java

package sunw.html;

import java.io.PrintStream;
import java.util.BitSet;
import java.util.Vector;

// Referenced classes of package sunw.html:
//            ContentModelState, DTD, DTDConstants, Element, 
//            NPrintWriter, Tag, ContentModel

public final class TagStack
    implements DTDConstants
{

    TagStack(Tag tag1, TagStack tagstack)
    {
        tag = tag1;
        elem = tag1.getElement();
        next = tagstack;
        Element element = tag1.getElement();
        if(element.getContent() != null)
            state = new ContentModelState(element.getContent());
        if(tagstack != null)
        {
            inclusions = tagstack.inclusions;
            exclusions = tagstack.exclusions;
            pre = tagstack.pre;
        }
        if(tag1.isPreformatted())
            pre = true;
        if(element.inclusions != null)
            if(inclusions != null)
            {
                inclusions = (BitSet)inclusions.clone();
                inclusions.or(element.inclusions);
            } else
            {
                inclusions = element.inclusions;
            }
        if(element.exclusions != null)
        {
            if(exclusions != null)
            {
                exclusions = (BitSet)exclusions.clone();
                exclusions.or(element.exclusions);
                return;
            }
            exclusions = element.exclusions;
        }
    }

    public Tag getTag()
    {
        return tag;
    }

    public Element first()
    {
        if(state != null)
            return state.first();
        else
            return null;
    }

    public ContentModel contentModel()
    {
        if(state == null)
            return null;
        else
            return state.getModel();
    }

    boolean excluded(int i)
    {
        return exclusions != null && exclusions.get(elem.getIndex());
    }

    boolean included(Vector vector, DTD dtd)
    {
        for(int i = 0; i < inclusions.size(); i++)
            if(inclusions.get(i))
            {
                vector.addElement(dtd.getElement(i));
                System.out.println("Element add thru' inclusions: " + dtd.getElement(i).getName());
            }

        return !vector.isEmpty();
    }

    boolean advance(Element element)
    {
        if(exclusions != null && exclusions.get(element.getIndex()))
            return false;
        if(state != null)
        {
            ContentModelState contentmodelstate = state.advance(element);
            if(contentmodelstate != null)
            {
                if(element.getType() != 19)
                    state = contentmodelstate;
                return true;
            }
        } else
        if(elem.getType() == 19)
            return true;
        return inclusions != null && inclusions.get(element.getIndex());
    }

    boolean terminate()
    {
        return state == null || state.terminate();
    }

    public String toString()
    {
        if(next == null)
            return "<" + tag.getElement().getName() + ">";
        else
            return next + " <" + tag.getElement().getName() + ">";
    }

    Tag tag;
    Element elem;
    ContentModelState state;
    TagStack next;
    BitSet inclusions;
    BitSet exclusions;
    boolean net;
    boolean pre;
}
