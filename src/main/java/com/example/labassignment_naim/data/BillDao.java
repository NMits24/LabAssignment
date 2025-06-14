package com.example.labassignment_naim.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BillDao {
    @Insert
    void insertBill(BillEntry bill);

    @Query("SELECT * FROM bills ORDER BY id DESC")
    List<BillEntry> getAllBills();

    @Query("SELECT * FROM bills WHERE id = :billId")
    BillEntry getBillById(int billId);
}