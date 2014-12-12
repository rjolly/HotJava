// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HJColor.java

package sunw.hotjava.ui;

import java.awt.Color;

public class HJColor extends Color
{

    public HJColor(Color color)
    {
        super(color.getRGB());
    }

    public HJColor(int i, int j, int k)
    {
        super(i, j, k);
    }

    public Color brighter(float f)
    {
        int l = (int)(255F * f);
        int i;
        int j;
        int k;
        if(l < 0)
        {
            i = Math.max(getRed() + l, 0);
            j = Math.max(getGreen() + l, 0);
            k = Math.max(getBlue() + l, 0);
        } else
        {
            i = Math.min(getRed() + l, 255);
            j = Math.min(getGreen() + l, 255);
            k = Math.min(getBlue() + l, 255);
        }
        return new HJColor(i, j, k);
    }

    public Color brighter()
    {
        Color color = brighter(0.3F);
        return color;
    }

    public Color darker()
    {
        Color color = brighter(-0.3F);
        return color;
    }

    public boolean similar(Color color)
    {
        int i = Math.abs(getRed() - color.getRed());
        int j = Math.abs(getGreen() - color.getGreen());
        int k = Math.abs(getBlue() - color.getBlue());
        return i + j + k < 45;
    }
}
