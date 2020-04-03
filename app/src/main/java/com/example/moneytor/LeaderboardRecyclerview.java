package com.example.moneytor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardRecyclerview extends RecyclerView.Adapter<LeaderboardRecyclerview.ViewHolder> {
    private static final String TAG = "LeaderboardRecyclerview";

    private Context context;
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mScore = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mRank = new ArrayList<>();

    public LeaderboardRecyclerview(Context context, ArrayList<String> mNames, ArrayList<String> mScore, ArrayList<String> mImages, ArrayList<String> mRank) {
        this.context = context;
        this.mNames = mNames;
        this.mScore = mScore;
        this.mImages = mImages;
        this.mRank = mRank;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_layout_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Glide.with(context).asBitmap().load(mImages.get(position)).into(holder.image);
        holder.names.setText(mNames.get(position));
        holder.scores.setText(mScore.get(position));
        holder.ranks.setText(mRank.get(position));
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView names;
        TextView scores;
        TextView ranks;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.circleImage);
            names = itemView.findViewById(R.id.leaderboard_name);
            scores = itemView.findViewById(R.id.score);
            ranks = itemView.findViewById(R.id.rank);
            parentLayout = itemView.findViewById(R.id.leaderboard_layout);
        }
    }


}
