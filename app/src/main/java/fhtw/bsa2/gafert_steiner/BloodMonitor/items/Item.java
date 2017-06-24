package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.FEELING_NORMAL;


public class Item {
    private int id;
    private Date timestamp = new Date();
    private Integer mood = FEELING_NORMAL;
    private String reason;

    // Parser for manual add
    public Item(@NonNull Date timestamp, @NonNull Integer mood, @Nullable String reason) {
        this.timestamp = timestamp;
        this.mood = mood;
        this.reason = reason;
        this.id = Index.getInstance().getNextID();
        if (reason == null) {
            this.reason = "";
        }
    }

    public Item(@NonNull Integer id, @NonNull Date timestamp, @NonNull Integer mood, @Nullable String reason) {
        this.timestamp = timestamp;
        this.mood = mood;
        this.reason = reason;
        this.id = id;
        if (reason == null) {
            this.reason = "";
        }
    }

    public int getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM YYYY");
        return sdf.format(timestamp);
    }

    public Integer getMood() {
        return mood;
    }

    public void setMood(@NonNull Integer mood) {
        this.mood = mood;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}