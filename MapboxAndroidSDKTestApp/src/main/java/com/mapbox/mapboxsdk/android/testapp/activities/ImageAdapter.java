package com.mapbox.mapboxsdk.android.testapp.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by hetfieldan24 on 25.02.2015.
 */
public class ImageAdapter extends PagerAdapter
{
    private Context context;

    private Drawable[] drawable;

    ImageAdapter(Context context, ArrayList<Bitmap> bitmaps)
    {
        this.context = context;

        /*
        if(PlacesCardsActivity.flag)
        {
            Collections.reverse(bitmaps);
        }
        */

        drawable = new Drawable[bitmaps.size()];

        for(int index = 0; index < drawable.length; index++)
        {
            drawable[index] = new BitmapDrawable(context.getResources(), bitmaps.get(index));
        }
    }

    @Override
    public int getCount()
    {
        return drawable.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        ImageView imageView = new ImageView(context);

        imageView.setImageDrawable(drawable[position]);
        imageView.setTag("current_image" + position);

        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PlacesCardsActivity activity = (PlacesCardsActivity)context;
                activity.remove_layout();
            }
        });
        ((ViewPager) container).addView(imageView, 0);
        container.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        ((ViewPager) container).removeView((ImageView) object);
    }
}