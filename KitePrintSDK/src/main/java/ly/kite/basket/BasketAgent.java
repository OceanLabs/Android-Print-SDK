/*****************************************************
 *
 * BasketAgent.java
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

package ly.kite.basket;


///// Import(s) /////


///// Class Declaration /////

import android.content.Context;

import ly.kite.catalogue.Catalogue;
import ly.kite.ordering.Order;

/*****************************************************
 *
 * This class manages the basket.
 *
 *****************************************************/
public class BasketAgent
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "BasketAgent";


  ////////// Static Variable(s) //////////

  static private BasketAgent  sBasketAgent;


  ////////// Member Variable(s) //////////

  private Context              mApplicationContext;
  private BasketDatabaseAgent  mDatabaseAgent;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an instance of this agent.
   *
   *****************************************************/
  static public BasketAgent getInstance( Context context )
    {
    if ( sBasketAgent == null )
      {
      sBasketAgent = new BasketAgent( context );
      }

    return ( sBasketAgent );
    }


  ////////// Constructor(s) //////////

  private BasketAgent( Context context )
    {
    mApplicationContext = context.getApplicationContext();
    mDatabaseAgent      = new BasketDatabaseAgent( mApplicationContext, null );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Clears the basket.
   *
   *****************************************************/
  public BasketAgent clearBasket()
    {
    mDatabaseAgent.clearBasket();

    return ( this );
    }


  /*****************************************************
   *
   * Saves an order to the basket.
   *
   *****************************************************/
  public BasketAgent addToBasket( Order order )
    {
    // TODO: Copy referenced assets to basket

    mDatabaseAgent.saveToBasket( order );

    return ( this );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

