package com.example.grad_app.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grad_app.R;
import com.example.grad_app.activities.ProfileActivity;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

public class GradViewAdapter extends RecyclerView.Adapter<GradViewAdapter.Holder> implements Filterable {

    Context context;
    ArrayList<User> users;
    ArrayList<User> users_full;

    public static class Holder extends RecyclerView.ViewHolder{

        TextView name_surname, degree, job, email;
        ImageView profile_pic;

        public Holder(@NonNull View itemView) {
            super(itemView);

            name_surname = itemView.findViewById(R.id.item_name_surname);
            degree = itemView.findViewById(R.id.item_degree);
            job = itemView.findViewById(R.id.item_job);
            email = itemView.findViewById(R.id.item_email);
            profile_pic = itemView.findViewById(R.id.item_profilepic);
        }
    }

    public GradViewAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }



    @NonNull
    @Override
    public GradViewAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.grad_item, parent, false);
        users_full = new ArrayList<>(users);

        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GradViewAdapter.Holder holder, int position) {
        User user = users.get(position);
        String full_name = user.getName() + " " + user.getSurname();
        holder.name_surname.setText(full_name);
        holder.degree.setText(user.getDegree());
        if (user.getJob_country().length() != 0){
            String full_job = user.getJob_country() + ", " + user.getJob_city() + ", " + user.getJob_company();
            holder.job.setText(full_job);
        }
        holder.email.setText(user.getEmail());
        if (user.getPhoto() != null && !user.getPhoto().isEmpty()){
            Picasso.with(context.getApplicationContext()).load(user.getPhoto()).into(holder.profile_pic);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("userID", user.getUser_ID());
                Intent intent = new Intent(holder.itemView.getContext(), ProfileActivity.class);
                intent.putExtras(b);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public Filter getFilter() {
        return filter_users;
    }

    private Filter filter_users = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filtered_users = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filtered_users.addAll(users_full);
            }
            else{

                String filter_string = constraint.toString().trim();
                String user_full_name;
                for (User u : users_full){
                    user_full_name = u.getName() + " " + u.getSurname();
                    if (user_full_name.contains(filter_string)){
                        filtered_users.add(u);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filtered_users;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            users.clear();
            users.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };



}
