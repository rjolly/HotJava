// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TagItem.java

package sunw.hotjava.doc;

import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import sunw.html.*;

// Referenced classes of package sunw.hotjava.doc:
//            DocItem, DocConstants, Formatter, StyleSheet, 
//            Document, DocStyle, DocLine

public class TagItem extends DocItem
    implements Tag
{

    public TagItem()
    {
        hasJavaScript = false;
        checkedJavaScript = false;
    }

    public TagItem getTag(Document document)
    {
        return this;
    }

    public Font getFont()
    {
        if(style.fontName != null)
            return Font.decode(style.fontName);
        else
            return null;
    }

    public Element getElement()
    {
        return style.elem;
    }

    public Attributes getAttributes()
    {
        return atts;
    }

    public String getAttribute(String s)
    {
        if(atts != null)
            return atts.get(s);
        else
            return null;
    }

    public void setAttribute(String s, String s1)
    {
        if(atts == null)
            atts = new Attributes();
        atts.put(s, s1);
    }

    public String getType()
    {
        return style.type;
    }

    public boolean isStart()
    {
        return super.offset > 0;
    }

    public boolean isPreformatted()
    {
        return false;
    }

    public void init(Document document)
    {
    }

    public String getName()
    {
        return style.elem.getName();
    }

    public void modifyStyle(DocStyle docstyle)
    {
        style.apply(docstyle);
    }

    public String getLinkTarget()
    {
        String s = null;
        Attributes attributes = getAttributes();
        if(attributes != null && attributes.get("target") != null)
            s = attributes.get("target").toLowerCase();
        return s;
    }

    public void write(HTMLOutputWriter htmloutputwriter)
        throws IOException
    {
        htmloutputwriter.emptyTag(this);
    }

    public void writeStartTag(HTMLOutputWriter htmloutputwriter)
        throws IOException
    {
        htmloutputwriter.startTag(this);
    }

    public void writeEndTag(HTMLOutputWriter htmloutputwriter)
        throws IOException
    {
        htmloutputwriter.endTag(this);
    }

    public int paintEndTag(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        formatter.displayPos += 4096;
        return 0;
    }

    protected static final int parseInt(Attributes attributes, String s, int i, int j)
    {
        String s1 = attributes.get(s);
        if(s1 != null)
            try
            {
                return Math.max(Integer.parseInt(s1), i);
            }
            catch(NumberFormatException _ex) { }
        return j;
    }

    public String tagString()
    {
        if(isStart())
            return "<" + elementString(false) + ">";
        if(isEnd())
            return "</" + elementString(true) + ">";
        else
            return "[" + elementString(false) + "]";
    }

    protected String elementString(boolean flag)
    {
        return getName();
    }

    public String toString()
    {
        return tagString() + ", " + super.toString();
    }

    public String filterString(String s)
    {
        if(s == null)
            return null;
        int i = s.length();
        int j = 0;
        char ac[] = new char[i];
        char ac1[] = new char[i];
        s.getChars(0, i, ac1, 0);
        for(int k = 0; k < i; k++)
            if(ac1[k] > ' ')
                ac[j++] = ac1[k];

        return new String(ac, 0, j);
    }

    public boolean hasJavaScriptHandlers()
    {
        if(!checkedJavaScript)
        {
            checkedJavaScript = true;
            hasJavaScript = determineIfTagHasHandlers();
        }
        return hasJavaScript;
    }

    protected boolean determineIfTagHasHandlers()
    {
        Attributes attributes = getAttributes();
        if(attributes == null || handlerStrings == null)
            return false;
        for(int i = 0; i < handlerStrings.length; i++)
            if(attributes.get(handlerStrings[i]) != null)
                return true;

        return false;
    }

    protected StyleSheet style;
    protected Attributes atts;
    private boolean hasJavaScript;
    private boolean checkedJavaScript;
    protected String handlerStrings[];
}
