// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CharacterEncoding.java

package sunw.hotjava.misc;

import java.util.Hashtable;

public class CharacterEncoding
{

    public static String getNetworkName(String s)
    {
        return (String)networkNameTable.get(s);
    }

    public static String aliasName(String s)
    {
        String s1 = sun.io.CharacterEncoding.aliasName(s);
        if(s1 != null && internalNameTable.get(s1) != null)
            s1 = (String)internalNameTable.get(s1);
        return s1;
    }

    public CharacterEncoding()
    {
    }

    private static Hashtable networkNameTable;
    private static Hashtable internalNameTable;

    static 
    {
        networkNameTable = new Hashtable();
        networkNameTable.put("ASCII7", "us-ascii");
        networkNameTable.put("8859_1", "iso-8859-1");
        networkNameTable.put("8859_2", "iso-8859-2");
        networkNameTable.put("8859_3", "iso-8859-3");
        networkNameTable.put("8859_4", "iso-8859-4");
        networkNameTable.put("8859_5", "iso-8859-5");
        networkNameTable.put("8859_6", "iso-8859-6");
        networkNameTable.put("8859_7", "iso-8859-7");
        networkNameTable.put("8859_8", "iso-8859-8");
        networkNameTable.put("8859_9", "iso-8859-9");
        networkNameTable.put("JIS", "iso-2022-jp");
        networkNameTable.put("SJIS", "Shift_JIS");
        networkNameTable.put("EUCJIS", "EUC");
        networkNameTable.put("Cp437", "IBM437");
        networkNameTable.put("Cp737", "IBM737");
        networkNameTable.put("Cp775", "IBM775");
        networkNameTable.put("Cp850", "IBM850");
        networkNameTable.put("Cp852", "IBM852");
        networkNameTable.put("Cp855", "IBM855");
        networkNameTable.put("Cp857", "IBM857");
        networkNameTable.put("Cp860", "IBM860");
        networkNameTable.put("Cp861", "IBM861");
        networkNameTable.put("Cp863", "IBM863");
        networkNameTable.put("Cp865", "IBM865");
        networkNameTable.put("Cp866", "IBM866");
        networkNameTable.put("Cp869", "IBM869");
        networkNameTable.put("Cp874", "IBM874");
        networkNameTable.put("Cp1250", "windows-1250");
        networkNameTable.put("Cp1251", "windows-1251");
        networkNameTable.put("Cp1252", "windows-1252");
        networkNameTable.put("Cp1253", "windows-1253");
        networkNameTable.put("Cp1254", "windows-1254");
        networkNameTable.put("Cp1257", "windows-1257");
        networkNameTable.put("KOI8_R", "koi8-r");
        networkNameTable.put("ISO8859_1", "iso-8859-1");
        networkNameTable.put("ISO8859_2", "iso-8859-2");
        networkNameTable.put("ISO8859_3", "iso-8859-3");
        networkNameTable.put("ISO8859_4", "iso-8859-4");
        networkNameTable.put("ISO8859_5", "iso-8859-5");
        networkNameTable.put("ISO8859_6", "iso-8859-6");
        networkNameTable.put("ISO8859_7", "iso-8859-7");
        networkNameTable.put("ISO8859_8", "iso-8859-8");
        networkNameTable.put("ISO8859_9", "iso-8859-9");
        internalNameTable = new Hashtable();
        internalNameTable.put("ISO8859_1", "8859_1");
        internalNameTable.put("ISO8859_2", "8859_2");
        internalNameTable.put("ISO8859_3", "8859_3");
        internalNameTable.put("ISO8859_4", "8859_4");
        internalNameTable.put("ISO8859_5", "8859_5");
        internalNameTable.put("ISO8859_6", "8859_6");
        internalNameTable.put("ISO8859_7", "8859_7");
        internalNameTable.put("ISO8859_8", "8859_8");
        internalNameTable.put("ISO8859_9", "8859_9");
        internalNameTable.put("ISO2022JP", "JIS");
        internalNameTable.put("EUC_JP", "EUCJIS");
    }
}
