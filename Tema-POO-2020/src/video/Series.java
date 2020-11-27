package video;

import entertainment.Season;
import fileio.SerialInputData;
import fileio.ShowInput;

import java.util.ArrayList;

public class Series extends Video {

    private String title;
    private int year;
    private ArrayList<String> cast;
    private ArrayList<String> genres;
    private int numberOfSeasons;
    private ArrayList<Season> seasons;

    public Series(ShowInput data) {
        super(data);
    }

    public Series(SerialInputData data) {
        title = data.getTitle();
        year = data.getYear();
        cast = data.getCast();
        genres = data.getGenres();
        numberOfSeasons = data.getNumberSeason();
        seasons = data.getSeasons();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public ArrayList<String> getCast() {
        return cast;
    }

    @Override
    public void setCast(ArrayList<String> cast) {
        this.cast = cast;
    }

    @Override
    public ArrayList<String> getGenres() {
        return genres;
    }

    @Override
    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(ArrayList<Season> seasons) {
        this.seasons = seasons;
    }

    public double gettruerating() {
        double sum2 = 0.0;
        for (Season seasonx : seasons) {
            double sum = 0.0;
            for (Double grade : seasonx.getRatings()) {
                sum += grade;
            }
            if (sum != 0) {
                sum = sum / (double) seasonx.getRatings().size();
                sum2 += sum;
            } else {
                sum2 += 0;
            }
        }
        return sum2 / (double) seasons.size();
    }

    public Double getMinutes(Series serie) {
        double count_dur = 0.0;
        for (Season season : serie.getSeasons()) {
            count_dur += season.getDuration();
        }
        return count_dur;
    }

    @Override
    public String toString() {
        return "Series{" +
                "title='" + title + '\'' +
                ", year=" + year +
                ", cast=" + cast +
                ", genres=" + genres +
                ", numberOfSeasons=" + numberOfSeasons +
                ", seasons=" + seasons +
                '}';
    }
}
