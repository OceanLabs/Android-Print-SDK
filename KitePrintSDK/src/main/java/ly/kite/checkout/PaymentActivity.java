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
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.ProofOfPayment;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import ly.kite.analytics.Analytics;
import ly.kite.api.OrderState;
import ly.kite.pricing.IPricingConsumer;
import ly.kite.pricing.OrderPricing;
import ly.kite.pricing.PricingAgent;
import ly.kite.KiteSDK;
import ly.kite.catalogue.MultipleCurrencyAmount;
import ly.kite.ordering.Order;
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
                                                              TextView.OnEditorActionListener,
                                                              OrderSubmitter.IProgressListener,
                                                              View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  private static final String LOG_TAG = "PaymentActivity";

  public static final String KEY_ORDER = "ly.kite.ORDER";

  public static final String ENVIRONMENT_STAGING = "ly.kite.ENVIRONMENT_STAGING";
  public static final String ENVIRONMENT_LIVE = "ly.kite.ENVIRONMENT_LIVE";
  public static final String ENVIRONMENT_TEST = "ly.kite.ENVIRONMENT_TEST";

  private static final String CARD_IO_TOKEN = "f1d07b66ad21407daf153c0ac66c09d7";

  private static final int REQUEST_CODE_PAYPAL = 0;
  private static final int REQUEST_CODE_CREDITCARD = 1;
  private static final int REQUEST_CODE_RECEIPT = 2;

  private static final String PAYPAL_PROOF_OF_PAYMENT_PREFIX_ORIGINAL      = "PAY-";
  private static final String PAYPAL_PROOF_OF_PAYMENT_PREFIX_AUTHORISATION = "PAUTH-";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Order mOrder;
  private String               mAPIKey;
  private KiteSDK.Environment  mKiteSDKEnvironment;

  private ListView                 mOrderSummaryListView;
  private EditText                 mPromoEditText;
  private Button                   mPromoButton;
  private Button                   mPayPalButton;
  private Button                   mCreditCardButton;
  private ProgressBar              mProgressBar;

  private OrderPricing             mOrderPricing;

  private boolean                  mPromoActionClearsCode;
  private String                   mLastSubmittedPromoCode;
  private boolean                  mLastPriceRetrievalSucceeded;

  private OrderSubmissionFragment  mOrderSubmissionFragment;
  private ProgressDialog           mOrderSubmissionProgressDialog;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  static public void startForResult( Activity activity, Order printOrder, int requestCode )
    {
    Intent intent = new Intent( activity, PaymentActivity.class );

    intent.putExtra( KEY_ORDER, printOrder );

    activity.startActivityForResult( intent, requestCode );
    }


  /*****************************************************
   *
   * Converts the prefix on a proof of payment to indicate
   * that it is an authorisation not a sale.
   *
   *****************************************************/
  static private String authorisationProofOfPaymentFrom( String originalProofOfPayment )
    {
    if ( originalProofOfPayment == null ) return ( null );


    // Find a suitable substitution

    if ( originalProofOfPayment.startsWith( PAYPAL_PROOF_OF_PAYMENT_PREFIX_ORIGINAL ) )
      {
      return ( PAYPAL_PROOF_OF_PAYMENT_PREFIX_AUTHORISATION + originalProofOfPayment.substring( PAYPAL_PROOF_OF_PAYMENT_PREFIX_ORIGINAL.length() ) );
      }


    // If we can't find a substitution - return the original unchanged
    return ( originalProofOfPayment );
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


    // First look for a saved order (because it might have changed since we were first
    // created. If none if found - get it from the intent.

    if ( savedInstanceState != null )
      {
      mOrder = savedInstanceState.getParcelable( KEY_ORDER );
      }

    if ( mOrder == null )
      {
      Intent intent = getIntent();

      if ( intent != null )
        {
        mOrder = intent.getParcelableExtra( KEY_ORDER );
        }
      }

    if ( mOrder == null )
      {
      throw new IllegalArgumentException( "There must either be a saved Print Order, or one supplied in the intent used to start the Payment Activity" );
      }


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

    mOrderSummaryListView = (ListView)findViewById( R.id.order_summary_list_view );
    mPromoEditText        = (EditText)findViewById( R.id.promo_edit_text );
    mPromoButton          = (Button)findViewById( R.id.promo_button );
    mProgressBar          = (ProgressBar)findViewById( R.id.progress_bar );


    mPayPalButton = (Button)findViewById( R.id.cta_bar_left_button );

    if ( mPayPalButton == null ) mPayPalButton = (Button)findViewById( R.id.paypal_button );


    mCreditCardButton = (Button)findViewById( R.id.cta_bar_right_button );

    if ( mCreditCardButton == null ) mCreditCardButton = (Button)findViewById( R.id.credit_card_button );


    hideKeyboard();


    if ( mKiteSDKEnvironment.getPayPalEnvironment().equals( PayPalConfiguration.ENVIRONMENT_SANDBOX ) )
      {
      setTitle( R.string.title_payment_sandbox );
      }
    else
      {
      setTitle( R.string.title_payment );
      }


    Resources resources = getResources();

    mPayPalButton.setText( R.string.payment_paypal_button_text );
    mPayPalButton.setTextColor( resources.getColor( R.color.payment_paypal_button_text ) );

    mCreditCardButton.setText( R.string.payment_credit_card_button_text );
    mCreditCardButton.setTextColor( resources.getColor( R.color.payment_credit_card_button_text ) );


    // See if there is a retained order submission fragment already running

    FragmentManager fragmentManager = getFragmentManager();

    mOrderSubmissionFragment = (OrderSubmissionFragment)fragmentManager.findFragmentByTag( OrderSubmissionFragment.TAG );


    // Get the pricing information
    requestPrices();


    if ( savedInstanceState == null )
      {
      Analytics.getInstance( this ).trackPaymentScreenViewed( mOrder );
      }


    mPromoEditText.addTextChangedListener( new PromoCodeTextWatcher() );
    mPromoEditText.setOnEditorActionListener( this );

    mPayPalButton.setOnClickListener( this );
    mCreditCardButton.setOnClickListener( this );
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

              if ( paymentId != null )
                {
                submitOrderForPrinting( paymentId, Analytics.PAYMENT_METHOD_PAYPAL );
                }
              else
                {
                showErrorDialog( R.string.alert_dialog_message_no_payment_id );
                }
              }
            else
              {
              showErrorDialog( R.string.alert_dialog_message_no_proof_of_payment );
              }

            }
          catch ( Exception exception )
            {
            showErrorDialog( exception.getMessage() );
            }
          }
        else
          {
          showErrorDialog( R.string.alert_dialog_message_no_paypal_confirmation );
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
          showErrorDialog( R.string.alert_dialog_message_card_expired );

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
          showErrorDialog( R.string.alert_dialog_message_card_not_recognised );

          return;
          }

        final ProgressDialog dialog = new ProgressDialog( this );
        dialog.setCancelable( false );
        dialog.setTitle( R.string.alert_dialog_title_processing );
        dialog.setMessage( getString( R.string.alert_dialog_message_processing ) );
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


  ////////// OrderSubmitter.IProgressListener Method(s) //////////

  /*****************************************************
   *
   * Called with order submission progress.
   *
   *****************************************************/
  @Override
  public void onOrderUpdate( Order order, OrderState state, int primaryProgressPercent, int secondaryProgressPercent )
    {
    // Get or create the progress dialog
    ProgressDialog progressDialog = getProgressDialog();


    // Determine what the order state is, and set the progress dialog accordingly

    switch ( state )
      {
      case UPLOADING:
        progressDialog.setIndeterminate( false );
        //mProgressDialog.setProgressPercentFormat( NumberFormat.getPercentInstance() );
        progressDialog.setProgress( primaryProgressPercent );
        progressDialog.setSecondaryProgress( secondaryProgressPercent );
        progressDialog.setMessage( getString( R.string.order_submission_message_uploading ) );
        break;

      // The progress bar becomes indeterminate once the images have been uploaded

      case POSTED:
        progressDialog.setIndeterminate( true );
        progressDialog.setProgressPercentFormat( null );
        progressDialog.setMessage( getString( R.string.order_submission_message_posted ) );
        break;

      case RECEIVED:
        progressDialog.setIndeterminate( true );
        progressDialog.setProgressPercentFormat( null );
        progressDialog.setMessage( getString( R.string.order_submission_message_received ) );
        break;

      case ACCEPTED:
        progressDialog.setIndeterminate( true );
        progressDialog.setProgressPercentFormat( null );
        progressDialog.setMessage( getString( R.string.order_submission_message_accepted ) );
        break;

      // We shouldn't get any other states, but if we do - display its name
      default:
        progressDialog.setIndeterminate( true );
        progressDialog.setProgressPercentFormat( null );
        progressDialog.setMessage( state.name() );
        break;
      }
    }


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
                    new SubmitOrderRunnable(),
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

    Analytics.getInstance( PaymentActivity.this ).trackOrderSubmission( order );

    OrderReceiptActivity.startForResult( PaymentActivity.this, order, REQUEST_CODE_RECEIPT );
    }


  ////////// View.OnClickListener Method(s) //////////

  /*****************************************************
   *
   * Called when a button is clicked.
   *
   *****************************************************/
  @Override
  public void onClick( View view )
    {
    if ( view == mPayPalButton )
      {
      onPayPalButtonClicked( view );
      }
    else if ( view == mCreditCardButton )
      {
      onCreditCardButtonClicked( view );
      }
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

    mOrderPricing  = PricingAgent.getInstance().requestPricing( this, mOrder, mLastSubmittedPromoCode, this );


    // If the pricing wasn't cached - disable the buttons, and show the progress spinner, whilst
    // they are retrieved.

    if ( mOrderPricing == null )
      {
      mPromoButton.setEnabled( false );
      mCreditCardButton.setEnabled( false );
      mPayPalButton.setEnabled( false );

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

      mOrder.setPromoCode( mLastSubmittedPromoCode );


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

    mOrder.setOrderPricing( mOrderPricing );


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
        submitOrderForPrinting( null, Analytics.PAYMENT_METHOD_FREE );
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
                PayPalPayment.PAYMENT_INTENT_AUTHORIZE );  // The payment is actually taken on the server

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
        builder.setTitle( R.string.title_payment_source_sandbox );
        }
      else
        {
        builder.setTitle( R.string.title_payment_source );
        }

      builder.setItems( new String[]{ getString( R.string.alert_dialog_item_pay_with_new_card ), getString( R.string.alert_dialog_item_pay_with_existing_card_format_string, lastUsedCard.getLastFour() ) }, new DialogInterface.OnClickListener()
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


  private void payWithExistingCard( PayPalCard card )
    {
    final ProgressDialog dialog = new ProgressDialog( this );
    dialog.setCancelable( false );
    dialog.setTitle( R.string.alert_dialog_title_processing );
    dialog.setMessage( getString( R.string.alert_dialog_message_processing ) );
    dialog.show();

    SingleCurrencyAmount totalCost = mOrderPricing.getTotalCost().getDefaultAmountWithFallback();

    card.authoriseCard( mKiteSDKEnvironment,
            totalCost.getAmount(),
            totalCost.getCurrencyCode(),
            "",
            new PayPalCardChargeListener()
            {
            @Override
            public void onChargeSuccess( PayPalCard card, String proofOfPayment )
              {
              dialog.dismiss();
              submitOrderForPrinting( proofOfPayment, Analytics.PAYMENT_METHOD_CREDIT_CARD );
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


  public void submitOrderForPrinting( String paymentId, String analyticsPaymentMethod )
    {
    if ( paymentId != null )
      {
      mOrder.setProofOfPayment( authorisationProofOfPaymentFrom( paymentId ) );

      Analytics.getInstance( this ).trackPaymentCompleted( mOrder, analyticsPaymentMethod );
      }

    submitOrder();
    }


  /*****************************************************
   *
   * Submits the order for processing.
   *
   *****************************************************/
  public void submitOrder()
    {
    // Submit the order using the order submission fragment

    mOrderSubmissionFragment = new OrderSubmissionFragment();

    getFragmentManager()
      .beginTransaction()
        .add( mOrderSubmissionFragment, OrderSubmissionFragment.TAG )
      .commit();

    mOrderSubmissionFragment.submit( getApplicationContext(), mOrder );
    }


  /*****************************************************
   *
   * Returns a progress dialog.
   *
   *****************************************************/
  private ProgressDialog getProgressDialog()
    {
    // If there isn't already a progress dialog - create one now

    if ( mOrderSubmissionProgressDialog == null )
      {
      mOrderSubmissionProgressDialog = new ProgressDialog( this );

      mOrderSubmissionProgressDialog.setCancelable( false );
      mOrderSubmissionProgressDialog.setIndeterminate( false );
      mOrderSubmissionProgressDialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
      mOrderSubmissionProgressDialog.setProgressNumberFormat( null );   // Don't display the "N/100" text
      mOrderSubmissionProgressDialog.setTitle( R.string.alert_dialog_title_processing );
      mOrderSubmissionProgressDialog.setMessage( getString( R.string.alert_dialog_message_processing ) );
      mOrderSubmissionProgressDialog.setMax( 100 );
      }

    if ( ! mOrderSubmissionProgressDialog.isShowing() ) mOrderSubmissionProgressDialog.show();

    return ( mOrderSubmissionProgressDialog );
    }


  /*****************************************************
   *
   * Cleans up after order submission has finished.
   *
   *****************************************************/
  private void cleanUpAfterOrderSubmission()
    {
    // Make sure the progress dialog is gone

    if ( mOrderSubmissionProgressDialog != null )
      {
      mOrderSubmissionProgressDialog.dismiss();

      mOrderSubmissionProgressDialog = null;
      }


    // Make sure the fragment is gone

    if ( mOrderSubmissionFragment != null )
      {
      getFragmentManager()
        .beginTransaction()
          .remove( mOrderSubmissionFragment )
        .commit();

      mOrderSubmissionFragment = null;
      }
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


  /*****************************************************
   *
   * Submits the order.
   *
   *****************************************************/
  private class SubmitOrderRunnable implements Runnable
    {
    @Override
    public void run()
      {
      submitOrder();
      }
    }

  }
