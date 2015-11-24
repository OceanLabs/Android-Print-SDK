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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.ProofOfPayment;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import ly.kite.analytics.Analytics;
import ly.kite.pricing.IPricingConsumer;
import ly.kite.pricing.OrderPricing;
import ly.kite.pricing.PricingAgent;
import ly.kite.KiteSDK;
import ly.kite.catalogue.MultipleCurrencyAmount;
import ly.kite.catalogue.PrintOrder;
import ly.kite.catalogue.PrintOrderSubmissionListener;
import ly.kite.R;
import ly.kite.payment.PayPalCard;
import ly.kite.payment.PayPalCardChargeListener;
import ly.kite.payment.PayPalCardVaultStorageListener;
import ly.kite.journey.AKiteActivity;
import ly.kite.catalogue.SingleCurrencyAmount;


///// Class Declaration /////

/*****************************************************
 *
 * This activity displays the price / payment screen.
 *
 *****************************************************/
public class PaymentActivity extends AKiteActivity implements IPricingConsumer,
                                                              TextView.OnEditorActionListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  private static final String LOG_TAG = "PaymentActivity";

  public static final String EXTRA_PRINT_ORDER = "ly.kite.EXTRA_PRINT_ORDER";
  public static final String EXTRA_PRINT_ENVIRONMENT = "ly.kite.EXTRA_PRINT_ENVIRONMENT";
  public static final String EXTRA_PRINT_API_KEY = "ly.kite.EXTRA_PRINT_API_KEY";

  public static final String ENVIRONMENT_STAGING = "ly.kite.ENVIRONMENT_STAGING";
  public static final String ENVIRONMENT_LIVE = "ly.kite.ENVIRONMENT_LIVE";
  public static final String ENVIRONMENT_TEST = "ly.kite.ENVIRONMENT_TEST";

  private static final String CARD_IO_TOKEN = "f1d07b66ad21407daf153c0ac66c09d7";

  private static final int REQUEST_CODE_PAYPAL = 0;
  private static final int REQUEST_CODE_CREDITCARD = 1;
  private static final int REQUEST_CODE_RECEIPT = 2;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private PrintOrder           mPrintOrder;
  private String               mAPIKey;
  private KiteSDK.Environment  mKiteSDKEnvironment;
  //private PayPalCard.Environment mPayPalEnvironment;

  private ListView mOrderSummaryListView;
  private EditText mPromoEditText;
  private Button mPromoButton;
  private Button mCreditCardButton;
  private Button mPayPalButton;
  private ProgressBar mProgressBar;

  private OrderPricing mOrderPricing;

  private boolean mPromoActionClearsCode;
  private String mLastSubmittedPromoCode;
  private boolean mLastPriceRetrievalSucceeded;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  public static void start( Activity activity, PrintOrder printOrder, String apiKey, String environmentName, int requestCode )
    {
    Intent intent = new Intent( activity, PaymentActivity.class );

    intent.putExtra( PaymentActivity.EXTRA_PRINT_ORDER, (Parcelable) printOrder );
    intent.putExtra( PaymentActivity.EXTRA_PRINT_API_KEY, apiKey );
    intent.putExtra( PaymentActivity.EXTRA_PRINT_ENVIRONMENT, environmentName );

    activity.startActivityForResult( intent, requestCode );
    }


  ////////// Constructor(s) //////////


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );

    String apiKey = getIntent().getStringExtra( EXTRA_PRINT_API_KEY );
    String envString = getIntent().getStringExtra( EXTRA_PRINT_ENVIRONMENT );

    mPrintOrder = (PrintOrder) getIntent().getParcelableExtra( EXTRA_PRINT_ORDER );

    if ( apiKey == null )
      {
      throw new IllegalArgumentException( "You must specify an API key string extra in the intent used to start the PaymentActivity" );
      }

    if ( mPrintOrder == null )
      {
      throw new IllegalArgumentException( "You must specify a PrintOrder object extra in the intent used to start the PaymentActivity" );
      }


//    KiteSDK.DefaultEnvironment env = KiteSDK.DefaultEnvironment.LIVE;
//    mPayPalEnvironment = PayPalCard.Environment.LIVE;
//    if ( envString != null )
//      {
//      if ( envString.equals( ENVIRONMENT_STAGING ) )
//        {
//        env = KiteSDK.DefaultEnvironment.STAGING;
//        mPayPalEnvironment = PayPalCard.Environment.SANDBOX;
//        }
//      else if ( envString.equals( ENVIRONMENT_TEST ) )
//        {
//        env = KiteSDK.DefaultEnvironment.TEST;
//        mPayPalEnvironment = PayPalCard.Environment.SANDBOX;
//        }
//      }
//
//    mAPIKey = apiKey;
//    mKiteSDKEnvironment = env;
//
//    KiteSDK.getInstance( this ).setEnvironment( apiKey, env );

    mKiteSDKEnvironment = KiteSDK.getInstance( this ).getEnvironment();


        /*
         * Start PayPal Service
         */

    PayPalConfiguration payPalConfiguration = new PayPalConfiguration()
            .clientId( mKiteSDKEnvironment.getPayPalClientId() )
            .environment( mKiteSDKEnvironment.getPayPalEnvironment() )
            .acceptCreditCards( false );

    Intent intent = new Intent( this, PayPalService.class );
    intent.putExtra( PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration );

    startService( intent );


    // Set up the screen

    setContentView( R.layout.screen_payment );

    mOrderSummaryListView = (ListView) findViewById( R.id.order_summary_list_view );
    mPromoEditText        = (EditText) findViewById( R.id.promo_edit_text );
    mPromoButton          = (Button) findViewById( R.id.promo_button );
    mCreditCardButton     = (Button) findViewById( R.id.credit_card_button );
    mPayPalButton         = (Button) findViewById( R.id.paypal_button );
    mProgressBar          = (ProgressBar) findViewById( R.id.progress_bar );

    mPromoEditText.addTextChangedListener( new PromoCodeTextWatcher() );
    mPromoEditText.setOnEditorActionListener( this );

    hideKeyboard();

    if ( mKiteSDKEnvironment.getPayPalEnvironment().equals( PayPalConfiguration.ENVIRONMENT_SANDBOX ) )
      {
      setTitle( "Payment (Sandbox)" );
      }
    else
      {
      setTitle( "Payment" );
      }


    // Get the pricing information
    requestPrices();


    if ( savedInstanceState == null )
      {
      Analytics.getInstance( this ).trackPaymentScreenViewed( mPrintOrder );
      }
    }


  @Override
  public void onSaveInstanceState( Bundle outState )
    {
    super.onSaveInstanceState( outState );

    outState.putParcelable( EXTRA_PRINT_ORDER, mPrintOrder );
    outState.putString( EXTRA_PRINT_API_KEY, mAPIKey );
    }


  @Override
  public void onRestoreInstanceState( Bundle savedInstanceState )
    {
    super.onRestoreInstanceState( savedInstanceState );

    mPrintOrder = savedInstanceState.getParcelable( EXTRA_PRINT_ORDER );
    mAPIKey = savedInstanceState.getString( EXTRA_PRINT_API_KEY );
//    mKiteSDKEnvironment = (KiteSDK.DefaultEnvironment) savedInstanceState.getSerializable( EXTRA_PRINT_ENVIRONMENT );
//    KiteSDK.getInstance( this ).setEnvironment( mAPIKey, mKiteSDKEnvironment );
//
//    mPayPalEnvironment = PayPalCard.Environment.LIVE;
//    if ( mKiteSDKEnvironment == KiteSDK.DefaultEnvironment.STAGING || mKiteSDKEnvironment == KiteSDK.DefaultEnvironment.TEST )
//      {
//      mPayPalEnvironment = PayPalCard.Environment.SANDBOX;
//      }
    }


  @Override
  protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    if ( requestCode == REQUEST_CODE_PAYPAL )
      {
      if ( resultCode == Activity.RESULT_OK )
        {

        PaymentConfirmation paymentConfirmation = data.getParcelableExtra( com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION );

        if ( paymentConfirmation != null )
          {

          try
            {

            ProofOfPayment proofOfPayment = paymentConfirmation.getProofOfPayment();

            if ( proofOfPayment != null )
              {
              String paymentId = proofOfPayment.getPaymentId();

              //String proofOfPayment = paymentConfirmation.toJSONObject().getJSONObject("proof_of_payment").getJSONObject("adaptive_payment").getString( "pay_key" );

              if ( paymentId != null )
                {
                submitOrderForPrinting( paymentId );
                }
              else
                {
                showErrorDialog( "No payment id found in proof of payment" );
                }
              }
            else
              {
              showErrorDialog( "No proof of payment found in payment confirmation" );
              }

            }
          catch ( Exception exception )
            {
            showErrorDialog( exception.getMessage() );
            }
          }
        else
          {
          showErrorDialog( "No payment confirmation received from PayPal" );
          }
        }
      }
    else if ( requestCode == REQUEST_CODE_CREDITCARD )
      {
      if ( data != null && data.hasExtra( CardIOActivity.EXTRA_SCAN_RESULT ) )
        {
        CreditCard scanResult = data.getParcelableExtra( CardIOActivity.EXTRA_SCAN_RESULT );

        if ( !scanResult.isExpiryValid() )
          {
          showErrorDialog( "Sorry it looks like that card has expired. Please try again." );
          return;
          }

        PayPalCard card = new PayPalCard();
        card.setNumber( scanResult.cardNumber );
        card.setExpireMonth( scanResult.expiryMonth );
        card.setExpireYear( scanResult.expiryYear );
        card.setCvv2( scanResult.cvv );
        card.setCardType( PayPalCard.CardType.getCardType( scanResult.getCardType() ) );

        if ( card.getCardType() == PayPalCard.CardType.UNSUPPORTED )
          {
          showErrorDialog( "Sorry we couldn't recognize your card. Please try again manually entering your card details if necessary." );

          return;
          }

        final ProgressDialog dialog = new ProgressDialog( this );
        dialog.setCancelable( false );
        dialog.setTitle( "Processing" );
        dialog.setMessage( "One moment" );
        dialog.show();
        card.storeCard( mKiteSDKEnvironment, new PayPalCardVaultStorageListener()
        {
        @Override
        public void onStoreSuccess( PayPalCard card )
          {
          if ( dialog.isShowing() ) dialog.dismiss();

          payWithExistingCard( card );
          }

        @Override
        public void onError( PayPalCard card, Exception ex )
          {
          if ( dialog.isShowing() ) dialog.dismiss();

          showErrorDialog( ex.getMessage() );
          }
        } );

        }
      else
        {
        // card scan cancelled
        }
      }
    else if ( requestCode == REQUEST_CODE_RECEIPT )
      {
      setResult( Activity.RESULT_OK );
      finish();
      }
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
  public void paOnSuccess( OrderPricing pricing )
    {
    mOrderPricing                = pricing;

    mLastPriceRetrievalSucceeded = true;

    mPromoButton.setEnabled( true );
    mCreditCardButton.setEnabled( true );
    mPayPalButton.setEnabled( true );

    mProgressBar.setVisibility( View.GONE );


    onGotPrices();
    }


  /*****************************************************
   *
   * Called when the prices could not be retrieved.
   *
   *****************************************************/
  @Override
  public void paOnError( Exception exception )
    {
    mLastPriceRetrievalSucceeded = false;

    displayModalDialog
      (
      R.string.alert_dialog_title_oops,
      getString( R.string.alert_dialog_message_pricing_format_string, exception.getMessage() ),
      R.string.Retry,
      new RetrievePricingRunnable(),
      R.string.Cancel,
      new FinishRunnable()
      );
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
   * Requests pricing information.
   *
   *****************************************************/
  void requestPrices()
    {
    mLastSubmittedPromoCode = mPromoEditText.getText().toString();

    if ( mLastSubmittedPromoCode.trim().equals( "" ) ) mLastSubmittedPromoCode = null;

    mOrderPricing  = PricingAgent.getInstance().requestPricing( this, mPrintOrder, mLastSubmittedPromoCode, this );


    // If the pricing wasn't cached - disable the buttons, and show the progress spinner, whilst
    // they are retrieved.

    if ( mOrderPricing == null )
      {
      mPromoButton.setEnabled( false );
      mCreditCardButton.setEnabled( false );
      mCreditCardButton.setEnabled( false );

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
    // Verify that amy promo code was accepted

    String promoCodeInvalidMessage = mOrderPricing.getPromoCodeInvalidMessage();

    if ( promoCodeInvalidMessage != null )
      {
      // A promo code was sent with the request but was invalid.

      // Change the colour to highlight it
      mPromoEditText.setEnabled( true );
      mPromoEditText.setTextColor( getResources().getColor( R.color.payment_promo_code_text_error ) );

      mPromoButton.setText( R.string.payment_promo_button_text_clear );

      mPromoActionClearsCode = true;


      // Note that we show an error message, but we still update the
      // order summary and leave the buttons enabled. That way the
      // user can still pay without the benefit of any promotional
      // discount.

      showErrorDialog( promoCodeInvalidMessage );
      }
    else
      {
      // Either there was no promo code, or it was valid. Save which ever it was.

      mPrintOrder.setPromoCode( mLastSubmittedPromoCode );


      // If there is a promo code - change the text to "Clear" immediately following a retrieval. It
      // will get changed back to "Apply" as soon as the field is changed.

      if ( setPromoButtonEnabledState() )
        {
        mPromoEditText.setEnabled( false );

        mPromoButton.setText( R.string.payment_promo_button_text_clear );

        mPromoActionClearsCode = true;
        }
      else
        {
        mPromoEditText.setEnabled( true );

        mPromoButton.setText( R.string.payment_promo_button_text_apply );

        mPromoActionClearsCode = false;
        }
      }


    // Get the total cost, and save it in the order

    MultipleCurrencyAmount totalCost = mOrderPricing.getTotalCost();

    mPrintOrder.setOrderPricing( mOrderPricing );


    // If the cost is zero, we change the button text
    if ( totalCost.getDefaultAmountWithFallback().getAmount().compareTo( BigDecimal.ZERO ) <= 0 )
      {
      mPayPalButton.setVisibility( View.GONE );

      mCreditCardButton.setText( R.string.payment_credit_card_button_text_free );
      mCreditCardButton.setOnClickListener( new View.OnClickListener()
      {
      @Override
      public void onClick( View view )
        {
        submitOrderForPrinting( null );
        }
      } );
      }
    else
      {
      mPayPalButton.setVisibility( View.VISIBLE );

      mCreditCardButton.setText( R.string.payment_credit_card_button_text );
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

    mPromoButton.setEnabled( isEnabled );

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

      mPromoButton.setText( R.string.payment_promo_button_text_apply );
      mPromoButton.setEnabled( false );

      mPromoActionClearsCode = false;


      // If we are clearing a promo code that was successfully used - re-request the
      // prices (i.e. without the code).

      if ( mLastSubmittedPromoCode != null && mLastPriceRetrievalSucceeded )
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
   * Called when the pay by PayPal button is clicked.
   *
   *****************************************************/
  public void onPayPalButtonClicked( View view )
    {
    if ( mOrderPricing != null )
      {
      MultipleCurrencyAmount multipleCurrencyTotalCost = mOrderPricing.getTotalCost();

      if ( multipleCurrencyTotalCost != null )
        {
        SingleCurrencyAmount totalCost = multipleCurrencyTotalCost.getDefaultAmountWithFallback();

        // TODO: See if we can remove the credit card payment option
        PayPalPayment payment = new PayPalPayment(
                totalCost.getAmount(),
                totalCost.getCurrencyCode(),
                "Product",
                PayPalPayment.PAYMENT_INTENT_SALE );

        Intent intent = new Intent( this, com.paypal.android.sdk.payments.PaymentActivity.class );

        intent.putExtra( com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment );

        startActivityForResult( intent, REQUEST_CODE_PAYPAL );
        }
      }
    }


  /*****************************************************
   *
   * Called when the pay by credit card button is clicked.
   *
   *****************************************************/
  public void onCreditCardButtonClicked( View view )
    {
    // Check if a different credit card fragment has been declared

    String creditCardFragmentClassName = getString( R.string.credit_card_fragment_class_name );

    if ( creditCardFragmentClassName != null && ( ! creditCardFragmentClassName.trim().equals( "" ) ) )
      {
      payWithExternalCardFragment( creditCardFragmentClassName );

      return;
      }


    final PayPalCard lastUsedCard = PayPalCard.getLastUsedCard( this );
    if ( lastUsedCard != null && !lastUsedCard.hasVaultStorageExpired() )
      {
      AlertDialog.Builder builder = new AlertDialog.Builder( this );

      if ( mKiteSDKEnvironment.getPayPalEnvironment().equals( PayPalConfiguration.ENVIRONMENT_SANDBOX ) )
        {
        builder.setTitle( "Payment Source (Sandbox)" );
        }
      else
        {
        builder.setTitle( "Payment Source" );
        }

      builder.setItems( new String[]{ "Pay with new card", "Pay with card ending " + lastUsedCard.getLastFour() }, new DialogInterface.OnClickListener()
      {
      @Override
      public void onClick( DialogInterface dialogInterface, int itemIndex )
        {
        if ( itemIndex == 0 )
          {
          payWithNewCard();
          }
        else
          {
          payWithExistingCard( lastUsedCard );
          }
        }
      } );
      builder.show();
      }
    else
      {
      payWithNewCard();
      }
    }


  private void payWithExternalCardFragment( String fragmentClassName )
    {
    try
      {
      Class<?> fragmentClass = Class.forName( fragmentClassName );

      ICreditCardFragment creditCardFragment = (ICreditCardFragment)fragmentClass.newInstance();

      creditCardFragment.display( this );
      }
    catch ( ClassNotFoundException cnfe )
      {
      Log.e( LOG_TAG, "Unable to find external card fragment: " + fragmentClassName, cnfe );
      }
    catch ( InstantiationException ie )
      {
      Log.e( LOG_TAG, "Unable to instantiate external card fragment: " + fragmentClassName, ie );
      }
    catch ( IllegalAccessException iae )
      {
      Log.e( LOG_TAG, "Unable to access external card fragment: " + fragmentClassName, iae );
      }
    catch ( ClassCastException cce )
      {
      Log.e( LOG_TAG, "External card fragment is not an instance of ICreditCardFragment: " + fragmentClassName, cce );
      }
    }


  private void payWithNewCard()
    {
    Intent scanIntent = new Intent( this, CardIOActivity.class );

    scanIntent.putExtra( CardIOActivity.EXTRA_REQUIRE_EXPIRY, true );
    scanIntent.putExtra( CardIOActivity.EXTRA_REQUIRE_CVV, true );
    scanIntent.putExtra( CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false );

    startActivityForResult( scanIntent, REQUEST_CODE_CREDITCARD );
    }


  public static PayPalCard.Currency getPayPalCurrency( String currencyCode )
    {
    if ( currencyCode.equals( "GBP" ) )
      {
      return PayPalCard.Currency.GBP;
      }
    else if ( currencyCode.equals( "EUR" ) )
      {
      return PayPalCard.Currency.EUR;
      }
    else if ( currencyCode.equals( "USD" ) )
      {
      return PayPalCard.Currency.USD;
      }
    else if ( currencyCode.equals( "SGD" ) )
      {
      return PayPalCard.Currency.SGD;
      }
    else if ( currencyCode.equals( "AUD" ) )
      {
      return PayPalCard.Currency.AUD;
      }
    else if ( currencyCode.equals( "NZD" ) )
      {
      return PayPalCard.Currency.NZD;
      }
    else if ( currencyCode.equals( "CAD" ) )
      {
      return PayPalCard.Currency.CAD;
      }
    else
      {
      return PayPalCard.Currency.GBP;
      }
    }


  private void payWithExistingCard( PayPalCard card )
    {
    final ProgressDialog dialog = new ProgressDialog( this );
    dialog.setCancelable( false );
    dialog.setTitle( "Processing" );
    dialog.setMessage( "One moment" );
    dialog.show();

    SingleCurrencyAmount totalCost = mOrderPricing.getTotalCost().getDefaultAmountWithFallback();

    card.chargeCard( mKiteSDKEnvironment,
            totalCost.getAmount(),
            getPayPalCurrency( totalCost.getCurrencyCode() ),
            "",
            new PayPalCardChargeListener()
            {
            @Override
            public void onChargeSuccess( PayPalCard card, String proofOfPayment )
              {
              dialog.dismiss();
              submitOrderForPrinting( proofOfPayment );
              card.saveAsLastUsedCard( PaymentActivity.this );
              }

            @Override
            public void onError( PayPalCard card, Exception ex )
              {
              dialog.dismiss();
              showErrorDialog( ex.getMessage() );
              }
            } );
    }


  public void submitOrderForPrinting( String paymentId )
    {
    if ( paymentId != null )
      {
      mPrintOrder.setProofOfPayment( paymentId );

      Analytics.getInstance( this ).trackPaymentCompleted( mPrintOrder, Analytics.PAYMENT_METHOD_PAYPAL );
      }
    //mPrintOrder.saveToHistory(this);

    final ProgressDialog dialog = new ProgressDialog( this );
    dialog.setCancelable( false );
    dialog.setIndeterminate( false );
    dialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
    dialog.setProgressNumberFormat( null );   // Don't display the "N/100" text
    dialog.setTitle( "Processing" );
    dialog.setMessage( "One moment..." );
    dialog.setMax( 100 );
    dialog.show();

    mPrintOrder.submitForPrinting( this, new PrintOrderSubmissionListener()
    {
    @Override
    public void onProgress( PrintOrder printOrder, int primaryProgressPercent, int secondaryProgressPercent )
      {
      if ( Looper.myLooper() != Looper.getMainLooper() )
        throw new AssertionError( "Should be calling back on the main thread" );
      dialog.setProgress( primaryProgressPercent );
      dialog.setSecondaryProgress( secondaryProgressPercent );
      dialog.setMessage( "Uploading images" );
      }

    @Override
    public void onSubmissionComplete( PrintOrder printOrder, String orderIdReceipt )
      {
      if ( Looper.myLooper() != Looper.getMainLooper() )
        throw new AssertionError( "Should be calling back on the main thread" );
      //mPrintOrder.saveToHistory(PaymentActivity.this);
      dialog.dismiss();
      Intent i = new Intent( PaymentActivity.this, OrderReceiptActivity.class );
      i.putExtra( OrderReceiptActivity.EXTRA_PRINT_ORDER, (Parcelable) printOrder );
      startActivityForResult( i, REQUEST_CODE_RECEIPT );

      Analytics.getInstance( PaymentActivity.this ).trackOrderSubmission( printOrder );
      }

    @Override
    public void onError( PrintOrder printOrder, Exception error )
      {
      if ( Looper.myLooper() != Looper.getMainLooper() )
        throw new AssertionError( "Should be calling back on the main thread" );
      //mPrintOrder.saveToHistory(PaymentActivity.this);
      dialog.dismiss();
      //showErrorDialog(error.getMessage());

      Intent i = new Intent( PaymentActivity.this, OrderReceiptActivity.class );
      i.putExtra( OrderReceiptActivity.EXTRA_PRINT_ORDER, (Parcelable) printOrder );
      startActivityForResult( i, REQUEST_CODE_RECEIPT );
      }
    } );
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
      mPromoButton.setText( R.string.payment_promo_button_text_apply );

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

