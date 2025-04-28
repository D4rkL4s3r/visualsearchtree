package org.uclouvain.visualsearchtree.tree;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class NQueensInputController {
    @FXML
    private TextField nQueensInput;
    @FXML
    private Button submitButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label errorLabel;

    private Consumer<Integer> callback;

    public void setCallback(Consumer<Integer> callback) {
        this.callback = callback;
    }

    @FXML
    private void submit(ActionEvent event) {
        String input = nQueensInput.getText().trim();
        try {
            int n = Integer.parseInt(input);
            if (n < 1) {
                errorLabel.setText("Please enter a number greater than 0.");
                return;
            }
            callback.accept(n);
            closeStage();
        } catch (NumberFormatException e) {
            errorLabel.setText("Please enter a valid number.");
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) nQueensInput.getScene().getWindow();
        stage.close();
    }
}