package com.utkarsh.expensetracker.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;

import com.utkarsh.expensetracker.R;
import com.utkarsh.expensetracker.adapters.TransactionAdapter;
import com.utkarsh.expensetracker.database.DatabaseHelper;
import com.utkarsh.expensetracker.models.Transaction;

import java.util.List;

public class TransactionsFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        dbHelper = new DatabaseHelper(getActivity());
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadTransactions();

        return view;
    }

    private void loadTransactions() {
        List<Transaction> transactions = dbHelper.getAllTransactions();
        adapter = new TransactionAdapter(getActivity(), transactions);
        recyclerView.setAdapter(adapter);
    }
}