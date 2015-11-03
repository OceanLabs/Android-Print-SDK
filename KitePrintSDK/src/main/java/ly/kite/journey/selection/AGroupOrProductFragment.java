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

import ly.kite.R;
import ly.kite.catalogue.ICatalogueConsumer;
import ly.kite.journey.AKiteActivity;
import ly.kite.journey.AKiteFragment;
import ly.kite.catalogue.IGroupOrProduct;
import ly.kite.util.ImageAgent;
import ly.kite.widget.HeaderFooterGridView;
import ly.kite.widget.LabelledImageView;


///// Class Declaration /////

/*****************************************************
 *
 * This is the super class of the Product Group and
 * Product fragments.
 *
 *****************************************************/
abstract public class AGroupOrProductFragment extends AKiteFragment implements ICatalogueConsumer, AdapterView.OnItemClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  public  static final String  TAG                    = "AGroupOrProductFragment";

  private static final String  BUNDLE_KEY_PRODUCT_IDS = "productIds";


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
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    View view = layoutInflator.inflate( R.layout.screen_choose_group_or_product, container, false );

    mGridView    = (HeaderFooterGridView)view.findViewById( R.id.grid_view );


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


    getProducts();
    }


  /*****************************************************
   *
   * Called when the fragment is not on top.
   *
   *****************************************************/
  @Override
  public void onNotTop()
    {
    if ( mGridView != null ) mGridView.setAdapter( null );

    mGridAdaptor = null;
    }


  ////////// CatalogueLoader.ICatalogueConsumer Method(s) //////////

  /*****************************************************
   *
   * Called when the sync completes successfully.
   *
   *****************************************************/
  @Override
  public void onCatalogueError( Exception exception )
    {
    // Don't do anything. Catalogue load errors are dealt with
    // by the activity.
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Gets the products.
   *
   *****************************************************/
  protected void getProducts()
    {
    if ( mKiteActivity instanceof ICatalogueHolder )
      {
      ( (ICatalogueHolder)mKiteActivity ).getCatalogue( this );
      }
    }


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
    private String                           mPlaceholderImageURLString;
    private URL                              mPlaceholderImageURL;

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
        view = mLayoutInflator.inflate( mLayoutResourceId, null );

        viewReferences                   = new ViewReferences();
        viewReferences.productImageView  = (LabelledImageView)view.findViewById( R.id.labelled_image_view );
        viewReferences.priceOverlayFrame = (FrameLayout)view.findViewById( R.id.price_overlay_frame );
        viewReferences.fromTextView      = (TextView)view.findViewById( R.id.from_text_view );
        viewReferences.priceTextView     = (TextView)view.findViewById( R.id.price_text_view );

        view.setTag( viewReferences );
        }


      // Get the item we are displaying; set the label, and request the image from the image manager.
      // Show placeholders for any missing items.

      IGroupOrProduct groupOrProduct = (IGroupOrProduct)getItem( position );


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

        // Make sure we only expand the height
        //if ( aspectRatio > DEFAULT_ASPECT_RATIO ) aspectRatio = DEFAULT_ASPECT_RATIO;

        viewReferences.productImageView.setImageAspectRatio( aspectRatio );
        }



      URL imageURL;

      if ( groupOrProduct != null )
        {
        ///// Group / Product image /////

        viewReferences.productImageView.setLabel( groupOrProduct.getDisplayLabel(), groupOrProduct.getDisplayLabelColour() );

        // Populate any price overlay
        if ( viewReferences.priceOverlayFrame != null ) viewReferences.priceOverlayFrame.setVisibility( View.VISIBLE );
        if ( viewReferences.fromTextView      != null ) viewReferences.fromTextView.setVisibility( groupOrProduct.containsMultiplePrices() ? View.VISIBLE : View.GONE );
        if ( viewReferences.priceTextView     != null ) viewReferences.priceTextView.setText( groupOrProduct.getDisplayPrice() );

        imageURL = groupOrProduct.getDisplayImageURL();
        }
      else
        {
        ///// Placeholder image /////

        viewReferences.productImageView.setLabel( null );

        // Any price overlay should not be visible for a placeholder image
        if ( viewReferences.priceOverlayFrame != null ) viewReferences.priceOverlayFrame.setVisibility( View.GONE );

        imageURL = mPlaceholderImageURL;
        }


      viewReferences.productImageView.requestScaledImageOnceSized( AKiteActivity.IMAGE_CLASS_STRING_PRODUCT_ITEM, imageURL );


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
      LabelledImageView  productImageView;
      FrameLayout        priceOverlayFrame;
      TextView           fromTextView;
      TextView           priceTextView;
      }

    }

  }

