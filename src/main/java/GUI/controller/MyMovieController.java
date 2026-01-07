package GUI.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;

import java.net.URL;
import java.util.ResourceBundle;

public class MyMovieController {

    @FXML private Spinner spinnerIMDBSearch;
    @FXML private TextField txtFieldSearchBar;
    @FXML private Spinner spinnerPersonalRating;
    @FXML private Spinner spinnerIMDBRating;

    @FXML private Label lblFileSelected;
    @FXML private TextField txtYearInput;
    @FXML private TextField txtTimeInput;
    @FXML private TextField txtDirectorInput;
    @FXML private TextField txtTitleInput;

    @FXML private TableView tblMovie;
    @FXML private TableColumn colTime;
    @FXML private TableColumn colLastViewed;
    @FXML private TableColumn colDirector;
    @FXML private TableColumn colCategories;
    @FXML private TableColumn colPersonalRating;
    @FXML private TableColumn colImdbRating;
    @FXML private TableColumn colYear;
    @FXML private TableColumn colTitle;

    @FXML private ListView lwSearchCategories;
    @FXML private ListView<String> lwCategories;

    public void initialize() {
        String[] categories = {"Action", "Adventure", "Comedy", "Drama", "Fantasy", "Horror",
                "Musical", "Mystery", "Romance", "Science Fiction", "Sports", "Thriller", "Western"};

        lwCategories.getItems().addAll(categories);
        lwCategories.setCellFactory(CheckBoxListCell.forListView(item -> {
            BooleanProperty selected = new SimpleBooleanProperty();
            selected.addListener((obs, oldV, newV) ->
                    System.out.println("Check box search for " + item +
                            " changed from " + oldV + " to " + newV));
            return selected;
        }));

        lwSearchCategories.getItems().addAll(categories);
        lwSearchCategories.setCellFactory(CheckBoxListCell.forListView(item -> {
            BooleanProperty selected = new SimpleBooleanProperty();
            selected.addListener((obs, oldV, newV) ->
                    System.out.println("Check box for " + item +
                            " changed from " + oldV + " to " + newV));
            return selected;
        }));
    }

    @FXML
    private void onClickClear(ActionEvent actionEvent) {
    }

    @FXML
    private void onClickAddMovie(ActionEvent actionEvent) {
    }

    @FXML
    private void onClickUpdateMovie(ActionEvent actionEvent) {
    }

    @FXML
    private void onClickChooseFile(ActionEvent actionEvent) {
    }

}
