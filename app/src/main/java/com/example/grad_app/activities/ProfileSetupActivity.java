package com.example.grad_app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.grad_app.R;
import com.example.grad_app.helpers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

@SuppressWarnings("ALL")
public class ProfileSetupActivity extends AppCompatActivity {

    EditText et_name, et_surname, et_email, et_phone, et_job_country, et_job_city, et_job_company;

    RadioButton rb_lisans, rb_lisans2, rb_doktora;
    Spinner sp_enrollment_year, sp_grad_year;
    Button b_save_profile;
    String degree;

    ImageView profilepic;
    Uri uri;
    Bitmap bitmap;

    ProgressBar progressbar;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        et_name = findViewById(R.id.profilesetup_et_name);
        et_surname = findViewById(R.id.profilesetup_et_surname);
        et_email = findViewById(R.id.profilesetup_et_email);
        et_email.setEnabled(false);
        et_phone = findViewById(R.id.profilesetup_et_phone);
        et_job_country = findViewById(R.id.profilesetup_et_job_country);
        et_job_city = findViewById(R.id.profilesetup_et_job_city);
        et_job_company = findViewById(R.id.profilesetup_et_job_company);

        sp_enrollment_year = findViewById(R.id.profilesetup_sp_enrollment);
        sp_grad_year = findViewById(R.id.profilesetup_sp_grad_time);

        progressbar = findViewById(R.id.profilesetup_progressbar);
        rb_lisans = findViewById(R.id.radio_lisans);
        rb_lisans2 = findViewById(R.id.radio_lisans2);
        rb_doktora = findViewById(R.id.radio_doktora);


        profilepic = findViewById(R.id.profilesetup_i_profilepic);
        b_save_profile = findViewById(R.id.profilesetup_button_saveprofile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance("https://grad-app-a4cda-default-rtdb.europe-west1.firebasedatabase.app");
        databaseReference = database.getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference();

        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(this, R.array.grad_years, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sp_enrollment_year.setAdapter(spinner_adapter);
        sp_grad_year.setAdapter(spinner_adapter);


        fill_fields(user, spinner_adapter);

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_profile_pic();
            }
        });


        b_save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_inputs();
            }
        });


    }

    private void fill_fields(FirebaseUser fb_user, ArrayAdapter<CharSequence> spinner_adapter) {
        progressbar.setVisibility(View.VISIBLE);
        String userID = fb_user.getUid();
        database = FirebaseDatabase.getInstance("https://grad-app-a4cda-default-rtdb.europe-west1.firebasedatabase.app");
        databaseReference = database.getReference("users");

        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user_class = snapshot.getValue(User.class);
                progressbar.setVisibility(View.GONE);
                if (user_class != null){
                    et_name.setText(user_class.getName());
                    et_surname.setText(user_class.getSurname());
                    et_email.setText(user_class.getEmail());
                    et_phone.setText(user_class.getPhone_no());
                    et_job_country.setText(user_class.getJob_country());
                    et_job_city.setText(user_class.getJob_city());
                    et_job_company.setText(user_class.getJob_company());
                    sp_enrollment_year.setSelection(spinner_adapter.getPosition(user_class.getEnrollment_year()));
                    sp_grad_year.setSelection(spinner_adapter.getPosition(user_class.getGrad_year()));

                    degree = user_class.getDegree();

                    if (degree.equals(rb_lisans.getText().toString())){
                        rb_lisans.setChecked(true);
                    }
                    else if (degree.equals(rb_lisans2.getText().toString())){
                        rb_lisans2.setChecked(true);
                    }
                    else if (degree.equals(rb_doktora.getText().toString())){
                        rb_doktora.setChecked(true);
                    }

                    //eger serverda foto varsa onu goster
                    Uri uri_server = user.getPhotoUrl();

                    if (uri_server != null && !uri_server.toString().isEmpty()){
                        Picasso.with(ProfileSetupActivity.this).load(uri_server).into(profilepic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileSetupActivity.this,
                        "Sunucudan veriler alınırken hata meydana geldi, lütfen daha sonra tekrar deneyiniz", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onRadioButtonClicked(@NonNull View view) {
        if(view.getId() == R.id.radio_lisans){
            degree = "Lisans";
        }
        else if (view.getId() == R.id.radio_lisans2){
            degree = "Lisans Üstü";
        }
        else if (view.getId() == R.id.radio_doktora){
            degree = "Doktora";
        }
    }

    private void check_inputs() {
        progressbar.setVisibility(View.VISIBLE);
        String name = et_name.getText().toString();
        String surname = et_surname.getText().toString();
        String email = et_email.getText().toString();
        String phone = et_phone.getText().toString();
        String country = et_job_country.getText().toString();
        String city = et_job_city.getText().toString();
        String company = et_job_company.getText().toString();
        String enrollment_year = sp_enrollment_year.getSelectedItem().toString();
        String grad_year = sp_grad_year.getSelectedItem().toString();

        if (name.isEmpty()){
            progressbar.setVisibility(View.GONE);
            et_name.setError("Lütfen isminizi giriniz.");
            et_name.requestFocus();
        }
        else if (surname.isEmpty()){
            progressbar.setVisibility(View.GONE);
            et_surname.setError("Lütfen soyadınızı giriniz.");
            et_name.requestFocus();
        }
        else if(phone.length() != 11 && phone.length() != 0){
            progressbar.setVisibility(View.GONE);
            et_phone.setError("Lütfen geçerli bir telefon numarası giriniz.");
            et_phone.requestFocus();
        }
        else if (degree == null || degree.length() == 0){
            progressbar.setVisibility(View.GONE);
            Toast.makeText(this, "Lütfen eğitim durumunuzu seçiniz", Toast.LENGTH_SHORT).show();
        }
        else if (Integer.parseInt(enrollment_year) >= Integer.parseInt(grad_year)){
            progressbar.setVisibility(View.GONE);
            Toast.makeText(this, "Lütfen giriş ve mezun olma yılınızı kontrol ediniz.", Toast.LENGTH_SHORT).show();
        }
        else if ((!country.isEmpty() && (city.isEmpty() || company.isEmpty())) ||
                (!city.isEmpty() && (country.isEmpty() || company.isEmpty())) ||
                (!company.isEmpty() && (city.isEmpty() || country.isEmpty()))){
            progressbar.setVisibility(View.GONE);
            Toast.makeText(this, "Lütfen ülke, şehir ve şirket bilgilerinin hepsini doldurunuz veya boş bırakınız.", Toast.LENGTH_SHORT).show();
        }
        else {
            save_profile(name, surname, email, phone, country, city, company, degree, enrollment_year, grad_year, user.getUid());
        }

    }

    private void save_profile(String name, String surname, String email,
                              String phone, String country, String city,
                              String company, String degree, String enrollment_year, String grad_year, String user_ID) {
        User user_class = new User(name, surname, email, degree, country,
                city, company, phone, enrollment_year, grad_year, "", user.getUid());
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");

        if (uri != null){
            StorageReference fileReference =  storageReference.child("profile_pics").child(user.getUid() + "." + getFileExtension(uri));
            fileReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    //fotonun yüklenmesi
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            user = auth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri).build();
                            user.updateProfile(profileUpdates);
                            user_class.setPhoto(uri.toString());
                            user_class.setPhoto(user_class.getPhoto());

                            save_data_to_server(user_class);
                        }
                    });

                }
            });
        }

        else{
            save_data_to_server(user_class);
        }

    }

    private void save_data_to_server(User user_class){
        databaseReference.child(user.getUid()).setValue(user_class).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(ProfileSetupActivity.this, "Profil başarıyla kaydedildi.", Toast.LENGTH_SHORT).show();
                    send_user_to_profile();
                }
                else{
                    Toast.makeText(ProfileSetupActivity.this, "Profil kaydedilemedi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void select_profile_pic() {
        Dexter.withActivity(ProfileSetupActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent,"Resim Dosyası Seçiniz"), 1);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(requestCode==1  && resultCode==RESULT_OK && data != null && data.getData() != null)
        {
            uri=data.getData();
            try{
                InputStream inputStream=getContentResolver().openInputStream(uri);
                bitmap= BitmapFactory.decodeStream(inputStream);
                profilepic.setImageBitmap(bitmap);
            }catch (Exception ex)
            {
                Toast.makeText(this, "Sunucudan veri alırken hata meydana geldi.", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void send_user_to_profile() {
        Intent intent = new Intent(ProfileSetupActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

}