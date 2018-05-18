/*****************************************************
 *
 * MixpanelAgent.java
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

package ly.kite.analytics;


///// Import(s) /////

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import ly.kite.KiteSDK;
import ly.kite.util.HTTPJSONRequest;


///// Class Declaration /////

/*****************************************************
 *
 * This class uploads analytics data to the Mixpanel
 * API.
 *
 *****************************************************/
public class MixpanelAgent implements HTTPJSONRequest.IJSONResponseListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG             = "MixpanelAgent";

  private static final String  ENDPOINT_URL_STRING = "https://api.mixpanel.com/track/";
  public  static final String  API_TOKEN           = "cdf64507670dd359c43aa8895fb87676";  // Live
  //public  static final String  API_TOKEN           = "e08854f70bc6a97b9f14457cbbb29b24";  // JL Test

  ////////// Static Variable(s) //////////

  private static MixpanelAgent  sMixpanelAgent;


  ////////// Member Variable(s) //////////

  private Context  mContext;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns a singleton instance of this class.
   *
   *****************************************************/
  public static MixpanelAgent getInstance( Context context )
    {
    if ( sMixpanelAgent == null )
      {
      sMixpanelAgent = new MixpanelAgent( context );
      }

    return ( sMixpanelAgent );
    }


  ////////// Constructor(s) //////////

  private MixpanelAgent( Context context )
    {
    mContext = context;
    }


  ////////// BaseRequest.BaseRequestListener Method(s) //////////

  /*****************************************************
   *
   * Called when a request succeeds.
   *
   *****************************************************/
  @Override
  public void onSuccess( int httpStatusCode, JSONObject json )
    {
    // Ignore
    }


  /*****************************************************
   *
   * Called when a request fails.
   *
   *****************************************************/
  @Override
  public void onError( Exception exception )
    {
    Log.e( LOG_TAG, "Mixpanel request failed", exception );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Tracks an event.
   *
   *****************************************************/
  public void trackEvent( JSONObject eventJSONObject )
    {

    // If analytics hasn't been turned on, no events are sent

    if (!KiteSDK.getInstance(mContext).getKiteAnalyticsEnabled()) { return; }

    // The JSON needs to be encoded as Base64

    byte[] jsonBytes = eventJSONObject.toString().getBytes();

    //Log.d( TAG, "JSON request:\n" + eventJSONObject.toString() );

    String base64EncodedJSON = Base64.encodeToString( jsonBytes, Base64.NO_WRAP | Base64.URL_SAFE );


    // Perform the HTTP request

    String requestURLString = ENDPOINT_URL_STRING + "?ip=1&data=" + base64EncodedJSON;

    try
      {
      URL requestURL = new URL( requestURLString );

      new Thread( new HTTPRequest( requestURL ) ).start();
      }
    catch ( MalformedURLException mue )
      {
      Log.e( LOG_TAG, "Invalid URL: " + requestURLString, mue );
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Makes an HTTP request.
   *
   *****************************************************/
  private class HTTPRequest implements Runnable
    {
    private URL  mURL;


    HTTPRequest( URL url )
      {
      mURL = url;
      }


    @Override
    public void run()
      {
      // Open a connection to the URL

      try
        {
        URLConnection urlConnection = mURL.openConnection();

        if ( urlConnection instanceof HttpURLConnection )
          {
          HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;

          //httpURLConnection.setRequestMethod( HttpURLConnection.HTTP_GET );

          int statusCode = httpURLConnection.getResponseCode();

          if ( statusCode != 200 )
            {
            Log.e( LOG_TAG, "Invalid response code: " + statusCode );
            }

          httpURLConnection.disconnect();
          }
        else
          {
          Log.e( LOG_TAG, "Invalid connection type: " + urlConnection + ", expected HttpURLConnection" );
          }
        }
      catch ( IOException ioe )
        {
        Log.e( LOG_TAG, "Unable to open connection to " + mURL.toString(), ioe );
        }

      }
    }

  }

