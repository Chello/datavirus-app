package com.example.datavirus;

import android.content.res.Resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * This class contains a single daily report, obtained via italian Dipartimento di Protezione Civile.
 * This object cannot be created individually: it has to be built by a given JsonArray, using the static method DailyReport.reportsBuilder()
 */
public class DailyReport {

    /**
     * The geographic territory of appartenence
     */
    private DPCData.GeoField geoField;

    /**
     * The raw JsonObject containing data about that daily report
     */
    private JsonObject rawObject;

    /**
     * Returns the geographic field of appartenence of the daily report
     * @return the geographic field (NAZIONALE, REGIONALE, PROVINCIALE)
     */
    public DPCData.GeoField getGeoField() {
        return geoField;
    }

    /**
     * Returns available keys that makes sense to query
     * @return an array of queryable keys
     */
    public ArrayList<String> getKeys(Resources res) {
        ArrayList<String> toRet = new ArrayList<String>();
        Set<Map.Entry<String, JsonElement>> entries = this.rawObject.entrySet();
        for (Map.Entry<String, JsonElement> entry: entries) {
            String current = entry.getKey();
            if (DPCData.excludeList.contains(current))
                continue;
            toRet.add(current);
        }
        this.moveElemToPos(toRet, res.getString(R.string.denominazione_total),0);
        this.moveElemToPos(toRet, res.getString(R.string.denominazione_active), 1);
        this.moveElemToPos(toRet, res.getString(R.string.denominazione_healed), 2);
        this.moveElemToPos(toRet, res.getString(R.string.denominazione_deaths), 3);
        return toRet;
    }

    /**
     * Called for re-order items in the array
     * @param arr array to reorder
     * @param item item to move
     * @param itemPos position where move the item
     */
    private static void moveElemToPos(ArrayList<String> arr, String item, Integer itemPos) {
        if (!arr.contains(item))
            return;
        arr.remove(item);
        arr.add(itemPos, item );
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
            DPCData.GeoField curField;
            //DailyReport curDailyReport =
            if (curJsonObj.has("denominazione_provincia")) {
                curField = DPCData.GeoField.PROVINCIALE;
            } else if (curJsonObj.has("denominazione_regione")) {
                curField = DPCData.GeoField.REGIONALE;
            } else {
                curField = DPCData.GeoField.NAZIONALE;
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
    private DailyReport(DPCData.GeoField geoField, JsonObject rawObject) {
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
            catch (Exception ex) {
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
            catch (Exception ex) {
                return null;
            }
        return null;
    }
}
