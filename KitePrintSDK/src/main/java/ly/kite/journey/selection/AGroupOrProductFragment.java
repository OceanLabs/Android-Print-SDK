/*****************************************************
 *
 * AGroupOrProductFragment.java
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
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.IGroupOrProduct;
import ly.kite.catalogue.ProductGroup;
import ly.kite.image.ImageAgent;
import ly.kite.image.ImageLoadRequest;
import ly.kite.widget.AREImageView;
import ly.kite.widget.HeaderFooterGridView;
import ly.kite.widget.LabelledImageView;


///// Class Declaration /////

/*****************************************************
 *
 * This is the super class of the Product Group and
 * Product fragments.
 *
 *****************************************************/
abstract public class AGroupOrProductFragment extends AProductSelectionFragment implements AdapterView.OnItemClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                = "AGroupOrProductFragment";

  static private final String  BUNDLE_KEY_PRODUCT_IDS = "productIds";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected String[]              mProductIds;

  protected HeaderFooterGridView  mGridView;

  protected BaseAdapter           mGridAdaptor;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates common fragments arguments.
   *
   *****************************************************/
  static protected Bundle addCommonArguments( AGroupOrProductFragment fragment, String... productIds )
    {
    Bundle arguments = new Bundle();

    if ( productIds != null && productIds.length > 0 )
      {
      arguments.putStringArray( BUNDLE_KEY_PRODUCT_IDS, productIds );
      }

    fragment.setArguments( arguments );

    return ( arguments );
    }


  ////////// Constructor(s) //////////


  ////////// Fragment Method(s) //////////

  /*****************************************************
   *
   * Called when the fragment is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // Try to get any common arguments

    Bundle arguments = getArguments();

    if ( arguments != null )
      {
      mProductIds = arguments.getStringArray( BUNDLE_KEY_PRODUCT_IDS );
      }
    }


  /*****************************************************
   *
   * Returns the content view for this fragment
   *
   *****************************************************/
  public View onCreateView( LayoutInflater layoutInflator, int layoutResourceId, ViewGroup container, Bundle savedInstanceState )
    {
    View view = layoutInflator.inflate( layoutResourceId, container, false );

    mGridView = (HeaderFooterGridView)view.findViewById( R.id.grid_view );


    setManagedAdaptorView( mGridView );


    return ( view );
    }


  /*****************************************************
   *
   * Called when the fragment is on top.
   *
   *****************************************************/
  @Override
  public void onTop()
    {
    super.onTop();

    requestCatalogue();
    }


  /*****************************************************
   *
   * Called when the fragment is not on top.
   *
   *****************************************************/
  @Override
  public void onNotTop()
    {
    super.onNotTop();

    if ( mGridView != null ) mGridView.setAdapter( null );

    mGridAdaptor = null;
    }


  ////////// Method(s) //////////


  ////////// Inner Class(es) //////////

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

    //private static final float   DEFAULT_ASPECT_RATIO = 1.389f;


    ////////// Static Variable(s) //////////


    ////////// Member Variable(s) //////////

    private Context                          mContext;
    private List<? extends IGroupOrProduct>  mGroupOrProductList;
    private GridView                         mGridView;
    private int                              mLayoutResourceId;

    private int                              mActualItemCount;
    private int                              mApparentItemCount;

    private LayoutInflater                   mLayoutInflator;


    ////////// Static Initialiser(s) //////////


    ////////// Static Method(s) //////////


    ////////// Constructor(s) //////////

    GroupOrProductAdaptor( Context context, List<? extends IGroupOrProduct> displayItemList, GridView gridView, int layoutResourceId )
      {
      mContext            = context;
      mGroupOrProductList = displayItemList;
      mGridView           = gridView;
      mLayoutResourceId   = layoutResourceId;

      mLayoutInflator     = LayoutInflater.from( context );

      mActualItemCount    = mGroupOrProductList.size();
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
      return ( position >= 0 && position < mActualItemCount ? mGroupOrProductList.get( position ) : null );
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

      Object      tagObject;
      View        view;
      ViewHolder  viewHolder;

      if ( convertView != null &&
           ( tagObject = convertView.getTag() ) != null &&
           ( tagObject instanceof ViewHolder ) )
        {
        view       = convertView;
        viewHolder = (ViewHolder)tagObject;
        }
      else
        {
        view       = mLayoutInflator.inflate( mLayoutResourceId, null );
        viewHolder = new ViewHolder( view );

        view.setTag( viewHolder );
        }


      // Get the item we are displaying; set the label, and request the image from the image manager.
      // Show placeholders for any missing items.

      IGroupOrProduct groupOrProduct = (IGroupOrProduct)getItem( position );

      viewHolder.bind( groupOrProduct, parent );


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
      LabelledImageView  labelledImageView;
      AREImageView       areImageView;
      TextView           placeholderTextView;
      TextView           labelTextView;
      TextView           descriptionTextView;
      FrameLayout        priceOverlayFrame;
      TextView           fromTextView;
      TextView           priceTextView;
      View               overlayIndicatorView;


      ViewHolder( View view )
        {
        this.labelledImageView    = (LabelledImageView)view.findViewById( R.id.labelled_image_view );
        this.areImageView         = (AREImageView)view.findViewById( R.id.are_image_view );
        this.placeholderTextView  = (TextView)view.findViewById( R.id.placeholder_text_view );
        this.labelTextView        = (TextView)view.findViewById( R.id.label_text_view );
        this.descriptionTextView  = (TextView)view.findViewById( R.id.description_text_view );
        this.priceOverlayFrame    = (FrameLayout)view.findViewById( R.id.price_overlay_frame );
        this.fromTextView         = (TextView)view.findViewById( R.id.from_text_view );
        this.priceTextView        = (TextView)view.findViewById( R.id.price_text_view );
        this.overlayIndicatorView = view.findViewById( R.id.overlay_indicator_view );
        }


      void bind( IGroupOrProduct groupOrProduct, ViewGroup parent )
        {
        // If there are only two items - set the aspect ratio so that the images
        // fill the screen, in either orientation.

        float aspectRatio;

        if ( getCount() == 2 )
          {
          int orientation = mContext.getResources().getConfiguration().orientation;

          if ( orientation == Configuration.ORIENTATION_LANDSCAPE )
            {
            aspectRatio = parent.getWidth() * 0.5f / parent.getHeight();
            }
          else
            {
            aspectRatio = parent.getWidth() / ( parent.getHeight() * 0.5f );
            }

          if ( this.labelledImageView != null ) this.labelledImageView.setImageAspectRatio( aspectRatio );
          if ( this.areImageView      != null ) this.areImageView.setAspectRatio( aspectRatio );
          }


        boolean showPlaceholder = false;

        Object  imageSourceObject;

        if ( groupOrProduct != null )
          {
          ///// Group / Product image /////

          // Set any theme colour
          setThemeColour( mCatalogue.getPrimaryThemeColour(), this.labelledImageView );

          if ( this.labelledImageView != null )
            {
            this.labelledImageView.setImageAnchorGravity( groupOrProduct.getDisplayImageAnchorGravity( mContext ) );
            this.labelledImageView.setLabel( groupOrProduct.getDisplayLabel(), groupOrProduct.getDisplayLabelColour() );
            }

          if ( this.labelTextView != null ) this.labelTextView.setText( groupOrProduct.getDisplayLabel() );

          if ( this.descriptionTextView != null ) this.descriptionTextView.setText( groupOrProduct.getDescription() );

          // Populate any price overlay
          String displayPrice = groupOrProduct.getDisplayPrice( KiteSDK.getInstance( getActivity() ).getLockedCurrencyCode() );


          // We only display the price overlay if there's a display price. In the case of a product group
          // this will be if there is a common currency.

          if ( displayPrice != null )
            {
            if ( this.priceOverlayFrame != null ) this.priceOverlayFrame.setVisibility( View.VISIBLE );
            if ( this.fromTextView      != null ) this.fromTextView.setVisibility( groupOrProduct.containsMultiplePrices() ? View.VISIBLE : View.GONE );
            if ( this.priceTextView     != null )
              {
              this.priceTextView.setVisibility( View.VISIBLE );
              this.priceTextView.setText( displayPrice );
              }
            }
          else
            {
            if ( this.priceOverlayFrame != null ) this.priceOverlayFrame.setVisibility( View.GONE );
            if ( this.fromTextView      != null ) this.fromTextView.setVisibility( View.GONE );
            if ( this.priceTextView     != null ) this.priceTextView.setVisibility( View.GONE );
            }

          imageSourceObject = groupOrProduct.getDisplayImageURL();
          }
        else
          {
          ///// Placeholder image /////

          if ( this.labelledImageView != null ) this.labelledImageView.setLabel( null );
          if ( this.labelTextView     != null ) this.labelTextView.setText( null );

          // No prices should be visible for a placeholder image
          if ( this.priceOverlayFrame != null ) this.priceOverlayFrame.setVisibility( View.GONE );
          if ( this.fromTextView      != null ) this.fromTextView.setVisibility( View.GONE );
          if ( this.priceTextView     != null ) this.priceTextView.setVisibility( View.GONE );

          imageSourceObject = Integer.valueOf( R.drawable.placeholder );

          showPlaceholder = true;
          }


        if ( this.labelledImageView != null )
          {
          this.labelledImageView.requestScaledImageOnceSized( KiteSDK.IMAGE_CATEGORY_PRODUCT_ITEM, imageSourceObject );
          }

        if ( this.areImageView != null )
          {
          ImageAgent imageAgent = ImageAgent.with( getActivity() );

          ImageLoadRequest.Builder imageLoadRequestBuilder = null;

          if      ( imageSourceObject instanceof URL     ) imageLoadRequestBuilder = imageAgent.load( (URL)imageSourceObject, KiteSDK.IMAGE_CATEGORY_PRODUCT_ITEM );
          else if ( imageSourceObject instanceof Integer ) imageLoadRequestBuilder = imageAgent.load( (Integer)imageSourceObject );

          if ( imageLoadRequestBuilder != null )
            {
            imageLoadRequestBuilder
                    .reduceColourSpace()
                    .resizeForIfSized( this.areImageView )
                    .onlyScaleDown()
                    .into( this.areImageView, imageSourceObject );
            }
          }

        if ( this.placeholderTextView != null )
          {
          this.placeholderTextView.setVisibility( showPlaceholder ? View.VISIBLE : View.GONE );
          }

        // If there is an overlay indicator view, set its visibility according to any
        // product flag.

        if ( this.overlayIndicatorView != null )
          {
          Object tagObject = this.overlayIndicatorView.getTag();

          if ( tagObject != null &&
               tagObject instanceof String &&
               groupOrProduct != null &&
               groupOrProduct.flagIsSet( tagObject.toString() ) )
            {
            this.overlayIndicatorView.setVisibility(  View.VISIBLE );
            }
          else
            {
            this.overlayIndicatorView.setVisibility(  View.GONE );
            }
          }
        }

      }

    }

  }

