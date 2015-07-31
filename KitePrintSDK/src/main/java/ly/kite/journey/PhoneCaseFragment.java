/*****************************************************
 *
 * PhoneCaseFragment.java
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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.net.URL;
import java.util.ArrayList;

import ly.kite.R;
import ly.kite.analytics.Analytics;
import ly.kite.product.Asset;
import ly.kite.product.AssetHelper;
import ly.kite.product.Bleed;
import ly.kite.product.Product;
import ly.kite.util.ImageLoader;
import ly.kite.widget.MaskedRemoteImageView;


///// Class Declaration /////

/*****************************************************
 *
 * This activity allows the user to create a phone
 * case design using an image.
 *
 *****************************************************/
public class PhoneCaseFragment extends AJourneyFragment implements View.OnClickListener
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String      LOG_TAG                      = "PhoneCaseFragment";

  public  static final String      BUNDLE_KEY_ASSET_LIST = "assetList";
  public  static final String      BUNDLE_KEY_PRODUCT    = "product";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ArrayList<Asset>       mAssetArrayList;
  private Product                mProduct;

  private MaskedRemoteImageView  mMaskedRemoteImageView;
  private Button                 mNextButton;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static PhoneCaseFragment newInstance( ArrayList<Asset> assetArrayList, Product product )
    {
    PhoneCaseFragment fragment = new PhoneCaseFragment();

    Bundle arguments = new Bundle();
    arguments.putParcelableArrayList( BUNDLE_KEY_ASSET_LIST, assetArrayList );
    arguments.putParcelable( BUNDLE_KEY_PRODUCT, product );

    fragment.setArguments( arguments );

    return ( fragment );
    }


  ////////// Constructor(s) //////////


  ////////// Activity Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // Get the assets and product

    Bundle arguments = getArguments();

    if ( arguments == null )
      {
      Log.e( LOG_TAG, "No arguments found" );

      mKiteActivity.displayModalDialog(
              R.string.alert_dialog_title_no_arguments,
              R.string.alert_dialog_message_no_arguments,
              AKiteActivity.DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              mKiteActivity.new FinishRunnable()
      );

      return;
      }

    if ( ( mAssetArrayList = arguments.getParcelableArrayList( BUNDLE_KEY_ASSET_LIST ) ) == null || mAssetArrayList.size() < 1 )
      {
      Log.e( LOG_TAG, "No asset list found" );

      mKiteActivity.displayModalDialog(
              R.string.alert_dialog_title_no_asset_list,
              R.string.alert_dialog_message_no_asset_list,
              AKiteActivity.DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              mKiteActivity.new FinishRunnable()
      );

      return;
      }


    mProduct = arguments.getParcelable( BUNDLE_KEY_PRODUCT );

    if ( mProduct == null )
      {
      Log.e( LOG_TAG, "No product found" );

      mKiteActivity.displayModalDialog(
              R.string.alert_dialog_title_product_not_found,
              R.string.alert_dialog_message_product_not_found,
              AKiteActivity.DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              mKiteActivity.new FinishRunnable()
      );

      return;
      }


    //this.setHasOptionsMenu( true );
    }


  /*****************************************************
   *
   * Called the first time the options menu is created.
   *
   *****************************************************/
// Uncomment once we implement the add photo functionality
//  @Override
//  public boolean onCreateOptionsMenu( Menu menu )
//    {
//    MenuInflater menuInflator = getMenuInflater();
//
//    menuInflator.inflate( R.menu.add_photo, menu );
//
//    return ( true );
//    }


  /*****************************************************
   *
   * Returns the content view for this fragment
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    View view = layoutInflator.inflate( R.layout.screen_phone_case, container, false );

    mMaskedRemoteImageView = (MaskedRemoteImageView)view.findViewById( R.id.masked_remote_image_view );
    mNextButton            = (Button)view.findViewById( R.id.next_button );


    // Request the image and mask

    ImageLoader imageManager = ImageLoader.getInstance( mKiteActivity );
    Handler      handler      = new Handler();

    Asset        asset        = mAssetArrayList.get( 0 );
    URL          maskURL      = mProduct.getMaskURL();
    Bleed        maskBleed    = mProduct.getMaskBleed();

    mMaskedRemoteImageView.setImageKey( asset );
    mMaskedRemoteImageView.setMaskDetails( maskURL, maskBleed );

    AssetHelper.requestImage( mKiteActivity, asset, mMaskedRemoteImageView );
    imageManager.requestRemoteImage( AKiteActivity.IMAGE_CLASS_STRING_PRODUCT_ITEM, maskURL, handler, mMaskedRemoteImageView );


    // TODO: Create a common superclass of all product creation fragments
    if ( savedInstanceState == null )
      {
      Analytics.getInstance( mKiteActivity ).trackCreateProductScreenViewed( mProduct );
      }


    mNextButton.setOnClickListener( this );


    return ( view );
    }


  /*****************************************************
   *
   * Called when an item in the options menu is selected.
   *
   *****************************************************/
  @Override
  public boolean onOptionsItemSelected( MenuItem item )
    {
    // See what menu item was selected

    int itemId = item.getItemId();

    if ( itemId == R.id.add_photo )
      {
      ///// Add photo /////

      // TODO:

      return ( true );
      }


    return ( super.onOptionsItemSelected( item ) );
    }


  /*****************************************************
   *
   * Called when the fragment is top-most.
   *
   *****************************************************/
  @Override
  protected void onTop()
    {
    if ( mProduct != null ) mKiteActivity.setTitle( mProduct.getName() );
    }


  ////////// View.OnClickListener Method(s) //////////

  /*****************************************************
   *
   * Called when something is clicked.
   *
   *****************************************************/
  @Override
  public void onClick( View view )
    {
    if ( view == mNextButton )
      {
      ///// Next /////

      onNext();
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when the Next button is clicked.
   *
   *****************************************************/
  private void onNext()
    {
    // Create a print order from the cropped image, then start
    // the checkout activity.

    Bitmap croppedImageBitmap = mMaskedRemoteImageView.getMaskedImageView().getImageCroppedToMask();


    // Sometimes users can hit the next button before we've actually got all the images, so check
    // for this.

    if ( croppedImageBitmap == null )
      {
      Log.w( LOG_TAG, "Cropped image not yet available" );

      return;
      }


    // Create the cropped image asset as a file, so we don't hit problems with transaction sizes
    // when passing assets through intents.

    Asset croppedImageAsset = AssetHelper.createAsCachedFile( mKiteActivity, croppedImageBitmap );

    if ( croppedImageAsset == null )
      {
      Log.e( LOG_TAG, "Could not create cropped image asset" );

      mKiteActivity.displayModalDialog(
              R.string.alert_dialog_title_create_order,
              R.string.alert_dialog_message_no_cropped_image_asset,
              AKiteActivity.DONT_DISPLAY_BUTTON,
              null,
              R.string.Cancel,
              null );

      return;
      }


    // Call back to the activity
    if ( mKiteActivity instanceof ICallback )
      {
      ( (ICallback)mKiteActivity ).pcOnCreated( mProduct, croppedImageAsset );
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A callback interface.
   *
   *****************************************************/
  public interface ICallback
    {
    public void pcOnCreated( Product product, Asset imageAsset );
    }

  }

