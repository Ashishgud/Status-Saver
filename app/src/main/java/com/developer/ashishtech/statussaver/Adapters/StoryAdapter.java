package com.developer.ashishtech.statussaver.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developer.ashishtech.statussaver.Model.StoryModel;
import com.developer.ashishtech.statussaver.R;
import com.developer.ashishtech.statussaver.Status_Fragement;
import com.developer.ashishtech.statussaver.Utils.Constants;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private static final int MENU_ITEM_VIEW_TYPE = 0;
    private static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 1;
    private final Context context;
    ArrayList<StoryModel> savepath;
    public OutputStream destFolder;
    private final ArrayList<Object> filesList;

    public StoryAdapter(Context context, ArrayList<Object> filesList , ArrayList<StoryModel> savepath) {
        this.context = context;
        this.filesList = filesList;
        this.savepath = savepath;
    }

    @NonNull
    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case MENU_ITEM_VIEW_TYPE:
                @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_row,null,false);
                return new ViewHolder(view);
            case NATIVE_EXPRESS_AD_VIEW_TYPE:
            default:
                View nativeExpressLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.native_express_ad_container,
                        parent, false);
                return new NativeExpressAdViewHolder(nativeExpressLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        int viewType = getItemViewType(position);
        switch (viewType){
            case MENU_ITEM_VIEW_TYPE:
                final StoryModel files = (StoryModel) filesList.get(position);
                final Uri uri = Uri.parse(files.getUri().toString());
                holder.userName.setText(files.getName());
                if(files.getUri().toString().endsWith(".mp4"))
                {
                    holder.playIcon.setVisibility(View.VISIBLE);
                }else{
                    holder.playIcon.setVisibility(View.INVISIBLE);
                }
                Glide.with(context)
                        .load(files.getUri())
                        .into(holder.savedImage);

                holder.downloadID.setOnClickListener(v -> {
                    checkFolder();
                    final String path = ((StoryModel) filesList.get(position)).getPath();
                    final File file = new File(path);
                    String file_name = files.getFilename();
                    File destFile = null;
                    String destPath = null;
                    if(Build.VERSION.SDK_INT == Build.VERSION_CODES.R)
                    {
                        destPath =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+Constants.SAVE_FOLDER_NAME+"WhatsApp/";
                        destFile = new File(destPath);
                    }
                    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        try {
                            Uri uripath = savepath.get(0).savepathuri;
                            Uri newuri = DocumentsContract.createDocument(context.getContentResolver(), uripath, "*/*", file_name);
                            destFolder = context.getContentResolver().openOutputStream(newuri);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }
                    else {
                        destPath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.SAVE_FOLDER_NAME+"WhatsApp/";
                        destFile = new File(destPath);
                    }
                   //       DocumentFile df = DocumentFile.fromFile(destFile);
                    String sourcefileType = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.getContentResolver().getType(files.getUri()));

                    try {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                      //  copyFil(new BufferedInputStream(context.getContentResolver().openInputStream(files.getUri())), new BufferedOutputStream(context.getContentResolver().openOutputStream(df.getUri())));
copy(new BufferedInputStream(context.getContentResolver().openInputStream(files.getUri())),file_name,destFile,sourcefileType);
                            Toast.makeText(context, "Saved to: " + destPath + files.getFilename(), Toast.LENGTH_LONG).show();
                           // Toast.makeText(context,buid,Toast.LENGTH_LONG).show();
                        }
                        else {
                           FileUtils.copyFileToDirectory(file,destFile);
                            Toast.makeText(context, "Saved to: " + destPath + files.getFilename(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    MediaScannerConnection.scanFile(
                            context,
                            new String[]{ destPath + files.getFilename()},
                            new String[]{ "*/*"},
                            new MediaScannerConnection.MediaScannerConnectionClient()
                            {
                                public void onMediaScannerConnected()
                                {
                                }
                                public void onScanCompleted(String path, Uri uri1)
                                {
                                    Log.d("path: ",path);
                                }
                            });
                   // Toast.makeText(context, "Saved to: " + destPath + files.getFilename(), Toast.LENGTH_LONG).show();
                });
                break;

        }
    }

    private void copy(BufferedInputStream bufferedInputStream, String ifile, File outfile,String type) {
        InputStream in = null;
        OutputStream out = null;
       // String error = null;
        DocumentFile finalpath = DocumentFile.fromFile(outfile);
        try{
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                DocumentFile newFile = finalpath.createFile(type,ifile);
                out = context.getContentResolver().openOutputStream(newFile.getUri());
            } else {
                out = destFolder;
            }
            in= bufferedInputStream;
            byte[] buf = new byte[1024];
            int read;
            while ((read=in.read(buf))!= -1){
                out.write(buf,0,read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void checkFolder() {
        File dir =null;

            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.SAVE_FOLDER_NAME+"WhatsApp/";
            dir = new File(path);
        boolean isDirectoryCreated = dir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = dir.mkdir();
        }
        if (isDirectoryCreated) {
            Log.d("Folder", "Already Created");
        }
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    public static class NativeExpressAdViewHolder extends StoryAdapter.ViewHolder {
        NativeExpressAdViewHolder(View view) {
            super(view);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return (position % Status_Fragement.ITEMS_PER_AD == 0) ? NATIVE_EXPRESS_AD_VIEW_TYPE
                : MENU_ITEM_VIEW_TYPE;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView savedImage;
        ImageView playIcon;
        ImageView downloadID;
        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.profileUserName);
            savedImage = (ImageView) itemView.findViewById(R.id.mainImageView);
            playIcon = (ImageView) itemView.findViewById(R.id.playButtonImage);
            downloadID = (ImageView) itemView.findViewById(R.id.downloadID);
        }
    }
}
