package com.developer.ashishtech.statussaver;

import static android.app.Activity.RESULT_OK;

import static java.nio.file.Paths.get;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.developer.ashishtech.statussaver.Adapters.GalleryAdapter.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.developer.ashishtech.statussaver.Adapters.GalleryAdapter;
import com.developer.ashishtech.statussaver.Model.GalleryModel;
import com.developer.ashishtech.statussaver.Utils.Constants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class Gallery_Fragement extends Fragment {

    private AdView mAdView;
    private RecyclerView recyclerView;
    private File[] files;
    private SwipeRefreshLayout recyclerLayout;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    public static final int DELETE_REQUEST_CODE =13;
    private static Bundle mBundleRecyclerViewState;
    Parcelable listState;
    private static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery__fragement, container, false);

        setUpRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRecyclerView);

        setUpRecyclerView();
        recyclerLayout.setOnRefreshListener(() -> {
            recyclerLayout.setRefreshing(true);
            setUpRecyclerView();
            (new Handler()).postDelayed(() -> {
                recyclerLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Refreshed!", Toast.LENGTH_SHORT).show();
            }, 2000);

        });
        MobileAds.initialize(getActivity(), initializationStatus -> {
        });
        mAdView = view.findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                // Code to be executed when an ad request fails.
                super.onAdFailedToLoad(adError);
                mAdView.loadAd(adRequest);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                super.onAdOpened();
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUpRecyclerView() {
     try {
         recyclerView.setHasFixedSize(true);
         recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
         GalleryAdapter recyclerViewAdapter = new GalleryAdapter(getActivity(), getData());
         recyclerView.setAdapter(recyclerViewAdapter);
         recyclerViewAdapter.notifyDataSetChanged();
     }
     catch (Exception e)
     {

     }
    }

    private ArrayList<GalleryModel> getData() {
        ArrayList<GalleryModel> filesList = new ArrayList<>();
        GalleryModel f;
        String targetPath = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            targetPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        }
        else {
            targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.SAVE_FOLDER_NAME+"WhatsApp/";
        }
        File targetDirector = new File(targetPath);
        files = targetDirector.listFiles();
        if (files == null) {
//            noImageText.setVisibility(View.INVISIBLE);
        }
        try {
            Arrays.sort(files, (Comparator<File>) (o1, o2) -> Long.compare(((File) o2).lastModified(), ((File) o1).lastModified()));

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                f = new GalleryModel();
                f.setName("Saved Status: "+(i+1));
                f.setFilename(file.getName());
                f.setUri(Uri.fromFile(file));
                f.setPath(files[i].getAbsolutePath());
               if(file.getName()!=".nomedia")
                filesList.add(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filesList;
    }

    @Override
    public void onPause() {
        super.onPause();
        // save RecyclerView state
        if (mBundleRecyclerViewState != null && recyclerView != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            if (recyclerView.getLayoutManager() != null) {
                recyclerView.getLayoutManager().onRestoreInstanceState(listState);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(listState);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onBackPressed() {

      //  super.onBackPressed();
        Toast.makeText(getActivity(),"Back",Toast.LENGTH_SHORT).show();
    }
}
