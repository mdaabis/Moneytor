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
    ImageView pb;


    private double spent80;
    private double spent20;
    private String month="";

    public Fragment80_20() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment80_20, container, false);


        pb = (ImageView) view.findViewById(R.id.progress_bar);
//        pb.setTooltipText("D rus the g hench");

        addAmounts();
        getMonth();

        return view;
    }

    private String amountToPound(String amount) {
        DecimalFormat df = new DecimalFormat("0.00");
        Double amountL = Double.parseDouble(amount)/100;
        if(amount.charAt(0)=='-') {
            return "-" + df.format(amountL).substring(1);
        }
        return "" + df.format(amountL);
    }

    private void addAmounts(){
        List<Transaction> transactionsThisMonth =  FetchData.transactionsThisMonthFD;

        for(int i=0;i<transactionsThisMonth.size();i++) {
            if(transactionsThisMonth.get(i).getAmount()<0.0) {
                Double amountInPounds = Double.parseDouble(amountToPound(Double.toString(transactionsThisMonth.get(i).getAmount())));
                if(transactionsThisMonth.get(i).getCategory().equals("Finances")){
                    spent20+=amountInPounds;
                } else {
                    spent80+=amountInPounds;
                }
            }
        }
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
}
