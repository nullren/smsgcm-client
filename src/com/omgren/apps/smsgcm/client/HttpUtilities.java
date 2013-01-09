package com.omgren.apps.smsgcm.client;

import static com.omgren.apps.smsgcm.client.CommonUtilities.TAG;
import static com.omgren.apps.smsgcm.client.CommonUtilities.displayMessage;
import static java.net.URLEncoder.encode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Helper class used to communicate with the demo server.
 */
public final class HttpUtilities {

  /**
   * Issue a POST request to the server.
   *
   * @param endpoint POST address.
   * @param params request parameters.
   *
   * @throws IOException propagated from POST.
   */
  public static void post(final Context context, String endpoint, Map<String, String> params) throws IOException {
    byte[] bytes = makeQueryString(params);
    HttpsURLConnection conn = null;
    try {
      conn = urlConnect(context, endpoint);
      conn.setDoOutput(true);
      conn.setUseCaches(false);
      conn.setFixedLengthStreamingMode(bytes.length);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type",
          "application/x-www-form-urlencoded;charset=UTF-8");
      // post the request
      OutputStream out = conn.getOutputStream();
      out.write(bytes);
      out.close();
      // handle the response
      int status = conn.getResponseCode();
      if (status != 200) {
        throw new IOException("Post failed with error code " + status);
      }
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
  }

  /**
   * Something to make a query string quickly.
   *
   * @param params A map of parameters
   *
   * @return a byte array of the query string
   */
  private static byte[] makeQueryString(Map<String, String> params){
    StringBuilder bodyBuilder = new StringBuilder();
    Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
    // constructs the POST body using the parameters
    while (iterator.hasNext()) {
      Entry<String, String> param = iterator.next();
      try {
        bodyBuilder.append(encode(param.getKey(), "UTF-8")).append('=')
          .append(encode(param.getValue(), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        Log.e(TAG, "problem with encoding" + e);
      }
      if (iterator.hasNext()) {
        bodyBuilder.append('&');
      }
    }
    String body = bodyBuilder.toString();
    return body.getBytes();
  }

  /**
   * Opens a HttpURLConnection object from the URL
   *
   * @param endpoint A url to connect to
   *
   * @return an HttpURLConnection object
   *
   * @throws IOException
   */
  private static HttpsURLConnection urlConnect(final Context context, String endpoint) throws IOException {
    URL url;
    SSLContext sslContext;
    InputStream truststoreLocation, keystoreLocation;
    KeyManagerFactory kmf;
    TrustManagerFactory tmf;
    try {
      url = new URL(endpoint);

    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("invalid url: " + endpoint);
    }

    try {
      // CA cert store password
      String truststorePassword = "blahblah";
      // CA cert store
      truststoreLocation = context.getResources().openRawResource(R.raw.trust_store);

      KeyStore truststore = KeyStore.getInstance("BKS");
      truststore.load(truststoreLocation, truststorePassword.toCharArray());
      tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(truststore);
    } catch (Exception e) {
      throw new IOException("could not load CA cert");
    }

    try {
      // client cert
      keystoreLocation = context.getResources().openRawResource(R.raw.key_store);
    } catch (Exception e) {
      displayMessage(context, context.getString(R.string.cert_not_loaded_warning));
      throw new IOException("could not load client key file");
    }

    try {
      // client cert password
      SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
      String keystorePassword = sharedPref.getString(SettingsActivity.PREF_CERT_PASSWORD, "");

      KeyStore keystore = KeyStore.getInstance("PKCS12");
      keystore.load(keystoreLocation, keystorePassword.toCharArray());
      kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(keystore, "".toCharArray());
    } catch (Exception e) {
      displayMessage(context, context.getString(R.string.cert_password_warning));
      throw new IOException("could not get client key");
    }

    try {
      sslContext = SSLContext.getInstance("TLS");
      sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
    } catch (Exception e){
      throw new IOException("bad ssl stuff: " + e);
    }

    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

    if( sslContext != null )
      conn.setSSLSocketFactory(sslContext.getSocketFactory());

    return conn;
  }

  /**
   * Downloads HTTP content.
   *
   * @param url URL address.
   *
   * @return content of url address.
   */
  public static String get(final Context context, String url){
    BufferedReader buf = null;
    try {
      HttpsURLConnection conn = urlConnect(context, url);
      buf = new BufferedReader(new InputStreamReader(conn.getInputStream()));

      int status = conn.getResponseCode();
      if (status != 200) {
        throw new IOException("Get failed with error code " + status);
      }

      StringBuffer sb = new StringBuffer();
      String buffer;
      while((buffer = buf.readLine()) != null)
        sb.append(buffer);

      conn.disconnect();
      buf.close();

      return sb.toString();

    } catch (IOException e){
      Log.e(TAG, "IOException: " + e);
    } finally {
    }

    return null;
  }

}
