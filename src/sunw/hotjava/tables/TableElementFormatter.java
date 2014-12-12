// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TableElementFormatter.java

package sunw.hotjava.tables;

import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Vector;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.Length;
import sunw.hotjava.tags.Align;

// Referenced classes of package sunw.hotjava.tables:
//            BGColorInfo, TD, TableElementPanel, TablePanel

class TableElementFormatter extends Formatter
    implements Serializable
{
    private final class FormatterMouseListener extends MouseAdapter
        implements MouseMotionListener, Serializable
    {

        public void mouseExited(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mousePressed(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mouseReleased(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mouseDragged(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mouseMoved(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public FormatterMouseListener()
        {
        }
    }


    TableElementFormatter()
    {
        valid = false;
    }

    TableElementFormatter(TableElementPanel tableelementpanel, DocumentState documentstate, Document document, DocItem docitem, DocItem docitem1, DocItem docitem2, Length length, 
            Length length1, int i, int j, int k, BGColorInfo bgcolorinfo)
    {
        valid = false;
        super.lines = new DocLine[1];
        super.nlines = 0;
        super.lines[0] = new DocLine(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        parent = tableelementpanel;
        super.ds = documentstate;
        super.doc = document;
        startItem = docitem;
        myTR = docitem1;
        myTABLE = docitem2;
        cellWidth = length;
        cellHeight = length1;
        vAlign = i;
        colSpan = (byte)j;
        rowSpan = (byte)k;
        super.marginHeight = parent.getCellPadding();
        startIndex = docitem.getIndex() + 1;
        TagItem tagitem = docitem.getTag(document);
        tagitem.getAttributes();
        if(bgcolorinfo.isSet())
        {
            myBackgroundColorIsDeliberate = true;
            myBackgroundColor = bgcolorinfo.getColor();
            return;
        } else
        {
            myBackgroundColor = bgcolorinfo.getDefaultColor();
            return;
        }
    }

    TableElementFormatter(TableElementFormatter tableelementformatter, TableElementPanel tableelementpanel, DocumentState documentstate)
    {
        valid = false;
        super.lines = new DocLine[1];
        parent = tableelementpanel;
        super.ds = documentstate;
        super.doc = ((Formatter) (tableelementformatter)).doc;
        startItem = tableelementformatter.startItem;
        myTR = tableelementformatter.myTR;
        myTABLE = tableelementformatter.myTABLE;
        cellWidth = tableelementformatter.cellWidth;
        cellHeight = tableelementformatter.cellHeight;
        vAlign = tableelementformatter.vAlign;
        colSpan = tableelementformatter.colSpan;
        rowSpan = tableelementformatter.rowSpan;
        super.marginHeight = parent.getCellPadding();
        startIndex = tableelementformatter.startIndex;
        myBackgroundColor = Color.white;
        copyPanelsForPrinting(tableelementformatter, documentstate);
    }

    public void registerListeners()
    {
        Container container = getParent();
        if(container != null)
        {
            formatterMouseListener = new FormatterMouseListener();
            container.addMouseListener(formatterMouseListener);
            container.addMouseMotionListener(formatterMouseListener);
        }
    }

    public void unregisterListeners()
    {
        Container container = getParent();
        if(container != null)
        {
            container.removeMouseListener(formatterMouseListener);
            container.removeMouseMotionListener(formatterMouseListener);
        }
        super.unregisterListeners();
    }

    protected void processMouseAction(MouseEvent mouseevent)
    {
        super.processMouseAction(mouseevent);
    }

    void getFormPanel(Vector vector)
    {
        int i = getStartIndex();
        DocItem docitem;
        for(int j = getMaxIndex(); i < j; i += docitem.getFormPanel(this, vector))
            docitem = super.doc.getItem(i);

    }

    Length getCellWidth()
    {
        return cellWidth;
    }

    Length getCellHeight()
    {
        return cellHeight;
    }

    int getColSpan()
    {
        return colSpan;
    }

    int getRowSpan()
    {
        return rowSpan;
    }

    protected DocStyle getBaseStyle()
    {
        if(myBaseStyle != null)
            return myBaseStyle;
        DocStyle docstyle = super.ds.docStyle;
        docstyle = docstyle.push(myTABLE);
        if(myTR != null)
            docstyle = docstyle.push(myTR);
        docstyle = docstyle.push(startItem);
        return docstyle;
    }

    public void dispatchDocumentEvent(Object obj, int i, boolean flag, Object obj1)
    {
        getTopFormatter().dispatchDocumentEvent(obj, i, flag, obj1);
    }

    public void dispatchDocumentEvent(int i, Object obj)
    {
        getTopFormatter().dispatchDocumentEvent(i, obj);
    }

    boolean containsPos(int i)
    {
        int j = i >> 12;
        return getStartIndex() <= j && j < getMaxIndex();
    }

    void adjustSpanBy(int i, boolean flag)
    {
        if(flag)
        {
            colSpan += i;
            return;
        } else
        {
            rowSpan += i;
            return;
        }
    }

    public int getAvailableHeight()
    {
        return super.height;
    }

    public int getAvailableWidth()
    {
        return super.width;
    }

    public Container getParent()
    {
        return parent;
    }

    public Formatter getParentFormatter()
    {
        return parent.getParentFormatter();
    }

    void setSize(int i, int j)
    {
        super.width = i;
        super.height = j;
    }

    int getBaseline()
    {
        if(vAlign == 4 && super.nlines > 0)
            return super.lines[0].baseline;
        else
            return 0;
    }

    void setCommonBaseline(int i)
    {
        baselineOffset = 0;
        if(vAlign == 4 && super.nlines > 0)
        {
            DocLine docline = super.lines[0];
            baselineOffset = i - docline.baseline;
        }
    }

    void doVerticalAlignment()
    {
        int i = 0;
        int j = parent.getCellPadding();
        switch(vAlign)
        {
        case 2: // '\002'
            i = (super.height - super.docHeight - j) / 2;
            break;

        case 4: // '\004'
            i = baselineOffset;
            break;

        case 5: // '\005'
            i = super.height - j - super.docHeight;
            break;
        }
        int k = i - oldDocYStart;
        if(k != 0)
        {
            adjustLinesBy(k);
            updateFloatersY(k);
            oldDocYStart = i;
        }
    }

    private void adjustLinesBy(int i)
    {
        for(int j = 0; j < super.nlines; j++)
            super.lines[j].y += i;

    }

    public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1)
    {
        return super.ds.docStyle.win.imageUpdate(image, i, j, k, l, i1);
    }

    public void setDocFont(DocFont docfont)
    {
        valid = false;
        super.setDocFont(docfont);
    }

    public void reformat()
    {
        super.reformat();
        baselineOffset = 0;
        oldDocYStart = 0;
    }

    void setAssignedWidth(int i)
    {
        if(super.width != i)
        {
            super.width = i;
            reformat();
        }
        formatElement();
    }

    void setMinWidth(int i)
    {
        elementMeasurement.setMinWidth(i);
    }

    void measure(Measurement measurement)
    {
        updateMeasurements();
        measurement.setMinWidth(elementMeasurement.getMinWidth());
        measurement.setPreferredWidth(elementMeasurement.getPreferredWidth());
    }

    int getElementHeight()
    {
        return super.docHeight;
    }

    int getAdjustedElementHeight()
    {
        return super.docHeight + baselineOffset;
    }

    private void updateMeasurements()
    {
        if(valid)
            return;
        elementMeasurement = new Measurement();
        Measurement measurement = new Measurement();
        MeasureState measurestate = new MeasureState();
        measurestate.pos = startIndex << 12;
        myBaseStyle = getBaseStyle();
        DocStyle docstyle = getStyleAt(((TraversalState) (measurestate)).pos);
        measurestate.style = docstyle;
        int i = docstyle.nobreak;
        Measurement measurement1 = new Measurement();
        int j = getStartIndex() + getNumItems() << 12;
        FormatState formatstate = new FormatState();
        while(((TraversalState) (measurestate)).pos < j) 
        {
            formatstate.state = 0;
            measurement.reset();
            measurestate.margin = (((TraversalState) (measurestate)).style.left << 16) + ((TraversalState) (measurestate)).style.right;
            while(((TraversalState) (measurestate)).pos < j) 
            {
                int k = ((TraversalState) (measurestate)).pos >> 12;
                DocItem docitem = super.doc.getItem(k);
                int i1 = docitem.getOffset();
                measurement1.reset();
                if(i1 <= 0 ? i1 >= 0 ? docitem.measureItem(this, formatstate, measurement1, measurestate) : docitem.measureEndTag(this, formatstate, measurement1, measurestate) : docitem.measureStartTag(this, formatstate, measurement1, measurestate))
                    break;
                if(i == 1 && ((TraversalState) (measurestate)).style.nobreak != 2)
                    ((TraversalState) (measurestate)).style.nobreak = i;
                if(i == 1 && cellWidth.isSet() && measurement.getMinWidth() + measurement1.getMinWidth() > cellWidth.getValue())
                {
                    i = 0;
                    TD td = (TD)startItem;
                    td.ignoreNoWrap();
                    measurestate.pos -= 4096;
                    measurement1.reset();
                } else
                if(((TraversalState) (measurestate)).style.nobreak == 1)
                    measurement.appendNoBreak(measurement1);
                else
                    measurement.append(measurement1);
            }
            measurement.adjustBy((measurestate.margin & 0xffff) + (measurestate.margin >> 16));
            elementMeasurement.unify(measurement);
        }
        if(((TraversalState) (measurestate)).style.nobreak == 0)
            elementMeasurement.adjustForFloaters();
        else
            elementMeasurement.adjustForFloatersNoBreak();
        if(cellWidth.isSet() && !cellWidth.isPercentage())
        {
            int l = elementMeasurement.getMinWidth();
            if(l > cellWidth.getValue())
            {
                String s = Integer.toString(l);
                cellWidth = new Length(s);
            }
            elementMeasurement.reset();
            elementMeasurement.setMinWidth(l);
            cellWidth.setValue(cellWidth.getValue() + (measurestate.margin & 0xffff) + (measurestate.margin >> 16));
            elementMeasurement.setPreferredWidth(cellWidth.getValue());
        }
        myBaseStyle = null;
        valid = !measurestate.measurementInvalid;
    }

    public void paint(Graphics g)
    {
        Rectangle rectangle = g.getClipRect();
        synchronized(Globals.getAwtLock())
        {
            synchronized(super.doc)
            {
                if(rectangle == null)
                {
                    if(paint(g, 0, 0, super.width, super.height, true))
                        touch();
                } else
                {
                    int i = Math.max(rectangle.x, 0);
                    int j = Math.max(rectangle.y, 0);
                    int k = Math.min(rectangle.x + rectangle.width, super.width);
                    int l = Math.min(rectangle.y + rectangle.height, super.height);
                    if(i > super.width || k < 0 || j > super.height || l < 0)
                        return;
                    if(paint(g, i, j, k - i, l - j, true))
                        touch();
                }
            }
        }
    }

    protected void clipToDrawableArea(Graphics g)
    {
        int i = parent.getCellPadding();
        g.clipRect(i, i, super.width - 2 * i, super.height - 2 * i);
    }

    boolean paint(Graphics g, int i, int j, int k, int l, boolean flag)
    {
        if(getStartIndex() >= super.doc.nitems)
            return false;
        Graphics g1 = g.create();
        boolean flag1 = false;
        try
        {
            clipToDrawableArea(g1);
            int i1 = j;
            int j1 = i1 + l;
            DocStyle docstyle = null;
            boolean flag2 = super.ds.paintingScreen;
            if(flag && super.nlines > 0 && super.lines[0].y != 0)
                paintBack(g1, 0, 0, super.width, super.lines[0].y);
            for(int k1 = findY(j); k1 < super.nlines; k1++)
            {
                DocLine docline = super.lines[k1];
                if(docline.y >= j1)
                    break;
                i1 = docline.y;
                if(flag)
                    paintBack(g1, 0, i1, super.width, docline.height);
                if(!docline.updated || flag2)
                {
                    docstyle = paintLine(g1, docline, i1, i, k, docstyle, false);
                } else
                {
                    flag1 = true;
                    docstyle = null;
                }
                i1 += docline.height;
            }

            if(flag && i1 < j1)
                paintBack(g1, 0, i1, super.width, j1 - i1);
            paintFloaters(g1, i, j, k, l);
        }
        finally
        {
            g1.dispose();
        }
        return flag1;
    }

    protected void print(Graphics g, FormatterVBreakInfo formattervbreakinfo)
    {
        clipToDrawableArea(g);
        super.print(g, formattervbreakinfo, super.width);
    }

    public void touch(boolean flag, int i)
    {
        parent.touch(flag, i);
    }

    void requestReformat(boolean flag, int i)
    {
        parent.touch(flag, i, startItem);
        valid = false;
    }

    public void touch(boolean flag, int i, DocItem docitem)
    {
        int j = findPos(docitem.getIndex() << 12);
        if(j < super.nlines)
            super.lines[j].updated = true;
        parent.touch(flag, i, docitem);
    }

    protected void setVisible(Component component, BitSet bitset, int i)
    {
        if(bitset != null && bitset.get(i) && !(component instanceof TablePanel))
            component.setVisible(true);
    }

    protected void setInvisible(Component component, BitSet bitset, int i)
    {
        if(component.isVisible() && !(component instanceof TablePanel))
        {
            bitset.set(i);
            component.setVisible(false);
        }
    }

    protected void disconnectFromParent(Component component)
    {
        parent.remove(component);
    }

    public Graphics getGraphics()
    {
        if(parent != null && super.ds.started)
            return parent.getGraphics();
        else
            return null;
    }

    public void activateSubItems()
    {
        activateItemComponents();
    }

    public void notify(Document document, int i, int j, int k)
    {
        j = Math.max(j, startIndex << 12);
        int l = findAffected(j);
        switch(i)
        {
        case 10: // '\n'
            int j1 = j & 0x7ffff000;
            int i3 = 4096 - (j & 0xfff);
            int i4 = j1 + 4096;
            if((super.ds.selStart & 0x7ffff000) == j1 && super.ds.selStart > j)
                super.ds.selStart += i3;
            else
            if(super.ds.selStart > i4)
                super.ds.selStart += 4096;
            if((super.ds.selEnd & 0x7ffff000) == j1 && super.ds.selEnd >= j)
                super.ds.selEnd += i3;
            else
            if(super.ds.selEnd > i4)
                super.ds.selEnd += 4096;
            for(; l < super.nlines; l++)
            {
                DocLine docline7 = super.lines[l];
                docline7.updated = true;
                if((docline7.start & 0x7ffff000) == j1 && docline7.start > j)
                    docline7.start += i3;
                else
                if(docline7.start >= i4)
                    docline7.start += 4096;
                if((docline7.end & 0x7ffff000) == j1 && docline7.end >= j)
                    docline7.end += i3;
                else
                    docline7.end += 4096;
                if((docline7.tail & 0x7ffff000) == j1 && docline7.tail >= j)
                    docline7.tail += i3;
                else
                    docline7.tail += 4096;
            }

            valid = false;
            adjustElementItemCountBy(1);
            invalidateFloatersInRange(j, super.lines[--l].end);
            notifyResponsiblesInRange(document, i, j, k);
            return;

        case 12: // '\f'
            int k1 = j & 0x7ffff000;
            if((super.ds.selStart & 0x7ffff000) == k1 && super.ds.selStart > j)
                super.ds.selStart += k;
            if((super.ds.selEnd & 0x7ffff000) == k1 && super.ds.selEnd >= j)
                super.ds.selEnd += k;
            int j3 = k1 + 4096;
            for(; l < super.nlines; l++)
            {
                DocLine docline5 = super.lines[l];
                if(docline5.start > j3)
                    break;
                docline5.updated = true;
                if((docline5.start & 0x7ffff000) == k1 && docline5.start > j)
                    docline5.start += k;
                if((docline5.end & 0x7ffff000) == k1 && docline5.end >= j)
                    docline5.end += k;
                if((docline5.tail & 0x7ffff000) == k1 && docline5.tail >= j)
                    docline5.tail += k;
            }

            valid = false;
            adjustElementItemCountBy(k);
            invalidateFloatersInRange(j, super.lines[--l].end);
            notifyResponsiblesInRange(document, i, j, k);
            return;

        case 13: // '\r'
            notifyResponsiblesInRange(document, i, j, k);
            int l1 = j & 0x7ffff000;
            if((super.ds.selStart & 0x7ffff000) == l1 && super.ds.selStart > j)
                super.ds.selStart -= k;
            if((super.ds.selEnd & 0x7ffff000) == l1 && super.ds.selEnd > j)
                super.ds.selEnd -= k;
            for(; l < super.nlines; l++)
            {
                DocLine docline1 = super.lines[l];
                if(docline1.start <= j)
                {
                    invalidateFloatersInRange(docline1.start, docline1.end);
                    docline1.updated = true;
                }
                if((docline1.start & 0x7ffff000) == l1 && docline1.start > j)
                    docline1.start -= k;
                if((docline1.end & 0x7ffff000) == l1 && docline1.end > j)
                    docline1.end -= k;
                if((docline1.tail & 0x7ffff000) == l1 && docline1.tail > j)
                    docline1.tail -= k;
            }

            valid = false;
            adjustElementItemCountBy(-k);
            return;

        case 15: // '\017'
            if(super.ds.selStart > j)
                super.ds.selStart += k;
            if(super.ds.selEnd >= j)
                super.ds.selEnd += k;
            for(; l < super.nlines; l++)
            {
                DocLine docline = super.lines[l];
                if(docline.start > j)
                    break;
                if(docline.end >= j)
                    docline.end += k;
                docline.tail += k;
                docline.updated = true;
            }

            for(int i2 = l; i2 < super.nlines; i2++)
            {
                DocLine docline2 = super.lines[i2];
                docline2.start += k;
                docline2.end += k;
                docline2.tail += k;
            }

            valid = false;
            adjustElementItemCountBy(k);
            if(super.nlines > 0)
                invalidateFloatersInRange(j, super.lines[super.nlines - 1].end);
            notifyResponsiblesInRange(document, i, j, k);
            return;

        case 16: // '\020'
            int j2 = j + k;
            notifyResponsiblesInRange(document, i, j, k);
            removeFloatersInRange(j, j2);
            if(super.ds.selStart > j)
                super.ds.selStart -= k;
            if(super.ds.selEnd > j)
                super.ds.selEnd -= k;
            if(l < super.nlines)
            {
                DocLine docline3 = super.lines[l];
                if(docline3.start < j)
                {
                    if(docline3.end > j)
                        docline3.end = j;
                    docline3.tail = j;
                    docline3.updated = true;
                    l++;
                }
            }
            for(; l < super.nlines; l++)
            {
                DocLine docline4 = super.lines[l];
                if(docline4.tail > j2)
                {
                    if(docline4.start < j2)
                    {
                        docline4.start = j;
                        if(docline4.end <= j2)
                            docline4.end = j;
                        else
                            docline4.end -= k;
                        docline4.tail -= k;
                        docline4.updated = true;
                        l++;
                    }
                    break;
                }
                docline4.start = docline4.end = docline4.tail = j;
                docline4.updated = true;
            }

            for(int k3 = l; k3 < super.nlines; k3++)
            {
                DocLine docline6 = super.lines[k3];
                docline6.start -= k;
                docline6.end -= k;
                docline6.tail -= k;
            }

            valid = false;
            adjustElementItemCountBy(-k);
            return;

        case 18: // '\022'
        case 19: // '\023'
            if(document.isOkToFormat())
            {
                Graphics g = getGraphics();
                if(g != null)
                {
                    try
                    {
                        paintRangeNoFloaters(g, j, j + k, i == 18);
                        paintFloatersInRange(g, j, j + k);
                    }
                    finally
                    {
                        g.dispose();
                    }
                    return;
                }
            }
            return;

        case 17: // '\021'
            int k2 = j + k;
            int l3 = l;
            boolean flag = false;
            for(; l < super.nlines; l++)
            {
                DocLine docline8 = super.lines[l];
                if(docline8.start >= k2)
                    break;
                docline8.updated = true;
                flag = true;
            }

            for(; l < super.nlines; l++)
            {
                if(super.lines[l].y == super.lines[l3].y)
                {
                    super.lines[l].updated = true;
                    flag = true;
                    k2 = super.lines[l].end;
                    continue;
                }
                if(super.lines[l].y > super.lines[l3].y)
                    break;
            }

            notifyResponsiblesInRange(document, i, j, k);
            dirtyFloaterLines(new FormatState(), j, k2);
            if(flag)
                requestReformat(true, 200);
            return;

        case 21: // '\025'
            DocItem docitem;
            for(int l2 = getStartIndex() + getNumItems() << 12; j < l2; j = docitem.getIndex() + docitem.getOffset() + 1 << 12)
            {
                notifyResponsiblesInRange(document, i, j, k);
                docitem = document.items[j >> 12];
                for(int i1 = 0; i1 < super.nItemComponents; i1++)
                {
                    Component component = super.itemComponents[i1].getComponent();
                    int j4 = super.itemComponents[i1].getIndex();
                    DocItem docitem1 = document.items[j4];
                    if(docitem1 == docitem)
                    {
                        if(super.ds.started)
                            ((DocPanel)component).stop();
                        ((DocPanel)component).destroy();
                        if(parent != null)
                            parent.remove(component);
                        System.arraycopy(super.itemComponents, i1 + 1, super.itemComponents, i1, super.nItemComponents - i1 - 1);
                        super.itemComponents[super.nItemComponents - 1] = null;
                        super.nItemComponents--;
                        i1--;
                    }
                }

                deactivateItems();
            }

            return;

        case 11: // '\013'
        case 14: // '\016'
        case 20: // '\024'
        case 22: // '\026'
        default:
            return;
        }
    }

    private void adjustElementItemCountBy(int i)
    {
    }

    public Color getFormatterBackgroundColor()
    {
        return myBackgroundColor;
    }

    public boolean isFormatterBackgroundColorDeliberate()
    {
        return myBackgroundColorIsDeliberate;
    }

    protected int getStartIndex()
    {
        return startIndex;
    }

    protected int getNumItems()
    {
        return startItem.getOffset() - 1;
    }

    protected int getMaxIndex()
    {
        return (startIndex + startItem.getOffset()) - 1;
    }

    public void getBackgroundDisplacement(Point point)
    {
        parent.getBackgroundDisplacement(point);
    }

    private void formatElement()
    {
        DocLine adocline[] = super.lines;
        int i = super.nlines;
        int j;
        for(j = 0; j < i && !adocline[j].updated; j++);
        int k = getMaxIndex() << 12;
        int l = j <= 0 ? getStartIndex() << 12 : adocline[j - 1].end;
        int i1 = j <= 0 ? 0 : adocline[j - 1].y + adocline[j - 1].height;
        int j1 = j;
        FormatState formatstate = new FormatState();
        FormatState formatstate1 = new FormatState();
        formatstate.pos = getStartIndex() - 1 << 12;
        myBaseStyle = getBaseStyle();
        while(l < k) 
        {
            DocLine docline = formatLine(i1, l, formatstate, formatstate1);
            if(docline == null)
                break;
            docline.y = i1;
            l = docline.end;
            i1 += docline.height;
            int k1 = docline.height;
            for(; j1 < i && adocline[j1].end <= l; j1++)
                k1 -= adocline[j1].height;

            super.docHeight += k1;
            int l1 = (docline.margin >> 16) + docline.width;
            if(super.docWidth < l1)
                super.docWidth = l1;
            if(j1 == j)
            {
                byte byte0 = 10;
                if(i + byte0 > adocline.length)
                {
                    DocLine adocline1[] = new DocLine[adocline.length + byte0];
                    System.arraycopy(adocline, 0, adocline1, 0, i);
                    adocline = adocline1;
                }
                System.arraycopy(adocline, j1, adocline, j1 + byte0, i - j1);
                j1 += byte0;
                i += byte0;
            }
            adocline[j++] = docline;
            if(j1 < i && !adocline[j1].updated && adocline[j1].start == l && adocline[j1].margin == formatstate.margin)
            {
                do
                {
                    DocLine docline1 = adocline[j1++];
                    updateFloatersYInRange(docline1.start, docline1.end, i1 - docline1.y);
                    docline1.y = i1;
                    i1 += docline1.height;
                    adocline[j++] = docline1;
                } while(j1 < i && !adocline[j1].updated);
                l = adocline[j1 - 1].end;
            }
        }
        super.docHeight = adjustDocHeightForFloaters(super.docHeight);
        if(j1 < i && adocline[j1].y != i1)
        {
            updateFloatersYInRange(l, k, i1 - adocline[j1].y);
            do
            {
                DocLine docline2 = adocline[j1++];
                docline2.y = i1;
                i1 += docline2.height;
                adocline[j++] = docline2;
            } while(j1 < i);
        } else
        {
            System.arraycopy(adocline, j1, adocline, j, i - j1);
        }
        i += j - j1;
        super.nlines = i;
        super.lines = adocline;
        myBaseStyle = null;
    }

    public void select(int i, int j)
    {
        DocumentFormatter documentformatter = getTopFormatter();
        super.ds.selecter = this;
        documentformatter.doSelect(i, j);
    }

    static final long serialVersionUID = 0x73be1be1c8eab8f1L;
    private DocItem startItem;
    private int startIndex;
    private boolean valid;
    private byte rowSpan;
    private byte colSpan;
    private Measurement elementMeasurement;
    private Length cellWidth;
    private Length cellHeight;
    private int vAlign;
    private int baselineOffset;
    private int oldDocYStart;
    private TableElementPanel parent;
    private Color myBackgroundColor;
    private boolean myBackgroundColorIsDeliberate;
    private DocItem myTR;
    private DocItem myTABLE;
    private DocStyle myBaseStyle;
    FormatterMouseListener formatterMouseListener;
}
