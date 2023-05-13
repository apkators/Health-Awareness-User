package com.health.threat.awareness.user.dialog_fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.health.threat.awareness.user.R;
import com.health.threat.awareness.user.model.VirusModel;

import java.util.Objects;

public class VirusDetailDialogFragment extends DialogFragment {
    VirusModel virusModel;
    ImageButton cancelBtn;
    TextView HospitalName, DateAndTimeTV;
    TextView Case_title, Case_description;
    TextView PatientName,PatientEmail,PatientPhone;
    TextView tvLatitude,tvLongitude,tvAltitude;
    //Button btnMarkAreaOnMap; //date_time_set
    String date, month, year, hour, minutes;
    TextView RemarksTV;
    View RemarksView;
    Spinner StatusSpinner;

    public VirusDetailDialogFragment(){}

    public VirusDetailDialogFragment(VirusModel v) {
        // Required empty public constructor
        virusModel = v;
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
        View view = inflater.inflate(R.layout.fragment_virus_detail_dialog, container, false);

        date = month = year = hour = minutes = null;

        cancelBtn = view.findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        tvLatitude = view.findViewById(R.id.tvLatitude);
        tvLongitude = view.findViewById(R.id.tvLongitude);
        tvAltitude = view.findViewById(R.id.tvAltitude);
        HospitalName = view.findViewById(R.id.HospitalName);
        Case_title = view.findViewById(R.id.Case_title);
        Case_description = view.findViewById(R.id.Case_description);
        DateAndTimeTV = view.findViewById(R.id.DateAndTimeTV);
        PatientName = view.findViewById(R.id.PatientName);
        PatientEmail = view.findViewById(R.id.PatientEmail);
        PatientPhone = view.findViewById(R.id.PatientPhone);
        RemarksView = view.findViewById(R.id.RemarksView);
        RemarksTV = view.findViewById(R.id.RemarksTV);
        StatusSpinner =view.findViewById(R.id.StatusSpinner);

        String[] statusSpinners={"Select","Normal","Virus","Emergency"};

        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,statusSpinners);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        StatusSpinner.setAdapter(aa);
        StatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*if (position!=0 && position!=1)
                {
                    //Show Remarks for Admin
                    RemarksView.setVisibility(View.VISIBLE);
                }else
                    RemarksView.setVisibility(View.GONE);*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //date_time_set = view.findViewById(R.id.date_time_set);
        //btnMarkAreaOnMap = view.findViewById(R.id.btnMarkAreaOnMap);

        if (virusModel.getDate() != null && !virusModel.getDate().equals(""))
            DateAndTimeTV.setText("" + virusModel.getDate() + "-" + virusModel.getMonth() + "-" + virusModel.getYear() + " " + virusModel.getHour() + ":" + virusModel.getMinutes());
        else
            DateAndTimeTV.setVisibility(View.GONE);

        if (virusModel.getHospitalName() != null && !virusModel.getHospitalName().equals(""))
            HospitalName.setText("" + virusModel.getHospitalName());
        else
            HospitalName.setVisibility(View.GONE);

        if (virusModel.getCase_title() != null && !virusModel.getCase_title().equals(""))
            Case_title.setText("" + virusModel.getCase_title());
        else
            Case_title.setVisibility(View.GONE);

        if (virusModel.getCase_description() != null && !virusModel.getCase_description().equals(""))
            Case_description.setText("" + virusModel.getCase_description());
        else
            Case_description.setVisibility(View.GONE);

        if (virusModel.getLatitude() != null && !virusModel.getLatitude().equals(""))
            tvLatitude.setText("" + virusModel.getLatitude());
        else
            tvLatitude.setVisibility(View.GONE);

        if (virusModel.getLongitude() != null && !virusModel.getLongitude().equals(""))
            tvLongitude.setText("" + virusModel.getLongitude());
        else
            tvLongitude.setVisibility(View.GONE);

        if (virusModel.getAltitude() != null && !virusModel.getAltitude().equals(""))
            tvAltitude.setText("" + virusModel.getAltitude());
        else
            tvAltitude.setVisibility(View.GONE);

        if (virusModel.getCaseStatus() != null && !virusModel.getCaseStatus().equals("")) {
            StatusSpinner.setSelection(aa.getPosition("" + virusModel.getCaseStatus()));
            StatusSpinner.setFocusable(false);
            StatusSpinner.setFocusableInTouchMode(false);
            StatusSpinner.setClickable(false);
            StatusSpinner.setOnItemSelectedListener(null);
        }

        if (virusModel.getRemarks() != null && !virusModel.getRemarks().equals(""))
            RemarksTV.setText("" + virusModel.getRemarks());
        else
            RemarksTV.setVisibility(View.GONE);

        /*FirebaseDatabase.getInstance().getReference().child("AppUsers").child(virusModel.getAffectedUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String patientName = snapshot.child("Name").getValue(String.class);
                    String patientMobile = snapshot.child("Mobile").getValue(String.class);
                    String patientEmail = snapshot.child("Email").getValue(String.class);

                    PatientName.setText(patientName + "");
                    PatientEmail.setText(patientEmail + "");
                    PatientPhone.setText(patientMobile + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        return view;
    }
}