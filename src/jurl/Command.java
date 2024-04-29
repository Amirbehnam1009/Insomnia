package jurl;

import httpclient.entity.Request;

import java.util.Set;

/**
 * Holds a command data including request
 */
public class Command {
    /**
     * request of the command
     */
    private Request request;
    /**
     * determines help command
     */
    private boolean help;
    /**
     * determines save option in command
     */
    private boolean save;
    /**
     * group name of command
     */
    private String groupName = "";
    /**
     * determines create group command
     */
    private boolean isCreateGroup;
    /**
     * determines list groups command
     */
    private boolean isGroupList;
    /**
     * determines list requests of a group command
     */
    private boolean isList;
    /**
     * determines fire command
     */
    private boolean isFire;
    /**
     * fire request indexes in fire command
     */
    private Set<Integer> fireRequestIndexList;

    /**
     * Gets request of the command.
     *
     * @return request of the command
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Sets request of the command.
     *
     * @param request request of the command
     */
    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * Gets help command state.
     *
     * @return help command state
     */
    public boolean isHelp() {
        return help;
    }

    /**
     * Sets help command state.
     *
     * @param help help command state
     */
    public void setHelp(boolean help) {
        this.help = help;
    }

    /**
     * Gets save option in command.
     *
     * @return save option in command
     */
    public boolean isSave() {
        return save;
    }

    /**
     * Sets save option in command.
     *
     * @param save save option in command
     */
    public void setSave(boolean save) {
        this.save = save;
    }

    /**
     * Gets group name of command.
     *
     * @return group name of command
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets group name of command.
     *
     * @param groupName group name of command
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Gets create group command state.
     *
     * @return create group command state
     */
    public boolean isCreateGroup() {
        return isCreateGroup;
    }

    /**
     * Sets create group command state.
     *
     * @param createGroup create group command state
     */
    public void setCreateGroup(boolean createGroup) {
        isCreateGroup = createGroup;
    }

    /**
     * Gets list groups command state.
     *
     * @return list groups command state.
     */
    public boolean isGroupList() {
        return isGroupList;
    }

    /**
     * Sets list groups command state.
     *
     * @param groupList list groups command state.
     */
    public void setGroupList(boolean groupList) {
        isGroupList = groupList;
    }

    /**
     * Gets list requests of a group command state.
     *
     * @return list requests of a group command state
     */
    public boolean isList() {
        return isList;
    }

    /**
     * Sets list requests of a group command state.
     *
     * @param list list requests of a group command state
     */
    public void setList(boolean list) {
        isList = list;
    }

    /**
     * Gets fire command state.
     *
     * @return fire command state
     */
    public boolean isFire() {
        return isFire;
    }

    /**
     * Sets fire command state.
     *
     * @param fire fire command state
     */
    public void setFire(boolean fire) {
        isFire = fire;
    }

    /**
     * Gets fire request indexes.
     *
     * @return fire request indexes
     */
    public Set<Integer> getFireRequestIndexList() {
        return fireRequestIndexList;
    }

    /**
     * Sets fire request indexes.
     *
     * @param fireRequestIndexList fire request indexes
     */
    public void setFireRequestIndexList(Set<Integer> fireRequestIndexList) {
        this.fireRequestIndexList = fireRequestIndexList;
    }
}
