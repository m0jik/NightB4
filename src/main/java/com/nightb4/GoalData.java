package com.nightb4;
public class GoalData {
    public String text;
    public boolean selected;

    // Default constructor is needed for Gson.
    public GoalData() {}

    // Properties for the individual goals
    public GoalData(String text, boolean selected) {
        this.text = text;
        this.selected = selected;
    }
}
