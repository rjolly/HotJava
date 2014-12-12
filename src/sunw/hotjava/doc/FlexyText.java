// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FlexyText.java

package sunw.hotjava.doc;

import java.awt.FontMetrics;
import java.text.BreakIterator;

// Referenced classes of package sunw.hotjava.doc:
//            DocConstants, DocStyle, FormatState, Formatter, 
//            Measurement, TextCharIterator, TraversalState, MeasureState

public class FlexyText
{

    public static boolean formatText(Formatter formatter, int i, int j, FormatState formatstate, FormatState formatstate1, int k, int l, FontMetrics fontmetrics, 
            char ac[], int i1)
    {
        BreakIterator breakiterator = BreakIterator.getLineInstance();
        TextCharIterator textchariterator = new TextCharIterator(ac, j);
        breakiterator.setText(textchariterator);
        int j1 = j;
        for(int k1 = breakiterator.first(); k1 != -1; k1 = breakiterator.next())
        {
            int l1 = fontmetrics.charsWidth(ac, j1, k1 - j1);
            k += l1;
            if(k > l || k1 > j1)
            {
                formatter.setBreak(formatstate, formatstate1, j1, k - l1);
                formatstate.pos = i | k1;
                formatstate.width = k;
                return false;
            }
            j1 = k1;
        }

        formatstate.width = k;
        formatstate.pos = i + 4096;
        return false;
    }

    public static boolean measureItem(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate, char ac[], int i)
    {
        FontMetrics fontmetrics = ((TraversalState) (measurestate)).style.getFontMetrics();
        int j = 0;
        BreakIterator breakiterator = BreakIterator.getLineInstance();
        TextCharIterator textchariterator = new TextCharIterator(ac, 0);
        breakiterator.setText(textchariterator);
        int l;
        for(int k = 0; (l = breakiterator.next()) != -1; k = l)
        {
            int i1 = fontmetrics.charsWidth(ac, k, l - k);
            measurement.setMinWidth(i1);
            j += i1;
        }

        measurement.setPreferredWidth(j);
        measurestate.pos += 4096;
        return false;
    }

    public FlexyText()
    {
    }
}
