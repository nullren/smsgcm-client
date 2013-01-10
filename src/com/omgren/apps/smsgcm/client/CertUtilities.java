package com.omgren.apps.smsgcm.client;

import static com.omgren.apps.smsgcm.client.CommonUtilities.displayMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

public final class CertUtilities {

  private static TrustManager[] getTrustManagers(final Context context) throws CertException {
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
      throw new CertException("could not load CA cert: " + e);
    } 
  }

  public static void copyKeystoreFile(final Context context) throws CertException, IOException {
    //throw new CertException("not created this function yet!");

    // check we can read and write to storage
    String state = Environment.getExternalStorageState();
    if(!Environment.MEDIA_MOUNTED.equals(state)){
      displayMessage(context, context.getString(R.string.cert_storage_unavailable));
      throw new CertException("device mounted");
    }

    // look for the keystore in Download
    File path = Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_DOWNLOADS);
    File unsecuredKeystore = new File(path, context.getString(R.string.cert_name));
    if( !unsecuredKeystore.exists() ){
      displayMessage(context, context.getString(R.string.cert_not_installed));
      throw new CertException("keystore does not exist in " + unsecuredKeystore.getAbsolutePath());
    }

    // load location of internal spot
    FileOutputStream securedKeystore = context.openFileOutput(context.getString(R.string.cert_name), Context.MODE_PRIVATE);

    // copy to internal spot
    FileChannel unsecureFile = new FileInputStream(unsecuredKeystore).getChannel();
    FileChannel secureFile = securedKeystore.getChannel();
    try {
      unsecureFile.transferTo(0, unsecureFile.size(), secureFile);
    } catch (Exception e) {
      displayMessage(context, context.getString(R.string.cert_transfer_error));
      throw new CertException("failed to copy credentials: " + e);
    } finally {
      if( unsecureFile != null ) unsecureFile.close();
      if( secureFile != null ) secureFile.close();
    }

    // delete the unsecured file
    try {
      unsecuredKeystore.delete();
    } catch (Exception e) {
      throw new CertException("could not delete unsecured credentials: " + e);
    }
  }

  private static InputStream getKeystoreFile(final Context context) throws CertException {
    try {
      return context.openFileInput(context.getString(R.string.cert_name));
    } catch(Exception e) {
      displayMessage(context, context.getString(R.string.cert_not_loaded_warning));
      throw new CertException("could not load client key file: " + e);
    }
  }

  private static KeyManager[] getKeyManagers(final Context context) throws CertException {

    InputStream keystoreLocation = getKeystoreFile(context);
    KeyManagerFactory kmf;

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
      throw new CertException("could not get client key");
    }
  }

  public static SSLContext getSSLContext(final Context cx) throws CertException {
    SSLContext sslContext;

    try {
      sslContext = SSLContext.getInstance("TLS");
      sslContext.init(getKeyManagers(cx), getTrustManagers(cx), new SecureRandom());
    } catch (Exception e){
      throw new CertException("bad ssl stuff: " + e);
    }

    return sslContext;
  }

}