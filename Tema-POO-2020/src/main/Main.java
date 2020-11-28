package main;

import actor.Actor;
import actor.ActorDB;
import checker.Checkstyle;
import checker.Checker;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implementation.
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
        ArrayList<User> userlist = new ArrayList<>();
        UserDB users = new UserDB(userlist);
        users.copy(input.getUsers());
        users.findfavmovies(users, input.getMovies());
        users.sortFavourite(users);

        ArrayList<Movie> movielist = new ArrayList<>();
        MovieDB movies = new MovieDB(movielist);
        movies.copy(input.getMovies());

        ArrayList<Series> serieslist = new ArrayList<>();
        SeriesDB series = new SeriesDB(serieslist);
        series.copy(input.getSerials());

        ArrayList<ActionInputData> actionlist = new ArrayList<>();
        CommandsDB comm = new CommandsDB(actionlist);
        comm.copy(input.getCommands());

        ArrayList<Actor> actorlist = new ArrayList<>();
        HashMap<String, Double> actorrating = new HashMap<>();
        ActorDB actors = new ActorDB(actorlist, actorrating);
        actors.copy(input.getActors());

        int count = 0;
        User wanteduser;
        for (ActionInputData x : comm.getComlist()) {
            message.setLength(0);

            if ("command".equals(x.getActionType())) {
                if ("view".equals(x.getType())) {
                    wanteduser = users.search(x.getUsername());
                    wanteduser.view(x.getTitle());
                    message.append("success -> ");
                    message.append(x.getTitle());
                    message.append(" was viewed with total views of ");
                    message.append(wanteduser.getHistory().get(x.getTitle()));
                    //noinspection unchecked
                    arrayResult.add(fileWriter.writeFile(comm.getComlist().get(count).getActionId(),
                            "", message.toString()));
                } else if ("favorite".equals(x.getType())) {
                    wanteduser = users.search(x.getUsername());
                    message.append(wanteduser.favorites(x.getTitle(), wanteduser));
                    //noinspection unchecked
                    arrayResult.add(fileWriter.writeFile(comm.getComlist().get(count).getActionId(),
                            "", message.toString()));
                } else if ("rating".equals(x.getType())) {
                    wanteduser = users.search(x.getUsername());
                    message.append(wanteduser.rating(wanteduser, x.getGrade(), x.getTitle(),
                            x.getSeasonNumber(), movies));
                    //noinspection unchecked
                    arrayResult.add(fileWriter.writeFile(comm.getComlist().get(count).getActionId(),
                            "", message.toString()));
                    if (message.toString().contains("success")) {
                        Movie dummymovie = movies.search(movies, x.getTitle());
                        if (dummymovie != null) {
                            dummymovie.setRating(dummymovie.getRating() + x.getGrade());
                            dummymovie.setCounter(dummymovie.getCounter() + 1);
                        }
                        Series dummyseries = series.search(series, x.getTitle());
                        if (dummyseries != null) {
                            dummyseries.getSeasons().get(x.getSeasonNumber() - 1)
                                    .getRatings().add(x.getGrade());
                        }
                    }
                }
            } else if ("query".equals(x.getActionType())) {
                if (x.getObjectType().equals("actors")) {
                    if (x.getCriteria().equals("filter_description")) {
                        message.append("Query result: [");
                        actors.copynames(x.getFilters().get(2));
                        int number = Math.min(actors.getActornames().size(), x.getNumber());
                        Collections.sort(actors.getActornames());
                        if (x.getSortType().equals("asc")) {
                            for (String name : actors.getActornames()) {
                                message.append(name);
                                message.append(", ");
                            }
                        } else {
                            for (int i = actors.getActornames().size() - 1;
                                 i >= (actors.getActornames().size() - number); i--) {
                                message.append(actors.getActornames().get(i));
                                message.append(", ");
                            }
                        }
                        if (!message.toString().equals("Query result: [")) {
                            message.setLength(message.length() - 2);
                        }
                        message.append("]");
                        //noinspection unchecked
                        arrayResult.add(fileWriter.writeFile(
                                comm.getComlist().get(count).getActionId(),
                                "", message.toString()));

                    }
                    if (x.getCriteria().equals("average")) {
                        message.append("Query result: [");
                        actors.restart(actors);
                        for (Movie movie : movies.getMovielist()) {
                            if (movie.getCounter() != 0) {
                                for (String name : movie.getCast()) {
                                    Actor dummyactor = actors.search(name);
                                    if (dummyactor != null) {
                                        dummyactor.setCounter(dummyactor.getCounter() + 1);
                                        dummyactor.setRating(dummyactor.getRating()
                                                + movie.gettruerating());
                                    }
                                }
                            }
                        }

                        for (Series serie : series.getSerieslist()) {
                            if (serie.gettruerating() != 0.0) {
                                for (String name : serie.getCast()) {
                                    Actor dummyactor = actors.search(name);
                                    if (dummyactor != null) {
                                        dummyactor.setCounter(dummyactor.getCounter() + 1);
                                        dummyactor.setRating(dummyactor.getRating()
                                                + serie.gettruerating());
                                    }
                                }
                            }
                        }
                        actors.copyratings();
                        actors.sortRatings(actors);
                        ArrayList<String> keys = new ArrayList<>(actors.getActorrating().keySet());
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
                        //noinspection unchecked
                        arrayResult.add(fileWriter
                                .writeFile(comm.getComlist().get(count).getActionId(),
                                        "", message.toString()));
                    }
                    if (x.getCriteria().equals("awards")) {
                        message.append("Query result: [");
                        final int awardfilternumber = 3;
                        List<String> prizes = x.getFilters().get(awardfilternumber);
                        actors.copyawardactors(actors, prizes);
                        actors.sortAwardsnumber(actors);
                        List<String> keys = new ArrayList<>(actors.getactorawardsnmb().keySet());
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
                        //noinspection unchecked
                        arrayResult.add(fileWriter.writeFile(comm.getComlist()
                                .get(count).getActionId(), "", message.toString()));
                    }
                }
                if (!"movies".equals(x.getObjectType())) {
                    if ("shows".equals(x.getObjectType())) {
                        if ("ratings".equals(x.getCriteria())) {
                            message.append("Query result: [");
                            String year = x.getFilters().get(0).get(0);
                            String genr = x.getFilters().get(1).get(0);
                            series.copyratings(series, genr, year);
                            series.sortratings(series);
                            ArrayList<String> keys = new ArrayList<>(
                                    series.getRatingslist().keySet());
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
                            //noinspection unchecked
                            arrayResult.add(fileWriter
                                    .writeFile(comm.getComlist().get(count).getActionId(),
                                            "", message.toString()));
                        } else if ("longest".equals(x.getCriteria())) {
                            message.append("Query result: [");
                            String year = x.getFilters().get(0).get(0);
                            String genr = x.getFilters().get(1).get(0);
                            series.copyduration(series, genr, year);
                            if (series.getDurationlist().size() != 0) {
                                series.sortduration(series);
                                ArrayList<String> keys1 = new ArrayList<>(
                                        series.getDurationlist().keySet());
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
                            //noinspection unchecked
                            arrayResult.add(fileWriter
                                    .writeFile(comm.getComlist().get(count).getActionId(),
                                            "", message.toString()));
                        } else if ("most_viewed".equals(x.getCriteria())) {
                            message.append("Query result: [");
                            String year = x.getFilters().get(0).get(0);
                            String genr = x.getFilters().get(1).get(0);
                            series.copyviews(users, series, genr, year);
                            series.sortviewslist(series);

                            ArrayList<String> keys = new ArrayList<>(
                                    series.getViewslist().keySet());
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
                            //noinspection unchecked
                            arrayResult.add(fileWriter
                                    .writeFile(comm.getComlist().get(count).getActionId(),
                                            "", message.toString()));
                            series.getFavoritetitles().clear();
                        } else if ("favorite".equals(x.getCriteria())) {
                            message.append("Query result: [");
                            String year = x.getFilters().get(0).get(0);
                            String genr = x.getFilters().get(1).get(0);
                            series.copyfavoritestitle(series, users, genr, year);
                            series.sortfavoritetitles(series);
                            ArrayList<String> keys = new ArrayList<>(
                                    series.getFavoritetitles().keySet());
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
                            //noinspection unchecked
                            arrayResult.add(fileWriter
                                    .writeFile(comm.getComlist().get(count).getActionId(),
                                            "", message.toString()));
                        }
                    } else if ("users".equals(x.getObjectType())) {
                        if (x.getCriteria().equals("num_ratings")) {
                            message.append("Query result: [");
                            users.copynumactions(users);
                            users.sortnoactions(users);
                            ArrayList<String> keys = new ArrayList<>(
                                    users.getActionslist().keySet());
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
                            //noinspection unchecked
                            arrayResult.add(fileWriter
                                    .writeFile(comm.getComlist().get(count).getActionId(),
                                            "", message.toString()));
                        }
                    }
                } else {
                    if ("ratings".equals(x.getCriteria())) {
                        message.append("Query result: [");
                        String year = x.getFilters().get(0).get(0);
                        String genr = x.getFilters().get(1).get(0);
                        movies.copyratings(movies, genr, year);
                        movies.sortratinglist(movies);
                        ArrayList<String> keys = new ArrayList<>(
                                movies.getRatinglist().keySet());
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
                        //noinspection unchecked
                        arrayResult.add(fileWriter.writeFile(comm.getComlist().get(count)
                                .getActionId(), "", message.toString()));
                    } else if ("longest".equals(x.getCriteria())) {
                        message.append("Query result: [");
                        String year = x.getFilters().get(0).get(0);
                        String genr = x.getFilters().get(1).get(0);
                        movies.copyduration(movies, genr, year);
                        movies.sortdurationlist(movies);
                        ArrayList<String> keys = new ArrayList<>(movies.getDurationlist()
                                .keySet());
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
                        //noinspection unchecked
                        arrayResult.add(fileWriter.writeFile(
                                comm.getComlist().get(count).getActionId(),
                                "", message.toString()));
                    } else if ("most_viewed".equals(x.getCriteria())) {
                        message.append("Query result: [");
                        String year = x.getFilters().get(0).get(0);
                        String genr = x.getFilters().get(1).get(0);
                        movies.copyviews(users, movies, genr, year);
                        movies.sortViewslist(movies);
                        ArrayList<String> keys = new ArrayList<>(movies.getViewslist().keySet());
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
                        //noinspection unchecked
                        arrayResult.add(fileWriter.writeFile(
                                comm.getComlist().get(count).getActionId(),
                                "", message.toString()));
                    } else if ("favorite".equals(x.getCriteria())) {
                        message.append("Query result: [");
                        String year = x.getFilters().get(0).get(0);
                        String genr = x.getFilters().get(1).get(0);
                        movies.copyfavoritestitle(movies, users, genr, year);
                        movies.sortfavoritetitles(movies);
                        if (movies.getFavoritestitle().size() != 0) {
                            ArrayList<String> keys = new ArrayList<>(
                                    movies.getFavoritestitle().keySet());
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
                        //noinspection unchecked
                        arrayResult.add(fileWriter
                                .writeFile(comm.getComlist().get(count).getActionId(),
                                        "", message.toString()));
                    }
                }
            } else if ("recommendation".equals(x.getActionType())) {
                if ("standard".equals(x.getType())) {
                    message.append("StandardRecommendation");
                    User wanted = users.search(x.getUsername());
                    boolean ok = true;
                    for (Movie movie : movies.getMovielist()) {
                        if (ok && !wanted.getHistory().containsKey(movie.getTitle())) {
                            message.append(" result: ");
                            message.append(movie.getTitle());
                            ok = false;
                        }
                    }
                    for (Series seriesx : series.getSerieslist()) {
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
                    //noinspection unchecked
                    arrayResult.add(fileWriter.writeFile(comm.getComlist().get(count).getActionId(),
                            "", message.toString()));
                } else if ("best_unseen".equals(x.getType())) {
                    User wanted = users.search(x.getUsername());
                    wanted.copyshowsratings(movies, series, wanted);
                    wanted.sortshowsratings(wanted);
                    if (wanted.getShowsratings().size() != 0) {
                        message.append("BestRatedUnseenRecommendation result: ");
                        ArrayList<String> keys = new ArrayList<>(wanted.getShowsratings().keySet());
                        message.append(keys.get(keys.size() - 1));
                        //noinspection unchecked
                        arrayResult.add(fileWriter
                                .writeFile(comm.getComlist().get(count).getActionId(),
                                        "", message.toString()));
                    } else {
                        boolean ok = false;
                        for (Movie movie : movies.getMovielist()) {
                            if (!ok && !wanted.getHistory().containsKey(movie.getTitle())) {
                                message.append("BestRatedUnseenRecommendation result: ");
                                message.append(movie.getTitle());
                                //noinspection unchecked
                                arrayResult.add(fileWriter
                                        .writeFile(comm.getComlist().get(count).getActionId(),
                                                "", message.toString()));
                                ok = true;
                            }
                        }
                        for (Series serie : series.getSerieslist()) {
                            if (!ok && !wanted.getHistory().containsKey(serie.getTitle())) {
                                message.append("BestRatedUnseenRecommendation result: ");
                                message.append(serie.getTitle());
                                //noinspection unchecked
                                arrayResult.add(fileWriter
                                        .writeFile(comm.getComlist().get(count).getActionId(),
                                                "", message.toString()));
                                ok = true;
                            }
                        }
                        if (!ok) {
                            message.append("BestRatedUnseenRecommendation cannot be applied!");
                            //noinspection unchecked
                            arrayResult.add(fileWriter
                                    .writeFile(comm.getComlist().get(count).getActionId(),
                                            "", message.toString()));

                        }
                    }
                } else if ("popular".equals(x.getType())) {
                    User wanted = users.search(x.getUsername());
                    if (!wanted.getSubscriptionType().equals("PREMIUM")) {
                        message.append("PopularRecommendation cannot be applied!");
                        //noinspection unchecked
                        arrayResult.add(fileWriter
                                .writeFile(comm.getComlist().get(count).getActionId(),
                                        "", message.toString()));
                    } else {
                        users.copygenrelist(movies, series);
                        users.sortgenreslist(users);
                        ArrayList<String> keys = new ArrayList<>(users.getGenrelist().keySet());
                        if (keys.size() == 0) {
                            message.append("PopularRecommendation cannot be applied!");
                            //noinspection unchecked
                            arrayResult.add(fileWriter
                                    .writeFile(comm.getComlist().get(count).getActionId(),
                                            "", message.toString()));
                        } else {
                            boolean ok = true;
                            for (int j = keys.size() - 1; j >= 0; j--) {
                                for (Movie movie : movies.getMovielist()) {
                                    if (ok && !wanted.getHistory().containsKey(movie.getTitle())
                                            && movie.getGenres().contains(keys.get(j))) {
                                        message.append("PopularRecommendation result: ");
                                        message.append(movie.getTitle());
                                        //noinspection unchecked
                                        arrayResult.add(fileWriter.writeFile(comm.getComlist()
                                                .get(count).getActionId(), "", message.toString()));
                                        ok = false;
                                    }
                                }
                                if (ok) {
                                    for (Series serie : series.getSerieslist()) {
                                        if (ok && !wanted.getHistory().containsKey(serie.getTitle())
                                                && serie.getGenres().contains(keys.get(j))) {
                                            message.append("PopularRecommendation result: ");
                                            message.append(serie.getTitle());
                                            //noinspection unchecked
                                            arrayResult.add(fileWriter
                                                    .writeFile(comm.getComlist().get(count)
                                                            .getActionId(), "", message.toString()));
                                            ok = false;
                                        }
                                    }
                                }
                            }
                            if (ok) {
                                message.setLength(0);
                                message.append("PopularRecommendation cannot be applied!");
                                //noinspection unchecked
                                arrayResult.add(fileWriter
                                        .writeFile(comm.getComlist().get(count).getActionId(),
                                                "", message.toString()));
                            }
                        }
                    }
                } else if ("favorite".equals(x.getType())) {
                    User wanted = users.search(x.getUsername());
                    if (!wanted.getSubscriptionType().equals("PREMIUM")) {
                        message.append("FavoriteRecommendation cannot be applied!");
                    } else {
                        users.copyfavorites(users, wanted);
                        users.sortfavorite(users);
                        ArrayList<String> keys = new ArrayList<>(users.getFavorites().keySet());
                        ArrayList<Integer> values = new ArrayList<>(users.getFavorites().values());
                        if (keys.size() == 0) {
                            message.append("FavoriteRecommendation cannot be applied!");
                        } else {
                            if (values.size() == 1) {
                                message.append("FavoriteRecommendation result: ");
                                message.append(keys.get(keys.size() - 1));
                            } else if (values.size() >= 2) {
                                if (!values.get(values.size() - 1)
                                        .equals(values.get(values.size() - 2))) {
                                    message.append("FavoriteRecommendation result: ");
                                    message.append(keys.get(keys.size() - 1));
                                } else {
                                    boolean ok = false;
                                    for (Movie movie : movies.getMovielist()) {
                                        if (!ok && movie.getnmbfavorites(users)
                                                == values.get(values.size() - 1)) {
                                            message.append("FavoriteRecommendation result: ");
                                            message.append(movie.getTitle());
                                            ok = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //noinspection unchecked
                    arrayResult.add(fileWriter.writeFile(comm.getComlist().get(count).
                            getActionId(), "", message.toString()));
                } else if ("search".equals(x.getType())) {
                    User wanted = users.search(x.getUsername());
                    if (!wanted.getSubscriptionType().equals("PREMIUM")) {
                        message.append("SearchRecommendation cannot be applied!");
                    } else {
                        message.append("SearchRecommendation result: [");
                        String gen = x.getGenre();
                        wanted.copyratingsgenre(wanted, movies, series, gen);
                        wanted.sortratingsgenre(wanted);
                        ArrayList<String> keys = new ArrayList<>(wanted.getRatingsgenre().keySet());
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
                    //noinspection unchecked
                    arrayResult.add(fileWriter.writeFile(comm.getComlist().get(count).
                            getActionId(), "", message.toString()));
                }
            }
            count++;
        }

        fileWriter.closeJSON(arrayResult);
    }

}
