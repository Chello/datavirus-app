package com.example.datavirus;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Singleton class for read and write the tiles starred
 */
public class SaveReadStarredTiles {


    private static SaveReadStarredTiles instance;

    private Context context;

    private ArrayList<FieldGeographicElement> savedTiles;

    public ArrayList<FieldGeographicElement> getSavedTiles() {
        return savedTiles;
    }

    public SaveReadStarredTiles(Context context) {
        this.context = context;
        this.savedTiles = new ArrayList<>();
        this.loadFile();
    }

    public static SaveReadStarredTiles getInstance(Context context) {
        if (instance == null)
            instance = new SaveReadStarredTiles(context);
        return instance;
    }

    public void saveElement(FieldGeographicElement geographicElement) {

        if (this.exists(geographicElement) != -1) {
            return;
        }
        this.savedTiles.add(geographicElement);
        this.savetoFile();
        this.loadFile();
    }

    public Integer exists(FieldGeographicElement geographicElement) {
        Integer i;
        for (i = 0; i < this.savedTiles.size(); i++) {
            if (this.savedTiles.get(i).compareTo(geographicElement) == 0) {
                Log.d("Salvatore ", "l'ho trovato");
                return i;
            }
        }
        Log.d("Salvatore ", "Non l'ho trovato");
        return -1;
    }

    public void deleteElement(FieldGeographicElement geographicElement) {
        Integer i = this.exists(geographicElement);
        if (i != -1) {
            Log.d("Salvatore ", "Remosso");
            this.savedTiles.remove((int) i);
        }
        this.savetoFile();
        this.loadFile();
    }

    private void savetoFile() {
        try {
            // Creates a file in the primary external storage space of the
            // current application.
            // If the file does not exists, it is created.
            File testFile = new File(this.context.getExternalFilesDir(null), "TestFile.txt");
            if (!testFile.exists())
                testFile.createNewFile();

            // Adds a line to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, false /*append*/));
            writer.write(new Gson().toJson(this.savedTiles));
            writer.close();
            // Refresh the data so it can seen when the device is plugged in a
            // computer. You may have to unplug and replug the device to see the
            // latest changes. This is not necessary if the user should not modify
            // the files.
        } catch (IOException e) {
            Log.e("ReadWriteFile", "Unable to write to the TestFile.txt file.");
        }
    }

    private void loadFile() {
        String textFromFile = "";
        // Gets the file from the primary external storage space of the
        // current application.
        File testFile = new File(this.context.getExternalFilesDir(null), "TestFile.txt");
        if (testFile != null) {
            StringBuilder stringBuilder = new StringBuilder();
            // Reads the data from the file
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;

                while ((line = reader.readLine()) != null) {
                    textFromFile += line.toString();
                    textFromFile += "\n";
                }
                reader.close();
                FieldGeographicElement[] userArray = new Gson().fromJson(textFromFile, FieldGeographicElement[].class);
                Log.d("Lettura file", textFromFile);
                this.savedTiles = new ArrayList<FieldGeographicElement>(Arrays.asList(userArray));
            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the TestFile.txt file.");
            }
        }
    }

}
