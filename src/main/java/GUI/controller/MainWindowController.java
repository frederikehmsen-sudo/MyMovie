package GUI.controller;

import BE.Category;
import BE.Movie;
import DAL.db.MovieDAO_DB;
import GUI.model.MovieModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.beans.property.SimpleObjectProperty;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainWindowController {

    @FXML private TableView<Movie> tblMovie;
    @FXML private TableColumn<Movie, Float> colTime;
    @FXML private TableColumn<Movie, java.time.LocalDate> colLastViewed;
    @FXML private TableColumn<Movie, String> colDirector;
    @FXML private TableColumn<Movie, String> colCategories;
    @FXML private TableColumn<Movie, Float> colPersonalRating;
    @FXML private TableColumn<Movie, Float> colImdbRating;
    @FXML private TableColumn<Movie, Integer> colYear;
    @FXML private TableColumn<Movie, String> colTitle;

    @FXML private ListView<Category> lwCategoryFilter;
    @FXML private Spinner<Double> spinnerIMDBSearch;
    @FXML private TextField txtFieldSearchBar;

    private Map<Category, BooleanProperty> categoryCheckBoxStates = new HashMap<>();
    private FilteredList<Movie> filteredMovies;
    private MovieModel model;
    private MovieDAO_DB dao;

    @FXML
    private Spinner spinnerPersonalSearch;
    private final ObservableList<Category> selectedCategories = FXCollections.observableArrayList();

    public void initialize() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colImdbRating.setCellValueFactory(new PropertyValueFactory<>("imdbRating"));
        colPersonalRating.setCellValueFactory(new PropertyValueFactory<>("personalRating"));
        // Skud ud til tutor Ervin og stackoverflow.
        colCategories.setCellValueFactory((TableColumn.CellDataFeatures<Movie, String> cellData) -> {
            Movie movie = cellData.getValue();
            List<Category> categories = movie.getCategories();

            String categoryNames = categories.stream()
                    .map(Category::getName)
                    .collect(java.util.stream.Collectors.joining(", "));

            return new SimpleObjectProperty<>(categoryNames);
        });
        colDirector.setCellValueFactory(new PropertyValueFactory<>("director"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colLastViewed.setCellValueFactory(new PropertyValueFactory<>("lastView"));

        // Spinner initialized
        SpinnerValueFactory<Double> imdbSearchValueFactory
                = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 0.0, 0.1);
        spinnerIMDBSearch.setValueFactory(imdbSearchValueFactory);

        SpinnerValueFactory<Double> personalSearchValueFactory
                = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 0.0, 0.1);
        spinnerPersonalSearch.setValueFactory(personalSearchValueFactory);

        lwCategoryFilter.setCellFactory(CheckBoxListCell.forListView(category -> {
            // Get or create the boolean property for this category and store it in the map
            BooleanProperty selected = categoryCheckBoxStates.computeIfAbsent(category,
                    c -> new SimpleBooleanProperty(false));

            selected.addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    selectedCategories.add(category);
                } else {
                    selectedCategories.remove(category);
                }
                updateFilters();
            });
            return selected;
        }));

        titleDoubleClick();
        openingReminder();
    }

    public void setModel(MovieModel model) {
        this.model = model;

        try {
            this.dao = new MovieDAO_DB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Create filtered list wrapping the observable list, and then wrap the filtered list in sorted list,
        // so that we can sort between the different properties
        filteredMovies = new FilteredList<>(model.getObservableMovies(), m -> true);
        SortedList<Movie> sortedData = new SortedList<>(filteredMovies);
        sortedData.comparatorProperty().bind(tblMovie.comparatorProperty());
        tblMovie.setItems(sortedData);
        lwCategoryFilter.setItems(model.getObservableCategories());

        //Search filter
        searchMovie();
    }

    private void searchMovie(){
        txtFieldSearchBar.textProperty().addListener((observableValue, oldValue, newValue) ->
                updateFilters());
        spinnerIMDBSearch.valueProperty().addListener((obs, oldVal, newVal) ->
                updateFilters());
        spinnerPersonalSearch.valueProperty().addListener((obs, oldVal, newVal) ->
                updateFilters());
        lwCategoryFilter.editableProperty().addListener((observable, oldValue, newValue) ->
                updateFilters());

        updateFilters();
    }

    private void updateFilters() {
        filteredMovies.setPredicate(movie -> {
            // Text search filter
            String searchText = txtFieldSearchBar.getText();
            if (searchText != null && !searchText.isEmpty()) {
                String lowerCase = searchText.toLowerCase();
                boolean matchesText = movie.getTitle().toLowerCase().contains(lowerCase) ||
                        Integer.toString(movie.getYear()).contains(lowerCase) ||
                        movie.getDirector().toLowerCase().contains(lowerCase);
                if (!matchesText) return false;
            }

            // IMDB rating filter
            Double minRatingImdb = (Double) spinnerIMDBSearch.getValue();
            if (minRatingImdb != null && minRatingImdb > 0.0) {
                if (movie.getImdbRating() < minRatingImdb) return false;
            }
            // Personal rating filter
            Double minRatingPersonal = (Double) spinnerPersonalSearch.getValue();
            if (minRatingPersonal != null && minRatingPersonal > 0.0) {
                if (movie.getPersonalRating() < minRatingPersonal) return false;
            }

            // Categories filter
            if(!selectedCategories.isEmpty()){
                boolean hasSelectedCategory = movie.getCategories().containsAll(selectedCategories);
                if (!hasSelectedCategory) return false;
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
        Movie selectedMovie = tblMovie.getSelectionModel().getSelectedItem();
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
        Movie selectedMovie = tblMovie.getSelectionModel().getSelectedItem();

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
        for (BooleanProperty property : categoryCheckBoxStates.values()) {
            property.set(false);
        }
        selectedCategories.clear();
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
            // Updates lastView to todays date
            Movie selectedMovie = tblMovie.getSelectionModel().getSelectedItem();
                selectedMovie.setLastView(LocalDate.now());
                model.updateMovie(selectedMovie);

        } catch (Exception e) {
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

    private void openingReminder() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reminder To Remove Movies");
        alert.setHeaderText(null);
        alert.setContentText("Remember to delete your movies with a personal rating under 6\n" +
                "and that have not been opened from the application\nin more than 2 years.");
        alert.showAndWait();
    }
}
