package com.utkarsh.expensetracker.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.utkarsh.expensetracker.models.Category;
import com.utkarsh.expensetracker.database.DatabaseHelper;
import com.utkarsh.expensetracker.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private Spinner spinnerCategory, spinnerType;
    private EditText etAmount, etNote;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        dbHelper = new DatabaseHelper(this);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerType = findViewById(R.id.spinnerType);
        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        btnSave = findViewById(R.id.btnSave);

        setupSpinners();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTransaction();
            }
        });
    }

    private void setupSpinners() {
        // Type spinner
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.transaction_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Category spinner
        List<Category> categories = dbHelper.getAllCategories();
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void saveTransaction() {
        String amountStr = etAmount.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (amountStr.isEmpty()) {
            etAmount.setError("Amount is required");
            return;
        }

        double amount = Double.parseDouble(amountStr);
        Category category = (Category) spinnerCategory.getSelectedItem();
        String type = spinnerType.getSelectedItem().toString();

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        long id = dbHelper.addTransaction(amount, category.getId(), currentDate, note, type.toLowerCase());

        if (id != -1) {
            Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save transaction", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}