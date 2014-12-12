// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImageCacher.java

package sunw.hotjava.bean;

import java.awt.Image;
import java.net.URL;

public interface ImageCacher
{

    public abstract void setReferenceDepth(int i);

    public abstract Image getImage(Object obj, URL url, URL url1);

    public abstract void flushAllImages();

    public abstract void flushDocumentImages(Object obj);
}
