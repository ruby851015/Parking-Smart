package com.itshareplus.googlemapdemo;

/**
 * Created by katsudon on 2017/9/11.
 */

public class ParkinglotInfo {
    public String name;
    public double lat;
    public double lng;
    public String leftspace;
    public String chargeway;
    public String businessHrs;
    public String location;
    public int x;
    public int y;
    public String areaname;

    public ParkinglotInfo(){

    }
    public ParkinglotInfo(String name,
                          double lat,
                          double lng,
                          String leftspace,
                          String chargeway,
                          String businessHrs,
                          String location,
                          int x,
                          int y,
                          String areaname){

        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.leftspace = leftspace;
        this.chargeway = chargeway;
        this.businessHrs = businessHrs;
        this.location = location;
        this.x = x;
        this.y = y;
        this.areaname = areaname;
    }
}