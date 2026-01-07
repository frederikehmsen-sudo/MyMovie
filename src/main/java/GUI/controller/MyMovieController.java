package GUI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MyMovieController {


    @FXML
    private Spinner spinnerIMDBSearch;
    @FXML
    private TextField txtFieldSearchBar;
    @FXML
    private Spinner spinnerPersonalRating;
    @FXML
    private Spinner spinnerIMDBRating;
    @FXML
    private Label lblFileSelected;
    @FXML
    private TextField txtYearInput;
    @FXML
    private TextField txtTimeInput;
    @FXML
    private TextField txtDirectorInput;
    @FXML
    private TextField txtTitleInput;
    @FXML
    private TableColumn colTime;
    @FXML
    private TableColumn colLastViewed;
    @FXML
    private TableColumn colDirector;
    @FXML
    private TableColumn colCategories;
    @FXML
    private TableColumn colPersonalRating;
    @FXML
    private TableColumn colImdbRating;
    @FXML
    private TableColumn colYear;
    @FXML
    private TableColumn colTitle;
    @FXML
    private TableView tblMovie;

    @FXML
    private void onClickClear(ActionEvent actionEvent) {
    }

    @FXML
    private void onAddMovie(ActionEvent actionEvent) {
    }

    @FXML
    private void onUpdateMovie(ActionEvent actionEvent) {
    }

    @FXML
    private void onChooseFile(ActionEvent actionEvent) {
    }
}
