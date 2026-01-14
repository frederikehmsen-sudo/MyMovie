package DAL.db;

import BE.Category;
import BE.Movie;
import DAL.ICategoryDataAccess;
import DAL.ICategoryOnMovieDataAccess;
import DAL.IMovieDataAccess;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO_DB implements ICategoryDataAccess, IMovieDataAccess, ICategoryOnMovieDataAccess {

    private DBConnector databaseConnector = new DBConnector();

    public MovieDAO_DB() throws Exception {
    }

    @Override
    public Category createCategory(Category newCategory) throws Exception {
        String sql = "INSERT INTO dbo.Category (name) VALUES (?);";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, newCategory.getName());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    newCategory.setId(rs.getInt(1));
                }
            }
            return newCategory;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Could not create category", ex);
        }
    }

    @Override
    public List<Category> getAllCategories() throws Exception {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Category ORDER BY name";
        try (Connection conn = databaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
            return categories;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Could not get all categories", ex);
        }
    }

    @Override
    public void deleteCategory(int id) throws Exception {
        String sql = "DELETE FROM dbo.Category WHERE id = ?;";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Could not delete category from database.",ex);
        }
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
                Movie movie = new Movie(rs.getInt("id"), rs.getString("title"), rs.getFloat("imdbRating"),
                        rs.getString("fileLink"), rs.getDate("lastView").toLocalDate(), rs.getFloat("personalRating"),
                        rs.getString("director"), rs.getFloat("time"), rs.getInt("year"));
                // Load categories for a movie (all movies)
                movie.setCategories(getCategoriesForMovie(movie.getId()));
                movies.add(movie);
            }
            return movies;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Could not get movies", ex);
        }
    }

    @Override
    public void updateMovie(Movie movie) throws Exception {
        String sql = "UPDATE dbo.Movie SET title = ?, imdbRating = ?, fileLink = ?, lastView = ?, personalRating = ?, director = ?, time = ?, year = ? WHERE id = ?;";

        try (Connection conn = databaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, movie.getTitle());
            stmt.setFloat(2, movie.getImdbRating());
            stmt.setString(3, movie.getFileLink());
            stmt.setDate(4, java.sql.Date.valueOf(movie.getLastView()));
            stmt.setFloat(5, movie.getPersonalRating());
            stmt.setString(6, movie.getDirector());
            stmt.setFloat(7, movie.getTime());
            stmt.setInt(8, movie.getYear());
            stmt.setInt(9, movie.getId());

            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Could not update movie",ex);
        }
    }

    public void deleteMovie(int id) throws Exception {
        String sql = "DELETE FROM dbo.Movie WHERE id = ?;";

        removeAllCategoriesFromMovie(id);

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
     * Assigns a category to a movie in the junction table
     */
    public void addCategoryToMovie(int movieId, int categoryId) throws Exception {
        String sql = "INSERT INTO dbo.CatMovie (movieId, categoryId) VALUES (?, ?);";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Could not add category to movie", ex);
        }
    }

    /**
     * Gets all categories assigned to a specific movie
     */
    public List<Category> getCategoriesForMovie(int movieId) throws Exception {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.id, c.name FROM Category c " +
                "INNER JOIN CatMovie mc ON c.id = mc.categoryId " +
                "WHERE mc.movieId = ? ORDER BY c.name";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
            return categories;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Could not get categories for movie", ex);
        }
    }

    /**
     * Removes all categories from a movie
     */
    public void removeAllCategoriesFromMovie(int movieId) throws Exception {
        String sql = "DELETE FROM dbo.CatMovie WHERE movieId = ?;";

        try (Connection conn = databaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, movieId);
            stmt.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Could not remove categories from movie", ex);
        }
    }
}
