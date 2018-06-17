package ru.vkatzyn.lorempicsum;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Helper class for managing picture URL strings in cache.
 */
public class CacheUtils {
    public static final String urlCacheFilename = "pictures_url_storage";

    private CacheUtils() {

    }


    /**
     * Saves picture URL string to cache.
     *
     * @param url     picture URL string
     * @param context
     */
    public static void savePictureUrlToCache(String url, Context context) {
        File file = new File(context.getCacheDir(), urlCacheFilename);
        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.e(CacheUtils.class.getSimpleName(), "Error creating cache file.", e);
        }

        FileOutputStream fileOutputStream = null;
        if (file.canWrite()) {
            try {
                fileOutputStream = new FileOutputStream(file, true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                if (file.length() != 0) {
                    bufferedWriter.newLine();
                }
                bufferedWriter.write(url);
                bufferedWriter.flush();
            } catch (IOException e) {
                Log.e(CacheUtils.class.getSimpleName(), "Error writing url to file.", e);
            } finally {
                try {

                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    getPictureUrls(context);
                } catch (IOException e) {
                    Log.e(CacheUtils.class.getSimpleName(), "Error closing output stream.", e);
                }
            }
        }

    }

    /**
     * Retrieves picture URL strings from cache.
     *
     * @param context
     * @return ArrayList with picture URLs. Null if none of the URLs were cached.
     */
    public static ArrayList<String> getPictureUrls(Context context) {
        File file = new File(context.getCacheDir(), urlCacheFilename);
        if (!file.exists()) {
            return null;
        }

        ArrayList<String> pictureUrls = new ArrayList<>();
        FileInputStream fileInputStream = null;
        if (file.canRead()) {
            try {
                fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    pictureUrls.add(line);
                }
            } catch (IOException e) {
                Log.e(CacheUtils.class.getSimpleName(), e.getMessage(), e);
            } finally {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    Log.e(CacheUtils.class.getSimpleName(), "Couldn't close inputstream.", e);
                }
            }
        }

        return pictureUrls;
    }


    /**
     * Check whether the URL is in cache.
     *
     * @param url     picture url string
     * @param context
     * @return True if the URL string is in cache. False otherwise.
     */
    public static boolean isPictureUrlCached(String url, Context context) {
        ArrayList<String> urls = getPictureUrls(context);
        return urls != null && urls.indexOf(url) != -1;
    }
}
