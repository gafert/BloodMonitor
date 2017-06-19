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

    public final static int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final Type listType = new TypeToken<ArrayList<Item>>() {
    }.getType();
}
