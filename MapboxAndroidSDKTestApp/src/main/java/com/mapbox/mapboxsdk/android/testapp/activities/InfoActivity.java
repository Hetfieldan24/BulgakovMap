package com.mapbox.mapboxsdk.android.testapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.android.testapp.util.CustomAdapterSingleItem;
import com.mapbox.mapboxsdk.android.testapp.util.FontHelper;
import com.mapbox.mapboxsdk.android.testapp.util.Globals;
import com.mapbox.mapboxsdk.android.testapp.util.ListModel;

import java.util.ArrayList;

public class InfoActivity extends Activity implements View.OnClickListener
{
    private ArrayList<ListModel> CustomListViewValuesArr = new ArrayList<ListModel>();
    private ImageButton placesButton;
    private ImageButton routesButton;
    private ImageButton searchButton;
    private ImageButton infoButton;
    private TextView placesTextView, routesTextView, searchTextView, infoTextView;
    private int green;
    private int grey;
    public ArrayList<String> itemNames;
    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        globals = Globals.getInstance();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Globals globals = Globals.getInstance();

        itemNames = new ArrayList<String>();

        Resources res = getResources();

        itemNames.add(res.getString(R.string.title_about_project));
        itemNames.add(res.getString(R.string.title_bulg_and_moscow));
        itemNames.add(res.getString(R.string.title_about_app));
        itemNames.add(res.getString(R.string.create_response));
        itemNames.add(res.getString(R.string.options));

        globals.setItemNames(itemNames);

        setListData();

        ListView list = (ListView) findViewById(R.id.listView);

        globals.setFlagCustomAdapterPlacesList("Info");
        CustomAdapterSingleItem adapter = new CustomAdapterSingleItem(InfoActivity.this,
                CustomListViewValuesArr, res);
        list.setAdapter(adapter);

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

        infoButton.setBackgroundResource(R.drawable.info_button_active);
        infoTextView.setTextColor(green);
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
                globals.setPositionCustomInfo(-1);

                clearButtonsBackgrounds();
                placesButton.setBackgroundResource(R.drawable.places_button_active);

                clearButtonsColors();
                placesTextView.setTextColor(green);

                startActivity(new Intent(InfoActivity.this, MainActivity.class));
                break;

            case R.id.routesLayout:

                globals.setPositionMain(-1);
                globals.setPositionCustomInfo(-1);

                clearButtonsBackgrounds();
                routesButton.setBackgroundResource(R.drawable.routes_button_active);

                clearButtonsColors();
                routesTextView.setTextColor(green);


                startActivity(new Intent(InfoActivity.this, RoutesActivity.class));
                break;

            case R.id.searchLayout:

                globals.setPositionMain(-1);
                globals.setPositionCustomInfo(-1);

                clearButtonsBackgrounds();
                searchButton.setBackgroundResource(R.drawable.search_button_active);

                clearButtonsColors();
                searchTextView.setTextColor(green);

                startActivity(new Intent(InfoActivity.this, SearchActivity.class));
                break;

            case R.id.infoLayout:

                globals.setPositionMain(-1);
                globals.setPositionCustomInfo(-1);

                clearButtonsBackgrounds();
                infoButton.setBackgroundResource(R.drawable.info_button_active);

                clearButtonsColors();
                infoTextView.setTextColor(green);
                break;

            case R.id.backLayout:
                finish();
        }
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        globals.setCurrentlyRunningInfoActivity(true); //Store status of Activity somewhere like in shared //preference
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        globals.setCurrentlyRunningInfoActivity(false);//Store status of Activity somewhere like in shared //preference
    }

    @Override
    protected void onDestroy()
    {
        super.onStop();
        globals.setCurrentlyRunningInfoActivity(false);//Store status of Activity somewhere like in shared //preference
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
}
