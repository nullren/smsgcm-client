package com.omgren.apps.smsgcm.client;

import static com.omgren.apps.smsgcm.client.CommonUtilities.displayMessage;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ImportReceiver extends BroadcastReceiver {
  private static final String TAG = "ImportReceiver";

  @Override
    public void onReceive(Context context, Intent intent){

      Log.i(TAG, "received request to copy credentials");
      displayMessage(context, context.getString(R.string.recv_sms, "Just kidding", "i want to copy a cert"));

    }

}
