package GUI.controller;

import BE.Category;
import BE.Movie;
import BLL.util.MyMovieSearcher;
import DAL.db.MovieDAO_DB;
import GUI.model.MovieModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainWindowController {

    @FXML private TableView tblMovie;
    @FXML private TableColumn colTime;
    @FXML private TableColumn colLastViewed;
    @FXML private TableColumn colDirector;
    @FXML private TableColumn colCategories;
    @FXML private TableColumn colPersonalRating;
    @FXML private TableColumn colImdbRating;
    @FXML private TableColumn colYear;
    @FXML private TableColumn colTitle;

    @FXML private ListView lwCategoryFilter;
    @FXML private Spinner spinnerIMDBSearch;
    @FXML private TextField txtFieldSearchBar;

    private MovieModel model;
    private MovieDAO_DB dao;
    private MyMovieSearcher searcher;

    public void initialize() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colImdbRating.setCellValueFactory(new PropertyValueFactory<>("imdbRating"));
        colPersonalRating.setCellValueFactory(new PropertyValueFactory<>("personalRating"));
        colCategories.setCellValueFactory(new PropertyValueFactory<>("category"));
        colDirector.setCellValueFactory(new PropertyValueFactory<>("director"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colLastViewed.setCellValueFactory(new PropertyValueFactory<>("lastView"));

        titleDoubleClick();

        // Spinner initialized
        SpinnerValueFactory<Double> imdbSearchValueFactory
                = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 0.0, 0.1);
        spinnerIMDBSearch.setValueFactory(imdbSearchValueFactory);

        lwCategoryFilter.setCellFactory(CheckBoxListCell.forListView(item -> {
            BooleanProperty selected = new SimpleBooleanProperty();
            selected.addListener((obs, oldV, newV) ->
                    System.out.println("Check box for " + item +
                            " changed from " + oldV + " to " + newV));
            return selected;
        }));
    }

    public void setModel(MovieModel model) {
        this.model = model;
        this.searcher = new MyMovieSearcher();

        try {
            this.dao = new MovieDAO_DB();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fill tables with observable data from model
        tblMovie.setItems(model.getObservableMovies());
        lwCategoryFilter.setItems(model.getObservableCategories());
    }

    /**
     * Opens window (MovieWindow.fxml) to create a new movie.
     */
    @FXML
    private void onClickAddMovie(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MovieWindow.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();

        stage.setTitle("New Movie");
        stage.setScene(scene);

        // Pass model to the new controller
        MovieWindowController controller = loader.getController();
        controller.setModel(model);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    /**
     * Opens window (MovieWindow.fxml) to edit the selected movie.
     */
    @FXML
    private void onClickUpdateMovie(ActionEvent actionEvent) throws IOException {
        Movie selectedMovie = (Movie) tblMovie.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MovieWindow.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();

        stage.setTitle("Edit Movie");
        stage.setScene(scene);

        // Pass model to the new controller
        MovieWindowController controller = loader.getController();
        controller.setModel(model);
        controller.setEditingMovie(selectedMovie);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    /**
     * Deletes the selected movie.
     */
    @FXML
    private void onClickDeleteMovie(ActionEvent actionEvent) {
        Movie selectedMovie = (Movie) tblMovie.getSelectionModel().getSelectedItem();

        if (selectedMovie != null) {
            try {
                model.deleteMovie(selectedMovie);
            } catch (Throwable t) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Deleting Movie");
                alert.setHeaderText(t.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void onClickClearSearch(ActionEvent actionEvent) {
        txtFieldSearchBar.clear();
        spinnerIMDBSearch.getValueFactory().setValue(0.0);
        // TODO: Unselect selected categories
    }

    /**
     * Opens window (CategoryWindow.fxml) to create a new category.
     */
    @FXML
    private void onClickAddCategory(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CategoryWindow.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();

        stage.setTitle("New Category");
        stage.setScene(scene);

        // Pass model to the new controller
        CategoryWindowController controller = loader.getController();
        controller.setModel(model);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    /**
     * Deletes the selected category.
     */
    @FXML
    private void onClickRemoveCategory(ActionEvent actionEvent) {
        Category selectedCategory = (Category) lwCategoryFilter.getSelectionModel().getSelectedItem();

        if (selectedCategory != null) {
            try {
                model.deleteCategory(selectedCategory);
            } catch (Throwable t) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Deleting Category");
                alert.setHeaderText(t.getMessage());
                alert.showAndWait();
            }
        }
    }

    // MEDIA PLAYER
    private void openInMediaPlayer(String fileLink){
        try{
            File file = new File(fileLink);

            if (!file.exists()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error finding file");
                alert.showAndWait();
                return;
            }
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Unable to open the media player");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void titleDoubleClick() {
        tblMovie.setRowFactory(tv -> {
            TableRow<Movie> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Movie movie = row.getItem();
                    openInMediaPlayer(movie.getFileLink());
                }
            });

            return row;
        });
    }
}
