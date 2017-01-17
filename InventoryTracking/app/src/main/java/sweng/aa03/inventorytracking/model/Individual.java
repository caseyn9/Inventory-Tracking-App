package sweng.aa03.inventorytracking.model;

import java.util.ArrayList;

/**
 * Created by eganl on 29/11/2016.
 */

public class Individual {

    public String name;
//    public ArrayList<Project> projects;
//    public ArrayList<Equipment> equipment;
    private String key;

    public Individual() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Individual(String name) {
        this.name = name;
    }

    public void addKey(String key) { this.key = key; }

//    public ArrayList<Project> getProjects(){ return this.projects; }
//
//    public ArrayList<Equipment> getEquipment(){ return equipment; }
//
//    public void addProject (Project project){
//        projects.add(project);
//    }
//
//    public void addEquipment (Equipment e) {
//        equipment.add(e);
//    }

    public String getKey() { return  this.key; }

}