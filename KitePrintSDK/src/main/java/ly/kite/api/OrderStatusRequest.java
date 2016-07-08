/*****************************************************
 *
 * OrderStatusRequest.java
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


///// Class Declaration /////

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import ly.kite.KiteSDK;
import ly.kite.util.HTTPJSONRequest;

/*****************************************************
 *
 * This class makes an API request for the status of
 * an order.
 *
 *****************************************************/
public class OrderStatusRequest implements HTTPJSONRequest.IJSONResponseListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                     = "OrderStatusRequest";

  static private final String  URL_FORMAT_STRING           = "%s/order/%s";

  static private final String  JSON_NAME_ERROR             = "error";
  static private final String  JSON_NAME_CODE              = "code";
  static private final String  JSON_NAME_MESSAGE           = "message";
  static private final String  JSON_NAME_STATUS            = "status";
  static private final String  JSON_NAME_ORDER_ID          = "order_id";
  static private final String  JSON_NAME_ORIGINAL_ORDER_ID = "print_order_id";

  static private final String  ERROR_CODE_DUPLICATE_ORDER = "E20";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context          mContext;
  private IResultListener  mListener;

  private String           mLastRequestedOrderId;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public OrderStatusRequest( Context context, IResultListener listener )
    {
    mContext  = context;
    mListener = listener;
    }


  ////////// HTTPJSONRequest.HTTPJSONRequestListener Method(s) //////////

  /*****************************************************
   *
   * Called when the request returns successfully.
   *
   *****************************************************/
  @Override
  public void onSuccess( int httpStatusCode, JSONObject jsonObject )
    {
    //Log.d( LOG_TAG, "onSuccess( httpStatusCode = " + httpStatusCode + ", jsonObject = " + jsonObject.toString() + " )" );

    if ( httpStatusCode >= 200 && httpStatusCode <= 299 )
      {
      try
        {
        // Verify that the order id matches the one we requested

        String orderId = jsonObject.optString( JSON_NAME_ORDER_ID );

        if ( orderId == null || ! orderId.equals( mLastRequestedOrderId ) )
          {
          returnError( "Response order id ( " + orderId + " ) does not match requested order id: " + mLastRequestedOrderId );

          return;
          }


        // See if there is an error

        JSONObject errorJSONObject = jsonObject.optJSONObject( JSON_NAME_ERROR );

        if ( errorJSONObject != null )
          {
          String errorCode    = errorJSONObject.getString( JSON_NAME_CODE );
          String errorMessage = errorJSONObject.getString( JSON_NAME_MESSAGE );

          // Check for special case of duplicate order
          if ( errorCode != null && errorCode.equals( ERROR_CODE_DUPLICATE_ORDER ) )
            {
            String originalOrderId = errorJSONObject.getString( JSON_NAME_ORIGINAL_ORDER_ID );

            returnError( ErrorType.DUPLICATE, originalOrderId, errorMessage );
            }
          else
            {
            returnError( errorMessage );
            }
          }
        else
          {
          // We couldn't find an error, so decode the order status

          String status = jsonObject.getString( JSON_NAME_STATUS );

          OrderState state = OrderState.fromJSONValue( status );

          if ( state != null )
            {
            returnState( state );
            }
          else
            {
            returnError( "Invalid order status: " + status );
            }
          }
        }
      catch ( JSONException je )
        {
        returnError( "Unable to parse JSON: " + jsonObject.toString() );
        }
      }
    else
      {
      returnError( "Invalid HTTP status code: " + httpStatusCode );
      }

    }


  /*****************************************************
   *
   * Called when there is an error.
   *
   *****************************************************/
  @Override
  public void onError( Exception exception )
    {
    returnError( exception );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Makes a status request.
   *
   *****************************************************/
  public void start( String orderId )
    {
    mLastRequestedOrderId = orderId;

    String url = String.format( URL_FORMAT_STRING, KiteSDK.getInstance( mContext ).getAPIEndpoint(), orderId );

    KiteAPIRequest request = new KiteAPIRequest( mContext, KiteAPIRequest.HttpMethod.GET, url, null, null );

    request.start( this );
    }


  /*****************************************************
   *
   * Returns an order state to the listener.
   *
   *****************************************************/
  private void returnState( OrderState state )
    {
    if ( mListener != null ) mListener.osOnSuccess( this, state );
    }


  /*****************************************************
   *
   * Returns an error to the listener.
   *
   *****************************************************/
  private void returnError( ErrorType type, String originalOrderId, Exception exception )
    {
    if ( mListener != null ) mListener.osOnError( this, type, originalOrderId, exception );
    }


  /*****************************************************
   *
   * Returns an error to the listener.
   *
   *****************************************************/
  private void returnError( Exception exception )
    {
    returnError( ErrorType.OTHER, null, exception );
    }


  /*****************************************************
   *
   * Returns an error to the listener.
   *
   *****************************************************/
  private void returnError( ErrorType type, String originalOrderId, String message )
    {
    returnError( type, originalOrderId, new Exception( message ) );
    }


  /*****************************************************
   *
   * Returns an error to the listener.
   *
   *****************************************************/
  private void returnError( String message )
    {
    returnError( ErrorType.OTHER, null, message );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An error type.
   *
   *****************************************************/
  public enum ErrorType
    {
    DUPLICATE,
    OTHER
    }


  /*****************************************************
   *
   * A result listener.
   *
   *****************************************************/
  public interface IResultListener
    {
    public void osOnSuccess( OrderStatusRequest request, OrderState state );
    public void osOnError( OrderStatusRequest request, ErrorType errorType, String originalOrderId, Exception exception );
    }

  }
