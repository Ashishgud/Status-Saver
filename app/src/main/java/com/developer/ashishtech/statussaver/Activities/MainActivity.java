package com.developer.ashishtech.statussaver.Activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.developer.ashishtech.statussaver.Adapters.Fragement_Adapter;
import com.developer.ashishtech.statussaver.R;
import com.developer.ashishtech.statussaver.Utils.Constants;
import com.google.android.material.tabs.TabLayout;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

public class MainActivity extends AppCompatActivity {
    Toolbar mToolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    Drawer resultDrawer;
    AccountHeader headerResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            viewPager = findViewById(R.id.viewpager2);
            viewPager.setAdapter(new Fragement_Adapter(getSupportFragmentManager()));
            tabLayout = findViewById(R.id.tabLayout2);
            tabLayout.setupWithViewPager(viewPager);
        }
        catch (Exception e)
        {}
        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (getAppIntro(this)) {
            Intent i = new Intent(this, IntroActivity.class);
            startActivity(i);
        }

       // Typeface typeface = Typeface.createFromAsset(getAssets(), "sintony-regular.otf");
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(false)
                .withAlternativeProfileHeaderSwitching(false)
                .withCompactStyle(false)
                .withHeaderBackground(R.color.colorPrimary)
                .withDividerBelowHeader(false)
                .withProfileImagesVisible(true)
                //.withTypeface(typeface)
                .addProfiles(new ProfileDrawerItem().withIcon(R.drawable.icon).withName(getResources().getString(R.string.app_name)).withEmail(getResources()
                        .getString(R.string.developer_email)))
                .build();
        resultDrawer = new DrawerBuilder()
                .withActivity(this)
                .withSelectedItem(-1)
                .withFullscreen(true)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggle(true)
                .withCloseOnClick(true)
                .withMultiSelect(false)
                .withTranslucentStatusBar(true)
                .withToolbar(mToolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withSelectable(false).withName("WhatsApp Business").withIcon(R.drawable.wb_icon).withOnDrawerItemClickListener((view, position, drawerItem) -> {
                            try{
                                startActivity(new Intent(getApplicationContext(),MainActivity2.class));
                            }
                            catch (ActivityNotFoundException e) {

                               Toast.makeText(this,"Activity not found",Toast.LENGTH_SHORT).show();
                            }
                            return false;
                        })
                            ,new PrimaryDrawerItem().withSelectable(false).withName("Rate Us").withIcon(R.drawable.ic_star_black_24dp).withOnDrawerItemClickListener((view, position, drawerItem) -> {
                            try{

                                Uri uri = Uri.parse("market://details?id="+getPackageName());
                                Intent gotoMarket = new Intent(Intent.ACTION_VIEW,uri);
                                startActivity(gotoMarket);

                            }
                            catch (ActivityNotFoundException e) {

                                Uri uri = Uri.parse("http://play.google.com/store/apps/details?id="+getPackageName());
                                Intent gotoMarket = new Intent(Intent.ACTION_VIEW,uri);
                                startActivity(gotoMarket);
                            }
                            return false;
                        })//.withTypeface(typeface),

                        ,new PrimaryDrawerItem().withSelectable(false).withName("Share").withIcon(R.drawable.ic_share_black_24dp).withOnDrawerItemClickListener((view, position, drawerItem) -> {
                            try{
                              Intent shareintent = new Intent();
                              shareintent.setAction(Intent.ACTION_SEND);
                              shareintent.putExtra(Intent.EXTRA_TEXT,"Status Saver for WhatsApp\n\nLink :https://play.google.com/store/apps/details?id=com.developer.ashishtech.statussaver");
                              shareintent.setType("text/plain");
                              startActivity(Intent.createChooser(shareintent, "Share Status Saver for WhatsApp via:"));
                            }
                            catch (ActivityNotFoundException e) {

                            }
                            return false;
                        })//.withTypeface(typeface),
                        ,
                        new PrimaryDrawerItem().withSelectable(false).withName("How to use?").withIcon(R.drawable.ic_info_outline_black_24dp).withOnDrawerItemClickListener((view, position, drawerItem) -> {
                            startActivity(new Intent(getApplicationContext(),IntroActivity.class));
                            return false;
                        })
                        ,new PrimaryDrawerItem().withSelectable(false).withName("Exit").withIcon(R.drawable.ic_exit_to_app_black_24dp).withOnDrawerItemClickListener((view, position, drawerItem) -> {
                            onBackPressed();
                            return false;
                        })
                        ,new PrimaryDrawerItem().withSelectable(false).withName("Privacy Policy").withOnDrawerItemClickListener((view, position, drawerItem) -> {
                            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("PRIVACY POLICY")
                                    .setMessage(R.string.privacy_message)
                                    .setPositiveButton(android.R.string.yes, (dialog1, which) -> dialog1.dismiss())
                                    .setIcon(R.drawable.ic_info_black_24dp)
                                    .show();
                            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                            assert textView != null;
                            textView.setScroller(new Scroller(MainActivity.this));
                            textView.setVerticalScrollBarEnabled(true);
                            textView.setMovementMethod(new ScrollingMovementMethod());


                            return false;
                        })//.withTypeface(typeface)

                ).withSavedInstance(savedInstanceState)
                .build();

    }


    private boolean getAppIntro(MainActivity mainActivity) {
        SharedPreferences preferences;
        preferences = mainActivity.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean("AppIntro", true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ic_whatapp) {
            try {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                }
                else {
                    Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
            }
        }
        if (item.getItemId() == R.id.ic_whatapp4b) {
            try {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.whatsapp.w4b");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                }
                else {
                    Toast.makeText(this, "WhatsApp Business not installed", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
            }
        }
        if (item.getItemId() == R.id.ic_tutorial) {
            startActivity(new Intent(getApplicationContext(),IntroActivity.class));

        }
        if (item.getItemId() == R.id.ic_about) {
            startActivity(new Intent(getApplicationContext(),about_mine.class));

        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onBackPressed() {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    }
                    System.exit(0);

    }
}
