package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import android.content.Context;
import android.support.annotation.NonNull;
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

public class ItemHolder {
    private static final String TAG = "ItemHolder";
    private static ItemHolder ourInstance = null;
    private Context context;
    private List<Item> items;
    private List<ItemsChangedListener> listener;
    private boolean loadedItems = false;

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

    public static ItemHolder getInstance(@NonNull Context context) {
        ourInstance = new ItemHolder(context);
        return ourInstance;
    }

    public List<Item> getItems() {
        if (!loadedItems) {
            loadedItems = true;
            ArrayList<Item> _items = FileIO.getInstance().readItemFile();
            if (_items != null) {
                setItems(_items);
            }
        }
        return items;
    }

    public void setItems(@NonNull List<Item> items) {
        this.items = items;
        FileIO.getInstance().writeItemFile(items);
        if (listener != null) {
            for (ItemsChangedListener _listener : listener)
                _listener.onChanged();
        }
    }

    public void merge(@NonNull List<Item> _items) {
        this.items.addAll(_items);
        FileIO.getInstance().writeItemFile(items);
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

    public boolean add(@NonNull Item newEntry) {
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

            // Sort the list by timestamp
            Collections.sort(items, new Comparator<Item>() {
                public int compare(Item o1, Item o2) {
                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }
            });

            if (listener != null) {
                for (ItemsChangedListener _listener : listener)
                    _listener.onChanged();   // Call listener
            }

            FileIO.getInstance().writeItemFile(items);
            FileIO.getInstance().writeToServer(newEntry);
        }
        return !duplicate;
    }

    public void deleteLocalFiles() {
        items.clear();
        FileIO IO = FileIO.getInstance();
        IO.deleteFiles();
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
