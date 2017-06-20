package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import fhtw.bsa2.gafert_steiner.BloodMonitor.FileIO;
import fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit;

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
        if (items.isEmpty()) {
            loadFromFile();
        }
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
        if (listener != null) {
            for (ItemsChangedListener _listener : listener)
                _listener.onChanged();
        }
    }

    public void setDummyItems() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        try {
            Item item1 = new Item(1, sdf.parse("070501"), GlobalShit.FEELING_HAPPY, "I am a dummy dateView and have no feels...");
            Item item2 = new Item(2, sdf.parse("090703"), GlobalShit.FEELING_SAD, "I care!");
            Item item3 = new Item(3, sdf.parse("100804"), GlobalShit.FEELING_VERY_HAPPY, "I dont");
            Item item4 = new Item(4, sdf.parse("081202"), GlobalShit.FEELING_HAPPY, "Sometimes even a dummy has feelings.");
            Item item5 = new Item(4, sdf.parse("082207"), GlobalShit.FEELING_NORMAL, "Normal as always, but I got a text and this text is very very very long, I dont want to be to long, but long enough to show i can be long, I hope im long enough now");
            Item item6 = new Item(4, sdf.parse("091002"), GlobalShit.FEELING_SAD, "I got feelings");
            Item item7 = new Item(4, sdf.parse("100901"), GlobalShit.FEELING_NORMAL, null);
            Item item8 = new Item(4, sdf.parse("111516"), GlobalShit.FEELING_VERY_SAD, "OMG what a teeerible day");
            Item item9 = new Item(4, sdf.parse("011215"), GlobalShit.FEELING_HAPPY, "I hope your happy now");
            Item item10 = new Item(4, sdf.parse("131315"), GlobalShit.FEELING_NORMAL, "I hope your happy now");
            Item item11 = new Item(4, sdf.parse("121117"), GlobalShit.FEELING_HAPPY, "I hope your happy now");
            Item item12 = new Item(4, sdf.parse("221011"), GlobalShit.FEELING_SAD, "Sad as a sandwich");
            Item item13 = new Item(4, sdf.parse("260910"), GlobalShit.FEELING_HAPPY, "Happy as a fruit");
            Item item14 = new Item(4, sdf.parse("280813"), GlobalShit.FEELING_VERY_HAPPY, "I hope your happy now");
            Item item15 = new Item(1, sdf.parse("030704"), GlobalShit.FEELING_HAPPY, "I am a dummy dateView and have no feels...");
            Item item16 = new Item(2, sdf.parse("090602"), GlobalShit.FEELING_SAD, "I care!");
            Item item17 = new Item(3, sdf.parse("100501"), GlobalShit.FEELING_VERY_HAPPY, "I dont");
            Item item18 = new Item(4, sdf.parse("010407"), GlobalShit.FEELING_HAPPY, "Sometimes even a dummy has feelings.");
            Item item19 = new Item(4, sdf.parse("080302"), GlobalShit.FEELING_NORMAL, "Normal as always, but I got a text and this text is very very very long, I dont want to be to long, but long enough to show i can be long, I hope im long enough now");
            Item item20 = new Item(4, sdf.parse("091009"), GlobalShit.FEELING_SAD, "I got feelings");
            Item item21 = new Item(4, sdf.parse("100904"), GlobalShit.FEELING_NORMAL, null);
            Item item22 = new Item(4, sdf.parse("111119"), GlobalShit.FEELING_VERY_SAD, "OMG what a teeerible day");
            Item item23 = new Item(4, sdf.parse("161401"), GlobalShit.FEELING_HAPPY, "I hope your happy now");
            Item item24 = new Item(4, sdf.parse("160510"), GlobalShit.FEELING_NORMAL, "I hope your happy now");
            Item item25 = new Item(4, sdf.parse("051212"), GlobalShit.FEELING_HAPPY, "I hope your happy now");
            Item item26 = new Item(4, sdf.parse("010115"), GlobalShit.FEELING_SAD, "Sad as a sandwich");
            Item item27 = new Item(4, sdf.parse("260713"), GlobalShit.FEELING_HAPPY, "Happy as a fruit");
            Item item28 = new Item(4, sdf.parse("130615"), GlobalShit.FEELING_VERY_HAPPY, "I hope your happy now");

            add(item1);
            add(item2);
            add(item3);
            add(item4);
            add(item5);
            add(item6);
            add(item7);
            add(item8);
            add(item9);
            add(item10);
            add(item11);
            add(item12);
            add(item13);
            add(item14);
            add(item15);
            add(item16);
            add(item17);
            add(item18);
            add(item19);
            add(item20);
            add(item21);
            add(item22);
            add(item23);
            add(item24);
            add(item25);
            add(item26);
            add(item27);
            add(item28);

        } catch (ParseException e) {
            Log.e("ItemHolder", "setDummyItems: Could not set dummy items");
        }
    }

    public ArrayList<Item> getItemsReversed() {
        if (items.isEmpty()) {
            loadFromFile();
        }
        itemsReversed = (ArrayList<Item>) items.clone();

        Collections.sort(itemsReversed, new Comparator<Item>() {
            public int compare(Item o1, Item o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        return itemsReversed;
    }

    public void add(Item newEntry) {
        // Replace already set item by date
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
                return o1.getDate().compareTo(o2.getDate());
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
        items = new ArrayList<>();
        itemsReversed = new ArrayList<>();

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
            ArrayList<Item> _items = gson.fromJson(emotionJson, GlobalShit.listType);
            for (Item _item : _items) {
                add(_item);
            }
        }
    }

    public interface ItemsChangedListener {
        void onChanged();
    }
}
