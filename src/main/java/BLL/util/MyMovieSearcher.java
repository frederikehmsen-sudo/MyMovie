package BLL.util;

import BE.Movie;

import java.util.ArrayList;
import java.util.List;

public class MyMovieSearcher {
    // Main search method: takes a list of movies and a search query, then returns all movies where the title or director   matches the query.
    public List<Movie> search(List<Movie> searchBase, String query) {
        List<Movie> searchResult = new ArrayList<>();

        // Loop through every movie in the provided list
        for (Movie movie : searchBase) {

            // If the query matches either the movie title or the artist name, add the movie to the result list
            if (compareToMovieTitle(query, movie) || compareToMovieDirector(query, movie)) {
                searchResult.add(movie);
            }
        }
        // Return all found matches
        return searchResult;
    }

    // Checks if the query matches part of the director's name (case-insensitive)
    private boolean compareToMovieDirector(String query, Movie movie) {
        return movie.getDirector().toLowerCase().contains(query.toLowerCase());
    }

    // Checks if the query matches part of the movie title (case-insensitive)
    private boolean compareToMovieTitle(String query, Movie movie) {
        return movie.getTitle().toLowerCase().contains(query.toLowerCase());
    }
}
