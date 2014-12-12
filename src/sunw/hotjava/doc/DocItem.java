// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocItem.java

package sunw.hotjava.doc;

import java.awt.Component;
import java.awt.Graphics;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Vector;
import sunw.html.HTMLOutputWriter;

// Referenced classes of package sunw.hotjava.doc:
//            DocConstants, DocLine, FloaterInfo, Formatter, 
//            ItemIterator, TraversalState, Document, TagItem, 
//            DocStyle, FormatState, VBreakInfo, FormatterVBreakInfo, 
//            NamedLink, OverlappingStringMatch, Measurement, MeasureState, 
//            DocItemVisitor

public class DocItem
    implements DocConstants, Serializable
{

    public DocItem()
    {
        index = -1;
    }

    public TagItem getTag(Document document)
    {
        return null;
    }

    public int getOffset()
    {
        return offset;
    }

    protected int getActivateIncrement()
    {
        return 1;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int i, Document document)
    {
        index = i;
    }

    public boolean isStart()
    {
        return false;
    }

    public boolean isEnd()
    {
        return false;
    }

    public boolean isText()
    {
        return false;
    }

    public boolean isBlock()
    {
        return false;
    }

    public boolean isMappable()
    {
        return false;
    }

    public String getMapAltText(DocLine docline, DocStyle docstyle, int i, int j)
    {
        return null;
    }

    public URL getImageURL()
    {
        return null;
    }

    public boolean needsLoading()
    {
        return false;
    }

    public void load(Formatter formatter)
    {
    }

    public boolean needsActivation()
    {
        return false;
    }

    public boolean activate(Formatter formatter, Document document)
    {
        return false;
    }

    public void deactivate(Formatter formatter)
    {
    }

    public int findLabel(String s)
    {
        return -1;
    }

    public String getText()
    {
        return null;
    }

    public String getType()
    {
        return "unknown";
    }

    public boolean isType(String s)
    {
        return getType().equals(s);
    }

    public void modifyStyle(DocStyle docstyle)
    {
    }

    public DocStyle modifyStyleInPlace(DocStyle docstyle)
    {
        return docstyle;
    }

    public boolean modifiesStyleInPlace()
    {
        return false;
    }

    public void write(HTMLOutputWriter htmloutputwriter)
        throws IOException
    {
    }

    public void writeStartTag(HTMLOutputWriter htmloutputwriter)
        throws IOException
    {
    }

    public void writeEndTag(HTMLOutputWriter htmloutputwriter)
        throws IOException
    {
    }

    public int startOffset(Document document, int i)
    {
        return -1;
    }

    public boolean format(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        return false;
    }

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        return false;
    }

    public boolean formatEndTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        return false;
    }

    public Component createView(Formatter formatter, Document document)
    {
        return null;
    }

    public int findSplitY(Formatter formatter, FloaterInfo floaterinfo, int i, int j, VBreakInfo vbreakinfo)
    {
        if(floaterinfo.height > j)
            return i;
        else
            return floaterinfo.startY;
    }

    public int findSplitY(Formatter formatter, ItemIterator itemiterator, DocStyle docstyle, int i, int j, DocLine docline, VBreakInfo vbreakinfo)
    {
        if(itemiterator.getOffset() < 0)
        {
            itemiterator.skipItem();
            return i;
        }
        itemiterator.skipItem();
        if(docline.height > j)
            return i;
        else
            return docline.y;
    }

    public void recordBreakInfo(Formatter formatter, FloaterInfo floaterinfo, int i, int j, FormatterVBreakInfo formattervbreakinfo, FormatterVBreakInfo formattervbreakinfo1)
    {
    }

    public void recordBreakInfo(Formatter formatter, ItemIterator itemiterator, DocStyle docstyle, int i, int j, DocLine docline, FormatterVBreakInfo formattervbreakinfo, 
            FormatterVBreakInfo formattervbreakinfo1)
    {
        itemiterator.skipItem();
    }

    public int print(Formatter formatter, Graphics g, int i, int j, DocLine docline, VBreakInfo vbreakinfo)
    {
        int k = paint(formatter, g, i, j, docline);
        return k;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline, boolean flag)
    {
        return paint(formatter, g, i, j, docline);
    }

    protected int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        formatter.displayPos += 4096;
        return 0;
    }

    public int findX(DocLine docline, DocStyle docstyle, int i, int j, int k, Formatter formatter)
    {
        return -1;
    }

    public int findStartTagX(DocLine docline, DocStyle docstyle, int i, int j, int k, Formatter formatter)
    {
        return -1;
    }

    public int findEndTagX(DocLine docline, DocStyle docstyle, int i, int j, int k, Formatter formatter)
    {
        return -1;
    }

    public int getWidth(DocLine docline, DocStyle docstyle, int i, int j, int k)
    {
        return 0;
    }

    public int getWidth(Formatter formatter, DocStyle docstyle)
    {
        return 0;
    }

    public int getAscent(Formatter formatter, FormatState formatstate)
    {
        return 0;
    }

    public int getDescent(Formatter formatter, FormatState formatstate)
    {
        return 0;
    }

    public int getStartTagWidth(DocLine docline, DocStyle docstyle, int i, int j, int k)
    {
        return 0;
    }

    public int getEndTagWidth(DocLine docline, DocStyle docstyle, int i, int j, int k)
    {
        return 0;
    }

    public NamedLink map(DocLine docline, DocStyle docstyle, String s, int i, int j)
    {
        return null;
    }

    public int insert(Document document, int i, char ac[], int j, int k)
    {
        return i;
    }

    public DocItem split(Document document, int i)
    {
        return null;
    }

    public void delete(Document document, int i, int j)
    {
    }

    public int find(String s, int i)
    {
        return -1;
    }

    public int find(String s, int i, boolean flag)
    {
        return -1;
    }

    public int find(String s, int i, boolean flag, OverlappingStringMatch overlappingstringmatch)
    {
        return -1;
    }

    public int find(String s, int i, OverlappingStringMatch overlappingstringmatch)
    {
        return -1;
    }

    public int getFormPanel(Formatter formatter, Vector vector)
    {
        return 1;
    }

    public void addFormElement(TagItem tagitem)
    {
    }

    public void setFormParent(TagItem tagitem)
    {
    }

    public TagItem getFormParent()
    {
        return null;
    }

    public void flush()
    {
    }

    public boolean measureStartTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        measurestate.pos += 4096;
        return false;
    }

    public boolean measureEndTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        measurestate.pos += 4096;
        return false;
    }

    public boolean measureItem(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        measurestate.pos += 4096;
        return false;
    }

    public String toString()
    {
        return "index=" + index + ",off=" + offset;
    }

    public boolean visit(DocItemVisitor docitemvisitor)
    {
        return true;
    }

    static final long serialVersionUID = 0x9d3c7ec7a8c31995L;
    int offset;
    int index;
}
