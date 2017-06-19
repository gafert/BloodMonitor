package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import java.text.SimpleDateFormat;

import java.util.Date;


public class Item {

    Date date;
    Integer mood;
    String imagePath;
    String reason;

    public Item(Date date, Integer mood, String imagePath, String reason) {
        this.date = date;
        this.mood = mood;
        this.imagePath = imagePath;
        this.reason = reason;
    }

    public Date getDate() {
        return date;
    }

    public String getDateString(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd. MM YYYY");
        return sdf.format(date);
    }

    public void setDate(Date entryDate) {
        this.date = entryDate;
    }

    public Integer getMood() {
        return mood;
    }

    public void setMood(Integer mood) {
        this.mood = mood;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}