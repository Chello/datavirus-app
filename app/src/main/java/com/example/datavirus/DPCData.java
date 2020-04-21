package com.example.datavirus;

import android.util.Log;

import com.google.gson.Gson;

/**
 * Objects from this class will contain data from Nazionale, Regionale and Provinciale sections.
 * These data are structured as the italian Dipartimento della Protezione Civile JSONs are structured
 */

public class DPCData {

    private Gson gson;
    private AndamentoNazionale nazionale;

    public DPCData(String nazionaleJson) {
        this.gson = new Gson();

        this.nazionale = gson.fromJson(nazionaleJson, AndamentoNazionale.class);
        Log.d("DPCParse", "Here we are");
    }

    public class AndamentoNazionale {
        private Integer ricoverati_con_sintomi;
        private Integer terapia_intensiva;
        private Integer isolamento_domiciliare;
        private Integer totale_positivi;

        public AndamentoNazionale(Integer ricoverati_con_sintomi, Integer terapia_intensiva, Integer isolamento_domiciliare, Integer totale_positivi) {
            this.ricoverati_con_sintomi = ricoverati_con_sintomi;
            this.terapia_intensiva = terapia_intensiva;
            this.isolamento_domiciliare = isolamento_domiciliare;
            this.totale_positivi = totale_positivi;
        }
    }

}
