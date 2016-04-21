/*****************************************************
 *
 * BasketActivity.java
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

package ly.kite.journey.basket;


///// Import(s) /////

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ly.kite.checkout.CheckoutActivity;
import ly.kite.journey.AKiteActivity;
import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This class is the activity that displays the basket
 * screen.
 *
 *****************************************************/
public class BasketActivity extends AKiteActivity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "BasketActivity";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Starts this activity.
   *
   *****************************************************/
  static public void start( Context context )
    {
    Intent intent = new Intent( context, BasketActivity.class );

    context.startActivity( intent );
    }


  /*****************************************************
   *
   * Starts this activity for a result.
   *
   *****************************************************/
  static public void startForResult( Activity activity, int requestCode )
    {
    Intent intent = new Intent( activity, BasketActivity.class );

    activity.startActivityForResult( intent, requestCode );
    }


  ////////// Constructor(s) //////////


  ////////// AKiteActivity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );

    setContentView( R.layout.screen_basket );

    setTitle( R.string.title_basket );

    setLeftButtonText( R.string.basket_left_button_text );
    setLeftButtonColourRes( R.color.basket_left_button );

    setRightButtonText( R.string.basket_right_button_text );
    setRightButtonColourRes( R.color.basket_right_button );
    }


  /*****************************************************
   *
   * Called when back is pressed.
   *
   *****************************************************/
  @Override
  public void onBackPressed()
    {
    // Once we have reached the basket, we don't want back
    // to go back to the last creation screen. Instead it
    // behaves in the same way as if continue shopping were
    // clicked.

    continueShopping();
    }


  /*****************************************************
   *
   * Called when the left CTA button is clicked.
   *
   *****************************************************/
  @Override
  protected void onLeftButtonClicked()
    {
    continueShopping();
    }


  /*****************************************************
   *
   * Called when the right CTA button is clicked.
   *
   *****************************************************/
  @Override
  protected void onRightButtonClicked()
    {
    // Check out

    //CheckoutActivity.startForResult( this,  );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called to continue shopping.
   *
   *****************************************************/
  private void continueShopping()
    {
    setResult( ACTIVITY_RESULT_CODE_CONTINUE_SHOPPING );

    finish();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

