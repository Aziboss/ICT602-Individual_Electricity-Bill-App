package com.example.a1_electricitybill;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private DataHelper dbHelper;
    private Spinner spinnerMonth;
    private EditText editUnits;
    private SeekBar seekRebate;
    private TextView txtRebateValue, txtTotal, txtFinal;
    private Button btnCalculate, btnViewHistory, btnAbout;

    private String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DataHelper(this);

        // Initialize views
        spinnerMonth = findViewById(R.id.spinnerMonth);
        editUnits = findViewById(R.id.editUnits);
        seekRebate = findViewById(R.id.seekRebate);
        txtRebateValue = findViewById(R.id.txtRebateValue);
        txtTotal = findViewById(R.id.txtTotal);
        txtFinal = findViewById(R.id.txtFinal);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        btnAbout = findViewById(R.id.btnAbout);

        // Setup Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        // SeekBar listener
        seekRebate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtRebateValue.setText(progress + "%");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Button listeners
        btnCalculate.setOnClickListener(v -> calculateAndSave());
        btnViewHistory.setOnClickListener(v -> startActivity(new android.content.Intent(MainActivity.this, HistoryActivity.class)));
        btnAbout.setOnClickListener(v -> startActivity(new android.content.Intent(MainActivity.this, AboutActivity.class)));
    }

    private void calculateAndSave() {
        String month = months[spinnerMonth.getSelectedItemPosition()];
        String unitsStr = editUnits.getText().toString().trim();
        int rebate = seekRebate.getProgress();

        // Validation
        if (unitsStr.isEmpty()) {
            editUnits.setError("Please enter units");
            return;
        }
        int units;
        try {
            units = Integer.parseInt(unitsStr);
        } catch (NumberFormatException e) {
            editUnits.setError("Invalid number");
            return;
        }
        if (units < 1 || units > 1000) {
            editUnits.setError("Units must be between 1 and 1000");
            return;
        }

        // Calculate
        double total = BillCalculator.calculateTotalCharges(units);
        double finalCost = BillCalculator.finalCost(total, rebate);

        // Display results
        txtTotal.setText("Total charges: RM " + df.format(total));
        txtFinal.setText("Final cost after " + rebate + "% rebate: RM " + df.format(finalCost));

        // Save to database
        dbHelper.insertBill(month, units, total, rebate, finalCost);
        Toast.makeText(this, "Saved to database", Toast.LENGTH_SHORT).show();

        // Optional: clear input or leave as is
    }
}