package user;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fileio.UserInputData;
import video.Movie;
import video.MovieDB;
import video.Series;
import video.SeriesDB;


public class User {

    private String username;
    private String subscriptionType;
    private Map<String, Integer> history;
    private final ArrayList<String> favoriteMovies;
    private final ArrayList<String> movieuser;
    private final Map<String, ArrayList<String>> serieuser;
    private int nmbofactions;
    private HashMap<String, Double> showsratings;

    public User(final UserInputData a) {
        username = a.getUsername();
        subscriptionType = a.getSubscriptionType();
        history = a.getHistory();
        favoriteMovies = a.getFavoriteMovies();
        movieuser = new ArrayList<>();
        serieuser = new HashMap<>();
        nmbofactions = 0;
        showsratings = new HashMap<>();
    }

    private HashMap<String, Double> ratingsgenre = new HashMap<>();

    public final HashMap<String, Double> getRatingsgenre() {
        return ratingsgenre;
    }

    public final void setRatingsgenre(final HashMap<String, Double> ratingsgenre) {
        this.ratingsgenre = ratingsgenre;
    }

    public final Map<String, ArrayList<String>> getSerieuser() {
        return serieuser;
    }

    public final String getUsername() {
        return username;
    }

    public final void setUsername(final String username) {
        this.username = username;
    }

    public final String getSubscriptionType() {
        return subscriptionType;
    }

    public final void setSubscriptionType(final String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public final Map<String, Integer> getHistory() {
        return history;
    }

    public final void setHistory(final Map<String, Integer> history) {
        this.history = history;
    }

    public final ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public final int getNoactions() {
        return nmbofactions;
    }

    public final void setNoactions(final int nmbofactions1) {
        this.nmbofactions = nmbofactions1;
    }

    public final HashMap<String, Double> getShowsratings() {
        return showsratings;
    }

    public final void setShowsratings(final HashMap<String, Double> showsratings) {
        this.showsratings = showsratings;
    }

    public final ArrayList<String> getMovieuser() {
        return movieuser;
    }

    /**
     * @param title add a video to history
     */
    public void view(final String title) {
        if (history.containsKey(title)) {
            int count = history.get(title);
            history.put(title, count + 1);
        } else {
            history.put(title, 1);
        }
    }

    /**
     * @param videoname the video to be searched for
     * @return the index of the searched video
     */
    public int searchFavourites(final String videoname) {
        if (favoriteMovies.size() == 0) {
            return -1;
        } else {
            for (int i = 0; i < favoriteMovies.size(); i++) {
                if (favoriteMovies.get(i).equals(videoname)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * @param title      the title of the show
     * @param wanteduser the user for which we complete the command
     * @return the message generated for the action made
     */
    public String favorites(final String title, final User wanteduser) {
        StringBuilder message = new StringBuilder();
        if (!wanteduser.getHistory().containsKey(title)) {
            message.append("error -> ");
            message.append(title);
            message.append(" is not seen");
        } else {
            int position = wanteduser.searchFavourites(title);
            if (position == -1) {
                wanteduser.favoriteMovies.add(title);
                message.append("success -> ");
                message.append(title);
                message.append(" was added as favourite");

            } else {
                message.append("error -> ");
                message.append(title);
                message.append(" is already in favourite list");
            }
        }
        return message.toString();
    }

    /**
     * @param wanteduser   the user which started the action
     * @param rating       the rating he's about to give
     * @param title        the name of the show
     * @param seasonnumber the number of the season
     * @param movies       list of all the movies in the database
     *                     first check whether given title is a movie or a show
     *                     then checks if the show is seen, or already in the favourites
     * @return message showcasing the result of the user's action
     */
    public String rating(final User wanteduser, final double rating, final String title,
                         final int seasonnumber, final MovieDB movies) {
        StringBuilder message = new StringBuilder();
        Movie dummymovie = movies.search(movies, title);
        if (dummymovie != null) {
            if (wanteduser.getHistory().containsKey(title)
                    && wanteduser.getMovieuser().contains(title)) {
                message.append("error -> ");
                message.append(title);
                message.append(" has been already rated");

            } else if (wanteduser.getHistory().containsKey(title)
                    && !wanteduser.getMovieuser().contains(title)) {
                message.append("success -> ");
                message.append(title);
                message.append(" was rated with ");
                message.append(rating);
                message.append(" by ");
                message.append(wanteduser.getUsername());
                wanteduser.setNoactions(wanteduser.getNoactions() + 1);
                wanteduser.getMovieuser().add(title);
            } else if (!wanteduser.getHistory().containsKey(title)) {
                message.append("error -> ");
                message.append(title);
                message.append(" is not seen");
            }

        } else {
            if (!wanteduser.getSerieuser().containsKey(title)) {
                wanteduser.getSerieuser().put(title, new ArrayList<>());
            }
            if (wanteduser.getHistory().containsKey(title)
                    && wanteduser.getSerieuser().get(title)
                    .contains(Integer.toString(seasonnumber))) {
                message.append("error -> ");
                message.append(title);
                message.append(" has been already rated");

            } else if (wanteduser.getHistory().containsKey(title)
                    && !wanteduser.getSerieuser().get(title)
                    .contains(Integer.toString(seasonnumber))) {
                message.append("success -> ");
                message.append(title);
                message.append(" was rated with ");
                message.append(rating);
                message.append(" by ");
                message.append(wanteduser.getUsername());
                wanteduser.getSerieuser().get(title).add(Integer
                        .toString(seasonnumber));
                wanteduser.setNoactions(wanteduser.getNoactions() + 1);
            } else if (!wanteduser.getHistory().containsKey(title)) {
                message.append("error -> ");
                message.append(title);
                message.append(" is not seen");
            }
        }
        return message.toString();
    }

    /**
     * @param movies list of all the movies in the database
     * @param series list of all the series in the database
     * @param user   the user who initiates the command
     */
    public final void copyshowsratings(final MovieDB movies, final SeriesDB series,
                                       final User user) {
        user.getShowsratings().clear();
        for (Movie movie : movies.getMovielist()) {
            if (!user.getHistory().containsKey(movie.getTitle())
                    && movie.gettruerating() != 0) {
                user.getShowsratings().put(movie.getTitle(), movie.gettruerating());
            }
        }
        for (Series serie : series.getSerieslist()) {
            if (!user.getHistory().containsKey(serie.getTitle())
                    && serie.gettruerating() != 0) {
                user.getShowsratings().put(serie.getTitle(), serie.gettruerating());
            }
        }
    }

    /**
     * @param user the user who inititates the command
     *             sort all user's videos by rating
     */
    public final void sortshowsratings(final User user) {
        List<Double> mapvalues = new ArrayList<>(user.getShowsratings().values());
        List<String> mapkeys = new ArrayList<>(user.getShowsratings().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (double val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                double comp1 = user.getShowsratings().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        user.setShowsratings(sortedMap);
    }

    /**
     * @param user   the user who initiated the command
     * @param movies a list of all the movies in the database
     * @param series a list of all the series in the database
     * @param gen    the genre of the videos we want to search
     *               creates a hashmap of videos of the same genre,
     *               by adding a video name and its average rating
     */
    public final void copyratingsgenre(final User user, final MovieDB movies,
                                       final SeriesDB series, final String gen) {
        user.getRatingsgenre().clear();
        for (Movie movie : movies.getMovielist()) {
            if (movie.getGenres().contains(gen)
                    && !user.getHistory().containsKey(movie.getTitle())) {
                user.getRatingsgenre().put(movie.getTitle(), movie.gettruerating());
            }
        }
        for (Series serie : series.getSerieslist()) {
            if (serie.getGenres().contains(gen)
                    && !user.getHistory().containsKey(serie.getTitle())) {
                user.getRatingsgenre().put(serie.getTitle(), serie.gettruerating());
            }
        }
    }

    /**
     * @param user the user who started the action
     *             sort the above mentioned ratings list
     */
    public final void sortratingsgenre(final User user) {
        List<Double> mapvalues = new ArrayList<>(user.getRatingsgenre().values());
        List<String> mapkeys = new ArrayList<>(user.getRatingsgenre().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (double val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();
            while (key.hasNext()) {
                String cheie = key.next();
                double comp1 = user.getRatingsgenre().get(cheie);
                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }
            }
        }
        user.setRatingsgenre(sortedMap);
    }

}
