package com.rsopher.fwtm;

/**
 * Created by revan on 3/8/15.
 */
public class Player {
    public int team;
    public String name;
    public double[] location;
    public boolean is_active;

    public Player(){};
    public Player(int team, String name, double[] location, boolean is_active) {
        this.team = team;
        this.name = name;
        this.location = location;
        this.is_active = is_active;
    }
}
