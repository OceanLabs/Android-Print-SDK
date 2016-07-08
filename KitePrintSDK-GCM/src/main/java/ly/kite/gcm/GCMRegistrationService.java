/*****************************************************
 *
 * GCMRegistrationService.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2015 Kite Tech Ltd. https://www.kite.ly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The software MAY ONLY be used with the Kite Tech Ltd platform and MAY NOT be modified
 * to be used with any competitor platforms. This means the software MAY NOT be modified 
 * to place orders with any competitors to Kite Tech Ltd, all orders MUST go through the
 * Kite Tech Ltd platform servers. 
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.gcm;


///// Import(s) /////

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import ly.kite.KiteSDK;
import ly.kite.util.HTTPJSONRequest;


///// Class Declaration /////

/*****************************************************
 *
 * This service registers for GCM.
 *
 *****************************************************/
public class GCMRegistrationService extends IntentService implements HTTPJSONRequest.IJSONResponseListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  private static final String TAG                                              = "GCMRegistrationService";

  private static final String SHARED_PREFERENCES_NAME                          = "kite_sdk_gcm_shared_prefs";
  private static final String SHARED_PREFERENCES_KEY_GCM_REGISTRATION_REQUIRED = "gcm_registration_required";

  private static final String USER_REQUEST_FORMAT_STRING                       = "%s/person/";

  private static final String JSON_NAME_ENVIRONMENT                            = "environment";
  private static final String JSON_NAME_PLATFORM                               = "platform";
  private static final String JSON_NAME_PUSH_TOKEN                             = "push_token";
  private static final String JSON_NAME_SET                                    = "set";
  private static final String JSON_NAME_TOKEN                                  = "token";
  private static final String JSON_NAME_UUID                                   = "uuid";

  private static final String JSON_VALUE_ANDROID                               = "Android";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private HTTPJSONRequest  mHTTPJSONRequest;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Starts this service.
   *
   *****************************************************/
  static public void start( Context context )
    {
    Intent intent = new Intent( context, GCMRegistrationService.class );

    context.startService( intent );
    }


  ////////// Constructor(s) //////////

  public GCMRegistrationService()
    {
    super( TAG );
    }


  ////////// IntentService Method(s) //////////

  /*****************************************************
   *
   * Handles an intent.
   *
   *****************************************************/
  @Override
  protected void onHandleIntent( Intent intent )
    {
    // Check if GCM is required

    String gcmSenderId = getString( R.string.gcm_sender_id );

    if ( gcmSenderId == null || gcmSenderId.trim().equals( "" ) ) return;


    // Check if we need to register

    SharedPreferences sharedPreferences = getSharedPreferences();

    boolean registrationRequired = sharedPreferences.getBoolean( SHARED_PREFERENCES_KEY_GCM_REGISTRATION_REQUIRED, true );

    if ( ! registrationRequired ) return;


    try
      {
      // Initially this call goes out to the network to retrieve the token, subsequent calls
      // are local.

      InstanceID instanceID = InstanceID.getInstance( this );

      String token = instanceID.getToken( gcmSenderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null );

      Log.i( TAG, "Got GCM registration token: " + token );


      sendRegistrationToServer( token );
      }
    catch ( Exception exception )
      {
      Log.e( TAG, "Failed to complete token refresh", exception );


      // If an exception happens while fetching the new token or updating our registration data
      // on a third-party server, this ensures that we'll attempt the update at a later time.

      saveRegistrationRequired( true );
      }

    // We don't need to perform any notification because this is a background process and we
    // don't care if it fails.
    }


  ////////// HTTPJSONRequest Method(s) //////////

  /*****************************************************
   *
   * Called when the HTTP request succeeds.
   *
   *****************************************************/
  @Override
  public void onSuccess( int httpStatusCode, JSONObject json )
    {
    Log.i( TAG, "Registered GCM token OK" );

    saveRegistrationRequired( false );

    mHTTPJSONRequest = null;
    }


  /*****************************************************
   *
   * Called when the HTTP request fails.
   *
   *****************************************************/
  @Override
  public void onError( Exception exception )
    {
    Log.e( TAG, "Failed to register GCM token", exception );

    saveRegistrationRequired( true );

    mHTTPJSONRequest = null;
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the shared preferences.
   *
   *****************************************************/
  private SharedPreferences getSharedPreferences()
    {
    return ( getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE ) );
    }


  /*****************************************************
   *
   * Sends registration information to the Kite server.
   *
   *****************************************************/
  private void sendRegistrationToServer( String token ) throws JSONException
    {
    // Make sure we are not already calling the endpoint
    if ( mHTTPJSONRequest != null ) return;


    // Create the JSON body

    JSONObject jsonPushTokenObject = new JSONObject();
    jsonPushTokenObject.put( JSON_NAME_PLATFORM, JSON_VALUE_ANDROID );
    jsonPushTokenObject.put( JSON_NAME_TOKEN,    token );

    JSONObject jsonSetObject = new JSONObject();
    jsonSetObject.put( JSON_NAME_PUSH_TOKEN,  jsonPushTokenObject );
    jsonSetObject.put( JSON_NAME_PLATFORM,    JSON_VALUE_ANDROID );
    jsonSetObject.put( JSON_NAME_ENVIRONMENT, KiteSDK.getInstance( this ).getEnvironment().toString() );

    JSONObject jsonBodyObject = new JSONObject();
    jsonBodyObject.put( JSON_NAME_UUID, KiteSDK.getInstance( this ).getUniqueUserId() );
    jsonBodyObject.put( JSON_NAME_SET, jsonSetObject );


    // Create and start the request

    String requestURLString = String.format( USER_REQUEST_FORMAT_STRING, KiteSDK.getInstance( this ).getAPIEndpoint() );

    mHTTPJSONRequest = new HTTPJSONRequest( this, HTTPJSONRequest.HttpMethod.POST, requestURLString, null, jsonBodyObject.toString() );

    mHTTPJSONRequest.start( this );
    }


  /*****************************************************
   *
   * Saves a flag indicating whether registration is required
   * in the future.
   *
   *****************************************************/
  private void saveRegistrationRequired( boolean registrationRequired )
    {
    getSharedPreferences()
      .edit()
        .putBoolean( SHARED_PREFERENCES_KEY_GCM_REGISTRATION_REQUIRED, registrationRequired )
      .apply();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

