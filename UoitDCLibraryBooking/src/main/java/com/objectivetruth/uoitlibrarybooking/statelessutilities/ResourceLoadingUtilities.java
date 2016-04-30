package com.objectivetruth.uoitlibrarybooking.statelessutilities;

import android.content.Context;
import timber.log.Timber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceLoadingUtilities {

    /**
     * Loads a file from the {@code /assets} folder of the app as a String ready to be manipulated
     * @param context
     * @param name
     * @return
     */
    public static String loadAssetTextAsString(Context context, String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ( (str = in.readLine()) != null ) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Timber.e("Error opening asset " + name, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Timber.e("Error closing asset " + name, e);
                }
            }
        }

        return null;
    }
}
