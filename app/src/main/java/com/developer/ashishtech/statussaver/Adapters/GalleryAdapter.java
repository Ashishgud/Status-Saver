package com.developer.ashishtech.statussaver.Adapters;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developer.ashishtech.statussaver.Model.GalleryModel;
import com.developer.ashishtech.statussaver.R;
import com.geniusforapp.fancydialog.FancyAlertDialog;

import java.io.File;
import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<GalleryModel> filesList;
    public GalleryAdapter(Context context, ArrayList<GalleryModel> filesList) {
        this.context = context;
        this.filesList = filesList;
    }
    @NonNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_card_row,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final GalleryModel files = filesList.get(position);
        final Uri uri = Uri.parse(files.getUri().toString());
        final File file = new File(uri.getPath());
        holder.userName.setText(files.getName());

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if(files.getUri().toString().endsWith(".mp4"))
        {
            holder.playIcon.setVisibility(View.VISIBLE);
        }else{
            holder.playIcon.setVisibility(View.INVISIBLE);
        }
        Glide.with(context)
                .load(files.getUri())
                .into(holder.savedImage);
        holder.savedImage.setOnClickListener(v -> {
            if(files.getUri().toString().endsWith(".mp4")){
                Uri VideoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider",file);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setDataAndType(VideoURI, "video/*");
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
                }
            }else if(files.getUri().toString().endsWith(".jpg")){
                Uri VideoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider",file);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setDataAndType(VideoURI, "image/*");
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.repostID.setOnClickListener(v -> {
            Uri mainUri = Uri.fromFile(file);
            if(files.getUri().toString().endsWith(".jpg")){
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.setPackage("com.whatsapp");
                try {
                    context.startActivity(Intent.createChooser(sharingIntent, "Share Image using"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
                }
            }else if(files.getUri().toString().endsWith(".mp4")){
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("video/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.setPackage("com.whatsapp");
                try {
                    context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.deleteID.setOnClickListener(v -> {
            final String path = filesList.get(position).getPath();
            final File file1 = new File(path);
            @SuppressLint("NotifyDataSetChanged") FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(context)
                        .setTextTitle("DELETE?")
                        .setBody("Are you sure you want to delete this file?")
                        .setNegativeColor(R.color.colorNegative)
                        .setNegativeButtonText("Cancel")
                        .setOnNegativeClicked((view, dialog) -> dialog.dismiss())
                        .setPositiveButtonText("Delete")
                        .setPositiveColor(R.color.colorPositive)
                        .setOnPositiveClicked((view, dialog) -> {
                            try {
                                if (file1.exists()) {

                                  boolean del = file1.delete();

                                    filesList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, filesList.size());
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "File was Deleted", Toast.LENGTH_SHORT).show();
                                    if (del) {
                                        MediaScannerConnection.scanFile(
                                                context,
                                                new String[]{path, path},
                                                new String[]{"image/jpg", "video/mp4"},
                                                new MediaScannerConnection.MediaScannerConnectionClient() {
                                                    public void onMediaScannerConnected() {
                                                    }

                                                    public void onScanCompleted(String path1, Uri uri1) {
                                                        Log.d("Video path: ", path1);
                                                    }
                                                });
                                    }
                                }
                                dialog.dismiss();
                            } catch (Exception e) {
                                // TODO let the user know the file couldn't be deleted
                                e.printStackTrace();
                            }
                        })
                        .build();
                alert.show();
        });

        holder.shareID.setOnClickListener(v -> {
            Uri mainUri = Uri.fromFile(file);
            if(files.getUri().toString().endsWith(".jpg")){
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    context.startActivity(Intent.createChooser(sharingIntent, "Share Image using"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
                }
            }else if(files.getUri().toString().endsWith(".mp4")){
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("video/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return filesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView savedImage;
        ImageView playIcon;
        ImageView repostID, shareID, deleteID;
        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.profileUserName);
            savedImage = (ImageView) itemView.findViewById(R.id.mainImageView);
            playIcon = (ImageView) itemView.findViewById(R.id.playButtonImage);
            repostID = (ImageView) itemView.findViewById(R.id.repostID);
            shareID = (ImageView) itemView.findViewById(R.id.shareID);
            deleteID = (ImageView) itemView.findViewById(R.id.deleteID);
        }
    }

}
