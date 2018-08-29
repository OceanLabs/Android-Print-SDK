/*****************************************************
 *
 * SimpleParentItem.java
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
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents a parent picker item. It may be used
 * to create an arbitrary hierarchy of picker items. A parent
 * picker item can contain other parents or children.
 *
 *****************************************************/
public class SimpleParentItem implements IImagePickerItem
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "SimpleParentItem";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private String                       mKey;
  private String                       mName;
  private ArrayList<IImagePickerItem>  mSubItemList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public SimpleParentItem( String key, String name )
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


  public SimpleParentItem( Parcel inParcel )
    {
    mKey  = inParcel.readString();
    mName = inParcel.readString();
    }


  ////////// IImagePickerItem Method(s) //////////

  /*****************************************************
   *
   * Returns the thumbnail image URL. We use the first
   * image in the group for this purpose.
   *
   *****************************************************/
  @Override
  public void loadThumbnailImageInto( ImageView imageView )
    {
    Picasso.get()
            .load( mSubItemList.get( 0 ).getImageURLString() )
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
   * Returns a key if this item is a parent and can be
   * descended into.
   *
   *****************************************************/
  public String getKeyIfParent()
    {
    return ( mKey );
    }


  /*****************************************************
   *
   * Returns a selectable item if this item may be selected,
   * null otherwise.
   *
   *****************************************************/
  public ISelectableItem getSelectableItem()
    {
    return ( null );
    }


  /*****************************************************
   *
   * Returns true if the group has selected children.
   *
   *****************************************************/
  @Override
  public int getSelectedCount( LinkedHashMap<String,ISelectableItem> selectableItemTable )
    {
    // Go through all the images in this group and count ones that are selected

    int count = 0;

    for ( IImagePickerItem subItem : mSubItemList )
      {
      count += subItem.getSelectedCount( selectableItemTable );
      }

    return ( count );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Adds an image to the group.
   *
   *****************************************************/
  public void addImage( SimpleChildItem image )
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

