package com.example.grad_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.example.grad_app.R;
import com.example.grad_app.helpers.GradViewAdapter;
import com.example.grad_app.helpers.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GradSearchActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    Button b_go_back;
    SearchView et_search_bar;
    DatabaseReference databaseReference;
    GradViewAdapter gradViewAdapter;
    ArrayList<User> users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grad_search);


        recyclerView = findViewById(R.id.gradsearch_rcview);
        b_go_back = findViewById(R.id.gradsearch_b_goback);
        et_search_bar = findViewById(R.id.gradsearch_searchbar);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        users = new ArrayList<>();


        gradViewAdapter = new GradViewAdapter(this, users);
        recyclerView.setAdapter(gradViewAdapter);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    users.add(user);
                }
                gradViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        et_search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                gradViewAdapter.getFilter().filter(newText);
                return false;
            }
        });


        b_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_user_to_main();
            }
        });


    }


    private void send_user_to_main() {
        Intent intent = new Intent(GradSearchActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}