// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ReleasableResource.java

package sunw.hotjava.misc;


public interface ReleasableResource
{

    public abstract boolean releaseResources(int i);

    public static final int APPLETS = 1;
    public static final int FORMS = 2;
    public static final int IMAGES = 3;
    public static final int IMAGE_MAPS = 4;
    public static final int BACKGROUNDS = 5;
    public static final int STATELESS = 6;
    public static final int DOCUMENT = 7;
}
