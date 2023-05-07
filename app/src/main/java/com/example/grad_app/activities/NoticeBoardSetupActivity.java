package com.example.grad_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.grad_app.R;
import com.example.grad_app.helpers.Notice;
import com.example.grad_app.helpers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class NoticeBoardSetupActivity extends AppCompatActivity {

    EditText et_currentdate, et_expirationdate, et_headline, et_notice;
    Button b_create_notice, b_go_back;
    ProgressBar progressBar;
    DatePickerDialog datePickerDialog;
    Calendar selected_date;
    DatabaseReference databaseReference;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board_setup);

        et_currentdate = findViewById(R.id.noticesetup_t_currentDate);
        et_expirationdate = findViewById(R.id.noticesetup_et_expirationdate);
        et_headline = findViewById(R.id.noticesetup_et_headline);
        et_notice = findViewById(R.id.noticesetup_et_notice);
        progressBar = findViewById(R.id.noticesetup_progressbar);
        b_create_notice = findViewById(R.id.noticesetup_b_create);
        b_go_back = findViewById(R.id.noticesetup_b_goback);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        selected_date = Calendar.getInstance();
        //bugünün tarihini doldur
        et_currentdate.setText(get_todays_date());
        et_currentdate.setEnabled(false);
        et_expirationdate.setFocusable(false);
        et_expirationdate.setClickable(true);



        //tarih seçme
        et_expirationdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        init_date_picker();


        b_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_user_to_noticeboard();
            }
        });

        b_create_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_notice_inputs();
            }
        });
    }

    private void init_date_picker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String selected_date = month + "/" + day + "/" + year;
                et_expirationdate.setText(selected_date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
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

    private void send_user_to_noticeboard() {
        Intent intent = new Intent(NoticeBoardSetupActivity.this, NoticeBoardActivity.class);
        startActivity(intent);
        finish();
    }

    private void check_notice_inputs(){
        String expiration_date = et_expirationdate.getText().toString();
        String headline = et_headline.getText().toString();
        String notice = et_notice.getText().toString();

        if(headline.isEmpty()){
            et_headline.setError("Lütfen duyuru için bir başlık giriniz.");
            et_headline.requestFocus();
        }
        else if (notice.isEmpty()){
            et_notice.setError("Lütfen duyuru giriniz.");
            et_notice.requestFocus();
        }
        else if (expiration_date.isEmpty()){
            et_expirationdate.setError("Lütfen duyuru için bitiş tarihi giriniz.");
        }
        else{
            save_notice_to_server();
        }


    }

    private void save_notice_to_server(){
        //sunucudan kullanıcı bilgilerini çek
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user_class = snapshot.getValue(User.class);
                //kullanıcı bilgilerini çekme başarılıysa duyuruyu kaydet
                if (user_class != null){
                    String name_surname = user_class.getName() + " " + user_class.getSurname();
                    StringTokenizer stringTokenizer_today = new StringTokenizer(get_todays_date(), "/");
                    StringTokenizer stringTokenizer_expiration = new StringTokenizer(et_expirationdate.getText().toString(), "/");
                    ArrayList<String> date_today = new ArrayList<>();
                    ArrayList<String> date_expiration = new ArrayList<>();
                    while(stringTokenizer_today.hasMoreElements()){
                        date_today.add(stringTokenizer_today.nextToken());
                    }
                    while(stringTokenizer_expiration.hasMoreElements()){
                        date_expiration.add(stringTokenizer_expiration.nextToken());
                    }

                    Notice notice = new Notice(et_headline.getText().toString(),
                            date_today.get(0), date_today.get(1), date_today.get(2),
                            date_expiration.get(0), date_expiration.get(1), date_expiration.get(2),
                            name_surname, user_class.getPhoto(), et_notice.getText().toString());

                    databaseReference.child("notices").child(user.getUid() + "+" + Calendar.getInstance().getTimeInMillis()).setValue(notice).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()){
                                Toast.makeText(NoticeBoardSetupActivity.this, "Duyuru başarıyla kaydedildi.", Toast.LENGTH_SHORT).show();
                                send_user_to_noticeboard();
                            }
                            else{
                                Toast.makeText(NoticeBoardSetupActivity.this, "Duyuru kaydedilemedi.", Toast.LENGTH_SHORT).show();
                                send_user_to_noticeboard();
                            }
                        }
                    });

                }
                else{
                    Toast.makeText(NoticeBoardSetupActivity.this, "Sunucudan veri çekerken hata meydana geldi.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //databaseReference.child("notices").setValue();
    }

}