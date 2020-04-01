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
import java.util.Calendar;
import java.util.List;


public class Fragment80_20 extends Fragment {

    private int score;
    private ProgressBar pb20;
    private ProgressBar pb80;
    private TextView inTV;
    private TextView outTV;
    private TextView savingsTV;
    private TextView otherTV;
    private TextView monthTV;

    private DatabaseReference current_user_db;
    private double spent80;
    private double spent20;
    private int percentage20;
    private int percentage80;
    private double remaining20;
    private double remaining80;
    private double moneyOut;


    public Fragment80_20() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment80_20, container, false);
        addAmounts();
        remaining();

        pb20 = view.findViewById(R.id.progress_bar_investment);
        pb80 = view.findViewById(R.id.progress_bar_other);
        inTV = view.findViewById(R.id.in);
        outTV = view.findViewById(R.id.out);
        savingsTV = view.findViewById(R.id.investmentTV);
        otherTV = view.findViewById(R.id.otherTV);
        monthTV = view.findViewById(R.id.month);

        String moneyIn = "In: £" + amountToPound(Double.toString(FetchData.moneyIn));

        monthTV.setText(getMonth());
        inTV.setText(moneyIn);
        outTV.setText("Out: £" + amountToPound(Double.toString(moneyOut)));

        percentage80 = (int) Math.round((spent80 / (FetchData.moneyIn * 0.8)) * 100);
        percentage20 = (int) Math.round((FetchData.moneyIn - spent80) / (FetchData.moneyIn * 0.2) * 100);

        pb20.setTooltipText("Remaining: " + amountToPoundWithMinus(Double.toString(remaining20)));
        pb20.setProgress(percentage20);

        pb80.setTooltipText("Remaining: " + amountToPoundWithMinus(Double.toString(remaining80)));
        pb80.setProgress(percentage80);

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

        for (int i = 0; i < transactionsThisMonth.size(); i++) {
            if (transactionsThisMonth.get(i).getAmount() < 0.0) {
                Double amountInPounds = Double.parseDouble(amountToPound(Double.toString(transactionsThisMonth.get(i).getAmount())));
                if (transactionsThisMonth.get(i).getCategory().equals("Finances")) {
                    spent20 += (amountInPounds * 100);
                } else {
                    spent80 += (amountInPounds * 100);
                }
            }
        }

        moneyOut = (spent20 + spent80);
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
        remaining20 = (FetchData.moneyIn * 0.2) - (FetchData.moneyIn - spent80 - spent20);
        remaining80 = (FetchData.moneyIn * 0.8) - spent80;
    }

    private String amountToPoundWithMinus(String amount) {
        DecimalFormat df = new DecimalFormat("0.00");
        Double amountL = Double.parseDouble(amount) / 100;
        if (amount.charAt(0) == '-') {
//            System.out.println("Amount to pound method: "+ df.format(amountL).substring(1));
            return "-£" + df.format(amountL).substring(1);
        }
//        System.out.println("Amount to pound method: "+ df.format(amountL));
        return "£" + df.format(amountL);
    }

    private void getScore() {
        double otherScore = ((FetchData.moneyIn * 0.8) - Math.abs(spent80)) / (FetchData.moneyIn * 0.8) * 100;
        double savingsScore = ((FetchData.moneyIn - Math.abs(spent80)) - (FetchData.moneyIn * 0.2)) / (FetchData.moneyIn * 0.2) * 100;
        System.out.println("Money in: " + FetchData.moneyIn);
        System.out.println("80: " + spent80 + ":" + otherScore);
        System.out.println("20: " + (FetchData.moneyIn - Math.abs(spent80)) + ":" + savingsScore);
        score = (int) Math.round(otherScore + savingsScore * 2);
    }

    private void setScore() {
        current_user_db = FirebaseDatabase.getInstance().getReference().child("Leaderboard");
        FetchData.entry.put(FetchData.fullName, score);
        current_user_db.setValue(FetchData.entry);
    }
}
