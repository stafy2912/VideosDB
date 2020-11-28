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

    private final ArrayList<Actor> actorlist;

    private final ArrayList<String> actornames;

    private HashMap<String, Double> actorrating;

    private HashMap<String, Integer> actorawardsnmb;

    /**
     * @return a hashmap of <actor name><number of awards>
     */
    public HashMap<String, Integer> getactorawardsnmb() {
        return actorawardsnmb;
    }

    public ActorDB(final ArrayList<Actor> actorlist, final HashMap<String, Double> map) {
        this.actorlist = actorlist;
        this.actorrating = map;
        this.actorawardsnmb = new HashMap<>();
        this.actornames = new ArrayList<>();
    }


    public final ArrayList<Actor> getActorlist() {
        return actorlist;
    }

    public final ArrayList<String> getActornames() {
        return actornames;
    }

    public final HashMap<String, Double> getActorrating() {
        return actorrating;
    }

    public final void setActorrating(final HashMap<String, Double> actorrating) {
        this.actorrating = actorrating;
    }

    public final void setActorawardsnmb(final HashMap<String, Integer> actorawardsnmb) {
        this.actorawardsnmb = actorawardsnmb;
    }

    /**
     * @param datalist the input database for actors
     * copies the input actors to my own actors
     */
    public final void copy(final List<ActorInputData> datalist) {
        Actor bufferactor;
        for (ActorInputData actorx : datalist) {
            bufferactor = new Actor(actorx);
            actorlist.add(bufferactor);
        }
    }

    /**
     * @param title the name of the actor
     * @return the requested actor
     */
    public final Actor search(final String title) {
        Actor result = null;
        for (Actor actorx : actorlist) {
            if (actorx.getName().equals(title)) {
                result = actorx;
                break;
            }
        }
        return result;
    }

    /**
     * creates a rating list for actors
     */
    public final void copyratings() {
        actorrating.clear();
        for (Actor x : actorlist) {
            if (x.getCounter() != 0) {
                actorrating.put(x.getName(), x.getRating() / x.getCounter());
            }
        }
    }

    /**
     * @param words the words filter for the wanted actor
     *              searches for the actor that has that criteria
     */
    public final void copynames(final List<String> words) {
        actornames.clear();
        for (Actor actor : actorlist) {
            boolean ok = true;
            for (String word : words) {
                Pattern pattern = Pattern.compile("[ ,!.'()-]" + word + "[ ,!.'()-]",
                        Pattern.CASE_INSENSITIVE);
                Matcher match = pattern.matcher(actor.getCareerDescription());
                if (!match.find()) {
                    ok = false;
                }
            }
            if (ok) {
                actornames.add(actor.getName());
            }
        }
    }

    /**
     * @param actors reset the rating and counter
     */
    public final void restart(final ActorDB actors) {
        for (Actor actor : actors.getActorlist()) {
            actor.setCounter(0);
            actor.setRating(0.0d);
        }
    }

    /**
     * @param actors sorts all actors based on ratings
     */
    public final void sortRatings(final ActorDB actors) {
        List<Double> mapvalues = new ArrayList<>(actorrating.values());
        List<String> mapkeys = new ArrayList<>(actorrating.keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (double val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                double comp1 = actorrating.get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        actors.setActorrating(sortedMap);
    }

    /**
     * @param actors creates a list of actor names and their respective awards count
     * @param data   a list of awards
     */
    public final void copyawardactors(final ActorDB actors, final List<String> data) {
        actors.getactorawardsnmb().clear();
        for (Actor actor : actors.getActorlist()) {
            if (actor.checkawards(actor, data)) {
                actors.getactorawardsnmb().put(actor.getName(), actor.awardsnumber(actor));
            }
        }
    }

    /**
     * @param actors sorts actors based on number of awards
     */
    public final void sortAwardsnumber(final ActorDB actors) {
        List<Integer> mapvalues = new ArrayList<>(actors.getactorawardsnmb().values());
        List<String> mapkeys = new ArrayList<>(actors.getactorawardsnmb().keySet());
        Collections.sort(mapkeys);
        Collections.sort(mapvalues);
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (int val : mapvalues) {
            Iterator<String> key = mapkeys.iterator();

            while (key.hasNext()) {
                String cheie = key.next();
                int comp1 = actors.getactorawardsnmb().get(cheie);

                if (comp1 == val) {
                    key.remove();
                    sortedMap.put(cheie, val);
                    break;
                }

            }

        }
        actors.setActorawardsnmb(sortedMap);
    }

}
