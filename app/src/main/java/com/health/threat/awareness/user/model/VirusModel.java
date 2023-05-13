package com.health.threat.awareness.user.model;

import java.util.ArrayList;

public class VirusModel {
    String ID;
    String Latitude;
    String Longitude;
    String Altitude;
    String CaseCategory;
    String CaseStatus;
    String Case_description;
    String Case_title;
    String HospitalID;
    String HospitalName;
    String Name;
    String Remarks;
    String Sickness;
    Boolean SicknessIdentified;
    ArrayList<String> AffectedUsersID = new ArrayList<>();
    ArrayList<String> AppointmentsIDs = new ArrayList<>();
    String Date;
    String Month;
    String Year;
    String Minutes;
    String Hour;
    String By;
    String AffectedUserID;

    public VirusModel() {
    }

    public VirusModel(String ID, String latitude, String longitude, String altitude, String caseCategory, String caseStatus, String case_description, String case_title, String hospitalID, String hospitalName, String name, String remarks, String sickness, Boolean sicknessIdentified, ArrayList<String> affectedUsersID, ArrayList<String> appointmentsIDs, String date, String month, String year, String minutes, String hour) {
        this.ID = ID;
        Latitude = latitude;
        Longitude = longitude;
        Altitude = altitude;
        CaseCategory = caseCategory;
        CaseStatus = caseStatus;
        Case_description = case_description;
        Case_title = case_title;
        HospitalID = hospitalID;
        HospitalName = hospitalName;
        Name = name;
        Remarks = remarks;
        Sickness = sickness;
        SicknessIdentified = sicknessIdentified;
        AffectedUsersID = affectedUsersID;
        AppointmentsIDs = appointmentsIDs;
        Date = date;
        Month = month;
        Year = year;
        Minutes = minutes;
        Hour = hour;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getAltitude() {
        return Altitude;
    }

    public void setAltitude(String altitude) {
        Altitude = altitude;
    }

    public String getCaseCategory() {
        return CaseCategory;
    }

    public void setCaseCategory(String caseCategory) {
        CaseCategory = caseCategory;
    }

    public String getCaseStatus() {
        return CaseStatus;
    }

    public void setCaseStatus(String caseStatus) {
        CaseStatus = caseStatus;
    }

    public String getCase_description() {
        return Case_description;
    }

    public void setCase_description(String case_description) {
        Case_description = case_description;
    }

    public String getCase_title() {
        return Case_title;
    }

    public void setCase_title(String case_title) {
        Case_title = case_title;
    }

    public String getHospitalID() {
        return HospitalID;
    }

    public void setHospitalID(String hospitalID) {
        HospitalID = hospitalID;
    }

    public String getHospitalName() {
        return HospitalName;
    }

    public void setHospitalName(String hospitalName) {
        HospitalName = hospitalName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getSickness() {
        return Sickness;
    }

    public void setSickness(String sickness) {
        Sickness = sickness;
    }

    public Boolean getSicknessIdentified() {
        return SicknessIdentified;
    }

    public void setSicknessIdentified(Boolean sicknessIdentified) {
        SicknessIdentified = sicknessIdentified;
    }

    public ArrayList<String> getAffectedUsersID() {
        return AffectedUsersID;
    }

    public void setAffectedUsersID(ArrayList<String> affectedUsersID) {
        AffectedUsersID = affectedUsersID;
    }

    public ArrayList<String> getAppointmentsIDs() {
        return AppointmentsIDs;
    }

    public void setAppointmentsIDs(ArrayList<String> appointmentsIDs) {
        AppointmentsIDs = appointmentsIDs;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getMinutes() {
        return Minutes;
    }

    public void setMinutes(String minutes) {
        Minutes = minutes;
    }

    public String getHour() {
        return Hour;
    }

    public void setHour(String hour) {
        Hour = hour;
    }

    public String getBy() {
        return By;
    }

    public void setBy(String by) {
        By = by;
    }

    public String getAffectedUserID() {
        return AffectedUserID;
    }

    public void setAffectedUserID(String affectedUserID) {
        AffectedUserID = affectedUserID;
    }
}
