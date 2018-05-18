/*****************************************************
 *
 * AssetFragment.java
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

package ly.kite.util;


///// Import(s) /////

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ly.kite.image.ImageAgent;


///// Class Declaration /////

/*****************************************************
 *
 * An AssetFragment combines an Asset with a proportional
 * rectangle, to define a resultant image that may be all
 * or a part of the original source asset.
 *
 *****************************************************/
public class AssetFragment implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                     = "AssetFragment";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<AssetFragment> CREATOR = new Parcelable.Creator<AssetFragment>()
    {
    public AssetFragment createFromParcel( Parcel in )
      {
      return ( new AssetFragment( in ) );
      }

    public AssetFragment[] newArray( int size )
      {
      return ( new AssetFragment[ size ] );
      }
    };


  ////////// Member Variable(s) //////////

  private Asset    mAsset;

  // The fragment is stored as proportions of the asset. The left and right parameters are
  // fractions of the width; the top and bottom parameters are fractions of the height.
  private RectF    mProportionalRectangle;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns true if both the asset fragments are null, or equal.
   *
   *****************************************************/
  static public boolean areBothNullOrEqual( AssetFragment assetFragment1, AssetFragment assetFragment2 )
    {
    if ( assetFragment1 == null && assetFragment2 == null ) return ( true );
    if ( assetFragment1 == null || assetFragment2 == null ) return ( false );

    return ( assetFragment1.equals( assetFragment2 ) );
    }


  ////////// Constructor(s) //////////

  public AssetFragment( Asset asset, RectF proportionalRectangle )
    {
    mAsset = asset;

    setProportionalRectangle( proportionalRectangle );
    }

  public AssetFragment( Asset asset )
    {
    this( asset, null );
    }

  private AssetFragment( Parcel sourceParcel )
    {
    mAsset                 = sourceParcel.readParcelable( Asset.class.getClassLoader() );
    mProportionalRectangle = sourceParcel.readParcelable( RectF.class.getClassLoader() );
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
    targetParcel.writeParcelable( mAsset, flags );
    targetParcel.writeParcelable( mProportionalRectangle, flags );
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
   * Sets the fragment rectangle.
   *
   *****************************************************/
  public AssetFragment setProportionalRectangle( RectF proportionalRectangle )
    {
    mProportionalRectangle = ( proportionalRectangle != null ? proportionalRectangle : ImageAgent.FULL_PROPORTIONAL_RECTANGLE );

    return ( this );
    }


  /*****************************************************
   *
   * Returns the fragment rectangle.
   *
   *****************************************************/
  public RectF getProportionalRectangle()
    {
    return ( mProportionalRectangle );
    }


  /*****************************************************
   *
   * Returns true if the fragment is full size.
   *
   *****************************************************/
  public boolean isFullSize()
    {
    return ( mProportionalRectangle.left   <= 0.0f &&
             mProportionalRectangle.top    <= 0.0f &&
             mProportionalRectangle.right  >= 1.0f &&
             mProportionalRectangle.bottom >= 1.0f );
    }


  /*****************************************************
   *
   * Returns a string representation of this asset fragment.
   *
   *****************************************************/
  @Override
  public String toString()
    {
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder
            .append( mAsset.toString() )
            .append( " ( " )
            .append( String.valueOf( mProportionalRectangle.left   ) ).append( ", " )
            .append( String.valueOf( mProportionalRectangle.top    ) ).append( ", " )
            .append( String.valueOf( mProportionalRectangle.right  ) ).append( ", " )
            .append( String.valueOf( mProportionalRectangle.bottom ) )
            .append( " )" );

    return ( stringBuilder.toString() );
    }


  /*****************************************************
   *
   * Returns true if this asset fragment equals the supplied
   * asset fragment.
   *
   *****************************************************/
  @Override
  public boolean equals( Object otherObject )
    {
    if ( otherObject == null || ! ( otherObject instanceof AssetFragment ) )
      {
      return ( false );
      }

    AssetFragment otherAssetFragment = (AssetFragment) otherObject;


    if ( otherAssetFragment == this ) return ( true );


    return ( mAsset.equals( otherAssetFragment.mAsset ) &&
            mProportionalRectangle.equals( otherAssetFragment.mProportionalRectangle ) );
    }

  /*****************************************************
   *
   * Sets the asset preview image
   *
   *****************************************************/
  public void setAssetPreviewBitmap(Bitmap bitmap)
    {
      mAsset.setPreviewBitmap(bitmap);
    }

  /*****************************************************
   *
   * Returns the asset preview image
   *
   *****************************************************/
  public Bitmap getAssetPreviewBitmap()
    {
      return mAsset.getPreviewBitmap();
    }

  /*****************************************************
   *
   * Checks if there is a preview image available
   *
   *****************************************************/
  public Boolean hasAssetPreviewBitmap()
    {
      return mAsset.hasPreviewImage();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }
