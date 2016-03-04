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

import ly.kite.R;
import ly.kite.journey.AKiteActivity;
import ly.kite.catalogue.Asset;
import ly.kite.catalogue.AssetHelper;
import ly.kite.catalogue.Product;
import ly.kite.widget.EditableImageContainerFrame;
import ly.kite.widget.PromptTextFrame;


///// Class Declaration /////

/*****************************************************
 *
 * This is the parent of fragments that allow users to
 * edit an image to fit within a mask.
 *
 *****************************************************/
abstract public class AEditImageFragment extends AProductCreationFragment implements View.OnClickListener, EditableImageContainerFrame.ICallback
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String      LOG_TAG                             = "AEditImageFragment";

  public  static final String      BUNDLE_KEY_PRODUCT                  = "product";

  private static final long        PROMPT_ANIMATION_START_DELAY_MILLIS = 500L;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected Product                      mProduct;

  protected EditableImageContainerFrame  mEditableImageContainerFrame;
  protected Button                       mCancelButton;
  protected Button                       mConfirmButton;
  private   PromptTextFrame              mPromptTextFrame;

  protected boolean                      mShowPromptText;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


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


    // Get the assets and product

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

    mEditableImageContainerFrame = (EditableImageContainerFrame)view.findViewById( R.id.editable_image_container_frame );
    mCancelButton                = (Button)view.findViewById( R.id.cancel_button );
    mConfirmButton               = (Button)view.findViewById( R.id.confirm_button );
    mPromptTextFrame             = (PromptTextFrame)view.findViewById( R.id.prompt_text_frame );


    // If there is a container frame - set the callback.
    if ( mEditableImageContainerFrame != null ) mEditableImageContainerFrame.setCallback( this );

    if ( mCancelButton != null ) mCancelButton.setOnClickListener( this );

    // The confirm button is not enabled until both image and mask are loaded
    if ( mConfirmButton != null ) mConfirmButton.setEnabled( false );


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
    }


  /*****************************************************
   *
   * Called when an item in the options menu is selected.
   *
   *****************************************************/
  @Override
  public boolean onOptionsItemSelected( MenuItem item )
    {
    int itemId = item.getItemId();


    if ( itemId == R.id.rotate_menu_item )
      {
      ///// Rotate /////

      if ( mEditableImageContainerFrame != null ) mEditableImageContainerFrame.requestAnticlockwiseRotation();
      }
    else if ( itemId == R.id.flip_menu_item )
      {
      ///// Flip /////

      if ( mEditableImageContainerFrame != null ) mEditableImageContainerFrame.requestVerticalFlip();
      }


    return ( super.onOptionsItemSelected( item ) );
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


  ////////// View.OnClickListener Method(s) //////////

  /*****************************************************
   *
   * Called when something is clicked.
   *
   *****************************************************/
  @Override
  public void onClick( View view )
    {
    if ( view == mCancelButton )
      {
      ///// Cancel /////

      onCancel();
      }
    else if ( view == mConfirmButton )
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

    if ( mConfirmButton != null )
      {
      mConfirmButton.setEnabled( true );

      mConfirmButton.setOnClickListener( this );
      }

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


  ////////// Method(s) //////////

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

