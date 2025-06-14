package com.example.labassignment_naim;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class BillDetailActivity extends AppCompatActivity {

    private TextView detailMonthTextView;
    private TextView detailUnitsTextView;
    private TextView detailRebateTextView;
    private TextView detailTotalChargesTextView;
    private TextView detailFinalCostTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);

        // Initialize TextViews
        detailMonthTextView = findViewById(R.id.detailMonthTextView);
        detailUnitsTextView = findViewById(R.id.detailUnitsTextView);
        detailRebateTextView = findViewById(R.id.detailRebateTextView);
        detailTotalChargesTextView = findViewById(R.id.detailTotalChargesTextView);
        detailFinalCostTextView = findViewById(R.id.detailFinalCostTextView);

        // Get data from the Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String month = extras.getString("month");
            double unitsUsed = extras.getDouble("unitsUsed");
            double rebatePercentage = extras.getDouble("rebatePercentage");
            double totalCharges = extras.getDouble("totalCharges");
            double finalCost = extras.getDouble("finalCost");

            DecimalFormat df = new DecimalFormat("0.00");

            // Display data
            detailMonthTextView.setText(String.format("Month: %s", month));
            detailUnitsTextView.setText(String.format("Units Used: %s kWh", df.format(unitsUsed)));
            detailRebateTextView.setText(String.format("Rebate: %s%%", df.format(rebatePercentage)));
            detailTotalChargesTextView.setText(String.format("Total Charges: RM %s", df.format(totalCharges)));
            detailFinalCostTextView.setText(String.format("Final Cost: RM %s", df.format(finalCost)));
        } else {
            detailMonthTextView.setText("No bill details available.");
        }
    }
}