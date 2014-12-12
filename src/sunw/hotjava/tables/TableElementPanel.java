// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TableElementPanel.java

package sunw.hotjava.tables;

import java.awt.*;
import java.util.Vector;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.Length;
import sunw.hotjava.tags.TagAppletPanel;

// Referenced classes of package sunw.hotjava.tables:
//            DeliberatelyPaintable, TableElementFormatter, TablePanel, BGColorInfo, 
//            TableVBreakInfo

public class TableElementPanel extends Container
    implements DocPanel, DocConstants, FormatterOwner, DeliberatelyPaintable
{

    public TableElementPanel(Document document, DocumentState documentstate, DocItem docitem, DocItem docitem1, DocItem docitem2, TablePanel tablepanel, Length length, 
            Length length1, int i, int j, int k, BGColorInfo bgcolorinfo)
    {
        isCaption = false;
        setLayout(null);
        owner = tablepanel;
        isCaption = isTag(document, docitem, "caption");
        hasBorder = owner.needsBorder() && !isCaption;
        padding = getCellPadding();
        if(docitem.getOffset() > 1 && hasBorder)
            padding++;
        current = new TableElementFormatter(this, documentstate, document, docitem, docitem1, docitem2, length, length1, i, j, k, bgcolorinfo);
    }

    TableElementPanel(TableElementPanel tableelementpanel, TablePanel tablepanel, DocumentState documentstate)
    {
        isCaption = false;
        setLayout(null);
        owner = tablepanel;
        padding = tableelementpanel.padding;
        hasBorder = tableelementpanel.hasBorder;
        isCaption = tableelementpanel.isCaption;
        current = new TableElementFormatter(tableelementpanel.current, this, documentstate);
    }

    public Formatter getFormatter()
    {
        return current;
    }

    private boolean isCaption()
    {
        return isCaption;
    }

    private boolean isTag(Document document, DocItem docitem, String s)
    {
        TagItem tagitem = docitem.getTag(document);
        if(tagitem == null)
            return false;
        else
            return s.equals(tagitem.getName());
    }

    public void paint(Graphics g)
    {
        paintBackgroundAndWell(g);
        if(current != null)
            current.paint(g);
    }

    public void repaint()
    {
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paintDeliberately(Graphics g)
    {
        paint(g);
    }

    int findSplitY(int i, int j, VBreakInfo vbreakinfo)
    {
        if(current != null)
            return current.findSplitY(i, j, (FormatterVBreakInfo)vbreakinfo);
        else
            return i;
    }

    void recordBreakInfo(int i, int j, TableVBreakInfo tablevbreakinfo, TableVBreakInfo tablevbreakinfo1)
    {
        FormatterVBreakInfo formattervbreakinfo = (FormatterVBreakInfo)tablevbreakinfo.getItemBreakInfo(this);
        if(formattervbreakinfo == null)
        {
            formattervbreakinfo = new FormatterVBreakInfo();
            tablevbreakinfo.setItemBreakInfo(this, formattervbreakinfo);
        }
        FormatterVBreakInfo formattervbreakinfo1 = new FormatterVBreakInfo();
        tablevbreakinfo1.setItemBreakInfo(this, formattervbreakinfo1);
        if(current != null)
            current.recordBreakInfo(i, j, formattervbreakinfo, formattervbreakinfo1);
    }

    void recordFloaterBreakInfo(int i, int j, TableVBreakInfo tablevbreakinfo, TableVBreakInfo tablevbreakinfo1)
    {
        FormatterVBreakInfo formattervbreakinfo = (FormatterVBreakInfo)tablevbreakinfo.getItemBreakInfo(this);
        if(formattervbreakinfo == null)
        {
            formattervbreakinfo = new FormatterVBreakInfo();
            tablevbreakinfo.setItemBreakInfo(this, formattervbreakinfo);
        }
        FormatterVBreakInfo formattervbreakinfo1 = new FormatterVBreakInfo();
        tablevbreakinfo1.setItemBreakInfo(this, formattervbreakinfo1);
        if(current != null)
            current.recordBreakInfo(i, j, formattervbreakinfo, formattervbreakinfo1);
    }

    public void print(Graphics g, VBreakInfo vbreakinfo)
    {
        if(hasBorder && !isEmpty())
            drawBorder(g);
        if(current != null)
            current.print(g, (FormatterVBreakInfo)vbreakinfo);
    }

    private void paintBackgroundAndWell(Graphics g)
    {
        paintBack(g);
        if(hasBorder && !isEmpty())
            drawWell(g, getBackground());
    }

    private void drawBorder(Graphics g)
    {
        Color color = g.getColor();
        g.setColor(Color.black);
        Dimension dimension = getSize();
        g.drawRect(0, 0, dimension.width - 1, dimension.height - 1);
        g.setColor(color);
    }

    private void drawWell(Graphics g, Color color)
    {
        Color color1 = g.getColor();
        if(color != null)
            g.setColor(Globals.getVisible3DColor(color));
        Dimension dimension = getSize();
        g.draw3DRect(0, 0, dimension.width - 1, dimension.height - 1, false);
        g.setColor(color1);
    }

    boolean isEmpty()
    {
        return current.getNumItems() == 0;
    }

    private void paintBack(Graphics g)
    {
        DocumentState documentstate = current.getDocumentState();
        Dimension dimension = getSize();
        if(documentstate.bg != null && !current.isFormatterBackgroundColorDeliberate())
        {
            Point point = new Point(0, 0);
            getBackgroundDisplacement(point);
            documentstate.bg.paint(g, point.x, point.y, 0, 0, dimension.width, dimension.height);
            return;
        }
        if(getBackground() != null)
        {
            Color color = g.getColor();
            g.setColor(getBackground());
            g.fillRect(0, 0, dimension.width, dimension.height);
            g.setColor(color);
        }
    }

    boolean paintRangeNoFloaters(Graphics g, int i, int j, boolean flag)
    {
        return current.paintRangeNoFloaters(g, i, j, flag);
    }

    boolean paintRange(Graphics g, int i, int j, boolean flag)
    {
        return current.paintRange(g, i, j, flag);
    }

    boolean intersectsRange(int i, int j)
    {
        return i >> 12 < current.getMaxIndex() && current.getStartIndex() <= j >> 12;
    }

    void getFormPanel(Vector vector)
    {
        current.getFormPanel(vector);
    }

    public int getCellPadding()
    {
        return owner.getCellPadding();
    }

    public int getCellSpacing()
    {
        return owner.getCellSpacing();
    }

    public int getBorderThickness()
    {
        return !hasBorder || isEmpty() ? 0 : 1;
    }

    int getColSpan()
    {
        return current.getColSpan();
    }

    int getRowSpan()
    {
        return current.getRowSpan();
    }

    void adjustSpanBy(int i, boolean flag)
    {
        current.adjustSpanBy(i, flag);
    }

    void doVerticalAlignment()
    {
        current.doVerticalAlignment();
    }

    void setMinWidth(int i)
    {
        current.setMinWidth(i);
    }

    public Document getDocument()
    {
        if(current != null)
            return current.getDocument();
        else
            return null;
    }

    void getBackgroundDisplacement(Point point)
    {
        Point point1 = getLocation();
        point.translate(-point1.x, -point1.y);
        owner.getBackgroundDisplacement(point);
    }

    public void setSize(int i, int j)
    {
        super.setSize(i, j);
        current.setSize(i, j);
    }

    public void activateSubItems()
    {
        current.activateSubItems();
    }

    public void start()
    {
        Color color = current.getFormatterBackgroundColor();
        if(color != null)
            setBackground(color);
        current.registerListeners();
        current.start();
    }

    public void stop()
    {
        current.stop();
        current.unregisterListeners();
    }

    public void unregisterListeners()
    {
        current.unregisterListeners();
    }

    public void destroy()
    {
        current.destroy();
    }

    public void interruptLoading()
    {
        current.interruptLoading();
    }

    public void notify(Document document, int i, int j, int k)
    {
        current.notify(document, i, j, k);
    }

    public void reformat()
    {
        current.reformat();
    }

    public int findYFor(int i)
    {
        return current.findYFor(i);
    }

    public boolean containsPos(int i)
    {
        return current.containsPos(i);
    }

    TagAppletPanel getAppletPanel(String s)
    {
        return current.getAppletPanel(s);
    }

    void getAppletPanels(Vector vector)
    {
        current.getAppletPanels(vector);
    }

    void measure(Measurement measurement)
    {
        current.measure(measurement);
    }

    int getElementHeight()
    {
        return current.getElementHeight() + padding;
    }

    int getAdjustedElementHeight()
    {
        return current.getAdjustedElementHeight() + padding;
    }

    Length getCellWidth()
    {
        return current.getCellWidth();
    }

    Length getCellHeight()
    {
        return current.getCellHeight();
    }

    int getBaseline()
    {
        return current.getBaseline();
    }

    void setCommonBaseline(int i)
    {
        current.setCommonBaseline(i);
    }

    void setAssignedWidth(int i)
    {
        i -= getCellSpacing();
        current.setAssignedWidth(i);
    }

    public Formatter getParentFormatter()
    {
        return owner.getParentFormatter();
    }

    void touch(boolean flag, int i)
    {
        owner.touch(flag, i);
    }

    void touch(boolean flag, int i, DocItem docitem)
    {
        owner.touch(flag, i, docitem);
    }

    public void setObsolete(boolean flag)
    {
    }

    TableElementFormatter current;
    private int padding;
    private boolean hasBorder;
    private boolean isCaption;
    TablePanel owner;
}
