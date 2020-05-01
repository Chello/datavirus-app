package com.example.datavirus;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChartElementsList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartElementsList extends Fragment {

    private ChartElementsAdapter adapter;

    public ChartElementsList() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ChartElementsList.
     */
    // TODO: Rename and change types and number of parameters
    public static ChartElementsList newInstance() {
        ChartElementsList fragment = new ChartElementsList();
        return fragment;
    }

    private void populateRecycler(View v) {
        if (v == null) v = getView();
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.chart_elements_recycler);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        this.adapter = new ChartElementsAdapter();
        recyclerView.setAdapter(this.adapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chart_elements_list, container, false);
        populateRecycler(v);
        return v;
    }

    public class ChartElementsAdapter extends RecyclerView.Adapter<ChartElementsAdapter.ChartElementsHolder> {

        private ArrayList<String> elements;

        public ChartElementsAdapter() {
            this.elements = ChartModel.getInstance().getElementsName();
        }

        @NonNull
        @Override
        public ChartElementsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chart_element_recycler_item, parent, false);
            ChartElementsHolder vh = new ChartElementsHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ChartElementsHolder holder, int position) {
            holder.setDenominazione(this.elements.get(position));
        }

        @Override
        public int getItemCount() {
            return this.elements.size();
        }

        public class ChartElementsHolder extends RecyclerView.ViewHolder {

            private TextView denominazione;
            private TextView field;

            public ChartElementsHolder(View v) {
                super(v);
                this.denominazione = v.findViewById(R.id.chart_element_recycler_denominazione);
                this.field = v.findViewById(R.id.chart_element_recycler_field);
            }

            public void setDenominazione(String denominazione) {
                this.denominazione.setText(denominazione);
            }

            public void setField(String field) {
                this.field.setText(field);
            }
        }
    }
}
