// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StyleSheet.java

package sunw.hotjava.doc;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;
import sunw.html.Element;

// Referenced classes of package sunw.hotjava.doc:
//            DocConstants, DocFont, DocStyle

public class StyleSheet
    implements DocConstants, Serializable
{

    StyleSheet()
    {
    }

    StyleSheet(Element element)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        elem = element;
        element.data = this;
        String s = element.getName();
        type = hjbproperties.getProperty(s + ".type", "unknown");
        fontName = hjbproperties.getProperty(s + ".font");
        label = hjbproperties.getProperty(s + ".label");
        if(label == null)
        {
            label = element.getName();
            if(label.length() > 3)
                label = label.substring(0, 3);
        }
        String s1 = hjbproperties.getProperty(s + ".size");
        if(s1 != null)
            if(s1.startsWith("+"))
                relSize = Integer.valueOf(s1.substring(1)).intValue();
            else
            if(s1.startsWith("-"))
                relSize = -Integer.valueOf(s1.substring(1)).intValue();
            else
                absSize = Integer.valueOf(s1).intValue();
        String s2 = hjbproperties.getProperty(s + ".style");
        if(s2 != null)
            if(s2.equals("bold"))
                fontStyle = 1;
            else
            if(s2.equals("italic"))
                fontStyle = 2;
            else
            if(s2.equals("bolditalic"))
                fontStyle = 3;
            else
            if(s2.equals("typewriter"))
                fontStyle = 4;
            else
            if(s2.equals("underline"))
                fontStyle = 5;
            else
            if(s2.equals("strike"))
                fontStyle = 8;
            else
            if(s2.equals("superscript"))
                fontStyle = 6;
            else
            if(s2.equals("subscript"))
                fontStyle = 7;
        nobreak = hjbproperties.getBoolean(s + ".nobreak");
        color = hjbproperties.getColor(s + ".color", null);
        above = hjbproperties.getInteger(s + ".above", 0);
        below = hjbproperties.getInteger(s + ".below", 0);
        left = hjbproperties.getInteger(s + ".left", 0);
        right = hjbproperties.getInteger(s + ".right", 0);
        format = -1;
        String s3 = hjbproperties.getProperty(s + ".format");
        if(s3 != null)
        {
            if(s3.equals("left"))
            {
                format = 0;
                return;
            }
            if(s3.equals("right"))
            {
                format = 1;
                return;
            }
            if(s3.equals("center"))
            {
                format = 3;
                return;
            }
            if(s3.equals("none"))
            {
                format = 2;
                return;
            }
            if(s3.equals("justified"))
                format = 0;
        }
    }

    public void apply(DocStyle docstyle)
    {
        DocFont docfont = docstyle.font;
        if(fontName != null)
            docfont = docfont.getName(fontName);
        if(absSize != 0)
            docfont = docfont.getIndex(absSize);
        else
        if(relSize != 0)
            docfont = docfont.getBigger(relSize);
        switch(fontStyle)
        {
        case 1: // '\001'
            docfont = docfont.getBold();
            break;

        case 2: // '\002'
            docfont = docfont.getItalic();
            break;

        case 3: // '\003'
            docfont = docfont.getItalic().getBold();
            break;

        case 4: // '\004'
            docfont = docfont.getTypewriter();
            break;

        case 5: // '\005'
            docstyle.underline = true;
            break;

        case 8: // '\b'
            docstyle.strike = true;
            break;

        case 6: // '\006'
            docstyle.script = fontStyle;
            docstyle.superscriptLevel++;
            docfont = docfont.getSmaller(2);
            break;

        case 7: // '\007'
            docstyle.script = fontStyle;
            docstyle.subscriptLevel++;
            docfont = docfont.getSmaller(2);
            break;
        }
        if(nobreak)
            docstyle.nobreak = 1;
        if(color != null)
            docstyle.color = color;
        if(format >= 0)
            docstyle.format = format;
        if(docstyle.font != docfont)
            docstyle.font = docfont;
    }

    public int getAbove(DocStyle docstyle)
    {
        return scalePercentageByFontSize(above, docstyle);
    }

    public int getBelow(DocStyle docstyle)
    {
        return scalePercentageByFontSize(below, docstyle);
    }

    private int scalePercentageByFontSize(int i, DocStyle docstyle)
    {
        int j = 10;
        try
        {
            j = docstyle.font.getSize();
        }
        catch(Exception _ex) { }
        return (int)((float)(i * j) / 100F);
    }

    public String getLabel()
    {
        return label;
    }

    public static StyleSheet getStyleSheet(Element element)
    {
        if(element.data != null)
            return (StyleSheet)element.data;
        else
            return new StyleSheet(element);
    }

    static final int BOLD = 1;
    static final int ITALIC = 2;
    static final int BOLDITALIC = 3;
    static final int TYPEWRITER = 4;
    static final int UNDERLINE = 5;
    static final int SUPERSCRIPT = 6;
    static final int SUBSCRIPT = 7;
    static final int STRIKE = 8;
    static final long serialVersionUID = 0x8b4e1f386225a903L;
    Element elem;
    public String type;
    public String fontName;
    public String label;
    public int fontStyle;
    public int absSize;
    public int relSize;
    public Color color;
    public boolean nobreak;
    public int left;
    public int right;
    private int above;
    private int below;
    private int format;
}
