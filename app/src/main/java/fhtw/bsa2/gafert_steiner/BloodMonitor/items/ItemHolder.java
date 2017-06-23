package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import fhtw.bsa2.gafert_steiner.BloodMonitor.FileIO;
import fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit;

/**
 * Created by michi on 19.06.17.
 */

public class ItemHolder {
    private static final ItemHolder ourInstance = new ItemHolder();
    private List<Item> items;
    private List<ItemsChangedListener> listener;

    private ItemHolder() {
        items = new ArrayList<>();
        listener = new ArrayList<>();
    }

    @Nullable
    public static ItemHolder getInstance() {
        return ourInstance;
    }

    public List<Item> getItems() {
        if (items.isEmpty()) {
            loadFromFile();
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

    public void loadFromFile() {
        // Load Entries from file
        FileIO fileIO = FileIO.getInstance();
        String emotionJson = fileIO.readFile();

        if (emotionJson != null) {
            setItems((ArrayList<Item>) new Gson().fromJson(emotionJson, GlobalShit.ITEM_LIST_TYPE_TOKEN));
        }
    }

    public void setDummyItems() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        try {
            add(new Item(sdf.parse("070501"), GlobalShit.FEELING_HAPPY, "I am a dummy dateView and have no feels..."));
        } catch (ParseException e) {
            Log.e("ItemHolder", "setDummyItems: Could not set dummy items");
        }
    }

    public void add(Item newEntry) {
        // Replace already set item by date
        int i = 0;
        boolean duplicate = false;
        /*for (Item entry : items) {
            if (newEntry.getDate().equals(entry.getDate())) {
                duplicate = true;
                break;
            }
            i++;
        }*/
        if (duplicate == true) {
            items.set(i, newEntry);
        } else {
            // Assign a index add at it to the array
            newEntry.setId(items.size());
            items.add(newEntry);
        }

        /*Collections.sort(items, new Comparator<Item>() {
            public int compare(Item o1, Item o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });*/

        // Fire the custom listener
        if (listener != null) {
            for (ItemsChangedListener _listener : listener)
                _listener.onChanged();   // Call listener
        }

        FileIO.getInstance().save(items, newEntry);
    }

    public void deleteLocalFiles() {
        items = new ArrayList<>();

        // Deletes all Files
        FileIO IO = FileIO.getInstance();
        IO.deleteFile();

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
