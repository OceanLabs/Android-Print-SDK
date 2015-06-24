/*****************************************************
 *
 * GroupOrProductAdaptor.java
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.shopping;


///// Import(s) /////

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import ly.kite.R;
import ly.kite.util.ImageManager;
import ly.kite.widget.GroupOrProductView;


///// Class Declaration /////

/*****************************************************
 *
 * This class is an adaptor for product groups or
 * products.
 *
 *****************************************************/
public class GroupOrProductAdaptor extends BaseAdapter
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG              = "GroupOrProductAdaptor";

  private static final int     LAYOUT_RESOURCE_ID   = R.layout.grid_item_group_or_product;

  private static final String  IMAGE_CLASS_STRING   = "product_item";

  private static final float   DEFAULT_ASPECT_RATIO = 1.389f;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context                         mContext;
  private List<? extends GroupOrProduct>  mDisplayItemList;

  private LayoutInflater                  mLayoutInflator;
  private ImageManager                    mImageManager;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  GroupOrProductAdaptor( Context context, List<? extends GroupOrProduct> displayItemList )
    {
    mContext         = context;
    mDisplayItemList = displayItemList;

    mLayoutInflator  = LayoutInflater.from( context );
    mImageManager    = ImageManager.getInstance( context );
    }


  ////////// BaseAdapter Method(s) //////////

  /*****************************************************
   *
   * Returns the number of product items.
   *
   *****************************************************/
  @Override
  public int getCount()
    {
    return ( mDisplayItemList.size() );
    }


  /*****************************************************
   *
   * Returns the product item at the requested position.
   *
   *****************************************************/
  @Override
  public Object getItem( int position )
    {
    return ( mDisplayItemList.get( position ) );
    }


  /*****************************************************
   *
   * Returns an id for the product item at the requested
   * position.
   *
   *****************************************************/
  @Override
  public long getItemId( int position )
    {
    return ( 0 );
    }


  /*****************************************************
   *
   * Returns the view for the product item at the requested
   * position.
   *
   *****************************************************/
  @Override
  public View getView( int position, View convertView, ViewGroup parent )
    {
    // Either re-use the convert view, or create a new one.

    Object          tagObject;
    View            view;
    ViewReferences  viewReferences;

    if ( convertView != null &&
            ( tagObject = convertView.getTag() ) != null &&
            ( tagObject instanceof ViewReferences ) )
      {
      view           = convertView;
      viewReferences = (ViewReferences)tagObject;
      }
    else
      {
      view = mLayoutInflator.inflate( LAYOUT_RESOURCE_ID, null );

      viewReferences                    = new ViewReferences();
      viewReferences.groupOrProductView = (GroupOrProductView)view.findViewById( R.id.group_or_product_view );

      view.setTag( viewReferences );
      }


    // Get the item we are displaying; set the label, and request the image from the image manager. We
    // also need to set the size of the image.

    GroupOrProduct groupOrProduct = (GroupOrProduct)getItem( position );

    // TODO: If there are only two items, change the aspect ratio
    viewReferences.groupOrProductView.setAspectRatio( DEFAULT_ASPECT_RATIO );
    viewReferences.groupOrProductView.setLabel( groupOrProduct.getDisplayLabel(), groupOrProduct.getDisplayLabelColour() );

    viewReferences.groupOrProductView.setExpectedImageURL( groupOrProduct.getDisplayImageURL().toString() );

    mImageManager.getRemoteImage( IMAGE_CLASS_STRING, groupOrProduct.getDisplayImageURL(), parent.getHandler(), viewReferences.groupOrProductView );


    return ( view );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * References to views within the layout.
   *
   *****************************************************/
  private class ViewReferences
    {
    GroupOrProductView groupOrProductView;
    }

  }

