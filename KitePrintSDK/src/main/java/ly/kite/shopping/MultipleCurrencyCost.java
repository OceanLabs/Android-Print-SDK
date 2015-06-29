/*****************************************************
 *
 * MultipleCurrencyCost.java
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

package ly.kite.shopping;


///// Import(s) /////


///// Class Declaration /////

import java.util.Currency;
import java.util.HashMap;
import java.util.Set;

/*****************************************************
 *
 * This class represents a cost in multiple currencies.
 *
 *****************************************************/
public class MultipleCurrencyCost
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "MultipleCurrencyCost";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private HashMap<Currency,SingleCurrencyCost> mCurrencyCostTable;
  private HashMap<String,SingleCurrencyCost>   mCurrencyCodeCostTable;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public MultipleCurrencyCost()
    {
    mCurrencyCostTable     = new HashMap<Currency,SingleCurrencyCost>();
    mCurrencyCodeCostTable = new HashMap<String,SingleCurrencyCost>();
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Adds a cost in a single currency.
   *
   *****************************************************/
  public void add( SingleCurrencyCost singleCurrencyCost )
    {
    mCurrencyCostTable.put( singleCurrencyCost.getCurrency(), singleCurrencyCost );
    mCurrencyCodeCostTable.put( singleCurrencyCost.getCurrency().getCurrencyCode(), singleCurrencyCost );
    }


  /*****************************************************
   *
   * Returns a the cost for a currency code.
   *
   *****************************************************/
  public SingleCurrencyCost get( String currencyCode )
    {
    return ( mCurrencyCodeCostTable.get( currencyCode ) );
    }


  /*****************************************************
   *
   * Returns the cost at a position.
   *
   *****************************************************/
  public SingleCurrencyCost get( int position )
    {
    return ( mCurrencyCodeCostTable.get( position ) );
    }


  /*****************************************************
   *
   * Returns all the currency codes.
   *
   *****************************************************/
  public Set<String> getAllCurrencyCodes()
    {
    return ( mCurrencyCodeCostTable.keySet() );
    }



  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

