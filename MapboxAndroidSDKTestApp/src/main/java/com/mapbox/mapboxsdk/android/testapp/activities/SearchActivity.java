package com.mapbox.mapboxsdk.android.testapp.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.android.testapp.util.BaseActivity;
import com.mapbox.mapboxsdk.android.testapp.util.CustomAdapterPlacesList;
import com.mapbox.mapboxsdk.android.testapp.util.CustomAdapterSingleItem;
import com.mapbox.mapboxsdk.android.testapp.util.FontHelper;
import com.mapbox.mapboxsdk.android.testapp.util.Globals;
import com.mapbox.mapboxsdk.android.testapp.util.ListModel;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

public class SearchActivity extends BaseActivity implements View.OnClickListener
{
    private ListView list;
    private CustomAdapterSingleItem adapter;
    private ArrayList<ListModel> CustomListViewValuesArr = new ArrayList<ListModel>();
    private ArrayList<Integer> distance;
    private EditText inputSearch;
    private ArrayList<ListModel> valuesArr;
    private ArrayList<ListModel> tempValuesArr;
    private ImageButton placesButton;
    private ImageButton routesButton;
    private ImageButton searchButton;
    private ImageButton infoButton;
    private TextView placesTextView, routesTextView, searchTextView, infoTextView;
    private boolean matchExists = false;
    private LinearLayout bottomBar;
    private int green;
    private int grey;
    private ImageView aboveBottomBar;
    private ArrayList<LatLng> points;
    private Globals globals;
    private ArrayList<String> themesNamesList;
    private ArrayList<ArrayList<String>> tagsNames;
    private ArrayList<ArrayList<String>> nameTagsRoutesArray;
    private int numberPlaces = -1;
    private ArrayList<Integer> routesIndexes;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        tagsNames = new ArrayList<ArrayList<String>>();
        nameTagsRoutesArray = new ArrayList<ArrayList<String>>();

        globals = Globals.getInstance();

        globals.setFlagCustomInfo("");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bottomBar = (LinearLayout)findViewById(R.id.bottomBar);
        aboveBottomBar = (ImageView)findViewById(R.id.aboveBottomBar);
        //belowListBar = (ImageView)findViewById(R.id.belowListBar);
        attachKeyboardListeners();

        distance = new ArrayList<Integer>();

        int cntTags = Integer.parseInt(readFromFile("cntTags.txt").get(0));
        int cntTagsRoutes = Integer.parseInt(readFromFile("cntTagsRoutes.txt").get(0));

        if(globals.getLanguage().equals("ru"))
        {
            themesNamesList = readFromFile("nameThemesArrayList.txt");

            Collections.sort(themesNamesList, new Comparator<String>()
            {
                @Override
                public int compare(String s1, String s2)
                {
                    return s1.compareToIgnoreCase(s2);
                }
            });

            for(int index = 0; index < cntTags; index++)
            {
                tagsNames.add(readFromFile("nameTagsArray" + index + ".txt"));
            }

            for(int index = 0; index < cntTagsRoutes; index++)
            {
                nameTagsRoutesArray.add(readFromFile("nameTagsRoutesArray" + index + ".txt"));
            }
        }
        else
        {
            themesNamesList = readFromFile("nameThemesEnArrayList.txt");

            Collections.sort(themesNamesList, new Comparator<String>()
            {
                @Override
                public int compare(String s1, String s2)
                {
                    return s1.compareToIgnoreCase(s2);
                }
            });

            for(int index = 0; index < cntTags; index++)
            {
                tagsNames.add(readFromFile("nameTagsEnArray" + index + ".txt"));
            }

            for(int index = 0; index < cntTagsRoutes; index++)
            {
                nameTagsRoutesArray.add(readFromFile("nameTagsEnRoutesArray" + index + ".txt"));
            }
        }
        valuesArr = new ArrayList<ListModel>();

        setListData();

        Resources res = getResources();
        list=(ListView)findViewById(R.id.listView);

        /**************** Create Custom Adapter *********/
        globals.setFlagCustomAdapterPlacesList("");
        adapter = new CustomAdapterSingleItem(SearchActivity.this, CustomListViewValuesArr, res);
        list.setAdapter(adapter);

        inputSearch = (EditText)findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3)
            {
                points = new ArrayList<LatLng>();

                matchExists = false;

                if(!cs.toString().equals(""))
                {
                    TextView textView = (TextView)findViewById(R.id.no_result);
                    ListModel sched;

                    ArrayList<String> distanceArray;
                    ArrayList<String> nameArray;
                    ArrayList<String> addressArray;
                    ArrayList<String> descriptionArray;
                    ArrayList<String> textMainArray;
                    ArrayList<String> namesRoutes;
                    ArrayList<String> pointsFile;

                    if(globals.getLanguage().equals("ru"))
                    {
                        nameArray = readFromFile("nameArray.txt");
                        addressArray = readFromFile("addressArray.txt");
                        descriptionArray = readFromFile("descriptionArray.txt");
                        textMainArray = readFromFile("textMainArray.txt");
                        namesRoutes = readFromFile("routesNameArray.txt");
                    }
                    else
                    {
                        nameArray = readFromFile("nameEnArray.txt");
                        addressArray = readFromFile("addressEnArray.txt");
                        descriptionArray = readFromFile("descriptionEnArray.txt");
                        textMainArray = readFromFile("textMainEnArray.txt");
                        namesRoutes = readFromFile("routesNameEnArray.txt");
                    }

                    pointsFile = readFromFile("points.txt");

                    if (new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/" + "BulgakovMoscow/" + "distanceArray.txt").exists())
                    {
                        distanceArray = readFromFile("distanceArray.txt");

                        for (String aDistanceArray : distanceArray)
                            distance.add(Integer.valueOf(aDistanceArray));
                    }

                    LatLng tempPoint;
                    String[] strokes;
                    Globals globals = Globals.getInstance();

                    ArrayList<Integer> checkMatchesIndexArray = new ArrayList<Integer>();
                    boolean noMatches = true;

                    ArrayList<Integer> checkMatchesIndexRoutesArray = new ArrayList<Integer>();
                    boolean noMatchesRoutes = true;

                    list.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);

                    for (int index = 0; index < nameArray.size(); index++)
                    {
                        if (Pattern.compile(Pattern.quote(cs.toString()),
                                Pattern.CASE_INSENSITIVE).matcher(nameArray.get(index)).find())
                        {
                            checkMatchesIndexArray.add(index);
                            matchExists = true;

                            sched = new ListModel();

                            sched.setPlaceName(nameArray.get(index));
                            if(!addressArray.get(index).equals("null"))
                            {
                                sched.setAddress(addressArray.get(index));
                            }
                            else
                            {
                                sched.setAddress(" ");
                            }
                            sched.setDescription(descriptionArray.get(index));
                            sched.setTextMain(textMainArray.get(index));

                            sched.setUrl(readFromFile("urls" + index + ".txt"));

                            strokes = pointsFile.get(index).split(",");
                            tempPoint = new LatLng(Double.parseDouble(strokes[0]), Double.parseDouble(strokes[1]));
                            points.add(tempPoint);

                            if (!distance.isEmpty())
                                sched.setDistance(String.valueOf(distance.get(index)));

                            valuesArr.add(sched);
                        }

                        for(int i = 0; i < tagsNames.get(index).size(); i++)
                        {
                            if (cs.toString().equalsIgnoreCase(tagsNames.get(index).get(i)))
                            {
                                matchExists = true;

                                for (int k = 0; k < checkMatchesIndexArray.size(); k++)
                                {
                                    if (index == checkMatchesIndexArray.get(k))
                                    {
                                        noMatches = false;
                                    }
                                    else
                                    {
                                        noMatches = true;
                                    }
                                }
                                if(noMatches)
                                {
                                    sched = new ListModel();

                                    sched.setPlaceName(nameArray.get(index));
                                    if(!addressArray.get(index).equals("null"))
                                    {
                                        sched.setAddress(addressArray.get(index));
                                    }
                                    else
                                    {
                                        sched.setAddress(" ");
                                    }
                                    sched.setDescription(descriptionArray.get(index));
                                    sched.setTextMain(textMainArray.get(index));

                                    sched.setUrl(readFromFile("urls" + index + ".txt"));

                                    strokes = pointsFile.get(index).split(",");
                                    tempPoint = new LatLng(Double.parseDouble(strokes[0]), Double.parseDouble(strokes[1]));
                                    points.add(tempPoint);

                                    if (!distance.isEmpty())
                                        sched.setDistance(String.valueOf(distance.get(index)));

                                    valuesArr.add(sched);
                                }
                            }
                        }
                        numberPlaces = valuesArr.size();
                    }

                    routesIndexes = new ArrayList<Integer>();

                    for (int index = 0; index < namesRoutes.size(); index++)
                    {
                        if (Pattern.compile(Pattern.quote(cs.toString()),
                                Pattern.CASE_INSENSITIVE).matcher(namesRoutes.get(index)).find())
                        {
                            checkMatchesIndexRoutesArray.add(index);
                            routesIndexes.add(index);
                            globals.setPositionSearchActivity(index);
                            matchExists = true;

                            sched = new ListModel();

                            sched.setPlaceName(namesRoutes.get(index));

                            if (globals.getLanguage().equals("ru"))
                            {
                                sched.setAddress("маршрут");
                            } else
                            {
                                sched.setAddress("route");
                            }

                            sched.setDistance(" ");

                            valuesArr.add(sched);
                        }

                        for(int i = 0; i < nameTagsRoutesArray.get(index).size(); i++)
                        {
                            if (cs.toString().equalsIgnoreCase(nameTagsRoutesArray.get(index).get(i)))
                            {
                                matchExists = true;

                                for (int k = 0; k < checkMatchesIndexRoutesArray.size(); k++)
                                {
                                    if (index == checkMatchesIndexRoutesArray.get(k))
                                    {
                                        noMatchesRoutes = false;
                                    }
                                }
                                if(noMatchesRoutes)
                                {
                                    routesIndexes.add(index);
                                    globals.setPositionSearchActivity(index);
                                    matchExists = true;

                                    sched = new ListModel();

                                    sched.setPlaceName(namesRoutes.get(index));

                                    if (globals.getLanguage().equals("ru"))
                                    {
                                        sched.setAddress("маршрут");
                                    }
                                    else
                                    {
                                        sched.setAddress("route");
                                    }

                                    sched.setDistance(" ");

                                    valuesArr.add(sched);
                                }
                            }
                        }
                    }

                    globals.setPointsSearch(points);

                    globals.setFlagCustomAdapterPlacesList("Search");

                    CustomAdapterPlacesList adapter =
                            new CustomAdapterPlacesList(SearchActivity.this, valuesArr);

                    list.setAdapter(adapter);
                    tempValuesArr = valuesArr;
                    valuesArr = new ArrayList<ListModel>();

                    if(!matchExists)
                    {
                        Log.e("matches not found ", "!..");
                        list.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                    }
                }

                else
                {
                    list.setVisibility(View.VISIBLE);
                    TextView textView = (TextView)findViewById(R.id.no_result);
                    textView.setVisibility(View.GONE);

                    globals.setFlagCustomAdapterPlacesList("");
                    adapter = new CustomAdapterSingleItem(SearchActivity.this,
                            CustomListViewValuesArr, getResources());
                    list.setAdapter(adapter);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        placesButton = (ImageButton)findViewById(R.id.placesButton);
        routesButton = (ImageButton)findViewById(R.id.routesButton);
        searchButton = (ImageButton)findViewById(R.id.searchButton);
        infoButton = (ImageButton)findViewById(R.id.infoButton);
        ImageButton clearButton = (ImageButton) findViewById(R.id.clearButton);

        clearButton.setOnClickListener(this);

        RelativeLayout placesLayout = (RelativeLayout) findViewById(R.id.placesLayout);
        RelativeLayout routesLayout = (RelativeLayout) findViewById(R.id.routesLayout);
        RelativeLayout searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);
        RelativeLayout infoLayout = (RelativeLayout) findViewById(R.id.infoLayout);

        placesLayout.setOnClickListener(this);
        routesLayout.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
        infoLayout.setOnClickListener(this);

        FontHelper.applyFont(this, findViewById(R.id.drawer_layout), "fonts/GTH75.otf");
        green = getResources().getColor(R.color.green);
        grey = getResources().getColor(R.color.grey);

        placesTextView = (TextView)findViewById(R.id.placesTextView);
        routesTextView = (TextView)findViewById(R.id.routesTextView);
        searchTextView = (TextView)findViewById(R.id.searchTextView);
        infoTextView = (TextView)findViewById(R.id.infoTextView);

        searchButton.setBackgroundResource(R.drawable.search_button_active);
        searchTextView.setTextColor(green);
    }

        /****** Function to set data in ArrayList *************/
    public void setListData()
    {

        ListModel sched;

        globals = Globals.getInstance();

        for (int i = 0; i < themesNamesList.size(); i++)
        {
            sched = new ListModel();

            if(!themesNamesList.get(i).equals("null"))
            {
                sched.setTagName(themesNamesList.get(i));
            }

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

                startActivity(new Intent(SearchActivity.this, MainActivity.class));
                break;

            case R.id.routesLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                routesButton.setBackgroundResource(R.drawable.routes_button_active);

                clearButtonsColors();
                routesTextView.setTextColor(green);


                startActivity(new Intent(SearchActivity.this, RoutesActivity.class));
                break;

            case R.id.searchLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                searchButton.setBackgroundResource(R.drawable.search_button_active);

                clearButtonsColors();
                searchTextView.setTextColor(green);

                break;

            case R.id.infoLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                infoButton.setBackgroundResource(R.drawable.info_button_active);

                clearButtonsColors();
                infoTextView.setTextColor(green);

                startActivity(new Intent(SearchActivity.this, InfoActivity.class));
                break;

            case R.id.clearButton:
                inputSearch.setText("");
                break;
        }
    }

    public void onItemClick(int mPosition)
    {
        globals.setPositionSearchActivity(mPosition);

        if( (globals.getFlagCustomAdapterPlacesList().equals("Search"))
                && (mPosition >= numberPlaces))
        {
            int position = routesIndexes.get(mPosition - numberPlaces);

            globals.setPositionCustomAdapterSingleItem(position);

            Intent intent = new Intent(SearchActivity.this, RoutesInfoActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            ArrayList<String> routesDuration;
            ArrayList<String> routesDescription;
            ArrayList<String> routesNumberOfPoints;
            ArrayList<String> itemNames;
            ArrayList<String> routesDistance;

            if(globals.getLanguage().equals("ru"))
            {
                itemNames = readFromFile("routesNameArray.txt");
                routesDescription = readFromFile("routesDescription.txt");
            }
            else
            {
                itemNames = readFromFile("routesNameEnArray.txt");
                routesDescription = readFromFile("routesDescriptionEn.txt");
            }
            routesDuration = readFromFile("routesDuration.txt");
            routesDistance = readFromFile("routesDistance.txt");
            routesNumberOfPoints = readFromFile("routesNumberOfPoints.txt");

            intent.putExtra("name", itemNames.get(position));
            intent.putExtra("description", routesDescription.get(position));
            intent.putExtra("distance", routesDistance.get(position));
            intent.putExtra("duration", routesDuration.get(position));
            intent.putExtra("numberOfPoints", routesNumberOfPoints.get(position));

            startActivity(intent);
        }

        else if(globals.getFlagCustomAdapterPlacesList().equals("Search"))
        {
            Intent intenttwo = new Intent(SearchActivity.this, PlacesCardsActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intenttwo.putExtra("name", tempValuesArr.get(mPosition).getPlaceName());
            intenttwo.putExtra("fullName", tempValuesArr.get(mPosition).getPlaceName());
            intenttwo.putExtra("address", tempValuesArr.get(mPosition).getAddress());
            intenttwo.putExtra("description", tempValuesArr.get(mPosition).getDescription());
            intenttwo.putExtra("textMain", tempValuesArr.get(mPosition).getTextMain());
            intenttwo.putExtra("url", tempValuesArr.get(mPosition).getUrl());

            startActivity(intenttwo);
        }
        else
        {
            String tempThemeName = themesNamesList.get(mPosition);

            Intent intent = new Intent(SearchActivity.this, TagPlaceActivity.class);
            intent.putExtra("themeName", tempThemeName);
            startActivity(intent);
        }
    }

    @Override
    protected void onShowKeyboard(int keyboardHeight)
    {
        // do things when keyboard is shown
        bottomBar.setVisibility(View.GONE);
        aboveBottomBar.setVisibility(View.GONE);
        //belowListBar.setVisibility(View.GONE);
    }

    @Override
    protected void onHideKeyboard()
    {
        // do things when keyboard is hidden
        bottomBar.setVisibility(View.VISIBLE);
        aboveBottomBar.setVisibility(View.VISIBLE);
        //belowListBar.setVisibility(View.VISIBLE);
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
            Toast.makeText(SearchActivity.this,
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
        return result;
    }
}
