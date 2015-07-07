package com.mapbox.mapboxsdk.android.testapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.android.testapp.util.FontHelper;
import com.mapbox.mapboxsdk.android.testapp.util.Globals;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class RoutesInfoActivity extends Activity implements View.OnClickListener
{
    private ImageView routeImg;
    private TextView placesTextView, routesTextView, searchTextView, infoTextView;
    private ImageButton placesButton;
    private ImageButton routesButton;
    private ImageButton searchButton;
    private ImageButton infoButton;
    private int green;
    private int grey;
    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_info);

        globals = Globals.getInstance();
        globals.setFlagRoutesInfo(true);

        globals.setPositionRoutesInfo(globals.getPositionCustomAdapterSingleItem());

        TextView routeName = (TextView)findViewById(R.id.routeName);
        TextView description = (TextView)findViewById(R.id.description);
        TextView routeInfo = (TextView)findViewById(R.id.routeInfo);

        routeImg = (ImageView)findViewById(R.id.routeImg);

        routeName.setText(getIntent().getStringExtra("name"));

        routeInfo.setText(getIntent().getStringExtra("distance") + " | "
                + getIntent().getStringExtra("numberOfPoints") + " | "
                + getIntent().getStringExtra("duration"));

        placesButton = (ImageButton)findViewById(R.id.placesButton);
        routesButton = (ImageButton)findViewById(R.id.routesButton);
        searchButton = (ImageButton)findViewById(R.id.searchButton);
        infoButton = (ImageButton)findViewById(R.id.infoButton);
        RelativeLayout backLayout = (RelativeLayout) findViewById(R.id.backLayout);

        backLayout.setOnClickListener(this);

        RelativeLayout placesLayout = (RelativeLayout) findViewById(R.id.placesLayout);
        RelativeLayout routesLayout = (RelativeLayout) findViewById(R.id.routesLayout);
        RelativeLayout searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);
        RelativeLayout infoLayout = (RelativeLayout) findViewById(R.id.infoLayout);
        RelativeLayout createRouteLayout = (RelativeLayout) findViewById(R.id.createRouteLayout);

        if(!getIntent().getStringExtra("description").equals("null"))
        {
            description.setText(getIntent().getStringExtra("description"));
        }
        else
        {
            description.setVisibility(View.GONE);
            createRouteLayout.setVisibility(View.GONE);
            ImageView secondBar = (ImageView)findViewById(R.id.secondBar);
            secondBar.setVisibility(View.GONE);
            Log.e("description no", getIntent().getStringExtra("description"));
        }

        placesLayout.setOnClickListener(this);
        routesLayout.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
        infoLayout.setOnClickListener(this);
        createRouteLayout.setOnClickListener(this);

        FontHelper.applyFont(this, findViewById(R.id.drawer_layout), "fonts/GTH75.otf");
        green = getResources().getColor(R.color.green);
        grey = getResources().getColor(R.color.grey);

        placesTextView = (TextView)findViewById(R.id.placesTextView);
        routesTextView = (TextView)findViewById(R.id.routesTextView);
        searchTextView = (TextView)findViewById(R.id.searchTextView);
        infoTextView = (TextView)findViewById(R.id.infoTextView);

        routesButton.setBackgroundResource(R.drawable.routes_button_active);
        routesTextView.setTextColor(green);

        Typeface newspaper = Typeface.createFromAsset(getAssets(), "fonts/NewspaperSansC.otf");

        routeName.setTypeface(newspaper);

        loadImageFromUrl();
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
            Toast.makeText(RoutesInfoActivity.this,
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
        return result;
    }

    private void loadImageFromUrl()
    {
        int number;
        String imageUrl;
        String imageUrlFromServer;
        globals = Globals.getInstance();
        number = globals.getPositionCustomAdapterSingleItem();

        DisplayImageOptions options;

        ImageLoaderConfiguration config;

        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .resetViewBeforeLoading(true)
                .build();

        config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(options)
                .build();

        imageUrlFromServer = readFromFile("routesUrls.txt").get(number);

        if (number >= 0)
        {
            if (!imageUrlFromServer.equals("null"))
            {
                imageUrl = "http://mobile.bulgakovmuseum.ru/" + imageUrlFromServer;

                ImageLoader.getInstance().init(config);
                ImageLoader imageLoader = ImageLoader.getInstance();

                List<Bitmap> bmps =
                        MemoryCacheUtils.findCachedBitmapsForImageUri(
                                imageUrl, ImageLoader.getInstance().getMemoryCache());

                if (!bmps.isEmpty())
                {
                    routeImg.setImageBitmap(bmps.get(0));

                    for (Bitmap bmp : bmps)
                    {
                        bmp.recycle();
                    }

                } else
                {
                    imageLoader.displayImage(imageUrl, routeImg, options);
                }
            } else
            {
                FrameLayout frameLayout = (FrameLayout)findViewById(R.id.image_layout);

                frameLayout.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onBackPressed()
    {
        ImageLoader.getInstance().stop();
        super.onBackPressed();
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

                startActivity(new Intent(RoutesInfoActivity.this, MainActivity.class));
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

                startActivity(new Intent(RoutesInfoActivity.this, SearchActivity.class));
                break;

            case R.id.infoLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                infoButton.setBackgroundResource(R.drawable.info_button_active);

                clearButtonsColors();
                infoTextView.setTextColor(green);

                startActivity(new Intent(RoutesInfoActivity.this, InfoActivity.class));
                break;

            case R.id.createRouteLayout:

                globals.setPositionMain(-1);

                globals.setFlagRoutesInfo(true);

                startActivity(new Intent(RoutesInfoActivity.this, MainActivity.class));
                break;

            case R.id.backLayout:
                finish();
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
