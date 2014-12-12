// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocConstants.java

package sunw.hotjava.doc;


public interface DocConstants
{

    public static final int FORMAT_BEGIN = 0;
    public static final int FORMAT_MIDDLE = 1;
    public static final int FORMAT_END = 2;
    public static final int FORMAT_LEFT = 0;
    public static final int FORMAT_RIGHT = 1;
    public static final int FORMAT_NONE = 2;
    public static final int FORMAT_CENTER = 3;
    public static final int FORMAT_JUSTIFIED = 0;
    public static final int STATE_EDITABLE = 0;
    public static final int STATE_NOTEDITABLE = 1;
    public static final int STATE_CHANGED = 2;
    public static final int STATE_ERROR = 3;
    public static final int STATE_RELOADING = 4;
    public static final int STATE_BUSY = 10;
    public static final int STATE_CONNECTING = 11;
    public static final int STATE_LOADING = 12;
    public static final int STATE_SAVING = 13;
    public static final int STATE_PRINTING = 14;
    public static final int STATE_INTERRUPTED = 15;
    public static final int DOC_SPLIT_TEXT = 10;
    public static final int DOC_JOIN_TEXT = 11;
    public static final int DOC_INSERT_TEXT = 12;
    public static final int DOC_DELETE_TEXT = 13;
    public static final int DOC_INSERT_ITEMS = 15;
    public static final int DOC_DELETE_ITEMS = 16;
    public static final int DOC_CHANGE_RANGE = 17;
    public static final int DOC_PAINT_RANGE = 18;
    public static final int DOC_UPDATE_RANGE = 19;
    public static final int DOC_ACTIVATE_ITEM = 20;
    public static final int DOC_DEACTIVATE_ITEM = 21;
    public static final int DOC_PARSE_COMPLETED = 22;
    public static final int DOC_FORMAT_SCREEN = 23;
    public static final int DOC_RELOAD = 24;
    public static final int OFFBITS = 12;
    public static final int OFFMASK = 4095;
    public static final int OFFMAX = 4095;
    public static final int INDEXBITS = 20;
    public static final int INDEXINC = 4096;
    public static final int INDEXMASK = 0x7ffff000;
    public static final int INDEXMAX = 0x7ffff000;
    public static final int MAXPOS = 0x7fffffff;
    public static final int MAXITEMS = 0x7ffff;
    public static final String NEWLINE = "\n";
    public static final int NOBREAK = 1;
    public static final int BREAK = 0;
    public static final int BREAK_HINT = 2;
}
