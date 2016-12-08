/*****************************************************
 *
 * OrderSubmitter.java
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

package ly.kite.checkout;


///// Import(s) /////

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import ly.kite.KiteSDK;
import ly.kite.api.OrderState;
import ly.kite.api.OrderStatusRequest;
import ly.kite.ordering.IOrderSubmissionSuccessListener;
import ly.kite.ordering.Order;


///// Class Declaration /////

/*****************************************************
 *
 * This class submits an order, and then monitors its
 * progress, reporting back to a listener.
 *
 *****************************************************/
public class OrderSubmitter implements Order.ISubmissionProgressListener,
                                       OrderStatusRequest.IResultListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                 = "OrderSubmitter";

  static private final boolean DEBUGGING_ENABLED       = false;

  static private final long    POLLING_TIMEOUT_MILLIS  = 1000 * 20;  // 20 seconds
  static private final long    POLLING_INTERVAL_MILLIS = 1000 * 2;   //  2 seconds


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context                           mApplicationContext;
  private Order                             mOrder;
  private IOrderSubmissionProgressListener  mProgressListener;

  private Handler                           mHandler;

  private long                              mPollingStartElapsedRealtimeMillis;
  private long                              mPollingEndElapsedRealtimeMillis;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public OrderSubmitter( Context context, Order order, IOrderSubmissionProgressListener progressListener )
    {
    mApplicationContext = context.getApplicationContext();
    mOrder              = order;
    mProgressListener   = progressListener;

    mHandler            = new Handler();
    }


  ////////// IPrintOrderSubmissionListener Method(s) //////////

  /*****************************************************
   *
   * Called with progress during the image upload.
   *
   *****************************************************/
  @Override
  public void onProgress( Order order, int primaryProgressPercent, int secondaryProgressPercent )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "onProgress( order = " + order + ", primaryProgressPercent = " + primaryProgressPercent + ", secondaryProgressPercent = " + secondaryProgressPercent + " )" );

    mProgressListener.onOrderUpdate( mOrder, OrderState.UPLOADING, primaryProgressPercent, secondaryProgressPercent );
    }


  /*****************************************************
   *
   * Called when the order has been posted and we have
   * received an order id.
   *
   * The order id will have already been attached to the
   * order at this point.
   *
   *****************************************************/
  @Override
  public void onSubmissionComplete( Order order, String orderId )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "onSubmissionComplete( order = " + order + ", orderId = " + orderId + " )" );

    mProgressListener.onOrderUpdate( mOrder, OrderState.POSTED, 0, 0 );

    // The order has been submitted, so start polling for the order status
    startPollingOrderStatus();
    }


  /*****************************************************
   *
   * Called when there is an error during order posting.
   *
   *****************************************************/
  @Override
  public void onError( Order order, Exception exception )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "onError( order = " + order + ", exception = " + exception + " )" );

    mProgressListener.onOrderError( order, exception );
    }


  ////////// OrderStatusRequest.IResultListener Method(s) //////////

  /*****************************************************
   *
   * Called when the order status has been successfully
   * retrieved.
   *
   *****************************************************/
  @Override
  public void osOnSuccess( OrderStatusRequest request, OrderState state )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "osOnSuccess( request = " + request + ", state = " + state  + " )" );

    // If the order is in one of these states - stop polling
    if ( state == OrderState.VALIDATED ||
         state == OrderState.PROCESSED ||
         state == OrderState.CANCELLED )
      {
      onOrderComplete( mOrder, state );
      }
    else
      {
      // If we have timed out - inform the listener and stop
      if ( SystemClock.elapsedRealtime() >= mPollingEndElapsedRealtimeMillis )
        {
        mProgressListener.onOrderTimeout( mOrder );
        }
      else
        {
        // Update the listener with the intermediate state
        mProgressListener.onOrderUpdate( mOrder, state, 0, 0 );

        // The order is not complete but we haven't timed out, so re-poll after a delay
        mHandler.postDelayed( new PollStatusRunnable(), POLLING_INTERVAL_MILLIS );
        }
      }
    }


  /*****************************************************
   *
   * Called when there was an error retrieving the order
   * status.
   *
   *****************************************************/
  @Override
  public void osOnError( OrderStatusRequest request, OrderStatusRequest.ErrorType errorType, String originalOrderId, Exception exception )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "osOnError( request = " + request + ", errorType = " + errorType + ", originalOrderId = " + originalOrderId + ", exception = " + exception + " )" );

    // Check the type of error

    if ( errorType == OrderStatusRequest.ErrorType.DUPLICATE )
      {
      mProgressListener.onOrderDuplicate( mOrder, originalOrderId );
      }
    else
      {
      // Clear the receipt (which will have been set by the initial
      // successful submission), and set the error.
      mOrder.setError( exception);

      mProgressListener.onOrderError( mOrder, exception );
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Submits the order.
   *
   *****************************************************/
  void submit()
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "submit()" );

    // If the order already has an id (from the server) then
    // we don't want to resubmit it.

    if ( mOrder.getReceipt() == null )
      {
      mOrder.submitForPrinting( mApplicationContext, this );
      }
    else
      {
      startPollingOrderStatus();
      }
    }


  /*****************************************************
   *
   * Starts polling for the order status.
   *
   *****************************************************/
  private void startPollingOrderStatus()
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "startPollingOrderStatus()" );

    // Save the time at which we started polling
    mPollingStartElapsedRealtimeMillis = SystemClock.elapsedRealtime();
    mPollingEndElapsedRealtimeMillis   = mPollingStartElapsedRealtimeMillis + POLLING_TIMEOUT_MILLIS;

    pollOrderStatus();
    }


  /*****************************************************
   *
   * Retrieves (polls) the order status.
   *
   *****************************************************/
  private void pollOrderStatus()
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "pollOrderStatus()" );

    new OrderStatusRequest( mApplicationContext, this ).start( mOrder.getReceipt() );
    }


  /*****************************************************
   *
   * Called to return order completion.
   *
   *****************************************************/
  private void onOrderComplete( Order order, OrderState state )
    {
    // Callback to the progress listener
    mProgressListener.onOrderComplete( mOrder, state );


    // If the order was completed successfully - callback to any
    // customiser-registered listener.

    if ( state == OrderState.VALIDATED || state == OrderState.PROCESSED )
      {
      IOrderSubmissionSuccessListener listener = KiteSDK
              .getInstance( mApplicationContext )
                .getCustomiser()
                  .getOrderSubmissionSuccessListener();

      if ( listener != null )
        {
        Order sanitisedOrder = order.createSanitisedCopy();

        listener.onOrderSubmissionSuccess( sanitisedOrder );
        }
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A runnable that polls the order status.
   *
   *****************************************************/
  private class PollStatusRunnable implements Runnable
    {
    @Override
    public void run()
      {
      pollOrderStatus();
      }
    }

  }

