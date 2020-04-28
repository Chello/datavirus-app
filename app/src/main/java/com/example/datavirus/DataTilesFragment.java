package com.example.datavirus;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class DataTilesFragment extends DataSingleTileFragment {

    private static final String TOTAL = "total";
    private static final String TOTAL_DELTA = "total_delta";
    private static final String ACTIVE = "active";
    private static final String ACTIVE_DELTA = "active_delta";
    private static final String HEALED = "healed";
    private static final String HEALED_DELTA = "healed_delta";
    private static final String DEATHS = "deaths";
    private static final String DEATHS_DELTA = "deaths_delta";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.data_tiles, container, false);

        if (getArguments() != null) {
            updateTiles(v, getArguments());
        }
        return v;
    }

    public static DataTilesFragment newInstance(DPCData.DailyReport[] report) {

        Bundle args = new Bundle();
        args.putInt(TOTAL, report[report.length -1].getInt("totale_casi"));
        args.putInt(TOTAL_DELTA, report[report.length -1].getInt("totale_casi") - report[report.length-2].getInt("totale_casi"));

        args.putInt(ACTIVE, report[report.length -1].getInt("totale_positivi"));
        args.putInt(ACTIVE_DELTA, report[report.length -1].getInt("totale_positivi") - report[report.length-2].getInt("totale_positivi"));

        args.putInt(HEALED, report[report.length -1].getInt("dimessi_guariti"));
        args.putInt(HEALED_DELTA, report[report.length -1].getInt("dimessi_guariti") - report[report.length-2].getInt("dimessi_guariti"));

        args.putInt(DEATHS, report[report.length -1].getInt("deceduti"));
        args.putInt(DEATHS_DELTA, report[report.length -1].getInt("deceduti") - report[report.length-2].getInt("deceduti"));

        DataTilesFragment fragment = new DataTilesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Update tiles, using data passed.
     * It only uses the last two days for getting the current data and the delta
     * @param v the view containing objects to set
     */
    protected void updateTiles(View v, Bundle b) {
        super.updateTiles(v, b);
        Resources res = getResources();

        //Set active value of last day
        TextView active = (TextView) v.findViewById(R.id.tile_active);
        active.setText(String.format(res.getString(R.string.placeholder_decimal), b.getInt(ACTIVE)));
        //Set active delta of last day
        TextView activeDelta = (TextView) v.findViewById(R.id.tile_active_delta);
        activeDelta.setText(String.format(res.getString(R.string.placeholder_delta), b.getInt(ACTIVE_DELTA)));

        //Set healed value of last day
        TextView healed = (TextView) v.findViewById(R.id.tile_healed);
        healed.setText(String.format(res.getString(R.string.placeholder_decimal), b.getInt(HEALED)));
        //Set healed delta of last day
        TextView healedDelta = (TextView) v.findViewById(R.id.tile_healed_delta);
        healedDelta.setText(String.format(res.getString(R.string.placeholder_delta), b.getInt(HEALED_DELTA)));

        //Set deaths value of last day
        TextView deaths = (TextView) v.findViewById(R.id.tile_deaths);
        deaths.setText(String.format(res.getString(R.string.placeholder_decimal), b.getInt(DEATHS)));
        //Set deaths delta of last day
        TextView deathsDelta = (TextView) v.findViewById(R.id.tile_deaths_delta);
        deathsDelta.setText(String.format(res.getString(R.string.placeholder_delta), b.getInt(DEATHS_DELTA)));
    }

    public static class DataTilesAdapter extends RecyclerView.Adapter<DataTilesAdapter.DataTilesHolder> {
        private DPCData.DailyReport[] reports;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class DataTilesHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public CardView textView;
            public TextView qty;
            public TextView qty_delta;
            public TextView tile_head;
            public DataTilesHolder(CardView v) {
                super(v);
                this.textView = v;
                this.qty = v.findViewById(R.id.tile_qty);
                this.qty_delta = v.findViewById(R.id.tile_qty_delta);
                this.tile_head = v.findViewById(R.id.tile_head);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public DataTilesAdapter(DPCData.DailyReport[] myDataset) {
            this.reports = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public DataTilesHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
            // create a new view
            CardView v = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_tile, parent, false);
            DataTilesHolder vh = new DataTilesHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(DataTilesHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            //holder.textView.setText(mDataset[position]);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return this.reports[0].getKeys().size();
        }
    }


}
