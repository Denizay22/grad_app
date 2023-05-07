package com.example.grad_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class ProfileActivity extends AppCompatActivity {

    EditText et_name, et_surname, et_email, et_phone,
            et_job_country, et_job_city, et_job_company,
            et_enrollment_year, et_grad_year, et_degree;

    Button b_update_profile, b_go_back_to_main;
    ImageView profilepic;
    ProgressBar progressbar;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    int mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        et_name = findViewById(R.id.profile_et_name);
        et_name.setEnabled(false);
        et_surname = findViewById(R.id.profile_et_surname);
        et_surname.setEnabled(false);
        et_email = findViewById(R.id.profile_et_email);
        et_email.setEnabled(false);
        et_phone = findViewById(R.id.profile_et_phone);
        et_phone.setEnabled(false);
        et_enrollment_year = findViewById(R.id.profile_et_enrollment);
        et_enrollment_year.setEnabled(false);
        et_grad_year = findViewById(R.id.profile_et_grad);
        et_grad_year.setEnabled(false);
        et_degree = findViewById(R.id.profile_et_education);
        et_degree.setEnabled(false);
        et_job_country = findViewById(R.id.profile_et_job_country);
        et_job_country.setEnabled(false);
        et_job_city = findViewById(R.id.profile_et_job_city);
        et_job_city.setEnabled(false);
        et_job_company = findViewById(R.id.profile_et_job_company);
        et_job_company.setEnabled(false);


        progressbar = findViewById(R.id.profile_progressbar);


        profilepic = findViewById(R.id.profile_i_profilepic);

        b_update_profile = findViewById(R.id.profile_button_saveprofile);
        b_go_back_to_main = findViewById(R.id.profile_button_go_main_menu);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference();

        Bundle bundle = getIntent().getExtras();

        fill_fields(bundle);

        b_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_user_to_profile_setup();
            }
        });

        b_go_back_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_user_back(mode);
            }
        });



    }


    private void fill_fields(Bundle bundle) {
        progressbar.setVisibility(View.VISIBLE);
        //eğer bundle varsa user_class, serverdan gelen UID ile çekilecek
        //eğer yoksa auth.getcurrentUser ile çekilecek.
        String userID;

        if (bundle == null){
            //kullanıcı kendi profilini görüntülüyor
            userID = user.getUid();
            mode = 1;
        }
        else{
            //kullanıcı başka profili görüntülüyor
            userID = bundle.getString("userID");
            b_update_profile.setVisibility(View.GONE);
            b_update_profile.setActivated(false);
            b_go_back_to_main.setText(getResources().getString(R.string.go_back));
            mode = 2;

        }


        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");

        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user_class = snapshot.getValue(User.class);

                if (user_class != null){
                    et_name.setText(user_class.getName());
                    et_surname.setText(user_class.getSurname());
                    et_email.setText(user_class.getEmail());
                    et_phone.setText(user_class.getPhone_no());
                    et_job_country.setText(user_class.getJob_country());
                    et_job_city.setText(user_class.getJob_city());
                    et_job_company.setText(user_class.getJob_company());
                    et_enrollment_year.setText(user_class.getEnrollment_year());
                    et_grad_year.setText(user_class.getGrad_year());
                    et_degree.setText(user_class.getDegree());


                    //eger kullancının fotosu varsa onu göster
                    String photo_link = user_class.getPhoto();
                    if (photo_link.length() != 0){
                        Uri uri_photo = Uri.parse(photo_link);
                        if (uri_photo != null && !uri_photo.toString().isEmpty()){
                            Picasso.with(ProfileActivity.this).load(uri_photo).into(profilepic);
                        }
                    }
                }
                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this,
                        "Sunucudan veriler alınırken hata meydana geldi, lütfen daha sonra tekrar deneyiniz", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void send_user_to_profile_setup() {
        Intent intent = new Intent(ProfileActivity.this, ProfileSetupActivity.class);
        startActivity(intent);
        finish();
    }

    private void send_user_back(int mode) {
        Intent intent;
        if (mode == 1){
            //kendi profiline bakıyor
            intent = new Intent(ProfileActivity.this, MainActivity.class);
        }
        else{
            //başka birinin profiline bakıyor
            intent = new Intent(ProfileActivity.this, GradSearchActivity.class);
        }
        startActivity(intent);
        finish();
    }

}