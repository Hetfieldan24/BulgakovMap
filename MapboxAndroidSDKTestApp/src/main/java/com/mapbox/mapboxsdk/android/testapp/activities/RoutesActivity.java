package com.mapbox.mapboxsdk.android.testapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.android.testapp.util.CustomAdapterSingleItem;
import com.mapbox.mapboxsdk.android.testapp.util.FontHelper;
import com.mapbox.mapboxsdk.android.testapp.util.Globals;
import com.mapbox.mapboxsdk.android.testapp.util.ListModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class RoutesActivity extends Activity implements View.OnClickListener
{
    private ArrayList<ListModel> CustomListViewValuesArr;
    private ImageButton placesButton;
    private ImageButton routesButton;
    private ImageButton searchButton;
    private ImageButton infoButton;
    private TextView placesTextView, routesTextView, searchTextView, infoTextView;
    private int green;
    private int grey;
    private ArrayList<String> itemNames;
    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        CustomListViewValuesArr = new ArrayList<ListModel>();

        globals = Globals.getInstance();

        globals.setFlagCustomAdapterPlacesList("");

        if(savedInstanceState == null && globals.isFirstLaunchRoutesActivity())
        {
            globals.setFirstLaunchRoutesActivity(true);
        }
        else
        {
            globals.setFirstLaunchRoutesActivity(false);
        }

        setContent();

        placesButton = (ImageButton)findViewById(R.id.placesButton);
        routesButton = (ImageButton)findViewById(R.id.routesButton);
        searchButton = (ImageButton)findViewById(R.id.searchButton);
        infoButton = (ImageButton)findViewById(R.id.infoButton);
        RelativeLayout backLayout = (RelativeLayout) findViewById(R.id.backLayout);

        placesTextView = (TextView)findViewById(R.id.placesTextView);
        routesTextView = (TextView)findViewById(R.id.routesTextView);
        searchTextView = (TextView)findViewById(R.id.searchTextView);
        infoTextView = (TextView)findViewById(R.id.infoTextView);

        RelativeLayout placesLayout = (RelativeLayout) findViewById(R.id.placesLayout);
        RelativeLayout routesLayout = (RelativeLayout) findViewById(R.id.routesLayout);
        RelativeLayout searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);
        RelativeLayout infoLayout = (RelativeLayout) findViewById(R.id.infoLayout);

        placesLayout.setOnClickListener(this);
        routesLayout.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
        infoLayout.setOnClickListener(this);
        backLayout.setOnClickListener(this);

        FontHelper.applyFont(this, findViewById(R.id.drawer_layout), "fonts/GTH75.otf");
        green = getResources().getColor(R.color.green);
        grey = getResources().getColor(R.color.grey);

        routesButton.setBackgroundResource(R.drawable.routes_button_active);
        routesTextView.setTextColor(green);
    }

    public void setListData()
    {
        ListModel sched;
        for (int i = 0; i < itemNames.size(); i++)
        {
            sched = new ListModel();

            sched.setTagName(itemNames.get(i));

            /******** Take Model Object in ArrayList **********/
            CustomListViewValuesArr.add(sched);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.placesLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                placesButton.setBackgroundResource(R.drawable.places_button_active);

                clearButtonsColors();
                placesTextView.setTextColor(green);

                startActivity(new Intent(RoutesActivity.this, MainActivity.class));
                break;

            case R.id.routesLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                routesButton.setBackgroundResource(R.drawable.routes_button_active);

                clearButtonsColors();
                routesTextView.setTextColor(green);

                break;

            case R.id.searchLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                searchButton.setBackgroundResource(R.drawable.search_button_active);

                clearButtonsColors();
                searchTextView.setTextColor(green);

                startActivity(new Intent(RoutesActivity.this, SearchActivity.class));
                break;

            case R.id.infoLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                infoButton.setBackgroundResource(R.drawable.info_button_active);

                clearButtonsColors();
                infoTextView.setTextColor(green);

                startActivity(new Intent(RoutesActivity.this, InfoActivity.class));
                break;

            case R.id.backLayout:
                finish();
        }
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        globals = Globals.getInstance();
        globals.setCurrentlyRunningRoutesActivity(true); //Store status of Activity somewhere like in shared //preference
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        globals = Globals.getInstance();
        globals.setCurrentlyRunningRoutesActivity(false);//Store status of Activity somewhere like in shared //preference
    }

    public void clearButtonsBackgrounds()
    {
        placesButton.setBackgroundResource(R.drawable.places_button);
        routesButton.setBackgroundResource(R.drawable.routes_button);
        searchButton.setBackgroundResource(R.drawable.search_button);
        infoButton.setBackgroundResource(R.drawable.info_button);
    }

    public void clearButtonsColors()
    {
        placesTextView.setTextColor(grey);
        routesTextView.setTextColor(grey);
        searchTextView.setTextColor(grey);
        infoTextView.setTextColor(grey);
    }

    public void setContent()
    {
        globals = Globals.getInstance();
        if(globals.getLanguage().equals("ru"))
        {
            itemNames = readFromFile("routesNameArray.txt");
        }
        else
        {
            itemNames = readFromFile("routesNameEnArray.txt");
        }

        setListData();

        Resources res = getResources();
        ListView list = (ListView) findViewById(R.id.listView);

        CustomAdapterSingleItem adapter = new CustomAdapterSingleItem(RoutesActivity.this,
                CustomListViewValuesArr, res);
        list.setAdapter(adapter);
    }

    public ArrayList<String> readFromFile(String fileName)
    {
        StringBuffer buffer = new StringBuffer();
        ArrayList<String> result = new ArrayList<String>();

        String FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "BulgakovMoscow";
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
            Toast.makeText(RoutesActivity.this,
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
        return result;
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                RoutesActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
