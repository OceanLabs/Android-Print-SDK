/*****************************************************
 *
 * StripeCreditCardFragment.java
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

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.app.RetainedFragmentHelper;
import ly.kite.catalogue.SingleCurrencyAmounts;
import ly.kite.ordering.Order;


///// Class Declaration /////

/*****************************************************
 *
 * This abstract class is the parent of fragments that
 * collect credit card details.
 *
 *****************************************************/
public class StripeCreditCardAgent extends ACreditCardDialogFragment implements ICreditCardAgent
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  TAG = "StripeCreditCardFrag.";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context                 mContext;
  private Order                   mOrder;
  private SingleCurrencyAmounts   mSingleCurrencyAmount;

  private Resources               mResources;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// ACreditCardDialogFragment Method(s) //////////


  ////////// ICreditCardFragment Method(s) //////////

  /*****************************************************
   *
   * Returns true if the agent uses PayPal to process
   * credit card payments.
   *
   *****************************************************/
  public boolean usesPayPal()
    {
    return ( false );
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
    mOrder                = order;
    mSingleCurrencyAmount = singleCurrencyAmount;

    mResources            = context.getResources();

    setTargetFragment( paymentFragment, 0 );

    show( paymentFragment.getFragmentManager(), TAG );
    }


  /*****************************************************
   *
   * Passes an activity result to the agent.
   *
   *****************************************************/
  @Override
  public void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    // We aren't expecting an activity result
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when the proceed button has been clicked.
   *
   *****************************************************/
  @Override
  protected void onProceed( String cardNumberString, String expiryMonthString, String expiryYearString, String cvvString )
    {
    // Do basic validation of card details

    if ( ! validateCard( cardNumberString, expiryMonthString, expiryYearString, cvvString ) ) return;


    // Create a Stripe card and perform additional validation. If it's OK, use it for payment.

    Card card = getValidatedCard( cardNumberString, expiryMonthString, expiryYearString, cvvString );

    if ( card != null )
      {
      onUseCard( card );
      }
    }


  /*****************************************************
   *
   * Returns a validated Stripe card, or null, if the card
   * could not be validated.
   *
   *****************************************************/
  private Card getValidatedCard( String cardNumberString, String expiryMonthString, String expiryYearString, String cvvString )
    {
    // Create a Stripe card and validate it

    try
      {
      Card card = new Card( cardNumberString, Integer.parseInt( expiryMonthString ), Integer.parseInt( expiryYearString ), cvvString );

      if ( ! card.validateNumber() )
        {
        onDisplayError( R.string.kitesdk_card_error_invalid_number);

        return ( null );
        }

      if ( ! card.validateExpiryDate() )
        {
        onDisplayError( R.string.kitesdk_card_error_invalid_expiry_date);

        return ( null );
        }

      if ( ! card.validateCVC() )
        {
        onDisplayError( R.string.kitesdk_card_error_invalid_cvv);

        return ( null );
        }


      return ( card );
      }
    catch ( Exception exception )
      {
      onDisplayError( getString( R.string.kitesdk_stripe_error_invalid_card_details) + ": " + exception.getMessage() );
      }

    return ( null );
    }


  /*****************************************************
   *
   * Called when the card has been validated.
   *
   *****************************************************/
  private void onUseCard( Card card )
    {
    // We can't call back to the activity because it has no concept of other credit cards. So
    // we need to do the processing ourselves, and then return the token as the payment id to the
    // Payment Activity.

    String stripePublicKey = KiteSDK.getInstance( mContext ).getStripePublicKey();

    Stripe stripe = stripe = new Stripe( mContext, stripePublicKey );


    onClearError();

    onProcessingStarted();

    // Try to get a payment token
    stripe.createToken( card, new StripeTokenCallback() );
    }


  ////////// Inner Class(es) //////////

  private class StripeTokenCallback implements TokenCallback
    {
    public void onSuccess( final Token token )
      {
      onProcessingStopped();

      Log.i( TAG, "Successfully retrieved Stripe token: " + token.toString() );


      // Return the token to the payment fragment as soon as it is attached (if it
      // is the activity) or set (if it is a fragment).

      setStateNotifier( new RetainedFragmentHelper.AStateNotifier()
        {
        @Override
        public void notify( Object callback )
          {
          APaymentFragment paymentFragment = (APaymentFragment)callback;

          paymentFragment.submitOrderForPrinting( token.getId(), KiteSDK.getInstance( mContext ).getStripeAccountId(), PaymentMethod.CREDIT_CARD );

          // Once the order has been submitted, we can be dismissed.
          remove();
          }
        } );
      }

    public void onError( Exception exception )
      {
      onProcessingStopped();

      Log.e( TAG, "Error retrieving token", exception );

      // We may not be attached to an activity at this point (e.g. if the device has been rotated),
      // so we need to be careful that the getString call doesn't fail.
      try
        {
        onDisplayError( mResources.getString( R.string.kitesdk_stripe_error_retrieve_token) + ": " + exception.getMessage() );
        }
      catch ( Exception ignore )
        {
        }
      }
    }
  }
