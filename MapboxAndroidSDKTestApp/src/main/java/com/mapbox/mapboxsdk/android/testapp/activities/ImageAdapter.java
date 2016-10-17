package com.mapbox.mapboxsdk.android.testapp.activities;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends PagerAdapter
{

    private List<Bitmap> drawable;

    ImageAdapter(ArrayList<Bitmap> bitmaps)
    {
        drawable = new ArrayList<Bitmap>();

        for(int index = 0; index < bitmaps.size(); index++)
        {
            drawable.add(bitmaps.get(index));
        }
    }

    @Override
    public int getCount()
    {
        return drawable.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position)
    {
        ImageView imageView = new ImageView(container.getContext());

        imageView.setImageBitmap(drawable.get(position));
        imageView.setTag("current_image" + position);

        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PlacesCardsActivity activity = (PlacesCardsActivity)container.getContext();
                activity.remove_layout();
            }
        });
        ((ViewPager) container).addView(imageView, 0);
        container.setLayoutParams(new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        ((ViewPager) container).removeView((ImageView) object);
    }

}