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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import ly.kite.R;
import ly.kite.journey.AImageSource;
import ly.kite.journey.AKiteActivity;
import ly.kite.catalogue.Asset;
import ly.kite.catalogue.AssetHelper;
import ly.kite.catalogue.Product;
import ly.kite.journey.AssetsAndQuantity;
import ly.kite.widget.EditableImageContainerFrame;
import ly.kite.widget.PromptTextFrame;


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
  static private final String      LOG_TAG                             = "AEditImageFragment";

  static public  final String      BUNDLE_KEY_PRODUCT                  = "product";
  static private final String      BUNDLE_KEY_IMAGE_ASSET              = "imageAsset";

  static private final long        PROMPT_ANIMATION_START_DELAY_MILLIS = 500L;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected Product                      mProduct;

  protected Asset                        mImageAsset;

  protected EditableImageContainerFrame  mEditableImageContainerFrame;
  private   PromptTextFrame              mPromptTextFrame;

  protected boolean                      mShowPromptText;


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
  protected AEditImageFragment( Product product , Asset imageAsset )
    {
    Bundle arguments = new Bundle();

    arguments.putParcelable( BUNDLE_KEY_PRODUCT, product );

    if ( imageAsset != null ) arguments.putParcelable( BUNDLE_KEY_IMAGE_ASSET, imageAsset );

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
              R.string.alert_dialog_title_no_arguments,
              R.string.alert_dialog_message_no_arguments,
              AKiteActivity.NO_BUTTON,
              null,
              R.string.Cancel,
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
              R.string.alert_dialog_title_product_not_found,
              R.string.alert_dialog_message_product_not_found,
              AKiteActivity.NO_BUTTON,
              null,
              R.string.Cancel,
              mKiteActivity.new FinishRunnable()
      );

      return;
      }


    // A new image may have been picked since the screen was entered, so check for a saved
    // image first, then fallback to any supplied as an argument.

    if ( savedInstanceState != null )
      {
      mImageAsset = savedInstanceState.getParcelable( BUNDLE_KEY_IMAGE_ASSET );
      }

    if ( mImageAsset == null )
      {
      mImageAsset = arguments.getParcelable( BUNDLE_KEY_IMAGE_ASSET );
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
    View view = layoutInflator.inflate( R.layout.screen_edit_image, container, false );

    super.onViewCreated( view );

    mEditableImageContainerFrame = (EditableImageContainerFrame)view.findViewById( R.id.editable_image_container_frame );
    mPromptTextFrame             = (PromptTextFrame)view.findViewById( R.id.prompt_text_frame );


    // If there is a container frame - set the callback.
    if ( mEditableImageContainerFrame != null ) mEditableImageContainerFrame.setCallback( this );

    setBackwardsButtonOnClickListener( this );

    // The confirm button is not enabled until both image and mask are loaded
    setForwardsButtonEnabled( false );


    return ( view );
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


    // Only show the prompt animation the first time this
    // screen is displayed.

    if ( savedInstanceState == null && mPromptTextFrame != null )
      {
      mShowPromptText = true;
      }


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

    if ( mImageAsset != null )
      {
      outState.putParcelable( BUNDLE_KEY_IMAGE_ASSET, mImageAsset );
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
    if ( view == getBackwardsButton() )
      {
      ///// Cancel /////

      onCancel();
      }
    else if ( view == getForwardsButton() )
      {
      ///// Confirm /////

      onConfirm();
      }
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
    // Once both the image and the mask have been loaded -
    // display any prompt text.

    if ( mShowPromptText && mPromptTextFrame != null )
      {
      mShowPromptText = false;

      mPromptTextFrame.startDisplayCycle();
      }


    // Enable any confirm button
    setForwardsButtonEnabled( true );
    setForwardsButtonOnClickListener( this );
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
            R.string.alert_dialog_title_load_image,
            R.string.alert_dialog_message_could_not_load_image,
            R.string.Retry,
            new LoadAllImagesRunnable(),
            R.string.Cancel,
            mKiteActivity.new FinishRunnable() );
    }


  ////////// AImageSource.IAssetConsumer Method(s) //////////

  /*****************************************************
   *
   * Called with new picked assets. For editing single
   * images, we don't need to create cropped versions of
   * the images up front, so simply add the image to the
   * list.
   *
   *****************************************************/
  @Override
  public void isacOnAssets( List<Asset> assetList )
    {
    if ( assetList != null && assetList.size() > 0 )
      {
      // Add the assets to the shared list
      for ( Asset asset : assetList )
        {
        mAssetsAndQuantityArrayList.add( 0, new AssetsAndQuantity( asset ) );
        }


      // Save the first asset and use it

      mImageAsset = mAssetsAndQuantityArrayList.get( 0 ).getUneditedAsset();

      useAssetForImage( mImageAsset, true );
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Uses the supplied asset for the photo.
   *
   *****************************************************/
  private void useAssetForImage( Asset asset, boolean imageIsNew )
    {
    if ( mEditableImageContainerFrame != null )
      {
      if ( imageIsNew ) mEditableImageContainerFrame.clearState();

      mEditableImageContainerFrame.setAndLoadImage( asset );
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


    if ( itemId == R.id.rotate_anticlockwise_menu_item )
      {
      ///// Rotate /////

      if ( mEditableImageContainerFrame != null ) mEditableImageContainerFrame.requestAnticlockwiseRotation();

      return ( true );
      }
    else if ( itemId == R.id.flip_horizontally_menu_item )
      {
      ///// Flip /////

      if ( mEditableImageContainerFrame != null ) mEditableImageContainerFrame.requestHorizontalFlip();

      return ( true );
      }


    return ( false );
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
  abstract protected void onConfirm();


  /*****************************************************
   *
   * Gets the cropped image and creates a file-backed
   * asset from it.
   *
   *****************************************************/
  protected Asset getEditedImageAsset()
    {
    if ( mEditableImageContainerFrame == null ) return ( null );


    Bitmap editedImageBitmap = mEditableImageContainerFrame.getEditableImageView().getImageCroppedToMask();


    // Sometimes users can hit the next button before we've actually got all the images, so check
    // for this.

    if ( editedImageBitmap == null )
      {
      Log.w( LOG_TAG, "Cropped image not yet available" );

      return ( null );
      }


    // Create the cropped image asset as a file. We always create file-backed assets within
    // the SDK so we don't hit problems with transaction sizes when passing assets through
    // intents.

    Asset editedImageAsset = AssetHelper.createAsCachedFile( mKiteActivity, editedImageBitmap );

    if ( editedImageAsset == null )
      {
      Log.e( LOG_TAG, "Could not create edited image asset" );

      mKiteActivity.displayModalDialog(
              R.string.alert_dialog_title_create_order,
              R.string.alert_dialog_message_no_cropped_image_asset,
              AKiteActivity.NO_BUTTON,
              null,
              R.string.Cancel,
              null );

      return ( null );
      }


    return ( editedImageAsset );
    }


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

  }

