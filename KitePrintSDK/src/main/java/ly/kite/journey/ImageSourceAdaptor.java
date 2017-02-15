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

  private Context                  mContext;
  private List<AImageSource>       mImageSourceList;
  private AImageSource.LayoutType  mLayoutType;

  private LayoutInflater           mLayoutInflator;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public ImageSourceAdaptor( Context context, AImageSource.LayoutType layoutType )
    {
    mContext        = context;
    mLayoutType     = layoutType;

    mLayoutInflator = LayoutInflater.from( context );
    }


  public ImageSourceAdaptor( Context context, AImageSource.LayoutType layoutType, List<AImageSource> imageSourceList )
    {
    this( context, layoutType );

    mImageSourceList = imageSourceList;
    }


  public ImageSourceAdaptor( Context context, AImageSource.LayoutType layoutType, AImageSource... imageSources )
    {
    this( context, layoutType );

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
    AImageSource imageSource = (AImageSource)getItem( position );


    // Either re-use the convert view, or create a new one.

    Object          tagObject;
    View            view;
    ViewHolder viewHolder;

    if ( convertView != null &&
            ( tagObject = convertView.getTag() ) != null &&
            ( tagObject instanceof ViewHolder ) )
      {
      view       = convertView;
      viewHolder = (ViewHolder)tagObject;
      }
    else
      {
      view       = mLayoutInflator.inflate( imageSource.getLayoutResource( mLayoutType ), parent, false );
      viewHolder = new ViewHolder( view );

      view.setTag( viewHolder );
      }


    viewHolder.bind( imageSource );


    return ( view );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * References to views within the layout.
   *
   *****************************************************/
  private class ViewHolder
    {
    View       view;
    View       backgroundView;
    ImageView  iconImageView;
    TextView   labelTextView;


    ViewHolder( View view )
      {
      this.view           = view;
      this.backgroundView = view.findViewById( R.id.background_view );
      this.iconImageView  = (ImageView)view.findViewById( R.id.icon_image_view );
      this.labelTextView  = (TextView)view.findViewById( R.id.label_text_view );
      }


    void bind( AImageSource imageSource )
      {
      if ( this.backgroundView != null ) this.backgroundView.setBackgroundColor( mContext.getResources().getColor( imageSource.getBackgroundColourResourceId( mLayoutType ) ) );
      if ( this.iconImageView  != null ) this.iconImageView.setImageResource( imageSource.getIconResourceId( mLayoutType ) );
      if ( this.labelTextView  != null ) this.labelTextView.setText( imageSource.getLabelResourceId() );
      }
    }

  }

