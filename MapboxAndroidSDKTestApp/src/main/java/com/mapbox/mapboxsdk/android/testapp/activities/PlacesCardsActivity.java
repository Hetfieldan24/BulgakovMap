package com.mapbox.mapboxsdk.android.testapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.mapbox.mapboxsdk.android.testapp.R;
import com.mapbox.mapboxsdk.android.testapp.util.FontHelper;
import com.mapbox.mapboxsdk.android.testapp.util.Globals;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.viewpagerindicator.LinePageIndicator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PlacesCardsActivity extends ActionBarActivity implements View.OnClickListener
{
    public static boolean flag = false;
    private ImageButton placesButton;
    private ImageButton routesButton;
    private ImageButton searchButton;
    private ImageButton infoButton;
    private TextView placesTextView, routesTextView, searchTextView, infoTextView;
    private String textMain;
    private TextView descriptionTextView;
    private String description;
    private int green;
    private int grey;
    private ImageView show;
    private ImageView zoomImage;
    private TextView fullNameTextView;
    private ViewPager viewPager;
    private Globals globals;
    private ArrayList<Bitmap> loadedBitmaps;
    private List<Bitmap> bmps;
    private ArrayList<Bitmap> bmpArray;
    private RelativeLayout layoutRemove;
    private RelativeLayout imageParentLayout;
    private CallbackManager callbackManager;
    private String url="";
    private ImageView view;
    private LinePageIndicator mIndicator;
    private ArrayList<Bitmap> tempBmpArray;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_cards);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bmpArray = new ArrayList<Bitmap>();
        tempBmpArray = new ArrayList<Bitmap>();

        layoutRemove = (RelativeLayout)findViewById(R.id.mainLayout);
        imageParentLayout = (RelativeLayout)findViewById(R.id.layout);

        viewPager = (ViewPager) findViewById(R.id.placeImg);
        show = (ImageView)findViewById(R.id.show_image);
        show.setOnClickListener(this);

        placesButton = (ImageButton)findViewById(R.id.placesButton);
        routesButton = (ImageButton)findViewById(R.id.routesButton);
        searchButton = (ImageButton)findViewById(R.id.searchButton);
        infoButton = (ImageButton)findViewById(R.id.infoButton);
        RelativeLayout backLayout = (RelativeLayout) findViewById(R.id.backLayout);

        RelativeLayout placesLayout = (RelativeLayout) findViewById(R.id.placesLayout);
        RelativeLayout routesLayout = (RelativeLayout) findViewById(R.id.routesLayout);
        RelativeLayout searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);
        RelativeLayout infoLayout = (RelativeLayout) findViewById(R.id.infoLayout);
        zoomImage = (ImageView)findViewById(R.id.layout_for_zoom_image);
        RelativeLayout  textMainLayout = (RelativeLayout) findViewById(R.id.textMainLayout);
        RelativeLayout showPlaceLayout = (RelativeLayout) findViewById(R.id.show_layout);
        RelativeLayout shareLayout = (RelativeLayout) findViewById(R.id.share_layout);

        backLayout.setOnClickListener(this);
        textMainLayout.setOnClickListener(this);
        showPlaceLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);

        placesLayout.setOnClickListener(this);
        routesLayout.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
        infoLayout.setOnClickListener(this);
        zoomImage.setOnClickListener(this);

        FontHelper.applyFont(this, findViewById(R.id.drawer_layout), "fonts/GTH75.otf");
        green = getResources().getColor(R.color.green);
        grey = getResources().getColor(R.color.grey);

        placesTextView = (TextView)findViewById(R.id.placesTextView);
        routesTextView = (TextView)findViewById(R.id.routesTextView);
        searchTextView = (TextView)findViewById(R.id.searchTextView);
        infoTextView = (TextView)findViewById(R.id.infoTextView);

        Typeface newspaper = Typeface.createFromAsset(getAssets(), "fonts/NewspaperSansC.otf");

        fullNameTextView = (TextView)findViewById(R.id.fullName);
        TextView addressTextView = (TextView)findViewById(R.id.address);
        descriptionTextView = (TextView)findViewById(R.id.description);

        fullNameTextView.setTypeface(newspaper);

        String fullName = getIntent().getStringExtra("fullName");
        String address = getIntent().getStringExtra("address");
        description = getIntent().getStringExtra("description");
        textMain = getIntent().getStringExtra("textMain");

        //nameTextView.setText(name);
        fullNameTextView.setText(fullName);
        if(!address.equals("null"))
        {
            addressTextView.setText(address);
        }
        else
        {
            addressTextView.setVisibility(View.GONE);
        }

        if(!textMain.equals("null"))
        {
            descriptionTextView.setText(textMain);
        }
        else
        {
            descriptionTextView.setVisibility(View.GONE);
            textMainLayout.setVisibility(View.GONE);
            ImageView secondBar = (ImageView)findViewById(R.id.secondBar);
            secondBar.setVisibility(View.GONE);
        }

        if(description.equals("null"))
        {
            textMainLayout.setVisibility(View.GONE);
            ImageView secondBar = (ImageView)findViewById(R.id.secondBar);
            secondBar.setVisibility(View.GONE);
        }

        loadImageFromUrl();

        placesButton.setBackgroundResource(R.drawable.places_button_active);
        placesTextView.setTextColor(green);

        view = (ImageView)viewPager.findViewWithTag("current_image" + viewPager.getCurrentItem());
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

                startActivity(new Intent(PlacesCardsActivity.this, MainActivity.class));

                break;

            case R.id.routesLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                routesButton.setBackgroundResource(R.drawable.routes_button_active);

                clearButtonsColors();
                routesTextView.setTextColor(green);

                startActivity(new Intent(PlacesCardsActivity.this, RoutesActivity.class));
                break;

            case R.id.searchLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                searchButton.setBackgroundResource(R.drawable.search_button_active);

                clearButtonsColors();
                searchTextView.setTextColor(green);

                startActivity(new Intent(PlacesCardsActivity.this, SearchActivity.class));
                break;

            case R.id.infoLayout:

                globals.setPositionMain(-1);

                clearButtonsBackgrounds();
                infoButton.setBackgroundResource(R.drawable.info_button_active);

                clearButtonsColors();
                infoTextView.setTextColor(green);

                startActivity(new Intent(PlacesCardsActivity.this, InfoActivity.class));
                break;

            case R.id.backLayout:

                finish();
                break;

            case R.id.share_layout:

                RelativeLayout shareChooseLayout = (RelativeLayout)findViewById(R.id.shareChooseLayout);
                layoutRemove.setVisibility(View.GONE);
                shareChooseLayout.setVisibility(View.VISIBLE);
                final Button shareWithFacebook = (Button)findViewById(R.id.shareWithFacebook);
                final Button shareWithoutFacebook = (Button)findViewById(R.id.shareWithoutFacebook);

                shareWithFacebook.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (!url.equals(""))
                        {
                            ShareDialog shareDialog;

                            FacebookSdk.sdkInitialize(getApplicationContext());
                            callbackManager = CallbackManager.Factory.create();
                            shareDialog = new ShareDialog(PlacesCardsActivity.this);
                            // this part is optional
                            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>()
                            {
                                @Override
                                public void onSuccess(Sharer.Result result)
                                {

                                }

                                @Override
                                public void onCancel()
                                {
                                    Toast.makeText(PlacesCardsActivity.this, "registerCallback was cancelled!", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onError(FacebookException error)
                                {
                                    Toast.makeText(PlacesCardsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                            if (ShareDialog.canShow(ShareLinkContent.class))
                            {
                                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                        .setContentTitle(fullNameTextView.getText() + getString(R.string.share_string))
                                        .setContentDescription(
                                                " ")
                                        .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                                        .setImageUrl(Uri.parse("http://mobile.bulgakovmuseum.ru/" + url))
                                        .build();

                                shareDialog.show(linkContent);
                            } else
                            {
                                Toast.makeText(PlacesCardsActivity.this, "can't share content", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            ImageView view = (ImageView)viewPager.findViewWithTag("current_image" + viewPager.getCurrentItem());

                            Intent intent = null;

                            Uri bmpUri = getLocalBitmapUri(view);
                            intent = new Intent(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_TEXT, fullNameTextView.getText() + getString(R.string.share_string));
                            startActivity(Intent.createChooser(intent,"Share apps:"));
                        }
                    }
                });

                shareWithoutFacebook.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        /*
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = fullNameTextView.getText() + getString(R.string.share_string);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Поделиться"));
                        */
                        shareWithoutFacebookFunction(PlacesCardsActivity.this, fullNameTextView.getText() + getResources().getString(R.string.share_string));
                    }
                });
                /*
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = fullNameTextView.getText().toString();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Поделиться"));
                */

                /*
                ImageView view = (ImageView)viewPager.findViewWithTag("current_image" + viewPager.getCurrentItem());

                Intent intent = null;

                Uri bmpUri = getLocalBitmapUri(view);
                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_TEXT, fullNameTextView.getText() + getString(R.string.share_string));
                intent.putExtra(Intent.EXTRA_TITLE, "my awesome caption in the EXTRA_TITLE field");
                startActivity(Intent.createChooser(intent,"compatible apps:"));
                */


                /*
                ImageView view = (ImageView) viewPager.findViewWithTag("current_image" + viewPager.getCurrentItem());

                Uri bmpUri = getLocalBitmapUri(view);

                ShareDialog shareDialog;

                FacebookSdk.sdkInitialize(getApplicationContext());
                callbackManager = CallbackManager.Factory.create();
                shareDialog = new ShareDialog(this);
                // this part is optional
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>()
                {
                    @Override
                    public void onSuccess(Sharer.Result result)
                    {

                    }

                    @Override
                    public void onCancel()
                    {
                        Toast.makeText(PlacesCardsActivity.this, "registerCallback was cancelled!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException error)
                    {
                        Toast.makeText(PlacesCardsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                if (ShareDialog.canShow(ShareLinkContent.class))
                {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(fullNameTextView.getText() + getString(R.string.share_string))
                            .setContentDescription(
                                    " ")
                            .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                            .setImageUrl(Uri.parse("http://mobile.bulgakovmuseum.ru/" + url))
                            .build();

                    shareDialog.show(linkContent);
                }
                else
                {
                    Toast.makeText(PlacesCardsActivity.this, "can't share content", Toast.LENGTH_LONG).show();
                }
                */


                /*
                Resources resources = getResources();

                Intent emailIntent = new Intent();
                emailIntent.setAction(Intent.ACTION_SEND);
                // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
                emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("share_email_native"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "share_email_subject");
                emailIntent.setType("message/rfc822");

                PackageManager pm = getPackageManager();
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");


                Intent openInChooser = Intent.createChooser(emailIntent, "share_chooser_text");

                List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
                List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
                for (int i = 0; i < resInfo.size(); i++) {
                    // Extract the label, append it, and repackage it in a LabeledIntent
                    ResolveInfo ri = resInfo.get(i);
                    String packageName = ri.activityInfo.packageName;
                    if(packageName.contains("android.email")) {
                        emailIntent.setPackage(packageName);
                    } else if(packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm")) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        if(packageName.contains("twitter")) {
                            intent.putExtra(Intent.EXTRA_TEXT, "share_twitter");
                        } else if(packageName.contains("facebook")) {
                            // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                            // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                            // will show the <meta content ="..."> text from that page with our link in Facebook.
                            intent.putExtra(Intent.EXTRA_TEXT, "share_facebook");
                        } else if(packageName.contains("mms")) {
                            intent.putExtra(Intent.EXTRA_TEXT, "share_sms");
                        } else if(packageName.contains("android.gm")) {
                            intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("share_email_gmail"));
                            intent.putExtra(Intent.EXTRA_SUBJECT, "share_email_subject");
                            intent.setType("message/rfc822");
                        }

                        intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                    }
                }

                // convert intentList to array
                LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

                openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                startActivity(openInChooser);
                */


                //shareWithoutFacebook(PlacesCardsActivity.this, "ASDASDASDASD");

                break;

            case R.id.show_layout:

                globals = Globals.getInstance();
                globals.setShowPlacePlacesCards(true);
                startActivity(new Intent(PlacesCardsActivity.this, MainActivity.class));
                break;

            case R.id.textMainLayout:

                    descriptionTextView.setClickable(false);
                    descriptionTextView.setText(textMain + "\n\n" + description);
                    RelativeLayout layout = (RelativeLayout) findViewById(R.id.textMainLayout);
                    ImageView bar = (ImageView) findViewById(R.id.secondBar);
                    layout.setVisibility(View.GONE);
                    bar.setVisibility(View.GONE);
                    descriptionTextView.setClickable(true);
                    descriptionTextView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            descriptionTextView.setText(textMain);
                            RelativeLayout layout = (RelativeLayout) findViewById(R.id.textMainLayout);
                            ImageView bar = (ImageView) findViewById(R.id.secondBar);
                            layout.setVisibility(View.VISIBLE);
                            bar.setVisibility(View.VISIBLE);
                        }
                    });

                break;

            case R.id.show_image:

                globals = Globals.getInstance();
                globals.setShowPlacePlacesCards(true);
                startActivity(new Intent(PlacesCardsActivity.this, MainActivity.class));
                break;

            case R.id.layout_for_zoom_image:

                if(zoomImage != null)
                {
                    zoomImage.setVisibility(View.GONE);
                    imageParentLayout.setVisibility(View.GONE);
                }
                layoutRemove = (RelativeLayout)findViewById(R.id.mainLayout);
                if(layoutRemove != null)
                    layoutRemove.setVisibility(View.VISIBLE);

                break;

        }
    }

    /*
    @Override
    public void onBackPressed()
    {
        ImageLoader.getInstance().stop();
        super.onBackPressed();
    }
    */

    private void loadImageFromUrl()
    {
        globals = Globals.getInstance();
        int number;
        final ArrayList<String> imageUrl;
        ArrayList<String> imageUrlsFromServer = new ArrayList<String>();
        number = globals.getPositionPlacesList();
        loadedBitmaps = new ArrayList<Bitmap>();

        DisplayImageOptions options;

        ImageLoaderConfiguration config;

        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .resetViewBeforeLoading(true)
                .build();

        config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(options)
                .build();

        ImageLoader.getInstance().init(config);
        ImageLoader imageLoader = ImageLoader.getInstance();

        if (number >= 0)
        {
            if(globals.getFlagCustomAdapterPlacesList().equals("List"))
            {
                imageUrlsFromServer = readFromFile("urls" + number + ".txt");
            }
            else if(globals.getFlagCustomAdapterPlacesList().equals("Main"))
            {
                Log.e("positionMain Places", String.valueOf(globals.getPositionMain()));
                imageUrlsFromServer = readFromFile("urls" + globals.getPositionMain() + ".txt");
                globals.setFlagCustomAdapterPlacesList("List");
                globals.setPositionPlacesList(globals.getPositionMain());
            }

            if(getIntent().getStringArrayListExtra("url") != null)
            {
                imageUrlsFromServer = getIntent().getStringArrayListExtra("url");
            }

            if (!imageUrlsFromServer.isEmpty() && !imageUrlsFromServer.get(0).equals("null"))
            {
                url = imageUrlsFromServer.get(0);
                for(int index = 0; index < imageUrlsFromServer.size(); index++)
                {
                    if(!imageUrlsFromServer.get(index).equals("null"))
                    {
                        imageUrlsFromServer.set(index, "http://mobile.bulgakovmuseum.ru/" + imageUrlsFromServer.get(index));
                    }
                    else
                    {
                        imageUrlsFromServer.remove(index);
                    }
                }
                imageUrl = imageUrlsFromServer;

                ImageAdapter adapter;

                for(int index = 0; index < imageUrl.size(); index ++)
                {
                    bmps =
                            MemoryCacheUtils.findCachedBitmapsForImageUri(
                                    imageUrl.get(index), ImageLoader.getInstance().getMemoryCache());

                    if (!bmps.isEmpty())
                    {
                        for(int i = 0; i < bmps.size(); i++)
                        {
                            bmpArray.add(bmps.get(i));
                        }
                        Log.e("bmpArray", bmpArray.toString());
                        adapter = new ImageAdapter(PlacesCardsActivity.this, bmpArray);
                        viewPager.setAdapter(adapter);
                        mIndicator = (LinePageIndicator)findViewById(R.id.indicator);
                        mIndicator.setViewPager(viewPager);
                    }
                    else
                    {
                        final ArrayList<String> finalImageUrlsFromServer = imageUrl;

                        if (!imageUrl.get(index).equals("null"))
                        {
                            final int finalIndex = index;
                            imageLoader.loadImage(imageUrl.get(index), options,
                                    new SimpleImageLoadingListener()
                                    {
                                        @Override
                                        public void onLoadingComplete(String imageUri,
                                                                      View view, Bitmap loadedImage)
                                        {
                                            loadedBitmaps.add(loadedImage);

                                            if (loadedBitmaps.size() == finalImageUrlsFromServer.size())

                                            {
                                                List<Bitmap> tempBmps;

                                                for(int index = 0; index < imageUrl.size(); index ++)
                                                {
                                                    tempBmps =
                                                            MemoryCacheUtils.findCachedBitmapsForImageUri(
                                                                    imageUrl.get(index), ImageLoader.getInstance().getMemoryCache());

                                                    if (!tempBmps.isEmpty())
                                                    {
                                                        for (int i = 0; i < tempBmps.size(); i++)
                                                        {
                                                            tempBmpArray.add(tempBmps.get(i));
                                                        }
                                                    }
                                                }

                                                if(!(tempBmpArray.get(tempBmpArray.size()-1).toString())
                                                        .equals(
                                                                (MemoryCacheUtils.findCachedBitmapsForImageUri(
                                                                        imageUrl.get(finalIndex),
                                                                        ImageLoader.getInstance().getMemoryCache()).toString()
                                                                        .replaceAll("\\[", "").replaceAll("\\]",""))
                                                        ))
                                                {
                                                    flag = true;
                                                    Log.e("tempBmpArray before reverse", tempBmpArray.toString());
                                                    //Collections.reverse(tempBmpArray);
                                                    Log.e("tempBmpArray after reverse", tempBmpArray.toString());

                                                    ImageAdapter adapter =
                                                            new ImageAdapter(PlacesCardsActivity.this, tempBmpArray);
                                                    viewPager.setAdapter(adapter);
                                                    mIndicator = (LinePageIndicator) findViewById(R.id.indicator);
                                                    mIndicator.setViewPager(viewPager);
                                                }
                                                else
                                                {
                                                    flag = false;
                                                    Log.e("loadedBitmaps without reverse", loadedBitmaps.toString());

                                                    ImageAdapter adapter =
                                                            new ImageAdapter(PlacesCardsActivity.this, loadedBitmaps);
                                                    viewPager.setAdapter(adapter);
                                                    mIndicator = (LinePageIndicator) findViewById(R.id.indicator);
                                                    mIndicator.setViewPager(viewPager);
                                                }

                                            }

                                        }

                                        @Override
                                        public void onLoadingFailed(String imageUri, View view, FailReason failReason)
                                        {
                                            show = (ImageView) findViewById(R.id.show_image);
                                            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.image_layout);

                                            show.setVisibility(View.GONE);
                                            frameLayout.setVisibility(View.GONE);
                                        }
                                    });
                        }
                    }
                }
            }
            else
            {
                show = (ImageView)findViewById(R.id.show_image);
                FrameLayout frameLayout = (FrameLayout)findViewById(R.id.image_layout);

                show.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
            }
        }
        else
        {
            show = (ImageView)findViewById(R.id.show_image);
            FrameLayout frameLayout = (FrameLayout)findViewById(R.id.image_layout);

            show.setVisibility(View.GONE);
            frameLayout.setVisibility(View.GONE);
        }
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
            Toast.makeText(PlacesCardsActivity.this,
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
        return result;
    }

    @Override
    protected void onDestroy()
    {
        /*
        if(bmps != null)
        {
            if(!bmps.isEmpty())
            for (Bitmap bmp : bmps)
            {
                bmp.recycle();
            }
        }

        if(loadedBitmaps != null)
        {
            if(!loadedBitmaps.isEmpty())
            for (Bitmap loadedBitmap : loadedBitmaps)
            {
                loadedBitmap.recycle();
            }
        }

        if(bmpArray != null)
        {
            if(!bmpArray.isEmpty())
            {
                for (Bitmap bmp : bmpArray)
                {
                    bmp.recycle();
                }
            }
        }
        */
        super.onDestroy();
    }

    public void remove_layout()
    {
        layoutRemove = (RelativeLayout)findViewById(R.id.mainLayout);
        if(zoomImage != null)
        {
            zoomImage.setVisibility(View.VISIBLE);
            imageParentLayout.setVisibility(View.VISIBLE);
        }
        layoutRemove.setVisibility(View.GONE);
        ImageView view = (ImageView)viewPager.findViewWithTag("current_image" + viewPager.getCurrentItem());
        if(view != null)
        {
            imageParentLayout.setVisibility(View.VISIBLE);
            zoomImage.setImageDrawable(view.getDrawable());
        }
    }

    public Uri getLocalBitmapUri(ImageView imageView)
    {
        // Extract Bitmap from ImageView drawable
        Drawable drawable;
        Uri bmpUri = null;

        if(imageView != null)
        {
            drawable = imageView.getDrawable();

            Bitmap bmp = null;
            if (drawable instanceof BitmapDrawable)
            {
                bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            } else
            {
                return null;
            }
            // Store image to default external storage directory

            try
            {
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
                file.getParentFile().mkdirs();
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                bmpUri = Uri.fromFile(file);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return bmpUri;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void shareWithoutFacebookFunction(Context context, String text)
    {
        ImageView view = (ImageView)viewPager.findViewWithTag("current_image" + viewPager.getCurrentItem());

        Uri bmpUri = getLocalBitmapUri(view);

        // get available share intents
        List<Intent> targets = new ArrayList<Intent>();
        Intent template = new Intent(Intent.ACTION_SEND);
        template.putExtra(Intent.EXTRA_STREAM, bmpUri);
        template.setType("image/*");
        template.putExtra(Intent.EXTRA_TEXT, fullNameTextView.getText() + getString(R.string.share_string));
        List<ResolveInfo> resolveInfos = context.getPackageManager().
                queryIntentActivities(template, 0);

        // remove facebook which has a broken share intent
        for (ResolveInfo resolveInfo : resolveInfos)
        {
            String packageName = resolveInfo.activityInfo.packageName;
            if (!packageName.equals("com.facebook.katana"))
            {
                Intent target = new Intent(android.content.Intent.ACTION_SEND);
                target.putExtra(Intent.EXTRA_STREAM, bmpUri);
                target.setType("image/*");
                target.putExtra(Intent.EXTRA_TEXT, fullNameTextView.getText() + getString(R.string.share_string));
                target.setPackage(packageName);
                targets.add(target);
            }
        }
        Intent chooser = Intent.createChooser(targets.remove(0), "Chooser Dialog Title");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toArray(new Parcelable[]{}));
        context.startActivity(chooser);
    }
}