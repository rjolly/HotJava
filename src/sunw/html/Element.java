// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Element.java

package sunw.html;

import java.io.Serializable;
import java.util.*;

// Referenced classes of package sunw.html:
//            AttributeList, DTDConstants, ContentModel

public final class Element
    implements DTDConstants, Serializable
{

    Element()
    {
        type = 19;
    }

    Element(String s, int i)
    {
        type = 19;
        name = s;
        index = i;
        maxIndex = Math.max(maxIndex, i);
    }

    public String getName()
    {
        return name;
    }

    public boolean omitStart()
    {
        return oStart;
    }

    public boolean omitEnd()
    {
        return oEnd;
    }

    public int getType()
    {
        return type;
    }

    public ContentModel getContent()
    {
        return content;
    }

    public AttributeList getAttributes()
    {
        return atts;
    }

    public int getIndex()
    {
        return index;
    }

    public boolean isEmpty()
    {
        return type == 17;
    }

    public String toString()
    {
        return name;
    }

    public AttributeList getAttribute(String s)
    {
        for(AttributeList attributelist = atts; attributelist != null; attributelist = attributelist.next)
            if(attributelist.name.equals(s))
                return attributelist;

        return null;
    }

    public AttributeList getAttributeByValue(String s)
    {
        for(AttributeList attributelist = atts; attributelist != null; attributelist = attributelist.next)
            if(attributelist.values != null && attributelist.values.contains(s))
                return attributelist;

        return null;
    }

    public static int getMaxIndex()
    {
        return maxIndex;
    }

    public static int name2type(String s)
    {
        Integer integer = (Integer)contentTypes.get(s);
        if(integer != null)
            return integer.intValue();
        else
            return 0;
    }

    public int index;
    public String name;
    public boolean oStart;
    public boolean oEnd;
    public BitSet inclusions;
    public BitSet exclusions;
    public int type;
    public ContentModel content;
    public AttributeList atts;
    static int maxIndex;
    public Object data;
    static Hashtable contentTypes;

    static 
    {
        contentTypes = new Hashtable();
        contentTypes.put("CDATA", new Integer(1));
        contentTypes.put("RCDATA", new Integer(16));
        contentTypes.put("EMPTY", new Integer(17));
        contentTypes.put("ANY", new Integer(19));
    }
}
