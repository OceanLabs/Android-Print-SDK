/*****************************************************
 *
 * MultipleDestinationShippingCosts.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*****************************************************
 *
 * This class represents shipping costs for an item.
 *
 *****************************************************/
public class MultipleDestinationShippingCosts
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                        = "MultipleDestinationShippingCosts";

  public  static final String  DESTINATION_CODE_EUROPE        = "europe";
  public  static final String  DESTINATION_CODE_REST_OF_WORLD = "rest_of_world";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private HashMap<String,SingleDestinationShippingCost>  mDestinationCostTable;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public MultipleDestinationShippingCosts()
    {
    mDestinationCostTable = new HashMap<>();
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Adds a destination, together with the cost in one of more
   * currencies.
   *
   *****************************************************/
  public void add( String destinationCode, MultipleCurrencyCost cost )
    {
    mDestinationCostTable.put( destinationCode, new SingleDestinationShippingCost( destinationCode, cost ) );
    }


  /*****************************************************
   *
   * Returns the shipping cost for a destination.
   *
   *****************************************************/
  public MultipleCurrencyCost getCost( String destinationCode )
    {
    return ( mDestinationCostTable.get( destinationCode ).getCost() );
    }


  /*****************************************************
   *
   * Returns the shipping costs as a list.
   *
   *****************************************************/
  public List<SingleDestinationShippingCost> asList()
    {
    return ( new ArrayList<SingleDestinationShippingCost>( mDestinationCostTable.values() ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Returns the shipping costs as a list.
   *
   *****************************************************/

  }

