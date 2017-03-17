/*****************************************************
 *
 * OrderReceiptActivity.java
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
import android.content.Context;
import android.content.Intent;

import ly.kite.ordering.Order;
import ly.kite.R;
import ly.kite.ordering.OrderingDataAgent;


///// Class Declaration /////

/*****************************************************
 *
 * This activity displays the order receipt screen.
 *
 *****************************************************/
public class OrderReceiptActivity extends AReceiptActivity
  {


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an intent used to start this activity.
   *
   *****************************************************/
  static private Intent getStartIntent( Context context, long previousOrderId, Order order, boolean hideSuccessfulNextButton )
    {
    Intent intent = new Intent( context, OrderReceiptActivity.class );

    addPreviousOrder( previousOrderId, intent );

    addExtra( order, intent );
    addHideSuccessfulNextButton( hideSuccessfulNextButton, intent );

    return ( intent );
    }


  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  static public void start( Context context, long previousOrderId, Order order, boolean hideSuccessfulNextButton )
    {
    Intent intent = getStartIntent( context, previousOrderId, order, hideSuccessfulNextButton );

    context.startActivity( intent );
    }


  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  static public void start( Context context, Order order )
    {
    start( context, OrderingDataAgent.NO_ORDER_ID, order, false );
    }


  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  static public void startForResult( Activity activity, long previousOrderId, Order order, int requestCode )
    {
    Intent intent = getStartIntent( activity, previousOrderId, order, false );

    activity.startActivityForResult( intent, requestCode );
    }


  /*****************************************************
   *
   * Convenience method for starting this activity.
   *
   *****************************************************/
  static public void startForResult( Activity activity, Order order, int requestCode )
    {
    startForResult( activity, OrderingDataAgent.NO_ORDER_ID, order, requestCode );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Displays the success screen.
   *
   *****************************************************/
  @Override
  protected void onShowReceiptSuccess()
    {
    setContentView( R.layout.screen_order_receipt );

    setDisplayActionBarHomeAsUpEnabled( false );
    }


  /*****************************************************
   *
   * Displays the failure.
   *
   *****************************************************/
  @Override
  protected void onShowReceiptFailure()
    {
    setContentView( R.layout.screen_order_failure );

    setDisplayActionBarHomeAsUpEnabled( true );

    if ( mOrder.getLastPrintSubmissionError() != null )
      {
      showErrorDialog( mOrder.getLastPrintSubmissionError().getMessage() );
      }
    }


  /*****************************************************
   *
   * Called when the next button is clicked.
   *
   *****************************************************/
  @Override
  protected void onNext()
    {
    continueShopping();
    }

  }
