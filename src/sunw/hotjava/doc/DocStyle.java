// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocStyle.java

package sunw.hotjava.doc;

import java.awt.*;

// Referenced classes of package sunw.hotjava.doc:
//            DocConstants, DocFont, DocItem, Formatter, 
//            TagItem, DocumentFormatter, Document

public class DocStyle
    implements Cloneable
{

    public DocStyle(DocFont docfont)
    {
        color = Color.black;
        left = 20;
        right = 20;
        format = 2;
        font = docfont;
        compact = false;
        blinking = false;
    }

    public DocStyle push(DocItem docitem)
    {
        DocStyle docstyle = (DocStyle)clone();
        docstyle.tag = (TagItem)docitem;
        docstyle.next = this;
        if(docitem != null)
            docitem.modifyStyle(docstyle);
        return docstyle;
    }

    public FontMetrics getFontMetrics()
    {
        return font.getFontMetrics(this);
    }

    public int adjustFontSize(int i)
    {
        return i + (win.getDocFont().getIndex() - 5);
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch(CloneNotSupportedException _ex)
        {
            throw new InternalError();
        }
    }

    public String toString()
    {
        return font + "/" + color + "/" + tag;
    }

    public DocumentFormatter win;
    public Document doc;
    public TagItem tag;
    public DocStyle next;
    public int left;
    public int right;
    public int ascent;
    public int descent;
    public int format;
    public DocFont font;
    public Graphics nonScreenGraphics;
    public Color color;
    public int nobreak;
    public boolean underline;
    public boolean strike;
    public int script;
    public int subscriptLevel;
    public int superscriptLevel;
    public String href;
    public boolean blinking;
    public boolean compact;
    public static final int DEFAULT_FONT_SIZE = 5;
}
