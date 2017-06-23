package fhtw.bsa2.gafert_steiner.BloodMonitor.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fhtw.bsa2.gafert_steiner.BloodMonitor.FileIO;
import fhtw.bsa2.gafert_steiner.BloodMonitor.R;
import fhtw.bsa2.gafert_steiner.BloodMonitor.chart.ChartMarker;
import fhtw.bsa2.gafert_steiner.BloodMonitor.chart.DateFormatter;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.Item;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.ItemArrayAdapter;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.ItemHolder;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.RecyclerViewMargin;
import info.hoang8f.widget.FButton;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.FEELING_VERY_HAPPY;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.GlobalShit.FEELING_VERY_SAD;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Chart");

        // Read and write access via context
        FileIO.getInstance(getApplicationContext());

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final ItemArrayAdapter itemArrayAdapter = new ItemArrayAdapter(this, R.layout.example_recycler_view_element, DATE_COMPARATOR);
        recyclerView.setAdapter(itemArrayAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerViewMargin(20, 1));  // Margin for the list

        itemArrayAdapter.add(ItemHolder.getInstance().getItems());  // Fill the list
        setupGraph(ItemHolder.getInstance().getItems());            // Fill the chart

        /*View bottomSheet = findViewById(R.id.bottom_sheet1);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setPeekHeight(160);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);*/

        /*Button chart = (Button) findViewById(R.id.button);
        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });*/

        FButton addButton = (FButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addActivity = new Intent(MainActivity.this, AddActivity.class);
                startActivity(addActivity);
            }
        });

        // Call everything when list updates
        ItemHolder.getInstance().setItemsChangedListener(new ItemHolder.ItemsChangedListener() {
            @Override
            public void onChanged() {
                itemArrayAdapter.replaceAll(ItemHolder.getInstance().getItems());
                recyclerView.scrollToPosition(0);
                setupGraph(ItemHolder.getInstance().getItems());
            }
        });

        final EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                appBar.setExpanded(false);
                return false;
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.menu_add_dummy:
                ItemHolder.getInstance().setDummyItems();
                break;
            case R.id.menu_clear:
                ItemHolder.getInstance().deleteLocalFiles();
                break;
            case R.id.menu_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupGraph(final List<Item> _data) {

        LineChart chart = (LineChart) findViewById(R.id.chart);
        int crop = 7;
        ArrayList<Item> data = new ArrayList<>();
        if (_data.size() > crop) {
            data = new ArrayList<>(_data.subList(_data.size() - crop, _data.size()));
        } else {
            data.addAll(_data);
        }

        if (!data.isEmpty()) {
            // Entry Array
            List<Entry> happinessEntries = new ArrayList<>();
            int count = 0;
            for (Item entry : data) {
                happinessEntries.add(new Entry(count, entry.getMood()));
                count++;
            }

            // Das die werte nicht auf der seite kleben
            chart.getXAxis().setAxisMinimum(-1.2f);
            chart.getXAxis().setAxisMaximum(count + 0.2f);

            // Colors for styling
            int[] colors = new int[3];
            colors[0] = ContextCompat.getColor(this, R.color.colorAccent);
            colors[1] = ContextCompat.getColor(this, R.color.colorPrimary);

            // Make a new data set with entries
            LineDataSet happinessDateSet = new LineDataSet(happinessEntries, "Happiness");

            // Style the dataSet
            happinessDateSet.setColor(colors[0]);                       // Format Line
            happinessDateSet.setCircleColor(colors[0]);
            happinessDateSet.setCircleColorHole(colors[1]);
            happinessDateSet.setCircleRadius(7);
            happinessDateSet.setCircleHoleRadius(5);
            happinessDateSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);    // Makes it line smooth
            happinessDateSet.setHighlightEnabled(true);                 // Allow highlighting for DataSet
            happinessDateSet.setDrawHighlightIndicators(false);         // Draw point on which someone clicked
            happinessDateSet.setLineWidth(2.5f);

            List<ILineDataSet> dataSet = new ArrayList<ILineDataSet>();
            dataSet.add(happinessDateSet);                              // All lines are added to a dataSet

            LineData lineData = new LineData(dataSet);
            lineData.setDrawValues(false);                              // Disable point labeling

            chart.setData(lineData);
            chart.moveViewTo(chart.getData().getEntryCount(), 0, YAxis.AxisDependency.RIGHT);   // Set viewport to last entries
        } else {
            chart.clear();
            chart.invalidate();
        }


        // Add the lines to the chart
        chart.getLegend().setEnabled(false);                                // Disables Legend

        // Style X Axis
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getXAxis().setValueFormatter(new DateFormatter(data));        // Format x values to see day
        chart.getXAxis().setGranularity(1);                                 // Just whole numbers are represented
        chart.getXAxis().setLabelRotationAngle(30);
        chart.getXAxis().setLabelCount(10);                                 // Max labels in the chart
        chart.getXAxis().setTextSize(8);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);           // X Values are at the bottom of the chart

        // Range of
        chart.setVisibleXRangeMaximum(10);
        chart.setVisibleXRangeMinimum(5);

        // Style Y Axis
        chart.getAxisRight().setTextColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);

        // Always draw Y as high as max values + offset
        chart.getAxisLeft().setAxisMaximum(FEELING_VERY_HAPPY + 5);
        chart.getAxisLeft().setAxisMinimum(FEELING_VERY_SAD - 5);

        // Disable all Y Axis except 1
        chart.getAxisLeft().setDrawAxisLine(false);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setEnabled(false);

        chart.setDescription(null);                                         // Remove Description

        // Chart interactive
        chart.setDragEnabled(true);                                         // Chart is dragable
        chart.setScaleXEnabled(true);                                       // Only scaleable on X
        chart.setScaleYEnabled(false);

        // Custom marker to hightlight entries
        ChartMarker elevationMarker = new ChartMarker(this);       // Make a custom marker
        elevationMarker.setOffset(
                -(elevationMarker.getWidth() / 2),
                -(elevationMarker.getHeight() / 2));                        // Center the marker layout
        chart.setMarker(elevationMarker);                                   // Set the new marker to the chart
        chart.setViewPortOffsets(0f, 0f, 0f, 120f);

        // Add a marker hightlight listener
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(MainActivity.this, "Selected a value", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
            }
        });

        chart.invalidate(); // Draw chart
    }

}
