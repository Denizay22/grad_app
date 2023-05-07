package com.example.grad_app.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grad_app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NoticeBoardAdapter extends RecyclerView.Adapter<NoticeBoardAdapter.Holder> {

    Context context;
    List<Notice> notices;

    public static class Holder extends RecyclerView.ViewHolder{
        TextView name_surname, headline, date, notice, expiration_date;
        ImageView profile_pic;

        public Holder(@NonNull View itemView){
            super(itemView);

            name_surname = itemView.findViewById(R.id.notice_name_surname);
            headline = itemView.findViewById(R.id.notice_headline);
            date = itemView.findViewById(R.id.notice_t_date_fill);
            expiration_date = itemView.findViewById(R.id.notice_t_expirationdate_fill);
            profile_pic = itemView.findViewById(R.id.notice_profile_pic);
            notice = itemView.findViewById(R.id.notice_message);
        }
    }

    public NoticeBoardAdapter(Context context, List<Notice> notices){
        this.context = context;
        this.notices = notices;
    }



    @NonNull
    @Override
    public NoticeBoardAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.notice_item, parent, false);
        return new NoticeBoardAdapter.Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeBoardAdapter.Holder holder, int position) {
        Notice notice = notices.get(position);
        holder.name_surname.setText(notice.getName_surname());
        String date_string = notice.getToday_month() + "/" + notice.getToday_day() + "/" + notice.getToday_year();
        String expiration_date_string = notice.getExpiration_month() + "/" + notice.getExpiration_Day() + "/" + notice.getExpiration_year();
        holder.date.setText(date_string);
        holder.expiration_date.setText(expiration_date_string);
        holder.headline.setText(notice.getHeadline());
        holder.notice.setText(notice.getNotice());
        if (notice.getPhoto().length() != 0){
            Picasso.with(context.getApplicationContext()).load(notice.getPhoto()).into(holder.profile_pic);
        }

    }

    @Override
    public int getItemCount() {
        return notices.size();
    }
}
