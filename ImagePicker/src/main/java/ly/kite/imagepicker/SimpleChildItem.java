/*****************************************************
 *
 * SimpleChildItem.java
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

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.LinkedHashMap;


///// Class Declaration /////

/*****************************************************
 *
 * This class is an implementation of an image picker
 * item that represents a single image.
 *
 *****************************************************/
public class SimpleChildItem implements IImagePickerItem, ISelectableItem
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG          = "SimpleChildItem";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
    public SimpleChildItem createFromParcel( Parcel in )
      {
      return new SimpleChildItem( in );
      }

    public SimpleChildItem[] newArray( int size )
      {
      return new SimpleChildItem[ size ];
      }
    };


  ////////// Member Variable(s) //////////

  private String  mURLString;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public SimpleChildItem( String urlString )
    {
    mURLString = urlString;
    }

  private SimpleChildItem( Parcel sourceParcel )
    {
    mURLString = sourceParcel.readString();
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
    targetParcel.writeString( mURLString );
    }


  ////////// IImagePickerItem Method(s) //////////

  /*****************************************************
   *
   * Returns the image URL.
   *
   *****************************************************/
  @Override
  public void loadThumbnailImageInto( Context context, ImageView imageView )
    {
    Picasso.with( context )
            .load( mURLString )
            .resizeDimen( R.dimen.ip_image_default_resize_width, R.dimen.ip_image_default_resize_height )
            .centerCrop()
            .onlyScaleDown()
            .into( imageView );
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
   * Returns a key if this item is a parent and can be
   * descended into.
   *
   *****************************************************/
  public String getKeyIfParent()
    {
    return ( null );
    }


  /*****************************************************
   *
   * Returns a selectable item if this item may be selected,
   * null otherwise.
   *
   *****************************************************/
  public ISelectableItem getSelectableItem()
    {
    return ( this );
    }


  /*****************************************************
   *
   * Returns true if the item has selected children.
   *
   *****************************************************/
  @Override
  public int getSelectedCount( LinkedHashMap<String,ISelectableItem> selectableItemTable )
    {
    return ( selectableItemTable.containsKey( getKey() ) ? 1 : 0 );
    }


  ////////// ISelectableItem Method(s) //////////

  /*****************************************************
   *
   * Returns a key for the item.
   *
   *****************************************************/
  public String getKey()
    {
    return ( mURLString );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

