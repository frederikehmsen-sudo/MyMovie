package BE;

import DAL.ICategoryDataAccess;
import javafx.collections.ObservableList;

import java.util.List;

public class Movie {
    private int id;
    private String title;
    private float imdbRating;
    private float personalRating;
    private String fileLink;
    private float lastView;
    private float time;
    private String director;
    private int year;
    private ObservableList categoryList;

    public Movie(int id, String title, float imdbRating, float personalRating, String fileLink, float lastView, int time, String director, int year, ObservableList categoryList){
        this.id = id;
        this.title = title;
        this.imdbRating = imdbRating;
        this.personalRating = personalRating;
        this.fileLink = fileLink;
        this.lastView = lastView;
        this.time = time;
        this.director = director;
        this.year = year;
        this.categoryList = categoryList;
    }
    public String getTitle(){
        return title;
    }
    public float getImdbRating(){
        return imdbRating;
    }
    public float getPersonalRating(){
        return personalRating;
    }
    public String getFileLink(){
        return fileLink;
    }
    public float getLastView(){
        return lastView;
    }
    public void setTitle(){
        this.title = title;
    }
    public void setImdbRating(){
        this.imdbRating = imdbRating;
    }
    public void setPersonalRating(){
        this.personalRating = personalRating;
    }
    public void setFileLink(){
        this.fileLink = fileLink;
    }
    public void setLastView(){
        this.lastView = lastView;
    }
    public int getid(){
        return id;
    }
    public float getTime(){
        return time;
    }
    public String getDirector(){
        return director;
    }
    public int getYear(){
        return year;
    }
    public void setTime(){
        this.time = time;
    }
    public void setDirector(){
        this.director = director;
    }
    public void setYear(){
        this.year = year;
    }
    public ObservableList getCategoryList(){
        return categoryList;
    }
    public void setCategoryList(){
        this.categoryList = categoryList;
    }
}
