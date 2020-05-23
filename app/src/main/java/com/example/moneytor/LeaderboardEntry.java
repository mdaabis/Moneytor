package com.example.moneytor;

public class LeaderboardEntry {

    private String fullName;
    private int score;
    private String rank;

    /**
     * Constructor used to initialise variables
     *
     * @param fullName Name of user
     * @param score    User's score
     */
    public LeaderboardEntry(String fullName, int score) {
        this.fullName = fullName;
        this.score = score;
    }

    /**
     * @return User's full name
     */
    public String getFullName() {
        return fullName;
    }


    /**
     * @return User's score
     */
    public int getScore() {
        return score;
    }


    /**
     * @return User's rank
     */
    public String getRank() {
        return rank;
    }


    /**
     * @param rank Set the user's rank in the leader board
     */
    public void setRank(String rank) {
        this.rank = rank;
    }
}
