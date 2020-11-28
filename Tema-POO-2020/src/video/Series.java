package video;

import entertainment.Season;
import fileio.SerialInputData;
import fileio.ShowInput;

import java.util.ArrayList;

public class Series extends Video {


    private ArrayList<Season> seasons;

    public Series(final ShowInput data) {
        super(data);
    }

    public Series(final SerialInputData data) {
        this.title = data.getTitle();
        this.year = data.getYear();
        this.cast = data.getCast();
        this.genres = data.getGenres();
        this.seasons = data.getSeasons();
    }

    public final ArrayList<Season> getSeasons() {
        return seasons;
    }

    public final void setSeasons(final ArrayList<Season> seasons) {
        this.seasons = seasons;
    }

    /**
     * @return the average rating of a series
     */
    public final double gettruerating() {
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

    /**
     * @param serie the wanted series
     * @return the duration of the wanted series
     */
    public final Double getMinutes(final Series serie) {
        double countdur = 0.0;
        for (Season season : serie.getSeasons()) {
            countdur += season.getDuration();
        }
        return countdur;
    }


}
