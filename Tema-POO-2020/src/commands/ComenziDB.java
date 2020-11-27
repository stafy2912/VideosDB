package commands;

import fileio.ActionInputData;

import java.util.List;

public class ComenziDB {

    public List<ActionInputData> com_list;

    public List<ActionInputData> getCom_list() {
        return com_list;
    }

    public final void setCom_list(List<ActionInputData> com_list) {
        this.com_list = com_list;
    }

    public ComenziDB(List<ActionInputData> com_list) {
        this.com_list = com_list;
    }

    public final void copy(List<ActionInputData> data_list) {
        com_list = data_list;
    }
}
