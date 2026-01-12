package GUI.controller;

import BE.Category;
import BE.Movie;
import BLL.util.MyMovieSearcher;
import DAL.db.MovieDAO_DB;
import GUI.model.MovieModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
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
import java.util.List;

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
    @FXML
    private Spinner spinnerPersonalSearch;

    public MainWindowController() {

        try{
        model = new MovieModel();
    } catch (Exception e){
        displayError(e);
        e.printStackTrace();
    }
}

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
        searchMovie();

        // Spinner initialized
        SpinnerValueFactory<Double> imdbSearchValueFactory
                = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 0.0, 0.1);
        spinnerIMDBSearch.setValueFactory(imdbSearchValueFactory);

        SpinnerValueFactory<Double> personalSearchValueFactory
                = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 0.0, 0.1);
        spinnerPersonalSearch.setValueFactory(personalSearchValueFactory);

        lwCategoryFilter.setCellFactory(CheckBoxListCell.forListView(item -> {
            BooleanProperty selected = new SimpleBooleanProperty();
            selected.addListener((obs, oldV, newV) ->
                    System.out.println("Check box for " + item +
                            " changed from " + oldV + " to " + newV));
            return selected;
        }));
    }

    private void searchMovie(){
        txtFieldSearchBar.textProperty().addListener((observableValue, oldValue, newValue) ->
                updateFilters());

        spinnerIMDBSearch.valueProperty().addListener((obs, oldVal, newVal) ->
                updateFilters());
        spinnerPersonalSearch.valueProperty().addListener((obs, oldVal, newVal) ->
                updateFilters());

        // Add listener for category selection if needed

        updateFilters(); // Initial setup
    }

    private void updateFilters() {
        model.getObservableMovies().setPredicate(movie -> {
            // Text search filter
            String searchText = txtFieldSearchBar.getText();
            if (searchText != null && !searchText.isEmpty()) {
                String lowerCase = searchText.toLowerCase();
                boolean matchesText = movie.getTitle().toLowerCase().contains(lowerCase) ||
                        Integer.toString(movie.getYear()).contains(lowerCase) || movie.getDirector().toLowerCase().contains(lowerCase);
                if (!matchesText) return false;
            }

            // IMDB rating filter
            Double minRatingImdb = (Double) spinnerIMDBSearch.getValue();
            if (minRatingImdb != null && minRatingImdb > 0.0) {
                if (movie.getImdbRating() < minRatingImdb) return false;
            }
            Double minRatingPersonal = (Double) spinnerPersonalSearch.getValue();
            if (minRatingPersonal != null && minRatingPersonal > 0.0) {
                if (movie.getImdbRating() < minRatingPersonal) return false;
            }

            // If all filters pass, show the movie
            return true;
        });
    }

    private void displayError(Throwable t) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }


    public void setModel(MovieModel model) {
        this.model = model;
        //this.searcher = new MyMovieSearcher();

        try {
            this.dao = new MovieDAO_DB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SortedList<Movie> sortedData = new SortedList<>(model.getObservableMovies());
        sortedData.comparatorProperty().bind(tblMovie.comparatorProperty());
        // Fill tables with observable data from model
        tblMovie.setItems(sortedData);
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
        spinnerPersonalSearch.getValueFactory().setValue(0.0);
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
