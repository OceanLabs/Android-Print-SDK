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

package ly.kite.journey;


///// Import(s) /////

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import ly.kite.R;
import ly.kite.product.ProductLoader;
import ly.kite.product.IGroupOrProduct;
import ly.kite.util.ImageLoader;
import ly.kite.widget.HeaderFooterGridView;
import ly.kite.widget.LabelledImageView;


///// Class Declaration /////

/*****************************************************
 *
 * This is the super class of the Product Group and
 * Product fragments.
 *
 *****************************************************/
abstract public class AGroupOrProductFragment extends AKiteFragment implements ProductLoader.ProductConsumer, AdapterView.OnItemClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  public static final String  TAG = "AGroupOrProductFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected HeaderFooterGridView  mGridView;
  protected ProgressBar           mProgressBar;

  protected ProductLoader         mProductLoader;
  protected BaseAdapter           mGridAdaptor;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


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
    mProgressBar = (ProgressBar)view.findViewById( R.id.progress_bar );


    setManagedAdaptorView( mGridView );


    return ( view );
    }


  /*****************************************************
   *
   * Called when the fragment becomes visible.
   *
   *****************************************************/
  @Override
  public void onStart()
    {
    super.onStart();


    getProducts();
    }


  /*****************************************************
   *
   * Called after the fragment is no longer visible.
   *
   *****************************************************/
  @Override
  public void onStop()
    {
    super.onStop();


    // Clear out the stored images to reduce memory usage
    // when not on this screen.

    mGridView.setAdapter( null );

    mGridAdaptor = null;
    }


  ////////// ProductSyncer.SyncListener Method(s) //////////

  /*****************************************************
   *
   * Called when the sync completes successfully.
   *
   *****************************************************/
  @Override
  public void onProductRetrievalError( Exception exception )
    {
    // Don't do anything if the activity is no longer visible
    if ( ! mKiteActivity.isVisible() ) return;

    onProductFetchFinished();

    mKiteActivity.displayModalDialog
            (
                    R.string.alert_dialog_title_error_retrieving_products,
                    R.string.alert_dialog_message_error_retrieving_products,
                    R.string.Retry,
                    new SyncProductsRunnable(),
                    R.string.Cancel,
                    mKiteActivity.new FinishRunnable()
            );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Gets the products.
   *
   *****************************************************/
  protected void getProducts()
    {
    mProgressBar.setVisibility( View.VISIBLE );


    // Request a set of products. We supply a handler because we don't want to be called
    // back immediately - often the GridView won't have been configured correctly yet (because
    // when we specify the number of columns it doesn't take effect immediately).

    mProductLoader = ProductLoader.getInstance( mKiteActivity );

    mProductLoader.getAllProducts( MAX_ACCEPTED_PRODUCT_AGE_MILLIS, this );
    }


  /*****************************************************
   *
   * Updates the UI (i.e. removes the progress spinner)
   * when syncing has finished, regardless of whether there
   * was an error or not.
   *
   *****************************************************/
  public void onProductFetchFinished()
    {
    mProgressBar.setVisibility( View.GONE );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Starts a product sync.
   *
   *****************************************************/
  private class SyncProductsRunnable implements Runnable
    {
    @Override
    public void run()
      {
      getProducts();
      }
    }


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

    private static final float   DEFAULT_ASPECT_RATIO = 1.389f;


    ////////// Static Variable(s) //////////


    ////////// Member Variable(s) //////////

    private Context mContext;
    private List<? extends IGroupOrProduct> mGroupOrProductList;
    private GridView                         mGridView;

    private int                              mActualItemCount;
    private int                              mApparentItemCount;
    private String                           mPlaceholderImageURLString;
    private URL mPlaceholderImageURL;

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
        view = mLayoutInflator.inflate( R.layout.grid_item_product_image, null );

        viewReferences                   = new ViewReferences();
        viewReferences.productImageView = (LabelledImageView)view.findViewById( R.id.product_image_view );

        view.setTag( viewReferences );
        }


      // Get the item we are displaying; set the label, and request the image from the image manager. We
      // also need to set the size of the image. Show placeholders for any missing items.

      IGroupOrProduct groupOrProduct = (IGroupOrProduct)getItem( position );

      viewReferences.productImageView.setAspectRatio( DEFAULT_ASPECT_RATIO );

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


      viewReferences.productImageView.setKey( imageURL );

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

  }

