/*****************************************************
 *
 * AOrderSubmissionActivity.java
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

package ly.kite.checkout;


///// Import(s) /////

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import ly.kite.R;
import ly.kite.analytics.Analytics;
import ly.kite.api.OrderState;
import ly.kite.journey.AKiteActivity;
import ly.kite.ordering.Order;
import ly.kite.ordering.OrderingDataAgent;


///// Class Declaration /////

/*****************************************************
 *
 * This class is the parent of activities that submit
 * orders.
 *
 *****************************************************/
abstract public class AOrderSubmissionActivity extends AKiteActivity implements IOrderSubmissionResultListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private   final String  LOG_TAG               = "AOrderSubmissionActivity";

  static private   final boolean FORCE_ORDER_TO_FAIL   = false;

  static private   final String  KEY_PREVIOUS_ORDER_ID = "ly.kite.previousOrderId";


  ////////// Static Variable(s) //////////

  static private boolean mRunsInBackground = false;


  ////////// Member Variable(s) //////////

  private OrderSubmissionFragment  mOrderSubmissionFragment;

  private long                     mPreviousOrderId;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Adds a previous order id as an extra to an intent.
   *
   *****************************************************/
  static protected void addPreviousOrder( long previousOrderId, Intent intent )
    {
    if ( previousOrderId >= 0 ) intent.putExtra( KEY_PREVIOUS_ORDER_ID, previousOrderId );
    }


  ////////// Constructor(s) //////////


  ////////// AKiteActivity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  protected void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // Check for a previous order id

    Intent intent = getIntent();

    if ( intent != null )
      {
      mPreviousOrderId = intent.getLongExtra( KEY_PREVIOUS_ORDER_ID, OrderingDataAgent.NO_ORDER_ID );
      }


    // See if there is a retained order submission fragment already running

    FragmentManager fragmentManager = getFragmentManager();

    mOrderSubmissionFragment = OrderSubmissionFragment.findFragment( this );
    }

  @Override
  protected void onResume()
    {
     super.onResume();
     mRunsInBackground = false;
     if( mOrderSubmissionFragment != null && !mOrderSubmissionFragment.isAdded())
      {
       mOrderSubmissionFragment.show(getFragmentManager(), "OrderSubmissionFragment");
      }
    }

  @Override
  protected void onPause()
    {
    super.onPause();
    mRunsInBackground = true;
    }

    ////////// OrderSubmitter.IProgressListener Method(s) //////////

  /*****************************************************
   *
   * Called with order submission progress.
   *
   *****************************************************/
  @Override
  public void onOrderComplete( Order order, OrderState state )
    {
    cleanUpAfterOrderSubmission();


    // Determine what the order state is, and set the progress dialog accordingly

    switch ( state )
      {
      case VALIDATED:

        // Fall through

      case PROCESSED:

        // Fall through

      default:

        break;

      case CANCELLED:

        displayModalDialog
                (
                        R.string.kitesdk_alert_dialog_title_order_cancelled,
                        R.string.kitesdk_alert_dialog_message_order_cancelled,
                        R.string.kitesdk_OK,
                        null,
                        NO_BUTTON,
                        null
                );

        return;
      }


    Analytics.getInstance( this ).trackPrintOrderSubmission( order );

    onOrderSuccessInt( order );
    }


  /*****************************************************
   *
   * Called when there is an error submitting the order.
   *
   *****************************************************/
  public void onOrderError( Order order, Exception exception )
    {
    cleanUpAfterOrderSubmission();

    onOrderFailureInt( order, exception );
    }


  /*****************************************************
   *
   * Called when there is a duplicate (successful) order.
   *
   *****************************************************/
  @Override
  public void onOrderDuplicate( Order order, String originalOrderId )
    {
    cleanUpAfterOrderSubmission();


    // We do need to replace any order id with the original one
    order.setReceipt( originalOrderId );


    // A duplicate is treated in the same way as a successful submission, since it means
    // the proof of payment has already been accepted and processed.

    onOrderSuccessInt( order );
    }


  /*****************************************************
   *
   * Called when polling for a completed order status
   * times out.
   *
   *****************************************************/
  @Override
  public void onOrderTimeout( Order order )
    {
    cleanUpAfterOrderSubmission();


    // When the order times out, behave as we would with a failed order

    Exception timeoutException = new Exception( getString( R.string.kitesdk_order_timeout_message) );

    order.setError( timeoutException );

    onOrderFailureInt( order, timeoutException );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Submits the order for processing.
   *
   *****************************************************/
  protected void submitOrder( Order order )
    {
    if ( FORCE_ORDER_TO_FAIL )
      {
      // If we are forcing an order to fail, save the proof of payment in
      // the user data, so that we can set it back later if we want.
      order.setNotificationEmail( null );
      order.removeUserDataParameter( "email" );
      order.setUserDataParameter( "proof_of_payment", order.getProofOfPayment() );
      order.setProofOfPayment( "PAY-error" );
      }

    // Submit the order using the order submission fragment
    OrderSubmissionFragment.start(this, order, new OrderSubmissionFragment.OrderSubmissionListener<OrderSubmissionFragment>() {
      @Override
      public void onTrigger(OrderSubmissionFragment result) {
        mOrderSubmissionFragment = result;
      }
    });
    }


  /*****************************************************
   *
   * Called when the order succeeds.
   *
   *****************************************************/
  private void onOrderSuccessInt( Order order )
    {
    // Save the successful order, taking into consideration any previous order id
    OrderingDataAgent.getInstance( this ).onOrderSuccess( mPreviousOrderId, order );

    // Deliver the successful order to the app
    onOrderSuccess( order );
    }


  /*****************************************************
   *
   * Called when the order fails.
   *
   *****************************************************/
  private void onOrderFailureInt( Order order, Exception exception )
    {
    // Save the failed order
    long localOrderId = OrderingDataAgent.getInstance( this ).onOrderFailure( mPreviousOrderId, order );

    // Deliver the failed order to the app
    onOrderFailure( localOrderId, order, exception );
    }


  /*****************************************************
   *
   * Called when the order succeeds.
   *
   *****************************************************/
  abstract protected void onOrderSuccess( Order order );


  /*****************************************************
   *
   * Called when the order fails.
   *
   *****************************************************/
  abstract protected void onOrderFailure( long localOrderId, Order order, Exception exception );


  /*****************************************************
   *
   * Cleans up after order submission has finished.
   *
   *****************************************************/
  private void cleanUpAfterOrderSubmission()
    {
    // Make sure the fragment is gone

    if ( mOrderSubmissionFragment != null )
      {
      // Make sure it's not running in background , as it will crash
      if ( !mRunsInBackground ) {
        mOrderSubmissionFragment.dismiss();
      }

      mOrderSubmissionFragment = null;
      }
    }

  /*****************************************************
   *
   * Returns the running mode of the activity/app
   *  TRUE when app is in background
   *  FALSE when app is in foreground
   *
   *****************************************************/
    public boolean isRunningInBackground()
    {
      return mRunsInBackground;
    }



  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Submits the order.
   *
   *****************************************************/
  private class SubmitOrderRunnable implements Runnable
    {
    private Order  mOrder;


    SubmitOrderRunnable( Order order )
      {
      mOrder = order;
      }


    @Override
    public void run()
      {
      submitOrder( mOrder );
      }
    }


  }

