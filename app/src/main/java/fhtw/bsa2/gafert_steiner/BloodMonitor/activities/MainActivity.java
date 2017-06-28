package fhtw.bsa2.gafert_steiner.BloodMonitor.activities;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.IdentificationGenerator;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.Item;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.ItemArrayAdapter;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.ItemHolder;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.RecyclerViewMargin;
import info.hoang8f.widget.FButton;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.CHART_DIASTOLIC;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.CHART_EMOTIONS;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.CHART_HEART_RATE;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.CHART_SYSTOLIC;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_VERY_HAPPY;
import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.FEELING_VERY_SAD;

public class MainActivity extends AppCompatActivity {

    private static final Comparator<Item> DATE_COMPARATOR = new Comparator<Item>() {
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
    // Party Mode
    Handler h = new Handler();
    int delay = 100;
    Runnable runnable;
    boolean partyActive = false;
    int partyCounter = 0;
    MediaPlayer mediaPlayer;
    private Menu menu;

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

        // Setup Singletons
        FileIO.getInstance(getApplicationContext());
        IdentificationGenerator.getInstance(getApplicationContext());
        ItemHolder.getInstance(getApplicationContext());
        FileIO.getInstance().sync(true);

        // Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Chart");

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final ItemArrayAdapter itemArrayAdapter = new ItemArrayAdapter(this, R.layout.example_recycler_view_element, DATE_COMPARATOR);
        recyclerView.setAdapter(itemArrayAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerViewMargin(20, 1));  // Margin for the list
        itemArrayAdapter.add(ItemHolder.getInstance().getItems());  // Fill the list
        setupGraph(ItemHolder.getInstance().getItems(), CHART_EMOTIONS);            // Fill the chart

        FButton addButton = (FButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addActivity = new Intent(MainActivity.this, AddActivity.class);
                startActivity(addActivity);
            }
        });

        findViewById(R.id.heartRateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupGraph(ItemHolder.getInstance().getItems(), CHART_HEART_RATE);

            }
        });
        findViewById(R.id.diastolicButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupGraph(ItemHolder.getInstance().getItems(), CHART_DIASTOLIC);

            }
        });
        findViewById(R.id.systolicButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupGraph(ItemHolder.getInstance().getItems(), CHART_SYSTOLIC);

            }
        });
        findViewById(R.id.emotionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupGraph(ItemHolder.getInstance().getItems(), CHART_EMOTIONS);

            }
        });

        // Call everything when list updates
        ItemHolder.getInstance().setItemsChangedListener(new ItemHolder.ItemsChangedListener() {
            @Override
            public void onChanged() {
                itemArrayAdapter.replaceAll(ItemHolder.getInstance().getItems());
                recyclerView.scrollToPosition(0);
                setupGraph(ItemHolder.getInstance().getItems(), CHART_EMOTIONS);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                ItemHolder.getInstance().deleteLocalFiles();
                break;
            case R.id.menu_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.menu_sync:
                FileIO.getInstance().sync(true);
                break;
            case R.id.menu_party:
                MenuItem menuParty = menu.findItem(R.id.menu_party);
                if (!partyActive) {
                    menuParty.setTitle("Turn off party Mode");
                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.party);
                    mediaPlayer.start();
                    h.postDelayed(new Runnable() {
                        public void run() {
                            partyActive = true;
                            String partyMode;
                            switch (partyCounter) {
                                case 0:
                                    partyMode = CHART_DIASTOLIC;
                                    break;
                                case 1:
                                    partyMode = CHART_EMOTIONS;
                                    break;
                                case 2:
                                    partyMode = CHART_SYSTOLIC;
                                    break;
                                case 3:
                                    partyMode = CHART_HEART_RATE;
                                    break;
                                default:
                                    partyMode = CHART_SYSTOLIC;
                                    partyCounter = -1;
                                    break;
                            }
                            partyCounter++;
                            setupGraph(ItemHolder.getInstance().getItems(), partyMode);
                            runnable = this;
                            h.postDelayed(runnable, delay);
                        }
                    }, delay);
                } else {
                    mediaPlayer.stop();
                    menuParty.setTitle("Party Mode");
                    partyActive = false;
                    h.removeCallbacks(runnable);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupGraph(final List<Item> _data, String show) {

        LineChart chart = (LineChart) findViewById(R.id.chart);
        chart.clear();

        int crop = 20;
        ArrayList<Item> data = new ArrayList<>();
        if (_data.size() > crop) {
            data = new ArrayList<>(_data.subList(_data.size() - crop, _data.size()));
        } else {
            data.addAll(_data);
        }

        // Colors for styling
        int[] colors = new int[5];
        colors[0] = ContextCompat.getColor(this, R.color.colorAccent);
        colors[1] = ContextCompat.getColor(this, R.color.colorPrimary);
        colors[2] = ContextCompat.getColor(this, R.color.blue);
        colors[3] = ContextCompat.getColor(this, R.color.red);
        colors[4] = ContextCompat.getColor(this, R.color.green);

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

        // Range of X axis
        chart.setVisibleXRangeMaximum(10);
        chart.setVisibleXRangeMinimum(5);

        // Style Y Axis
        chart.getAxisRight().setTextColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);

        // Disable all Y Axis except 1
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setDrawLabels(false);
        chart.setDescription(null);                                         // Remove Description

        // Chart interactive
        chart.setDragEnabled(true);                                         // Chart is dragable
        chart.setScaleXEnabled(true);                                       // Only scaleable on X

        // Always draw Y as high as max values + offset
        if (show.equals(CHART_EMOTIONS)) {
            chart.getAxisLeft().setAxisMaximum(FEELING_VERY_HAPPY + 5);
            chart.getAxisLeft().setAxisMinimum(FEELING_VERY_SAD - 5);
            chart.setScaleYEnabled(false);
        } else {
            chart.getAxisLeft().resetAxisMaximum();
            chart.getAxisLeft().setAxisMinimum(-10);
            chart.setScaleYEnabled(true);
        }

        // Custom marker to highlight entries
        ChartMarker elevationMarker = new ChartMarker(this);       // Make a custom marker
        elevationMarker.setOffset(
                -(elevationMarker.getWidth() / 2),
                -(elevationMarker.getHeight() / 2));                        // Center the marker layout
        chart.setMarker(elevationMarker);                                   // Set the new marker to the chart
        chart.setViewPortOffsets(0f, 20f, 0f, 80f);

        // Add a marker hightight listener
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(MainActivity.this, "Selected a value", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
            }
        });

        //
        //      FILL THE CHART WITH VALUES
        //

        if (!data.isEmpty()) {
            // Entry Array
            List<Entry> entryList = new ArrayList<>();
            int count = 0;

            switch (show) {
                case CHART_EMOTIONS:
                    for (Item entry : data) {
                        entryList.add(new Entry(count, entry.getMood()));
                        count++;
                    }
                    break;
                case CHART_DIASTOLIC:
                    for (Item entry : data) {
                        entryList.add(new Entry(count, entry.getDiastolicPressure()));
                        count++;
                    }
                    break;
                case CHART_SYSTOLIC:
                    for (Item entry : data) {
                        entryList.add(new Entry(count, entry.getSystolicPressure()));
                        count++;
                    }
                    break;
                case CHART_HEART_RATE:
                    for (Item entry : data) {
                        entryList.add(new Entry(count, entry.getHeartRate()));
                        count++;
                    }
                    break;
            }

            // Das die werte nicht auf der seite kleben
            chart.getXAxis().setAxisMinimum(-1.2f);
            chart.getXAxis().setAxisMaximum(count + 0.2f);

            // Make a new data set with entries
            LineDataSet entryDataSet = new LineDataSet(entryList, "");

            // Style the dataSet
            entryDataSet.setCircleColorHole(colors[1]);
            entryDataSet.setCircleRadius(7);
            entryDataSet.setCircleHoleRadius(5);
            entryDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);    // Makes it line smooth
            entryDataSet.setHighlightEnabled(true);                 // Allow highlighting for DataSet
            entryDataSet.setDrawHighlightIndicators(false);         // Draw point on which someone clicked
            entryDataSet.setLineWidth(2f);

            List<ILineDataSet> dataSet = new ArrayList<>();
            dataSet.add(entryDataSet);                              // All lines are added to a dataSet

            LineData lineData = new LineData(dataSet);
            switch (show) {
                case CHART_EMOTIONS:
                    lineData.setDrawValues(false);
                    lineData.setValueTextColor(colors[0]);
                    entryDataSet.setColor(colors[0]);
                    entryDataSet.setCircleColor(colors[0]);
                    break;
                case CHART_DIASTOLIC:
                    lineData.setDrawValues(true);
                    lineData.setValueTextColor(colors[2]);
                    entryDataSet.setColor(colors[2]);
                    entryDataSet.setCircleColor(colors[2]);
                    break;
                case CHART_SYSTOLIC:
                    lineData.setDrawValues(true);
                    lineData.setValueTextColor(colors[4]);
                    entryDataSet.setColor(colors[4]);
                    entryDataSet.setCircleColor(colors[4]);
                    break;
                case CHART_HEART_RATE:
                    lineData.setDrawValues(true);
                    lineData.setValueTextColor(colors[3]);
                    entryDataSet.setColor(colors[3]);
                    entryDataSet.setCircleColor(colors[3]);
                    break;
            }

            chart.setData(lineData);
            chart.moveViewTo(chart.getData().getEntryCount(), 0, YAxis.AxisDependency.RIGHT);   // Set viewport to last entries
        } else {
            chart.clear();
        }

        chart.invalidate(); // Draw chart
    }

}
