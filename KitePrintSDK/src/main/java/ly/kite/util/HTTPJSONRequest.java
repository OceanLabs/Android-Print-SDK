/*****************************************************
 *
 * HTTPJSONRequest.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2016 Kite Tech Ltd. https://www.kite.ly
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

package ly.kite.util;


///// Import(s) /////

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

import ly.kite.KiteSDKException;
import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a generic HTTP request that returns
 * a JSON body response.
 *
 *****************************************************/
public class HTTPJSONRequest extends HTTPRequest
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "HTTPJSONRequest";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private IJSONResponseListener  mJSONResponseListener;

  private JSONObject             mJSONResponse;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public HTTPJSONRequest( Context context, HttpMethod httpMethod, String urlString, Map<String, String> headerMap, String requestBodyString )
    {
    super( context, httpMethod, urlString, headerMap, requestBodyString );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Starts the request.
   *
   *****************************************************/
  public void start( IJSONResponseListener listener )
    {
    mJSONResponseListener = listener;

    super.start( null );
    }


  /*****************************************************
   *
   * Processes the response on a background thread.
   *
   *****************************************************/
  @Override
  protected void processResponseInBackground( HttpResponse response ) throws Exception
    {
    BufferedReader reader = new BufferedReader( new InputStreamReader( response.getEntity().getContent(), "UTF-8" ) );
    StringBuilder builder = new StringBuilder();
    for ( String line = null; ( line = reader.readLine() ) != null; )
      {
      builder.append( line ).append( "\n" );
      }


    // If we get a body - parse it as JSON. Some endpoints don't return anything, so
    // if this happens we just create an empty JSON object.

    String bodyJSONString = builder.toString();

    if ( ! bodyJSONString.trim().equals( "" ) )
      {
      try
        {
        JSONTokener tokener = new JSONTokener( bodyJSONString );

        mJSONResponse = new JSONObject( tokener );
        }
      catch ( JSONException je )
        {
        // Display the body in the log. Sometimes the server returns non-JSON, which
        // we want to see.
        Log.e( LOG_TAG, "Unable to parse response as JSON:\n" + bodyJSONString, je );

        // If the body is HTML rather than JSON, try and come up with a more user-friendly
        // error message.
        if ( bodyJSONString.contains( "<!DOCTYPE html>" ) )
          {
          if ( bodyJSONString.contains( "Offline for Maintenance" ) )
            {
            throw ( new KiteSDKException( mApplicationContext.getString( R.string.kitesdk_alert_dialog_message_server_offline_maintenance) ) );
            }
          else
            {
            throw ( new KiteSDKException( mApplicationContext.getString( R.string.kitesdk_alert_dialog_message_server_returned_html) ) );
            }
          }
        else
          {
          // Re-throw the exception
          throw ( je );
          }
        }
      }
    else
      {
      mJSONResponse = new JSONObject();
      }
    }


  /*****************************************************
   *
   * Called on the UI thread when a successful response is
   * received.
   *
   *****************************************************/
  protected void onResponseSuccess( int httpStatusCode )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "onResponseSuccess( httpStatusCode = " + httpStatusCode + " ) JSON = " + mJSONResponse.toString() );

    if ( mJSONResponseListener != null ) mJSONResponseListener.onSuccess( httpStatusCode, mJSONResponse );
    }


  /*****************************************************
   *
   * Called on the UI thread when an error response is
   * obtained.
   *
   *****************************************************/
  protected void onResponseError( Exception exception )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "onResponseError( exception = " + exception + " )" );

    if ( mJSONResponseListener != null ) mJSONResponseListener.onError( exception );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A listener interface for the result of a request.
   *
   *****************************************************/
  public interface IJSONResponseListener
    {
    void onSuccess( int httpStatusCode, JSONObject json );
    void onError  ( Exception exception );
    }

  }

