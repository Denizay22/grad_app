package com.example.grad_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.grad_app.R;
import com.example.grad_app.helpers.Notice;
import com.example.grad_app.helpers.NoticeBoardAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class NoticeBoardActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    Button b_new_notice;
    Button b_go_back;
    ProgressBar progressBar;
    DatabaseReference databaseReference;
    NoticeBoardAdapter noticeBoardAdapter;
    ArrayList<Notice> notices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);

        recyclerView = findViewById(R.id.noticeboard_rcview);
        b_go_back = findViewById(R.id.noticeboard_b_goback);
        b_new_notice = findViewById(R.id.noticeboard_add_notice);

        databaseReference = FirebaseDatabase.getInstance().getReference("notices");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.noticeboard_progressbar);
        notices = new ArrayList<>();

        noticeBoardAdapter = new NoticeBoardAdapter(this, notices);
        recyclerView.setAdapter(noticeBoardAdapter);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Notice notice = dataSnapshot.getValue(Notice.class);

                    if (notice != null) {
                        Calendar cal_notice_expiration = Calendar.getInstance();
                        cal_notice_expiration.set(Integer.parseInt(notice.getExpiration_year()),
                                Integer.parseInt(notice.getExpiration_month()) - 1,
                                Integer.parseInt(notice.getExpiration_Day()));

                        Calendar cal_today = Calendar.getInstance();
                        if (!cal_notice_expiration.before(cal_today)){
                            notices.add(notice);
                        }
                    }
                    else{
                        Toast.makeText(NoticeBoardActivity.this, "Sunucudan veri alırken hata meydana geldi.", Toast.LENGTH_SHORT).show();
                    }

                }
                progressBar.setVisibility(View.GONE);
                noticeBoardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        b_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_user_to_main();
            }
        });

        b_new_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_user_to_new_notice();
            }
        });
    }

    private void send_user_to_main() {
        Intent intent = new Intent(NoticeBoardActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void send_user_to_new_notice() {
        Intent intent = new Intent(NoticeBoardActivity.this, NoticeBoardSetupActivity.class);
        startActivity(intent);
        finish();
    }

}