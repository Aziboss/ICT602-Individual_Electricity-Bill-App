package com.example.a1_electricitybill;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class DetailActivity extends AppCompatActivity {

    private DataHelper dbHelper;
    private Spinner spinnerMonth;
    private EditText editUnits, editRebate;
    private TextView txtTotal, txtFinal, titleDetail;
    private Button btnAction, btnBack;
    private int billId;
    private String mode; // "view" or "update"
    private String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    private DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbHelper = new DataHelper(this);
        billId = getIntent().getIntExtra("bill_id", -1);
        mode = getIntent().getStringExtra("mode");

        if (billId == -1) {
            finish();
            return;
        }

        // Initialize views
        spinnerMonth = findViewById(R.id.spinnerMonthDetail);
        editUnits = findViewById(R.id.editUnitsDetail);
        editRebate = findViewById(R.id.editRebateDetail);
        txtTotal = findViewById(R.id.txtTotalDetail);
        txtFinal = findViewById(R.id.txtFinalDetail);
        btnAction = findViewById(R.id.btnActionDetail);
        btnBack = findViewById(R.id.btnBackDetail);
        titleDetail = findViewById(R.id.titleDetail);

        // Setup Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        loadData();

        if (mode.equals("view")) {
            titleDetail.setText("+--------------View Bill--------------+");
            btnAction.setText("Edit");
            setEditable(false);
            btnAction.setOnClickListener(v -> switchToUpdateMode());
        } else {
            titleDetail.setText("+--------------Update Bill--------------+");
            btnAction.setText("Update");
            setEditable(true);
            btnAction.setOnClickListener(v -> updateRecord());
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadData() {
        Cursor c = dbHelper.getBillById(billId);
        if (c.moveToFirst()) {
            String month = c.getString(c.getColumnIndex(DataHelper.COL_MONTH));
            int units = c.getInt(c.getColumnIndex(DataHelper.COL_UNITS));
            int rebate = c.getInt(c.getColumnIndex(DataHelper.COL_REBATE));
            double total = c.getDouble(c.getColumnIndex(DataHelper.COL_TOTAL));
            double finalCost = c.getDouble(c.getColumnIndex(DataHelper.COL_FINAL));

            // Set spinner selection
            int pos = 0;
            for (int i = 0; i < months.length; i++) {
                if (months[i].equals(month)) {
                    pos = i;
                    break;
                }
            }
            spinnerMonth.setSelection(pos);
            editUnits.setText(String.valueOf(units));
            editRebate.setText(String.valueOf(rebate));
            txtTotal.setText("RM " + df.format(total));
            txtFinal.setText("RM " + df.format(finalCost));
        }
        c.close();
    }

    private void setEditable(boolean editable) {
        spinnerMonth.setEnabled(editable);
        editUnits.setEnabled(editable);
        editRebate.setEnabled(editable);
        // Total and final are always read‑only
    }

    private void switchToUpdateMode() {
        mode = "update";
        titleDetail.setText("Update Bill");
        btnAction.setText("Update");
        setEditable(true);
        btnAction.setOnClickListener(v -> updateRecord());
    }

    private void updateRecord() {
        // Validation
        String unitsStr = editUnits.getText().toString().trim();
        String rebateStr = editRebate.getText().toString().trim();
        if (unitsStr.isEmpty() || rebateStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        int units, rebate;
        try {
            units = Integer.parseInt(unitsStr);
            rebate = Integer.parseInt(rebateStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (units < 1 || units > 1000) {
            editUnits.setError("Units 1-1000");
            return;
        }
        if (rebate < 0 || rebate > 5) {
            editRebate.setError("Rebate 0-5%");
            return;
        }

        String month = months[spinnerMonth.getSelectedItemPosition()];
        double total = BillCalculator.calculateTotalCharges(units);
        double finalCost = BillCalculator.finalCost(total, rebate);

        dbHelper.updateBill(billId, month, units, total, rebate, finalCost);
        Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}