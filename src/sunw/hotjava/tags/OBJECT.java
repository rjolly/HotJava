// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OBJECT.java

package sunw.hotjava.tags;

import java.awt.Component;
import java.awt.Graphics;
import java.net.URL;
import sunw.hotjava.doc.*;

// Referenced classes of package sunw.hotjava.tags:
//            APPLET, IMG

public class OBJECT extends TagItem
    implements Floatable
{

    public void init(Document document)
    {
        if(super.atts == null)
            return;
        String s = getAttribute("codetype");
        if(s != null)
        {
            s = s.toLowerCase();
            if(s.equals("application/java-vm") || s.equals("application/x-java-vm") || s.equals("application/vnd.javasoft.java-vm"))
            {
                object = new APPLET();
                ((APPLET)object).init(document, this);
                return;
            }
        }
        String s1 = getAttribute("type");
        if(s1 != null)
        {
            s1 = s1.toLowerCase();
            if(s1.equals("application/java-serialized-object") || s1.equals("application/x-java-serialized-object") || s1.equals("application/vnd.javasoft.java-serialized-object"))
            {
                object = new APPLET();
                ((APPLET)object).init(document, this);
                return;
            }
        }
        String s2 = getAttribute("classid");
        if(s2 != null)
        {
            int i = s2.indexOf(':');
            if(i >= 1 && s2.substring(0, i).toLowerCase().equals("java"))
            {
                object = new APPLET();
                ((APPLET)object).init(document, this);
                return;
            }
            if(s2.equals("clsid:8AD9C840-044E-11D1-B3E9-00805F499D93"))
            {
                object = new APPLET();
                ((APPLET)object).init(document, this);
                return;
            }
        }
        if(s1 != null)
        {
            s1 = s1.toLowerCase();
            if(s1.equals("image/gif") || s1.equals("image/jpeg"))
            {
                object = new IMG();
                ((IMG)object).init(document, this);
                return;
            }
        }
        String s3 = getAttribute("data");
        if(s3 != null)
        {
            int j = s3.lastIndexOf('.');
            if(j >= 1)
            {
                String s4 = s3.substring(j).toLowerCase();
                if(s4.equals(".jpg") || s4.equals(".gif"))
                {
                    object = new IMG();
                    ((IMG)object).init(document, this);
                    return;
                }
            }
        }
    }

    public boolean needsActivation()
    {
        if(object != null)
            return object.needsActivation();
        else
            return false;
    }

    public Component createView(Formatter formatter, Document document)
    {
        if(needsActivation())
            return object.createView(formatter, document);
        else
            return null;
    }

    public URL getImageURL()
    {
        if(object != null)
            return object.getImageURL();
        else
            return null;
    }

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state == 2)
            return true;
        if(object == null)
        {
            formatstate.pos += 4096;
            return false;
        }
        if(object instanceof IMG)
            return object.format(formatter, formatstate, formatstate1);
        else
            return object.formatStartTag(formatter, formatstate, formatstate1);
    }

    public int findStartTagX(DocLine docline, DocStyle docstyle, int i, int j, int k, Formatter formatter)
    {
        if(object != null && (object instanceof IMG))
            return object.findX(docline, docstyle, i, j, k, formatter);
        else
            return super.findStartTagX(docline, docstyle, i, j, k, formatter);
    }

    public int findEndTagX(DocLine docline, DocStyle docstyle, int i, int j, int k, Formatter formatter)
    {
        return findStartTagX(docline, docstyle, i, j, k, formatter);
    }

    public int getStartTagWidth(DocLine docline, DocStyle docstyle, int i, int j, int k)
    {
        if(object != null && (object instanceof IMG))
            return ((IMG)object).getWidth(docstyle);
        else
            return super.getStartTagWidth(docline, docstyle, i, j, k);
    }

    public int getEndTagWidth(DocLine docline, DocStyle docstyle, int i, int j, int k)
    {
        return getStartTagWidth(docline, docstyle, i, j, k);
    }

    public boolean formatEndTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        formatstate.pos += 4096;
        return false;
    }

    public int getWidth(Formatter formatter, DocStyle docstyle)
    {
        if(object != null)
            return object.getWidth(formatter, docstyle);
        else
            return 0;
    }

    public int getDescent(Formatter formatter, FormatState formatstate)
    {
        if(object != null)
            return object.getDescent(formatter, formatstate);
        else
            return 0;
    }

    public int getAscent(Formatter formatter, FormatState formatstate)
    {
        if(object != null)
            return object.getAscent(formatter, formatstate);
        else
            return 0;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        if(object != null)
        {
            return object.paint(formatter, g, i, j, docline, false);
        } else
        {
            formatter.displayPos += 4096;
            return 0;
        }
    }

    public int print(Formatter formatter, Graphics g, int i, int j, DocLine docline, VBreakInfo vbreakinfo)
    {
        return object.print(formatter, g, i, j, docline, vbreakinfo);
    }

    public int paint(Formatter formatter, Graphics g, int i, int j)
    {
        if(object != null)
        {
            return ((Floatable)object).paint(formatter, g, i, j);
        } else
        {
            formatter.displayPos += 4096;
            return 0;
        }
    }

    public int print(Formatter formatter, Graphics g, int i, int j, VBreakInfo vbreakinfo)
    {
        if(object instanceof Floatable)
            return ((Floatable)object).print(formatter, g, i, j, vbreakinfo);
        else
            return 0;
    }

    public boolean needsLoading()
    {
        if(object != null)
            return object.needsLoading();
        else
            return false;
    }

    public void load(Formatter formatter)
    {
        if(object != null)
            object.load(formatter);
    }

    public boolean isMappable()
    {
        if(object != null)
            return object.isMappable();
        else
            return false;
    }

    public NamedLink map(DocLine docline, DocStyle docstyle, String s, int i, int j)
    {
        if(object != null)
            return object.map(docline, docstyle, s, i, j);
        else
            return null;
    }

    public OBJECT()
    {
    }

    DocItem object;
    public static final String activatorClassID = "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93";
}
