package fhtw.bsa2.gafert_steiner.BloodMonitor;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import fhtw.bsa2.gafert_steiner.BloodMonitor.items.Item;

/**
 * Created by michi on 19.06.17.
 */

public class GlobalShit {
    // Static Values for the feeling
    public final static int FEELING_VERY_HAPPY = 20;
    public final static int FEELING_HAPPY = 10;
    public final static int FEELING_NORMAL = 0;
    public final static int FEELING_SAD = -10;
    public final static int FEELING_VERY_SAD = -20;

    public static final Type ITEM_LIST_TYPE_TOKEN = new TypeToken<ArrayList<Item>>() {
    }.getType();

    public static final Type ITEM_TYPE_TOKEN = new TypeToken<Item>() {
    }.getType();
}
