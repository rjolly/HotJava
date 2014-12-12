// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Entity.java

package sunw.html;

import java.util.Hashtable;

// Referenced classes of package sunw.html:
//            DTDConstants

public final class Entity
    implements DTDConstants
{

    public Entity(String s, int i, char ac[])
    {
        name = s;
        type = i;
        data = ac;
    }

    public String getName()
    {
        return name;
    }

    public int getType()
    {
        return type & 0xffff;
    }

    public boolean isParameter()
    {
        return (type & 0x40000) != 0;
    }

    public boolean isGeneral()
    {
        return (type & 0x10000) != 0;
    }

    public char[] getData()
    {
        return data;
    }

    public String getString()
    {
        return new String(data, 0, data.length);
    }

    public static int name2type(String s)
    {
        Integer integer = (Integer)entityTypes.get(s);
        if(integer == null)
            return 1;
        else
            return integer.intValue();
    }

    public String name;
    public int type;
    public char data[];
    static Hashtable entityTypes;

    static 
    {
        entityTypes = new Hashtable();
        entityTypes.put("PUBLIC", new Integer(10));
        entityTypes.put("CDATA", new Integer(1));
        entityTypes.put("SDATA", new Integer(11));
        entityTypes.put("PI", new Integer(12));
        entityTypes.put("STARTTAG", new Integer(13));
        entityTypes.put("ENDTAG", new Integer(14));
        entityTypes.put("MS", new Integer(15));
        entityTypes.put("MD", new Integer(16));
        entityTypes.put("SYSTEM", new Integer(17));
    }
}
