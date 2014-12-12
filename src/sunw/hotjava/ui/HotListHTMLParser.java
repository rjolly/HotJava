// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotList.java

package sunw.hotjava.ui;

import java.io.IOException;
import java.io.PrintStream;
import sunw.html.*;

// Referenced classes of package sunw.hotjava.ui:
//            HotList

class HotListHTMLParser extends Parser
{

    HotListHTMLParser(HotList hotlist1)
    {
        inTitleContext = false;
        errorState = false;
        title = "";
        reverse = "false";
        inH3Context = false;
        inListTitleContext = false;
        hotlist = hotlist1;
    }

    void dbg(String s)
    {
    }

    public String getErrorReport()
    {
        return firstError;
    }

    public int getErrorCount()
    {
        return errorCount;
    }

    protected boolean parseMarkupDeclarations(StringBuffer stringbuffer)
        throws IOException
    {
        if(stringbuffer.length() == "DOCTYPE".length() && stringbuffer.toString().toUpperCase().equals("DOCTYPE"))
        {
            String s = parseDTDMarkup();
            if(!s.equals(""))
                errorState = hotlist.processDoctype(s);
            return true;
        } else
        {
            return false;
        }
    }

    protected void handleText(char ac[], Tag tag)
    {
        if(errorState)
            return;
        if(inTitleContext)
        {
            title = title + new String(ac);
            return;
        }
        if(inH3Context)
            if(special != null)
            {
                if(special.equals("personal"))
                    folderName = hotlist.newPlaces;
                else
                if(special.equals("cool"))
                    folderName = hotlist.coolPlaces;
                special = null;
                return;
            } else
            {
                folderName = new String(ac);
                return;
            }
        if(inListTitleContext)
        {
            hotListName = new String(ac);
            return;
        } else
        {
            return;
        }
    }

    protected void handleComment(String s)
    {
    }

    protected void handleEmptyTag(Tag tag)
    {
        String s = tag.getElement().getName();
        if("hr".equals(s))
        {
            hotlist.addSeparatorItem();
            return;
        } else
        {
            return;
        }
    }

    protected void handleStartTag(Tag tag)
    {
        if(errorState)
            return;
        String s = tag.getElement().getName();
        if("a".equals(s))
        {
            Attributes attributes = tag.getAttributes();
            String s1 = attributes.get("href");
            if(s1 != null)
            {
                inTitleContext = true;
                url = s1;
                title = "";
            }
            add_date = attributes.get("add_date");
            last_modified = attributes.get("last_modified");
            String s3 = attributes.get("visit_count");
            String s5 = attributes.get("last_visit");
            if(s5 != null)
                try
                {
                    last_visit = Integer.parseInt(s5);
                }
                catch(NumberFormatException _ex)
                {
                    last_visit = 0;
                }
            if(s3 != null)
                try
                {
                    visit_count = Integer.parseInt(s3);
                }
                catch(NumberFormatException _ex)
                {
                    visit_count = 0;
                }
            folded = null;
            return;
        }
        if("dl".equals(s))
        {
            if(dlCount != 0 || !hotlist.importOurType())
                hotlist.folderPush();
            dlCount++;
            return;
        }
        if("h3".equals(s))
        {
            inH3Context = true;
            Attributes attributes1 = tag.getAttributes();
            if(attributes1 != null)
            {
                add_date = attributes1.get("add_date");
                String s2 = attributes1.get("visit_count");
                String s4 = attributes1.get("last_visit");
                String s6 = attributes1.get("sort_type");
                String s7 = attributes1.get("reverse");
                if(s4 != null)
                    try
                    {
                        last_visit = Integer.parseInt(s4);
                    }
                    catch(NumberFormatException _ex)
                    {
                        last_visit = 0;
                    }
                if(s2 != null)
                    try
                    {
                        visit_count = Integer.parseInt(s2);
                    }
                    catch(NumberFormatException _ex)
                    {
                        visit_count = 0;
                    }
                if(s7 == null)
                    s7 = "false";
                if(s6 != null)
                    try
                    {
                        sort_type = Integer.parseInt(attributes1.get("sort_type"));
                    }
                    catch(NumberFormatException _ex)
                    {
                        sort_type = 70;
                    }
                else
                    sort_type = 70;
                folded = attributes1.get("folded");
                special = attributes1.get("special");
            }
            last_modified = null;
            return;
        }
        if("title".equals(s))
        {
            inListTitleContext = true;
            return;
        } else
        {
            return;
        }
    }

    protected void handleEndTag(Tag tag)
    {
        if(errorState)
            return;
        String s = tag.getElement().getName();
        if("a".equals(s))
        {
            inTitleContext = false;
            if(url != null)
            {
                if(title != null)
                    title = title.replace('\n', ' ');
                else
                    title = "";
                hotlist.addGotoItem(url, title, visit_count, last_visit, -5);
            }
            return;
        }
        if("h3".equals(s))
        {
            inH3Context = false;
            if(folderName != null)
                folderName = folderName.replace('\n', ' ');
            else
                folderName = "";
            folderName = hotlist.getUniqueName(folderName);
            if(folded == null)
            {
                hotlist.addGotoFolder(folderName, false, visit_count, last_visit, sort_type, reverse, -5);
                return;
            } else
            {
                hotlist.addGotoFolder(folderName, true, visit_count, last_visit, sort_type, reverse, -5);
                return;
            }
        }
        if("dl".equals(s))
        {
            hotlist.folderPop();
            return;
        }
        if("title".equals(s))
        {
            inListTitleContext = false;
            return;
        } else
        {
            return;
        }
    }

    protected void handleError(int i, String s)
    {
        if(firstError == null)
            firstError = "line " + i + ": error: " + s;
        errorCount++;
    }

    protected void error(String s, String s1, String s2, String s3)
    {
        if(firstError == null)
        {
            super.error(s, s1, s2, s3);
            return;
        } else
        {
            handleError(getCurrentLine(), s);
            return;
        }
    }

    HotList hotlist;
    String firstError;
    int errorCount;
    boolean inTitleContext;
    int dlCount;
    boolean errorState;
    String title;
    String url;
    String add_date;
    int last_visit;
    String last_modified;
    int visit_count;
    int sort_type;
    String reverse;
    String folded;
    boolean inH3Context;
    String folderName;
    String special;
    boolean inListTitleContext;
    String hotListName;
    static final boolean debug = false;
}
