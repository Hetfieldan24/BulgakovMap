package com.mapbox.mapboxsdk.android.testapp.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.android.testapp.fragments.MainTestFragment;
import com.mapbox.mapboxsdk.android.testapp.fragments.MainTestFragmentEn;
import com.mapbox.mapboxsdk.android.testapp.fragments.PlacesListFragment;
import com.mapbox.mapboxsdk.android.testapp.util.FontHelper;
import com.mapbox.mapboxsdk.android.testapp.util.Globals;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends ActionBarActivity
        implements View.OnClickListener, Thread.UncaughtExceptionHandler
{
    private Button mapButton, listButton;
    private ImageButton placesButton, routesButton, searchButton, infoButton;
    private TextView placesTextView, routesTextView, searchTextView, infoTextView;
    private int white;
    private int green;
    private int grey;
    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());

        globals = Globals.getInstance();

        if(!globals.isFlagRoutesInfo())
        {
            setContentView(R.layout.activity_main);
        }
        else
        {
            setContentView(R.layout.activity_main_routes);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        placesButton = (ImageButton)findViewById(R.id.placesButton);
        routesButton = (ImageButton)findViewById(R.id.routesButton);
        searchButton = (ImageButton)findViewById(R.id.searchButton);
        infoButton = (ImageButton)findViewById(R.id.infoButton);

        RelativeLayout placesLayout = (RelativeLayout) findViewById(R.id.placesLayout);
        RelativeLayout routesLayout = (RelativeLayout) findViewById(R.id.routesLayout);
        RelativeLayout searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);
        RelativeLayout infoLayout = (RelativeLayout) findViewById(R.id.infoLayout);
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.drawer_layout);

        placesLayout.setOnClickListener(this);
        routesLayout.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
        infoLayout.setOnClickListener(this);
        mainLayout.setOnClickListener(this);

        placesTextView = (TextView)findViewById(R.id.placesTextView);
        routesTextView = (TextView)findViewById(R.id.routesTextView);
        searchTextView = (TextView)findViewById(R.id.searchTextView);
        infoTextView = (TextView)findViewById(R.id.infoTextView);

        FontHelper.applyFont(this, findViewById(R.id.drawer_layout), "fonts/GTH75.otf");
        white = getResources().getColor(R.color.white);
        green = getResources().getColor(R.color.green);
        grey = getResources().getColor(R.color.grey);

        if(!globals.isFlagRoutesInfo())
        {
            listButton = (Button) findViewById(R.id.listButton);
            mapButton = (Button) findViewById(R.id.mapButton);
            listButton.setOnClickListener(this);
            mapButton.setOnClickListener(this);
            mapButton.setTextColor(white);
            listButton.setTextColor(green);
        }
        else
        {
            RelativeLayout backLayout = (RelativeLayout)findViewById(R.id.backLayout);
            backLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    finish();
                }
            });
        }

        placesTextView.setTextColor(green);

        placesButton.setBackgroundResource(R.drawable.places_button_active);
        placesTextView.setTextColor(green);

        if(savedInstanceState == null && globals.isFirstLaunchMainTest())
        {
            globals.setFirstLaunchMainTest(true);
        }
        else
        {
            globals.setFirstLaunchMainTest(false);
        }

        if(getResources().getConfiguration().locale.toString().toLowerCase().equals("ru_ru"))
        {
            if (globals.getLanguage().equals("ru"))
            {
                selectItem(0);
            } else
            {
                selectItem(2);
            }
        }
        else
        {
            globals.setLanguage("en");
            selectItem(2);
        }

        if(globals.isFlagRoutesInfo())
        {
            clearButtonsBackgrounds();
            routesButton.setBackgroundResource(R.drawable.routes_button_active);

            clearButtonsColors();
            routesTextView.setTextColor(green);
        }
    }

    private void selectItem(int position)
    {
        Fragment fragment;

        switch (position)
        {
            case 0:
                fragment = new MainTestFragment();
                break;
            case 1:
                fragment = new PlacesListFragment();
                break;
            case 2:
                fragment = new MainTestFragmentEn();
                break;
            default:
                fragment = new MainTestFragment();
                break;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit();

    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.mapButton:

                globals.setPositionMain(-1);

                mapButton.setBackgroundResource(R.drawable.map_button);
                listButton.setBackgroundResource(R.drawable.inactive_button_list);

                mapButton.setTextColor(white);
                listButton.setTextColor(green);

                if(globals.getLanguage().equals("ru"))
                {
                    selectItem(0);
                }
                else
                {
                    selectItem(2);
                }
                break;

            case R.id.listButton:

                globals.setPositionMain(-1);

                listButton.setBackgroundResource(R.drawable.list_button);
                mapButton.setBackgroundResource(R.drawable.inactive_button_map);

                listButton.setTextColor(white);
                mapButton.setTextColor(green);

                selectItem(1);
                break;

            case R.id.placesLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                placesButton.setBackgroundResource(R.drawable.places_button_active);

                clearButtonsColors();
                placesTextView.setTextColor(green);

                selectItem(0);
                break;

            case R.id.routesLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                routesButton.setBackgroundResource(R.drawable.routes_button_active);

                clearButtonsColors();
                routesTextView.setTextColor(green);

                startActivity(new Intent(MainActivity.this, RoutesActivity.class));
                break;

            case R.id.searchLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                searchButton.setBackgroundResource(R.drawable.search_button_active);

                clearButtonsColors();
                searchTextView.setTextColor(green);

                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;

            case R.id.infoLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                infoButton.setBackgroundResource(R.drawable.info_button_active);

                clearButtonsColors();
                infoTextView.setTextColor(green);

                startActivity(new Intent(MainActivity.this, InfoActivity.class));
                break;
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        globals = Globals.getInstance();
        if(globals.getLanguage().equals("en"))
        {
            setTextViewLanguage();
        }
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

    public void setTextViewLanguage()
    {
        placesTextView.setText(R.string.places);
        routesTextView.setText(R.string.routes);
        searchTextView.setText(R.string.search);
        infoTextView.setText(R.string.info);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        Toast.makeText(MainActivity.this, "Произошла ошибка " + ex.getMessage(), Toast.LENGTH_LONG).show();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}