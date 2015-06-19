/*****************************************************
 *
 * ProductSyncer.java
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

package ly.kite.print;


///// Import(s) /////

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


///// Class Declaration /////

/****************************************************
 *
 * This class is used to retrieve a list of available
 * products from the server API.
 *
 * Created by alibros on 06/01/15.
 *
 ****************************************************/
public class ProductSyncer implements BaseRequest.BaseRequestListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  private static final String LOG_TAG               = "ProductSyncer";

  private static final String REQUEST_FORMAT_STRING = "%s/template/?limit=100";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private SyncListener  mListener;
  private BaseRequest   mBaseRequest;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// BaseRequest.BaseRequestListener Method(s) //////////

  /****************************************************
   *
   * Called when a request completes successfully.
   *
   ****************************************************/
  @Override
  public void onSuccess( int httpStatusCode, JSONObject jsonData )
    {

    try
      {
      // Check if we got a valid HTTP response code

      if ( httpStatusCode >= 200 && httpStatusCode <= 299 )
        {
        // Try to get an array of products, then go through each of them.

        JSONArray productJSONArray = jsonData.getJSONArray( "objects" );

        ArrayList<Product> productList = new ArrayList<Product>();

        for ( int productIndex = 0; productIndex < productJSONArray.length(); productIndex ++ )
          {
          // Parse the product data to create a Product object, and then add it to our list.

          try
            {
            Product product = Product.parseTemplate( productJSONArray.getJSONObject( productIndex ) );

            productList.add( product );
            }
          catch ( JSONException ignore )
            {
            // Ignore individual errors - try and get as many products as possible
            }
          }

        mListener.onSyncComplete( productList );
        }
      else
        {
        // Invalid HTTP response code - see if we can get an error message

        JSONObject errorJSONObject = jsonData.getJSONObject( BaseRequest.ERROR_RESPONSE_JSON_OBJECT_NAME );

        String     errorMessage    = errorJSONObject.getString( BaseRequest.ERROR_RESPONSE_MESSAGE_JSON_NAME );
        String     errorCode       = errorJSONObject.getString( BaseRequest.ERROR_RESPONSE_CODE_JSON_NAME );

        mListener.onError( new KitePrintSDKException( errorMessage ) );
        }

      }
    catch ( JSONException je )
      {
      mListener.onError( je );
      }

    }


  /****************************************************
   *
   * Called when a request fails with an error.
   *
   ****************************************************/
  @Override
  public void onError( Exception exception )
    {
    mListener.onError( exception );
    }


  ////////// Method(s) //////////

  /****************************************************
   *
   * Syncs the available Products.
   *
   * @param listener The sync listener for the result.
   *
   ****************************************************/
  public void sync( final SyncListener listener )
    {
    // Verify that no other sync has been started
    assert mBaseRequest == null : "you can only submit a request once";

    // Save the listener
    mListener = listener;


    // Create the request and initiate it

    String url = String.format( REQUEST_FORMAT_STRING, KitePrintSDK.getEnvironment().getPrintAPIEndpoint() );

    mBaseRequest = new BaseRequest( BaseRequest.HttpMethod.GET, url, null, null );

    mBaseRequest.start( this );
    }



  /****************************************************
   *
   * Cancels the request.
   *
   ****************************************************/
  public void cancelSubmissionForPrinting()
    {
    if ( mBaseRequest != null )
      {
      mBaseRequest.cancel();
      mBaseRequest = null;
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * This interface defines a listener to the result of
   * a products request.
   *
   * Created by alibros on 06/01/15.
   *
   *****************************************************/
  public interface SyncListener
    {
    ////////// Static Constant(s) //////////


    ////////// Method(s) //////////

    /*****************************************************
     *
     * Called when a request completes successfully.
     *
     * @param productList A list of products returned from
     *                    the server.
     *
     *****************************************************/
    void onSyncComplete( ArrayList<Product> productList );


    /*****************************************************
     *
     * Called when a request results in an error.
     *
     * @param error The exception that was thrown.
     *
     *****************************************************/
    void onError( Exception error );
    }

  }
