package com.utkarsh.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.utkarsh.expensetracker.models.Category;
import com.utkarsh.expensetracker.models.CategorySummary;
import com.utkarsh.expensetracker.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "expense_tracker.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String TABLE_CATEGORIES = "categories";

    // Common column names
    private static final String KEY_ID = "id";

    // Transactions table columns
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_CATEGORY_ID = "category_id";
    private static final String KEY_DATE = "date";
    private static final String KEY_NOTE = "note";
    private static final String KEY_TYPE = "type"; // "income" or "expense"

    // Categories table columns
    private static final String KEY_CATEGORY_NAME = "name";
    private static final String KEY_CATEGORY_COLOR = "color";

    // Create tables SQL
    private static final String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_AMOUNT + " REAL NOT NULL,"
            + KEY_CATEGORY_ID + " INTEGER,"
            + KEY_DATE + " TEXT NOT NULL,"
            + KEY_NOTE + " TEXT,"
            + KEY_TYPE + " TEXT NOT NULL,"
            + "FOREIGN KEY(" + KEY_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + KEY_ID + "))";

    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CATEGORY_NAME + " TEXT NOT NULL,"
            + KEY_CATEGORY_COLOR + " TEXT NOT NULL)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_TRANSACTIONS);
        insertDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    private void insertDefaultCategories(SQLiteDatabase db) {
        String[] expenseCategories = {"Food", "Transport", "Shopping", "Entertainment", "Bills", "Healthcare"};
        String[] incomeCategories = {"Salary", "Gift", "Bonus", "Investment"};

        for (String category : expenseCategories) {
            ContentValues values = new ContentValues();
            values.put(KEY_CATEGORY_NAME, category);
            values.put(KEY_CATEGORY_COLOR, getRandomColor());
            db.insert(TABLE_CATEGORIES, null, values);
        }

        for (String category : incomeCategories) {
            ContentValues values = new ContentValues();
            values.put(KEY_CATEGORY_NAME, category);
            values.put(KEY_CATEGORY_COLOR, getRandomColor());
            db.insert(TABLE_CATEGORIES, null, values);
        }
    }

    private String getRandomColor() {
        String[] colors = {"#FF5733", "#33FF57", "#3357FF", "#F3FF33", "#FF33F3", "#33FFF3"};
        return colors[(int) (Math.random() * colors.length)];
    }

    // Add transaction
    public long addTransaction(double amount, long categoryId, String date, String note, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AMOUNT, amount);
        values.put(KEY_CATEGORY_ID, categoryId);
        values.put(KEY_DATE, date);
        values.put(KEY_NOTE, note);
        values.put(KEY_TYPE, type);
        return db.insert(TABLE_TRANSACTIONS, null, values);
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TRANSACTIONS + " ORDER BY " + KEY_DATE + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                transaction.setAmount(cursor.getDouble(cursor.getColumnIndex(KEY_AMOUNT)));
                transaction.setCategoryId(cursor.getLong(cursor.getColumnIndex(KEY_CATEGORY_ID)));
                transaction.setDate(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                transaction.setNote(cursor.getString(cursor.getColumnIndex(KEY_NOTE)));
                transaction.setType(cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transactions;
    }

    // Get all categories
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_NAME)));
                category.setColor(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_COLOR)));
                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    // Get summary data for charts
    public List<CategorySummary> getCategorySummary(String type) {
        List<CategorySummary> summary = new ArrayList<>();
        String selectQuery = "SELECT c." + KEY_ID + ", c." + KEY_CATEGORY_NAME + ", c." + KEY_CATEGORY_COLOR +
                ", SUM(t." + KEY_AMOUNT + ") as total FROM " + TABLE_TRANSACTIONS + " t " +
                "JOIN " + TABLE_CATEGORIES + " c ON t." + KEY_CATEGORY_ID + " = c." + KEY_ID +
                " WHERE t." + KEY_TYPE + " = ? GROUP BY c." + KEY_ID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{type});

        if (cursor.moveToFirst()) {
            do {
                CategorySummary item = new CategorySummary();
                item.setCategoryId(cursor.getInt(0));
                item.setCategoryName(cursor.getString(1));
                item.setCategoryColor(cursor.getString(2));
                item.setTotalAmount(cursor.getDouble(3));
                summary.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return summary;
    }
}