package sweng.aa03.inventorytracking.model;

/**
 * Created by eganl on 29/11/2016.
 */

public class Equipment {
    public String name;
    public String barcode;
    public String individualID;
    public String projectID;
    public long returnBy;
    public boolean damaged;
    private String key;

    public Equipment() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Equipment(String name,String barcode, String individualID, String projectID) {
        this.name = name;
        this.barcode = barcode;
        this.individualID = individualID;
        this.projectID = projectID;
        this.returnBy = -1;
        damaged = false;
    }

    public void addKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
