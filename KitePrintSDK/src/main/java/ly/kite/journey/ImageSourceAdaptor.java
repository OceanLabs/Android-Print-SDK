/*****************************************************
 *
 * ImageSourceAdaptor.java
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

package ly.kite.journey;


///// Import(s) /////

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * An adaptor for the image sources.
 *
 *****************************************************/
public class ImageSourceAdaptor extends BaseAdapter
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "ImageSourceAdaptor";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context             mContext;
  private List<AImageSource>  mImageSourceList;
  private int                 mItemLayoutResourceId;

  private LayoutInflater      mLayoutInflator;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public ImageSourceAdaptor( Context context, int itemLayoutResourceId )
    {
    mContext              = context;
    mItemLayoutResourceId = itemLayoutResourceId;

    mLayoutInflator       = LayoutInflater.from( context );
    }


  public ImageSourceAdaptor( Context context, int itemLayoutResourceId, List<AImageSource> imageSourceList )
    {
    this( context, itemLayoutResourceId );

    mImageSourceList = imageSourceList;
    }


  public ImageSourceAdaptor( Context context, int itemLayoutResourceId, AImageSource... imageSources )
    {
    this( context, itemLayoutResourceId );

    mImageSourceList = new ArrayList<>();

    if ( imageSources != null )
      {
      for ( AImageSource imageSource : imageSources ) mImageSourceList.add( imageSource );
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
    return ( mImageSourceList.size() );
    }


  /*****************************************************
   *
   * Returns the product item at the requested position.
   *
   *****************************************************/
  @Override
  public Object getItem( int position )
    {
    return ( mImageSourceList.get( position ) );
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
      view = mLayoutInflator.inflate( mItemLayoutResourceId, null );

      viewReferences                = new ViewReferences();
      viewReferences.backgroundView = view.findViewById( R.id.background_view );
      viewReferences.iconImageView  = (ImageView)view.findViewById( R.id.icon_image_view );
      viewReferences.labelTextView  = (TextView)view.findViewById( R.id.label_text_view );

      view.setTag( viewReferences );
      }


    AImageSource imageSource = (AImageSource)getItem( position );

    viewReferences.backgroundView.setBackgroundColor( mContext.getResources().getColor( imageSource.getBackgroundColourResourceId() ) );
    viewReferences.iconImageView.setImageResource( imageSource.getIconResourceId() );
    viewReferences.labelTextView.setText( imageSource.getLabelResourceId() );


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
    View       backgroundView;
    ImageView  iconImageView;
    TextView   labelTextView;
    }

  }

