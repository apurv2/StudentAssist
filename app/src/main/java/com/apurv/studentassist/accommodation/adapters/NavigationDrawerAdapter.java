package com.apurv.studentassist.accommodation.adapters;

/**
 * Created by akamalapuri on 9/24/2015.
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apurv.studentassist.R;
import com.apurv.studentassist.accommodation.classes.NavigationDrawerData;

import java.util.Collections;
import java.util.List;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {


    private LayoutInflater inflater;

    List<NavigationDrawerData> datas = Collections.emptyList();

    public NavigationDrawerAdapter(Context context, List<NavigationDrawerData> data) {

        inflater = LayoutInflater.from(context);
        this.datas = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {


        View view = inflater.inflate(R.layout.navigation_drawer_custom_row, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int i) {

        NavigationDrawerData data = datas.get(i);
        holder.title.setText(data.getText());
        holder.icon.setImageResource(data.getIconId());


    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView icon;


        public MyViewHolder(View itemView) {
            super(itemView);


            title = (TextView) itemView.findViewById(R.id.NDTextView);
            icon = (ImageView) itemView.findViewById(R.id.NDImage);


        }
    }


}
