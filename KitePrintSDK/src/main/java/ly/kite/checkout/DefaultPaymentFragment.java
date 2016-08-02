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

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import ly.kite.KiteSDK;
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
  static private final int     ACTIVITY_REQUEST_CODE_CREDITCARD = 26;

  //static private final String CARD_IO_TOKEN = "f1d07b66ad21407daf153c0ac66c09d7";

  static private final String PAYPAL_PROOF_OF_PAYMENT_PREFIX_ORIGINAL      = "PAY-";
  static private final String PAYPAL_PROOF_OF_PAYMENT_PREFIX_AUTHORISATION = "PAUTH-";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Button  mPayPalButton;
  private Button  mCreditCardButton;


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


    // Set up the buttons
    mPayPalButton.setText( R.string.payment_paypal_button_text );
    mPayPalButton.setTextColor( getResources().getColor( R.color.payment_paypal_button_text ) );

    mCreditCardButton.setText( R.string.payment_credit_card_button_text );
    mCreditCardButton.setTextColor( getResources().getColor( R.color.payment_credit_card_button_text ) );


    mPayPalButton.setOnClickListener( this );
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
    mPayPalButton.setEnabled( enabled );
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
          submitOrderForPrinting( null, Analytics.PAYMENT_METHOD_FREE );
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

      return;
      }
    else if ( requestCode == ACTIVITY_REQUEST_CODE_CREDITCARD )
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

        final ProgressDialog dialog = new ProgressDialog( getActivity() );
        dialog.setCancelable( false );
        dialog.setTitle( R.string.alert_dialog_title_processing );
        dialog.setMessage( getString( R.string.alert_dialog_message_processing ) );
        dialog.show();
        card.storeCard( KiteSDK.getInstance( getActivity() ), new PayPalCardVaultStorageListener()
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

      return;
      }


    super.onActivityResult( requestCode, resultCode, data );
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


    final PayPalCard lastUsedCard = PayPalCard.getLastUsedCard( getActivity() );

    if ( lastUsedCard != null && !lastUsedCard.hasVaultStorageExpired() )
      {
      AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

      if ( KiteSDK.getInstance( getActivity() ).getPayPalEnvironment().equals( PayPalConfiguration.ENVIRONMENT_SANDBOX ) )
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

      creditCardFragment.display( getActivity(), mOrder );
      }
    catch ( ClassNotFoundException cnfe )
      {
      Log.e( LOG_TAG, "Unable to find external card fragment: " + fragmentClassName, cnfe );
      }
    catch ( java.lang.InstantiationException ie )
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
    Intent scanIntent = new Intent( getActivity(), CardIOActivity.class );

    scanIntent.putExtra( CardIOActivity.EXTRA_REQUIRE_EXPIRY, true );
    scanIntent.putExtra( CardIOActivity.EXTRA_REQUIRE_CVV, true );
    scanIntent.putExtra( CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false );

    startActivityForResult( scanIntent, ACTIVITY_REQUEST_CODE_CREDITCARD );
    }


  private void payWithExistingCard( PayPalCard card )
    {
    final ProgressDialog dialog = new ProgressDialog( getActivity() );
    dialog.setCancelable( false );
    dialog.setTitle( R.string.alert_dialog_title_processing );
    dialog.setMessage( getString( R.string.alert_dialog_message_processing ) );
    dialog.show();

    SingleCurrencyAmount totalCost = mOrderPricing.getTotalCost().getDefaultAmountWithFallback();

    card.authoriseCard( KiteSDK.getInstance( getActivity() ),
            totalCost.getAmount(),
            totalCost.getCurrencyCode(),
            "",
            mOrder.getShippingAddress(),
            new PayPalCardChargeListener()
              {
              @Override
              public void onChargeSuccess( PayPalCard card, String proofOfPayment )
                {
                dialog.dismiss();
                submitOrderForPrinting( proofOfPayment, Analytics.PAYMENT_METHOD_CREDIT_CARD );
                card.saveAsLastUsedCard( getActivity() );
                }

              @Override
              public void onError( PayPalCard card, Exception ex )
                {
                Log.e( LOG_TAG, "Error authorising card", ex );

                dialog.dismiss();

                showErrorDialog( ex.getMessage() );
                }
              } );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when the pay by PayPal button is clicked.
   *
   *****************************************************/
  public void onPayPalButtonClicked( View view )
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
  private void submitOrderForPrinting( String paymentId, String analyticsPaymentMethod )
    {
    getPaymentActivity().submitOrderForPrinting( authorisationProofOfPaymentFrom( paymentId ), KiteSDK.getInstance( getActivity() ).getPayPalAccountId(), analyticsPaymentMethod );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

