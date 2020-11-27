package user;

import fileio.MovieInputData;
import fileio.UserInputData;
import video.Movie;
import video.MovieDB;
import video.Series;
import video.SeriesDB;

import java.util.*;

public class UserDB {

    public ArrayList<User> user_list;

    public HashMap<String, Integer> actions_list;

    public HashMap<String, Integer> favorites;

    public HashMap<String, Double> genre_list;

    private HashMap<String, Integer> fav_movies;

    public HashMap<String, Integer> getActions_list() {
        return actions_list;
    }

    public void setActions_list(HashMap<String, Integer> actions_list) {
        this.actions_list = actions_list;
    }

    public UserDB(ArrayList<User> user_list) {
        this.user_list = user_list;
        this.fav_movies = new HashMap<>();
        this.actions_list = new HashMap<>();
        this.favorites = new HashMap<>();
        this.genre_list = new HashMap<>();
        this.fav_movies = new HashMap<>();
    }

    public HashMap<String, Double> getGenre_list() {
        return genre_list;
    }

    public HashMap<String, Integer> getFavorites() {
        return favorites;
    }

    public void setFavorites(HashMap<String, Integer> favorites) {
        this.favorites = favorites;
    }

    public void setGenre_list(HashMap<String, Double> genre_list) {
        this.genre_list = genre_list;
    }

    public HashMap<String, Integer> getFav_movies() {
        return fav_movies;
    }

    public void setFav_movies(HashMap<String, Integer> fav_movies) {
        this.fav_movies = fav_movies;
    }

    public ArrayList<User> getUser_list() {
        return user_list;
    }

    public void setUser_list(ArrayList<User> user_list) {
        this.user_list = user_list;
    }

    public void copy(List<UserInputData> data_list) {
        User buffer_user;
        for (UserInputData userx : data_list) {
            buffer_user = new User(userx);
            user_list.add(buffer_user);
        }
    }


    public void find_favmovies(UserDB users, List<MovieInputData> data) {
        for (MovieInputData movie : data) {
            if (!fav_movies.containsKey(movie.getTitle())) {
                fav_movies.put(movie.getTitle(), 1);
            }
        }

        for (int i = 0; i < users.user_list.size(); i++) {
            for (String Title : users.user_list.get(i).getFavoriteMovies()) {
                if (fav_movies.containsKey(Title)) {
                    fav_movies.put(Title, fav_movies.get(Title) + 1);
                }
            }
        }
    }


    public User search(String nickname) {
        for (User user : user_list) {
            if (user.getUsername().equals(nickname)) {
                return user;
            }
        }
        return null;
    }

    public void sortFavourite(UserDB users) {
        List<Integer> mapvalues = new ArrayList<>(fav_movies.values());
        List<String> mapkeys = new ArrayList<>(fav_movies.keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = fav_movies.get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        users.setFav_movies(sortedMap);
    }

    public void copy_numactions(UserDB users) {
        users.getActions_list().clear();
        for (User user : users.getUser_list()) {
            if (user.getNo_actions() != 0) {
                actions_list.put(user.getUsername(), user.getNo_actions());
            }
        }
    }

    public void sortnoactions(UserDB users) {
        List<Integer> mapvalues = new ArrayList<>(users.getActions_list().values());
        List<String> mapkeys = new ArrayList<>(users.getActions_list().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = users.getActions_list().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        users.setActions_list(sortedMap);
    }

    public void copy_genre_list(MovieDB movies, SeriesDB series) {
        genre_list.clear();
        for (Movie movie : movies.getMovie_list()) {
            for (String gen : movie.getGenres()) {
                if (!genre_list.containsKey(gen)) {
                    genre_list.put(gen, 1.0);
                } else {
                    genre_list.put(gen, genre_list.get(gen) + 1);
                }
            }
        }
        for (Series serie : series.getSeries_list()) {
            for (String gen : serie.getGenres()) {
                if (!genre_list.containsKey(gen)) {
                    genre_list.put(gen, 1.0);
                } else {
                    genre_list.put(gen, genre_list.get(gen) + 1);
                }
            }
        }
    }

    public void sort_genres_list(UserDB users) {
        List<Double> mapvalues = new ArrayList<>(users.getGenre_list().values());
        List<String> mapkeys = new ArrayList<>(users.getGenre_list().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (double val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                double comp1 = users.getGenre_list().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        users.setGenre_list(sortedMap);
    }

    public void copy_favorites(UserDB users, User wanted) {
        users.getFavorites().clear();
        for (User user : users.getUser_list()) {
            for (String title : user.getFavoriteMovies()) {
                if (!users.getFavorites().containsKey(title) && !wanted.getHistory().containsKey(title)) {
                    users.getFavorites().put(title, 1);
                } else if (users.getFavorites().containsKey(title) && !wanted.getHistory().containsKey(title)) {
                    users.getFavorites().put(title, users.getFavorites().get(title) + 1);
                }
            }
        }
    }

    public void sort_favorit(UserDB users) {
        List<Integer> mapvalues = new ArrayList<>(users.getFavorites().values());
        List<String> mapkeys = new ArrayList<>(users.getFavorites().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = users.getFavorites().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        users.setFavorites(sortedMap);
    }

//    public void copy_allfavorites(UserDB users)  {
//        for (User user : users.getUser_list()){
//            for (String title : user.getFavoriteMovies()){
//                if (!users.getAll_favorites().containsKey(title)){
//                    users.getAll_favorites().put(title, 1);
//                }
//                else if (users.getAll_favorites().containsKey(title)){
//                    users.getAll_favorites().put(title, users.getAll_favorites().get(title) + 1);
//                }
//            }
//
//        }
//    }
//
//    public void sort_allfavorit(UserDB users){
//        List<Integer> mapvalues = new ArrayList<>(users.getAll_favorites().values());
//        List<String> mapkeys = new ArrayList<>(users.getAll_favorites().keySet());
//        Collections.sort(mapkeys);
//        Collections.sort(mapvalues);
//        LinkedHashMap<String,Integer> sortedMap = new LinkedHashMap<>();
//        for (int val : mapvalues) {
//            Iterator<String> key = mapkeys.iterator();
//
//            while (key.hasNext()) {
//                String cheie = key.next();
//                int comp1 = users.getAll_favorites().get(cheie);
//
//                if (comp1 == val) {
//                    key.remove();
//                    sortedMap.put(cheie, val);
//                    break;
//                }
//
//            }
//
//        }
//        users.setFavorites(sortedMap);
//    }


}


