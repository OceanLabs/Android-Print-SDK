/*****************************************************
 *
 * ProductImageAdaptor.java
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

package ly.kite.journey.selection;


///// Import(s) /////

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.URL;
import java.util.List;

import ly.kite.R;
import ly.kite.image.ImageAgent;
import ly.kite.widget.LabelledImageView;


///// Class Declaration /////

/*****************************************************
 *
 * This class acts as an adaptor between product images
 * and a pager view.
 *
 *****************************************************/
public class ProductImagePagerAdaptor extends PagerAdapter
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG              = "ProductImageAdaptor";

  private static final String  IMAGE_CLASS_STRING   = "product_image";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context               mContext;
  private List<URL>             mImageURLList;
  private View.OnClickListener  mOnClickListener;

  private LayoutInflater        mLayoutInflator;
  private ImageAgent            mImageAgent;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public ProductImagePagerAdaptor( Context context, List<URL> imageURLList, View.OnClickListener onClickListener )
    {
    mContext         = context;
    mImageURLList    = imageURLList;
    mOnClickListener = onClickListener;

    mLayoutInflator  = LayoutInflater.from( context );
    mImageAgent      = ImageAgent.getInstance( context );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the number of images.
   *
   *****************************************************/
  @Override
  public int getCount()
    {
    return ( mImageURLList.size() );
    }


  /*****************************************************
   *
   * Creates a new page for the given position.
   *
   *****************************************************/
  @Override
  public Object instantiateItem( ViewGroup container, int position )
    {
    // Get the image URL for the position
    URL imageURL = mImageURLList.get( position );


    // Inflate the view

    View view = mLayoutInflator.inflate( R.layout.pager_item_product_overview_image, container, false );

    container.addView( view );


    // Set up the view

    LabelledImageView labelledImageView = (LabelledImageView)view.findViewById( R.id.labelled_image_view );

    labelledImageView.setOnClickListener( mOnClickListener );  // The view pager won't respond to click events, so we need to add them to each page

    labelledImageView.requestScaledImageOnceSized( IMAGE_CLASS_STRING, imageURL );


    return ( view );
    }


  /*****************************************************
   *
   * Returns true if the view is associated with the object.
   * Since we return the view anyway, this is true if the
   * view and the object are the same object.
   *
   *****************************************************/
  @Override
  public boolean isViewFromObject( View view, Object object )
    {
    return ( view == object );
    }


  /*****************************************************
   *
   * Destroys an item.
   *
   *****************************************************/
  @Override
  public void destroyItem( ViewGroup container, int position, Object object )
    {
    container.removeView( (View)object );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

