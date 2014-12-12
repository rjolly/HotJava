// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   A.java

package sunw.hotjava.tags;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.bean.URLPooler;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;
import sunw.html.Attributes;

// Referenced classes of package sunw.hotjava.tags:
//            ImageMap

public class A extends FlowTagItem
{

    public static String getDefaultNewColor()
    {
        return "0x0000B0";
    }

    public static String getDefaultOldColor()
    {
        return "0x4000B0";
    }

    public static String getDefaultActiveColor()
    {
        return "0xF0F000";
    }

    public A()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        if(lazyLoadingNotDone)
        {
            newColor = hjbproperties.getColor("a.newcolor", Globals.mapNamedColor("0x0000B0"));
            oldColor = hjbproperties.getColor("a.oldcolor", Globals.mapNamedColor("0x4000B0"));
            activeColor = hjbproperties.getColor("a.activecolor", Globals.mapNamedColor("0xF0F000"));
            underline = "true".equals(hjbproperties.getProperty("anchorStyle"));
            lazyLoadingNotDone = false;
        }
        super.handlerStrings = myHandlers;
    }

    public int findLabel(String s)
    {
        if(super.atts != null && s.equals(super.atts.get("name")))
            return getIndex() << 12;
        else
            return -1;
    }

    public synchronized void init(Document document)
    {
        super.init(document);
        if(super.atts != null)
        {
            origHref = super.atts.get("href");
            if(origHref != null)
            {
                int i = 0;
                for(int j = origHref.length(); i < j && Character.isWhitespace(origHref.charAt(i)); i++);
                origHref = origHref.substring(i);
            }
            href = filterString(origHref);
            if(href != null)
                try
                {
                    href = href.length() == 0 ? (new URL(document.getBaseURL(), ".")).toExternalForm() : (new URL(document.getBaseURL(), href)).toExternalForm();
                }
                catch(MalformedURLException _ex) { }
            shape = super.atts.get("shape");
            coords = super.atts.get("coords");
            ismap = super.atts.get("ismap") != null;
        }
    }

    public void addImapInfo(Document document, ImageMap imagemap)
    {
        String s = null;
        int i = getIndex();
        for(int j = i + getOffset(); i < j; i++)
        {
            DocItem docitem = document.getItem(i);
            if(docitem instanceof TextItem)
                s = s + ((TextItem)docitem).getText();
        }

        imagemap.addArea(document.getBaseURL(), shape, coords, href, false, ismap, s, getLinkTarget(), null);
    }

    public void modifyStyle(DocStyle docstyle)
    {
        super.modifyStyle(docstyle);
        if(href != null)
        {
            URLPooler urlpooler = HotJavaBrowserBean.getURLPoolManager();
            if(urlpooler != null && urlpooler.get(href) != null)
                docstyle.color = (Color)docstyle.doc.getProperty("vlink.color", oldColor);
            else
                docstyle.color = (Color)docstyle.doc.getProperty("link.color", newColor);
            docstyle.href = href;
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            docstyle.underline = "true".equals(hjbproperties.getProperty("anchorStyle"));
        }
        if(docstyle.win.getActive() == this)
            docstyle.color = (Color)docstyle.doc.getProperty("alink.color", activeColor);
    }

    public boolean isMappable()
    {
        return href != null;
    }

    public NamedLink map(DocLine docline, DocStyle docstyle, String s, int i, int j)
    {
        URL url = null;
        try
        {
            url = new URL(href);
        }
        catch(MalformedURLException _ex)
        {
            try
            {
                url = new URL("doc://unknown.protocol/" + origHref);
            }
            catch(MalformedURLException _ex2) { }
        }
        if(url != null)
        {
            String s1 = getLinkTarget();
            return new NamedLink(s1, url, null);
        } else
        {
            return null;
        }
    }

    public void setURL(URL url)
    {
        href = url.toString();
        super.atts.put("href", url.toString());
    }

    public boolean visit(DocItemVisitor docitemvisitor)
    {
        return docitemvisitor.visitATag(this);
    }

    public static Color newColor = null;
    public static Color oldColor = null;
    public static Color activeColor = null;
    public static boolean underline = true;
    public String href;
    private String shape;
    private String coords;
    private boolean ismap;
    private static boolean lazyLoadingNotDone = true;
    private String origHref;
    private static final String defaultNewColor = "0x0000B0";
    private static final String defaultOldColor = "0x4000B0";
    private static final String defaultActiveColor = "0xF0F000";
    private static final String myHandlers[] = {
        "onclick", "ondblclick", "onmousedown", "onmouseup", "onmouseout", "onmouseover"
    };

}
