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

package ly.kite.product;


///// Import(s) /////

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import ly.kite.R;
import ly.kite.journey.AKiteActivity;
import ly.kite.util.ImageLoader;
import ly.kite.widget.LabelledImageView;


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

  private static final int     LAYOUT_RESOURCE_ID   = R.layout.grid_item_product_image;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context                          mContext;
  private List<? extends IGroupOrProduct>  mGroupOrProductList;
  private GridView                         mGridView;

  private int                              mActualItemCount;
  private int                              mApparentItemCount;
  private String                           mPlaceholderImageURLString;
  private URL                              mPlaceholderImageURL;

  private LayoutInflater                   mLayoutInflator;
  private ImageLoader mImageManager;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  GroupOrProductAdaptor( Context context, List<? extends IGroupOrProduct> displayItemList, GridView gridView )
    {
    mContext            = context;
    mGroupOrProductList = displayItemList;
    mGridView           = gridView;

    mLayoutInflator     = LayoutInflater.from( context );
    mImageManager       = ImageLoader.getInstance( context );

    mActualItemCount    = mGroupOrProductList.size();


    // Get the URL of the placeholder image

    mPlaceholderImageURLString = context.getString( R.string.group_or_product_placeholder_image_url );

    try
      {
      mPlaceholderImageURL = new URL( mPlaceholderImageURLString );
      }
    catch ( MalformedURLException mue )
      {
      }
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
    // We don't want to calculate the apparent item count in the constructor, because
    // setting the number of columns for the GridView is requested after the layout has
    // been inflated, and usually results in the adaptor being created before the change
    // has taken effect.

    int columnCount = mGridView.getNumColumns();

    // We always round the number of images to be a multiple of the column count, so we can display a placeholder
    // image in any 'missing' slots.
    mApparentItemCount = ( columnCount > 1 ? ( ( mActualItemCount + ( columnCount / 2 ) ) / columnCount ) * columnCount: mActualItemCount );

    return ( mApparentItemCount );
    }


  /*****************************************************
   *
   * Returns the product item at the requested position.
   *
   *****************************************************/
  @Override
  public Object getItem( int position )
    {
    return ( position < mActualItemCount ? mGroupOrProductList.get( position ) : null );
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

      viewReferences                   = new ViewReferences();
      viewReferences.productImageView = (LabelledImageView)view.findViewById( R.id.product_image_view );

      view.setTag( viewReferences );
      }


    // Get the item we are displaying; set the label, and request the image from the image manager. We
    // also need to set the size of the image. Show placeholders for any missing items.

    IGroupOrProduct groupOrProduct = (IGroupOrProduct)getItem( position );

    URL    imageURL;
    String imageURLString;

    if ( groupOrProduct != null )
      {
      viewReferences.productImageView.setLabel( groupOrProduct.getDisplayLabel(), groupOrProduct.getDisplayLabelColour() );

      imageURL       = groupOrProduct.getDisplayImageURL();
      imageURLString = imageURL.toString();
      }
    else
      {
      viewReferences.productImageView.setLabel( null );

      imageURL       = mPlaceholderImageURL;
      imageURLString = mPlaceholderImageURLString;
      }


    viewReferences.productImageView.clearForNewImage( imageURL );

    mImageManager.requestRemoteImage( AKiteActivity.IMAGE_CLASS_STRING_PRODUCT_ITEM, imageURL, parent.getHandler(), viewReferences.productImageView );


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
    LabelledImageView productImageView;
    }

  }

