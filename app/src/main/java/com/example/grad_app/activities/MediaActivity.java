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
import com.example.grad_app.helpers.Media;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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


public class MediaActivity extends AppCompatActivity {

    TextView t_name_surname, t_date, t_text;
    ImageView i_profilepic, i_pic;
    Button b_goback, b_remove;
    ProgressBar progressBar;
    FirebaseUser user;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        t_name_surname = findViewById(R.id.media_name);
        t_date = findViewById(R.id.media_date);
        t_text = findViewById(R.id.media_text);
        i_profilepic = findViewById(R.id.media_profile_pic);
        i_pic = findViewById(R.id.media_pic);
        b_goback = findViewById(R.id.media_b_goback);
        b_remove = findViewById(R.id.media_b_remove);

        progressBar = findViewById(R.id.media_progressbar);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Bundle bundle = getIntent().getExtras();

        fill_fields(bundle);


        b_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_user_to_mediawall();
            }
        });

        b_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove_media(bundle);
            }
        });
    }

    private void fill_fields(Bundle bundle){
        progressBar.setVisibility(View.VISIBLE);
        String bundle_userID = bundle.getString("userID");
        String bundle_mediaID = bundle.getString("mediaID");

        if (!bundle_userID.equals(user.getUid())){
            //kullanıcı başka birinin fotografını görüntülüyor
            b_remove.setVisibility(View.GONE);
            b_remove.setActivated(false);
        }



        databaseReference.child("medias").child(bundle_mediaID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Media media_class = snapshot.getValue(Media.class);

                if (media_class != null){
                    t_name_surname.setText(media_class.getName_surname());
                    String date = media_class.getDate_month() + "/" + media_class.getDate_day() + "/" + media_class.getDate_year();
                    t_date.setText(date);
                    t_text.setText(media_class.getText());

                    String profile_photo = media_class.getProfile_photo();
                    if (profile_photo.length() != 0){
                        Uri uri_photo = Uri.parse(profile_photo);

                        if (uri_photo != null && !uri_photo.toString().isEmpty()){
                            Picasso.with(MediaActivity.this).load(uri_photo).into(i_profilepic);
                        }
                    }

                    String media = media_class.getMedia();
                    if (profile_photo.length() != 0){
                        Uri uri_media = Uri.parse(media);

                        if (uri_media != null && !uri_media.toString().isEmpty()){
                            Picasso.with(MediaActivity.this).load(uri_media).into(i_pic);
                        }
                    }

                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void remove_media(Bundle bundle){
        String bundle_mediaID = bundle.getString("mediaID");
        String bundle_media_extension = bundle.getString("media_extension");
        //medyanın silinmesi
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("medias").child(bundle_mediaID + "." + bundle_media_extension);

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //realtime database'den media bilgilerinin silinmesi
                databaseReference.child("medias").child(bundle_mediaID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MediaActivity.this, "Medya başarıyla silindi.", Toast.LENGTH_SHORT).show();
                            send_user_to_mediawall();
                        }
                    }
                });
            }
        });


    }
    private void send_user_to_mediawall(){
        Intent intent = new Intent(MediaActivity.this, MediaWallActivity.class);
        startActivity(intent);
        finish();
    }
}