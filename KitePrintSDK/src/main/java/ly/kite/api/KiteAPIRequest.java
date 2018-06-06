/*****************************************************
 *
 * KiteAPIRequest.java
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

package ly.kite.api;


///// Import(s) /////

import android.content.Context;

import java.util.Locale;
import java.util.Map;

import ly.kite.BuildConfig;
import ly.kite.KiteSDK;
import ly.kite.util.HTTPJSONRequest;


///// Class Declaration /////

/**
 * Created by deonbotha on 02/02/2014.
 */

/*****************************************************
 *
 * This class is the basis for all HTTP JSON requests to
 * the Kite API server.
 *
 *****************************************************/
public class KiteAPIRequest extends HTTPJSONRequest
  {
  ////////// Static Constant(s) //////////

  static private final String LOG_TAG                          = "KiteAPIRequest";

  static  public final String ERROR_RESPONSE_JSON_OBJECT_NAME  = "error";
  static  public final String ERROR_RESPONSE_MESSAGE_JSON_NAME = "message";
  static  public final String ERROR_RESPONSE_CODE_JSON_NAME    = "code";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public KiteAPIRequest( Context context, HttpMethod httpMethod, String urlString, Map<String, String> headerMap, String requestBodyString )
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
    // Add Kite headers to the request

    KiteSDK kiteSDK = KiteSDK.getInstance( mApplicationContext );

    setHeader( "Authorization", "ApiKey " + kiteSDK.getAPIKey() + ":" );
    setHeader( "User-Agent", "Kite SDK Android v" + KiteSDK.SDK_VERSION );
    setHeader( "X-App-Package", mApplicationContext.getPackageName() );
    setHeader( "X-App-Name", mApplicationContext.getString( mApplicationContext.getApplicationInfo().labelRes ) );
    setHeader( "X-Person-UUID", kiteSDK.getUniqueUserId() );


    String languageCode = Locale.getDefault().getLanguage();

    if ( languageCode != null && ( ! languageCode.trim().equals( "" ) ) )
      {
      setHeader( "Accept-Language", languageCode );
      }


    super.start( listener );
    }


  ////////// Inner Class(es) //////////


  }