package DAL;

import BE.Category;

import java.util.List;

public interface ICategoryOnMovieDataAccess {
    void addCategoryToMovie(int id, int id1) throws Exception;
    void removeAllCategoriesFromMovie(int id) throws Exception;
    List<Category> getCategoriesForMovie(int id) throws Exception;
}
