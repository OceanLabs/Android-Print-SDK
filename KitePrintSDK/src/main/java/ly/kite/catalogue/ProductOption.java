/*****************************************************
 *
 * ProductOption.java
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


///// Class Declaration /////

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/*****************************************************
 *
 * This class represents a product option.
 *
 *****************************************************/
public class ProductOption implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "ProductOption";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<ProductOption> CREATOR =
          new Parcelable.Creator<ProductOption>()
          {
          public ProductOption createFromParcel( Parcel sourceParcel )
            {
            return (new ProductOption( sourceParcel ) );
            }

          public ProductOption[] newArray( int size )
            {
            return (new ProductOption[ size ]);
            }
          };


  ////////// Member Variable(s) //////////

  final private String            mCode;
  final private String            mName;

  final private ArrayList<Value>  mValueList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  ProductOption( String code, String name )
    {
    mCode      = code;
    mName      = name;

    mValueList = new ArrayList<>( 2 );
    }


  // Constructor used by parcelable interface
  private ProductOption( Parcel sourceParcel )
    {
    mCode = sourceParcel.readString();
    mName = sourceParcel.readString();

    mValueList = new ArrayList<>( 2 );

    sourceParcel.readList( mValueList, Value.class.getClassLoader() );
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
    targetParcel.writeString( mCode );
    targetParcel.writeString( mName );

    targetParcel.writeList( mValueList );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the option code.
   *
   *****************************************************/
  public String getCode()
    {
    return ( mCode );
    }


  /*****************************************************
   *
   * Returns the option name.
   *
   *****************************************************/
  public String getName()
    {
    return ( mName );
    }


  /*****************************************************
   *
   * Adds a value to this option.
   *
   *****************************************************/
  void addValue( String code, String name )
    {
    mValueList.add( new Value( code, name ) );
    }


  /*****************************************************
   *
   * Returns a list of values for this option.
   *
   *****************************************************/
  public ArrayList<Value> getValueList()
    {
    return ( mValueList );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An option value.
   *
   *****************************************************/
  public static class Value implements Parcelable
    {
    ////////// Static Variable(s) //////////

    public static final Parcelable.Creator<Value> CREATOR =
      new Parcelable.Creator<Value>()
        {
        public Value createFromParcel( Parcel sourceParcel )
          {
          return ( new Value( sourceParcel ) );
          }

        public Value[] newArray( int size )
          {
          return ( new Value[ size ] );
          }
        };


    ////////// Member Variable(s) //////////

    private String  mCode;
    private String  mName;


    ////////// Constructor(s) //////////

    Value( String code, String name )
      {
      mCode = code;
      mName = name;
      }

    Value( Parcel sourceParcel )
      {
      mCode = sourceParcel.readString();
      mName = sourceParcel.readString();
      }


    ////////// Parcelable Method(s) //////////

    @Override
    public int describeContents()
      {
      return ( 0 );
      }

    @Override
    public void writeToParcel( Parcel dest, int flags )
      {
      dest.writeString( mCode );
      dest.writeString( mName );
      }


    ////////// Method(s) //////////

    public String getCode()
      {
      return ( mCode );
      }

    public String getName()
      {
      return ( mName );
      }

    @Override
    public String toString()
      {
      return ( mName );
      }

    }

  }

