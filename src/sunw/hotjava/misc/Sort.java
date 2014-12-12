// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Sort.java

package sunw.hotjava.misc;


// Referenced classes of package sunw.hotjava.misc:
//            Compare

public class Sort
{

    private static void swap(Object aobj[], int i, int j)
    {
        Object obj = aobj[i];
        aobj[i] = aobj[j];
        aobj[j] = obj;
    }

    public static void quicksort(Object aobj[], int i, int j, Compare compare)
    {
        if(i >= j)
            return;
        swap(aobj, i, (i + j) / 2);
        int l = i;
        for(int k = i + 1; k <= j; k++)
            if(compare.doCompare(aobj[k], aobj[i]) < 0)
                swap(aobj, ++l, k);

        swap(aobj, i, l);
        quicksort(aobj, i, l - 1, compare);
        quicksort(aobj, l + 1, j, compare);
    }

    public static void quicksort(Object aobj[], Compare compare)
    {
        quicksort(aobj, 0, aobj.length - 1, compare);
    }

    public Sort()
    {
    }
}
