// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BGColorInfo.java

package sunw.hotjava.tables;

import java.awt.Color;

class BGColorInfo
{

    BGColorInfo(Color color1)
    {
        isSet = false;
        defaultBGColor = color1;
    }

    BGColorInfo(BGColorInfo bgcolorinfo)
    {
        isSet = false;
        if(bgcolorinfo.isSet())
            setColor(bgcolorinfo.getColor());
        defaultBGColor = bgcolorinfo.getDefaultColor();
    }

    Color getColor()
    {
        return color;
    }

    Color getDefaultColor()
    {
        return defaultBGColor;
    }

    void setColor(Color color1)
    {
        isSet = true;
        color = color1;
    }

    boolean isSet()
    {
        return isSet;
    }

    private boolean isSet;
    private Color color;
    private Color defaultBGColor;
}
