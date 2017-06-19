package fhtw.bsa2.gafert_steiner.BloodMonitor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fhtw.bsa2.gafert_steiner.BloodMonitor.FileIO;
import fhtw.bsa2.gafert_steiner.BloodMonitor.R;
import fhtw.bsa2.gafert_steiner.BloodMonitor.RecyclerViewMargin;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.Item;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.ItemArrayAdapter;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.ItemHolder;

public class MainActivity extends AppCompatActivity {

    private static final Comparator<Item> DATE_COMPARATOR = new Comparator<Item>() {
        @Override
        public int compare(Item a, Item b) {
            return b.getDate().compareTo(a.getDate());
        }
    };

    private static List<Item> filter(List<Item> models, String query) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Read and write access via context
        FileIO.getInstance(getApplicationContext());

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final ItemArrayAdapter itemArrayAdapter = new ItemArrayAdapter(this, R.layout.example_recycler_view_element, DATE_COMPARATOR);
        itemArrayAdapter.add(ItemHolder.getInstance().getItems());
        recyclerView.setAdapter(itemArrayAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerViewMargin(20, 1));

        View bottomSheet = findViewById(R.id.bottom_sheet1);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setPeekHeight(160);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        Button chart = (Button) findViewById(R.id.button);
        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

        ImageButton deleteButton = (ImageButton) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemHolder.getInstance().deleteAll();
            }
        });

        ImageButton addButton = (ImageButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addActivity = new Intent(MainActivity.this, AddActivity.class);
                startActivity(addActivity);
            }
        });

        ImageButton addDummyButton = (ImageButton) findViewById(R.id.addDummyButton);
        addDummyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemHolder.getInstance().setDummyItems();
            }
        });

        ItemHolder.getInstance().setItemsChangedListener(new ItemHolder.ItemsChangedListener() {
            @Override
            public void onChanged() {
                itemArrayAdapter.replaceAll(ItemHolder.getInstance().getItems());
            }
        });

        final EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final List<Item> filteredModelList = filter(ItemHolder.getInstance().getItems(), s.toString());
                recyclerView.scrollToPosition(0);
                itemArrayAdapter.replaceAll(filteredModelList);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}
