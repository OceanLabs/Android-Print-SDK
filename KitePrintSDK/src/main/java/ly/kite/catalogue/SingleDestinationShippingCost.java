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

package ly.kite.catalogue;


///// Import(s) /////

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import ly.kite.R;
import ly.kite.address.Country;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents shipping costs for an item to
 * a single destination.
 *
 *****************************************************/
public class SingleDestinationShippingCost implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                        = "SingleDestinationShippingCost";

  public  static final String  DESTINATION_CODE_EUROPE        = "europe";
  public  static final String  DESTINATION_CODE_REST_OF_WORLD = "rest_of_world";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<SingleDestinationShippingCost> CREATOR =
    new Parcelable.Creator<SingleDestinationShippingCost>()
      {
      public SingleDestinationShippingCost createFromParcel( Parcel sourceParcel )
        {
        return ( new SingleDestinationShippingCost( sourceParcel ) );
        }

      public SingleDestinationShippingCost[] newArray( int size )
        {
        return ( new SingleDestinationShippingCost[ size ] );
        }
      };


  ////////// Member Variable(s) //////////

  private String                mDestinationCode;
  private MultipleCurrencyAmounts mCost;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  SingleDestinationShippingCost( String destinationCode, MultipleCurrencyAmounts cost )
    {
    mDestinationCode = destinationCode;
    mCost            = cost;
    }


  // Constructor used by parcelable interface
  private SingleDestinationShippingCost( Parcel sourceParcel )
    {
    mDestinationCode = sourceParcel.readString();
    mCost            = (MultipleCurrencyAmounts)sourceParcel.readParcelable( MultipleCurrencyAmounts.class.getClassLoader() );
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
    targetParcel.writeString( mDestinationCode );
    targetParcel.writeParcelable( mCost, flags );
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
    if ( Country.UK.usesISOCode( mDestinationCode )                   ) return ( context.getString( R.string.kitesdk_destination_description_gbr) );
    if ( DESTINATION_CODE_EUROPE.equals( mDestinationCode )        ) return ( context.getString( R.string.kitesdk_destination_description_europe) );
    if ( DESTINATION_CODE_REST_OF_WORLD.equals( mDestinationCode ) ) return ( context.getString( R.string.kitesdk_destination_description_rest_of_world) );

    return ( mDestinationCode );
    }


  /*****************************************************
   *
   * Returns the cost.
   *
   *****************************************************/
  public MultipleCurrencyAmounts getCost()
    {
    return ( mCost );
    }


  /*****************************************************
   *
   * Returns the cost in a currency.
   *
   *****************************************************/
  public SingleCurrencyAmounts getCost( String currencyCode )
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

