package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Item {
    private int id;
    private Date date;
    private Integer mood;
    private String reason;

    public Item(Date timestamp, Integer mood, String reason) {
        this.date = timestamp;
        this.mood = mood;
        this.reason = reason;
        this.id = Index.getInstance().getNextID();
        if (reason == null) {
            this.reason = "";
        }
    }

    public Item(int id, Date timestamp, Integer mood, String reason) {
        this.date = timestamp;
        this.mood = mood;
        this.reason = reason;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return date;
    }

    public void setTimestamp(Date entryDate) {
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