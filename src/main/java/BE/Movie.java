package BE;

import java.time.LocalDate;

public class Movie {

    private int id;
    private String title;
    private float imdbRating;
    private float personalRating;
    private String fileLink;
    private LocalDate lastView;
    private float time;
    private String director;
    private int year;

    public Movie(int id, String title, float imdbRating, String fileLink, LocalDate lastView, float personalRating, String director, float time, int year) {
        this.id = id;
        this.title = title;
        this.imdbRating = imdbRating;
        this.fileLink = fileLink;
        this.lastView = lastView;
        this.personalRating = personalRating;
        this.director = director;
        this.time = time;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public float getImdbRating() {
        return imdbRating;
    }

    public float getPersonalRating() {
        return personalRating;
    }

    public String getFileLink() {
        return fileLink;
    }

    public LocalDate getLastView() {
        return lastView;
    }

    public float getTime() {
        return time;
    }

    public String getDirector() {
        return director;
    }

    public int getYear() {
        return year;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImdbRating(float imdbRating) {
        this.imdbRating = imdbRating;
    }

    public void setPersonalRating(float personalRating) {
        this.personalRating = personalRating;
    }

    public void setFileLink(String fileLink) {
        this.fileLink = fileLink;
    }

    public void setLastView(LocalDate lastView) {
        this.lastView = lastView;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
