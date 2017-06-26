package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;

import fhtw.bsa2.gafert_steiner.BloodMonitor.R;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_HAPPY;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_NORMAL;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_SAD;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_VERY_HAPPY;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_VERY_SAD;

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
        holder.locationView.setText(_item.getLocationString());

        if (mSortedList.get(listPosition).getReason() == null ||
                mSortedList.get(listPosition).getReason().equals("")) {
            holder.reasonView.setText("...");
        } else {
            holder.reasonView.setText(mSortedList.get(listPosition).getReason());
        }

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

    public void add(Item model) {
        mSortedList.add(model);
    }

    public void add(List<Item> models) {
        mSortedList.addAll(models);
    }

    public void remove(Item model) {
        mSortedList.remove(model);
    }

    public void remove(List<Item> models) {
        mSortedList.beginBatchedUpdates();
        for (Item model : models) {
            mSortedList.remove(model);
        }
        mSortedList.endBatchedUpdates();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView dateView;
        public TextView reasonView;
        public ImageView emotionImageView;
        public TextView idView;
        public TextView locationView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateView = (TextView) itemView.findViewById(R.id.dateTextView);
            reasonView = (TextView) itemView.findViewById(R.id.reasonTextView);
            emotionImageView = (ImageView) itemView.findViewById(R.id.emotionImageView);
            idView = (TextView) itemView.findViewById(R.id.idTextView);
            locationView = (TextView) itemView.findViewById(R.id.locationTextView);
        }

        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + " " + dateView.getText());
        }
    }
}
