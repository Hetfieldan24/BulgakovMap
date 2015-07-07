package com.mapbox.mapboxsdk.android.testapp.util;


import java.util.ArrayList;

public class ListModel
{
	private String placeName="";
    private String address="";
    private String distance="";
    private String tagName="";
    private String description="";
    private String textMain="";
    private ArrayList<String> url;

    /*********** Set Methods ******************/
	public void setPlaceName(String placeName)
	{
		this.placeName = placeName;
	}
    public void setAddress(String address)
    {
        this.address = address;
    }
	/*********** Get Methods ****************/
	public String getPlaceName()
	{
		return this.placeName;
	}

    public String getAddress()
    {
        return address;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setTagName(String tagName)
    {
        this.tagName = tagName;
    }

    public String getTagName()
    {
        return tagName;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setTextMain(String textMain)
    {
        this.textMain = textMain;
    }

    public String getTextMain()
    {
        return textMain;
    }

    public void setUrl(ArrayList<String> url)
    {
        this.url = url;
    }

    public ArrayList<String> getUrl()
    {
        return url;
    }
}
