// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ColorNameTable.java

package sunw.hotjava.misc;

import java.awt.Color;
import java.io.*;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;

// Referenced classes of package sunw.hotjava.misc:
//            HJBProperties

public class ColorNameTable
{

    private static synchronized void loadColorNames()
    {
        if(loadedFullTable)
            return;
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        String s = hjbproperties.getProperty("hotjava.home");
        File file = new File(s + File.separator + "lib" + File.separator + "colorname.properties");
        BufferedReader bufferedreader = null;
        if(file.canRead())
            try
            {
                bufferedreader = new BufferedReader(new FileReader(file));
            }
            catch(FileNotFoundException _ex)
            {
                return;
            }
        else
            try
            {
                java.io.InputStream inputstream = (sunw.hotjava.misc.ColorNameTable.class).getResourceAsStream("/lib/colorname.properties");
                if(inputstream != null)
                {
                    bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
                } else
                {
                    java.io.InputStream inputstream1 = (sunw.hotjava.misc.ColorNameTable.class).getResourceAsStream("/lib/colorname.properties.gz");
                    if(inputstream1 != null)
                        bufferedreader = new BufferedReader(new InputStreamReader(new GZIPInputStream(inputstream1)));
                    else
                        return;
                }
            }
            catch(IOException _ex)
            {
                return;
            }
        try
        {
            Hashtable hashtable = new Hashtable(500);
            load(bufferedreader, hashtable);
            loadedFullTable = true;
            colorTable = hashtable;
            bufferedreader.close();
            return;
        }
        catch(IOException _ex)
        {
            return;
        }
    }

    public static Color getColor(String s)
    {
        s = s.toLowerCase();
        Color color = (Color)colorTable.get(s);
        if(color == null)
        {
            if(loadedFullTable)
            {
                return null;
            } else
            {
                System.gc();
                System.runFinalization();
                loadColorNames();
                return (Color)colorTable.get(s);
            }
        } else
        {
            return color;
        }
    }

    private static void load(Reader reader, Hashtable hashtable)
        throws IOException
    {
        int i = reader.read();
        do
            switch(i)
            {
            case -1: 
                return;

            case 33: // '!'
            case 35: // '#'
                do
                    i = reader.read();
                while(i >= 0 && i != 10 && i != 13);
                break;

            case 9: // '\t'
            case 10: // '\n'
            case 13: // '\r'
            case 32: // ' '
                i = reader.read();
                break;

            default:
                StringBuffer stringbuffer = new StringBuffer();
                for(; i >= 0 && i != 61 && i != 58 && i != 32 && i != 9 && i != 10 && i != 13; i = reader.read())
                    stringbuffer.append((char)i);

                for(; i == 32 && i == 9; i = reader.read());
                if(i == 61 || i == 58)
                    i = reader.read();
                for(; i == 32 && i == 9; i = reader.read());
                StringBuffer stringbuffer1 = new StringBuffer();
                while(i >= 0 && i != 10 && i != 13) 
                {
                    int j = 0;
                    if(i == 92)
                        switch(i = reader.read())
                        {
                        case 13: // '\r'
                            if((i = reader.read()) != 10 && i != 32 && i != 9)
                                continue;
                            // fall through

                        case 10: // '\n'
                            while((i = reader.read()) == 32 || i == 9) ;
                            continue;

                        case 116: // 't'
                            i = 9;
                            j = reader.read();
                            break;

                        case 110: // 'n'
                            i = 10;
                            j = reader.read();
                            break;

                        case 114: // 'r'
                            i = 13;
                            j = reader.read();
                            break;

                        case 117: // 'u'
                            while((i = reader.read()) == 117) ;
                            int k = 0;
label0:
                            for(int l = 0; l < 4; l++)
                            {
                                j = reader.read();
                                switch(i)
                                {
                                default:
                                    break label0;

                                case 48: // '0'
                                case 49: // '1'
                                case 50: // '2'
                                case 51: // '3'
                                case 52: // '4'
                                case 53: // '5'
                                case 54: // '6'
                                case 55: // '7'
                                case 56: // '8'
                                case 57: // '9'
                                    k = ((k << 4) + i) - 48;
                                    break;

                                case 97: // 'a'
                                case 98: // 'b'
                                case 99: // 'c'
                                case 100: // 'd'
                                case 101: // 'e'
                                case 102: // 'f'
                                    k = ((k << 4) + 10 + i) - 97;
                                    break;

                                case 65: // 'A'
                                case 66: // 'B'
                                case 67: // 'C'
                                case 68: // 'D'
                                case 69: // 'E'
                                case 70: // 'F'
                                    k = ((k << 4) + 10 + i) - 65;
                                    break;
                                }
                                i = j;
                            }

                            i = k;
                            break;

                        default:
                            j = reader.read();
                            break;
                        }
                    else
                        j = reader.read();
                    stringbuffer1.append((char)i);
                    i = j;
                }
                try
                {
                    hashtable.put(stringbuffer.toString().toLowerCase(), Color.decode("#" + stringbuffer1.toString()));
                }
                catch(NumberFormatException _ex) { }
                break;
            }
        while(true);
    }

    public ColorNameTable()
    {
    }

    private static Hashtable colorTable;
    private static boolean loadedFullTable;

    static 
    {
        colorTable = new Hashtable(16);
        colorTable.put("black", new Color(0));
        colorTable.put("silver", new Color(0xc0c0c0));
        colorTable.put("gray", new Color(0x808080));
        colorTable.put("white", new Color(0xffffff));
        colorTable.put("maroon", new Color(0x800000));
        colorTable.put("red", new Color(0xff0000));
        colorTable.put("purple", new Color(0x800080));
        colorTable.put("fuchsia", new Color(0xff00ff));
        colorTable.put("green", new Color(32768));
        colorTable.put("lime", new Color(65280));
        colorTable.put("olive", new Color(0x808000));
        colorTable.put("yellow", new Color(0xffff00));
        colorTable.put("navy", new Color(128));
        colorTable.put("blue", new Color(255));
        colorTable.put("teal", new Color(32896));
        colorTable.put("aqua", new Color(65535));
    }
}
