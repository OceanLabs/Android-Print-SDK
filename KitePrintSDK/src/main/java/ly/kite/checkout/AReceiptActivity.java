/*****************************************************
 *
 * AReceiptActivity.java
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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import ly.kite.ordering.Order;
import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This is the parent of receipt activities, and provides
 * functions common to receipt success / failure screens,
 * such as
 *   - populating screen fields
 *   - listening to / handling button clicks
 *   - retrying order submission
 *
 *****************************************************/
abstract public class AReceiptActivity extends AOrderSubmissionActivity implements View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                                       = "AReceiptActivity";

  static private final String  INTENT_EXTRA_NAME_HIDE_SUCCESSFUL_NEXT_BUTTON = "ly.kite.hideSuccessfulNextButton";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected Order         mOrder;
  private   boolean       mHideSuccessfulNextButton;
  private   boolean       mOrderSuccess;

  protected TextView      mOrderReceiptTextView;
  protected TextView      mOrderProofOfPaymentTextView;
  protected TextView      mNotificationEmailAddressTextView;

  protected ListView      mOrderSummaryListView;
  protected LinearLayout  mOrderSummaryLinearLayout;

  protected View          mNextView;
  protected View          mRetryPrintView;
  protected TextView      mCTABarRightTextView;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Adds an order to an intent.
   *
   *****************************************************/
  static protected void addExtra( Order order, Intent intent )
    {
    intent.putExtra( INTENT_EXTRA_NAME_ORDER, order );
    }


  /*****************************************************
   *
   * Adds the hide successful next button flag to an intent.
   *
   *****************************************************/
  static protected void addHideSuccessfulNextButton( boolean hide, Intent intent )
    {
    intent.putExtra( INTENT_EXTRA_NAME_HIDE_SUCCESSFUL_NEXT_BUTTON, hide );
    }


  ////////// Constructor(s) //////////


  ////////// AOrderSubmissionActivity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  protected void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );

    Intent intent = getIntent();

    if ( intent != null )
      {
      Order order = (Order)intent.getParcelableExtra( INTENT_EXTRA_NAME_ORDER );

      if ( order != null )
        {
        mHideSuccessfulNextButton = intent.getBooleanExtra( INTENT_EXTRA_NAME_HIDE_SUCCESSFUL_NEXT_BUTTON, false );

        onOrder( order );
        }
      else
        {
        Log.e( LOG_TAG, "No order found in intent" );
        }
      }
    else
      {
      Log.e( LOG_TAG, "No intent" );
      }
    }


  /*****************************************************
   *
   * Sets the content view.
   *
   *****************************************************/
  @Override
  public void setContentView( int layoutResourceId )
    {
    super.setContentView( layoutResourceId );

    mOrderReceiptTextView             = (TextView)findViewById( R.id.order_receipt_text_view );
    mOrderProofOfPaymentTextView      = (TextView)findViewById( R.id.order_proof_of_payment_text_view );
    mNotificationEmailAddressTextView = (TextView)findViewById( R.id.notification_email_address_text_view );
    mOrderSummaryListView             = (ListView)findViewById( R.id.order_summary_list_view );
    mOrderSummaryLinearLayout         = (LinearLayout)findViewById( R.id.order_summary_linear_layout );

    mNextView                         = findViewById( R.id.next_view );
    mRetryPrintView                   = findViewById( R.id.retry_print_view );

    mCTABarRightTextView              = (TextView)findViewById( R.id.cta_bar_right_text_view );


    // Populate any fields that were found

    if ( mOrderReceiptTextView != null )
      {
      mOrderReceiptTextView.setText( mOrder.getReceipt() );
      }

    if ( mOrderProofOfPaymentTextView != null )
      {
      mOrderProofOfPaymentTextView.setText( mOrder.getProofOfPayment() );
      }

    if ( mNotificationEmailAddressTextView != null )
      {
      mNotificationEmailAddressTextView.setText( mOrder.getNotificationEmail() );
      }

    if ( mOrderSummaryListView != null )
      {
      mOrderSummaryListView.setAdapter( new OrderPricingAdaptor( this, mOrder.getOrderPricing() ) );
      }

    if ( mOrderSummaryLinearLayout != null )
      {
      OrderPricingAdaptor.addItems( this, mOrder.getOrderPricing(), mOrderSummaryLinearLayout );
      }


    // Set listeners for any buttons found

    if ( mNextView != null )
      {
      mNextView.setOnClickListener( this );
      }

    if ( mCTABarRightTextView != null )
      {
      mCTABarRightTextView.setOnClickListener( this );
      }

    if ( mRetryPrintView != null )
      {
      mRetryPrintView.setOnClickListener( this );
      }
    }


  /*****************************************************
   *
   * Called when an action is clicked.
   *
   *****************************************************/
  @Override
  public boolean onOptionsItemSelected( MenuItem item )
    {
    if ( item.getItemId() == android.R.id.home )
      {
      if(mOrderSuccess)
        {
        onHome();
        }
      else
        {
        onBackPressed();
        }
      return ( true );
      }

    return ( super.onOptionsItemSelected( item ) );
    }


  /*****************************************************
   *
   * Called when the 'hardware' back key is pressed. If the
   * order succeeded, the default behaviour is for the back
   * key to do the same as if the next button were clicked.
   * If the order failed, the back key just goes back!
   *
   *****************************************************/
  @Override
  public void onBackPressed()
    {
    if ( mOrderSuccess )
      {
      onNext();

      return;
      }

    super.onBackPressed();
    }


  /*****************************************************
   *
   * Called when the order is successfully submitted after
   * a retry.
   *
   *****************************************************/
  @Override
  protected void onOrderSuccess( Order order )
    {
    onOrder( order );
    }


  /*****************************************************
   *
   * Called when the order fails.
   *
   *****************************************************/
  @Override
  protected void onOrderFailure( long localOrderId, Order order, Exception exception )
    {
    // The local order id should not have changed, so we don't need to save it

    onOrder( order );
    }


  ////////// OnClickListener Method(s) //////////

  /*****************************************************
   *
   * Called when a view is clicked.
   *
   *****************************************************/
  @Override
  public void onClick( View view )
    {
    if ( view == mNextView || ( mOrderSuccess && view == mCTABarRightTextView ) )
      {
      ///// Next /////

      onNext();

      return;
      }

    if ( view == mRetryPrintView || ( ( ! mOrderSuccess ) && view == mCTABarRightTextView ) )
      {
      ///// Retry print /////

      onRetryPrint();

      return;
      }

    super.onClick( view );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called with an updated order.
   *
   *****************************************************/
  protected void onOrder( Order order )
    {
    mOrder        = order;
    mOrderSuccess = order.isPrinted();

    if ( mOrderSuccess )
      {
      onShowReceiptSuccess();

      if ( mHideSuccessfulNextButton )
        {
        if ( mNextView != null ) mNextView.setVisibility( View.GONE );
        }

      if ( mCTABarRightTextView != null )
        {
        mCTABarRightTextView.setText( R.string.kitesdk_Continue_Shopping);
        }
      }
    else
      {
      onShowReceiptFailure();

      if ( mCTABarRightTextView != null )
        {
        mCTABarRightTextView.setText( R.string.kitesdk_Retry);
        }
      }
    }


  /*****************************************************
   *
   * Displays the success screen.
   *
   *****************************************************/
  abstract protected void onShowReceiptSuccess();


  /*****************************************************
   *
   * Displays the failure.
   *
   *****************************************************/
  abstract protected void onShowReceiptFailure();


  /*****************************************************
   *
   * Sets the status of the home icon on the action bar.
   *
   *****************************************************/
  protected void setDisplayActionBarHomeAsUpEnabled( boolean enabled )
    {
    ActionBar actionBar = getActionBar();

    if ( actionBar != null ) actionBar.setDisplayHomeAsUpEnabled( enabled );
    }


  /*****************************************************
   *
   * Called when home (the back arrow) is clicked. The default
   * behaviour is to do the same as if the next button had
   * been clicked.
   *
   *****************************************************/
  protected void onHome()
    {
    onNext();
    }


  /*****************************************************
   *
   * Called when the next button is clicked.
   *
   *****************************************************/
  abstract protected void onNext();


  /*****************************************************
   *
   * Called when the retry print button is clicked.
   *
   *****************************************************/
  protected void onRetryPrint()
    {
    // Submit the order again
    submitOrder( mOrder );
    }


  /*****************************************************
   *
   * Continues shopping.
   *
   *****************************************************/
  protected void continueShopping()
    {
    setResult( Activity.RESULT_OK );

    finish();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

