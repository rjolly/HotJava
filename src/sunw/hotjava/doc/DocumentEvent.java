// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentEvent.java

package sunw.hotjava.doc;

import java.util.EventObject;

// Referenced classes of package sunw.hotjava.doc:
//            Document, DocItem

public class DocumentEvent extends EventObject
{

    public DocumentEvent(Object obj, int i, boolean flag, Object obj1)
    {
        super(obj);
        eventCancelled = false;
        arg = obj1;
        id = i;
        shiftDown = flag;
    }

    public DocumentEvent(Object obj, int i, Object obj1)
    {
        this(obj, i, false, obj1);
    }

    public int getID()
    {
        return id;
    }

    public Object getArgument()
    {
        return arg;
    }

    public boolean isShiftDown()
    {
        return shiftDown;
    }

    public void setDocument(Document document)
    {
        doc = document;
    }

    public Document getDocument()
    {
        return doc;
    }

    public DocItem getDocItemSource()
    {
        return docItemSource;
    }

    public void setDocItemSource(DocItem docitem)
    {
        docItemSource = docitem;
    }

    public boolean getEventCancelled()
    {
        return eventCancelled;
    }

    public void setEventCancelled(boolean flag)
    {
        eventCancelled = flag;
    }

    public int getClickCount()
    {
        return clickCount;
    }

    public void setClickCount(int i)
    {
        clickCount = i;
    }

    public static final int DOC_EVENT = 1000;
    public static final int STATUS = 1000;
    public static final int LINK = 1001;
    public static final int GOTO = 1002;
    public static final int RESIZE = 1003;
    public static final int PROP = 1004;
    public static final int STATE = 1005;
    public static final int SHOW = 1006;
    public static final int SELECT = 1007;
    public static final int HIST = 1008;
    public static final int INTERRUPT = 1009;
    public static final int SCROLL = 1010;
    public static final int BADLINK = 1011;
    public static final int STOPLOADING = 1012;
    public static final int SAVE = 1013;
    public static final int REMEMBER = 1014;
    public static final int CLONE = 1015;
    public static final int SHOW_MENUS = 1016;
    public static final int ADD_LISTENER_TO_FORMATTER = 1017;
    public static final int REMOVE_LISTENER_FROM_FORMATTER = 1018;
    public static final int ADD_LISTENER_TO_APPLET = 1019;
    public static final int BACK = 1020;
    public static final int FORWARD = 1021;
    public static final int OPEN = 1022;
    public static final int SET_MSG = 1024;
    public static final int INTERNAL_GOTO = 1025;
    public static final int SET_LASTFOCUS_HOLDER = 1026;
    public static final int HOVERINFO = 1027;
    public static final int REDISPLAYED = 1028;
    public static final int ADDFRAME = 1029;
    public static final int DELFRAME = 1030;
    public static final int GOTO_FROM_POPUP = 1031;
    public static final int BASEURL = 1032;
    public static final int SHOWTEMPLATE = 1033;
    public static final int ABOUT_TO_GOTO = 1034;
    public static final int MOUSE_DOWN_LINK = 1035;
    public static final int MOUSE_UP_LINK = 1036;
    public static final int REPAINT = 1037;
    public static final int FORMAT_COMPLETE = 1038;
    public static final int DEFAULT_STATUS = 1039;
    public static final int UNLOAD = 1040;
    public static final int FRAME_EXISTS = 1041;
    public static final int FRAME_RESHOWN = 1042;
    public static final int LISTENERS_READY = 1043;
    Object arg;
    protected int id;
    boolean shiftDown;
    private Document doc;
    private DocItem docItemSource;
    private boolean eventCancelled;
    private int clickCount;
}
