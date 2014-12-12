// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Align.java

package sunw.hotjava.tags;

import java.util.Hashtable;
import sunw.hotjava.doc.*;
import sunw.html.Attributes;

public class Align
{

    public static int getAlign(Attributes attributes)
    {
        return getAlign(attributes, "align");
    }

    public static int getAlign(Attributes attributes, String s)
    {
        return getAlign(attributes, s, 5);
    }

    public static int getAlign(Attributes attributes, String s, int i)
    {
        if(attributes != null)
        {
            String s1 = attributes.get(s);
            if(s1 != null)
            {
                Number number = (Number)alignHash.get(s1.toLowerCase());
                if(number != null)
                    return number.intValue();
            }
        }
        return i;
    }

    public static int getFormat(Attributes attributes, String s, int i)
    {
        if(attributes != null)
        {
            String s1 = attributes.get(s);
            if(s1 != null)
            {
                Number number = (Number)alignHash.get(s1.toLowerCase());
                if(number != null)
                    switch(number.intValue())
                    {
                    case 7: // '\007'
                        return 0;

                    case 2: // '\002'
                        return 3;

                    case 8: // '\b'
                        return 1;
                    }
            }
        }
        return i;
    }

    public static int getCheckedAlign(Attributes attributes, String s, int i, int j)
    {
        int k = getAlign(attributes, s, j);
        if((1 << k & i) == 0)
            return -1;
        else
            return k;
    }

    public static int yOffset(DocLine docline, DocStyle docstyle, int i, int j)
    {
        switch(i)
        {
        case 0: // '\0'
            return docline.baseline - Math.max(docline.textAscent, docline.lnascent);

        case 1: // '\001'
            return docline.baseline - docline.textAscent;

        case 2: // '\002'
            return docline.baseline - j / 2;

        case 3: // '\003'
            return (docline.height - j) / 2;

        case 4: // '\004'
        case 5: // '\005'
            return docline.baseline - j;

        case 6: // '\006'
            return (docline.baseline + docline.lndescent) - j;
        }
        return 0;
    }

    public static int getAscent(FormatState formatstate, int i, int j)
    {
        switch(i)
        {
        case 0: // '\0'
            return formatstate.ascent;

        case 1: // '\001'
            return formatstate.textAscent;

        case 2: // '\002'
            return j / 2;

        case 3: // '\003'
            return j / 2 + ((TraversalState) (formatstate)).style.ascent / 2;

        case 4: // '\004'
        case 5: // '\005'
            return j;

        case 6: // '\006'
            return j - formatstate.descent;
        }
        return j;
    }

    public static int getDescent(FormatState formatstate, int i, int j)
    {
        switch(i)
        {
        case 0: // '\0'
            return j - formatstate.ascent;

        case 1: // '\001'
            return j - formatstate.textAscent;

        case 2: // '\002'
            return j / 2;

        case 3: // '\003'
            return j / 2 - ((TraversalState) (formatstate)).style.ascent / 2;

        case 4: // '\004'
        case 5: // '\005'
            return 0;

        case 6: // '\006'
            return formatstate.descent;
        }
        return 0;
    }

    public Align()
    {
    }

    public static final int ALIGN_TOP = 0;
    public static final int ALIGN_TEXTTOP = 1;
    public static final int ALIGN_MIDDLE = 2;
    public static final int ALIGN_ABSMIDDLE = 3;
    public static final int ALIGN_BASELINE = 4;
    public static final int ALIGN_BOTTOM = 5;
    public static final int ALIGN_ABSBOTTOM = 6;
    public static final int ALIGN_LEFT = 7;
    public static final int ALIGN_RIGHT = 8;
    public static final int ALIGN_CENTER = 2;
    public static final int TOP_MASK = 1;
    public static final int TEXTTOP_MASK = 2;
    public static final int MIDDLE_MASK = 4;
    public static final int ABSMIDDLE_MASK = 8;
    public static final int BASELINE_MASK = 16;
    public static final int BOTTOM_MASK = 32;
    public static final int ABSBOTTOM_MASK = 64;
    public static final int LEFT_MASK = 128;
    public static final int RIGHT_MASK = 256;
    public static final int CENTER_MASK = 4;
    private static Hashtable alignHash;

    static 
    {
        alignHash = new Hashtable(11);
        alignHash.put("top", new Integer(0));
        alignHash.put("texttop", new Integer(1));
        alignHash.put("middle", new Integer(2));
        alignHash.put("absmiddle", new Integer(3));
        alignHash.put("baseline", new Integer(4));
        alignHash.put("bottom", new Integer(5));
        alignHash.put("absbottom", new Integer(6));
        alignHash.put("left", new Integer(7));
        alignHash.put("right", new Integer(8));
        alignHash.put("center", new Integer(2));
    }
}
