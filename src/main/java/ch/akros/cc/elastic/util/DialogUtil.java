package ch.akros.cc.elastic.util;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;

import static javafx.scene.control.Alert.AlertType;

/**
 * Created by Patrick on 10.05.2017.
 */
public final class DialogUtil {

    private DialogUtil() {
    }

    public static void showDialog(final Stage owner, final AlertType type, final String content) {
        final Alert alert = createGenericDialog(owner, type, type.toString(), null, content);
        alert.showAndWait();
    }

    public static void showExceptionDialog(final Stage owner, final String description, final Exception ex) {
        final Alert alert = createGenericDialog(owner, AlertType.ERROR, "Error",
                description, ex.getMessage());

        // Create expandable Exception.
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        final String exceptionText = sw.toString();

        final Label label = new Label("The exception stacktrace was:");

        final TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        final GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    private static Alert createGenericDialog(final Stage owner, final AlertType type, final String title,
                                             final String headerText, final String contentText) {
        final Alert alert = new Alert(type);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        return alert;
    }
}
