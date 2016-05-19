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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

import ly.kite.KiteSDK;
import ly.kite.analytics.Analytics;
import ly.kite.app.IndeterminateProgressDialogFragment;
import ly.kite.checkout.ACreditCardDialogFragment;
import ly.kite.checkout.ICreditCardFragment;
import ly.kite.checkout.PaymentActivity;
import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This abstract class is the parent of fragments that
 * collect credit card details.
 *
 *****************************************************/
public class StripeCreditCardFragment extends ACreditCardDialogFragment implements ICreditCardFragment
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  TAG = "StripeCreditCardFrag.";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Activity  mActivity;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// ICreditCardFragment Method(s) //////////

  /*****************************************************
   *
   * Displays this fragment.
   *
   *****************************************************/
  @Override
  public void display( Activity activity )
    {
    mActivity = activity;

    // Since we are a subclass of a dialog fragment, display us
    // as a dialog.
    show( activity.getFragmentManager(), TAG );
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
    // Create a Stripe card and validate it

    try
      {
      Card card = new Card( cardNumberString, Integer.parseInt( expiryMonthString ), Integer.parseInt( expiryYearString ), cvvString );

      if ( ! card.validateNumber() )
        {
        onDisplayError( R.string.card_error_invalid_number );

        return;
        }

      if ( ! card.validateExpiryDate() )
        {
        onDisplayError( R.string.card_error_invalid_expiry_date );

        return;
        }

      if ( ! card.validateCVC() )
        {
        onDisplayError( R.string.card_error_invalid_cvv );

        return;
        }


      onUseCard( card );
      }
    catch ( Exception exception )
      {
      onDisplayError( "Invalid card details: " + exception.getMessage() );
      }
    }


  /*****************************************************
   *
   * Called when the card has been validated.
   *
   *****************************************************/
  private void onUseCard( Card card )
    {
    // Hide the dialog
    dismiss();


    // We can't call back to the activity because it has no concept of other credit cards. So
    // we need to do the processing ourself, and then return the token as the payment id to the
    // Payment Activity.

    String stripePublicKey = KiteSDK.getInstance( mActivity ).getStripePublicKey();

    Stripe stripe = null;

    try
      {
      stripe = new Stripe( stripePublicKey );
      }
    catch ( AuthenticationException e )
      {
      Log.e( TAG, "Unable to create Stripe object", e );

      // TODO: Display an error dialog
      Toast.makeText( mActivity, "Unable to create Stripe object", Toast.LENGTH_LONG ).show();

      return;
      }


    // Show a progress dialog

    final IndeterminateProgressDialogFragment indeterminateProgressDialogFragment = IndeterminateProgressDialogFragment.newInstance( getActivity(), R.string.Processing_ );

    indeterminateProgressDialogFragment.show( getActivity().getFragmentManager(), IndeterminateProgressDialogFragment.TAG );

    indeterminateProgressDialogFragment.setCancelable( false );


    // TODO: Get rid of this anonymous inner class
    stripe.createToken
      (
      card,
      new TokenCallback()
        {
        public void onSuccess( Token token )
          {
          indeterminateProgressDialogFragment.dismiss();

          // Return the token to the activity

          if ( mActivity instanceof PaymentActivity )
            {
            ( (PaymentActivity)mActivity ).submitOrderForPrinting( token.getId(), Analytics.PAYMENT_METHOD_CREDIT_CARD );
            }
          }

        public void onError( Exception exception )
          {
          indeterminateProgressDialogFragment.dismiss();

          // TODO: Display error dialog
          // Show localised error message
          Toast.makeText( mActivity, exception.getMessage(), Toast.LENGTH_LONG ).show();
          }
        }
      );
    }


  ////////// Inner Class(es) //////////

  }

