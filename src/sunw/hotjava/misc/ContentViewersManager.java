// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ContentViewersManager.java

package sunw.hotjava.misc;

import java.io.*;
import java.util.*;
import sun.net.www.MimeEntry;
import sun.net.www.MimeTable;

// Referenced classes of package sunw.hotjava.misc:
//            Compare, HJBProperties, Sort

public class ContentViewersManager extends Observable
    implements Compare
{

    public static void create()
    {
        getManager();
    }

    public static ContentViewersManager getManager()
    {
        if(manager == null)
            manager = new ContentViewersManager();
        return manager;
    }

    private ContentViewersManager()
    {
        removedViewers = new Vector(10);
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        Hashtable hashtable = new Hashtable();
        String s = hjbproperties.getProperty("admin.mimetype.handlers");
        if(s != null)
        {
            for(StringTokenizer stringtokenizer = new StringTokenizer(s, "|"); stringtokenizer.hasMoreTokens();)
            {
                String s2 = stringtokenizer.nextToken();
                int i = s2.indexOf('=');
                if(i > 0)
                    hashtable.put(s2.substring(0, i), s2.substring(i + 1));
            }

        }
        s = hjbproperties.getProperty("mimetype.handlers");
        if(s != null)
        {
            for(StringTokenizer stringtokenizer1 = new StringTokenizer(s, "|"); stringtokenizer1.hasMoreTokens();)
            {
                String s3 = stringtokenizer1.nextToken();
                int j = s3.indexOf('=');
                if(j > 0)
                    hashtable.put(s3.substring(0, j), s3.substring(j + 1));
            }

        }
        String s1 = System.getProperty("user.home");
        if(s1 != null && !s1.endsWith(File.separator))
            s1 = s1 + File.separator;
        String s4 = s1 + ".hotjava" + File.separator + "content-types.properties";
        File file = new File(s4);
        if(file.exists())
            hjbproperties.put("content.types.user.table", s4);
        viewers = MimeTable.getDefaultTable();
        if("JavaOS".equals(System.getProperty("os.name")) && file.exists())
            try
            {
                Properties properties = new Properties();
                BufferedInputStream bufferedinputstream = new BufferedInputStream(new FileInputStream(file));
                for(Enumeration enumeration1 = viewers.elements(); enumeration1.hasMoreElements(); viewers.remove((MimeEntry)enumeration1.nextElement()));
                properties.load(bufferedinputstream);
                bufferedinputstream.close();
                parse(properties);
            }
            catch(IOException ioexception)
            {
                System.err.println("MimeTable.load: file = " + file.getPath() + ", " + ioexception);
                ioexception.printStackTrace();
            }
        for(Enumeration enumeration = hashtable.keys(); enumeration.hasMoreElements();)
        {
            String s5 = (String)enumeration.nextElement();
            String s6 = (String)hashtable.get(s5);
            MimeEntry mimeentry = viewers.find(s5);
            if(mimeentry != null)
            {
                mimeentry.setAction(1, s6);
            } else
            {
                MimeEntry mimeentry1 = new MimeEntry(s5);
                mimeentry1.setAction(1, s6);
                viewers.add(mimeentry1);
            }
        }

        setCurrentViewer(viewers.find("text/plain"));
    }

    public MimeTable getMimeTable()
    {
        return viewers;
    }

    public MimeEntry getContentViewer(String s)
    {
        return viewers.find(s);
    }

    public MimeEntry getContentViewer(String s, String s1)
    {
        return getContentViewer(s + "/" + s1);
    }

    public Enumeration getViewersInfo()
    {
        return viewers.elements();
    }

    public MimeEntry getCurrentViewer()
    {
        return currentViewer;
    }

    public void setCurrentViewer(MimeEntry mimeentry)
    {
        currentViewer = (MimeEntry)mimeentry.clone();
        setChanged();
    }

    public void setCurrentViewerByType(String s)
    {
        try
        {
            MimeEntry mimeentry = viewers.find(s);
            if(mimeentry != null)
                currentViewer = (MimeEntry)mimeentry.clone();
            else
                currentViewer = new MimeEntry(s);
        }
        catch(NullPointerException nullpointerexception)
        {
            System.err.println("ContentViewersManager.setCurrentViewerByType: " + nullpointerexception);
        }
        setChanged();
    }

    public void setCurrentViewerByDescription(String s)
    {
        try
        {
            MimeEntry mimeentry = viewers.findByDescription(s);
            if(mimeentry != null)
                currentViewer = (MimeEntry)mimeentry.clone();
        }
        catch(NullPointerException nullpointerexception)
        {
            System.err.println("ContentViewersManager.setCurrentViewerByDescription: " + nullpointerexception);
        }
        setChanged();
    }

    public void setType(String s)
    {
        currentViewer.setType(s);
        setChanged();
    }

    public void setAction(int i)
    {
        currentViewer.setAction(i);
        setChanged();
    }

    public void setCommand(String s)
    {
        currentViewer.setCommand(s);
        setChanged();
    }

    public void setDescription(String s)
    {
        currentViewer.setDescription(s);
        setChanged();
    }

    public void setFileExtensions(String s)
    {
        currentViewer.setExtensions(s);
        setChanged();
    }

    public void setIconFilename(String s)
    {
        currentViewer.setImageFileName(s);
        setChanged();
    }

    public void remove(String s)
    {
        viewers.remove(s);
    }

    public void removeCurrentViewer()
    {
        String s = currentViewer.getType();
        MimeEntry mimeentry = viewers.remove(s);
        removedViewers.addElement(mimeentry);
        currentViewer = null;
        setChanged();
    }

    public void undoRemove()
    {
        for(Enumeration enumeration = removedViewers.elements(); enumeration.hasMoreElements(); viewers.add(currentViewer))
        {
            MimeEntry mimeentry = (MimeEntry)enumeration.nextElement();
            setCurrentViewer(mimeentry);
        }

        setChanged();
        removedViewers.removeAllElements();
    }

    public void defineViewer(String s, int i, String s1, String s2, String s3, String s4)
    {
        MimeEntry mimeentry = new MimeEntry(s);
        mimeentry.setAction(i);
        if(s1 != null && s1.length() > 0)
            mimeentry.setCommand(s1);
        if(s2 != null && s2.length() > 0)
            mimeentry.setExtensions(s2);
        if(s3 != null && s3.length() > 0)
            mimeentry.setImageFileName(s3);
        if(s4 != null && s4.length() > 0)
            mimeentry.setDescription(s4);
        setCurrentViewer(mimeentry);
        setChanged();
    }

    public void apply()
    {
        viewers.add(currentViewer);
        removedViewers.removeAllElements();
        setChanged();
    }

    public boolean save()
    {
        String s = System.getProperty("user.home");
        if(s != null && !s.endsWith(File.separator))
            s = s + File.separator;
        String s1 = s + ".hotjava" + File.separator + "content-types.properties";
        if(viewers.save(s1))
        {
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
            hjbproperties.put("content.types.user.table", s1);
            return true;
        } else
        {
            return false;
        }
    }

    public int doCompare(Object obj, Object obj1)
    {
        if(obj == null)
            return -1;
        if(obj1 == null)
        {
            return 1;
        } else
        {
            MimeEntry mimeentry = (MimeEntry)obj;
            MimeEntry mimeentry1 = (MimeEntry)obj1;
            return mimeentry.getDescription().compareTo(mimeentry1.getDescription());
        }
    }

    public MimeEntry[] getSortedViewers()
    {
        MimeEntry amimeentry[] = new MimeEntry[viewers.getSize()];
        Enumeration enumeration = getViewersInfo();
        for(int i = 0; enumeration.hasMoreElements(); i++)
            amimeentry[i] = (MimeEntry)enumeration.nextElement();

        Sort.quicksort(amimeentry, this);
        return amimeentry;
    }

    void parse(Properties properties)
    {
        String s = (String)properties.get("temp.file.template");
        if(s != null)
            properties.remove("temp.file.template");
        String s1;
        String s2;
        for(Enumeration enumeration = properties.propertyNames(); enumeration.hasMoreElements(); parse(s1, s2))
        {
            s1 = (String)enumeration.nextElement();
            s2 = properties.getProperty(s1);
        }

    }

    void parse(String s, String s1)
    {
        MimeEntry mimeentry = new MimeEntry(s);
        String s2;
        for(StringTokenizer stringtokenizer = new StringTokenizer(s1, ";"); stringtokenizer.hasMoreTokens(); parse(s2, mimeentry))
            s2 = stringtokenizer.nextToken();

        viewers.add(mimeentry);
    }

    void parse(String s, MimeEntry mimeentry)
    {
        String s1 = null;
        String s2 = null;
        boolean flag = false;
        for(StringTokenizer stringtokenizer = new StringTokenizer(s, "="); stringtokenizer.hasMoreTokens();)
            if(flag)
            {
                s2 = stringtokenizer.nextToken().trim();
            } else
            {
                s1 = stringtokenizer.nextToken().trim();
                flag = true;
            }

        fill(mimeentry, s1, s2);
    }

    void fill(MimeEntry mimeentry, String s, String s1)
    {
        if("description".equalsIgnoreCase(s))
        {
            mimeentry.setDescription(s1);
            return;
        }
        if("action".equalsIgnoreCase(s))
        {
            mimeentry.setAction(getActionCode(s1));
            return;
        }
        if("application".equalsIgnoreCase(s))
        {
            mimeentry.setCommand(s1);
            return;
        }
        if("icon".equalsIgnoreCase(s))
        {
            mimeentry.setImageFileName(s1);
            return;
        }
        if("file_extensions".equalsIgnoreCase(s))
            mimeentry.setExtensions(s1);
    }

    int getActionCode(String s)
    {
        String as[] = {
            "unknown", "browser", "save", "application"
        };
        for(int i = 0; i < as.length; i++)
            if(s.equalsIgnoreCase(as[i]))
                return i;

        return 0;
    }

    private static ContentViewersManager manager = null;
    private MimeTable viewers;
    private MimeEntry currentViewer;
    private Vector removedViewers;

}
