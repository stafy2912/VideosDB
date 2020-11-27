package video;

import fileio.ShowInput;

import java.util.ArrayList;

public class Video {

    private String title;
    private int year;
    private ArrayList<String> cast;
    private ArrayList<String> genres;

    public Video() {
    }

    public Video(ShowInput data) {
        title = data.getTitle();
        year = data.getYear();
        cast = data.getCast();
        genres = data.getGenres();
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public ArrayList<String> getCast() {
        return cast;
    }

    public void setCast(ArrayList<String> cast) {
        this.cast = cast;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    @Override
    public String toString() {
        return "Video{" +
                "name='" + title + '\'' +
                ", year=" + year +
                ", cast=" + cast +
                ", genres=" + genres +
                '}';
    }

}
