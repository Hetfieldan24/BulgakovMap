package com.mapbox.mapboxsdk.android.testapp.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.mapbox.mapboxsdk.android.testapp.fragments.MainTestFragment;
import com.mapbox.mapboxsdk.android.testapp.fragments.MainTestFragmentEn;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by hetfieldan24 on 12.09.2014.
 */
public class GETUpdateTask extends AsyncTask<String, Void, JSONObject>
{
    private Context context;
    private Fragment fragment;
    private ProgressDialog pd;

    public GETUpdateTask(Context context, Fragment fragment)
    {
        super();
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    protected void onPreExecute()
    {
        pd = new ProgressDialog(context);
        Globals globals = Globals.getInstance();

        if(globals.getLanguage().equals("ru"))
        {
            pd.setMessage("Идёт загрузка...");
            pd.setCancelable(false);
            pd.setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена",
                    new DialogInterface.OnClickListener()
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
        // если какой-то фейл, проверяем на null
        // фейл может быть по многим причинам: сервер упал, нет сети на устройстве и т.д.
        if (jsonData != null)
        {
            super.onPostExecute(jsonData);
            try
            {
                JSONArray data = jsonData.getJSONArray("places");

                String lastUpdate = jsonData.getString("lastUpdate");
                ArrayList<String> lastUpdateArray = new ArrayList<String>();
                lastUpdateArray.add(lastUpdate);
                Log.e("lastUpdate ", lastUpdate);

                cleanCash(new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + "BulgakovCash"));

                if (!(new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + "BulgakovMoscow/"
                        + "lastUpdateArray.txt").exists()) || (!lastUpdateArray.get(0).equals(
                        readFromFile("lastUpdateArray.txt").get(0))))
                {
                    String name,
                            address,
                            description,
                            textMain,
                            nameEn,
                            addressEn,
                            descriptionEn,
                            textMainEn,
                            nameThemes,
                            nameThemesEn,
                            nameTags,
                            nameTagsEn,
                            url,
                            previewUrl;
                    double lat, lng;
                    int cntUrl = 0;
                    int cntThemes = 0;
                    int cntTags = 0;
                    JSONObject objPictures;
                    JSONObject obj;
                    JSONArray pictures;
                    JSONObject objThemes;
                    JSONObject objTags;
                    JSONArray themes;
                    JSONArray tags;
                    JSONObject preview;

                    ArrayList<LatLng> points = new ArrayList<LatLng>();
                    ArrayList<Double> latArray = new ArrayList<Double>();
                    ArrayList<Double> lngArray = new ArrayList<Double>();
                    ArrayList<String> nameArray = new ArrayList<String>();
                    ArrayList<String> addressArray = new ArrayList<String>();
                    ArrayList<String> descriptionArray = new ArrayList<String>();
                    ArrayList<String> textMainArray = new ArrayList<String>();
                    ArrayList<String> urlArray = new ArrayList<String>();

                    ArrayList<String> nameEnArray = new ArrayList<String>();
                    ArrayList<String> addressEnArray = new ArrayList<String>();
                    ArrayList<String> descriptionEnArray = new ArrayList<String>();
                    ArrayList<String> textMainEnArray = new ArrayList<String>();

                    ArrayList<String> nameThemesArray = new ArrayList<String>();
                    ArrayList<String> nameThemesEnArray = new ArrayList<String>();
                    ArrayList<String> nameThemesArrayList = new ArrayList<String>();
                    ArrayList<String> nameThemesEnArrayList = new ArrayList<String>();

                    ArrayList<String> nameTagsArray = new ArrayList<String>();
                    ArrayList<String> nameTagsEnArray = new ArrayList<String>();

                    String DIR_SD = "BulgakovMoscow";
                    String FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/" + DIR_SD;
                    File sdPath = new File(FILEPATH);

                    try
                    {
                        if (sdPath.exists())
                        {
                            FileUtils.cleanDirectory(sdPath);
                            Log.e("Cleaning directory", "...");
                        }
                        else
                        {
                            sdPath.mkdirs();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < data.length(); i++)
                    {
                        obj = data.getJSONObject(i);

                        if (!obj.getString("invisible").equals("true"))
                        {
                            lat = Double.parseDouble(obj.getString("lat"));
                            lng = Double.parseDouble(obj.getString("lng"));
                            points.add(new LatLng(lat, lng));
                            latArray.add(lat);
                            lngArray.add(lng);

                            if (!(obj.getString("preview").equals("null")))
                            {
                                preview = obj.getJSONObject("preview");

                                previewUrl = preview.getString("url");

                                urlArray.add(previewUrl);
                            } else
                            {
                                urlArray.add("null");
                            }

                            if (!(obj.getString("pictures").equals("null")))
                            {
                                pictures = obj.getJSONArray("pictures");
                                for (int k = 0; k < pictures.length(); k++)
                                {
                                    objPictures = pictures.getJSONObject(k);
                                    url = objPictures.getString("url");

                                    urlArray.add(url);
                                }
                            } else
                            {
                                urlArray.add("null");
                            }

                            writeInFile(urlArray, "urls" + cntUrl + ".txt");
                            urlArray = new ArrayList<String>();
                            cntUrl++;

                            if (!obj.getString("themes").equals("null"))
                            {
                                themes = obj.getJSONArray("themes");
                                for (int k = 0; k < themes.length(); k++)
                                {
                                    objThemes = themes.getJSONObject(k);

                                    nameThemes = objThemes.getString("name");
                                    nameThemesEn = objThemes.getString("name_en");

                                    nameThemesArray.add(nameThemes);
                                    nameThemesEnArray.add(nameThemesEn);

                                    nameThemesArrayList.add(nameThemes);
                                    nameThemesEnArrayList.add(nameThemesEn);
                                }
                            } else
                            {
                                nameThemesArray.add("null");
                                nameThemesEnArray.add("null");
                            }

                            writeInFile(nameThemesArray, "nameThemesArray" + cntThemes + ".txt");
                            nameThemesArray = new ArrayList<String>();
                            writeInFile(nameThemesEnArray, "nameThemesEnArray" + cntThemes + ".txt");
                            nameThemesEnArray = new ArrayList<String>();
                            cntThemes++;

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

                            writeInFile(nameTagsArray, "nameTagsArray" + cntTags + ".txt");
                            nameTagsArray = new ArrayList<String>();
                            writeInFile(nameTagsEnArray, "nameTagsEnArray" + cntTags + ".txt");
                            nameTagsEnArray = new ArrayList<String>();
                            cntTags++;

                            if (!obj.getString("name").equals(""))
                                name = obj.getString("name");
                            else
                                name = "null";

                            if (!obj.getString("name_en").equals(""))
                                nameEn = obj.getString("name_en");
                            else
                                nameEn = "null";

                            if (!obj.getString("address").equals(""))
                                address = obj.getString("address");
                            else
                                address = "null";

                            if (!obj.getString("address_en").equals(""))
                                addressEn = obj.getString("address_en");
                            else
                                addressEn = "null";

                            if (!obj.getString("description").equals(""))
                                description = obj.getString("description");
                            else
                                description = "null";

                            if (obj.getString("text_main").equals(""))
                                textMain = "null";
                            else
                                textMain = obj.getString("text_main");

                            if (obj.getString("description_en").equals(""))
                                descriptionEn = "null";
                            else
                                descriptionEn = obj.getString("description_en");

                            if (obj.getString("text_main_en").equals(""))
                                textMainEn = "null";
                            else
                                textMainEn = obj.getString("text_main_en");

                            nameArray.add(name);
                            nameEnArray.add(nameEn);

                            addressArray.add(address);
                            descriptionArray.add(description);
                            textMainArray.add(textMain);

                            addressEnArray.add(addressEn);
                            descriptionEnArray.add(descriptionEn);
                            textMainEnArray.add(textMainEn);

                        }
                    }

                    writeInFile(lastUpdateArray, "lastUpdateArray.txt");

                    ArrayList<Integer> cntThemesArray = new ArrayList<Integer>();
                    cntThemesArray.add(cntThemes);
                    writeInFile(cntThemesArray, "cntThemes.txt");

                    ArrayList<Integer> cntTagsArray = new ArrayList<Integer>();
                    cntTagsArray.add(cntTags);
                    writeInFile(cntTagsArray, "cntTags.txt");

                    writeInFile(deleteDuplicates(nameThemesArrayList), "nameThemesArrayList.txt");
                    writeInFile(deleteDuplicates(nameThemesEnArrayList), "nameThemesEnArrayList.txt");

                    writeInFile(latArray, "latArray.txt");
                    writeInFile(lngArray, "lngArray.txt");
                    writeInFile(points, "points.txt");

                    writeInFile(nameArray, "nameArray.txt");
                    writeInFile(addressArray, "addressArray.txt");
                    writeInFile(descriptionArray, "descriptionArray.txt");
                    writeInFile(textMainArray, "textMainArray.txt");

                    writeInFile(nameEnArray, "nameEnArray.txt");
                    writeInFile(addressEnArray, "addressEnArray.txt");
                    writeInFile(descriptionEnArray, "descriptionEnArray.txt");
                    writeInFile(textMainEnArray, "textMainEnArray.txt");
                }
                else if (lastUpdateArray.get(0).equals(
                        readFromFile("lastUpdateArray.txt").get(0)))
                {
                    Log.e("no update, last was ", lastUpdate);
                }

                String locale = context.getResources().getConfiguration().locale.toString();

                if (locale.toLowerCase().equals("ru_ru"))
                {
                    MainTestFragment mainTestFragment = (MainTestFragment) fragment;
                    mainTestFragment.showMarkers();
                    mainTestFragment.firstLaunch();
                } else
                {
                    MainTestFragmentEn mainTestFragment = (MainTestFragmentEn) fragment;
                    mainTestFragment.showMarkers();
                    mainTestFragment.firstLaunch();
                }
            }
            catch (JSONException e)
            {
                Toast.makeText(context, "Not successful! JSONException: " + e,
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        else
        {
            Log.e("JSONDATA NULL ", "...");
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
            Log.e("IllegalArgumException", e.getMessage());
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

        // посылаем запрос методом GET
        JSONObject json = jParser.makeHttpRequest(url, "GET", params);

        return json;
    }

    public ArrayList<String> deleteDuplicates(ArrayList<String> array)
    {
        return new ArrayList<String>(new HashSet<String>(array));
    }

    public void writeInFile(ArrayList arrayList, String fileName)
    {
        try
        {
            String FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + "BulgakovMoscow";
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
            Toast.makeText(context,
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
        return result;
    }

    private void cleanCash(File fileCash)
    {
        if(!fileCash.exists())
            return;
        if(fileCash.isDirectory())
        {
            for(File f : fileCash.listFiles())
                cleanCash(f);
            fileCash.delete();
        }
        else
        {
            fileCash.delete();
        }
    }

}