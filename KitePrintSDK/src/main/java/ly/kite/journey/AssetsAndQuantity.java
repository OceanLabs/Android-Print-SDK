/*****************************************************
 *
 * AssetsAndQuantity.java
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

package ly.kite.journey;


///// Import(s) /////

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import ly.kite.product.Asset;


///// Class Declaration /////

/*****************************************************
 *
 * This class holds two assets and a quantity. The assets
 * are an original asset, and a cropped asset.
 *
 *****************************************************/
public class AssetsAndQuantity implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG       = "AssetsAndQuantity";

  private static final byte    TRUE_AS_BYTE  = 1;
  private static final byte    FALSE_AS_BYTE = 0;


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<AssetsAndQuantity> CREATOR = new Parcelable.Creator<AssetsAndQuantity>()
    {
    public AssetsAndQuantity createFromParcel( Parcel in )
      {
      return ( new AssetsAndQuantity( in ) );
      }

    public AssetsAndQuantity[] newArray( int size )
      {
      return ( new AssetsAndQuantity[ size ] );
      }
    };


  ////////// Member Variable(s) //////////

  private Asset            mUneditedAsset;
  private Asset            mEditedAsset;
  private int              mQuantity;

  private String           mEditedForProductId;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns true if the asset is in the list.
   *
   *****************************************************/
  static public boolean uneditedAssetIsInList( List<AssetsAndQuantity> assetsAndQuantityList, Asset soughtUneditedAsset )
    {
    for ( AssetsAndQuantity candidateAssetsAndQuantity : assetsAndQuantityList )
      {
      if ( candidateAssetsAndQuantity.getUneditedAsset().equals( soughtUneditedAsset ) ) return ( true );
      }

    return ( false );
    }


  ////////// Constructor(s) //////////

  public AssetsAndQuantity( Asset uneditedAsset, int quantity )
    {
    mUneditedAsset = uneditedAsset;
    mQuantity      = quantity;
    }


  private AssetsAndQuantity( Parcel sourceParcel )
    {
    mUneditedAsset = sourceParcel.readParcelable( Asset.class.getClassLoader() );

    if ( sourceParcel.readByte() == TRUE_AS_BYTE )
      {
      mEditedAsset = sourceParcel.readParcelable( Asset.class.getClassLoader() );
      }

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
    targetParcel.writeParcelable( mUneditedAsset, flags );

    if ( mEditedAsset != null )
      {
      targetParcel.writeByte( TRUE_AS_BYTE );

      targetParcel.writeParcelable( mEditedAsset, flags );
      }
    else
      {
      targetParcel.writeByte( FALSE_AS_BYTE );
      }

    targetParcel.writeInt( mQuantity );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the unedited asset.
   *
   *****************************************************/
  public Asset getUneditedAsset()
    {
    return ( mUneditedAsset );
    }


  /*****************************************************
   *
   * Sets the edited asset.
   *
   *****************************************************/
  public void setEditedAsset( Asset editedAsset, String editedForProductId )
    {
    mEditedAsset = editedAsset;

    // If the edited asset is being cleared - also clear
    // the edit for value.
    if ( editedAsset == null ) mEditedForProductId = null;
    else                       mEditedForProductId = editedForProductId;
    }


  /*****************************************************
   *
   * Returns the edited asset.
   *
   *****************************************************/
  public Asset getEditedAsset()
    {
    return ( mEditedAsset );
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
   * Returns the user journey type that the edited asset
   * was intended for.
   *
   *****************************************************/
  public String getEditedForProductId()
    {
    return ( mEditedForProductId );
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

