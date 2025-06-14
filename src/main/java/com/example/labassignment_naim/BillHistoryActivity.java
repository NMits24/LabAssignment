package com.example.labassignment_naim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.labassignment_naim.data.AppDatabase;
import com.example.labassignment_naim.data.BillEntry;
import com.example.labassignment_naim.data.BillDao;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BillHistoryActivity extends AppCompatActivity {

    private ListView billHistoryListView;
    private ArrayAdapter<String> adapter;
    private List<BillEntry> billList;

    private AppDatabase db;
    private BillDao billDao;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_history);

        billHistoryListView = findViewById(R.id.billHistoryListView);

        // Initialize database
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "bill_database").build();
        billDao = db.billDao();
        executorService = Executors.newSingleThreadExecutor();

        billList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        billHistoryListView.setAdapter(adapter);

        loadBillsFromDatabase();

        billHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BillEntry selectedBill = billList.get(position);

                Intent intent = new Intent(BillHistoryActivity.this, BillDetailActivity.class);
                intent.putExtra("month", selectedBill.month);
                intent.putExtra("unitsUsed", selectedBill.unitsUsed);
                intent.putExtra("rebatePercentage", selectedBill.rebatePercentage);
                intent.putExtra("totalCharges", selectedBill.totalCharges);
                intent.putExtra("finalCost", selectedBill.finalCost);
                startActivity(intent);
            }
        });
    }

    private void loadBillsFromDatabase() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<BillEntry> bills = billDao.getAllBills();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        billList.clear();
                        billList.addAll(bills);
                        adapter.clear();
                        DecimalFormat df = new DecimalFormat("0.00");
                        for (BillEntry bill : bills) {
                            adapter.add(bill.month + ": RM " + df.format(bill.finalCost));
                        }
                        adapter.notifyDataSetChanged();
                        if (bills.isEmpty()) {
                            Toast.makeText(BillHistoryActivity.this, "No bills saved yet. Calculate a bill on the main screen!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBillsFromDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}