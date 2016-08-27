package com.apurv.studentassist.airport.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apurv.studentassist.R;
import com.apurv.studentassist.airport.classes.AirportService;
import com.apurv.studentassist.airport.interfaces.RecyclerTouchInterface;

import java.util.Collections;
import java.util.List;

/**
 * Created by apurv on 6/23/15.
 */
public class AirportAdapter extends RecyclerView.Adapter<AirportAdapter.MyViewHolder> {


    private LayoutInflater inflater;
    List<AirportService> data = Collections.emptyList();
    RecyclerTouchInterface parentActivity;


    public AirportAdapter(Context context, List<AirportService> data, RecyclerTouchInterface parentActivity) {

        inflater = LayoutInflater.from(context);
        this.data = data;
        this.parentActivity = parentActivity;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {


        View view = inflater.inflate(R.layout.airportpickup_view, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AirportAdapter.MyViewHolder myViewHolder, int i) {


        AirportService service = data.get(i);

        myViewHolder.serviceName.setText(service.getServiceName());
        myViewHolder.serviceDescription.setText(service.getDescription());
    }


    @Override
    public int getItemCount() {

        return data.size();
    }


    public void addAll(List<AirportService> services) {

        for (int i = 0; i < services.size(); i++) {
            data.add(services.get(i));
            notifyItemInserted(data.size());
        }
    }

    public void clear() {

        for (int i = data.size() - 1; i >= 0; i--) {
            data.remove(data.get(i));
            notifyItemRemoved(data.size());
        }


    }


    public void delete(int position) {

        data.remove(data.get(position));
        notifyItemRemoved(data.size());
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView serviceName;
        TextView serviceDescription;


        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            serviceName = (TextView) itemView.findViewById(R.id.serviceName);
            serviceDescription = (TextView) itemView.findViewById(R.id.serviceDescription);


        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            parentActivity.onTouch(position, v);

        }
    }


}
