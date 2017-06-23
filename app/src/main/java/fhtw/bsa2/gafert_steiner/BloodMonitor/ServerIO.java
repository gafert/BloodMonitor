package fhtw.bsa2.gafert_steiner.BloodMonitor;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerIO {

    private final static String TAG = "ServerIO";

    public static String saveToServer(String url, String jsonString) {
        Log.d(TAG, "saveToServer: Url=" + url);
        String result = null;

        try {
            // openConnection
            URL mUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) mUrl.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            con.setRequestProperty("User-Agent", "BSA");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            // Send POST output
            OutputStream printout = new BufferedOutputStream(con.getOutputStream());
            printout.write(jsonString.getBytes());
            printout.flush();
            printout.close();

            // Get reply from Server
            InputStream inputStream = con.getInputStream();
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                buffer.append(inputLine + "\n");
            }
            result = buffer.toString();
            con.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String loadFromServer(String url) {
        String result = null;

        try {
            // openConnection and set-up connection
            URL mUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) mUrl.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "BSA");
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
            Log.e("ServerIO", e.getMessage());
        }

        return result;
    }

}
