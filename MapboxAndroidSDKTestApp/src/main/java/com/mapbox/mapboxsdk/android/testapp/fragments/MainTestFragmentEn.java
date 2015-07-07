package com.mapbox.mapboxsdk.android.testapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.android.testapp.activities.PlacesCardsActivity;
import com.mapbox.mapboxsdk.android.testapp.util.GETRoutesUpdateTask;
import com.mapbox.mapboxsdk.android.testapp.util.GETUpdateTask;
import com.mapbox.mapboxsdk.android.testapp.util.GPSTracker;
import com.mapbox.mapboxsdk.android.testapp.util.Globals;
import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.constants.MapboxConstants;
import com.mapbox.mapboxsdk.events.DelayedMapListener;
import com.mapbox.mapboxsdk.events.MapListener;
import com.mapbox.mapboxsdk.events.RotateEvent;
import com.mapbox.mapboxsdk.events.ScrollEvent;
import com.mapbox.mapboxsdk.events.ZoomEvent;
import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.offline.OfflineMapDatabase;
import com.mapbox.mapboxsdk.offline.OfflineMapDownloader;
import com.mapbox.mapboxsdk.offline.OfflineMapDownloaderListener;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.tileprovider.tilesource.ITileLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.MapboxTileLayer;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.MapViewListener;
import com.mapbox.mapboxsdk.views.util.TilesLoadedListener;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainTestFragmentEn extends Fragment implements MapboxConstants, OfflineMapDownloaderListener
{
    private MapView mv;
    private String mainMap = "bulgakovmap.l75gjll2";
    private SharedPreferences prefs = null;
    private ArrayList<Integer> distance;
    private AsyncTask updateTask;
    private ArrayList<LatLng> points;
    private final String TAG = "SaveMapOfflineTestFragment";
    private boolean saveMapIndicator = true;
    private ArrayList<Marker> markers;
    private Globals globals;
    private int matchIndex = -1;
    private ImageButton imageButton;
    private RelativeLayout frameLayout;
    private LinearLayout bubble;
    private TextView bubbleTitle;
    private Button labelButton;
    private int positionMain = -1;
    private ArrayList<String> placesNames;
    private boolean showBubble = false;
    private boolean markersShowed = false;
    private ArrayList<Integer> indexesNames;
    private ArrayList<Integer> indexesPoints;
    private RelativeLayout mainLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_maintest, container, false);

        indexesPoints = new ArrayList<Integer>();
        indexesNames = new ArrayList<Integer>();

        mainLayout = (RelativeLayout)view.findViewById(R.id.mainLayout);
        bubble = (LinearLayout)view.findViewById(R.id.bubble);
        bubbleTitle = (TextView)view.findViewById(R.id.title);
        mainLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                ArrayList<String> placesAddresses;
                ArrayList<String> placesDescription;
                ArrayList<String> placesTextMain;

                if (globals.getLanguage().equals("ru"))
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

                globals.setPositionCustomInfo(positionMain);
                Intent intent = new Intent(getActivity(), PlacesCardsActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if(!indexesPoints.isEmpty())
                {
                    intent.putExtra("name", placesNames.get(indexesPoints.get(positionMain)));
                    intent.putExtra("fullName", placesNames.get(indexesPoints.get(positionMain)));
                    intent.putExtra("address", placesAddresses.get(indexesPoints.get(positionMain)));
                    intent.putExtra("description", placesDescription.get(indexesPoints.get(positionMain)));
                    intent.putExtra("textMain", placesTextMain.get(indexesPoints.get(positionMain)));

                    globals.setPositionMain(indexesPoints.get(positionMain));

                    Log.e("positionMain ", String.valueOf(indexesPoints.get(positionMain)));
                }
                else
                {
                    intent.putExtra("name", placesNames.get(positionMain));
                    intent.putExtra("fullName", placesNames.get(positionMain));
                    intent.putExtra("address", placesAddresses.get(positionMain));
                    intent.putExtra("description", placesDescription.get(positionMain));
                    intent.putExtra("textMain", placesTextMain.get(positionMain));

                    globals.setPositionMain(positionMain);

                    Log.e("positionMain ", String.valueOf(positionMain));
                }

                globals.setFlagCustomInfo("CustomInfoWindow");

                globals.setFlagCustomAdapterPlacesList("Main");

                getActivity().startActivity(intent);
            }
        });

        frameLayout = (RelativeLayout)view.findViewById(R.id.mapContainer);

        globals = Globals.getInstance();

        markers = new ArrayList<Marker>();
        mv = (MapView) view.findViewById(R.id.mapview);

        replaceMapView(mainMap);
        mv.loadFromGeoJSONURL("https://gist.githubusercontent.com/bleege/133920f60eb7a334430f/raw/5392bad4e09015d3995d6153db21869b02f34d27/map.geojson");

        labelButton = (Button)view.findViewById(R.id.labelButton);
        labelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                globals = Globals.getInstance();

                if(isAdded())
                {
                    if(labelButton.getText().equals(getResources().getString(R.string.mark_passed)))
                    {
                        labelButton.setText(getResources().getString(R.string.uncheck));

                        if (positionMain != -1)
                        {
                            markers.get(positionMain)
                                    .setIcon(new Icon(getActivity().getResources().getDrawable(R.drawable.inactive_place)));
                        } else
                        {
                            markers.get(globals.getPositionPlacesList())
                                    .setIcon(new Icon(getActivity().getResources().getDrawable(R.drawable.inactive_place)));
                        }
                    }
                    else
                    {
                        labelButton.setText(getResources().getString(R.string.mark_passed));

                        if (positionMain != -1)
                        {
                            markers.get(positionMain)
                                    .setIcon(new Icon(getActivity().getResources().getDrawable(R.drawable.active_place)));
                        } else
                        {
                            markers.get(globals.getPositionPlacesList())
                                    .setIcon(new Icon(getActivity().getResources().getDrawable(R.drawable.active_place)));
                        }
                    }
                }
            }
        });

        imageButton = (ImageButton) view.findViewById(R.id.locationButton);
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDialogLocation();
            }
        });

        prefs = getActivity().getSharedPreferences("com.mapbox.mapboxsdk.android.testapp", Context.MODE_PRIVATE);

        distance = new ArrayList<Integer>();
        points = new ArrayList<LatLng>();

        mv.setOnTilesLoadedListener(new TilesLoadedListener()
        {
            @Override
            public boolean onTilesLoaded()
            {
                return false;
            }

            @Override
            public boolean onTilesLoadStarted()
            {
                // TODO Auto-generated method stub
                return false;
            }
        });
        mv.setVisibility(View.VISIBLE);

        if(isNetworkAvailable())
        {
            if (globals.isFirstLaunchMainTest())
            {

                updateTask = new GETUpdateTask(getActivity(), this)
                        .execute("http://mobile.bulgakovmuseum.ru/api/list/places");
                AsyncTask task;
                task = new GETRoutesUpdateTask(getActivity())
                        .execute("http://mobile.bulgakovmuseum.ru/api/list/routes");


                globals.setFirstLaunchMainTest(false);
            }
            else
            {
                if(!globals.isFlagRoutesInfo())
                {
                    showMarkers();
                }
                else
                {
                    /*
                    RelativeLayout topBar = (RelativeLayout)getActivity().findViewById(R.id.topBar);

                    RelativeLayout topBarRoutes = (RelativeLayout)getActivity().findViewById(R.id.topBarRoutes);
                    ImageView belowTopBar = (ImageView)getActivity().findViewById(R.id.belowTopBarRoutes);
                    RelativeLayout backLayout = (RelativeLayout)getActivity().findViewById(R.id.backLayout);

                    topBar.setVisibility(View.GONE);

                    topBarRoutes.setVisibility(View.VISIBLE);
                    belowTopBar.setVisibility(View.VISIBLE);

                    backLayout.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            getActivity().finish();
                        }
                    });
                    */
                    showRoutesMarkers();
                }
            }
        }
        else
        {
            loadMap();
            if(!globals.isFlagRoutesInfo())
            {
                showMarkers();
            }
            else
            {
                showRoutesMarkers();
            }
        }

        if(globals.isShowPlacePlacesCards())
        {
            Log.e("isShowPlacePlacesCards", "isShowPlacePlacesCards");
            showBubble = true;
            if(globals.getFlagCustomAdapterPlacesList().equals("List"))
            {
                Log.e("List", "List");

                positionMain = globals.getPositionPlacesList();

                mv.setCenter(points.get(globals.getPositionPlacesList()));

                bubble.setVisibility(View.VISIBLE);
                bubbleTitle.setText(markers.get(positionMain).getTitle());
                globals.setFlagCustomAdapterPlacesList("");
            }
            else if(globals.getFlagCustomAdapterPlacesList().equals("Tag"))
            {
                Log.e("Tag", "Tag");

                for(int i = 0; i < markers.size(); i++)
                {
                    if (markers.get(i).getPoint().distanceTo(globals.getPointsTagPlace().get(globals.getPositionTagPlace())) <= 1)
                    {
                        matchIndex = i;
                        //closeBubble(i);
                    }
                }
                positionMain = matchIndex;

                mv.setCenter(points.get(positionMain));

                bubble.setVisibility(View.VISIBLE);
                bubbleTitle.setText(markers.get(positionMain).getTitle());

                globals.setFlagCustomAdapterPlacesList("");
            }
            else if(globals.getFlagCustomAdapterPlacesList().equals("Search"))
            {
                Log.e("Search", "Search");

                for(int i = 0; i < markers.size(); i++)
                {
                    if (markers.get(i).getPoint().distanceTo(globals.getPointsSearch().get(globals.getPositionSearchActivity())) <= 1)
                    {
                        matchIndex = i;
                        //closeBubble(i);
                    }
                }
                positionMain = matchIndex;

                mv.setCenter(points.get(positionMain));

                bubble.setVisibility(View.VISIBLE);
                bubbleTitle.setText(markers.get(positionMain).getTitle());
            }
            mv.setZoom(19);

            globals.setFlagCustomAdapterPlacesList("");
            globals.setShowPlacePlacesCards(false);
        }
        else if(globals.isFlagRoutesInfo())
        {
            Log.e("isFlagRoutesInfo", "isFlagRoutesInfo");

            globals.setFlagRoutesInfo(false);

            Paint linePaint = new Paint();
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setColor(Color.RED);
            linePaint.setStrokeWidth(8);

            PathOverlay pathOverlay = new PathOverlay().setPaint(linePaint);
            String[] strokes;
            LatLng tempPoint;
            LatLng firstPoint = new LatLng(55.7673, 37.5934);
            ArrayList<String> routesPointsFile = readFromFile("routesPoints" + globals.getPositionRoutesInfo() + ".txt");

            for(int index = 0; index < routesPointsFile.size(); index++)
            {
                strokes = routesPointsFile.get(index).split(",");
                tempPoint = new LatLng(Double.parseDouble(strokes[0]), Double.parseDouble(strokes[1]));
                if(!strokes[0].equals("0.0"))
                    firstPoint = tempPoint;
                pathOverlay.addPoint(tempPoint);
            }
            mv.getOverlays().add(pathOverlay);
            mv.setCenter(firstPoint);
            mv.setZoom(13);
        }

        return view;
    }

    protected void replaceMapView(String layer)
    {
        ITileLayer source;
        BoundingBox box;
        source = new MapboxTileLayer(layer);

        mv.setTileSource(source);
        box = source.getBoundingBox();

        mv.setScrollableAreaLimit(box);
        mv.setMinZoomLevel(1);
        mv.setMaxZoomLevel(19);
        mv.setCenter(new LatLng(55.767363, 37.592843));
        mv.setZoom(14);
    }

    public void GPSEnabled()
    {
        mv.setUserLocationEnabled(true)
                .setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW);

        if(mv.getUserLocation() != null)
        {
            LatLng startingPoint = new LatLng(mv.getUserLocation().getLatitude(), mv.getUserLocation().getLongitude());

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();

            ArrayList<String> sortedNameArray;
            ArrayList<String> sortedAddressArray;
            ArrayList<String> sortedDescriptionArray;
            ArrayList<String> sortedTextMainArray;
            ArrayList<String> sortedNameThemesArray;

            ArrayList<String> sortedNameEnArray;
            ArrayList<String> sortedAddressEnArray;
            ArrayList<String> sortedDescriptionEnArray;
            ArrayList<String> sortedTextMainEnArray;
            ArrayList<String> sortedNameThemesEnArray;

            sortedNameArray = readFromFile("nameArray.txt");
            sortedAddressArray = readFromFile("addressArray.txt");
            sortedDescriptionArray = readFromFile("descriptionArray.txt");
            sortedTextMainArray = readFromFile("textMainArray.txt");
            sortedNameThemesArray = readFromFile("nameThemesArrayForSearch.txt");

            if(sortedNameThemesArray.size() < points.size())
            {
                int difference = points.size() - sortedNameThemesArray.size();
                for(int spike = 0; spike < difference; spike++)
                {
                    sortedNameThemesArray.add("null");
                }
            }

            sortedNameEnArray = readFromFile("nameEnArray.txt");
            sortedAddressEnArray = readFromFile("addressEnArray.txt");
            sortedDescriptionEnArray = readFromFile("descriptionEnArray.txt");
            sortedTextMainEnArray = readFromFile("textMainEnArray.txt");
            sortedNameThemesEnArray = readFromFile("nameTagEnArrayForSearch.txt");

            if(sortedNameThemesEnArray.size() < points.size())
            {
                int difference = points.size() - sortedNameThemesEnArray.size();
                for(int spike = 0; spike < difference; spike++)
                {
                    sortedNameThemesEnArray.add("null");
                }
            }

            distance = new ArrayList<Integer>();

            for (int i = 0; i < points.size(); i++)
            {
                distance.add(startingPoint.distanceTo(points.get(i)));
            }
            Log.e("distance ", distance.toString());

            int tempDistance;
            LatLng tempPoints;
            String tempName;
            String tempAddress;
            String tempDescription;
            String tempTextMain;

            String tempNameEn;
            String tempAddressEn;
            String tempDescriptionEn;
            String tempTextMainEn;

            String name0;
            String name1;
            String nameTemp;

            String nameTheme0;
            String nameTheme1;
            String nameThemeTemp;

            String nameTag0;
            String nameTag1;
            String nameTagTemp;
            SwapFiles s;

            //Bubble sort
            for( int index = distance.size() - 1; index >= 0; index-- )
            {
                for( int j = 0; j < index; j++ )
                {
                    if(distance.get(j) > distance.get(j + 1))
                    {
                        tempDistance = distance.get(j);
                        distance.set(j, distance.get(j + 1));
                        distance.set(j + 1, tempDistance);

                        tempPoints = points.get(j);
                        points.set(j, points.get(j + 1));
                        points.set(j + 1, tempPoints);

                        tempName = sortedNameArray.get(j);
                        sortedNameArray.set(j, sortedNameArray.get(j + 1));
                        sortedNameArray.set(j + 1, tempName);

                        tempAddress = sortedAddressArray.get(j);
                        sortedAddressArray.set(j, sortedAddressArray.get(j + 1));
                        sortedAddressArray.set(j + 1, tempAddress);

                        tempDescription = sortedDescriptionArray.get(j);
                        sortedDescriptionArray.set(j, sortedDescriptionArray.get(j + 1));
                        sortedDescriptionArray.set(j + 1, tempDescription);

                        tempTextMain = sortedTextMainArray.get(j);
                        sortedTextMainArray.set(j, sortedTextMainArray.get(j + 1));
                        sortedTextMainArray.set(j + 1, tempTextMain);

                        tempNameEn = sortedNameEnArray.get(j);
                        sortedNameEnArray.set(j, sortedNameEnArray.get(j + 1));
                        sortedNameEnArray.set(j + 1, tempNameEn);

                        tempAddressEn = sortedAddressEnArray.get(j);
                        sortedAddressEnArray.set(j, sortedAddressEnArray.get(j + 1));
                        sortedAddressEnArray.set(j + 1, tempAddressEn);

                        tempDescriptionEn = sortedDescriptionEnArray.get(j);
                        sortedDescriptionEnArray.set(j, sortedDescriptionEnArray.get(j + 1));
                        sortedDescriptionEnArray.set(j + 1, tempDescriptionEn);

                        tempTextMainEn = sortedTextMainEnArray.get(j);
                        sortedTextMainEnArray.set(j, sortedTextMainEnArray.get(j + 1));
                        sortedTextMainEnArray.set(j + 1, tempTextMainEn);

                        name0 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "BulgakovMoscow"
                                + "/urls" + j + ".txt";

                        name1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "BulgakovMoscow"
                                + "/urls" + (j+1) + ".txt";

                        nameTemp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "BulgakovMoscow"
                                + "/urls" + 999 + ".txt";

                        nameTheme0 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "BulgakovMoscow"
                                + "/nameThemesArray" + j + ".txt";

                        nameTheme1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "BulgakovMoscow"
                                + "/nameThemesArray" + (j+1) + ".txt";

                        nameThemeTemp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "BulgakovMoscow"
                                + "/nameThemesArray" + 999 + ".txt";

                        nameTag0 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "BulgakovMoscow"
                                + "/nameTagsArray" + j + ".txt";

                        nameTag1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "BulgakovMoscow"
                                + "/nameTagsArray" + (j+1) + ".txt";

                        nameTagTemp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "BulgakovMoscow"
                                + "/nameTagsArray" + 999 + ".txt";

                        s = new SwapFiles();
                        s.copy(name0, nameTemp);
                        s.copy(name1, name0);
                        s.copy(nameTemp, name1);
                        s.copy(nameTheme0, nameThemeTemp);
                        s.copy(nameTheme1, nameTheme0);
                        s.copy(nameThemeTemp, nameTheme1);
                        s.copy(nameTag0, nameTagTemp);
                        s.copy(nameTag1, nameTag0);
                        s.copy(nameTagTemp, nameTag1);
                    }
                }
            }

            writeInFile(distance, "distanceArray.txt");
            writeInFile(points, "points.txt");

            Log.e("pointsGPSEMABLED ", points.toString());

            writeInFile(sortedNameArray, "nameArray.txt");
            writeInFile(sortedAddressArray, "addressArray.txt");
            writeInFile(sortedDescriptionArray, "descriptionArray.txt");
            writeInFile(sortedTextMainArray, "textMainArray.txt");
            writeInFile(sortedNameThemesArray, "nameThemesArrayForSearch.txt");

            writeInFile(sortedNameEnArray, "nameEnArray.txt");
            writeInFile(sortedAddressEnArray, "addressEnArray.txt");
            writeInFile(sortedDescriptionEnArray, "descriptionEnArray.txt");
            writeInFile(sortedTextMainEnArray, "textMainEnArray.txt");
            writeInFile(sortedNameThemesEnArray, "nameThemesEnArrayForSearch.txt");

            editor.apply();
        }
        else
        {
            Toast.makeText(getActivity(), "Не удалось получить ваши координаты", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0)
        {
            switch (requestCode)
            {
                case 1:
                    GPSEnabled();
                    break;
                default:
                    break;
            }
        }
    }

    public void showDialogLocation()
    {
        GPSTracker gps = new GPSTracker(getActivity());

        // check if GPS enabled
        if(!(gps.canGetLocation()))
        {
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setMessage("Необходимо включить GPS. Перейти к настройкам?");
            ad.setPositiveButton("Да", new DialogInterface.OnClickListener()
            {

                public void onClick(DialogInterface dialog, int arg1)
                {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 1);
                }
            });
            ad.setNegativeButton("Нет", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int arg1)
                {

                }
            });
            ad.setCancelable(true);
            ad.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                public void onCancel(DialogInterface dialog)
                {

                }
            });
            ad.show();
        }
        else
        {
            GPSEnabled();
        }
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void loadMap()
    {
        /*
        OfflineMapDownloader offlineMapDownloader = OfflineMapDownloader.getOfflineMapDownloader(getActivity());
        ArrayList<OfflineMapDatabase> offlineMapDatabases = offlineMapDownloader.getMutableOfflineMapDatabases();
        if (offlineMapDatabases != null && offlineMapDatabases.size() > 0)
        {
            OfflineMapDatabase db = offlineMapDatabases.get(0);
            Log.e(TAG, "Загружаем карту...");
            OfflineMapTileProvider tp = new OfflineMapTileProvider(getActivity(), db);
            TilesOverlay offlineMapOverlay = new TilesOverlay(tp);
            mv.addOverlay(offlineMapOverlay);

            showMarkers();
        }
        else
        {
            Log.e(TAG, "Нет загруженных ранее карт, необходимо подключение к интернету.");
        }
        */
    }

    public void firstLaunch()
    {
        if (prefs.getBoolean("firstrun", true))
        {
            showDialogLocation();
            prefs.edit().putBoolean("firstrun", false).apply();
            updateSavingMap();
        }
    }

    public void updateSavingMap()
    {
        /*
        if (isNetworkAvailable())
        {
            OfflineMapDownloader offlineMapDownloader = OfflineMapDownloader.getOfflineMapDownloader(getActivity());


            if (offlineMapDownloader.isMapIdAlreadyAnOfflineMapDatabase(mainMap))
            {
                boolean result = offlineMapDownloader.removeOfflineMapDatabaseWithID(mainMap);
                Log.e("Delete ", String.format(MAPBOX_LOCALE, "Result of deletion attempt: '%s'", result));
            }

            Log.e(TAG, "Сохраняем карту...");
            BoundingBox boundingBox = mv.getBoundingBox();
            CoordinateSpan span = new CoordinateSpan(boundingBox.getLatitudeSpan(), boundingBox.getLongitudeSpan());
            CoordinateRegion coordinateRegion = new CoordinateRegion(mv.getCenter(), span);
            offlineMapDownloader.beginDownloadingMapID(mainMap,
                    coordinateRegion, (int) mv.getZoomLevel(), (int) mv.getZoomLevel());
        }
        */
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (prefs.getBoolean("firstrun", true) && updateTask != null && updateTask.getStatus().equals(AsyncTask.Status.FINISHED))
        {
            showDialogLocation();
            prefs.edit().putBoolean("firstrun", false).apply();
        }
    }

    @Override
    public void onStop()
    {
        super.onPause();
        if( (saveMapIndicator) && (mv.getBoundingBox()) != null && (mv.getCenter()) != new LatLng(0.0, 0.0, 0.0) )
        {
            updateSavingMap();
            saveMapIndicator = !saveMapIndicator;
        }
    }

    public static ArrayList<String> readFromFile(String fileName)
    {
        ArrayList<String> result = new ArrayList<String>();

        String FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "BulgakovMoscow";
        File sdPath = new File(FILEPATH);

        if (!sdPath.exists())
        {
            if(sdPath.mkdirs())
            {
                Log.e("Directory was created!", "...");
            }
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
                    result.add(str);
                }

                inputstream.close();
            }
        }
        catch (Throwable t)
        {
            Log.e("Error ", t.toString());
        }
        return result;
    }

    @Override
    public void stateChanged(OfflineMapDownloader.MBXOfflineMapDownloaderState newState)
    {

    }

    @Override
    public void initialCountOfFiles(Integer numberOfFiles)
    {

    }

    @Override
    public void progressUpdate(Integer numberOfFilesWritten, Integer numberOfFilesExcepted)
    {

    }

    @Override
    public void networkConnectivityError(Throwable error)
    {

    }

    @Override
    public void sqlLiteError(Throwable error)
    {

    }

    @Override
    public void httpStatusError(Throwable error)
    {

    }

    @Override
    public void completionOfOfflineDatabaseMap(OfflineMapDatabase offlineMapDatabase)
    {

    }

    public static class SwapFiles
    {
        //function to copy the contents of one file to another
        public void copy(String a, String b)
        {
            try
            {
                //copy the contents one file to another
                String temp = "";
                FileInputStream fis = new FileInputStream(a);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis, "iso-8859-1"));
                BufferedWriter bw = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(b), "iso-8859-1"));
                while ((temp = br.readLine()) != null)
                {
                    temp = StringEscapeUtils.unescapeJava(temp);

                    bw.write(temp);
                    bw.newLine();
                    bw.flush();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void writeInFile(ArrayList arrayList, String fileName)
    {
        try
        {
            String FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "BulgakovMoscow";
            File sdPath = new File(FILEPATH);

            if (!sdPath.exists())
            {
                sdPath.mkdirs();
            }

            File file =
                    new File(FILEPATH, fileName);
            FileOutputStream outputstream = new FileOutputStream(file);

            for(int index = 0; index < arrayList.size(); index++)
            {
                outputstream.write(arrayList.get(index).toString().getBytes("windows-1251"));
                if(index != arrayList.size() - 1)
                    outputstream.write("\n".getBytes("windows-1251"));
            }

            outputstream.close();
        } catch (Throwable t)
        {
            Log.e("SaveFile Error: ", t.toString());
        }
    }

    public void showRoutesMarkers()
    {
        ArrayList<String> visiblePointsNames;

        if(globals.getLanguage().equals("ru"))
        {
            visiblePointsNames = readFromFile("visiblePointsNames" + globals.getPositionRoutesInfo() + ".txt");
        }

        else
        {
            visiblePointsNames = readFromFile("visiblePointsNames" + globals.getPositionRoutesInfo() + ".txt");
        }

        String[] strokes;
        ArrayList<String> pointsFile = readFromFile("points.txt");
        LatLng tempPoint;

        if(globals.getLanguage().equals("ru"))
        {
            placesNames = readFromFile("nameArray.txt");
        }
        else
        {
            placesNames = readFromFile("nameEnArray.txt");
        }

        indexesPoints = new ArrayList<Integer>();
        indexesNames = new ArrayList<Integer>();

        ArrayList<LatLng> routesPoints = new ArrayList<LatLng>();

        //среди всех точек ищем те, названия которых совпадают с названиями точек маршрута
        for(int i = 0; i < pointsFile.size(); i++)
        {
            for(int k = 0; k < visiblePointsNames.size(); k++)
            {
                //если название видимой точки совпадает с названием из общего списка мест,
                //добавляем в routesPoints его координаты,
                //в indexesPoints добавляем его номер в общем списке мест
                //в indexesNames добавляем его номер среди видимых точек
                if (visiblePointsNames.get(k).equals(placesNames.get(i)))
                {
                    strokes = pointsFile.get(i).split(",");
                    tempPoint = new LatLng(Double.parseDouble(strokes[0]), Double.parseDouble(strokes[1]));
                    routesPoints.add(tempPoint);
                    indexesNames.add(k);
                    indexesPoints.add(i);
                }
            }
        }

        Drawable shape;

        if(isAdded())
        {
            shape = getResources().getDrawable(R.drawable.active_place);
            markers = new ArrayList<Marker>();

            for(int i = 0; i < routesPoints.size(); i++)
            {
                markers.add(new Marker(mv, visiblePointsNames.get(indexesNames.get(i)), "", routesPoints.get(i)));
                markers.get(i).setIcon(new Icon(shape));
                markers.get(i).setDescription(String.valueOf(i));

                mv.addMarker(markers.get(i));
            }

            //описываем дефолтный слушатель карты(для пузыря)
            mv.setMapViewListener(new MapViewListener()
            {
                @Override
                public void onShowMarker(MapView pMapView, Marker pMarker)
                {

                }

                @Override
                public void onHideMarker(MapView pMapView, Marker pMarker)
                {

                }

                @Override
                public void onTapMarker(MapView pMapView, Marker pMarker)
                {
                    //mv.addListener(MainTestFragment.this);
                    showBubble = true;
                    positionMain = Integer.parseInt(pMarker.getDescription());
                    bubbleTitle.setText(pMarker.getTitle());
                }

                @Override
                public void onLongPressMarker(MapView pMapView, Marker pMarker)
                {
                    //mv.addListener(MainTestFragment.this);
                    showBubble = true;
                    positionMain = Integer.parseInt(pMarker.getDescription());
                    bubbleTitle.setText(pMarker.getTitle());
                }

                @Override
                public void onTapMap(MapView pMapView, ILatLng pPosition)
                {
                    bubble.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLongPressMap(MapView pMapView, ILatLng pPosition)
                {
                    bubble.setVisibility(View.INVISIBLE);
                }
            });

            mv.addListener(new DelayedMapListener(new MapListener()
            {
                @Override
                public void onScroll(ScrollEvent scrollEvent)
                {
                    //Log.e("scrolling is ", "finished!");
                    if (showBubble)
                    {
                        //Log.e("showBubble ", "..");
                        bubble.setVisibility(View.VISIBLE);
                        showBubble = false;
                    } else
                    {
                        //Log.e("closeBubble", "..");
                        bubble.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onZoom(ZoomEvent zoomEvent)
                {
                    if (showBubble)
                    {
                        //Log.e("showBubble ", "..");
                        bubble.setVisibility(View.VISIBLE);
                        showBubble = false;
                    } else
                    {
                        //Log.e("closeBubble", "..");
                        bubble.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onRotate(RotateEvent event)
                {

                }
            }));
        }

        globals.setMarkersMainTest(markers);

        if( (saveMapIndicator) && (mv.getBoundingBox()) != null && (mv.getCenter()) != new LatLng(0.0, 0.0, 0.0) )
        {
            //updateSavingMap();
            saveMapIndicator = !saveMapIndicator;
        }
    }

    public void showMarkers()
    {
        Log.e("showMarkers", "showMarkers");
        if (!markersShowed)
        {
            ArrayList<String> placesNames;
            if (globals.getLanguage().equals("ru"))
            {
                placesNames = readFromFile("nameArray.txt");
            } else
            {
                placesNames = readFromFile("nameEnArray.txt");
            }

            String[] strokes;
            LatLng tempPoint;
            ArrayList<String> pointsFile = readFromFile("points.txt");

            points = new ArrayList<LatLng>();
            for (int i = 0; i < pointsFile.size(); i++)
            {
                strokes = pointsFile.get(i).split(",");
                tempPoint = new LatLng(Double.parseDouble(strokes[0]), Double.parseDouble(strokes[1]));
                points.add(tempPoint);
            }
            Log.e("points ", points.toString());


            Drawable shape;

            if (isAdded())
            {
                shape = getResources().getDrawable(R.drawable.active_place);

                Log.e("SHOW placesNames " + placesNames.size(), placesNames.toString());
                Log.e("SHOW points " + points.size(), points.toString());

                markers = new ArrayList<Marker>();
                for (int i = 0; i < points.size(); i++)
                {
                    markers.add(new Marker(mv, placesNames.get(i), "", points.get(i)));
                    markers.get(i).setIcon(new Icon(shape));
                    markers.get(i).setDescription(String.valueOf(i));

                    mv.addMarker(markers.get(i));
                }

                //описываем дефолтный слушатель карты(для пузыря)
                mv.setMapViewListener(new MapViewListener()
                {
                    @Override
                    public void onShowMarker(MapView pMapView, Marker pMarker)
                    {

                    }

                    @Override
                    public void onHideMarker(MapView pMapView, Marker pMarker)
                    {

                    }

                    @Override
                    public void onTapMarker(MapView pMapView, Marker pMarker)
                    {
                        showBubble = true;
                        positionMain = Integer.parseInt(pMarker.getDescription());
                        bubbleTitle.setText(pMarker.getTitle());
                        //bubble.setVisibility(View.VISIBLE);
                        //mv.addListener(MainTestFragment.this);
                        //Log.e("ONTAPMARKER ", "..");
                    }

                    @Override
                    public void onLongPressMarker(MapView pMapView, Marker pMarker)
                    {
                        showBubble = true;
                        positionMain = Integer.parseInt(pMarker.getDescription());
                        bubbleTitle.setText(pMarker.getTitle());
                    }

                    @Override
                    public void onTapMap(MapView pMapView, ILatLng pPosition)
                    {
                        bubble.setVisibility(View.INVISIBLE);
                        //Log.e("ONTAPMAP ", "ON TAP");
                    }

                    @Override
                    public void onLongPressMap(MapView pMapView, ILatLng pPosition)
                    {
                        bubble.setVisibility(View.INVISIBLE);
                    }
                });

                mv.addListener(new DelayedMapListener(new MapListener()
                {
                    @Override
                    public void onScroll(ScrollEvent scrollEvent)
                    {
                        //Log.e("scrolling is ", "finished!");
                        if (showBubble)
                        {
                            //Log.e("showBubble ", "..");
                            bubble.setVisibility(View.VISIBLE);
                            showBubble = false;
                        } else
                        {
                            //Log.e("closeBubble", "..");
                            bubble.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onZoom(ZoomEvent zoomEvent)
                    {
                        if (showBubble)
                        {
                            //Log.e("showBubble ", "..");
                            bubble.setVisibility(View.VISIBLE);
                            showBubble = false;
                        } else
                        {
                            //Log.e("closeBubble", "..");
                            bubble.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onRotate(RotateEvent event)
                    {

                    }
                }));
            }

            globals.setMarkersMainTest(markers);

        /*
        if( (saveMapIndicator) && (mv.getBoundingBox()) != null && (mv.getCenter()) != new LatLng(0.0, 0.0, 0.0) )
        {
            //updateSavingMap();
            saveMapIndicator = !saveMapIndicator;
        }
        */
            markersShowed = true;
        }
    }
}