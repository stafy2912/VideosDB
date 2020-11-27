package video;

import fileio.MovieInputData;
import user.User;
import user.UserDB;

import java.util.*;

public class MovieDB {

    public HashMap<String, Integer> getFavorites_title() {
        return favorites_title;
    }

    public void setFavorites_title(HashMap<String, Integer> favorites_title) {
        this.favorites_title = favorites_title;
    }

    private HashMap<String, Integer> views_list;

    private ArrayList<Movie> movie_list;

    private HashMap<String, Double> rating_list;

    private HashMap<String, Integer> duration_list;

    private HashMap<String, Integer> favorites_title;

    public HashMap<String, Integer> getViews_list() {
        return views_list;
    }

    public void setViews_list(HashMap<String, Integer> views_list) {
        this.views_list = views_list;
    }

    public HashMap<String, Integer> getDuration_list() {
        return duration_list;
    }

    public void setDuration_list(HashMap<String, Integer> duration_list) {
        this.duration_list = duration_list;
    }

    public HashMap<String, Double> getRating_list() {
        return rating_list;
    }

    public void setRating_list(HashMap<String, Double> rating_list) {
        this.rating_list = rating_list;
    }

    public ArrayList<Movie> getMovie_list() {
        return movie_list;
    }

    public void setMovie_list(ArrayList<Movie> movie_list) {
        this.movie_list = movie_list;
    }

    public MovieDB(ArrayList<Movie> list) {
        movie_list = list;
    }

    public void copy(List<MovieInputData> data_list) {
        Movie buffer_movie;
        for (MovieInputData moviex : data_list) {

            buffer_movie = new Movie(moviex);
            movie_list.add(buffer_movie);
        }
        this.views_list = new HashMap<>();

        this.rating_list = new HashMap<>();
        this.duration_list = new HashMap<>();
        this.favorites_title = new HashMap<>();
    }

    public Movie search(MovieDB movies, String Name) {
        Movie dummy = null;
        for (Movie movie : movies.getMovie_list()) {
            if (movie.getTitle().equals(Name)) {
                dummy = movie;
            }
        }
        return dummy;
    }

    public void copy_ratings(MovieDB movies, String gen, String year) {
        boolean ok;
        movies.getRating_list().clear();
        for (Movie movie : movies.getMovie_list()) {
            ok = movie.getGenres().contains(gen);
            if (ok || gen == null) {
                if (movie.gettruerating() != 0 && (Integer.toString(movie.getYear()).equals(year) || year == null)) {
                    movies.getRating_list().put(movie.getTitle(), movie.gettruerating());
                }
            }
        }
    }

    public void sortrating_list(MovieDB movies) {
        List<Double> mapvalues = new ArrayList<>(rating_list.values());
        List<String> mapkeys = new ArrayList<>(rating_list.keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (double val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                double comp1 = rating_list.get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        movies.setRating_list(sortedMap);
    }


    public void copy_duration(MovieDB movies, String gen, String year) {
        movies.getDuration_list().clear();
        for (Movie movie : movies.getMovie_list()) {
            if ((year == null || Integer.toString(movie.getYear()).equals(year)) && (movie.getGenres().contains(gen) || gen == null)) {
                movies.getDuration_list().put(movie.getTitle(), movie.getDuration());
            }
        }
    }

    public void sortduration_list(MovieDB movies) {
        List<Integer> mapvalues = new ArrayList<>(duration_list.values());
        List<String> mapkeys = new ArrayList<>(duration_list.keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = duration_list.get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        movies.setDuration_list(sortedMap);
    }

    public void copy_views(UserDB users, MovieDB movies, String gen, String year) {
        movies.getViews_list().clear();
        for (Movie movie : movies.getMovie_list()) {
            if ((gen == null || movie.getGenres().contains(gen)) && (year == null || Integer.toString(movie.getYear()).equals(year))) {
                for (User user : users.getUser_list()) {
                    if (user.getHistory().containsKey(movie.getTitle())) {
                        if (!movies.getViews_list().containsKey(movie.getTitle())) {
                            movies.getViews_list().put(movie.getTitle(), user.getHistory().get(movie.getTitle()));
                        } else {
                            movies.getViews_list().put(movie.getTitle(), movies.getViews_list().get(movie.getTitle()) + user.getHistory().get(movie.getTitle()));
                        }
                    }
                }
            }
        }
    }


    public void sortviews_list(MovieDB movies) {
        List<Integer> mapvalues = new ArrayList<>(movies.getViews_list().values());
        List<String> mapkeys = new ArrayList<>(movies.getViews_list().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = movies.getViews_list().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        movies.setViews_list(sortedMap);
    }

    public void copy_favoritestitle(MovieDB movies, UserDB users, String gen, String year) {
        movies.getFavorites_title().clear();
        for (User user : users.getUser_list()) {
            for (Movie movie : movies.getMovie_list()) {
                if ((Integer.toString(movie.getYear()).equals(year) || year == null) && (movie.getGenres().contains(gen) || gen == null)) {
                    if (user.getFavoriteMovies().contains(movie.getTitle()) && !movies.getFavorites_title().containsKey(movie.getTitle())) {
                        movies.getFavorites_title().put(movie.getTitle(), 1);
                    } else if (user.getFavoriteMovies().contains(movie.getTitle()) && movies.getFavorites_title().containsKey(movie.getTitle())) {
                        movies.getFavorites_title().put(movie.getTitle(), movies.getFavorites_title().get(movie.getTitle()) + 1);
                    }
                }
            }
        }
    }

    public void sort_favoritetitles(MovieDB movies) {
        List<Integer> mapvalues = new ArrayList<>(movies.getFavorites_title().values());
        List<String> mapkeys = new ArrayList<>(movies.getFavorites_title().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = movies.getFavorites_title().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        movies.setFavorites_title(sortedMap);
    }

//    public void copy_allratings(MovieDB movies, String gen) {
//        for (Movie movie : movies.getMovie_list()) {
//            if (movie.getGenres().contains(gen)) {
//                movies.getAllratings_list().put(movie.getTitle(), movie.gettruerating());
//            }
//        }
//    }

}
