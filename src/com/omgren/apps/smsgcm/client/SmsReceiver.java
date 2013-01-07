package com.omgren.apps.smsgcm.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import static com.omgren.apps.smsgcm.client.CommonUtilities.displayMessage;
import static com.omgren.apps.smsgcm.client.ServerUtilities.uploadMessage;
import com.omgren.apps.smsgcm.common.SmsMessageDummy;

public class SmsReceiver extends BroadcastReceiver {
  private static final String TAG = "SmsReceiver";

  @Override
    public void onReceive(Context context, Intent intent){
      Bundle bundle = intent.getExtras();
      Object messages[] = (Object[]) bundle.get("pdus");

      // go through sms messages
      for( int n = 0; n < messages.length; n++ ){
        SmsMessage sms = SmsMessage.createFromPdu((byte[])messages[n]);
        SmsMessageDummy sms_ = new SmsMessageDummy();

        // get sender and message
        sms_.message = sms.getDisplayMessageBody();
        sms_.address = sms.getDisplayOriginatingAddress();
        sms_.name = sms_.address;

        // lookup name from phone number
        if( !sms.isEmail() )
          sms_.name = lookupName(context, sms_.address);

        Log.i(TAG, "received sms form " + sms_.name + " saying " + sms_.message);
        displayMessage(context, "RECV SMS from " + sms_.name + ": " + sms_.message);

        // send the message to the server
        uploadMessage(sms_);
      }

    }

  public String lookupName(Context context, String phoneNumber){
    String name = phoneNumber;

    String[] projection = new String[]{ PhoneLookup.DISPLAY_NAME };
    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
    Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

    try {
      if( cursor.moveToNext() ){
        name = cursor.getString(cursor.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME));
      }
    } finally {
      cursor.close();
    }

    return name;
  }
}
