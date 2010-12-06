package com.lucasdev.stereodroid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Util {
	
	public static String convertStreamToString(InputStream is) {
		StringBuilder sb = null;
		try {
			/*
			 * To convert the InputStream to String we use the
			 * BufferedReader.readLine() method. We iterate until the BufferedReader
			 * return null which means there's no more data to read. Each line will
			 * appended to a StringBuilder and returned as String.
			 */
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeStream(is);
		}
		return sb.toString();
	}
	
	public static JSONObject getJsonFromUrl(String url){
		StereodroidOAuth oauth = StereodroidOAuth.getInstance();
		String jsonString = convertStreamToString(oauth.doRequest(url).getStream());
		JSONObject json = null;
		try {
			json = new JSONObject(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
    private static final int IO_BUFFER_SIZE = 4 * 1024;
    
    /**
     * Loads a bitmap from the specified url. This can take a while, so it should not
     * be called from the UI thread.
     * 
     * @param url The location of the bitmap asset
     * 
     * @return The bitmap, or null if it could not be loaded
     */
    public static Bitmap loadBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;

        try {
            in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE);

            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
            copy(in, out);
            out.flush();

            final byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (IOException e) {
            Log.e(Stereodroid.TAG, "Could not load Bitmap from: " + url);
        } finally {
            closeStream(in);
            closeStream(out);
        }

        return bitmap;
    }

    /**
     * Closes the specified stream.
     * 
     * @param stream The stream to close.
     */
    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                android.util.Log.e(Stereodroid.TAG, "Could not close stream", e);
            }
        }
    }

    /**
     * Copy the content of the input stream into the output stream, using a
     * temporary byte array buffer whose size is defined by
     * {@link #IO_BUFFER_SIZE}.
     * 
     * @param in The input stream to copy from.
     * @param out The output stream to copy to.
     * @throws IOException If any error occurs during the copy.
     */
    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

}
