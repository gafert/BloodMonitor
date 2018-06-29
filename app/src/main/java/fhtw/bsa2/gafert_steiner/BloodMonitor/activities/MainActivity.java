package fhtw.bsa2.gafert_steiner.BloodMonitor.activities;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import fhtw.bsa2.gafert_steiner.BloodMonitor.FileIO;
import fhtw.bsa2.gafert_steiner.BloodMonitor.R;
import fhtw.bsa2.gafert_steiner.BloodMonitor.chart.DateFormatter;
import fhtw.bsa2.gafert_steiner.BloodMonitor.items.*;

import static fhtw.bsa2.gafert_steiner.BloodMonitor.Constants.*;

public class MainActivity extends AppCompatActivity {
    private final int delay = 100;
    // Party Mode vars
    private Handler h = new Handler();
    private Runnable runnable;
    private boolean partyActive = false;
    private int partyCounter = 0;

    private AppBarLayout appBar;
    private Menu menu;
    private LinearLayout emotionImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Singletons
        FileIO.getInstance(getApplicationContext());
        IDProvider.getInstance(getApplicationContext());
        ItemHolder.getInstance(getApplicationContext());

        //FileIO.getInstance().sync(true);

        // Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBar = (AppBarLayout) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

        emotionImages = (LinearLayout) findViewById(R.id.emotionImages);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final ItemArrayAdapter itemArrayAdapter = new ItemArrayAdapter(this, R.layout.example_recycler_view_element, DATE_COMPARATOR);
        recyclerView.setAdapter(itemArrayAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerViewMargin(20, 1));  // Margin for the list
        itemArrayAdapter.add(ItemHolder.getInstance().getItems());  // Fill the list
        setupGraph(ItemHolder.getInstance().getItems(), CHART_EMOTIONS);            // Fill the chart

        Button addButton = (Button) findViewById(R.id.addButton);
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
                final List<Item> filteredModelList = searchFilter(ItemHolder.getInstance().getItems(), s.toString());
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
                partyMode();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupGraph(final List<Item> _data, String show) {

        LineChart chart = (LineChart) findViewById(R.id.chart);
        chart.clear();

        // Crop the list to 20 entries
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
        chart.getXAxis().setLabelRotationAngle(45);
        chart.getXAxis().setLabelCount(10);                                 // Max labels in the chart
        chart.getXAxis().setTextSize(8);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);           // X Values are at the bottom of the chart

        // Range of X axis
        // Most elements and least elements in the view
        chart.setVisibleXRangeMaximum(10);
        chart.setVisibleXRangeMinimum(5);

        chart.getAxisRight().setTextColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setDrawLabels(false);
        chart.setDescription(null);

        // Chart interactive
        chart.setDragEnabled(true);
        chart.setScaleXEnabled(true);
        chart.setScaleYEnabled(false);

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            chart.setViewPortOffsets(0f, 5f, 0f, 75f);
            chart.getXAxis().setYOffset(10);
        } else {
            chart.setViewPortOffsets(0f, 5f, 0f, 130f);
            chart.getXAxis().setYOffset(10);
        }

        // Always draw Y as high as max values + offset
        if (show.equals(CHART_EMOTIONS)) {
            emotionImages.setVisibility(View.VISIBLE);
            chart.getAxisLeft().setAxisMaximum(FEELING_VERY_HAPPY + 5);
            chart.getAxisLeft().setAxisMinimum(FEELING_VERY_SAD - 5);
        } else {
            emotionImages.setVisibility(View.INVISIBLE);
            chart.getAxisLeft().resetAxisMaximum();
            chart.getAxisLeft().setAxisMinimum(-15);
        }

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
            entryDataSet.setHighlightEnabled(false);                 // Allow highlighting for DataSet
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

    /**
     * Fun gag, switches between the different chart views and plays music
     * Can be turned on and of with this function
     */
    private void partyMode() {
        MenuItem menuParty = menu.findItem(R.id.menu_party);
        if (!partyActive) {
            menuParty.setTitle("Turn off Party Mode");
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
            menuParty.setTitle("Party Mode");
            partyActive = false;
            h.removeCallbacks(runnable);
        }
    }

}
