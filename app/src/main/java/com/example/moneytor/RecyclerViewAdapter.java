package com.example.moneytor;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mAmount = new ArrayList<>();
    private ArrayList<String> mCategory = new ArrayList<>();
    private ArrayList<String> mDate = new ArrayList<>();
    private ArrayList<String> mDescription = new ArrayList<>();
    private ArrayList<String> mNotes = new ArrayList<>();
    private ArrayList<Boolean> mIsPositive = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> mAmount, ArrayList<String> mCategory, ArrayList<String> mDate, ArrayList<String> mDescription, ArrayList<String> mNotes, ArrayList<Boolean> mIsPositive) {
        this.mAmount = mAmount;
        this.mCategory = mCategory;
        this.mDate = mDate;
        this.mDescription = mDescription;
        this.mNotes = mNotes;
        this.mIsPositive = mIsPositive;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.amount.setText(mAmount.get(position));
        holder.category.setText(mCategory.get(position));
        holder.date.setText(mDate.get(position));
        holder.description.setText(mDescription.get(position));
        holder.notes.setText(mNotes.get(position));

//        if(mIsPositive.get(position)) {
//            holder.amount.setTextColor(Color.BLUE);
//        } else {
//            holder.amount.setTextColor(Color.RED);
//        }

    }

    @Override
    public int getItemCount() {
        return mAmount.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView amount, category, date, description, notes;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.amountLLI);
            category = itemView.findViewById(R.id.categoryLLI);
            date = itemView.findViewById(R.id.dateLLI);
            description = itemView.findViewById(R.id.descriptionLLI);
            notes = itemView.findViewById(R.id.notesLLI);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }


}
