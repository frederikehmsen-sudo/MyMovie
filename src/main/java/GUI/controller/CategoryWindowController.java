package GUI.controller;

import BE.Category;
import DAL.db.MovieDAO_DB;
import GUI.model.MovieModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CategoryWindowController {

    private MovieModel model;
    private MovieDAO_DB dao;

    @FXML private TextField txtCategoryInput;

    public void setModel(MovieModel model) {
        this.model = model;

        try {
            this.dao = new MovieDAO_DB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClickAddCategory(ActionEvent actionEvent) {
        String input = txtCategoryInput.getText().trim();
        if (!input.isEmpty()) {
            try {
                Category newCategory = new Category(0, input);
                model.createCategory(newCategory);

                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Saving Category");
                alert.setHeaderText(e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Input");
            alert.setHeaderText("Please fill in the input or exit the window.");
            alert.showAndWait();
        }
    }
}
