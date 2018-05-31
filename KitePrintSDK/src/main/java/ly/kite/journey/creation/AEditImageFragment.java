/*****************************************************
 *
 * AEditImageFragment.java
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

import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.image.ImageAgent;
import ly.kite.image.ImageProcessingRequest;
import ly.kite.journey.AImageSource;
import ly.kite.journey.AKiteActivity;
import ly.kite.util.Asset;
import ly.kite.catalogue.Product;
import ly.kite.util.AssetFragment;
import ly.kite.widget.EditableImageContainerFrame;


///// Class Declaration /////

/*****************************************************
 *
 * This is the parent of fragments that allow users to
 * edit an image to fit within a mask.
 *
 *****************************************************/
abstract public class AEditImageFragment extends AProductCreationFragment implements View.OnClickListener,
                                                                                     EditableImageContainerFrame.ICallback,
                                                                                     AImageSource.IAssetConsumer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String      LOG_TAG                         = "AEditImageFragment";

  static public  final String      BUNDLE_KEY_PRODUCT              = "product";
  static private final String      BUNDLE_KEY_IMAGE_ASSET_FRAGMENT = "imageAssetFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected Product                      mProduct;

  protected AssetFragment                mUnmodifiedImageAssetFragment;
  protected Asset                        mModifiedAsset;

  protected EditableImageContainerFrame  mEditableImageContainerFrame;
  private   ProgressBar                  mProgressSpinner;

  private   boolean                      mTransformOperationInProgress;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  protected AEditImageFragment()
    {
    }


  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  protected AEditImageFragment( Product product, AssetFragment imageAssetFragment )
    {
    Bundle arguments = new Bundle();

    arguments.putParcelable( BUNDLE_KEY_PRODUCT, product );

    if ( imageAssetFragment != null ) arguments.putParcelable( BUNDLE_KEY_IMAGE_ASSET_FRAGMENT, imageAssetFragment );

    setArguments( arguments );
    }


  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  protected AEditImageFragment( Product product )
    {
    this( product, null );
    }


  ////////// AKiteFragment Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // Check for arguments

    Bundle arguments = getArguments();

    if ( arguments == null )
      {
      Log.e( LOG_TAG, "No arguments found" );

      mKiteActivity.displayModalDialog(
              R.string.kitesdk_alert_dialog_title_no_arguments,
              R.string.kitesdk_alert_dialog_message_no_arguments,
              AKiteActivity.NO_BUTTON,
              null,
              R.string.kitesdk_Cancel,
              mKiteActivity.new FinishRunnable()
      );

      return;
      }


    // The product must always be supplied in the arguments

    mProduct = arguments.getParcelable( BUNDLE_KEY_PRODUCT );

    if ( mProduct == null )
      {
      Log.e( LOG_TAG, "No product found" );

      mKiteActivity.displayModalDialog(
              R.string.kitesdk_alert_dialog_title_product_not_found,
              R.string.kitesdk_alert_dialog_message_product_not_found,
              AKiteActivity.NO_BUTTON,
              null,
              R.string.kitesdk_Cancel,
              mKiteActivity.new FinishRunnable()
      );

      return;
      }


    // A new image may have been picked since the screen was entered, so check for a saved
    // image first, then fallback to any supplied as an argument.

    if ( savedInstanceState != null )
      {
      mUnmodifiedImageAssetFragment = savedInstanceState.getParcelable( BUNDLE_KEY_IMAGE_ASSET_FRAGMENT );
      }

    if ( mUnmodifiedImageAssetFragment == null )
      {
      mUnmodifiedImageAssetFragment = arguments.getParcelable( BUNDLE_KEY_IMAGE_ASSET_FRAGMENT );
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

    super.onViewCreated( view );

    mEditableImageContainerFrame = (EditableImageContainerFrame)view.findViewById( R.id.editable_image_container_frame );
    mProgressSpinner             = (ProgressBar)view.findViewById( R.id.progress_spinner );


    // If there is a container frame - set the callback
    if ( mEditableImageContainerFrame != null ) mEditableImageContainerFrame.setCallback( this );


    setBackwardsTextViewVisibility( View.GONE );


    setForwardsTextViewVisibility( View.VISIBLE );
    setForwardsTextViewText( R.string.kitesdk_edit_image_forwards_button_text);
    setForwardsTextViewBold( true );

    // The confirm button is not enabled until both image and mask are loaded
    setForwardsTextViewEnabled( false );


    return ( view );
    }


  /*****************************************************
   *
   * Returns the content view for this fragment
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    return ( onCreateView( layoutInflator, R.layout.screen_edit_image, container, savedInstanceState ) );
    }


  /*****************************************************
   *
   * Called when the activity has been created.
   *
   *****************************************************/
  @Override
  public void onActivityCreated( Bundle savedInstanceState )
    {
    super.onActivityCreated( savedInstanceState );


    // Try to restore any previous cropping

    if ( mEditableImageContainerFrame != null )
      {
      if ( savedInstanceState != null )
        {
        mEditableImageContainerFrame.restoreState( savedInstanceState );
        }
      }

    }


  /*****************************************************
   *
   * Returns the maximum number of images required when
   * adding images.
   *
   *****************************************************/
  @Override
  protected int getMaxAddImageCount()
    {
    return ( AImageSource.SINGLE_IMAGE );
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

    if ( mEditableImageContainerFrame != null ) mEditableImageContainerFrame.loadAllImages();
    }


  /*****************************************************
   *
   * Called when the fragment is not top-most.
   *
   *****************************************************/
  @Override
  public void onNotTop()
    {
    super.onNotTop();

    if ( mEditableImageContainerFrame != null ) mEditableImageContainerFrame.unloadAllImages();
    }


  /*****************************************************
   *
   * Called to save the instance state.
   *
   *****************************************************/
  @Override
  public void onSaveInstanceState( Bundle outState )
    {
    super.onSaveInstanceState( outState );


    // A different asset may have been selected by the user, which is why we save the most
    // recent version.

    if ( mUnmodifiedImageAssetFragment != null )
      {
      outState.putParcelable( BUNDLE_KEY_IMAGE_ASSET_FRAGMENT, mUnmodifiedImageAssetFragment );
      }


    // Try and save the cropping

    if ( mEditableImageContainerFrame != null )
      {
      mEditableImageContainerFrame.saveState( outState );
      }
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
    if ( view == getBackwardsTextView() )
      {
      ///// Cancel /////

      onCancel();

      return;
      }
    else if ( view == getForwardsTextView() )
      {
      ///// Confirm /////

      onConfirm();

      return;
      }

    super.onClick( view );
    }


  ////////// EditableImageContainerFrame.ICallback Method(s) //////////

  /*****************************************************
   *
   * Called when both the image and the mask have been
   * loaded.
   *
   *****************************************************/
  @Override
  public void onLoadComplete()
    {
    if ( KiteSDK.DEBUG_IMAGE_CONTAINERS ) Log.d( LOG_TAG, "onLoadComplete()" );

    onScreenReady();

    // Enable any confirm button
    setForwardsTextViewEnabled( true );
    setForwardsTextViewOnClickListener( this );
    }


  /*****************************************************
   *
   * Called when an image could not be loaded.
   *
   *****************************************************/
  @Override
  public void onLoadError()
    {
    mKiteActivity.displayModalDialog(
            R.string.kitesdk_alert_dialog_title_load_image,
            R.string.kitesdk_alert_dialog_message_could_not_load_image,
            R.string.kitesdk_Retry,
            new LoadAllImagesRunnable(),
            R.string.kitesdk_Cancel,
            mKiteActivity.new FinishRunnable() );
    }


  ////////// AImageSource.IAssetConsumer Method(s) //////////

  /*****************************************************
   *
   * Called with new picked assets. For editing single
   * images, we want to ensure the following:
   *   - We don't need to create cropped versions of the
   *     images up front
   *   - We don't want to add the image to the original list
   *     of assets because (a) if we cancel, we want the list
   *     to stay as it was, and (b) we are going to replace
   *     an image in the list anyway (and only the ProductCreationActivity
   *     knows which one it is).
   *
   *****************************************************/
  @Override
  public void isacOnAssets( List<Asset> assetList )
    {
    Asset replacementAsset = Asset.findFirst( assetList );

    if ( replacementAsset != null )
      {
      mUnmodifiedImageAssetFragment = new AssetFragment( replacementAsset );

      useAssetForImage( mUnmodifiedImageAssetFragment, true );
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Uses the supplied asset for the photo.
   *
   *****************************************************/
  protected void useAssetForImage( AssetFragment assetFragment, boolean imageIsNew )
    {
    if ( mEditableImageContainerFrame != null )
      {
      if ( imageIsNew ) mEditableImageContainerFrame.clearState();

      mEditableImageContainerFrame.setAndLoadImage( assetFragment );
      }
    }


  /*****************************************************
   *
   * Called when an item in the options menu is selected,
   * which has not already been handled.
   *
   *****************************************************/
  @Override
  protected boolean onCheckCustomOptionItem( MenuItem item )
    {
    int itemId = item.getItemId();


    if ( !mTransformOperationInProgress )
      {
      if ( itemId == R.id.rotate_anticlockwise_menu_item )
        {
        ///// Rotate /////

        if ( mEditableImageContainerFrame != null )
          {
          ImageProcessingRequest.Builder builder;

          if ( mModifiedAsset != null )
            {
            builder = ImageAgent.with( mKiteActivity )
                    .transform( mModifiedAsset );
            }
          else
            {
            builder = ImageAgent.with( mKiteActivity )
                    .transform( mUnmodifiedImageAssetFragment.getAsset() )
                    .intoNewAsset();
            }

          onTransformOperationStarted();

          builder
                  .byRotatingAnticlockwise()
                  .thenNotify( new ProcessedImageLoader() );

          }

        return ( true );
        }
      else if ( itemId == R.id.flip_horizontally_menu_item )
        {
        ///// Flip /////

        if ( mEditableImageContainerFrame != null )
          {
          ImageProcessingRequest.Builder builder;

          if ( mModifiedAsset != null )
            {
            builder = ImageAgent.with( mKiteActivity )
                    .transform( mModifiedAsset );
            }
          else
            {
            builder = ImageAgent.with( mKiteActivity )
                    .transform( mUnmodifiedImageAssetFragment.getAsset() )
                    .intoNewAsset();
            }

          onTransformOperationStarted();

          builder
                  .byFlippingHorizontally()
                  .thenNotify( new ProcessedImageLoader() );
          }

        return ( true );
        }
      }


    return ( false );
    }


  /*****************************************************
   *
   * Called when a transformation operation (flip / rotate)
   * starts.
   *
   *****************************************************/
  protected void onTransformOperationStarted()
    {
    mTransformOperationInProgress = true;

    if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.VISIBLE );
    }


  /*****************************************************
   *
   * Called when a transformation operation (flip / rotate)
   * finishes.
   *
   *****************************************************/
  protected void onTransformOperationEnded()
    {
    mTransformOperationInProgress = false;

    if ( mProgressSpinner != null ) mProgressSpinner.setVisibility( View.GONE );
    }


  /*****************************************************
   *
   * Called when the cancel button is clicked.
   *
   *****************************************************/
  protected void onCancel()
    {
    }


  /*****************************************************
   *
   * Called when the confirm button is clicked.
   *
   *****************************************************/
  protected void onConfirm()
    {
    requestCompletedAssetFragment();
    }


  /*****************************************************
   *
   * Called by child fragments when editing is complete.
   * This may be asynchronous; the {@link #onEditingComplete(AssetFragment)}
   * method is called with the asset fragment.
   *
   *****************************************************/
  protected void requestCompletedAssetFragment()
    {
    if ( mEditableImageContainerFrame == null ) return;


    RectF imageProportionalCropRectangle = mEditableImageContainerFrame.getEditableImageView().getImageProportionalCropRectangle();


    // Sometimes users can hit the next button before we've actually got all the images, so check
    // for this.

    if ( imageProportionalCropRectangle == null )
      {
      Log.w( LOG_TAG, "Cropped image not yet available" );

      return;
      }


    Asset asset = ( mModifiedAsset != null ? mModifiedAsset : mUnmodifiedImageAssetFragment.getAsset() );

    onEditingComplete( new AssetFragment( asset, imageProportionalCropRectangle ) );
    }


  /*****************************************************
   *
   * Called when an edited asset is available and may be
   * returned to the activity. Child fragments override
   * this method.
   *
   *****************************************************/
  abstract protected void onEditingComplete( AssetFragment assetFragment );


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * Starts any image requests.
   *
   *****************************************************/
  private class LoadAllImagesRunnable implements Runnable
    {
    @Override
    public void run()
      {
      mEditableImageContainerFrame.loadAllImages();
      }
    }


  /*****************************************************
   *
   * Image processing callback that sets the image and
   * loads it.
   *
   *****************************************************/
  private class ProcessedImageLoader implements ImageProcessingRequest.ICallback
    {
    @Override
    public void ipcOnImageAvailable( Asset processedAsset )
      {
      onTransformOperationEnded();

      mModifiedAsset = processedAsset;

      if ( mEditableImageContainerFrame != null ) mEditableImageContainerFrame.setAndLoadImage( processedAsset );
      }

    @Override
    public void ipcOnImageUnavailable()
      {
      onTransformOperationEnded();
      }
    }

  }

