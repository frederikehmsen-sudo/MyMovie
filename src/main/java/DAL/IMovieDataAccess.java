package DAL;

import BE.Movie;

import java.util.List;

public interface IMovieDataAccess {
    Movie createMovie(Movie newMovie) throws Exception;
    List<Movie> getAllMovies() throws Exception;
    void updateMovie(Movie movie) throws Exception;
    void deleteMovie(Movie movie) throws Exception;
}
