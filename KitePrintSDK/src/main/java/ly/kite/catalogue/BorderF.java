/*****************************************************
 *
 * BorderF.java
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


///// Class Declaration /////

/*****************************************************
 *
 * This class represents a border with float values.
 *
 *****************************************************/
public class BorderF implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "BorderF";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<BorderF> CREATOR =
    new Parcelable.Creator<BorderF>()
      {
      public BorderF createFromParcel( Parcel sourceParcel )
        {
        return ( new BorderF( sourceParcel ) );
        }

      public BorderF[] newArray( int size )
        {
        return ( new BorderF[ size ] );
        }
      };


  ////////// Member Variable(s) //////////

  final public float left;
  final public float top;
  final public float right;
  final public float bottom;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public BorderF( float top, float right, float bottom, float left )
    {
    this.top    = top;
    this.right  = right;
    this.bottom = bottom;
    this.left   = left;
    }

  public BorderF()
    {
    this( 0f, 0f, 0f, 0f );
    }

  // Constructor used by parcelable interface
  private BorderF( Parcel sourceParcel )
    {
    this.top    = sourceParcel.readFloat();
    this.left   = sourceParcel.readFloat();
    this.right  = sourceParcel.readFloat();
    this.bottom = sourceParcel.readFloat();
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
    targetParcel.writeFloat( this.top );
    targetParcel.writeFloat( this.left );
    targetParcel.writeFloat( this.right );
    targetParcel.writeFloat( this.bottom );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Creates a string representation of this BorderF.
   *
   *****************************************************/
  @Override
  public String toString()
    {
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder
            .append( "{ top = " ).append( this.top )
            .append( ", left = " ).append( this.left )
            .append( ", right = " ).append( this.right )
            .append( ", bottom = " ).append( this.bottom ).append( " }" );

    return ( stringBuilder.toString() );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

