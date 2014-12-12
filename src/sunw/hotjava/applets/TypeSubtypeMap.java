// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ContentViewersPreferencesDialog.java

package sunw.hotjava.applets;

import java.io.*;
import java.util.*;
import sunw.hotjava.misc.Compare;
import sunw.hotjava.misc.Sort;

// Referenced classes of package sunw.hotjava.applets:
//            ContentViewersPreferencesDialog

class TypeSubtypeMap
    implements Compare
{

    public TypeSubtypeMap()
    {
        map = new String[7][];
        try
        {
            InputStream inputstream = (sunw.hotjava.misc.HJBProperties.class).getResourceAsStream("/lib/type-subtypes-map.properties");
            if(inputstream == null)
            {
                throw new IOException("Unable to find a property file : type-subtypes-map.properties");
            } else
            {
                read(inputstream);
                inputstream.close();
                return;
            }
        }
        catch(IOException ioexception)
        {
            System.err.println("TypeSubtypeMap constructor failed: " + ioexception);
        }
    }

    private void read(InputStream inputstream)
    {
        Properties properties = new Properties();
        try
        {
            properties.load(inputstream);
            for(int i = 0; i < typeNames.length; i++)
            {
                String s = (String)properties.get(typeNames[i]);
                parse(i, s);
            }

            return;
        }
        catch(IOException ioexception)
        {
            System.err.println("TypeSubtypeMap.read failed: " + ioexception);
        }
    }

    private void parse(int i, String s)
    {
        if(s == null)
        {
            System.err.println("TypeSubtypeMap.parse got empty list");
            return;
        }
        StringTokenizer stringtokenizer = new StringTokenizer(s, "|");
        int j = stringtokenizer.countTokens();
        String as[] = new String[j];
        for(int k = 0; stringtokenizer.hasMoreTokens(); k++)
            as[k] = stringtokenizer.nextToken();

        Sort.quicksort(as, this);
        map[i] = as;
    }

    public String[] getSubtypes(int i)
    {
        try
        {
            return map[i];
        }
        catch(IndexOutOfBoundsException _ex)
        {
            return null;
        }
    }

    public boolean typeHasSubtype(int i, String s)
    {
        String as[] = map[i];
        if(s == null || as == null)
            return false;
        for(int j = 0; j < as.length; j++)
            if(s.equals(as[j]))
                return true;

        return false;
    }

    public void addSubtype(int i, String s)
    {
        try
        {
            if(s == null)
                return;
            String as[] = map[i];
            if(as == null)
            {
                as = new String[1];
                as[0] = s;
                map[i] = as;
                return;
            }
            if(typeHasSubtype(i, s))
            {
                return;
            } else
            {
                String as1[] = new String[as.length + 1];
                System.arraycopy(as, 0, as1, 0, as.length);
                as1[as.length] = s;
                Sort.quicksort(as1, this);
                map[i] = as1;
                return;
            }
        }
        catch(IndexOutOfBoundsException indexoutofboundsexception)
        {
            indexoutofboundsexception.printStackTrace();
        }
    }

    public int doCompare(Object obj, Object obj1)
    {
        String s = (String)obj;
        String s1 = (String)obj1;
        return s.compareTo(s1);
    }

    String map[][];
    static final int APPLICATION = 0;
    static final int AUDIO = 1;
    static final int IMAGE = 2;
    static final int MESSAGE = 3;
    static final int MULTIPART = 4;
    static final int TEXT = 5;
    static final int VIDEO = 6;
    static final String typeNames[] = {
        "application", "audio", "image", "message", "multipart", "text", "video"
    };

}
