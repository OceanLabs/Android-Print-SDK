/*****************************************************
 *
 * AProductCreationFragment.java
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

package ly.kite.journey.creation;


///// Import(s) /////

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.catalogue.Asset;
import ly.kite.catalogue.AssetHelper;
import ly.kite.journey.AImageSource;
import ly.kite.journey.AKiteFragment;
import ly.kite.journey.AssetsAndQuantity;
import ly.kite.journey.IAssetsAndQuantityHolder;
import ly.kite.catalogue.Product;
import ly.kite.util.IImageConsumer;
import ly.kite.util.IImageTransformer;
import ly.kite.util.ImageAgent;


///// Class Declaration /////

/*****************************************************
 *
 * This is the abstract super-class of product creation
 * fragments. It provides some common features.
 *
 *****************************************************/
abstract public class AProductCreationFragment extends    AKiteFragment
                                               implements AImageSource.IAssetConsumer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG           = "AProductCreationFrag.";

  static private final int     PROGRESS_COMPLETE = 100;  // 100%


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected Product                       mProduct;
  protected ArrayList<AssetsAndQuantity>  mAssetsAndQuantityArrayList;

  private   ProgressBar                   mProgressBar;
  private   Button                        mProceedOverlayButton;
  private   Button                        mCancelButton;
  private   Button                        mConfirmButton;
  private   Button                        mCTABarLeftButton;
  private   Button                        mCTABarRightButton;

  protected int                           mInitialAssetsToCropCount;
  protected int                           mRemainingAssetsToCropCount;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// AKiteFragment Method(s) //////////

  /*****************************************************
   *
   * Called when the fragment is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // Get the product

    Bundle arguments = getArguments();

    if ( arguments == null )
      {
      Log.e( LOG_TAG, "No arguments found" );

      return;
      }

    mProduct = arguments.getParcelable( BUNDLE_KEY_PRODUCT );


    if ( mProduct == null )
      {
      throw ( new IllegalStateException( "No product supplied" ) );
      }
    }


  /*****************************************************
   *
   * Called after the activity is created.
   *
   *****************************************************/
  @Override
  public void onActivityCreated( Bundle savedInstanceState )
    {
    super.onActivityCreated( savedInstanceState );


    // We can't get the shared assets and quantity list until after the
    // activity has been created.

    if ( mKiteActivity != null && mKiteActivity instanceof IAssetsAndQuantityHolder )
      {
      mAssetsAndQuantityArrayList = ( (IAssetsAndQuantityHolder)mKiteActivity ).getAssetsAndQuantityArrayList();
      }

    if ( mAssetsAndQuantityArrayList == null )
      {
      throw ( new IllegalStateException( "The assets and quantity list could not be obtained" ) );
      }

    }


  /*****************************************************
   *
   * Called the first time the options menu is created.
   *
   *****************************************************/
  protected void onCreateOptionsMenu( Menu menu, MenuInflater menuInflator, int menuResourceId )
    {
    // The add photo XML has menu options for all the image sources, but they might
    // not all be enabled. So after we've inflated it, we need to go through an remove
    // any source that isn't available.

    menuInflator.inflate( menuResourceId, menu );

    MenuItem addPhotoItem = menu.findItem( R.id.add_photo_menu_item );

    if ( addPhotoItem != null )
      {
      SubMenu addPhotoSubMenu = addPhotoItem.getSubMenu();

      if ( addPhotoSubMenu != null )
        {
        addImageSourceMenuItems( addPhotoSubMenu );
        }
      }

    }


  /*****************************************************
   *
   * Called when an options item is selected.
   *
   *****************************************************/
  @Override
  final public boolean onOptionsItemSelected( MenuItem item )
    {
    // Check for add image

    if ( onCheckAddImageOptionItem( item, getMaxAddImageCount() ) )
      {
      return ( true );
      }


    // Check for custom item

    if ( onCheckCustomOptionItem( item ) )
      {
      return ( true );
      }


    return ( super.onOptionsItemSelected( item ) );
    }


  /*****************************************************
   *
   * Called when an item in the options menu is selected.
   *
   *****************************************************/
  final protected boolean onCheckAddImageOptionItem( MenuItem item, int maxImageCount )
    {
    int itemId = item.getItemId();


    // If one of the image source menu items was selected - launch the appropriate picker

    AImageSource imageSource = KiteSDK.getInstance( mKiteActivity ).getImageSourceByMenuItemId( itemId );

    if ( imageSource != null )
      {
      imageSource.onPick( this, maxImageCount );

      return ( true );
      }


    return ( false );
    }


  /*****************************************************
   *
   * Called with the result of an activity.
   *
   *****************************************************/
  @Override
  public void onActivityResult( int requestCode, int resultCode, Intent returnedIntent )
    {
    super.onActivityResult( requestCode, resultCode, returnedIntent );

    // Get assets for any images returned and add them
    KiteSDK.getInstance( mKiteActivity ).getAssetsFromPickerResult( mKiteActivity, requestCode, resultCode, returnedIntent, this );
    }


  /*****************************************************
   *
   * Called when the fragment is top-most.
   *
   *****************************************************/
  @Override
  public void onTop()
    {
    super.onTop();

    // We don't enable the proceed button until all the assets have been cropped
    setForwardsButtonEnabled( mRemainingAssetsToCropCount < 1 );
    }


  ////////// AImageSource.IAssetConsumer Method(s) //////////

  /*****************************************************
   *
   * Called with new picked assets.
   *
   *****************************************************/
  @Override
  public void isacOnAssets( List<Asset> assetList )
    {
    if ( assetList != null )
      {
      onAddAssets( assetList );
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called by a child class when the view has been created.
   *
   *****************************************************/
  protected void onViewCreated( View view )
    {
    // Get references to any views
    mProgressBar          = (ProgressBar)view.findViewById( R.id.progress_bar );
    mProceedOverlayButton = (Button)view.findViewById( R.id.proceed_overlay_button );
    mCancelButton         = (Button)view.findViewById( R.id.cancel_button );
    mConfirmButton        = (Button)view.findViewById( R.id.confirm_button );
    mCTABarLeftButton     = (Button)view.findViewById( R.id.cta_bar_left_button );
    mCTABarRightButton    = (Button)view.findViewById( R.id.cta_bar_right_button );
    }


  /*****************************************************
   *
   * Adds menu items for all image sources.
   *
   *****************************************************/
  protected void addImageSourceMenuItems( Menu menu )
    {
    for ( AImageSource imageSource : KiteSDK.getInstance( mKiteActivity ).getAvailableImageSources() )
      {
      imageSource.addAsMenuItem( menu );
      }
    }


  /*****************************************************
   *
   * Returns a backwards button.
   *
   *****************************************************/
  protected Button getBackwardsButton()
    {
    if ( mCTABarLeftButton != null ) return ( mCTABarLeftButton );
    if ( mCancelButton     != null ) return ( mCancelButton );

    return ( null );
    }


  /*****************************************************
   *
   * Sets the visibility of any backwards button.
   *
   *****************************************************/
  protected void setBackwardsButtonVisibility( int visibility )
    {
    Button backwardsButton = getBackwardsButton();

    if ( backwardsButton != null ) backwardsButton.setVisibility( visibility );
    }


  /*****************************************************
   *
   * Sets the text of any backwards button.
   *
   *****************************************************/
  protected void setBackwardsButtonText( int textResourceId )
    {
    Button backwardsButton = getBackwardsButton();

    if ( backwardsButton != null ) backwardsButton.setText( textResourceId );
    }


  /*****************************************************
   *
   * Sets the enabled state of any backwards button.
   *
   *****************************************************/
  protected void setBackwardsButtonEnabled( boolean enabled )
    {
    Button backwardsButton = getBackwardsButton();

    if ( backwardsButton != null ) backwardsButton.setEnabled( enabled );
    }


  /*****************************************************
   *
   * Sets the listener for any backwards button.
   *
   *****************************************************/
  protected void setBackwardsButtonOnClickListener( View.OnClickListener listener )
    {
    Button backwardsButton = getBackwardsButton();

    if ( backwardsButton != null ) backwardsButton.setOnClickListener( listener );
    }


  /*****************************************************
   *
   * Returns a forwards button.
   *
   *****************************************************/
  protected Button getForwardsButton()
    {
    if ( mCTABarRightButton    != null ) return ( mCTABarRightButton );
    if ( mProceedOverlayButton != null ) return ( mProceedOverlayButton );
    if ( mConfirmButton        != null ) return ( mConfirmButton );

    return ( null );
    }


  /*****************************************************
   *
   * Sets the visibility of any forwards button.
   *
   *****************************************************/
  protected void setForwardsButtonVisibility( int visibility )
    {
    Button forwardsButton = getForwardsButton();

    if ( forwardsButton != null ) forwardsButton.setVisibility( visibility );
    }


  /*****************************************************
   *
   * Sets the text of any forwards button.
   *
   *****************************************************/
  protected void setForwardsButtonText( int textResourceId )
    {
    Button forwardsButton = getForwardsButton();

    if ( forwardsButton != null ) forwardsButton.setText( textResourceId );
    }


  /*****************************************************
   *
   * Sets the style of any forwards button.
   *
   *****************************************************/
  protected void setForwardsButtonBold( boolean bold )
    {
    Button forwardsButton = getForwardsButton();

    if ( forwardsButton != null )
      {
      forwardsButton.setTypeface( forwardsButton.getTypeface(), bold ? Typeface.BOLD : Typeface.NORMAL );
      }
    }


  /*****************************************************
   *
   * Sets the enabled state of any forwards button.
   *
   *****************************************************/
  protected void setForwardsButtonEnabled( boolean enabled )
    {
    Button forwardsButton = getForwardsButton();

    if ( forwardsButton != null ) forwardsButton.setEnabled( enabled );
    }


  /*****************************************************
   *
   * Sets the listener for any forwards button.
   *
   *****************************************************/
  protected void setForwardsButtonOnClickListener( View.OnClickListener listener )
    {
    Button forwardsButton = getForwardsButton();

    if ( forwardsButton != null ) forwardsButton.setOnClickListener( listener );
    }


  /*****************************************************
   *
   * Ensures that we have square cropped images for all
   * assets.
   *
   * @return true if there are images to crop, false
   *         otherwise.
   *
   *****************************************************/
  protected boolean requestCroppedAssets()
    {
    // First build a list of assets that need to be cropped, so we know how many there are
    // before we actually start requesting them. Note that the asset array list may be
    // sparsely populated for certain products (such as photobooks, which can have blank
    // pages).

    String productId = mProduct.getId();

    List<AssetsAndQuantity> assetsAndQuantityToCropList = new ArrayList<>( mAssetsAndQuantityArrayList.size() );

    for ( AssetsAndQuantity assetsAndQuantity : mAssetsAndQuantityArrayList )
      {
      if ( assetsAndQuantity != null )
        {
        // If we don't already have an edited asset - create one now

        if ( ( productId == null ) || ( !productId.equals( assetsAndQuantity.getEditedForProductId() ) ) )
          {
          assetsAndQuantityToCropList.add( assetsAndQuantity );
          }
        }
      }


    // Get the counts

    mRemainingAssetsToCropCount = mInitialAssetsToCropCount = assetsAndQuantityToCropList.size();

    showProgress( mRemainingAssetsToCropCount, mInitialAssetsToCropCount );


    // Now go back through and request all the images

    for ( AssetsAndQuantity assetsAndQuantity : assetsAndQuantityToCropList )
      {
      AssetImageCropper cropper = new AssetImageCropper( assetsAndQuantity, mProduct.getImageAspectRatio() );

      AssetHelper.requestImage( mKiteActivity, assetsAndQuantity.getUneditedAsset(), cropper, 0, cropper );
      }


    // Set the enabled state of the proceed button according to whether there are assets to crop
    setForwardsButtonEnabled( mInitialAssetsToCropCount > 0 );

    return ( mInitialAssetsToCropCount > 0 );
    }


  /*****************************************************
   *
   * Requests a single square cropped image for an asset.
   *
   *****************************************************/
  protected void requestCroppedAsset( AssetsAndQuantity assetsAndQuantity )
    {
    AssetImageCropper cropper = new AssetImageCropper( assetsAndQuantity, mProduct.getImageAspectRatio() );

    AssetHelper.requestImage( mKiteActivity, assetsAndQuantity.getUneditedAsset(), cropper, 0, cropper );
    }


  /*****************************************************
   *
   * Shows the cropping progress.
   *
   *****************************************************/
  protected void showProgress( int remainingCount, int totalCount )
    {
    if ( mProgressBar == null ) return;


    // If there are no images, or none left to crop, don't show the
    // progress bar.

    if ( totalCount < 1 || remainingCount < 1 )
      {
      mProgressBar.setVisibility( View.INVISIBLE );
      }
    else
      {
      mProgressBar.setVisibility( View.VISIBLE );

      mProgressBar.setProgress( PROGRESS_COMPLETE * ( totalCount - remainingCount ) / totalCount );
      }
    }


  /*****************************************************
   *
   * Called when an image is cropped.
   *
   *****************************************************/
  protected void onImageCropped( AssetsAndQuantity assetsAndQuantity )
    {
    }


  /*****************************************************
   *
   * Called when all images have been cropped.
   *
   *****************************************************/
  protected void onAllImagesCropped()
    {
    }


  /*****************************************************
   *
   * Called when an item in the options menu is selected,
   * which has not already been handled.
   *
   *****************************************************/
  protected boolean onCheckCustomOptionItem( MenuItem item )
    {
    return ( false );
    }


  /*****************************************************
   *
   * Called when a new asset is added. Will be called multiple
   * times if a number of assets are added together.
   *
   *****************************************************/
  protected void onAssetAdded( AssetsAndQuantity assetsAndQuantity )
    {
    }


  /*****************************************************
   *
   * Returns the maximum number of images required when
   * adding images.
   *
   *****************************************************/
  protected int getMaxAddImageCount()
    {
    return ( AImageSource.UNLIMITED_IMAGES );
    }


  /*****************************************************
   *
   * Adds new unedited assets to the end of the current list.
   * Duplicates will be discarded.
   *
   *****************************************************/
  protected void onAddAssets( List<Asset> newAssetList )
    {
    for ( Asset asset : newAssetList )
      {
      // We don't allow duplicate images, so first check that the asset isn't already in
      // our list. Note that we don't check the scenario where the image is the same but
      // from a different source - a byte by byte comparison would take too long, and a
      // duplicate is unlikely anyway.

      if ( ! AssetsAndQuantity.uneditedAssetIsInList( mAssetsAndQuantityArrayList, asset ) )
        {
        // Start with the unedited asset, and a quantity of 1.
        AssetsAndQuantity assetsAndQuantity = new AssetsAndQuantity( asset );

        // Add the selected image to our asset lists, mark it as checked
        mAssetsAndQuantityArrayList.add( assetsAndQuantity );

        onAssetAdded( assetsAndQuantity );
        }
      }


    onNewAssetsPossiblyAdded();
    }


  /*****************************************************
   *
   * Adds new unedited assets into the current list, filling
   * in any empty slots from the supplied position.
   *
   *****************************************************/
  protected void onAddAssets( List<Asset> newAssetList, int insertionPointIndex )
    {
    for ( Asset asset : newAssetList )
      {
      if ( insertionPointIndex >= mAssetsAndQuantityArrayList.size() ) break;

      AssetsAndQuantity assetsAndQuantity = new AssetsAndQuantity( asset );

      mAssetsAndQuantityArrayList.set( insertionPointIndex, assetsAndQuantity );

      onAssetAdded( assetsAndQuantity );


      // Find the next free slot
      while ( insertionPointIndex < mAssetsAndQuantityArrayList.size() &&
              mAssetsAndQuantityArrayList.get( ++ insertionPointIndex ) != null );
      }


    onNewAssetsPossiblyAdded();
    }


  /*****************************************************
   *
   * Called after new assets have been added. Note that the
   * number of assets added may be 0.
   *
   *****************************************************/
  protected void onNewAssetsPossiblyAdded()
    {
    // Get cropped versions of any new assets, and call back to the child class if
    // there were some new ones.
    if ( requestCroppedAssets() )
      {
      onNewAssetsBeingCropped();

      setForwardsButtonEnabled( false );
      }
    }


  /*****************************************************
   *
   * Called when at least one new asset has been added.
   *
   *****************************************************/
  protected void onNewAssetsBeingCropped()
    {
    }


  /*****************************************************
   *
   * Displays a dialog informaing the user that they have
   * not filled a pack.
   *
   *****************************************************/
  protected void displayNotFullDialog( int expectedImageCount, int actualImageCount, Runnable proceedRunnable )
    {
    mKiteActivity.displayModalDialog(
      getString( R.string.alert_dialog_title_pack_not_full_format_string, actualImageCount, getResources().getQuantityString( R.plurals.photo_plurals, actualImageCount ) ),
      getString( R.string.alert_dialog_message_pack_not_full_format_string, expectedImageCount - actualImageCount ),
      R.string.print_these,
      proceedRunnable,
      R.string.add_more, null );
    }


  /*****************************************************
   *
   * Finds an edited asset in the list.
   *
   *****************************************************/
  protected int findEditedAsset( Asset soughtEditedAsset )
    {
    return ( AssetsAndQuantity.findEditedAsset( mAssetsAndQuantityArrayList, soughtEditedAsset ) );
    }



  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An image transformer that crops the supplied image
   * to a square, creates an asset from it, and then stores
   * it as an edited asset.
   *
   * We also use it as the image consumer, because the available
   * method gets called on the UI thread.
   *
   *****************************************************/
  private class AssetImageCropper implements IImageTransformer, IImageConsumer
    {
    private AssetsAndQuantity  mAssetsAndQuantity;
    private float              mCroppedAspectRatio;


    AssetImageCropper( AssetsAndQuantity assetsAndQuantity, float croppedAspectRatio )
      {
      mAssetsAndQuantity  = assetsAndQuantity;
      mCroppedAspectRatio = croppedAspectRatio;
      }


    ////////// AssetHelper.IImageTransformer Method(s) //////////

    /*****************************************************
     *
     * Called on a background thread to transform a bitmap.
     * We use this to crop the bitmap, and create a file-backed
     * asset from it.
     *
     *****************************************************/
    @Override
    public Bitmap getTransformedBitmap( Bitmap bitmap )
      {
      // Crop the bitmap to the required shape
      Bitmap croppedBitmap = ImageAgent.crop( bitmap, mCroppedAspectRatio );


      // Create a new file-backed asset from the cropped bitmap, and save it as the edited asset.

      Asset editedAsset = AssetHelper.createAsCachedFile( mKiteActivity, croppedBitmap );

      mAssetsAndQuantity.setEditedAsset( editedAsset, mProduct.getId() );


      return ( croppedBitmap );
      }


    ////////// IImageConsumer Method(s) //////////

    @Override
    public void onImageDownloading( Object key )
      {
      // Ignore
      }


    /*****************************************************
     *
     * Called on the UI thread, with the cropped image.
     *
     *****************************************************/
    @Override
    public void onImageAvailable( Object key, Bitmap bitmap )
      {
      onImageCropped( mAssetsAndQuantity );


      // If we now have all the cropped images - enable the proceed button.

      mRemainingAssetsToCropCount--;

      showProgress( mRemainingAssetsToCropCount, mInitialAssetsToCropCount );

      if ( mRemainingAssetsToCropCount < 1 )
        {
        setForwardsButtonEnabled( true );

        onAllImagesCropped();
        }
      }


    /*****************************************************
     *
     * Called when an image could not be loaded.
     *
     *****************************************************/
    @Override
    public void onImageUnavailable( Object key, Exception exception )
      {
      // TODO
      }

    }

  }

