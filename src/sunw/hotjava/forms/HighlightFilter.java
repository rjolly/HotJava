// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HighlightFilter.java

package sunw.hotjava.forms;

import java.awt.image.RGBImageFilter;

public class HighlightFilter extends RGBImageFilter
{

    public HighlightFilter(boolean flag, int i)
    {
        brighter = flag;
        percent = i;
        super.canFilterIndexColorModel = true;
    }

    public int filterRGB(int i, int j, int k)
    {
        int l = k >> 16 & 0xff;
        int i1 = k >> 8 & 0xff;
        int j1 = k & 0xff;
        if(brighter)
        {
            l = 255 - ((255 - l) * (100 - percent)) / 100;
            i1 = 255 - ((255 - i1) * (100 - percent)) / 100;
            j1 = 255 - ((255 - j1) * (100 - percent)) / 100;
        } else
        {
            l = (l * (100 - percent)) / 100;
            i1 = (i1 * (100 - percent)) / 100;
            j1 = (j1 * (100 - percent)) / 100;
        }
        if(l < 0)
            l = 0;
        if(l > 255)
            l = 255;
        if(i1 < 0)
            i1 = 0;
        if(i1 > 255)
            i1 = 255;
        if(j1 < 0)
            j1 = 0;
        if(j1 > 255)
            j1 = 255;
        return k & 0xff000000 | l << 16 | i1 << 8 | j1;
    }

    boolean brighter;
    int percent;
}
