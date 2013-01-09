package com.omgren.apps.smsgcm.client;

import static com.omgren.apps.smsgcm.client.CommonUtilities.displayMessage;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class CertUtilities {

  private static TrustManager[] getTrustManagers(final Context context) throws IOException {
    TrustManagerFactory tmf;
    try {
      // CA cert store password
      String truststorePassword = "blahblah";
      // CA cert store
      InputStream truststoreLocation = context.getResources().openRawResource(R.raw.trust_store);

      KeyStore truststore = KeyStore.getInstance("BKS");
      truststore.load(truststoreLocation, truststorePassword.toCharArray());
      tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(truststore);
      return tmf.getTrustManagers();

    } catch (Exception e) {
      throw new IOException("could not load CA cert: " + e);
    } 
  }

  private static KeyManager[] getKeyManagers(final Context context) throws IOException {
    InputStream keystoreLocation;
    KeyManagerFactory kmf;

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
      return kmf.getKeyManagers();
    } catch (Exception e) {
      displayMessage(context, context.getString(R.string.cert_password_warning));
      throw new IOException("could not get client key");
    }
  }

  public static SSLContext getSSLContext(final Context cx) throws IOException {
    SSLContext sslContext;

    try {
      sslContext = SSLContext.getInstance("TLS");
      sslContext.init(getKeyManagers(cx), getTrustManagers(cx), new SecureRandom());
    } catch (Exception e){
      throw new IOException("bad ssl stuff: " + e);
    }

    return sslContext;
  }

}
