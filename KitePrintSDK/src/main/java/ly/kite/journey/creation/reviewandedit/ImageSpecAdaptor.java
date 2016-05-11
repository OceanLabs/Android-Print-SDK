/*****************************************************
 *
 * ImageSpecAdaptor.java
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

package ly.kite.journey.creation.reviewandedit;


///// Import(s) /////

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ly.kite.R;
import ly.kite.ordering.ImageSpec;
import ly.kite.catalogue.BorderF;
import ly.kite.catalogue.Product;
import ly.kite.util.AssetFragment;
import ly.kite.widget.FramedImageView;
import ly.kite.widget.ViewHelper;


///// Class Declaration /////

/*****************************************************
 *
 * An adaptor for the image sources.
 *
 *****************************************************/
public class ImageSpecAdaptor extends BaseAdapter
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG          = "ImageSpecAdaptor";

  private static final int     MAX_BORDER_VALUE                      = 1000;
  private static final float   BORDER_VALUE_TO_PROPORTION_MULTIPLIER = 1.0f / MAX_BORDER_VALUE;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context                  mContext;
  private List<ImageSpec>          mImageSpecList;
  private Product                  mProduct;
  private IListener                mListener;

  private LayoutInflater           mLayoutInflator;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public ImageSpecAdaptor( Context context, List<ImageSpec> imageSpecList, Product product, IListener listener )
    {
    mContext         = context;
    mImageSpecList   = imageSpecList;
    mProduct         = product;
    mListener        = listener;

    mLayoutInflator  = LayoutInflater.from( context );
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
    return ( mImageSpecList.size() );
    }


  /*****************************************************
   *
   * Returns the product item at the requested position.
   *
   *****************************************************/
  @Override
  public Object getItem( int position )
    {
    return ( mImageSpecList.get( position ) );
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
      view                            = mLayoutInflator.inflate( R.layout.grid_item_review_and_crop, parent, false );
      viewReferences                  = new ViewReferences();
      viewReferences.framedImageView  = (FramedImageView)view.findViewById( R.id.framed_image_view );
      viewReferences.quantityTextView = (TextView)view.findViewById( R.id.quantity_text_view );
      viewReferences.decreaseButton   = (Button)view.findViewById( R.id.decrease_button );
      viewReferences.increaseButton   = (Button)view.findViewById( R.id.increase_button );
      viewReferences.editButton       = (Button)view.findViewById( R.id.edit_button );


      // We only need to set the overlay or border once, when the view is first created,
      // since any re-use of the view will keep the properties.

      BorderF imageBorder = mProduct.getImageBorder();

      if ( imageBorder != null )
        {
        viewReferences.framedImageView.setBackgroundColor( mContext.getResources().getColor( android.R.color.white ) );

        viewReferences.framedImageView.setPaddingProportions(
                imageBorder.left,
                imageBorder.top,
                imageBorder.right,
                imageBorder.bottom );
        }

      viewReferences.framedImageView.setStencil( mProduct.getUserJourneyType().editMaskResourceId() );

      // Set the aspect ratio of the review image to match the image aspect ratio
      viewReferences.framedImageView.setImageAspectRatio( mProduct.getImageAspectRatio() );

      view.setTag( viewReferences );
      }


    // Set up the view

    ImageSpec     imageSpec     = (ImageSpec)getItem( position );
    AssetFragment assetFragment = imageSpec.getAssetFragment();

    viewReferences.framedImageView.requestScaledImageOnceSized( assetFragment );

    viewReferences.quantityTextView.setText( String.valueOf( imageSpec.getQuantity() ) );
    viewReferences.imageIndex = position;

    viewReferences.framedImageView.setOnClickListener( viewReferences );
    viewReferences.decreaseButton.setOnClickListener( viewReferences );
    viewReferences.increaseButton.setOnClickListener( viewReferences );
    viewReferences.editButton.setOnClickListener( viewReferences );


    // Scan through the view hierarchy and set any special properties. This allows apps to apply
    // special features to the view, such as an overlay for certain products. This is either a stroke
    // of genius, or the dirtiest hack ever ... history will decide.

    ViewHelper.setAllViewProperties( view, mProduct );


    return ( view );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * The event listener.
   *
   *****************************************************/
  public interface IListener
    {
    public void onWantsToBeZero   ( int assetIndex );
    public void onQuantityChanged ( int assetIndex );
    public void onEdit            ( int assetIndex );
    }


  /*****************************************************
   *
   * References to views within the layout. This also
   * acts as the on click listener for the controls.
   *
   *****************************************************/
  private class ViewReferences implements View.OnClickListener
    {
    FramedImageView  framedImageView;
    TextView         quantityTextView;
    Button           decreaseButton;
    Button           increaseButton;
    Button           editButton;

    int              imageIndex;


    @Override
    public void onClick( View view )
      {
      ImageSpec imageSpec = mImageSpecList.get( this.imageIndex );

      if ( view == this.framedImageView )
        {
        ///// (Image) /////

        mListener.onEdit( this.imageIndex );
        }
      else if ( view == this.decreaseButton )
        {
        ///// Decrease /////

        // If the quantity would go to zero, notify the listener first.

        if ( imageSpec.getQuantity() <= 1 )
          {
          mListener.onWantsToBeZero( this.imageIndex );
          }
        else
          {
          this.quantityTextView.setText( String.valueOf( imageSpec.decrementQuantity() ) );

          mListener.onQuantityChanged( this.imageIndex );
          }
        }
      else if ( view == this.increaseButton )
        {
        ///// Increase /////

        this.quantityTextView.setText( String.valueOf( imageSpec.incrementQuantity() ) );

        mListener.onQuantityChanged( this.imageIndex );
        }
      else if ( view == this.editButton )
        {
        ///// Edit /////

        mListener.onEdit( this.imageIndex );
        }
      }
    }

  }

