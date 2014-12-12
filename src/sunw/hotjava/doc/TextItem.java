// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TextItem.java

package sunw.hotjava.doc;

import java.awt.*;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.doc:
//            DocItem, DocConstants, DocFont, DocLine, 
//            DocStyle, Document, FormatState, Formatter, 
//            Measurement, OverlappingStringMatch, StyleSheet, TagItem, 
//            TraversalState, MeasureState, VBreakInfo

public class TextItem extends DocItem
{

    public static void initProperties()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        String s = "hotjava.optimizeLineBreaks";
        enableFastFormatText = hjbproperties.getBoolean(s);
        if(!enableFastFormatText)
        {
            try
            {
                flexyTextClass = Class.forName("sunw.hotjava.doc.FlexyText");
                if(flexyTextClass == null)
                {
                    enableFastFormatText = true;
                    return;
                }
                Method amethod[] = flexyTextClass.getMethods();
                for(int i = 0; i < amethod.length; i++)
                    if(amethod[i].getName().equals("formatText"))
                        meth_formatText = amethod[i];
                    else
                    if(amethod[i].getName().equals("measureItem"))
                        meth_measureItem = amethod[i];

                fJavaTextPresent = true;
                return;
            }
            catch(Exception _ex)
            {
                enableFastFormatText = true;
            }
            return;
        } else
        {
            return;
        }
    }

    TextItem(char ac[], int i, int j)
    {
        isPseudoTextItem = false;
        fastFormatTextOK = true;
        measuredTextWidth = -1;
        doBlink = false;
        flexyTextClass = null;
        data = new char[j];
        length = j;
        System.arraycopy(ac, i, data, 0, j);
        checkFastFormatTextOK(ac);
    }

    public void setPseudoTextItem(boolean flag)
    {
        isPseudoTextItem = flag;
    }

    public int findLabel(String s)
    {
        if(s.length() > 0 && s.charAt(0) == '@')
        {
            int i = Integer.parseInt(s.substring(1));
            if(i-- <= 1)
                return super.index << 12;
            int j = length;
            for(int k = 0; k < j; k++)
                if(data[k] == '\r')
                {
                    if(k + 1 < j && data[k + 1] == '\n')
                        k++;
                    if(i-- <= 1)
                        return super.index << 12 | k;
                } else
                if(data[k] == '\n' && i-- <= 1)
                    return super.index << 12 | k;

        }
        return -1;
    }

    public boolean isText()
    {
        return true;
    }

    public String getText()
    {
        return new String(data, 0, length);
    }

    public int getLength()
    {
        return length;
    }

    public String getText(int i, int j)
    {
        if(i > j)
            return null;
        int k = i >> 12;
        int l = j >> 12;
        int i1 = getIndex();
        int j1 = 0;
        int k1 = 0;
        if(k > i1 || l < i1)
            return null;
        if(k == i1)
            j1 = i & 0xfff;
        else
            j1 = 0;
        if(l == i1)
            k1 = j & 0xfff;
        else
            k1 = length;
        return new String(data, j1, k1 - j1);
    }

    public int startOffset(Document document, int i)
    {
        if(i > length)
            i = length;
        while(i-- > 0) 
            switch(data[i])
            {
            case 10: // '\n'
            case 13: // '\r'
                return i + 1;
            }
        return super.index <= 0 || !document.items[super.index - 1].isBlock() || !document.items[super.index - 1].isEnd() ? -1 : 0;
    }

    public boolean keepInitialSpace(Formatter formatter, DocItem docitem)
    {
        return !docitem.isBlock() && !formatter.isFloater(docitem);
    }

    public boolean ignoreInitialSpace(Formatter formatter, DocItem docitem)
    {
        Document document = formatter.getDocument();
        TagItem tagitem = docitem.getTag(document);
        if(tagitem != null && tagitem.isPreformatted())
            return false;
        if(docitem.isBlock() || formatter.isFloater(docitem))
            return true;
        if(docitem.isEnd())
        {
            docitem = document.items[docitem.getIndex() + docitem.getOffset()];
            if(formatter.isFloater(docitem))
                return true;
        }
        return false;
    }

    public boolean format(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state == 2)
            return true;
        if(((TraversalState) (formatstate)).style.blinking)
            formatter.addBlinker(this);
        int i = ((TraversalState) (formatstate)).pos & 0x7ffff000;
        int j = ((TraversalState) (formatstate)).pos & 0xfff;
        DocStyle docstyle = ((TraversalState) (formatstate)).style;
        int k = formatstate.width;
        int l = formatstate.maxWidth;
        FontMetrics fontmetrics = docstyle.font.getFontMetrics(docstyle);
        if(j == 0 && data.length != 0 && data[j] == ' ' && docstyle.nobreak == 0)
        {
            Document document = formatter.getDocument();
            boolean flag = false;
            if(formatstate.width == 0)
            {
                flag = true;
            } else
            {
                for(int i1 = getIndex() - 1; i1 >= 0; i1--)
                {
                    DocItem docitem = document.items[i1];
                    if(docitem.isText())
                    {
                        TextItem textitem = (TextItem)docitem;
                        if(textitem.getText().length() == 0)
                            flag = true;
                        break;
                    }
                    if(ignoreInitialSpace(formatter, docitem))
                    {
                        flag = true;
                        break;
                    }
                    if(!keepInitialSpace(formatter, docitem))
                        continue;
                    flag = false;
                    break;
                }

            }
            if(flag)
                if(length > 1)
                {
                    char ac[] = new char[--length];
                    System.arraycopy(data, 1, ac, 0, length);
                    data = ac;
                } else
                {
                    length = 0;
                    if(docstyle.nobreak == 0)
                        formatter.setBreak(formatstate, formatstate1, j, k);
                    formatstate.pos = i + 4096;
                    return false;
                }
        }
        formatstate.state = 1;
        if(j < length)
        {
            formatstate.ascent = Math.max(formatstate.ascent, fontmetrics.getAscent());
            formatstate.descent = Math.max(formatstate.descent, fontmetrics.getDescent());
            formatstate.textAscent = Math.max(formatstate.textAscent, fontmetrics.getAscent());
        }
        superscriptAscent = (int)(0.40000000000000002D * (double)formatstate.textAscent);
        scriptDelta = (int)(0.20000000000000001D * (double)formatstate.textAscent);
        switch(docstyle.script)
        {
        case 6: // '\006'
            formatstate.ascent = Math.max(formatstate.ascent, (superscriptAscent + docstyle.superscriptLevel * scriptDelta + fontmetrics.getAscent()) - formatstate.above);
            break;

        case 7: // '\007'
            formatstate.descent = Math.max(formatstate.descent, (docstyle.subscriptLevel * scriptDelta + fontmetrics.getDescent()) - formatstate.below);
            break;
        }
        if(measuredTextWidth >= 0 && k + measuredTextWidth < l)
        {
            formatstate.width += measuredTextWidth;
            formatstate.pos = i + 4096;
            return false;
        }
        if(docstyle.nobreak != 0)
            return formatNonBreakText(formatter, i, j, formatstate, formatstate1, k, l, fontmetrics);
        else
            return formatText(formatter, i, j, formatstate, formatstate1, k, l, fontmetrics);
    }

    private boolean formatNonBreakText(Formatter formatter, int i, int j, FormatState formatstate, FormatState formatstate1, int k, int l, 
            FontMetrics fontmetrics)
    {
        int i1 = fontmetrics.charWidth('0') * 8;
        int j1 = k;
        int k1 = j;
        while(j < length) 
        {
            int l1 = data[j++] & 0xffff;
            switch(l1)
            {
            case 13: // '\r'
                if(j < length && data[j] == '\n')
                    j++;
                // fall through

            case 10: // '\n'
                formatstate.pos = i | j;
                formatstate.width = k;
                return true;

            case 9: // '\t'
                k += i1 - k % i1;
                break;

            case 160: 
                k += fontmetrics.charWidth(' ');
                break;

            default:
                k += fontmetrics.charWidth(l1);
                break;
            }
            if(k > l && j1 > 0 && ((TraversalState) (formatstate)).style.nobreak == 0)
            {
                formatter.setBreak(formatstate, formatstate1, k1, j1);
                formatstate.pos = i | k1;
                formatstate.width = j1;
                return true;
            }
        }
        formatstate.width = k;
        formatstate.pos = i + 4096;
        return false;
    }

    private boolean formatText(Formatter formatter, int i, int j, FormatState formatstate, FormatState formatstate1, int k, int l, 
            FontMetrics fontmetrics)
    {
        if(fastFormatTextOK)
            return fastFormatText(formatter, i, j, formatstate, formatstate1, k, l, fontmetrics);
        if(fJavaTextPresent && meth_formatText != null)
        {
            Boolean boolean1 = Boolean.FALSE;
            try
            {
                boolean1 = (Boolean)meth_formatText.invoke(null, new Object[] {
                    formatter, new Integer(i), new Integer(j), formatstate, formatstate1, new Integer(k), new Integer(l), fontmetrics, data, new Integer(length)
                });
            }
            catch(InvocationTargetException invocationtargetexception)
            {
                System.out.println("## invocation ex: " + invocationtargetexception.getTargetException());
            }
            catch(Exception exception)
            {
                System.out.println("## meth_formatText threw: " + exception);
            }
            return boolean1.booleanValue();
        } else
        {
            return false;
        }
    }

    private boolean fastFormatText(Formatter formatter, int i, int j, FormatState formatstate, FormatState formatstate1, int k, int l, 
            FontMetrics fontmetrics)
    {
        int i1 = fontmetrics.getMaxAdvance();
        if(i1 <= 0)
            i1 = l + 1;
        int k1;
        for(int j1 = j; j1 < data.length; j1 = k1)
        {
            boolean flag = false;
            if(k <= l)
            {
                int l1 = (l - k) / i1 + j1;
                if(l1 >= data.length)
                    l1 = data.length - 1;
                for(k1 = l1; k1 >= j1 && data[k1] != ' '; k1--);
                if(k1 >= j1)
                    flag = data[k1] == ' ';
                else
                    for(k1 = l1; k1 < data.length && data[k1] != ' '; k1++);
            } else
            {
                for(k1 = j1; k1 < data.length && data[k1] != ' '; k1++);
                if(k1 < data.length)
                    flag = data[k1] == ' ';
            }
            if(k1 < data.length)
                k1++;
            int i2 = fontmetrics.charsWidth(data, j1, k1 - j1);
            k += i2;
            if(k > l)
            {
                if(k == i2 || flag)
                    formatter.setBreak(formatstate, formatstate1, k1, k);
                formatstate.pos = i | k1;
                formatstate.width = k;
                return false;
            }
            if(k1 == data.length)
                break;
            formatter.setBreak(formatstate, formatstate1, k1, k);
        }

        formatstate.width = k;
        formatstate.pos = i + 4096;
        return false;
    }

    private boolean fastMeasureText(Formatter formatter, Measurement measurement, MeasureState measurestate)
    {
        int i = 0;
        int j = 0;
        FontMetrics fontmetrics = ((TraversalState) (measurestate)).style.getFontMetrics();
        int k = fontmetrics.charWidth(' ');
        int l;
        for(; i < data.length; i = l)
        {
            for(l = i; l < data.length && data[l] != ' '; l++);
            int i1 = fontmetrics.charsWidth(data, i, l - i);
            measurement.setMinWidth(i1);
            if(l < data.length)
            {
                l++;
                j += k;
            }
            j += i1;
        }

        measurement.setPreferredWidth(j);
        return false;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        return paint(false, formatter, g, i, j, docline);
    }

    public int print(Formatter formatter, Graphics g, int i, int j, DocLine docline, VBreakInfo vbreakinfo)
    {
        return paint(true, formatter, g, i, j, docline);
    }

    private int paint(boolean flag, Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        DocStyle docstyle = formatter.displayStyle;
        int k = formatter.displayPos;
        int l = k & 0xfff;
        int i1 = k >>> 12 != docline.end >>> 12 ? length : docline.end & 0xfff;
        int j1 = 0;
        if(length == 0)
        {
            formatter.displayPos = (formatter.displayPos & 0x7ffff000) + 4096;
            return j1;
        }
        Color color = docstyle.color;
        if(flag)
            color = Color.black;
        g.setColor(color);
        g.setFont(docstyle.font);
        j += docline.baseline;
        FontMetrics fontmetrics = docstyle.getFontMetrics();
        int k1 = fontmetrics.charWidth('0') * 8;
        switch(docstyle.script)
        {
        case 6: // '\006'
            if(docstyle.subscriptLevel != 0)
                j -= (docstyle.superscriptLevel - docstyle.subscriptLevel) * scriptDelta;
            else
                j -= superscriptAscent + (docstyle.superscriptLevel - docstyle.subscriptLevel) * scriptDelta;
            break;

        case 7: // '\007'
            if(docstyle.superscriptLevel != 0)
                j += (docstyle.subscriptLevel - docstyle.superscriptLevel) * scriptDelta - superscriptAscent;
            else
                j += (docstyle.subscriptLevel - docstyle.superscriptLevel) * scriptDelta;
            break;
        }
        if(docstyle.format == 2 || docstyle.nobreak != 0)
        {
            for(int l1 = l; l1 < i1; l1++)
            {
                char c = data[l1];
                if(c < ' ')
                {
                    if(!doBlink || flag)
                        g.drawChars(data, l, l1 - l, i + j1, j);
                    j1 += fontmetrics.charsWidth(data, l, l1 - l);
                    if(c == '\t')
                        j1 += k1 - ((i + j1) - (docline.margin >>> 16)) % k1;
                    l = l1 + 1;
                }
            }

            if(!doBlink || flag)
                g.drawChars(data, l, i1 - l, i + j1, j);
            j1 += fontmetrics.charsWidth(data, l, i1 - l);
        } else
        {
            if(!doBlink || flag)
                g.drawChars(data, l, i1 - l, i, j);
            j1 = fontmetrics.charsWidth(data, l, i1 - l);
        }
        if(docstyle.underline && (length != 1 || (data[0] & 0xff) != 32) && (!doBlink || flag))
            g.drawLine(i, j + 1, i + j1, j + 1);
        if(docstyle.strike && (!doBlink || flag))
            g.drawLine(i, j - fontmetrics.getAscent() / 2, i + j1, j - fontmetrics.getAscent() / 2);
        formatter.displayPos = (formatter.displayPos & 0x7ffff000) + 4096;
        return j1;
    }

    public int findX(DocLine docline, DocStyle docstyle, int i, int j, int k, Formatter formatter)
    {
        if(i >= k)
            return j;
        if(isPseudoTextItem)
            return -1;
        int l = j & 0x7ffff000;
        int i1 = j & 0xfff;
        FontMetrics fontmetrics = docstyle.getFontMetrics();
        int j1 = fontmetrics.charWidth('0') * 8;
        int i2 = 0;
        k -= i;
        while(i1 < length) 
        {
            int k1;
            int l1;
            switch(k1 = data[i1++] & 0xffff)
            {
            case 13: // '\r'
                if(i1 < length && data[i1] == '\n')
                    i1++;
                // fall through

            case 10: // '\n'
                return l | i1;

            case 9: // '\t'
                l1 = j1 - (i + i2) % j1;
                break;

            case 160: 
                l1 = fontmetrics.charWidth(' ');
                break;

            default:
                l1 = fontmetrics.charWidth(k1);
                break;
            }
            if(i2 + (l1 >> 1) >= k)
                return l | i1 - 1;
            i2 += l1;
            if(i2 >= k)
                return l | i1;
        }
        return -1 - i2;
    }

    public int getWidth(DocLine docline, DocStyle docstyle, int i, int j, int k)
    {
        if(k > length)
            k = length;
        if(j >= k)
            return 0;
        FontMetrics fontmetrics = docstyle.getFontMetrics();
        int l = fontmetrics.charWidth('0') * 8;
        int i1 = 0;
        int j1;
        while(j < k) 
            switch(j1 = data[j++] & 0xffff)
            {
            case 10: // '\n'
            case 13: // '\r'
                return i1;

            case 9: // '\t'
                i1 += l - (i + i1) % l;
                break;

            default:
                i1 += fontmetrics.charWidth(j1);
                break;
            }
        return i1;
    }

    public int insert(Document document, int i, char ac[], int j, int k)
    {
        if(length + k > 4095)
            return -1;
        if(j > length)
            j = length;
        if(length + k > data.length)
        {
            char ac1[] = new char[Math.max(data.length * 2, length + k + 10)];
            System.arraycopy(data, 0, ac1, 0, length);
            data = ac1;
        }
        int l = i & 0xfff;
        if(l > length)
            l = length;
        System.arraycopy(data, l, data, l + k, length - l);
        System.arraycopy(ac, j, data, l, k);
        length += k;
        checkFastFormatTextOK(ac);
        return (i & 0x7ffff000) + l + k;
    }

    public DocItem split(Document document, int i)
    {
        int j = i & 0xfff;
        if(j < length)
        {
            TextItem textitem = new TextItem(data, j, length - j);
            length = j;
            return textitem;
        } else
        {
            return null;
        }
    }

    public void delete(Document document, int i, int j)
    {
        int k = i & 0xfff;
        if(k + j > length)
            j = length - k;
        if(j <= 0)
        {
            return;
        } else
        {
            length -= j;
            System.arraycopy(data, k + j, data, k, length - k);
            return;
        }
    }

    public int find(String s, int i, boolean flag, OverlappingStringMatch overlappingstringmatch)
    {
        if(flag)
            return findOverlappingIgnoreCase(s, i, overlappingstringmatch);
        else
            return findOverlapping(s, i, overlappingstringmatch);
    }

    int findOverlapping(String s, int i, OverlappingStringMatch overlappingstringmatch)
    {
        int j = s.length();
        char c = s.charAt(0);
        boolean flag = false;
label0:
        for(; i < length; i++)
        {
            if(c != data[i])
                continue;
            int k;
            for(k = 1; k < j && k + i < length; k++)
                if(s.charAt(k) != data[i + k])
                    continue label0;

            overlappingstringmatch.overlappingMatch = s.substring(0, k);
            overlappingstringmatch.offsetIntoFirstString = i;
            overlappingstringmatch.matchLength = k;
            return i;
        }

        return -1;
    }

    int findOverlappingIgnoreCase(String s, int i, OverlappingStringMatch overlappingstringmatch)
    {
        String s1 = s.toLowerCase();
        int j = s1.length();
        char c = s1.charAt(0);
        boolean flag = false;
label0:
        for(; i < length; i++)
        {
            if(c != Character.toLowerCase(data[i]))
                continue;
            int k;
            for(k = 1; k < j && k + i < length; k++)
                if(s1.charAt(k) != Character.toLowerCase(data[i + k]))
                    continue label0;

            overlappingstringmatch.overlappingMatch = s.substring(0, k);
            overlappingstringmatch.offsetIntoFirstString = i;
            overlappingstringmatch.matchLength = k;
            return i;
        }

        return -1;
    }

    public boolean measureItem(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        int i = ((TraversalState) (measurestate)).pos & 0xfff;
        if(formatstate.state == 2)
            return true;
        if(isEmpty())
        {
            measurestate.pos += 4096;
            return false;
        }
        formatstate.state = 1;
        FontMetrics fontmetrics = ((TraversalState) (measurestate)).style.getFontMetrics();
        if(((TraversalState) (measurestate)).style.nobreak != 0)
        {
            boolean flag = measureNonBreakItem(formatstate, fontmetrics, i, measurement, measurestate);
            if(!flag)
                measuredTextWidth = measurement.getPreferredWidth();
            return flag;
        }
        if(fastFormatTextOK)
        {
            fastMeasureText(formatter, measurement, measurestate);
        } else
        {
            try
            {
                Boolean boolean1 = (Boolean)meth_measureItem.invoke(null, new Object[] {
                    formatter, formatstate, measurement, measurestate, data, new Integer(length)
                });
                boolean flag1 = boolean1.booleanValue();
                if(!flag1)
                    measuredTextWidth = measurement.getPreferredWidth();
                return flag1;
            }
            catch(Exception exception)
            {
                System.out.println("## meth_measureItem threw: " + exception);
            }
            return false;
        }
        measuredTextWidth = measurement.getPreferredWidth();
        measurestate.pos += 4096;
        return false;
    }

    private boolean measureNonBreakItem(FormatState formatstate, FontMetrics fontmetrics, int i, Measurement measurement, MeasureState measurestate)
    {
        int j = 0;
        int k = fontmetrics.charWidth('0') * 8;
        while(i < length) 
        {
            int l = data[i++] & 0xffff;
            switch(l)
            {
            case 13: // '\r'
                if(i < length && data[i] == '\n')
                    i++;
                // fall through

            case 10: // '\n'
                measurestate.pos = ((TraversalState) (measurestate)).pos & 0x7ffff000 | i;
                measurement.setMinWidth(j);
                measurement.setPreferredWidth(j);
                return true;

            case 9: // '\t'
                j += k - j % k;
                break;

            case 160: 
                j += fontmetrics.charWidth(' ');
                break;

            default:
                j += fontmetrics.charWidth(l);
                break;
            }
        }
        measurement.setMinWidth(j);
        measurement.setPreferredWidth(j);
        measurestate.pos = getIndex() + 1 << 12;
        return false;
    }

    public String toString()
    {
        return "\"" + new String(data, 0, length) + "\"";
    }

    private void checkFastFormatTextOK(char ac[])
    {
        if(!fastFormatTextOK)
            return;
        if(fJavaTextPresent)
        {
            for(int i = 0; i < ac.length; i++)
                if(ac[i] >= '\u05FF')
                {
                    fastFormatTextOK = false;
                    return;
                }

        }
    }

    public void invert()
    {
        doBlink = !doBlink;
    }

    public boolean noblink()
    {
        boolean flag = doBlink;
        doBlink = false;
        return flag;
    }

    private boolean isEmpty()
    {
        for(int i = 0; i < length; i++)
            if(data[i] != ' ')
                return false;

        return true;
    }

    char data[];
    int length;
    private boolean isPseudoTextItem;
    private static final float brightWhiteThreshold = 0.95F;
    private static final float satWhiteThreshold = 0.05F;
    private static final float hueWhiteThreshold = 0.05F;
    private static boolean enableFastFormatText;
    private static boolean fJavaTextPresent;
    private boolean fastFormatTextOK;
    static Class flexyTextClass;
    static Method meth_formatText;
    static Method meth_measureItem;
    int superscriptAscent;
    int scriptDelta;
    static float hsb[];
    private int measuredTextWidth;
    boolean doBlink;
}
