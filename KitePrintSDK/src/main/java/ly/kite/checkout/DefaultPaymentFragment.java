/*****************************************************
 *
 * DefaultPaymentFragment.java
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.CardRequirements;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentMethodToken;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.ProofOfPayment;
import com.paypal.android.sdk.payments.ShippingAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import ly.kite.KiteSDK;
import ly.kite.address.Address;
import ly.kite.catalogue.MultipleCurrencyAmounts;
import ly.kite.catalogue.SingleCurrencyAmounts;
import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This class is the default payment agent, which starts
 * the payment activity.
 *
 *****************************************************/
public class DefaultPaymentFragment extends APaymentFragment
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                          = "DefaultPaymentFragment";

  static private final int     ACTIVITY_REQUEST_CODE_PAYPAL     = 23;
  static private final int ACTIVITY_REQUEST_CODE_GOOGLE_PAY     = 27;

  //static private final String CARD_IO_TOKEN = "f1d07b66ad21407daf153c0ac66c09d7";

  static private final String PAYPAL_PROOF_OF_PAYMENT_PREFIX_ORIGINAL      = "PAY-";
  static private final String PAYPAL_PROOF_OF_PAYMENT_PREFIX_AUTHORISATION = "PAUTH-";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private boolean           mGooglePayAvailable;
  private boolean           mPayPalAvailable;

  private ImageButton       mGooglePayButton;
  private ImageButton       mPayPalButton;
  private Button            mCreditCardButton;

  private PaymentsClient    mPaymentsClient;
  private ICreditCardAgent  mCreditCardAgent;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

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


  ////////// APaymentFragment Method(s) //////////

  /*****************************************************
   *
   * Returns a view for the fragment.
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState )
    {
    View view = layoutInflater.inflate( R.layout.fragment_default_payment, container, false );

    mGooglePayButton =  view.findViewById( R.id.google_pay_button );
    mPayPalButton = view.findViewById( R.id.paypal_button );
    mCreditCardButton = view.findViewById( R.id.credit_card_button );


    // Determine what payment options are available / enabled

    mGooglePayAvailable = KiteSDK.getInstance( getActivity() ).getGooglePayPaymentsEnabled();
    mPayPalAvailable = KiteSDK.getInstance( getActivity() ).getPayPalPaymentsAvailable();


    // Set up the buttons

    if ( mGooglePayAvailable ){
      startGooglePayClient();
    } else {
      mGooglePayButton.setVisibility( View.GONE );
    }

    if ( mPayPalAvailable ){
      mPayPalButton.setOnClickListener( this );
    } else {
      mPayPalButton.setVisibility( View.GONE );
    }

    mCreditCardButton.setOnClickListener( this );

    getPaymentActivity().onPaymentFragmentReady();

    return ( view );
    }


  /*****************************************************
   *
   * Called to enable / disable buttons.
   *
   *****************************************************/
  @Override
  public void onEnableButtons( boolean enabled )
    {
    if ( mGooglePayButton != null ) mGooglePayButton.setEnabled( enabled && mGooglePayAvailable );
    if ( mPayPalButton    != null ) mPayPalButton.setEnabled( enabled && mPayPalAvailable );

    mCreditCardButton.setEnabled( enabled );
    }


  /*****************************************************
   *
   * Called to set / unset free checkout
   *
   *****************************************************/
  @Override
  public void onCheckoutFree( boolean free )
    {
    if ( free )
      {
      if ( mGooglePayButton   != null ) mGooglePayButton.setVisibility( View.GONE );
      if ( mPayPalButton      != null ) mPayPalButton.setVisibility( View.GONE );

      mCreditCardButton.setText( R.string.kitesdk_payment_credit_card_button_text_free);
      mCreditCardButton.setOnClickListener( new View.OnClickListener()
        {
        @Override
        public void onClick( View view )
          {
          submitOrderForPrinting( null, null, PaymentMethod.FREE );
          }
        } );
      }
    }


  /*****************************************************
   *
   * Called when a view is clicked.
   *
   *****************************************************/
  @Override
  public void onClick( View view )
    {
    // Both payment methods depend on us having the order price

    if ( mOrderPricing != null ) {
      if ( mGooglePayButton != null && view == mGooglePayButton ) {
          onGooglePayClicked( view );
        } else if ( mPayPalButton != null && view == mPayPalButton ) {
          onPayPalClicked( view );
        } else if (view == mCreditCardButton) {
          onCreditCardClicked( view );
        }
      }
    }


  @Override
  public void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    if ( requestCode == ACTIVITY_REQUEST_CODE_PAYPAL )
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
                submitOrderForPrinting( paymentId, KiteSDK.getInstance( getActivity() ).getPayPalAccountId(), PaymentMethod.PAYPAL );
                }
              else
                {
                showErrorDialog( R.string.kitesdk_alert_dialog_message_no_payment_id);
                }
              }
            else
              {
              showErrorDialog( R.string.kitesdk_alert_dialog_message_no_proof_of_payment);
              }

            }
          catch ( Exception exception )
            {
            showErrorDialog( exception.getMessage() );
            }
          }
        else
          {
          showErrorDialog( R.string.kitesdk_alert_dialog_message_no_paypal_confirmation);
          }
        }

      return;

    } else if ( requestCode == ACTIVITY_REQUEST_CODE_GOOGLE_PAY ) {
      switch ( resultCode ) {
        case Activity.RESULT_OK:
          PaymentData paymentData = PaymentData.getFromIntent( data );

          if ( paymentData != null ) {
            PaymentMethodToken token = paymentData.getPaymentMethodToken();

            if ( token != null ) {
              try {
                final String stripeToken = new JSONObject( token.getToken() ).getString( "id" );
                getPaymentActivity().submitOrderForPrinting( stripeToken, null, PaymentMethod.GOOGLE_PAY );
              } catch ( JSONException e ) {
                Log.i( LOG_TAG, "Error: " + e );
              }
            } else {
              showErrorDialog( R.string.kitesdk_alert_dialog_message_no_payment_id );
            }
          } else {
            showErrorDialog( R.string.kitesdk_alert_dialog_message_no_proof_of_payment );
          }
          break;

        case Activity.RESULT_CANCELED:
          break;

        case AutoResolveHelper.RESULT_ERROR:
          Status status = AutoResolveHelper.getStatusFromIntent( data );
          if ( status != null ) Log.e( LOG_TAG, status.toString() );
          break;
        default:
          // Do nothing.
      }
    }

    if ( mCreditCardAgent != null ) mCreditCardAgent.onActivityResult( requestCode, resultCode, data );

    super.onActivityResult( requestCode, resultCode, data );
    }


  /*****************************************************
   *
   * Called when pay by credit card is clicked.
   *
   *****************************************************/
  public void onCreditCardClicked( View view )
    {
    // Call the credit card agent

    mCreditCardAgent = KiteSDK.getInstance( getActivity() ).getCustomiser().getCreditCardAgent();

    mCreditCardAgent.onPayClicked( getActivity(), this, mOrder, getTotalCost() );
    }




  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the total cost in the locked currency.
   *
   *****************************************************/
  private SingleCurrencyAmounts getTotalCost()
    {
    MultipleCurrencyAmounts totalCostMultiple = mOrderPricing.getTotalCost();

    if ( totalCostMultiple == null ) return ( null );

    return ( totalCostMultiple.getAmountsWithFallback( KiteSDK.getInstance( getActivity() ).getLockedCurrencyCode() ) );
    }


  /*****************************************************
   *
   * Returns a PayPal shipping address.
   *
   *****************************************************/
  protected ShippingAddress getShippingAddress()
    {
    Address shippingAddress = mOrder.getShippingAddress();

    if ( shippingAddress != null )
      {
      return (
              new ShippingAddress()
                      .recipientName( shippingAddress.getRecipientName() )
                      .line1( shippingAddress.getLine1() )
                      .line2( shippingAddress.getLine2() )
                      .city( shippingAddress.getCity() )
                      .state( shippingAddress.getStateOrCounty() )
                      .postalCode( shippingAddress.getZipOrPostalCode() )
                      .countryCode( shippingAddress.getCountry().iso2Code().toUpperCase() ) );
      }


    return ( null );
    }


  /*****************************************************
   *
   * Starts the Google Pay payments client.
   *
   *****************************************************/
  public void startGooglePayClient()
  {
    int walletConstant;

    if (KiteSDK.getInstance(getActivity()).getEnvironmentName().equals("Live")){
      walletConstant = WalletConstants.ENVIRONMENT_PRODUCTION;
    } else {
      walletConstant = WalletConstants.ENVIRONMENT_TEST;
    }

    mPaymentsClient = Wallet.getPaymentsClient(getActivity(), new Wallet.WalletOptions.Builder()
      .setEnvironment(walletConstant)
      .build());

    // Checks if Google Pay is ready to pay

    IsReadyToPayRequest request =
      IsReadyToPayRequest.newBuilder()
        .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
        .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
        .build();
    Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
    task.addOnCompleteListener(
      new OnCompleteListener<Boolean>() {
        public void onComplete(Task<Boolean> task) {
          try {
            setGooglePayAvailable(task.getResult(ApiException.class));
          } catch (ApiException exception) {
            exception.getStatusCode();
            setGooglePayAvailable(false);
          }
        }
      });

  }


  /*****************************************************
   *
   * Sets availability of Google Pay.
   *
   *****************************************************/
  private void setGooglePayAvailable(boolean available) {
    if (available) {
      mGooglePayButton.setOnClickListener( this );
      mGooglePayButton.setVisibility(View.VISIBLE);
    } else {
      mGooglePayButton.setVisibility(View.GONE);
    }
  }


  /*****************************************************
   *
   * Called when pay by PayPal is clicked.
   *
   *****************************************************/
  public void onPayPalClicked( View view )
    {
    SingleCurrencyAmounts totalCost = getTotalCost();

    if ( totalCost != null )
      {
      // Authorise the payment. Payment is actually taken on the server

      // TODO: Remove the credit card payment option
      PayPalPayment payment = new PayPalPayment(
              totalCost.getAmount(),
              totalCost.getCurrencyCode(),
              "Product",
              PayPalPayment.PAYMENT_INTENT_AUTHORIZE );


      // Add any shipping address

      ShippingAddress shippingAddress = getShippingAddress();

      if ( shippingAddress != null ) payment.providedShippingAddress( getShippingAddress() );


      Intent intent = new Intent( getActivity(), com.paypal.android.sdk.payments.PaymentActivity.class );

      intent.putExtra( com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment );

      startActivityForResult( intent, ACTIVITY_REQUEST_CODE_PAYPAL );
      }
    }

  /*****************************************************
   *
   * Called when pay by Google Pay is clicked.
   *
   *****************************************************/
  public void onGooglePayClicked( View view ) {

    final SingleCurrencyAmounts totalCost = getTotalCost();
    if (totalCost != null) {
      PaymentDataRequest request = createPaymentDataRequest(totalCost.getAmount().toString(),
          totalCost.getCurrencyCode());
      if (request != null) {
        AutoResolveHelper.resolveTask( mPaymentsClient.loadPaymentData(request),
            getPaymentActivity(), ACTIVITY_REQUEST_CODE_GOOGLE_PAY);
      }
    }
  }


  /*****************************************************
   *
   * Submits the order for printing.
   *
   *****************************************************/
  @Override
  public void submitOrderForPrinting( String paymentId, String accountId, PaymentMethod paymentMethod )
    {
    getPaymentActivity().submitOrderForPrinting( authorisationProofOfPaymentFrom( paymentId ), accountId, paymentMethod );
    }


  /*****************************************************
   *
   * Creates a PaymentDataRequest for Google Pay.
   *
   *****************************************************/

  public PaymentDataRequest createPaymentDataRequest(String amount, String currencyCode) {
    PaymentDataRequest.Builder request =
        PaymentDataRequest.newBuilder()
            .setTransactionInfo(
                TransactionInfo.newBuilder()
                    .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                    .setTotalPrice(amount)
                    .setCurrencyCode(currencyCode)
                    .build())
            .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
            .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
            .setCardRequirements(
                CardRequirements.newBuilder()
                    .addAllowedCardNetworks(
                        Arrays.asList(
                            WalletConstants.CARD_NETWORK_AMEX,
                            WalletConstants.CARD_NETWORK_DISCOVER,
                            WalletConstants.CARD_NETWORK_VISA,
                            WalletConstants.CARD_NETWORK_MASTERCARD))
                    .build());

    PaymentMethodTokenizationParameters params =
        PaymentMethodTokenizationParameters.newBuilder()
            .setPaymentMethodTokenizationType(
                WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
            .addParameter("gateway", "stripe")
            .addParameter("stripe:publishableKey", KiteSDK.getInstance(getActivity()).getStripePublicKey())
            .addParameter("stripe:version", "5.0.0")
            .build();

    request.setPaymentMethodTokenizationParameters(params);

    return request.build();
  }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

