/**
 * This class downloads and parses the data from https://github.com/pcm-dpc/COVID-19
 * It's a SINGLETON!
 */

package com.example.datavirus;

import android.os.AsyncTask;
import android.util.Log;

import org.json.*;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class DataParser {

    private static String regionaleUrl = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-regioni.json";
    private static String nazionaleUrl = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json";
    private static String provincialiUrl = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-province.json";

    private static DataParser instance;
    private DataParser() {}

    public static DataParser getInstance() {
        if (instance == null)
            instance = new DataParser();
        return instance;
    }

    public static boolean refreshData() {
        new AsyncDownloader().execute(nazionaleUrl, regionaleUrl, provincialiUrl);
        return true;
    }

    private static class AsyncDownloader  extends AsyncTask<String, Integer, ArrayList<JSONArray>> {

        @Override
        protected ArrayList<JSONArray> doInBackground(String... strings) {
            try {
                ArrayList<JSONArray> arr = new ArrayList<>();
                for (String url: strings) {
                    arr.add(retrieveJSONFromUrl(url));
                }
                return arr;
            } catch (IOException e) {
                Log.e("DataParser", "Download error");
                return null;
            } catch (JSONException e) {
                Log.e("DataParser", "JSON parse error");
                return null;
            }
        }

        private JSONArray retrieveJSONFromUrl(String Url) throws IOException, JSONException {
            //Download json from source URL
            Scanner scanner = new Scanner(new URL(Url).openStream(), StandardCharsets.UTF_8.toString());
            scanner.useDelimiter("\\A");
            String scanned = scanner.hasNext() ? scanner.next() : "";
            //Parse into json
            return new JSONArray(scanned);

        }
    }
}
