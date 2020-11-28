package actor;

import fileio.ActorInputData;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Actor {

    private final String name;
    private final String careerDescription;
    private final ArrayList<String> filmography;
    private final Map<ActorsAwards, Integer> awards;
    private Double rating;
    private Integer counter;

    public Actor(final ActorInputData actor) {
        this.name = actor.getName();
        this.careerDescription = actor.getCareerDescription();
        this.filmography = actor.getFilmography();
        this.awards = actor.getAwards();
        this.rating = 0.0d;
        this.counter = 0;
    }

    public String getName() {
        return name;
    }

    public String getCareerDescription() {
        return careerDescription;
    }

    public ArrayList<String> getFilmography() {
        return filmography;
    }

    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(final Double rating) {
        this.rating = rating;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(final Integer counter) {
        this.counter = counter;
    }

    /**
     * @param actor actor in the data base
     * @return number of awards
     */
    public int awardsnumber(final Actor actor) {
        List<Integer> vals = new ArrayList<>(actor.getAwards().values());
        int sum = 0;
        if (vals.size() != 0) {
            for (Object value : vals) {
                sum += (Integer) value;
            }
        }
        return sum;
    }

    /**
     * @param actor check the awards of this actpr
     * @param data  the awards as string list
     * @return true if the actor has all the awards or false if not
     */
    public boolean checkawards(final Actor actor, final List<String> data) {
        List<ActorsAwards> prizes = new ArrayList<>(actor.getAwards().keySet());
        int i = 0;
        int j = 0;
        while (i < prizes.size() && j < data.size()) {
            if (data.get(j).equals(prizes.get(i).toString())) {
                j++;
                i = 0;
            } else {
                i++;
            }
            if (j == data.size()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Actor{"
                + "name='" + name + '\''
                + ", careerDescription='" + careerDescription + '\''
                + ", filmography=" + filmography
                + ", awards=" + awards
                + ", rating=" + rating
                + ", counter=" + counter
                + '}';
    }
}
