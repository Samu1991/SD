package common;

import java.util.ArrayList;
public class UserObj {
    private int healthNumber;
    private String name;
    private String password;
    private String county;
    private boolean healthState;
    private ArrayList<String> closeContacts;

    public  UserObj(){
    }

    public UserObj(int healthNumber, String password, String name, String county, boolean healthState, ArrayList<String> closeContacts) {
        this.healthNumber = healthNumber;
        this.password = password;
        this.name = name;
        this.county = county;
        this.healthState = healthState;
        this.closeContacts = closeContacts;
    }

    //region get-set

    public int getHealthNumber() {
        return healthNumber;
    }

    public void setHealthNumber(int healthNumber) {
        this.healthNumber = healthNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public boolean getHealthState() {
        return healthState;
    }

    public void setHealthState(boolean healthState) {
        this.healthState = healthState;
    }

    public ArrayList<String> getCloseContacts() {
        return closeContacts;
    }

    public void setCloseContacts(ArrayList<String> closeContacts) {
        this.closeContacts = closeContacts;
    }


    //endregion
}
