package com.omgren.apps.smsgcm.client;

import android.telephony.SmsManager;
import android.content.Context;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

import static com.omgren.apps.smsgcm.client.CommonUtilities.displayMessage;

public class SmsSender {
  private static final String TAG = "SmsSender";
  public void send(Context context, String recipient, String message){
    SmsManager smsman = SmsManager.getDefault();
    ArrayList<String> msgs = smsman.divideMessage(message);
    smsman.sendMultipartTextMessage(recipient, null, msgs, null, null);

    record_sent(context, recipient, message);
    Log.i(TAG, "sent sms to " + recipient + " saying " + message);
    displayMessage(context, context.getString(R.string.sent_sms, recipient, message));
  }

  public void record_sent(Context context, String address, String message){
    // store the sent sms in the sent folder (that shouldn't be necessary?!)
    ContentValues values = new ContentValues();
    values.put("address", address);
    values.put("body", message);
    context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
  }

}
