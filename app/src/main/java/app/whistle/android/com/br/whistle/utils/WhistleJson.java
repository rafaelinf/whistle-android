package app.whistle.android.com.br.whistle.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import app.whistle.android.com.br.whistle.auxiliary.JsonResponse;
import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;

/**
 * Created by rafael on 27/01/2016.
 */
public class WhistleJson {

    private static final String LOG_CLASS = "WhistleJson";

/*
    @Deprecated
    public static String makeRequest(String urlAddress) {

        String urlFull = WhistleSingleton.URL_WHISTLE_WS + urlAddress;
        //Log.i(LOG_CLASS, "urlFull 1 = " + urlFull);

        urlFull = urlFull.replaceAll(" ", "%20");
        Log.i(LOG_CLASS, "urlFull 2 = " + urlFull);

        HttpURLConnection con = null;
        URL url = null;
        String response = null;
        try {
            url = new URL(urlFull);
            con = (HttpURLConnection) url.openConnection();
            response = readStream(con.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }
        return response;
    }

    @Deprecated
    public static String sendPost(String restPath, String urlAddress, String json) {
        HttpURLConnection urlConnection = null;
        URL url = null;
        String response = null;

        try {

            String urlFull = WhistleSingleton.URL_WHISTLE_WS + restPath + "/" + urlAddress;
            urlFull = urlFull.replaceAll(" ", "%20").replace(Character.toString((char) 29), "");
            //Log.i(LOG_CLASS, "sendPost urlFull = " + urlFull);
            //Log.i(LOG_CLASS, "sendPost json = " + json);

            url = new URL(urlFull);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            //urlConnection.setRequestProperty("Auth-token", getAuthorization());

            OutputStream os = urlConnection.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();

            response = readStream(urlConnection.getInputStream());

            return response;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    ////////////////////////////////////////////////////////
*/

    public static JsonResponse makeRequest(String urlAddress) {
        try {

            String urlFull = WhistleSingleton.URL_WHISTLE_WS + urlAddress;
            urlFull = urlFull.replaceAll(" ", "%20");

            URL url = new URL(urlFull);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //response = readStream(con.getInputStream());

            int responseCode = urlConnection.getResponseCode();
            //System.out.println("Response Code : " + responseCode);

            StringBuffer response = new StringBuffer();
            if(responseCode == 200){

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //print result
                System.out.println(response.toString());

            }

            return new JsonResponse(responseCode, response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

/*
    public static HttpURLConnection sendPost_(String restPath, String urlAddress, String json){
        HttpURLConnection urlConnection = null;
        URL url = null;
        String response = null;

        try {

            String urlFull = WhistleSingleton.URL_WHISTLE_WS + restPath + "/" + urlAddress;
            urlFull = urlFull.replaceAll(" ", "%20").replace(Character.toString((char) 29), "");
            url = new URL(urlFull);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            //urlConnection.setRequestProperty("Auth-token", getAuthorization());

            OutputStream os = urlConnection.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();

            //response = readStream(urlConnection.getInputStream());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }

        return urlConnection;
    }
*/

    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {

            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }

        return builder.toString();
    }

    public static JsonResponse sendPost(String restPath, String urlAddress, String json) {
        HttpURLConnection urlConnection = null;
        URL url = null;

        try {

            String urlFull = WhistleSingleton.URL_WHISTLE_WS + restPath + "/" + urlAddress;
            urlFull = urlFull.replaceAll(" ", "%20").replace(Character.toString((char) 29), "");
            url = new URL(urlFull);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);

            // Send post request
            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes(json);
            wr.flush();
            wr.close();

            int responseCode = urlConnection.getResponseCode();

            StringBuffer response = new StringBuffer();
            if(responseCode == 200){

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //print result
                System.out.println(response.toString());

            }

            return new JsonResponse(responseCode, response.toString());

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
