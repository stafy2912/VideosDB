package main;

import actor.Actor;
import actor.ActorDB;
import checker.Checkstyle;
import checker.Checker;
import commands.ComenziDB;
import common.Constants;
import fileio.ActionInputData;
import fileio.Input;
import fileio.InputLoader;
import fileio.Writer;
import org.json.simple.JSONArray;
import user.User;
import user.UserDB;
import video.Movie;
import video.MovieDB;
import video.Series;
import video.SeriesDB;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * Call the main checker and the coding style checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(Constants.TESTS_PATH);
        Path path = Paths.get(Constants.RESULT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        File outputDirectory = new File(Constants.RESULT_PATH);

        Checker checker = new Checker();
        checker.deleteFiles(outputDirectory.listFiles());

        for (File file : Objects.requireNonNull(directory.listFiles())) {

            String filepath = Constants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getAbsolutePath(), filepath);
            }
        }

        checker.iterateFiles(Constants.RESULT_PATH, Constants.REF_PATH, Constants.TESTS_PATH);
        Checkstyle test = new Checkstyle();
        test.testCheckstyle();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        InputLoader inputLoader = new InputLoader(filePath1);
        Input input = inputLoader.readData();

        Writer fileWriter = new Writer(filePath2);
        JSONArray arrayResult = new JSONArray();


        StringBuilder message = new StringBuilder();
        ArrayList<User> user_list = new ArrayList<>();
        UserDB users = new UserDB(user_list);
        users.copy(input.getUsers());
        users.find_favmovies(users, input.getMovies());
        users.sortFavourite(users);

        ArrayList<Movie> movie_list = new ArrayList<>();
        MovieDB movies = new MovieDB(movie_list);
        movies.copy(input.getMovies());

        ArrayList<Series> series_list = new ArrayList<>();
        SeriesDB series = new SeriesDB(series_list);
        series.copy(input.getSerials());

        ArrayList<ActionInputData> action_list = new ArrayList<>();
        ComenziDB comm = new ComenziDB(action_list);
        comm.copy(input.getCommands());

        ArrayList<Actor> actor_list = new ArrayList<>();
        HashMap<String, Double> actor_rating = new HashMap<>();
        ActorDB actors = new ActorDB(actor_list, actor_rating);
        actors.copy(input.getActors());

        int count = 0;
        User wanted_user;
        for (ActionInputData x : comm.getCom_list()) {
            message.setLength(0);

            switch (x.getActionType()) {
                case "command":
                    switch (x.getType()) {
                        case "view" -> {
                            wanted_user = users.search(x.getUsername());
                            wanted_user.View(x.getTitle());
                            message.append("success -> ");
                            message.append(x.getTitle());
                            message.append(" was viewed with total views of ");
                            message.append(wanted_user.getHistory().get(x.getTitle()));
                            arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                        }
                        case "favorite" -> {
                            wanted_user = users.search(x.getUsername());
                            message.append(wanted_user.Favorites(x.getTitle(), wanted_user));
                            arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                        }
                        case "rating" -> {
                            wanted_user = users.search(x.getUsername());
                            message.append(wanted_user.Rating(wanted_user, x.getGrade(), x.getTitle(), x.getSeasonNumber(), movies));
                            arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                            if (message.toString().contains("success")) {
                                Movie dummy_movie = movies.search(movies, x.getTitle());
                                if (dummy_movie != null) {
                                    dummy_movie.setRating(dummy_movie.getRating() + x.getGrade());
                                    dummy_movie.setCounter(dummy_movie.getCounter() + 1);
                                }
                                Series dummy_series = series.search(series, x.getTitle());
                                if (dummy_series != null) {
                                    dummy_series.getSeasons().get(x.getSeasonNumber() - 1).getRatings().add(x.getGrade());
                                }
                            }
                        }
                    }
                    break;
                case "query":
                    if (x.getObjectType().equals("actors")) {
                        if (x.getCriteria().equals("filter_description")) {
                            message.append("Query result: [");
                            actors.copy_names(x.getFilters().get(2));
                            int number = Math.min(actors.getActor_names().size(), x.getNumber());
                            Collections.sort(actors.getActor_names());
                            if (x.getSortType().equals("asc")) {
                                for (String name : actors.getActor_names()) {
                                    message.append(name);
                                    message.append(", ");
                                }
                            } else {
                                for (int i = actors.getActor_names().size() - 1; i >= (actors.getActor_names().size() - number); i--) {
                                    message.append(actors.getActor_names().get(i));
                                    message.append(", ");
                                }
                            }
                            if (!message.toString().equals("Query result: [")) {
                                message.setLength(message.length() - 2);
                            }
                            message.append("]");
                            arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));

                        }
                        if (x.getCriteria().equals("average")) {
                            message.append("Query result: [");
                            actors.restart(actors);
                            for (Movie movie : movies.getMovie_list()) {
                                if (movie.getCounter() != 0) {
                                    for (String name : movie.getCast()) {
                                        Actor actor_dummy = actors.search(name);
                                        if (actor_dummy != null) {
                                            actor_dummy.setCounter(actor_dummy.getCounter() + 1);
                                            actor_dummy.setRating(actor_dummy.getRating() + movie.gettruerating());
                                        }
                                    }
                                }
                            }

                            for (Series serie : series.getSeries_list()) {
                                if (serie.gettruerating() != 0.0) {
                                    for (String name : serie.getCast()) {
                                        Actor actor_dummy = actors.search(name);
                                        if (actor_dummy != null) {
                                            actor_dummy.setCounter(actor_dummy.getCounter() + 1);
                                            actor_dummy.setRating(actor_dummy.getRating() + serie.gettruerating());
                                        }
                                    }
                                }
                            }
                            actors.copy_ratings();
                            actors.sortRatings(actors);
                            ArrayList<String> keys = new ArrayList<>(actors.getActor_rating().keySet());
                            int number = Math.min(keys.size(), x.getNumber());
                            if (x.getSortType().equals("asc")) {
                                for (int i = 0; i < number; i++) {
                                    Object obj = keys.get(i);
                                    message.append(obj.toString());
                                    message.append(", ");
                                }
                            } else {
                                if (number == keys.size()) {
                                    for (int i = keys.size() - 1; i >= (keys.size() - number); i--) {
                                        Object obj = keys.get(i);
                                        message.append(obj.toString());
                                        message.append(", ");
                                    }
                                } else {
                                    int i = 0;
                                    while (i != number) {
                                        Object obj = keys.get(keys.size() - 1 - i);
                                        message.append(obj.toString());
                                        message.append(", ");
                                        i++;
                                    }
                                }
                            }
                            message.setLength(message.length() - 2);
                            message.append("]");
                            arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                        }
                        if (x.getCriteria().equals("awards")) {
                            message.append("Query result: [");
                            actors.copy_awdactorss(actors, x.getFilters().get(3));
                            actors.sort_awardsnumber(actors);
                            List<String> keys = new ArrayList<>(actors.getActor_awardsnmb().keySet());
                            int number = Math.min(x.getNumber(), keys.size());
                            if (x.getSortType().equals("asc")) {
                                for (int i = 0; i < number; i++) {
                                    message.append(keys.get(i));
                                    message.append(", ");
                                }
                            } else {
                                for (int i = keys.size() - 1; i >= (keys.size() - number); i--) {
                                    message.append(keys.get(i));
                                    message.append(", ");
                                }
                            }
                            if (!message.toString().equals("Query result: [")) {
                                message.setLength(message.length() - 2);
                            }
                            message.append("]");
                            arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                        }
                    }
                    switch (x.getObjectType()) {
                        case "movies":
                            switch (x.getCriteria()) {
                                case "ratings" -> {
                                    message.append("Query result: [");
                                    String year = x.getFilters().get(0).get(0);
                                    String genr = x.getFilters().get(1).get(0);
                                    movies.copy_ratings(movies, genr, year);
                                    movies.sortrating_list(movies);
                                    ArrayList<String> keys = new ArrayList<>(movies.getRating_list().keySet());
                                    int number = Math.min(keys.size(), x.getNumber());
                                    if (x.getSortType().equals("asc")) {
                                        for (int i = 0; i < number; i++) {
                                            Object obj = keys.get(i);
                                            message.append(obj.toString());
                                            message.append(", ");
                                        }
                                    } else {
                                        for (int i = number - 1; i == 0; i--) {
                                            Object obj = keys.get(i);
                                            message.append(obj.toString());
                                            message.append(", ");
                                        }
                                    }
                                    if (!message.toString().equals("Query result: [")) {
                                        message.setLength(message.length() - 2);
                                    }
                                    message.append("]");
                                    arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                                }
                                case "longest" -> {
                                    message.append("Query result: [");
                                    String year = x.getFilters().get(0).get(0);
                                    String genr = x.getFilters().get(1).get(0);
                                    movies.copy_duration(movies, genr, year);
                                    movies.sortduration_list(movies);
                                    ArrayList<String> keys = new ArrayList<>(movies.getDuration_list().keySet());
                                    int number = Math.min(keys.size(), x.getNumber());
                                    if (x.getSortType().equals("asc")) {
                                        for (int i = 0; i < number; i++) {
                                            Object obj = keys.get(i);
                                            message.append(obj.toString());
                                            message.append(", ");
                                        }
                                    } else {
                                        for (int i = keys.size() - 1; i >= (keys.size() - number); i--) {
                                            Object obj = keys.get(i);
                                            message.append(obj.toString());
                                            message.append(", ");
                                        }
                                    }
                                    if (!message.toString().equals("Query result: [")) {
                                        message.setLength(message.length() - 2);
                                    }
                                    message.append("]");
                                    arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                                }
                                case "most_viewed" -> {
                                    message.append("Query result: [");
                                    String year = x.getFilters().get(0).get(0);
                                    String genr = x.getFilters().get(1).get(0);
                                    movies.copy_views(users, movies, genr, year);
                                    movies.sortviews_list(movies);
                                    ArrayList<String> keys = new ArrayList<>(movies.getViews_list().keySet());
                                    int number = Math.min(keys.size(), x.getNumber());
                                    if (x.getSortType().equals("asc")) {
                                        for (int i = 0; i < number; i++) {
                                            Object obj = keys.get(i);
                                            message.append(obj.toString());
                                            message.append(", ");
                                        }
                                    } else {
                                        for (int i = keys.size() - 1; i >= (keys.size() - number); i--) {
                                            Object obj = keys.get(i);
                                            message.append(obj.toString());
                                            message.append(", ");
                                        }
                                    }
                                    message.setLength(message.length() - 2);
                                    message.append("]");
                                    arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                                }
                                case "favorite" -> {
                                    message.append("Query result: [");
                                    String year = x.getFilters().get(0).get(0);
                                    String genr = x.getFilters().get(1).get(0);
                                    movies.copy_favoritestitle(movies, users, genr, year);
                                    movies.sort_favoritetitles(movies);
                                    if (movies.getFavorites_title().size() != 0) {
                                        ArrayList<String> keys = new ArrayList<>(movies.getFavorites_title().keySet());
                                        if (x.getSortType().equals("asc")) {
                                            for (Object obj : keys) {
                                                message.append(obj.toString());
                                                message.append(", ");
                                            }
                                        } else {
                                            for (int i = keys.size() - 1; i >= 0; i--) {
                                                Object obj = keys.get(i);
                                                message.append(obj.toString());
                                                message.append(", ");
                                            }
                                        }
                                        message.setLength(message.length() - 2);
                                    }
                                    message.append("]");
                                    arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                                }
                            }
                            break;
                        case "shows":
                            switch (x.getCriteria()) {
                                case "ratings" -> {
                                    message.append("Query result: [");
                                    String year = x.getFilters().get(0).get(0);
                                    String genr = x.getFilters().get(1).get(0);
                                    series.copy_ratings(series, genr, year);
                                    series.sort_ratings(series);
                                    ArrayList<String> keys = new ArrayList<>(series.getRatings_list().keySet());
                                    int number = Math.min(keys.size(), x.getNumber());
                                    if (x.getSortType().equals("asc")) {
                                        for (int i = 0; i < number; i++) {
                                            Object obj = keys.get(i);
                                            message.append(obj.toString());
                                            message.append(", ");
                                        }
                                    } else {
                                        for (int i = keys.size() - 1; i >= 0; i--) {
                                            Object obj = keys.get(i);
                                            message.append(obj.toString());
                                            message.append(", ");
                                        }
                                    }
                                    if (!message.toString().equals("Query result: [")) {
                                        message.setLength(message.length() - 2);
                                    }
                                    message.append("]");
                                    arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                                }
                                case "longest" -> {
                                    message.append("Query result: [");
                                    String year = x.getFilters().get(0).get(0);
                                    String genr = x.getFilters().get(1).get(0);
                                    series.copy_duration(series, genr, year);
                                    if (series.getDuration_list().size() != 0) {
                                        series.sort_duration(series);
                                        ArrayList<String> keys1 = new ArrayList<>(series.getDuration_list().keySet());
                                        int number = Math.min(keys1.size(), x.getNumber());
                                        if (x.getSortType().equals("asc")) {
                                            for (int i = 0; i < number; i++) {
                                                Object obj = keys1.get(i);
                                                message.append(obj.toString());
                                                message.append(", ");
                                            }
                                        } else {
                                            for (int i = number - 1; i >= 0; i--) {
                                                Object obj = keys1.get(i);
                                                message.append(obj.toString());
                                                message.append(", ");
                                            }
                                        }
                                        if (!message.toString().equals("Query result: [")) {
                                            message.setLength(message.length() - 2);
                                        }
                                    }
                                    message.append("]");
                                    arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                                }
                                case "most_viewed" -> {
                                    message.append("Query result: [");
                                    String year = x.getFilters().get(0).get(0);
                                    String genr = x.getFilters().get(1).get(0);
                                    series.copy_views(users, series, genr, year);
                                    series.sortviews_list(series);

                                    ArrayList<String> keys = new ArrayList<>(series.getViews_list().keySet());
                                    int number = Math.min(x.getNumber(), keys.size());
                                    if (x.getSortType().equals("asc")) {
                                        for (int i = 0; i < number; i++) {
                                            message.append(keys.get(i));
                                            message.append(", ");
                                        }
                                    } else {
                                        for (int i = number - 1; i >= 0; i--) {
                                            message.append(keys.get(i));
                                            message.append(", ");
                                        }

                                    }
                                    if (!message.toString().equals("Query result: [")) {
                                        message.setLength(message.length() - 2);
                                    }
                                    message.append("]");
                                    arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                                    series.getFavorite_titles().clear();
                                }
                                case "favorite" -> {
                                    message.append("Query result: [");
                                    String year = x.getFilters().get(0).get(0);
                                    String genr = x.getFilters().get(1).get(0);
                                    series.copy_favoritestitle(series, users, genr, year);
                                    series.sort_favoritetitles(series);
                                    ArrayList<String> keys = new ArrayList<>(series.getFavorite_titles().keySet());
                                    if (keys.size() != 0) {
                                        int number = Math.min(x.getNumber(), keys.size());
                                        if (x.getSortType().equals("asc")) {
                                            for (int i = 0; i < number; i++) {
                                                Object obj = keys.get(i);
                                                message.append(obj.toString());
                                                message.append(", ");
                                            }
                                        } else {
                                            for (int i = number - 1; i >= 0; i--) {
                                                Object obj = keys.get(i);
                                                message.append(obj.toString());
                                                message.append(", ");
                                            }
                                        }
                                        message.setLength(message.length() - 2);
                                    }
                                    message.append("]");
                                    arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));

                                }
                            }
                            break;
                        case "users":
                            if (x.getCriteria().equals("num_ratings")) {
                                message.append("Query result: [");
                                users.copy_numactions(users);
                                users.sortnoactions(users);
                                ArrayList<String> keys = new ArrayList<>(users.getActions_list().keySet());
                                int number = Math.min(x.getNumber(), keys.size());
                                if (keys.size() != 0) {
                                    if (x.getSortType().equals("asc")) {
                                        for (int i = 0; i < number; i++) {
                                            message.append(keys.get(i));
                                            message.append(", ");
                                        }
                                    } else {
                                        int i = 0;
                                        while (i != number) {
                                            message.append(keys.get(keys.size() - 1 - i));
                                            message.append(", ");
                                            i++;
                                        }
                                    }
                                    message.setLength(message.length() - 2);
                                }
                                message.append("]");
                                arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                            }
                            break;
                    }
                    break;
                case "recommendation":
                    switch (x.getType()) {
                        case "standard" -> {
                            message.append("StandardRecommendation");
                            User wanted = users.search(x.getUsername());
                            boolean ok = true;
                            for (Movie movie : movies.getMovie_list()) {
                                if (ok && !wanted.getHistory().containsKey(movie.getTitle())) {
                                    message.append(" result: ");
                                    message.append(movie.getTitle());
                                    ok = false;
                                }
                            }
                            for (Series seriesx : series.getSeries_list()) {
                                if (ok && !wanted.getHistory().containsKey(seriesx.getTitle())) {
                                    message.append(" result: ");
                                    message.append(seriesx.getTitle());
                                    ok = false;
                                }
                            }
                            if (ok) {
                                message.setLength(0);
                                message.append("StandardRecommendation cannot be applied!");
                            }
                            arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));

                        }
                        case "best_unseen" -> {
                            User wanted = users.search(x.getUsername());
                            wanted.copy_showsratings(movies, series, wanted);
                            wanted.sort_showsratings(wanted);
                            if (wanted.getShows_ratings().size() != 0) {
                                message.append("BestRatedUnseenRecommendation result: ");
                                ArrayList<String> keys = new ArrayList<>(wanted.getShows_ratings().keySet());
                                message.append(keys.get(keys.size() - 1));
                                arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                            } else {
                                boolean ok = false;
                                for (Movie movie : movies.getMovie_list()) {
                                    if (!ok && !wanted.getHistory().containsKey(movie.getTitle())) {
                                        message.append("BestRatedUnseenRecommendation result: ");
                                        message.append(movie.getTitle());
                                        arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                                        ok = true;
                                    }
                                }
                                for (Series serie : series.getSeries_list()) {
                                    if (!ok && !wanted.getHistory().containsKey(serie.getTitle())) {
                                        message.append("BestRatedUnseenRecommendation result: ");
                                        message.append(serie.getTitle());
                                        arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                                        ok = true;
                                    }
                                }
                                if (!ok) {
                                    message.append("BestRatedUnseenRecommendation cannot be applied!");
                                    arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));

                                }
                            }
                        }
                        case "popular" -> {
                            User wanted = users.search(x.getUsername());
                            if (!wanted.getSubscriptionType().equals("PREMIUM")) {
                                message.append("PopularRecommendation cannot be applied!");
                                arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                            } else {
                                users.copy_genre_list(movies, series);
                                users.sort_genres_list(users);
                                ArrayList<String> keys = new ArrayList<>(users.getGenre_list().keySet());
                                if (keys.size() == 0) {
                                    message.append("PopularRecommendation cannot be applied!");
                                    arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                                } else {
                                    boolean ok = true;
                                    for (int j = keys.size() - 1; j >= 0; j--) {
                                        for (Movie movie : movies.getMovie_list()) {
                                            if (ok && !wanted.getHistory().containsKey(movie.getTitle()) && movie.getGenres().contains(keys.get(j))) {
                                                message.append("PopularRecommendation result: ");
                                                message.append(movie.getTitle());
                                                arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                                                ok = false;
                                            }
                                        }
                                        if (ok) {
                                            for (Series serie : series.getSeries_list()) {
                                                if (ok && !wanted.getHistory().containsKey(serie.getTitle()) && serie.getGenres().contains(keys.get(j))) {
                                                    message.append("PopularRecommendation result: ");
                                                    message.append(serie.getTitle());
                                                    arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                                                    ok = false;
                                                }
                                            }
                                        }
                                    }
                                    if (ok) {
                                        message.setLength(0);
                                        message.append("PopularRecommendation cannot be applied!");
                                        arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                                    }
                                }
                            }
                        }
                        case "favorite" -> {
                            User wanted = users.search(x.getUsername());
                            if (!wanted.getSubscriptionType().equals("PREMIUM")) {
                                message.append("FavoriteRecommendation cannot be applied!");
                            } else {
                                users.copy_favorites(users, wanted);
                                users.sort_favorit(users);
                                ArrayList<String> keys = new ArrayList<>(users.getFavorites().keySet());
                                ArrayList<Integer> values = new ArrayList<>(users.getFavorites().values());
                                if (keys.size() == 0) {
                                    message.append("FavoriteRecommendation cannot be applied!");
                                } else {
                                    if (values.size() == 1) {
                                        message.append("FavoriteRecommendation result: ");
                                        message.append(keys.get(keys.size() - 1));
                                    } else if (values.size() >= 2) {
                                        if (!values.get(values.size() - 1).equals(values.get(values.size() - 2))) {
                                            message.append("FavoriteRecommendation result: ");
                                            message.append(keys.get(keys.size() - 1));
                                        } else {
                                            boolean ok = false;
                                            for (Movie movie : movies.getMovie_list()) {
                                                if (!ok && movie.get_nofavorites(users) == values.get(values.size() - 1)) {
                                                    message.append("FavoriteRecommendation result: ");
                                                    message.append(movie.getTitle());
                                                    ok = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                        }
                        case "search" -> {
                            User wanted = users.search(x.getUsername());
                            if (!wanted.getSubscriptionType().equals("PREMIUM")) {
                                message.append("SearchRecommendation cannot be applied!");
                            } else {
                                message.append("SearchRecommendation result: [");
                                String gen = x.getGenre();
                                wanted.copy_ratingsgenre(wanted, movies, series, gen);
                                wanted.sort_ratingsgenre(wanted);
                                ArrayList<String> keys = new ArrayList<>(wanted.getRatings_genre().keySet());
                                if (keys.size() == 0) {
                                    message.setLength(0);
                                    message.append("SearchRecommendation cannot be applied!");
                                } else {
                                    for (Object obj : keys) {
                                        message.append(obj.toString());
                                        message.append(", ");
                                    }
                                    message.setLength(message.length() - 2);
                                    message.append("]");
                                }
                            }
                            arrayResult.add(fileWriter.writeFile(comm.getCom_list().get(count).getActionId(), "", message.toString()));
                        }
                    }
                    break;
            }
            count++;
        }

        fileWriter.closeJSON(arrayResult);
    }

}
