package video;

import fileio.MovieInputData;
import user.User;
import user.UserDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public final class MovieDB {

    public HashMap<String, Integer> getFavoritestitle() {
        return favoritestitle;
    }

    public void setFavoritestitle(final HashMap<String, Integer> favoritestitle1) {
        this.favoritestitle = favoritestitle1;
    }

    private HashMap<String, Integer> viewslist;

    private final ArrayList<Movie> movielist;

    private HashMap<String, Double> ratinglist;

    private HashMap<String, Integer> durationlist;

    private HashMap<String, Integer> favoritestitle;

    public HashMap<String, Integer> getViewslist() {
        return viewslist;
    }

    public void setViewslist(final HashMap<String, Integer> viewslist1) {
        this.viewslist = viewslist1;
    }

    public HashMap<String, Integer> getDurationlist() {
        return durationlist;
    }

    public void setDurationlist(final HashMap<String, Integer> durationlist1) {
        this.durationlist = durationlist1;
    }

    public HashMap<String, Double> getRatinglist() {
        return ratinglist;
    }

    public void setRatinglist(final HashMap<String, Double> ratinglist1) {
        this.ratinglist = ratinglist1;
    }

    public ArrayList<Movie> getMovielist() {
        return movielist;
    }

    public MovieDB(final ArrayList<Movie> list) {
        movielist = list;
    }

    /**
     * @param datalist movieinput data base
     *                 creating my own movie data base
     */
    public void copy(final List<MovieInputData> datalist) {
        Movie buffermovie;
        for (MovieInputData moviex : datalist) {

            buffermovie = new Movie(moviex);
            movielist.add(buffermovie);
        }
        this.viewslist = new HashMap<>();

        this.ratinglist = new HashMap<>();
        this.durationlist = new HashMap<>();
        this.favoritestitle = new HashMap<>();
    }

    /**
     * @param movies the movies data base
     * @param name   the name data base
     * @return the wanted movie, or null if not found
     */
    public Movie search(final MovieDB movies, final String name) {
        Movie dummy = null;
        for (Movie movie : movies.getMovielist()) {
            if (movie.getTitle().equals(name)) {
                dummy = movie;
            }
        }
        return dummy;
    }

    /**
     * @param movies the movies data base
     * @param gen    the filter genre
     * @param year   the filter year
     *               creates a hashmap of <movie name><movie ratings>
     */
    public void copyratings(final MovieDB movies, final String gen,
                            final String year) {
        boolean ok;
        movies.getRatinglist().clear();
        for (Movie movie : movies.getMovielist()) {
            ok = movie.getGenres().contains(gen);
            if (ok || gen == null) {
                if (movie.gettruerating() != 0
                        && (Integer.toString(movie.getYear()).equals(year) || year == null)) {
                    movies.getRatinglist().put(movie.getTitle(), movie.gettruerating());
                }
            }
        }
    }

    /**
     * @param movies the movies data base
     *               sorts the ratong hashmap containing
     *               <movie name><movie rating>
     */
    public void sortratinglist(final MovieDB movies) {
        List<Double> mapvalues = new ArrayList<>(ratinglist.values());
        List<String> mapkeys = new ArrayList<>(ratinglist.keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (double val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                double comp1 = ratinglist.get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        movies.setRatinglist(sortedMap);
    }

    /**
     * @param movies the movies data base
     * @param gen    the filter genre
     * @param year   the filter year
     */
    public void copyduration(final MovieDB movies, final String gen, final String year) {
        movies.getDurationlist().clear();
        for (Movie movie : movies.getMovielist()) {
            if ((year == null || Integer.toString(movie.getYear()).equals(year))
                    && (movie.getGenres().contains(gen) || gen == null)) {
                movies.getDurationlist().put(movie.getTitle(), movie.getDuration());
            }
        }
    }

    /**
     * @param movies the movies data base
     *               sorts the hashmap containing
     *               <movie name><movie duration>
     */
    public void sortdurationlist(final MovieDB movies) {
        List<Integer> mapvalues = new ArrayList<>(durationlist.values());
        List<String> mapkeys = new ArrayList<>(durationlist.keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = durationlist.get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        movies.setDurationlist(sortedMap);
    }

    /**
     * @param users  the users data base
     * @param movies the movies data base
     * @param gen    the filter genre
     * @param year   the filter year
     *               creates a hasmap of <movie name><number of views>
     */
    public void copyviews(final UserDB users, final MovieDB movies,
                          final String gen, final String year) {
        movies.getViewslist().clear();
        for (Movie movie : movies.getMovielist()) {
            if ((gen == null || movie.getGenres().contains(gen))
                    && (year == null || Integer.toString(movie.getYear()).equals(year))) {
                for (User user : users.getUserlist()) {
                    if (user.getHistory().containsKey(movie.getTitle())) {
                        if (!movies.getViewslist().containsKey(movie.getTitle())) {
                            movies.getViewslist().put(movie.getTitle(),
                                    user.getHistory().get(movie.getTitle()));
                        } else {
                            movies.getViewslist().put(movie.getTitle(),
                                    movies.getViewslist().get(movie.getTitle())
                                            + user.getHistory().get(movie.getTitle()));
                        }
                    }
                }
            }
        }
    }

    /**
     * @param movies the movies data base
     *               sorts the above created hashmap
     */
    public void sortViewslist(final MovieDB movies) {
        List<Integer> mapvalues = new ArrayList<>(movies.getViewslist().values());
        List<String> mapkeys = new ArrayList<>(movies.getViewslist().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = movies.getViewslist().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        movies.setViewslist(sortedMap);
    }

    /**
     * @param movies the movies data base
     * @param users  the user data base
     * @param gen    the genre fol filter
     * @param year   the year in filter
     */
    public void copyfavoritestitle(final MovieDB movies, final UserDB users,
                                   final String gen, final String year) {
        movies.getFavoritestitle().clear();
        for (User user : users.getUserlist()) {
            for (Movie movie : movies.getMovielist()) {
                if ((Integer.toString(movie.getYear()).equals(year) || year == null)
                        && (movie.getGenres().contains(gen) || gen == null)) {
                    if (user.getFavoriteMovies().contains(movie.getTitle())
                            && !movies.getFavoritestitle().containsKey(movie.getTitle())) {
                        movies.getFavoritestitle().put(movie.getTitle(), 1);
                    } else if (user.getFavoriteMovies().contains(movie.getTitle())
                            && movies.getFavoritestitle().containsKey(movie.getTitle())) {
                        movies.getFavoritestitle().put(movie.getTitle(),
                                movies.getFavoritestitle().get(movie.getTitle()) + 1);
                    }
                }
            }
        }
    }

    /**
     * @param movies the movies database
     *               sorts the favorite movies
     */
    public void sortfavoritetitles(final MovieDB movies) {
        List<Integer> mapvalues = new ArrayList<>(movies.getFavoritestitle().values());
        List<String> mapkeys = new ArrayList<>(movies.getFavoritestitle().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = movies.getFavoritestitle().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        movies.setFavoritestitle(sortedMap);
    }

}
