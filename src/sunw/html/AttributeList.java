// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AttributeList.java

package sunw.html;

import java.io.Serializable;
import java.util.*;

// Referenced classes of package sunw.html:
//            DTDConstants

public final class AttributeList
    implements DTDConstants, Serializable
{

    AttributeList()
    {
    }

    public AttributeList(String s)
    {
        name = s;
    }

    public AttributeList(String s, int i, int j, String s1, Vector vector, AttributeList attributelist)
    {
        name = s;
        type = i;
        modifier = j;
        value = s1;
        values = vector;
        next = attributelist;
    }

    public String getName()
    {
        return name;
    }

    public int getType()
    {
        return type;
    }

    public int getModifier()
    {
        return modifier;
    }

    public Enumeration getValues()
    {
        if(values != null)
            return values.elements();
        else
            return null;
    }

    public String getValue()
    {
        return value;
    }

    public AttributeList getNext()
    {
        return next;
    }

    public String toString()
    {
        return name;
    }

    static void defineAttributeType(String s, int i)
    {
        Integer integer = new Integer(i);
        attributeTypes.put(s, integer);
        attributeTypes.put(integer, s);
    }

    public static int name2type(String s)
    {
        Integer integer = (Integer)attributeTypes.get(s);
        if(integer == null)
            return 1;
        else
            return integer.intValue();
    }

    public static String type2name(int i)
    {
        return (String)attributeTypes.get(new Integer(i));
    }

    public String name;
    public int type;
    public Vector values;
    public int modifier;
    public String value;
    public AttributeList next;
    static Hashtable attributeTypes;

    static 
    {
        attributeTypes = new Hashtable();
        defineAttributeType("CDATA", 1);
        defineAttributeType("ENTITY", 2);
        defineAttributeType("ENTITIES", 3);
        defineAttributeType("ID", 4);
        defineAttributeType("IDREF", 5);
        defineAttributeType("IDREFS", 6);
        defineAttributeType("NAME", 7);
        defineAttributeType("NAMES", 8);
        defineAttributeType("NMTOKEN", 9);
        defineAttributeType("NMTOKENS", 10);
        defineAttributeType("NOTATION", 11);
        defineAttributeType("NUMBER", 12);
        defineAttributeType("NUMBERS", 13);
        defineAttributeType("NUTOKEN", 14);
        defineAttributeType("NUTOKENS", 15);
        attributeTypes.put("fixed", new Integer(1));
        attributeTypes.put("required", new Integer(2));
        attributeTypes.put("current", new Integer(3));
        attributeTypes.put("conref", new Integer(4));
        attributeTypes.put("implied", new Integer(5));
    }
}
