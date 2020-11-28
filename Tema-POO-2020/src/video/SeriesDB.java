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

    private final ArrayList<Series> serieslist;

    private HashMap<String, Integer> favoritetitles;

    private HashMap<String, Double> ratingslist;

    private HashMap<String, Double> durationlist;

    private HashMap<String, Integer> viewslist;

    public final HashMap<String, Integer> getViewslist() {
        return viewslist;
    }

    public final void setViewslist(final HashMap<String, Integer> viewslist) {
        this.viewslist = viewslist;
    }

    public final HashMap<String, Double> getDurationlist() {
        return durationlist;
    }

    public final void setDurationlist(final HashMap<String, Double> durationlist1) {
        this.durationlist = durationlist1;
    }

    public final HashMap<String, Double> getRatingslist() {
        return ratingslist;
    }

    public final void setRatingslist(final HashMap<String, Double> ratingslist1) {
        this.ratingslist = ratingslist1;
    }

    public SeriesDB(final ArrayList<Series> serieslist1) {
        this.serieslist = serieslist1;
    }

    public final ArrayList<Series> getSerieslist() {
        return serieslist;
    }

    public final HashMap<String, Integer> getFavoritetitles() {
        return favoritetitles;
    }

    public final void setFavoritetitles(final HashMap<String, Integer> favoritetitles1) {
        this.favoritetitles = favoritetitles1;
    }

    /**
     * @param datalist a list of series input
     *     creating my own database
     */
    public void copy(final List<SerialInputData> datalist) {
        Series bufferseries;
        for (SerialInputData seriesx : datalist) {
            bufferseries = new Series(seriesx);
            serieslist.add(bufferseries);
        }
        this.favoritetitles = new HashMap<>();
        this.ratingslist = new HashMap<>();
        this.durationlist = new HashMap<>();
        this.viewslist = new HashMap<>();
    }

    /**
     * @param series the series database
     * @param title the title of the series
     * @return the searched series, or null if it's not found
     */
    public final Series search(final SeriesDB series, final String title) {
        for (Series x : series.getSerieslist()) {
            if (x.getTitle().equals(title)) {
                return x;
            }
        }
        return null;
    }

    /**
     * @param seriex the series database
     * @param gen    the wanted genre
     * @param year   the year of apparition
     *               creates a hashmap of <Serie name><Serie average rating>
     *               only adds elements with the specificated year/genre
     */
    public final void copyratings(final SeriesDB seriex, final String gen, final String year) {
        seriex.getRatingslist().clear();
        for (Series series : seriex.getSerieslist()) {
            if ((gen == null || series.getGenres().contains(gen))
                    && (year == null || Integer.toString(series.getYear()).equals(year))) {
                if (series.gettruerating() != 0) {
                    seriex.getRatingslist().put(series.getTitle(), series.gettruerating());
                }
            }
        }
    }

    /**
     * @param series the series database
     *               sorts the ratings list created above
     */
    public final void sortratings(final SeriesDB series) {
        List<Double> mapvalues = new ArrayList<>(ratingslist.values());
        List<String> mapkeys = new ArrayList<>(ratingslist.keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (double val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                double comp1 = ratingslist.get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        series.setRatingslist(sortedMap);
    }

    /**
     * @param series the series database
     * @param gen    the wanted genre
     * @param year   the wanted year
     *               creates a hashmap of <serie name><serie duration>
     *               only adds elements with the speicified year/genre
     */
    public final void copyduration(final SeriesDB series, final String gen, final String year) {
        series.getDurationlist().clear();
        for (Series serie : series.getSerieslist()) {
            if ((year == null || Integer.toString(serie.getYear()).equals(year))
                    && (gen == null || serie.getGenres().contains(gen))) {
                series.getDurationlist().put(serie.getTitle(), serie.getMinutes(serie));
            }
        }

    }

    /**
     * @param series the series database
     *               sort the above created duration list
     */
    public final void sortduration(final SeriesDB series) {
        List<Double> mapvalues = new ArrayList<>(durationlist.values());
        List<String> mapkeys = new ArrayList<>(durationlist.keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (double val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                double comp1 = durationlist.get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        series.setDurationlist(sortedMap);
    }

    /**
     * @param users  the users database
     * @param series the series database
     * @param gen    the wanted genre
     * @param year   the year of apparition wanted
     *               <p>
     *               creates a hashmap of <Serie title><number of views>
     */
    public final void copyviews(final UserDB users, final SeriesDB series,
                                 final String gen, final String year) {
        series.getViewslist().clear();
        for (Series serie : series.getSerieslist()) {
            if ((gen == null || serie.getGenres().contains(gen))
                    && (year == null || Integer.toString(serie.getYear()).equals(year))) {
                for (User user : users.getUserlist()) {
                    if (user.getHistory().containsKey(serie.getTitle())) {
                        if (!series.getViewslist().containsKey(serie.getTitle())) {
                            series.getViewslist().put(serie.getTitle(),
                                    user.getHistory().get(serie.getTitle()));
                        } else {
                            series.getViewslist().put(serie.getTitle(),
                                    series.getViewslist().get(serie.getTitle())
                                            + user.getHistory().get(serie.getTitle()));
                        }
                    }
                }
            }
        }
    }

    /**
     * @param series series database
     *               sorts the above mentioned hashmap
     */
    public final void sortviewslist(final SeriesDB series) {
        List<Integer> mapvalues = new ArrayList<>(series.getViewslist().values());
        List<String> mapkeys = new ArrayList<>(series.getViewslist().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();
            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = series.getViewslist().get(cheie);
                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }
            }
        }
        series.setViewslist(sortedMap);
    }

    /**
     * @param series series database
     * @param users  users database
     * @param gen    the genre wanted
     * @param year   the wanted year of apparition
     *               creates a hashmap of
     *               <show name><how often it is met in the users favourite list>
     */
    public final void copyfavoritestitle(final SeriesDB series, final UserDB users,
                                         final String gen, final String year) {
        series.getFavoritetitles().clear();
        for (Series serie : series.getSerieslist()) {
            for (User user : users.getUserlist()) {
                if ((gen == null || serie.getGenres().contains(gen))
                        &&
                        (year == null || Integer.toString(serie.getYear()).equals(year))) {
                    if (user.getFavoriteMovies().contains(serie.getTitle())
                            &&
                            !series.getFavoritetitles().containsKey(serie.getTitle())) {
                        series.getFavoritetitles().put(serie.getTitle(), 1);
                    } else if (user.getFavoriteMovies().contains(serie.getTitle())
                            &&
                            series.getFavoritetitles().containsKey(serie.getTitle())) {
                        series.getFavoritetitles().put(serie.getTitle(),
                                series.getFavoritetitles().get(serie.getTitle()) + 1);
                    }
                }
            }
        }
    }

    /**
     * @param series series database
     *               sorts the above mentioned database
     */
    public final void sortfavoritetitles(final SeriesDB series) {
        List<Integer> mapvalues = new ArrayList<>(series.getFavoritetitles().values());
        List<String> mapkeys = new ArrayList<>(series.getFavoritetitles().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();
            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = series.getFavoritetitles().get(cheie);
                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }
            }
        }
        series.setFavoritetitles(sortedMap);
    }
}
