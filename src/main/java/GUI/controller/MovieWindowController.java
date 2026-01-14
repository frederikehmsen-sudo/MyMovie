package GUI.controller;

import BE.Category;
import BE.Movie;
import DAL.db.MovieDAO_DB;
import GUI.model.MovieModel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieWindowController {

    @FXML private Spinner spinnerPersonalRating;
    @FXML private Spinner spinnerIMDBRating;

    @FXML private Label lblFileSelected;
    @FXML private TextField txtYearInput;
    @FXML private TextField txtTimeInput;
    @FXML private TextField txtDirectorInput;
    @FXML private TextField txtTitleInput;
    @FXML private ListView lwAllCategories;

    private MovieModel model;
    private MovieDAO_DB dao;
    private Movie editingMovie = null; // Movie being edited (null if creating new)
    private boolean editMode = false; // Boolean for whether the controller is in edit mode
    private Map<Category, BooleanProperty> categorySelectionMap = new HashMap<>();

    public void initialize() {
        // Spinners initialized
        SpinnerValueFactory<Double> imdbValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 0.0, 0.1);
        spinnerIMDBRating.setValueFactory(imdbValueFactory);

        SpinnerValueFactory<Double> personalValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10.0, 0.0, 0.1);
        spinnerPersonalRating.setValueFactory(personalValueFactory);

        // This creates checkboxes for each category in the ListView
        // computeIfAbsent checks if the category already has a BooleanProperty in the map
        // If not, it creates a new one with default value false (unchecked)
        // This BooleanProperty controls whether the checkbox is checked or not
        lwAllCategories.setCellFactory(CheckBoxListCell.forListView(category -> {
            BooleanProperty selected = categorySelectionMap.computeIfAbsent(
                    (Category) category,
                    C -> new SimpleBooleanProperty(false)
            );
            return selected;
        }));
    }

    public void setModel(MovieModel model) {
        this.model = model;

        try {
            this.dao = new MovieDAO_DB();
        } catch (Exception e) {
            e.printStackTrace();
        }

        lwAllCategories.setItems(model.getObservableCategories());

        // Initialize the selection map with all available categories
        // Each category gets a BooleanProperty set to false (unchecked by default)
        // This map tracks which categories are selected in the ListView
        for (Category category : model.getObservableCategories()) {
            categorySelectionMap.put(category, new SimpleBooleanProperty(false));
        }
    }

    /**
     * Prepares the controller for editing an existing movie.
     * Fills all input fields with the movie's current data.
     */
    public void setEditingMovie(Movie movie) {
        if (movie != null) {
            this.editingMovie = movie;
            this.editMode = true;

            txtTitleInput.setText(movie.getTitle());
            txtDirectorInput.setText(movie.getDirector());
            txtYearInput.setText(String.valueOf(movie.getYear()));
            txtTimeInput.setText(String.valueOf(movie.getTime()));
            lblFileSelected.setText(movie.getFileLink());
            spinnerIMDBRating.getValueFactory().setValue((double) movie.getImdbRating());
            spinnerPersonalRating.getValueFactory().setValue((double) movie.getPersonalRating());
            try {
                List<Category> movieCategories = model.getCategoriesForMovie(movie);
                selectCategories(movieCategories);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves a new movie or updates an existing one.
     * Validates input, updates the model, shows confirmation alerts,
     * and closes the window when finished.
     */
    @FXML
    private void onClickAddMovie(ActionEvent actionEvent) {
        String title = txtTitleInput.getText().trim();
        String director = txtDirectorInput.getText().trim();
        String yearText = txtYearInput.getText().trim();
        String timeText = txtTimeInput.getText().trim();
        String fileLink = lblFileSelected.getText().trim();
        float imdbRating = ((Double) spinnerIMDBRating.getValue()).floatValue();
        float personalRating = ((Double) spinnerPersonalRating.getValue()).floatValue();

        // Validate required fields
        if (title.isEmpty() || director.isEmpty() || yearText.isEmpty() || timeText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Fields");
            alert.setHeaderText("Please fill in all required fields");
            alert.showAndWait();
            return;
        }

        int year;
        float time;

        try {
            year = Integer.parseInt(yearText);
            time = Float.parseFloat(timeText);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Year and Time must be a number");
            alert.showAndWait();
            return;
        }

        if (year <= 0 || time <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Values");
            alert.setHeaderText("Year and time must be positive numbers");
            alert.showAndWait();
            return;
        }

        LocalDate lastView = LocalDate.now();

        try {
            // Update existing movie if we are in edit mode
            if (editMode && editingMovie != null) {
                // Update movie object with new values
                editingMovie.setTitle(title);
                editingMovie.setDirector(director);
                editingMovie.setYear(year);
                editingMovie.setTime(time);
                editingMovie.setFileLink(fileLink);
                editingMovie.setImdbRating(imdbRating);
                editingMovie.setPersonalRating(personalRating);

                model.updateMovie(editingMovie); // Persist update via model

                List<Category> selectedCategories = getSelectedCategories();
                model.setCategoriesForMovie(editingMovie, selectedCategories);

                editingMovie.setCategories(selectedCategories);

                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.close();
            }
            // Create new movie
            else {
                Movie newMovie = new Movie(0, title, imdbRating, fileLink, lastView, personalRating, director, time, year);
                Movie createdMovie = model.createMovie(newMovie); // Save movie through model

                List<Category> selectedCategories = getSelectedCategories();
                model.setCategoriesForMovie(createdMovie, selectedCategories);

                createdMovie.setCategories(selectedCategories);

                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.close();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Saving Movie");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void onClickChooseFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose MP4 or MPEG4 File");

        File initialDir = new File("data/MP4andMPEG4_Files");
        if (initialDir.exists() && initialDir.isDirectory()) {
            fileChooser.setInitialDirectory(initialDir);
        }

        File file = fileChooser.showOpenDialog(lblFileSelected.getScene().getWindow());

        if (file != null) {
            if (file.getName().toLowerCase().endsWith(".mp4") || file.getName().toLowerCase().endsWith(".mpeg4")) {
                Path projectRoot = Paths.get("").toAbsolutePath();
                Path selectedPath = file.toPath().toAbsolutePath();
                Path relativePath = projectRoot.relativize(selectedPath);
                lblFileSelected.setText(relativePath.toString());
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid File");
                alert.setHeaderText("Only MP4 and MPEG4 files are allowed");
                alert.setContentText("Please choose a file ending with .mp4 or .mpeg4");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No File Chosen");
            alert.setHeaderText("A MP4 or MPEG4 of the movie is needed");
            alert.setContentText("Please choose a file ending with .mp4 or .mpeg4");
            alert.showAndWait();
        }
    }

    /**
     * Unselects and selects categories based on the movie's existing categories
     * Doesn't work properly without
     */
    private void selectCategories(List<Category> categoriesToSelect) {
        // Unselect all
        for (BooleanProperty prop : categorySelectionMap.values()) {
            prop.set(false);
        }

        // Select the ones that should be selected
        for (Category category : categoriesToSelect) {
            BooleanProperty prop = categorySelectionMap.get(category);
            if (prop != null) {
                prop.set(true);
            }
        }
        lwAllCategories.refresh();
    }

    /**
     * Gets the selected categories from the map
     */
    private List<Category> getSelectedCategories() {
        List<Category> selectedCategories = new ArrayList<>();

        for (Map.Entry<Category, BooleanProperty> entry : categorySelectionMap.entrySet()) {
            if (entry.getValue().get()) {
                selectedCategories.add(entry.getKey());
            }
        }
        return selectedCategories;
    }
}
