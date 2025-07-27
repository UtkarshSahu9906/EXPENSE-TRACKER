package com.utkarsh.expensetracker.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import com.google.android.material.tabs.TabLayout;
import com.utkarsh.expensetracker.database.DatabaseHelper;
import com.utkarsh.expensetracker.R;
import com.utkarsh.expensetracker.fragments.SummaryFragment;
import com.utkarsh.expensetracker.fragments.TransactionsFragment;
import com.utkarsh.expensetracker.adapters.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        Button btnAddTransaction = findViewById(R.id.btnAddTransaction);
        btnAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddTransactionActivity.class));
            }
        });

        // Setup ViewPager and TabLayout
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SummaryFragment(), "Summary");
        adapter.addFragment(new TransactionsFragment(), "Transactions");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}