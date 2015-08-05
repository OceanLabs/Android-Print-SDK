/*****************************************************
 *
 * AssetAndQuantity.java
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

package ly.kite.product;


///// Import(s) /////

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents an asset and a quantity.
 *
 *****************************************************/
public class AssetAndQuantity implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "AssetAndQuantity";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<AssetAndQuantity> CREATOR = new Parcelable.Creator<AssetAndQuantity>()
    {
    public AssetAndQuantity createFromParcel( Parcel in )
      {
      return ( new AssetAndQuantity( in ) );
      }

    public AssetAndQuantity[] newArray( int size )
      {
      return ( new AssetAndQuantity[ size ] );
      }
    };


  ////////// Member Variable(s) //////////

  final private Asset  mAsset;
        private int    mQuantity;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns true if the asset is in the list.
   *
   *****************************************************/
  static public boolean isInList( List<AssetAndQuantity> assetAndQuantityList, Asset soughtAsset )
    {
    for ( AssetAndQuantity candidateAssetAndQuantity : assetAndQuantityList )
      {
      if ( candidateAssetAndQuantity.getAsset().equals( soughtAsset ) ) return ( true );
      }

    return ( false );
    }


  ////////// Constructor(s) //////////

  public AssetAndQuantity( Asset asset, int quantity )
    {
    mAsset    = asset;
    mQuantity = quantity;
    }


  private AssetAndQuantity( Parcel sourceParcel )
    {
    mAsset    = new Asset( sourceParcel );
    mQuantity = sourceParcel.readInt();
    }


  ////////// Parcelable Method(s) //////////

  @Override
  public int describeContents()
    {
    return ( 0 );
    }


  @Override
  public void writeToParcel( Parcel targetParcel, int flags )
    {
    mAsset.writeToParcel( targetParcel, flags );

    targetParcel.writeInt( mQuantity );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the asset.
   *
   *****************************************************/
  public Asset getAsset()
    {
    return ( mAsset );
    }


  /*****************************************************
   *
   * Returns the quantity.
   *
   *****************************************************/
  public int getQuantity()
    {
    return ( mQuantity );
    }


  /*****************************************************
   *
   * Decrements the quantity by 1 and returns the new value.
   * Will not decrement past 0.
   *
   *****************************************************/
  public int decrement()
    {
    if ( mQuantity > 0 ) mQuantity --;

    return ( mQuantity );
    }


  /*****************************************************
   *
   * Increments the quantity by 1 and returns the new value.
   *
   *****************************************************/
  public int increment()
    {
    return ( ++ mQuantity );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

