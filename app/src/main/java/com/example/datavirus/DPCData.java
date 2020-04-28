package com.example.datavirus;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Objects from this class will contain data from Nazionale, Regionale and Provinciale sections.
 * These data are structured as the italian Dipartimento della Protezione Civile JSONs are structured
 */

public class DPCData  {
    enum GeoField {
        NAZIONALE, REGIONALE, PROVINCIALE
    }

    /**
     * List of keys to exclude from a DailyReport but in a Json exists
     */
    private static List<String> excludeList = Arrays.asList("note_en", "note_it", "lat","long", "stato", "sigla_provincia",
            "denominazione_provincia", "codice_provincia",
            "denominazione_regione", "codice_regione", "In fase di definizione/aggiornamento");

    private Gson gson;
    //Reports for territorial data
    private DailyReport[] nazionale;
    private HashMap<String, ArrayList<DailyReport>> regionale;
    private HashMap<String, ArrayList<DailyReport>> provinciale;

    public ArrayList<String> getNazionaleKeyList() {
        return nazionaleKeyList;
    }
    public ArrayList<String> getRegionaleKeyList() {
        return regionaleKeyList;
    }
    public ArrayList<String> getProvincialeKeyList() {
        return provincialeKeyList;
    }

    private ArrayList<String> nazionaleKeyList;
    private ArrayList<String> regionaleKeyList;
    private ArrayList<String> provincialeKeyList;

    /**
     * Returns the list of Regioni
     * @return
     */
    public ArrayList<String> getRegioniList() {
        Set s = this.regionale.keySet();
        ArrayList<String> arr = new ArrayList<String>(s);
        Collections.sort(arr);
        return arr;
    }

    /**
     * Returns ordered array of GeographicElements.
     * This array has the following skeleton:
     * The first element is about national datas
     * then follows regions (in alphabetical order) and for each region, follows all provinces that belongs to that region
     * @return the array of GraphicElements, sorted
     */
    public ArrayList<GeographicElement> getOrderedGeoElements() {
        ArrayList<GeographicElement> toReturn = new ArrayList<GeographicElement>();
        toReturn.add(new GeographicElement(GeoField.NAZIONALE));
        for (String regione : this.getRegioniList()) {
            toReturn.add(new GeographicElement(regione, GeoField.REGIONALE));
            toReturn.addAll(this.getProvinciaFromRegione(regione));
        }

        return toReturn;
    }

    /**
     * Returns list of provinces that belongs the specified region
     * @param regione the specified region
     * @return array of GeographicElement, only provinces
     */
    private ArrayList<GeographicElement> getProvinciaFromRegione(String regione) {
        ArrayList<GeographicElement> toReturn = new ArrayList<GeographicElement>();
        for (Map.Entry<String, ArrayList<DailyReport>> pair: this.provinciale.entrySet()) {
            if (pair.getValue().get(0).getString("denominazione_regione").equals(regione) &&
                !excludeList.contains(pair.getKey())) {
                toReturn.add(new GeographicElement(pair.getKey(), GeoField.PROVINCIALE));
            }
        }
        return toReturn;

    }

    /**
     * Returns the list of Province
     * @return
     */
    public ArrayList<String> getProvinceList() {
        Set<String> s = this.provinciale.keySet();
        return new ArrayList<String>(s);
    }

    /**
     * Returns the Nazionale report
     * @return the Nazionale report
     */
    public DailyReport[] getNazionale() {
        return this.nazionale;
    }

    public DailyReport[] getReportFromGeoData(GeographicElement element) {
        if (element.getGeoField() == GeoField.NAZIONALE)
            return this.nazionale;
        if (element.getGeoField() == GeoField.REGIONALE)
            return this.getRegionaleReport(element.getDenominazione());
        if (element.getGeoField() == GeoField.PROVINCIALE)
            return this.getProvincialeReport(element.getDenominazione());
        return null;
    }

    public Calendar getDate() {
        try {
            //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd`T`hh:mm:ss");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Integer last = this.nazionale.length -1;
            String dateRaw = this.nazionale[last].getString("data");
            //return format.parse(dateRaw);
            Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
            calendar.setTime(dateFormat.parse(dateRaw));   // assigns calendar to given date
            return calendar;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Returns the Regionale report, by giving it's regione
     * @param regione the regione to obtain
     * @return the Regiona report
     */
    public DailyReport[] getRegionaleReport(String regione) {
        DailyReport[] arr = new DailyReport[this.regionale.size()];
        if (this.regionale.containsKey(regione))
            return this.regionale.get(regione).toArray(arr);
        return null;
    }

    /**
     * Returns the Provinciale report, by giving it's provincia
     * @param provincia the provincia to obtain
     * @return the Provincia report
     */
    public DailyReport[] getProvincialeReport(String provincia) {
        if (this.provinciale.containsKey(provincia)) {
            ArrayList<DailyReport> arr = this.provinciale.get(provincia);
            return arr.toArray(new DailyReport[arr.size()]);
        }
        return null;
    }

    public DPCData(String nazionaleJson, String regionaleJson, String provincialeJson) {
        this.gson = new Gson();

        JsonParser parser = new JsonParser();

        this.nazionale = DailyReport.reportsBuilder(parser.parse(nazionaleJson).getAsJsonArray());
        DailyReport[] regionale = DailyReport.reportsBuilder(parser.parse(regionaleJson).getAsJsonArray());
        DailyReport[] provinciale = DailyReport.reportsBuilder(parser.parse(provincialeJson).getAsJsonArray());

        this.regionale = createKeyValueList(regionale);
        this.provinciale = createKeyValueList(provinciale);

        this.nazionaleKeyList = this.getKeysFromJsonArray(nazionaleJson);
        this.regionaleKeyList = this.getKeysFromJsonArray(regionaleJson);
        this.provincialeKeyList = this.getKeysFromJsonArray(provincialeJson);

        Log.d("DPCParse", "Data parsed");
    }

    /**
     * https://stackoverflow.com/questions/31094305/java-gson-getting-the-list-of-all-keys-under-a-jsonobject
     * Retrieves the key list of the first element of the array of object passed via String
     *
     * @param json Json, array of objects
     * @return the key list of the first array
     */

    private ArrayList<String> getKeysFromJsonArray(String json) {
        ArrayList<String> toRet = new ArrayList<String>();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json);
        JsonArray arr = element.getAsJsonArray();
        JsonObject obj = arr.get(0).getAsJsonObject(); //since you know it's a JsonObject
        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();//will return members of your object
        for (Map.Entry<String, JsonElement> entry: entries) {
            String current = entry.getKey();
            if (this.excludeList.contains(current))
                continue;
            toRet.add(current);
            //Log.d("Has json parsed: " , entry.getKey() );
        }
        return toRet;
    }

    /**
     * Creates the key-value Hash Table, useful for organizing data into field of interest.
     * The field used is DPCData's 'denominazione', obtained via getDenominazione().
     * This method is used for split into province/regioni the array passed, so will have a well-ordered HashMap by provincie/regioni
     * @param reports the row array to order
     * @return an ordered Hash Table by field ('denominazione')
     */
    private HashMap<String, ArrayList<DailyReport>> createKeyValueList(DailyReport[] reports) {
        //This HashMap contains the ArrayList
        HashMap<String, ArrayList<DailyReport>> build = new HashMap<String, ArrayList<DailyReport>>();
        //Build the reports, by field
        for (DailyReport report : reports) {
            String currentDenominazione = report.getGeoField() == GeoField.REGIONALE ? report.getString("denominazione_regione") : report.getString("denominazione_provincia");
            //if the array does not yet contains the current 'denominazione'
            if (!build.containsKey(currentDenominazione))
                //create it
                build.put(currentDenominazione, new ArrayList<DailyReport>());
            //put the current report into the hashmap in the right array
            build.get(currentDenominazione).add(report);
        }
        return build;
    }

    /**
     * This class contains a single daily report, obtained via italian Dipartimento di Protezione Civile.
     * This object cannot be created individually: it has to be built by a given JsonArray, using the static method DailyReport.reportsBuilder()
     */
    public static class DailyReport {

        /**
         * The geographic territory of appartenence
         */
        private GeoField geoField;

        /**
         * The raw JsonObject containing data about that daily report
         */
        private JsonObject rawObject;

        /**
         * Returns the geographic field of appartenence of the daily report
         * @return the geographic field (NAZIONALE, REGIONALE, PROVINCIALE)
         */
        public GeoField getGeoField() {
            return geoField;
        }

        /**
         * Builds an array of DailyReport by the json array string
         * @param array array of JsonObject's
         * @return a DailyReport array containing all the reports structured
         */
        public static DailyReport[] reportsBuilder(JsonArray array) {
            DailyReport[] toReturn = new DailyReport[array.size()];
            for (int i = 0; i < array.size(); i++) {
                JsonObject curJsonObj = array.get(i).getAsJsonObject();
                GeoField curField;
                //DailyReport curDailyReport =
                if (curJsonObj.has("denominazione_provincia")) {
                    curField = GeoField.PROVINCIALE;
                } else if (curJsonObj.has("denominazione_regione")) {
                    curField = GeoField.REGIONALE;
                } else {
                    curField = GeoField.NAZIONALE;
                }
                toReturn[i] = new DailyReport(curField, curJsonObj);
            }
            return toReturn;
        }

        /**
         * Private constructor of a DailyReport
         * @param geoField the geographic field of appartenance
         * @param rawObject the raw JsonObject containing datas
         */
        private DailyReport(GeoField geoField, JsonObject rawObject) {
            this.geoField = geoField;
            this.rawObject = rawObject;
        }

        /**
         * Returns an Integer value by it's key
         * @param key the key of the JsonObject
         * @return the corresponding key's integer (if any, otherwise returns null)
         */
        public Integer getInt(String key) {
            if (this.rawObject.has(key))
                try {
                    return this.rawObject.get(key).getAsInt();
                }
                catch (ClassCastException ex) {
                    return null;
                }
            return null;
        }

        /**
         * Returns a String value by it's key
         * @param key the key of the JsonObject
         * @return the corresponding key's string (if any, otherwise returns null)
         */
        public String getString(String key) {
            if (this.rawObject.has(key))
                try {
                    return this.rawObject.get(key).getAsString();
                }
                catch (ClassCastException ex) {
                    return null;
                }
            return null;
        }
    }

    public static class GeographicElement {
        private String denominazione;
        private GeoField geoField;

        public String getDenominazione() {
            return denominazione;
        }

        public void setDenominazione(String denominazione) {
            this.denominazione = denominazione;
        }

        public GeoField getGeoField() {
            return geoField;
        }

        public void setGeoField(GeoField geoField) {
            this.geoField = geoField;
        }

        public GeographicElement(String denominazione, GeoField geoField) {
            this.denominazione = denominazione;
            this.geoField = geoField;
        }

        public GeographicElement(GeoField geoField) {
            this.geoField = geoField;
            this.denominazione = "Nazionale";
        }
    }

}
