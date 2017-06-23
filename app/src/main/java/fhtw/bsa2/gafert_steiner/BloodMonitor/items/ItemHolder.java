package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fhtw.bsa2.gafert_steiner.BloodMonitor.FileIO;
import fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit;

/**
 * Created by michi on 19.06.17.
 */

public class ItemHolder {
    private static final String TAG = "ItemHolder";
    private static ItemHolder ourInstance = null;
    private Context context;
    private List<Item> items;
    private List<ItemsChangedListener> listener;

    private ItemHolder(Context context) {
        this.items = new ArrayList<>();
        this.listener = new ArrayList<>();
        this.context = context;
    }

    @Nullable
    public static ItemHolder getInstance() {
        if (ourInstance == null) {
            Log.e(TAG, "getInstance: Context not set");
        }
        return ourInstance;
    }

    public static ItemHolder getInstance(Context context) {
        ourInstance = new ItemHolder(context);
        return ourInstance;
    }

    public List<Item> getItems() {
        if (items.isEmpty()) {
            ArrayList<Item> items = FileIO.getInstance().readListFile();
            if (items != null) {
                setItems(items);
            }
        }
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        if (listener != null) {
            for (ItemsChangedListener _listener : listener)
                _listener.onChanged();
        }
    }

    public void setDummyItems() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        try {
            add(new Item(sdf.parse("070501"), GlobalShit.FEELING_HAPPY, "I am a dummy dateView and have no feels..."));
        } catch (ParseException e) {
            Log.e("ItemHolder", "setDummyItems: Could not set dummy item");
        }
    }

    public boolean add(Item newEntry) {
        // Replace already set item by date
        boolean duplicate = false;
        for (Item entry : items) {
            if (newEntry.getTimestamp().equals(entry.getTimestamp())) {
                duplicate = true;
                break;
            }
        }

        if (duplicate) {
            Toast.makeText(context, "Already saved with this date", Toast.LENGTH_SHORT).show();
        } else {
            // newEntry.setId(items.size());
            items.add(newEntry);

            // Fire the custom listener
            if (listener != null) {
                for (ItemsChangedListener _listener : listener)
                    _listener.onChanged();   // Call listener
            }

            FileIO.getInstance().saveListFile(items);
            FileIO.getInstance().saveToServer(newEntry);
        }

        Collections.sort(items, new Comparator<Item>() {
            public int compare(Item o1, Item o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });

        return !duplicate;
    }

    public void deleteLocalFiles() {
        items.clear();

        // Deletes all Files
        FileIO IO = FileIO.getInstance();
        IO.deleteFiles();

        // Fire the custom listener
        if (listener != null)
            for (ItemsChangedListener _listener : listener)
                _listener.onChanged();   // Call listener
    }

    public void setItemsChangedListener(ItemsChangedListener _listener) {
        listener.add(_listener);
    }

    public interface ItemsChangedListener {
        void onChanged();
    }

}
