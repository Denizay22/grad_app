package com.example.grad_app.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grad_app.R;
import com.example.grad_app.activities.MediaActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MediaWallAdapter extends RecyclerView.Adapter<MediaWallAdapter.Holder> {

    Context context;

    ArrayList<Media> medias;

    public static class Holder extends RecyclerView.ViewHolder{

        ImageView i_media;

        public Holder(@NonNull View itemView) {
            super(itemView);

            i_media = itemView.findViewById(R.id.mediasingle_media);
        }
    }

    public MediaWallAdapter(Context context, ArrayList<Media> medias){
        this.context = context;
        this.medias = medias;
    }


    @NonNull
    @Override
    public MediaWallAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.mediawall_media, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaWallAdapter.Holder holder, int position) {
        Media media = medias.get(position);

        if (media.getMedia() != null && !media.getMedia().isEmpty()){
            Picasso.with(context.getApplicationContext()).load(media.getMedia()).into(holder.i_media);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("userID", media.getUser_ID());
                b.putString("mediaID", media.getMedia_ID());
                b.putString("media_extension", media.getMedia_file_extension());
                Intent intent = new Intent(holder.itemView.getContext(), MediaActivity.class);
                intent.putExtras(b);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medias.size();
    }
}
