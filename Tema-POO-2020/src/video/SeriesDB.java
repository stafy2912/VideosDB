package video;


import fileio.SerialInputData;
import user.User;
import user.UserDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class SeriesDB {

    ArrayList<Series> series_list;

    public HashMap<String, Integer> favorite_titles;

    public HashMap<String, Double> ratings_list;

    public HashMap<String, Double> durations_list;

    public HashMap<String, Integer> views_list;

    public HashMap<String, Double> allratings_list;

    public HashMap<String, Double> getAllratings_list() {
        return allratings_list;
    }

    public HashMap<String, Integer> getViews_list() {
        return views_list;
    }

    public void setViews_list(HashMap<String, Integer> views_list) {
        this.views_list = views_list;
    }

    public HashMap<String, Double> getDuration_list() {
        return durations_list;
    }

    public void setDuration_list(HashMap<String, Double> durations_list) {
        this.durations_list = durations_list;
    }

    public HashMap<String, Double> getRatings_list() {
        return ratings_list;
    }

    public void setRatings_list(HashMap<String, Double> ratings_list) {
        this.ratings_list = ratings_list;
    }

    public SeriesDB(ArrayList<Series> series_list) {
        this.series_list = series_list;
    }

    public ArrayList<Series> getSeries_list() {
        return series_list;
    }

    public void setSeries_list(ArrayList<Series> series_list) {
        this.series_list = series_list;
    }

    public HashMap<String, Integer> getFavorite_titles() {
        return favorite_titles;
    }

    public void setFavorite_titles(HashMap<String, Integer> favorite_titles) {
        this.favorite_titles = favorite_titles;
    }

    public void copy(List<SerialInputData> data_list) {
        Series buffer_series;
        for (SerialInputData seriesx : data_list) {
            buffer_series = new Series(seriesx);
            series_list.add(buffer_series);
        }
        this.favorite_titles = new HashMap<>();
        this.ratings_list = new HashMap<>();
        this.durations_list = new HashMap<>();
        this.views_list = new HashMap<>();
        this.allratings_list = new HashMap<>();
    }

    public Series search(SeriesDB series, String Title) {
        for (Series x : series.getSeries_list()) {
            if (x.getTitle().equals(Title)) {
                return x;
            }
        }
        return null;
    }

    public void copy_ratings(SeriesDB seriex, String gen, String year) {
        seriex.getRatings_list().clear();
        for (Series series : seriex.getSeries_list()) {
            if ((gen == null || series.getGenres().contains(gen)) && (year == null || Integer.toString(series.getYear()).equals(year))) {
                if (series.gettruerating() != 0) {
                    seriex.getRatings_list().put(series.getTitle(), series.gettruerating());
                }
            }
        }
    }

    public void sort_ratings(SeriesDB series) {
        List<Double> mapvalues = new ArrayList<>(ratings_list.values());
        List<String> mapkeys = new ArrayList<>(ratings_list.keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (double val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                double comp1 = ratings_list.get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        series.setRatings_list(sortedMap);
    }

    public void copy_duration(SeriesDB series, String gen, String year) {
        series.getDuration_list().clear();
        for (Series serie : series.getSeries_list()) {
            if ((year == null || Integer.toString(serie.getYear()).equals(year)) && (gen == null || serie.getGenres().contains(gen))) {
                series.getDuration_list().put(serie.getTitle(), serie.getMinutes(serie));
            }
        }

    }

    public void sort_duration(SeriesDB series) {
        List<Double> mapvalues = new ArrayList<>(durations_list.values());
        List<String> mapkeys = new ArrayList<>(durations_list.keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (double val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                double comp1 = durations_list.get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        series.setDuration_list(sortedMap);
    }

    public void copy_views(UserDB users, SeriesDB series, String gen, String year) {
        series.getViews_list().clear();
        for (Series serie : series.getSeries_list()) {
            if ((gen == null || serie.getGenres().contains(gen)) && (year == null || Integer.toString(serie.getYear()).equals(year))) {
                for (User user : users.getUser_list()) {
                    if (user.getHistory().containsKey(serie.getTitle())) {
                        if (!series.getViews_list().containsKey(serie.getTitle())) {
                            series.getViews_list().put(serie.getTitle(), user.getHistory().get(serie.getTitle()));
                        } else {
                            series.getViews_list().put(serie.getTitle(), series.getViews_list().get(serie.getTitle()) + user.getHistory().get(serie.getTitle()));
                        }
                    }
                }
            }
        }
    }

    public void sortviews_list(SeriesDB series) {
        List<Integer> mapvalues = new ArrayList<>(series.getViews_list().values());
        List<String> mapkeys = new ArrayList<>(series.getViews_list().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = series.getViews_list().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        series.setViews_list(sortedMap);
    }

    public void copy_favoritestitle(SeriesDB series, UserDB users, String gen, String year) {
        series.getFavorite_titles().clear();
        for (Series serie : series.getSeries_list()) {
            for (User user : users.getUser_list()) {
                if ((gen == null || serie.getGenres().contains(gen)) && (year == null || Integer.toString(serie.getYear()).equals(year))) {
                    if (user.getFavoriteMovies().contains(serie.getTitle()) && !series.getFavorite_titles().containsKey(serie.getTitle())) {
                        series.getFavorite_titles().put(serie.getTitle(), 1);
                    } else if (user.getFavoriteMovies().contains(serie.getTitle()) && series.getFavorite_titles().containsKey(serie.getTitle())) {
                        series.getFavorite_titles().put(serie.getTitle(), series.getFavorite_titles().get(serie.getTitle()) + 1);
                    }
                }
            }
        }
    }

    public void sort_favoritetitles(SeriesDB series) {
        List<Integer> mapvalues = new ArrayList<>(series.getFavorite_titles().values());
        List<String> mapkeys = new ArrayList<>(series.getFavorite_titles().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = series.getFavorite_titles().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        series.setFavorite_titles(sortedMap);
    }

}
