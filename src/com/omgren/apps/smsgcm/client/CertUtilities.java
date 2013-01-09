package com.omgren.apps.smsgcm.client;

import static com.omgren.apps.smsgcm.client.CommonUtilities.displayMessage;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class CertUtilities {

  public static SSLContext getSSLContext(final Context context) throws IOException {
    SSLContext sslContext;
    InputStream truststoreLocation, keystoreLocation;
    KeyManagerFactory kmf;
    TrustManagerFactory tmf;

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

    return sslContext;
  }

}
