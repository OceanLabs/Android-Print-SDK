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
 * An adaptor for each image on the review and edit screen.
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
      view       = mLayoutInflator.inflate( R.layout.grid_item_review_and_crop, parent, false );
      viewHolder = new ViewHolder( view );


      // We only need to set the overlay or border once, when the view is first created,
      // since any re-use of the view will keep the properties.

      BorderF imageBorder = null;

      if ( mProduct.flagIsSet( Product.Flag.SUPPORTS_TEXT_ON_BORDER ) )
        {
        imageBorder = new BorderF( 0.1f, 0.1f, 0.3f, 0.1f );
        }
      else
        {
        imageBorder = mProduct.getImageBorder();
        }

      if ( imageBorder != null )
        {
        viewHolder.framedImageView.setBackgroundColor( mContext.getResources().getColor( android.R.color.white ) );

        viewHolder.framedImageView.setPaddingProportions(
                imageBorder.left,
                imageBorder.top,
                imageBorder.right,
                imageBorder.bottom );
        }

      viewHolder.framedImageView.setStencil( mProduct.getUserJourneyType().editMaskResourceId() );

      // Set the aspect ratio of the review image to match the image aspect ratio
      viewHolder.framedImageView.setImageAspectRatio( mProduct.getImageAspectRatio() );

      view.setTag( viewHolder );
      }


    // Set up the view

    ImageSpec     imageSpec     = (ImageSpec)getItem( position );
    AssetFragment assetFragment = imageSpec.getAssetFragment();

    viewHolder.framedImageView.requestScaledImageOnceSized( assetFragment );

    if ( viewHolder.borderTextView != null )
      {
      viewHolder.borderTextView.setText( imageSpec.getBorderText() );
      }

    viewHolder.quantityTextView.setText( String.valueOf( imageSpec.getQuantity() ) );
    viewHolder.imageIndex = position;

    viewHolder.framedImageView.setOnClickListener( viewHolder );
    viewHolder.decreaseButton.setOnClickListener( viewHolder );
    viewHolder.increaseButton.setOnClickListener( viewHolder );
    viewHolder.editTextView.setOnClickListener( viewHolder );


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
  private class ViewHolder implements View.OnClickListener
    {
    FramedImageView  framedImageView;
    TextView         borderTextView;
    TextView         quantityTextView;
    Button           decreaseButton;
    Button           increaseButton;
    TextView         editTextView;

    int              imageIndex;


    ViewHolder( View view )
      {
      this.framedImageView  = (FramedImageView)view.findViewById( R.id.framed_image_view );
      this.borderTextView   = (TextView)view.findViewById( R.id.border_text_view );
      this.quantityTextView = (TextView)view.findViewById( R.id.quantity_text_view );
      this.decreaseButton   = (Button)view.findViewById( R.id.decrease_button );
      this.increaseButton   = (Button)view.findViewById( R.id.increase_button );
      this.editTextView     = (TextView)view.findViewById( R.id.edit_text_view );
      }


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
      else if ( view == this.editTextView )
        {
        ///// Edit /////

        mListener.onEdit( this.imageIndex );
        }
      }
    }

  }

