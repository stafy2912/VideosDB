package actor;

import fileio.ActorInputData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActorDB {

    private ArrayList<Actor> actor_list;

    private ArrayList<String> actor_names;

    private HashMap<String, Double> actor_rating;

    private HashMap<String, Integer> actor_awardsnmb;

    public HashMap<String, Integer> getActor_awardsnmb() {
        return actor_awardsnmb;
    }

    public final void setActor_awardsnmb(HashMap<String, Integer> actor_awardsnmb) {
        this.actor_awardsnmb = actor_awardsnmb;
    }

    public ActorDB(ArrayList<Actor> actor_list, HashMap<String, Double> map) {
        this.actor_list = actor_list;
        this.actor_rating = map;
        this.actor_awardsnmb = new HashMap<>();
        this.actor_names = new ArrayList<>();
    }

    public final ArrayList<String> getActor_names() {
        return actor_names;
    }

    public final void setActor_names(ArrayList<String> actor_names) {
        this.actor_names = actor_names;
    }

    public final ArrayList<Actor> getActor_list() {
        return actor_list;
    }

    public final void setActor_list(ArrayList<Actor> actor_list) {
        this.actor_list = actor_list;
    }

    public final HashMap<String, Double> getActor_rating() {
        return actor_rating;
    }

    public final void setActor_rating(HashMap<String, Double> actor_rating) {
        this.actor_rating = actor_rating;
    }

    public final void copy(List<ActorInputData> data_list) {
        Actor buffer_actor;
        for (ActorInputData actorx : data_list) {
            buffer_actor = new Actor(actorx);
            actor_list.add(buffer_actor);
        }
    }

    public final Actor search(String Title) {
        Actor result = null;
        for (Actor actorx : actor_list) {
            if (actorx.getName().equals(Title)) {
                result = actorx;
                break;
            }
        }
        return result;
    }

    public final void copy_ratings() {
        actor_rating.clear();
        for (Actor x : actor_list) {
            if (x.getCounter() != 0) {
                actor_rating.put(x.getName(), x.getRating() / x.getCounter());
            }
        }
    }

    public final void copy_names(List<String> words) {
        actor_names.clear();
        for (Actor actor : actor_list) {
            boolean ok = true;
            for (String word : words) {
                Pattern pattern = Pattern.compile("[ ,!.'()-]" + word + "[ ,!.'()-]", Pattern.CASE_INSENSITIVE);
                Matcher match = pattern.matcher(actor.getCareerDescription());
                if (!match.find()) {
                    ok = false;
                }
            }
            if (ok) {
                actor_names.add(actor.getName());
            }
        }
    }

    public final void restart(ActorDB actors) {
        for (Actor actor : actors.getActor_list()) {
            actor.setCounter(0);
            actor.setRating(0.0d);
        }
    }

    public final void sortRatings(ActorDB actors) {
        List<Double> mapvalues = new ArrayList<>(actor_rating.values());
        List<String> mapkeys = new ArrayList<>(actor_rating.keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (double val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                double comp1 = actor_rating.get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        actors.setActor_rating(sortedMap);
    }

    public final void copy_awdactorss(ActorDB actors, List<String> data) {
        actors.getActor_awardsnmb().clear();
        for (Actor actor : actors.getActor_list()) {
            if (actor.check_awards(actor, data)) {
                actors.getActor_awardsnmb().put(actor.getName(), actor.awards_number(actor));
            }
        }
    }

    public final void sort_awardsnumber(ActorDB actors) {
        List<Integer> mapvalues = new ArrayList<>(actors.getActor_awardsnmb().values());
        List<String> mapkeys = new ArrayList<>(actors.getActor_awardsnmb().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = actors.getActor_awardsnmb().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        actors.setActor_awardsnmb(sortedMap);
    }

}
