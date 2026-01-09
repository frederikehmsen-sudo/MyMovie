package DAL.db;

import BE.Category;
import BE.Movie;
import DAL.ICategoryDataAccess;
import DAL.IMovieDataAccess;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO_DB implements ICategoryDataAccess, IMovieDataAccess {

    private DBConnector databaseConnector = new DBConnector();

    public MovieDAO_DB() throws Exception {
    }

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
        String sql = "INSERT INTO dbo.Movie (title, imdbRating, fileLink, lastView, personalRating, director, time, year) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, newMovie.getTitle());
            stmt.setFloat(2, newMovie.getImdbRating());
            stmt.setString(3, newMovie.getFileLink());
            stmt.setDate(4, java.sql.Date.valueOf(newMovie.getLastView()));
            stmt.setFloat(5, newMovie.getPersonalRating());
            stmt.setString(6, newMovie.getDirector());
            stmt.setFloat(7, newMovie.getTime());
            stmt.setInt(8, newMovie.getYear());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    newMovie.setId(rs.getInt(1));
                }
            }
            return newMovie;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Could not create movie", ex);
        }
    }

    @Override
    public List<Movie> getAllMovies() throws Exception {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM Movie ORDER BY title";
        try (Connection conn = databaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                movies.add(createMovieFromResultSet(rs));
            }
            return movies;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Could not get movies", ex);
        }
    }

    @Override
    public void updateMovie(Movie movie) throws Exception {
        String sql = "UPDATE dbo.Movie SET title = ?, imdbRating = ?, fileLink = ?, personalRating = ?, director = ?, time = ?, year = ? WHERE id = ?;";

        try (Connection conn = databaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, movie.getTitle());
            stmt.setFloat(2, movie.getImdbRating());
            stmt.setString(3, movie.getFileLink());
            stmt.setFloat(4, movie.getPersonalRating());
            stmt.setString(5, movie.getDirector());
            stmt.setFloat(6, movie.getTime());
            stmt.setInt(7, movie.getYear());
            stmt.setInt(8, movie.getId());

            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Could not update movie",ex);
        }
    }


    public void deleteMovie(int id) throws Exception {
        String sql = "DELETE FROM dbo.Movie WHERE id = ?;";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Could not delete movie from database.",ex);
        }
    }

    /**
     * Helper method to create Movie object from ResultSet
     */
    private Movie createMovieFromResultSet(ResultSet rs) throws Exception {
        return new Movie(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getFloat("imdbRating"),
                rs.getString("fileLink"),
                rs.getDate("lastView").toLocalDate(),
                rs.getFloat("personalRating"),
                rs.getString("director"),
                rs.getFloat("time"),
                rs.getInt("year")
        );
    };
}
