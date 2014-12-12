// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SelectionOwner.java

package sunw.hotjava.doc;

import java.awt.datatransfer.*;

public interface SelectionOwner
    extends ClipboardOwner
{

    public abstract void lostOwnership();

    public abstract void lostOwnership(Clipboard clipboard, Transferable transferable);

    public abstract boolean hasSelection();

    public abstract String getSelectedText();
}
