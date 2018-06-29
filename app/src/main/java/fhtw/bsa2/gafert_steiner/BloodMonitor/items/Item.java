package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import android.support.annotation.NonNull;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_NORMAL;


public class Item {
    private int id;
    private Date timestamp;
    private Integer mood;
    private String reason;
    private SimpleLocation location;
    private int heartRate;
    private int diastolicPressure;
    private int systolicPressure;

    /**
     * Creates an Item with a unique ID
     *
     * @param location  Where the recording was taken
     * @param timestamp The date of the entry (as it is a diary app)
     * @param mood      How the person is feeling
     * @param reason    Why the person is feeling that way
     */
    public Item(SimpleLocation location, @NonNull Date timestamp, int mood, String reason, Integer systolicPressure, Integer diastolicPressure, Integer heartRate) {
        // Set the last ID to the size of all Items in the Files/List/Server
        IDProvider.getInstance().setLastID(ItemHolder.getInstance().getItems().size());
        this.id = IDProvider.getInstance().getNextID();
        this.timestamp = timestamp;
        this.mood = mood;
        this.reason = reason;
        this.location = location;

        setSystolicPressure(systolicPressure);
        setDiastolicPressure(diastolicPressure);
        setHeartRate(heartRate);
    }

    public int getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        if(heartRate != null){
            this.heartRate = heartRate;
        }else{
            this.heartRate = 0;
        }
    }

    public int getDiastolicPressure() {
        return diastolicPressure;
    }

    public void setDiastolicPressure(Integer diastolicPressure) {
        if(diastolicPressure != null){
            this.diastolicPressure = diastolicPressure;
        }else{
            this.diastolicPressure = 0;
        }
    }

    public int getSystolicPressure() {
        return systolicPressure;
    }

    public void setSystolicPressure(Integer systolicPressure) {
        if(systolicPressure != null){
            this.systolicPressure = systolicPressure;
        }else{
            this.systolicPressure = 0;
        }
    }

    public SimpleLocation getLocation() {
        return location;
    }

    public void setLocation(SimpleLocation location) {
        this.location = location;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NonNull Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestampString() {
        if (timestamp == null) {
            Log.e("MainActivity", "Item with ID=" + getId() + " does not have a timestamp");
            return "NO DATE!";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM yyyy", Locale.getDefault());
            return sdf.format(timestamp);
        }
    }

    public Integer getMood() {
        if (mood == null) {
            mood = FEELING_NORMAL;
        }
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