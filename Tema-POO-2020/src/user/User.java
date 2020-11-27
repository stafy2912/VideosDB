package user;


import java.util.*;

import fileio.UserInputData;
import video.Movie;
import video.MovieDB;
import video.Series;
import video.SeriesDB;


public class User {

    private String username;
    private String subscriptionType;
    private  Map<String, Integer> history;
    private final ArrayList<String> favoriteMovies;
    private final ArrayList<String> movie_user;
    private Map<String, ArrayList<String> > serie_user;
    private int no_actions;
    private HashMap<String, Double> shows_ratings;

    public User(UserInputData a) {
        username = a.getUsername();
        subscriptionType = a.getSubscriptionType();
        history = a.getHistory();
        favoriteMovies = a.getFavoriteMovies();
        movie_user = new ArrayList<>();
        serie_user = new HashMap<>();
        no_actions = 0;
        shows_ratings = new HashMap<>();
    }

    public HashMap<String, Double> ratings_genre = new HashMap<>();

    public HashMap<String, Double> getRatings_genre() {
        return ratings_genre;
    }

    public void setRatings_genre(HashMap<String, Double> ratings_genre) {
        this.ratings_genre = ratings_genre;
    }

    public Map<String, ArrayList<String>> getSerie_user() {
        return serie_user;
    }

    public void setSerie_user(Map<String, ArrayList<String>> serie_user) {
        this.serie_user = serie_user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public void setHistory(Map<String, Integer> history) {
        this.history = history;
    }

    public ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public int getNo_actions() {
        return no_actions;
    }

    public void setNo_actions(int no_actions) {
        this.no_actions = no_actions;
    }

    public HashMap<String, Double> getShows_ratings() {
        return shows_ratings;
    }

    public void setShows_ratings(HashMap<String, Double> shows_ratings) {
        this.shows_ratings = shows_ratings;
    }

    public void View(String Title) {
        if (history.containsKey(Title)) {
            int count = history.get(Title);
            history.put(Title, count + 1);
        } else {
            history.put(Title, 1);
        }
    }

    public int search_Favourites(String video_name) {
        if (favoriteMovies.size() == 0){
            return -1;
        }
        else {
            for (int i = 0; i < favoriteMovies.size(); i++) {
                if (favoriteMovies.get(i).equals(video_name)) {
                    return i;
                }
            }
        }
        return -1;
    }


    public String Favorites(String Title, User wanted_user) {
        StringBuilder message = new StringBuilder();
        if (!history.containsKey(Title)) {
            message.append("error -> ");
            message.append(Title);
            message.append(" is not seen");
        } else {
            int position = wanted_user.search_Favourites(Title);
            if (position == -1) {
                wanted_user.favoriteMovies.add(Title);
                message.append("success -> ");
                message.append(Title);
                message.append(" was added as favourite");

            } else {
                message.append("error -> ");
                message.append(Title);
                message.append(" is already in favourite list");
            }
        }
        return message.toString();
    }

    public String Rating(User wanted_user, double rating, String Title, int season_number, MovieDB movies){
        StringBuilder message = new StringBuilder();
        Movie dummy_movie = movies.search(movies, Title);
        if (dummy_movie != null) {
            if (history.containsKey(Title) && movie_user.contains(Title)) {
                message.append("error -> ");
                message.append(Title);
                message.append(" has been already rated");

            } else if (history.containsKey(Title) && !movie_user.contains(Title)) {
                message.append("success -> ");
                message.append(Title);
                message.append(" was rated with ");
                message.append(rating);
                message.append(" by ");
                message.append(wanted_user.getUsername());
                wanted_user.setNo_actions(wanted_user.getNo_actions() + 1);
                movie_user.add(Title);
            } else if (!history.containsKey(Title)) {
                message.append("error -> ");
                message.append(Title);
                message.append(" is not seen");
            }

        }
        else {
            if (!serie_user.containsKey(Title)) {
                serie_user.put(Title, new ArrayList<>());
            }
            if (history.containsKey(Title) && serie_user.get(Title).contains(Integer.toString(season_number))) {
                message.append("error -> ");
                message.append(Title);
                message.append(" has been already rated");

            } else if (history.containsKey(Title) && !serie_user.get(Title).contains(Integer.toString(season_number))) {
                message.append("success -> ");
                message.append(Title);
                message.append(" was rated with ");
                message.append(rating);
                message.append(" by ");
                message.append(wanted_user.getUsername());
                serie_user.get(Title).add(Integer.toString(season_number));
                wanted_user.setNo_actions(wanted_user.getNo_actions() + 1);
            } else if (!history.containsKey(Title)) {
                message.append("error -> ");
                message.append(Title);
                message.append(" is not seen");
            }
        }
        return message.toString();
        }


    public void copy_showsratings(MovieDB movies, SeriesDB series, User user){
        user.getShows_ratings().clear();
        for (Movie movie : movies.getMovie_list()){
            if (!user.getHistory().containsKey(movie.getTitle()) && movie.gettruerating() != 0) {
                user.getShows_ratings().put(movie.getTitle(), movie.gettruerating());
            }
        }
        for (Series serie : series.getSeries_list()){
            if (!user.getHistory().containsKey(serie.getTitle()) && serie.gettruerating() != 0 ) {
                user.getShows_ratings().put(serie.getTitle(), serie.gettruerating());
            }
        }


    }


    public void sort_showsratings(User user){
        List<Double> mapvalues = new ArrayList<>(user.getShows_ratings().values());
        List<String> mapkeys = new ArrayList<>(user.getShows_ratings().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String,Double> sortedMap = new LinkedHashMap<>();
        for (double val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                double comp1 = user.getShows_ratings().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        user.setShows_ratings(sortedMap);
    }

//    public void sort_moviesratings(User user){
//        List<Double> mapvalues = new ArrayList<>(user.getMovie_rating().values());
//        List<String> mapkeys = new ArrayList<>(user.getMovie_rating().keySet());
//        Collections.sort(mapkeys);
//        Collections.sort(mapvalues);
//        LinkedHashMap<String,Double> sortedMap = new LinkedHashMap<>();
//        Iterator<Double> iterator = mapvalues.iterator();
//        while (iterator.hasNext()){
//            double val = iterator.next();
//            Iterator<String> key = mapkeys.iterator();
//
//            while (key.hasNext() ){
//                String cheie = key.next();
//                double comp1 = user.getMovie_rating().get(cheie);
//
//                if (comp1 == val){
//                    key.remove();
//                    sortedMap.put(cheie, val);
//                    break;
//                }
//
//            }
//
//        }
//        user.setMovie_rating(sortedMap);
//    }

    public void copy_ratingsgenre(User user, MovieDB movies, SeriesDB series, String gen){
        user.getRatings_genre().clear();
        for (Movie movie : movies.getMovie_list()){
            if (movie.getGenres().contains(gen) && !user.getHistory().containsKey(movie.getTitle())){
                user.getRatings_genre().put(movie.getTitle(), movie.gettruerating());
            }
        }
        for (Series serie : series.getSeries_list() ){
            if (serie.getGenres().contains(gen)  && !user.getHistory().containsKey(serie.getTitle())){
                user.getRatings_genre().put(serie.getTitle(), serie.gettruerating());
            }
        }
    }

    public void sort_ratingsgenre(User user){
        List<Double> mapvalues = new ArrayList<>(user.getRatings_genre().values());
        List<String> mapkeys = new ArrayList<>(user.getRatings_genre().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String,Double> sortedMap = new LinkedHashMap<>();
        for (double val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                double comp1 = user.getRatings_genre().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        user.setRatings_genre(sortedMap);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", subscriptionType='" + subscriptionType + '\'' +
                ", history=" + history +
                ", favoriteMovies=" + favoriteMovies +
                ", movie_user=" + movie_user +
                ", serie_user=" + serie_user +
                ", no_actions=" + no_actions +
                '}';
    }
}
