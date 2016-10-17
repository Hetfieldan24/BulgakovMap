package com.mapbox.mapboxsdk.android.testapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.android.testapp.util.CustomAdapterTagsResult;
import com.mapbox.mapboxsdk.android.testapp.util.Globals;
import com.mapbox.mapboxsdk.android.testapp.util.ListModel;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TagPlaceActivity extends Activity implements View.OnClickListener
{
    private ArrayList<ListModel> valuesArr;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_place);

        ArrayList<ArrayList<String>> themesNames = new ArrayList<ArrayList<String>>();

        Globals globals = Globals.getInstance();

        globals.setFlagCustomAdapterPlacesList("Tag");

        TextView name = (TextView)findViewById(R.id.name);

        Typeface gothic = Typeface.createFromAsset(getAssets(), "fonts/GTH75.otf");

        name.setTypeface(gothic);

        name.setText(getIntent().getStringExtra("themeName"));

        RelativeLayout backLayout = (RelativeLayout) findViewById(R.id.backLayout);

        backLayout.setOnClickListener(this);

        String tempThemeName = getIntent().getStringExtra("themeName");

        if(globals.getLanguage().equals("ru"))
        {
            int cntThemes = Integer.parseInt(readFromFile("cntThemes.txt").get(0));

            for(int index = 0; index < cntThemes; index++)
            {
                themesNames.add(readFromFile("nameThemesArray" + index + ".txt"));
            }
        }
        else
        {
            int cntThemes = Integer.parseInt(readFromFile("cntThemes.txt").get(0));

            for(int index = 0; index < cntThemes; index++)
            {
                themesNames.add(readFromFile("nameThemesEnArray" + index + ".txt"));
            }
        }

        valuesArr = new ArrayList<ListModel>();

        ArrayList<LatLng> points = new ArrayList<LatLng>();
        ListModel sched;

        ArrayList<String> nameArray = new ArrayList<String>();
        ArrayList<String> addressArray = new ArrayList<String>();
        ArrayList<String> descriptionArray = new ArrayList<String>();
        ArrayList<String> textMainArray = new ArrayList<String>();

        ArrayList<String> nameEnArray = new ArrayList<String>();
        ArrayList<String> addressEnArray = new ArrayList<String>();
        ArrayList<String> descriptionEnArray = new ArrayList<String>();
        ArrayList<String> textMainEnArray = new ArrayList<String>();
        ArrayList<String> pointsFile;

        if(globals.getLanguage().equals("ru"))
        {
            nameArray = readFromFile("nameArray.txt");
            addressArray = readFromFile("addressArray.txt");
            descriptionArray = readFromFile("descriptionArray.txt");
            textMainArray = readFromFile("textMainArray.txt");
        }
        else
        {
            nameEnArray = readFromFile("nameEnArray.txt");
            addressEnArray = readFromFile("addressEnArray.txt");
            descriptionEnArray = readFromFile("descriptionEnArray.txt");
            textMainEnArray = readFromFile("textMainEnArray.txt");
        }
        pointsFile = readFromFile("points.txt");

        //определяем совпадения по тегам, сохраняем соответствующие имя/адрес
        for (int index = 0; index < themesNames.size(); index++)
        {
            for(int i = 0; i < themesNames.get(index).size(); i++)
            {
                if (tempThemeName.regionMatches(true,
                        0, themesNames.get(index).get(i),
                        0, tempThemeName.length()))
                {
                    Log.e("MATCH, ", index + "; " + i + "; " + themesNames.get(index).get(i));
                    sched = new ListModel();

                    if (globals.getLanguage().equals("ru"))
                    {
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
                    } else
                    {
                        sched.setPlaceName(nameEnArray.get(index));
                        if(!addressEnArray.get(index).equals("null"))
                        {
                            sched.setAddress(addressEnArray.get(index));
                        }
                        else
                        {
                            sched.setAddress(" ");
                        }
                        sched.setDescription(descriptionEnArray.get(index));
                        sched.setTextMain(textMainEnArray.get(index));
                    }
                    sched.setUrl(readFromFile("urls" + index + ".txt"));

                    LatLng tempPoint;
                    String[] strokes;

                    strokes = pointsFile.get(index).split(",");
                    tempPoint = new LatLng(Double.parseDouble(strokes[0]),
                            Double.parseDouble(strokes[1]));
                    points.add(tempPoint);

                    /******** Take Model Object in ArrayList **********/
                    valuesArr.add(sched);
                }
            }
        }

        globals.setPointsTagPlace(points);

        //выводим список соответствующих имен/адресов
        CustomAdapterTagsResult adapter =
                new CustomAdapterTagsResult(TagPlaceActivity.this, valuesArr);
        ListView list = (ListView)findViewById(R.id.listView);

        list.setAdapter(adapter);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.backLayout:
                finish();
                break;
        }
    }

    public void onItemClick(int mPosition)
    {
        Intent intenttwo = new Intent(TagPlaceActivity.this, PlacesCardsActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intenttwo.putExtra("name", valuesArr.get(mPosition).getPlaceName());
        intenttwo.putExtra("fullName", valuesArr.get(mPosition).getPlaceName());
        intenttwo.putExtra("address", valuesArr.get(mPosition).getAddress());
        intenttwo.putExtra("description", valuesArr.get(mPosition).getDescription());
        intenttwo.putExtra("textMain", valuesArr.get(mPosition).getTextMain());
        intenttwo.putExtra("url", valuesArr.get(mPosition).getUrl());

        Globals globals = Globals.getInstance();

        globals.setPositionTagPlace(mPosition);
        startActivity(intenttwo);
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
            Toast.makeText(TagPlaceActivity.this,
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
        return result;
    }
}
