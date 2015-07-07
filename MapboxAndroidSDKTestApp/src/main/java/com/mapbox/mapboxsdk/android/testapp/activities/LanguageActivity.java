package com.mapbox.mapboxsdk.android.testapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.android.testapp.util.FontHelper;
import com.mapbox.mapboxsdk.android.testapp.util.Globals;

import java.util.Locale;

public class LanguageActivity extends Activity implements View.OnClickListener
{
    private TextView text, subtitle;
    private ImageButton placesButton;
    private ImageButton routesButton;
    private ImageButton searchButton;
    private ImageButton infoButton;
    private TextView placesTextView, routesTextView, searchTextView, infoTextView;
    private int green, grey;
    private Resources res;
    private Locale current;
    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language);

        globals = Globals.getInstance();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView name = (TextView)findViewById(R.id.name);
        text = (TextView)findViewById(R.id.text);
        subtitle = (TextView)findViewById(R.id.subtitle);

        res = getResources();

        name.setText(res.getString(R.string.options));

        green = res.getColor(R.color.green);
        grey = res.getColor(R.color.grey);

        current = getResources().getConfiguration().locale;
        if(current.toString().toLowerCase().equals("ru_ru"))
        {
            text.setText(R.string.lng_en);
            text.setTextColor(res.getColor(R.color.black));
            subtitle.setText(R.string.lng_rus);
            subtitle.setTextColor(green);
        }
        else
        {
            text.setText(R.string.lng_en);
            text.setTextColor(green);
            subtitle.setText(R.string.lng_rus);
            subtitle.setTextColor(res.getColor(R.color.black));
        }

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

        RelativeLayout subtitleLayout = (RelativeLayout) findViewById(R.id.subtitleLayout);
        RelativeLayout textLayout = (RelativeLayout) findViewById(R.id.textLayout);

        placesLayout.setOnClickListener(this);
        routesLayout.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
        infoLayout.setOnClickListener(this);
        backLayout.setOnClickListener(this);
        subtitleLayout.setOnClickListener(this);
        textLayout.setOnClickListener(this);

        FontHelper.applyFont(this, findViewById(R.id.drawer_layout), "fonts/GTH75.otf");

        infoButton.setBackgroundResource(R.drawable.info_button_active);
        infoTextView.setTextColor(green);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.placesLayout:

                clearButtonsBackgrounds();
                placesButton.setBackgroundResource(R.drawable.places_button_active);

                clearButtonsColors();
                placesTextView.setTextColor(green);

                startActivity(new Intent(LanguageActivity.this, MainActivity.class));
                break;

            case R.id.routesLayout:

                clearButtonsBackgrounds();
                routesButton.setBackgroundResource(R.drawable.routes_button_active);

                clearButtonsColors();
                routesTextView.setTextColor(green);


                startActivity(new Intent(LanguageActivity.this, RoutesActivity.class));
                break;

            case R.id.searchLayout:

                clearButtonsBackgrounds();
                searchButton.setBackgroundResource(R.drawable.search_button_active);

                clearButtonsColors();
                searchTextView.setTextColor(green);

                startActivity(new Intent(LanguageActivity.this, SearchActivity.class));
                break;

            case R.id.infoLayout:

                clearButtonsBackgrounds();
                infoButton.setBackgroundResource(R.drawable.info_button_active);

                clearButtonsColors();
                infoTextView.setTextColor(green);
                break;

            case R.id.subtitleLayout:

                res = getResources();
                // Change locale settings in the app.
                DisplayMetrics dm = res.getDisplayMetrics();
                android.content.res.Configuration conf = res.getConfiguration();
                conf.locale = new Locale("ru_RU".toLowerCase());
                res.updateConfiguration(conf, dm);

                text.setText(R.string.lng_en);
                text.setTextColor(grey);
                subtitle.setText(R.string.lng_rus);
                subtitle.setTextColor(green);

                globals.setLanguage("ru");

                Intent intent = getIntent();

                finish();

                current = getResources().getConfiguration().locale;

                startActivity(intent);
                break;

            case R.id.textLayout:

                res = getResources();
                // Change locale settings in the app.
                dm = res.getDisplayMetrics();
                conf = res.getConfiguration();
                conf.locale = new Locale("en".toLowerCase());
                res.updateConfiguration(conf, dm);

                text.setText(R.string.lng_en);
                text.setTextColor(green);
                subtitle.setText(R.string.lng_rus);
                subtitle.setTextColor(grey);

                globals.setLanguage("en");

                intent = getIntent();

                finish();

                current = getResources().getConfiguration().locale;

                startActivity(intent);

                break;

            case R.id.backLayout:

                finish();
                startActivity(new Intent(LanguageActivity.this, InfoActivity.class));
                break;

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
}
