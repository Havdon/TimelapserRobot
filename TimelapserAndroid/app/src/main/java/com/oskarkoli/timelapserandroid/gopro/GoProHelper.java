package com.oskarkoli.timelapserandroid.gopro;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;

/**
 * Helper function for GoPro management.
 */
public class GoProHelper {
    public static final String PHOTO_MODE = "%01";
    public static final String PREVIEW_ON = "%02";



    public static boolean takePicture() throws InterruptedException {
        try {
            HttpResponse response = makeRequest("bacpac", "SH", "%01");

            int code = response.getStatusLine().getStatusCode();
            if (code == 410) {
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if the streaming path on the server is up and working.
     * @return  Return true if call was successful.
     * @throws InterruptedException
     */
    public static boolean isPreviewValid() throws InterruptedException {
        try {
            HttpResponse response = makeRequest("http://10.5.5.9/", null, null, null);
            if(response == null) {
                return false;
            }
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Enables streaming preview on camera.
     * Note: Streaming drains the battery quickly.
     * @return  True if call was successful.
     * @throws InterruptedException
     */
    public static boolean enablePreview() throws InterruptedException {
        try {
            HttpResponse response = makeRequest("camera", "PV", PREVIEW_ON);

            int code = response.getStatusLine().getStatusCode();
            if (code == 410) {
                return false;
            } else {
                Thread.sleep(2000); // Wait for GoPro
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sets the camera to picture mode (In contrast to video mode)
     * @return  True if call was successful.
     * @throws InterruptedException
     */
    public static boolean setPictureMode() throws InterruptedException {
        try {
            HttpResponse response = makeRequest("camera", "CM", PHOTO_MODE);

            int code = response.getStatusLine().getStatusCode();
            if (code == 410) {
                return false;
            } else {
                Thread.sleep(2000); // Wait for GoPro
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Sends a power on signal to GoPro. Sleeps 3 seconds to allow camera to react to command.
     * @param on    True is on, false is off.
     * @return True if call was successful.
     * @throws InterruptedException
     */
    public static boolean setPowerMode(boolean on) throws InterruptedException {
        try {
            String value = null;
            if(on) {
                value = "%01";
            } else {
                value = "%00";
            }
            HttpResponse response = makeRequest("bacpac", "PW", value);

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));


            Thread.sleep(3000); // Sleep 3 seconds before continuing. (Allow GoPro to start)

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private static HttpResponse makeRequest(String root, String command, String value) throws IOException {
        return makeRequest(GoProManager.BASE_URL, root, command, value);
    }

    /**
     * Makes request to GoPro server.
     *
     * Urls are in form: baseUrl/root/command?t=value
     *
     * @throws IOException
     */
    private static HttpResponse makeRequest(String baseUrl, String root, String command, String value) throws IOException {
        String url = baseUrl;
        if(root != null) {
            url = url + "" + root;
        }
        if(command != null) {
            url = url + "/" + command + "?t=" + GoProManager.PASSWORD;
            if(value != null) {
                url = url + "&p=" + value;
            }
        }

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        try {
            HttpResponse response = httpclient.execute(request);
            int code = response.getStatusLine().getStatusCode();
            return response;
        } catch (SocketException e) {
            return null;
        }


    }


}
