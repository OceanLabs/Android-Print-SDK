/*****************************************************
 *
 * PaymentActivity.java
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

import java.math.BigDecimal;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;

import ly.kite.analytics.Analytics;
import ly.kite.catalogue.SingleCurrencyAmounts;
import ly.kite.pricing.OrderPricing;
import ly.kite.pricing.PricingAgent;
import ly.kite.KiteSDK;
import ly.kite.catalogue.MultipleCurrencyAmounts;
import ly.kite.ordering.Order;
import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This activity displays the price / payment screen.
 *
 *****************************************************/
public class PaymentActivity extends AOrderSubmissionActivity implements PricingAgent.IPricingConsumer,
                                                              TextView.OnEditorActionListener,
                                                              View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  static private final String LOG_TAG                           = "PaymentActivity";

  static public  final String KEY_ORDER                         = "ly.kite.Order";
  static public  final String KEY_PAYPAL_SUPPORTED_CURRENCY_CODES = "ly.kite.PayPalAcceptedCurrencies";

  static private final String PARAMETER_NAME_PAYMENT_ACCOUNT_ID = "payment_account_id";
  static private final String PARAMETER_NAME_PAYMENT_GATEWAY    = "payment_gateway";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Order                mOrder;
  private ArrayList<String>    mPayPalSupportedCurrencyCodes;

  private APaymentFragment     mPaymentFragment;

  private boolean              mWaitingForInstanceStateRestore;
  private boolean              mPaymentFragmentReady;

  private ListView             mOrderSummaryListView;
  private EditText             mPromoEditText;
  private TextView             mPromoTextView;
  private ProgressBar          mProgressBar;

  private OrderPricing         mOrderPricing;

  private boolean              mPromoActionClearsCode;
  private String               mLastSubmittedPromoCode;
  private boolean              mLastPriceRetrievalSucceeded;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  static public void startForResult( Activity activity, Order order, ArrayList<String> payPalSupportedCurrencyCodes, int requestCode )
    {
    Intent intent = new Intent( activity, PaymentActivity.class );

    intent.putExtra( KEY_ORDER, order );

    if ( payPalSupportedCurrencyCodes != null ) intent.putStringArrayListExtra( KEY_PAYPAL_SUPPORTED_CURRENCY_CODES, payPalSupportedCurrencyCodes );

    activity.startActivityForResult( intent, requestCode );
    }


  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  static public void startForResult( Activity activity, Order order, int requestCode )
    {
    startForResult( activity, order, null, requestCode );
    }


  ////////// Constructor(s) //////////


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  protected void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // First look for a saved order (because it might have changed since we were first
    // created. If none if found - get it from the intent.

    if ( savedInstanceState != null )
      {
      mWaitingForInstanceStateRestore = true;

      mOrder = savedInstanceState.getParcelable( KEY_ORDER );
      }
    else
      {
      mWaitingForInstanceStateRestore = false;
      }


    Intent intent = getIntent();

    if ( intent != null )
      {
      if ( mOrder == null )
        {
        mOrder = intent.getParcelableExtra( KEY_ORDER );
        }

      mPayPalSupportedCurrencyCodes = intent.getStringArrayListExtra( KEY_PAYPAL_SUPPORTED_CURRENCY_CODES );
      }


    if ( mOrder == null )
      {
      throw new IllegalArgumentException( "There must either be a saved Order, or one supplied in the intent used to start the Payment Activity" );
      }


    KiteSDK kiteSDK = KiteSDK.getInstance( this );


        /*
         * Start PayPal Service
         */

    PayPalConfiguration payPalConfiguration = new PayPalConfiguration()
            .clientId( kiteSDK.getPayPalClientId() )
            .environment( kiteSDK.getPayPalEnvironment() )
            .acceptCreditCards( false );

    Intent payPalServiceIntent = new Intent( this, PayPalService.class );

    payPalServiceIntent.putExtra( PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration );

    startService( payPalServiceIntent );


    // Set up the screen

    setContentView( R.layout.screen_payment );

    mOrderSummaryListView = (ListView)findViewById( R.id.order_summary_list_view );
    mPromoEditText        = (EditText)findViewById( R.id.promo_edit_text );
    mPromoTextView        = (TextView)findViewById( R.id.promo_text_view );
    mProgressBar          = (ProgressBar)findViewById( R.id.progress_bar );


    // Add the payment fragment

    if ( savedInstanceState == null )
      {
      ViewGroup paymentFragmentContainer = (ViewGroup)findViewById( R.id.payment_fragment_container );

      if ( paymentFragmentContainer != null )
        {
        mPaymentFragment = mSDKCustomiser.getPaymentFragment();

        getFragmentManager()
          .beginTransaction()
            .add( R.id.payment_fragment_container, mPaymentFragment, APaymentFragment.TAG )
          .commit();
        }
      }
    else
      {
      mPaymentFragment = (APaymentFragment)getFragmentManager().findFragmentByTag( APaymentFragment.TAG );
      }


    hideKeyboard();


    if ( kiteSDK.getPayPalEnvironment().equals( PayPalConfiguration.ENVIRONMENT_SANDBOX ) )
      {
      setTitle( R.string.kitesdk_title_payment_sandbox);
      }
    else
      {
      setTitle( R.string.kitesdk_title_payment);
      }


    Resources resources = getResources();


    // The prices are requested once the payment fragment has had its view created

    if ( savedInstanceState == null )
      {
      Analytics.getInstance( this ).trackPaymentMethodScreenViewed( mOrder );
      }


    mPromoEditText.addTextChangedListener( new PromoCodeTextWatcher() );
    mPromoEditText.setOnEditorActionListener( this );
    }


  @Override
  public void onRestoreInstanceState( Bundle state )
    {
    super.onRestoreInstanceState( state );

    mWaitingForInstanceStateRestore = false;

    checkRequestPrices();
    }

  @Override
  public void onSaveInstanceState( Bundle outState )
    {
    super.onSaveInstanceState( outState );

    outState.putParcelable( KEY_ORDER, mOrder );
    }


  @Override
  protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    // Pass any results to the payment fragment

    if ( mPaymentFragment != null )
      {
      mPaymentFragment.onActivityResult( requestCode, resultCode, data );
      }


    super.onActivityResult( requestCode, resultCode, data );
    }

  @Override
  public void onDestroy()
    {
    stopService( new Intent( this, PayPalService.class ) );
    super.onDestroy();
    }


  @Override
  public boolean onMenuItemSelected( int featureId, MenuItem item )
    {
    if ( item.getItemId() == android.R.id.home )
      {
      finish();
      return true;
      }
    return super.onMenuItemSelected( featureId, item );
    }


  ////////// IPricingConsumer Method(s) //////////

  /*****************************************************
   *
   * Called when the prices are successfully retrieved.
   *
   *****************************************************/
  @Override
  public void paOnSuccess( int requestId, OrderPricing orderPricing )
    {
    mOrderPricing = orderPricing;

    mPromoTextView.setEnabled( true );

    mProgressBar.setVisibility( View.GONE );


    onGotPrices();
    }


  /*****************************************************
   *
   * Called when the prices could not be retrieved.
   *
   *****************************************************/
  @Override
  public void paOnError( int requestId, Exception exception )
    {
    mLastPriceRetrievalSucceeded = false;

    displayModalDialog
      (
      R.string.kitesdk_alert_dialog_title_oops,
      getString( R.string.kitesdk_alert_dialog_message_pricing_format_string, exception.getMessage() ),
      R.string.kitesdk_Retry,
      new RetrievePricingRunnable(),
      R.string.kitesdk_Cancel,
      new FinishRunnable()
      );
    }



  /*****************************************************
   *
   * Called when the order is successfully submitted.
   *
   *****************************************************/
  @Override
  protected void onOrderSuccess( Order order )
    {
    if ( mPaymentFragment != null ) mPaymentFragment.onOrderSuccess( this, order, ACTIVITY_REQUEST_CODE_CHECKOUT );
    }


  /*****************************************************
   *
   * Called when the order fails.
   *
   *****************************************************/
  @Override
  protected void onOrderFailure( long localOrderId, Order order, Exception exception )
    {
    if ( mPaymentFragment != null ) mPaymentFragment.onOrderFailure( this, localOrderId, order, exception, ACTIVITY_REQUEST_CODE_CHECKOUT );
    }


  ////////// TextView.OnEditorActionListener Method(s) //////////

  /*****************************************************
   *
   * Called when an action occurs on the editor. We use this
   * to determine when the done button is pressed on the on-screen
   * keyboard.
   *
   *****************************************************/
  @Override
  public boolean onEditorAction( TextView v, int actionId, KeyEvent event )
    {
    if ( actionId == EditorInfo.IME_ACTION_DONE )
      {
      onPerformPromoAction();
      }

    // Return false even if we intercepted the done - so the keyboard
    // will be hidden.

    return ( false );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called by the payment fragment.
   *
   *****************************************************/
  public void onPaymentFragmentReady()
    {
    mPaymentFragmentReady = true;

    checkRequestPrices();
    }


  /*****************************************************
   *
   * Checks if the state has been restored, and the payment
   * fragment is ready, before requesting prices.
   *
   *****************************************************/
  private void checkRequestPrices()
    {
    if ( mPaymentFragmentReady && ( ! mWaitingForInstanceStateRestore ) )
      {
      requestPrices();
      }
    }


  /*****************************************************
   *
   * Requests pricing information.
   *
   *****************************************************/
  void requestPrices()
    {
    String promoCode = mPromoEditText.getText().toString();

    if ( promoCode != null && promoCode.trim().equals( "" ) ) promoCode = null;

    mOrderPricing = PricingAgent.getInstance().requestPricing( this, mOrder, mLastSubmittedPromoCode = promoCode, mPayPalSupportedCurrencyCodes, this );


    // If the pricing wasn't cached - disable the buttons, and show the progress spinner, whilst
    // they are retrieved.

    if ( mOrderPricing == null )
      {
      mPromoTextView.setEnabled( false );

      if ( mPaymentFragment != null ) mPaymentFragment.onEnableButtons( false );

      mProgressBar.setVisibility( View.VISIBLE );

      return;
      }


    onGotPrices();
    }


  /*****************************************************
   *
   * Updates the screen once we have retrieved the pricing
   * information.
   *
   *****************************************************/
  void onGotPrices()
    {
    if ( mPaymentFragment != null ) mPaymentFragment.onOrderUpdate( mOrder, mOrderPricing );


    // Verify that amy promo code was accepted

    String promoCodeInvalidMessage = mOrderPricing.getPromoCodeInvalidMessage();

    if ( promoCodeInvalidMessage != null )
      {
      mLastPriceRetrievalSucceeded = false;


      // A promo code was sent with the request but was invalid.

      // Change the colour to highlight it
      mPromoEditText.setEnabled( true );
      mPromoEditText.setTextColor( getResources().getColor( R.color.payment_promo_code_text_error ) );

      mPromoTextView.setText( R.string.kitesdk_payment_promo_button_text_clear);

      mPromoActionClearsCode = true;


      // Note that we show an error message, but we still update the
      // order summary and leave the buttons enabled. That way the
      // user can still pay without the benefit of any promotional
      // discount.

      showErrorDialog( promoCodeInvalidMessage );
      }
    else
      {
      mLastPriceRetrievalSucceeded = true;


      // Either there was no promo code, or it was valid. Save which ever it was.

      mOrder.setPromoCode( mLastSubmittedPromoCode );


      // If there is a promo code - change the text to "Clear" immediately following a retrieval. It
      // will get changed back to "Apply" as soon as the field is changed.

      if ( setPromoButtonEnabledState() )
        {
        mPromoEditText.setEnabled( false );

        mPromoTextView.setText( R.string.kitesdk_payment_promo_button_text_clear);

        mPromoActionClearsCode = true;
        }
      else
        {
        mPromoEditText.setEnabled( true );

        mPromoTextView.setText( R.string.kitesdk_payment_promo_button_text_apply);

        mPromoActionClearsCode = false;
        }
      }


    // Save the pricing in the order
    mOrder.setOrderPricing( mOrderPricing );


    // Get the total cost in the most appropriate currency

    String                 preferredCurrencyCode = KiteSDK.getInstance( this ).getLockedCurrencyCode();
    MultipleCurrencyAmounts totalCostMultiple     = mOrderPricing.getTotalCost();
    SingleCurrencyAmounts totalCostSingle       = totalCostMultiple.getAmountsWithFallback( preferredCurrencyCode );


    // If the cost is zero, we change the button text
    if ( totalCostSingle.getAmount().compareTo( BigDecimal.ZERO ) <= 0 )
      {
      if ( mPaymentFragment != null ) mPaymentFragment.onCheckoutFree( true );
      }
    else
      {
      if ( mPaymentFragment != null ) mPaymentFragment.onCheckoutFree( false );
      }


    if ( mPaymentFragment != null )
      {
      mPaymentFragment.onEnableButtons( true );
      }


    OrderPricingAdaptor adaptor = new OrderPricingAdaptor( this, mOrderPricing );

    mOrderSummaryListView.setAdapter( adaptor );
    }


  /*****************************************************
   *
   * Sets the enabled state of the promo button.
   *
   * @return The enabled state.
   *
   *****************************************************/
  private boolean setPromoButtonEnabledState()
    {
    boolean isEnabled = ( mPromoEditText.getText().length() > 0 );

    mPromoTextView.setEnabled( isEnabled );

    return ( isEnabled );
    }


  /*****************************************************
   *
   * Called when the promo button is called. It may be
   * in one of two states:
   *   - Apply
   *   - Clear
   *
   *****************************************************/
  public void onPromoButtonClicked( View view )
    {
    onPerformPromoAction();
    }


  /*****************************************************
   *
   * Called when the promo button is called. It may be
   * in one of two states:
   *   - Apply
   *   - Clear
   *
   *****************************************************/
  public void onPerformPromoAction()
    {
    if ( mPromoActionClearsCode )
      {
      mPromoEditText.setEnabled( true );
      mPromoEditText.setText( null );

      mPromoTextView.setText( R.string.kitesdk_payment_promo_button_text_apply);
      mPromoTextView.setEnabled( false );

      mPromoActionClearsCode = false;


      // If we are clearing a promo code that was successfully used - re-request the
      // prices (i.e. without the code).

      if ( mLastPriceRetrievalSucceeded && mLastSubmittedPromoCode != null  )
        {
        requestPrices();
        }
      }
    else
      {
      hideKeyboardDelayed();

      requestPrices();
      }
    }


  /*****************************************************
   *
   * Submits the order for printing.
   *
   * @param proofOfPayment A string containing the proof of
   *                       payment.
   * @param accountId The account id to be added to the order,
   *                  or null if no account id should be added.
   *
   *****************************************************/
  public void submitOrderForPrinting( String proofOfPayment, String accountId, PaymentMethod paymentMethod )
    {
    if ( KiteSDK.DEBUG_PAYMENT_KEYS ) Log.d( LOG_TAG, "submitOrderForPrinting( proofOfPayment = " + proofOfPayment + ", accountId = " + accountId + ", paymentMethod = " + paymentMethod + " )" );

    if ( proofOfPayment != null )
      {
      mOrder.setProofOfPayment( proofOfPayment );
      }

    if ( accountId != null )
      {
      mOrder.setAdditionalParameter( PARAMETER_NAME_PAYMENT_ACCOUNT_ID, accountId );
      }

    mOrder.setAdditionalParameter( PARAMETER_NAME_PAYMENT_GATEWAY, paymentMethod.orderPaymentGateway() );

    Analytics.getInstance( this ).trackPaymentCompleted( mOrder, paymentMethod.analyticsPaymentMethod() );

    if ( mPaymentFragment != null ) mPaymentFragment.onPreSubmission( mOrder );

    submitOrder( mOrder );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A text watcher for the promo code.
   *
   *****************************************************/
  private class PromoCodeTextWatcher implements TextWatcher
    {
    @Override
    public void beforeTextChanged( CharSequence charSequence, int i, int i2, int i3 )
      {
      // Ignore
      }

    @Override
    public void onTextChanged( CharSequence charSequence, int i, int i2, int i3 )
      {
      // Ignore
      }

    @Override
    public void afterTextChanged( Editable editable )
      {
      // Clear any error colour on the text
      mPromoEditText.setTextColor( getResources().getColor( R.color.payment_promo_code_text_default ) );

      // Set the enabled state
      setPromoButtonEnabledState();

      // Change the button text back to Apply (even if we disable the button because the code is blank)
      mPromoTextView.setText( R.string.kitesdk_payment_promo_button_text_apply);

      mPromoActionClearsCode = false;
      }
    }


  /*****************************************************
   *
   * Starts pricing retrieval.
   *
   *****************************************************/
  private class RetrievePricingRunnable implements Runnable
    {
    @Override
    public void run()
      {
      requestPrices();
      }
    }


  }
