// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocFont.java

package sunw.hotjava.doc;

import java.awt.*;
import java.util.StringTokenizer;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.doc:
//            DocStyle

public final class DocFont extends Font
{

    private static synchronized int[] initSharedSizeMap(int i, boolean flag)
    {
        if(sharedSizeMaps[i] == null)
        {
            int ai[] = new int[32];
            String s = flag ? "printfonts.sizes" : "fonts.sizes";
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            StringTokenizer stringtokenizer = new StringTokenizer(hjbproperties.getProperty(s), ", \t\n");
            int j;
            for(j = 0; j < ai.length && stringtokenizer.hasMoreTokens();)
                ai[j++] = Integer.valueOf(stringtokenizer.nextToken()).intValue();

            sharedSizeMaps[i] = new int[j];
            System.arraycopy(ai, 0, sharedSizeMaps[i], 0, j);
        }
        return sharedSizeMaps[i];
    }

    private static int getInitialSize(int i, boolean flag)
    {
        int j = flag ? 1 : 0;
        int ai[];
        if(sharedSizeMaps[j] != null)
            ai = sharedSizeMaps[j];
        else
            ai = initSharedSizeMap(j, flag);
        return ai[i];
    }

    public static DocFont findFont(boolean flag, int i, int j)
    {
        String s = flag ? "hotjava.printfont" : "hotjava.docfont";
        String s1 = HJBProperties.getHJBProperties("beanPropertiesKey").getProperty(s);
        return getFont(s1, i, j, flag);
    }

    private DocFont(String s, int i, int j, boolean flag)
    {
        super(s, i, getInitialSize(j, flag));
        forPrinting = flag;
        index = j;
        int k = flag ? 1 : 0;
        sizeMap = sharedSizeMaps[k];
        sizes = new DocFont[sizeMap.length];
    }

    private static synchronized DocFont getFont(String s, int i, int j, boolean flag)
    {
        for(int k = 0; k < arrayCount; k++)
            if(fontArray[k].index == j && fontArray[k].getStyle() == i && fontArray[k].forPrinting == flag && s.equalsIgnoreCase(fontArray[k].getName()))
                return fontArray[k];

        if(arrayCount == 300)
            return new DocFont(s, i, j, flag);
        if(arrayCount == fontArray.length)
        {
            DocFont adocfont[] = new DocFont[fontArray.length + 100];
            if(fontArray != null)
                System.arraycopy(fontArray, 0, adocfont, 0, arrayCount);
            fontArray = adocfont;
        }
        return fontArray[arrayCount++] = new DocFont(s, i, j, flag);
    }

    public DocFont getName(String s)
    {
        return getFont(s, super.style, index, forPrinting);
    }

    public DocFont getBold()
    {
        if(bold == null)
            bold = getFont(super.name, super.style | 1, index, forPrinting);
        return bold;
    }

    public DocFont getItalic()
    {
        if(italic == null)
            italic = getFont(super.name, super.style | 2, index, forPrinting);
        return italic;
    }

    public DocFont getTypewriter()
    {
        if(typewriter == null)
            typewriter = getFont("Monospaced", super.style, index, forPrinting);
        return typewriter;
    }

    public DocFont getIndex(int i)
    {
        if(i < 0)
            i = 0;
        else
        if(i >= sizeMap.length)
            i = sizeMap.length - 1;
        if(sizes[i] == null)
            sizes[i] = getFont(super.name, super.style, i, forPrinting);
        return sizes[i];
    }

    public DocFont getBigger(int i)
    {
        return getIndex(index + i);
    }

    public DocFont getSmaller(int i)
    {
        return getIndex(index - i);
    }

    public int getIndex()
    {
        return index;
    }

    public FontMetrics getFontMetrics(DocStyle docstyle)
    {
        if(docstyle != null && docstyle.nonScreenGraphics != null)
            return docstyle.nonScreenGraphics.getFontMetrics(this);
        if(metrics == null)
            metrics = Globals.getFontMetrics(this);
        return metrics;
    }

    int index;
    boolean forPrinting;
    private DocFont typewriter;
    private DocFont bold;
    private DocFont italic;
    private FontMetrics metrics;
    private DocFont sizes[];
    private int sizeMap[];
    private static int sharedSizeMaps[][] = new int[2][];
    private static final int maxSize = 300;
    private static final int chunkSize = 100;
    private static int arrayCount;
    private static DocFont fontArray[] = new DocFont[100];

}
