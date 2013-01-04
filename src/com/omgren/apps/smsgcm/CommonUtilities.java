/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.omgren.apps.smsgcm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {

    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    static final String SERVER_URL = "http://awesome.omgren.com:8080/gcm-demo";

    /**
     * Google API project id registered to use GCM.
     */
    static final String SENDER_ID = "1034760575801";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "SMSGCM";

    /**
     * Intent used to display a message in the screen.
     */
    static final String DISPLAY_MESSAGE_ACTION =
            "com.omgren.apps.smsgcm.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
    
    static public String httpDownloader(String u){
    	BufferedReader buf = null;
    	try {
    		URL url = new URL(u);
    		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    		buf = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
    		
    		StringBuffer sb = new StringBuffer();
    		String buffer;
    		while((buffer = buf.readLine()) != null)
    			sb.append(buffer);
    		
    		urlConnection.disconnect();
    		buf.close();
    		
    		return sb.toString();
    		
    	} catch (MalformedURLException e) {
    		Log.e(TAG, "bad url: " + u);
    	} catch (IOException e){
    		Log.e(TAG, "IOException");
    	} finally {
    	}
    	
    	return null;
    }
    
}
