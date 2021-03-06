package fhtw.bsa2.gafert_steiner.BloodMonitor;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Contains post and get methods
 */
class ServerIO {

    private final static String TAG = "ServerIO";

    /**
     * Connects to the server and posts a jsonString
     *
     * @param url        Where the jsonString should be posted
     * @param jsonString The jsonFile to post
     * @return Returns true if the String was successfully send
     */
    static String saveToServer(String url, String jsonString) {
        Log.d(TAG, "writeToServer: Url=" + url);
        String result = null;

        try {
            // openConnection
            URL mUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) mUrl.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            con.setRequestProperty("User-Agent", "BSA");
            con.setConnectTimeout(1000);
            con.setReadTimeout(1000);

            // Send POST output
            OutputStream printout = new BufferedOutputStream(con.getOutputStream());
            printout.write(jsonString.getBytes());
            printout.flush();
            printout.close();

            // Get reply from Server
            InputStream inputStream = con.getInputStream();
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                buffer.append(inputLine).append("\n");
            }
            result = buffer.toString();
            con.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Connects to a Rest Server and gets all information in a String
     *
     * @param url The Server Address
     * @return Returns the gotten String of the Rest Server
     */
    static String loadFromServer(String url) {
        String result = null;

        try {
            // openConnection and set-up connection
            URL mUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) mUrl.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "BSA");
            con.setConnectTimeout(1000);
            con.setReadTimeout(1000);

            int responseCode = con.getResponseCode();
            // Handling of response code should be added here …
            Log.d("ServerIO", "HTTP Response: " + responseCode); // 200 OK

            // Prepare connection for reading a stream and read in while
            BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
            String output;

            while ((output = br.readLine()) != null) {
                if (result == null) {
                    result = "";
                }
                result += output;
            }

            con.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
