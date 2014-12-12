// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TablePanel.java

package sunw.hotjava.tables;

import java.awt.*;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.Length;
import sunw.hotjava.tags.Align;
import sunw.hotjava.tags.TagAppletPanel;
import sunw.html.Attributes;

// Referenced classes of package sunw.hotjava.tables:
//            BGColorInfo, CellInfoVector, CellSpec, ConstraintSummary, 
//            DeliberatelyPaintable, TableElementPanel, TableElementPanelEnumerator, TableGrid, 
//            TableVBreakInfo

public class TablePanel extends Container
    implements DocPanel, DocConstants, DeliberatelyPaintable
{

    TablePanel(Formatter formatter, TagItem tagitem, Document document)
    {
        border = -1;
        borderWidth = 1;
        cellPadding = 1;
        cellSpacing = 2;
        tableGrid = new TableGrid();
        suppressRepaint = false;
        firstCall = true;
        setVisible(false);
        setLayout(null);
        parentFormatter = formatter;
        doc = document;
        item = tagitem;
        Color color = formatter.getFormatterBackgroundColor();
        BGColorInfo bgcolorinfo = new BGColorInfo(color);
        if(formatter.isFormatterBackgroundColorDeliberate())
            bgcolorinfo.setColor(color);
        BGColorInfo bgcolorinfo1 = new BGColorInfo(bgcolorinfo);
        Attributes attributes = tagitem.getAttributes();
        if(attributes != null)
        {
            givenWidth = new Length(attributes.get("width"));
            givenHeight = new Length(attributes.get("height"));
            borderWidth = parseIntAtt(attributes, "border", 1);
            hasBorder = (attributes.get("border") != null || attributes.get("dummy") != null) && borderWidth > 0;
            cellPadding = parseIntAtt(attributes, "cellpadding", 1);
            cellSpacing = parseIntAtt(attributes, "cellspacing", 2);
            if(cellPadding == 0 && hasBorder)
                cellPadding = 1;
            tableAlignment = parseHorizAlign(attributes, "align", 7);
            parseColor(attributes, "bgcolor", bgcolorinfo1);
        } else
        {
            givenWidth = new Length(null);
            givenHeight = new Length(null);
        }
        parseRows(bgcolorinfo, bgcolorinfo1);
        initInfoVectors();
        formatter.claimResponsibilityFor(this, tagitem.getIndex(), tagitem.getIndex() + tagitem.getOffset());
    }

    TablePanel(TablePanel tablepanel, Formatter formatter, DocumentState documentstate)
    {
        border = -1;
        borderWidth = 1;
        cellPadding = 1;
        cellSpacing = 2;
        tableGrid = new TableGrid();
        suppressRepaint = false;
        firstCall = true;
        setLayout(null);
        doc = tablepanel.doc;
        item = tablepanel.item;
        parentFormatter = formatter;
        border = tablepanel.border;
        borderWidth = tablepanel.borderWidth;
        givenWidth = tablepanel.givenWidth;
        givenHeight = tablepanel.givenHeight;
        cellPadding = tablepanel.cellPadding;
        cellSpacing = tablepanel.cellSpacing;
        numRows = tablepanel.numRows;
        numCols = tablepanel.numCols;
        maxRowSpan = tablepanel.maxRowSpan;
        maxColSpan = tablepanel.maxColSpan;
        tableAlignment = tablepanel.tableAlignment;
        captionAlignment = tablepanel.captionAlignment;
        captionItem = tablepanel.captionItem;
        tableGrid = new TableGrid(tablepanel.tableGrid, this, documentstate);
        colInfo = new CellInfoVector(numCols, cellPadding, cellSpacing);
        rowInfo = new CellInfoVector(numRows, cellPadding, cellSpacing);
        if(captionItem != null)
        {
            int i = 0;
            if(captionAlignment == 5)
                i = numRows - 1;
            captionPanel = tableGrid.elementAt(0, i);
        }
        hasBorder = tablepanel.hasBorder;
    }

    public Component copyForPrinting(Formatter formatter, DocumentState documentstate)
    {
        return new TablePanel(this, formatter, documentstate);
    }

    private void initInfoVectors()
    {
        colInfo = new CellInfoVector(numCols, cellPadding, cellSpacing);
        rowInfo = new CellInfoVector(numRows, cellPadding, cellSpacing);
    }

    private void parseRows(BGColorInfo bgcolorinfo, BGColorInfo bgcolorinfo1)
    {
        int i = 0;
        int j = item.getIndex() + 1;
        do
        {
            DocItem docitem = doc.getItem(j);
            int k = docitem.getOffset();
            if(k < 0)
                break;
            if(isTag(docitem, "tr"))
            {
                parseTableRow(docitem, j, i, bgcolorinfo1);
                i++;
            } else
            if(isTag(docitem, "caption"))
                parseCaption(docitem);
            j += k;
            j++;
        } while(true);
        canonicalizeTable();
        if(captionItem != null)
        {
            captionPanel = makeCaptionPanel(bgcolorinfo);
            tableGrid.insertCaption(captionPanel, captionAlignment == 0, numCols);
            add(captionPanel);
            numRows++;
        }
    }

    private void parseCaption(DocItem docitem)
    {
        if(captionItem != null)
        {
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! multiple captions");
            return;
        }
        captionItem = docitem;
        captionAlignment = 0;
        TagItem tagitem = docitem.getTag(doc);
        if(tagitem != null)
        {
            Attributes attributes = tagitem.getAttributes();
            if(attributes != null)
                captionAlignment = parseAlignment(attributes, "align", 33, 0);
        }
    }

    TableElementPanel makeCaptionPanel(BGColorInfo bgcolorinfo)
    {
        return new TableElementPanel(doc, parentFormatter.getDocumentState(), captionItem, null, item, this, new Length(null), new Length(null), captionAlignment, numCols, 1, bgcolorinfo);
    }

    private void parseTableRow(DocItem docitem, int i, int j, BGColorInfo bgcolorinfo)
    {
        int k = 2;
        BGColorInfo bgcolorinfo1 = new BGColorInfo(bgcolorinfo);
        TagItem tagitem = docitem.getTag(doc);
        if(tagitem != null)
        {
            Attributes attributes = tagitem.getAttributes();
            if(attributes != null)
            {
                k = parseVertAlign(attributes, "valign", k);
                parseColor(attributes, "bgcolor", bgcolorinfo1);
            }
        }
        int l = docitem.getIndex() + 1;
        do
        {
            DocItem docitem1 = doc.getItem(l);
            int i1 = docitem1.getOffset();
            if(i1 < 0)
                return;
            if(isTag(docitem1, "td") || isTag(docitem1, "th"))
                parseElements(j, docitem1, docitem, k, bgcolorinfo1);
            l += i1;
            l++;
        } while(true);
    }

    private void parseElements(int i, DocItem docitem, DocItem docitem1, int j, BGColorInfo bgcolorinfo)
    {
        TagItem tagitem = docitem.getTag(doc);
        if(tagitem != null)
        {
            Attributes attributes = tagitem.getAttributes();
            int k;
            int l;
            int i1;
            if(attributes == null)
            {
                k = 1;
                l = 1;
                i1 = j;
            } else
            {
                k = parseIntAtt(attributes, "rowspan", 1);
                l = parseIntAtt(attributes, "colspan", 1);
                i1 = parseVertAlign(attributes, "valign", j);
                if(l == 0)
                    l = 1;
                if(k == 0)
                    k = 1;
            }
            if(maxRowSpan < k)
                maxRowSpan = k;
            if(maxColSpan < l)
                maxColSpan = l;
            Length length;
            Length length1;
            if(attributes == null)
            {
                length = new Length(null);
                length1 = new Length(null);
            } else
            {
                length = new Length(attributes.get("width"));
                length1 = new Length(attributes.get("height"));
            }
            BGColorInfo bgcolorinfo1 = new BGColorInfo(bgcolorinfo);
            parseColor(attributes, "bgcolor", bgcolorinfo1);
            int j1 = tableGrid.findNextFreeCol(i);
            int k1 = j1 + l;
            int l1 = i + k;
            if(numCols < k1)
                numCols = k1;
            if(numRows < l1)
                numRows = l1;
            TableElementPanel tableelementpanel = new TableElementPanel(doc, parentFormatter.getDocumentState(), docitem, docitem1, item, this, length, length1, i1, l, k, bgcolorinfo1);
            tableGrid.addElement(tableelementpanel, j1, i, l, k);
            add(tableelementpanel);
        }
    }

    protected int parseIntAtt(Attributes attributes, String s, int i)
    {
        int j = i;
        if(attributes != null)
        {
            String s1 = attributes.get(s);
            if(s1 != null)
                try
                {
                    j = Integer.parseInt(s1);
                }
                catch(Exception _ex) { }
        }
        return j;
    }

    private boolean isTag(DocItem docitem, String s)
    {
        TagItem tagitem = docitem.getTag(doc);
        if(tagitem == null)
            return false;
        else
            return s.equals(tagitem.getName());
    }

    private int parseHorizAlign(Attributes attributes, String s, int i)
    {
        int j = Align.getCheckedAlign(attributes, s, 388, i);
        if(j < 0)
            j = i;
        return j;
    }

    private int parseVertAlign(Attributes attributes, String s, int i)
    {
        int j = Align.getCheckedAlign(attributes, s, 53, i);
        if(j < 0)
            j = i;
        return j;
    }

    private int parseAlignment(Attributes attributes, String s, int i, int j)
    {
        int k = Align.getCheckedAlign(attributes, s, i, j);
        return k;
    }

    private void parseColor(Attributes attributes, String s, BGColorInfo bgcolorinfo)
    {
        if(attributes != null)
        {
            String s1 = attributes.get(s);
            if(s1 != null)
            {
                Color color = Globals.mapNamedColor(s1);
                if(color != null)
                    bgcolorinfo.setColor(color);
            }
        }
    }

    private void canonicalizeTable()
    {
        boolean flag = removeExtraRows();
        flag &= removeExtraCols();
        if(flag)
            updateMaxSpanInfo();
    }

    private boolean removeExtraRows()
    {
        boolean flag = false;
        for(int i = 0; i < numRows; i++)
        {
            boolean flag1 = false;
            boolean flag2 = false;
            for(int j = 0; j < numCols; j++)
            {
                TableElementPanel tableelementpanel = tableGrid.elementAt(j, i);
                if(tableelementpanel != null)
                {
                    if(i > 0 && !flag1)
                    {
                        TableElementPanel tableelementpanel1 = tableGrid.elementAt(j, i - 1);
                        if(tableelementpanel1 != null && tableelementpanel1 != tableelementpanel)
                            flag1 = true;
                    }
                    if(i < numRows - 1 && !flag2)
                    {
                        TableElementPanel tableelementpanel2 = tableGrid.elementAt(j, i + 1);
                        if(tableelementpanel2 != tableelementpanel)
                            flag2 = true;
                    }
                    j += tableelementpanel.getColSpan() - 1;
                }
            }

            if(i > 0 && !flag1 || i < numRows - 1 && !flag2)
            {
                for(int k = 0; k < numCols; k++)
                {
                    TableElementPanel tableelementpanel3 = tableGrid.elementAt(k, i);
                    if(tableelementpanel3 != null)
                    {
                        k += tableelementpanel3.getColSpan() - 1;
                        tableelementpanel3.adjustSpanBy(-1, false);
                    }
                }

                tableGrid.removeRow(i);
                i--;
                numRows--;
                flag = true;
            }
        }

        return flag;
    }

    private boolean removeExtraCols()
    {
        boolean flag = false;
        for(int i = 1; i < numCols; i++)
        {
            boolean flag1 = false;
            for(int j = 0; j < numRows; j++)
            {
                TableElementPanel tableelementpanel = tableGrid.elementAt(i, j);
                if(tableelementpanel == null)
                    continue;
                TableElementPanel tableelementpanel1 = tableGrid.elementAt(i - 1, j);
                if(tableelementpanel1 != tableelementpanel)
                {
                    flag1 = true;
                    break;
                }
                j += tableelementpanel.getRowSpan() - 1;
            }

            if(!flag1)
            {
                for(int k = 0; k < numRows; k++)
                {
                    TableElementPanel tableelementpanel2 = tableGrid.elementAt(i, k);
                    if(tableelementpanel2 != null)
                    {
                        k += tableelementpanel2.getRowSpan() - 1;
                        tableelementpanel2.adjustSpanBy(-1, true);
                    }
                }

                tableGrid.removeColumn(i);
                i--;
                numCols--;
                flag = true;
            }
        }

        return flag;
    }

    private void updateMaxSpanInfo()
    {
        maxColSpan = 0;
        maxRowSpan = 0;
        for(int i = 0; i < numRows; i++)
        {
            for(int j = 0; j < numCols; j++)
            {
                TableElementPanel tableelementpanel = tableGrid.elementAt(j, i);
                if(tableelementpanel != null)
                {
                    maxColSpan = Math.max(maxColSpan, tableelementpanel.getColSpan());
                    maxRowSpan = Math.max(maxRowSpan, tableelementpanel.getRowSpan());
                }
            }

        }

    }

    int getCellPadding()
    {
        return cellPadding;
    }

    int getCellSpacing()
    {
        return cellSpacing;
    }

    public static void paintWithClipping(Graphics g, Component component)
    {
        Graphics g1 = component.getGraphics();
        Rectangle rectangle = component.getBounds();
        try
        {
            boolean flag = clipGraphicsToParent(g, g1, component, rectangle);
            if(flag)
                ((DeliberatelyPaintable)component).paintDeliberately(g1);
        }
        finally
        {
            g1.dispose();
        }
    }

    public static void paintWithClipping(Graphics g, Component component, Rectangle rectangle)
    {
        Graphics g1 = component.getGraphics();
        try
        {
            boolean flag = clipGraphicsToParent(g, g1, component, rectangle);
            if(flag)
                ((DeliberatelyPaintable)component).paintDeliberately(g1);
        }
        finally
        {
            g1.dispose();
        }
    }

    private boolean paintRangeNoFloatersWithClipping(Graphics g, int i, int j, boolean flag, TableElementPanel tableelementpanel)
    {
        boolean flag1 = false;
        Graphics g1 = tableelementpanel.getGraphics();
        Rectangle rectangle = tableelementpanel.getBounds();
        try
        {
            boolean flag2 = clipGraphicsToParent(g, g1, tableelementpanel, rectangle);
            if(flag2)
                flag1 = tableelementpanel.paintRangeNoFloaters(g1, i, j, flag);
        }
        finally
        {
            g1.dispose();
        }
        return flag1;
    }

    private boolean paintRangeWithClipping(Graphics g, int i, int j, boolean flag, TableElementPanel tableelementpanel)
    {
        boolean flag1 = false;
        Graphics g1 = tableelementpanel.getGraphics();
        Rectangle rectangle = tableelementpanel.getBounds();
        try
        {
            boolean flag2 = clipGraphicsToParent(g, g1, tableelementpanel, rectangle);
            if(flag2)
                flag1 = tableelementpanel.paintRange(g1, i, j, flag);
        }
        finally
        {
            g1.dispose();
        }
        return flag1;
    }

    private static boolean clipGraphicsToParent(Graphics g, Graphics g1, Component component, Rectangle rectangle)
    {
        Rectangle rectangle1 = g.getClipBounds();
        if(rectangle1 != null)
        {
            if(!rectangle.intersects(rectangle1))
                return false;
            Point point = component.getLocation();
            g1.clipRect(rectangle1.x - point.x, rectangle1.y - point.y, rectangle1.width, rectangle1.height);
        }
        return true;
    }

    void getFormPanel(Vector vector)
    {
        for(Enumeration enumeration = enumeratePanels(); enumeration.hasMoreElements(); ((TableElementPanel)enumeration.nextElement()).getFormPanel(vector));
    }

    public void activateSubItems()
    {
        for(Enumeration enumeration = enumeratePanels(); enumeration.hasMoreElements(); ((DocPanel)enumeration.nextElement()).activateSubItems());
    }

    public void start()
    {
        Color color = parentFormatter.getFormatterBackgroundColor();
        if(color != null)
            setBackground(color);
        for(Enumeration enumeration = enumeratePanels(); enumeration.hasMoreElements(); ((DocPanel)enumeration.nextElement()).start());
    }

    public void unregisterListeners()
    {
        for(Enumeration enumeration = enumeratePanels(); enumeration.hasMoreElements();)
        {
            Component component = (Component)enumeration.nextElement();
            if(component instanceof TablePanel)
                ((TablePanel)component).unregisterListeners();
            else
            if(component instanceof TableElementPanel)
                ((TableElementPanel)component).unregisterListeners();
        }

    }

    public void stop()
    {
        for(Enumeration enumeration = enumeratePanels(); enumeration.hasMoreElements(); ((DocPanel)enumeration.nextElement()).stop());
    }

    public void destroy()
    {
        for(Enumeration enumeration = enumeratePanels(); enumeration.hasMoreElements(); ((DocPanel)enumeration.nextElement()).destroy());
        parentFormatter.disclaimResponsibilityFor(this, item.getIndex(), item.getIndex() + item.getOffset());
    }

    public void interruptLoading()
    {
        for(Enumeration enumeration = enumeratePanels(); enumeration.hasMoreElements(); ((DocPanel)enumeration.nextElement()).interruptLoading());
    }

    public void notify(Document document, int i, int j, int k)
    {
        for(Enumeration enumeration = enumeratePanels(); enumeration.hasMoreElements(); ((DocPanel)enumeration.nextElement()).notify(document, i, j, k));
    }

    public void reformat()
    {
        for(Enumeration enumeration = enumeratePanels(); enumeration.hasMoreElements(); ((DocPanel)enumeration.nextElement()).reformat());
    }

    public int findYFor(int i)
    {
        for(Enumeration enumeration = enumeratePanels(); enumeration.hasMoreElements();)
        {
            TableElementPanel tableelementpanel = (TableElementPanel)enumeration.nextElement();
            if(tableelementpanel.containsPos(i))
                return tableelementpanel.findYFor(i);
        }

        return 0;
    }

    public boolean paintRange(Graphics g, int i, int j, boolean flag)
    {
        if(!isShowing())
            return false;
        boolean flag1 = false;
        for(Enumeration enumeration = enumeratePanels(); enumeration.hasMoreElements();)
        {
            TableElementPanel tableelementpanel = (TableElementPanel)enumeration.nextElement();
            if(tableelementpanel.intersectsRange(i, j))
                flag1 |= paintRangeWithClipping(g, i, j, flag, tableelementpanel);
        }

        return flag1;
    }

    public boolean paintRangeNoFloaters(Graphics g, int i, int j, boolean flag)
    {
        if(!isShowing())
            return false;
        boolean flag1 = false;
        for(Enumeration enumeration = enumeratePanels(); enumeration.hasMoreElements();)
        {
            TableElementPanel tableelementpanel = (TableElementPanel)enumeration.nextElement();
            if(tableelementpanel.intersectsRange(i, j))
                flag1 |= paintRangeNoFloatersWithClipping(g, i, j, flag, tableelementpanel);
        }

        return flag1;
    }

    public void paint(Graphics g)
    {
        paintBackgroundAndBorder(g);
        paintDeliberately(g);
    }

    public void setVisible(boolean flag)
    {
        if(firstCall && flag)
        {
            super.setVisible(flag);
            firstCall = false;
            return;
        }
        if(isVisible() != flag)
        {
            suppressRepaint = true;
            super.setVisible(flag);
            suppressRepaint = false;
        }
    }

    public void repaint()
    {
        if(!suppressRepaint)
            super.repaint();
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paintDeliberately(Graphics g)
    {
        int i = getComponentCount();
        Rectangle rectangle = g.getClipBounds();
        int j = rectangle.x;
        int k = rectangle.y;
        int l = rectangle.width;
        int i1 = rectangle.height;
        Object obj;
        for(obj = this; obj != null; obj = ((Component) (obj)).getParent())
            if(obj instanceof ScrollPane)
                break;

        if(obj == null)
        {
            System.out.println("Couldn't find a scrollpane");
            return;
        }
        ScrollPane scrollpane = (ScrollPane)obj;
        Dimension dimension = scrollpane.getViewportSize();
        int j1 = dimension.width;
        int k1 = dimension.height;
        int l1;
        if(prevX < j)
            l1 = (l + j) - j1;
        else
            l1 = j;
        int i2;
        if(prevY < k)
            i2 = (i1 + k) - k1;
        else
            i2 = k;
        prevX = l1;
        prevY = i2;
        rectangle = g.getClipBounds();
        paintBackgroundAndBorder(g);
        for(int j2 = 0; j2 < i; j2++)
        {
            Component component = getComponent(j2);
            Rectangle rectangle1 = component.getBounds();
            if(rectangle1.intersects(rectangle))
                paintWithClipping(g, component, rectangle1);
        }

    }

    private void paintBack(Graphics g, int i, int j, int k, int l)
    {
        DocumentState documentstate = parentFormatter.getDocumentState();
        if(documentstate.bg != null)
        {
            if(!(g instanceof PrintGraphics))
            {
                Point point = new Point(0, 0);
                getBackgroundDisplacement(point);
                documentstate.bg.paint(g, point.x, point.y, i, j, k, l);
                return;
            }
        } else
        if(getBackground() != null)
        {
            Color color = g.getColor();
            g.setColor(getBackground());
            g.fillRect(i, j, k, l);
            g.setColor(color);
        }
    }

    void getBackgroundDisplacement(Point point)
    {
        Point point1 = getLocation();
        int i = parentFormatter.findPos(item.getIndex() << 12);
        point1.y = parentFormatter.findYOfDocLine(i);
        point.translate(-point1.x, -point1.y);
        parentFormatter.getBackgroundDisplacement(point);
    }

    private void paintBackgroundAndBorder(Graphics g)
    {
        Dimension dimension = getSize();
        paintBack(g, 0, 0, dimension.width, dimension.height);
        Point point = new Point(0, 0);
        adjustForCaption(point, dimension);
        if(hasBorder)
        {
            DocumentState documentstate = parentFormatter.getDocumentState();
            draw3DBorder(g, dimension, point, documentstate.background);
        }
    }

    private void printBackgroundAndBorder(Graphics g)
    {
        Dimension dimension = getSize();
        Point point = new Point(0, 0);
        adjustForCaption(point, dimension);
        if(hasBorder)
        {
            if(borderWidth < 3)
            {
                drawBorder(g, dimension, point);
                return;
            }
            draw3DBorder(g, dimension, point, Color.white);
        }
    }

    private void drawBorder(Graphics g, Dimension dimension, Point point)
    {
        Color color = g.getColor();
        g.setColor(Color.black);
        for(int i = 0; i < borderWidth; i++)
            g.drawRect(i + point.x, i + point.y, dimension.width - (2 * i + 1), dimension.height - (2 * i + 1));

        g.setColor(color);
    }

    private void draw3DBorder(Graphics g, Dimension dimension, Point point, Color color)
    {
        Color color1 = g.getColor();
        g.setColor(Globals.getVisible3DColor(color));
        for(int i = 0; i < borderWidth; i++)
            g.draw3DRect(i + point.x, i + point.y, dimension.width - (2 * i + 1), dimension.height - (2 * i + 1), true);

        g.setColor(color1);
    }

    private void adjustForCaption(Point point, Dimension dimension)
    {
        if(captionPanel == null)
            return;
        dimension.height -= captionPanel.getElementHeight() + cellSpacing;
        if(captionAlignment == 0)
            point.y += captionPanel.getElementHeight() + cellSpacing;
    }

    int findSplitY(int i, int j, TableVBreakInfo tablevbreakinfo)
    {
        int k = 0;
        if(tablevbreakinfo != null)
            k = tablevbreakinfo.getStartRow();
        int l;
label0:
        for(l = k; l < numRows; l++)
        {
            for(int i1 = 0; i1 < numCols; i1++)
            {
                if(!isUpperLeftArray[l][i1])
                    continue;
                TableElementPanel tableelementpanel = tableGridArray[l][i1];
                if(tableelementpanel == null)
                    continue;
                Rectangle rectangle = tableelementpanel.getBounds();
                int j1 = rectangle.y + rectangle.height;
                if(j1 < i)
                    continue;
                int k1 = rectangle.height;
                if(k1 < j && hasBorder)
                    return rectangle.y;
                break label0;
            }

        }

        if(l >= numRows)
            return i;
        else
            return findRowSplitY(l, i, j, tablevbreakinfo);
    }

    private int findRowSplitY(int i, int j, int k, TableVBreakInfo tablevbreakinfo)
    {
        int l = j;
        for(int i1 = 0; i1 < numCols; i1++)
            if(isUpperLeftArray[i][i1])
            {
                TableElementPanel tableelementpanel = tableGridArray[i][i1];
                if(tableelementpanel != null)
                {
                    Rectangle rectangle = tableelementpanel.getBounds();
                    int j1 = rectangle.y + rectangle.height;
                    if(j1 >= j)
                    {
                        sunw.hotjava.doc.VBreakInfo vbreakinfo = null;
                        if(tablevbreakinfo != null)
                            vbreakinfo = tablevbreakinfo.getItemBreakInfo(tableelementpanel);
                        int k1 = tableelementpanel.findSplitY(j - rectangle.y, k, vbreakinfo) + rectangle.y;
                        if(k1 < l)
                            l = k1;
                    }
                }
            }

        return l;
    }

    void recordBreakInfo(int i, int j, TableVBreakInfo tablevbreakinfo, TableVBreakInfo tablevbreakinfo1)
    {
        int k = 0;
        if(tablevbreakinfo != null)
            k = tablevbreakinfo.getStartRow();
        int l = borderWidth + cellSpacing;
        boolean flag = false;
        int i1;
        for(i1 = 0; i1 < numRows; i1++)
        {
            if(l >= i)
            {
                flag = l == i;
                break;
            }
            l += rowInfo.getAssignedSize(i1) + cellSpacing;
        }

        tablevbreakinfo1.setEndRow(numRows);
        tablevbreakinfo.setEndRow(i1);
        recordPanelFloaterBreakInfo(i, j, tablevbreakinfo, tablevbreakinfo1, k, i1);
        if(flag)
        {
            tablevbreakinfo1.setStartRow(i1);
            if(i1 >= numRows)
                return;
            i1--;
        } else
        {
            i1--;
            tablevbreakinfo1.setStartRow(i1);
        }
        if(i1 < 0)
            return;
        for(int j1 = 0; j1 < numCols; j1++)
        {
            TableElementPanel tableelementpanel = tableGridArray[i1][j1];
            if(tableelementpanel != null && (j1 <= 0 || tableelementpanel != tableGridArray[i1][j1 - 1]))
            {
                Rectangle rectangle = tableelementpanel.getBounds();
                if(rectangle.y < i && rectangle.y + rectangle.height > j)
                    tableelementpanel.recordBreakInfo(i - rectangle.y, j - rectangle.y, tablevbreakinfo, tablevbreakinfo1);
            }
        }

    }

    private void recordPanelFloaterBreakInfo(int i, int j, TableVBreakInfo tablevbreakinfo, TableVBreakInfo tablevbreakinfo1, int k, int l)
    {
        for(int i1 = k; i1 < l; i1++)
        {
            for(int j1 = 0; j1 < numCols; j1++)
            {
                TableElementPanel tableelementpanel = tableGridArray[i1][j1];
                if(tableelementpanel != null && (i1 != k ? isUpperLeftArray[i1][j1] : j1 <= 0 || tableelementpanel != tableGridArray[i1][j1 - 1]))
                {
                    Rectangle rectangle = tableelementpanel.getBounds();
                    tableelementpanel.recordFloaterBreakInfo(i - rectangle.y, j - rectangle.y, tablevbreakinfo, tablevbreakinfo1);
                }
            }

        }

    }

    void print(Graphics g, TableVBreakInfo tablevbreakinfo)
    {
        int i = 0;
        int j = numRows;
        if(tablevbreakinfo != null)
        {
            i = tablevbreakinfo.getStartRow();
            j = tablevbreakinfo.getEndRow();
        }
        printBackgroundAndBorder(g);
        if(i >= numRows)
            return;
        for(int k = 0; k < numCols; k++)
            if(!isRowStart(k, i) && isColStart(k, i))
            {
                TableElementPanel tableelementpanel = tableGrid.elementAt(k, i);
                if(tableelementpanel != null)
                    printTableElement(g, tableelementpanel, tablevbreakinfo);
            }

        for(int l = i; l < j; l++)
        {
            for(int i1 = 0; i1 < numCols; i1++)
            {
                TableElementPanel tableelementpanel1 = tableGrid.elementAt(i1, l);
                if(tableelementpanel1 != null && tableGrid.isUpperLeft(i1, l))
                    printTableElement(g, tableelementpanel1, tablevbreakinfo);
            }

        }

    }

    private void printTableElement(Graphics g, TableElementPanel tableelementpanel, TableVBreakInfo tablevbreakinfo)
    {
        sunw.hotjava.doc.VBreakInfo vbreakinfo = null;
        if(tablevbreakinfo != null)
            vbreakinfo = tablevbreakinfo.getItemBreakInfo(tableelementpanel);
        Rectangle rectangle = tableelementpanel.getBounds();
        Graphics g1 = g.create(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        try
        {
            tableelementpanel.print(g1, vbreakinfo);
        }
        finally
        {
            g1.dispose();
        }
    }

    private boolean isRowStart(int i, int j)
    {
        if(j == 0)
            return true;
        TableElementPanel tableelementpanel = tableGridArray[j][i];
        return tableelementpanel != null && tableelementpanel != tableGridArray[j - 1][i];
    }

    private boolean isColStart(int i, int j)
    {
        if(i == 0)
            return true;
        TableElementPanel tableelementpanel = tableGridArray[j][i];
        return tableelementpanel != null && tableelementpanel != tableGridArray[j][i - 1];
    }

    boolean needsBorder()
    {
        return hasBorder;
    }

    public Enumeration enumeratePanels()
    {
        return new TableElementPanelEnumerator(this);
    }

    public TagAppletPanel getAppletPanel(String s)
    {
        for(Enumeration enumeration = enumeratePanels(); enumeration.hasMoreElements();)
        {
            TableElementPanel tableelementpanel = (TableElementPanel)enumeration.nextElement();
            TagAppletPanel tagappletpanel = tableelementpanel.getAppletPanel(s);
            if(tagappletpanel != null)
                return tagappletpanel;
        }

        return null;
    }

    public void getAppletPanels(Vector vector)
    {
        TableElementPanel tableelementpanel;
        for(Enumeration enumeration = enumeratePanels(); enumeration.hasMoreElements(); tableelementpanel.getAppletPanels(vector))
            tableelementpanel = (TableElementPanel)enumeration.nextElement();

    }

    void format(FormatState formatstate)
    {
        maxWidth = formatstate.maxWidth;
        layout();
    }

    public int getHeight()
    {
        validate();
        return tableHeight + 2 * (borderWidth + cellSpacing);
    }

    int getAvailableWidth()
    {
        int i = maxWidth != 0 ? maxWidth : parentFormatter.getAvailableWidth(item.getIndex() << 12);
        if(givenWidth.isSet())
            if(givenWidth.isPercentage())
                i = (int)((double)i * ((double)givenWidth.getValue() / 100D));
            else
                i = givenWidth.getValue();
        return i - 2 * (borderWidth + cellSpacing);
    }

    int getGivenHeight()
    {
        if(givenHeight.isSet())
        {
            if(givenHeight.isPercentage())
                return (int)(((double)givenHeight.getValue() / 100D) * (double)parentFormatter.getAvailableHeight());
            else
                return givenHeight.getValue();
        } else
        {
            return -1;
        }
    }

    public void layout()
    {
        tableGridArray = new TableElementPanel[numRows][numCols];
        isUpperLeftArray = new boolean[numRows][numCols];
        for(int i = 0; i < numRows; i++)
        {
            for(int j = 0; j < numCols; j++)
            {
                tableGridArray[i][j] = tableGrid.elementAt(j, i);
                if(tableGridArray[i][j] == null)
                    isUpperLeftArray[i][j] = false;
                else
                    isUpperLeftArray[i][j] = tableGrid.isUpperLeft(j, i);
            }

        }

        initInfoVectors();
        measureTableWidth();
        scaleWidthForPercentages();
        distributeLeftoverSpace(tableWidth, colInfo);
        setFormatterWidths();
        alignCommonBaselines();
        measureTableHeight();
        tableHeight = scaleForPercentages(rowInfo, tableHeight);
        layoutSubPanels();
    }

    private void setFormatterWidths()
    {
        new Measurement();
        for(int i = 0; i < numRows; i++)
        {
            for(int j = 0; j < numCols; j++)
                if(isUpperLeftArray[i][j])
                {
                    TableElementPanel tableelementpanel = tableGridArray[i][j];
                    int k = 0;
                    for(int l = j; l < numCols && tableelementpanel == tableGridArray[i][l]; l++)
                        k += colInfo.getAssignedSize(l) + cellSpacing;

                    tableelementpanel.setAssignedWidth(k);
                }

        }

    }

    private void alignCommonBaselines()
    {
        Measurement measurement = new Measurement();
        for(int i = 0; i < numRows; i++)
        {
            int j = 0;
            for(int k = 0; k < numCols; k++)
                if(isUpperLeftArray[i][k])
                {
                    TableElementPanel tableelementpanel = tableGridArray[i][k];
                    j = Math.max(j, tableelementpanel.getBaseline());
                }

            for(int l = 0; l < numCols; l++)
                if(isUpperLeftArray[i][l])
                {
                    TableElementPanel tableelementpanel1 = tableGridArray[i][l];
                    int i1 = tableelementpanel1.getElementHeight();
                    tableelementpanel1.setCommonBaseline(j);
                    int j1 = tableelementpanel1.getAdjustedElementHeight();
                    if(i1 < j1)
                    {
                        measurement.reset();
                        measurement.setMinWidth(j1);
                        measurement.setPreferredWidth(j1);
                        rowInfo.addConstraintInfo(i, tableelementpanel1.getRowSpan(), measurement, -1);
                    }
                }

        }

    }

    private void layoutSubPanels()
    {
        int i = borderWidth + cellSpacing;
        for(int j = 0; j < numRows; j++)
        {
            int k = borderWidth + cellSpacing;
            for(int l = 0; l < numCols; l++)
            {
                if(isUpperLeftArray[j][l])
                {
                    TableElementPanel tableelementpanel = tableGridArray[j][l];
                    setPanelSize(tableelementpanel, l, j);
                    tableelementpanel.doVerticalAlignment();
                    if(j == 0 && captionPanel != null && captionAlignment == 0)
                        tableelementpanel.setLocation(k, i - borderWidth);
                    else
                    if(j == numRows - 1 && captionPanel != null && captionAlignment == 5)
                        tableelementpanel.setLocation(k, i + borderWidth);
                    else
                        tableelementpanel.setLocation(k, i);
                }
                k += colInfo.getAssignedSize(l) + cellSpacing;
            }

            i += rowInfo.getAssignedSize(j) + cellSpacing;
        }

        tableHeight = Math.max(i - borderWidth - 2 * cellSpacing, getGivenHeight());
        resizeTableToFit();
    }

    private void setPanelSize(TableElementPanel tableelementpanel, int i, int j)
    {
        int k = -cellSpacing;
        int l = -cellSpacing;
        int i1 = j + tableelementpanel.getRowSpan();
        int j1 = i + tableelementpanel.getColSpan();
        for(int k1 = j; k1 < i1; k1++)
            k += rowInfo.getAssignedSize(k1) + cellSpacing;

        for(int l1 = i; l1 < j1; l1++)
            l += colInfo.getAssignedSize(l1) + cellSpacing;

        tableelementpanel.setSize(l, k);
    }

    private void measureTableWidth()
    {
        Measurement measurement = new Measurement();
        MeasureState measurestate = new MeasureState();
        measure(measurement, measurestate);
        tableWidth = measurement.getPreferredWidth();
        minTableWidth = measurement.getMinWidth();
    }

    private void scaleWidthForPercentages()
    {
        if(getDesiredWidth() > 0)
        {
            if(!givenWidth.isPercentage() && givenWidth.getValue() < minTableWidth)
            {
                String s = Integer.toString(minTableWidth);
                givenWidth = new Length(s);
            }
            scaleWidthWithFixedTableSize();
            return;
        }
        Measurement measurement = new Measurement();
        int i = getAvailableWidth();
        setForMinTableWidth(measurement, i);
        if(minTableWidth >= i)
            tableWidth = minTableWidth;
        adjustCellWidthsPerRow(measurement, i);
        scalePercentageColumnsByRow(measurement);
        tableWidth = growForSpanners(true, colInfo, tableWidth);
        apportionRemainingColumnSpace();
        tableWidth = sumAssignedSizes(colInfo);
        if(tableWidth > i)
            scaleWidthBackToFit();
    }

    private void setForMinTableWidth(Measurement measurement, int i)
    {
        for(int j = 0; j < numRows; j++)
        {
            int k = -cellSpacing;
            int l = -cellSpacing;
            for(int i1 = 0; i1 < numCols; i1++)
            {
                TableElementPanel tableelementpanel = tableGridArray[j][i1];
                if(tableelementpanel != null)
                {
                    int k1 = i1 + 1;
                    TableElementPanel tableelementpanel2 = k1 >= numCols ? null : tableGridArray[j][k1];
                    if(tableelementpanel2 != tableelementpanel)
                    {
                        measurement.reset();
                        tableelementpanel.measure(measurement);
                        int i2 = measurement.getMinWidth();
                        l += measurement.getPreferredWidth() + cellSpacing;
                        if(tableelementpanel.getColSpan() == 1)
                        {
                            int j2 = colInfo.getAssignedSize(i1);
                            if(j2 < i2)
                                colInfo.setAssignedSize(i1, i2);
                        }
                        k += i2 + cellSpacing;
                    }
                }
            }

            minTableWidth = Math.max(minTableWidth, Math.max(k, sumAssignedSizes(colInfo)));
            if(tableWidth < l)
                tableWidth = l;
            if(minTableWidth <= i)
            {
                for(int j1 = 0; j1 < numCols; j1++)
                {
                    TableElementPanel tableelementpanel1 = tableGridArray[j][j1];
                    if(tableelementpanel1 != null)
                    {
                        int l1 = j1 + 1;
                        TableElementPanel tableelementpanel3 = l1 >= numCols ? null : tableGridArray[j][l1];
                        if(tableelementpanel3 != tableelementpanel1)
                        {
                            measurement.reset();
                            tableelementpanel1.measure(measurement);
                            Length length = tableelementpanel1.getCellWidth();
                            if(length.isSet() && length.isPercentage() && !tableelementpanel1.isEmpty())
                            {
                                int k2 = (int)(((double)measurement.getPreferredWidth() * 100D) / (double)length.getValue());
                                tableWidth = tableWidth < k2 ? k2 : tableWidth;
                            }
                        }
                    }
                }

            }
        }

    }

    private void adjustCellWidthsPerRow(Measurement measurement, int i)
    {
        int j = 0;
        for(int k = 0; k < numCols; k++)
        {
            int l = 10000;
            int j1 = -1;
            int l1 = 0;
            int i2 = 0;
            int j2 = 0;
            int k2 = 0;
            boolean flag = false;
            int i3 = -1;
            int j3 = 0;
            for(int k3 = 0; k3 < numRows; k3++)
            {
                TableElementPanel tableelementpanel = tableGridArray[k3][k];
                if(tableelementpanel != null && tableelementpanel.getColSpan() == 1)
                {
                    measurement.reset();
                    tableelementpanel.measure(measurement);
                    l1 = Math.max(l1, measurement.getMinWidth());
                    int l2 = measurement.getPreferredWidth();
                    i2 = Math.max(i2, l2);
                    Length length = tableelementpanel.getCellWidth();
                    if(length.isSet())
                    {
                        int l3 = length.getValue();
                        if(length.isPercentage() && !tableelementpanel.isEmpty())
                        {
                            if(l > l3)
                                l = l3;
                            if(j1 < l3)
                                j1 = l3;
                        } else
                        {
                            j2 = Math.max(j2, l3);
                        }
                    } else
                    {
                        k2 = i2;
                    }
                }
            }

            if(j1 > 0)
            {
                if(j2 > 0)
                {
                    i3 = l;
                    j3 = Math.max(j2, l1);
                } else
                {
                    i3 = j1;
                    j3 = i2;
                }
                j += i3;
            } else
            {
                i3 = -1;
                j3 = j2;
                if(k2 > 0)
                    j3 = Math.max(j3, i2);
            }
            colInfo.setAssignedSize(k, j3, i3);
        }

        if(j > 100)
        {
            for(int i1 = 0; i1 < numCols; i1++)
            {
                int k1 = colInfo.getPercentage(i1);
                if(k1 > 0)
                    colInfo.setPercentage(i1, Math.max(1, (k1 * 100) / j));
            }

        }
    }

    private void scalePercentageColumnsByRow(Measurement measurement)
    {
        int i = 0;
        int j = 0;
        int k = 10000;
        int l = 0;
        for(int i1 = 0; i1 < numCols; i1++)
        {
            int j1 = colInfo.getPercentage(i1);
            int l1 = colInfo.getAssignedSize(i1);
            if(j1 == -1)
            {
                i += l1;
            } else
            {
                j += j1;
                if(k > j1)
                    k = j1;
                int j2 = (l1 * 100) / j1;
                if(j2 > l)
                    l = j2;
            }
        }

        int k1 = 0;
        if(i != 0)
        {
            if(j == 100)
                j--;
            k1 = (i * 100) / (100 - j);
        }
        tableWidth = Math.max(tableWidth, Math.max(l, k1));
        for(int i2 = 0; i2 < numCols; i2++)
        {
            int k2 = colInfo.getPercentage(i2);
            if(k2 != -1)
            {
                int l2 = (tableWidth * k2) / 100;
                colInfo.setAssignedSize(i2, l2);
            }
        }

    }

    private void apportionRemainingColumnSpace()
    {
        int i = 0;
        int j = 0;
        for(int k = 0; k < numCols; k++)
            if(isPercent(k))
                i += colInfo.getAssignedSize(k);
            else
                j += getPreferredColWidth(k);

        if(i == 0)
            return;
        if(j > 0)
        {
            int l = tableWidth - i;
            for(int i1 = 0; i1 < numCols; i1++)
                if(!isPercent(i1))
                {
                    int j1 = (getPreferredColWidth(i1) * l) / j;
                    colInfo.setAssignedSize(i1, j1);
                }

        }
    }

    private void scaleWidthUpToFit()
    {
        int i = cellSpacing * (numCols - 1);
        int j = getDesiredWidth();
        double d = ((double)j - (double)i) / (double)(tableWidth - i);
        tableWidth = j;
        for(int k = 0; k < numCols; k++)
        {
            int l = colInfo.getAssignedSize(k);
            l = (int)((double)l * d);
            colInfo.setAssignedSize(k, l);
        }

    }

    private int getDesiredWidth()
    {
        int i = maxWidth != 0 ? maxWidth : parentFormatter.getAvailableWidth(item.getIndex() << 12);
        int j = 0;
        if(givenWidth.isSet())
        {
            if(givenWidth.isPercentage())
                j = (int)((double)i * ((double)givenWidth.getValue() / 100D));
            else
                j = givenWidth.getValue();
            j -= 2 * (borderWidth + cellSpacing);
        }
        return j;
    }

    private boolean isPercent(int i)
    {
        for(int j = 0; j < numRows; j++)
        {
            TableElementPanel tableelementpanel = tableGridArray[j][i];
            if(tableelementpanel != null && tableelementpanel.getColSpan() <= 1)
            {
                Length length = tableelementpanel.getCellWidth();
                if(length.isSet() && length.isPercentage())
                    return true;
            }
        }

        return false;
    }

    private int getPreferredColWidth(int i)
    {
        Measurement measurement = new Measurement();
        int j = 0;
        for(int k = 0; k < numRows; k++)
        {
            TableElementPanel tableelementpanel = tableGridArray[k][i];
            if(tableelementpanel != null && tableelementpanel.getColSpan() <= 1)
            {
                measurement.reset();
                tableelementpanel.measure(measurement);
                j = Math.max(j, measurement.getPreferredWidth());
            }
        }

        return j;
    }

    private int getMinColWidth(int i)
    {
        Measurement measurement = new Measurement();
        int j = 0;
        for(int k = 0; k < numRows; k++)
        {
            TableElementPanel tableelementpanel = tableGrid.elementAt(i, k);
            if(tableelementpanel != null && tableelementpanel.getColSpan() <= 1)
            {
                measurement.reset();
                tableelementpanel.measure(measurement);
                j = Math.max(j, measurement.getMinWidth());
            }
        }

        return j;
    }

    private void setMinColWidth(int i, int j)
    {
        if(i < 0 || i > numCols - 1)
            return;
        for(int k = 0; k < numRows; k++)
        {
            TableElementPanel tableelementpanel = tableGridArray[k][i];
            if(tableelementpanel != null && tableelementpanel.getColSpan() <= 1)
            {
                tableelementpanel.setMinWidth(j);
                return;
            }
        }

    }

    private void scaleWidthWithFixedTableSize()
    {
        int i = getDesiredWidth();
        Measurement measurement = new Measurement();
        boolean aflag[] = new boolean[numRows];
        minTableWidth = -cellSpacing;
        int ai[] = new int[numCols];
        for(int j = 0; j < numCols; j++)
        {
            int k = -1;
            int i1 = -1;
            ai[j] = 0;
            for(int k1 = 0; k1 < numRows; k1++)
            {
                TableElementPanel tableelementpanel = tableGrid.elementAt(j, k1);
                if(tableelementpanel != null)
                    if(tableelementpanel.getColSpan() > 1)
                    {
                        aflag[k1] = true;
                    } else
                    {
                        measurement.reset();
                        tableelementpanel.measure(measurement);
                        ai[j] = Math.max(ai[j], measurement.getMinWidth());
                        Length length = tableelementpanel.getCellWidth();
                        if(length.isSet())
                        {
                            if(length.isPercentage())
                                k = Math.max(k, (i * length.getValue()) / 100);
                            else
                                k = Math.max(k, length.getValue());
                        } else
                        {
                            i1 = Math.max(i1, measurement.getPreferredWidth());
                        }
                    }
            }

            minTableWidth += ai[j] + cellSpacing;
            if(k < 0)
                k = i1;
            else
                k = Math.max(k, ai[j]);
            colInfo.setAssignedSize(j, k);
        }

        int l = 0;
        for(int j1 = 0; j1 < numRows; j1++)
            if(aflag[j1])
            {
                int l1 = -cellSpacing;
                for(int i2 = 0; i2 < numCols; i2++)
                {
                    TableElementPanel tableelementpanel1 = tableGridArray[j1][i2];
                    if(tableelementpanel1 != null)
                        if(tableelementpanel1.getColSpan() > 1)
                        {
                            measurement.reset();
                            tableelementpanel1.measure(measurement);
                            l1 += measurement.getMinWidth() + cellSpacing;
                            i2 += tableelementpanel1.getColSpan() - 1;
                        } else
                        {
                            l1 += ai[i2] + cellSpacing;
                        }
                }

                l = Math.max(l, l1);
            }

        minTableWidth = Math.max(minTableWidth, l);
        tableWidth = Math.max(sumAssignedSizes(colInfo), minTableWidth);
        tableWidth = growForSpanners(true, colInfo, tableWidth);
        if(tableWidth > i)
        {
            scaleWidthBackToFit();
            return;
        }
        if(tableWidth < i && !expandUnconstrainedColumns())
            scaleWidthUpToFit();
    }

    private boolean expandUnconstrainedColumns()
    {
        int i = 0;
        int j = -1;
        for(int k = 0; k < numCols; k++)
            if(!colIsConstrained(k))
            {
                i++;
                j = k;
            }

        if(i == 0)
            return false;
        if(i == 1)
        {
            int l = getDesiredWidth();
            int j1 = colInfo.getAssignedSize(j);
            j1 += l - tableWidth;
            colInfo.setAssignedSize(j, j1);
            tableWidth = l;
            return true;
        }
        int i1 = getDesiredWidth();
        int k1 = i;
        for(int l1 = 0; k1 > 0 && l1 < numCols; l1++)
            if(!colIsConstrained(l1))
            {
                int i2 = (i1 - tableWidth) / k1;
                int j2 = colInfo.getAssignedSize(l1);
                j2 += i2;
                tableWidth += i2;
                colInfo.setAssignedSize(l1, j2);
                k1--;
            }

        return true;
    }

    private boolean colIsConstrained(int i)
    {
        for(int j = 0; j < numRows; j++)
        {
            TableElementPanel tableelementpanel = tableGrid.elementAt(i, j);
            if(tableelementpanel != null && tableelementpanel.getColSpan() <= 1)
            {
                Length length = tableelementpanel.getCellWidth();
                if(length.isSet())
                    return true;
            }
        }

        return false;
    }

    private int sumAssignedSizes(CellInfoVector cellinfovector)
    {
        int i = cellinfovector.length();
        int j = -cellSpacing;
        for(int k = 0; k < i; k++)
            j += cellinfovector.getAssignedSize(k) + cellSpacing;

        return j;
    }

    private void distributeLeftoverSpace(int i, CellInfoVector cellinfovector)
    {
        int j = i - sumAssignedSizes(cellinfovector);
        if(j > 0)
        {
            int k = cellinfovector.length();
            if(k > 0)
            {
                int l = ((j + k) - 1) / k;
                for(int i1 = 0; i1 < k && j > 0; i1++)
                {
                    int j1 = cellinfovector.getAssignedSize(i1) + l;
                    cellinfovector.setAssignedSize(i1, j1);
                    j -= l;
                }

            }
        }
    }

    private void scaleWidthBackToFit()
    {
        int i = getAvailableWidth();
        i = Math.max(i, minTableWidth);
        int j = cellSpacing * (numCols - 1);
        double d = 1.0D;
        if(tableWidth != minTableWidth)
            d = ((double)i - (double)j - (double)minTableWidth) / (double)(tableWidth - minTableWidth);
        tableWidth = i;
        for(int k = 0; k < numCols; k++)
        {
            int l = colInfo.getAssignedSize(k);
            int i1 = getMinColWidth(k);
            l = i1 + (int)((double)(l - i1) * d);
            colInfo.setAssignedSize(k, l);
        }

    }

    private int scaleForPercentages(CellInfoVector cellinfovector, int i)
    {
        int j = i;
        int k = cellinfovector.getMaxSpan();
        int l = cellinfovector.length();
        for(int i1 = 1; i1 <= k; i1++)
        {
            for(int j1 = 0; j1 < l; j1++)
            {
                ConstraintSummary constraintsummary = cellinfovector.getConstraintFor(j1, i1);
                if(constraintsummary != null && constraintsummary.minPercent >= 0)
                {
                    double d = (double)constraintsummary.minPercent / 100D;
                    int k1 = constraintsummary.prefSize;
                    int l1 = getSubordinateLength(cellinfovector, j1, i1);
                    k1 = Math.max(k1, l1);
                    if(d * (double)j < (double)k1)
                        j = (int)((double)k1 / d);
                }
            }

        }

        return Math.max(j, getGivenHeight());
    }

    private int growForSpanners(boolean flag, CellInfoVector cellinfovector, int i)
    {
        int j = cellinfovector.getMaxSpan();
        int k = cellinfovector.length();
        int l = i;
        int ai[] = new int[k];
        for(int i1 = 0; i1 < k; i1++)
            ai[i1] = cellinfovector.getAssignedSize(i1);

        for(int j1 = 2; j1 <= j; j1++)
        {
            for(int k1 = 0; k1 < k; k1++)
            {
                ConstraintSummary constraintsummary = cellinfovector.getConstraintFor(k1, j1);
                if(constraintsummary != null)
                {
                    int l1 = Math.max((k1 - j1) + 1, 0);
                    int i2 = constraintsummary.minSize + cellSpacing;
                    int j2 = -cellSpacing;
                    int k2 = -cellSpacing;
                    for(int l2 = l1; l2 <= k1; l2++)
                    {
                        if(flag)
                            k2 += getMinColWidth(l2) + cellSpacing;
                        j2 += ai[l2] + cellSpacing;
                    }

                    if(i2 > j2)
                    {
                        int i3 = (j1 - 1) * cellSpacing;
                        double d = 1.0D;
                        if(j2 - i3 > 0)
                            d = (double)(i2 - i3) / (double)(j2 - i3);
                        int l3 = i2 - j2;
                        l += l3;
                        int i4 = -cellSpacing;
                        for(int j4 = l1; j4 <= k1; j4++)
                        {
                            int k4 = ai[j4];
                            int i5 = (int)((double)k4 * d);
                            i4 += i5 + cellSpacing;
                            cellinfovector.setAssignedSize(j4, i5);
                            if(flag)
                                setMinColWidth(j4, i5);
                        }

                        int l4 = i2 - i4;
                        if(l4 > 0)
                        {
                            int j5 = cellinfovector.getAssignedSize(k1);
                            j5 += l4;
                            cellinfovector.setAssignedSize(k1, j5);
                            if(flag)
                                setMinColWidth(k1, j5);
                        }
                    } else
                    if(flag && i2 > k2)
                    {
                        float f = (float)i2 / (float)j2;
                        for(int j3 = l1; j3 <= k1; j3++)
                        {
                            int k3 = ai[j3];
                            setMinColWidth(j3, (int)(f * (float)k3));
                        }

                    }
                }
            }

        }

        return l;
    }

    private int getMinSize(CellInfoVector cellinfovector)
    {
        int i = cellinfovector.length() - 1;
        CellSpec cellspec = cellinfovector.cells[i];
        return cellspec.cumulativeMinSize;
    }

    private void apportionSpace(CellInfoVector cellinfovector, int i)
    {
        int k = cellinfovector.length();
        int l = -cellSpacing;
        for(int i1 = 0; i1 < k; i1++)
        {
            int j1 = 0;
            ConstraintSummary constraintsummary = cellinfovector.getConstraintFor(i1, 1);
            if(constraintsummary != null)
                j1 = constraintsummary.prefSize;
            cellinfovector.setAssignedSize(i1, j1);
            l += j1 + cellSpacing;
        }

        if(l < i)
        {
            int k1 = cellSpacing * (k - 1);
            double d = (double)(i - k1) / (double)(l - k1);
            for(int l1 = 0; l1 < k; l1++)
            {
                int i2 = cellinfovector.getAssignedSize(l1);
                ConstraintSummary constraintsummary1 = cellinfovector.getConstraintFor(l1, 1);
                int j;
                if(constraintsummary1 == null || constraintsummary1.minPercent < 0)
                    j = (int)((double)i2 * d);
                else
                    j = (int)(((double)constraintsummary1.minPercent / 100D) * (double)i);
                cellinfovector.setAssignedSize(l1, j);
            }

        }
    }

    private void measureTableHeight()
    {
        Measurement measurement = new Measurement();
        for(int i = 0; i < numRows; i++)
        {
            for(int j = 0; j < numCols; j++)
            {
                TableElementPanel tableelementpanel = tableGridArray[i][j];
                if(tableelementpanel != null)
                {
                    TableElementPanel tableelementpanel1 = i + 1 >= numRows ? null : tableGridArray[i + 1][j];
                    if(tableelementpanel1 != tableelementpanel)
                    {
                        int l = tableelementpanel.getElementHeight();
                        int i1 = -1;
                        Length length = tableelementpanel.getCellHeight();
                        if(length.isSet())
                            if(length.isPercentage())
                                i1 = length.getValue();
                            else
                            if(l < length.getValue())
                                l = length.getValue();
                        measurement.reset();
                        measurement.setMinWidth(l);
                        measurement.setPreferredWidth(l);
                        int j1 = tableelementpanel.getRowSpan();
                        rowInfo.addConstraintInfo(i, j1, measurement, i1);
                    }
                }
            }

            int k = 0;
            ConstraintSummary constraintsummary = rowInfo.getConstraintFor(i, 1);
            if(constraintsummary != null)
                k = constraintsummary.prefSize;
            rowInfo.setAssignedSize(i, k);
        }

        growForSpanners(false, rowInfo, 0);
        tableHeight = Math.max(sumAssignedSizes(rowInfo), getGivenHeight());
    }

    private void resizeTableToFit()
    {
        setSize(tableWidth + 2 * (borderWidth + cellSpacing), tableHeight + 2 * (borderWidth + cellSpacing));
    }

    void measureWidth(Measurement measurement, MeasureState measurestate)
    {
        measure(measurement, measurestate);
        measurement.adjustBy(2 * (borderWidth + cellSpacing));
    }

    void measure(Measurement measurement, MeasureState measurestate)
    {
        Measurement measurement1 = new Measurement();
        for(int i = 0; i < numCols; i++)
        {
            for(int j = 0; j < numRows; j++)
            {
                TableElementPanel tableelementpanel = tableGrid.elementAt(i, j);
                if(tableelementpanel != null)
                {
                    TableElementPanel tableelementpanel1 = tableGrid.elementAt(i + 1, j);
                    if(tableelementpanel1 != tableelementpanel)
                    {
                        measurement1.reset();
                        tableelementpanel.measure(measurement1);
                        int l = -1;
                        Length length = tableelementpanel.getCellWidth();
                        if(length.isSet() && length.isPercentage())
                            l = length.getValue();
                        colInfo.addConstraintInfo(i, tableelementpanel.getColSpan(), measurement1, l);
                    }
                }
            }

        }

        colInfo.measure(measurement);
        if(givenWidth.isSet())
        {
            int k;
            if(givenWidth.isPercentage())
                k = (int)(((double)givenWidth.getValue() / 100D) * (double)(maxWidth != 0 ? maxWidth : parentFormatter.getAvailableWidth(item.getIndex() << 12)));
            else
                k = givenWidth.getValue();
            if(k > measurement.getMinWidth())
            {
                measurement.setMinWidth(k);
                measurement.setPreferredWidth(k);
                return;
            }
            measurement.setPreferredWidth(measurement.getMinWidth());
        }
    }

    private int getSubordinateLength(CellInfoVector cellinfovector, int i, int j)
    {
        if(j == 1)
            return 0;
        int k = -cellSpacing;
        for(int l = (i - j) + 1; l <= i; l++)
        {
            ConstraintSummary constraintsummary = cellinfovector.getConstraintFor(l, 1);
            if(constraintsummary != null)
                k += constraintsummary.prefSize + cellSpacing;
        }

        return k;
    }

    private int getMinSubordinateLength(CellInfoVector cellinfovector, int i, int j)
    {
        if(j == 1)
            return 0;
        int k = -cellSpacing;
        for(int l = (i - j) + 1; l <= i; l++)
        {
            ConstraintSummary constraintsummary = cellinfovector.getConstraintFor(l, 1);
            if(constraintsummary != null)
                k += constraintsummary.minSize + cellSpacing;
        }

        return k;
    }

    public Formatter getParentFormatter()
    {
        return parentFormatter;
    }

    void touch(boolean flag, int i)
    {
        parentFormatter.touch(flag, i, item);
    }

    void touch(boolean flag, int i, DocItem docitem)
    {
        parentFormatter.touch(flag, i, item);
    }

    public void addTablePanelFormatters(Vector vector)
    {
        TableElementPanel tableelementpanel;
        for(Enumeration enumeration = enumeratePanels(); enumeration.hasMoreElements(); vector.addElement(tableelementpanel.current))
            tableelementpanel = (TableElementPanel)enumeration.nextElement();

    }

    public void setObsolete(boolean flag)
    {
    }

    private static final int RAISED_BORDER_THRESHOLD = 3;
    private Document doc;
    private TagItem item;
    private Formatter parentFormatter;
    private int border;
    private int borderWidth;
    private Length givenWidth;
    private Length givenHeight;
    private int cellPadding;
    private int cellSpacing;
    private int numRows;
    private int numCols;
    private TableElementPanel tableGridArray[][];
    private boolean isUpperLeftArray[][];
    private int maxRowSpan;
    private int maxColSpan;
    private int tableAlignment;
    private int captionAlignment;
    private DocItem captionItem;
    private TableElementPanel captionPanel;
    private boolean hasBorder;
    private TableGrid tableGrid;
    private CellInfoVector colInfo;
    private CellInfoVector rowInfo;
    private int tableWidth;
    private int tableHeight;
    private int minTableWidth;
    private int maxWidth;
    boolean suppressRepaint;
    boolean firstCall;
    private int prevX;
    private int prevY;
}
