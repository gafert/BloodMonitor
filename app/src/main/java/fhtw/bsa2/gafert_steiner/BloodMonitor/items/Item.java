package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_NORMAL;


public class Item {
    private int id;
    private Date timestamp;
    private Integer mood;
    private String reason;
    private Location location;

    /**
     * Creates an Item with a unique ID
     *
     * @param location  Where the recording was taken
     * @param timestamp The date of the entry (as it is a diary app)
     * @param mood      How the person is feeling
     * @param reason    Why the person is feeling that way
     */
    public Item(Location location, @NonNull Date timestamp, Integer mood, String reason) {
        this.id = IdentificationGenerator.getInstance().getNextID();
        this.timestamp = timestamp;
        this.mood = mood;
        this.reason = reason;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLocationString() {
        if (location == null) {
            return "No location data";
        }
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());
        String location = "Latitude: " + latitude + "\nLongitude: " + longitude;
        return location;
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

    public void setTimestamp(@NonNull Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestampString() {
        if (timestamp == null) {
            Log.e("MainActivity", "Item with ID=" + getId() + " does not have a timestamp");
            return "NO DATE!";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd. MMM YYYY");
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