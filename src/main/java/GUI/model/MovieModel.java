package GUI.model;

import BE.Category;
import BE.Movie;
import BLL.MovieManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class MovieModel {
    private MovieManager movieManager; // Reference to business logic manager
    private ObservableList<Movie> movies; // Observable list of all movies
    private ObservableList<Category> categories; // Observable list of all categories
    //private FilteredList<Movie> filteredList; // Filtered view of movies for search/filtering

    /**
     * Constructor initializes the model and loads all movies and categories from the manager.
     * @throws Exception if data retrieval from MovieManager fails.
     */
    public MovieModel() throws Exception {
        movieManager = new MovieManager();

        // Load all movies from manager
        movies = FXCollections.observableArrayList();
        movies.addAll(movieManager.getAllMovies());
        //filteredList = new FilteredList<>(movies);

        // Load all categories from manager
        categories = FXCollections.observableArrayList();
        categories.addAll(movieManager.getAllCategories());
    }

    // MOVIES
    /**
     * Creates a new movie and adds it to the observable list.
     * @return The newly created Movie object.
     * @throws Exception if creation in MovieManager fails.
     */
    public Movie createMovie(Movie newMovie) throws Exception {
        Movie movieCreated = movieManager.createMovie(newMovie);
        movies.add(movieCreated); // Add to observable list for UI
        return movieCreated;
    }

    /**
     * Searches movies based on a query and updates the observable list.
     * @throws Exception if search in MovieManager fails.
     */
    /*public void searchMovies(String query) throws Exception {
        List<Movie> searchResults = movieManager.searchMovies(query);
        movies.clear();
        movies.addAll(searchResults); // Update observable list
    }*/

    /**
     * Returns the observable list of movies for UI binding.
     * @return ObservableList<Movie>
     */
    public ObservableList<Movie> getObservableMovies() {
        return movies;
    }

    /**
     * Updates an existing movie in both the business layer and observable list.
     * @throws Exception if update in MovieManager fails.
     */
    public void updateMovie(Movie updatedMovie) throws Exception {
        movieManager.updateMovie(updatedMovie); // Update in business layer

        // Update in observable list for UI refresh
        int index = movies.indexOf(updatedMovie);
        if (index != -1) {
            movies.set(index, updatedMovie);
        }
    }

    /**
     * Deletes a movie from both the business layer and observable list.
     * @throws Exception if deletion in MovieManager fails.
     */
    public void deleteMovie(Movie selectedMovie) throws Exception {
        movieManager.deleteMovie(selectedMovie); // Delete in business layer
        movies.remove(selectedMovie); // Remove from observable list for UI
    }

    // CATEGORIES
    /**
     * Creates a new category and adds it to the observable list.
     * @return The newly created Category object.
     * @throws Exception if creation fails in MovieManager.
     */
    public Category createCategory(Category newCategory) throws Exception {
        System.out.println("Trying to create category: " + newCategory.getName());
        Category categoryCreated = movieManager.createCategory(newCategory);
        if (categoryCreated == null) {
            System.out.println("MovieManager returned null!");
            throw new Exception("Failed to create category in DB");
        }
        categories.add(categoryCreated); // Add to observable list for UI
        return categoryCreated;
    }

    /**
     * Returns the observable list of categories for UI binding.24
     * @return ObservableList<Category>
     */
    public ObservableList<Category> getObservableCategories() {
        return categories;
    }

    /**
     * Deletes a category from both the business layer and observable list.
     * @throws Exception if deletion in MovieManager fails.
     */
    public void deleteCategory(Category selectedCategory) throws Exception {
        movieManager.deleteCategory(selectedCategory); // Delete in business layer
        categories.remove(selectedCategory); // Remove from observable list for UI
    }

    // CATEGORY ON MOVIES
    public void addCategoryToMovie(Movie movie, Category category) throws Exception {
        movieManager.addCategoryToMovie(movie, category);
    }

    public void removeCategoryFromMovie(Movie movie, Category category) throws Exception {
        movieManager.removeCategoryFromMovie(movie, category);
    }

    public List<Category> getCategoriesForMovie(Movie movie) throws Exception {
        return movieManager.getCategoriesForMovie(movie);
    }

    public void setCategoriesForMovie(Movie movie, List<Category> categories) throws Exception {
        movieManager.setCategoriesForMovie(movie, categories);
    }
}
