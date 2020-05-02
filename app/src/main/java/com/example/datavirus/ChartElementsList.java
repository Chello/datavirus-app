package com.example.datavirus;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager layoutManager = linearLayoutManager;
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

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
        private ArrayList<Integer> colors;
        //TODO devi fare un;interfaccia su chartactivity per triggerare il refresh
        private Context context;

        public ChartElementsAdapter() {
            this.elements = ChartModel.getInstance().getElementsName();
            this.colors = ChartModel.getInstance().getElementsColor();
        }

        @NonNull
        @Override
        public ChartElementsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chart_element_recycler_item, parent, false);
            ChartElementsHolder vh = new ChartElementsHolder(v, (OnTickChange) getActivity());
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ChartElementsHolder holder, int position) {
            holder.setDenominazione(this.elements.get(position));
            holder.setColor(this.colors.get(position));
            holder.setPos(position);
        }

        @Override
        public int getItemCount() {
            return this.elements.size();
        }

        public class ChartElementsHolder extends RecyclerView.ViewHolder {

            private TextView denominazione;
            private CheckBox visible;
            private Integer pos;
            private OnTickChange listener;
            private CardView color;

            public ChartElementsHolder(View v, final OnTickChange listener) {
                super(v);
                this.denominazione = v.findViewById(R.id.chart_element_recycler_denominazione);
                this.visible = v.findViewById(R.id.chart_element_recycler_visible_checkbox);
                this.setChecked(true);
                this.listener = listener;
                this.color = v.findViewById(R.id.chart_element_recycler_color);
                this.visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ChartModel cm = ChartModel.getInstance();
                        cm.setElementVisible(pos, isChecked);
                        listener.onTickChange();
                    }
                });
            }

            public void setDenominazione(String denominazione) {
                this.denominazione.setText(denominazione);
            }

            public void setChecked(Boolean check) {
                this.visible.setChecked(check);
            }

            public void setColor(Integer color) {
                this.color.setBackgroundColor(color);
            }

            public void setPos(Integer pos) {
                this.pos = pos;
            }
        }
    }
}
