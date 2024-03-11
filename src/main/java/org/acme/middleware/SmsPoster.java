package org.acme.middleware;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SmsPoster {

    public static void sendSMSHttp(final String msisdnFrom, final String msisdnTo, String message) {
        String messageToSend = "";
        try {
            messageToSend = URLEncoder.encode(message.trim(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final String finalMessageToSend = messageToSend;
        new Thread(new Runnable() {
            public void run() {
                HttpURLConnection httpConn = null;
                try {
                    // Config config = new Config(); // TODO: USE CONFIG
                    // String URL = config.getUrlApiSms();
                    String URL = "http://10.249.8.70:8081/sendsms/";
                    URL url = new URL(URL + msisdnFrom + "/" + msisdnTo);
                    URLConnection urlConnection = url.openConnection();
                    httpConn = (HttpURLConnection) urlConnection;
                    httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    httpConn.setRequestProperty("Connection", "keep-alive");
                    httpConn.setRequestMethod("POST");
                    String data = "message=" + finalMessageToSend;
                    httpConn.setDoOutput(true);
                    httpConn.setDoInput(true);
                    // send request
                    OutputStreamWriter wr = new OutputStreamWriter(httpConn.getOutputStream());
                    wr.write(data.toString());
                    wr.flush();
                    wr.close();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                    String line = "";
                    String result = "";
                    while ((line = rd.readLine()) != null) {
                        result.concat(line);
                    }
                    rd.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                } finally {
                    if (httpConn != null) {
                        httpConn.disconnect();
                    }
                }
            }
        }).start();
    }
}
