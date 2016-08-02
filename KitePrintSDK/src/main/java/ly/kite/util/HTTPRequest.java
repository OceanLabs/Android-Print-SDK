/*****************************************************
 *
 * HTTPRequest.java
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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


///// Class Declaration /////

/*****************************************************
 *
 * This class is a generic HTTP request.
 *
 *****************************************************/
public class HTTPRequest
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private   final String   LOG_TAG           = "HTTPRequest";

  static protected final boolean  DEBUGGING_ENABLED = false;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected Context              mApplicationContext;
  protected HttpMethod           mHTTPMethod;
  protected String               mURLString;
  protected Map<String, String>  mHeaderMap;
  protected String               mRequestBodyString;

  private   IResponseListener    mResponseListener;

  private AsyncTask<Void, Void, HTTPRequestResult> mRequestTask;



  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public HTTPRequest( Context context, HttpMethod httpMethod, String urlString, Map<String, String> headerMap, String requestBodyString )
    {
    mApplicationContext = context.getApplicationContext();
    mHTTPMethod         = httpMethod;
    mURLString          = urlString;
    mHeaderMap          = headerMap;
    mRequestBodyString  = requestBodyString;
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets a header.
   *
   *****************************************************/
  protected void setHeader( String name, String value )
    {
    if ( mHeaderMap == null ) mHeaderMap = new HashMap<>();

    mHeaderMap.put( name, value );
    }


  /*****************************************************
   *
   * Starts the request.
   *
   *****************************************************/
  public void start( IResponseListener listener )
    {
    if ( mRequestTask != null )
      {
      throw ( new IllegalStateException( "This HTTP JSON request has already been started" ) );
      }


    // Create and start a new request task

    mResponseListener = listener;

    mRequestTask = new RequestTask();

    mRequestTask.execute();
    }


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
   * Processes the response on a background thread.
   *
   *****************************************************/
  protected void processResponseInBackground( HttpResponse response ) throws Exception
    {
    }


  /*****************************************************
   *
   * Called on the UI thread when a successful response is
   * received.
   *
   *****************************************************/
  protected void onResponseSuccess( int httpStatusCode )
    {
    if ( mResponseListener != null ) mResponseListener.onSuccess( httpStatusCode );
    }


  /*****************************************************
   *
   * Called on the UI thread when an error response is
   * obtained.
   *
   *****************************************************/
  protected void onResponseError( Exception exception )
    {
    if ( mResponseListener != null ) mResponseListener.onError( exception );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An HTTP method.
   *
   *****************************************************/
  public enum HttpMethod
    {
      POST  ( "POST"  ),
      GET   ( "GET"   ),
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
  static protected class HttpPatch extends HttpPost
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
   * A response to an HTTP request.
   *
   *****************************************************/
  static private class HTTPRequestResult
    {
    private int        httpStatusCode;
    private Exception  exception;
    }


  /*****************************************************
   *
   * A listener interface for the result of a request.
   *
   *****************************************************/
  public interface IResponseListener
    {
    void onSuccess( int httpStatusCode );
    void onError  ( Exception exception );
    }


  /*****************************************************
   *
   * A request task.
   *
   *****************************************************/
  private class RequestTask extends AsyncTask<Void, Void, HTTPRequestResult>
    {
    @Override
    protected HTTPRequestResult doInBackground( Void... voids )
      {
      HTTPRequestResult httpRequestResult = new HTTPRequestResult();

      HttpClient httpclient = new DefaultHttpClient();
      HttpRequestBase request    = null;

      if ( mHTTPMethod == HttpMethod.GET )
        {
        request = new HttpGet( mURLString );
        }
      else if ( mHTTPMethod == HttpMethod.POST || mHTTPMethod == HttpMethod.PATCH )
        {
        HttpPost postReq = ( mHTTPMethod == HttpMethod.POST ? new HttpPost( mURLString ) : new HttpPatch( mURLString ) );

        postReq.setHeader( "Content-Type", "application/json; charset=utf-8" );

        if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "Request body: " + mRequestBodyString );

        try
          {
          postReq.setEntity( new StringEntity( mRequestBodyString, "utf-8" ) );
          }
        catch ( UnsupportedEncodingException e )
          {
          httpRequestResult.exception = e;

          return httpRequestResult;
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


      if ( DEBUGGING_ENABLED )
        {
        Log.d( LOG_TAG, "URI: " + request.getURI() );

        Header[] headerArray = request.getAllHeaders();

        for ( Header header : headerArray )
          {
          Log.d( LOG_TAG, header.getName() + " : " + header.getValue() );
          }
        }


      String bodyJSONString = null;

      try
        {
        HttpResponse response = httpclient.execute( request );

        httpRequestResult.httpStatusCode = response.getStatusLine().getStatusCode();

        processResponseInBackground( response );
        }
      catch ( Exception exception )
        {
        Log.e( LOG_TAG, "Unable to process response", exception );

        httpRequestResult.exception = exception;
        }


      return ( httpRequestResult );
      }


    @Override
    protected void onPostExecute( HTTPRequestResult response )
      {
      if ( isCancelled() ) return;

      if ( response.exception != null )
        {
        onResponseError( response.exception );
        }
      else
        {
        onResponseSuccess( response.httpStatusCode );
        }
      }
    }

  }

