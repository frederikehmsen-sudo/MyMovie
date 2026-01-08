package GUI.controller;

import BE.Movie;
import GUI.model.MovieModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
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

    private MovieModel model;

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
    public void setModel(MovieModel model) {
        this.model = model;
    }

    @FXML
    private void onClickClear(ActionEvent actionEvent) {
    }

    @FXML
    private void onClickAddMovie(ActionEvent actionEvent) {
        // 1️⃣ Read text input FIRST
        String title = txtTitleInput.getText().trim();
        String director = txtDirectorInput.getText().trim();
        String yearText = txtYearInput.getText().trim();
        String timeText = txtTimeInput.getText().trim();
        String fileLink = lblFileSelected.getText().trim();

        // 2️⃣ Check for empty fields
        if (title.isEmpty() || director.isEmpty() || yearText.isEmpty() || timeText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Fields");
            alert.setHeaderText("Please fill in all required fields");
            alert.showAndWait();
            return;
        }

        // 3️⃣ Parse numbers safely
        int year;
        float time;

        try {
            year = Integer.parseInt(yearText);
            time = Float.parseFloat(timeText);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Year must be an integer and Time must be a number");
            alert.showAndWait();
            return;
        }

        // 4️⃣ Validate numeric values
        if (year <= 0 || time <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Values");
            alert.setHeaderText("Year and time must be positive numbers");
            alert.showAndWait();
            return;
        }

        // 5️⃣ Get spinner values
        float imdbRating = ((Double) spinnerIMDBRating.getValue()).floatValue();
        float personalRating = ((Double) spinnerPersonalRating.getValue()).floatValue();
        LocalDate lastView = LocalDate.now();

        try {
            Movie newMovie = new Movie(
                    0,
                    title,
                    imdbRating,
                    fileLink,
                    lastView,
                    personalRating,
                    director,
                    time,
                    year
            );

            model.createMovie(newMovie);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Movie Added");
            alert.setHeaderText("Movie successfully added!");
            alert.setContentText(newMovie.getTitle() + " by " + newMovie.getDirector());
            alert.showAndWait();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Saving Movie");
            alert.setHeaderText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void onClickUpdateMovie(ActionEvent actionEvent) {
    }

    @FXML
    private void onClickChooseFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose MP4 File");

        File initialDir = new File("data/MP4_Files");
        if (initialDir.exists() && initialDir.isDirectory()) {
            fileChooser.setInitialDirectory(initialDir); // Start in MP4 folder
        }

        File file = fileChooser.showOpenDialog(lblFileSelected.getScene().getWindow()); // Open dialog

        if (file != null && file.getName().toLowerCase().endsWith(".mp")) {
            Path dataFolder = Paths.get("data").toAbsolutePath();
            Path selectedPath = file.toPath().toAbsolutePath();
            Path relativePath = dataFolder.relativize(selectedPath);

            lblFileSelected.setText(relativePath.toString()); // Store relative path
        } else if (file != null) {
            // Invalid file selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid File");
            alert.setHeaderText("Only MP4 files are allowed");
            alert.setContentText("Please choose a file ending with .mp4");
            alert.showAndWait();
        }
    }

    @FXML
    private void onClickRemoveMovie(ActionEvent actionEvent) {
    }

    @FXML
    private void onClickClearText(ActionEvent actionEvent) {
    }
}
