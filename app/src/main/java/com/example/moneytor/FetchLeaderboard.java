package com.example.moneytor;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FetchLeaderboard extends AsyncTask<Void, Void, Void> {
    public static List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();


    @Override
    protected Void doInBackground(Void... voids) {
        getData();
        return null;
    }

    /*
     * User scores retrieved from Firebase Realtime Database and stored in a hashmap of leader board entries
     */
    private void getData() {
        String path = "Leaderboard";
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(path);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                leaderboardEntries.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.getKey();
                    int score = Integer.parseInt(snapshot.getValue().toString());
                    LeaderboardEntry entry = new LeaderboardEntry(name, score);
                    leaderboardEntries.add(entry);
                    Collections.sort(leaderboardEntries, new Comparator<LeaderboardEntry>() {
                        public int compare(LeaderboardEntry t1, LeaderboardEntry t2) {
                            return Integer.valueOf(t2.getScore()).compareTo(t1.getScore());
                        }
                    });
                }

                for (int i = 0; i < leaderboardEntries.size(); i++) {
                    int rank = i + 1;
                    leaderboardEntries.get(i).setRank(Integer.toString(rank));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
