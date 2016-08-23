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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.ProofOfPayment;
import com.paypal.android.sdk.payments.ShippingAddress;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import ly.kite.KiteSDK;
import ly.kite.address.Address;
import ly.kite.analytics.Analytics;
import ly.kite.catalogue.MultipleCurrencyAmount;
import ly.kite.catalogue.SingleCurrencyAmount;
import ly.kite.payment.PayPalCard;
import ly.kite.payment.PayPalCardChargeListener;
import ly.kite.payment.PayPalCardVaultStorageListener;
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

  //static private final String CARD_IO_TOKEN = "f1d07b66ad21407daf153c0ac66c09d7";

  static private final String PAYPAL_PROOF_OF_PAYMENT_PREFIX_ORIGINAL      = "PAY-";
  static private final String PAYPAL_PROOF_OF_PAYMENT_PREFIX_AUTHORISATION = "PAUTH-";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private boolean           mPayPalAvailable;

  private Button            mPayPalButton;
  private Button            mCreditCardButton;

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


    mPayPalButton = (Button)view.findViewById( R.id.paypal_button );

    if ( mPayPalButton == null ) mPayPalButton = (Button)view.findViewById( R.id.cta_bar_left_button );


    mCreditCardButton = (Button)view.findViewById( R.id.credit_card_button );

    if ( mCreditCardButton == null ) mCreditCardButton = (Button)view.findViewById( R.id.cta_bar_right_button );


    // Determine if PayPal payments are available
    mPayPalAvailable = KiteSDK.getInstance( getActivity() ).getPayPalPaymentsAvailable();


    // Set up the buttons

    if ( mPayPalAvailable )
      {
      mPayPalButton.setText( R.string.payment_paypal_button_text );
      mPayPalButton.setTextColor( getResources().getColor( R.color.payment_paypal_button_text ) );

      mPayPalButton.setOnClickListener( this );
      }
    else
      {
      mPayPalButton.setVisibility( View.GONE );
      }


    mCreditCardButton.setText( R.string.payment_credit_card_button_text );
    mCreditCardButton.setTextColor( getResources().getColor( R.color.payment_credit_card_button_text ) );

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
    mCreditCardButton.setEnabled( enabled );
    mPayPalButton.setEnabled( enabled && mPayPalAvailable );
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
      mPayPalButton.setVisibility( View.GONE );

      mCreditCardButton.setText( R.string.payment_credit_card_button_text_free );
      mCreditCardButton.setOnClickListener( new View.OnClickListener()
        {
        @Override
        public void onClick( View view )
          {
          submitOrderForPrinting( null, null, Analytics.PAYMENT_METHOD_FREE );
          }
        } );
      }
    else
      {
      mPayPalButton.setVisibility( View.VISIBLE );

      mCreditCardButton.setText( R.string.payment_credit_card_button_text );
      mCreditCardButton.setOnClickListener( this );
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

    if ( mOrderPricing != null )
      {
      if ( view == mPayPalButton )
        {
        onPayPalButtonClicked( view );

        return;
        }
      else if ( view == mCreditCardButton )
        {
        onCreditCardButtonClicked( view );

        return;
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
                submitOrderForPrinting( paymentId, KiteSDK.getInstance( getActivity() ).getPayPalAccountId(), Analytics.PAYMENT_METHOD_PAYPAL );
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

      return;
      }


    if ( mCreditCardAgent != null ) mCreditCardAgent.onActivityResult( requestCode, resultCode, data );

    super.onActivityResult( requestCode, resultCode, data );
    }


  /*****************************************************
   *
   * Called when the pay by credit card button is clicked.
   *
   *****************************************************/
  public void onCreditCardButtonClicked( View view )
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
  private SingleCurrencyAmount getTotalCost()
    {
    MultipleCurrencyAmount totalCostMultiple = mOrderPricing.getTotalCost();

    if ( totalCostMultiple == null ) return ( null );

    return ( totalCostMultiple.getAmountWithFallback( KiteSDK.getInstance( getActivity() ).getLockedCurrencyCode() ) );
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
   * Called when the pay by PayPal button is clicked.
   *
   *****************************************************/
  public void onPayPalButtonClicked( View view )
    {
    SingleCurrencyAmount totalCost = getTotalCost();

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
   * Submits the order for printing. This should only be
   * called from this fragment (an external credit card
   * fragment should call the payment activity directly),
   * and only for PayPal payments (since we assume the
   * PayPal account id).
   *
   *****************************************************/
  public void submitOrderForPrinting( String paymentId, String accountId, String analyticsPaymentMethod )
    {
    getPaymentActivity().submitOrderForPrinting( authorisationProofOfPaymentFrom( paymentId ), accountId, analyticsPaymentMethod );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

