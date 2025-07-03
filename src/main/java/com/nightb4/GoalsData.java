package com.nightb4;
import java.util.ArrayList;
import java.util.List;

public class GoalsData {
    // Today’s goals
    public List<GoalData> dayGoals = new ArrayList<>();
    public List<GoalData> morningGoals = new ArrayList<>();
    public List<GoalData> afternoonGoals = new ArrayList<>();
    public List<GoalData> eveningGoals = new ArrayList<>();
    
    // Tomorrow’s goals
    public List<GoalData> tomorrowDayGoals = new ArrayList<>();
    public List<GoalData> tomorrowMorningGoals = new ArrayList<>();
    public List<GoalData> tomorrowAfternoonGoals = new ArrayList<>();
    public List<GoalData> tomorrowEveningGoals = new ArrayList<>();
}