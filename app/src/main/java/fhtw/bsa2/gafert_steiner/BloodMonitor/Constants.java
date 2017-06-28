package fhtw.bsa2.gafert_steiner.BloodMonitor;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fhtw.bsa2.gafert_steiner.BloodMonitor.items.Item;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.ItemHolder;

/**
 * Created by michi on 19.06.17.
 */

public class Constants {
    public final static int FEELING_VERY_HAPPY = 20;
    public final static int FEELING_HAPPY = 10;
    public final static int FEELING_NORMAL = 0;
    public final static int FEELING_SAD = -10;
    public final static int FEELING_VERY_SAD = -20;

    public static final String INDEX_PREF = "indexpref";
    public static final String SETTINGS = "IpPrefs";
    public static final String IP_PREF = "postIp";
    public static final String PORT_PREF = "port";
    public static final String POST_DIRECTORY_PREF = "postDirectory";
    public static final String GET_DIRECTORY_PREF = "getDirectory";
    public static final String GET_URL_PREF = "getUrl";
    public static final String POST_URL_PREF = "postUrl";

    public static final String ITEMS_FILE = "Items.json";

    public static final Type ITEM_LIST_TYPE_TOKEN = new TypeToken<ArrayList<Item>>() {
    }.getType();

    public static final Type ITEM_TYPE_TOKEN = new TypeToken<Item>() {
    }.getType();

    public static final int LOCATION_REQ_PERM = 99;

    public static final String CHART_EMOTIONS = "emotions";
    public static final String CHART_HEART_RATE = "heartRate";
    public static final String CHART_DIASTOLIC = "diastolic";
    public static final String CHART_SYSTOLIC = "systolic";
    public static final Comparator<Item> DATE_COMPARATOR = new Comparator<Item>() {
        @Override
        public int compare(Item a, Item b) {
            try {
                return b.getTimestamp().compareTo(a.getTimestamp());
            } catch (NullPointerException e) {
                if (!ItemHolder.getInstance().getItems().isEmpty()) {
                    //e.printStackTrace();
                }
            }
            return 0;
        }
    };

    /**
     * Suppress default constructor for noninstantiability
     */
    private Constants() {
        throw new AssertionError();
    }

    public static List<Item> searchFilter(List<Item> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Item> filteredModelList = new ArrayList<>();
        for (Item model : models) {
            final String text = model.getReason().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
