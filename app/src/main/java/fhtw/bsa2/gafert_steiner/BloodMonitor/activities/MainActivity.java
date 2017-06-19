package fhtw.bsa2.gafert_steiner.BloodMonitor.activities;

import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import fhtw.bsa2.gafert_steiner.BloodMonitor.FileIO;
import fhtw.bsa2.gafert_steiner.BloodMonitor.ItemHolder;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.Item;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.ItemArrayAdapter;
import fhtw.bsa2.gafert_steiner.BloodMonitor.R;
import fhtw.bsa2.gafert_steiner.BloodMonitor.RecyclerViewMargin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Read and write access via context
        FileIO.getInstance(getApplicationContext());

        ItemHolder.getInstance().getItems();
        if (ItemHolder.getInstance().getItems().isEmpty()){
            ItemHolder.getInstance().setDummyItems();
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        ItemArrayAdapter itemArrayAdapter = new ItemArrayAdapter(R.layout.example_recycler_view_element, ItemHolder.getInstance().getItems());
        recyclerView.setAdapter(itemArrayAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecyclerViewMargin(20, 1));

        View bottomSheet = findViewById(R.id.bottom_sheet1);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setPeekHeight(160);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        Button button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

    }
}
