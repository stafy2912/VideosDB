package video;

import fileio.MovieInputData;
import user.User;
import user.UserDB;

import java.util.ArrayList;

public class Movie extends Video {

    private int duration;
    private String title;
    private int year;
    private ArrayList<String> cast;
    private ArrayList<String> genres;
    private double rating;
    private int counter;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public ArrayList<String> getCast() {
        return cast;
    }

    @Override
    public void setCast(ArrayList<String> cast) {
        this.cast = cast;
    }

    @Override
    public ArrayList<String> getGenres() {
        return genres;
    }

    @Override
    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public Movie(MovieInputData data) {
        title = data.getTitle();
        year = data.getYear();
        cast = data.getCast();
        genres = data.getGenres();
        duration = data.getDuration();
        rating = 0;
        counter = 0;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public double gettruerating() {
        if (counter != 0) {
            return rating / counter;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "duration=" + duration +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", cast=" + cast +
                ", genres=" + genres +
                ", rating=" + rating +
                ", counter=" + counter +
                '}';
    }

    public void addRating(Double grade) {
        rating += grade;
    }

    public int get_nofavorites(UserDB users) {
        int count = 0;
        for (User user : users.getUser_list()) {
            if (user.getFavoriteMovies().contains(title)) {
                count++;
            }
        }
        return count;
    }

}
