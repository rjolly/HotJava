// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FormatState.java

package sunw.hotjava.doc;


// Referenced classes of package sunw.hotjava.doc:
//            TraversalState, DocLine

public class FormatState extends TraversalState
{

    public void copyFrom(FormatState formatstate)
    {
        super.style = ((TraversalState) (formatstate)).style;
        super.pos = ((TraversalState) (formatstate)).pos;
        startPos = formatstate.startPos;
        width = formatstate.width;
        maxWidth = formatstate.maxWidth;
        margin = formatstate.margin;
        ascent = formatstate.ascent;
        descent = formatstate.descent;
        above = formatstate.above;
        below = formatstate.below;
        y = formatstate.y;
        state = formatstate.state;
        format = formatstate.format;
        textAscent = formatstate.textAscent;
        isPrinting = formatstate.isPrinting;
    }

    public FormatState()
    {
    }

    public int startPos;
    public int width;
    public int maxWidth;
    public int margin;
    public int ascent;
    public int descent;
    public int above;
    public int below;
    public int y;
    public int state;
    public int format;
    public int textAscent;
    public DocLine prevLine;
    public int totalHeight;
    public boolean isPrinting;
}
