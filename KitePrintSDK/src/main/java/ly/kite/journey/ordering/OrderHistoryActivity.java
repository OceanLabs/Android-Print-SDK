/*****************************************************
 *
 * OrderHistoryActivity.java
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

package ly.kite.journey.ordering;


///// Import(s) /////

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ly.kite.R;
import ly.kite.checkout.OrderSubmissionFragment;
import ly.kite.journey.AKiteActivity;


///// Class Declaration /////

/*****************************************************
 *
 * This activity shows the order history screen, it is
 * basically just a wrapper around the order history
 * fragment, which gives us the flexibility of showing
 * orders in a fragment or activity.
 *
 *****************************************************/
public class OrderHistoryActivity extends AKiteActivity implements OrderHistoryFragment.ICancelListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "OrderHistoryActivity";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Starts this activity.
   *
   *****************************************************/
  static public void start( Activity activity )
    {
    Intent intent = new Intent( activity, OrderHistoryActivity.class );

    activity.startActivity( intent );
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

    setContentView( R.layout.screen_general_fragment_container );

    if ( savedInstanceState == null )
      {
      getFragmentManager()
        .beginTransaction()
          .add( R.id.fragment_container, new OrderHistoryFragment() )
          .addToBackStack( OrderHistoryFragment.TAG )
        .commit();
      }
    }


  ////////// AKiteActivity Method(s) //////////

  /*****************************************************
   *
   * Called if the catalogue load is cancelled.
   *
   *****************************************************/
  @Override
  public void onLoadCancelled()
    {
    popFragment();

    finish();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

