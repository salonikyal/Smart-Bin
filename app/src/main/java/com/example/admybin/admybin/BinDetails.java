package com.example.admybin.admybin;

public class BinDetails {

    private int People;
    private int Level;

    private String bincode;


    public BinDetails() {
    }

    public BinDetails(int People, int Level, String bincode) {

        this.People = People;
        this.Level = Level;

        this.bincode = bincode;
    }


    public int getPeople() {
        return People;
    }

    public void setPeople(int people) {
        People = people;
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        Level = level;
    }


    public String getBincode() {
        return bincode;
    }

    public void setBincode(String bincode) {
        this.bincode = bincode;
    }
}
