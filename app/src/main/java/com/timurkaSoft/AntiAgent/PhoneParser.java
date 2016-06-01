package com.timurkaSoft.AntiAgent;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PhoneParser {

    private static final String TAG = PhoneParser.class.getSimpleName();
    private static final String LANG = "eng";
    private static final String TESSDATA = "tessdata";
    private final String DATA_PATH;

    private TessBaseAPI tessBaseApi;

    public PhoneParser(Context context) {
        DATA_PATH = context.getFilesDir() + "/TesseractSample/";
        prepareTesseract(context);
    }

    private void prepareTesseract(Context context) {
        try {
            prepareDirectory(DATA_PATH + TESSDATA);
        } catch (Exception e) {
            e.printStackTrace();
        }

        copyTessDataFiles(context, TESSDATA);
    }

    private void prepareDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "ERROR: Creation of directory " + path + " failed, check does Android Manifest have permission to write to external storage.");
            }
        } else {
            Log.i(TAG, "Created directory " + path);
        }
    }

    private void copyTessDataFiles(Context context, String path) {
        try {
            String fileList[] = context.getAssets().list(path);

            for (String fileName : fileList) {
                String pathToDataFile = DATA_PATH + path + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {

                    InputStream in = context.getAssets().open(path + "/" + fileName);

                    OutputStream out = new FileOutputStream(pathToDataFile);

                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    Log.d(TAG, "Copied " + fileName + "to tessdata");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to copy files to tessdata " + e.toString());
        }
    }

    public String extractText(Bitmap bitmap) {
        try {
            tessBaseApi = new TessBaseAPI();
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage());
            if (tessBaseApi == null) {
                Log.e(TAG, "TessBaseAPI is null. TessFactory not returning tess object.");
            }
        }

        tessBaseApi.init(DATA_PATH, LANG);
        tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890+()-");

        Log.d(TAG, "training file loaded");
        tessBaseApi.setImage(bitmap);
        String extractedText = "empty result";
        try {
            extractedText = tessBaseApi.getUTF8Text();
        } catch (Exception e) {
            Log.e(TAG, "Error in recognizing text...");
        }
        tessBaseApi.end();
        return extractedText;
    }

}


