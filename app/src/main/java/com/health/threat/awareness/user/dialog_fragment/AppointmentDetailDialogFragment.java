package com.health.threat.awareness.user.dialog_fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.health.threat.awareness.user.R;
import com.health.threat.awareness.user.app.App;
import com.health.threat.awareness.user.model.Appointment;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Objects;

public class AppointmentDetailDialogFragment extends DialogFragment {
    ImageButton cancelBtn;
    Appointment appointment;

    TextView HospitalName, DateAndTimeTV;
    EditText Sickness_title, Symptoms_description;
    Button date_time_set, btnUpdate, btnDelete;
    String date, month, year, hour, minutes;

    public AppointmentDetailDialogFragment(Appointment a) {
        // Required empty public constructor
        appointment = a;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroy() {
        if (getDialog() != null && getDialog().isShowing())
            getDialog().dismiss();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_dialog, container, false);

        date = month = year = hour = minutes = null;

        cancelBtn = view.findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        HospitalName = view.findViewById(R.id.HospitalName);
        Sickness_title = view.findViewById(R.id.Sickness_title);
        Symptoms_description = view.findViewById(R.id.Symptoms_description);
        DateAndTimeTV = view.findViewById(R.id.DateAndTimeTV);

        date_time_set = view.findViewById(R.id.date_time_set);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnDelete = view.findViewById(R.id.btnDelete);

        if (appointment.getDate() != null && !appointment.getDate().equals(""))
            DateAndTimeTV.setText("" + appointment.getDate() + "-" + appointment.getMonth() + "-" + appointment.getYear() + " " + appointment.getHour() + ":" + appointment.getMinutes());
        else
            DateAndTimeTV.setVisibility(View.GONE);

        if (appointment.getHospitalName() != null && !appointment.getHospitalName().equals(""))
            HospitalName.setText("" + appointment.getHospitalName());
        else
            HospitalName.setVisibility(View.GONE);

        if (appointment.getSickness_title() != null && !appointment.getSickness_title().equals(""))
            Sickness_title.setText("" + appointment.getSickness_title());
        else
            Sickness_title.setVisibility(View.GONE);

        if (appointment.getSymptoms_description() != null && !appointment.getSymptoms_description().equals(""))
            Symptoms_description.setText("" + appointment.getSymptoms_description());
        else
            Symptoms_description.setVisibility(View.GONE);

        date_time_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View dialogView = View.inflate(getActivity(), R.layout.date_time_picker, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(requireActivity()).create();

                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
                        TimePicker timePicker = dialogView.findViewById(R.id.time_picker);

                        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth(),
                                timePicker.getHour(),
                                timePicker.getMinute());

                        date = String.valueOf(datePicker.getDayOfMonth());
                        month = String.valueOf(datePicker.getMonth());
                        year = String.valueOf(datePicker.getYear());
                        hour = String.valueOf(timePicker.getHour());
                        minutes = String.valueOf(timePicker.getMinute());

                        DateAndTimeTV.setText(date + "-" + month + "-" + year + " " + hour + ":" + minutes);
                        //time = calendar.getTimeInMillis();
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setView(dialogView);
                alertDialog.show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Sickness_title.getText() == null || Sickness_title.getText().toString().equals("")) {
                    Toast.makeText(requireActivity(), "Sickness Title is Required", Toast.LENGTH_SHORT).show();
                    Sickness_title.requestFocus();
                    Sickness_title.setError("Required");
                    return;
                }

                if (Symptoms_description.getText() == null || Symptoms_description.getText().toString().equals("")) {
                    Toast.makeText(requireActivity(), "Symptoms Description is Required", Toast.LENGTH_SHORT).show();
                    Symptoms_description.requestFocus();
                    Symptoms_description.setError("Required");
                    return;
                }

                Toast.makeText(requireActivity(), "Please wait...", Toast.LENGTH_SHORT).show();

                UpdateAppointmentToDatabase();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Please wait, Deleting Category", Toast.LENGTH_SHORT).show();
                DeleteAppointment();
            }
        });

        return view;
    }

    private void UpdateAppointmentToDatabase() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("Sickness_title", Sickness_title.getText().toString());
        data.put("Symptoms_description", Symptoms_description.getText().toString());

        if (date != null) {
            data.put("date", date);
            data.put("month", month);
            data.put("year", year);
            data.put("hour", hour);
            data.put("minutes", minutes);
        }

        DatabaseReference ownerRef;
        ownerRef = FirebaseDatabase.getInstance().getReference().child("Appointments");

        ownerRef.child(appointment.getID()).updateChildren(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireActivity(), "Appointment updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(requireActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(requireActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void DeleteAppointment() {
        DatabaseReference ownerRef;
        ownerRef = FirebaseDatabase.getInstance().getReference().child("Appointments");

        ownerRef.child(appointment.getID()).setValue(null)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireActivity(), "Appointment Delete successfully", Toast.LENGTH_SHORT).show();
                        getDialog().dismiss();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(requireActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(requireActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}