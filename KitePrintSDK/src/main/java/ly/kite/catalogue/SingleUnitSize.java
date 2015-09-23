/*****************************************************
 *
 * SingleUnitSize.java
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

import android.os.Parcel;
import android.os.Parcelable;

import ly.kite.KiteSDK;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents a size in a length unit.
 *
 *****************************************************/
public class SingleUnitSize implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG              = "SingleUnitSize";

  public  static final float   DEFAULT_ASPECT_RATIO = 1.0f;


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<SingleUnitSize> CREATOR =
    new Parcelable.Creator<SingleUnitSize>()
      {
      public SingleUnitSize createFromParcel( Parcel sourceParcel )
        {
        return ( new SingleUnitSize( sourceParcel ) );
        }

      public SingleUnitSize[] newArray( int size )
        {
        return ( new SingleUnitSize[ size ] );
        }
      };


  ////////// Member Variable(s) //////////

  private UnitOfLength  mUnit;
  private float         mWidth;
  private float         mHeight;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public SingleUnitSize( UnitOfLength unit, float width, float height )
    {
    mUnit   = unit;
    mWidth  = width;
    mHeight = height;
    }


  // Constructor used by parcelable interface
  private SingleUnitSize( Parcel sourceParcel )
    {
    mUnit   = UnitOfLength.valueOf( sourceParcel.readString() );
    mWidth  = sourceParcel.readFloat();
    mHeight = sourceParcel.readFloat();
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
    targetParcel.writeString( mUnit.name() );
    targetParcel.writeFloat( mWidth );
    targetParcel.writeFloat( mHeight );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the unit.
   *
   *****************************************************/
  public UnitOfLength getUnit()
    {
    return ( mUnit );
    }


  /*****************************************************
   *
   * Returns the width.
   *
   *****************************************************/
  public float getWidth()
    {
    return ( mWidth );
    }


  /*****************************************************
   *
   * Returns the height.
   *
   *****************************************************/
  public float getHeight()
    {
    return ( mHeight );
    }


  /*****************************************************
   *
   * Returns the aspect ratio.
   *
   *****************************************************/
  public float getAspectRatio()
    {
    // Avoid divide by zero
    if ( mHeight >= KiteSDK.FLOAT_ZERO_THRESHOLD ) return ( mWidth / mHeight );

    return ( DEFAULT_ASPECT_RATIO );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

