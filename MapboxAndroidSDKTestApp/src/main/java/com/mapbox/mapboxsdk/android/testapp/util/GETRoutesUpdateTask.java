package com.mapbox.mapboxsdk.android.testapp.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.mapbox.mapboxsdk.android.testapp.activities.RoutesActivity;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hetfieldan24 on 12.09.2014.
 */
public class GETRoutesUpdateTask extends AsyncTask<String, Void, JSONObject>
{
    private ProgressDialog pd;
    private Activity activity;
    private Globals globals;

    public GETRoutesUpdateTask(Activity activity)
    {
        super();
        this.activity = activity;
    }

    @Override
    protected void onPreExecute()
    {
        pd = new ProgressDialog(activity);

        globals = Globals.getInstance();

        if(globals.getLanguage().equals("ru"))
        {
            pd.setMessage("Идёт загрузка...");
            pd.setCancelable(false);
            pd.setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
        }
        else
        {
            pd.setMessage("Loading...");
            pd.setCancelable(false);
            pd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
        }
        pd.show();

        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... urls)
    {
        return loadJSON(urls[0]);
    }

    @Override
    protected void onPostExecute(JSONObject jsonData)
    {
        if (jsonData != null)
        {
            super.onPostExecute(jsonData);
            try
            {
                String lastUpdateRoutes = jsonData.getString("lastUpdate");
                ArrayList<String> lastUpdateRoutesArray = new ArrayList<String>();
                lastUpdateRoutesArray.add(lastUpdateRoutes);
                Log.e("lastUpdate ", lastUpdateRoutes);

                if(!(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "BulgakovMoscow/" + "lastUpdateRoutesArray.txt").exists()) || (!lastUpdateRoutesArray.get(0).equals(
                        readFromFile("lastUpdateRoutesArray.txt").get(0))))
                {
                    JSONArray data = jsonData.getJSONArray("routes");

                    ArrayList<LatLng> routesPoints = new ArrayList<LatLng>();
                    ArrayList<String> routesDistance = new ArrayList<String>();
                    ArrayList<String> routesDuration = new ArrayList<String>();
                    ArrayList<String> routesDescription = new ArrayList<String>();
                    ArrayList<String> routesNameArray = new ArrayList<String>();
                    ArrayList<String> routesNumberOfPoints = new ArrayList<String>();
                    ArrayList<String> routesUrls = new ArrayList<String>();
                    ArrayList<String> routesDescriptionEn = new ArrayList<String>();
                    ArrayList<String> routesNameEnArray = new ArrayList<String>();
                    ArrayList<String> visiblePointsNames = new ArrayList<String>();
                    ArrayList<LatLng> visiblePoints = new ArrayList<LatLng>();

                    String[] strokes;
                    JSONArray polyline;
                    JSONObject obj;
                    double tempDistance;
                    double tempDuration;
                    String hoursDuration;
                    String minutesDuration;
                    int cnt = 0;
                    int cntVisiblePoints = 0;
                    JSONArray points;
                    JSONObject objPoints;
                    String tempString;
                    JSONObject pictures;
                    String url;
                    int intPart;
                    int doublePart;
                    double lat;
                    double lng;
                    JSONObject objTags;
                    JSONArray tags;
                    String nameTags,
                            nameTagsEn;
                    ArrayList<String> nameTagsArray = new ArrayList<String>();
                    ArrayList<String> nameTagsEnArray = new ArrayList<String>();
                    int cntTagsRoutes = 0;

                    for (int i = 0; i < data.length(); i++)
                    {
                        obj = data.getJSONObject(i);

                        routesNameArray.add(obj.getString("name"));
                        routesDescription.add(obj.getString("text_main"));

                        routesNameEnArray.add(obj.getString("name_en"));
                        routesDescriptionEn.add(obj.getString("text_main_en"));

                        if (!(obj.getString("preview").equals("null")))
                        {
                            pictures = obj.getJSONObject("preview");
                            url = pictures.getString("url");

                            routesUrls.add(url);
                        } else
                        {
                            routesUrls.add("null");
                        }

                        points = obj.getJSONArray("points");
                        for (int index = 0; index < points.length(); index++)
                        {
                            objPoints = points.getJSONObject(index);
                            if (!objPoints.getString("invisible").equals("true"))
                            {
                                cntVisiblePoints++;
                                visiblePointsNames.add(objPoints.getString("name"));

                                /*
                                lat = Double.parseDouble(obj.getString("lat"));
                                lng = Double.parseDouble(obj.getString("lng"));

                                visiblePoints.add(new LatLng(lat, lng));
                                */
                            }
                        }
                        routesNumberOfPoints.add(String.valueOf(cntVisiblePoints) + " мест");
                        cntVisiblePoints = 0;
                        writeInFile(visiblePointsNames, "visiblePointsNames" + i + ".txt");
                        visiblePointsNames = new ArrayList<String>();

                        /*
                        writeInFile(visiblePoints, "visiblePoints" + i + ".txt");
                        visiblePoints = new ArrayList<>();
                        */

                        if (!obj.getString("distance").equals("null"))
                        {

                            if((obj.getString("distance").contains(",")) && obj.getString("distance").contains("км"))
                            {
                                tempDistance = Double.valueOf(obj.getString("distance")
                                                .replaceAll(",", ".")
                                                .substring(0, obj.getString("distance").length() - 3));
                            }
                            else if(obj.getString("distance").contains(","))
                            {
                                tempDistance = Double.valueOf(obj.getString("distance")
                                        .replaceAll(",", "."));
                            }
                            else if(obj.getString("distance").contains("км"))
                            {
                                tempDistance = Double.valueOf(obj.getString("distance")
                                        .substring(0, obj.getString("distance").length() - 3));
                            }
                            else
                            {
                                tempDistance = Double.valueOf(obj.getString("distance"));
                            }
                            tempDistance = tempDistance * 1000;
                            routesDistance.add((int) tempDistance + " м");
                        } else
                            routesDistance.add(0 + " м");

                        if (!obj.getString("duration").equals("null"))
                        {
                            if((obj.getString("duration").contains(",")))
                            {
                                tempDuration = Double.valueOf(obj.getString("duration").replaceAll(",", "."));
                                intPart = (int) tempDuration;
                                doublePart = (int) ((tempDuration - intPart) * 60);
                            }
                            else if((obj.getString("duration").contains("ч"))
                                    && (obj.getString("duration").contains("м")))
                            {
                                intPart = Integer.parseInt((obj.getString("duration")
                                        .substring(0, obj.getString("duration").indexOf("ч") - 1)));
                                doublePart = Integer.parseInt((obj.getString("duration")
                                        .substring(obj.getString("duration").indexOf("м") - 3,
                                                obj.getString("duration").indexOf("м") - 1)));
                            }
                            else if((obj.getString("duration").contains("ч")))
                            {
                                intPart = Integer.parseInt((obj.getString("duration")
                                        .substring(0, obj.getString("duration").indexOf("ч") - 1)));
                                doublePart = 0;
                            }
                            else
                            {
                                tempDuration = Double.valueOf(obj.getString("duration"));
                                intPart = (int) tempDuration;
                                doublePart = (int) ((tempDuration - intPart) * 60);
                            }

                            if(globals.getLanguage().equals("ru"))
                            {
                                if ((intPart % 10) == 1 && (intPart != 11))
                                    hoursDuration = " час";
                                else if (((intPart % 10) == 2 && (intPart != 12))
                                        || ((intPart % 10) == 3 && (intPart != 13))
                                        || (intPart % 10) == 4 && (intPart != 14))
                                    hoursDuration = " часа";
                                else
                                    hoursDuration = " часов";

                                if ((doublePart % 10) == 1 && (doublePart != 11))
                                    minutesDuration = " минута";
                                else if (((doublePart % 10) == 2 && (doublePart != 12))
                                        || ((doublePart % 10) == 3 && (doublePart != 13))
                                        || (doublePart % 10) == 4 && (doublePart != 14))
                                    minutesDuration = " минуты";
                                else
                                    minutesDuration = " минут";
                            } else
                            {
                                if (intPart == 1)
                                    hoursDuration = " hour";
                                else
                                    hoursDuration = " hours";

                                if (doublePart == 1)
                                    minutesDuration = " minute";
                                else
                                    minutesDuration = " minutes";
                            }

                            if (intPart != 0 && doublePart != 0)
                                tempString = String.valueOf(intPart) + hoursDuration + " " +
                                        String.valueOf(doublePart) + minutesDuration;
                            else if (intPart != 0 && doublePart == 0)
                                tempString = String.valueOf(intPart) + hoursDuration;
                            else if (intPart == 0 && doublePart != 0)
                                tempString = String.valueOf(doublePart) + minutesDuration;
                            else
                                tempString = String.valueOf(0);
                            routesDuration.add(tempString);
                        } else
                            routesDuration.add(String.valueOf(0));

                        if (obj.getString("polyline").equals("null"))
                        {
                            routesPoints.add(new LatLng(0, 0));
                        } else
                        {
                            polyline = obj.getJSONArray("polyline");

                            for (int k = 0; k < polyline.length(); k++)
                            {
                                strokes =
                                        polyline.getString(k).replaceAll("\\[", "").replaceAll("\\]", "").split(",");

                                routesPoints.add(new LatLng(Double.valueOf(strokes[0]), Double.valueOf(strokes[1])));
                            }
                        }
                        writeInFile(routesPoints, "routesPoints" + cnt + ".txt");
                        routesPoints = new ArrayList<LatLng>();
                        cnt++;

                        if (!obj.getString("tags").equals("null"))
                        {
                            tags = obj.getJSONArray("tags");
                            for (int k = 0; k < tags.length(); k++)
                            {
                                objTags = tags.getJSONObject(k);

                                nameTags = objTags.getString("name");
                                nameTagsEn = objTags.getString("name_en");

                                nameTagsArray.add(nameTags);
                                nameTagsEnArray.add(nameTagsEn);
                            }
                        } else
                        {
                            nameTagsArray.add("null");
                            nameTagsEnArray.add("null");
                        }

                        writeInFile(nameTagsArray, "nameTagsRoutesArray" + cntTagsRoutes + ".txt");
                        nameTagsArray = new ArrayList<String>();
                        writeInFile(nameTagsEnArray, "nameTagsEnRoutesArray" + cntTagsRoutes + ".txt");
                        nameTagsEnArray = new ArrayList<String>();
                        cntTagsRoutes++;
                    }

                    ArrayList<Integer> cntTagsRoutesArray = new ArrayList<Integer>();
                    cntTagsRoutesArray.add(cntTagsRoutes);
                    writeInFile(cntTagsRoutesArray, "cntTagsRoutes.txt");

                    writeInFile(routesNumberOfPoints, "routesNumberOfPoints.txt");
                    writeInFile(routesUrls, "routesUrls.txt");
                    writeInFile(routesNameArray, "routesNameArray.txt");
                    writeInFile(routesDuration, "routesDuration.txt");
                    writeInFile(routesDistance, "routesDistance.txt");
                    writeInFile(routesDescription, "routesDescription.txt");
                    writeInFile(routesNameEnArray, "routesNameEnArray.txt");
                    writeInFile(routesDescriptionEn, "routesDescriptionEn.txt");
                }
                else if(lastUpdateRoutesArray.get(0).equals(
                        readFromFile("lastUpdateArray.txt").get(0)))
                {
                    Log.e("no update, last update was ", lastUpdateRoutes);
                }
                Log.e("activity", activity.getLocalClassName());
                if(activity.getLocalClassName().equals("activities.RoutesActivity"))
                {
                    RoutesActivity sct = (RoutesActivity) activity;
                    sct.setContent();
                    sct = null;
                }
            }
            catch (JSONException e)
            {
                Toast.makeText(activity, "Not successful! JSONException: " + e, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        try
        {
            if ((this.pd != null) && (this.pd.isShowing()))
            {
                this.pd.dismiss();
            }
        }
        catch (final IllegalArgumentException e)
        {
            Log.e("IllegalArgumentException", e.getMessage());
        }
        catch (final Exception e)
        {
            Log.e("Exception", e.getMessage());
        }
        finally
        {
            this.pd = null;
        }
    }
    public JSONObject loadJSON(String url)
    {

        JSONParser jParser = new JSONParser();
        // здесь параметры необходимые в запрос добавляем
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("key", "Fmjtd|luu8216rng%2C85%3Do5-942nlr"));
        params.add(new BasicNameValuePair("from", "55.76735009999999,37.59345589999998"));
        params.add(new BasicNameValuePair("to", "55.76763846802284,37.59553670883179"));
        params.add(new BasicNameValuePair("shapeFormat", "raw"));
        params.add(new BasicNameValuePair("generalize", "0"));
        params.add(new BasicNameValuePair("locale", "ru_RU"));
        params.add(new BasicNameValuePair("routeType", "pedestrian"));

        // посылаем запрос методом GET

        return jParser.makeHttpRequest(url, "GET", params);
    }

    public void writeInFile(ArrayList arrayList, String fileName)
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
            Toast.makeText(activity,
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
        return result;
    }

}