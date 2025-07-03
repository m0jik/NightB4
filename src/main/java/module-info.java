module com.nightb4 {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.nightb4 to javafx.fxml;
    exports com.nightb4;
}
