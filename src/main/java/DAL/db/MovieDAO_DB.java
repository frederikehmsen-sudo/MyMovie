package DAL.db;

import BE.Category;
import BE.Movie;
import DAL.ICategoryDataAccess;
import DAL.IMovieDataAccess;

import java.util.List;

public class MovieDAO_DB implements ICategoryDataAccess, IMovieDataAccess {

    @Override
    public Category createCategory(Category newCategory) throws Exception {
        return null;
    }

    @Override
    public List<Category> getAllCategories() throws Exception {
        return List.of();
    }

    @Override
    public void deleteCategory(Category category) throws Exception {

    }

    @Override
    public Movie createMovie(Movie newMovie) throws Exception {
        return null;
    }

    @Override
    public List<Movie> getAllMovies() throws Exception {
        return List.of();
    }

    @Override
    public void updateMovie(Movie movie) throws Exception {

    }

    @Override
    public void deleteMovie(Movie movie) throws Exception {

    }
}
