package com.example.datavirus;

import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Objects from this class will contain data from Nazionale, Regionale and Provinciale sections.
 * These data are structured as the italian Dipartimento della Protezione Civile's JSONs are structured.
 *
 * This class is intended as a Singleton. The instance is accessible via DataParser::getDPCDataInstance()
 */

public class DPCData {
    enum GeoField {
        NAZIONALE, REGIONALE, PROVINCIALE
    }

    /**
     * List of keys to exclude from a DailyReport but in a Json exists
     */
    public static List<String> excludeList = Arrays.asList("note_en", "note_it", "lat","long", "stato", "sigla_provincia",
            "denominazione_provincia", "codice_provincia",
            "denominazione_regione", "codice_regione", "In fase di definizione/aggiornamento");

    //private static List<String> excludeList;
    //Reports for territorial data
    private DailyReport[] nazionale;
    private HashMap<String, ArrayList<DailyReport>> regionale;
    private HashMap<String, ArrayList<DailyReport>> provinciale;

    /**
     * Constructor. Parses the Json strings and structures data
     * @param nazionaleJson json for nazionale field
     * @param regionaleJson json for regionale field
     * @param provincialeJson json for provinciale field
     */
    public DPCData(String nazionaleJson, String regionaleJson, String provincialeJson) {

        JsonParser parser = new JsonParser();

        this.nazionale = DailyReport.reportsBuilder(parser.parse(nazionaleJson).getAsJsonArray());
        DailyReport[] regionale = DailyReport.reportsBuilder(parser.parse(regionaleJson).getAsJsonArray());
        DailyReport[] provinciale = DailyReport.reportsBuilder(parser.parse(provincialeJson).getAsJsonArray());

        this.regionale = createKeyValueList(regionale);
        this.provinciale = createKeyValueList(provinciale);

        Log.d("DPCParse", "Data parsed");
    }

    /**
     * Returns the number of reports, that corresponds at the number of days since the outbreak count started
     * @return the number of reports
     */
    public Integer getReportsNumber() {
        return this.nazionale.length;
    }

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
     * Returns the Nazionale report
     * @return the Nazionale report
     */
    public DailyReport[] getNazionale() {
        return this.nazionale;
    }

    public DailyReport[] getReportFromGeoElement(GeographicElement element) {
        if (element.getGeoField() == GeoField.NAZIONALE)
            return this.nazionale;
        if (element.getGeoField() == GeoField.REGIONALE)
            return this.getRegionaleReport(element.getDenominazione());
        if (element.getGeoField() == GeoField.PROVINCIALE)
            return this.getProvincialeReport(element.getDenominazione());
        return null;
    }

    /**
     * Returns a list of integer representing the numbers relative to FieldGeographicElement passed
     * @param element the element to search for
     * @return list of integer, progression of the curve
     */
    public ArrayList<Integer> getValuesFromGeoField(FieldGeographicElement element) {
        DailyReport[] reports = this.getReportFromGeoElement(element);
        ArrayList<Integer> toRet = new ArrayList<Integer>();
        for (DailyReport report : reports) {
            toRet.add(report.getInt(element.getField()));
        }
        return toRet;
    }

    /**
     * Returns the date of the last DailyReport
     * @return the date of the last DailyReport
     */
    public Calendar getLastDate() {
        return this.getDate(this.nazionale.length -1);
    }

    /**
     * Returns the date of the first DailyReport
     * @return the date of the first DailyReport
     */
    public Calendar getFirstDate() {
        return this.getDate(0);
    }

    /**
     * Returns the date of the report in day number (from the start of the outbreak)
     * @param pos the day from the start of the outbreak
     * @return the date of the day requested in parameter
     */
    private Calendar getDate(Integer pos) {
        String dateRaw = this.nazionale[pos].getString("data");
        return DPCData.convertStringToDate(dateRaw);
    }

    /**
     * Converts a Json Date string into a Calendar object
     * @param s
     * @return
     */
    private static Calendar convertStringToDate(String s) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ITALY);
            Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
            calendar.setTime(dateFormat.parse(s));   // assigns calendar to given date
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
    private DailyReport[] getRegionaleReport(String regione) {
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
    private DailyReport[] getProvincialeReport(String provincia) {
        if (this.provinciale.containsKey(provincia)) {
            ArrayList<DailyReport> arr = this.provinciale.get(provincia);
            return arr.toArray(new DailyReport[arr.size()]);
        }
        return null;
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
     * Trims the String passed, replacing '_' with ' ' and capitalizyng first letter
     * @param s the string to trim
     * @return the string trimmed
     */
    public static String trimTitleString(String s) {
        s = s.trim().replace('_', ' '); //replace bad characters
        return s.substring(0, 1).toUpperCase() + s.substring(1); //first capital letter
    }
}
