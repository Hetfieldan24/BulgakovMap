package com.mapbox.mapboxsdk.android.testapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.android.testapp.util.FontHelper;
import com.mapbox.mapboxsdk.android.testapp.util.Globals;

public class TextInfoActivity extends Activity implements View.OnClickListener
{
    private ImageButton placesButton;
    private ImageButton routesButton;
    private ImageButton searchButton;
    private ImageButton infoButton;
    private TextView placesTextView, routesTextView, searchTextView, infoTextView;
    private int green, grey;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_info);

        Globals globals = Globals.getInstance();
        globals.setFlagCustomAdapterPlacesList("");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Resources res = getResources();

        TextView name = (TextView)findViewById(R.id.name);
        TextView nameCenter = (TextView)findViewById(R.id.nameCenter);
        TextView text = (TextView) findViewById(R.id.text);

        int position = getIntent().getIntExtra("position", -1);

        if(!(globals.getItemNames().get(position).equals(res.getString(R.string.title_bulg_and_moscow))))
        {
            name.setVisibility(View.GONE);
            nameCenter.setVisibility(View.VISIBLE);
            nameCenter.setText(globals.getItemNames().get(position));
        }
        else

        {
            name.setVisibility(View.VISIBLE);
            nameCenter.setVisibility(View.GONE);
            name.setText(globals.getItemNames().get(position));
        }

        green = res.getColor(R.color.green);
        grey = res.getColor(R.color.grey);

        switch (position)
        {
            case 0:
                text.setText(R.string.about_project);
                break;

            case 1:
                TextView prolog = (TextView)findViewById(R.id.prolog);
                prolog.setVisibility(View.VISIBLE);
                prolog.setText(R.string.bulg_and_moscow_prolog);
                text.setText(getString(R.string.bulg_and_moscow));
                break;

            case 2:
                text.setText(R.string.about_app);
                text.setMovementMethod(LinkMovementMethod.getInstance());
                break;
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

        placesLayout.setOnClickListener(this);
        routesLayout.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
        infoLayout.setOnClickListener(this);
        backLayout.setOnClickListener(this);

        FontHelper.applyFont(this, findViewById(R.id.drawer_layout), "fonts/GTH75.otf");

        infoButton.setBackgroundResource(R.drawable.info_button_active);
        infoTextView.setTextColor(green);
    }

    @Override
    public void onClick(View v)
    {
        Globals globals = Globals.getInstance();

        switch(v.getId())
        {
            case R.id.placesLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                placesButton.setBackgroundResource(R.drawable.places_button_active);

                clearButtonsColors();
                placesTextView.setTextColor(green);

                startActivity(new Intent(TextInfoActivity.this, MainActivity.class));
                break;

            case R.id.routesLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                routesButton.setBackgroundResource(R.drawable.routes_button_active);

                clearButtonsColors();
                routesTextView.setTextColor(green);


                startActivity(new Intent(TextInfoActivity.this, RoutesActivity.class));
                break;

            case R.id.searchLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                searchButton.setBackgroundResource(R.drawable.search_button_active);

                clearButtonsColors();
                searchTextView.setTextColor(green);

                startActivity(new Intent(TextInfoActivity.this, SearchActivity.class));
                break;

            case R.id.infoLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                infoButton.setBackgroundResource(R.drawable.info_button_active);

                clearButtonsColors();
                infoTextView.setTextColor(green);
                break;

            case R.id.backLayout:

                startActivity(new Intent(TextInfoActivity.this, InfoActivity.class));
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
