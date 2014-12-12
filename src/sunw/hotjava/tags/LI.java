// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   LI.java

package sunw.hotjava.tags;

import java.awt.*;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;
import sunw.html.Attributes;

// Referenced classes of package sunw.hotjava.tags:
//            UL

public class LI extends BulletTagItem
{

    public synchronized void init(Document document)
    {
        doc = document;
    }

    private int getItemNumber(DocStyle docstyle)
    {
        int i = 0;
        for(int j = docstyle.next.tag.getIndex() + 1; j <= docstyle.tag.getIndex(); j++)
        {
            DocItem docitem = docstyle.doc.getItem(j);
            TagItem tagitem = docitem.getTag(docstyle.doc);
            if(tagitem != null && tagitem.getName().equals("li"))
                i++;
            if(docitem.getOffset() > 0)
                j += docitem.getOffset();
        }

        return i;
    }

    static int bulletNameToType(String s)
    {
        for(int i = 0; i < bulletNames.length; i++)
            if(s.equalsIgnoreCase(bulletNames[i]))
                return i;

        return 0;
    }

    static int nextBulletType(int i)
    {
        for(int j = 0; j < bulletOrder.length - 1; j++)
            if(bulletOrder[j] == i)
                return bulletOrder[j + 1];

        return 0;
    }

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(!super.formatStartTag(formatter, formatstate, formatstate1))
        {
            int i = getIndex();
            if(i <= doc.nitems && doc.getTagName(doc.getItem(i + 1)).equals("pre"))
                formatstate.state = 0;
            return false;
        } else
        {
            return true;
        }
    }

    public int paintBullet(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        DocStyle docstyle = formatter.displayStyle;
        int k = super.style.left;
        if(docstyle == null || docstyle.next == null || docstyle.next.tag == null)
            return k;
        TagItem tagitem = docstyle.next.tag;
        String s = tagitem.getName();
        if("ul".equals(s) || "menu".equals(s) || "dir".equals(s))
            paintUnorderedItem(formatter, g, docstyle, i + k, j, docline);
        else
        if("ol".equals(s))
            paintOrderedItem(g, docstyle, i, j, docline);
        return k;
    }

    private void paintUnorderedItem(Formatter formatter, Graphics g, DocStyle docstyle, int i, int j, DocLine docline)
    {
        HJBProperties.getHJBProperties("beanPropertiesKey");
        int k = docstyle.ascent;
        i -= k + 2;
        Color color = formatter.getFormatterBackgroundColor();
        if(k >= 0)
        {
            formatter.getFormatterBackgroundColor();
            Graphics g1 = formatter.getHackGraphics();
            if(g1 != null)
                g = g1;
            int l = i + 2;
            int i1 = (j + 2 + docline.baseline) - k;
            drawBullet(g, color, docstyle, l, i1, k - 6);
        }
    }

    private void drawBullet(Graphics g, Color color, DocStyle docstyle, int i, int j, int k)
    {
        TagItem tagitem = docstyle.next.tag;
        int l = 0;
        Color color1 = g.getColor();
        if(super.atts != null)
        {
            String s = super.atts.get("type");
            if(s != null)
            {
                l = bulletNameToType(s);
                if(tagitem instanceof UL)
                {
                    UL ul1 = (UL)tagitem;
                    ul1.bulletType = l;
                }
            }
        } else
        if(tagitem instanceof UL)
        {
            UL ul = (UL)tagitem;
            l = ul.bulletType;
        }
        g.setColor(docstyle.color);
        j++;
        switch(l)
        {
        default:
            break;

        case 0: // '\0'
            if(k > 6)
            {
                g.fillOval(i, j, k, k);
                break;
            }
            if(k < 3)
                k++;
            g.fillRect(i, j, k, k);
            break;

        case 1: // '\001'
            g.setColor(color);
            g.fillOval(i, j, k, k);
            g.setColor(docstyle.color);
            g.drawOval(i, j, k, k);
            break;

        case 2: // '\002'
            g.drawRect(i, j, k, k);
            break;
        }
        g.setColor(color1);
    }

    private void paintOrderedItem(Graphics g, DocStyle docstyle, int i, int j, DocLine docline)
    {
        Attributes attributes = docstyle.next.tag.getAttributes();
        boolean flag = false;
        int k = 0;
        Attributes attributes1 = docstyle.tag.getAttributes();
        if(attributes1 != null)
            try
            {
                k = Integer.parseInt(attributes1.get("value"));
                flag = true;
            }
            catch(Exception _ex) { }
        if(!flag)
        {
            k = getItemNumber(docstyle);
            if(attributes != null)
            {
                int l = 1;
                try
                {
                    l = Integer.parseInt(attributes.get("start"));
                }
                catch(Exception _ex) { }
                k += l - 1;
            }
        }
        g.setFont(docstyle.font);
        g.setColor(docstyle.color);
        String s = formatItemNum(k, attributes, attributes1) + ".";
        i += super.style.left - 4;
        FontMetrics fontmetrics = docstyle.font.getFontMetrics(docstyle);
        i -= fontmetrics.stringWidth(s);
        i = Math.max(0, i);
        g.drawString(s, i, j + docline.baseline);
    }

    private String formatItemNum(int i, Attributes attributes, Attributes attributes1)
    {
        String s = "1";
        if(attributes != null)
        {
            String s1 = attributes.get("type");
            if(s1 != null)
                s = s1;
        }
        if(attributes1 != null)
        {
            String s2 = attributes1.get("type");
            if(s2 != null)
                s = s2;
        }
        boolean flag = false;
        String s3;
        switch(s.charAt(0))
        {
        case 49: // '1'
        default:
            s3 = String.valueOf(i);
            break;

        case 65: // 'A'
            flag = true;
            // fall through

        case 97: // 'a'
            s3 = formatAlphaNumerals(i);
            break;

        case 73: // 'I'
            flag = true;
            // fall through

        case 105: // 'i'
            s3 = formatRomanNumerals(i);
            break;
        }
        if(flag)
            s3 = s3.toUpperCase();
        return s3;
    }

    private String formatAlphaNumerals(int i)
    {
        String s = "";
        if(i > 26)
            s = formatAlphaNumerals(i / 26) + formatAlphaNumerals(i % 26);
        else
            s = String.valueOf((char)((97 + i) - 1));
        return s;
    }

    private String formatRomanNumerals(int i)
    {
        return formatRomanNumerals(0, i);
    }

    private String formatRomanNumerals(int i, int j)
    {
        if(j < 10)
            return formatRomanDigit(i, j);
        else
            return formatRomanNumerals(i + 1, j / 10) + formatRomanDigit(i, j % 10);
    }

    private String formatRomanDigit(int i, int j)
    {
        String s = "";
        if(j == 9)
        {
            s = s + romanChars[i][0];
            s = s + romanChars[i + 1][0];
            return s;
        }
        if(j == 4)
        {
            s = s + romanChars[i][0];
            s = s + romanChars[i][1];
            return s;
        }
        if(j >= 5)
        {
            s = s + romanChars[i][1];
            j -= 5;
        }
        for(int k = 0; k < j; k++)
            s = s + romanChars[i][0];

        return s;
    }

    public boolean measureStartTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        if(!super.measureStartTag(formatter, formatstate, measurement, measurestate))
        {
            int i = getIndex();
            if(i <= doc.nitems && doc.getTagName(doc.getItem(i + 1)).equals("pre"))
                formatstate.state = 0;
            return false;
        } else
        {
            return true;
        }
    }

    public LI()
    {
    }

    private Document doc;
    private static final int xOffset = 2;
    private static final int numericRightOffset = 4;
    static final int DISC_IMG_INDEX = 0;
    static final int CIRCLE_IMG_INDEX = 1;
    static final int SQUARE_IMG_INDEX = 2;
    private static int bulletOrder[] = {
        0, 1, 2, 2
    };
    private static String bulletNames[] = {
        "disc", "circle", "square"
    };
    private static final char romanChars[][] = {
        {
            'i', 'v'
        }, {
            'x', 'l'
        }, {
            'c', 'd'
        }, {
            'm', '?'
        }
    };

}
