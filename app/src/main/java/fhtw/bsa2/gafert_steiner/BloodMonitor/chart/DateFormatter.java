package fhtw.bsa2.gafert_steiner.BloodMonitor.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fhtw.bsa2.gafert_steiner.BloodMonitor.items.Item;

public class DateFormatter implements IAxisValueFormatter {

    private ArrayList<Item> items;

    public DateFormatter() {
        items = new ArrayList<>();
    }

    public DateFormatter(ArrayList<Item> items) {
        this.items = items;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        String newDate = ""; // Set default

        if (!items.isEmpty()) {
            try {
                Item emotionEntry = items.get((int) value);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E d. MMM");
                Date date = emotionEntry.getTimestamp();
                newDate = simpleDateFormat.format(date);                                           // Set the date to the text
            } catch (Exception e) {
                //Log.e("AddFragment", "onDateSet: Could not parse to date string");
            }
        }

        return newDate;
    }
}
