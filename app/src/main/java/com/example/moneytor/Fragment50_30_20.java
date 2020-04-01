package com.example.moneytor;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Fragment50_30_20 extends Fragment {

    private int score;
    private TextView month;
    private TextView inTV;
    private TextView outTV;
    private TextView bills;
    private TextView recreation;
    private TextView savings;
    private ProgressBar billsPB;
    private ProgressBar recreationPB;
    private ProgressBar savingsPB;

    private double spentBills;
    private double spentRecreation;
    private double spentSavings;
    private int percentageBills;
    private int percentageRecreation;
    private int percentageSavings;
    private double moneyOut;
    private double remainingBills;
    private double remainingRecreation;
    private double remainingSavings;
    private DatabaseReference current_user_db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment50_30_20, container, false);

        addAmounts();
        remaining();
        percentage();

        month = view.findViewById(R.id.month50);
        inTV = view.findViewById(R.id.in50);
        outTV = view.findViewById(R.id.out50);
        bills = view.findViewById(R.id.billsTV);
        recreation = view.findViewById(R.id.recreationTV);
        savings = view.findViewById(R.id.savingsTV);
        billsPB = view.findViewById(R.id.progress_bar_bills);
        recreationPB = view.findViewById(R.id.progress_bar_recreation);
        savingsPB = view.findViewById(R.id.progress_bar_savings);

        String moneyIn = "In: £" + amountToPound(Double.toString(FetchData.moneyIn));
        String out = "Out: £" + amountToPound(Double.toString(moneyOut));

        inTV.setText(moneyIn);
        outTV.setText(out);

        billsPB.setProgress(percentageBills);
        billsPB.setTooltipText("Remaining " + amountToPoundWithMinus(Double.toString(remainingBills)));

        recreationPB.setProgress(percentageRecreation);
        recreationPB.setTooltipText("Remaining " + amountToPoundWithMinus(Double.toString(remainingRecreation)));

        savingsPB.setProgress(percentageSavings);
        savingsPB.setTooltipText("Remaining " + amountToPoundWithMinus(Double.toString(remainingSavings)));

        month.setText(getMonth());

        getScore();
        setScore();

        return view;
    }

    private String amountToPound(String amount) {
        DecimalFormat df = new DecimalFormat("0.00");
        Double amountL = Double.parseDouble(amount) / 100;
        if (amount.charAt(0) == '-') {
            return "" + df.format(amountL).substring(1);
        }
        return "" + df.format(amountL);
    }

    private void addAmounts() {
        List<Transaction> transactionsThisMonth = FetchData.transactionsThisMonthFD;
        List<String> billsList = new ArrayList<>(Arrays.asList("Bills", "Expenses", "Groceries", "Transport"));
        List<String> recreationList = new ArrayList<>(Arrays.asList("Charity", "Eating out", "Entertainment", "Family", "General", "Holidays", "Personal care", "Shopping", "Cash"));
//        List<String> savingsList = new ArrayList<>(Arrays.asList("Finances"));


        for (int i = 0; i < transactionsThisMonth.size(); i++) {
            if (transactionsThisMonth.get(i).getAmount() < 0.0) {
                Double amountInPounds = transactionsThisMonth.get(i).getAmount();
                if (billsList.contains(transactionsThisMonth.get(i).getCategory())) {
                    spentBills += amountInPounds;
                } else if (recreationList.contains(transactionsThisMonth.get(i).getCategory())) {
                    spentRecreation += amountInPounds;
                } else {
//                } else if (savingsList.contains(transactionsThisMonth.1get(i).getCategory())) {
                    spentSavings += amountInPounds;
                }
            }
        }

        moneyOut = spentBills + spentRecreation + spentSavings;
    }

    private String getMonth() {
        String[] monthName = {"January", "February",
                "March", "April", "May", "June", "July",
                "August", "September", "October", "November",
                "December"};

        Calendar cal = Calendar.getInstance();
        String month = monthName[cal.get(Calendar.MONTH)];

        System.out.println("Month name: " + month);
        return month;
    }

    private void remaining() {
        remainingBills = (FetchData.moneyIn * 0.5) - Math.abs(spentBills);
        remainingRecreation = (FetchData.moneyIn * 0.3) - Math.abs(spentRecreation);
        remainingSavings = (FetchData.moneyIn * 0.2) - (FetchData.moneyIn - Math.abs(spentRecreation + spentBills + spentSavings));
    }

    private void percentage() {
        percentageBills = (int) Math.round((Math.abs(spentBills) / (FetchData.moneyIn * 0.5)) * 100);
        percentageRecreation = (int) Math.round((Math.abs(spentRecreation) / (FetchData.moneyIn * 0.3)) * 100);
        percentageSavings = (int) Math.round(((FetchData.moneyIn - Math.abs(spentBills + spentRecreation)) / (FetchData.moneyIn * 0.2)) * 100);
    }

    private String amountToPoundWithMinus(String amount) {
        DecimalFormat df = new DecimalFormat("0.00");
        Double amountL = Double.parseDouble(amount) / 100;
        if (amount.charAt(0) == '-') {
            return "-£" + df.format(amountL).substring(1);
        }
        return "£" + df.format(amountL);
    }

    private void getScore() {
        double billsScore = ((FetchData.moneyIn * 0.5) - Math.abs(spentBills)) / (FetchData.moneyIn * 0.5) * 100;
        double recScore = ((FetchData.moneyIn * 0.3) - Math.abs(spentRecreation)) / (FetchData.moneyIn * 0.3) * 100;
        double savingsScore = ((FetchData.moneyIn - Math.abs(spentBills + spentRecreation)) - (FetchData.moneyIn * 0.2)) / (FetchData.moneyIn * 0.2) * 100;
        System.out.println("Money in: " + FetchData.moneyIn);
        System.out.println("bills: " + spentBills + ":" + billsScore);
        System.out.println("recs: " + spentRecreation + ":" + recScore);
        System.out.println("savings: " + (FetchData.moneyIn - Math.abs(spentBills + spentRecreation)) + ":" + savingsScore);
        score = (int) Math.round(billsScore * 1.5 + recScore + savingsScore * 2);
    }

    private void setScore() {
        current_user_db = FirebaseDatabase.getInstance().getReference().child("Leaderboard");
        FetchData.entry.put(FetchData.fullName, score);
        System.out.println("size of: " + FetchData.entry.size());
        current_user_db.setValue(FetchData.entry);
    }

}
