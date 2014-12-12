// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocItemVisitor.java

package sunw.hotjava.doc;

import sunw.hotjava.forms.*;
import sunw.hotjava.tags.*;

public interface DocItemVisitor
{

    public abstract boolean visitATag(A a);

    public abstract boolean visitAREATag(AREA area);

    public abstract boolean visitIMGTag(IMG img);

    public abstract boolean visitFORMTag(FORM form);

    public abstract boolean visitAPPLETTag(APPLET applet);

    public abstract boolean visitINPUTTag(INPUT input);

    public abstract boolean visitTEXTAREATag(TEXTAREA textarea);

    public abstract boolean visitSELECTTag(SELECT select);

    public abstract boolean visitOPTIONTag(OPTION option);
}
