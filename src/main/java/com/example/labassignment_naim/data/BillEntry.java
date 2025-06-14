package com.example.labassignment_naim.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bills")
public class BillEntry {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String month;
    public double unitsUsed;
    public double rebatePercentage;
    public double totalCharges;
    public double finalCost;

    public BillEntry(String month, double unitsUsed, double rebatePercentage, double totalCharges, double finalCost) {
        this.month = month;
        this.unitsUsed = unitsUsed;
        this.rebatePercentage = rebatePercentage;
        this.totalCharges = totalCharges;
        this.finalCost = finalCost;
    }
}