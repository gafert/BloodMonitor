package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Item {

    int id;
    Date date;
    Integer mood;
    String reason;

    public Item(int id, Date date, Integer mood, String reason) {
        this.id = id;
        this.date = date;
        this.mood = mood;
        this.reason = reason;
        if (reason == null) {
            this.reason = "";
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date entryDate) {
        this.date = entryDate;
    }

    public String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM YYYY");
        return sdf.format(date);
    }

    public Integer getMood() {
        return mood;
    }

    public void setMood(Integer mood) {
        this.mood = mood;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}