package com.mapbox.mapboxsdk.android.testapp.util;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.android.testapp.activities.SearchActivity;
import com.mapbox.mapboxsdk.android.testapp.fragments.PlacesListFragment;

import java.util.ArrayList;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class CustomAdapterPlacesList extends BaseAdapter   implements OnClickListener {
    
	/*********** Declare Used Variables *********/
    private final Fragment fragment;
    private final Activity activity;
    private ArrayList data;
    private Globals globals;

    /*************  CustomAdapter Constructor *****************/
    public CustomAdapterPlacesList(Fragment a, ArrayList d)
    {

    	/********** Take passed values **********/
        fragment = a;
        data = d;

        /***********  Layout inflator to call external xml layout () **********************/
        activity = null;
    }

    public CustomAdapterPlacesList(Activity a, ArrayList d) {

        /********** Take passed values **********/
        activity = a;
        data = d;

        /***********  Layout inflator to call external xml layout () **********************/
        fragment = null;
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
        public TextView routeLength;
        public TextView placeName;
        public TextView address;
    }

    /*********** Depends upon data size called for each row , Create each ListView row ***********/
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View vi=convertView;
        LayoutInflater inflater;

        if(convertView==null)
        {
            if(fragment != null)
            {
                inflater = fragment.getActivity().getLayoutInflater();
            }
            else
            {
                inflater = activity.getLayoutInflater();
            }

            vi = inflater.inflate(R.layout.list_item_places_list, null);

            ViewHolder holder=new ViewHolder();

            holder.routeLength=(TextView)vi.findViewById(R.id.routeLength);
            holder.placeName=(TextView)vi.findViewById(R.id.placeName);
            holder.address=(TextView)vi.findViewById(R.id.address);

            vi.setTag(holder);
        }
        
        if(data.size() > 0 )
        {
            ListModel tempValues;
	        tempValues = (ListModel) data.get(position);
            ViewHolder holder = (ViewHolder) vi.getTag();
            Typeface gothic;

            if(fragment != null)
            {
                gothic = Typeface.createFromAsset(fragment.getActivity().getAssets(), "fonts/GTH75.otf");
            }
            else
            {
                gothic = Typeface.createFromAsset(activity.getAssets(), "fonts/GTH75.otf");
            }

            if(tempValues.getDistance().equals(" "))
            {
                holder.routeLength.setText("");
                holder.routeLength.setTypeface(gothic);
            }
            else if(!tempValues.getDistance().equals(""))
            {
                holder.routeLength.setText(tempValues.getDistance() + " м");
                holder.routeLength.setTypeface(gothic);
            }

            holder.placeName.setText(tempValues.getPlaceName());
            holder.placeName.setTypeface(gothic);

            if(!tempValues.getAddress().equals(""))
            {
                holder.address.setText(tempValues.getAddress());
                holder.address.setTypeface(gothic);
            }

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
            globals = Globals.getInstance();
            if(globals.getFlagCustomAdapterPlacesList().equals("List"))
            {
                PlacesListFragment sct = (PlacesListFragment) fragment;
                sct.onItemClick(mPosition);
            }
            else if(globals.getFlagCustomAdapterPlacesList().equals("Search"))
            {
                SearchActivity sct = (SearchActivity) activity;
                sct.onItemClick(mPosition);
            }
            globals.setPositionPlacesList(mPosition);
        }
    }
}