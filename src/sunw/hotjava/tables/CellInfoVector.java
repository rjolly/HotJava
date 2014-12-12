// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CellInfoVector.java

package sunw.hotjava.tables;

import sunw.hotjava.doc.Measurement;

// Referenced classes of package sunw.hotjava.tables:
//            CellSpec, ConstraintSummary

class CellInfoVector
{

    CellInfoVector(int i, int j, int k)
    {
        cells = new CellSpec[i];
        cellPadding = j;
        cellSpacing = k;
    }

    void addConstraintInfo(int i, int j, Measurement measurement, int k)
    {
        maxSpan = Math.max(maxSpan, j);
        CellSpec cellspec = cells[i];
        if(cellspec == null)
            cellspec = cells[i] = new CellSpec();
        if(cellspec.constraints == null)
        {
            cellspec.constraints = new ConstraintSummary(j, measurement, k);
            return;
        }
        if(cellspec.constraints.cellsSpanned > j)
        {
            ConstraintSummary constraintsummary = new ConstraintSummary(j, measurement, k);
            constraintsummary.next = cellspec.constraints;
            cellspec.constraints = constraintsummary;
            return;
        }
        ConstraintSummary constraintsummary1;
        for(constraintsummary1 = cellspec.constraints; constraintsummary1 != null; constraintsummary1 = constraintsummary1.next)
        {
            if(constraintsummary1.cellsSpanned == j)
            {
                constraintsummary1.accumulateInfo(measurement, k);
                return;
            }
            if(constraintsummary1.next == null || constraintsummary1.next.cellsSpanned > j)
                break;
        }

        ConstraintSummary constraintsummary2 = new ConstraintSummary(j, measurement, k);
        constraintsummary2.next = constraintsummary1.next;
        constraintsummary1.next = constraintsummary2;
    }

    ConstraintSummary getConstraintFor(int i, int j)
    {
        if(i >= 0 && i < cells.length && cells[i] != null)
        {
            for(ConstraintSummary constraintsummary = cells[i].constraints; constraintsummary != null && constraintsummary.cellsSpanned <= j; constraintsummary = constraintsummary.next)
                if(constraintsummary.cellsSpanned == j)
                    return constraintsummary;

        }
        return null;
    }

    int getPercentage(int i)
    {
        if(i >= cells.length || i < 0)
            return -1;
        CellSpec cellspec = cells[i];
        if(cellspec != null)
            return cellspec.percentage;
        else
            return -1;
    }

    void setPercentage(int i, int j)
    {
        if(i < cells.length && i >= 0)
        {
            CellSpec cellspec = cells[i];
            if(cellspec != null)
                cellspec.percentage = j;
        }
    }

    int getAssignedSize(int i)
    {
        if(i >= cells.length || i < 0)
            return 0;
        CellSpec cellspec = cells[i];
        if(cellspec != null)
            return cellspec.assignedSize;
        else
            return 0;
    }

    void setAssignedSize(int i, int j)
    {
        if(i < cells.length && i >= 0)
        {
            CellSpec cellspec = cells[i];
            if(cellspec != null)
                cellspec.assignedSize = j;
        }
    }

    void setAssignedSize(int i, int j, int k)
    {
        if(i < cells.length && i >= 0)
        {
            CellSpec cellspec = cells[i];
            if(cellspec != null)
            {
                cellspec.assignedSize = j;
                cellspec.percentage = k;
            }
        }
    }

    void measure(Measurement measurement)
    {
        for(int i = 0; i < cells.length; i++)
        {
            CellSpec cellspec = cells[i];
            if(cellspec == null)
                cells[i] = cellspec = new CellSpec();
            cellspec.cumulativeMinSize = 0;
            cellspec.cumulativePrefSize = 0;
            for(ConstraintSummary constraintsummary = cellspec.constraints; constraintsummary != null; constraintsummary = constraintsummary.next)
            {
                int j = constraintsummary.minSize;
                int k = constraintsummary.prefSize;
                int l = i - constraintsummary.cellsSpanned;
                if(l >= 0)
                {
                    j += cells[l].cumulativeMinSize + cellSpacing;
                    k += cells[l].cumulativePrefSize + cellSpacing;
                }
                cellspec.cumulativeMinSize = Math.max(cellspec.cumulativeMinSize, j);
                cellspec.cumulativePrefSize = Math.max(cellspec.cumulativePrefSize, k);
            }

        }

        if(cells == null || cells.length == 0)
        {
            measurement.setMinWidth(0);
            measurement.setPreferredWidth(0);
            return;
        } else
        {
            CellSpec cellspec1 = cells[cells.length - 1];
            measurement.setMinWidth(cellspec1.cumulativeMinSize);
            measurement.setPreferredWidth(cellspec1.cumulativePrefSize);
            return;
        }
    }

    int length()
    {
        return cells.length;
    }

    int getMaxSpan()
    {
        return maxSpan;
    }

    CellSpec cells[];
    int maxSpan;
    int cellPadding;
    int cellSpacing;
}
