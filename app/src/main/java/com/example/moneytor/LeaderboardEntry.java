package com.example.moneytor;

public class LeaderboardEntry {

    private String fullName;
    private int score;

    public LeaderboardEntry(String fullName, int score) {
        this.fullName = fullName;
        this.score = score;
    }

    public String getFullName() {
        return fullName;
    }

    public int getScore() {
        return score;
    }
}
