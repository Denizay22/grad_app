package com.example.grad_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.grad_app.R;
import com.example.grad_app.helpers.Media;
import com.example.grad_app.helpers.MediaWallAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MediaWallActivity extends AppCompatActivity {

    MediaWallAdapter mediaWallAdapter;
    Button b_goback, b_upload_media;
    ArrayList<Media> medias;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_wall);

        RecyclerView recyclerView = findViewById(R.id.mediawall_recycler);
        b_goback = findViewById(R.id.mediawall_b_goback);
        b_upload_media = findViewById(R.id.mediawall_add_media);

        databaseReference = FirebaseDatabase.getInstance().getReference("medias");

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        medias = new ArrayList<>();

        mediaWallAdapter = new MediaWallAdapter(this, medias);
        recyclerView.setAdapter(mediaWallAdapter);

        b_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_user_to_main();
            }
        });

        b_upload_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_user_to_upload_media();
            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Media media = dataSnapshot.getValue(Media.class);

                    if (media != null){
                        medias.add(media);
                    }
                }
                mediaWallAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void send_user_to_main() {
        Intent intent = new Intent(MediaWallActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void send_user_to_upload_media() {
        Intent intent = new Intent(MediaWallActivity.this, MediaUploadActivity.class);
        startActivity(intent);
        finish();
    }
}