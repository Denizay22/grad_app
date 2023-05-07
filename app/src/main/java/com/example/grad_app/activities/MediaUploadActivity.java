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
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.grad_app.R;
import com.example.grad_app.helpers.Media;
import com.example.grad_app.helpers.User;
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
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class MediaUploadActivity extends AppCompatActivity {

    ImageView i_media;
    EditText et_text;
    Button b_upload;
    Button b_goback;

    Uri uri_local;
    Bitmap bitmap;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_upload);

        i_media = findViewById(R.id.mediaupload_media);
        et_text = findViewById(R.id.mediaupload_et_text);
        b_upload = findViewById(R.id.mediaupload_b_upload);
        b_goback = findViewById(R.id.mediaupload_b_goback);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        i_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_media();
            }
        });

        b_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_media();
            }
        });

        b_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_user_to_mediawall();
            }
        });
    }

    private String get_todays_date()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return month + "/" + day + "/" + year;
    }

    private void upload_media(){



        if (uri_local != null){
            //fotoyu uplaodla
            String file_name = user.getUid() + "+" + Calendar.getInstance().getTimeInMillis();
            StorageReference fileReference = storageReference.child("medias").child(file_name + "." + getFileExtension(uri_local));
            fileReference.putFile(uri_local).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //user verilerini serverdan çek
                            databaseReference.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user_class = snapshot.getValue(User.class);
                                    if (user_class != null){
                                        //media bilgilerini database'e kaydet
                                        String name_surname = user_class.getName() + " " + user_class.getSurname();
                                        StringTokenizer stringTokenizer_today = new StringTokenizer(get_todays_date(), "/");
                                        ArrayList<String> date_today = new ArrayList<>();
                                        while(stringTokenizer_today.hasMoreElements()){
                                            date_today.add(stringTokenizer_today.nextToken());
                                        }
                                        Media media = new Media(user_class.getUser_ID(), name_surname,
                                                date_today.get(0), date_today.get(1), date_today.get(2),
                                                et_text.getText().toString(), uri.toString(),  file_name,
                                                getFileExtension(uri_local) , user_class.getPhoto());
                                        //medya bilgilerini servera yükle
                                        databaseReference.child("medias").child(file_name).setValue(media).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(MediaUploadActivity.this, "Medya başarıyla yüklendi.", Toast.LENGTH_SHORT).show();
                                                    send_user_to_mediawall();
                                                }
                                                else{
                                                    Toast.makeText(MediaUploadActivity.this, "Medya yüklenemedi.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                }
            });

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void select_media() {
        Dexter.withActivity(MediaUploadActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Medya seçiniz."), 1);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri_local = data.getData();
            try{
                InputStream inputStream = getContentResolver().openInputStream(uri_local);
                bitmap = BitmapFactory.decodeStream(inputStream);
                i_media.setImageBitmap(bitmap);
            }
            catch (Exception ex){
                Toast.makeText(this, "Resim seçerken hata meydana geldi.", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void send_user_to_mediawall(){
        Intent intent = new Intent(MediaUploadActivity.this, MediaWallActivity.class);
        startActivity(intent);
        finish();
    }
}