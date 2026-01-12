package BE;

public class Category {

    private int id;
    private String name;

    public Category(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }

    /** [Notes for difficult code]
     * Allows HashMap to match categories by id instead of memory address.
     * Categories loaded at different times are treated as different objects without this.
     * (Basically makes sure our checkbox selection won't break and gets the categories on update movie)
     * @param o   the reference object with which to compare.
     * @return
     */
    @Override
    public boolean equals(Object o) {
        // Check if comparing to itself
        if (this == o) return true;

        // Check if the other object is null or a different class
        if (o == null || getClass() != o.getClass()) return false;

        // Cast to Category and compare IDs
        Category category = (Category) o;
        return id == category.id;
    }

    /**
     * We use this to generate a hash code based on category id.
     * This allows our HashMap to store and retrieve categories on movies.
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
