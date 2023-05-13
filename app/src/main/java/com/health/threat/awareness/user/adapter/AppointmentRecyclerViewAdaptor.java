package com.health.threat.awareness.user.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.health.threat.awareness.user.R;
import com.health.threat.awareness.user.dialog_fragment.AppointmentDetailDialogFragment;
import com.health.threat.awareness.user.model.Appointment;
import java.util.ArrayList;

public class AppointmentRecyclerViewAdaptor extends RecyclerView.Adapter<AppointmentRecyclerViewAdaptor.MyHolder> {
    FragmentActivity ct;
    ArrayList<Appointment> al;

    public AppointmentRecyclerViewAdaptor(FragmentActivity cont, ArrayList<Appointment> al) {
        this.ct = cont;
        this.al = al;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(ct);
        View v = li.inflate(R.layout.appoinment_recyclerview_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(AppointmentRecyclerViewAdaptor.MyHolder holder, final int position) {
        final Appointment p1 = al.get(position);

        if (p1.getDate()!=null && !p1.getDate().equals(""))
            holder.AppointmentDate.setText("" + p1.getDate()+"-"+p1.getMonth()+"-"+p1.getYear()+" "+p1.getHour()+":"+p1.getMinutes());
        else
            holder.AppointmentDate.setVisibility(View.GONE);

        if (p1.getHospitalName()!=null && !p1.getHospitalName().equals(""))
            holder.HospitalName.setText("" + p1.getHospitalName());
        else
            holder.HospitalName.setVisibility(View.GONE);

        if (p1.getSickness_title()!=null && !p1.getSickness_title().equals(""))
            holder.SicknessTitle.setText("" + p1.getSickness_title());
        else
            holder.SicknessTitle.setVisibility(View.GONE);

        if (p1.getSymptoms_description()!=null && !p1.getSymptoms_description().equals(""))
            holder.SicknessDescription.setText("" + p1.getSymptoms_description());
        else
            holder.SicknessDescription.setVisibility(View.GONE);

        holder.cld.setOnClickListener(view -> {
            AppointmentDetailDialogFragment dialogFragment = new AppointmentDetailDialogFragment(p1);
            dialogFragment.show(ct.getSupportFragmentManager(), "Show");
        });
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        TextView AppointmentDate;
        TextView HospitalName,SicknessTitle,SicknessDescription;
        CardView cld;

        public MyHolder(View itemView) {
            super(itemView);
            cld = itemView.findViewById(R.id.AppointmentCard);
            AppointmentDate = itemView.findViewById(R.id.AppointmentDate);
            HospitalName = itemView.findViewById(R.id.HospitalName);
            SicknessTitle = itemView.findViewById(R.id.SicknessTitle);
            SicknessDescription = itemView.findViewById(R.id.SicknessDescription);
        }
    }
}