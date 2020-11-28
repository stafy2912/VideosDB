package video;

import fileio.ShowInput;

import java.util.ArrayList;

public class Video {

    protected String title;
    protected int year;
    protected ArrayList<String> cast;
    protected ArrayList<String> genres;

    public Video() {
    }

    public Video(final ShowInput data) {
        title = data.getTitle();
        year = data.getYear();
        cast = data.getCast();
        genres = data.getGenres();
    }

    /**
     * @return title of the video
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title set title of the video
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * @return year of apparition
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year set year of apparition
     */
    public void setYear(final int year) {
        this.year = year;
    }

    /**
     * @return cast of the video
     */
    public ArrayList<String> getCast() {
        return cast;
    }

    /**
     * @param cast set cast of the video
     */
    public void setCast(final ArrayList<String> cast) {
        this.cast = cast;
    }

    /**
     * @return list of genres for the video
     */
    public ArrayList<String> getGenres() {
        return genres;
    }

    /**
     * @param genres sets the genres list foor the video
     */
    public void setGenres(final ArrayList<String> genres) {
        this.genres = genres;
    }


}
