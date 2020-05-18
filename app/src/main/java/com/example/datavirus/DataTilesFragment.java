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

/**
 * Fragment that shows tiles about various FieldGeographicElement obtained via StarredTileSaver
 * Or for a static Geographic Element, showing all fields contained in that reports
 */
public class DataTilesFragment extends Fragment {

    /**
     * Adapter for the RecyclerView
     */
    private TilesAdapter adapter;

    /**
     * Function for showing a static GeographicElement for the tiles
     * @param geographicElement the geographic element to set
     */
    public void setStaticGeoField(GeographicElement geographicElement) {
        populateRecycler(geographicElement);
    }

    /**
     * Function for showing the saved tiles
     */
    public void setSavedTilesReport() {
        this.populateRecycler(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.data_tiles, container, false);
    }

    /**
     * Populates the RecylerView of tiles.
     * It sets up fragment with global GeographicElement if set.
     * If geographicElement is set null, it will shown all saved tiles
     * @param geographicElement tiles showing data from this geographicElement
     */
    private void populateRecycler(@Nullable GeographicElement geographicElement) {
        View v = getView();
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_tiles);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        if (geographicElement != null)
            this.adapter = new TilesAdapter(DataParser.getDPCDataInstance(), geographicElement);
        else this.adapter = new TilesAdapter(DataParser.getDPCDataInstance());

        recyclerView.setAdapter(this.adapter);
    }

    /**
     * Returns a new instance of DataTilesFragment
     * @return a new instance of DataTilesFragment
     */
    public static DataTilesFragment newInstance() {
        return new DataTilesFragment();
    }

    /**
     * Adapter for RecyclerView tiles
     */
    public class TilesAdapter extends RecyclerView.Adapter<TilesAdapter.DataTilesHolder> {

        private ArrayList<Integer> last;
        private ArrayList<Integer> lastLast;
        private ArrayList<FieldGeographicElement> elements;
        private Boolean staticGeoField;

        /**
         * Constructor for creating a tile list of saved tiles
         * @param covidData DPCData report handler
         */
        public TilesAdapter(DPCData covidData) {
            ManageStarredTiles saver = ManageStarredTiles.getInstance(getContext());

            this.staticGeoField = false;
            this.last = new ArrayList<>();
            this.lastLast = new ArrayList<>();
            this.elements = saver.getSavedTiles();

            for (FieldGeographicElement fge : this.elements) {
                Integer last = covidData.getValuesFromGeoField(fge).get(covidData.getReportsNumber() -1);
                if (last != null) {
                    Integer lastLast = last - covidData.getValuesFromGeoField(fge).get(covidData.getReportsNumber() -2);
                    this.last.add(last);
                    this.lastLast.add(lastLast);
                }
            }

        }
        /**
         * Constructor for creating a tile list from a global static GeographicElement
         * @param covidData DPCData report handler
         * @param global the global GeographicElement for obtain reports
         */
        public TilesAdapter(DPCData covidData, GeographicElement global) {
            DailyReport[] reports = covidData.getReportFromGeoElement(global);

            this.staticGeoField = true;
            this.elements = new ArrayList<>();
            this.last = new ArrayList<>();
            this.lastLast = new ArrayList<>();

            ArrayList<String> keys = reports[reports.length - 1].getKeys(getResources());

            //For each key beautify the string
            for (int i = 0; i < keys.size(); i++) {
                FieldGeographicElement fge = new FieldGeographicElement(global, keys.get(i));
                Integer last = covidData.getValuesFromGeoField(fge).get(reports.length -1);
                //String s = this.elements.add();
                if (last != null) {
                    Integer lastLast = last - covidData.getValuesFromGeoField(fge).get(reports.length -2);
                    this.elements.add(fge);
                    this.last.add(last);
                    this.lastLast.add(lastLast);
                }
            }
            //this.fields = fields.toArray(new String[0]);
        }

        /**
         * Creates a ViewHolder
         * @param parent the parent ViewGroup
         * @param viewType the viewType
         * @return a new DataTilesHolder instance
         */
        @Override
        public DataTilesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView v = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_tile, parent, false);
            DataTilesHolder vh = new DataTilesHolder(v, (OnTileClick) getActivity());
            return vh;
        }

        /**
         * Binds an holder with data
         * @param holder the data holder
         * @param position the position of the element in the RecyclerView
         */
        @Override
        public void onBindViewHolder(DataTilesHolder holder, int position) {
            holder.setTile(this.elements.get(position), this.staticGeoField);
            holder.setQty(this.last.get(position));
            holder.setQtyDelta(this.lastLast.get(position));
        }

        /**
         * Returns the count of the item in the RecyclerView
         * @return the count of the item in the RecyclerView
         */
        @Override
        public int getItemCount() {
            return this.last.size();
        }

        /**
         * Data holder for tiles in the RecyclerView
         */
        public class DataTilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private CardView container;
            private TextView qty;
            private TextView qty_delta;
            private TextView tile_head;
            private CheckBox star;

            private FieldGeographicElement fieldGeographicElement;

            private OnTileClick listener;

            /**
             * Sets the quantity for the tile
             * @param qty the quantity
             */
            public void setQty(Integer qty) {
                this.qty.setText(qty.toString());
            }

            /**
             * Sets the delta quantity for the tile
             * @param qty_delta the delta quantity
             */
            public void setQtyDelta(Integer qty_delta) {
                this.qty_delta.setText(String.format(getResources().getString(R.string.placeholder_delta), qty_delta));
            }

            /**
             * Sets tile data like color, position and title
             * @param fieldGeographicElement the element of tile container
             * @param specifyRegion if region has to be added to title of this tile
             */
            public void setTile(final FieldGeographicElement fieldGeographicElement, Boolean specifyRegion) {
                this.fieldGeographicElement = fieldGeographicElement;
                //If this element exists
                if (ManageStarredTiles.getInstance(getContext()).getPos(fieldGeographicElement) != -1)
                    //tick the tick
                    this.star.setChecked(true);
                //If there's not a denominazione
                if (specifyRegion)
                    this.tile_head.setText(DPCData.trimTitleString(fieldGeographicElement.getField()));//only set header text with the tile_head
                //otherwise set both denominazione and field
                else this.tile_head.setText(String.format((String) getResources().getText(R.string.placeholder_concat),
                        DPCData.trimTitleString(fieldGeographicElement.getField()),
                        fieldGeographicElement.getDenominazione()));

                //setting colors
                if (fieldGeographicElement.getField().equals(getResources().getText(R.string.denominazione_total)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorBlue, null));
                else if (fieldGeographicElement.getField().equals(getResources().getText(R.string.denominazione_active)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorOrange, null));
                else if (fieldGeographicElement.getField().equals(getResources().getText(R.string.denominazione_healed)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorGreen, null));
                else if (fieldGeographicElement.getField().equals(getResources().getText(R.string.denominazione_deaths)))
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorRed, null));
                else
                    this.container.setBackgroundColor(getResources().getColor(R.color.colorGrey, null));

                //set listener
                this.star.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //star.setBackgroundResource(R.drawable.star);
                        if (isChecked)
                            ManageStarredTiles.getInstance(getContext()).saveElement(fieldGeographicElement);
                        else ManageStarredTiles.getInstance(getContext()).deleteElement(fieldGeographicElement);

                    }
                });
            }

            /**
             * Constructor of DataTilesHolder
             * @param v the cardview wrapper
             * @param listener the listener when tile is clicked
             */
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
                listener.onTileClick(fieldGeographicElement);
            }
        }
    }

}
