package BLL;

import BE.Category;
import BE.Movie;
import DAL.ICategoryDataAccess;
import DAL.ICategoryOnMovieDataAccess;
import DAL.IMovieDataAccess;
import DAL.db.MovieDAO_DB;

import java.util.List;

/**
 * The manager class for Movies.
 * Handles CRUD operations for movies and categories,
 * and provides search functionality for movies.
 */

public class MovieManager {

    private IMovieDataAccess movieDataAccess;
    private ICategoryDataAccess categoryDataAccess;
    private ICategoryOnMovieDataAccess categoryOnMovieDataAccess;

    public MovieManager() throws Exception {
        movieDataAccess = new MovieDAO_DB();
        categoryDataAccess = new MovieDAO_DB();
        categoryOnMovieDataAccess = new MovieDAO_DB();
    }

    // Movies
    public List<Movie> getAllMovies() throws Exception {
        return movieDataAccess.getAllMovies();
    }

    public Movie createMovie(Movie newMovie) throws Exception {
        return movieDataAccess.createMovie(newMovie);
    }

    public void updateMovie(Movie updatedMovie) throws Exception {
        movieDataAccess.updateMovie(updatedMovie);
    }

    public void deleteMovie(Movie selectedMovie) throws Exception {
        movieDataAccess.deleteMovie(selectedMovie.getId());
    }

    // Categories
    public List<Category> getAllCategories() throws Exception {
        return categoryDataAccess.getAllCategories();
    }

    public Category createCategory(Category newCategory) throws Exception {
        return categoryDataAccess.createCategory(newCategory);
    }

    public void deleteCategory(Category selectedCategory) throws Exception {
        categoryDataAccess.deleteCategory(selectedCategory.getId());
    }

    // Categories on Movies

    public List<Category> getCategoriesForMovie(Movie movie) throws Exception {
        return categoryOnMovieDataAccess.getCategoriesForMovie(movie.getId());
    }

    public void setCategoriesForMovie(Movie movie, List<Category> categories) throws Exception {
        // Remove all existing categories first
        categoryOnMovieDataAccess.removeAllCategoriesFromMovie(movie.getId());

        // Add the new categories
        for (Category category : categories) {
            categoryOnMovieDataAccess.addCategoryToMovie(movie.getId(), category.getId());
        }
    }
}
