package com.mapbox.mapboxsdk.android.testapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.android.testapp.activities.PlacesCardsActivity;
import com.mapbox.mapboxsdk.android.testapp.util.CustomAdapterPlacesList;
import com.mapbox.mapboxsdk.android.testapp.util.Globals;
import com.mapbox.mapboxsdk.android.testapp.util.ListModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class PlacesListFragment extends Fragment
{
    private ArrayList<ListModel> CustomListViewValuesArr = new ArrayList<ListModel>();
    private ArrayList<String> placesNames;
    private ArrayList<String> placesAddresses;
    private ArrayList<String> placesDescription;
    private ArrayList<String> placesTextMain;
    private ArrayList<Integer> distance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.places_list_fragment, container, false);

        /******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
        Globals globals = Globals.getInstance();

        if(globals.getLanguage().equals("ru"))
        {
            placesNames = readFromFile("nameArray.txt");
            placesAddresses = readFromFile("addressArray.txt");
            placesDescription = readFromFile("descriptionArray.txt");
            placesTextMain = readFromFile("textMainArray.txt");
        }
        else
        {
            placesNames = readFromFile("nameEnArray.txt");
            placesAddresses = readFromFile("addressEnArray.txt");
            placesDescription = readFromFile("descriptionEnArray.txt");
            placesTextMain = readFromFile("textMainEnArray.txt");
        }
        distance = new ArrayList<Integer>();

        setListData();

        ListView list = (ListView) view.findViewById(R.id.listView);

        /**************** Create Custom Adapter *********/
        globals.setFlagCustomAdapterPlacesList("List");

        CustomAdapterPlacesList adapter = new CustomAdapterPlacesList(this, CustomListViewValuesArr);
        list.setAdapter(adapter);

        return view;
    }

    /****** Function to set data in ArrayList *************/
    public void setListData()
    {
        ListModel sched;

        if(new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + "BulgakovMoscow/" + "distanceArray.txt").exists())
        {
            ArrayList<String> distanceFile = readFromFile("distanceArray.txt");

            for (int index = 0; index < distanceFile.size(); index++)
            {
                distance.add(Integer.valueOf(distanceFile.get(index)));
            }
        }

        Log.e("distancePLACESLIST ", distance.toString());

        for (int i = 0; i < placesNames.size(); i++)
        {
            sched = new ListModel();

            Log.e("placesAddresses  ", placesAddresses.toString());

            if(!placesNames.get(i).equals("null"))
            {
                sched.setPlaceName(placesNames.get(i));
            }
            else
            {
                sched.setPlaceName("");
            }
            if(!placesAddresses.get(i).equals("null"))
            {
                sched.setAddress(placesAddresses.get(i));
            }
            else
            {
                sched.setAddress(" ");
            }
            if(!distance.isEmpty())
                sched.setDistance(String.valueOf(distance.get(i)));

            /******** Take Model Object in ArrayList **********/
            CustomListViewValuesArr.add(sched);
        }
    }

    public void onItemClick(int mPosition)
    {
        Intent intent = new Intent(getActivity(), PlacesCardsActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Globals globals = Globals.getInstance();
        globals.setPositionPlacesList(mPosition);

        intent.putExtra("name", placesNames.get(mPosition));
        intent.putExtra("fullName", placesNames.get(mPosition));
        intent.putExtra("address", placesAddresses.get(mPosition));
        intent.putExtra("description", placesDescription.get(mPosition));
        intent.putExtra("textMain", placesTextMain.get(mPosition));
        getActivity().startActivity(intent);
    }

    public ArrayList<String> readFromFile(String fileName)
    {
        StringBuffer buffer = new StringBuffer();
        ArrayList<String> result = new ArrayList<String>();

        String FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + "BulgakovMoscow";
        File sdPath = new File(FILEPATH);

        if (!sdPath.exists())
        {
            sdPath.mkdirs();
        }

        try
        {
            FileInputStream inputstream = new FileInputStream(FILEPATH + "/" + fileName);

            if (inputstream != null)
            {
                InputStreamReader isr = new InputStreamReader(inputstream, "windows-1251");
                BufferedReader reader = new BufferedReader(isr);
                String str;

                while ((str = reader.readLine()) != null)
                {
                    buffer.append(str).append("\n");
                    result.add(str);
                }

                inputstream.close();
            }
        }
        catch (Throwable t)
        {
            Toast.makeText(getActivity(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
        return result;
    }
}