/*****************************************************
 *
 * PayPalCreditCardAgent.java
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


///// Class Declaration /////

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.paypal.android.sdk.payments.PayPalConfiguration;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.catalogue.SingleCurrencyAmounts;
import ly.kite.ordering.Order;
import ly.kite.payment.PayPalCard;
import ly.kite.payment.PayPalCardChargeListener;
import ly.kite.payment.PayPalCardVaultStorageListener;

/*****************************************************
 *
 * This class is the PayPal credit card agent.
 *
 *****************************************************/
public class PayPalCreditCardAgent implements ICreditCardAgent
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                          = "PayPalCreditCardAgent";

  static private final int     ACTIVITY_REQUEST_CODE_CREDITCARD = 32;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context                mContext;
  private APaymentFragment       mPaymentFragment;
  private Order                  mOrder;
  private SingleCurrencyAmounts  mSingleCurrencyAmount;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// ICreditCardAgent Method(s) //////////

  /*****************************************************
   *
   * Returns true if the agent uses PayPal to process
   * credit card payments.
   *
   *****************************************************/
  public boolean usesPayPal()
    {
    return ( true );
    }


  /*****************************************************
   *
   * Notifies the agent that the user has clicked on the
   * credit card payment button.
   *
   *****************************************************/
  @Override
  public void onPayClicked( Context context, APaymentFragment paymentFragment, Order order, SingleCurrencyAmounts singleCurrencyAmount )
    {
    mContext              = context;
    mPaymentFragment      = paymentFragment;
    mOrder                = order;
    mSingleCurrencyAmount = singleCurrencyAmount;


    final PayPalCard lastUsedCard = PayPalCard.getLastUsedCard( context );

    if ( lastUsedCard != null && !lastUsedCard.hasVaultStorageExpired() )
      {
      AlertDialog.Builder builder = new AlertDialog.Builder( context );

      if ( KiteSDK.getInstance( context ).getPayPalEnvironment().equals( PayPalConfiguration.ENVIRONMENT_SANDBOX ) )
        {
        builder.setTitle( R.string.kitesdk_title_payment_source_sandbox);
        }
      else
        {
        builder.setTitle( R.string.kitesdk_title_payment_source);
        }

      builder.setItems( new String[]{ context.getString( R.string.kitesdk_alert_dialog_item_pay_with_new_card), context.getString( R.string.kitesdk_alert_dialog_item_pay_with_existing_card_format_string, lastUsedCard.getLastFour() ) }, new DialogInterface.OnClickListener()
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



  /*****************************************************
   *
   * Passes an activity result to the agent.
   *
   *****************************************************/
  @Override
  public void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    //Activity activity = paymentFragment.getActivity();

    if ( requestCode == ACTIVITY_REQUEST_CODE_CREDITCARD )
      {
      if ( data != null && data.hasExtra( CardIOActivity.EXTRA_SCAN_RESULT ) )
        {
        CreditCard scanResult = data.getParcelableExtra( CardIOActivity.EXTRA_SCAN_RESULT );

        if ( !scanResult.isExpiryValid() )
          {
          mPaymentFragment.showErrorDialog( R.string.kitesdk_alert_dialog_message_card_expired);

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
          mPaymentFragment.showErrorDialog( R.string.kitesdk_alert_dialog_message_card_not_recognised);

          return;
          }

        final ProgressDialog dialog = new ProgressDialog( mContext );
        dialog.setCancelable( false );
        dialog.setTitle( R.string.kitesdk_alert_dialog_title_processing);
        dialog.setMessage( mContext.getString( R.string.kitesdk_alert_dialog_message_processing) );
        dialog.show();
        card.storeCard( KiteSDK.getInstance( mContext ), new PayPalCardVaultStorageListener()
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

            mPaymentFragment.showErrorDialog( R.string.kitesdk_alert_dialog_message_unable_to_store_card, ex.getMessage() );
            }
          } );

        }
      else
        {
        // card scan cancelled
        }

      return;
      }
    }


  ////////// Method(s) //////////

  private void payWithNewCard()
    {
    Intent scanIntent = new Intent( mContext, CardIOActivity.class );

    scanIntent.putExtra( CardIOActivity.EXTRA_REQUIRE_EXPIRY, true );
    scanIntent.putExtra( CardIOActivity.EXTRA_REQUIRE_CVV, true );
    scanIntent.putExtra( CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false );

    mPaymentFragment.startActivityForResult( scanIntent, ACTIVITY_REQUEST_CODE_CREDITCARD );
    }


  private void payWithExistingCard( PayPalCard card )
    {
    final ProgressDialog dialog = new ProgressDialog( mContext );
    dialog.setCancelable( false );
    dialog.setTitle( R.string.kitesdk_alert_dialog_title_processing);
    dialog.setMessage( mContext.getString( R.string.kitesdk_alert_dialog_message_processing) );
    dialog.show();

    card.authoriseCard( KiteSDK.getInstance( mContext ),
            mSingleCurrencyAmount.getAmount(),
            mSingleCurrencyAmount.getCurrencyCode(),
            "",
            mOrder.getShippingAddress(),
            new PayPalCardChargeListener()
              {
              @Override
              public void onChargeSuccess( PayPalCard card, String proofOfPayment )
                {
                dialog.dismiss();

                card.saveAsLastUsedCard( mContext );

                mPaymentFragment.submitOrderForPrinting( proofOfPayment, KiteSDK.getInstance( mContext ).getPayPalAccountId(), PaymentMethod.CREDIT_CARD );
                }

              @Override
              public void onError( PayPalCard card, Exception ex )
                {
                Log.e( LOG_TAG, "Error authorising card", ex );

                dialog.dismiss();

                mPaymentFragment.showErrorDialog( ex.getMessage() );
                }
              } );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

