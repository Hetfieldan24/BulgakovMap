package com.mapbox.mapboxsdk.android.testapp.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.android.testapp.activities.MainActivity;
import com.mapbox.mapboxsdk.android.testapp.activities.TagPlaceActivity;

import java.util.ArrayList;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class CustomAdapterTagsResult extends BaseAdapter implements OnClickListener {

	/*********** Declare Used Variables *********/
    private final Activity activity;
    private ArrayList data;
    private Globals globals;

    /*************  CustomAdapter Constructor *****************/
    public CustomAdapterTagsResult(Activity a, ArrayList d) {

        /********** Take passed values **********/
        activity = a;
        data = d;
        /***********  Layout inflator to call external xml layout () **********************/

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
        public TextView placeName;
        public TextView address;
        public ImageButton button;
    }

    /*********** Depends upon data size called for each row , Create each ListView row ***********/
    public View getView(final int position, View convertView, ViewGroup parent)
    {
    	
        View vi=convertView;

        if(convertView==null)
        {
            LayoutInflater inflater = activity.getLayoutInflater();
            vi = inflater.inflate(R.layout.list_item_tags_result, null);

            ViewHolder holder=new ViewHolder();

            holder.placeName=(TextView)vi.findViewById(R.id.placeName);
            holder.address=(TextView)vi.findViewById(R.id.address);
            holder.button = (ImageButton)vi.findViewById(R.id.goTo);

            vi.setTag(holder);
        }
        
        if(data.size() > 0 )
        {
            ListModel tempValues;
	        tempValues = (ListModel) data.get(position);
            ViewHolder holder = (ViewHolder) vi.getTag();

            Typeface gothic = Typeface.createFromAsset(activity.getAssets(), "fonts/GTH75.otf");

            holder.placeName.setText(tempValues.getPlaceName());
            holder.placeName.setTypeface(gothic);
            holder.address.setText(tempValues.getAddress());
            holder.address.setTypeface(gothic);
            holder.button.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    globals = Globals.getInstance();

                    globals.setShowPlacePlacesCards(true);
                    globals.setPositionPlacesList(position);

                    activity.startActivity(new Intent(activity, MainActivity.class));
                }
            });

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
            TagPlaceActivity sct = (TagPlaceActivity) activity;
            sct.onItemClick(mPosition);

            globals = Globals.getInstance();
            globals.setPositionPlacesList(mPosition);

        }
    }
}