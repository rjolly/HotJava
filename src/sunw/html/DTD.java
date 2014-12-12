// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DTD.java

package sunw.html;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

// Referenced classes of package sunw.html:
//            AttributeList, ContentModel, DTDConstants, Element, 
//            Entity

public class DTD
    implements DTDConstants
{

    protected DTD(String s)
    {
        elements = new Vector();
        elementHash = new Hashtable();
        entityHash = new Hashtable();
        name = s;
        defEntity("#RE", 0x10000, 13);
        defEntity("#RS", 0x10000, 10);
        defEntity("#SPACE", 0x10000, 32);
    }

    public String getName()
    {
        return name;
    }

    public Entity getEntity(String s)
    {
        return (Entity)entityHash.get(s);
    }

    public Entity getEntity(int i)
    {
        return (Entity)entityHash.get(new Integer(i));
    }

    public boolean elementExists(String s)
    {
        Element element = (Element)elementHash.get(s);
        return element != null;
    }

    public Element getElement(String s)
    {
        Element element = (Element)elementHash.get(s);
        if(element == null)
        {
            element = new Element(s, elements.size());
            elements.addElement(element);
            elementHash.put(s, element);
        }
        return element;
    }

    public Element getElement(int i)
    {
        return (Element)elements.elementAt(i);
    }

    public Entity defineEntity(String s, int i, char ac[])
    {
        Entity entity = (Entity)entityHash.get(s);
        if(entity == null)
        {
            entity = new Entity(s, i, ac);
            entityHash.put(s, entity);
            if((i & 0x10000) != 0 && ac.length == 1)
                switch(i & 0xfffeffff)
                {
                case 1: // '\001'
                case 11: // '\013'
                    entityHash.put(new Integer(ac[0]), entity);
                    break;
                }
        }
        return entity;
    }

    public Element defineElement(String s, int i, boolean flag, boolean flag1, ContentModel contentmodel, BitSet bitset, BitSet bitset1, 
            AttributeList attributelist)
    {
        Element element = getElement(s);
        element.type = i;
        element.oStart = flag;
        element.oEnd = flag1;
        element.content = contentmodel;
        element.exclusions = bitset;
        element.inclusions = bitset1;
        element.atts = attributelist;
        return element;
    }

    public void defineAttributes(String s, AttributeList attributelist)
    {
        Element element = getElement(s);
        element.atts = attributelist;
    }

    public Entity defEntity(String s, int i, int j)
    {
        char ac[] = {
            (char)j
        };
        return defineEntity(s, i, ac);
    }

    protected Entity defEntity(String s, int i, String s1)
    {
        int j = s1.length();
        char ac[] = new char[j];
        s1.getChars(0, j, ac, 0);
        return defineEntity(s, i, ac);
    }

    protected Element defElement(String s, int i, boolean flag, boolean flag1, ContentModel contentmodel, String as[], String as1[], 
            AttributeList attributelist)
    {
        BitSet bitset = null;
        if(as != null && as.length > 0)
        {
            bitset = new BitSet();
            for(int j = 0; j < as.length; j++)
            {
                String s1 = as[j];
                if(s1.length() > 0)
                    bitset.set(getElement(s1).getIndex());
            }

        }
        BitSet bitset1 = null;
        if(as1 != null && as1.length > 0)
        {
            bitset1 = new BitSet();
            for(int k = 0; k < as1.length; k++)
            {
                String s2 = as1[k];
                if(s2.length() > 0)
                    bitset1.set(getElement(s2).getIndex());
            }

        }
        return defineElement(s, i, flag, flag1, contentmodel, bitset, bitset1, attributelist);
    }

    protected AttributeList defAttributeList(String s, int i, int j, String s1, String s2, AttributeList attributelist)
    {
        Vector vector = null;
        if(s2 != null)
        {
            vector = new Vector();
            for(StringTokenizer stringtokenizer = new StringTokenizer(s2, "|"); stringtokenizer.hasMoreTokens();)
            {
                String s3 = stringtokenizer.nextToken();
                if(s3.length() > 0)
                    vector.addElement(s3);
            }

        }
        return new AttributeList(s, i, j, s1, vector, attributelist);
    }

    protected ContentModel defContentModel(int i, Object obj, ContentModel contentmodel)
    {
        return new ContentModel(i, obj, contentmodel);
    }

    public String toString()
    {
        return name;
    }

    public static void putDTDHash(String s, DTD dtd)
    {
        dtdHash.put(s, dtd);
    }

    public static DTD getDTD(String s)
        throws IOException
    {
        s = s.toLowerCase();
        DTD dtd = (DTD)dtdHash.get(s);
        if(dtd == null)
            dtd = new DTD(s);
        return dtd;
    }

    public void read(DataInputStream datainputstream)
        throws IOException
    {
        if(datainputstream.readInt() != FILE_VERSION)
            throw new IOException("bad .bdtd version");
        String as[] = new String[datainputstream.readShort()];
        for(int i = 0; i < as.length; i++)
            as[i] = datainputstream.readUTF();

        short word0 = datainputstream.readShort();
        for(int j = 0; j < word0; j++)
        {
            short word1 = datainputstream.readShort();
            byte byte0 = datainputstream.readByte();
            String s = datainputstream.readUTF();
            defEntity(as[word1], byte0 | 0x10000, s);
        }

        word0 = datainputstream.readShort();
        for(int k = 0; k < word0; k++)
        {
            short word2 = datainputstream.readShort();
            byte byte1 = datainputstream.readByte();
            byte byte2 = datainputstream.readByte();
            ContentModel contentmodel = readContentModel(datainputstream, as);
            String as1[] = readNameArray(datainputstream, as);
            String as2[] = readNameArray(datainputstream, as);
            AttributeList attributelist = readAttributeList(datainputstream, as);
            defElement(as[word2], byte1, (byte2 & 1) != 0, (byte2 & 2) != 0, contentmodel, as1, as2, attributelist);
        }

    }

    private ContentModel readContentModel(DataInputStream datainputstream, String as[])
        throws IOException
    {
        byte byte0 = datainputstream.readByte();
        switch(byte0)
        {
        case 0: // '\0'
            return null;

        case 1: // '\001'
            byte byte1 = datainputstream.readByte();
            ContentModel contentmodel = readContentModel(datainputstream, as);
            ContentModel contentmodel1 = readContentModel(datainputstream, as);
            return defContentModel(byte1, contentmodel, contentmodel1);

        case 2: // '\002'
            byte byte2 = datainputstream.readByte();
            Element element = getElement(as[datainputstream.readShort()]);
            ContentModel contentmodel2 = readContentModel(datainputstream, as);
            return defContentModel(byte2, element, contentmodel2);
        }
        throw new IOException("bad bdtd");
    }

    private String[] readNameArray(DataInputStream datainputstream, String as[])
        throws IOException
    {
        short word0 = datainputstream.readShort();
        if(word0 == 0)
            return null;
        String as1[] = new String[word0];
        for(int i = 0; i < word0; i++)
            as1[i] = as[datainputstream.readShort()];

        return as1;
    }

    private AttributeList readAttributeList(DataInputStream datainputstream, String as[])
        throws IOException
    {
        AttributeList attributelist = null;
        for(int i = datainputstream.readByte(); i > 0; i--)
        {
            short word0 = datainputstream.readShort();
            byte byte0 = datainputstream.readByte();
            byte byte1 = datainputstream.readByte();
            short word1 = datainputstream.readShort();
            String s = word1 != -1 ? as[word1] : null;
            Vector vector = null;
            short word2 = datainputstream.readShort();
            if(word2 > 0)
            {
                vector = new Vector(word2);
                for(int j = 0; j < word2; j++)
                    vector.addElement(as[datainputstream.readShort()]);

            }
            attributelist = new AttributeList(as[word0], byte0, byte1, s, vector, attributelist);
        }

        return attributelist;
    }

    public String name;
    public Vector elements;
    public Hashtable elementHash;
    public Hashtable entityHash;
    public final Element pcdata = getElement("#pcdata");
    public final Element html = getElement("html");
    public final Element meta = getElement("meta");
    public final Element base = getElement("base");
    public final Element isindex = getElement("isindex");
    public final Element head = getElement("head");
    public final Element body = getElement("body");
    public final Element frameset = getElement("frameset");
    public final Element applet = getElement("applet");
    public final Element param = getElement("param");
    public final Element p = getElement("p");
    public final Element title = getElement("title");
    public final Element script = getElement("script");
    public static int FILE_VERSION = 1;
    static Hashtable dtdHash = new Hashtable();

}
