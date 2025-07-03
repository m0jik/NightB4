package com.nightb4;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class SecondaryController {

    // Tomorrow's Day Section Controls
    @FXML private ListView<Goal> dayListView;
    @FXML private TextField dayTextField;

    // Tomorrow's Morning Section Controls
    @FXML private ListView<Goal> morningListView;
    @FXML private TextField morningTextField;

    // Tomorrow's Afternoon Section Controls
    @FXML private ListView<Goal> afternoonListView;
    @FXML private TextField afternoonTextField;

    // Tomorrow's Evening Section Controls
    @FXML private ListView<Goal> eveningListView;
    @FXML private TextField eveningTextField;

    // ObservableLists for tomorrow's goals (just text, no checkboxes)
    public static final ObservableList<Goal> dayGoals =
            FXCollections.observableArrayList(goal -> new javafx.beans.Observable[]{ goal.textProperty() });
    public static final ObservableList<Goal> morningGoals =
            FXCollections.observableArrayList(goal -> new javafx.beans.Observable[]{ goal.textProperty() });
    public static final ObservableList<Goal> afternoonGoals =
            FXCollections.observableArrayList(goal -> new javafx.beans.Observable[]{ goal.textProperty() });
    public static final ObservableList<Goal> eveningGoals =
            FXCollections.observableArrayList(goal -> new javafx.beans.Observable[]{ goal.textProperty() });

    @FXML
    private void initialize() {
        // Clear lists to avoid duplicates on reinitialization.
        dayGoals.clear();
        morningGoals.clear();
        afternoonGoals.clear();
        eveningGoals.clear();

        // Bind each ListView to its ObservableList. No custom cell factory → plain text only.
        dayListView.setItems(dayGoals);
        morningListView.setItems(morningGoals);
        afternoonListView.setItems(afternoonGoals);
        eveningListView.setItems(eveningGoals);

        // Load persisted “tomorrow” data from JSON
        GoalsData data = GoalsStorage.loadGoals();
        if (data != null) {
            for (GoalData gd : data.tomorrowDayGoals) {
                dayGoals.add(new Goal(gd.text, gd.selected));
            }
            for (GoalData gd : data.tomorrowMorningGoals) {
                morningGoals.add(new Goal(gd.text, gd.selected));
            }
            for (GoalData gd : data.tomorrowAfternoonGoals) {
                afternoonGoals.add(new Goal(gd.text, gd.selected));
            }
            for (GoalData gd : data.tomorrowEveningGoals) {
                eveningGoals.add(new Goal(gd.text, gd.selected));
            }
        }
    }

    // ----------------------------
    // Add/Delete Handlers with Immediate Persistence for Tomorrow's Goals
    // ----------------------------
    @FXML
    private void handleAddDay(ActionEvent event) {
        String text = dayTextField.getText();
        if (text != null && !text.trim().isEmpty()) {
            dayGoals.add(new Goal(text));
            dayTextField.clear();
            persistTomorrowSections();
        }
    }

    @FXML
    private void handleDeleteDay(ActionEvent event) {
        Goal selected = dayListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dayGoals.remove(selected);
            persistTomorrowSections();
        }
    }

    @FXML
    private void handleAddMorning(ActionEvent event) {
        String text = morningTextField.getText();
        if (text != null && !text.trim().isEmpty()) {
            morningGoals.add(new Goal(text));
            morningTextField.clear();
            persistTomorrowSections();
        }
    }

    @FXML
    private void handleDeleteMorning(ActionEvent event) {
        Goal selected = morningListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            morningGoals.remove(selected);
            persistTomorrowSections();
        }
    }

    @FXML
    private void handleAddAfternoon(ActionEvent event) {
        String text = afternoonTextField.getText();
        if (text != null && !text.trim().isEmpty()) {
            afternoonGoals.add(new Goal(text));
            afternoonTextField.clear();
            persistTomorrowSections();
        }
    }

    @FXML
    private void handleDeleteAfternoon(ActionEvent event) {
        Goal selected = afternoonListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            afternoonGoals.remove(selected);
            persistTomorrowSections();
        }
    }

    @FXML
    private void handleAddEvening(ActionEvent event) {
        String text = eveningTextField.getText();
        if (text != null && !text.trim().isEmpty()) {
            eveningGoals.add(new Goal(text));
            eveningTextField.clear();
            persistTomorrowSections();
        }
    }

    @FXML
    private void handleDeleteEvening(ActionEvent event) {
        Goal selected = eveningListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            eveningGoals.remove(selected);
            persistTomorrowSections();
        }
    }

    // ----------------------------
    // Immediate Persistence for Tomorrow's Sections
    //    MADE PUBLIC STATIC so App.onClose can call it.
    // ----------------------------
    public static void persistTomorrowSections() {
        GoalsData data = GoalsStorage.loadGoals();
        if (data == null) {
            data = new GoalsData();
        }
        // Replace the “tomorrow” lists in the data model
        data.tomorrowDayGoals.clear();
        data.tomorrowDayGoals.addAll(convertGoalsStatic(dayGoals));

        data.tomorrowMorningGoals.clear();
        data.tomorrowMorningGoals.addAll(convertGoalsStatic(morningGoals));

        data.tomorrowAfternoonGoals.clear();
        data.tomorrowAfternoonGoals.addAll(convertGoalsStatic(afternoonGoals));

        data.tomorrowEveningGoals.clear();
        data.tomorrowEveningGoals.addAll(convertGoalsStatic(eveningGoals));

        GoalsStorage.saveGoals(data);
    }

    // Static helper for persistTomorrowSections
    private static List<GoalData> convertGoalsStatic(ObservableList<Goal> goals) {
        List<GoalData> list = new ArrayList<>();
        for (Goal g : goals) {
            list.add(new GoalData(g.getText(), g.isSelected()));
        }
        return list;
    }

    // ----------------------------
    // Navigation: Save on switch, then switch to Primary
    // ----------------------------
    @FXML
    private void switchToPrimary(ActionEvent event) {
        try {
            // Persist any outstanding changes for tomorrow's goals before switching.
            persistTomorrowSections();
            App.setRoot("primary");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------------------
    // Inner class representing a Goal.
    // ----------------------------
    public static class Goal {
        private final javafx.beans.property.SimpleStringProperty text;
        private final javafx.beans.property.SimpleBooleanProperty selected;

        public Goal(String t) {
            this(t, false);
        }
        public Goal(String t, boolean selected) {
            this.text = new javafx.beans.property.SimpleStringProperty(t);
            this.selected = new javafx.beans.property.SimpleBooleanProperty(selected);
        }
        public String getText() {
            return text.get();
        }
        public void setText(String t) {
            text.set(t);
        }
        public javafx.beans.property.StringProperty textProperty() {
            return text;
        }
        public boolean isSelected() {
            return selected.get();
        }
        public void setSelected(boolean val) {
            selected.set(val);
        }
        public javafx.beans.property.BooleanProperty selectedProperty() {
            return selected;
        }
        @Override
        public String toString() {
            return getText();
        }
    }
}
