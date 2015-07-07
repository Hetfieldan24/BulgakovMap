package com.mapbox.mapboxsdk.android.testapp.util;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;

import java.util.ArrayList;

/**
 * Created by hetfieldan24 on 02.11.2014.
 */
public class Globals
{
    private static volatile Globals instance;

    private ArrayList<Marker> markersMainTest;
    private ArrayList<String> itemNames;
    private ArrayList<LatLng> pointsSearch;
    private ArrayList<LatLng> pointsTagPlace;
    private String language = "ru";
    private boolean firstLaunchMainTest = true;
    private boolean showPlacePlacesCards = false;
    private boolean firstLaunchRoutesActivity = true;
    private boolean flagRoutesInfo = false;
    private boolean CurrentlyRunningInfoActivity = false;
    private boolean CurrentlyRunningRoutesActivity = false;
    private int positionRoutesInfo;
    private int positionCustomAdapterSingleItem = 0;
    private int positionSearchActivity;
    private int positionTagPlace;
    private int positionPlacesList;
    private String flagCustomAdapterPlacesList = "";
    private int positionCustomInfo;
    private String flagCustomInfo = "";
    private int positionMain = -1;
    private int positionCustomInside = -1;

    //GPSTracker
    // The minimum distance to change Updates in meters
    private final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    public static Globals getInstance()
    {
        Globals localInstance = instance;
        if(localInstance == null)
        {
            synchronized (Globals.class)
            {
                localInstance = instance;

                if(localInstance == null)
                {
                    instance = new Globals();
                }
            }
        }
        return instance;
    }

    public void setMarkersMainTest(ArrayList<Marker> markersMainTest)
    {
        this.markersMainTest = markersMainTest;
    }

    public ArrayList<Marker> getMarkersMainTest()
    {
        return markersMainTest;
    }

    public void setItemNames(ArrayList<String> itemNames)
    {
        this.itemNames = itemNames;
    }

    public ArrayList<String> getItemNames()
    {
        return itemNames;
    }

    public void setPointsSearch(ArrayList<LatLng> pointsSearch)
    {
        this.pointsSearch = pointsSearch;
    }

    public ArrayList<LatLng> getPointsSearch()
    {
        return pointsSearch;
    }

    public void setPointsTagPlace(ArrayList<LatLng> pointsTagPlace)
    {
        this.pointsTagPlace = pointsTagPlace;
    }

    public ArrayList<LatLng> getPointsTagPlace()
    {
        return pointsTagPlace;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setFirstLaunchMainTest(boolean firstLaunchMainTest)
    {
        this.firstLaunchMainTest = firstLaunchMainTest;
    }

    public boolean isFirstLaunchMainTest()
    {
        return firstLaunchMainTest;
    }

    public void setShowPlacePlacesCards(boolean showPlacePlacesCards)
    {
        this.showPlacePlacesCards = showPlacePlacesCards;
    }

    public boolean isShowPlacePlacesCards()
    {
        return showPlacePlacesCards;
    }

    public void setFirstLaunchRoutesActivity(boolean firstLaunchRoutesActivity)
    {
        this.firstLaunchRoutesActivity = firstLaunchRoutesActivity;
    }

    public boolean isFirstLaunchRoutesActivity()
    {
        return firstLaunchRoutesActivity;
    }

    public void setFlagRoutesInfo(boolean flagRoutesInfo)
    {
        this.flagRoutesInfo = flagRoutesInfo;
    }

    public boolean isFlagRoutesInfo()
    {
        return flagRoutesInfo;
    }

    public void setCurrentlyRunningInfoActivity(boolean currentlyRunningInfoActivity)
    {
        CurrentlyRunningInfoActivity = currentlyRunningInfoActivity;
    }

    public boolean isCurrentlyRunningInfoActivity()
    {
        return CurrentlyRunningInfoActivity;
    }

    public void setCurrentlyRunningRoutesActivity(boolean currentlyRunningRoutesActivity)
    {
        CurrentlyRunningRoutesActivity = currentlyRunningRoutesActivity;
    }

    public boolean isCurrentlyRunningRoutesActivity()
    {
        return CurrentlyRunningRoutesActivity;
    }

    public void setPositionRoutesInfo(int positionRoutesInfo)
    {
        this.positionRoutesInfo = positionRoutesInfo;
    }

    public int getPositionRoutesInfo()
    {
        return positionRoutesInfo;
    }

    public void setPositionCustomAdapterSingleItem(int positionCustomAdapterSingleItem)
    {
        this.positionCustomAdapterSingleItem = positionCustomAdapterSingleItem;
    }

    public int getPositionCustomAdapterSingleItem()
    {
        return positionCustomAdapterSingleItem;
    }

    public void setPositionSearchActivity(int positionSearchActivity)
    {
        this.positionSearchActivity = positionSearchActivity;
    }

    public int getPositionSearchActivity()
    {
        return positionSearchActivity;
    }

    public void setPositionTagPlace(int positionTagPlace)
    {
        this.positionTagPlace = positionTagPlace;
    }

    public int getPositionTagPlace()
    {
        return positionTagPlace;
    }

    public void setPositionPlacesList(int positionPlacesList)
    {
        this.positionPlacesList = positionPlacesList;
    }

    public int getPositionPlacesList()
    {
        return positionPlacesList;
    }

    public void setFlagCustomAdapterPlacesList(String flagCustomAdapterPlacesList)
    {
        this.flagCustomAdapterPlacesList = flagCustomAdapterPlacesList;
    }

    public String getFlagCustomAdapterPlacesList()
    {
        return flagCustomAdapterPlacesList;
    }

    public void setPositionCustomInfo(int positionCustomInfo)
    {
        this.positionCustomInfo = positionCustomInfo;
    }

    public int getPositionCustomInfo()
    {
        return positionCustomInfo;
    }

    public void setFlagCustomInfo(String flagCustomInfo)
    {
        this.flagCustomInfo = flagCustomInfo;
    }

    public String getFlagCustomInfo()
    {
        return flagCustomInfo;
    }

    public long getMIN_DISTANCE_CHANGE_FOR_UPDATES()
    {
        return MIN_DISTANCE_CHANGE_FOR_UPDATES;
    }

    public long getMIN_TIME_BW_UPDATES()
    {
        return MIN_TIME_BW_UPDATES;
    }

    public void setPositionMain(int positionMain)
    {
        this.positionMain = positionMain;
    }

    public int getPositionMain()
    {
        return positionMain;
    }

    public void setPositionCustomInside(int positionCustomInside)
    {
        this.positionCustomInside = positionCustomInside;
    }

    public int getPositionCustomInside()
    {
        return positionCustomInside;
    }
}