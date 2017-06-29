package fhtw.bsa2.gafert_steiner.BloodMonitor.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import fhtw.bsa2.gafert_steiner.BloodMonitor.items.Item;

/**
 * Used by {@link com.github.mikephil.charting.charts.LineChart} to style the x Axis Values
 * to the corresponding Dates of the {@link Item}
 */
public class DateFormatter implements IAxisValueFormatter {

    private final ArrayList<Item> items;

    public DateFormatter(ArrayList<Item> items) {
        this.items = items;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        String newDate = ""; // Set default

        if (!items.isEmpty()) {
            try {
                Item emotionEntry = items.get((int) value);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E d. MMM", Locale.getDefault());
                Date date = emotionEntry.getTimestamp();
                newDate = simpleDateFormat.format(date);                                           // Set the date to the text
            } catch (Exception e) {
                //Log.e("AddFragment", "onDateSet: Could not parse to date string");
            }
        }

        return newDate;
    }
}
