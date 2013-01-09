package com.omgren.apps.smsgcm.client;

import static com.omgren.apps.smsgcm.client.CommonUtilities.displayMessage;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

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
