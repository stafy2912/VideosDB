package commands;

import fileio.ActionInputData;

import java.util.ArrayList;
import java.util.List;

public class comenzi {

    private final int actionId;
    private final String actionType;
    private final String type;
    private final String username;
    private final String objectType;
    private final String sortType;
    private final String criteria;
    private final String title;
    private final String genre;
    private final int number;
    private final double grade;
    private final int seasonNumber;
    private final List<List<String>> filters;
    private final List<String> words;
    private final List<String> awards;

    public final int getActionId() {
        return actionId;
    }

    public final String getActionType() {
        return actionType;
    }

    public final String getType() {
        return type;
    }

    public final String getUsername() {
        return username;
    }

    public final String getSortType() {
        return sortType;
    }

    public final String getCriteria() {
        return criteria;
    }

    public final String getTitle() {
        return title;
    }

    public final String getGenre() {
        return genre;
    }

    public final int getNumber() {
        return number;
    }

    public final double getGrade() {
        return grade;
    }

    public final int getSeasonNumber() {
        return seasonNumber;
    }

    public final List<List<String>> getFilters() {
        return filters;
    }

    public comenzi(ActionInputData data) {
        actionId = data.getActionId();
        actionType = data.getActionType();
        objectType = data.getObjectType();
        sortType = data.getType();
        criteria = data.getCriteria();
        number = data.getNumber();
        filters = data.getFilters();
        title = data.getTitle();
        type = data.getType();
        username = data.getUsername();
        genre = data.getGenre();
        grade = data.getGrade();
        seasonNumber = data.getSeasonNumber();
        awards = new ArrayList<>();
        words = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "comenzi{" +
                "actionId=" + actionId +
                ", actionType='" + actionType + '\'' +
                ", type='" + type + '\'' +
                ", username='" + username + '\'' +
                ", objectType='" + objectType + '\'' +
                ", sortType='" + sortType + '\'' +
                ", criteria='" + criteria + '\'' +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", number=" + number +
                ", grade=" + grade +
                ", seasonNumber=" + seasonNumber +
                ", filters=" + filters +
                ", words=" + words +
                ", awards=" + awards +
                '}';
    }


}
