/*****************************************************
 *
 * ParentItem.java
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents a parent picker item. It may be used
 * to create an arbitrary hierarchy of picker items. A parent
 * picker item can contain other parents or children.
 *
 *****************************************************/
public class ParentItem implements IImagePickerItem
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG          = "ParentItem";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private String                       mKey;
  private String                       mName;
  private ArrayList<IImagePickerItem>  mSubItemList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public ParentItem( String key, String name )
    {
    // Make sure we were supplied a key
    if ( key == null || key.trim().equals( "" ) )
      {
      throw ( new IllegalArgumentException( "A key must be supplied" ) );
      }

    mKey         = key;
    mName        = name;

    mSubItemList = new ArrayList<>();
    }


  ////////// IImagePickerItem Method(s) //////////

  /*****************************************************
   *
   * Returns the thumbnail image URL. We use the first
   * image in the group for this purpose.
   *
   *****************************************************/
  @Override
  public String getThumbnailImageURLString()
    {
    return ( mSubItemList.get( 0 ).getThumbnailImageURLString() );
    }


  /*****************************************************
   *
   * Returns the full image URL.
   *
   *****************************************************/
  @Override
  public String getImageURLString()
    {
    // There is no full image for an album since it can't be
    // selected.
    return ( null );
    }


  /*****************************************************
   *
   * Returns the label.
   *
   *****************************************************/
  @Override
  public String getLabel()
    {
    return ( mName );
    }


  /*****************************************************
   *
   * Returns a non-null unique string identifying the parent
   * to the application.
   *
   *****************************************************/
  @Override
  public String getParentKey()
    {
    return ( mKey );
    }


  /*****************************************************
   *
   * Returns true if the group has selected children.
   *
   *****************************************************/
  @Override
  public int getSelectedCount( HashSet<String> urlStringSet )
    {
    // Go through all the images in this group and count ones that are selected

    int count = 0;

    for ( IImagePickerItem subItem : mSubItemList )
      {
      count += subItem.getSelectedCount( urlStringSet );
      }

    return ( count );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Adds an image to the group.
   *
   *****************************************************/
  public void addImage( ChildItem image )
    {
    mSubItemList.add( image );
    }


  /*****************************************************
   *
   * Returns the image list.
   *
   *****************************************************/
  public List<IImagePickerItem> getSubItemList()
    {
    return ( mSubItemList );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

