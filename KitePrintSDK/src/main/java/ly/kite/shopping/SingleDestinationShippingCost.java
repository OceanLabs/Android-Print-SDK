/*****************************************************
 *
 * SingleDestinationShippingCost.java
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

import android.content.Context;

import ly.kite.R;
import ly.kite.address.Country;

/*****************************************************
 *
 * This class represents shipping costs for an item to
 * a single destination.
 *
 *****************************************************/
public class SingleDestinationShippingCost
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                        = "SingleDestinationShippingCost";

  public  static final String  DESTINATION_CODE_EUROPE        = "europe";
  public  static final String  DESTINATION_CODE_REST_OF_WORLD = "rest_of_world";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private String                mDestinationCode;
  private MultipleCurrencyCost  mCost;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  SingleDestinationShippingCost( String destinationCode, MultipleCurrencyCost cost )
    {
    mDestinationCode = destinationCode;
    mCost            = cost;
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the destination code.
   *
   *****************************************************/
  public String getDestinationCode()
    {
    return ( mDestinationCode );
    }


  /*****************************************************
   *
   * Returns a displayable description for the the destination.
   *
   *****************************************************/
  public String getDestinationDescription( Context context )
    {
    if ( Country.UK.usesISOCode( mDestinationCode )                   ) return ( context.getString( R.string.destination_description_gbr ) );
    if ( DESTINATION_CODE_EUROPE.equals( mDestinationCode )        ) return ( context.getString( R.string.destination_description_europe ) );
    if ( DESTINATION_CODE_REST_OF_WORLD.equals( mDestinationCode ) ) return ( context.getString( R.string.destination_description_rest_of_world ) );

    return ( mDestinationCode );
    }


  /*****************************************************
   *
   * Returns the cost.
   *
   *****************************************************/
  public MultipleCurrencyCost getCost()
    {
    return ( mCost );
    }


  /*****************************************************
   *
   * Returns the cost in a currency.
   *
   *****************************************************/
  public SingleCurrencyCost getCost( String currencyCode )
    {
    return ( mCost.get( currencyCode ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Returns the shipping costs as a list.
   *
   *****************************************************/

  }

