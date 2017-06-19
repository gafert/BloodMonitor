package fhtw.bsa2.gafert_steiner.BloodMonitor.items;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;

import fhtw.bsa2.gafert_steiner.BloodMonitor.R;

public class ItemArrayAdapter extends RecyclerView.Adapter<ItemArrayAdapter.ViewHolder> {

    private final Comparator<Item> comparator;
    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private int listItemLayout;
    private Context context;
    private ItemArrayAdapter mAdapter;
    private final SortedList<Item> mSortedList = new SortedList<>(Item.class, new SortedList.Callback<Item>() {
        @Override
        public void onInserted(int position, int count) {
            mAdapter.notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            mAdapter.notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            mAdapter.notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            mAdapter.notifyItemRangeChanged(position, count);
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

    // Constructor of the class
    public ItemArrayAdapter(Context context, int layoutId, Comparator<Item> comparator) {
        listItemLayout = layoutId;
        this.comparator = comparator;
        this.context = context;
        mAdapter = this;
    }

    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        holder.dateView.setText(mSortedList.get(listPosition).getDateString());
        holder.reasonView.setText(mSortedList.get(listPosition).getReason());
    }

    // get the size of the list
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

    public void remove(Item model) {
        mSortedList.remove(model);
    }

    public void add(List<Item> models) {
        mSortedList.addAll(models);
    }

    public void remove(List<Item> models) {
        mSortedList.beginBatchedUpdates();
        for (Item model : models) {
            mSortedList.remove(model);
        }
        mSortedList.endBatchedUpdates();
    }

    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView dateView;
        public TextView reasonView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateView = (TextView) itemView.findViewById(R.id.dateTextView);
            reasonView = (TextView) itemView.findViewById(R.id.reasonTextView);
        }

        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + " " + dateView.getText());
        }
    }
}
