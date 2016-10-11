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

package ly.kite.catalogue;


///// Import(s) /////

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

import ly.kite.address.Country;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents shipping costs for an item.
 *
 *****************************************************/
public class MultipleDestinationShippingCosts implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                        = "MultipleDestinationShippingCosts";

//  public  static final String  DESTINATION_CODE_EUROPE        = "europe";
//  public  static final String  DESTINATION_CODE_REST_OF_WORLD = "rest_of_world";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<MultipleDestinationShippingCosts> CREATOR =
    new Parcelable.Creator<MultipleDestinationShippingCosts>()
      {
      public MultipleDestinationShippingCosts createFromParcel( Parcel sourceParcel )
        {
        return ( new MultipleDestinationShippingCosts( sourceParcel ) );
        }

      public MultipleDestinationShippingCosts[] newArray( int size )
        {
        return ( new MultipleDestinationShippingCosts[ size ] );
        }
      };


  ////////// Member Variable(s) //////////

  private HashMap<String,SingleDestinationShippingCost>  mDestinationCostTable;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public MultipleDestinationShippingCosts()
    {
    mDestinationCostTable = new HashMap<>();
    }


  // Constructor used by parcelable interface
  private MultipleDestinationShippingCosts( Parcel sourceParcel )
    {
    this();

    int count = sourceParcel.readInt();

    for ( int index = 0; index < count; index ++ )
      {
      add( (SingleDestinationShippingCost)sourceParcel.readParcelable( SingleDestinationShippingCost.class.getClassLoader() ) );
      }
    }


  ////////// Parcelable Method(s) //////////

  /*****************************************************
   *
   * Describes the contents of this parcelable.
   *
   *****************************************************/
  @Override
  public int describeContents()
    {
    return ( 0 );
    }


  /*****************************************************
   *
   * Write the contents of this product to a parcel.
   *
   *****************************************************/
  @Override
  public void writeToParcel( Parcel targetParcel, int flags )
    {
    targetParcel.writeInt( mDestinationCostTable.size() );

    for ( SingleDestinationShippingCost singleDestinationShippingCost : mDestinationCostTable.values() )
      {
      targetParcel.writeParcelable( singleDestinationShippingCost, flags );
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Adds a destination, together with the cost in one of more
   * currencies.
   *
   *****************************************************/
  public void add( SingleDestinationShippingCost singleDestinationShippingCost )
    {
    mDestinationCostTable.put( singleDestinationShippingCost.getDestinationCode(), singleDestinationShippingCost );
    }


  /*****************************************************
   *
   * Adds a destination, together with the cost in one of more
   * currencies.
   *
   *****************************************************/
  public void add( String destinationCode, MultipleCurrencyAmounts cost )
    {
    add( new SingleDestinationShippingCost( destinationCode, cost ) );
    }


  /*****************************************************
   *
   * Returns the shipping cost for a country.
   *
   *****************************************************/
  public SingleDestinationShippingCost getCost( Country country )
    {
    SingleDestinationShippingCost singleDestinationShippingCost;


    // See if there is a cost for the country as a destination

    singleDestinationShippingCost = mDestinationCostTable.get( country.iso3Code() );

    if ( singleDestinationShippingCost != null ) return ( singleDestinationShippingCost );


    // Otherwise if the country is in Europe, see if there is a cost for Europe as a destination
    if ( country.isInEurope() )
      {
      singleDestinationShippingCost = mDestinationCostTable.get( SingleDestinationShippingCost.DESTINATION_CODE_EUROPE );

      if ( singleDestinationShippingCost != null ) return ( singleDestinationShippingCost );
      }


    // Otherwise see if there is a cost for the rest of world as a destination

    singleDestinationShippingCost = mDestinationCostTable.get( SingleDestinationShippingCost.DESTINATION_CODE_REST_OF_WORLD );

    return ( singleDestinationShippingCost );
    }


  /*****************************************************
   *
   * Returns a cost for shipping to the current locale,
   * formatted according to the locale.
   *
   *****************************************************/
  public String getDisplayCost( Locale locale )
    {
    // Get the cost for shipping

    Country country = Country.getInstance( locale );

    SingleDestinationShippingCost shippingCost = getCost( country );


    // Now get a formatted string for the amount
    return ( shippingCost.getCost().getDisplayAmountWithFallback( locale ) );
    }


  /*****************************************************
   *
   * Returns the shipping cost for a destination.
   *
   *****************************************************/
  public MultipleCurrencyAmounts getCost( String destinationCode )
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


  }

