package com.OA.mytracker;

import java.io.Serializable;

public class UserInformation implements Serializable {
    public String lastonlinedate;
    public Double lat;
    public Double lon;


    public String getLastOnlineDate() {
        return lastonlinedate;
    }

    public void setLastOnlineDate(String lastOnlineDate) {
        lastonlinedate = lastOnlineDate;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public UserInformation() {

    }

    public UserInformation(String LastOnlineDate,Double lat,Double lon) {
        this.lastonlinedate=LastOnlineDate;
        this.lat=lat;
        this.lon=lon;
    }


}
