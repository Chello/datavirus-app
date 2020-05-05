package com.example.datavirus;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

//TODO javadoc
public class DataTilesFragment extends Fragment {

    private StaticGeoTilesAdapter adapter;

    private DPCData covidData;

    protected GeographicElement geoField;

    public void setStaticGeoField(GeographicElement field, DPCData reports) {
        this.geoField = field;
        this.covidData = reports;
        populateRecycler(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.data_tiles, container, false);
        //populateRecycler(v);
        return v;
    }


    private void populateRecycler(@Nullable View v) {
        if (v == null) v = getView();
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_tiles);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        this.adapter = new StaticGeoTilesAdapter(this.covidData.getReportFromGeoElement(this.geoField));
        recyclerView.setAdapter(this.adapter);
    }

    public static DataTilesFragment newInstance() {
        return new DataTilesFragment();
    }

    /**
     * Adapter for tiles with static GeographicField
     */
    public class StaticGeoTilesAdapter extends RecyclerView.Adapter<StaticGeoTilesAdapter.DataTilesHolder> {
        private DailyReport[] reports;
        private String[] fields;
        private Integer last;
        private Integer lastLast;

        public StaticGeoTilesAdapter(DailyReport[] myDataset) {
            this.reports = myDataset;
            this.last = myDataset.length -1;
            this.lastLast = myDataset.length -2;
            Integer keysLenght = myDataset[this.last].getKeys(getResources()).size();
            ArrayList<String> fields = new ArrayList<String>();
            //For each key beautify the string
            for (int i = 0; i < keysLenght; i++) {
                String s = myDataset[this.last].getKeys(getResources()).get(i); //get the string
                if (myDataset[this.last].getInt(s) != null)
                    fields.add(s);
            }
            this.fields = fields.toArray(new String[0]);
        }

        @Override
        public DataTilesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView v = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_tile, parent, false);
            DataTilesHolder vh = new DataTilesHolder(v, (OnTileClick) getActivity());
            return vh;
        }

        @Override
        public void onBindViewHolder(DataTilesHolder holder, int position) {
            String key = this.fields[position];
            holder.setTileHeadTick(key, null);
            holder.setQty(this.reports[this.last].getInt(key));
            holder.setQtyDelta(this.reports[this.last].getInt(key) - this.reports[this.lastLast].getInt(key));
        }

        @Override
        public int getItemCount() {
            return this.fields.length;
        }

        public class DataTilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            // each data item is just a string in this case
            private CardView container;
            private TextView qty;
            private TextView qty_delta;
            private TextView tile_head;
            private CheckBox star;

            private String field;

            private OnTileClick listener;

            public void setQty(Integer qty) {
                this.qty.setText(qty.toString());
            }

            public void setQtyDelta(Integer qty_delta) {
                this.qty_delta.setText(qty_delta.toString());
            }

            public void setTileHeadTick(String tile_head, @Nullable String denominazione) {
                this.field = tile_head;
                //If this element exists
                if (StarredTileSaver.getInstance(getContext()).exists(geoField, tile_head) != -1)
                    //tick the tick
                    this.star.setChecked(true);
                //If there's not a denominazione
                if (denominazione == null)
                    this.tile_head.setText(this.adjustTitleString(tile_head));//only set header text with the tile_head
                else this.tile_head.setText(String.format((String) getResources().getText(R.string.placeholder_concat), this.adjustTitleString(tile_head), denominazione));

                //setting colors
                if (tile_head.equals(getResources().getText(R.string.denominazione_total)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorBlue, null));
                else if (tile_head.equals(getResources().getText(R.string.denominazione_active)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorOrange, null));
                else if (tile_head.equals(getResources().getText(R.string.denominazione_healed)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorGreen, null));
                else if (tile_head.equals(getResources().getText(R.string.denominazione_deaths)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorRed, null));
                else
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorGrey, null));

                //set listener
                this.star.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //star.setBackgroundResource(R.drawable.star);
                        if (isChecked)
                            StarredTileSaver.getInstance(getContext()).saveElement(geoField, field);
                        else StarredTileSaver.getInstance(getContext()).deleteElement(geoField, field);

                    }
                });
            }

            private String adjustTitleString(String s) {
                s = s.trim().replace('_', ' '); //replace bad characters
                return s.substring(0, 1).toUpperCase() + s.substring(1); //first capital letter
            }

            public DataTilesHolder(CardView v, OnTileClick listener) {
                super(v);
                this.listener = listener;
                this.container = v;
                this.qty = v.findViewById(R.id.tile_qty);
                this.qty_delta = v.findViewById(R.id.tile_qty_delta);
                this.tile_head = v.findViewById(R.id.tile_head);
                this.star = v.findViewById(R.id.recycler_tile_star);
                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                listener.onTileClick(geoField, fields[getAdapterPosition()]);
            }
        }
    }

}
