/*****************************************************
 *
 * HTTPJSONRequest.java
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

package ly.kite.util;


///// Import(s) /////

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

import ly.kite.BuildConfig;
import ly.kite.KiteSDK;


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
public class HTTPJSONRequest
  {
  ////////// Static Constant(s) //////////

  static private final String LOG_TAG                          = "HTTPJSONRequest";

  static  public final String ERROR_RESPONSE_JSON_OBJECT_NAME  = "error";
  static  public final String ERROR_RESPONSE_MESSAGE_JSON_NAME = "message";
  static  public final String ERROR_RESPONSE_CODE_JSON_NAME    = "code";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private final Context                                  mContext;
  private final HttpMethod                               mHTTPMethod;
  private final String                                   mURLString;
  private final Map<String, String>                      mHeaderMap;
  private final String                                   mRequestBodyString;

  private       AsyncTask<Void, Void, JSONHttpResponse>  mRequestTask;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public HTTPJSONRequest( Context context, HttpMethod httpMethod, String urlString, Map<String, String> headerMap, String requestBodyString )
    {
    mContext           = context;
    mHTTPMethod        = httpMethod;
    mURLString         = urlString;
    mHeaderMap         = headerMap;
    mRequestBodyString = requestBodyString;
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Cancels the request.
   *
   *****************************************************/
  public void cancel()
    {
    if ( mRequestTask != null )
      {
      mRequestTask.cancel( true );

      mRequestTask = null;
      }
    }


  /*****************************************************
   *
   * Starts the request.
   *
   *****************************************************/
  public void start( HTTPJSONRequestListener listener )
    {
    if ( mRequestTask != null )
      {
      throw ( new IllegalStateException( "This HTTP JSON request has already been started" ) );
      }


    // Create and start a new request task

    mRequestTask = new RequestTask( listener );

    mRequestTask.execute();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A listener interface for the result of a request.
   *
   *****************************************************/
  public interface HTTPJSONRequestListener
    {
    void onSuccess( int httpStatusCode, JSONObject json );
    void onError  ( Exception exception );
    }


  /*****************************************************
   *
   * An HTTP method.
   *
   *****************************************************/
  public enum HttpMethod
    {
    POST  ( "POST" ),
    GET   ( "GET" ),
    PATCH ( "PATCH" );


    private final String methodName;


    private HttpMethod( String method )
      {
      this.methodName = method;
      }
    }


  /*****************************************************
   *
   * An HTTP PATCH method.
   *
   *****************************************************/
  static private class HttpPatch extends HttpPost
    {
    static public final String METHOD_PATCH = "PATCH";


    public HttpPatch( final String url )
      {
      super( url );
      }


    @Override
    public String getMethod()
      {
      return ( METHOD_PATCH );
      }
    }


  /*****************************************************
   *
   * A JSON response to an HTTP request.
   *
   *****************************************************/
  static private class JSONHttpResponse
    {
    private Exception   error;
    private int         httpStatusCode;
    private JSONObject  json;
    }


  /*****************************************************
   *
   * A request task.
   *
   *****************************************************/
  private class RequestTask extends AsyncTask<Void, Void, JSONHttpResponse>
    {
    private HTTPJSONRequestListener  mListener;


    RequestTask( HTTPJSONRequestListener listener )
      {
      mListener = listener;
      }


    @Override
    protected JSONHttpResponse doInBackground( Void... voids )
      {
      JSONHttpResponse jsonResponse = new JSONHttpResponse();

      HttpClient      httpclient = new DefaultHttpClient();
      HttpRequestBase request    = null;

      if ( mHTTPMethod == HttpMethod.GET )
        {
        request = new HttpGet( mURLString );
        }
      else if ( mHTTPMethod == HttpMethod.POST || mHTTPMethod == HttpMethod.PATCH )
        {
        HttpPost postReq = ( mHTTPMethod == HttpMethod.POST ? new HttpPost( mURLString ) : new HttpPatch( mURLString ) );

        postReq.setHeader( "Content-Type", "application/json; charset=utf-8" );

        try
          {
          postReq.setEntity( new StringEntity( mRequestBodyString, "utf-8" ) );
          }
        catch ( UnsupportedEncodingException e )
          {
          jsonResponse.error = e;
          return jsonResponse;
          }

        request = postReq;
        }

      if ( mHeaderMap != null )
        {
        for ( Map.Entry<String, String> entry : mHeaderMap.entrySet() )
          {
          request.setHeader( entry.getKey(), entry.getValue() );
          }
        }

      KiteSDK kiteSDK = KiteSDK.getInstance( mContext );

      request.setHeader( "Authorization", "ApiKey " + kiteSDK.getAPIKey() + ":" );
      request.setHeader( "User-Agent", "Kite SDK Android v" + BuildConfig.VERSION_NAME );
      request.setHeader( "X-App-Package", mContext.getPackageName() );
      request.setHeader( "X-App-Name", mContext.getString( mContext.getApplicationInfo().labelRes ) );
      request.setHeader( "X-Person-UUID", kiteSDK.getUniqueUserId() );


      String languageCode = Locale.getDefault().getLanguage();

      if ( languageCode != null && ( ! languageCode.trim().equals( "" ) ) )
        {
        request.setHeader( "Accept-Language", languageCode );
        }


      String bodyJSONString = null;

      try
        {
        HttpResponse response = httpclient.execute( request );
        BufferedReader reader = new BufferedReader( new InputStreamReader( response.getEntity().getContent(), "UTF-8" ) );
        StringBuilder builder = new StringBuilder();
        for ( String line = null; ( line = reader.readLine() ) != null; )
          {
          builder.append( line ).append( "\n" );
          }


        // If we get a body - parse it as JSON. Some endpoints don't return anything, so
        // if this happens we just create an empty JSON object.

        bodyJSONString = builder.toString();

        if ( ! bodyJSONString.trim().equals( "" ) )
          {
          JSONTokener tokener = new JSONTokener( bodyJSONString );

          jsonResponse.json = new JSONObject( tokener );
          }
        else
          {
          jsonResponse.json = new JSONObject();
          }

        jsonResponse.httpStatusCode = response.getStatusLine().getStatusCode();
        }
      catch ( Exception exception )
        {
        Log.e( LOG_TAG, "Unable to parse body: " + bodyJSONString, exception );

        jsonResponse.error = exception;
        }


      return ( jsonResponse );
      }


    @Override
    protected void onPostExecute( JSONHttpResponse response )
      {
      if ( isCancelled() ) return;

      if ( response.error != null )
        {
        mListener.onError( response.error );
        }
      else
        {
        mListener.onSuccess( response.httpStatusCode, response.json );
        }
      }
    }

  }