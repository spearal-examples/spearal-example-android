package org.spearal.examples.android.conf;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by william on 24/10/2014.
 */
public class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

    private final ImageView imageView;
    protected boolean success = false;

    public LoadImageTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            InputStream in = url.openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in, null, new BitmapFactory.Options());
            success = true;
            return bitmap;
        }
        catch (MalformedURLException e) {
            Log.e(getClass().getSimpleName(), "Incorrect url " + urls[0]);
        }
        catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Network error during image load " + urls[0]);
        }
        return null;
    }

    @Override
    protected void onPostExecute(final Bitmap bitmap) {
        if (success)
            imageView.setImageBitmap(bitmap);
    }
}