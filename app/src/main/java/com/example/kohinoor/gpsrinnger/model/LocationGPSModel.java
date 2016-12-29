package com.example.kohinoor.gpsrinnger.model;

import java.io.Serializable;

/**
 * Created by Kohinoor on 12/25/2016.
 */

public class LocationGPSModel implements Serializable{

    public LocationGPSModel()
    {

    }

    private String _locationName;
    public void set_Location(String locationName)
    {
        _locationName = locationName;
    }
    public String get_locationName()
    {
        return _locationName;
    }

    private String _lat;
    public void set_Lat(String lat)
    {
        _lat = lat;
    }
    public String get_Lat()
    {
        return _lat;
    }

    private String _lon;
    public void set_Lon(String lng)
    {
        _lon = lng;
    }
    public String get_Lon()
    {
        return _lon;
    }

    private int _vol = 0;
    public void set_Vol(int vol)
    {
        _vol = vol;
    }
    public int get_Vol()
    {
        return _vol;
    }
}
