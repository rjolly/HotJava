// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SELECT.java

package sunw.hotjava.forms;

import java.awt.*;
import java.util.Vector;
import sunw.hotjava.doc.*;

// Referenced classes of package sunw.hotjava.forms:
//            FORM, FormPanel, OPTION

public class SELECT extends TagItem
{

    public SELECT()
    {
        super.handlerStrings = myHandlers;
    }

    public boolean needsActivation()
    {
        return true;
    }

    public int getWidth(Formatter formatter, DocStyle docstyle)
    {
        FormPanel formpanel = (FormPanel)formatter.getPanel(this);
        if(formpanel != null)
        {
            Dimension dimension = formpanel.getPreferredSize();
            return dimension.width;
        } else
        {
            return 0;
        }
    }

    public int findStartTagX(DocLine docline, DocStyle docstyle, int i, int j, int k, Formatter formatter)
    {
        return -1 - getWidth(formatter, docstyle);
    }

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state == 2)
            return true;
        FormPanel formpanel = (FormPanel)formatter.getPanel(this);
        if(formpanel != null)
        {
            Dimension dimension = formpanel.getPreferredSize();
            if(formatstate.width + dimension.width > formatstate.maxWidth && formatstate.width != 0 && ((TraversalState) (formatstate)).style.nobreak == 0)
                return true;
            formpanel.setSize(dimension.width, dimension.height);
            formpanel.validate();
            formatstate.width += dimension.width;
            formatstate.ascent = Math.max(formatstate.ascent, formpanel.getAscent());
            formatstate.descent = Math.max(formatstate.descent, formpanel.getDescent());
        }
        formatstate.below = Math.max(3, formatstate.below);
        formatstate.above = Math.max(3, formatstate.above);
        formatstate.pos += getOffset() << 12;
        formatstate.state = 1;
        return false;
    }

    public boolean formatEndTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        formatstate.pos += 4096;
        markTextItemsAsPseudo(formatter.getDocument());
        return false;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        formatter.displayPos += getOffset() << 12;
        FormPanel formpanel = (FormPanel)formatter.getPanel(this);
        if(formpanel != null)
        {
            j += (docline.baseline - formpanel.getSize().height) + formpanel.getDescent();
            formpanel.setLocation(i, j);
            formpanel.validate();
            DocStyle docstyle = formatter.displayStyle;
            if(docstyle != null)
                formpanel.setForeground(docstyle.color);
            formpanel.setBackground(formatter.getFormatterBackgroundColor());
            formpanel.setVisible(true);
            return formpanel.getSize().width;
        } else
        {
            return 0;
        }
    }

    public int print(Formatter formatter, Graphics g, int i, int j, DocLine docline, VBreakInfo vbreakinfo)
    {
        formatter.displayPos += getOffset() << 12;
        FormPanel formpanel = (FormPanel)formatter.getPanel(this);
        if(formpanel != null)
        {
            j += (docline.baseline - formpanel.getSize().height) + formpanel.getDescent();
            Dimension dimension = formpanel.getSize();
            int k = dimension.width;
            int l = dimension.height;
            Graphics g1 = g.create(i, j, k, l);
            DocStyle docstyle = formatter.displayStyle;
            if(docstyle != null)
                formpanel.setForeground(docstyle.color);
            boolean flag = formpanel.isVisible();
            try
            {
                if(!flag)
                    formpanel.setVisible(true);
                formpanel.printAll(g1);
            }
            catch(Exception _ex) { }
            finally
            {
                g1.dispose();
                if(!flag)
                    formpanel.setVisible(false);
            }
            return dimension.width;
        } else
        {
            return 0;
        }
    }

    public int getFormPanel(Formatter formatter, Vector vector)
    {
        int i = 0;
        FormPanel formpanel;
        do
        {
            formpanel = (FormPanel)formatter.getPanel(this);
            i++;
        } while(formpanel == null && (formatter = formatter.getParentFormatter()) != null);
        if(formpanel != null)
            vector.addElement(formpanel);
        return getOffset() + 1;
    }

    public Component createView(Formatter formatter, Document document)
    {
        FormPanel formpanel = new FormPanel(formatter, document, this, "select");
        int i = getIndex();
        int j = getOffset();
        document.change(i << 12, i + j + 1 << 12);
        return formpanel;
    }

    public boolean measureStartTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        FormPanel formpanel = (FormPanel)formatter.getPanel(this);
        if(formpanel != null)
        {
            Dimension dimension = formpanel.getPreferredSize();
            measurement.setMinWidth(dimension.width);
            measurement.setPreferredWidth(dimension.width);
            if(dimension.equals(new Dimension()))
                measurestate.measurementInvalid = true;
        }
        measurestate.pos += getOffset() + 1 << 12;
        return false;
    }

    private void markTextItemsAsPseudo(Document document)
    {
        int i = getIndex();
        for(int j = i + getOffset(); i++ < j;)
        {
            DocItem docitem = document.getItem(i);
            if(docitem instanceof OPTION)
                ((OPTION)docitem).markTextItemsAsPseudo(document);
        }

    }

    public void setFormParent(TagItem tagitem)
    {
        if(tagitem == null || !(tagitem instanceof FORM))
        {
            return;
        } else
        {
            parentForm = (FORM)tagitem;
            return;
        }
    }

    public TagItem getFormParent()
    {
        return parentForm;
    }

    public boolean visit(DocItemVisitor docitemvisitor)
    {
        return docitemvisitor.visitSELECTTag(this);
    }

    private FORM parentForm;
    private static final String myHandlers[] = {
        "onblur", "onfocus", "onchange"
    };

}
