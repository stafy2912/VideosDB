package user;

import fileio.MovieInputData;
import fileio.UserInputData;
import video.Movie;
import video.MovieDB;
import video.Series;
import video.SeriesDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class UserDB {

    private ArrayList<User> userlist;

    private HashMap<String, Integer> actionslist;

    private HashMap<String, Integer> favorites;

    private HashMap<String, Double> genrelist;

    private HashMap<String, Integer> favmovies;

    public final HashMap<String, Integer> getActionslist() {
        return actionslist;
    }

    public final void setActionslist(final HashMap<String, Integer> actionslist) {
        this.actionslist = actionslist;
    }

    public UserDB(final ArrayList<User> userlist) {
        this.userlist = userlist;
        this.favmovies = new HashMap<>();
        this.actionslist = new HashMap<>();
        this.favorites = new HashMap<>();
        this.genrelist = new HashMap<>();
        this.favmovies = new HashMap<>();
    }

    public final HashMap<String, Double> getGenrelist() {
        return genrelist;
    }

    public final HashMap<String, Integer> getFavorites() {
        return favorites;
    }

    public final void setFavorites(final HashMap<String, Integer> favorites) {
        this.favorites = favorites;
    }

    public final void setGenrelist(final HashMap<String, Double> genrelist1) {
        this.genrelist = genrelist1;
    }

    public final HashMap<String, Integer> getFavmovies() {
        return favmovies;
    }

    public final void setFavmovies(final HashMap<String, Integer> favmovies1) {
        this.favmovies = favmovies1;
    }

    public final ArrayList<User> getUserlist() {
        return userlist;
    }

    public final void setUserlist(final ArrayList<User> userlist1) {
        this.userlist = userlist1;
    }

    /**
     * @param datalist iall users from input
     *                  creates my own database of users
     */
    public final void copy(final List<UserInputData> datalist) {
        User bufferuser;
        for (UserInputData userx : datalist) {
            bufferuser = new User(userx);
            userlist.add(bufferuser);
        }
    }

    /**
     * @param users the users data base
     * @param data  list of movies
     *              creates a list of all favourite movies
     */
    public void findfavmovies(final UserDB users, final List<MovieInputData> data) {
        for (MovieInputData movie : data) {
            if (!favmovies.containsKey(movie.getTitle())) {
                favmovies.put(movie.getTitle(), 1);
            }
        }
        for (int i = 0; i < users.userlist.size(); i++) {
            for (String title : users.userlist.get(i).getFavoriteMovies()) {
                if (favmovies.containsKey(title)) {
                    favmovies.put(title, favmovies.get(title) + 1);
                }
            }
        }
    }

    /**
     * @param nickname the user's name
     * @return the wanted user or null if not found
     */
    public User search(final String nickname) {
        for (User user : userlist) {
            if (user.getUsername().equals(nickname)) {
                return user;
            }
        }
        return null;
    }

    /**
     * @param users the users database
     *              sorts the favourite movies
     */
    public void sortFavourite(final UserDB users) {
        List<Integer> mapvalues = new ArrayList<>(users.getFavmovies().values());
        List<String> mapkeys = new ArrayList<>(users.getFavmovies().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = users.getFavmovies().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        users.setFavmovies(sortedMap);
    }

    /**
     * @param users ths users data base
     *              creates a hashmap of <user name><num of valid actions>
     */
    public void copynumactions(final UserDB users) {
        users.getActionslist().clear();
        for (User user : users.getUserlist()) {
            if (user.getNoactions() != 0) {
                actionslist.put(user.getUsername(), user.getNoactions());
            }
        }
    }

    /**
     * @param users the users database
     *              sorts the above mentioned hasmap
     */
    public void sortnoactions(final UserDB users) {
        List<Integer> mapvalues = new ArrayList<>(users.getActionslist().values());
        List<String> mapkeys = new ArrayList<>(users.getActionslist().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = users.getActionslist().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        users.setActionslist(sortedMap);
    }

    /**
     * @param movies the movies database
     * @param series the series database
     *               creates a hashmap of <genre><number of apparitions>
     */
    public void copygenrelist(final MovieDB movies, final SeriesDB series) {
        genrelist.clear();
        for (Movie movie : movies.getMovielist()) {
            for (String gen : movie.getGenres()) {
                if (!genrelist.containsKey(gen)) {
                    genrelist.put(gen, 1.0);
                } else {
                    genrelist.put(gen, genrelist.get(gen) + 1);
                }
            }
        }
        for (Series serie : series.getSerieslist()) {
            for (String gen : serie.getGenres()) {
                if (!genrelist.containsKey(gen)) {
                    genrelist.put(gen, 1.0);
                } else {
                    genrelist.put(gen, genrelist.get(gen) + 1);
                }
            }
        }
    }

    /**
     * @param users the users database
     *              sorts the genrelist hashmap
     */
    public void sortgenreslist(final UserDB users) {
        List<Double> mapvalues = new ArrayList<>(users.getGenrelist().values());
        List<String> mapkeys = new ArrayList<>(users.getGenrelist().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (double val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                double comp1 = users.getGenrelist().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        users.setGenrelist(sortedMap);
    }

    /**
     * @param users  the users database
     * @param wanted the user for whom we search
     *               creates a hashmap of
     *               <video title><number of apparitions in favorite list>
     */
    public void copyfavorites(final UserDB users, final User wanted) {
        users.getFavorites().clear();
        for (User user : users.getUserlist()) {
            for (String title : user.getFavoriteMovies()) {
                if (!users.getFavorites().containsKey(title)
                        && !wanted.getHistory().containsKey(title)) {
                    users.getFavorites().put(title, 1);
                } else if (users.getFavorites().containsKey(title)
                        && !wanted.getHistory().containsKey(title)) {
                    users.getFavorites().put(title, users.getFavorites().get(title) + 1);
                }
            }
        }
    }

    /**
     * @param users the users database
     *              sorts the favorite_shows hashmap
     */
    public void sortfavorite(final UserDB users) {
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

}


