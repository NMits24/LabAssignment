package com.example.labassignment_naim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.labassignment_naim.data.AppDatabase;
import com.example.labassignment_naim.data.BillDao;
import com.example.labassignment_naim.data.BillEntry;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Spinner monthSpinner;
    private EditText kwhEditText;
    private EditText rebateEditText;
    private Button calculateButton;
    private TextView totalChargesTextView;
    private TextView finalCostTextView;
    private Button viewHistoryButton;
    private Button aboutButton;

    private AppDatabase db;
    private BillDao billDao;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        monthSpinner = findViewById(R.id.monthSpinner);
        kwhEditText = findViewById(R.id.kwhEditText);
        rebateEditText = findViewById(R.id.rebateEditText);
        calculateButton = findViewById(R.id.calculateButton);
        totalChargesTextView = findViewById(R.id.totalChargesTextView);
        finalCostTextView = findViewById(R.id.finalCostTextView);
        viewHistoryButton = findViewById(R.id.viewHistoryButton);
        aboutButton = findViewById(R.id.aboutButton);

        // Setup Spinner for months
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);

        // Initialize Room Database and Executor Service
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "bill_database").build();
        billDao = db.billDao();
        executorService = Executors.newSingleThreadExecutor();

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateElectricityBill();
            }
        });

        viewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BillHistoryActivity.class);
                startActivity(intent);
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    private void calculateElectricityBill() {
        String kwhString = kwhEditText.getText().toString();
        String rebateString = rebateEditText.getText().toString();

        if (kwhString.isEmpty()) {
            kwhEditText.setError("Electricity units are required.");
            Toast.makeText(this, "Please enter electricity units.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rebateString.isEmpty()) {
            rebateEditText.setError("Rebate percentage is required.");
            Toast.makeText(this, "Please enter rebate percentage.", Toast.LENGTH_SHORT).show();
            return;
        }

        double kwh;
        double rebatePercentage;
        try {
            kwh = Double.parseDouble(kwhString);
            rebatePercentage = Double.parseDouble(rebateString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for units and rebate.", Toast.LENGTH_LONG).show();
            return;
        }

        if (kwh <= 0) {
            kwhEditText.setError("Units used must be greater than 0.");
            Toast.makeText(this, "Units used must be positive.", Toast.LENGTH_LONG).show();
            return;
        }

        if (rebatePercentage < 0 || rebatePercentage > 5) {
            rebateEditText.setError("Rebate must be between 0% and 5%.");
            Toast.makeText(this, "Rebate must be between 0% and 5%.", Toast.LENGTH_LONG).show();
            return;
        }

        // --- Electricity Bill Calculation Logic ---
        double totalCharges = 0;
        double remainingKwh = kwh;

        // Block 1: 1-200 kWh at 21.8 sen/kWh
        if (remainingKwh > 0) {
            double unitsInBlock = Math.min(remainingKwh, 200);
            totalCharges += unitsInBlock * 0.218;
            remainingKwh -= unitsInBlock;
        }

        // Block 2: 201-300 kWh at 33.4 sen/kWh
        if (remainingKwh > 0) {
            double unitsInBlock = Math.min(remainingKwh, 100);
            totalCharges += unitsInBlock * 0.334;
            remainingKwh -= unitsInBlock;
        }

        // Block 3: 301-600 kWh at 51.6 sen/kWh
        if (remainingKwh > 0) {
            double unitsInBlock = Math.min(remainingKwh, 300);
            totalCharges += unitsInBlock * 0.516;
            remainingKwh -= unitsInBlock;
        }

        // Block 4: 601-900 kWh onwards at 54.6 sen/kWh
        if (remainingKwh > 0) {
            totalCharges += remainingKwh * 0.546;
        }

        // --- Final Cost after Rebate Calculation ---
        double finalCost = totalCharges - (totalCharges * (rebatePercentage / 100.0));

        DecimalFormat df = new DecimalFormat("0.00");

        // Display outputs
        totalChargesTextView.setText(String.format("Total Charges: RM %s", df.format(totalCharges)));
        finalCostTextView.setText(String.format("Final Cost: RM %s", df.format(finalCost)));

        // Get selected month
        String selectedMonth = monthSpinner.getSelectedItem().toString();

        // Save to Database
        final double finalKwh = kwh;
        final double finalRebate = rebatePercentage;
        final double finalTotalCharges = totalCharges;
        final double finalCalculatedCost = finalCost;
        final String finalMonth = selectedMonth;

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                BillEntry billEntry = new BillEntry(
                        finalMonth,
                        finalKwh,
                        finalRebate,
                        finalTotalCharges,
                        finalCalculatedCost
                );
                billDao.insertBill(billEntry);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Bill for " + finalMonth + " saved successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}