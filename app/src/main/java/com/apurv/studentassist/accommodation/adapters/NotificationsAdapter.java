package com.apurv.studentassist.accommodation.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.classes.NotificationSettingsDetails;

import java.util.Collections;
import java.util.List;

/**
 * Created by apurv on 6/23/15.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {


    private LayoutInflater inflater;
    List<NotificationSettingsDetails> datas = Collections.emptyList();


    public NotificationsAdapter(Context context, List<NotificationSettingsDetails> data) {

        inflater = LayoutInflater.from(context);
        this.datas = data;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {


        View view = inflater.inflate(R.layout.notification_ad_view, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NotificationsAdapter.MyViewHolder myViewHolder, int i) {


        NotificationSettingsDetails data = datas.get(i);

        myViewHolder.spinner1.setText(data.getleftSpinner());
        myViewHolder.spinner2.setText(data.getrightSpinner());
    }


    @Override
    public int getItemCount() {

        return datas.size();
    }


    public void addAll(List<NotificationSettingsDetails> notifications) {

        for (int i = 0; i < notifications.size(); i++) {
            datas.add(notifications.get(i));
            notifyItemInserted(datas.size());
        }
    }

    public void clear() {

        for (int i = datas.size() - 1; i >= 0; i--) {
            datas.remove(datas.get(i));
            notifyItemRemoved(datas.size());
        }


    }

    public void delete(int position) {
        datas.remove(datas.get(position));

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, datas.size());

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView spinner1;
        TextView spinner2;


        public MyViewHolder(View itemView) {
            super(itemView);


            spinner1 = (TextView) itemView.findViewById(R.id.apartmentName);
            spinner2 = (TextView) itemView.findViewById(R.id.gender);


        }
    }


}
