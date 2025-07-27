package com.utkarsh.expensetracker.fragments;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.utkarsh.expensetracker.models.CategorySummary;
import com.utkarsh.expensetracker.R;
import com.utkarsh.expensetracker.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class SummaryFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private PieChart expenseChart, incomeChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        dbHelper = new DatabaseHelper(getActivity());
        expenseChart = view.findViewById(R.id.expenseChart);
        incomeChart = view.findViewById(R.id.incomeChart);

        setupCharts();

        return view;
    }

    private void setupCharts() {
        // Expense chart
        List<CategorySummary> expenseSummary = dbHelper.getCategorySummary("expense");
        List<PieEntry> expenseEntries = new ArrayList<>();

        for (CategorySummary item : expenseSummary) {
            expenseEntries.add(new PieEntry((float) item.getTotalAmount(), item.getCategoryName()));
        }

        PieDataSet expenseDataSet = new PieDataSet(expenseEntries, "Expenses");
        expenseDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        expenseDataSet.setValueTextColor(Color.WHITE);
        expenseDataSet.setValueTextSize(12f);

        PieData expenseData = new PieData(expenseDataSet);
        expenseChart.setData(expenseData);
        expenseChart.getDescription().setEnabled(false);
        expenseChart.setCenterText("Expenses");
        expenseChart.animateY(1000);
        expenseChart.invalidate();

        // Income chart
        List<CategorySummary> incomeSummary = dbHelper.getCategorySummary("income");
        List<PieEntry> incomeEntries = new ArrayList<>();

        for (CategorySummary item : incomeSummary) {
            incomeEntries.add(new PieEntry((float) item.getTotalAmount(), item.getCategoryName()));
        }

        PieDataSet incomeDataSet = new PieDataSet(incomeEntries, "Income");
        incomeDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        incomeDataSet.setValueTextColor(Color.WHITE);
        incomeDataSet.setValueTextSize(12f);

        PieData incomeData = new PieData(incomeDataSet);
        incomeChart.setData(incomeData);
        incomeChart.getDescription().setEnabled(false);
        incomeChart.setCenterText("Income");
        incomeChart.animateY(1000);
        incomeChart.invalidate();
    }
}