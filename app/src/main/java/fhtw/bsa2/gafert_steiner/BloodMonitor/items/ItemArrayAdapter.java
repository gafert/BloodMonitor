package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import fhtw.bsa2.gafert_steiner.BloodMonitor.R;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_HAPPY;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_NORMAL;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_SAD;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_VERY_HAPPY;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_VERY_SAD;

/**
 * Used to display the Items in a {@link RecyclerView}
 */
public class ItemArrayAdapter extends RecyclerView.Adapter<ItemArrayAdapter.ViewHolder> {

    private final Comparator<Item> comparator;
    private int listItemLayout;
    private Context context;
    private ItemArrayAdapter adapter;
    private final SortedList<Item> mSortedList = new SortedList<>(Item.class, new SortedList.Callback<Item>() {
        @Override
        public void onInserted(int position, int count) {
            adapter.notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            adapter.notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            adapter.notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            adapter.notifyItemRangeChanged(position, count);
        }

        @Override
        public int compare(Item a, Item b) {
            return comparator.compare(a, b);
        }

        @Override
        public boolean areContentsTheSame(Item oldItem, Item newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Item item1, Item item2) {
            return item1 == item2;
        }
    });

    /**
     * @param context    Application context needed to get the address by the location with {@link Geocoder}
     * @param layoutId   The Layout Resource
     * @param comparator The Comparator to sort the list (e.g Alphabetically)
     *                   We only use DATE_COMPARATOR
     */
    public ItemArrayAdapter(Context context, int layoutId, Comparator<Item> comparator) {
        this.listItemLayout = layoutId;
        this.comparator = comparator;
        this.context = context;
        this.adapter = this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        Item _item = mSortedList.get(listPosition);
        holder.dateView.setText(_item.getTimestampString());
        holder.idView.setText(String.valueOf(_item.getId()));
        holder.heartRateView.setText(String.valueOf(_item.getHeartRate()));
        holder.diastolicView.setText(String.valueOf(_item.getDiastolicPressure()));
        holder.systolicView.setText(String.valueOf(_item.getSystolicPressure()));

        // Set location
        // Get address and other stuff and display it
        // If the address is already in the item
        if (_item.getLocation() != null) {
            final Double latitude = _item.getLocation().getLatitude();
            final Double longitude = _item.getLocation().getLongitude();

            // Geocoder makes recyclerView lag on initialisation of the item -> make in async task
            new AsyncGetAddress().execute(latitude, longitude, holder.locationView);

            holder.locationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("geo:<" + latitude + ">,<" + longitude + ">?q=<" + latitude + ">,<" + longitude + ">(Blood Monitor Entry)");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
            });
        }

        // Set reason
        if (mSortedList.get(listPosition).getReason() == null ||
                mSortedList.get(listPosition).getReason().equals("")) {
            holder.reasonView.setText("...");
        } else {
            holder.reasonView.setText(mSortedList.get(listPosition).getReason());
        }

        // Set emotion Image
        if (mSortedList.get(listPosition).getMood() != null) {
            switch (mSortedList.get(listPosition).getMood()) {
                case FEELING_VERY_HAPPY:
                    holder.emotionImageView.setImageResource(R.drawable.heart_eyes_emoji);
                    break;
                case FEELING_HAPPY:
                    holder.emotionImageView.setImageResource(R.drawable.slightly_smiling_face_emoji);
                    break;
                case FEELING_SAD:
                    holder.emotionImageView.setImageResource(R.drawable.sad_face_emoji);
                    break;
                case FEELING_NORMAL:
                    holder.emotionImageView.setImageResource(R.drawable.confused_face_emoji);
                    break;
                case FEELING_VERY_SAD:
                    holder.emotionImageView.setImageResource(R.drawable.loudly_crying_face_emoji);
                    break;
                default:
                    holder.emotionImageView.setImageResource(R.drawable.slightly_smiling_face_emoji);
                    break;
            }
        } else {
            holder.emotionImageView.setImageResource(R.drawable.slightly_smiling_face_emoji);
        }
    }

    @Override
    public int getItemCount() {
        return mSortedList == null ? 0 : mSortedList.size();
    }

    /**
     * Replaces all elements of the recyclerView with a new list
     *
     * @param models The Item list of the new Items
     */
    public void replaceAll(List<Item> models) {
        mSortedList.beginBatchedUpdates();
        for (int i = mSortedList.size() - 1; i >= 0; i--) {
            final Item model = mSortedList.get(i);
            if (!models.contains(model)) {
                mSortedList.remove(model);
            }
        }
        mSortedList.addAll(models);
        mSortedList.endBatchedUpdates();
    }

    /**
     * Adds a single Element to the RecyclerView
     * @param model The Item to be added
     */
    public void add(Item model) {
        mSortedList.add(model);
    }

    /**
     * Addes a List of Item to the RecyclerView
     * @param models The items to be added
     */
    public void add(List<Item> models) {
        mSortedList.addAll(models);
    }

    /**
     * Removes a specific Item
     * @param model Item to be removed
     */
    public void remove(Item model) {
        mSortedList.remove(model);
    }

    /**
     * Removes a List of Items
     * @param models Items to be removed
     */
    public void remove(List<Item> models) {
        mSortedList.beginBatchedUpdates();
        for (Item model : models) {
            mSortedList.remove(model);
        }
        mSortedList.endBatchedUpdates();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dateView;
        TextView reasonView;
        ImageView emotionImageView;
        TextView idView;
        TextView locationView;
        TextView heartRateView;
        TextView systolicView;
        TextView diastolicView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateView = (TextView) itemView.findViewById(R.id.dateTextView);
            reasonView = (TextView) itemView.findViewById(R.id.reasonTextView);
            emotionImageView = (ImageView) itemView.findViewById(R.id.emotionImageView);
            idView = (TextView) itemView.findViewById(R.id.idTextView);
            locationView = (TextView) itemView.findViewById(R.id.locationTextView);
            heartRateView = (TextView) itemView.findViewById(R.id.heartRateTextView);
            diastolicView = (TextView) itemView.findViewById(R.id.diastolicTextView);
            systolicView = (TextView) itemView.findViewById(R.id.systolicTextView);
        }

        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + " " + dateView.getText());
        }
    }

    /**
     * Gets the address in an AsyncTask to prevent lagging of the recyclerView
     * {@link Geocoder} requires Internet, when there is no connection the Location will be set to
     * "No Location". If the Geocoder has Internet it looks the address up and returns it.
     */
    private class AsyncGetAddress extends AsyncTask<Object, String, String> {
        private TextView locationView;

        @Override
        protected String doInBackground(Object... params) {
            Double latitude = (Double) params[0];
            Double longitude = (Double) params[1];
            locationView = (TextView) params[2];

            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.getDefault());

                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                return city + " " + postalCode + ", " + address;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "No Location Data";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            locationView.setText(s);
        }
    }
}
