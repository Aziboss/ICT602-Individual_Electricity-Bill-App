package com.example.a1_electricitybill;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "electricity.db";
    private static final int DATABASE_VERSION = 1;

    // Table and columns
    public static final String TABLE_BILLS = "bills";
    public static final String COL_ID = "id";
    public static final String COL_MONTH = "month";
    public static final String COL_UNITS = "units";
    public static final String COL_TOTAL = "total";
    public static final String COL_REBATE = "rebate";
    public static final String COL_FINAL = "final";

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_BILLS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MONTH + " TEXT, " +
                COL_UNITS + " INTEGER, " +
                COL_TOTAL + " REAL, " +
                COL_REBATE + " INTEGER, " +
                COL_FINAL + " REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILLS);
        onCreate(db);
    }

    // Insert a new record
    public long insertBill(String month, int units, double total, int rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MONTH, month);
        values.put(COL_UNITS, units);
        values.put(COL_TOTAL, total);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL, finalCost);
        return db.insert(TABLE_BILLS, null, values);
    }

    // Get all records (Cursor)
    public Cursor getAllBills() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BILLS + " ORDER BY " + COL_ID + " DESC", null);
    }

    // Get a single record by ID
    public Cursor getBillById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BILLS + " WHERE " + COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Update a record
    public int updateBill(int id, String month, int units, double total, int rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MONTH, month);
        values.put(COL_UNITS, units);
        values.put(COL_TOTAL, total);
        values.put(COL_REBATE, rebate);
        values.put(COL_FINAL, finalCost);
        return db.update(TABLE_BILLS, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Delete a record
    public void deleteBill(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BILLS, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }
}
