package DAL;

import BE.Category;

import java.util.List;

public interface ICategoryDataAccess {
    Category createCategory(Category newCategory) throws Exception;
    List<Category> getAllCategories() throws Exception;
    void deleteCategory(int id) throws Exception;
}
