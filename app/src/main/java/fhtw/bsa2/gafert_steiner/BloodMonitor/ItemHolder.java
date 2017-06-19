package fhtw.bsa2.gafert_steiner.BloodMonitor;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import fhtw.bsa2.gafert_steiner.BloodMonitor.items.Item;

/**
 * Created by michi on 19.06.17.
 */

public class ItemHolder {
    private static final ItemHolder ourInstance = new ItemHolder();
    private ArrayList<Item> items;
    private ArrayList<Item> itemsReversed;
    private ArrayList<ItemsChangedListener> listener;

    private ItemHolder() {
        items = new ArrayList<>();
        itemsReversed = new ArrayList<>();
        listener = new ArrayList<>();
    }

    @Nullable
    public static ItemHolder getInstance() {
        return ourInstance;
    }

    public ArrayList<Item> getItems() {
        if(items.isEmpty()){
            loadFromFile();
        }
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void setDummyItems() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        try {
            Item item1 = new Item(sdf.parse("070501"), GlobalShit.FEELING_HAPPY, null, "I am a dummy item and have no feels...");
            Item item2 = new Item(sdf.parse("080602"), GlobalShit.FEELING_HAPPY, null, "I am a dummy item and have no feels...");
            Item item3 = new Item(sdf.parse("090703"), GlobalShit.FEELING_HAPPY, null, "I am a dummy item and have no feels...");
            Item item4 = new Item(sdf.parse("100804"), GlobalShit.FEELING_HAPPY, null, "I am a dummy item and have no feels...");

            add(item1);
            add(item2);
            add(item3);
            add(item4);
        } catch (ParseException e) {
            Log.e("ItemHolder", "setDummyItems: Could not set dummy items");
        }
    }

    public ArrayList<Item> getItemsReversed() {
        if(items.isEmpty()){
            loadFromFile();
        }
        itemsReversed = (ArrayList<Item>) items.clone();

        Collections.sort(items, new Comparator<Item>() {
            public int compare(Item o1, Item o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        return itemsReversed;
    }

    public void add(Item newEntry) {

        // Replace already set item
        int i = 0;
        boolean duplicate = false;
        for (Item entry : items) {
            if (newEntry.getDate().equals(entry.getDate())) {
                duplicate = true;
                break;
            }
            i++;
        }
        if (duplicate == true) {
            items.set(i, newEntry);
        } else {
            items.add(newEntry);
        }

        Collections.sort(items, new Comparator<Item>() {
            public int compare(Item o1, Item o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        // Fire the custom listener
        if (listener != null) {
            for (ItemsChangedListener _listener : listener)
                _listener.onChanged();   // Call listener
        }

        //Convert ArrayList of Emotion Entries to Json and save it to local file
        Gson gson = new Gson();
        String json = gson.toJson(items, GlobalShit.listType);
        Log.d("JSON", json);

        // Get from File
        //TODO: Get files from server and sync
        FileIO IO = FileIO.getInstance();
        IO.writeFile(json);
    }

    public void deleteAll() {
        items = null;
        itemsReversed = null;

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

    public void loadFromFile() {
        // Load Entries from file
        FileIO fileIO = FileIO.getInstance();
        String emotionJson = fileIO.readFile();

        if (emotionJson != null) {
            Gson gson = new Gson();
            items = gson.fromJson(emotionJson, GlobalShit.listType);
        }
    }

    public interface ItemsChangedListener {
        void onChanged();
    }
}
