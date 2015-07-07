package com.mapbox.mapboxsdk.android.testapp.util;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.android.testapp.activities.LanguageActivity;
import com.mapbox.mapboxsdk.android.testapp.activities.RoutesInfoActivity;
import com.mapbox.mapboxsdk.android.testapp.activities.SearchActivity;
import com.mapbox.mapboxsdk.android.testapp.activities.TextInfoActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class CustomAdapterSingleItem extends BaseAdapter implements OnClickListener
{

	/*********** Declare Used Variables *********/
    private final Activity activity;
    private ArrayList data;
    private Resources res;

    /*************  CustomAdapter Constructor *****************/
    public CustomAdapterSingleItem(Activity a, ArrayList d, Resources resLocal)
    {
    	/********** Take passed values **********/
        activity = a;
        data = d;
        res = resLocal;
    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {
    	
    	if(data.size()<=0)
    		return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    /********* Create a holder to contain inflated xml file elements ***********/
    public static class ViewHolder
    {
        public ImageButton button;
        public TextView tagName;
    }

    /*********** Depends upon data size called for each row , Create each ListView row ***********/
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View vi=convertView;

        if(convertView==null)
        {
            LayoutInflater inflater = activity.getLayoutInflater();
            vi = inflater.inflate(R.layout.list_item_single, null);

            ViewHolder holder = new ViewHolder();

            holder.tagName=(TextView)vi.findViewById(R.id.placeName);

            vi.setTag(holder);
        }
        
        if(data.size() > 0 )
        {
            ListModel tempValues;
	        tempValues = (ListModel) data.get(position);
            ViewHolder holder = (ViewHolder) vi.getTag();

            Typeface gothic = Typeface.createFromAsset(activity.getAssets(), "fonts/GTH75.otf");

            holder.tagName.setText(tempValues.getTagName());
            holder.tagName.setTypeface(gothic);

            vi.setOnClickListener(new OnItemClickListener(position));
        }
        return vi;
    }
    
    @Override
    public void onClick(View v) {
            Log.v("CustomAdapter", "=====Row button clicked");
    }
    
    /********* Called when Item click in ListView ************/
    private class OnItemClickListener implements OnClickListener{
        private int mPosition;
        
        OnItemClickListener(int position){
        	 mPosition = position;
        }
        
        @Override
        public void onClick(View arg0)
        {
            Globals globals = Globals.getInstance();

            if(globals.isCurrentlyRunningInfoActivity())
            {
                Intent intent = new Intent(activity, TextInfoActivity.class);

                if(globals.getItemNames().get(mPosition).equals(res.getString(R.string.options)))
                {
                    intent = new Intent(activity, LanguageActivity.class);
                }

                if(globals.getItemNames().get(mPosition).equals(res.getString(R.string.create_response)))
                {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps"));
                }

                intent.putExtra("position", mPosition);

                activity.finish();
                activity.startActivity(intent);
            }

            else if(globals.isCurrentlyRunningRoutesActivity())
            {
                globals.setPositionCustomAdapterSingleItem(mPosition);

                Intent intent = new Intent(activity, RoutesInfoActivity.class);

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

                Log.e("description yes", routesDescription.get(mPosition));

                intent.putExtra("name", itemNames.get(mPosition));
                intent.putExtra("description", routesDescription.get(mPosition));
                intent.putExtra("distance", routesDistance.get(mPosition));
                intent.putExtra("duration", routesDuration.get(mPosition));
                intent.putExtra("numberOfPoints", routesNumberOfPoints.get(mPosition));

                activity.startActivity(intent);
            }

            else
            {
                SearchActivity sct = (SearchActivity) activity;
                sct.onItemClick(mPosition);

                globals.setPositionPlacesList(mPosition);
            }
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