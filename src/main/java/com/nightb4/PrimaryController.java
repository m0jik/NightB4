package com.nightb4;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.StringConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrimaryController {

    @FXML
    private Button primaryButton;

    // Today Section Controls
    @FXML private ListView<Goal> dayListView;
    @FXML private TextField dayTextField;

    // Morning Section Controls
    @FXML private ListView<Goal> morningListView;
    @FXML private TextField morningTextField;

    // Afternoon Section Controls
    @FXML private ListView<Goal> afternoonListView;
    @FXML private TextField afternoonTextField;

    // Evening Section Controls
    @FXML private ListView<Goal> eveningListView;
    @FXML private TextField eveningTextField;

    // ObservableLists for today's goals (with checkboxes)
    public static final ObservableList<Goal> dayGoals =
            FXCollections.observableArrayList(goal -> new javafx.beans.Observable[]{ goal.selectedProperty() });
    public static final ObservableList<Goal> morningGoals =
            FXCollections.observableArrayList(goal -> new javafx.beans.Observable[]{ goal.selectedProperty() });
    public static final ObservableList<Goal> afternoonGoals =
            FXCollections.observableArrayList(goal -> new javafx.beans.Observable[]{ goal.selectedProperty() });
    public static final ObservableList<Goal> eveningGoals =
            FXCollections.observableArrayList(goal -> new javafx.beans.Observable[]{ goal.selectedProperty() });

    @FXML
    private void initialize() {
        // Clear lists to avoid duplication on reinitialize
        dayGoals.clear();
        morningGoals.clear();
        afternoonGoals.clear();
        eveningGoals.clear();

        // StringConverter to display Goals as plain text
        StringConverter<Goal> converter = new StringConverter<Goal>() {
            @Override
            public String toString(Goal goal) {
                return goal.getText();
            }
            @Override
            public Goal fromString(String string) {
                return new Goal(string);
            }
        };

        // Set up each ListView with its corresponding ObservableList and checkbox cell factory.
        dayListView.setItems(dayGoals);
        dayListView.setCellFactory(CheckBoxListCell.forListView(g -> g.selectedProperty(), converter));
        attachSelectionListener(dayGoals);

        morningListView.setItems(morningGoals);
        morningListView.setCellFactory(CheckBoxListCell.forListView(g -> g.selectedProperty(), converter));
        attachSelectionListener(morningGoals);

        afternoonListView.setItems(afternoonGoals);
        afternoonListView.setCellFactory(CheckBoxListCell.forListView(g -> g.selectedProperty(), converter));
        attachSelectionListener(afternoonGoals);

        eveningListView.setItems(eveningGoals);
        eveningListView.setCellFactory(CheckBoxListCell.forListView(g -> g.selectedProperty(), converter));
        attachSelectionListener(eveningGoals);

        // Load persisted data.
        GoalsData data = GoalsStorage.loadGoals();
        if (data != null) {
            for (GoalData gd : data.dayGoals) {
                Goal g = new Goal(gd.text, gd.selected);
                dayGoals.add(g);
                // Listener for each added item is handled by attachSelectionListener(dayGoals)
            }
            for (GoalData gd : data.morningGoals) {
                Goal g = new Goal(gd.text, gd.selected);
                morningGoals.add(g);
            }
            for (GoalData gd : data.afternoonGoals) {
                Goal g = new Goal(gd.text, gd.selected);
                afternoonGoals.add(g);
            }
            for (GoalData gd : data.eveningGoals) {
                Goal g = new Goal(gd.text, gd.selected);
                eveningGoals.add(g);
            }
        }
        // All existing items now have listeners attached.
    }

    // ----------------------------
    // Add/Delete Handlers with Immediate Persistence
    // ----------------------------

    @FXML
    private void handleAddDay(ActionEvent event) {
        String text = dayTextField.getText();
        if (text != null && !text.trim().isEmpty()) {
            Goal g = new Goal(text);
            dayGoals.add(g);
            attachSelectionListener(g);
            dayTextField.clear();
            persistAllSections();
        }
    }

    @FXML
    private void handleDeleteDay(ActionEvent event) {
        Goal selected = dayListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dayGoals.remove(selected);
            persistAllSections();
        }
    }

    @FXML
    private void handleAddMorning(ActionEvent event) {
        String text = morningTextField.getText();
        if (text != null && !text.trim().isEmpty()) {
            Goal g = new Goal(text);
            morningGoals.add(g);
            attachSelectionListener(g);
            morningTextField.clear();
            persistAllSections();
        }
    }

    @FXML
    private void handleDeleteMorning(ActionEvent event) {
        Goal selected = morningListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            morningGoals.remove(selected);
            persistAllSections();
        }
    }

    @FXML
    private void handleAddAfternoon(ActionEvent event) {
        String text = afternoonTextField.getText();
        if (text != null && !text.trim().isEmpty()) {
            Goal g = new Goal(text);
            afternoonGoals.add(g);
            attachSelectionListener(g);
            afternoonTextField.clear();
            persistAllSections();
        }
    }

    @FXML
    private void handleDeleteAfternoon(ActionEvent event) {
        Goal selected = afternoonListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            afternoonGoals.remove(selected);
            persistAllSections();
        }
    }

    @FXML
    private void handleAddEvening(ActionEvent event) {
        String text = eveningTextField.getText();
        if (text != null && !text.trim().isEmpty()) {
            Goal g = new Goal(text);
            eveningGoals.add(g);
            attachSelectionListener(g);
            eveningTextField.clear();
            persistAllSections();
        }
    }

    @FXML
    private void handleDeleteEvening(ActionEvent event) {
        Goal selected = eveningListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            eveningGoals.remove(selected);
            persistAllSections();
        }
    }

    // ----------------------------
    // Rollover Handler: Replace today's goals with tomorrow's and clear tomorrow's.
    // ----------------------------
    @FXML
    private void handleDoneWithToday(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Rollover");
        alert.setHeaderText("Rollover Today");
        alert.setContentText("This will replace today's goals in all sections with tomorrow's goals and then clear tomorrow's data. Do you want to proceed?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Move tomorrow’s data into today’s lists
            dayGoals.clear();
            dayGoals.addAll(convertSecondaryGoals(SecondaryController.dayGoals));
            attachSelectionListener(dayGoals);
            SecondaryController.dayGoals.clear();

            morningGoals.clear();
            morningGoals.addAll(convertSecondaryGoals(SecondaryController.morningGoals));
            attachSelectionListener(morningGoals);
            SecondaryController.morningGoals.clear();

            afternoonGoals.clear();
            afternoonGoals.addAll(convertSecondaryGoals(SecondaryController.afternoonGoals));
            attachSelectionListener(afternoonGoals);
            SecondaryController.afternoonGoals.clear();

            eveningGoals.clear();
            eveningGoals.addAll(convertSecondaryGoals(SecondaryController.eveningGoals));
            attachSelectionListener(eveningGoals);
            SecondaryController.eveningGoals.clear();

            GoalsStorage.rolloverGoals();
        }
    }

    // Helper to convert a list of SecondaryController.Goal into a list of PrimaryController.Goal.
    private List<Goal> convertSecondaryGoals(ObservableList<SecondaryController.Goal> secondaryGoals) {
        List<Goal> list = new ArrayList<>();
        for (SecondaryController.Goal sg : secondaryGoals) {
            list.add(new Goal(sg.getText(), sg.isSelected()));
        }
        return list;
    }

    // Switches to tomorrow's goals
    @FXML
    private void switchToSecondary(ActionEvent event) {
        try {
            App.setRoot("secondary");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------------------
    // Helper method: Persist (today’s) current state of all sections.
    //    MADE PUBLIC STATIC so App.onClose can call it.
    // ----------------------------
    public static void persistAllSections() {
        GoalsData data = GoalsStorage.loadGoals();
        if (data == null) {
            data = new GoalsData();
        }
        // Replace today’s lists in the data model
        data.dayGoals.clear();
        data.dayGoals.addAll(convertGoalsStatic(dayGoals));

        data.morningGoals.clear();
        data.morningGoals.addAll(convertGoalsStatic(morningGoals));

        data.afternoonGoals.clear();
        data.afternoonGoals.addAll(convertGoalsStatic(afternoonGoals));

        data.eveningGoals.clear();
        data.eveningGoals.addAll(convertGoalsStatic(eveningGoals));

        // Leave tomorrow’s lists untouched.
        GoalsStorage.saveGoals(data);
    }

    // Static helper for persistAllSections:
    private static List<GoalData> convertGoalsStatic(ObservableList<Goal> goals) {
        List<GoalData> list = new ArrayList<>();
        for (Goal g : goals) {
            list.add(new GoalData(g.getText(), g.isSelected()));
        }
        return list;
    }

    // ----------------------------
    // Listeners for checkbox changes
    // ----------------------------
    // Whenever a checkbox toggles, save “Today” immediately.
    private void attachSelectionListener(ObservableList<Goal> goals) {
        for (Goal g : goals) {
            attachSelectionListener(g);
        }
        // Also listen for any future additions so new items get a listener
        goals.addListener((ListChangeListener<Goal>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Goal g2 : change.getAddedSubList()) {
                        attachSelectionListener(g2);
                    }
                }
            }
        });
    }

    private void attachSelectionListener(Goal g) {
        g.selectedProperty().addListener((obs, wasSelected, nowSelected) -> {
            // As soon as the user checks/unchecks a “Today” goal, save right away:
            persistAllSections();
        });
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
