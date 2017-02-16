/*****************************************************
 *
 * APaymentFragment.java
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
import android.view.View;

import ly.kite.journey.AKiteFragment;
import ly.kite.ordering.Order;
import ly.kite.pricing.OrderPricing;


///// Class Declaration /////

/*****************************************************
 *
 * This is the parent class of payment fragments.
 *
 *****************************************************/
abstract public class APaymentFragment extends AKiteFragment implements View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  static public final String  TAG = "APaymentFragment";


  ////////// Member Variable(s) //////////

  protected Order         mOrder;
  protected OrderPricing  mOrderPricing;

  protected boolean       mPayPalAvailable;


  ////////// Static Method(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the payment activity.
   *
   *****************************************************/
  protected PaymentActivity getPaymentActivity()
    {
    Activity activity = getActivity();

    if ( activity != null && activity instanceof PaymentActivity )
      {
      return ( (PaymentActivity)activity );
      }

    return ( null );
    }


  /*****************************************************
   *
   * Called to enable / disable buttons.
   *
   *****************************************************/
  abstract public void onEnableButtons( boolean enabled );


  /*****************************************************
   *
   * Called with the order pricing.
   *
   *****************************************************/
  public void onOrderUpdate( Order order, OrderPricing orderPricing )
    {
    mOrder        = order;
    mOrderPricing = orderPricing;
    }


  /*****************************************************
   *
   * Called to set / unset free checkout.
   *
   *****************************************************/
  abstract public void onCheckoutFree( boolean free );


  /*****************************************************
   *
   * Displays an error dialog.
   *
   *****************************************************/
  protected void showErrorDialog( int titleResourceId, String message )
    {
    PaymentActivity paymentActivity = getPaymentActivity();

    if ( paymentActivity != null ) paymentActivity.showErrorDialog( titleResourceId, message );
    }


  /*****************************************************
   *
   * Displays an error dialog.
   *
   *****************************************************/
  protected void showErrorDialog( String message )
    {
    PaymentActivity paymentActivity = getPaymentActivity();

    if ( paymentActivity != null ) paymentActivity.showErrorDialog( message );
    }


  /*****************************************************
   *
   * Displays an error dialog.
   *
   *****************************************************/
  protected void showErrorDialog( int messageResourceId )
    {
    PaymentActivity paymentActivity = getPaymentActivity();

    if ( paymentActivity != null ) paymentActivity.showErrorDialog( messageResourceId );
    }


  /*****************************************************
   *
   * Submits the order for printing.
   *
   *****************************************************/
  abstract public void submitOrderForPrinting( String paymentId, String accountId, PaymentMethod paymentMethod );


  /*****************************************************
   *
   * Called just before the order is submitted.
   *
   *****************************************************/
  public void onPreSubmission( Order order )
    {
    }


  /*****************************************************
   *
   * Called when the order succeeds.
   *
   *****************************************************/
  public void onOrderSuccess( Activity activity, Order order, int requestCode )
    {
    // The default action is to go to the receipt screen
    OrderReceiptActivity.startForResult( activity, order, requestCode );
    }


  /*****************************************************
   *
   * Called when the order fails.
   *
   *****************************************************/
  public void onOrderFailure( Activity activity, long localOrderId, Order order, Exception exception, int requestCode )
    {
    // The default action is to go to the receipt screen
    OrderReceiptActivity.startForResult( activity, localOrderId, order, requestCode );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

