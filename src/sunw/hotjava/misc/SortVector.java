// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SortVector.java

package sunw.hotjava.misc;

import java.util.Vector;

// Referenced classes of package sunw.hotjava.misc:
//            Compare

public class SortVector
{

    private static void swap(Vector vector, int i, int j)
    {
        Object obj = vector.elementAt(i);
        vector.setElementAt(vector.elementAt(j), i);
        vector.setElementAt(obj, j);
    }

    public static void quicksort(Vector vector, int i, int j, Compare compare)
    {
        if(i >= j)
            return;
        int l = i;
        for(int k = i + 1; k <= j; k++)
            if(compare.doCompare(vector.elementAt(k), vector.elementAt(i)) < 0)
                swap(vector, ++l, k);

        swap(vector, i, l);
        quicksort(vector, i, l - 1, compare);
        quicksort(vector, l + 1, j, compare);
    }

    public static void quicksort(Vector vector, Compare compare)
    {
        quicksort(vector, 0, vector.size() - 1, compare);
    }

    public static void reverse(Vector vector)
    {
        int i = vector.size();
        for(int j = 0; j < i / 2; j++)
            swap(vector, j, i - (j + 1));

    }

    public SortVector()
    {
    }
}
