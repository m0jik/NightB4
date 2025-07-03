package com.nightb4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GoalsStorage {
    private static final String FILE_NAME = "goals.json";

    // Loads the goals upon program initialization
    public static GoalsData loadGoals() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new GoalsData();
        }
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, GoalsData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new GoalsData();
    }

    // Saves goals whenever the "add" button is pressed
    public static void saveGoals(GoalsData data) {
        try (FileWriter writer = new FileWriter(FILE_NAME, false)) { // Overwrite mode
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Rollover: Replace today's goals with tomorrow's goals for every section,
     * then clear tomorrow's goals from the persisted data.
     */
    public static void rolloverGoals() {
        GoalsData data = loadGoals();
        if (data == null) {
            data = new GoalsData();
        }
        // Day section rollover.
        data.dayGoals.clear();
        data.dayGoals.addAll(data.tomorrowDayGoals);
        data.tomorrowDayGoals.clear();
        
        // Morning section rollover.
        data.morningGoals.clear();
        data.morningGoals.addAll(data.tomorrowMorningGoals);
        data.tomorrowMorningGoals.clear();
        
        // Afternoon section rollover.
        data.afternoonGoals.clear();
        data.afternoonGoals.addAll(data.tomorrowAfternoonGoals);
        data.tomorrowAfternoonGoals.clear();
        
        // Evening section rollover.
        data.eveningGoals.clear();
        data.eveningGoals.addAll(data.tomorrowEveningGoals);
        data.tomorrowEveningGoals.clear();
        
        saveGoals(data);
    }
}