package com.developer.ashishtech.statussaver;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.developer.ashishtech.statussaver.Activities.MainActivity;
import com.developer.ashishtech.statussaver.Adapters.StoryAdapter;
import com.developer.ashishtech.statussaver.Model.StoryModel;
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

public class Status_Fragement extends Fragment {
    private AdView mAdView;
    private RecyclerView recyclerView;
    Dialog dialog;
    Uri savepathuri;
    ArrayList<StoryModel> savepath = new ArrayList<>();
    private SwipeRefreshLayout recyclerLayout;
    public static final int ITEMS_PER_AD = 6;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    String targetPath;
    ArrayList<Object> filesList = new ArrayList<>();

    public Status_Fragement() {
        // Required empty public constructor
    }
    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status__fragement, container, false);
        SharedPreferences preferences = requireActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        String uri = preferences.getString("uri","no");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            if(!uri.matches("no")){
               get(uri);
            }
            else {
                checkfolder();
            }
        }
            boolean result = checkPermission();
            if (result) {
                setUpRecyclerView();
            } else {
                checkAgain();
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            SavedFolder();
        }

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
                StoryAdapter recyclerViewAdapter = new StoryAdapter(getActivity(), getData() , savepath);
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
            } catch (Exception e) {
            }
        }
    public void SavedFolder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            SharedPreferences preferencessave = requireActivity().getSharedPreferences("apps", Context.MODE_PRIVATE);
            String urisave = preferencessave.getString("urisave", "no");
            if (!urisave.matches("no")) {
//                   t(this,urisave, Toast.LENGTH_LONG).show();
                getsave(urisave);
//                    String[] words = urisave.split(" ");
//                    String lastword = words[words.length -1];
//                    Toast.makeTex
            }
            else {
                permissiondeniedsave();

            }
        }
    }
    private void permissiondeniedsave() {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog_save);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            dialog.getWindow().setBackgroundDrawable(getDrawable(android.R.color.transparent));
        }
        dialog.show();
        dialog.setCancelable(false);
        Button YES = dialog.findViewById(R.id.button_save);
        YES.setOnClickListener(view -> {
            checksavefolder();
            dialog.dismiss();
        });
    }

    private void  checksavefolder(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            StorageManager storageManager = (StorageManager) getActivity().getSystemService(Context.STORAGE_SERVICE);
            Intent intentsave = storageManager.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
            Uri urisave = intentsave.getParcelableExtra("android.provider.extra.INITIAL_URI");
            String schemesave = urisave.toString();
            schemesave = schemesave.replace("/root/", "/document/");
            intentsave.putExtra("android.provider.extra.INITIAL_URI", urisave);
            try {
                startActivityForResult(intentsave, 7);
            } catch (ActivityNotFoundException ignored) {
            }
        }

    }

    public void getsave(String save)
    {
        DocumentFile tree = DocumentFile.fromTreeUri(getContext(), Uri.parse(save));
        savepathuri = tree.getUri();
        savepath.add(new StoryModel(savepathuri));

    }
    private void get(String s)
    {
        StoryModel f;
        DocumentFile tree = DocumentFile.fromTreeUri(getContext(), Uri.parse(s));

        DocumentFile[] files1 = tree.listFiles();
        try {
            Arrays.sort(files1, (Comparator<DocumentFile>) (o1, o2) -> Long.compare(((DocumentFile) o2).lastModified(), ((DocumentFile) o1).lastModified()));
            for (int i = 0; i < files1.length; i++) {
                DocumentFile file1 = files1[i];
              //  File dfile = new File(targetPath, ".nomedia");
               // dfile.delete();
                f = new StoryModel();
                f.setName("Status:" + (i));
                f.setUri(file1.getUri());
                f.setPath(files1[i].getUri().getPath());
          //      String a = files1[i].getUri().getPath();
                f.setFilename(file1.getName());
               // Toast.makeText(getActivity(),a, Toast.LENGTH_LONG).show();
                filesList.add(f);
                try {
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    StoryAdapter recyclerViewAdapter = new StoryAdapter(getActivity(), filesList , savepath);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
                catch (Exception e)
                { }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
        private ArrayList<Object> getData () {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                StoryModel f;
                targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.FOLDER_NAME + "Media/.Statuses";
                File dir = new File(targetPath);
                if (!dir.exists()) {
                    //noinspection deprecation
                    targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.Folder_Name + "Media/.Statuses/";
                }
                File targetDirector = new File(targetPath);
                File[] files = targetDirector.listFiles();
                if (files == null) {
//          noImageText.setVisibility(View.INVISIBLE);
                }
                try {
                    Arrays.sort(files, (Comparator<File>) (o1, o2) -> Long.compare(((File) o2).lastModified(), ((File) o1).lastModified()));
                    for (int i = 0; i < files.length; i++) {
                        File file = files[i];
                        File dfile = new File(targetPath, ".nomedia");
                        dfile.delete();
                        f = new StoryModel();
                        f.setName("Status: " + (i));
                        f.setUri(Uri.fromFile(file));
                        f.setPath(files[i].getAbsolutePath());
                        f.setFilename(file.getName());

                        filesList.add(f);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
                return filesList;
            }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
      if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, (dialog, which) -> ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE));
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    public void checkAgain() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
            alertBuilder.setPositiveButton(android.R.string.yes, (dialog, which) -> ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE));
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }

    }
public void checkfolder(){
    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)
    {
        String path = Environment.getExternalStorageDirectory()+Constants.Folder_Name +"Media/.Statuses";
        File file = new File(path);
        String startDir = null,finalDirPath;
        if(file.exists()){
            startDir ="Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
        }
        StorageManager sm = (StorageManager) getActivity().getSystemService(Context.STORAGE_SERVICE);
        Intent intent =sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
        Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");
        String scheme = uri.toString();
        scheme = scheme.replace("/root/","/document/");
        finalDirPath = scheme + "%3A" + startDir;
        uri = Uri.parse(finalDirPath);
        intent.putExtra("android.provider.extra.INITIAL_URI",uri);
        try
        {
            startActivityForResult(intent,6);
        }
        catch (ActivityNotFoundException ignored)
        {

        }
    }
}
    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 6 && resultCode == RESULT_OK){
            if(data != null){
          //      Toast.makeText(getActivity(),"Work",Toast.LENGTH_LONG).show();
              Uri  uri = data.getData();
              if(uri.getPath().endsWith(".Statuses")){
                  final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                      getActivity().getApplicationContext().getContentResolver().takePersistableUriPermission(uri,takeFlags);

                  }
                  SharedPreferences preferences = getContext().getSharedPreferences("app",Context.MODE_PRIVATE);
                  SharedPreferences.Editor editor = preferences.edit();
                  editor.putString("uri",String.valueOf(uri));
                  editor.apply();
              //    runRecyclerviewCode(String.valueOf(uri));
            get(String.valueOf(uri));
                // startActivity(new Intent(getActivity(),Status_Fragement.class));

              }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpRecyclerView();
                } else {
                    checkAgain();
                }
                break;
        }
    }

}
