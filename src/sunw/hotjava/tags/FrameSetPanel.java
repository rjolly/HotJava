// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FrameSetPanel.java

package sunw.hotjava.tags;

import java.awt.*;
import java.util.BitSet;
import java.util.StringTokenizer;
import java.util.Vector;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.doc.*;
import sunw.html.Attributes;

// Referenced classes of package sunw.hotjava.tags:
//            EdgeInfo, FRAME, FRAMESET, FramePanel, 
//            Resizable, ResizeRequestEvent, ResizeTracker

public class FrameSetPanel extends Panel
    implements DocPanel, Resizable
{

    FrameSetPanel(Formatter formatter, TagItem tagitem, int i)
    {
        this(formatter, tagitem, null, null, i, null);
    }

    FrameSetPanel(Formatter formatter, TagItem tagitem, String as[], String as1[], int i)
    {
        this(formatter, tagitem, as, as1, i, null);
    }

    FrameSetPanel(Formatter formatter, TagItem tagitem, String as[], String as1[], int i, Container container)
    {
        kids = new Document[4];
        hasBeenResized = false;
        setLayout(null);
        doc = formatter.getDocument();
        item = tagitem;
        border = i;
        rows = parseRowColSpec("rows", tagitem.getAttributes(), as);
        cols = parseRowColSpec("cols", tagitem.getAttributes(), as1);
        initRowsAndCols();
        Container container1 = formatter.getParent();
        if(container != null)
            container.add(this);
        else
        if(container1 != null)
        {
            container1.add(this);
            if(container1 instanceof DocumentFormatterPanel)
            {
                docPanel = ((DocumentFormatterPanel)container1).getDocumentPanel();
                docPanel.dispatchDocumentEvent(1041, null);
            }
        }
        initComponents(rows.length, cols.length, formatter);
        formatter.addFrameSetPanel(this);
    }

    public FRAMESET getFrameSetTag()
    {
        return (FRAMESET)item;
    }

    private void initResizableVectors()
    {
        vertResizables = new BitSet(rows.length);
        horizResizables = new BitSet(cols.length);
        for(int i = 1; i < rows.length; i++)
            vertResizables.set(i);

        for(int j = 1; j < cols.length; j++)
            horizResizables.set(j);

    }

    public void setNotResizable(Point point)
    {
        if(point.y != 0)
            vertResizables.clear(point.y);
        else
        if(resizeListener != null)
            resizeListener.setNotResizable(gridLoc, 2);
        if(point.y != rows.length - 1)
            vertResizables.clear(point.y + 1);
        else
        if(resizeListener != null)
            resizeListener.setNotResizable(gridLoc, 3);
        if(point.x != 0)
            horizResizables.clear(point.x);
        else
        if(resizeListener != null)
            resizeListener.setNotResizable(gridLoc, 0);
        if(point.x != cols.length - 1)
        {
            horizResizables.clear(point.x + 1);
            return;
        }
        if(resizeListener != null)
            resizeListener.setNotResizable(gridLoc, 1);
    }

    public void setNotResizable(Point point, int i)
    {
        if(i == 2 && point.y != 0)
        {
            vertResizables.clear(point.y);
            return;
        }
        if(i == 3 && point.y != rows.length - 1)
        {
            vertResizables.clear(point.y + 1);
            return;
        }
        if(i == 0 && point.x != 0)
        {
            horizResizables.clear(point.x);
            return;
        }
        if(i == 1 && point.x != cols.length - 1)
        {
            horizResizables.clear(point.x + 1);
            return;
        }
        if(resizeListener != null)
            resizeListener.setNotResizable(gridLoc, i);
    }

    private void initRowsAndCols()
    {
        if(rows == null || cols == null)
            return;
        percentRowsAndCols = new int[2][];
        relativeRowsAndCols = new int[2][];
        absoluteRowsAndCols = new int[2][];
        absoluteTotals = new int[2];
        relativeTotals = new int[2];
        percentTotals = new int[2];
        Object obj = null;
        for(int i = 0; i < 2; i++)
        {
            String as[];
            if(i == 0)
                as = rows;
            else
                as = cols;
            absoluteTotals[i] = 0;
            relativeTotals[i] = 0;
            percentTotals[i] = 0;
            percentRowsAndCols[i] = new int[as.length];
            relativeRowsAndCols[i] = new int[as.length];
            absoluteRowsAndCols[i] = new int[as.length];
            for(int j = 0; j < as.length; j++)
            {
                percentRowsAndCols[i][j] = -1;
                relativeRowsAndCols[i][j] = -1;
                absoluteRowsAndCols[i][j] = -1;
                int l = as[j].indexOf("*");
                if(l != -1)
                {
                    int j1 = parseDigits(as[j].substring(0, l));
                    if(j1 > 0)
                    {
                        relativeRowsAndCols[i][j] = j1;
                        relativeTotals[i] += j1;
                    } else
                    {
                        relativeRowsAndCols[i][j] = 1;
                        relativeTotals[i]++;
                    }
                } else
                if(as[j].indexOf('%') != -1)
                {
                    percentRowsAndCols[i][j] = parseDigits(as[j]);
                    percentTotals[i] += percentRowsAndCols[i][j];
                } else
                {
                    absoluteRowsAndCols[i][j] = Integer.parseInt(as[j]);
                    absoluteTotals[i] += absoluteRowsAndCols[i][j];
                }
            }

        }

        for(int k = 0; k < 2; k++)
            if(percentTotals[k] > 100)
            {
                for(int i1 = 0; i1 < percentRowsAndCols[k].length; i1++)
                    if(percentRowsAndCols[k][i1] > 0)
                        percentRowsAndCols[k][i1] = (percentRowsAndCols[k][i1] * 100) / percentTotals[k];

                percentTotals[k] = 100;
            }

    }

    private String[] parseRowColSpec(String s, Attributes attributes, String as[])
    {
        String s1 = "*";
        if(attributes != null)
            if(attributes.get(s) != null)
                s1 = attributes.get(s);
            else
            if(as != null)
                return as;
        StringTokenizer stringtokenizer = new StringTokenizer(s1, ",");
        int i = stringtokenizer.countTokens();
        String as1[] = new String[i];
        for(int j = 0; j < i; j++)
            as1[j] = stringtokenizer.nextToken().trim();

        return as1;
    }

    private String getFrameAttribute(String s, Attributes attributes, String s1)
    {
        String s2 = s1;
        if(attributes != null)
        {
            s2 = attributes.get(s);
            if(s2 == null)
                s2 = s1;
        }
        return s2;
    }

    public void getAppletPanels(Vector vector)
    {
        for(int i = 0; i < frameList.length; i++)
        {
            Component component = frameList[i];
            if(component instanceof FramePanel)
            {
                sunw.hotjava.doc.DocumentFormatter documentformatter = ((FramePanel)component).getFormatter();
                if(documentformatter != null)
                    documentformatter.getAppletPanels(vector);
            }
        }

    }

    public Component[] getFrameList()
    {
        return frameList;
    }

    public void reload()
    {
        for(int i = 0; i < frameList.length; i++)
        {
            Component component = frameList[i];
            if(component instanceof FramePanel)
                ((FramePanel)component).reload();
            else
            if(component instanceof FrameSetPanel)
                ((FrameSetPanel)component).reload();
        }

    }

    public void updateDocument()
    {
        if(docPanel != null)
            docPanel.dispatchDocumentEvent(1041, null);
        if(doc.isExpired())
        {
            reload();
            return;
        }
        for(int i = 0; i < frameList.length; i++)
        {
            Component component = frameList[i];
            if(component instanceof FramePanel)
                ((FramePanel)component).updateDocument();
            else
            if(component instanceof FrameSetPanel)
                ((FrameSetPanel)component).updateDocument();
        }

        if(docPanel != null)
        {
            sunw.hotjava.doc.DocumentFormatter documentformatter = docPanel.getFormatter();
            if(documentformatter != null)
                documentformatter.dispatchDocumentEvent(1042, null);
        }
    }

    protected HotJavaBrowserBean getContainingHotJavaBrowserBean()
    {
        return HotJavaBrowserBean.getContainingHotJavaBrowserBean(this);
    }

    private boolean isFramesetComponent(String s, int i)
    {
        if("frame".equalsIgnoreCase(s))
            return true;
        if("noframes".equalsIgnoreCase(s))
            return false;
        if("frameset".equalsIgnoreCase(s))
            return i >= 0;
        else
            return false;
    }

    private void initComponents(int i, int j, Formatter formatter)
    {
        frameList = new Component[i * j];
        int k = item.getIndex();
        int l = k + item.getOffset();
        int i1 = 0;
        FrameSetPanel framesetpanel = findTopFrameSet(formatter);
        int j1 = k + 1;
        int k1 = 0;
        for(; j1 < l; j1++)
        {
            DocItem docitem = doc.getItem(j1);
            int l1 = docitem.getOffset();
            if(!(docitem instanceof TagItem))
                continue;
            if(!isFramesetComponent(((TagItem)docitem).getName(), l1))
            {
                if(l1 >= 0)
                    j1 += l1;
                continue;
            }
            if(k1 >= frameList.length)
            {
                makeRoomForUnspecifiedItems();
                initRowsAndCols();
            }
            if(!framesetpanel.checkAndRegisterDescendant())
                break;
            if(docitem instanceof FRAMESET)
            {
                FrameSetPanel framesetpanel1 = new FrameSetPanel(formatter, (TagItem)docitem, null, null, border, this);
                frameList[k1++] = framesetpanel1;
            } else
            if(docitem instanceof FRAME)
            {
                FRAME frame = (FRAME)docitem;
                FramePanel framepanel = new FramePanel(formatter, frame, border, this);
                if(docKids % 4 == 0 && docKids != 0)
                {
                    Document adocument[] = new Document[docKids + 4];
                    System.arraycopy(kids, 0, adocument, 0, docKids);
                    kids = adocument;
                }
                kids[docKids++] = framepanel.getDocument();
                frameList[k1++] = framepanel;
            }
            j1 += l1;
            i1 = k1;
        }

        if(i1 < frameList.length)
        {
            removeSpaceOfUnspecifiedItems(frameList.length - i1);
            initRowsAndCols();
        }
        initResizableVectors();
        setFrameGridInfo();
    }

    private FrameSetPanel findTopFrameSet(Formatter formatter)
    {
        FrameSetPanel framesetpanel = this;
        for(Container container = formatter.getParent(); container != null; container = container.getParent())
            if(container instanceof FrameSetPanel)
                framesetpanel = (FrameSetPanel)container;

        return framesetpanel;
    }

    private boolean checkAndRegisterDescendant()
    {
        synchronized(this)
        {
            numDescendants++;
            boolean flag = numDescendants <= 70;
            return flag;
        }
    }

    public void invalidateFrames()
    {
        for(int i = 0; i < docKids; i++)
            if(kids[i] != null)
                kids[i].invalidateFrames();

        removeAll();
        kids = new Document[4];
        docKids = 0;
    }

    private void makeRoomForUnspecifiedItems()
    {
        if(rows.length == 1 && rows[0].equals("100%"))
        {
            rows[0] = "*";
            String as[] = new String[rows.length + 1];
            System.arraycopy(rows, 0, as, 0, rows.length);
            as[rows.length] = "*";
            rows = as;
        } else
        if(cols.length == 1 && cols[0].equals("100%"))
        {
            cols[0] = "*";
            String as1[] = new String[cols.length + 1];
            System.arraycopy(cols, 0, as1, 0, cols.length);
            as1[cols.length] = "*";
            cols = as1;
        } else
        {
            String as2[] = new String[rows.length + 1];
            System.arraycopy(rows, 0, as2, 0, rows.length);
            as2[rows.length] = "*";
            rows = as2;
        }
        Component acomponent[] = new Component[rows.length * cols.length];
        System.arraycopy(frameList, 0, acomponent, 0, frameList.length);
        frameList = acomponent;
    }

    private void removeSpaceOfUnspecifiedItems(int i)
    {
        int j = i / cols.length;
        int k = i % cols.length;
        if(j > 0)
        {
            String as[] = new String[rows.length - j];
            System.arraycopy(rows, 0, as, 0, as.length);
            rows = as;
        }
        if(k > 0)
        {
            String as1[] = new String[cols.length - k];
            System.arraycopy(cols, 0, as1, 0, as1.length);
            cols = as1;
        }
        Component acomponent[] = new Component[rows.length * cols.length];
        System.arraycopy(frameList, 0, acomponent, 0, acomponent.length);
        frameList = acomponent;
    }

    private void setFrameGridInfo()
    {
        Object obj = null;
        Object obj1 = null;
        for(int i = 0; i < rows.length; i++)
        {
            for(int j = 0; j < cols.length; j++)
            {
                Component component = getFrameAt(i, j);
                if(component instanceof Resizable)
                {
                    Resizable resizable = (Resizable)component;
                    Point point = new Point(j, i);
                    resizable.setResizeListener(this, point);
                }
            }

        }

    }

    Component getFrameAt(int i, int j)
    {
        int k = i * cols.length + j;
        return frameList[k];
    }

    public void activateSubItems()
    {
        for(int i = frameList.length; i-- > 0;)
        {
            Component component = frameList[i];
            if(component != null)
                ((DocPanel)component).activateSubItems();
        }

    }

    public void start()
    {
        for(int i = frameList.length; i-- > 0;)
        {
            Component component = frameList[i];
            if(component != null)
                ((DocPanel)component).start();
        }

    }

    public void stop()
    {
        for(int i = frameList.length; i-- > 0;)
        {
            Component component = frameList[i];
            if(component != null)
                ((DocPanel)component).stop();
        }

    }

    public void destroy()
    {
        for(int i = frameList.length; i-- > 0;)
        {
            Component component = frameList[i];
            if(component != null)
                ((DocPanel)component).destroy();
        }

    }

    public void setObsolete(boolean flag)
    {
        for(int i = frameList.length; i-- > 0;)
        {
            Component component = frameList[i];
            if(component != null)
                ((DocPanel)component).setObsolete(flag);
        }

    }

    public void interruptLoading()
    {
        for(int i = frameList.length; i-- > 0;)
        {
            Component component = frameList[i];
            if(component != null)
                ((DocPanel)component).interruptLoading();
        }

    }

    public void notify(Document document, int i, int j, int k)
    {
    }

    public void reformat()
    {
        for(int i = 0; i < frameList.length; i++)
            ((DocumentPanel)frameList[i]).reformat();

    }

    public int findYFor(int i)
    {
        return 0;
    }

    void spread(int i, int j, int ai[])
    {
        if(i == 0 || ai.length == 0)
            return;
        int k = 0;
        int l = i;
        for(int i1 = 0; i1 < ai.length; i1++)
            if(absoluteRowsAndCols[j][i1] > 0)
            {
                ai[i1] = absoluteRowsAndCols[j][i1];
                l -= ai[i1];
            }

        k = l;
        for(int j1 = 0; j1 < ai.length; j1++)
            if(percentRowsAndCols[j][j1] > 0 && k > 0)
            {
                ai[j1] = (k * percentRowsAndCols[j][j1]) / 100;
                l -= ai[j1];
            } else
            if(percentRowsAndCols[j][j1] > 0 && k <= 0)
            {
                ai[j1] = i / ai.length;
                l -= ai[j1];
            }

        if(l > 0 && relativeTotals[j] > 0)
        {
            for(int k1 = 0; k1 < ai.length; k1++)
                if(relativeRowsAndCols[j][k1] > 0)
                    ai[k1] = (l * relativeRowsAndCols[j][k1]) / relativeTotals[j];

            return;
        }
        if(l != 0)
        {
            float f = i - l;
            float af[] = new float[ai.length];
            l = i;
            for(int l1 = 0; l1 < ai.length; l1++)
            {
                af[l1] = ((float)ai[l1] / f) * 100F;
                ai[l1] = (int)(((float)i * af[l1]) / 100F);
                l -= ai[l1];
            }

            int i2 = 0;
            while(l != 0) 
            {
                if(l < 0)
                {
                    ai[i2++]--;
                    l++;
                } else
                {
                    ai[i2++]++;
                    l--;
                }
                if(i2 >= ai.length)
                    i2 = 0;
            }
        }
    }

    public Dimension minimumSize()
    {
        return getSize();
    }

    public Dimension preferredSize()
    {
        return getSize();
    }

    public void setBounds(int i, int j, int k, int l)
    {
        hasBeenResized = false;
        Rectangle rectangle = getBounds();
        super.setBounds(i, j, k, l);
        changeBounds(i - rectangle.x, j - rectangle.y, k - rectangle.width, l - rectangle.height);
    }

    public void layout()
    {
        if(!hasBeenResized)
        {
            Dimension dimension = getSize();
            int ai[] = new int[cols.length];
            spread(dimension.width, 1, ai);
            int ai1[] = new int[rows.length];
            spread(dimension.height, 0, ai1);
            int i = 0;
            int j = 0;
            for(int k = 0; i < rows.length; k += ai1[i++])
            {
                int l = 0;
                int i1 = 0;
                while(l < cols.length) 
                {
                    Component component = frameList[j];
                    if(component != null)
                        component.setBounds(i1, k, ai[l], ai1[i]);
                    i1 += ai[l++];
                    j++;
                }
            }

            super.layout();
        }
    }

    public void print(PrintJob printjob, HotJavaBrowserBean hotjavabrowserbean)
        throws DocBusyException
    {
        int i = frameList.length;
        for(int j = 0; j < i; j++)
        {
            Component component = frameList[j];
            if(component != null)
                if(component instanceof FrameSetPanel)
                    ((FrameSetPanel)component).print(printjob, hotjavabrowserbean);
                else
                if(component instanceof DocumentPanel)
                    ((DocumentPanel)component).print(printjob, hotjavabrowserbean);
                else
                if(component instanceof FramePanel)
                    ((FramePanel)component).print(printjob, hotjavabrowserbean);
        }

    }

    int parseDigits(String s)
    {
        int i = 0;
        for(int j = 0; j < s.length(); j++)
        {
            char c = s.charAt(j);
            if(Character.isDigit(c))
                i = i * 10 + Character.digit(c, 10);
        }

        return i;
    }

    void unsafeValidate()
    {
        validateTree();
    }

    public void setResizeListener(FrameSetPanel framesetpanel, Point point)
    {
        resizeListener = framesetpanel;
        gridLoc = point;
        setFrameGridInfo();
    }

    void handleResizeEvent(ResizeRequestEvent resizerequestevent)
    {
        BitSet bitset = horizResizables;
        int i;
        if(gridLoc != null)
            i = gridLoc.x;
        else
            i = -1;
        int j = cols.length;
        if(resizerequestevent.isVertical())
        {
            if(gridLoc != null)
                i = gridLoc.y;
            else
                i = -1;
            bitset = vertResizables;
            j = rows.length;
        }
        int k = resizerequestevent.getValue();
        if(k == 0)
        {
            if(resizeListener != null)
                resizeListener.handleResizeEvent(new ResizeRequestEvent(resizerequestevent, i));
            return;
        }
        if(k == j)
        {
            if(resizeListener != null)
                resizeListener.handleResizeEvent(new ResizeRequestEvent(resizerequestevent, i + 1));
            return;
        }
        if(bitset.get(k))
            setupResizeTracker(resizerequestevent);
    }

    private void setupResizeTracker(ResizeRequestEvent resizerequestevent)
    {
        int i = resizerequestevent.getValue();
        Rectangle rectangle;
        if(resizerequestevent.isVertical())
        {
            Component component = getFrameAt(i - 1, 0);
            Rectangle rectangle1 = component.getBounds();
            Component component2 = getFrameAt(i, 0);
            Rectangle rectangle3 = component2.getBounds();
            int j = getVerLimit(i, 3);
            int l = getVerLimit(i, 4);
            Dimension dimension = getSize();
            rectangle = new Rectangle(rectangle1.x, rectangle3.y - j, dimension.width, j + l);
        } else
        {
            Component component1 = getFrameAt(0, i - 1);
            Rectangle rectangle2 = component1.getBounds();
            Component component3 = getFrameAt(0, i);
            Rectangle rectangle4 = component3.getBounds();
            int k = getHorLimit(i, 5);
            int i1 = getHorLimit(i, 2);
            Dimension dimension1 = getSize();
            rectangle = new Rectangle(rectangle4.x - k, rectangle2.y, k + i1, dimension1.height);
        }
        EdgeInfo edgeinfo = new EdgeInfo(resizerequestevent.isVertical(), resizerequestevent.getValue());
        new ResizeTracker(resizerequestevent.getLocation(), rectangle, this, (Component)resizerequestevent.getSource(), edgeinfo);
    }

    void resizeFrames(EdgeInfo edgeinfo, Point point)
    {
        hasBeenResized = true;
        if(edgeinfo.isVertical())
        {
            int i = edgeinfo.getValue();
            int k = point.y;
            for(int i1 = 0; i1 < cols.length; i1++)
            {
                Component component = getFrameAt(i - 1, i1);
                Rectangle rectangle = component.getBounds();
                rectangle.height = k - rectangle.y;
                component.setBounds(rectangle);
                Component component2 = getFrameAt(i, i1);
                Rectangle rectangle2 = component2.getBounds();
                rectangle2.height += rectangle2.y - k;
                rectangle2.y = k;
                component2.setBounds(rectangle2);
            }

        } else
        {
            int j = edgeinfo.getValue();
            int l = point.x;
            for(int j1 = 0; j1 < rows.length; j1++)
            {
                Component component1 = getFrameAt(j1, j - 1);
                Rectangle rectangle1 = component1.getBounds();
                rectangle1.width = l - rectangle1.x;
                component1.setBounds(rectangle1);
                Component component3 = getFrameAt(j1, j);
                Rectangle rectangle3 = component3.getBounds();
                rectangle3.width += rectangle3.x - l;
                rectangle3.x = l;
                component3.setBounds(rectangle3);
            }

        }
        validateFrames();
    }

    protected void validateFrames()
    {
        super.layout();
        Component acomponent[] = getComponents();
        for(int i = 0; i < acomponent.length; i++)
        {
            Component component = acomponent[i];
            if(component instanceof FrameSetPanel)
                ((FrameSetPanel)component).validateFrames();
            else
                component.validate();
        }

    }

    protected void changeBounds(int i, int j, int k, int l)
    {
        if(k != 0)
            if(i != 0)
            {
                Component acomponent[] = getFramesCol(0);
                for(int i1 = 0; i1 < acomponent.length; i1++)
                {
                    Rectangle rectangle = acomponent[i1].getBounds();
                    rectangle.width += k;
                    acomponent[i1].setBounds(rectangle);
                }

                for(int i2 = 1; i2 < cols.length; i2++)
                {
                    Component acomponent1[] = getFramesCol(i2);
                    for(int k2 = 0; k2 < acomponent1.length; k2++)
                    {
                        Rectangle rectangle4 = acomponent1[k2].getBounds();
                        rectangle4.x += k;
                        acomponent1[k2].setBounds(rectangle4);
                    }

                }

            } else
            if(i == 0)
            {
                Component acomponent2[] = getFramesCol(cols.length - 1);
                for(int j1 = 0; j1 < acomponent2.length; j1++)
                {
                    Rectangle rectangle1 = acomponent2[j1].getBounds();
                    rectangle1.width += k;
                    acomponent2[j1].setBounds(rectangle1);
                }

            }
        if(l != 0)
        {
            if(j == 0)
            {
                Component acomponent3[] = getFramesRow(rows.length - 1);
                for(int k1 = 0; k1 < acomponent3.length; k1++)
                {
                    Rectangle rectangle2 = acomponent3[k1].getBounds();
                    rectangle2.height += l;
                    acomponent3[k1].setBounds(rectangle2);
                }

                return;
            }
            Component acomponent4[] = getFramesRow(0);
            for(int l1 = 0; l1 < acomponent4.length; l1++)
            {
                Rectangle rectangle3 = acomponent4[l1].getBounds();
                rectangle3.height -= j;
                acomponent4[l1].setBounds(rectangle3);
            }

            for(int j2 = 1; j2 < rows.length; j2++)
            {
                Component acomponent5[] = getFramesRow(j2);
                for(int l2 = 0; l2 < acomponent5.length; l2++)
                {
                    Rectangle rectangle5 = acomponent5[l2].getBounds();
                    rectangle5.y -= j;
                    acomponent5[l2].setBounds(rectangle5);
                }

            }

        }
    }

    protected Component[] getFramesCol(int i)
    {
        int j = cols.length;
        int k = rows.length;
        Component acomponent[] = new Component[k];
        if(i <= j)
        {
            for(int l = 0; l < k; l++)
                acomponent[l] = frameList[l * j + i];

            return acomponent;
        } else
        {
            return null;
        }
    }

    protected Component[] getFramesRow(int i)
    {
        int j = cols.length;
        int k = rows.length;
        Component acomponent[] = new Component[j];
        if(i <= k)
        {
            for(int l = 0; l < j; l++)
                acomponent[l] = frameList[i * j + l];

            return acomponent;
        } else
        {
            return null;
        }
    }

    protected int getHorLimit(int i, int j)
    {
        Component acomponent[] = getFramesCol(i + (j != 5 ? 0 : -1));
        int k = acomponent[0].getBounds().width;
        for(int l = 0; l < acomponent.length; l++)
            if(acomponent[l] instanceof FrameSetPanel)
            {
                FrameSetPanel framesetpanel = (FrameSetPanel)acomponent[l];
                int i1 = j != 5 ? 0 : framesetpanel.cols.length;
                int j1 = framesetpanel.getHorLimit(i1, j);
                k = j1 >= k ? k : j1;
            }

        return k;
    }

    protected int getVerLimit(int i, int j)
    {
        Component acomponent[] = getFramesRow(i + (j != 3 ? 0 : -1));
        int k = acomponent[0].getBounds().height;
        for(int l = 0; l < acomponent.length; l++)
            if(acomponent[l] instanceof FrameSetPanel)
            {
                FrameSetPanel framesetpanel = (FrameSetPanel)acomponent[l];
                int i1 = j != 3 ? 0 : framesetpanel.rows.length;
                int j1 = framesetpanel.getVerLimit(i1, j);
                k = j1 >= k ? k : j1;
            }

        return k;
    }

    public static final int DEFAULT_BORDER = 3;
    public static final int MAX_DESCENTANTS = 70;
    static final int ROWS = 0;
    static final int COLS = 1;
    static final int LEFT = 5;
    static final int RIGHT = 2;
    static final int ABOVE = 3;
    static final int BELOW = 4;
    Document doc;
    Document kids[];
    int docKids;
    TagItem item;
    String rows[];
    String cols[];
    int percentRowsAndCols[][];
    int relativeRowsAndCols[][];
    int absoluteRowsAndCols[][];
    int absoluteTotals[];
    int relativeTotals[];
    int percentTotals[];
    Component frameList[];
    private BitSet vertResizables;
    private BitSet horizResizables;
    private boolean hasBeenResized;
    int border;
    private int numDescendants;
    private FrameSetPanel resizeListener;
    private Point gridLoc;
    private DocumentPanel docPanel;
}
