/*****************************************************
 *
 * ICreditCardAgent.java
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

import ly.kite.catalogue.SingleCurrencyAmounts;
import ly.kite.ordering.Order;


///// Interface Declaration /////

/*****************************************************
 *
 * This interface defines a fragment that is used to
 * collect and process credit cards for payment.
 *
 *****************************************************/
public interface ICreditCardAgent
  {
  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns true if the agent uses PayPal to process
   * credit card payments.
   *
   *****************************************************/
  public boolean usesPayPal();


  /*****************************************************
   *
   * Notifies the agent that the user has clicked on the
   * credit card payment button.
   *
   *****************************************************/
  public void onPayClicked( Context context, APaymentFragment paymentFragment, Order order, SingleCurrencyAmounts singleCurrencyAmount );


  /*****************************************************
   *
   * Passes an activity result to the agent.
   *
   *****************************************************/
  public void onActivityResult( int requestCode, int resultCode, Intent data );

  }