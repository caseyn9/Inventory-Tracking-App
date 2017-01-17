package sweng.aa03.inventorytracking.model;

import java.util.ArrayList;

/**
 * Created by eganl on 29/11/2016.
 */

public class Project {
    public String name;
    public String description;
    public String leaderName;
    public long endTime;
    public ArrayList<String> memberNames;
    private String key;

    public Project() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Project(String name, String description, String leaderName,long endTime, ArrayList<String> memberNames) {
        this.name = name;
        this.description = description;
        this.leaderName = leaderName;
        this.endTime = endTime;
        this. memberNames = memberNames;
    }

    public void addKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
