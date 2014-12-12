// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Tag.java

package sunw.html;


// Referenced classes of package sunw.html:
//            Element, Attributes

public interface Tag
{

    public abstract boolean isBlock();

    public abstract boolean isPreformatted();

    public abstract Element getElement();

    public abstract Attributes getAttributes();

    public abstract boolean hasJavaScriptHandlers();
}
