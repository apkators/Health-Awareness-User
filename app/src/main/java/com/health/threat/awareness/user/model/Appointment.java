package com.health.threat.awareness.user.model;

public class Appointment {
    String ID;
    String Sickness_title;
    String Symptoms_description;
    String HospitalID;
    String HospitalName;
    String date;
    String month;
    String year;
    String hour;
    String minutes;
    String By;
    String AppointmentStatus;
    boolean SicknessIdentified;
    String Sickness;

    public Appointment() {
    }

    public Appointment(String ID, String sickness_title, String symptoms_description, String hospitalID, String hospitalName, String date, String month, String year, String hour, String minutes, String by, String appointmentStatus, boolean sicknessIdentified, String sickness) {
        this.ID = ID;
        Sickness_title = sickness_title;
        Symptoms_description = symptoms_description;
        HospitalID = hospitalID;
        HospitalName = hospitalName;
        this.date = date;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minutes = minutes;
        By = by;
        AppointmentStatus = appointmentStatus;
        SicknessIdentified = sicknessIdentified;
        Sickness = sickness;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSickness_title() {
        return Sickness_title;
    }

    public void setSickness_title(String sickness_title) {
        Sickness_title = sickness_title;
    }

    public String getSymptoms_description() {
        return Symptoms_description;
    }

    public void setSymptoms_description(String symptoms_description) {
        Symptoms_description = symptoms_description;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getBy() {
        return By;
    }

    public void setBy(String by) {
        By = by;
    }

    public String getAppointmentStatus() {
        return AppointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        AppointmentStatus = appointmentStatus;
    }

    public boolean getSicknessIdentified() {
        return SicknessIdentified;
    }

    public void setSicknessIdentified(boolean sicknessIdentified) {
        SicknessIdentified = sicknessIdentified;
    }

    public String getSickness() {
        return Sickness;
    }

    public void setSickness(String sickness) {
        Sickness = sickness;
    }
}
