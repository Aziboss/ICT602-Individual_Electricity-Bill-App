package com.example.a1_electricitybill;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class HistoryActivity extends AppCompatActivity {

    private DataHelper dbHelper;
    private ListView listView;
    private Cursor cursor;
    private ArrayAdapter<String> adapter;
    private String[] displayData;
    private DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = new DataHelper(this);
        listView = findViewById(R.id.listViewHistory);

        refreshList();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            cursor.moveToPosition(position);
            final int recordId = cursor.getInt(cursor.getColumnIndex(DataHelper.COL_ID));
            final String month = cursor.getString(cursor.getColumnIndex(DataHelper.COL_MONTH));
            final double finalCost = cursor.getDouble(cursor.getColumnIndex(DataHelper.COL_FINAL));

            final CharSequence[] options = {"View Details", "Update", "Delete"};
            AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
            builder.setTitle(month + " - RM " + df.format(finalCost));
            builder.setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0: // View
                        openDetail(recordId, "view");
                        break;
                    case 1: // Update
                        openDetail(recordId, "update");
                        break;
                    case 2: // Delete
                        confirmDelete(recordId);
                        break;
                }
            });
            builder.create().show();
        });
    }

    private void refreshList() {
        cursor = dbHelper.getAllBills();
        if (cursor.getCount() == 0) {
            displayData = new String[]{"No records yet"};
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayData);
            listView.setAdapter(adapter);
            return;
        }

        displayData = new String[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String month = cursor.getString(cursor.getColumnIndex(DataHelper.COL_MONTH));
            double finalCost = cursor.getDouble(cursor.getColumnIndex(DataHelper.COL_FINAL));
            displayData[i] = month + " - RM " + df.format(finalCost);
            cursor.moveToNext();
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayData);
        listView.setAdapter(adapter);
        cursor.moveToFirst(); // reset for click listener
    }

    private void openDetail(int id, String mode) {
        android.content.Intent intent = new android.content.Intent(this, DetailActivity.class);
        intent.putExtra("bill_id", id);
        intent.putExtra("mode", mode);
        startActivity(intent);
    }

    private void confirmDelete(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete this bill?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteBill(id);
                    refreshList();
                    Toast.makeText(HistoryActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }
}