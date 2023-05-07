package com.example.grad_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grad_app.R;
import com.example.grad_app.helpers.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    ImageView profilepic;
    TextView name_surname;
    Button button_my_profile, button_grad_search, button_noticeboard, button_media_wall;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profilepic = findViewById(R.id.main_userInfo_profilepic);
        name_surname = findViewById(R.id.main_userInfo_name);

        button_my_profile = findViewById(R.id.main_b_profile);
        button_grad_search = findViewById(R.id.main_button_grad_search);
        button_noticeboard = findViewById(R.id.main_button_noticeboard);
        button_media_wall = findViewById(R.id.main_button_media_wall);

        progressBar = findViewById(R.id.main_progressbar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference();

        button_my_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        button_grad_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GradSearchActivity.class);
                startActivity(intent);
            }
        });

        button_noticeboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoticeBoardActivity.class);
                startActivity(intent);
            }
        });

        button_media_wall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MediaWallActivity.class);
                startActivity(intent);
            }
        });

        fill_info(user);
    }
    private void fill_info(FirebaseUser fb_user) {
        progressBar.setVisibility(View.VISIBLE);
        String userID = fb_user.getUid();
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user_class = snapshot.getValue(User.class);

                if (user_class !=null){
                    String full_name = user_class.getName() + " " + user_class.getSurname();
                    name_surname.setText(full_name);

                    Uri uri_server =fb_user.getPhotoUrl();

                    if (uri_server != null && !uri_server.toString().isEmpty()){
                        Picasso.with(MainActivity.this).load(uri_server).into(profilepic);
                    }

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,
                        "Sunucudan veriler alınırken hata meydana geldi, lütfen daha sonra tekrar deneyiniz", Toast.LENGTH_SHORT).show();
            }
        });
    }


}