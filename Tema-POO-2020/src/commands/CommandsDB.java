package commands;

import fileio.ActionInputData;

import java.util.List;

public class CommandsDB {

    private List<ActionInputData> comlist;

    public final List<ActionInputData> getComlist() {
        return comlist;
    }

    public final void setComlist(final List<ActionInputData> comlist1) {
        this.comlist = comlist1;
    }

    public CommandsDB(final List<ActionInputData> comlist1) {
        this.comlist = comlist1;
    }

    /**
     *
     * @param datalist the input database of commands
     *                 creating my own data base
     */
    public final void copy(final List<ActionInputData> datalist) {
        comlist = datalist;
    }
}
