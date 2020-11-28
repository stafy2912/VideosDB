package video;

import fileio.MovieInputData;
import user.User;
import user.UserDB;


public class Movie extends Video {

    private int duration;
    private double rating;
    private int counter;

    public Movie(final MovieInputData data) {
        title = data.getTitle();
        year = data.getYear();
        cast = data.getCast();
        genres = data.getGenres();
        duration = data.getDuration();
        rating = 0;
        counter = 0;
    }

    public final int getDuration() {
        return duration;
    }

    public final void setDuration(final int duration) {
        this.duration = duration;
    }

    public final double getRating() {
        return rating;
    }

    public final void setRating(final double rating) {
        this.rating = rating;
    }

    public final int getCounter() {
        return counter;
    }

    public final void setCounter(final int counter) {
        this.counter = counter;
    }

    /**
     *
     * @return the average rating of a movie
     */
    public final double gettruerating() {
        if (counter != 0) {
            return rating / counter;
        }
        return 0;
    }

    /**
     * @param users the users database
     * @return the number of times the movie (its name is title)
     * is in the favoite list of the users
     */
    public int getnmbfavorites(final UserDB users) {
        int count = 0;
        for (User user : users.getUserlist()) {
            if (user.getFavoriteMovies().contains(title)) {
                count++;
            }
        }
        return count;
    }

}
