package com.example.moneytor;


import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Fragment80_20 extends Fragment {

//    int selectedElement = FetchData.selectedElement;
    ProgressBar pb20;
    ProgressBar pb80;
    TextView inTV;
    TextView outTV;
    TextView savingsTV;
    TextView otherTV;
    TextView monthTV;


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

        pb20 = (ProgressBar) view.findViewById(R.id.progress_bar_investment);
        pb80 = (ProgressBar) view.findViewById(R.id.progress_bar_other);
        inTV = (TextView) view.findViewById(R.id.in);
        outTV = (TextView) view.findViewById(R.id.out);
        savingsTV = (TextView) view.findViewById(R.id.investmentTV);
        otherTV = (TextView) view.findViewById(R.id.otherTV);
        monthTV = (TextView) view.findViewById(R.id.month);

        String moneyIn = "In: £"+amountToPound(Double.toString(FetchData.moneyIn));

        monthTV.setText(getMonth());
        inTV.setText(moneyIn);
        outTV.setText("Out: £"+amountToPound(Double.toString(moneyOut)));

//        System.out.println("Spent 80: " + spent80);
//        System.out.println("Fetch Money in: " + FetchData.moneyIn);
//        System.out.println("Fetch Money in * 0.2 : " + (FetchData.moneyIn*0.2));

        percentage80 = (int) Math.round((spent80/(FetchData.moneyIn*0.8))*100);
        percentage20 = (int) Math.round((FetchData.moneyIn - spent80)/(FetchData.moneyIn*0.2)*100);
        System.out.println("percentage 20: " + percentage20);
        System.out.println("percentage 80: " + percentage80);
//        System.out.println("remaining 80: " + remaining80);
//        System.out.println("remaining 20: " + remaining20);

        pb20.setTooltipText("Remaining: " + amountToPoundWithMinus(Double.toString(remaining20)));
        pb20.setProgress(percentage20);

        pb80.setTooltipText("Remaining: " + amountToPoundWithMinus(Double.toString(remaining80)));
        pb80.setProgress(percentage80);
        return view;
    }

    private String amountToPound(String amount) {
        DecimalFormat df = new DecimalFormat("0.00");
        Double amountL = Double.parseDouble(amount)/100;
        if(amount.charAt(0)=='-') {
            return "" + df.format(amountL).substring(1);
        }
        return "" + df.format(amountL);
    }

    private void addAmounts(){
        List<Transaction> transactionsThisMonth =  FetchData.transactionsThisMonthFD;

        for(int i=0;i<transactionsThisMonth.size();i++) {
            if(transactionsThisMonth.get(i).getAmount()<0.0) {
                Double amountInPounds = Double.parseDouble(amountToPound(Double.toString(transactionsThisMonth.get(i).getAmount())));
                if(transactionsThisMonth.get(i).getCategory().equals("Finances")){
                    spent20+=(amountInPounds*100);
                } else {
                    spent80+=(amountInPounds*100);
                }
            }
        }

        moneyOut = (spent20 + spent80);
    }

    private String getMonth(){
        String[] monthName = {"January", "February",
                "March", "April", "May", "June", "July",
                "August", "September", "October", "November",
                "December"};

        Calendar cal = Calendar.getInstance();
        String month = monthName[cal.get(Calendar.MONTH)];

        System.out.println("Month name: " + month);
        return month;
    }

    private void remaining(){
        remaining20 = (FetchData.moneyIn*0.2) - (FetchData.moneyIn - spent80 - spent20);
        remaining80 = (FetchData.moneyIn*0.8) - spent80;
        System.out.println("Remaining 20: " + remaining20);
        System.out.println("Remaining 80: " + remaining80);
    }

    private String amountToPoundWithMinus(String amount) {
        DecimalFormat df = new DecimalFormat("0.00");
        Double amountL = Double.parseDouble(amount)/100;
        if(amount.charAt(0)=='-') {
//            System.out.println("Amount to pound method: "+ df.format(amountL).substring(1));
            return "-£" + df.format(amountL).substring(1);
        }
//        System.out.println("Amount to pound method: "+ df.format(amountL));
        return "£" + df.format(amountL);
    }


}
