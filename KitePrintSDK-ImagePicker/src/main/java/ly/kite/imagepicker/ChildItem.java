/*****************************************************
 *
 * ChildItem.java
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

package ly.kite.imagepicker;


///// Import(s) /////

import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;


///// Class Declaration /////

/*****************************************************
 *
 * This class is an implementation of an image picker
 * item that represents a single image.
 *
 *****************************************************/
public class ChildItem implements IImagePickerItem
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG          = "ChildItem";

  static private final long    serialVersionUID = 0L;


  ////////// Static Variable(s) //////////

//  public static final Parcelable.Creator<ChildItem> CREATOR =
//      new Parcelable.Creator<ChildItem>()
//          {
//          public ChildItem createFromParcel( Parcel sourceParcel )
//            {
//            return ( new ChildItem( sourceParcel ) );
//            }
//
//          public ChildItem[] newArray( int size )
//            {
//            return ( new ChildItem[ size ] );
//            }
//          };


  ////////// Member Variable(s) //////////

  private String  mURLString;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public ChildItem( String urlString )
    {
    mURLString = urlString;
    }

//  private ChildItem( Parcel sourceParcel )
//    {
//    mURLString = sourceParcel.readString();
//    }


  ////////// Serializable Method(s) //////////

  private void writeObject( ObjectOutputStream oos ) throws IOException
    {
    oos.writeObject( mURLString );
    }


  private void readObject( ObjectInputStream ois ) throws IOException, ClassNotFoundException
    {
    mURLString = (String)ois.readObject();
    }


  ////////// Parcelable Method(s) //////////

//  @Override
//  public int describeContents()
//    {
//    return ( 0 );
//    }
//
//  @Override
//  public void writeToParcel( Parcel targetParcel, int flags )
//    {
//    targetParcel.writeString( mURLString );
//    }


  ////////// IImagePickerItem Method(s) //////////

  /*****************************************************
   *
   * Returns the image URL.
   *
   *****************************************************/
  @Override
  public String getThumbnailImageURLString()
    {
    return ( mURLString );
    }


  /*****************************************************
   *
   * Returns the full image URL.
   *
   *****************************************************/
  @Override
  public String getImageURLString()
    {
    return ( mURLString );
    }


  /*****************************************************
   *
   * Returns the label.
   *
   *****************************************************/
  @Override
  public String getLabel()
    {
    return ( null );
    }


  /*****************************************************
   *
   * Returns a non-null unique string identifying the parent
   * to the application. Child items must return null.
   *
   *****************************************************/
  @Override
  public String getParentKey()
    {
    return ( null );
    }


  /*****************************************************
   *
   * Returns true if the item has selected children.
   *
   *****************************************************/
  @Override
  public int getSelectedCount( HashSet<String> urlStringSet )
    {
    return ( urlStringSet.contains( mURLString ) ? 1 : 0 );
    }


  ////////// Method(s) //////////


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

