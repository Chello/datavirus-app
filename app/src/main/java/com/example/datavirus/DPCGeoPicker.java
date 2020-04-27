package com.example.datavirus;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.datavirus.OnDPCGeoListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DPCGeoPicker#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DPCGeoPicker extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DPCGeoAdapter adapter;

    private DPCData covidData;

    public DPCGeoPicker() {
        // Required empty public constructor
    }

    /**
     * This method sets the covidData for
     * @param covidData
     */
    public void setCovidData(DPCData covidData) {
        this.covidData = covidData;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment DPCDataPicker.
     */
    // TODO: Rename and change types and number of parameters
    public static DPCGeoPicker newInstance(DPCData covidData) {
        DPCGeoPicker fragment = new DPCGeoPicker();
        fragment.setCovidData(covidData);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_d_p_c_data_picker, container, false);
        this.populateRecycler(v);
        this.setUpSearchView(v);
        return v;
    }

    private void populateRecycler(View v) {
        if (v == null) v = getView();
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        this.adapter = new DPCGeoAdapter(this.covidData.getOrderedGeoElements(), (OnDPCGeoListener) getActivity());
        recyclerView.setAdapter(this.adapter);
    }

    private void setUpSearchView(View v) {
        SearchView searchView = (SearchView) v.findViewById(R.id.action_search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    class DPCGeoAdapter extends RecyclerView.Adapter<DPCGeoAdapter.DPCGeoHolder> implements Filterable {
        /**
         * All geo elements
         */
        private ArrayList<DPCData.GeographicElement> geoElements;
        /**
         * List for searching elements
         */
        private ArrayList<DPCData.GeographicElement> searchList;
        /**
         * Listener for onclick items
         */
        private OnDPCGeoListener listener;

        /**
         * Filter for search an item by SearchView
         */
        private Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<DPCData.GeographicElement> filteredList = new ArrayList<>();
                Resources res = getResources();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(searchList);
                } else {
                    //Adjusting filter pattern
                    String filterPatter = constraint.toString().toLowerCase().trim();
                    //for each item check if searched pattern corresponds
                    for (DPCData.GeographicElement elem : searchList) {
                        //Creating string with both definizione and Geographic field concat
                        String toSearchFor = elem.getDenominazione().toLowerCase().trim();
                        if (elem.getGeoField() == DPCData.GeoField.REGIONALE)
                            toSearchFor += res.getString(R.string.region).toLowerCase();
                        else if (elem.getGeoField() == DPCData.GeoField.PROVINCIALE)
                            toSearchFor += res.getString(R.string.province).toLowerCase();

                        //If this string contains what user searched...
                        if (toSearchFor.contains(filterPatter)) {
                            filteredList.add(elem); //Add to return list
                        }
                    }
                }
                FilterResults toReturn = new FilterResults();
                toReturn.values = filteredList;
                return toReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                geoElements.clear();
                geoElements.addAll((ArrayList) results.values);
                notifyDataSetChanged();
            }
        };

        public DPCGeoAdapter(ArrayList<DPCData.GeographicElement> list, OnDPCGeoListener listener) {
            this.geoElements = list;
            this.searchList = new ArrayList<>(list);
            this.listener = listener;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public DPCGeoHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            // create a new view
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate( R.layout.dialog_recycler_item, parent, false);

            DPCGeoHolder vh = new DPCGeoHolder(v, this.listener);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(DPCGeoHolder holder, int position) {
            Resources res = getResources();
            DPCData.GeographicElement current = geoElements.get(position);
            if (current.getGeoField() == DPCData.GeoField.REGIONALE) {
                holder.mainText.setText(res.getText(R.string.region));
                holder.mainText.setTextColor(Color.BLUE);
                holder.mainText.setTypeface(holder.mainText.getTypeface(), Typeface.NORMAL);
            } else if (current.getGeoField() == DPCData.GeoField.PROVINCIALE) {
                holder.mainText.setText(res.getText(R.string.province));
                holder.mainText.setTextColor(Color.GREEN);
                holder.mainText.setTypeface(holder.mainText.getTypeface(), Typeface.NORMAL);
            }
            else if (current.getGeoField() == DPCData.GeoField.NAZIONALE) {
                holder.mainText.setText(current.getDenominazione());
                holder.mainText.setTextColor(Color.BLACK);
                holder.mainText.setTypeface(holder.mainText.getTypeface(), Typeface.BOLD);
                holder.secText.setText("");
                return;
            }
            holder.secText.setText(current.getDenominazione());

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return this.geoElements.size();
        }

        @Override
        public Filter getFilter() {
            return filter;
        }

        /**
         * Holder for the click listener
         */
        public class DPCGeoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView mainText;
            public TextView secText;
            private OnDPCGeoListener listener;

            public DPCGeoHolder(LinearLayout v, OnDPCGeoListener listener) {
                super(v);
                this.listener = listener;
                v.setOnClickListener(this);
                mainText = (TextView) v.findViewById(R.id.dialog_recycler_main);
                secText = (TextView) v.findViewById(R.id.dialog_recycler_sec);
            }

            @Override
            public void onClick(View v) {
                listener.onDPCGeoClick(geoElements.get(getAdapterPosition()));
                dismiss();
            }
        }
    }
}
