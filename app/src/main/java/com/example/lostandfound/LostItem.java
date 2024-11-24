package com.example.lostandfound;

public class LostItem {
    private int matches;
    private int id;
    private String name;
    private long timeReported;
    private long timeLost;
    private String category;
    private String location;

    public LostItem(int matches, int id, String name, long timeReported, long timeLost, String category, String location) {
        this.matches = matches;
        this.id = id;
        this.name = name;
        this.timeReported = timeReported;
        this.timeLost = timeLost;
        this.category = category;
        this.location = location;
    }

    public int getMatches() {
        return matches;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getTimeReported() {
        return timeReported;
    }

    public long getTimeLost() {
        return timeLost;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }
}
