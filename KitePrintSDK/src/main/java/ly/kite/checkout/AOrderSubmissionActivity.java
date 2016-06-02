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
import android.app.ProgressDialog;
import android.os.Bundle;

import ly.kite.R;
import ly.kite.analytics.Analytics;
import ly.kite.api.OrderState;
import ly.kite.journey.AKiteActivity;
import ly.kite.ordering.Order;


///// Class Declaration /////

/*****************************************************
 *
 * This class is the parent of activities that submit
 * orders.
 *
 *****************************************************/
public class AOrderSubmissionActivity extends AKiteActivity implements IOrderSubmissionResultListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "AOrderSubmissionActivity";

  //private static final int REQUEST_CODE_RECEIPT = 57;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private OrderSubmissionFragment  mOrderSubmissionFragment;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


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


    // See if there is a retained order submission fragment already running

    FragmentManager fragmentManager = getFragmentManager();

    mOrderSubmissionFragment = (OrderSubmissionFragment)fragmentManager.findFragmentByTag( OrderSubmissionFragment.TAG );
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

        cleanUpAfterOrderSubmission();

        displayModalDialog
                (
                        R.string.alert_dialog_title_order_cancelled,
                        R.string.alert_dialog_message_order_cancelled,
                        R.string.OK,
                        null,
                        NO_BUTTON,
                        null
                );

        return;
      }


    // For anything that's not cancelled - go to the receipt screen
    onOrderSuccess( order );
    }


  /*****************************************************
   *
   * Called when there is an error submitting the order.
   *
   *****************************************************/
  public void onOrderError( Order order, Exception exception )
    {
    cleanUpAfterOrderSubmission();

    displayModalDialog
            (
                    R.string.alert_dialog_title_order_submission_error,
                    exception.getMessage(),
                    R.string.OK,
                    null,
                    NO_BUTTON,
                    null
            );

    // We no longer seem to have a route into the receipt screen on error
    //OrderReceiptActivity.startForResult( PaymentActivity.this, order, REQUEST_CODE_RECEIPT );
    }


  @Override
  public void onOrderDuplicate( Order order, String originalOrderId )
    {
    // We do need to replace any order id with the original one
    order.setReceipt( originalOrderId );


    // A duplicate is treated in the same way as a successful submission, since it means
    // the proof of payment has already been accepted and processed.

    onOrderSuccess( order );
    }


  @Override
  public void onOrderTimeout( Order order )
    {
    cleanUpAfterOrderSubmission();

    displayModalDialog
            (
                    R.string.alert_dialog_title_order_timeout,
                    R.string.alert_dialog_message_order_timeout,
                    R.string.order_timeout_button_wait,
                    new SubmitOrderRunnable( order ),
                    R.string.order_timeout_button_give_up,
                    null
            );
    }


  /*****************************************************
   *
   * Proceeds to the receipt screen.
   *
   *****************************************************/
  private void onOrderSuccess( Order order )
    {
    cleanUpAfterOrderSubmission();

    Analytics.getInstance( this ).trackOrderSubmission( order );

    OrderReceiptActivity.startForResult( this, order, ACTIVITY_REQUEST_CODE_CHECKOUT );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Submits the order for processing.
   *
   *****************************************************/
  protected void submitOrder( Order order )
    {
    // Submit the order using the order submission fragment
    mOrderSubmissionFragment = OrderSubmissionFragment.start( this, order );
    }


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
      mOrderSubmissionFragment.dismiss();

      mOrderSubmissionFragment = null;
      }
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

