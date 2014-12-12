// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TABLE.java

package sunw.hotjava.tables;

import java.awt.*;
import java.util.Vector;
import sunw.hotjava.doc.*;
import sunw.hotjava.tags.Align;
import sunw.html.Attributes;

// Referenced classes of package sunw.hotjava.tables:
//            TablePanel, TableVBreakInfo

public class TABLE extends TagItem
    implements Floatable
{

    public TABLE()
    {
        fmt = 2;
        savedXPos = -1;
        surroundedByForm = false;
        formsToEnd = 0;
    }

    public void init(Document document)
    {
        if(super.atts != null)
            fmt = Align.getFormat(super.atts, "align", 2);
        align = Align.getAlign(super.atts);
    }

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state != 0)
        {
            formatstate.below = Math.max(formatstate.above, super.style.getAbove(((TraversalState) (formatstate)).style));
            return true;
        }
        TablePanel tablepanel = (TablePanel)formatter.getPanel(this);
        if(tablepanel != null)
        {
            tablepanel.format(formatstate);
            if(alignIsFloating())
            {
                boolean flag = false;
                if(!formatter.isFloater(this) && formatstate.width + getWidth(formatter, ((TraversalState) (formatstate)).style) > formatstate.maxWidth)
                {
                    if(formatter.floatersInYRange(formatstate.y))
                    {
                        formatstate.below += formatter.getCumulativeFloaterHeight(formatstate.y);
                        return true;
                    }
                    flag = true;
                }
                formatter.queueFloater(formatter, formatstate, this, getAscent(formatter, formatstate) + getDescent(formatter, formatstate), align == 7);
                formatstate.pos += getOffset() << 12;
                return flag;
            }
        }
        formatstate.style = ((TraversalState) (formatstate)).style.push(this);
        formatstate.above = Math.max(formatstate.above, 0);
        formatstate.below = Math.max(0, formatstate.below);
        formatstate.state = 1;
        formatstate.pos += getOffset() << 12;
        if(tablepanel != null)
        {
            Dimension dimension = tablepanel.getSize();
            formatstate.ascent = dimension.height;
            formatstate.width += dimension.width;
            formatstate.descent = 0;
            if(fmt != 2)
                formatstate.format = fmt;
        }
        return false;
    }

    public boolean formatEndTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(alignIsFloating())
        {
            formatstate.pos += 4096;
            return false;
        }
        if(formatstate.state != 0)
        {
            return true;
        } else
        {
            formatstate.above = Math.max(formatstate.below, super.style.getBelow(((TraversalState) (formatstate)).style));
            formatstate.pos += 4096;
            formatstate.style = ((TraversalState) (formatstate)).style.next;
            formatstate.state = 2;
            return false;
        }
    }

    protected int getActivateIncrement()
    {
        return getOffset();
    }

    public int getWidth(Formatter formatter, DocStyle docstyle)
    {
        TablePanel tablepanel = (TablePanel)formatter.getPanel(this);
        if(tablepanel != null)
        {
            Dimension dimension = tablepanel.getSize();
            return dimension.width;
        } else
        {
            return 0;
        }
    }

    public int getAscent(Formatter formatter, FormatState formatstate)
    {
        TablePanel tablepanel = (TablePanel)formatter.getPanel(this);
        if(tablepanel != null)
        {
            Dimension dimension = tablepanel.getSize();
            return Align.getAscent(formatstate, align, dimension.height);
        } else
        {
            return 0;
        }
    }

    public int getDescent(Formatter formatter, FormatState formatstate)
    {
        TablePanel tablepanel = (TablePanel)formatter.getPanel(this);
        if(tablepanel != null)
        {
            Dimension dimension = tablepanel.getSize();
            return Align.getDescent(formatstate, align, dimension.height);
        } else
        {
            return 0;
        }
    }

    private boolean alignIsFloating()
    {
        return align == 7 || align == 8;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline, boolean flag)
    {
        if(alignIsFloating())
        {
            formatter.displayPos += getOffset() << 12;
            return 0;
        } else
        {
            return paintTable(formatter, g, i, j, docline, flag);
        }
    }

    public int getLastX()
    {
        return savedXPos;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j)
    {
        savedXPos = i;
        return paintTable(formatter, g, i, j, null, false);
    }

    private int paintTable(Formatter formatter, Graphics g, int i, int j, DocLine docline, boolean flag)
    {
        formatter.displayPos += getOffset() + 1 << 12;
        TablePanel tablepanel = (TablePanel)formatter.getPanel(this);
        if(tablepanel != null)
        {
            if(docline != null)
                tablepanel.setLocation(i, j + docline.getAbove());
            else
                tablepanel.setLocation(i, j);
            if(!tablepanel.isVisible())
                tablepanel.setVisible(true);
            else
            if(!flag)
                TablePanel.paintWithClipping(g, tablepanel);
        }
        return 0;
    }

    public int findSplitY(Formatter formatter, ItemIterator itemiterator, DocStyle docstyle, int i, int j, DocLine docline, VBreakInfo vbreakinfo)
    {
        if(itemiterator.getOffset() < 0)
        {
            itemiterator.skipItem();
            return i;
        }
        itemiterator.skipToEnd();
        if(alignIsFloating())
            return i;
        else
            return findPanelSplitY(formatter, docline.y, i, j, vbreakinfo);
    }

    public int findSplitY(Formatter formatter, FloaterInfo floaterinfo, int i, int j, VBreakInfo vbreakinfo)
    {
        return findPanelSplitY(formatter, floaterinfo.getStartY(), i, j, vbreakinfo);
    }

    private int findPanelSplitY(Formatter formatter, int i, int j, int k, VBreakInfo vbreakinfo)
    {
        TablePanel tablepanel = (TablePanel)formatter.getPanel(this);
        int l = i;
        if(tablepanel != null)
        {
            tablepanel.setLocation(0, i);
            tablepanel.getSize();
            tablepanel.validate();
            int i1 = tablepanel.getLocation().y;
            TableVBreakInfo tablevbreakinfo = null;
            if(vbreakinfo != null)
                tablevbreakinfo = (TableVBreakInfo)((CompoundVBreakInfo)vbreakinfo).getItemBreakInfo(tablepanel);
            return tablepanel.findSplitY(j - i1, k, tablevbreakinfo) + i1;
        } else
        {
            return l;
        }
    }

    public void recordBreakInfo(Formatter formatter, ItemIterator itemiterator, DocStyle docstyle, int i, int j, DocLine docline, FormatterVBreakInfo formattervbreakinfo, 
            FormatterVBreakInfo formattervbreakinfo1)
    {
        if(itemiterator.getOffset() < 0)
        {
            itemiterator.skipItem();
            return;
        }
        itemiterator.skipToEnd();
        if(alignIsFloating())
        {
            return;
        } else
        {
            recordPanelBreakInfo(formatter, i, j, formattervbreakinfo, formattervbreakinfo1);
            return;
        }
    }

    public void recordBreakInfo(Formatter formatter, FloaterInfo floaterinfo, int i, int j, FormatterVBreakInfo formattervbreakinfo, FormatterVBreakInfo formattervbreakinfo1)
    {
        recordPanelBreakInfo(formatter, i, j, formattervbreakinfo, formattervbreakinfo1);
    }

    private void recordPanelBreakInfo(Formatter formatter, int i, int j, FormatterVBreakInfo formattervbreakinfo, FormatterVBreakInfo formattervbreakinfo1)
    {
        TablePanel tablepanel = (TablePanel)formatter.getPanel(this);
        if(tablepanel != null)
        {
            TableVBreakInfo tablevbreakinfo = (TableVBreakInfo)formattervbreakinfo.getItemBreakInfo(this);
            if(tablevbreakinfo == null)
            {
                tablevbreakinfo = new TableVBreakInfo();
                formattervbreakinfo.setItemBreakInfo(this, tablevbreakinfo);
            }
            TableVBreakInfo tablevbreakinfo1 = new TableVBreakInfo();
            formattervbreakinfo1.setItemBreakInfo(this, tablevbreakinfo1);
            int k = tablepanel.getLocation().y;
            tablepanel.recordBreakInfo(i - k, j - k, tablevbreakinfo, tablevbreakinfo1);
        }
    }

    public int print(Formatter formatter, Graphics g, int i, int j, DocLine docline, VBreakInfo vbreakinfo)
    {
        formatter.displayPos += getOffset() + 1 << 12;
        if(alignIsFloating())
            return 0;
        else
            return printTable(formatter, g, i, j, vbreakinfo);
    }

    public int print(Formatter formatter, Graphics g, int i, int j, VBreakInfo vbreakinfo)
    {
        formatter.displayPos += getOffset() + 1 << 12;
        return printTable(formatter, g, i, j, vbreakinfo);
    }

    private int printTable(Formatter formatter, Graphics g, int i, int j, VBreakInfo vbreakinfo)
    {
        TablePanel tablepanel = (TablePanel)formatter.getPanel(this);
        if(tablepanel != null)
        {
            Dimension dimension = tablepanel.getSize();
            tablepanel.validate();
            TableVBreakInfo tablevbreakinfo = (TableVBreakInfo)vbreakinfo;
            Graphics g1 = g.create(i, j, dimension.width, dimension.height);
            try
            {
                tablepanel.print(g1, tablevbreakinfo);
            }
            finally
            {
                g1.dispose();
            }
        }
        return 0;
    }

    public boolean needsActivation()
    {
        return true;
    }

    public boolean measureStartTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        TablePanel tablepanel = (TablePanel)formatter.getPanel(this);
        Measurement measurement1 = new Measurement();
        measurestate.style = ((TraversalState) (measurestate)).style.push(this);
        if(formatstate.state != 0)
            return true;
        if(tablepanel != null)
        {
            tablepanel.measureWidth(measurement1, measurestate);
        } else
        {
            measurement.reset();
            measurestate.measurementInvalid = true;
        }
        measurestate.pos += getOffset() << 12;
        if(alignIsFloating())
        {
            measurement.setFloaterMinWidth(measurement1.getMinWidth());
            measurement.setFloaterPreferredWidth(measurement1.getPreferredWidth());
            return false;
        } else
        {
            formatstate.state = 1;
            measurement.setMinWidth(measurement1.getMinWidth());
            measurement.setPreferredWidth(measurement1.getPreferredWidth());
            return false;
        }
    }

    public boolean measureEndTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        measurestate.pos += 4096;
        if(alignIsFloating())
        {
            return false;
        } else
        {
            measurestate.style = ((TraversalState) (measurestate)).style.next;
            return true;
        }
    }

    public int getFormPanel(Formatter formatter, Vector vector)
    {
        TablePanel tablepanel;
        do
            tablepanel = (TablePanel)formatter.getPanel(this);
        while(tablepanel == null && (formatter = formatter.getParentFormatter()) != null);
        if(tablepanel != null)
            tablepanel.getFormPanel(vector);
        return getOffset() + 1;
    }

    public Component createView(Formatter formatter, Document document)
    {
        TablePanel tablepanel = new TablePanel(formatter, this, document);
        return tablepanel;
    }

    public void setSurroundedTableByForm(boolean flag)
    {
        surroundedByForm = flag;
    }

    public boolean getSurroundedTableByForm()
    {
        return surroundedByForm;
    }

    public void incrementFormsToEnd()
    {
        formsToEnd++;
    }

    public int getFormsToEnd()
    {
        return formsToEnd;
    }

    protected String elementString(boolean flag)
    {
        if(super.atts == null || flag)
        {
            return getName();
        } else
        {
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append(getName());
            addAtt(stringbuffer, "align");
            addAtt(stringbuffer, "width");
            addAtt(stringbuffer, "height");
            return stringbuffer.toString();
        }
    }

    private void addAtt(StringBuffer stringbuffer, String s)
    {
        String s1 = super.atts.get(s);
        if(s1 != null)
        {
            stringbuffer.append(" ");
            stringbuffer.append(s);
            stringbuffer.append("=");
            stringbuffer.append(s1);
        }
    }

    private int fmt;
    private int align;
    private boolean surroundedByForm;
    private int formsToEnd;
    private int savedXPos;
}
