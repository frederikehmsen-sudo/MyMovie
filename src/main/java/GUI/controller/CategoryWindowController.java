package GUI.controller;

import DAL.db.MovieDAO_DB;
import GUI.model.MovieModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class CategoryWindowController {

    private MovieModel model;
    private MovieDAO_DB dao;

    @FXML private ListView lwAllCategories;
    @FXML private TextField txtCategoryInput;

    public void setModel(MovieModel model) {
        this.model = model;

        try {
            this.dao = new MovieDAO_DB();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fill tables with observable data from model
        lwAllCategories.setItems(model.getObservableCategories());
    }

    @FXML
    private void onClickAddCategory(ActionEvent actionEvent) {

    }

    @FXML
    private void onClickRemoveCategory(ActionEvent actionEvent) {

    }
}
